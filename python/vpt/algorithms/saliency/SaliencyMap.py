"""Port of ``vpt.algorithms.saliency.SaliencyMap``: an Achanta-style
multi-scale saliency map. Converts to CIE Lab, builds a summed-area
(integral) table for O(1) box averages, computes a per-pixel colour
distance from three box-average scales (``minDim/8``, ``minDim/4``,
``minDim/2``), averages those three per-pixel saliency scores, then
region-averages the result over ``flatzones.color.ColorQFZSoille``
labels of the *original* (pre-Lab) image for the final output.

The Java reuses one scratch array (``SXY1``/``IIXY1``), mutating it in
place across loop iterations and immediately flushing it into `s`/`ii`
via `setVXYDouble` each time -- safe there because `setVXYDouble` copies
values into the image's own backing storage rather than keeping the
array reference (same as this port's `Image.setVXYDouble`), so nothing
observable depends on the reuse; ported as a plain fresh list per
iteration instead; the computed values are identical either way.

Several `int` divisions are routed through ``idiv`` even where the
divisor is providably safe in practice, matching this port's usual
discipline; ``avg[c] /= (xPlus - xMinus) * (yPlus - yMinus)`` is the one
genuine hazard -- that product is exactly `0` whenever a box-average
radius (`R2[i] / 2`) rounds down to `0` (a scale smaller than the image
itself, e.g. a very small input), so it's routed through ``jdiv``
(Java total double division, not `idiv`, since `avg` is `double`)."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import idiv, jdiv, jdiv_arr, jround, jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ..conversion.RGB2XYZ import RGB2XYZ
from ..conversion.XYZ2LAB import XYZ2LAB
from ..flatzones.color.ColorQFZSoille import ColorQFZSoille


class SaliencyMap(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 3:
            raise GlobalException("Sorry, only color images are accepted !")

        minDim = min(xdim, ydim)

        R2 = [idiv(minDim, 8), idiv(minDim, 4), idiv(minDim, 2)]

        inputOriginal = self.input.newInstance(True)

        self.input = RGB2XYZ.invoke(self.input)
        self.input = XYZ2LAB.invoke(self.input)
        self.input = XYZ2LAB.scaleToByte(self.input)

        ii_arr = self._buildIntegralImage(xdim, ydim)
        scales = self._computeScales(ii_arr, xdim, ydim, R2)

        saliencyMap = ByteImage(xdim, ydim, 1)
        for x in range(xdim):
            for y in range(ydim):
                total = 0
                for i in range(len(R2)):
                    total += scales[i].getXYByte(x, y)
                total = idiv(total, len(R2))

                saliencyMap.setXYByte(x, y, total)

        labelImage = ColorQFZSoille.invoke(inputOriginal, 50, 50)

        p = labelImage.getXYCMaximum(0)
        labels = [[0, 0] for _ in range(labelImage.getXYInt(p.x, p.y) + 1)]
        for x in range(xdim):
            for y in range(ydim):
                label = labelImage.getXYInt(x, y)

                labels[label][0] += 1
                labels[label][1] += saliencyMap.getXYByte(x, y)

        for i in range(1, len(labels)):
            labels[i][1] = idiv(labels[i][1], labels[i][0])

        self.output = ByteImage(xdim, ydim, 1)
        for x in range(xdim):
            for y in range(ydim):
                label = labelImage.getXYInt(x, y)
                self.output.setXYByte(x, y, labels[label][1])

    def _buildIntegralImage(self, xdim: int, ydim: int) -> np.ndarray:
        """Vectorized equivalent of ``_buildIntegralImageScalar`` below.
        ``s[x, y, c] = s[x, y-1, c] + input[x, y, c]`` is a plain running sum
        over y for each fixed x (base case ``s[x, 0, c] = input[x, 0, c]``
        matches an empty-prefix cumsum), and ``ii[x, y, c] = ii[x-1, y, c] +
        s[x, y, c]`` is the same running sum over x for each fixed y. Neither
        recurrence crosses the other axis, so both are exactly
        ``np.cumsum`` -- genuinely sequential in the summed axis, unlike
        ``np.sum``'s pairwise algorithm -- and the two cumsum passes can run
        over the whole array at once instead of being interleaved column by
        column the way the scalar loop happens to do it; the interleaving
        is not observable since ``s``'s recurrence never reads from ``ii``."""
        inputArr = self.input.getDoubleArray().reshape(ydim, xdim, 3)
        with np.errstate(over="ignore", invalid="ignore"):
            s_arr = np.cumsum(inputArr, axis=0)
            return np.cumsum(s_arr, axis=1)

    def _buildIntegralImageScalar(self, xdim: int, ydim: int) -> Image:
        ii = DoubleImage(xdim, ydim, 3)
        ii.fill(0)

        s = DoubleImage(xdim, ydim, 3)
        s.fill(0)

        for x in range(xdim):
            for y in range(ydim):
                if y - 1 < 0:
                    SXY1 = list(self.input.getVXYDouble(x, y))
                else:
                    SXY1 = [
                        s.getXYCDouble(x, y - 1, c) + self.input.getXYCDouble(x, y, c)
                        for c in range(3)
                    ]
                s.setVXYDouble(x, y, SXY1)

                if x - 1 < 0:
                    IIXY1 = list(s.getVXYDouble(x, y))
                else:
                    IIXY1 = [
                        ii.getXYCDouble(x - 1, y, c) + s.getXYCDouble(x, y, c)
                        for c in range(3)
                    ]
                ii.setVXYDouble(x, y, IIXY1)

        return ii

    def _computeScales(
        self, ii_arr: np.ndarray, xdim: int, ydim: int, R2: list[int]
    ) -> list[Image]:
        """Vectorized equivalent of ``_computeScalesScalar`` below. The box
        corners and their integral-image lookups are pure per-pixel index
        arithmetic (exact int operations, order-independent), and the
        colour-distance arithmetic is the same left-to-right expression as
        the scalar version, just batched: ``(four + one) - (two + three)``
        preserves the original grouping, ``jdiv_arr``/``jround_arr`` are the
        array twins of the scalar ``jdiv``/``jround`` already used here, and
        the 3-channel squared-difference sum is accumulated in the same
        ``((d0 + d1) + d2)`` order :func:`util.Distance.euclidean` uses."""
        inputArr = self.input.getDoubleArray().reshape(ydim, xdim, 3)
        X, Y = np.meshgrid(np.arange(xdim), np.arange(ydim))

        scales: list[Image] = [None] * len(R2)
        for i in range(len(R2)):
            r = idiv(R2[i], 2)
            xMinus = np.maximum(0, X - r)
            yMinus = np.maximum(0, Y - r)
            xPlus = np.minimum(xdim - 1, X + r)
            yPlus = np.minimum(ydim - 1, Y + r)

            four = ii_arr[yPlus, xPlus, :]
            three = ii_arr[yPlus, xMinus, :]
            two = ii_arr[yMinus, xPlus, :]
            one = ii_arr[yMinus, xMinus, :]

            avg = (four + one) - (two + three)

            denom = ((xPlus - xMinus) * (yPlus - yMinus)).astype(np.float64)
            denom = np.broadcast_to(denom[:, :, None], avg.shape)

            with np.errstate(over="ignore", invalid="ignore", divide="ignore"):
                avg = jdiv_arr(avg, denom)

                diff = avg - inputArr
                sq = diff * diff
                dist2 = sq[..., 0] + sq[..., 1] + sq[..., 2]
                dist = np.sqrt(dist2) / math.sqrt(3.0)

                scaleVals = jround_arr(dist * 255.0)

            scales[i] = ByteImage(xdim, ydim, 1)
            scales[i].setByteArray(scaleVals.reshape(-1))

        return scales

    def _computeScalesScalar(
        self, ii: Image, xdim: int, ydim: int, R2: list[int]
    ) -> list[Image]:
        scales: list[Image] = [None] * len(R2)
        for i in range(len(R2)):
            scales[i] = ByteImage(xdim, ydim, 1)
            for x in range(xdim):
                for y in range(ydim):
                    xMinus = max(0, x - idiv(R2[i], 2))
                    yMinus = max(0, y - idiv(R2[i], 2))
                    xPlus = min(xdim - 1, x + idiv(R2[i], 2))
                    yPlus = min(ydim - 1, y + idiv(R2[i], 2))

                    four = ii.getVXYDouble(xPlus, yPlus)
                    three = ii.getVXYDouble(xMinus, yPlus)
                    two = ii.getVXYDouble(xPlus, yMinus)
                    one = ii.getVXYDouble(xMinus, yMinus)

                    avg = [0.0, 0.0, 0.0]
                    avg[0] = four[0] + one[0] - (two[0] + three[0])
                    avg[1] = four[1] + one[1] - (two[1] + three[1])
                    avg[2] = four[2] + one[2] - (two[2] + three[2])

                    denom = (xPlus - xMinus) * (yPlus - yMinus)
                    avg[0] = jdiv(avg[0], denom)
                    avg[1] = jdiv(avg[1], denom)
                    avg[2] = jdiv(avg[2], denom)

                    dist = Distance.euclidean(
                        avg, self.input.getVXYDouble(x, y)
                    ) / math.sqrt(3.0)
                    scales[i].setXYByte(x, y, jround(dist * 255.0))

        return scales

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return SaliencyMap().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

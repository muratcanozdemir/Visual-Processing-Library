"""Port of ``vpt.algorithms.mm.gray.GDilation``.

Grayscale dilation by a flat structuring element. A general neighbourhood
scan (``getMax``) is used unless the SE is a box (``se.BOX``), in which case
dilation is separable into a horizontal pass followed by a vertical pass over
a shared sliding-window buffer -- the same optimisation ``GErosion`` mirrors
with ``min`` in place of ``max``. Out-of-bounds neighbours read as
``-Double.MAX_VALUE`` (via the padding helpers, or implicitly by being
excluded from the general-scan max) so they never win the max.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...._javacompat import DOUBLE_MAX_VALUE, idiv
from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE


class GDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        self.cdim = self.output.getCDim()

        if not self.se.BOX:
            self._executeGeneral()
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()
        tmp = self.input.newInstance(False)

        seSize = hline.getXDim()
        halfSize = idiv(seSize, 2)
        padding = halfSize
        segmentSize = 2 * seSize + 1
        numberOfSegments = idiv(
            self.input.getXDim() + padding + 3 * padding - 2 * halfSize, seSize
        )
        maxBuf = [0.0] * segmentSize

        for y in range(4 * self.ydim):
            for c in range(self.cdim):
                for i in range(numberOfSegments):
                    index = (i + 1) * seSize - 1
                    maxBuf[seSize - 1] = self._getPixelWithPadding(index, y, c, padding, self.input)
                    for j in range(1, seSize):
                        maxBuf[seSize - j - 1] = max(
                            maxBuf[seSize - j],
                            self._getPixelWithPadding(index - j, y, c, padding, self.input),
                        )
                        maxBuf[seSize + j - 1] = max(
                            maxBuf[seSize + j - 2],
                            self._getPixelWithPadding(index + j, y, c, padding, self.input),
                        )
                    index = halfSize + i * seSize
                    self._setPixelWithPadding(index, y, c, padding, tmp, maxBuf[0])
                    self._setPixelWithPadding(
                        index + seSize - 1, y, c, padding, tmp, maxBuf[2 * seSize - 2]
                    )
                    for k in range(1, seSize - 1):
                        self._setPixelWithPadding(
                            index + k, y, c, padding, tmp, max(maxBuf[k], maxBuf[k + seSize - 1])
                        )

        seSize = vline.getYDim()
        halfSize = idiv(seSize, 2)
        padding = halfSize
        numberOfSegments = idiv(
            self.input.getYDim() + padding + 3 * padding - 2 * halfSize, seSize
        )

        for x in range(4 * self.xdim):
            for c in range(self.cdim):
                for i in range(numberOfSegments):
                    index = (i + 1) * seSize - 1
                    maxBuf[seSize - 1] = self._getPixelWithPadding(x, index, c, padding, tmp)
                    for j in range(1, seSize):
                        maxBuf[seSize - j - 1] = max(
                            maxBuf[seSize - j],
                            self._getPixelWithPadding(x, index - j, c, padding, tmp),
                        )
                        maxBuf[seSize + j - 1] = max(
                            maxBuf[seSize + j - 2],
                            self._getPixelWithPadding(x, index + j, c, padding, tmp),
                        )
                    index = halfSize + i * seSize
                    self._setPixelWithPadding(x, index, c, padding, self.output, maxBuf[0])
                    self._setPixelWithPadding(
                        x, index + seSize - 1, c, padding, self.output, maxBuf[2 * seSize - 2]
                    )
                    for k in range(1, seSize - 1):
                        self._setPixelWithPadding(
                            x, index + k, c, padding, self.output,
                            max(maxBuf[k], maxBuf[k + seSize - 1]),
                        )

    # ------------------------------------------------------------------
    def _setPixelWithPadding(self, x, y, c, padding, img: Image, p: float) -> None:
        if (
            x >= padding
            and y >= padding
            and x <= self.xdim + padding - 1
            and y <= self.ydim + padding - 1
        ):
            img.setXYCDouble(x - padding, y - padding, c, p)

    def _getPixelWithPadding(self, x, y, c, padding, img: Image) -> float:
        if x < padding or y < padding:
            return -DOUBLE_MAX_VALUE
        if x > self.xdim + padding - 1 or y > self.ydim + padding - 1:
            return -DOUBLE_MAX_VALUE
        return img.getXYCDouble(x - padding, y - padding, c)

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMax`` scan below.
        For each offset, the whole shifted (non-wrapping, unlike
        ``Convolution``'s circular shift) image is compared against the
        running maximum with ``candidate > result`` -- IEEE-754 ``>``,
        elementwise identical to the scalar loop's ``v > maximum`` -- and
        selected via ``np.where`` where it wins, exactly reproducing the
        scalar assignment (including that a NaN candidate never satisfies
        ``>`` and so is silently skipped, never propagated, unlike
        ``np.maximum``/``np.fmax`` which either propagate or unconditionally
        suppress NaN; this project's Java-semantics discipline treats NaN as
        a real, quietly-propagating pixel value, so the comparison operator
        has to match the scalar one exactly, not just its non-NaN cases).
        Preserves a subtle existing quirk: because the scalar loop only
        updates on a *strict* ``>``, a neighbour whose true value exactly
        equals the ``-DOUBLE_MAX_VALUE`` sentinel is indistinguishable from
        "no valid neighbour" and also triggers the own-pixel fallback --
        reproduced here via the same ``> -DOUBLE_MAX_VALUE`` post-check, not
        "fixed"."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)
        result = np.full((self.ydim, self.xdim, self.cdim), -DOUBLE_MAX_VALUE, dtype=np.float64)

        for p in self.se.getCoords():
            px, py = p.x, p.y
            dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
            dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
            if dy1 <= dy0 or dx1 <= dx0:
                continue
            sy0, sy1 = dy0 - py, dy1 - py
            sx0, sx1 = dx0 - px, dx1 - px
            candidate = img[sy0:sy1, sx0:sx1, :]
            dst = result[dy0:dy1, dx0:dx1, :]
            result[dy0:dy1, dx0:dx1, :] = np.where(candidate > dst, candidate, dst)

        result = np.where(result > -DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _getMax(self, img: Image, x: int, y: int, c: int, coords) -> float:
        maximum = -DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                v = img.getXYCDouble(_x, _y, c)
                if v > maximum:
                    maximum = v
        return maximum if maximum > -DOUBLE_MAX_VALUE else img.getXYCDouble(x, y, c)

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GDilation().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

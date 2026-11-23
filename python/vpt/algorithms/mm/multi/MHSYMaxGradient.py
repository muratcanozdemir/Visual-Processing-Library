"""Port of ``vpt.algorithms.mm.multi.MHSYMaxGradient``.

A gradient over an HSY-like (channel 0 = hue, 1 = saturation, 2 = luminance)
image: takes the max, over the SE neighbourhood, of a saturation-weighted
hue distance, then takes the max of that against the plain scalar
``GInternGradient`` on the luminance and saturation channels separately.

POSSIBLE ORIGINAL-SOURCE ODDITY, preserved as found: the neighbourhood loop
computes ``Tools.hueDistance(centralHue, input.getXYCDouble(_x, _y, 2))`` --
comparing the *centre* pixel's hue (channel 0) against the *neighbour's
luminance* (channel 2), not the neighbour's hue. This is not a decompiler
artefact (the loop is well-formed); whether it's an intentional cross-channel
comparison or a channel-index typo in the original source is unclear without
further investigation. Transcribed exactly as written.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...._javacompat import jexp, jexp_arr
from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ....util.Tools import Tools
from ..gray.GInternGradient import GInternGradient


class MHSYMaxGradient(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

        self.xdim = 0
        self.ydim = 0
        self._lumGradient: Image | None = None
        self._satGradient: Image | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self.output = ByteImage(self.xdim, self.ydim, 1)

        self._lumGradient = GInternGradient.invoke(self.input.getChannel(2), self.se)
        self._satGradient = GInternGradient.invoke(self.input.getChannel(1), self.se)
        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getGradient`` scan
        below. Unlike ``MDilation``, this reduction is a fixed scalar
        formula (not an arbitrary caller-supplied ``Ordering``), so it fits
        the same shift-and-reduce shape as ``GDilation``/``MHSYMaxGradient``'s
        sibling files: for each SE offset, compute the whole-image
        contribution at once (hue distance + saturation-sigmoid weighting,
        an elementwise formula with no cross-pixel dependency) and fold it
        into a running maximum with ``candidate > result`` + ``np.where``,
        not ``np.maximum`` -- see ``GDilation._executeGeneral``'s docstring
        for why that distinction matters for NaN input. Excluded (out of
        bounds) offsets contribute nothing, matching the scalar ``if 0 <=
        _x < xdim...`` skip; there's no own-pixel fallback here (unlike
        ``GDilation``), ``maxDist`` starts at, and can legitimately stay,
        ``0.0``.

        The final ``max(maxDist, max(lumGradient, satGradient))`` uses
        Python's builtin two-argument ``max``, which for ``max(a, b)`` is
        ``a`` unless ``b > a`` -- i.e. NaN-order-dependent exactly like the
        running-maximum loop, not the same as ``np.maximum``. Reproduced
        here with the identical nested ``np.where(b > a, b, a)`` shape,
        evaluated in the same order (inner max first, then outer)."""
        # Read channels straight out of self.input's own array (elementwise
        # equal to getXYCDouble by getDoubleArray's contract) rather than via
        # getChannel(c), which round-trips each value through a fresh
        # same-typed image's setDouble/getDouble -- a no-op for a DoubleImage
        # input, but an unnecessary (and, for a quantizing type, unverified)
        # extra encode/decode step to depend on here.
        inputArr = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.input.getCDim())
        centralHue = inputArr[:, :, 0]
        centralSaturation = inputArr[:, :, 1]
        neighborLumFull = inputArr[:, :, 2]
        neighborSatFull = centralSaturation

        result = np.zeros((self.ydim, self.xdim), dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for p in self.se.getCoords():
                px, py = p.x, p.y
                dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
                dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
                if dy1 <= dy0 or dx1 <= dx0:
                    continue
                sy0, sy1 = dy0 - py, dy1 - py
                sx0, sx1 = dx0 - px, dx1 - px

                ch = centralHue[dy0:dy1, dx0:dx1]
                cs = centralSaturation[dy0:dy1, dx0:dx1]
                nl = neighborLumFull[sy0:sy1, sx0:sx1]
                ns = neighborSatFull[sy0:sy1, sx0:sx1]

                a = np.abs(nl - ch)
                hueDist = np.where(a <= 0.5, a, 1.0 - a)
                tmp = hueDist * 2.0
                tmp = tmp * (
                    1.0
                    / (
                        (1.0 + jexp_arr(-10.0 * (ns - 0.35)))
                        * (1.0 + jexp_arr(-10.0 * (cs - 0.35)))
                    )
                )

                dst = result[dy0:dy1, dx0:dx1]
                result[dy0:dy1, dx0:dx1] = np.where(tmp > dst, tmp, dst)

            lum = self._lumGradient.getDoubleArray().reshape(self.ydim, self.xdim)
            sat = self._satGradient.getDoubleArray().reshape(self.ydim, self.xdim)
            innerMax = np.where(sat > lum, sat, lum)
            outerMax = np.where(innerMax > result, innerMax, result)

        self.output.setDoubleArray(outerMax.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for y in range(self.ydim):
            for x in range(self.xdim):
                self.output.setXYDouble(x, y, self._getGradient(x, y, self.se.getCoords()))

    def _getGradient(self, x: int, y: int, coords) -> float:
        maxDist = 0.0

        centralSaturation = self.input.getXYCDouble(x, y, 1)
        centralHue = self.input.getXYCDouble(x, y, 0)
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                tmp = Tools.hueDistance(centralHue, self.input.getXYCDouble(_x, _y, 2)) * 2.0
                tmp *= 1.0 / (
                    (1.0 + jexp(-10.0 * (self.input.getXYCDouble(_x, _y, 1) - 0.35)))
                    * (1.0 + jexp(-10.0 * (centralSaturation - 0.35)))
                )
                if tmp > maxDist:
                    maxDist = tmp

        return max(maxDist, max(self._lumGradient.getXYDouble(x, y), self._satGradient.getXYDouble(x, y)))

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return MHSYMaxGradient().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

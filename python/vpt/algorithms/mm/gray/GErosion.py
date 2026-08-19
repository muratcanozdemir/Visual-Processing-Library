"""Port of ``vpt.algorithms.mm.gray.GErosion``.

Grayscale erosion by a flat structuring element -- ``GDilation`` with ``min``
in place of ``max`` and the SE reflected before use (the correct adjunction
for erosion). Out-of-bounds neighbours read as ``Double.MAX_VALUE`` so they
never win the min.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...._javacompat import DOUBLE_MAX_VALUE, idiv
from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE


class GErosion(Algorithm):
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

        self.se = self.se.reflection()

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
        minBuf = [0.0] * segmentSize

        for y in range(4 * self.ydim):
            for c in range(self.cdim):
                for i in range(numberOfSegments):
                    index = (i + 1) * seSize - 1
                    minBuf[seSize - 1] = self._getPixelWithPadding(index, y, c, padding, self.input)
                    for j in range(1, seSize):
                        minBuf[seSize - j - 1] = min(
                            minBuf[seSize - j],
                            self._getPixelWithPadding(index - j, y, c, padding, self.input),
                        )
                        minBuf[seSize + j - 1] = min(
                            minBuf[seSize + j - 2],
                            self._getPixelWithPadding(index + j, y, c, padding, self.input),
                        )
                    index = halfSize + i * seSize
                    self._setPixelWithPadding(index, y, c, padding, tmp, minBuf[0])
                    self._setPixelWithPadding(
                        index + seSize - 1, y, c, padding, tmp, minBuf[2 * seSize - 2]
                    )
                    for k in range(1, seSize - 1):
                        self._setPixelWithPadding(
                            index + k, y, c, padding, tmp, min(minBuf[k], minBuf[k + seSize - 1])
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
                    minBuf[seSize - 1] = self._getPixelWithPadding(x, index, c, padding, tmp)
                    for j in range(1, seSize):
                        minBuf[seSize - j - 1] = min(
                            minBuf[seSize - j],
                            self._getPixelWithPadding(x, index - j, c, padding, tmp),
                        )
                        minBuf[seSize + j - 1] = min(
                            minBuf[seSize + j - 2],
                            self._getPixelWithPadding(x, index + j, c, padding, tmp),
                        )
                    index = halfSize + i * seSize
                    self._setPixelWithPadding(x, index, c, padding, self.output, minBuf[0])
                    self._setPixelWithPadding(
                        x, index + seSize - 1, c, padding, self.output, minBuf[2 * seSize - 2]
                    )
                    for k in range(1, seSize - 1):
                        self._setPixelWithPadding(
                            x, index + k, c, padding, self.output,
                            min(minBuf[k], minBuf[k + seSize - 1]),
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
            return DOUBLE_MAX_VALUE
        if x > self.xdim + padding - 1 or y > self.ydim + padding - 1:
            return DOUBLE_MAX_VALUE
        return img.getXYCDouble(x - padding, y - padding, c)

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMin`` scan below --
        see ``GDilation._executeGeneral``'s docstring for the full
        reasoning (min in place of max, ``+DOUBLE_MAX_VALUE`` sentinel in
        place of ``-DOUBLE_MAX_VALUE``, same sentinel-collision quirk
        preserved, same ``candidate < result`` comparison-and-select in
        place of ``np.minimum`` so a NaN candidate is skipped rather than
        propagated, matching the scalar ``v < minimum`` loop exactly)."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)
        result = np.full((self.ydim, self.xdim, self.cdim), DOUBLE_MAX_VALUE, dtype=np.float64)

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
            result[dy0:dy1, dx0:dx1, :] = np.where(candidate < dst, candidate, dst)

        result = np.where(result < DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _getMin(self, img: Image, x: int, y: int, c: int, coords) -> float:
        minimum = DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                v = img.getXYCDouble(_x, _y, c)
                if v < minimum:
                    minimum = v
        return minimum if minimum < DOUBLE_MAX_VALUE else img.getXYCDouble(x, y, c)

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GErosion().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.gray.nonflat.GNFErosion``: ``GNFDilation``
with the SE reflected and ``min``/subtraction in place of ``max``/addition."""

from __future__ import annotations

import traceback

import numpy as np

from ....._javacompat import DOUBLE_MAX_VALUE
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import NonFlatSE


class GNFErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: NonFlatSE | None = None
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
        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMin`` scan below --
        see ``GNFDilation._executeGeneral``'s docstring for the shift-and-
        reduce reasoning (min/subtraction in place of max/addition here,
        ``+DOUBLE_MAX_VALUE`` sentinel, ``candidate < result`` + ``np.where``
        in place of ``np.minimum`` so NaN is skipped rather than propagated,
        otherwise identical)."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)
        result = np.full((self.ydim, self.xdim, self.cdim), DOUBLE_MAX_VALUE, dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for p in self.se.getCoords():
                px, py = p.x, p.y
                weight = self.se.getXYDouble(px + self.se.center.x, py + self.se.center.y)
                dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
                dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
                if dy1 <= dy0 or dx1 <= dx0:
                    continue
                sy0, sy1 = dy0 - py, dy1 - py
                sx0, sx1 = dx0 - px, dx1 - px
                candidate = img[sy0:sy1, sx0:sx1, :] - weight
                dst = result[dy0:dy1, dx0:dx1, :]
                result[dy0:dy1, dx0:dx1, :] = np.where(candidate < dst, candidate, dst)

            result = np.where(result < DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for x in range(self.xdim):
            for y in range(self.ydim):
                for c in range(self.cdim):
                    self.output.setXYCDouble(
                        x, y, c, self._getMin(self.input, x, y, c, self.se.getCoords())
                    )

    def _getMin(self, img: Image, x: int, y: int, c: int, coords) -> float:
        minimum = DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                v = img.getXYCDouble(_x, _y, c) - self.se.getXYDouble(
                    p.x + self.se.center.x, p.y + self.se.center.y
                )
                if v < minimum:
                    minimum = v
        return minimum if minimum < DOUBLE_MAX_VALUE else img.getXYCDouble(x, y, c)

    @staticmethod
    def invoke(image: Image, se: NonFlatSE) -> Image | None:
        try:
            return GNFErosion().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

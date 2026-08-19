"""Port of ``vpt.algorithms.mm.gray.adaptive.AErosion``: erosion by a
per-pixel adaptive structuring element (``se[x][y]``, from ``ComputeSEs``).

Note this does *not* reflect ``se[x][y]`` first (unlike the flat/non-flat/
floating erosions elsewhere in ``mm/gray``) -- transcribed as the Java has
it.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....._javacompat import DOUBLE_MAX_VALUE
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE


class AErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: list[list[FlatSE]] | None = None
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
        if self.cdim != 1:
            raise GlobalException("Only grayscale for now, sorry")

        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMin`` scan below --
        see ``ADilation._executeGeneral``'s docstring for the padded-
        coordinate-slot reasoning (min in place of max, ``+DOUBLE_MAX_VALUE``
        sentinel, ``candidate < result`` comparison, otherwise identical --
        note this class doesn't reflect ``se[x][y]`` first, matching its
        scalar ``_getMin`` below)."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim)

        coordLists = [
            [self.se[x][y].getCoords() for y in range(self.ydim)] for x in range(self.xdim)
        ]
        maxCoords = max(
            (len(coordLists[x][y]) for x in range(self.xdim) for y in range(self.ydim)),
            default=0,
        )

        pxArr = np.zeros((self.ydim, self.xdim, maxCoords), dtype=np.int64)
        pyArr = np.zeros((self.ydim, self.xdim, maxCoords), dtype=np.int64)
        validK = np.zeros((self.ydim, self.xdim, maxCoords), dtype=bool)
        for x in range(self.xdim):
            for y in range(self.ydim):
                coords = coordLists[x][y]
                for k, p in enumerate(coords):
                    pxArr[y, x, k] = p.x
                    pyArr[y, x, k] = p.y
                    validK[y, x, k] = True

        X, Y = np.meshgrid(np.arange(self.xdim), np.arange(self.ydim))
        result = np.full((self.ydim, self.xdim), DOUBLE_MAX_VALUE, dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for k in range(maxCoords):
                _x = X - pxArr[:, :, k]
                _y = Y - pyArr[:, :, k]
                inBounds = (_x >= 0) & (_x < self.xdim) & (_y >= 0) & (_y < self.ydim)
                validHere = inBounds & validK[:, :, k]
                cx = np.clip(_x, 0, self.xdim - 1)
                cy = np.clip(_y, 0, self.ydim - 1)
                v = img[cy, cx]
                candidate = np.where(validHere, v, DOUBLE_MAX_VALUE)
                result = np.where(candidate < result, candidate, result)

            result = np.where(result < DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for x in range(self.xdim):
            for y in range(self.ydim):
                self.output.setXYDouble(x, y, self._getMin(self.input, x, y, self.se[x][y].getCoords()))

    def _getMin(self, img: Image, x: int, y: int, coords) -> float:
        minimum = DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                v = img.getXYDouble(_x, _y)
                if v < minimum:
                    minimum = v
        return minimum if minimum < DOUBLE_MAX_VALUE else img.getXYDouble(x, y)

    @staticmethod
    def invoke(image: Image, se: list[list[FlatSE]]) -> Image | None:
        try:
            return AErosion().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

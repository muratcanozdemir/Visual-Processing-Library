"""Port of ``vpt.algorithms.mm.multi.MRankFilter``: the ``rank``-th vector
(by the given ``Ordering``) among the SE neighbourhood, per pixel -- the
vectorial generalisation of a scalar rank/median filter.

Same clamp-to-border edge handling and reused-``pixelArray``-buffer pattern
as ``MDilation``/``MErosion``. The BOX fast path splits the flat rank
into independent horizontal/vertical ranks (``rank % se.getXDim()``,
``rank // se.getXDim()``) rather than being separable in the way min/max
are -- this is *not* generally equivalent to ranking the full 2-D
neighbourhood at once, but it's exactly what the Java computes.
"""

from __future__ import annotations

import traceback

from ...._javacompat import idiv, imod
from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import Ordering
from ....util.se import FlatSE


class MRankFilter(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.rank: int | None = None
        self.or_: Ordering | None = None
        self.inputFields = "input,se,or_,rank"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self._pixelArray: list | None = None

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        if self.rank < 0 or self.rank > len(self.se.getCoords()) - 1:
            raise GlobalException("Invalid rank value")

        if not self.se.BOX:
            self._pixelArray = [None] * len(self.se.getCoords())
            for x in range(self.xdim):
                for y in range(self.ydim):
                    self.output.setVXYDouble(
                        x, y, self._getRank(self.input, x, y, self.se.getCoords(), self.rank)
                    )
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()

        hrank = imod(self.rank, self.se.getXDim())
        vrank = idiv(self.rank, self.se.getXDim())

        tmp = self.input.newInstance(False)

        self._pixelArray = [None] * len(hline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                tmp.setVXYDouble(x, y, self._getRank(self.input, x, y, hline.getCoords(), hrank))

        self._pixelArray = [None] * len(vline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                self.output.setVXYDouble(x, y, self._getRank(tmp, x, y, vline.getCoords(), vrank))

    def _getRank(self, img: Image, x: int, y: int, coords, rank: int):
        index = 0
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x < 0:
                _x = 0
            if _y < 0:
                _y = 0
            if _x >= self.xdim:
                _x = self.xdim - 1
            if _y >= self.ydim:
                _y = self.ydim - 1
            self._pixelArray[index] = img.getVXYDouble(_x, _y)
            index += 1
        return self.or_.rank(self._pixelArray, rank) if index > 0 else img.getVXYDouble(x, y)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: Ordering, rank: int) -> Image | None:
        try:
            return MRankFilter().preprocess(image, se, or_, rank)
        except GlobalException:
            traceback.print_exc()
        return None

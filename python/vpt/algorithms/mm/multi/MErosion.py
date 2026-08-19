"""Port of ``vpt.algorithms.mm.multi.MErosion``.

Multichannel (vectorial) erosion -- the ``MDilation`` counterpart, ``min`` in
place of ``max``. Unlike ``GErosion``, this does **not** reflect the SE
first; instead ``_getMin`` adds the coordinate offset where ``MDilation``'s
``_getMax`` subtracts it (``_x = x + p.x`` vs ``_x = x - p.x``). For an
asymmetric SE these two approaches are not obviously interchangeable, but
this is exactly what the Java does -- transcribed as found, not
"corrected" to match ``GErosion``'s reflect-then-subtract convention.
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import Ordering
from ....util.se import FlatSE


class MErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.or_: Ordering | None = None
        self.inputFields = "input,se,or_"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self._pixelArray: list | None = None

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()

        if not self.se.BOX:
            self._pixelArray = [None] * len(self.se.getCoords())
            for x in range(self.xdim):
                for y in range(self.ydim):
                    self.output.setVXYDouble(x, y, self._getMin(self.input, x, y, self.se.getCoords()))
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()
        tmp = self.input.newInstance(False)

        self._pixelArray = [None] * len(hline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                tmp.setVXYDouble(x, y, self._getMin(self.input, x, y, hline.getCoords()))

        self._pixelArray = [None] * len(vline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                self.output.setVXYDouble(x, y, self._getMin(tmp, x, y, vline.getCoords()))

    def _getMin(self, img: Image, x: int, y: int, coords):
        index = 0
        for p in coords:
            _x = x + p.x
            _y = y + p.y
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
        return self.or_.min(self._pixelArray) if index > 0 else img.getVXYDouble(x, y)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: Ordering) -> Image | None:
        try:
            return MErosion().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

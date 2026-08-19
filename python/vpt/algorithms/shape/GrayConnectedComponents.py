"""Port of ``vpt.algorithms.shape.GrayConnectedComponents``: like
``ConnectedComponents``, but 8-connected components are grouped by equal
*label value* (``getXYInt``) rather than by a boolean foreground test --
so this partitions the whole image (every pixel belongs to some
component), not just the foreground. Same ``temp``/``temp2`` roles as
``ConnectedComponents``: ``temp`` marks pixels already queued for the
current component (reset after each one); ``temp2`` marks pixels already
assigned to some component."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class GrayConnectedComponents(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[list[Point]] | None = None
        self.input: Image | None = None
        self.xdim = 0
        self.ydim = 0
        self._temp: BooleanImage | None = None
        self._temp2: BooleanImage | None = None
        self._stack: list[Point] = []
        self._list2: list[Point] = []
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self.output = []

        self._temp = BooleanImage(self.input, False)
        self._temp.fill(False)

        self._temp2 = BooleanImage(self.input, False)
        self._temp2.fill(False)

        self._stack = []

        self._list2 = []
        for y in range(self.ydim):
            for x in range(self.xdim):
                if not self._temp2.getXYBoolean(x, y):
                    cc = self._extractCC(Point(x, y), self.input, self.input.getXYInt(x, y))

                    self.output.append(cc)

    def _extractCC(self, r: Point, img: Image, label: int) -> list[Point]:
        self._stack.clear()
        self._stack.append(r)
        self._temp.setXYBoolean(r.x, r.y, True)
        self._list2.append(r)

        pixels: list[Point] = []
        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y
            pixels.append(tmp)

            self._temp2.setXYBoolean(x, y, True)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if _x >= 0 and _x < self.xdim and _y >= 0 and _y < self.ydim:
                    if not self._temp.getXYBoolean(_x, _y):
                        if img.getXYInt(_x, _y) == label:
                            t = Point(_x, _y)
                            self._stack.append(t)

                            self._temp.setXYBoolean(t.x, t.y, True)
                            self._list2.append(t)

        for t in self._list2:
            self._temp.setXYBoolean(t.x, t.y, False)
        self._list2.clear()

        return pixels

    @staticmethod
    def invoke(img: Image) -> list[list[Point]] | None:
        try:
            return GrayConnectedComponents().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

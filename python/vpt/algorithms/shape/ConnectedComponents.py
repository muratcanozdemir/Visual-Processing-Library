"""Port of ``vpt.algorithms.shape.ConnectedComponents``.

8-connected component extraction over a mono-channel boolean-valued image,
via an explicit stack-based flood fill (the Java original is one of the
"collapsed nested loop" decompiler-damage sites, see the root README's
"Decompiler damage repaired" section item 1 -- reconstructed here from a
recovered ``while``/``for`` pair). ``temp`` marks pixels already queued
(reset after each component so it can be reused); ``temp2`` marks pixels
already assigned to some component (persists across the whole image so
already-visited foreground pixels aren't re-extracted as new components).
"""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class ConnectedComponents(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[list[Point]] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self._temp: BooleanImage | None = None
        self._temp2: BooleanImage | None = None
        self._stack: list[Point] = []
        self._list2: list[Point] = []

    def execute(self) -> None:
        if self.input.getCDim() != 1:
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
                if self.input.getXYBoolean(x, y) and not self._temp2.getXYBoolean(x, y):
                    cc = self._extractCC(Point(x, y), self.input)
                    self.output.append(cc)

    def _extractCC(self, r: Point, img: Image) -> list[Point]:
        self._stack.clear()
        self._stack.append(r)
        self._temp.setXYBoolean(r.x, r.y, True)
        self._list2.append(r)

        pixels: list[Point] = []
        while self._stack:
            tmp = self._stack.pop()
            x, y = tmp.x, tmp.y
            pixels.append(tmp)

            self._temp2.setXYBoolean(x, y, True)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim and not self._temp.getXYBoolean(_x, _y):
                    if img.getXYBoolean(_x, _y):
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
            return ConnectedComponents().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

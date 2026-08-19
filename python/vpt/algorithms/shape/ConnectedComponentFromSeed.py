"""Port of ``vpt.algorithms.shape.ConnectedComponentFromSeed``: 8-connected
DFS flood fill from a single seed point, returning every foreground pixel
reached. ``temp2`` is set but never read anywhere else -- a dead field in
the Java, kept for structural parity."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image

_N = [
    Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1),
    Point(1, 1), Point(-1, -1), Point(-1, 1), Point(1, -1),
]  # fmt: skip


class ConnectedComponentFromSeed(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[Point] | None = None
        self.input: Image | None = None
        self.seed: Point | None = None
        self.xdim = 0
        self.ydim = 0
        self.temp: Image | None = None
        self.temp2: Image | None = None
        self.s: list[Point] | None = None
        self.list2: list[Point] | None = None
        self.inputFields = "input, seed"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self.output = []

        self.temp = BooleanImage(self.input, False)
        self.temp.fill(False)

        self.temp2 = BooleanImage(self.input, False)
        self.temp2.fill(False)

        self.s = []

        self.list2 = []

        self.output = self._extractCC(self.seed, self.input)

    def _extractCC(self, r: Point, img: Image) -> list[Point]:
        self.s.clear()
        self.s.append(r)
        self.temp.setXYBoolean(r.x, r.y, True)
        self.list2.append(r)

        pixels: list[Point] = []
        while self.s:
            tmp = self.s.pop()

            x = tmp.x
            y = tmp.y
            pixels.append(tmp)

            self.temp2.setXYBoolean(x, y, True)

            for i in range(len(_N)):
                _x = x + _N[i].x
                _y = y + _N[i].y
                if _x >= 0 and _x < self.xdim and _y >= 0 and _y < self.ydim:
                    if not self.temp.getXYBoolean(_x, _y):
                        q = img.getXYBoolean(_x, _y)
                        if q:
                            t = Point(_x, _y)
                            self.s.append(t)

                            self.temp.setXYBoolean(t.x, t.y, True)
                            self.list2.append(t)

        for t in self.list2:
            self.temp.setXYBoolean(t.x, t.y, False)
        self.list2.clear()

        return pixels

    @staticmethod
    def invoke(img: Image, seed: Point) -> list[Point] | None:
        try:
            return ConnectedComponentFromSeed().preprocess(img, seed)
        except GlobalException:
            traceback.print_exc()
        return None

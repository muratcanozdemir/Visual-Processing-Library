"""Port of ``vpt.algorithms.geometric.KeepLargestCC``: 8-connectivity BFS
flood fill over a ``BooleanImage``, keeping only the largest connected
component of ``True`` pixels.

``LinkedList.add``/``removeFirst`` is a plain FIFO queue here (unlike
``mm/multi/connected/maxtree.MyList``, no node-level behaviour is
exploited), so it's ported as ``collections.deque`` rather than a custom
linked structure. If the input has no ``True`` pixels at all, ``CCs`` is
empty and indexing it with ``index=0`` raises -- an
``IndexOutOfBoundsException`` in Java too, left unguarded here as well."""

from __future__ import annotations

import traceback
from collections import deque

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image

_N = [
    Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1),
    Point(1, 1), Point(-1, -1), Point(-1, 1), Point(1, -1),
]  # fmt: skip


class KeepLargestCC(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.temp: Image | None = None
        self.fifo: deque | None = None
        self.N = _N
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Only mono-channel images for now...")

        self.output = BooleanImage(self.input, False)
        self.output.fill(False)

        mask = BooleanImage(self.input, False)
        mask.fill(False)

        self.fifo = deque()

        self.temp = BooleanImage(xdim, ydim, 1)
        self.temp.fill(False)

        CCs = []
        for x in range(xdim):
            for y in range(ydim):
                if self.input.getXYBoolean(x, y):
                    if not mask.getXYBoolean(x, y):
                        newCC = []
                        CCs.append(newCC)

                        r = Point(x, y)

                        self.fifo.clear()
                        self.fifo.append(r)
                        self.temp.setXYBoolean(x, y, True)

                        list2 = [r]
                        while self.fifo:
                            p = self.fifo.popleft()
                            mask.setXYBoolean(p.x, p.y, True)
                            newCC.append(p)

                            for i in range(len(self.N)):
                                _x = p.x + self.N[i].x
                                _y = p.y + self.N[i].y
                                if _x >= 0 and _x < xdim and _y >= 0 and _y < ydim:
                                    if self.input.getXYBoolean(_x, _y):
                                        if not self.temp.getXYBoolean(_x, _y):
                                            t = Point(_x, _y)
                                            self.fifo.append(t)

                                            self.temp.setXYBoolean(t.x, t.y, True)
                                            list2.append(t)

                        for t in list2:
                            self.temp.setXYBoolean(t.x, t.y, False)

        maxSize = 0
        index = 0
        for i in range(len(CCs)):
            if len(CCs[i]) > maxSize:
                maxSize = len(CCs[i])
                index = i

        largestCC = CCs[index]
        for i in range(len(largestCC)):
            p = largestCC[i]
            self.output.setXYBoolean(p.x, p.y, True)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return KeepLargestCC().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

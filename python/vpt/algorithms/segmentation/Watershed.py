"""Port of ``vpt.algorithms.segmentation.Watershed``: the classic
Vincent-Soille immersion-simulation watershed transform, per channel.
Pixels are processed in ascending grey-level order (bucketed into 256
FIFO queues by byte value first); each level's pixels adjacent to an
already-labelled region are grown via a breadth-first geodesic dilation
(``flag`` distinguishes "still on a genuine watershed line" from "provisionally
merged, may yet split"), and any pixel untouched by that growth seeds a
brand new label via a second, unconditional flood fill.

``Fifo`` (a private inner class wrapping a ``Vector`` used strictly as a
FIFO queue) is ported as ``collections.deque`` rather than a bespoke
structure, matching the same reasoning as ``geometric.KeepLargestCC``: no
node-level behaviour is exploited here."""

from __future__ import annotations

import sys
import traceback
from collections import deque

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...IntegerImage import IntegerImage

WSHED = 0
INIT = -3
MASK = -2
INQUEUE = -1


class Watershed(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.xDim = 0
        self.yDim = 0
        self.cDim = 0
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.xDim = self.input.getXDim()
        self.yDim = self.input.getYDim()
        self.cDim = self.input.getCDim()

        self.output = IntegerImage(self.xDim, self.yDim, self.cDim)
        self.output.fill(INIT)
        for c in range(self.cDim):
            flag = False

            fifo = deque()

            currentLabel = 0

            distro = self._calculateInputPixelDistro(c)
            for i in range(256):
                size = len(distro[i])
                for j in range(size):
                    p = distro[i][j]

                    x = p.x
                    y = p.y

                    self.output.setXYCInt(x, y, c, MASK)
                    if self._areThereLabelledNeighbours(x, y, c):
                        self.output.setXYCInt(x, y, c, INQUEUE)
                        fifo.append(p)

                while fifo:
                    p = fifo.popleft()
                    x = p.x
                    yq = p.y

                    for j in range(yq - 1, yq + 2):
                        for k in range(x - 1, x + 2):
                            if k >= 0 and k < self.xDim and j >= 0 and j < self.yDim:
                                if (j != yq or k != x) and self.output.getXYCInt(k, j, c) > 0:
                                    if self.output.getXYCInt(x, yq, c) == INQUEUE or (
                                        self.output.getXYCInt(x, yq, c) == WSHED and flag
                                    ):
                                        self.output.setXYCInt(
                                            x, yq, c, self.output.getXYCInt(k, j, c)
                                        )
                                    elif self.output.getXYCInt(
                                        x, yq, c
                                    ) > 0 and self.output.getXYCInt(
                                        x, yq, c
                                    ) != self.output.getXYCInt(k, j, c):
                                        self.output.setXYCInt(x, yq, c, WSHED)
                                        flag = False
                                elif (
                                    self.output.getXYCInt(k, j, c) == WSHED
                                    and self.output.getXYCInt(x, yq, c) == INQUEUE
                                ):
                                    self.output.setXYCInt(x, yq, c, WSHED)
                                    flag = True
                                elif self.output.getXYCInt(k, j, c) == MASK:
                                    self.output.setXYCInt(k, j, c, INQUEUE)
                                    fifo.append(Point(k, j))

                size = len(distro[i])
                for j in range(size):
                    p = distro[i][j]

                    x = p.x
                    y = p.y
                    if self.output.getXYCInt(x, y, c) == MASK:
                        currentLabel += 1
                        fifo.append(p)
                        self.output.setXYCInt(x, y, c, currentLabel)

                        while fifo:
                            p = fifo.popleft()
                            x = p.x
                            y = p.y

                            for m in range(y - 1, y + 2):
                                for k in range(x - 1, x + 2):
                                    if (
                                        k >= 0
                                        and k < self.xDim
                                        and m >= 0
                                        and m < self.yDim
                                        and (k != x or m != y)
                                        and self.output.getXYCInt(k, m, c) == MASK
                                    ):
                                        fifo.append(Point(k, m))
                                        self.output.setXYCInt(k, m, c, currentLabel)

            currentLabel -= 1
            print(f"Number of labels:{currentLabel}", file=sys.stderr)

    def _calculateInputPixelDistro(self, c: int) -> list[list[Point]]:
        distro: list[list[Point]] = [[] for _ in range(256)]
        for x in range(self.xDim):
            for y in range(self.yDim):
                distro[self.input.getXYCByte(x, y, c)].append(Point(x, y))
        return distro

    def _areThereLabelledNeighbours(self, x: int, y: int, c: int) -> bool:
        for j in range(y - 1, y + 2):
            for i in range(x - 1, x + 2):
                if i >= 0 and i < self.xDim and j >= 0 and j < self.yDim:
                    if (i != x or j != y) and self.output.getXYCInt(i, j, c) >= 0:
                        return True
        return False

    @staticmethod
    def invoke(input: Image) -> Image | None:
        try:
            return Watershed().preprocess(input)
        except GlobalException:
            traceback.print_exc()
        return None

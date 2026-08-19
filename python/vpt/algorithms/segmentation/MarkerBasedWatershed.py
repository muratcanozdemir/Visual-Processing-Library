"""Port of ``vpt.algorithms.segmentation.MarkerBasedWatershed``: seeded
(marker-based) watershed via a hierarchical (256-bucket) FIFO priority
queue, run once per channel. Every 0-valued pixel not yet visited starts a
new marker region (flood-filled through other 0-valued, unlabelled pixels);
every marker point is queued at priority 0, then the queue repeatedly pops
the lowest-priority pending point and grows its label into any unlabelled
neighbour, queued at that neighbour's own pixel value.

``connexity4`` is a private field with no setter and never wired into
``inputFields`` -- always ``False`` for every real call, confirmed via
`javap` bytecode inspection alongside the check below. **Bytecode-confirmed
bug, dead in practice**: the 4-connexity restriction in both `marker` and
`getNonLabelledNeighbours` -- ``if (!connexity4 or j == 0 or k == 0)`` --
compares the neighbour's *absolute* row/column index to literal ``0``,
not to the centre pixel's own coordinates (which would be the sane
"same row or column as centre" 4-connexity test) or to a relative offset.
Even if ``connexity4`` were ever set ``True``, this would only restrict
diagonal neighbours near the image's top row / left column, not
image-wide. Transcribed exactly (including the field, always ``False``)
since ``connexity4 or True`` (from the OR) already makes the whole check
vacuous for every real invocation."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...IntegerImage import IntegerImage

IGNORE = -1
NULL = 0
GRAY_LEVELS = 256


class _HierarchicalQueue:
    def __init__(self, size: int):
        self.queue: list[list[Point]] = [[] for _ in range(size)]
        self.current = 0

    def add(self, p: Point, val: int) -> None:
        if val >= self.current:
            self.queue[val].append(p)
        else:
            self.queue[self.current].append(p)

    def get(self) -> Point | None:
        if len(self.queue[self.current]) >= 2:
            return self.queue[self.current].pop(0)
        if len(self.queue[self.current]) == 1:
            p = self.queue[self.current].pop(0)
            while self.current < 255 and len(self.queue[self.current]) == 0:
                self.current += 1
            return p
        return None

    def isEmpty(self) -> bool:
        total = 0
        for i in range(self.current, len(self.queue)):
            total += len(self.queue[i])
        return total == 0


class MarkerBasedWatershed(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.outputImage: Image | None = None
        self.mask: Image | None = None
        self.connexity4 = False
        self.inputFields = "inputImage"
        self.outputFields = "outputImage"

    def execute(self) -> None:
        self.mask = BooleanImage(self.inputImage, False)
        self.mask.fill(1)

        cdim = self.inputImage.getCDim()
        xdim = self.inputImage.getXDim()
        ydim = self.inputImage.getYDim()

        input_ = IntegerImage(xdim, ydim, 1)
        output = IntegerImage(xdim, ydim, 1)

        self.outputImage = IntegerImage(xdim, ydim, cdim)
        for c in range(cdim):
            for x in range(xdim):
                for y in range(ydim):
                    input_.setXYCInt(x, y, 0, self.inputImage.getXYCByte(x, y, c))

            queue = _HierarchicalQueue(256)
            currentLabel = 1

            output.fill(0)
            for x in range(xdim):
                for y in range(ydim):
                    p = input_.getXYInt(x, y)
                    v = output.getXYInt(x, y)
                    if not self.mask.getXYBoolean(x, y):
                        output.setXYInt(x, y, -1)
                    elif p == 0 and v == 0:
                        self._marker(input_, output, x, y, queue, currentLabel)
                        currentLabel += 1

            while not queue.isEmpty():
                p = queue.get()
                label = output.getXYInt(p.x, p.y)

                neighbours = self._getNonLabelledNeighbours(output, p.x, p.y)

                for i in range(len(neighbours)):
                    output.setXYInt(neighbours[i].x, neighbours[i].y, label)

                    val = input_.getXYInt(neighbours[i].x, neighbours[i].y)

                    queue.add(neighbours[i], val)

            for _x in range(xdim):
                for _y in range(ydim):
                    self.outputImage.setXYCInt(_x, _y, c, output.getXYCInt(_x, _y, 0))

    def _marker(
        self,
        input: Image,
        output: Image,
        x: int,
        y: int,
        queue: _HierarchicalQueue,
        label: int,
    ) -> None:
        fifo = [Point(x, y)]

        while len(fifo) > 0:
            p = fifo.pop(0)

            queue.add(p, 0)
            output.setXYInt(p.x, p.y, label)

            for j in range(p.y - 1, p.y + 2):
                for k in range(p.x - 1, p.x + 2):
                    if k >= 0 and k < input.getXDim() and j >= 0 and j < input.getYDim():
                        if not self.connexity4 or j == 0 or k == 0:
                            if self.mask.getXYBoolean(k, j):
                                if (
                                    (k != p.x or j != p.y)
                                    and input.getXYInt(k, j) == 0
                                    and output.getXYInt(k, j) == 0
                                ):
                                    size = len(fifo)
                                    b = False
                                    for i in range(size):
                                        v = fifo[i]
                                        if v.x == k and v.y == j:
                                            b = True
                                    if not b:
                                        fifo.append(Point(k, j))

    def _getNonLabelledNeighbours(self, output: Image, x: int, y: int) -> list[Point]:
        neighbours: list[Point | None] = [None] * 8

        cnt = 0
        for j in range(y - 1, y + 2):
            for i in range(x - 1, x + 2):
                if i >= 0 and i < output.getXDim() and j >= 0 and j < output.getYDim():
                    if not self.connexity4 or i == 0 or j == 0:
                        if self.mask.getXYBoolean(i, j):
                            z = output.getXYInt(i, j)
                            if (i != x or j != y) and z == 0:
                                neighbours[cnt] = Point(i, j)
                                cnt += 1

        if cnt < 8:
            tmp = [None] * cnt
            for i in range(cnt):
                tmp[i] = neighbours[i]
            neighbours = tmp

        return neighbours

    @staticmethod
    def invoke(input: Image) -> Image | None:
        try:
            return MarkerBasedWatershed().preprocess(input)
        except GlobalException:
            traceback.print_exc()
        return None

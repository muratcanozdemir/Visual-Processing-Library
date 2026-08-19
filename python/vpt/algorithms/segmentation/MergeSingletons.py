"""Port of ``vpt.algorithms.segmentation.MergeSingletons``: relabels every
pixel whose label differs from all of its 8-neighbours (a "singleton", same
test as ``NumberOfSingletons``) to its colour-nearest neighbour's label.

Reads/writes ``this.output`` throughout (a copy of ``labels``, mutated in
place as it goes), so a pixel processed later in scan order can see an
already-relabelled neighbour from earlier in the same pass -- not a
snapshot-then-apply. Transcribed with the same single mutable ``output``,
not a separate read/write buffer pair."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ._util import isSingle


class MergeSingletons(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.labels: Image | None = None
        self.original: Image | None = None
        self.xdim = 0
        self.ydim = 0
        self.inputFields = "labels, original"
        self.outputFields = "output"

    def execute(self) -> None:
        self.xdim = self.labels.getXDim()
        self.ydim = self.labels.getYDim()
        cdim = self.labels.getCDim()
        if cdim != 1:
            raise GlobalException("A label image cannot have more than one channel!")
        if self.xdim != self.original.getXDim() or self.ydim != self.original.getYDim():
            raise GlobalException("The labels and original image do not have the same dimensions!")

        self.output = self.labels.newInstance(True)
        for y in range(self.ydim):
            for x in range(self.xdim):
                if isSingle(x, y, self.xdim, self.ydim, self.output):
                    distance = 1.7976931348623157e308

                    p = self.original.getVXYByte(x, y)
                    newLabel = self.output.getXYInt(x, y)
                    for _x in range(x - 1, x + 2):
                        for _y in range(y - 1, y + 2):
                            if (
                                _x >= 0
                                and _y >= 0
                                and _x <= self.xdim - 1
                                and _y <= self.ydim - 1
                                and (_x != x or _y != y)
                            ):
                                tmp = Tools.EuclideanDistance(p, self.original.getVXYByte(_x, _y))
                                if tmp < distance:
                                    newLabel = self.output.getXYInt(_x, _y)
                                    distance = tmp

                    self.output.setXYInt(x, y, newLabel)

    @staticmethod
    def invoke(labels: Image, original: Image) -> Image | None:
        try:
            return MergeSingletons().preprocess(labels, original)
        except GlobalException:
            traceback.print_exc()
        return None

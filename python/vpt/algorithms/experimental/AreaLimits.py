"""Port of ``vpt.algorithms.experimental.AreaLimits``: partitions the
input into strict flat zones (``flatzones.gray.GrayFZ2``) and erases
(sets to byte ``0``) every zone whose pixel count falls outside
``[llimit, ulimit]`` -- a size-based flat-zone filter."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..flatzones.gray.GrayFZ2 import GrayFZ2


class AreaLimits(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.ulimit: int | None = None
        self.llimit: int | None = None
        self.input: Image | None = None
        self.inputFields = "input, ulimit, llimit"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("For now only grayscale images...")

        self.output = self.input.newInstance(True)

        qfz = GrayFZ2.invoke(self.input)
        for tmp in qfz:
            if len(tmp) > self.ulimit or len(tmp) < self.llimit:
                for p in tmp:
                    self.output.setXYByte(p.x, p.y, 0)

    @staticmethod
    def invoke(input: Image, ulimit: int, llimit: int) -> Image | None:
        try:
            return AreaLimits().preprocess(input, ulimit, llimit)
        except GlobalException:
            traceback.print_exc()
        return None

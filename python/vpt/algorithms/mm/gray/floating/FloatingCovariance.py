"""Port of ``vpt.algorithms.mm.gray.floating.FloatingCovariance``.

The granulometry-style "covariance" curve: erode by a growing circular
floating SE (radius ``(i-1)*step + 1`` for ``i`` in ``1..len``) and record,
per channel, the fraction of the original volume that survives at each
step.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FloatingFlatSE
from .....util.Tools import Tools
from .GFloatingErosion import GFloatingErosion


class FloatingCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.step: int | None = None
        self.inputFields = "input,len,step"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * cdim
        self.output = [0.0] * size

        originalVolumes = [0.0] * cdim
        for c in range(cdim):
            originalVolumes[c] = Tools.volume(self.input, c)

        for i in range(1, self.len + 1):
            se = FloatingFlatSE.circle((i - 1) * self.step + 1, ((i - 1) * self.step + 1) * 8)

            tmp = GFloatingErosion.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * self.len + i - 1] = Tools.volume(tmp, c) / originalVolumes[c]

    @staticmethod
    def invoke(image: Image, length: int, step: int) -> list[float] | None:
        try:
            return FloatingCovariance().preprocess(image, length, step)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.gray.floating.FloatingOpeningBasedCovariance``:
identical to ``FloatingCovariance`` except each step uses ``GFloatingOpening``
(erosion then dilation) instead of a bare erosion -- the opening-based
covariance curve is smoother and less sensitive to single-pixel noise than
the plain erosion-based one."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FloatingFlatSE
from .....util.Tools import Tools
from .GFloatingOpening import GFloatingOpening


class FloatingOpeningBasedCovariance(Algorithm):
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

            tmp = GFloatingOpening.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * self.len + i - 1] = Tools.volume(tmp, c) / originalVolumes[c]

    @staticmethod
    def invoke(image: Image, length: int, step: int) -> list[float] | None:
        try:
            return FloatingOpeningBasedCovariance().preprocess(image, length, step)
        except GlobalException:
            traceback.print_exc()
        return None

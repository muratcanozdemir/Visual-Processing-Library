"""Port of ``vpt.algorithms.mm.gray.geo.GRegionalMaxima``: reconstruct
``input - 1 (byte step)`` by dilation under ``input``; wherever that
reconstruction couldn't climb back up to the original value marks a regional
maximum, so subtracting it from ``input`` isolates those pixels."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....arithmetic.Subtraction import Subtraction
from ....arithmetic.SubtractionC import SubtractionC
from .FastGrayReconstruction import CONNEXITY8, FastGrayReconstruction

_BYTE_TO_DOUBLE = 0.00392156862745098


class GRegionalMaxima(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        fMinusOne = SubtractionC.invoke(self.inputImage, _BYTE_TO_DOUBLE)

        fMinusOne = FastGrayReconstruction.invoke(fMinusOne, self.inputImage, CONNEXITY8, False)

        self.output = Subtraction.invoke(self.inputImage, fMinusOne)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return GRegionalMaxima().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

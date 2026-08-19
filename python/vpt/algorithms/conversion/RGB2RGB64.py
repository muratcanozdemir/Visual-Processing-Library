"""Port of ``vpt.algorithms.conversion.RGB2RGB64``: quantise each channel
down to 64 levels (``(v // 64) * 64``, integer truncation).

Despite the class name and its 3-channel ``DoubleImage`` output, this stores
each channel through the *byte* view (``setXYCByte``), same as the Java --
the ``64`` factor is applied to the integer pixel value, not a floating-point
one.
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2RGB64(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 3)

        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus RGB image")

        # idiv(v, 64) == v // 64 for the non-negative v this always sees
        # (getXYCByte's range is [0, 255]), so plain floor-division is safe
        # here without needing an idiv_arr helper.
        rgb = self.input.getByteArray()
        self.output.setByteArray((rgb // 64) * 64)

    @staticmethod
    def convert(r: int, g: int, b: int) -> float:
        return (r + g + b) / 3.0

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2RGB64().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

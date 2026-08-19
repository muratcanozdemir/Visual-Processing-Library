"""Port of ``vpt.algorithms.point.Power``: ``pixel ** power`` through the
double view, on a deep copy of the input. Uses ``jpow`` rather than Python's
``**``/``math.pow`` since ``power`` is caller-supplied and pixel values can
be negative (e.g. an image already run through some other arithmetic op),
so a negative base with a non-integer exponent is a real possibility here,
not a theoretical one -- see ``jpow``'s docstring."""

from __future__ import annotations

import traceback

from ..._javacompat import jpow_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Power(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.power: float | None = None
        self.inputFields = "input,power"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        self.output.setDoubleArray(jpow_arr(self.output.getDoubleArray(), self.power))

    @staticmethod
    def invoke(image: Image, power: float) -> Image | None:
        try:
            return Power().preprocess(image, power)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.point.Log``: ``log10(1 + pixel)`` through the
double view, on a deep copy of the input."""

from __future__ import annotations

import traceback

from ..._javacompat import jlog10_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Log(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        self.output.setDoubleArray(jlog10_arr(1.0 + self.output.getDoubleArray()))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return Log().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.point.Inversion``: ``1.0 - pixel`` through the
double view, on a deep copy of the input."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Inversion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        self.output.setDoubleArray(1.0 - self.output.getDoubleArray())

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return Inversion().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.arithmetic.Average``: ``(pixel1 + pixel2) / 2``
through the double view.

Output type: a ``DoubleImage`` if either input is one, otherwise the same
type as ``input1`` (same ``instanceof`` pattern as ``Addition``/``Subtraction``).
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class Average(Algorithm):
    def __init__(self):
        super().__init__()
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input1,input2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("Arguments must be of same dimensions!")

        if isinstance(self.input1, DoubleImage) or isinstance(self.input2, DoubleImage):
            self.output = DoubleImage(self.input1, False)
        else:
            self.output = self.input1.newInstance(False)

        p1 = self.input1.getDoubleArray()
        p2 = self.input2.getDoubleArray()
        with np.errstate(invalid="ignore"):
            self.output.setDoubleArray((p1 + p2) / 2.0)

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return Average().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

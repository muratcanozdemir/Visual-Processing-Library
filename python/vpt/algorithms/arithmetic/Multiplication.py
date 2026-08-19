"""Port of ``vpt.algorithms.arithmetic.Multiplication``: ``min(1.0, pixel1 *
pixel2)`` through the double view.

Output type: a ``DoubleImage`` if either input is one, otherwise the same
type as ``inputImage1`` (same ``instanceof`` pattern as ``Addition``/``Average``/
``Subtraction``).

``min(1.0, x)`` has the constant first, same shape as ``AdditionC``: it
returns ``x`` only when ``x < 1.0``, otherwise ``1.0`` -- a NaN product is
clamped to ``1.0`` rather than propagated, unlike ``np.minimum``.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class Multiplication(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage1: Image | None = None
        self.inputImage2: Image | None = None
        self.inputFields = "inputImage1,inputImage2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.inputImage1.hasSameDimensionsAs(self.inputImage2):
            raise GlobalException("Arguments must be of same dimensions!")

        if isinstance(self.inputImage1, DoubleImage) or isinstance(self.inputImage2, DoubleImage):
            self.output = DoubleImage(self.inputImage1, False)
        else:
            self.output = self.inputImage1.newInstance(False)

        with np.errstate(invalid="ignore"):
            x = self.inputImage1.getDoubleArray() * self.inputImage2.getDoubleArray()
        self.output.setDoubleArray(np.where(x < 1.0, x, 1.0))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return Multiplication().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

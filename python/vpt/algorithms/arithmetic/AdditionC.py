"""Port of ``vpt.algorithms.arithmetic.AdditionC``: ``min(1.0, pixel + constant)``
through the double view.

``min(1.0, x)`` is Python two-arg ``min`` with the constant *first*: it
returns ``x`` only when ``x < 1.0``, otherwise ``1.0`` -- including when
``x`` is NaN (``x < 1.0`` is false for NaN, so the constant wins and NaN is
clamped away to ``1.0``, unlike ``np.minimum`` which would propagate NaN).
``np.where(x < 1.0, x, 1.0)`` reproduces that exactly."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class AdditionC(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.output: Image | None = None
        self.constant: float | None = None
        self.inputFields = "inputImage,constant"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage.newInstance(False)

        with np.errstate(invalid="ignore"):
            x = self.inputImage.getDoubleArray() + self.constant
        self.output.setDoubleArray(np.where(x < 1.0, x, 1.0))

    @staticmethod
    def invoke(image: Image, constant: float) -> Image | None:
        try:
            return AdditionC().preprocess(image, constant)
        except GlobalException:
            traceback.print_exc()
        return None

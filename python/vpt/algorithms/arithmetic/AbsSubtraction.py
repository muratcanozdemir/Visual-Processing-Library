"""Port of ``vpt.algorithms.arithmetic.AbsSubtraction``.

Elementwise ``|a - b|`` over two same-dimensioned images, computed and stored
through the double view so it works uniformly across all four pixel types.
``inf - inf`` (opposite-signed infinities) legitimately produces NaN, same
as the scalar version; NumPy's ``invalid value encountered in subtract``
warning for that case is suppressed rather than left to trip this project's
``filterwarnings = error::RuntimeWarning``.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class AbsSubtraction(Algorithm):
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

        self.output = self.inputImage1.newInstance(False)

        p1 = self.inputImage1.getDoubleArray()
        p2 = self.inputImage2.getDoubleArray()
        with np.errstate(invalid="ignore"):
            self.output.setDoubleArray(np.abs(p1 - p2))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return AbsSubtraction().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

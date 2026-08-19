"""Port of ``vpt.algorithms.linear.MeanFilter``: convolve with a flat
``size x size`` box kernel (all-ones, i.e. an unweighted local average)."""

from __future__ import annotations

import sys
import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from .Convolution import Convolution


class MeanFilter(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage: Image | None = None
        self.size: int | None = None
        self.inputFields = "inputImage,size"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.size < 3:
            self.size = 3
            print("Warning: kernel size too small, increased to 3", file=sys.stderr)

        kernel = NonFlatSE.box(self.size, self.size, 1.0)
        self.output = Convolution.invoke(self.inputImage, kernel)

    @staticmethod
    def invoke(image: Image, size: int) -> Image | None:
        try:
            return MeanFilter().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

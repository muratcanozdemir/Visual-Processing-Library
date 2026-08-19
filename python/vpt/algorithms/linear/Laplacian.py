"""Port of ``vpt.algorithms.linear.Laplacian``: convolve with the classic
4-neighbour Laplacian kernel ``[[0,-1,0],[-1,4,-1],[0,-1,0]]``, optionally
contrast-stretched."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ..histogram.ContrastStretch import ContrastStretch
from .Convolution import Convolution


class Laplacian(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.normalized: bool | None = None
        self.inputFields = "input,normalized"
        self.outputFields = "output"

    def execute(self) -> None:
        kernel = NonFlatSE(
            Point(1, 1),
            [[0.0, -1.0, 0.0], [-1.0, 4.0, -1.0], [0.0, -1.0, 0.0]],
        )
        self.output = Convolution.invoke(self.input, kernel)
        if self.normalized:
            self.output = ContrastStretch.invoke(self.output)

    @staticmethod
    def invoke(image: Image, normalized: bool) -> Image | None:
        try:
            return Laplacian().preprocess(image, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

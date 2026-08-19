"""Port of ``vpt.algorithms.texture.SmoothnessFactor``: rank-filters a
mono-channel image with a square SE at the median rank (``size*size // 2``)
and returns the ratio of the filtered image's volume to the original's."""

from __future__ import annotations

import traceback

from ..._javacompat import idiv, jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ...util.Tools import Tools
from ..mm.gray.GRankFilter import GRankFilter


class SmoothnessFactor(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self.inputFields = "input, size"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("The input image must be grayscale")

        filtered = GRankFilter.invoke(
            self.input, FlatSE.square(self.size), idiv(self.size * self.size, 2)
        )

        self.output = jdiv(Tools.volume(filtered, 0), Tools.volume(self.input, 0))

    @staticmethod
    def invoke(image: Image, size: int) -> float | None:
        try:
            return SmoothnessFactor().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

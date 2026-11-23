"""Port of ``vpt.algorithms.mm.gray.GInterpolation``: builds a
``2**length + 1``-frame animation between two images by recursive
divide-and-conquer morphological-median interpolation -- each call fills the
midpoint of a still-empty ``[index1, index2]`` span with the median of its
two (by now already-filled) endpoints, then recurses on each half."""

from __future__ import annotations

import traceback

from ...._javacompat import jint, jpow
from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from .GMedianImage import GMedianImage


class GInterpolation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[Image | None] | None = None
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.length: int | None = None
        self.inputFields = "input1, input2, length"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("The arguments must be of the same dimensions!")

        if self.length < 0:
            raise GlobalException("Invalid sequence length!")

        length = 2 * jint(jpow(2.0, self.length)) + 1

        self.output = [None] * length

        self.output[0] = self.input1
        self.output[length - 1] = self.input2

        self._fillIntermediateFrames(self.output, 0, length - 1)

    def _fillIntermediateFrames(self, output: list[Image | None], index1: int, index2: int) -> None:
        index = index1 + (index2 - index1) // 2
        if output[index] is not None:
            return
        tmp = GMedianImage.invoke(output[index1], output[index2])
        output[index] = tmp

        self._fillIntermediateFrames(output, index1, index)
        self._fillIntermediateFrames(output, index, index2)

    @staticmethod
    def invoke(image: Image, image2: Image, length: int) -> list[Image | None] | None:
        try:
            return GInterpolation().preprocess(image, image2, length)
        except GlobalException:
            traceback.print_exc()
        return None

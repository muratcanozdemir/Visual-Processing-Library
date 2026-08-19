"""Port of ``vpt.algorithms.shape.RegionalMomentsOfInertia``: slices a
shape into ``slides`` horizontal bands and reports each band's own
``MeanDistanceToCentroid``."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..geometric.Crop import Crop
from ._util import booleanBBox
from .MeanDistanceToCentroid import MeanDistanceToCentroid


class RegionalMomentsOfInertia(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.slides: int | None = None
        self.inputFields = "input, slides"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        self.output = [0.0] * self.slides

        top, bottom, _, _ = booleanBBox(self.input)

        diff = bottom - top
        slideWidth = idiv(diff, self.slides)
        for i in range(self.slides):
            tmp = Crop.invoke(self.input, Point(0, top), Point(xdim - 1, top + slideWidth))
            top += slideWidth
            self.output[i] = MeanDistanceToCentroid.invoke(tmp)

    @staticmethod
    def invoke(img: Image, slides: int) -> list[float] | None:
        try:
            return RegionalMomentsOfInertia().preprocess(img, slides)
        except GlobalException:
            traceback.print_exc()
        return None

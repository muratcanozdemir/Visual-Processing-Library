"""Port of ``vpt.algorithms.shape.NormalizedWidthFactor``: byte-for-byte
identical to ``AreaWidthFactor`` in the Java (not a bug -- a second,
independently-defined class with the same body, same category as
``linear.RotationInvariantGabor``/``Gabor``). Kept as its own duplicate
copy here too, mirroring that structure rather than delegating."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv, jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..geometric.Crop import Crop
from ._util import booleanBBox


class NormalizedWidthFactor(Algorithm):
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

        area = Tools.volume(self.input, 0)

        diff = bottom - top
        slideWidth = idiv(diff, self.slides)
        for i in range(self.slides):
            tmp = Crop.invoke(self.input, Point(0, top), Point(xdim - 1, top + slideWidth))
            top += slideWidth

            self.output[i] = jdiv(Tools.volume(tmp, 0), area)

    @staticmethod
    def invoke(img: Image, slides: int) -> list[float] | None:
        try:
            return NormalizedWidthFactor().preprocess(img, slides)
        except GlobalException:
            traceback.print_exc()
        return None

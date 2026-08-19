"""Port of ``vpt.algorithms.shape.WidthFactor``: slices a shape into
``slides`` horizontal bands and reports each band's own width relative to
the shape's overall max width.

**Preserved bug, bytecode-confirmed** (`javap`: `isub`/`idiv`/`i2d` in that
order): the final ratio ``(_right - _left) / maxWidth`` is computed in
Java ``int`` arithmetic -- all three operands are ``int`` locals -- and
only widened to ``double`` *after* the truncating division. Since a single
band's width can't exceed the overall max width, this comes out ``0`` for
almost every band (``1`` only on the rare band whose width exactly equals
the max). Routed through ``idiv`` to match, not "fixed" into the true
ratio the name implies."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..geometric.Crop import Crop
from ._util import booleanBBox


class WidthFactor(Algorithm):
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

        top, bottom, left, right = booleanBBox(self.input)

        maxWidth = right - left

        diff = bottom - top
        slideWidth = idiv(diff, self.slides)
        for i in range(self.slides):
            tmp = Crop.invoke(self.input, Point(0, top), Point(xdim - 1, top + slideWidth))
            top += slideWidth

            _, _, _left, _right = booleanBBox(tmp)

            self.output[i] = float(idiv(_right - _left, maxWidth))

    @staticmethod
    def invoke(img: Image, slides: int) -> list[float] | None:
        try:
            return WidthFactor().preprocess(img, slides)
        except GlobalException:
            traceback.print_exc()
        return None

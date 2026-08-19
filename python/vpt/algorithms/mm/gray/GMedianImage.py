"""Port of ``vpt.algorithms.mm.gray.GMedianImage``: the morphological median
of two images -- the fixpoint of alternately dilating the pointwise minimum
and eroding the pointwise maximum, then taking their pointwise maximum
against the previous iterate, until stable."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.Equal import Equal
from ...arithmetic.Maximum import Maximum
from ...arithmetic.Minimum import Minimum
from .GDilation import GDilation
from .GErosion import GErosion


class GMedianImage(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.inputFields = "input1, input2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("The arguments must be of the same dimensions!")

        z = Minimum.invoke(self.input1, self.input2)
        w = Maximum.invoke(self.input1, self.input2)
        m = Minimum.invoke(self.input1, self.input2)
        se = FlatSE.square(3)
        while True:
            tmp = m
            z = GDilation.invoke(z, se)
            w = GErosion.invoke(w, se)
            m = Maximum.invoke(Minimum.invoke(z, w), tmp)
            if Equal.invoke(m, tmp):
                break
        self.output = m

    @staticmethod
    def invoke(image: Image, image2: Image) -> Image | None:
        try:
            return GMedianImage().preprocess(image, image2)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.shape.Compactness``: the classic
``perimeter^2 / area`` shape compactness measure."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..mm.binary.BInternGradient import BInternGradient


class Compactness(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        perimeter = Tools.volume(borderImage, 0)
        area = Tools.volume(self.input, 0)

        self.output = jdiv(perimeter * perimeter, area)

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Compactness().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

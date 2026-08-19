"""Port of ``vpt.algorithms.mm.gray.geo.GRegionalMinima``: ``GRegionalMaxima``
of the inverted image (regional minima of ``f`` are regional maxima of
``1-f``)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....arithmetic.Subtraction import Subtraction
from ....arithmetic.SubtractionC import SubtractionC
from ....point.Inversion import Inversion
from .FastGrayReconstruction import CONNEXITY8, FastGrayReconstruction

_BYTE_TO_DOUBLE = 0.00392156862745098


class GRegionalMinima(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        self.inputImage = Inversion.invoke(self.inputImage)

        fMinusOne = SubtractionC.invoke(self.inputImage, _BYTE_TO_DOUBLE)

        fMinusOne = FastGrayReconstruction.invoke(fMinusOne, self.inputImage, CONNEXITY8, False)

        self.output = Subtraction.invoke(self.inputImage, fMinusOne)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return GRegionalMinima().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

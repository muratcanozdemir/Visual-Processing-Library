"""Port of ``vpt.algorithms.mm.gray.GLaplacian``: the morphological Laplacian,
``|external gradient - internal gradient|``."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .GExternGradient import GExternGradient
from .GInternGradient import GInternGradient


class GLaplacian(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        externGrad = GExternGradient.invoke(self.input, self.se)
        internGrad = GInternGradient.invoke(self.input, self.se)

        self.output = AbsSubtraction.invoke(externGrad, internGrad)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GLaplacian().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.gray.geo.GBotHatByReconstruction``:
|closing-by-reconstruction - input|."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.AbsSubtraction import AbsSubtraction
from .GClosingByReconstruction import GClosingByReconstruction


class GBotHatByReconstruction(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = AbsSubtraction.invoke(GClosingByReconstruction.invoke(self.input, self.se), self.input)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GBotHatByReconstruction().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

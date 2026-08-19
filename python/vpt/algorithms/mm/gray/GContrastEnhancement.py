"""Port of ``vpt.algorithms.mm.gray.GContrastEnhancement``: push bright
regions brighter and dark regions darker by adding the top-hat (bright
detail) and subtracting the bot-hat (dark detail)."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.Addition import Addition
from ...arithmetic.Subtraction import Subtraction
from .GBotHat import GBotHat
from .GTopHat import GTopHat


class GContrastEnhancement(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Addition.invoke(self.input, GTopHat.invoke(self.input, self.se))
        self.output = Subtraction.invoke(self.output, GBotHat.invoke(self.input, self.se))

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GContrastEnhancement().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

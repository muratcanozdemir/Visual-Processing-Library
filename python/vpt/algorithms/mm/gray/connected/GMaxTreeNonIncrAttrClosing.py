"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrClosing``:
``GMaxTreeNonIncrAttrOpening`` of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....point.Inversion import Inversion
from .attribute.Attribute import Attribute
from .GMaxTreeNonIncrAttrOpening import GMaxTreeNonIncrAttrOpening


class GMaxTreeNonIncrAttrClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.filteringStrategy: int | None = None
        self.inputFields = "input, crit, filteringStrategy"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(
            GMaxTreeNonIncrAttrOpening.invoke(
                Inversion.invoke(self.input), self.crit, self.filteringStrategy
            )
        )

    @staticmethod
    def invoke(image: Image, c: Attribute, filteringStrategy: int | None = None) -> Image | None:
        try:
            return GMaxTreeNonIncrAttrClosing().preprocess(image, c, filteringStrategy)
        except GlobalException:
            traceback.print_exc()
        return None

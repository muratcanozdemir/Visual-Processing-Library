"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeIncrAttrClosing``:
``GMaxTreeIncrAttrOpening`` of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....point.Inversion import Inversion
from .attribute.Attribute import Attribute
from .GMaxTreeIncrAttrOpening import GMaxTreeIncrAttrOpening


class GMaxTreeIncrAttrClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.inputFields = "input, crit"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(
            GMaxTreeIncrAttrOpening.invoke(Inversion.invoke(self.input), self.crit)
        )

    @staticmethod
    def invoke(image: Image, c: Attribute) -> Image | None:
        try:
            return GMaxTreeIncrAttrClosing().preprocess(image, c)
        except GlobalException:
            traceback.print_exc()
        return None

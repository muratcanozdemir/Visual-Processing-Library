"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeIncrMultiAttrClosing``:
``GMaxTreeIncrMultiAttrOpening`` of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....point.Inversion import Inversion
from .attribute.Attribute import Attribute
from .GMaxTreeIncrMultiAttrOpening import GMaxTreeIncrMultiAttrOpening


class GMaxTreeIncrMultiAttrClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.channel: int | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.inputFields = "input, channel, crit"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(
            GMaxTreeIncrMultiAttrOpening.invoke(Inversion.invoke(self.input), self.channel, self.crit)
        )

    @staticmethod
    def invoke(image: Image, channel: int, c: Attribute) -> Image | None:
        try:
            return GMaxTreeIncrMultiAttrClosing().preprocess(image, channel, c)
        except GlobalException:
            traceback.print_exc()
        return None

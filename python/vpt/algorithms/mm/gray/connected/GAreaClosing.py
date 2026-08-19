"""Port of ``vpt.algorithms.mm.gray.connected.GAreaClosing``: ``GAreaOpening``
of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....point.Inversion import Inversion
from .GAreaOpening import GAreaOpening


class GAreaClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.area: int | None = None
        self.output: Image | None = None
        self.inputFields = "input,area"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(GAreaOpening.invoke(Inversion.invoke(self.input), self.area))

    @staticmethod
    def invoke(image: Image, area: int) -> Image | None:
        try:
            return GAreaClosing().preprocess(image, area)
        except GlobalException:
            traceback.print_exc()
        return None

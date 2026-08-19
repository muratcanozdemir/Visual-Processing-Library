"""Port of ``vpt.algorithms.mm.gray.floating.GFloatingOpening``: erosion then
dilation, both floating-SE."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FloatingFlatSE
from .GFloatingDilation import GFloatingDilation
from .GFloatingErosion import GFloatingErosion


class GFloatingOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FloatingFlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = GFloatingErosion.invoke(self.input, self.se)
        self.output = GFloatingDilation.invoke(self.output, self.se)

    @staticmethod
    def invoke(image: Image, se: FloatingFlatSE) -> Image | None:
        try:
            return GFloatingOpening().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

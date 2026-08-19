"""Port of ``vpt.algorithms.mm.gray.adaptive.AClosing``: dilation then
erosion, both by the same per-pixel adaptive SE grid."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from .ADilation import ADilation
from .AErosion import AErosion


class AClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: list[list[FlatSE]] | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = ADilation.invoke(self.input, self.se)
        self.output = AErosion.invoke(self.output, self.se)

    @staticmethod
    def invoke(image: Image, se: list[list[FlatSE]]) -> Image | None:
        try:
            return AClosing().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

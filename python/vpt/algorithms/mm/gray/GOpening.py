"""Port of ``vpt.algorithms.mm.gray.GOpening``: erosion then dilation."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from .GDilation import GDilation
from .GErosion import GErosion


class GOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = GErosion.invoke(self.input, self.se)
        self.output = GDilation.invoke(self.output, self.se)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GOpening().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

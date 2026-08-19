"""Port of ``vpt.algorithms.mm.binary.BOpening``: erosion then dilation."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from .BDilation import BDilation
from .BErosion import BErosion


class BOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = BErosion.invoke(self.input, self.se)
        self.output = BDilation.invoke(self.output, self.se)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return BOpening().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

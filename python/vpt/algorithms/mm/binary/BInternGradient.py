"""Port of ``vpt.algorithms.mm.binary.BInternGradient``: |input - erosion|.

Unlike ``GInternGradient``, this one carries its own ``flag`` field and
forwards it to the 3-arg ``BErosion.invoke``, so the border-handling choice
made by the caller of ``BInternGradient`` propagates through to the erosion.
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .BErosion import BErosion


class BInternGradient(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.flag: bool | None = None
        self.inputFields = "input, se, flag"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = AbsSubtraction.invoke(
            self.input, BErosion.invoke(self.input, self.se, self.flag)
        )

    @staticmethod
    def invoke(image: Image, se: FlatSE, flag: bool = True) -> Image | None:
        try:
            return BInternGradient().preprocess(image, se, flag)
        except GlobalException:
            traceback.print_exc()
        return None

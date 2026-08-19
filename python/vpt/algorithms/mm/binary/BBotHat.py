"""Port of ``vpt.algorithms.mm.binary.BBotHat``: |closing - input|."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .BClosing import BClosing


class BBotHat(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = AbsSubtraction.invoke(BClosing.invoke(self.input, self.se), self.input)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return BBotHat().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

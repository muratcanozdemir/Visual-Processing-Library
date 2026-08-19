"""Port of ``vpt.algorithms.mm.multi.MBotHat``: |closing - input|."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import Ordering
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .MClosing import MClosing


class MBotHat(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.or_: Ordering | None = None
        self.inputFields = "input, se, or_"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = AbsSubtraction.invoke(MClosing.invoke(self.input, self.se, self.or_), self.input)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: Ordering) -> Image | None:
        try:
            return MBotHat().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.multi.MOCCO``: the average of an
open-then-close and a close-then-open, through the double view."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import Ordering
from ....util.se import FlatSE
from .MClosing import MClosing
from .MOpening import MOpening


class MOCCO(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.or_: Ordering | None = None
        self.inputFields = "input,se,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        tmp1 = MOpening.invoke(self.input, self.se, self.or_)
        tmp1 = MClosing.invoke(tmp1, self.se, self.or_)

        self.output = MClosing.invoke(self.input, self.se, self.or_)
        self.output = MOpening.invoke(self.output, self.se, self.or_)

        # Vectorized equivalent of the per-pixel average below: a pure
        # elementwise formula with no cross-pixel dependency.
        p1 = tmp1.getDoubleArray()
        p2 = self.output.getDoubleArray()
        self.output.setDoubleArray((p1 + p2) / 2.0)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: Ordering) -> Image | None:
        try:
            return MOCCO().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

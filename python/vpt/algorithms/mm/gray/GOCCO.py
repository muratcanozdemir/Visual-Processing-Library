"""Port of ``vpt.algorithms.mm.gray.GOCCO``: average of open-then-close and
close-then-open, a symmetric alternating filter that avoids OCCO's/COCO's
bias toward either bright or dark structures alone."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from .GClosing import GClosing
from .GOpening import GOpening


class GOCCO(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        tmp1 = GOpening.invoke(self.input, self.se)
        tmp1 = GClosing.invoke(tmp1, self.se)

        self.output = GClosing.invoke(self.input, self.se)
        self.output = GOpening.invoke(self.output, self.se)

        # Vectorized equivalent of the per-pixel average below: a pure
        # elementwise formula with no cross-pixel dependency.
        p1 = tmp1.getDoubleArray()
        p2 = self.output.getDoubleArray()
        self.output.setDoubleArray((p1 + p2) / 2.0)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GOCCO().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

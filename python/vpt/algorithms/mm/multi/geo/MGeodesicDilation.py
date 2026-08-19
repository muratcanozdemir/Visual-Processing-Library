"""Port of ``vpt.algorithms.mm.multi.geo.MGeodesicDilation``: dilate the
input by one step under a caller-supplied ``AlgebraicOrdering``, then clamp
it back to the mask (``vectorial min(dilate, mask)``)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .....util.se import FlatSE
from ....arithmetic.VectorialMinimum import VectorialMinimum
from ..MDilation import MDilation


class MGeodesicDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input,se,mask,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = VectorialMinimum.invoke(
            MDilation.invoke(self.input, self.se, self.or_), self.mask, self.or_
        )

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image, or_: AlgebraicOrdering) -> Image | None:
        try:
            return MGeodesicDilation().preprocess(image, se, mask, or_)
        except GlobalException:
            traceback.print_exc()
        return None

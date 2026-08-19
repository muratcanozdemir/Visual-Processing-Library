"""Port of ``vpt.algorithms.mm.multi.connected.MMaxTreeIncrAttrClosing``:
``MMaxTreeIncrAttrOpening`` of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from ....point.Inversion import Inversion
from .attribute.MultiAttribute import MultiAttribute
from .MMaxTreeIncrAttrOpening import MMaxTreeIncrAttrOpening


class MMaxTreeIncrAttrClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: MultiAttribute | None = None
        self.output: Image | None = None
        self.vo: AlgebraicOrdering | None = None
        self.inputFields = "input, crit, vo"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(
            MMaxTreeIncrAttrOpening.invoke(Inversion.invoke(self.input), self.crit, self.vo)
        )

    @staticmethod
    def invoke(image: Image, c: MultiAttribute, vo: AlgebraicOrdering) -> Image | None:
        try:
            return MMaxTreeIncrAttrClosing().preprocess(image, c, vo)
        except GlobalException:
            traceback.print_exc()
        return None

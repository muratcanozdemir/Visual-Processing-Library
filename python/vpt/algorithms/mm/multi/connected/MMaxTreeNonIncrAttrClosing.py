"""Port of ``vpt.algorithms.mm.multi.connected.MMaxTreeNonIncrAttrClosing``:
``MMaxTreeNonIncrAttrOpening`` of the inverted image, inverted back."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from ....point.Inversion import Inversion
from .attribute.MultiAttribute import MultiAttribute
from .MMaxTreeNonIncrAttrOpening import MMaxTreeNonIncrAttrOpening


class MMaxTreeNonIncrAttrClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: MultiAttribute | None = None
        self.output: Image | None = None
        self.vo: AlgebraicOrdering | None = None
        self.filteringStrategy: int | None = None
        self.inputFields = "input, crit, vo, filteringStrategy"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Inversion.invoke(
            MMaxTreeNonIncrAttrOpening.invoke(
                Inversion.invoke(self.input), self.crit, self.vo, self.filteringStrategy
            )
        )

    @staticmethod
    def invoke(
        image: Image,
        c: MultiAttribute,
        vo: AlgebraicOrdering,
        filteringStrategy: int | None = None,
    ) -> Image | None:
        try:
            return MMaxTreeNonIncrAttrClosing().preprocess(image, c, vo, filteringStrategy)
        except GlobalException:
            traceback.print_exc()
        return None

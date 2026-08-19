"""Port of ``vpt.algorithms.mm.multi.geo.MClosingByReconstructionClassic``:
dilate, then reconstruct by the naive fixpoint-loop ``MReconstructionByErosion``
(with a fixed ``FlatSE.square(3)`` connectivity) instead of the FIFO-based
``MFastReconstruction``."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .....util.se import FlatSE
from ..MDilation import MDilation
from .MReconstructionByErosion import MReconstructionByErosion


class MClosingByReconstructionClassic(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input,se,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = MDilation.invoke(self.input, self.se, self.or_)
        self.output = MReconstructionByErosion.invoke(marker, FlatSE.square(3), self.input, self.or_)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: AlgebraicOrdering) -> Image | None:
        try:
            return MClosingByReconstructionClassic().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

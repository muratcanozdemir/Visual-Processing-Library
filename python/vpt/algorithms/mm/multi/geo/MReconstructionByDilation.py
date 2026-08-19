"""Port of ``vpt.algorithms.mm.multi.geo.MReconstructionByDilation``: repeated
vectorial geodesic dilation until the fixpoint (the naive, non-FIFO analogue
of ``MFastReconstruction`` -- used by ``MOpeningByReconstructionClassic``)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .MGeodesicDilation import MGeodesicDilation


class MReconstructionByDilation(Algorithm):
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
        self.output = self.input
        while True:
            tmp = self.output
            self.output = MGeodesicDilation.invoke(tmp, self.se, self.mask, self.or_)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image, or_: AlgebraicOrdering) -> Image | None:
        try:
            return MReconstructionByDilation().preprocess(image, se, mask, or_)
        except GlobalException:
            traceback.print_exc()
        return None

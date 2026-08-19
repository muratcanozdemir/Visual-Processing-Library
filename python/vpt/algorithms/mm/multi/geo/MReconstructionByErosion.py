"""Port of ``vpt.algorithms.mm.multi.geo.MReconstructionByErosion``: repeated
vectorial geodesic erosion until the fixpoint (the naive, non-FIFO analogue
of ``MFastReconstruction`` -- used by ``MClosingByReconstructionClassic``)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .MGeodesicErosion import MGeodesicErosion


class MReconstructionByErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "inputImage,se,mask,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage
        while True:
            tmp = self.output
            self.output = MGeodesicErosion.invoke(tmp, self.se, self.mask, self.or_)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image, or_: AlgebraicOrdering) -> Image | None:
        try:
            return MReconstructionByErosion().preprocess(image, se, mask, or_)
        except GlobalException:
            traceback.print_exc()
        return None

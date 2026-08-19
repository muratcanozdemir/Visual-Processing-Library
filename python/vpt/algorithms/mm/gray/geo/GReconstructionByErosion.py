"""Port of ``vpt.algorithms.mm.gray.geo.GReconstructionByErosion``: repeated
geodesic erosion until the fixpoint."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .GGeodesicErosion import GGeodesicErosion


class GReconstructionByErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage,se,mask"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage
        while True:
            tmp = self.output
            self.output = GGeodesicErosion.invoke(tmp, self.se, self.mask)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return GReconstructionByErosion().preprocess(image, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

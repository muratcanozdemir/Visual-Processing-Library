"""Port of ``vpt.algorithms.mm.gray.geo.GGeodesicErosion``: erode the input
by one step, then clamp it back to the mask (``max(erode, mask)``)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Maximum import Maximum
from ..GErosion import GErosion


class GGeodesicErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage,se,mask"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Maximum.invoke(GErosion.invoke(self.inputImage, self.se), self.mask)

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return GGeodesicErosion().preprocess(image, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

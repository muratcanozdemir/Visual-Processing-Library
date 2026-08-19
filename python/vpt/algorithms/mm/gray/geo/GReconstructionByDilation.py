"""Port of ``vpt.algorithms.mm.gray.geo.GReconstructionByDilation``: repeated
geodesic dilation until the fixpoint. On ``ByteImage``/``DoubleImage``
marker/mask -- unlike ``mm.binary.geo.BReconstructionByDilation`` on
``BooleanImage`` -- ``Minimum`` is genuine numeric min, so this terminates
normally."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .GGeodesicDilation import GGeodesicDilation


class GReconstructionByDilation(Algorithm):
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
            self.output = GGeodesicDilation.invoke(tmp, self.se, self.mask)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return GReconstructionByDilation().preprocess(image, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

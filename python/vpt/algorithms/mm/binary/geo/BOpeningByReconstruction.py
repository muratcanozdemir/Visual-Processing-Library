"""Port of ``vpt.algorithms.mm.binary.geo.BOpeningByReconstruction``: erode,
then reconstruct by dilation under the original image.

HAZARD: on ``BooleanImage`` input -- the type this whole ``mm.binary``
package exists for -- ``BReconstructionByDilation`` hits the documented
NOR/non-convergence hazard and never returns. See that module's docstring.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ..BErosion import BErosion
from .BReconstructionByDilation import BReconstructionByDilation


class BOpeningByReconstruction(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = BErosion.invoke(self.input, self.se)

        self.output = BReconstructionByDilation.invoke(marker, self.se, self.input)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return BOpeningByReconstruction().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

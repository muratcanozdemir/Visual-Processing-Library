"""Port of ``vpt.algorithms.mm.binary.geo.BClosingByReconstruction``: dilate,
then reconstruct by erosion under the original image.

HAZARD: on ``BooleanImage`` input -- the type this whole ``mm.binary``
package exists for -- ``BReconstructionByErosion`` hits the documented
NAND/non-convergence hazard and never returns. See that module's docstring.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ..BDilation import BDilation
from .BReconstructionByErosion import BReconstructionByErosion


class BClosingByReconstruction(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = BDilation.invoke(self.input, self.se)

        self.output = BReconstructionByErosion.invoke(marker, self.se, self.input)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return BClosingByReconstruction().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

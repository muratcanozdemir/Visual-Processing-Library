"""Port of ``vpt.algorithms.mm.binary.geo.BGeodesicErosion``: erode the
input by one step, then clamp it back to the mask (``max(erode, mask)``).

HAZARD (original-library defect, not a porting bug), the ``Maximum`` dual of
``BGeodesicDilation``'s documented ``Minimum``/NOR hazard: on a
``BooleanImage``, whose ``getDouble`` is inverted (True -> 0.0, False -> 1.0)
while ``setDouble`` is not (``p > 0.0`` -> True), ``Maximum`` computes NAND
instead of OR: (True,True)->False, (False,False)->True,
(True,False)/(False,True)->True. Verified directly against this port's
``Maximum``. See ``BReconstructionByErosion`` for the consequence.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Maximum import Maximum
from ..BErosion import BErosion


class BGeodesicErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "input,se,mask"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Maximum.invoke(BErosion.invoke(self.input, self.se), self.mask)

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return BGeodesicErosion().preprocess(image, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

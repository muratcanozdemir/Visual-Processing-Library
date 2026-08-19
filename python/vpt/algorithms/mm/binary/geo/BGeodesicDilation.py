"""Port of ``vpt.algorithms.mm.binary.geo.BGeodesicDilation``: dilate the
marker by one step, then clamp it back to the mask (``min(dilate, mask)``).

HAZARD (original-library defect, not a porting bug): ``Minimum`` compares and
writes through the generic double view. On a ``BooleanImage``, whose
``getDouble`` is inverted (True -> 0.0, False -> 1.0) while ``setDouble`` is
not (``p > 0.0`` -> True), this makes ``Minimum`` compute NOR instead of AND:
(True,True)->False, (False,False)->True, (True,False)/(False,True)->False.
Verified directly against this port's ``Minimum``. Since ``Minimum`` and
``BooleanImage`` are both faithful transcriptions of their Java sources, the
same NOR behaviour occurs in the original library, not just here. See
``BReconstructionByDilation`` for the consequence.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Minimum import Minimum
from ..BDilation import BDilation


class BGeodesicDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "input,se,mask"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = Minimum.invoke(BDilation.invoke(self.input, self.se), self.mask)

    @staticmethod
    def invoke(image: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return BGeodesicDilation().preprocess(image, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

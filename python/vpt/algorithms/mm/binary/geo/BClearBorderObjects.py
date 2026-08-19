"""Port of ``vpt.algorithms.mm.binary.geo.BClearBorderObjects``: removes any
foreground object that touches the image border. Reconstructs a marker
that starts out true only where the input is true *on* the border, dilating
inward to recover the full extent of every border-touching object, then
subtracts that from the input -- leaving only the objects that never
touched the border.

HAZARD: ``marker`` is a ``BooleanImage`` and ``self.input`` is expected to be
one too (this whole ``mm.binary`` package exists for binary masks), which
drives ``BReconstructionByDilation`` down the documented NOR/non-convergence
hazard -- see that module's docstring. As far as this port can determine,
**this hangs forever on its intended input**, mirroring ``BFillHole``.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....BooleanImage import BooleanImage
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Subtraction import Subtraction
from .BReconstructionByDilation import BReconstructionByDilation


class BClearBorderObjects(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = BooleanImage(self.input, False)

        xdim = marker.getXDim()
        ydim = marker.getYDim()
        cdim = marker.getCDim()
        for c in range(cdim):
            for x in range(xdim):
                for y in range(ydim):
                    if x == 0 or y == 0 or x == xdim - 1 or y == ydim - 1:
                        marker.setXYCBoolean(x, y, c, self.input.getXYCBoolean(x, y, c))
                    else:
                        marker.setXYCBoolean(x, y, c, False)

        self.output = BReconstructionByDilation.invoke(marker, FlatSE.square(3), self.input)

        self.output = Subtraction.invoke(self.input, self.output)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return BClearBorderObjects().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

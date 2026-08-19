"""Port of ``vpt.algorithms.mm.binary.geo.BFillHole``.

Fills enclosed background holes: reconstructs the inverted input by dilation
from a marker that is true only on the image border, then inverts back.
Anything not reachable from the border by dilation within the inverted input
(i.e. an enclosed hole) ends up left out of the reconstruction and so comes
back ``True`` after the final inversion.

HAZARD: ``marker`` here is always a ``BooleanImage`` (constructed explicitly
above), and ``mask`` (``Inversion.invoke(self.input)``) is a ``BooleanImage``
too whenever ``self.input`` is -- which is BFillHole's whole intended use
case, since it exists to fill holes in binary masks. That drives
``BReconstructionByDilation`` down the boolean path documented in its
docstring as non-terminating (``Minimum`` computes NOR instead of AND on
``BooleanImage``s). In other words: as far as this port can determine,
**``BFillHole.invoke`` hangs forever on its primary intended input**, and
this is expected to reproduce against the Java oracle rather than be a
porting bug, since every piece it composes (``Minimum``, ``Equal``,
``BooleanImage``) is a faithful transcription verified independently. Not
exercised by any oracle harness (``oracle/Recovered.java`` only calls
``ConvexHull`` with ``filled=false``, which never reaches this code path).
Confirm against a JDK before relying on this method; do not call it as part
of a golden/test driver without a hang guard.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....BooleanImage import BooleanImage
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....point.Inversion import Inversion
from .BReconstructionByDilation import BReconstructionByDilation


class BFillHole(Algorithm):
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
                    onBorder = x == 0 or y == 0 or x == xdim - 1 or y == ydim - 1
                    marker.setXYCBoolean(x, y, c, onBorder)

        self.output = BReconstructionByDilation.invoke(marker, FlatSE.cross(3), Inversion.invoke(self.input))
        self.output = Inversion.invoke(self.output)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return BFillHole().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

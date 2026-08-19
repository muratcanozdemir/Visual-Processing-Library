"""Port of ``vpt.algorithms.mm.binary.geo.BReconstructionByDilation``:
repeated geodesic dilation of ``marker`` under ``mask`` until it stops
changing (idempotence -- the reconstruction fixpoint).

HAZARD, inherited from ``BGeodesicDilation``: on ``BooleanImage`` marker/mask
(as opposed to ``ByteImage``/``DoubleImage``), the ``Minimum`` step it relies
on computes NOR rather than AND (see ``BGeodesicDilation``'s docstring for
the mechanism). NOR is not monotonically decreasing/increasing the way a
real reconstruction step needs to be to guarantee convergence, so this loop
can oscillate and never satisfy the ``Equal`` fixpoint check -- an infinite
loop, confirmed empirically against this port and expected to reproduce
identically against the Java oracle, since nothing here diverges from a
faithful transcription. This is exactly the path ``BFillHole`` (and by
extension ``ConvexHull(..., filled=True)``) drives with BooleanImage
marker/mask; do not call this with boolean-typed images.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .BGeodesicDilation import BGeodesicDilation


class BReconstructionByDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.marker: Image | None = None
        self.mask: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "marker,se,mask"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.marker
        while True:
            tmp = self.output
            self.output = BGeodesicDilation.invoke(tmp, self.se, self.mask)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(marker: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return BReconstructionByDilation().preprocess(marker, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

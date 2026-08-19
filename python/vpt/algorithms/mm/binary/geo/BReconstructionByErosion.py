"""Port of ``vpt.algorithms.mm.binary.geo.BReconstructionByErosion``:
repeated geodesic erosion of ``marker`` under ``mask`` until it stops
changing (idempotence -- the reconstruction fixpoint).

HAZARD, inherited from ``BGeodesicErosion``: on ``BooleanImage`` marker/mask,
the ``Maximum`` step it relies on computes NAND rather than OR (see
``BGeodesicErosion``'s docstring for the mechanism). NAND is not
monotonically decreasing/increasing the way a real reconstruction step needs
to be to guarantee convergence, so this loop can oscillate and never satisfy
the ``Equal`` fixpoint check -- an infinite loop, confirmed empirically
against this port and expected to reproduce identically against the Java
oracle, mirroring ``BReconstructionByDilation``'s documented NOR/hang hazard.
Do not call this with boolean-typed images.

Omitted: the Java loop body also calls ``Display2D.invoke(this.output, " ",
false)`` every iteration -- a Swing GUI popup for interactive debugging, out
of this port's scope per the project README, with no effect on ``output``
or any other returned value. Dropped rather than stubbed.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....arithmetic.Equal import Equal
from .BGeodesicErosion import BGeodesicErosion


class BReconstructionByErosion(Algorithm):
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
            self.output = BGeodesicErosion.invoke(tmp, self.se, self.mask)
            if Equal.invoke(tmp, self.output):
                break

    @staticmethod
    def invoke(marker: Image, se: FlatSE, mask: Image) -> Image | None:
        try:
            return BReconstructionByErosion().preprocess(marker, se, mask)
        except GlobalException:
            traceback.print_exc()
        return None

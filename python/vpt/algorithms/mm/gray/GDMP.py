"""Port of ``vpt.algorithms.mm.gray.GDMP``: the grayscale Differential
Morphological Profile -- ``size`` steps of erosion-based reconstruction
(opening-side response, at output indices ``[size, 2*size)``) and ``size``
steps of dilation-based reconstruction (closing-side response, at output
indices ``[0, size)``), each step's contribution being the absolute
difference from the previous step's reconstruction. Fixed to a
``FlatSE.cross(3)`` structuring element and ``FastGrayReconstruction``'s
FIFO-based reconstruction. The vectorial analogue is ``mm/multi.MDMP``,
ported earlier this session and sharing this exact structure."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .GDilation import GDilation
from .geo.FastGrayReconstruction import CONNEXITY8, FastGrayReconstruction
from .GErosion import GErosion


class GDMP(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[Image | None] | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self.inputFields = "input,size"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.size <= 0:
            raise GlobalException("Invalid DMP size, must be > 0")

        self.output = [None] * (self.size * 2)

        se = FlatSE.cross(3)

        marker = self.input.newInstance(True)
        previous = self.input
        for i in range(self.size):
            marker = GErosion.invoke(marker, se)

            current = FastGrayReconstruction.invoke(marker, self.input, CONNEXITY8, False)

            self.output[self.size + i] = AbsSubtraction.invoke(previous, current)

            previous = current

        marker = self.input.newInstance(True)
        previous = self.input
        for i in range(self.size):
            marker = GDilation.invoke(marker, se)

            current = FastGrayReconstruction.invoke(marker, self.input, CONNEXITY8, True)

            self.output[i] = AbsSubtraction.invoke(previous, current)

            previous = current

    @staticmethod
    def invoke(image: Image, size: int) -> list[Image | None] | None:
        try:
            return GDMP().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

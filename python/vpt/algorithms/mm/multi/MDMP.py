"""Port of ``vpt.algorithms.mm.multi.MDMP``: the vectorial Differential
Morphological Profile -- ``size`` steps of erosion-based reconstruction
(closing-side response, at output indices ``[size, 2*size)``) and ``size``
steps of dilation-based reconstruction (opening-side response, at output
indices ``[0, size)``), each step's contribution being the absolute
difference from the previous step's reconstruction. Fixed to a
``FlatSE.cross(3)`` structuring element and ``MFastReconstruction``'s
FIFO-based reconstruction, unlike the ``mm.gray`` DMP family."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import AlgebraicOrdering
from ....util.se import FlatSE
from ...arithmetic.AbsSubtraction import AbsSubtraction
from .geo.MFastReconstruction import CONNEXITY8, MFastReconstruction
from .MDilation import MDilation
from .MErosion import MErosion


class MDMP(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[Image | None] | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input,size,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.size <= 0:
            raise GlobalException("Invalid DMP size, must be > 0")

        self.output = [None] * (self.size * 2)

        se = FlatSE.cross(3)

        marker = self.input.newInstance(True)
        previous = self.input
        for i in range(self.size):
            marker = MErosion.invoke(marker, se, self.or_)

            current = MFastReconstruction.invoke(marker, self.input, CONNEXITY8, False, self.or_)

            self.output[self.size + i] = AbsSubtraction.invoke(previous, current)

            previous = current

        marker = self.input.newInstance(True)
        previous = self.input
        for i in range(self.size):
            marker = MDilation.invoke(marker, se, self.or_)

            current = MFastReconstruction.invoke(marker, self.input, CONNEXITY8, True, self.or_)

            self.output[i] = AbsSubtraction.invoke(previous, current)

            previous = current

    @staticmethod
    def invoke(image: Image, size: int, or_: AlgebraicOrdering) -> list[Image | None] | None:
        try:
            return MDMP().preprocess(image, size, or_)
        except GlobalException:
            traceback.print_exc()
        return None

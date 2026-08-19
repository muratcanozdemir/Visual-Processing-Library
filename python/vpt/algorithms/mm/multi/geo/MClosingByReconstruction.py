"""Port of ``vpt.algorithms.mm.multi.geo.MClosingByReconstruction``: dilate,
then reconstruct by erosion (``inverse=True``) under the original image, via
the fast FIFO-based ``MFastReconstruction``."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .....util.se import FlatSE
from ..MDilation import MDilation
from .MFastReconstruction import CONNEXITY8, MFastReconstruction


class MClosingByReconstruction(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input,se,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = MDilation.invoke(self.input, self.se, self.or_)

        self.output = MFastReconstruction.invoke(marker, self.input, CONNEXITY8, True, self.or_)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: AlgebraicOrdering) -> Image | None:
        try:
            return MClosingByReconstruction().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

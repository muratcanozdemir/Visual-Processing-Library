"""Port of ``vpt.algorithms.mm.gray.geo.GClosingByReconstruction``: dilate,
then reconstruct by erosion (``inverse=True``) under the original image."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ..GDilation import GDilation
from .FastGrayReconstruction import CONNEXITY8, FastGrayReconstruction


class GClosingByReconstruction(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.output: Image | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = GDilation.invoke(self.input, self.se)

        self.output = FastGrayReconstruction.invoke(marker, self.input, CONNEXITY8, True)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return GClosingByReconstruction().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.gray.geo.GHMinima``: suppress minima shallower
than ``h`` grey levels by reconstructing ``input + h`` by erosion
(``inverse=True``) under ``input``."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....arithmetic.AdditionC import AdditionC
from .FastGrayReconstruction import CONNEXITY8, FastGrayReconstruction

_BYTE_TO_DOUBLE = 0.00392156862745098


class GHMinima(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.h: int | None = None
        self.output: Image | None = None
        self.inputFields = "input, h"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.h <= 0 or self.h >= 255:
            raise GlobalException("What kind of an h value is that?")

        fPlusH = AdditionC.invoke(self.input, self.h * _BYTE_TO_DOUBLE)

        self.output = FastGrayReconstruction.invoke(fPlusH, self.input, CONNEXITY8, True)

    @staticmethod
    def invoke(image: Image, h: int) -> Image | None:
        try:
            return GHMinima().preprocess(image, h)
        except GlobalException:
            traceback.print_exc()
        return None

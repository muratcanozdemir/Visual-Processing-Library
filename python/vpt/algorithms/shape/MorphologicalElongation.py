"""Port of ``vpt.algorithms.shape.MorphologicalElongation``: repeatedly
erode until the shape vanishes, then ``area / (4 * steps^2)`` -- a rough
elongation measure (compact blobs erode away fast; thin/elongated shapes
survive many more erosion steps relative to their area)."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..mm.binary.BErosion import BErosion


class MorphologicalElongation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input, se"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        tmp = self.input.newInstance(True)

        area = Tools.volume(self.input, 0)
        syc = 0
        while not tmp.isEmpty(0):
            syc += 1
            tmp = BErosion.invoke(tmp, self.se)

        self.output = jdiv(area, 4 * syc * syc)

    @staticmethod
    def invoke(img: Image, se: FlatSE) -> float | None:
        try:
            return MorphologicalElongation().preprocess(img, se)
        except GlobalException:
            traceback.print_exc()
        return None

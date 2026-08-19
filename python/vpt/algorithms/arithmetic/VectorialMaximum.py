"""Port of ``vpt.algorithms.arithmetic.VectorialMaximum``.

Per-pixel vectorial (multichannel) maximum under a caller-supplied
``AlgebraicOrdering`` -- there is no canonical total order on colour
vectors, so the ordering strategy is a parameter rather than a fixed
per-channel comparison.

The Java field is named ``or``, a valid Java identifier but a Python
keyword; renamed ``or_`` throughout, including in ``inputFields`` (which
only matters for this class's internal ``setattr``/``getattr`` binding, not
for any external caller -- ``invoke`` is still called positionally).
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.ordering import AlgebraicOrdering


class VectorialMaximum(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input1,input2,or_"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("Arguments must be of same dimensions!")

        self.output = self.input1.newInstance(False)
        for x in range(self.output.getXDim()):
            for y in range(self.output.getYDim()):
                p1 = self.input1.getVXYDouble(x, y)
                p2 = self.input2.getVXYDouble(x, y)
                self.output.setVXYDouble(x, y, self.or_.max(p1, p2))

    @staticmethod
    def invoke(image1: Image, image2: Image, or_: AlgebraicOrdering) -> Image | None:
        try:
            return VectorialMaximum().preprocess(image1, image2, or_)
        except GlobalException:
            traceback.print_exc()
        return None

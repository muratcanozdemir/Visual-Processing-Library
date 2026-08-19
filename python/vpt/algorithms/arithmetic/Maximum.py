"""Port of ``vpt.algorithms.arithmetic.Maximum``: elementwise ``max(a, b)``.

Unlike ``Minimum``, this compares with a plain ``<`` rather than
``Tools.cmpr``'s epsilon -- transcribed as found, not harmonised.

Python two-arg ``max(p1, p2)`` returns ``p2`` only when ``p2 > p1``,
otherwise ``p1`` -- so ``p1`` wins whenever either operand is NaN (a NaN
comparison is always false). ``np.where(p2 > p1, p2, p1)`` reproduces that
exactly; ``np.maximum`` would not (it propagates NaN regardless of operand
order).
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Maximum(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.inputFields = "input1,input2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("Arguments must be of same dimensions!")

        self.output = self.input1.newInstance(False)

        p1 = self.input1.getDoubleArray()
        p2 = self.input2.getDoubleArray()
        self.output.setDoubleArray(np.where(p2 > p1, p2, p1))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return Maximum().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

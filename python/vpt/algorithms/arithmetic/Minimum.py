"""Port of ``vpt.algorithms.arithmetic.Minimum``: elementwise ``min(a, b)``,
picking via ``Tools.cmpr``'s 1e-8 epsilon rather than a plain ``<``.

``cmpr(p1, p2) < 0`` (which selects ``p1``) is true exactly when
``p1 - p2 < -1e-8`` *or* the difference is NaN -- ``cmpr`` falls through
its epsilon-window and ``diff > 0.0`` checks to its ``return -1`` default
whenever ``diff`` is NaN, since every comparison against NaN is false.
Reproduced as ``np.isnan(diff) | (diff < -epsilon)``, else ``p2`` (the
epsilon-equal case also picks ``p2``, matching the scalar ``else`` branch).
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class Minimum(Algorithm):
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
        with np.errstate(invalid="ignore"):
            diff = p1 - p2
        lt0 = np.isnan(diff) | (diff < -Tools.epsilon)
        self.output.setDoubleArray(np.where(lt0, p1, p2))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return Minimum().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

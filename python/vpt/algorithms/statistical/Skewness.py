"""Port of ``vpt.algorithms.statistical.Skewness``: the third standardised
moment, ``mean((p-mean)^3) / mean((p-mean)^2)^1.5``.

The scalar loop keeps two independent running sums (``s1``, ``s2``) off
the same ``p`` in one pass. Splitting them into two separate
elementwise-computed arrays and two separate ``np.cumsum(...)[-1]`` calls
doesn't perturb either sum's own floating-point order -- each is still
accumulated left-to-right exactly as before, just not interleaved with
the other."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv, jpow
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Mean import Mean


class Skewness(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        mean = Mean.invoke(self.input)

        size = self.input.getSize()

        diff = self.input.getDoubleArray() - mean
        sq = diff * diff
        cube = sq * diff
        if size:
            s1 = float(np.cumsum(cube)[-1])
            s2 = float(np.cumsum(sq)[-1])
        else:
            s1 = 0.0
            s2 = 0.0

        s1 /= size
        s2 /= size

        self.output = jdiv(s1, jpow(s2, 1.5))

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Skewness().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

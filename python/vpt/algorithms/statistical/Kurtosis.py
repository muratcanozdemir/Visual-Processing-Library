"""Port of ``vpt.algorithms.statistical.Kurtosis``: the (non-excess) fourth
standardised moment, ``mean((p-mean)^4) / mean((p-mean)^2)^2``.

Same two-independent-running-sums shape as ``Skewness`` -- see that file's
docstring for why splitting the interleaved scalar sums into two separate
``np.cumsum(...)[-1]`` reductions doesn't perturb either one's summation
order."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Mean import Mean


class Kurtosis(Algorithm):
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
        quart = cube * diff
        if size:
            s1 = float(np.cumsum(quart)[-1])
            s2 = float(np.cumsum(sq)[-1])
        else:
            s1 = 0.0
            s2 = 0.0

        s1 /= size
        s2 /= size

        self.output = jdiv(s1, s2**2.0)

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Kurtosis().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

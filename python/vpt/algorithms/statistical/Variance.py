"""Port of ``vpt.algorithms.statistical.Variance``: population variance of
a mono-channel image (divides by ``size``, not ``size - 1``) -- the same
accumulation as ``SDev``, minus the final ``sqrt``.

Vectorized the same way as ``Mean``: ``(p - mean) ** 2`` computed
elementwise over the whole flat array (elementwise-identical to the scalar
loop's ``p = getDouble(i)``), then reduced with the sequential
``np.cumsum(...)[-1]`` trick to preserve the exact left-to-right summation
order."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Mean import Mean


class Variance(Algorithm):
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
        sqdiff = diff * diff
        sdev = float(np.cumsum(sqdiff)[-1]) if size else 0.0
        self.output = sdev / size

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Variance().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

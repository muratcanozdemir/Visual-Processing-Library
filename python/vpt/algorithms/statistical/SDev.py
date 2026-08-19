"""Port of ``vpt.algorithms.statistical.SDev``: population standard
deviation of a mono-channel image (divides by ``size``, not ``size - 1``).

Vectorized the same way as ``Variance``: ``(p - mean) ** 2`` computed
elementwise, then reduced with ``np.cumsum(...)[-1]`` to preserve the
exact left-to-right summation order before the final ``sqrt``."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Mean import Mean


class SDev(Algorithm):
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
        self.output = math.sqrt(sdev / size)

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return SDev().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.statistical.Mean``: the mean pixel value of a
mono-channel image, computed via a plain running sum (not ``numpy.mean``),
to match Java's exact left-to-right summation order and rounding.

Vectorized via ``np.cumsum(...)[-1]`` -- genuinely sequential, unlike
``np.sum``'s pairwise reduction -- over ``getDoubleArray()``, which is
elementwise equal to ``getDouble(i)`` for every ``i`` (see ``Image.py``),
so the flat array is already in the same left-to-right order the scalar
loop sums in. Same trick as ``Equalization.py``/``SaliencyMap.py``/
``ColorMoments._moment``."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Mean(Algorithm):
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

        size = self.input.getSize()
        arr = self.input.getDoubleArray()
        total = float(np.cumsum(arr)[-1]) if size else 0.0
        self.output = total / size

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Mean().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

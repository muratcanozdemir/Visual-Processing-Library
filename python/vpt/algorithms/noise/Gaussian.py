"""Port of ``vpt.algorithms.noise.Gaussian``: adds zero-mean Gaussian
noise (scaled by ``deviation``) to the double view of a deep copy of the
input, clamped to ``[0, 1]``. Uses an unseeded RNG (``java.util.Random``
in the oracle, ``random.gauss`` here) -- non-deterministic like
``segmentation.MeanShift``, so this is smoke-tested only, never
golden-verified."""

from __future__ import annotations

import random
import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Gaussian(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.deviation: float | None = None
        self.inputFields = "input, deviation"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        for i in range(self.output.getSize()):
            p = self.output.getDouble(i)
            p += self.deviation * random.gauss(0.0, 1.0)
            p = max(0.0, min(p, 1.0))
            self.output.setDouble(i, p)

    @staticmethod
    def invoke(image: Image, deviation: float) -> Image | None:
        try:
            return Gaussian().preprocess(image, deviation)
        except GlobalException:
            traceback.print_exc()
        return None

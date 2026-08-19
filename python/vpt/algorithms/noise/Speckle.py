"""Port of ``vpt.algorithms.noise.Speckle``: classic salt-and-pepper noise
-- each pixel independently has probability ``propability`` (misspelled in
the Java, kept as-is for parity) of being forced to ``0.0`` or ``1.0``
(coin flip between the two). Unseeded RNG, non-deterministic like
``noise.Gaussian``/``segmentation.MeanShift`` -- smoke-tested only."""

from __future__ import annotations

import random
import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Speckle(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.propability: float | None = None
        self.inputFields = "input, propability"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        for i in range(self.output.getSize()):
            if random.random() < self.propability:
                if random.random() < 0.5:
                    self.output.setDouble(i, 1.0)
                else:
                    self.output.setDouble(i, 0.0)

    @staticmethod
    def invoke(image: Image, propability: float) -> Image | None:
        try:
            return Speckle().preprocess(image, propability)
        except GlobalException:
            traceback.print_exc()
        return None

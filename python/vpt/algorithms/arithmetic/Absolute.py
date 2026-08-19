"""Port of ``vpt.algorithms.arithmetic.Absolute``: ``|pixel|`` through the
double view.

Unlike every other class in this package, the Java ``invoke`` declares
``throws GlobalException`` and does not catch it -- no try/except wrapper,
so a ``GlobalException`` propagates to the caller instead of being printed
and swallowed into a ``None`` return. Preserved: this is not a decompiler
artefact, just how the source is written.
"""

from __future__ import annotations

import numpy as np

from ...Algorithm import Algorithm
from ...Image import Image


class Absolute(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage.newInstance(False)

        self.output.setDoubleArray(np.abs(self.inputImage.getDoubleArray()))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        return Absolute().preprocess(image)

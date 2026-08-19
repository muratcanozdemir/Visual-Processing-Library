"""Port of ``vpt.algorithms.conversion.Hue2Distance``: ``2 * hueDistance(hue,
refHue)`` for every pixel of a mono-channel hue image."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class Hue2Distance(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.refHue: float | None = None
        self.output: Image | None = None
        self.inputFields = "input, refHue"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 1)

        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("The input must be a greyscale hue image")

        # Tools.hueDistance(hue, ref) = abs(ref-hue) if <= 0.5 else 1-that --
        # a pure elementwise formula, vectorizes losslessly.
        hue = self.input.getDoubleArray()
        a = np.abs(self.refHue - hue)
        self.output.setDoubleArray(2.0 * np.where(a <= 0.5, a, 1.0 - a))

    @staticmethod
    def invoke(image: Image, refHue: float) -> Image | None:
        try:
            return Hue2Distance().preprocess(image, refHue)
        except GlobalException:
            traceback.print_exc()
        return None

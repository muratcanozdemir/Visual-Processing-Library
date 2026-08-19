"""Port of ``vpt.algorithms.conversion.RGB2cRGB``: chromaticity RGB,
``channel / (r + g + b)``. Uses ``jdiv`` since a black pixel (``sum == 0``)
divides by zero -- Java's double division yields ``NaN``, not an
exception."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2cRGB(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input, False)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus RGB image")

        # jdiv(channel, s) with s == r+g+b: since r/g/b are each non-negative
        # bytes ([0, 255]), s == 0 implies r == g == b == 0, so the divisor
        # being zero always means the numerator is zero too -- exactly the
        # jdiv(0, 0) -> NaN case, never the Infinity one. That collapses to
        # a plain np.where without needing a general jdiv_arr.
        rgb = self.input.getByteArray().reshape(ydim, xdim, cdim).astype(np.float64)
        s = rgb[:, :, 0] + rgb[:, :, 1] + rgb[:, :, 2]
        with np.errstate(invalid="ignore", divide="ignore"):
            crgb = rgb / s[:, :, None]
        crgb = np.where(s[:, :, None] == 0.0, np.nan, crgb)

        self.output.setDoubleArray(crgb.reshape(-1))

    @staticmethod
    def convert(r: int, g: int, b: int) -> list[float]:
        s = float(r + g + b)
        return [jdiv(r, s), jdiv(g, s), jdiv(b, s)]

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2cRGB().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

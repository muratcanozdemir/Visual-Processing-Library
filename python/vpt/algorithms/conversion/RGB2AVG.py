"""Port of ``vpt.algorithms.conversion.RGB2AVG``: the plain per-pixel
average of the three RGB channels, ``(r + g + b) / 3``."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2AVG(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 1)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus RGB image")

        # Elementwise average -- vectorizes losslessly.
        rgb = self.input.getByteArray().reshape(ydim, xdim, cdim).astype(np.float64)
        avg = (rgb[:, :, 0] + rgb[:, :, 1] + rgb[:, :, 2]) / 3.0
        self.output.setDoubleArray(avg.reshape(-1))

    @staticmethod
    def convert(r: int, g: int, b: int) -> float:
        return (r + g + b) / 3.0

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2AVG().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

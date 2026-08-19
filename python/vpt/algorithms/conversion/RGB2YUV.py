"""Port of ``vpt.algorithms.conversion.RGB2YUV``: sRGB (normalised to
[0, 1]) to YUV via the standard linear transform matrix."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2YUV(Algorithm):
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

        # Elementwise linear combination -- vectorizes losslessly.
        rgb = self.input.getByteArray().reshape(ydim, xdim, cdim).astype(np.float64)
        N = rgb * 0.003921
        rN, gN, bN = N[:, :, 0], N[:, :, 1], N[:, :, 2]

        yuv = np.empty((ydim, xdim, cdim), dtype=np.float64)
        yuv[:, :, 0] = 0.299 * rN + 0.587 * gN + 0.114 * bN
        yuv[:, :, 1] = -0.147 * rN - 0.289 * gN + 0.436 * bN
        yuv[:, :, 2] = 0.615 * rN - 0.515 * gN - 0.1 * bN

        self.output.setDoubleArray(yuv.reshape(-1))

    @staticmethod
    def convert(r: int, g: int, b: int) -> list[float]:
        rN = r * 0.003921
        gN = g * 0.003921
        bN = b * 0.003921

        return [
            0.299 * rN + 0.587 * gN + 0.114 * bN,
            -0.147 * rN - 0.289 * gN + 0.436 * bN,
            0.615 * rN - 0.515 * gN - 0.1 * bN,
        ]

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2YUV().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

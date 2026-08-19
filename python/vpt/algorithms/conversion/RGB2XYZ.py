"""Port of ``vpt.algorithms.conversion.RGB2XYZ``: sRGB (normalised to
[0, 1]) to CIE XYZ via the standard linear transform matrix."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2XYZ(Algorithm):
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
        N = 0.00392156862745098 * rgb
        rN, gN, bN = N[:, :, 0], N[:, :, 1], N[:, :, 2]

        xyz = np.empty((ydim, xdim, cdim), dtype=np.float64)
        xyz[:, :, 0] = 0.4124564 * rN + 0.3575761 * gN + 0.1804375 * bN
        xyz[:, :, 1] = 0.2126729 * rN + 0.7151522 * gN + 0.072175 * bN
        xyz[:, :, 2] = 0.0193339 * rN + 0.119192 * gN + 0.9503041 * bN

        self.output.setDoubleArray(xyz.reshape(-1))

    @staticmethod
    def convert(r: int, g: int, b: int) -> list[float]:
        rN = 0.00392156862745098 * r
        gN = 0.00392156862745098 * g
        bN = 0.00392156862745098 * b

        return [
            0.4124564 * rN + 0.3575761 * gN + 0.1804375 * bN,
            0.2126729 * rN + 0.7151522 * gN + 0.072175 * bN,
            0.0193339 * rN + 0.119192 * gN + 0.9503041 * bN,
        ]

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2XYZ().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

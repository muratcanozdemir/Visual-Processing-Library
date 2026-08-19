"""Port of ``vpt.algorithms.conversion.RGB2Gray``.

ITU-R BT.601 luma weighting of a 3-channel RGB image into a single-channel
``DoubleImage``.
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class RGB2Gray(Algorithm):
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

        # Elementwise linear combination -- vectorizes losslessly.
        rgb = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        gray = 0.299 * rgb[:, :, 0] + 0.587 * rgb[:, :, 1] + 0.114 * rgb[:, :, 2]
        self.output.setDoubleArray(gray.reshape(-1))

    @staticmethod
    def convert(r: float, g: float, b: float) -> float:
        return 0.299 * r + 0.587 * g + 0.114 * b

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2Gray().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

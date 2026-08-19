"""Port of ``vpt.algorithms.histogram.RGB27Histogram``: a 3x3x3 joint colour
histogram, each channel quantised to 3 levels (``value // 86``)."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class RGB27Histogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.inputImage: Image | None = None
        self.normalized: bool | None = None
        self.inputFields = "inputImage,normalized"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.inputImage.getXDim()
        ydim = self.inputImage.getYDim()
        cdim = self.inputImage.getCDim()
        if cdim != 3:
            raise GlobalException("Only color images please !")

        # Pure count-only scatter (every increment is exactly 1.0), so
        # np.bincount over the vectorized combined bin index is bit-exact to
        # the scalar loop -- same pattern as the already-vectorized
        # Histogram.py.
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        r = pixels[:, :, 0] // 86
        g = pixels[:, :, 1] // 86
        b = pixels[:, :, 2] // 86
        bins = (r * 9 + g * 3 + b).ravel()

        counts = np.bincount(bins, minlength=27).astype(np.float64)

        if self.normalized:
            counts /= xdim * ydim

        self.output = counts.tolist()

    @staticmethod
    def invoke(image: Image, normalized: bool) -> list[float] | None:
        try:
            return RGB27Histogram().preprocess(image, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

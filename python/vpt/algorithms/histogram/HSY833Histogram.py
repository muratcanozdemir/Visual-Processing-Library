"""Port of ``vpt.algorithms.histogram.HSY833Histogram``: an 8x3x3 joint
histogram over an already-bucketed HSY image (the "833" scheme, see
``conversion.NonUniformHSY833``)."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class HSY833Histogram(Algorithm):
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
            raise GlobalException("Where is my color input image ?")

        # Pure count-only scatter (every increment is exactly 1.0), so
        # np.bincount over the vectorized combined bin index is bit-exact to
        # the scalar loop -- same pattern as the already-vectorized
        # Histogram.py. (Formula is hue*9+sat*3+lum verbatim from the Java
        # source despite the "833" scheme name.)
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        hue = pixels[:, :, 0]
        sat = pixels[:, :, 1]
        lum = pixels[:, :, 2]
        bins = (hue * 9 + sat * 3 + lum).ravel()

        counts = np.bincount(bins, minlength=72).astype(np.float64)

        if self.normalized:
            counts /= xdim * ydim

        self.output = counts.tolist()

    @staticmethod
    def invoke(image: Image, normalized: bool) -> list[float] | None:
        try:
            return HSY833Histogram().preprocess(image, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.histogram.HSY744Histogram``: a 7x4x4 joint
histogram over an already-bucketed HSY image (the "744" scheme, see
``conversion.NonUniformHSY744``)."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class HSY744Histogram(Algorithm):
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
        # Histogram.py.
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        hue = pixels[:, :, 0]
        sat = pixels[:, :, 1]
        lum = pixels[:, :, 2]
        bins = (hue * 16 + sat * 4 + lum).ravel()

        counts = np.bincount(bins, minlength=112).astype(np.float64)

        if self.normalized:
            counts /= xdim * ydim

        self.output = counts.tolist()

    @staticmethod
    def invoke(image: Image, normalized: bool) -> list[float] | None:
        try:
            return HSY744Histogram().preprocess(image, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

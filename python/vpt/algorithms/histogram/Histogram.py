"""Port of ``vpt.algorithms.histogram.Histogram``.

Per-channel 256-bin byte histogram, laid out as one flat ``cdim * 256`` list
(channel-major: bins ``[c*256, c*256+256)``).
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Histogram(Algorithm):
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

        # Per-pixel counting is order-independent (every increment is exactly
        # 1.0), so np.bincount per channel is bit-exact to the scalar loop.
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        counts = np.empty((cdim, 256), dtype=np.float64)
        for c in range(cdim):
            counts[c] = np.bincount(pixels[:, :, c].ravel(), minlength=256)

        if self.normalized:
            counts /= xdim * ydim

        self.output = counts.reshape(-1).tolist()

    @staticmethod
    def invoke(image: Image, normalized: bool) -> list[float] | None:
        try:
            return Histogram().preprocess(image, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

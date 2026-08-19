"""Port of ``vpt.algorithms.histogram.Equalization``: classic histogram
equalisation via a per-channel cumulative (normalised) histogram used as a
pixel-value remap table."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Histogram import Histogram


class Equalization(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage.newInstance(True)

        xdim = self.inputImage.getXDim()
        ydim = self.inputImage.getYDim()
        cdim = self.output.getCDim()

        histo = Histogram.invoke(self.inputImage, True)
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        out = np.empty((ydim, xdim, cdim), dtype=np.float64)
        for c in range(cdim):
            # np.cumsum is the same left-to-right running sum as the scalar
            # loop, so this stays bit-exact; the lookup is then a pure gather
            # (each output pixel depends only on its own byte value).
            table = np.cumsum(np.asarray(histo[c * 256 : c * 256 + 256], dtype=np.float64))
            out[:, :, c] = table[pixels[:, :, c]]

        self.output.setDoubleArray(out.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return Equalization().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

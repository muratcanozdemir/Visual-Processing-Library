"""Port of ``vpt.algorithms.mm.multi.MEuclideanGradient``: the Euclidean
norm of ``GInternGradient``'s per-channel result (run channel-wise on the
multichannel image), normalised by ``sqrt(cdim)`` to keep the range
comparable across images with different channel counts."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....DoubleImage import DoubleImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ..gray.GInternGradient import GInternGradient


class MEuclideanGradient(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        gradient = GInternGradient.invoke(self.input, self.se)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()

        self.output = DoubleImage(xdim, ydim, 1)

        normalizer = math.sqrt(cdim)
        # Vectorized equivalent of the per-pixel ``Tools.EuclideanNorm`` scan
        # below: ``np.cumsum`` reproduces that method's left-to-right
        # running sum of squares bit-exactly (unlike ``np.sum``'s pairwise
        # summation algorithm), so taking its last slice along the channel
        # axis before the final ``sqrt`` matches the scalar accumulation
        # order exactly.
        gradientArr = gradient.getDoubleArray().reshape(ydim, xdim, cdim)
        sumSquares = np.cumsum(gradientArr * gradientArr, axis=-1)[..., -1]
        norm = np.sqrt(sumSquares)
        self.output.setDoubleArray((norm / normalizer).reshape(-1))

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return MEuclideanGradient().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

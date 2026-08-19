"""Port of ``vpt.algorithms.mm.multi.MMaxGradient``: the per-pixel max over
channels of ``GGradient``'s (scalar, per-channel) result."""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ..gray.GGradient import GGradient


class MMaxGradient(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

    def execute(self) -> None:
        gradient = GGradient.invoke(self.input, self.se)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()

        self.output = ByteImage(xdim, ydim, 1)
        # Vectorized equivalent of the per-pixel channel-max scan below:
        # a plain ``np.max`` over the channel axis is safe here specifically
        # because byte channels can never hold NaN (unlike the NaN-hazard
        # float scans elsewhere in this codebase that need the
        # ``np.where(candidate > dst, ...)`` treatment instead of
        # ``np.maximum``/``np.max``), and the scalar loop's ``maximum = 0``
        # start is already a true lower bound for byte data, not a
        # sentinel-fallback case, so the two are equivalent for every input.
        gradientArr = gradient.getByteArray().reshape(ydim, xdim, cdim)
        outArr = np.max(gradientArr, axis=-1)
        self.output.setByteArray(outArr.reshape(-1))

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return MMaxGradient().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

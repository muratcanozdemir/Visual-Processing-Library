"""Port of ``vpt.algorithms.texture.MultiResolutionHistogram``: quantizes
a mono-channel image to 32 levels (``byte // 8``), then at 8 growing
circular-opening/closing scales (radius ``2i-1`` for ``i`` in ``1..8``)
records a 32-bin normalised histogram of the result.

``output`` layout: ``[opening histograms (8*32) ++ closing histograms
(8*32)]``, each scale's 32-bin histogram contiguous."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ..histogram.CustomHistogram import CustomHistogram
from ..mm.gray.GClosing import GClosing
from ..mm.gray.GOpening import GOpening

BINS = 32
SCALES = 8


class MultiResolutionHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Only greyscale images for now!")

        self.output = [0.0] * (2 * SCALES * BINS)
        tmp = self.input.newInstance(True)
        for i in range(tmp.getSize()):
            tmp.setByte(i, tmp.getByte(i) // 8)

        for i in range(1, SCALES + 1):
            se = FlatSE.circle((i - 1) * 2 + 1)

            tmp2 = GOpening.invoke(tmp, se)

            histo = CustomHistogram.invoke(tmp2, True, BINS)
            for j in range(len(histo)):
                self.output[(i - 1) * BINS + j] = histo[j]

            tmp2 = GClosing.invoke(tmp, se)

            histo = CustomHistogram.invoke(tmp2, True, BINS)
            for j in range(len(histo)):
                self.output[SCALES * BINS + (i - 1) * BINS + j] = histo[j]

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return MultiResolutionHistogram().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

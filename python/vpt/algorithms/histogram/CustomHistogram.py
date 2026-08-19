"""Port of ``vpt.algorithms.histogram.CustomHistogram``: like ``Histogram``,
but with a caller-chosen bin count instead of the fixed 256, and raising if
any pixel value is out of that range."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class CustomHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.inputImage: Image | None = None
        self.normalized: bool | None = None
        self.bins: int | None = None
        self.inputFields = "inputImage, normalized, bins"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.inputImage.getXDim()
        ydim = self.inputImage.getYDim()
        cdim = self.inputImage.getCDim()
        bins = self.bins

        # Pure count-only scatter (every increment is exactly 1.0), so
        # np.bincount per channel is bit-exact to the scalar loop -- same
        # pattern as the already-vectorized Histogram.py. The out-of-range
        # check is preserved: the scalar loop scans channel-major, then
        # x-major, then y-minor (`for c: for x: for y:`) and raises on the
        # first pixel value >= bins it meets in that order, so each
        # channel's byte plane is scanned in matching (x-major, y-minor)
        # order here before counting.
        pixels = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        counts = np.empty((cdim, bins), dtype=np.float64)
        for c in range(cdim):
            chan = pixels[:, :, c].T.ravel()  # x-major, y-minor scan order
            bad = chan >= bins
            if bad.any():
                bad_value = int(chan[bad][0])
                raise GlobalException(
                    f"A pixel value is beyond the allowed bin size! {bad_value}"
                )
            counts[c] = np.bincount(chan, minlength=bins)

        if self.normalized:
            counts /= xdim * ydim

        self.output = counts.reshape(-1).tolist()

    @staticmethod
    def invoke(image: Image, normalized: bool, bins: int) -> list[float] | None:
        try:
            return CustomHistogram().preprocess(image, normalized, bins)
        except GlobalException:
            traceback.print_exc()
        return None

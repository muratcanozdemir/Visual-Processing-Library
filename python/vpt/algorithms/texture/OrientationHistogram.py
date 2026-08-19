"""Port of ``vpt.algorithms.texture.OrientationHistogram``: bins
``OrientationMap``'s ``[0, 1)``-normalised dominant-orientation map into
``bins`` equal-width buckets and normalises by pixel count. The one
orientation of exactly ``1.0`` (the map's ``getDominantOrientation`` folds
its result into ``[0, 1)`` via ``atan2``, so this is a boundary edge case,
not the common path) is nudged down by ``1e-8`` first so
``int(orientation * bins)`` doesn't index one past the last bin.

Vectorized: the per-pixel scatter-add is a plain unweighted count, so
``np.bincount`` on the whole ``bins``-scaled orientation array reproduces
the scalar loop's totals regardless of scatter order (order only matters
for a floating-point *sum*, not an integer count). ``np.floor`` stands in
for Python's ``int()`` truncation -- equivalent here since every
orientation is non-negative (``OrientationMap`` folds into ``[0, 1)``,
and the ``==1.0`` nudge keeps it there). The scalar loop's Python
``IndexError`` if a value ever rounded up to bin ``bins`` (one past the
last slot) is reproduced explicitly, since ``np.bincount`` would
otherwise silently grow the array instead of raising."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .OrientationMap import OrientationMap


class OrientationHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.bins: int | None = None
        self.maskSize: int | None = None
        self._xdim: int = 0
        self._ydim: int = 0
        self.inputFields = "input, bins, maskSize"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("The input image must be grayscale")
        if self.bins < 1:
            raise GlobalException("Invalid bin value")

        self._xdim = self.input.getXDim()
        self._ydim = self.input.getYDim()

        size = self._xdim * self._ydim

        self.output = [0.0] * self.bins

        self.input = OrientationMap.invoke(self.input, self.maskSize)
        orientation = self.input.getDoubleArray()
        orientation = np.where(orientation == 1.0, orientation - 1.0e-8, orientation)

        idx = np.floor(orientation * self.bins).astype(np.int64)
        if idx.size and int(idx.max()) >= self.bins:
            raise IndexError("list index out of range")
        counts = np.bincount(idx, minlength=self.bins)
        for i in range(self.bins):
            self.output[i] = float(counts[i])

        for i in range(len(self.output)):
            self.output[i] /= size

    @staticmethod
    def invoke(image: Image, bins: int, maskSize: int) -> list[float] | None:
        try:
            return OrientationHistogram().preprocess(image, bins, maskSize)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.histogram.SaturationWeightedHueHistogram``:
a 12-bin hue histogram over an HSY image, each pixel's contribution weighted
by a sigmoid of its saturation and pixels below a low saturation/luma floor
excluded entirely.

Two hazards: ``output[jint(jround(h))]`` can index out of range (``h = hue *
11.0``, rounded) if ``hue`` is close enough to ``1.0`` to round up to ``12``
on a 12-element array -- an ``IndexOutOfBoundsException`` in Java too (not
caught anywhere), so left to raise here rather than defensively clamped.
``normalization`` can stay ``0.0`` if no pixel clears the saturation/luma
floor, so the final normalising division uses ``jdiv`` (Java's ``0.0/0.0``
is ``NaN``, not an exception)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv, jexp_arr, jint_arr, jround_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image

_FLOOR = 0.04705882352941176
_NBINS = 12


class SaturationWeightedHueHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.hsy: Image | None = None
        self.inputFields = "hsy"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.hsy.getXDim()
        ydim = self.hsy.getYDim()
        cdim = self.hsy.getCDim()

        if cdim != 3:
            raise GlobalException("cdim error")

        # Weighted scatter-add (coeff, not +1): per-bin sums are
        # FP-order-sensitive, so np.add.at is used to preserve the scalar
        # loop's index-order accumulation (unlike a plain fancy-index +=,
        # which silently drops duplicate-index writes). The loop scans
        # y-major, x-minor (`for y: for x:`); pixels.reshape(ydim, xdim,
        # cdim).ravel() visits elements in that same order.
        pixels = self.hsy.getDoubleArray().reshape(ydim, xdim, cdim)
        hue = (pixels[:, :, 0] * 11.0).ravel()
        sat = pixels[:, :, 1].ravel()
        lum = pixels[:, :, 2].ravel()

        mask = (sat >= _FLOOR) & (lum >= _FLOOR)
        sel_hue = hue[mask]
        sel_sat = sat[mask]

        idx = jint_arr(jround_arr(sel_hue))

        # The scalar loop relies on an uncaught out-of-range-index raise
        # (h rounding to 12, e.g.) rather than a defensive clamp -- a Java
        # ArrayIndexOutOfBoundsException there, an uncaught IndexError here
        # (invoke() only catches GlobalException, so it still propagates).
        # Python list indexing wraps for in-range negative indices exactly
        # like the original `self.output[idx]`, so only genuinely
        # out-of-range magnitudes (>= 12 or < -12) must raise -- reproduce
        # that by indexing a real 12-element list with the first offending
        # value in scan order, which raises the authentic IndexError.
        if idx.size:
            out_of_range = (idx < -_NBINS) | (idx >= _NBINS)
            if out_of_range.any():
                bad_pos = int(np.nonzero(out_of_range)[0][0])
                bad_idx = int(idx[bad_pos])
                ([0.0] * _NBINS)[bad_idx]  # raises the authentic IndexError

        coeff = self._sigma_arr(sel_sat)

        counts = np.zeros(_NBINS, dtype=np.float64)
        if idx.size:
            np.add.at(counts, idx, coeff)

        # normalization is a single running scalar accumulated in the same
        # left-to-right scan order as the bin sums above; np.add.at into a
        # 1-element accumulator (rather than .sum(), which uses pairwise
        # summation) reproduces that exact sequential FP rounding.
        norm_holder = np.zeros(1, dtype=np.float64)
        if coeff.size:
            np.add.at(norm_holder, np.zeros(coeff.size, dtype=np.int64), coeff)
        normalization = float(norm_holder[0])

        self.output = [jdiv(c, normalization) for c in counts]

    @staticmethod
    def _sigma_arr(z: np.ndarray) -> np.ndarray:
        return 1.0 / (1.0 + jexp_arr(-5.0 * (z - 0.5)))

    @staticmethod
    def invoke(hsy: Image) -> list[float] | None:
        try:
            return SaturationWeightedHueHistogram().preprocess(hsy)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.statistical.ContrastRatio``: ``max - min`` pixel
value over a mono-channel image, optionally restricted to a boolean mask.

The scalar loop seeds ``maximum=0.0``/``minimum=1.0`` and only updates on a
strict ``>``/``<`` comparison -- both IEEE-754 comparisons against NaN are
``False`` in Java *and* in Python, so a NaN pixel is silently skipped by
the running max/min exactly like in section 6.8's NaN-safe max/min
vectorizations. Unlike the ``np.cumsum`` sums elsewhere in this package,
max/min are associative and commutative, so scan order genuinely doesn't
matter here -- the final result is provably ``max(seed, max of the
non-NaN candidate values)`` (and the seed-only ``minimum``/``maximum``
symmetrically), regardless of which order the candidates are visited in.
Two edge cases still need explicit handling rather than a bare
``np.nanmax``/``np.nanmin``: an empty candidate set (mask never True, or
an all-NaN region) must leave the seeds untouched -- the scalar loop
simply never enters its ``if`` branch -- whereas ``np.nanmax`` on an
all-NaN slice warns and returns NaN instead."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class ContrastRatio(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.mask: Image | None = None
        self.inputFields = "input, mask"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only monochannel images for now...")

        arr = self.input.getDoubleArray()
        if self.mask is not None:
            maskArr = self.mask.getBooleanArray()
            candidates = arr[maskArr]
        else:
            candidates = arr

        valid = candidates[~np.isnan(candidates)]

        maximum = float(np.max(valid)) if valid.size else 0.0
        minimum = float(np.min(valid)) if valid.size else 1.0
        maximum = max(maximum, 0.0)
        minimum = min(minimum, 1.0)

        self.output = maximum - minimum

    @staticmethod
    def invoke(img: Image, mask: Image | None = None) -> float | None:
        try:
            return ContrastRatio().preprocess(img, mask)
        except GlobalException:
            traceback.print_exc()
        return None

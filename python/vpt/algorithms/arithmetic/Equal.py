"""Port of ``vpt.algorithms.arithmetic.Equal``: pixelwise equality (via
``Tools.cmpr``'s epsilon), short-circuiting on the first mismatch.

The mismatch mask is computed for the whole array at once, but the
observable side effect -- the single ``print(..., file=sys.stderr)`` for
the *first* mismatching pixel, then stop -- is preserved bit-for-bit by
only ever printing the lowest-index ``True`` in the mask (found via
``np.argmax``, which returns the first occurrence of the max value; on an
all-``False`` mask it harmlessly returns index 0, guarded by ``.any()``
first)."""

from __future__ import annotations

import sys
import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class Equal(Algorithm):
    def __init__(self):
        super().__init__()
        self.input1: Image | None = None
        self.input2: Image | None = None
        self.output: bool = True
        self.inputFields = "input1,input2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.input1.hasSameDimensionsAs(self.input2):
            raise GlobalException("Arguments must be of same dimensions!")

        p1 = self.input1.getDoubleArray()
        p2 = self.input2.getDoubleArray()
        with np.errstate(invalid="ignore"):
            diff = p1 - p2
        mismatch = ~((diff >= -Tools.epsilon) & (diff <= Tools.epsilon))
        if mismatch.any():
            i = int(mismatch.argmax())
            print(f"{float(p1[i])} {float(p2[i])}", file=sys.stderr)
            self.output = False

    @staticmethod
    def invoke(image1: Image, image2: Image) -> bool | None:
        try:
            return Equal().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

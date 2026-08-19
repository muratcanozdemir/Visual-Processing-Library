"""Port of ``vpt.algorithms.arithmetic.Subtraction``: clamped ``pixel1 - pixel2``
through the double view, to ``[0.0, 1.0]``.

Output type: a ``DoubleImage`` if either input is one, otherwise the same
type as ``inputImage1`` (mirroring the Java's ``instanceof`` check).

``min(1.0, max(x, 0.0))`` does *not* preserve NaN, unlike ``Addition``'s
``max(min(x, 1.0), 0.0)``: the nesting order is flipped. The inner
``max(x, 0.0)`` keeps NaN (``0.0 > nan`` is false, so the variable operand
wins), but the *outer* ``min(1.0, inner)`` has the constant first, so when
``inner`` is NaN, ``inner < 1.0`` is false and the constant ``1.0`` wins --
a NaN input pixel comes out as ``1.0``, not NaN. ``np.clip`` would get this
wrong (it preserves NaN). Reproduced instead as two explicit clamps applied
in the same low-then-high order as the nested calls.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class Subtraction(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage1: Image | None = None
        self.inputImage2: Image | None = None
        self.inputFields = "inputImage1,inputImage2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.inputImage1.hasSameDimensionsAs(self.inputImage2):
            raise GlobalException("Arguments must be of same dimensions!")

        if isinstance(self.inputImage1, DoubleImage) or isinstance(self.inputImage2, DoubleImage):
            self.output = DoubleImage(self.inputImage1, False)
        else:
            self.output = self.inputImage1.newInstance(False)

        with np.errstate(invalid="ignore"):
            x = self.inputImage1.getDoubleArray() - self.inputImage2.getDoubleArray()
        lo = np.where(x < 0.0, 0.0, x)
        self.output.setDoubleArray(np.where(lo < 1.0, lo, 1.0))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        try:
            return Subtraction().preprocess(image1, image2)
        except GlobalException:
            traceback.print_exc()
        return None

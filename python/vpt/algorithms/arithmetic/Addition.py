"""Port of ``vpt.algorithms.arithmetic.Addition``: clamped ``pixel1 + pixel2``
through the double view, to ``[0.0, 1.0]``.

Output type: a ``DoubleImage`` if either input is one, otherwise the same
type as ``inputImage1`` (mirroring the Java's ``instanceof`` check, same as
``Subtraction``).

Like ``Absolute``, ``invoke`` does not catch ``GlobalException`` here --
preserved as found, not harmonised with the rest of the package.

``max(min(x, 1.0), 0.0)`` is verified bit-exact against ``np.clip(x, 0.0,
1.0)`` across the full nan/inf/general-value sweep, including NaN (both
propagate NaN here, since the *inner* ``min`` sees the variable operand
first: ``min(nan, 1.0)`` keeps the NaN because ``1.0 < nan`` is false, and
the outer ``max`` then keeps it the same way) -- see ``Subtraction`` for the
opposite nesting order, which does *not* preserve NaN.
"""

from __future__ import annotations

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class Addition(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage1: Image | None = None
        self.inputImage2: Image | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage1,inputImage2"
        self.outputFields = "output"

    def execute(self) -> None:
        if not self.inputImage1.hasSameDimensionsAs(self.inputImage2):
            raise GlobalException("Arguments must be of same dimensions!")

        if isinstance(self.inputImage1, DoubleImage) or isinstance(self.inputImage2, DoubleImage):
            self.output = DoubleImage(self.inputImage1, False)
        else:
            self.output = self.inputImage1.newInstance(False)

        p1 = self.inputImage1.getDoubleArray()
        p2 = self.inputImage2.getDoubleArray()
        with np.errstate(invalid="ignore"):
            self.output.setDoubleArray(np.clip(p1 + p2, 0.0, 1.0))

    @staticmethod
    def invoke(image1: Image, image2: Image) -> Image | None:
        return Addition().preprocess(image1, image2)

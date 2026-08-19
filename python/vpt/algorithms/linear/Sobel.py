"""Port of ``vpt.algorithms.linear.Sobel``.

Sobel gradient in one of four flavours (selected by ``type``): the raw
horizontal (``GX``) or vertical (``GY``) derivative, the gradient magnitude
(``GRAD``), or the gradient direction (``THETA``). Each is a 3x3 convolution
via ``Convolution``, optionally contrast-stretched to ``[0, 1]``.

**Known divergence** in the un-normalized ``THETA`` branch: golden-verified
against the JDK 21 oracle to disagree by 1 ulp at pixels where the
convolved x-gradient is a tiny nonzero residual rather than exactly 0.0 --
`Math.atan2`'s general-case rounding isn't guaranteed to match
`math.atan2`'s bit-for-bit (nor even Java's own across platforms; see
``_javacompat.jatan2``). Not a porting bug -- documented in
``python/README.md``.
"""

from __future__ import annotations

import math
import traceback

from ..._javacompat import Point, jatan2
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ..histogram.ContrastStretch import ContrastStretch
from .Convolution import Convolution

_GX_KERNEL = [[-1.0, 0.0, 1.0], [-2.0, 0.0, 2.0], [-1.0, 0.0, 1.0]]
_GY_KERNEL = [[-1.0, -2.0, -1.0], [0.0, 0.0, 0.0], [1.0, 2.0, 1.0]]


class Sobel(Algorithm):
    GX = 0
    GY = 1
    GRAD = 2
    THETA = 3

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.type: int | None = None
        self.normalized: bool | None = None
        self.inputFields = "input,type,normalized"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.type == Sobel.GX:
            kernel = NonFlatSE(Point(1, 1), _GX_KERNEL)
            self.output = Convolution.invoke(self.input, kernel)
            if self.normalized:
                self.output = ContrastStretch.invoke(self.output)

        elif self.type == Sobel.GY:
            kernel = NonFlatSE(Point(1, 1), _GY_KERNEL)
            self.output = Convolution.invoke(self.input, kernel)
            if self.normalized:
                self.output = ContrastStretch.invoke(self.output)

        elif self.type == Sobel.GRAD:
            gradx = Convolution.invoke(self.input, NonFlatSE(Point(1, 1), _GX_KERNEL))
            grady = Convolution.invoke(self.input, NonFlatSE(Point(1, 1), _GY_KERNEL))

            self.output = DoubleImage(self.input, False)
            for i in range(gradx.getSize()):
                px = gradx.getDouble(i)
                py = grady.getDouble(i)
                self.output.setDouble(i, math.sqrt(px * px + py * py))
            if self.normalized:
                self.output = ContrastStretch.invoke(self.output)

        elif self.type == Sobel.THETA:
            gradx = Convolution.invoke(self.input, NonFlatSE(Point(1, 1), _GX_KERNEL))
            grady = Convolution.invoke(self.input, NonFlatSE(Point(1, 1), _GY_KERNEL))

            self.output = DoubleImage(self.input, False)
            for i in range(self.output.getSize()):
                px = gradx.getDouble(i)
                py = grady.getDouble(i)
                self.output.setDouble(i, jatan2(py, px))
            if self.normalized:
                self.output = ContrastStretch.invoke(self.output)

        else:
            raise GlobalException("Unsupported operation type")

    @staticmethod
    def invoke(image: Image, type: int, normalized: bool) -> Image | None:
        try:
            return Sobel().preprocess(image, type, normalized)
        except GlobalException:
            traceback.print_exc()
        return None

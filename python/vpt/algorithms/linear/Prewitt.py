"""Port of ``vpt.algorithms.linear.Prewitt``: Prewitt gradient in one of
four flavours (``GX``, ``GY``, ``GRAD`` magnitude, ``THETA`` direction),
each a 3x3 convolution via ``Convolution``, always contrast-stretched
(unlike ``Sobel``, there is no ``normalized`` toggle here).

``THETA`` uses ``Math.atan(py / px)`` (not ``atan2`` like ``Sobel``), so a
zero ``px`` is a real, common div-by-zero case (any vertical edge) -- routed
through ``jdiv``/``jatan`` rather than raw ``/``/``math.atan``."""

from __future__ import annotations

import math
import traceback

from ..._javacompat import Point, jatan, jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ..histogram.ContrastStretch import ContrastStretch
from .Convolution import Convolution

_GX_KERNEL = [[-1.0, 0.0, 1.0], [-1.0, 0.0, 1.0], [-1.0, 0.0, 1.0]]
_GY_KERNEL = [[-1.0, -1.0, -1.0], [0.0, 0.0, 0.0], [1.0, 1.0, 1.0]]


class Prewitt(Algorithm):
    GX = 0
    GY = 1
    GRAD = 2
    THETA = 3

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage: Image | None = None
        self.type: int | None = None
        self.inputFields = "inputImage,type"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.type == Prewitt.GX:
            kernel = NonFlatSE(Point(1, 1), _GX_KERNEL)
            self.output = Convolution.invoke(self.inputImage, kernel)
            self.output = ContrastStretch.invoke(self.output)

        elif self.type == Prewitt.GY:
            kernel = NonFlatSE(Point(1, 1), _GY_KERNEL)
            self.output = Convolution.invoke(self.inputImage, kernel)
            self.output = ContrastStretch.invoke(self.output)

        elif self.type == Prewitt.GRAD:
            gradx = Convolution.invoke(self.inputImage, NonFlatSE(Point(1, 1), _GX_KERNEL))
            grady = Convolution.invoke(self.inputImage, NonFlatSE(Point(1, 1), _GY_KERNEL))

            self.output = DoubleImage(self.inputImage, False)
            for i in range(gradx.getSize()):
                px = gradx.getDouble(i)
                py = grady.getDouble(i)
                self.output.setDouble(i, math.sqrt(px * px + py * py))
            self.output = ContrastStretch.invoke(self.output)

        elif self.type == Prewitt.THETA:
            gradx = Convolution.invoke(self.inputImage, NonFlatSE(Point(1, 1), _GX_KERNEL))
            grady = Convolution.invoke(self.inputImage, NonFlatSE(Point(1, 1), _GY_KERNEL))

            self.output = DoubleImage(self.inputImage, False)
            for i in range(gradx.getSize()):
                px = gradx.getDouble(i)
                py = grady.getDouble(i)
                self.output.setDouble(i, jatan(jdiv(py, px)))
            self.output = ContrastStretch.invoke(self.output)

        else:
            raise GlobalException("Unsupported operation type")

    @staticmethod
    def invoke(image: Image, type: int) -> Image | None:
        try:
            return Prewitt().preprocess(image, type)
        except GlobalException:
            traceback.print_exc()
        return None

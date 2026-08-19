"""Port of ``vpt.algorithms.statistical.Normalize``: rescales a mono-channel
image to a target mean/sdev, ``(pixel - imageMean + targetMean) /
(imageSdev / targetSdev)``."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from .Mean import Mean
from .SDev import SDev


class Normalize(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: DoubleImage | None = None
        self.input: Image | None = None
        self.mean: float | None = None
        self.sdev: float | None = None
        self.inputFields = "input, mean, sdev"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        self.output = DoubleImage(self.input, True)

        imageMean = Mean.invoke(self.input)
        imageSdev = SDev.invoke(self.input)

        size = self.input.getSize()
        for i in range(size):
            self.output.setDouble(
                i,
                jdiv(
                    self.input.getDouble(i) - imageMean + self.mean,
                    jdiv(imageSdev, self.sdev),
                ),
            )

    @staticmethod
    def invoke(img: Image, mean: float, sdev: float) -> DoubleImage | None:
        try:
            return Normalize().preprocess(img, mean, sdev)
        except GlobalException:
            traceback.print_exc()
        return None

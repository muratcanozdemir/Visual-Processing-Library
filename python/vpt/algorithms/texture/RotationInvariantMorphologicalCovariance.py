"""Port of ``vpt.algorithms.texture.RotationInvariantMorphologicalCovariance``:
the same 10-way valuation switch as ``HorizontalMorphologicalCovariance``
(including the same ``LOCATIONS`` if/else, re-verified via `javap` for
*this* class specifically -- same bytecode shape, real `default` case, no
fallthrough), but opening/closing with a growing circular SE
(``FlatSE.circle((i-1)*step + 1)``) instead of a two-point horizontal SE."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ...util.Tools import Tools
from ..frequential.FFT import FFT
from ..mm.gray.GClosing import GClosing
from ..mm.gray.GOpening import GOpening
from ..statistical.InvariantMoment import InvariantMoment
from ..statistical.Kurtosis import Kurtosis
from ..statistical.Skewness import Skewness
from ..statistical.Variance import Variance

VOLUME = 0
FOURIER_VOLUME = 1
MOMENT1 = 2
FOURIER_MOMENT1 = 3
LOCATIONS = 4
VARIANCE = 5
SKEWNESS = 6
KURTOSIS = 7
FOURIER_VARIANCE = 8
FOURIER_KURTOSIS = 9


class RotationInvariantMorphologicalCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.step: int | None = None
        self.valuation: int | None = None
        self.inputFields = "input,len,step,valuation"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * 2
        self.output = [0.0] * size
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        originalValue = self._valuation(self.input)
        for i in range(1, self.len + 1):
            se = FlatSE.circle((i - 1) * self.step + 1)

            tmp = GOpening.invoke(self.input, se)

            self.output[i - 1] = jdiv(self._valuation(tmp), originalValue)

            tmp = GClosing.invoke(self.input, se)

            self.output[self.len + i - 1] = jdiv(self._valuation(tmp), originalValue)

    def _valuation(self, img: Image | None) -> float:
        if self.valuation == VOLUME:
            return Tools.volume(img, 0)
        if self.valuation == VARIANCE:
            return Variance.invoke(img)
        if self.valuation == SKEWNESS:
            return Skewness.invoke(img)
        if self.valuation == KURTOSIS:
            return Kurtosis.invoke(img)
        if self.valuation == FOURIER_VARIANCE:
            fft: list[DoubleImage] = FFT.invoke(img.getChannel(0), False, 2)
            return Variance.invoke(fft[0])
        if self.valuation == FOURIER_KURTOSIS:
            fft = FFT.invoke(img.getChannel(0), False, 2)
            return Kurtosis.invoke(fft[0])
        if self.valuation == MOMENT1:
            return InvariantMoment.invoke(img, 1)[0]
        if self.valuation == FOURIER_VOLUME:
            fft = FFT.invoke(img, True, 2)
            return Tools.volume(fft[0], 0)
        if self.valuation == FOURIER_MOMENT1:
            fft = FFT.invoke(img, True, 2)
            return InvariantMoment.invoke(fft[0], 1)[0]
        if self.valuation == LOCATIONS:
            if img is None or img is self.input:
                return float(self.input.getXDim() * self.input.getYDim())
            counter = Tools.numberOfDifferentPixels(self.input, img)
            return float(counter)
        raise GlobalException("Invalid valuation type")

    @staticmethod
    def invoke(image: Image, length: int, step: int, valuation: int) -> list[float] | None:
        try:
            return RotationInvariantMorphologicalCovariance().preprocess(
                image, length, step, valuation
            )
        except GlobalException:
            traceback.print_exc()
        return None

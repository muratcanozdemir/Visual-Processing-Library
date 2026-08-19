"""Port of ``vpt.algorithms.texture.CircularGranulometry``: the same
granulometry-curve valuation scheme as ``AreaGranulometry`` (identical
switch of 10 valuations, identical output layout), but using a growing
circular structuring element (``FlatSE.disk((i-1)*step + 1)``) with plain
``GOpening``/``GClosing`` rather than an area-based opening/closing."""

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
FOURIER_VARIANCE = 2
MOMENT1 = 3
FOURIER_MOMENT1 = 4
VARIANCE = 5
SKEWNESS = 6
KURTOSIS = 7
FOURIER_KURTOSIS = 8
MOMENT2 = 9


class CircularGranulometry(Algorithm):
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
        size = self.len * cdim * 2
        self.output = [0.0] * size

        originalValues = [0.0] * cdim
        for c in range(cdim):
            originalValues[c] = self._valuation(self.input, c)

        for i in range(1, self.len + 1):
            se = FlatSE.disk((i - 1) * self.step + 1)

            tmp = GOpening.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * self.len * 2 + i - 1] = jdiv(
                    self._valuation(tmp, c), originalValues[c]
                )

            tmp = GClosing.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * self.len * 2 + self.len + i - 1] = jdiv(
                    self._valuation(tmp, c), originalValues[c]
                )

    def _valuation(self, img: Image, channel: int) -> float:
        if self.valuation == VOLUME:
            return Tools.volume(img, channel)
        if self.valuation == VARIANCE:
            return Variance.invoke(img)
        if self.valuation == SKEWNESS:
            return Skewness.invoke(img)
        if self.valuation == KURTOSIS:
            return Kurtosis.invoke(img)
        if self.valuation == FOURIER_VARIANCE:
            fft: list[DoubleImage] = FFT.invoke(img.getChannel(channel), False, 2)
            return Variance.invoke(fft[0])
        if self.valuation == FOURIER_KURTOSIS:
            fft = FFT.invoke(img.getChannel(channel), False, 2)
            return Kurtosis.invoke(fft[0])
        if self.valuation == FOURIER_VOLUME:
            fft = FFT.invoke(img.getChannel(channel), False, 2)
            return Tools.volume(fft[0], 0)
        if self.valuation == MOMENT1:
            return InvariantMoment.invoke(img, 1)[0]
        if self.valuation == MOMENT2:
            return InvariantMoment.invoke(img, 2)[0]
        if self.valuation == FOURIER_MOMENT1:
            fft = FFT.invoke(img, True, 2)
            return InvariantMoment.invoke(fft[0], 1)[0]
        raise GlobalException("Invalid valuation type")

    @staticmethod
    def invoke(image: Image, length: int, step: int, valuation: int) -> list[float] | None:
        try:
            return CircularGranulometry().preprocess(image, length, step, valuation)
        except GlobalException:
            traceback.print_exc()
        return None

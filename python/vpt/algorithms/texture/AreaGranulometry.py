"""Port of ``vpt.algorithms.texture.AreaGranulometry``: an area-opening/
closing granulometry curve. For ``len`` increasing area thresholds
(``i * step`` for ``i`` in ``1..len``), applies ``GAreaOpening``/
``GAreaClosing`` and records a per-channel valuation of the result,
normalised by the valuation of the unfiltered input -- 10 possible
valuations (raw volume, several statistical moments, and FFT-domain
variants of each, all sharing the same switch with ``CircularGranulometry``,
which differs only in *which* opening/closing it granulometrizes with).

``output`` is laid out as ``[channel][opening curve (len) ++ closing curve
(len)]``, flattened -- index ``c * len * 2 + i`` for openings, ``c * len *
2 + len + i`` for closings."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..frequential.FFT import FFT
from ..mm.gray.connected.GAreaClosing import GAreaClosing
from ..mm.gray.connected.GAreaOpening import GAreaOpening
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


class AreaGranulometry(Algorithm):
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
            tmp = GAreaOpening.invoke(self.input, i * self.step)
            for c in range(cdim):
                self.output[c * self.len * 2 + i - 1] = jdiv(
                    self._valuation(tmp, c), originalValues[c]
                )

            tmp = GAreaClosing.invoke(self.input, i * self.step)
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
            return AreaGranulometry().preprocess(image, length, step, valuation)
        except GlobalException:
            traceback.print_exc()
        return None

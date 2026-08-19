"""Port of ``vpt.algorithms.texture.LineGranulometry``: opens/closes with a
growing horizontal line SE (``FlatSE.hLine(2i+1)`` for ``i`` in ``1..len``)
and records a per-channel valuation of the result, normalised by the
valuation of the unfiltered input -- a restricted 4-way subset (``VOLUME``,
``FOURIER_VOLUME``, ``MOMENT1``, ``FOURIER_MOMENT1``) of the 10-way switch
used by ``AreaGranulometry``/``CircularGranulometry``."""

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

VOLUME = 0
FOURIER_VOLUME = 1
MOMENT1 = 2
FOURIER_MOMENT1 = 3


class LineGranulometry(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.valuation: int | None = None
        self.inputFields = "input,len,valuation"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * cdim * 2
        self.output = [0.0] * size

        originalValues = [0.0] * cdim
        for c in range(cdim):
            originalValues[c] = self._valuation(self.input, c)

        for i in range(1, self.len + 1):
            se = FlatSE.hLine(i * 2 + 1)

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
        if self.valuation == FOURIER_VOLUME:
            fft: list[DoubleImage] = FFT.invoke(img.getChannel(channel), False, 2)
            return Tools.volume(fft[0], 0)
        if self.valuation == MOMENT1:
            return InvariantMoment.invoke(img, 1)[0]
        if self.valuation == FOURIER_MOMENT1:
            fft = FFT.invoke(img, True, 2)
            return InvariantMoment.invoke(fft[0], 1)[0]
        raise GlobalException("Invalid valuation type")

    @staticmethod
    def invoke(image: Image, length: int, valuation: int) -> list[float] | None:
        try:
            return LineGranulometry().preprocess(image, length, valuation)
        except GlobalException:
            traceback.print_exc()
        return None

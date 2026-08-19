"""Port of ``vpt.algorithms.texture.HorizontalMorphologicalCovariance``:
opens/closes a mono-channel image with a growing horizontal 2-point SE
(``side = 2i+1`` for ``i`` in ``1..len``) and records a valuation of the
result normalised by the valuation of the unfiltered input -- a 10-way
switch of valuations (superset of ``LineGranulometry``'s 4, plus
``VARIANCE``/``SKEWNESS``/``KURTOSIS``/Fourier-variance/Fourier-kurtosis,
plus a ``LOCATIONS`` valuation absent elsewhere in the package).

``LOCATIONS`` (4) looked at first glance, from the raw JD-Core decompile,
like an unconditional fallthrough into ``numberOfDifferentPixels`` for
*every* valuation (a suspected "switch-case scope collapse", the exact
JD-Core failure mode `CLAUDE.md` warns about) -- the closing brace of
``case 4`` sits right where the switch's own closing brace would, and the
``throw new GlobalException(...)`` for unmatched values appears to sit
*after* the switch, textually unconditional. Verified false via `javap`:
the bytecode ``tableswitch`` has a real ``default`` target at the throw,
and ``case 4`` is its own self-contained if/else (``img is None or img is
self.input`` -> the whole-image pixel count; otherwise ->
``numberOfDifferentPixels(self.input, img)``) that always returns from
within the case -- ported as that if/else, not a fallthrough."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, jdiv
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


class HorizontalMorphologicalCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.valuation: int | None = None
        self.inputFields = "input,len,valuation"
        self.outputFields = "output"

    def execute(self) -> None:
        size = 2 * self.len
        self.output = [0.0] * size
        if self.input.getCDim() != 1:
            raise GlobalException("Only grayscale please")

        originalValue = self._valuation(self.input)
        for i in range(1, self.len + 1):
            side = i * 2 + 1

            se = FlatSE(side, 1, Point(i, 0))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, 0, True)

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
    def invoke(image: Image, length: int, valuation: int) -> list[float] | None:
        try:
            return HorizontalMorphologicalCovariance().preprocess(image, length, valuation)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.texture.SpatialLineGranulometry``: opens/
closes with a growing horizontal line SE (``FlatSE.hLine(2i+1)``) and
records a per-channel valuation of the result, normalised by the
valuation of the unfiltered input -- a 5-way switch (``VOLUME``,
``FOURIER_VOLUME``, ``MOMENT1``, ``FOURIER_MOMENT1``, ``MOMENT``).

**Preserved bug, bytecode-confirmed**: ``case 4`` (``MOMENT``) computes
``SpatialMoment.invoke(img.getChannel(channel), momentx, momenty)`` but
never returns it -- `javap` shows the call's result popped off the stack,
then falling straight through into the same unconditional
``throw new GlobalException("Invalid valuation type")`` that serves as the
switch's ``default``. So ``valuation=MOMENT`` always raises, regardless of
what the (silently discarded) spatial moment would have been -- ported as
an unreachable-but-present branch that computes the moment for parity and
then raises, not "fixed" into actually returning it.

Java's two ``invoke`` overloads (a 5-arg one, and a 3-arg one that passes
``momentx=momenty=0``, matching the field defaults) collapse into one
Python ``invoke`` with ``momentx: int = 0, momenty: int = 0``, the
established overload-collapsing convention elsewhere in the port."""

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
from ..statistical.SpatialMoment import SpatialMoment

VOLUME = 0
FOURIER_VOLUME = 1
MOMENT1 = 2
FOURIER_MOMENT1 = 3
MOMENT = 4


class SpatialLineGranulometry(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.valuation: int | None = None
        self.momentx: int = 0
        self.momenty: int = 0
        self.inputFields = "input,len,valuation,momentx,momenty"
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
        if self.valuation == MOMENT:
            SpatialMoment.invoke(img.getChannel(channel), self.momentx, self.momenty)
        raise GlobalException("Invalid valuation type")

    @staticmethod
    def invoke(
        image: Image, length: int, valuation: int, momentx: int = 0, momenty: int = 0
    ) -> list[float] | None:
        try:
            return SpatialLineGranulometry().preprocess(
                image, length, valuation, momentx, momenty
            )
        except GlobalException:
            traceback.print_exc()
        return None

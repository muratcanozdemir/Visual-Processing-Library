"""Port of ``vpt.algorithms.texture.SpatialCircularGranulometryFFT``:
identical to ``SpatialCircularGranulometry`` (same disk-SE opening/closing,
same ``1.0 - ratio`` / ``ratio`` asymmetry between the two branches), but
valuated by the ``(momentX, momentY)`` spatial moment of the FFT magnitude
rather than the plain spatial image."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ..frequential.FFT import FFT
from ..mm.gray.GClosing import GClosing
from ..mm.gray.GOpening import GOpening
from ..statistical.SpatialMoment import SpatialMoment


class SpatialCircularGranulometryFFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.step: int | None = None
        self.momentX: int | None = None
        self.momentY: int | None = None
        self.inputFields = "input,len,step,momentX,momentY"
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
                self.output[c * self.len * 2 + i - 1] = 1.0 - jdiv(
                    self._valuation(tmp, c), originalValues[c]
                )

            tmp = GClosing.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * self.len * 2 + self.len + i - 1] = jdiv(
                    self._valuation(tmp, c), originalValues[c]
                )

    def _valuation(self, img: Image, channel: int) -> float:
        fft: list[DoubleImage] = FFT.invoke(img.getChannel(channel), False, 2)
        return SpatialMoment.invoke(fft[0], self.momentX, self.momentY)[0]

    @staticmethod
    def invoke(
        image: Image, length: int, step: int, momentX: int, momentY: int
    ) -> list[float] | None:
        try:
            return SpatialCircularGranulometryFFT().preprocess(
                image, length, step, momentX, momentY
            )
        except GlobalException:
            traceback.print_exc()
        return None

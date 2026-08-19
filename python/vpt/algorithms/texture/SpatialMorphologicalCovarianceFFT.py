"""Port of ``vpt.algorithms.texture.SpatialMorphologicalCovarianceFFT``:
identical to ``SpatialMorphologicalCovariance`` (same four-direction
two-point-SE erosion scheme, same output layout), but valuated by the
``(momentX, momentY)`` spatial moment of each erosion's FFT magnitude
rather than the plain spatial image."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ..frequential.FFT import FFT
from ..mm.gray.GErosion import GErosion
from ..statistical.SpatialMoment import SpatialMoment


class SpatialMorphologicalCovarianceFFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.momentX: int | None = None
        self.momentY: int | None = None
        self.inputFields = "input, len, momentX, momentY"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * 4 * cdim
        self.output = [0.0] * size

        fft: list[DoubleImage] = FFT.invoke(self.input, False, 2)
        originalVolumes = SpatialMoment.invoke(fft[0], self.momentX, self.momentY)
        for i in range(1, self.len + 1):
            side = i * 2 + 1

            se = FlatSE(1, side, Point(0, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            moments = SpatialMoment.invoke(fft[0], self.momentX, self.momentY)
            for c in range(cdim):
                self.output[c * 4 * self.len + i - 1] = jdiv(moments[c], originalVolumes[c])

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            moments = SpatialMoment.invoke(fft[0], self.momentX, self.momentY)
            for c in range(cdim):
                self.output[c * 4 * self.len + self.len + i - 1] = jdiv(
                    moments[c], originalVolumes[c]
                )

            se = FlatSE(side, 1, Point(i, 0))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, 0, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            moments = SpatialMoment.invoke(fft[0], self.momentX, self.momentY)
            for c in range(cdim):
                self.output[c * 4 * self.len + 2 * self.len + i - 1] = jdiv(
                    moments[c], originalVolumes[c]
                )

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(side - 1, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            moments = SpatialMoment.invoke(fft[0], self.momentX, self.momentY)
            for c in range(cdim):
                self.output[c * 4 * self.len + 3 * self.len + i - 1] = jdiv(
                    moments[c], originalVolumes[c]
                )

    @staticmethod
    def invoke(image: Image, length: int, momentX: int, momentY: int) -> list[float] | None:
        try:
            return SpatialMorphologicalCovarianceFFT().preprocess(image, length, momentX, momentY)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.texture.MorphologicalCovarianceFFT``: the same
four-direction two-point-SE erosion scheme as ``MorphologicalCovariance``,
but valuated by FFT-domain volume (``Tools.volume(FFT.invoke(img, false,
2)[0], 0)``) instead of plain spatial volume."""

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
from ..mm.gray.GErosion import GErosion


class MorphologicalCovarianceFFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.inputFields = "input,len"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * 4 * cdim
        self.output = [0.0] * size

        originalVolumes = [0.0] * cdim
        for c in range(cdim):
            fft: list[DoubleImage] = FFT.invoke(self.input.getChannel(c), False, 2)
            originalVolumes[c] = Tools.volume(fft[0], 0)

        for i in range(1, self.len + 1):
            side = i * 2 + 1

            se = FlatSE(1, side, Point(0, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            fvol = Tools.volume(fft[0], 0)
            for c in range(cdim):
                self.output[c * 4 * self.len + i - 1] = jdiv(fvol, originalVolumes[c])

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            fvol = Tools.volume(fft[0], 0)
            for c in range(cdim):
                self.output[c * 4 * self.len + self.len + i - 1] = jdiv(fvol, originalVolumes[c])

            se = FlatSE(side, 1, Point(i, 0))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, 0, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            fvol = Tools.volume(fft[0], 0)
            for c in range(cdim):
                self.output[c * 4 * self.len + 2 * self.len + i - 1] = jdiv(
                    fvol, originalVolumes[c]
                )

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(side - 1, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)

            fft = FFT.invoke(tmp, False, 2)
            fvol = Tools.volume(fft[0], 0)
            for c in range(cdim):
                self.output[c * 4 * self.len + 3 * self.len + i - 1] = jdiv(
                    fvol, originalVolumes[c]
                )

    @staticmethod
    def invoke(image: Image, length: int) -> list[float] | None:
        try:
            return MorphologicalCovarianceFFT().preprocess(image, length)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.texture.MultiCircleFFTCovariance``: opens/
closes a mono-channel image with a growing circular SE
(``FlatSE.circle((i-1)*step + 1)`` for ``i`` in ``1..len``), FFTs the
result, and records ``howManyCircles`` concentric-ring volumes of the FFT
magnitude (``Tools.circularVolume``) per scale, each normalised by the
corresponding ring's volume in the unfiltered input's own FFT.

``output`` layout: ``[opening rows (len) ++ closing rows (len)]``, each row
``howManyCircles`` wide -- index ``(i-1)*howManyCircles + j`` for openings,
``howManyCircles*len + (i-1)*howManyCircles + j`` for closings."""

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


class MultiCircleFFTCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.step: int | None = None
        self.howManyCircles: int | None = None
        self.inputFields = "input,len,step,howManyCircles"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * self.howManyCircles * 2
        self.output = [0.0] * size
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale for now")

        originalValues = [0.0] * self.howManyCircles

        fft: list[DoubleImage] = FFT.invoke(self.input, True, 2)
        for j in range(self.howManyCircles):
            originalValues[j] = Tools.circularVolume(fft[0], 0, j, self.howManyCircles)

        for i in range(1, self.len + 1):
            se = FlatSE.circle((i - 1) * self.step + 1)

            tmp = GOpening.invoke(self.input, se)

            fft = FFT.invoke(tmp, True, 2)
            for j in range(self.howManyCircles):
                self.output[(i - 1) * self.howManyCircles + j] = jdiv(
                    Tools.circularVolume(fft[0], 0, j, self.howManyCircles), originalValues[j]
                )

            tmp = GClosing.invoke(self.input, se)

            fft = FFT.invoke(tmp, True, 2)
            for j in range(self.howManyCircles):
                self.output[
                    self.howManyCircles * self.len + (i - 1) * self.howManyCircles + j
                ] = jdiv(
                    Tools.circularVolume(fft[0], 0, j, self.howManyCircles), originalValues[j]
                )

    @staticmethod
    def invoke(
        image: Image, length: int, step: int, howManyCircles: int
    ) -> list[float] | None:
        try:
            return MultiCircleFFTCovariance().preprocess(image, length, step, howManyCircles)
        except GlobalException:
            traceback.print_exc()
        return None

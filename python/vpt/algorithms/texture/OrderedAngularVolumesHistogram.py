"""Port of ``vpt.algorithms.texture.OrderedAngularVolumesHistogram``:
flat-zone-quantizes the input (``GrayQFZSoille`` with alpha=omega=10,
replacing each zone with its mean via ``LabelsToMeanValue``), FFTs the
result, and records ``howManyAngles`` angular-wedge FFT volumes
(``Tools.regularAngularVolumes``), normalised by half the total FFT
volume and then sorted ascending.

Note ``self.input`` is reassigned mid-``execute`` to the flat-zone-mean
image (matching the Java, which does the same to its own ``this.input``
field) -- the FFT and the final ``regularAngularVolumes`` calls both run
on this replaced image, not the original."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..flatzones.gray.GrayQFZSoille import GrayQFZSoille
from ..frequential.FFT import FFT
from ..segmentation.LabelsToMeanValue import LabelsToMeanValue


class OrderedAngularVolumesHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.howManyAngles: int | None = None
        self.inputFields = "input, howManyAngles"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale for now")

        self.output = [0.0] * self.howManyAngles

        labels = GrayQFZSoille.invoke(self.input, 10, 10)
        self.input = LabelsToMeanValue.invoke(labels, self.input)

        fft: list[DoubleImage] = FFT.invoke(self.input, True, 2)

        normalizer = Tools.volume(fft[0], 0) / 2.0
        for i in range(self.howManyAngles):
            self.output[i] = jdiv(
                Tools.regularAngularVolumes(fft[0], i, self.howManyAngles), normalizer
            )
        self.output.sort()

    @staticmethod
    def invoke(image: Image, howManyAngles: int) -> list[float] | None:
        try:
            return OrderedAngularVolumesHistogram().preprocess(image, howManyAngles)
        except GlobalException:
            traceback.print_exc()
        return None

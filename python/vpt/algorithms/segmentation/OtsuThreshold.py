"""Port of ``vpt.algorithms.segmentation.OtsuThreshold``.

Global thresholding by maximising Otsu's between-class variance over the
un-normalised byte histogram, then delegating to ``Threshold``.
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ..histogram.Histogram import Histogram
from .Threshold import Threshold


class OtsuThreshold(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: BooleanImage | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        variances = [0.0] * 254

        histo = Histogram.invoke(self.input, False)
        for i in range(1, 255):
            n1 = 0.0
            n2 = 0.0
            mean1 = 0.0
            mean2 = 0.0
            for j in range(i):
                n1 += histo[j]
                mean1 += histo[j] * j
            for j in range(i, 256):
                n2 += histo[j]
                mean2 += histo[j] * j

            if n1 != 0.0 and n2 != 0.0:
                mean1 /= n1
                mean2 /= n2
                variances[i - 1] = n1 * n2 * (mean1 - mean2) * (mean1 - mean2)

        max_i = 0
        for i in range(1, len(variances)):
            if variances[i] > variances[max_i]:
                max_i = i

        self.output = Threshold.invoke(self.input, (max_i + 1) * DoubleImage.byteToDouble)

    @staticmethod
    def invoke(image: Image) -> BooleanImage | None:
        try:
            return OtsuThreshold().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

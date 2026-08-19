"""Port of ``vpt.algorithms.segmentation.Otsu``: the threshold-selection
half of Otsu's method in isolation (returns the best-threshold index rather
than a thresholded image -- see ``OtsuThreshold`` for the full algorithm,
which does not call this class).

Note: the Java source shows ``new double['\\u00fe']`` -- JD-Core rendering
the array-size push (bytecode: ``sipush 254``) as a char literal since 254
fits `char`'s range; verified against the class file. It's ``new
double[254]``, not a 1-element array indexed by a unicode character."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..histogram.Histogram import Histogram


class Otsu(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: int | None = None
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

        maximum = 0
        for i in range(1, len(variances)):
            if variances[i] > variances[maximum]:
                maximum = i
        self.output = maximum + 1

    @staticmethod
    def invoke(input: Image) -> int | None:
        try:
            return Otsu().preprocess(input)
        except GlobalException:
            traceback.print_exc()
        return None

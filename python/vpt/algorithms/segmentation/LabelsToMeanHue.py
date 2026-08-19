"""Port of ``vpt.algorithms.segmentation.LabelsToMeanHue``: replace every
pixel with its label region's circular mean hue (accumulate ``sin``/``cos``
of each hue angle per label, then ``atan2`` back to an angle in ``[0, 1)``)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jatan2_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image

TWO_PI = 6.283185307179586


class LabelsToMeanHue(Algorithm):
    def __init__(self):
        super().__init__()
        self.labelImage: Image | None = None
        self.original: Image | None = None
        self.output: Image | None = None
        self.inputFields = "labelImage,original"
        self.outputFields = "output"

    def execute(self) -> None:
        xDim = self.original.getXDim()
        yDim = self.original.getYDim()
        if self.original.getCDim() != 1 or self.labelImage.getCDim() != 1:
            raise GlobalException("The input and label images must be mono-channel!")

        self.output = self.original.newInstance(False)

        # np.bincount-by-label sin/cos sums, same shape as LabelsToMeanValue,
        # then jatan2_arr (added in 6.6) back to an angle. The sums are
        # FP-order-sensitive, so both arrays are transposed to (xDim, yDim)
        # before raveling: a C-order ravel of that shape visits (x, y) in
        # x-outer/y-inner order, matching the nested `for x: for y:` loop's
        # summation order exactly (see LabelsToMeanValue's longer note on
        # why np.bincount(weights=...) reproduces per-bin order this way).
        labelsArr = self.labelImage.getIntArray().reshape(yDim, xDim).T.ravel()
        nLabels = int(labelsArr.max()) + 1 if labelsArr.size else 0

        # `* np.pi * 2.0` (not `* (2.0 * np.pi)`) to match the scalar loop's
        # left-to-right rounding: `val * math.pi` rounds once, then `* 2.0`
        # is an exact power-of-two scale with no further rounding, whereas
        # precomputing `2*pi` first would round the multiply at a different
        # point and can differ by an ULP.
        angles = self.original.getDoubleArray().reshape(yDim, xDim).T.ravel() * np.pi * 2.0
        sinSums = np.bincount(labelsArr, weights=np.sin(angles), minlength=nLabels)
        cosSums = np.bincount(labelsArr, weights=np.cos(angles), minlength=nLabels)

        hue = jatan2_arr(sinSums, cosSums) / TWO_PI
        hue = np.where(hue < 0.0, hue + 1.0, hue)

        out = hue[labelsArr].reshape(xDim, yDim).T
        self.output.setDoubleArray(out.reshape(-1))

    @staticmethod
    def invoke(labelImage: Image, original: Image) -> Image | None:
        try:
            return LabelsToMeanHue().preprocess(labelImage, original)
        except GlobalException:
            traceback.print_exc()
        return None

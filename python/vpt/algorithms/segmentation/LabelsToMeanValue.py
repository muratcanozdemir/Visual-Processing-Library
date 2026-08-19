"""Port of ``vpt.algorithms.segmentation.LabelsToMeanValue``: replace every
pixel with the per-channel mean of its label's region.

``labels[label][c+1] /= labels[label][0]`` divides by that label's pixel
count, which is ``0.0`` for any label index with no pixels at all (label
numbering can have gaps if the max label value isn't densely used) -- a
genuine ``0.0/0.0`` Java yields as NaN, not an exception, so routed through
``jdiv``."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class LabelsToMeanValue(Algorithm):
    def __init__(self):
        super().__init__()
        self.labelImage: Image | None = None
        self.baseImage: Image | None = None
        self.outputImage: Image | None = None
        self.inputFields = "labelImage,baseImage"
        self.outputFields = "outputImage"

    def execute(self) -> None:
        self.outputImage = self.baseImage.newInstance(False)

        xDim = self.outputImage.getXDim()
        yDim = self.outputImage.getYDim()
        cDim = self.outputImage.getCDim()

        # np.bincount-by-label, same pattern as Histogram.py (3.2). The
        # per-label sums are FP-order-sensitive (unlike a pure count), so
        # the label and base-image arrays are both transposed to (xDim,
        # yDim, ...) before raveling: a C-order ravel of that shape visits
        # (x, y) in x-outer/y-inner order, matching the nested `for x: for
        # y:` loop above exactly -- np.bincount(weights=...) accumulates
        # each bin's contributions in the order they appear in its input,
        # so this reproduces the scalar loop's summation order bin-by-bin.
        # getXYCMaximum(0) (double view, x-outer/y-inner argmax) and
        # getIntArray().max() agree on which pixel is the max-labelled one
        # here: IntegerImage's getDouble is a strictly monotonic function of
        # the raw int32 value, so the two orderings coincide.
        labelsArr = self.labelImage.getIntArray().reshape(yDim, xDim).T.ravel()
        nLabels = int(labelsArr.max()) + 1 if labelsArr.size else 0

        counts = np.bincount(labelsArr, minlength=nLabels).astype(np.float64)
        base = self.baseImage.getDoubleArray().reshape(yDim, xDim, cDim)
        means = np.empty((nLabels, cDim), dtype=np.float64)
        for c in range(cDim):
            chan = base[:, :, c].T.ravel()
            sums = np.bincount(labelsArr, weights=chan, minlength=nLabels)
            means[:, c] = jdiv_arr(sums, counts)

        out = means[labelsArr].reshape(xDim, yDim, cDim).transpose(1, 0, 2)
        self.outputImage.setDoubleArray(out.reshape(-1))

    @staticmethod
    def invoke(labels: Image, original: Image) -> Image | None:
        try:
            return LabelsToMeanValue().preprocess(labels, original)
        except GlobalException:
            traceback.print_exc()
        return None

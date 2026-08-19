"""Port of ``vpt.algorithms.histogram.ContrastStretch``.

Per-channel min-max normalisation to ``[0, 1]``. A channel that is
constant (``max == min``) is left untouched rather than divided by zero.
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class ContrastStretch(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.inputImage: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.inputImage.newInstance(True)

        xdim = self.output.getXDim()
        ydim = self.output.getYDim()
        cdim = self.output.getCDim()

        # Each output pixel is an independent (pixel-min)/(max-min) formula
        # (no cross-pixel accumulation), so this vectorizes losslessly; the
        # divisor is guaranteed nonzero by the `maximum != minimum` guard,
        # matching Java's plain `/` with no need for jdiv.
        doubles = self.output.getDoubleArray().reshape(ydim, xdim, cdim)
        changed = False
        for c in range(cdim):
            p = self.output.getXYCMaximum(c)
            maximum = self.output.getXYCDouble(p.x, p.y, c)

            p = self.output.getXYCMinimum(c)
            minimum = self.output.getXYCDouble(p.x, p.y, c)

            if maximum != minimum:
                doubles[:, :, c] = (doubles[:, :, c] - minimum) / (maximum - minimum)
                changed = True

        if changed:
            self.output.setDoubleArray(doubles.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return ContrastStretch().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.conversion.NonUniformHSY833``: like
``NonUniformHSY733``, but hue is split into 8 buckets instead of 7 (an
extra split at 110 degrees between buckets 3 and 4)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class NonUniformHSY833(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 3:
            raise GlobalException("The input image must have 3 channels!")

        self.output = self.input.newInstance(True)

        # Elementwise per-pixel formula -- vectorizes losslessly, same
        # exhaustive-bucket reasoning as NonUniformHSY733.
        hsi = self.input.getByteArray().reshape(ydim, xdim, cdim)
        h, s, i = hsi[:, :, 0].astype(np.float64), hsi[:, :, 1], hsi[:, :, 2]

        sOut = jint_arr(np.floor(s / 86.0))
        iOut = jint_arr(np.floor(i / 86.0))

        hScaled = jint_arr(np.floor(h * 1.411764705882353))
        hOut = np.select(
            [
                (hScaled >= 330) | (hScaled <= 22),
                (hScaled >= 22) & (hScaled <= 45),
                (hScaled >= 45) & (hScaled <= 70),
                (hScaled >= 70) & (hScaled <= 110),
                (hScaled >= 110) & (hScaled <= 155),
                (hScaled >= 155) & (hScaled <= 186),
                (hScaled >= 186) & (hScaled <= 278),
                (hScaled >= 278) & (hScaled <= 330),
            ],
            [0, 1, 2, 3, 4, 5, 6, 7],
            default=hScaled,
        )

        out = np.empty((ydim, xdim, cdim), dtype=np.int64)
        out[:, :, 0], out[:, :, 1], out[:, :, 2] = hOut, sOut, iOut
        self.output.setByteArray(out.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return NonUniformHSY833().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

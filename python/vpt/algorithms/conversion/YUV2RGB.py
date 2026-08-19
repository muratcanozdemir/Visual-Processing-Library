"""Port of ``vpt.algorithms.conversion.YUV2RGB``: YUV back to sRGB (as
bytes), clamping any out-of-range result to 0 rather than the usual
0-or-255 saturation (see ``convert``'s docstring)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint, jint_arr, jround, jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image


class YUV2RGB(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = ByteImage(self.input, False)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus YUV image")

        # Elementwise linear combination + round/clamp -- vectorizes
        # losslessly via the array-suffixed jround_arr/jint_arr twins.
        yuv = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        Y, U, V = yuv[:, :, 0], yuv[:, :, 1], yuv[:, :, 2]

        rgb = np.empty((ydim, xdim, cdim), dtype=np.int64)
        rgb[:, :, 0] = jint_arr(jround_arr((Y + 1.14 * V) * 255.0))
        rgb[:, :, 1] = jint_arr(jround_arr((Y - 0.395 * U - 0.581 * V) * 255.0))
        rgb[:, :, 2] = jint_arr(jround_arr((Y + 2.032 * U) * 255.0))
        # Both underflow and overflow clamp to 0 (see the module docstring),
        # not the usual 0-or-255 saturation.
        rgb = np.where((rgb < 0) | (rgb > 255), 0, rgb)

        self.output.setByteArray(rgb.reshape(-1))

    @staticmethod
    def convert(Y: float, U: float, V: float) -> list[int]:
        rgb = [0, 0, 0]

        # Both underflow and overflow clamp to 0 (see the module docstring),
        # not the usual 0-or-255 saturation.
        rgb[0] = jint(jround((Y + 1.14 * V) * 255.0))
        if rgb[0] < 0 or rgb[0] > 255:
            rgb[0] = 0

        rgb[1] = jint(jround((Y - 0.395 * U - 0.581 * V) * 255.0))
        if rgb[1] < 0 or rgb[1] > 255:
            rgb[1] = 0

        rgb[2] = jint(jround((Y + 2.032 * U) * 255.0))
        if rgb[2] < 0 or rgb[2] > 255:
            rgb[2] = 0

        return rgb

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return YUV2RGB().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

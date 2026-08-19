"""Port of ``vpt.algorithms.conversion.XYZ2RGB``: CIE XYZ back to sRGB (as
bytes) via the standard inverse linear transform, saturating to [0, 255]."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint, jint_arr, jround, jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image


class XYZ2RGB(Algorithm):
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
            raise GlobalException("The input must be a tristumulus XYZ image")

        # Elementwise linear combination + round/clamp -- vectorizes
        # losslessly via the array-suffixed jround_arr/jint_arr twins.
        xyz = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        X, Y, Z = xyz[:, :, 0], xyz[:, :, 1], xyz[:, :, 2]

        rgb = np.empty((ydim, xdim, cdim), dtype=np.int64)
        rgb[:, :, 0] = jint_arr(jround_arr((3.2404542 * X - 1.5371385 * Y - 0.4985314 * Z) * 255.0))
        rgb[:, :, 1] = jint_arr(jround_arr((-0.969266 * X + 1.8760108 * Y + 0.041556 * Z) * 255.0))
        rgb[:, :, 2] = jint_arr(jround_arr((0.0556434 * X - 0.2040259 * Y + 1.0572252 * Z) * 255.0))
        rgb = np.clip(rgb, 0, 255)

        self.output.setByteArray(rgb.reshape(-1))

    @staticmethod
    def convert(X: float, Y: float, Z: float) -> list[int]:
        rgb = [0, 0, 0]

        rgb[0] = jint(jround((3.2404542 * X - 1.5371385 * Y - 0.4985314 * Z) * 255.0))
        if rgb[0] < 0:
            rgb[0] = 0
        elif rgb[0] > 255:
            rgb[0] = 255

        rgb[1] = jint(jround((-0.969266 * X + 1.8760108 * Y + 0.041556 * Z) * 255.0))
        if rgb[1] < 0:
            rgb[1] = 0
        elif rgb[1] > 255:
            rgb[1] = 255

        rgb[2] = jint(jround((0.0556434 * X - 0.2040259 * Y + 1.0572252 * Z) * 255.0))
        if rgb[2] < 0:
            rgb[2] = 0
        elif rgb[2] > 255:
            rgb[2] = 255

        return rgb

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return XYZ2RGB().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

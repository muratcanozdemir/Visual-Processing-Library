"""Port of ``vpt.algorithms.conversion.HSY2RGB``: the inverse of
``RGB2HSY`` -- hue/saturation/luma back to sRGB bytes, saturating to
[0, 255]."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jdiv, jdiv_arr, jint, jint_arr, jround, jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image

_PI_OVER_3 = 1.047197551196598
_TWO_PI_OVER_3 = 2.094395102393195


class HSY2RGB(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("And why exactly isn't the input image in color?")

        self.output = ByteImage(self.input, False)

        # Elementwise per-pixel formula -- vectorizes losslessly. `_PI_OVER_3`
        # is a nonzero constant so the `k`/`Hstar` divide needs no jdiv, but
        # `C`'s divisor (2*sin(...)) can be zero for particular Hstar values,
        # same hazard as RGB2HSY's C1/C -- jdiv_arr handles it.
        hsy = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        H, S, Y = hsy[:, :, 0], hsy[:, :, 1], hsy[:, :, 2]

        H = H * math.pi * 2.0

        k = jint_arr(np.floor(H / _PI_OVER_3))
        Hstar = H - k * _PI_OVER_3
        C = jdiv_arr(math.sqrt(3.0) * S, 2.0 * np.sin(_TWO_PI_OVER_3 - Hstar))

        C1 = C * np.cos(H)
        C2 = -1.0 * C * np.sin(H)

        R = 1.0 * Y + 0.701 * C1 + 0.2730866773266931 * C2
        G = 1.0 * Y - 0.299 * C1 - 0.3042635918629327 * C2
        B = 1.0 * Y - 0.299 * C1 + 0.8504369465163189 * C2

        rgb = np.empty((ydim, xdim, cdim), dtype=np.int64)
        rgb[:, :, 0] = jint_arr(jround_arr(R * 255.0))
        rgb[:, :, 1] = jint_arr(jround_arr(G * 255.0))
        rgb[:, :, 2] = jint_arr(jround_arr(B * 255.0))
        rgb = np.clip(rgb, 0, 255)

        self.output.setByteArray(rgb.reshape(-1))

    @staticmethod
    def convert(H: float, S: float, Y: float) -> list[int]:
        rgb = [0, 0, 0]

        H = H * math.pi * 2.0

        k = jint(math.floor(H / _PI_OVER_3))
        Hstar = H - k * _PI_OVER_3
        # Java double division: a zero denominator (Hstar hitting exactly
        # pi/3's midpoint) yields Infinity/NaN, not ZeroDivisionError.
        C = jdiv(math.sqrt(3.0) * S, 2.0 * math.sin(_TWO_PI_OVER_3 - Hstar))

        C1 = C * math.cos(H)
        C2 = -1.0 * C * math.sin(H)

        R = 1.0 * Y + 0.701 * C1 + 0.2730866773266931 * C2
        G = 1.0 * Y - 0.299 * C1 - 0.3042635918629327 * C2
        B = 1.0 * Y - 0.299 * C1 + 0.8504369465163189 * C2

        rgb[0] = jint(jround(R * 255.0))
        if rgb[0] > 255:
            rgb[0] = 255
        elif rgb[0] < 0:
            rgb[0] = 0

        rgb[1] = jint(jround(G * 255.0))
        if rgb[1] > 255:
            rgb[1] = 255
        elif rgb[1] < 0:
            rgb[1] = 0

        rgb[2] = jint(jround(B * 255.0))
        if rgb[2] > 255:
            rgb[2] = 255
        elif rgb[2] < 0:
            rgb[2] = 0

        return rgb

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return HSY2RGB().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

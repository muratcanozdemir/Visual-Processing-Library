"""Port of ``vpt.algorithms.conversion.RGB2NUHSY733``: RGB straight to the
non-uniform "733" HSY bucket scheme in one pass (rather than via a separate
``RGB2HSY`` + ``NonUniformHSY733`` step -- ``convert`` here is the same
HSY formula as ``RGB2HSY.convert``, duplicated exactly as the Java does
rather than shared, so this file stays 1:1 diffable against its source)."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jacos, jacos_arr, jdiv, jdiv_arr, jint_arr
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image

TWO_PI = 6.283185307179586


class RGB2NUHSY733(Algorithm):
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

        self.output = DoubleImage(self.input, False)

        # Elementwise per-pixel formula -- vectorizes losslessly, combining
        # the RGB2HSY core-formula reasoning (mutually-exclusive/exhaustive
        # hue branches, jdiv_arr/jacos_arr NaN/zero-safe) with the
        # NonUniformHSY733 bucket-quantization reasoning (exhaustive bucket
        # conditions, Java `else` debug-print branch is dead code).
        rgb = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        r, g, b = rgb[:, :, 0], rgb[:, :, 1], rgb[:, :, 2]

        y_ = 0.299 * r + 0.587 * g + 0.114 * b

        C1 = r - 0.5 * g - 0.5 * b
        C2 = -math.sqrt(3.0) / 2.0 * g + math.sqrt(3.0) / 2.0 * b
        C = np.sqrt(C1 * C1 + C2 * C2)

        ratio = jdiv_arr(C1, C)
        hue0 = np.select(
            [C == 0.0, C2 <= 0.0, C2 > 0.0],
            [0.0, jacos_arr(ratio), TWO_PI - jacos_arr(ratio)],
        )
        hue0 = hue0 / TWO_PI

        sat = np.maximum(r, np.maximum(g, b)) - np.minimum(r, np.minimum(g, b))

        satOut = jint_arr(np.floor(255.0 * sat / 86.0))
        lumOut = jint_arr(np.floor(255.0 * y_ / 86.0))

        hueScaled = jint_arr(np.floor(hue0 * 360.0))
        hueOut = np.select(
            [
                (hueScaled >= 330) | (hueScaled <= 22),
                (hueScaled >= 22) & (hueScaled <= 45),
                (hueScaled >= 45) & (hueScaled <= 70),
                (hueScaled >= 70) & (hueScaled <= 155),
                (hueScaled >= 155) & (hueScaled <= 186),
                (hueScaled >= 186) & (hueScaled <= 278),
                (hueScaled >= 278) & (hueScaled <= 330),
            ],
            [0, 1, 2, 3, 4, 5, 6],
            default=hueScaled,
        )

        out = np.empty((ydim, xdim, cdim), dtype=np.int64)
        out[:, :, 0], out[:, :, 1], out[:, :, 2] = hueOut, satOut, lumOut
        self.output.setByteArray(out.reshape(-1))

    @staticmethod
    def convert(r: float, g: float, b: float) -> list[float]:
        hsy = [0.0, 0.0, 0.0]

        hsy[2] = 0.299 * r + 0.587 * g + 0.114 * b

        C1 = r - 0.5 * g - 0.5 * b
        C2 = -math.sqrt(3.0) / 2.0 * g + math.sqrt(3.0) / 2.0 * b

        C = math.sqrt(C1 * C1 + C2 * C2)
        if C <= 0.0 and C >= 0.0:
            hsy[0] = 0.0
        elif C != 0.0 and C2 <= 0.0:
            hsy[0] = jacos(jdiv(C1, C))
        elif C != 0.0 and C2 > 0.0:
            hsy[0] = TWO_PI - jacos(jdiv(C1, C))

        hsy[1] = max(r, max(g, b)) - min(r, min(g, b))

        hsy[0] /= TWO_PI

        return hsy

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return RGB2NUHSY733().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

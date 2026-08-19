"""Port of ``vpt.algorithms.conversion.RGB2HSY``: sRGB to a hue/saturation/
luma colour space (hue as an angle normalised to [0, 1)).

Uses ``jacos``/``jdiv``: the ``C1/C`` ratio is only evaluated once ``C`` is
known nonzero, but accumulated rounding can still push it just outside
[-1, 1], where Java's ``Math.acos`` returns NaN rather than raising (see
``util.Tools.getAngleABC``'s identical hazard, already handled this way)."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jacos, jacos_arr, jdiv, jdiv_arr
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image

TWO_PI = 6.283185307179586


class RGB2HSY(Algorithm):
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

        # Elementwise per-pixel formula -- vectorizes losslessly. The three
        # hue branches (C == 0; C != 0 and C2 <= 0; C != 0 and C2 > 0) are
        # mutually exclusive and exhaustive, so np.select with first-match
        # priority reproduces the if/elif chain exactly. jdiv_arr/jacos_arr
        # are NaN/zero-divisor-safe, so it's fine that np.select evaluates
        # every branch's array eagerly even where it won't be selected (the
        # C == 0 pixels feed jdiv_arr(0, 0) -> NaN into the unused branches,
        # discarded by the selection).
        rgb = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        r, g, b = rgb[:, :, 0], rgb[:, :, 1], rgb[:, :, 2]

        y_ = 0.299 * r + 0.587 * g + 0.114 * b

        C1 = r - 0.5 * g - 0.5 * b
        C2 = -math.sqrt(3.0) / 2.0 * g + math.sqrt(3.0) / 2.0 * b
        C = np.sqrt(C1 * C1 + C2 * C2)

        ratio = jdiv_arr(C1, C)
        hue = np.select(
            [C == 0.0, C2 <= 0.0, C2 > 0.0],
            [0.0, jacos_arr(ratio), TWO_PI - jacos_arr(ratio)],
        )
        hue = hue / TWO_PI

        sat = np.maximum(r, np.maximum(g, b)) - np.minimum(r, np.minimum(g, b))

        hsy = np.empty((ydim, xdim, cdim), dtype=np.float64)
        hsy[:, :, 0], hsy[:, :, 1], hsy[:, :, 2] = hue, sat, y_
        self.output.setDoubleArray(hsy.reshape(-1))

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
            return RGB2HSY().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

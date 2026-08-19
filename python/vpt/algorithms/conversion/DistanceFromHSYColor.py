"""Port of ``vpt.algorithms.conversion.DistanceFromHSYColor``: distance of
every pixel of an HSY image from a reference RGB colour (converted to HSY
internally), blending hue distance and luma distance by a sigmoid of
saturation. Below a low luma or saturation floor, distance is pinned to 1.0
(treated as maximally different -- low-saturation/low-luma hue is
unreliable).

``nonlinear`` is a genuine dead field here: the Java class declares it but
never wires it into ``inputFields`` or reads it in ``execute()`` (unlike the
near-identical ``EuclideanDistanceFromRGBColor``, which does use its own
``nonlinear``). Kept as an unused attribute for structural parity, not
included in ``inputFields``."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from .RGB2HSY import RGB2HSY


class DistanceFromHSYColor(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.red: float | None = None
        self.green: float | None = None
        self.blue: float | None = None
        self.output: Image | None = None
        self.nonlinear: bool | None = None
        self.inputFields = "input, red, green, blue"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 1)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus color HSY image")

        ref = RGB2HSY.convert(self.red, self.green, self.blue)

        # Elementwise per-pixel formula, each branch selected only by that
        # pixel's own l/s -- vectorizes losslessly. The guarded branch
        # (l<=0.1 or s<=0.1) never evaluates hueDist/lumDist/sigma in the
        # scalar version, but they're pure arithmetic with no domain
        # restriction (abs, exp), so computing them for every pixel and
        # selecting via np.where afterward is equivalent.
        hsl = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        h, s, l = hsl[:, :, 0], hsl[:, :, 1], hsl[:, :, 2]

        a = np.abs(ref[0] - h)
        hueDist = np.where(a <= 0.5, a, 1.0 - a)
        lumDist = np.abs(l - ref[2])
        with np.errstate(over="ignore"):
            sigma = 1.0 / (1.0 + np.exp(-10.0 * (s - 0.35)))
        blend = hueDist * (1.0 - sigma) + lumDist * sigma

        distance = np.where((l <= 0.1) | (s <= 0.1), 1.0, blend)
        self.output.setDoubleArray(distance.reshape(-1))

    @staticmethod
    def _sigma(z: float) -> float:
        return 1.0 / (1.0 + math.exp(-10.0 * (z - 0.35)))

    @staticmethod
    def invoke(image: Image, red: float, green: float, blue: float) -> Image | None:
        try:
            return DistanceFromHSYColor().preprocess(image, red, green, blue)
        except GlobalException:
            traceback.print_exc()
        return None

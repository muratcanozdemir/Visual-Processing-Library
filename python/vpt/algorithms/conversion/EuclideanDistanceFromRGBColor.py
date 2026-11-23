"""Port of ``vpt.algorithms.conversion.EuclideanDistanceFromRGBColor``:
per-pixel Euclidean distance from a fixed reference RGB colour, normalised
by ``sqrt(3)`` (the maximum possible distance in a unit RGB cube), and
optionally squashed through a sigmoid."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jexp, jexp_arr
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class EuclideanDistanceFromRGBColor(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.red: float | None = None
        self.green: float | None = None
        self.blue: float | None = None
        self.output: Image | None = None
        self.nonlinear: bool | None = None
        self.inputFields = "input, red, green, blue, nonlinear"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 1)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus RGB image")

        # Distance.euclidean accumulates (p[i]-q[i])**2 left-to-right over
        # a fixed 3-term (R,G,B) sequence -- summing the three per-channel
        # squared-difference arrays in the same order is bit-exact to that
        # accumulation, and the sum of squares is never negative so a plain
        # np.sqrt (no jsqrt guard) is safe.
        rgb = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        e0 = rgb[:, :, 0] - self.red
        d0 = e0 * e0
        e1 = rgb[:, :, 1] - self.green
        d1 = e1 * e1
        e2 = rgb[:, :, 2] - self.blue
        d2 = e2 * e2
        distance = np.sqrt(d0 + d1 + d2) / math.sqrt(3.0)
        if self.nonlinear:
            # Array twin of _sigma -- math.exp is scalar-only, np.exp is the
            # array equivalent (both correctly-rounded IEEE-754, verified
            # bit-exact against the golden here rather than assumed).
            with np.errstate(over="ignore"):
                distance = 1.0 / (1.0 + jexp_arr(-5.0 * (distance - 0.5)))

        self.output.setDoubleArray(distance.reshape(-1))

    @staticmethod
    def _sigma(z: float) -> float:
        return 1.0 / (1.0 + jexp(-5.0 * (z - 0.5)))

    @staticmethod
    def invoke(
        image: Image, red: float, green: float, blue: float, nonlinear: bool
    ) -> Image | None:
        try:
            return EuclideanDistanceFromRGBColor().preprocess(image, red, green, blue, nonlinear)
        except GlobalException:
            traceback.print_exc()
        return None

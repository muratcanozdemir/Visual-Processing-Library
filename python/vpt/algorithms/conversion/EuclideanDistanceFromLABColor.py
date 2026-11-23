"""Port of ``vpt.algorithms.conversion.EuclideanDistanceFromLABColor``: like
``EuclideanDistanceFromRGBColor``, but the metric space is L*a*b* (byte-
scaled via ``XYZ2LAB.scaleToByte``) rather than raw RGB, and the reference
colour is converted RGB -> XYZ -> LAB and rescaled the same way by hand.

The Java also computes ``refHue = RGB2HSY.convert(red, green, blue)[0]`` and
then never reads it again -- a pure, side-effect-free dead computation.
Omitted here rather than transcribed: unlike this port's other preserved
quirks, there is no observable behaviour to preserve (nothing is printed,
no exception can result, and no shared state is touched)."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jint
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from .RGB2XYZ import RGB2XYZ
from .XYZ2LAB import XYZ2LAB


class EuclideanDistanceFromLABColor(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.red: float | None = None
        self.green: float | None = None
        self.blue: float | None = None
        self.output: Image | None = None
        self.inputFields = "input, red, green, blue"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input.getXDim(), self.input.getYDim(), 1)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus RGB image")

        q = RGB2XYZ.convert(
            jint(self.red * 255.0), jint(self.green * 255.0), jint(self.blue * 255.0)
        )
        q = XYZ2LAB.convert(q[0], q[1], q[2])
        q[0] = q[0] * 2.55 / 255.0
        q[1] = (q[1] + 128.0) / 255.0
        q[2] = (q[2] + 128.0) / 255.0

        self.input = RGB2XYZ.invoke(self.input)
        self.input = XYZ2LAB.invoke(self.input)
        self.input = XYZ2LAB.scaleToByte(self.input)

        # Same left-to-right 3-term accumulation as Distance.euclidean,
        # vectorized -- see EuclideanDistanceFromRGBColor for the reasoning.
        p = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        e0 = p[:, :, 0] - q[0]
        d0 = e0 * e0
        e1 = p[:, :, 1] - q[1]
        d1 = e1 * e1
        e2 = p[:, :, 2] - q[2]
        d2 = e2 * e2
        distance = np.sqrt(d0 + d1 + d2) / math.sqrt(3.0)

        self.output.setDoubleArray(distance.reshape(-1))

    @staticmethod
    def invoke(image: Image, red: float, green: float, blue: float) -> Image | None:
        try:
            return EuclideanDistanceFromLABColor().preprocess(image, red, green, blue)
        except GlobalException:
            traceback.print_exc()
        return None

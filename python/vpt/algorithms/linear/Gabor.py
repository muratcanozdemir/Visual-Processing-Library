"""Port of ``vpt.algorithms.linear.Gabor``: build a Gabor filter kernel
(one or more orientations summed) and convolve.

The Java accumulates ``d`` and ``sum`` through an explicit ``(float)``
narrowing cast at each step (``d += (float)gaborFunction(...)``; ``float
sum``), not full double precision -- a genuine, deliberate precision loss
in the original, not a decompiler artefact (there is no reason a purely
double-typed computation would otherwise need a cast). Reproduced via
``jfloat``, which performs the same double->float32->double round-trip
Java's implicit widening does after each cast. ``sum /= width * height``
is float ``/=`` int, which JLS 5.6.2 binary numeric promotion makes a
*true* single-precision division (the int promotes to float, not double)
-- ``jfloat_div``, not ``jfloat(sum_ / ...)``, since double-precision
division-then-round-to-float can disagree with a single correctly-rounded
float32 division by up to 1 ulp (double rounding)."""

from __future__ import annotations

import math
import traceback

from ..._javacompat import Point, idiv, jcos, jexp, jfloat, jfloat_div, jlog, jpow, jsin
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ..histogram.ContrastStretch import ContrastStretch
from .Convolution import Convolution

DEFAULT_ORIENTATIONS = [0.0]
DEFAULT_WAVE_LENGTH = 1.0
DEFAULT_PHASE_OFFSET = 0.0
DEFAULT_ASPECT_RATIO = 0.5
DEFAULT_BANDWIDTH = 1.0
DEFAULT_WIDTH = 3
DEFAULT_HEIGHT = 3
MIN_ASPECT_RATIO = 0.0
MAX_ASPECT_RATIO = 1.0


class Gabor(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.orientations: list[float] | None = None
        self.waveLength: float | None = None
        self.phaseOffset: float | None = None
        self.aspectRatio: float | None = None
        self.bandwidth: float | None = None
        self.width: int | None = None
        self.height: int | None = None
        self.output: Image | None = None
        self.inputFields = (
            "input, waveLength, orientations, phaseOffset, aspectRatio, bandwidth, width, height"
        )
        self.outputFields = "output"

    @staticmethod
    def _calculateSigma(waveLength: float, bandwidth: float) -> float:
        return (
            waveLength
            * math.sqrt(jlog(2.0) / 2.0)
            * (jpow(2.0, bandwidth) + 1.0)
            / ((jpow(2.0, bandwidth) - 1.0) * math.pi)
        )

    @staticmethod
    def _gaborFunction(
        x: float, y: float, sigma: float, aspectRatio: float, waveLength: float, phaseOffset: float
    ) -> float:
        gaborReal = jexp(
            -(jpow(x / sigma, 2.0) + jpow(y * aspectRatio / sigma, 2.0)) / 2.0
        ) * jcos(6.283185307179586 * x / waveLength + phaseOffset)
        gaborImage = jexp(
            -(jpow(x / sigma, 2.0) + jpow(y * aspectRatio / sigma, 2.0)) / 2.0
        ) * jsin(6.283185307179586 * x / waveLength + phaseOffset)
        return math.sqrt(jpow(gaborReal, 2.0) + jpow(gaborImage, 2.0))

    def getKernel(self) -> NonFlatSE:
        sigma = Gabor._calculateSigma(self.waveLength, self.bandwidth)

        dimage = DoubleImage(self.width, self.height, 1)
        dimage.fill(0.0)
        for x in range(-idiv(self.width, 2), idiv(self.width, 2) + 1):
            for y in range(-idiv(self.height, 2), idiv(self.height, 2) + 1):
                d = 0.0
                for orientation in self.orientations:
                    x1 = x * jcos(orientation) + y * jsin(orientation)
                    y1 = -1 * x * jsin(orientation) + y * jcos(orientation)
                    d += jfloat(
                        Gabor._gaborFunction(
                            x1, y1, sigma, self.aspectRatio, self.waveLength, self.phaseOffset
                        )
                    )
                dimage.setXYDouble(x + idiv(self.width, 2), y + idiv(self.height, 2), d)

        sum_ = 0.0
        for x in range(self.width):
            for y in range(self.height):
                sum_ = jfloat(sum_ + dimage.getXYDouble(x, y))

        # `sum /= width * height` in Java is float /= int: JLS binary
        # numeric promotion widens the int to float (not double), so this
        # is a true single-precision division, not double-then-round.
        sum_ = jfloat_div(sum_, self.width * self.height)
        for x in range(self.width):
            for y in range(self.height):
                d = dimage.getXYDouble(x, y)
                dimage.setXYDouble(x, y, d - sum_)

        return NonFlatSE(Point(idiv(self.width, 2), idiv(self.height, 2)), dimage)

    def execute(self) -> None:
        self.output = Convolution.invoke(self.input, self.getKernel())
        self.output = ContrastStretch.invoke(self.output)

    @staticmethod
    def invoke(
        image: Image,
        wavelength: float,
        orientations: list[float],
        phaseOffset: float,
        aspectRatio: float,
        bandwidth: float,
        width: int,
        height: int,
    ) -> Image | None:
        try:
            return Gabor().preprocess(
                image, wavelength, orientations, phaseOffset, aspectRatio, bandwidth, width, height
            )
        except GlobalException:
            traceback.print_exc()
        return None

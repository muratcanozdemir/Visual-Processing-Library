"""Port of ``vpt.algorithms.linear.Canny``: classic 5-stage Canny edge
detection (Gaussian blur, Sobel gradient magnitude/direction, direction
quantised to 4 bins, non-maximum suppression along the gradient direction,
``HysteresisThreshold``).

Drops the Java's ``Display2D.invoke(gradient, "", false)`` mid-pipeline
Swing debug popup -- out-of-scope GUI with no effect on ``output``, same
call already dropped from ``mm/binary/geo.BReconstructionByErosion``.

**Preserved bug** (bytecode-confirmed, not a decompiler artefact): of the
four border-clearing loops at the end of the first pass, the fourth
(intended to clear the right edge, ``x == xdim - 1``) instead repeats
``setXYCDouble(0, ydim - 1, c, 0.0)`` on every iteration -- its loop
variable is never read in the body. The right image border is therefore
never zeroed; only top, bottom, and left are. Transcribed via the same
dead loop rather than "fixed" to clear the right edge."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ..histogram.ContrastStretch import ContrastStretch
from ..segmentation.HysteresisThreshold import HysteresisThreshold
from .Convolution import Convolution
from .Sobel import Sobel

_GAUSSIAN_KERNEL = [
    [2.0, 4.0, 5.0, 4.0, 2.0],
    [4.0, 9.0, 12.0, 9.0, 4.0],
    [5.0, 12.0, 15.0, 12.0, 5.0],
    [4.0, 9.0, 12.0, 9.0, 4.0],
    [2.0, 4.0, 5.0, 4.0, 2.0],
]

_PI = 3.141592653589793


class Canny(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.lowThreshold: float | None = None
        self.highThreshold: float | None = None
        self.inputFields = "input,lowThreshold,highThreshold"
        self.outputFields = "output"

    def execute(self) -> None:
        self.lowThreshold = max(0.0, min(self.lowThreshold, 1.0))
        self.highThreshold = max(0.0, min(self.highThreshold, 1.0))

        kernel = NonFlatSE(Point(2, 2), _GAUSSIAN_KERNEL)
        tmp = Convolution.invoke(self.input, kernel)

        gradient = Sobel.invoke(tmp, Sobel.GRAD, True)
        # Java: Display2D.invoke(gradient, "", false) -- dropped, see docstring.

        theta = Sobel.invoke(tmp, Sobel.THETA, False)

        self.output = DoubleImage(gradient, True)
        for c in range(theta.getCDim()):
            for x in range(1, theta.getXDim() - 1):
                for y in range(1, theta.getYDim() - 1):
                    angle = theta.getXYCDouble(x, y, c)
                    if angle < 0.0:
                        angle += _PI
                    if (angle >= 0.0 and angle < 0.3926990816987241) or (
                        angle >= 2.748893571891069 and angle <= _PI
                    ):
                        theta.setXYCDouble(x, y, c, 0.0)
                    elif angle >= 0.3926990816987241 and angle < 1.178097245096172:
                        theta.setXYCDouble(x, y, c, 0.25)
                    elif angle >= 1.178097245096172 and angle < 1.963495408493621:
                        theta.setXYCDouble(x, y, c, 0.5)
                    elif angle >= 1.963495408493621 and angle < 2.748893571891069:
                        theta.setXYCDouble(x, y, c, 0.75)

        for c in range(self.input.getCDim()):
            for x in range(1, self.input.getXDim() - 1):
                for y in range(1, self.input.getYDim() - 1):
                    angle = theta.getXYCDouble(x, y, c)
                    grad = gradient.getXYCDouble(x, y, c)
                    if angle == 0.0:
                        west = gradient.getXYCDouble(x - 1, y, c)
                        east = gradient.getXYCDouble(x + 1, y, c)
                        if grad < west or grad < east:
                            self.output.setXYCDouble(x, y, c, 0.0)

                    elif angle == 0.5:
                        north = gradient.getXYCDouble(x, y - 1, c)
                        south = gradient.getXYCDouble(x, y + 1, c)
                        if grad < north or grad < south:
                            self.output.setXYCDouble(x, y, c, 0.0)

                    elif angle == 0.25:
                        northEast = gradient.getXYCDouble(x + 1, y - 1, c)
                        southWest = gradient.getXYCDouble(x - 1, y + 1, c)
                        if grad < northEast or grad < southWest:
                            self.output.setXYCDouble(x, y, c, 0.0)

                    elif angle == 0.75:
                        northWest = gradient.getXYCDouble(x - 1, y - 1, c)
                        southEast = gradient.getXYCDouble(x + 1, y + 1, c)
                        if grad < northWest or grad < southEast:
                            self.output.setXYCDouble(x, y, c, 0.0)

                    else:
                        self.output.setXYCDouble(x, y, c, 0.0)

            for x in range(self.output.getXDim()):
                self.output.setXYCDouble(x, 0, c, 0.0)
            for x in range(self.output.getXDim()):
                self.output.setXYCDouble(x, self.output.getYDim() - 1, c, 0.0)
            for y in range(self.output.getYDim()):
                self.output.setXYCDouble(0, y, c, 0.0)
            for y in range(self.output.getYDim()):
                # PRESERVED BUG: `y` is never read here -- see docstring.
                self.output.setXYCDouble(0, self.output.getYDim() - 1, c, 0.0)

        self.output = ContrastStretch.invoke(self.output)
        self.output = HysteresisThreshold.invoke(self.output, self.lowThreshold, self.highThreshold)

    @staticmethod
    def invoke(image: Image, lowThreshold: float, highThreshold: float) -> Image | None:
        try:
            return Canny().preprocess(image, lowThreshold, highThreshold)
        except GlobalException:
            traceback.print_exc()
        return None

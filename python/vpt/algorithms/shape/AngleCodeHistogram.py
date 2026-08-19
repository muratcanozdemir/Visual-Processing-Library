"""Port of ``vpt.algorithms.shape.AngleCodeHistogram``: samples the
contour at roughly-evenly-spaced key points (walking the border, taking
every ``step``-th pixel), then histograms the turning angle at each
consecutive key-point triple.

``step = (int)totalBorderPixels / numberOfKeyPoints`` is Java ``int``
division -- routed through ``idiv``, which (like the other ``idiv`` call
sites in this port) raises on a zero divisor, matching Java's
``ArithmeticException`` if ``fractionOfPerimeter`` is small enough that
``numberOfKeyPoints`` rounds to ``0``."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv, imod, jdiv, jint
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..mm.binary.BInternGradient import BInternGradient


class AngleCodeHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.bins: int | None = None
        self.fractionOfPerimeter: float | None = None
        self.inputFields = "input, bins, fractionOfPerimeter"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3), False)

        totalBorderPixels = Tools.volume(borderImage, 0)

        numberOfKeyPoints = jint(self.fractionOfPerimeter * totalBorderPixels)
        step = idiv(jint(totalBorderPixels), numberOfKeyPoints)

        mask = BooleanImage(self.input, False)
        mask.fill(False)

        borderKeyPoints = []
        tmp = 0
        for x in range(xdim):
            for y in range(ydim):
                r = borderImage.getXYBoolean(x, y)
                if r:
                    m = mask.getXYBoolean(x, y)
                    if not m:
                        p = Point(x, y)
                        while True:
                            mask.setXYBoolean(p.x, p.y, True)
                            tmp = imod(tmp + 1, step)
                            if tmp == 0:
                                borderKeyPoints.append(Point(p.x, p.y))
                            if (
                                p.x + 1 < xdim
                                and borderImage.getXYBoolean(p.x + 1, p.y)
                                and not mask.getXYBoolean(p.x + 1, p.y)
                            ):
                                p.x += 1
                            elif (
                                p.y + 1 < ydim
                                and borderImage.getXYBoolean(p.x, p.y + 1)
                                and not mask.getXYBoolean(p.x, p.y + 1)
                            ):
                                p.y += 1
                            elif (
                                p.x - 1 >= 0
                                and borderImage.getXYBoolean(p.x - 1, p.y)
                                and not mask.getXYBoolean(p.x - 1, p.y)
                            ):
                                p.x -= 1
                            else:
                                if (
                                    p.y - 1 < 0
                                    or not borderImage.getXYBoolean(p.x, p.y - 1)
                                    or mask.getXYBoolean(p.x, p.y - 1)
                                ):
                                    break
                                p.y -= 1

        self.output = [0.0] * self.bins

        keyPointCount = len(borderKeyPoints)
        for i in range(keyPointCount):
            p1 = borderKeyPoints[i]
            p2 = borderKeyPoints[(i + 1) % keyPointCount]
            p3 = borderKeyPoints[(i + 2) % keyPointCount]

            angle = jint(Tools.getAngleABC(p1, p2, p3))

            angle += 180

            self.output[jint(angle / 361.0 * self.bins)] += 1.0
        for i in range(len(self.output)):
            self.output[i] = jdiv(self.output[i], keyPointCount)

    @staticmethod
    def invoke(img: Image, bins: int, fractionOfPerimeter: float) -> list[float] | None:
        try:
            return AngleCodeHistogram().preprocess(img, bins, fractionOfPerimeter)
        except GlobalException:
            traceback.print_exc()
        return None

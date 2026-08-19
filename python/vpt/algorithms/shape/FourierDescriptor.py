"""Port of ``vpt.algorithms.shape.FourierDescriptor``: walks the shape's
8-connected contour (via ``BInternGradient``) recording each point's
distance from the centroid, then returns the leading coefficients of that
1D signal's DFT magnitude spectrum (``Tools.dftMag1d`` -- a real, separate
1D DFT, not affected by the ``frequential`` package's broken 2D
transforms)."""

from __future__ import annotations

import sys
import traceback

from ..._javacompat import Point, jint
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..geometric.ImageCentroid import ImageCentroid
from ..mm.binary.BInternGradient import BInternGradient


class FourierDescriptor(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.coeffs: int | None = None
        self.inputFields = "input, coeffs"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        centroid = ImageCentroid.invoke(self.input)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        self.output = [0.0] * jint(Tools.volume(borderImage, 0))
        syc = 0

        mask = BooleanImage(self.input, False)
        mask.fill(False)
        for x in range(xdim):
            for y in range(ydim):
                r = borderImage.getXYBoolean(x, y)
                if r:
                    m = mask.getXYBoolean(x, y)
                    if not m:
                        p = Point(x, y)
                        while True:
                            mask.setXYBoolean(p.x, p.y, True)
                            self.output[syc] = Distance.EuclideanDistance(
                                p.x, p.y, centroid.x, centroid.y
                            )
                            syc += 1
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

        self.output = Tools.dftMag1d(self.output)
        if len(self.output) >= self.coeffs:
            tmp = [0.0] * self.coeffs
            for i in range(self.coeffs):
                tmp[i] = self.output[i]
            self.output = tmp
        else:
            self.output = [0.0] * self.coeffs
            print("Not enough border points", file=sys.stderr)

    @staticmethod
    def invoke(img: Image, coeffs: int) -> list[float] | None:
        try:
            return FourierDescriptor().preprocess(img, coeffs)
        except GlobalException:
            traceback.print_exc()
        return None

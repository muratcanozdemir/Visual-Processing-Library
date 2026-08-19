"""Port of ``vpt.algorithms.shape.BasicShapeStatistics``: walks the shape's
contour (same 8-connected walk as ``FourierDescriptor``) recording each
point's distance from the centroid, then reports ``[min, max, median,
variance]`` of that distance distribution."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.se import FlatSE
from ..geometric.ImageCentroid import ImageCentroid
from ..mm.binary.BInternGradient import BInternGradient


class BasicShapeStatistics(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        centroid = ImageCentroid.invoke(self.input)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        borders = []

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
                            borders.append(
                                Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)
                            )
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

        self.output = [0.0] * 4
        border2 = list(borders)
        border2.sort()
        self.output[0] = border2[0]
        self.output[1] = border2[len(border2) - 1]
        self.output[2] = border2[idiv(len(border2), 2)]

        mean = 0.0
        for i in range(len(border2)):
            mean += border2[i]
        mean /= len(border2)
        for i in range(len(border2)):
            self.output[3] += (border2[i] - mean) * (border2[i] - mean)
        self.output[3] /= len(border2)

    @staticmethod
    def invoke(img: Image) -> list[float] | None:
        try:
            return BasicShapeStatistics().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

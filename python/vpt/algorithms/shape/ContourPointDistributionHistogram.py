"""Port of ``vpt.algorithms.shape.ContourPointDistributionHistogram``: a
histogram of contour-point counts within growing concentric rings around
the centroid, normalised by total shape volume."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv, jint
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..geometric.ImageCentroid import ImageCentroid
from ..mm.binary.BInternGradient import BInternGradient


class ContourPointDistributionHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.slides: int | None = None
        self.inputFields = "input, slides"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        ydim = self.input.getYDim()
        xdim = self.input.getXDim()
        if cdim != 1:
            raise GlobalException("Sorry, only monochannel images for now...")

        self.output = [0.0] * self.slides

        centroid = ImageCentroid.invoke(self.input)
        self.input = BInternGradient.invoke(self.input, FlatSE.square(3))
        totalVolume = Tools.volume(self.input, 0)

        maxDist = 0.0
        for y in range(ydim):
            for x in range(xdim):
                if self.input.getXYBoolean(x, y):
                    dist = Distance.EuclideanDistance(centroid.x, centroid.y, x, y)
                    if dist > maxDist:
                        maxDist = dist

        slideWidth = jint(jdiv(maxDist, self.slides))
        for i in range(self.slides):
            for y in range(ydim):
                for x in range(xdim):
                    if (
                        Distance.EuclideanDistance(x, y, centroid.x, centroid.y)
                        < slideWidth * (i + 1)
                    ) and self.input.getXYBoolean(x, y):
                        self.output[i] += 1.0

        for i in range(len(self.output)):
            self.output[i] = jdiv(self.output[i], totalVolume)

    @staticmethod
    def invoke(img: Image, slides: int) -> list[float] | None:
        try:
            return ContourPointDistributionHistogram().preprocess(img, slides)
        except GlobalException:
            traceback.print_exc()
        return None

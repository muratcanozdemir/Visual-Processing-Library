"""Port of ``vpt.algorithms.shape.CircularWidthFactor2``: like
``CircularWidthFactor``, but each ring's output is the *marginal* volume
fraction (this ring's cumulative fraction minus the previous ring's),
rather than the cumulative fraction itself."""

from __future__ import annotations

import traceback

from ..._javacompat import idiv, jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.Tools import Tools
from ..geometric.ImageCentroid import ImageCentroid


class CircularWidthFactor2(Algorithm):
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
            raise GlobalException("Sorry, only grayscale images for now...")

        self.output = [0.0] * self.slides

        centroid = ImageCentroid.invoke(self.input)
        totalVolume = Tools.volume(self.input, 0)

        dist1 = min(centroid.x, xdim - 1 - centroid.x)
        dist2 = min(centroid.y, ydim - 1 - centroid.y)
        minDist = min(dist1, dist2)
        slideWidth = idiv(minDist, self.slides)
        for i in range(self.slides):
            vol = 0.0
            for y in range(ydim):
                for x in range(xdim):
                    if Distance.EuclideanDistance(x, y, centroid.x, centroid.y) < slideWidth * (
                        i + 1
                    ):
                        vol += self.input.getXYDouble(x, y)
            if i == 0:
                self.output[i] = jdiv(vol, totalVolume)
            else:
                self.output[i] = jdiv(vol, totalVolume) - self.output[i - 1]

    @staticmethod
    def invoke(img: Image, slides: int) -> list[float] | None:
        try:
            return CircularWidthFactor2().preprocess(img, slides)
        except GlobalException:
            traceback.print_exc()
        return None

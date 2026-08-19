"""Port of ``vpt.algorithms.shape.MeanDistanceToCentroid``: the intensity-
weighted mean Euclidean distance from the image centroid."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.Tools import Tools
from ..geometric.ImageCentroid import ImageCentroid


class MeanDistanceToCentroid(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only grayscale images for now...")
        ydim = self.input.getYDim()
        xdim = self.input.getXDim()

        centroid = ImageCentroid.invoke(self.input)

        self.output = 0.0
        for y in range(ydim):
            for x in range(xdim):
                self.output += self.input.getXYDouble(x, y) * Distance.EuclideanDistance(
                    x, y, centroid.x, centroid.y
                )

        self.output = jdiv(self.output, Tools.volume(self.input, 0))

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return MeanDistanceToCentroid().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

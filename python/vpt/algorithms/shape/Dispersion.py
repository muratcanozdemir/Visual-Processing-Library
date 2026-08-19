"""Port of ``vpt.algorithms.shape.Dispersion``: ratio of the farthest to
the nearest contour point from the centroid, ``max_dist / min_dist``
(``0.0`` if the nearest contour point is exactly at the centroid).

**Confirmed to hang forever**: calls ``mm/binary/geo.BFillHole.invoke``
directly on its (post-``KeepLargestCC``) input -- the exact already-
documented hang path (see ``BFillHole.py``'s docstring, and
``geometric.ConvexAreaRatio.py``'s docstring for this session's direct
JDK-21 confirmation of the same underlying mechanism). Transcribed
faithfully; not smoke-tested, there is nothing to run to completion."""

from __future__ import annotations

import math
import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ..geometric.ImageCentroid import ImageCentroid
from ..geometric.KeepLargestCC import KeepLargestCC
from ..mm.binary.BInternGradient import BInternGradient
from ..mm.binary.geo.BFillHole import BFillHole


class Dispersion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        self.input = KeepLargestCC.invoke(self.input)
        self.input = BFillHole.invoke(self.input)

        centroid = ImageCentroid.invoke(self.input)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        maximum = 0.0
        minimum = 1.7976931348623157e308
        for x in range(xdim):
            for y in range(ydim):
                if borderImage.getXYBoolean(x, y):
                    tmp = math.sqrt(
                        (x - centroid.x) * (x - centroid.x) + (y - centroid.y) * (y - centroid.y)
                    )
                    if tmp > maximum:
                        maximum = tmp
                    if tmp < minimum:
                        minimum = tmp

        if minimum != 0.0:
            self.output = maximum / minimum
        else:
            self.output = 0.0

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Dispersion().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

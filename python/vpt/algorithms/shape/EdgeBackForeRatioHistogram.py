"""Port of ``vpt.algorithms.shape.EdgeBackForeRatioHistogram``: for every
contour pixel, the fraction of a circular window around it that's
foreground, binned into a histogram.

**Confirmed to hang forever**: calls ``mm/binary/geo.BFillHole.invoke``
directly, the same already-documented hang path as ``shape.Dispersion``
-- see ``ConvexAreaRatio.py``'s docstring for the JDK-21 confirmation.
Transcribed faithfully; not smoke-tested."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv, jint, jround
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ..geometric.KeepLargestCC import KeepLargestCC
from ..mm.binary.BInternGradient import BInternGradient
from ..mm.binary.geo.BFillHole import BFillHole


class EdgeBackForeRatioHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.bins: int | None = None
        self.windowRadius: int | None = None
        self.inputFields = "input, bins, windowRadius"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only grayscale images for now...")
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        self.input = KeepLargestCC.invoke(self.input)
        self.input = BFillHole.invoke(self.input)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        self.output = [0.0] * self.bins

        mask = FlatSE.circle(self.windowRadius)
        coords = mask.getCoords()

        size = 0
        for x in range(xdim):
            for y in range(ydim):
                if borderImage.getXYBoolean(x, y):
                    foreground = 0.0
                    for i in range(len(coords)):
                        _x = x - coords[i].x
                        _y = y - coords[i].y
                        if _x >= 0 and _y >= 0 and _x < xdim and _y < ydim:
                            if self.input.getXYBoolean(_x, _y):
                                foreground += 1.0

                    ratio = jdiv(foreground, len(coords))
                    size += 1
                    self.output[jint(jround(ratio * (self.bins - 1)))] += 1.0

        for i in range(len(self.output)):
            self.output[i] = jdiv(self.output[i], size)

    @staticmethod
    def invoke(img: Image, bins: int, windowRadius: int) -> list[float] | None:
        try:
            return EdgeBackForeRatioHistogram().preprocess(img, bins, windowRadius)
        except GlobalException:
            traceback.print_exc()
        return None

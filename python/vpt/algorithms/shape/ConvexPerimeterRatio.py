"""Port of ``vpt.algorithms.shape.ConvexPerimeterRatio``: the convex hull's
perimeter divided by the shape's own -- ``convexPerimeter / perimeter``.

**Confirmed to hang forever**, same as ``ConvexAreaRatio`` and
``mm/binary/geo.BFillHole`` -- see ``ConvexAreaRatio.py``'s docstring for
the confirmed-against-JDK-21 mechanism. This class also calls
``ConvexHull.invoke(input, filled=True)`` directly. Transcribed faithfully;
not smoke-tested, there is nothing to run to completion."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..geometric.ConvexHull import ConvexHull
from ..mm.binary.BInternGradient import BInternGradient


class ConvexPerimeterRatio(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        borders = BInternGradient.invoke(self.input, FlatSE.square(3), False)
        perimeter = Tools.volume(borders, 0)

        self.input = ConvexHull.invoke(self.input, True)

        borders = BInternGradient.invoke(self.input, FlatSE.square(3), False)
        convexPerimeter = Tools.volume(borders, 0)

        self.output = jdiv(convexPerimeter, perimeter)

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return ConvexPerimeterRatio().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

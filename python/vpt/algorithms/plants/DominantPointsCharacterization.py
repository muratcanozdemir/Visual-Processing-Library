"""Port of ``vpt.algorithms.plants.DominantPointsCharacterization``: a
pairwise-distance histogram over the shape's dominant contour points
(``shape.DominantPointsDetection`` at a fixed ``epsilon=20.0``), normalised
by the maximum observed distance and bin count. The bin-index division
(``d / maxDist * bins``) is routed through ``jdiv``/``jint`` rather than
plain ``/``/``int()``: if every dominant point coincided, ``maxDist`` would
be exactly ``0.0``, and Java's total float division (`NaN`/`Infinity`, not
an exception) still needs a defined narrowing-cast result. The Java's
``main`` (a hardcoded-local-path `PlantCLEF` dataset demo through
`io.Load`/`conversion.RGB2Gray`) is not ported, same treatment as every
other dataset-path demo `main` in this codebase."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv, jint
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ..shape.DominantPointsDetection import DominantPointsDetection


class DominantPointsCharacterization(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.bins: int | None = None
        self.inputFields = "input, bins"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        domPoints = DominantPointsDetection.invoke(self.input, 20.0)

        distances: list[float] = []

        maxDist = 0.0
        for i in range(len(domPoints)):
            for j in range(len(domPoints)):
                if i != j:
                    dist = Distance.EuclideanDistance(domPoints[i], domPoints[j])
                    distances.append(dist)
                    if dist > maxDist:
                        maxDist = dist

        self.output = [0.0] * self.bins
        for d in distances:
            bin_ = jint(jdiv(d, maxDist) * self.bins)
            if bin_ == self.bins:
                bin_ -= 1
            self.output[bin_] += 1.0

        for i in range(len(self.output)):
            self.output[i] /= len(distances)

    @staticmethod
    def invoke(img: Image, bins: int) -> list[float] | None:
        try:
            return DominantPointsCharacterization().preprocess(img, bins)
        except GlobalException:
            traceback.print_exc()
        return None

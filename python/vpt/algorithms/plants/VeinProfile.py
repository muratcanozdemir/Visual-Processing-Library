"""Port of ``vpt.algorithms.plants.VeinProfile``: isolates the leaf's
internal vein network (opening/closing by reconstruction to smooth the
blade, then an internal gradient with a cross SE to pick out thin
structures, masked to exclude the outer border) and reports, for a
growing sequence of ``GPathOpening`` minimum-path-lengths, what fraction
of the vein network's volume survives -- a profile of how much of the
signal is "long, thin paths" at each length threshold.

``Tools.volume(tmp, 0) / originalVol`` is routed through ``jdiv``:
``originalVol`` is legitimately `0.0` for a leaf silhouette whose internal
gradient finds no vein signal at all (a nearly-uniform blade), and Java's
total double division doesn't raise where Python's `/` on two floats
would."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..arithmetic.Minimum import Minimum
from ..mm.binary.BGradient import BGradient
from ..mm.gray.geo.GClosingByReconstruction import GClosingByReconstruction
from ..mm.gray.geo.GOpeningByReconstruction import GOpeningByReconstruction
from ..mm.gray.GInternGradient import GInternGradient
from ..mm.gray.path.GPathOpening import GPathOpening
from ..point.Inversion import Inversion
from ..segmentation.Threshold import Threshold


class VeinProfile(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self.initialLength: int | None = None
        self.step: int | None = None
        self.inputFields = "input, size, initialLength, step"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        mask = Threshold.invoke(self.input, 0.00392156862745098)

        outerBorders = BGradient.invoke(mask, FlatSE.square(7))
        outerBorders = Inversion.invoke(outerBorders)

        self.input = GOpeningByReconstruction.invoke(self.input, FlatSE.square(3))
        self.input = GClosingByReconstruction.invoke(self.input, FlatSE.square(3))

        veins = GInternGradient.invoke(self.input, FlatSE.cross(3))

        veins = Minimum.invoke(veins, outerBorders)
        originalVol = Tools.volume(veins, 0)

        self.output = [0.0] * self.size
        for i in range(self.size):
            tmp = GPathOpening.invoke(veins, self.initialLength + self.step * i)
            self.output[i] = jdiv(Tools.volume(tmp, 0), originalVol)

    @staticmethod
    def invoke(img: Image, size: int, initialLen: int, step: int) -> list[float] | None:
        try:
            return VeinProfile().preprocess(img, size, initialLen, step)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.plants.PetioleRemoval``: finds the top-hat
components of the input (a 31-pixel horizontal line SE, meant to pick out
the thin petiole stalk against the broader leaf blade), picks the largest
(and, if a close second-largest competes and sits lower in the image,
that one instead), erases it from a copy of the input, and keeps only the
largest remaining connected component. Deliberately a no-op (``output``
stays an unmodified copy of ``input``) if the largest component is too
small or neither of the two largest candidates' centroids sit low enough
in the image to plausibly be a petiole.

``max1 / max2`` is Java `int` division, routed through ``idiv`` -- both
for its zero-divisor `ArithmeticException` (`max2` stays `0` if there's
only one connected component, since the "exclude `index1`" loop that
would set it never executes) and for structural parity, though here it
happens not to change the observable result: the only use is `< 3.0`, and
`floor(x) < 3 == x < 3` for non-negative `x`, so the truncation is inert
for this particular comparison (unlike `WidthFactor`/
`relativeAreaDiff`'s int-division bugs, where the truncated *value*
itself is used, not just compared against a whole-number threshold)."""

from __future__ import annotations

import traceback

from ..._javacompat import idiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..geometric.KeepLargestCC import KeepLargestCC
from ..mm.binary.BTopHat import BTopHat
from ..shape.ConnectedComponents import ConnectedComponents


class PetioleRemoval(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        self.output = self.input.newInstance(True)

        self.input = BTopHat.invoke(self.input, FlatSE.hLine(31))

        CCs = ConnectedComponents.invoke(self.input)

        maxDims = [0] * len(CCs)
        i = 0
        for CC in CCs:
            leftX = 2147483647
            rightX = 0

            topY = 2147483647
            bottomY = 0
            for p in CC:
                if p.x < leftX:
                    leftX = p.x
                if p.x > rightX:
                    rightX = p.x
                if p.y > bottomY:
                    bottomY = p.y
                if p.y < topY:
                    topY = p.y

            maxDims[i] = max(rightX - leftX, bottomY - topY)
            i += 1

        max1 = 0
        index1 = 0
        for j in range(len(maxDims)):
            if maxDims[j] > max1:
                max1 = maxDims[j]
                index1 = j

        max2 = 0
        index2 = 0
        for j in range(len(maxDims)):
            if j != index1:
                if maxDims[j] > max2:
                    max2 = maxDims[j]
                    index2 = j

        if max1 < 20 or (
            Tools.centroid(CCs[index1]).y < 400 and Tools.centroid(CCs[index2]).y < 400
        ):
            return

        indexMax = index1
        if idiv(max1, max2) < 3.0:
            centroid1 = Tools.centroid(CCs[index1])
            centroid2 = Tools.centroid(CCs[index2])
            if centroid2.y > centroid1.y:
                indexMax = index2

        for p in CCs[indexMax]:
            self.output.setXYBoolean(p.x, p.y, False)

        self.output = KeepLargestCC.invoke(self.output)

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return PetioleRemoval().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

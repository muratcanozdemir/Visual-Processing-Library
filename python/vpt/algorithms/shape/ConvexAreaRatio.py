"""Port of ``vpt.algorithms.shape.ConvexAreaRatio``: how much bigger the
convex hull's area is than the shape's own, as a fraction of the hull's
area -- ``(convexArea - area) / convexArea``.

**Confirmed to hang forever**, same as ``mm/binary/geo.BFillHole``, and now
confirmed against the real JDK 21 (`geometric.README`'s note on this was
previously "expected but unconfirmed -- no oracle harness calls
`ConvexHull` with `filled=true`"; this session ran it directly and it
never returned, spewing `Display2D` debug output until killed). This class
calls ``ConvexHull.invoke(input, filled=True)`` directly -- the exact hang
path -- so calling ``ConvexAreaRatio.invoke`` will hang identically in this
port too. Transcribed faithfully (not routed to `filled=False`); not
smoke-tested for the same reason `BFillHole` isn't -- there is nothing to
run to completion."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..geometric.ConvexHull import ConvexHull


class ConvexAreaRatio(Algorithm):
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

        area = Tools.volume(self.input, 0)

        self.input = ConvexHull.invoke(self.input, True)

        convexArea = Tools.volume(self.input, 0)

        self.output = jdiv(convexArea - area, convexArea)

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return ConvexAreaRatio().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

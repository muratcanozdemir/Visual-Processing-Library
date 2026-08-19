"""Port of ``vpt.algorithms.geometric.ConvexHull``.

Convex hull outline of a mono-channel image's foreground pixels, drawn as a
closed polygon of Bresenham-rasterised edges (``Tools.drawLine``); optionally
filled by dilating the outline by a 3x3 square and filling enclosed holes.

PRESERVED DEFECT (inherited from ``BooleanImage``): ``numberOfPoints`` is
computed via ``Tools.volume``, which sums ``getXYCDouble`` -- and
``BooleanImage.getDouble`` is inverted (1.0 for a *false* pixel, see that
class's docstring), so this actually counts BACKGROUND pixels, not border
pixels. For the normal case (border is a small minority of the image), that
oversizes the ``border`` array, and only the first ``cnt`` slots get written
with real points -- the rest stay ``None``. This is exactly why the
reimplemented ``_quickhull.quickhull`` (like its Java counterpart,
``src/org/concord/swing/QuickHull.java``) explicitly filters out ``None``:
it was written with this quirk in mind. Reproduced as found; do not "fix"
the volume count -- if the border ever exceeds the background in size, the
``border[cnt] = ...`` write below raises ``IndexError``, matching the Java's
uncaught ``ArrayIndexOutOfBoundsException`` in that (pathological) case.

HAZARD: ``filled=True`` hangs. It routes through ``BFillHole`` on a
``BooleanImage``, which (see that module's docstring) does not terminate --
an apparent original-library defect, not introduced by this port.
``oracle/Recovered.java`` only exercises ``filled=false``. Do not call
``invoke(..., True)`` without a JDK-verified fix or a hang guard.
"""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..mm.binary.BDilation import BDilation
from ..mm.binary.BInternGradient import BInternGradient
from ..mm.binary.geo.BFillHole import BFillHole
from ._quickhull import quickhull


class ConvexHull(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: BooleanImage | None = None
        self.input: Image | None = None
        self.filled: bool | None = None
        self.inputFields = "input, filled"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 1:
            raise GlobalException("Only mono-channel images for now...")

        self.output = BooleanImage(self.input, False)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        numberOfPoints = Tools.volume(borderImage, 0)
        border: list[Point | None] = [None] * int(numberOfPoints)

        # Vectorized scan-and-collect: the flat storage's index formula
        # (cdim == 1 here) is ``x + xdim * y``, so reshaping to
        # ``[y][x]`` and transposing to ``[x][y]`` before ``np.nonzero``
        # yields the true (x, y) coordinates in the same x-outer/y-inner
        # order the nested loop scans in. ``border`` stays exactly as
        # oversized as before (PRESERVED DEFECT above) -- only the first
        # ``cnt`` slots are written, the rest stay ``None`` -- and a
        # foreground count that overflows it still raises the same
        # ``IndexError`` the scalar ``border[cnt] = ...`` write would, via
        # the same plain list-index assignment below.
        bimg = borderImage.getBooleanArray().reshape(ydim, xdim).T
        xs, ys = np.nonzero(bimg)
        for i in range(xs.shape[0]):
            border[i] = Point(int(xs[i]), int(ys[i]))

        hull = quickhull(border)

        hullSize = len(hull)
        for i in range(hullSize):
            p1 = hull[i]
            p2 = hull[(i + 1) % hullSize]
            line = Tools.drawLine(p1, p2)
            for pt in line:
                self.output.setXYBoolean(pt.x, pt.y, True)

        if self.filled:
            self.output = BDilation.invoke(self.output, FlatSE.square(3))
            self.output = BFillHole.invoke(self.output)

    @staticmethod
    def invoke(image: Image, filled: bool) -> Image | None:
        try:
            return ConvexHull().preprocess(image, filled)
        except GlobalException:
            traceback.print_exc()
        return None

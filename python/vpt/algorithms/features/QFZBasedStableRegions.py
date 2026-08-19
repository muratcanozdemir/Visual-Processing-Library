"""Port of ``vpt.algorithms.features.QFZBasedStableRegions``: MSER-style
stable-region detection built on the already-ported quasi-flat-zone
machinery. Grows ``GrayQFZSoille`` at 15 increasing ``(alpha, omega)``
scales (``10*i`` for ``i in 1..15``), partitions each scale into
components via ``GrayConnectedComponents2``, then for every pixel finds
how many *consecutive* scales (walking from the coarsest down) keep that
pixel's component size within a relative-difference threshold of its
value at the coarsest of those scales -- a region "stable" across enough
successive scales, above a minimum area, is kept.

**Preserved bug, bytecode-confirmed** (`javap`: `isub`/`idiv`/`i2d` in
that order): ``relativeAreaDiff(a1, a2)`` computes ``(a1 - a2) / a1`` in
Java `int` arithmetic, widening to `double` only *after* the truncating
division -- so for the realistic case (two component-size ints whose
difference is smaller in magnitude than `a1`, exactly the "areas are
similar" case this method exists to detect), the result truncates to
`0.0` regardless of the true relative difference, making the `< 0.2`
stability check almost always true. Routed through `idiv` to match
(``a1`` is always >= 1 in practice -- a pixel's own component always has
itself as a member -- so the zero-divisor `ArithmeticException` `idiv`
also reproduces is not expected to fire, but is preserved for parity).

The Java's ``main`` (a hardcoded-local-path `UCMerced LandUse` dataset
demo through `io.Load`/`Display2D`) is not ported, same treatment as
every other dataset-path demo `main` in this codebase."""

from __future__ import annotations

import sys
import time
import traceback

from ..._javacompat import idiv
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ..flatzones.gray.GrayQFZSoille import GrayQFZSoille
from ..shape.GrayConnectedComponents2 import GrayConnectedComponents2

_SCALE_COUNT = 15
_MIN_AREA = 250
_SUCCESSIVE = 9
_AREA_DIFF = 0.2


class QFZBasedStableRegions(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.cdim = self.input.getCDim()
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if self.cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        start = time.time()

        scales = [
            GrayQFZSoille.invoke(self.input, i * 10, i * 10)
            for i in range(1, _SCALE_COUNT + 1)
        ]

        zones = [GrayConnectedComponents2.invoke(s) for s in scales]

        bestQfz = [[None] * self.ydim for _ in range(self.xdim)]
        for y in range(self.ydim):
            for x in range(self.xdim):
                sizes = [0] * _SCALE_COUNT
                for s in range(_SCALE_COUNT - 1, -1, -1):
                    t = s
                    while t >= 0 and self._relativeAreaDiff(
                        len(zones[s][x][y]), len(zones[t][x][y])
                    ) < _AREA_DIFF:
                        t -= 1
                    sizes[s] = s - t + 1

                m = sizes[0]
                maxIndex = 0
                for s in range(1, len(sizes)):
                    if sizes[s] > m:
                        m = sizes[s]
                        maxIndex = s

                if m >= _SUCCESSIVE and len(zones[maxIndex][x][y]) >= _MIN_AREA:
                    bestQfz[x][y] = zones[maxIndex][x][y]
                else:
                    bestQfz[x][y] = None

        self.output = BooleanImage(self.input, False)
        self.output.fill(False)
        for x in range(self.xdim):
            for y in range(self.ydim):
                zone = bestQfz[x][y]
                if zone is not None and not self.output.getXYBoolean(
                    zone[0].x, zone[0].y
                ):
                    for p in zone:
                        self.output.setXYBoolean(p.x, p.y, True)

        print(int((time.time() - start) * 1000), file=sys.stderr)

    @staticmethod
    def _relativeAreaDiff(a1: int, a2: int) -> float:
        return float(idiv(a1 - a2, a1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return QFZBasedStableRegions().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

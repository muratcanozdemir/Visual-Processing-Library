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

import numpy as np

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

        # area[s, x, y] = size of pixel (x, y)'s component at scale s.
        area = np.array(
            [[[len(zone) for zone in column] for column in z] for z in zones], dtype=np.int64
        )

        # sizes[s] = 1 + how many consecutive scales t = s, s-1, ..., 0 keep
        # relativeAreaDiff(area[s], area[t]) < 0.2 -- the Java while-loop's
        # `s - t + 1`, computed for every pixel at once.
        sizes = np.empty_like(area)
        for s in range(_SCALE_COUNT - 1, -1, -1):
            alive = np.ones(area.shape[1:], dtype=bool)
            run = np.zeros(area.shape[1:], dtype=np.int64)
            for t in range(s, -1, -1):
                alive &= self._relativeAreaDiffArr(area[s], area[t]) < _AREA_DIFF
                run += alive
            sizes[s] = run + 1

        # First index of the maximum, like the Java's strict-`>` scan.
        maxIndex = np.argmax(sizes, axis=0)
        m = np.take_along_axis(sizes, maxIndex[None], axis=0)[0]
        maxArea = np.take_along_axis(area, maxIndex[None], axis=0)[0]
        keep = (m >= _SUCCESSIVE) & (maxArea >= _MIN_AREA)

        # Paint the kept zones in the Java's x-outer/y-inner order, skipping a
        # zone whose first point is already painted (preserved: zones from
        # different scales can overlap, so this can skip unpainted points).
        # Each zone object is shared by all its pixels, so after its first
        # visit it always hits the skip -- visit each one once.
        painted = np.zeros((self.ydim, self.xdim), dtype=bool)
        seen: set[int] = set()
        for x, y in zip(*np.nonzero(keep)):
            zone = zones[maxIndex[x, y]][x][y]
            if id(zone) in seen:
                continue
            seen.add(id(zone))
            if not painted[zone[0].y, zone[0].x]:
                for pt in zone:
                    painted[pt.y, pt.x] = True

        self.output = BooleanImage(self.input, False)
        self.output.setBooleanArray(painted.reshape(-1))

        print(int((time.time() - start) * 1000), file=sys.stderr)

    @staticmethod
    def _relativeAreaDiff(a1: int, a2: int) -> float:
        return float(idiv(a1 - a2, a1))

    @staticmethod
    def _relativeAreaDiffArr(a1: np.ndarray, a2: np.ndarray) -> np.ndarray:
        """Elementwise :meth:`_relativeAreaDiff`: Java ``int`` division
        (truncating toward zero), widened to double afterwards."""
        if (a1 == 0).any():
            idiv(0, 0)  # Java's ArithmeticException, as the scalar path raises
        num = a1 - a2
        q = np.abs(num) // np.abs(a1)
        return np.where((num < 0) != (a1 < 0), -q, q).astype(np.float64)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return QFZBasedStableRegions().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

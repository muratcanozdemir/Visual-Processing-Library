"""Port of ``vpt.algorithms.mm.gray.GLambdaLeveling``: repeatedly relaxes
``marker`` toward ``input`` -- wherever ``marker`` is more than ``lambda``
below ``input``, pull it up to ``min(input, local max of marker)``; wherever
it's more than ``lambda`` above, push it down to ``max(input, local min of
marker)`` -- until the marker stops changing (the lambda-leveling fixpoint).

``getMin``/``getMax`` use exclude-and-fallback edge handling (an SE
coordinate that falls out of bounds is simply skipped, not clamped into
range like ``GRankFilter``/``mm/multi``'s flat operators), falling back to
the centre pixel's own value if every neighbour was out of bounds -- the
same style as the other ``mm/gray`` flat operators (``GDilation``/
``GErosion``)."""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.Equal import Equal


class GLambdaLeveling(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.lambda_: int | None = None
        self.marker: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,marker,se,lambda_"
        self.outputFields = "output"
        self.cdim = 0
        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.cdim = self.input.getCDim()
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        if self.lambda_ < 0 or self.lambda_ > 254:
            raise GlobalException("The lambda value must be \\in [0,254]")

        while True:
            tmp = self.marker
            self.marker = self._level(self.marker, self.input)
            if Equal.invoke(self.marker, tmp):
                break
        self.output = self.marker

    def _level(self, marker: Image, input_: Image) -> Image:
        """Vectorized equivalent of the per-pixel scan below. ``tmp`` starts
        as a deep copy of ``marker`` (``newInstance(True)``), matching the
        scalar loop leaving a pixel untouched when neither branch fires.
        The two branches (``i > m + lambda``, ``m > i + lambda``) can never
        both hold for ``lambda >= 0`` -- summing the inequalities would
        require ``0 > 2*lambda`` -- so the scalar ``if``/``elif`` and the
        unordered ``np.where`` selection below are equivalent; no ordering
        is needed to reproduce the ``elif`` short-circuit.

        The local max/min themselves reuse the ``GDilation``/``GErosion``
        shift-and-reduce technique (see ``GDilation._executeGeneral``'s
        docstring), just over byte arrays with ``0``/``255`` sentinels (the
        scalar loop's own bounds) in place of ``+-DOUBLE_MAX_VALUE`` -- byte
        values can never be NaN, so the ``candidate > dst``-style compare
        here is purely a sentinel-collision-preserving convenience, not a
        NaN-safety requirement like the double-array case."""
        m = marker.getByteArray().reshape(self.ydim, self.xdim, self.cdim)
        i = input_.getByteArray().reshape(self.ydim, self.xdim, self.cdim)
        lam = self.lambda_
        coords = self.se.getCoords()

        localMax = np.zeros((self.ydim, self.xdim, self.cdim), dtype=np.int64)
        localMin = np.full((self.ydim, self.xdim, self.cdim), 255, dtype=np.int64)
        for p in coords:
            px, py = p.x, p.y
            dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
            dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
            if dy1 <= dy0 or dx1 <= dx0:
                continue
            sy0, sy1 = dy0 - py, dy1 - py
            sx0, sx1 = dx0 - px, dx1 - px
            candidate = m[sy0:sy1, sx0:sx1, :]

            dstMax = localMax[dy0:dy1, dx0:dx1, :]
            localMax[dy0:dy1, dx0:dx1, :] = np.where(candidate > dstMax, candidate, dstMax)

            dstMin = localMin[dy0:dy1, dx0:dx1, :]
            localMin[dy0:dy1, dx0:dx1, :] = np.where(candidate < dstMin, candidate, dstMin)

        localMax = np.where(localMax > 0, localMax, m)
        localMin = np.where(localMin < 255, localMin, m)

        riseMask = i > m + lam
        fallMask = m > i + lam

        result = m.copy()
        result = np.where(riseMask, np.minimum(i, localMax), result)
        result = np.where(fallMask, np.maximum(i, localMin), result)

        tmp = marker.newInstance(True)
        tmp.setByteArray(result.reshape(-1))
        return tmp

    def _getMin(self, img: Image, x: int, y: int, c: int, coords) -> int:
        minimum = 255
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x >= 0 and _y >= 0 and _x < self.xdim and _y < self.ydim:
                pixel = img.getXYCByte(_x, _y, c)
                minimum = min(minimum, pixel)
        return minimum if minimum < 255 else img.getXYCByte(x, y, c)

    def _getMax(self, img: Image, x: int, y: int, c: int, coords) -> int:
        maximum = 0
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x >= 0 and _y >= 0 and _x < self.xdim and _y < self.ydim:
                pixel = img.getXYCByte(_x, _y, c)
                maximum = max(maximum, pixel)
        return maximum if maximum > 0 else img.getXYCByte(x, y, c)

    @staticmethod
    def invoke(image: Image, marker: Image, se: FlatSE, lambda_: int) -> Image | None:
        try:
            return GLambdaLeveling().preprocess(image, marker, se, lambda_)
        except GlobalException:
            traceback.print_exc()
        return None

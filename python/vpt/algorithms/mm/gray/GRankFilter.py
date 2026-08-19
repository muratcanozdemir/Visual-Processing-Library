"""Port of ``vpt.algorithms.mm.gray.GRankFilter``: replace each pixel with
the ``rank``-th smallest value (0 = erosion, ``len(coords)-1`` = dilation)
among its SE neighbourhood, using border-*clamp* edge handling (every SE
coordinate is clamped into bounds and always contributes a sample, like
``mm/multi``'s flat operators -- not exclude-and-fallback like ``GDilation``/
``GErosion``). The BOX fast path separates the rank into an independent
horizontal rank (``rank % se.xdim``) and vertical rank (``rank // se.xdim``)
applied as two 1-D passes -- only exactly correct when the 2-D rank
decomposes that way, which is a property of the specific ``rank``/SE-shape
combination, not of rank filters in general; transcribed as found."""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE


class GRankFilter(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.rank: int | None = None
        self.inputFields = "input,se,rank"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        self.cdim = self.output.getCDim()

        if self.rank < 0 or self.rank > len(self.se.getCoords()) - 1:
            raise GlobalException("Invalid rank value")

        if not self.se.BOX:
            result = self._vectorizedRank(self.input.getDoubleArray(), self.se.getCoords(), self.rank)
            self.output.setDoubleArray(result.reshape(-1))
        else:
            hline = self.se.getHLine()
            vline = self.se.getVLine()

            hrank = self.rank % self.se.getXDim()
            vrank = self.rank // self.se.getXDim()

            tmp = self._vectorizedRank(self.input.getDoubleArray(), hline.getCoords(), hrank)
            result = self._vectorizedRank(tmp, vline.getCoords(), vrank)
            self.output.setDoubleArray(result.reshape(-1))

    def _vectorizedRank(self, img: np.ndarray, coords, rank: int) -> np.ndarray:
        """Vectorized equivalent of the per-pixel ``_getRank`` gather+sort
        below. The rank-th order statistic of a multiset doesn't depend on
        the order values were collected in, and for finite doubles ``<`` is
        a total order, so ``np.sort`` and Python's ``list.sort`` agree and
        this is bit-exact to the scalar gather+sort.

        Correction: a neighbourhood containing NaN is a real divergence, but
        not one introduced by vectorizing. Python's ``list.sort()`` uses
        ``<``, under which NaN compares false in every direction -- not a
        total order -- so its placement is a Timsort implementation detail,
        not a specified rule. ``np.sort`` instead deterministically sorts
        NaN to the end, which happens to *agree* with ``Double.compare``'s
        documented rule that NaN is considered greater than every other
        double, including ``+Infinity`` -- i.e. this vectorized version is
        arguably closer to the Java oracle's ordering for a NaN-containing
        neighbourhood than the scalar ``_getRank`` reference it's checked
        against. Neither this file's golden test nor its scalar-vs-
        vectorized parity script exercises NaN input; flagged here rather
        than "fixed" since there's no single correct target to fix it
        toward without also revisiting the scalar port.

        Border handling is clamp (not exclude-and-fallback like
        ``GDilation``), so every offset always contributes a sample."""
        img = img.reshape(self.ydim, self.xdim, self.cdim)
        X, Y = np.meshgrid(np.arange(self.xdim), np.arange(self.ydim))

        stack = np.empty((len(coords), self.ydim, self.xdim, self.cdim), dtype=np.float64)
        for i, p in enumerate(coords):
            cx = np.clip(X - p.x, 0, self.xdim - 1)
            cy = np.clip(Y - p.y, 0, self.ydim - 1)
            stack[i] = img[cy, cx, :]

        stack.sort(axis=0)
        return stack[rank]

    def _getRank(self, img: Image, x: int, y: int, c: int, coords, rank: int) -> float:
        pixels = []
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x < 0:
                _x = 0
            if _y < 0:
                _y = 0
            if _x >= self.xdim:
                _x = self.xdim - 1
            if _y >= self.ydim:
                _y = self.ydim - 1
            pixels.append(img.getXYCDouble(_x, _y, c))
        pixels.sort()
        return pixels[rank]

    @staticmethod
    def invoke(image: Image, se: FlatSE, rank: int) -> Image | None:
        try:
            return GRankFilter().preprocess(image, se, rank)
        except GlobalException:
            traceback.print_exc()
        return None

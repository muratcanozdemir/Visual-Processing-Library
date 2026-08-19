"""Port of ``vpt.algorithms.mm.gray.floating.GFloatingErosion``:
``GFloatingDilation`` with the SE reflected and ``min``/bilinear-read in
place of ``max``.

``FloatingFlatSE.reflection()`` returns ``self`` rather than a reflected
copy -- a preserved Java oddity documented on that class -- so this call is
a no-op, kept only for structural fidelity with the Java source.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....._javacompat import DOUBLE_MAX_VALUE, jround
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FloatingFlatSE
from .....util.Tools import Tools


class GFloatingErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FloatingFlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        self.cdim = self.output.getCDim()

        self.se = self.se.reflection()
        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMin``/
        ``pgetXYCDouble`` scan below -- see
        ``GFloatingDilation._executeGeneral``'s docstring for the full
        reasoning (min in place of max, ``+DOUBLE_MAX_VALUE`` sentinel,
        ``candidate < result`` comparison in place of ``candidate >
        result``, otherwise identical)."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)
        result = np.full((self.ydim, self.xdim, self.cdim), DOUBLE_MAX_VALUE, dtype=np.float64)

        xIdx = np.arange(self.xdim, dtype=np.float64)
        yIdx = np.arange(self.ydim, dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for p in self.se.getCoords():
                px, py = p.x, p.y
                xs = xIdx - px
                ys = yIdx - py
                valid = (
                    (ys >= 0.0) & (ys < self.ydim)
                )[:, None] & ((xs >= 0.0) & (xs < self.xdim))[None, :]

                exactX = Tools.cmpr(abs(jround(-px) - (-px)), 0.0) == 0
                exactY = Tools.cmpr(abs(jround(-py) - (-py)), 0.0) == 0

                if exactX and exactY:
                    cx = np.clip(np.floor(xs).astype(np.int64), 0, self.xdim - 1)
                    cy = np.clip(np.floor(ys).astype(np.int64), 0, self.ydim - 1)
                    V = img[cy[:, None], cx[None, :], :]
                else:
                    ll = np.floor(xs)
                    k = np.floor(ys)
                    a = xs - ll
                    b = ys - k
                    cx0 = np.clip(ll.astype(np.int64), 0, self.xdim - 1)
                    cx1 = np.clip(ll.astype(np.int64) + 1, 0, self.xdim - 1)
                    cy0 = np.clip(k.astype(np.int64), 0, self.ydim - 1)
                    cy1 = np.clip(k.astype(np.int64) + 1, 0, self.ydim - 1)
                    p1 = img[cy0[:, None], cx0[None, :], :]
                    p2 = img[cy0[:, None], cx1[None, :], :]
                    p3 = img[cy1[:, None], cx0[None, :], :]
                    p4 = img[cy1[:, None], cx1[None, :], :]
                    aB = a[None, :, None]
                    bB = b[:, None, None]
                    V = (
                        (1.0 - aB) * (1.0 - bB) * p1
                        + aB * (1.0 - bB) * p2
                        + (1.0 - aB) * bB * p3
                        + aB * bB * p4
                    )

                candidate = np.where(valid[:, :, None], V, DOUBLE_MAX_VALUE)
                result = np.where(candidate < result, candidate, result)

            result = np.where(result < DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for x in range(self.xdim):
            for y in range(self.ydim):
                for c in range(self.cdim):
                    self.output.setXYCDouble(
                        x, y, c, self._getMin(self.input, x, y, c, self.se.getCoords())
                    )

    def _getMin(self, img: Image, x: int, y: int, c: int, coords) -> float:
        minimum = DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0.0 <= _x < self.xdim and 0.0 <= _y < self.ydim:
                v = img.pgetXYCDouble(_x, _y, c)
                if v < minimum:
                    minimum = v
        return minimum if minimum < DOUBLE_MAX_VALUE else img.getXYCDouble(x, y, c)

    @staticmethod
    def invoke(image: Image, se: FloatingFlatSE) -> Image | None:
        try:
            return GFloatingErosion().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

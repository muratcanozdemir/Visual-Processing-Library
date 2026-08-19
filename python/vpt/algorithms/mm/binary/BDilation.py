"""Port of ``vpt.algorithms.mm.binary.BDilation``.

Binary dilation by a flat structuring element: the output pixel is true if
any in-bounds SE-covered neighbour is true. A box SE takes the separable
fast path (horizontal pass into a temp image, then a vertical pass), like
``GDilation``, but binary dilation needs no padding sentinel -- out-of-bounds
neighbours are simply skipped, and a pixel with no in-bounds neighbour at all
falls back to its own value (``getMax``'s ``touched`` flag).
"""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE


class BDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
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

        if not self.se.BOX:
            result = self._vectorizedMax(self.input.getBooleanArray(), self.se.getCoords())
            self.output.setBooleanArray(result.reshape(-1))
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()

        # Despite the name, this "BOX" pass is not the true O(n) van Herk
        # algorithm GDilation's BOX path uses -- it's still a per-pixel
        # `_getMax` neighbourhood scan, just against the smaller 1-D
        # hline/vline coordinate sets. Vectorizes with the same technique.
        tmp = self._vectorizedMax(self.input.getBooleanArray(), hline.getCoords())
        result = self._vectorizedMax(tmp, vline.getCoords())
        self.output.setBooleanArray(result.reshape(-1))

    def _vectorizedMax(self, imgBool: np.ndarray, coords) -> np.ndarray:
        """Vectorized equivalent of the per-pixel ``_getMax`` scan below:
        OR is commutative/associative (and the scalar loop's early
        ``break`` on the first true neighbour doesn't change the *result*,
        only how soon it stops looking), so reducing over SE offsets in
        any order is equivalent. ``touched`` -- whether *any* offset had an
        in-bounds neighbour, independent of that neighbour's value -- is
        pure geometry, so it's tracked as the union of each offset's valid
        region rather than the image content."""
        imgBool = imgBool.reshape(self.ydim, self.xdim, self.cdim)
        result = np.zeros((self.ydim, self.xdim, self.cdim), dtype=bool)
        touched = np.zeros((self.ydim, self.xdim), dtype=bool)

        for p in coords:
            px, py = p.x, p.y
            dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
            dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
            if dy1 <= dy0 or dx1 <= dx0:
                continue
            sy0, sy1 = dy0 - py, dy1 - py
            sx0, sx1 = dx0 - px, dx1 - px
            candidate = imgBool[sy0:sy1, sx0:sx1, :]
            result[dy0:dy1, dx0:dx1, :] |= candidate
            touched[dy0:dy1, dx0:dx1] = True

        return np.where(touched[:, :, None], result, imgBool)

    def _getMax(self, img: Image, x: int, y: int, c: int, coords) -> bool:
        maximum = False
        touched = False
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                touched = True
                if img.getXYCBoolean(_x, _y, c):
                    maximum = True
                    break
        return maximum if touched else img.getXYCBoolean(x, y, c)

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return BDilation().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

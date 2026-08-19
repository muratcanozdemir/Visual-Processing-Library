"""Port of ``vpt.algorithms.mm.binary.BErosion``.

Binary erosion: the output pixel is true only if every SE-covered neighbour
is true. The SE is reflected first, as in ``GErosion``. ``flag`` controls how
an out-of-bounds neighbour is treated: ``True`` (the default via the 2-arg
``invoke``) treats it as matching, so erosion doesn't shrink the border;
``False`` treats it as failing, which erodes the border away.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE


class BErosion(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.flag: bool | None = None
        self.inputFields = "input, se, flag"
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

        if not self.se.BOX:
            result = self._vectorizedMin(self.input.getBooleanArray(), self.se.getCoords())
            self.output.setBooleanArray(result.reshape(-1))
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()

        # As with BDilation's BOX path, this isn't the true O(n) van Herk
        # algorithm -- still a per-pixel `_getMin` scan against the smaller
        # hline/vline coordinate sets. Vectorizes the same way.
        tmp = self._vectorizedMin(self.input.getBooleanArray(), hline.getCoords())
        result = self._vectorizedMin(tmp, vline.getCoords())
        self.output.setBooleanArray(result.reshape(-1))

    def _vectorizedMin(self, imgBool: np.ndarray, coords) -> np.ndarray:
        """Vectorized equivalent of the per-pixel ``_getMin`` scan below:
        AND is commutative/associative (the scalar loop's early ``return
        False`` doesn't change the *result*), so reducing over SE offsets
        in any order is equivalent. Per offset, in-bounds positions must
        pass with the shifted image's actual value; out-of-bounds positions
        must pass with ``self.flag``'s fixed truth value -- filling the
        whole array with ``flag`` first and then overwriting the in-bounds
        region reproduces both halves of the scalar branch in one array."""
        imgBool = imgBool.reshape(self.ydim, self.xdim, self.cdim)
        result = np.ones((self.ydim, self.xdim, self.cdim), dtype=bool)

        for p in coords:
            px, py = p.x, p.y
            passesThisOffset = np.full(
                (self.ydim, self.xdim, self.cdim), bool(self.flag), dtype=bool
            )
            dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
            dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
            if dy1 > dy0 and dx1 > dx0:
                sy0, sy1 = dy0 - py, dy1 - py
                sx0, sx1 = dx0 - px, dx1 - px
                passesThisOffset[dy0:dy1, dx0:dx1, :] = imgBool[sy0:sy1, sx0:sx1, :]
            result &= passesThisOffset

        return result

    def _getMin(self, img: Image, x: int, y: int, c: int, coords) -> bool:
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x < 0 or _y < 0 or _x >= self.xdim or _y >= self.ydim:
                if not self.flag:
                    return False
            else:
                if not img.getXYCBoolean(_x, _y, c):
                    return False
        return True

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, se: FlatSE, flag: bool = True) -> Image | None:
        try:
            return BErosion().preprocess(image, se, flag)
        except GlobalException:
            traceback.print_exc()
        return None

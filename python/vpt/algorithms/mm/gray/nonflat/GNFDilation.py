"""Port of ``vpt.algorithms.mm.gray.nonflat.GNFDilation``.

Grayscale dilation by a non-flat (real-valued) structuring element: like
``GDilation``, but each neighbour contributes ``pixel + kernelWeight`` rather
than just ``pixel``, and there is no BOX fast path (that optimisation is
specific to flat SEs). Out-of-bounds neighbours are excluded rather than
padded; a pixel with no in-bounds SE coverage falls back to its own value.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....._javacompat import DOUBLE_MAX_VALUE
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import NonFlatSE


class GNFDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: NonFlatSE | None = None
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

        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getMax`` scan below --
        see ``GDilation._executeGeneral``'s docstring for the shift-and-
        reduce reasoning, including why the running maximum is updated with
        ``candidate > result`` + ``np.where`` rather than ``np.maximum``
        (NaN must be silently skipped, not propagated, to match the scalar
        ``v > maximum`` loop) -- same ``-DOUBLE_MAX_VALUE`` sentinel/own-
        pixel-fallback quirk applies. The one difference from a flat SE:
        each neighbour contributes ``pixel + kernelWeight`` rather than
        just ``pixel``. ``kernelWeight`` only depends on the SE offset
        ``p``, not the pixel being visited, so it's looked up once per
        offset (not once per pixel) and added to the whole shifted array
        before the per-offset max comparison -- the same single addition
        the scalar loop performs, just batched."""
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)
        result = np.full((self.ydim, self.xdim, self.cdim), -DOUBLE_MAX_VALUE, dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for p in self.se.getCoords():
                px, py = p.x, p.y
                weight = self.se.getXYDouble(px + self.se.center.x, py + self.se.center.y)
                dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
                dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
                if dy1 <= dy0 or dx1 <= dx0:
                    continue
                sy0, sy1 = dy0 - py, dy1 - py
                sx0, sx1 = dx0 - px, dx1 - px
                candidate = img[sy0:sy1, sx0:sx1, :] + weight
                dst = result[dy0:dy1, dx0:dx1, :]
                result[dy0:dy1, dx0:dx1, :] = np.where(candidate > dst, candidate, dst)

            result = np.where(result > -DOUBLE_MAX_VALUE, result, img)
        self.output.setDoubleArray(result.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for x in range(self.xdim):
            for y in range(self.ydim):
                for c in range(self.cdim):
                    self.output.setXYCDouble(
                        x, y, c, self._getMax(self.input, x, y, c, self.se.getCoords())
                    )

    def _getMax(self, img: Image, x: int, y: int, c: int, coords) -> float:
        maximum = -DOUBLE_MAX_VALUE
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                v = img.getXYCDouble(_x, _y, c) + self.se.getXYDouble(
                    p.x + self.se.center.x, p.y + self.se.center.y
                )
                if v > maximum:
                    maximum = v
        return maximum if maximum > -DOUBLE_MAX_VALUE else img.getXYCDouble(x, y, c)

    @staticmethod
    def invoke(image: Image, se: NonFlatSE) -> Image | None:
        try:
            return GNFDilation().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.multi.MDilation``.

Multichannel (vectorial) dilation: like ``GDilation``, but each pixel is a
full channel vector and "greatest" is decided by a caller-supplied
``Ordering`` rather than a scalar comparison (there is no canonical total
order on colour vectors). The BOX fast path is the same hline/vline
separable pass as ``GDilation``.

Edge handling differs from ``GDilation``: out-of-bounds SE offsets are
*clamped* to the nearest border pixel here (``_x = 0`` / ``_x = xdim - 1``
etc.) rather than excluded from the max -- border-replicate, not
"pretend the SE is smaller at the edge". Every coordinate always
contributes a sample, so ``pixelArray`` always fills completely.

``pixelArray`` is allocated once per pass and reused across every pixel's
``_getMax`` call (mirroring the Java field reused as a scratch buffer,
rather than a fresh list every call) -- safe here because every coordinate
is always written (via clamping), so nothing from a previous pixel ever
leaks through unwritten.
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import Ordering
from ....util.se import FlatSE


class MDilation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.or_: Ordering | None = None
        self.inputFields = "input,se,or_"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self._pixelArray: list | None = None

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()

        if not self.se.BOX:
            self._pixelArray = [None] * len(self.se.getCoords())
            for x in range(self.xdim):
                for y in range(self.ydim):
                    self.output.setVXYDouble(x, y, self._getMax(self.input, x, y, self.se.getCoords()))
            return

        hline = self.se.getHLine()
        vline = self.se.getVLine()
        tmp = self.input.newInstance(False)

        self._pixelArray = [None] * len(hline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                tmp.setVXYDouble(x, y, self._getMax(self.input, x, y, hline.getCoords()))

        self._pixelArray = [None] * len(vline.getCoords())
        for x in range(self.xdim):
            for y in range(self.ydim):
                self.output.setVXYDouble(x, y, self._getMax(tmp, x, y, vline.getCoords()))

    def _getMax(self, img: Image, x: int, y: int, coords):
        index = 0
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
            self._pixelArray[index] = img.getVXYDouble(_x, _y)
            index += 1
        return self.or_.max(self._pixelArray) if index > 0 else img.getVXYDouble(x, y)

    @staticmethod
    def invoke(image: Image, se: FlatSE, or_: Ordering) -> Image | None:
        try:
            return MDilation().preprocess(image, se, or_)
        except GlobalException:
            traceback.print_exc()
        return None

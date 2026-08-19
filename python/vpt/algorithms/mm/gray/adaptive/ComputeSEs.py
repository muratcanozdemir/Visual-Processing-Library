"""Port of ``vpt.algorithms.mm.gray.adaptive.ComputeSEs``.

Computes a per-pixel "amoeba" adaptive structuring element: starting from
each pixel ``r``, a weighted flood fill (``tmpImg`` holding each reached
pixel's propagated cost) grows outward through 8-connected neighbours,
adding ``1 + 0.25*|intensity difference|`` per hop, and stops extending a
branch once its accumulated cost exceeds ``size/2``. Every pixel reached
this way becomes a ``True`` entry in that pixel's ``FlatSE``, so the SE
shape adapts to local image content instead of being a fixed square/disk.

``output`` is a full ``xdim`` x ``ydim`` grid of ``FlatSE``s -- one per
pixel -- consumed by ``ADilation``/``AErosion`` as ``se[x][y]``.

Not ported: the Java ``main`` (a Swing/``Display2D`` demo, out of scope for
this port -- see ``CLAUDE.md``/``python/README.md`` on the GUI being
excluded).
"""

from __future__ import annotations

import traceback

from ....._javacompat import Point, idiv
from .....Algorithm import Algorithm
from .....ByteImage import ByteImage
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from .....util.Tools import Tools


class ComputeSEs(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[list[FlatSE]] | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self.inputFields = "input,size"
        self.outputFields = "output"

        self._stack: list[Point] = []
        self._list: list[Point] = []
        self._tmpImg: Image | None = None
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        self.cdim = self.input.getCDim()

        self._tmpImg = ByteImage(self.input, False)
        self._tmpImg.fill(0)

        if self.cdim != 1:
            raise GlobalException("Cannot handle non grayscale images yet...")

        self.output = [[None] * self.ydim for _ in range(self.xdim)]
        for y in range(self.ydim):
            for x in range(self.xdim):
                self.output[x][y] = self._computeAmoeba(Point(x, y))

    def _computeAmoeba(self, r: Point) -> FlatSE:
        adaptedSE = FlatSE.square(self.size * 2 + 1)
        adaptedSE.fill(False)

        self._stack.clear()
        self._stack.append(r)

        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y

            x_p = self.size - (x - r.x)
            y_p = self.size - (y - r.y)

            adaptedSE.setXYBoolean(x_p, y_p, True)
            if self._tmpImg.getXYByte(x, y) == 0:
                self._tmpImg.setXYByte(x, y, 1)
                self._list.append(tmp)

            p = self.input.getXYByte(x, y)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    q = self.input.getXYByte(_x, _y)

                    L = int(self._tmpImg.getXYByte(x, y) + 1 + 0.25 * abs(p - q))
                    if self._tmpImg.getXYByte(_x, _y) <= 0 or self._tmpImg.getXYByte(_x, _y) > L:
                        if L <= idiv(self.size, 2):
                            self._tmpImg.setXYByte(_x, _y, L)
                            t = Point(_x, _y)
                            self._stack.append(t)
                            self._list.append(t)

        for t in self._list:
            self._tmpImg.setXYByte(t.x, t.y, 0)
        self._list.clear()

        return adaptedSE

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, size: int) -> list[list[FlatSE]] | None:
        try:
            return ComputeSEs().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

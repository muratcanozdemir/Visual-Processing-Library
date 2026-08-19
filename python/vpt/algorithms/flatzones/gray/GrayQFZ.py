"""Port of ``vpt.algorithms.flatzones.gray.GrayQFZ``.

Quasi-flat zones: labels a grayscale image into connected regions where
every adjacent pixel pair differs by at most ``alpha`` and the region's
overall (max - min) range stays at most ``omega``. ``createQCC`` grows a
candidate zone via stack-based flood fill (another of the "collapsed nested
loop" decompiler-damage sites the root README flags, alongside
``ConnectedComponents``) and aborts (returning ``-1``) the moment either
bound would be violated; ``execute`` then retries with a smaller ``alpha``
until a zone actually forms, so every pixel ends up in *some* zone even if
that means shrinking alpha below the caller's requested value locally.
``localRanges`` records the alpha that was actually used to grow each
already-labelled pixel's zone, so a newly-growing zone can check it's not
about to swallow a neighbour that was already grown more permissively.
"""

from __future__ import annotations

import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....BooleanImage import BooleanImage
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class GrayQFZ(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: IntegerImage | None = None
        self.alpha: int = 0
        self.omega: int = 0
        self.inputFields = "input,alpha,omega"
        self.outputFields = "output"

        self._list: list = []
        self._list2: list = []
        self._stack: list = []
        self.xdim = 0
        self.ydim = 0
        self._label = 1
        self._max = 0
        self._min = 0
        self._diff = 0
        self._temp: BooleanImage | None = None
        self._localRanges: ByteImage | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        if self.input.getCDim() != 1:
            raise GlobalException("This implementation is only for grayscale images.")
        if self.alpha < 0 or self.alpha > 255 or self.omega < 0 or self.omega > 255:
            raise GlobalException("Arguments out of range.")

        self.output = IntegerImage(self.xdim, self.ydim, 1)
        self.output.fill(0)

        self._temp = BooleanImage(self.xdim, self.ydim, 1)
        self._temp.fill(False)

        self._localRanges = ByteImage(self.input, False)
        self._localRanges.fill(255)

        self._list = []
        self._list2 = []
        self._stack = []
        self._label = 1

        for x in range(self.xdim):
            for y in range(self.ydim):
                if self.output.getXYInt(x, y) <= 0:
                    self._max = 0
                    self._min = 255

                    t = Point(x, y)

                    if self._createQCC(t, self.alpha) > 0:
                        for s in self._list:
                            self._localRanges.setXYByte(s.x, s.y, self.alpha)
                    else:
                        tmpAlpha = self.alpha
                        while True:
                            self._max = 0
                            self._min = 255
                            for p in self._list:
                                self.output.setXYInt(p.x, p.y, 0)
                            self._list.clear()
                            tmpAlpha -= 1
                            if self._createQCC(t, tmpAlpha) > 0:
                                break
                        for s in self._list:
                            self._localRanges.setXYByte(s.x, s.y, tmpAlpha)

                    self._list.clear()

                    self._label += 1

    def _createQCC(self, r, alpha2: int) -> int:
        self._stack.clear()
        self._stack.append(r)
        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y

            p = self.input.getXYByte(x, y)
            if self._max < p:
                self._max = p
            if self._min > p:
                self._min = p
            self._diff = self._max - self._min
            if self.omega < self._diff:
                for t in self._list2:
                    self._temp.setXYBoolean(t.x, t.y, False)
                self._list2.clear()

                return -1

            self.output.setXYInt(x, y, self._label)

            self._list.append(tmp)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    q = self.input.getXYByte(_x, _y)
                    if self.output.getXYInt(_x, _y) > 0:
                        localR = self._localRanges.getXYByte(_x, _y)
                        if abs(p - q) <= alpha2 and localR <= alpha2:
                            for t in self._list2:
                                self._temp.setXYBoolean(t.x, t.y, False)
                            self._list2.clear()

                            return -1
                    elif not self._temp.getXYBoolean(_x, _y):
                        self._diff = abs(p - q)
                        if self._diff <= alpha2:
                            t = Point(_x, _y)
                            self._stack.append(t)

                            self._temp.setXYBoolean(t.x, t.y, True)
                            self._list2.append(t)

        for t in self._list2:
            self._temp.setXYBoolean(t.x, t.y, False)
        self._list2.clear()

        return 1

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(img: Image, alpha: int, omega: int) -> Image | None:
        try:
            return GrayQFZ().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

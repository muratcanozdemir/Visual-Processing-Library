"""Port of ``vpt.algorithms.flatzones.multi.QFZMultiMarginal``: the
channel-count-agnostic generalisation of
``flatzones.color.ColorQFZRGBMarginal`` -- works over any ``cdim``, not
just 3, and (unlike ``ColorQFZSoille``'s neighbouring package) the
per-channel range array is used correctly here."""

from __future__ import annotations

import sys
import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....BooleanImage import BooleanImage
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class QFZMultiMarginal(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: IntegerImage | None = None
        self.alpha: int = 0
        self.omega: int = 0
        self.inputFields = "input,alpha,omega"
        self.outputFields = "output"

        self._list: list[Point] = []
        self._list2: list[Point] = []
        self._stack: list[Point] = []
        self.xdim = 0
        self.ydim = 0
        self._label = 1
        self._max: list[int] = []
        self._min: list[int] = []
        self._diff: list[int] = []
        self._temp: BooleanImage | None = None
        self._localRanges: ByteImage | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if self.alpha < 0 or self.alpha > 254 or self.omega < 0 or self.omega > 254:
            raise GlobalException("Arguments out of range.")

        self.output = IntegerImage(self.xdim, self.ydim, 1)
        self.output.fill(0)

        self._temp = BooleanImage(self.xdim, self.ydim, 1)
        self._temp.fill(False)

        self._localRanges = ByteImage(self.xdim, self.ydim, 1)
        self._localRanges.fill(255)

        cdim = self.input.getCDim()
        self._max = [0] * cdim
        self._min = [0] * cdim
        self._diff = [0] * cdim

        for x in range(self.xdim):
            print(f"Sutun {x}", file=sys.stderr)
            for y in range(self.ydim):
                if self.output.getXYInt(x, y) <= 0:
                    self._resetMaxMin()

                    t = Point(x, y)

                    if self._createQCC(t, self.alpha) > 0:
                        for s in self._list:
                            self._localRanges.setXYByte(s.x, s.y, self.alpha)
                    else:
                        tmpAlpha = self.alpha
                        while True:
                            self._resetMaxMin()
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

        print(f"Total number of quasi flat zones: {self._label - 1}", file=sys.stderr)

    def _resetMaxMin(self) -> None:
        for i in range(len(self._max)):
            self._max[i] = 0
            self._min[i] = 255

    def _createQCC(self, r: Point, alpha2: int) -> int:
        self._stack.clear()
        self._stack.append(r)
        while self._stack:
            tmp = self._stack.pop()
            x = tmp.x
            y = tmp.y

            p = self.input.getVXYByte(x, y)
            for i in range(len(self._max)):
                self._max[i] = p[i] if self._max[i] < p[i] else self._max[i]
                self._min[i] = p[i] if self._min[i] > p[i] else self._min[i]
                self._diff[i] = abs(self._max[i] - self._min[i])

            tmpFlag = True
            for i in range(len(self._diff)):
                if self.omega < self._diff[i]:
                    tmpFlag = False

            if not tmpFlag:
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
                    q = self.input.getVXYByte(_x, _y)
                    for j in range(len(self._diff)):
                        self._diff[j] = abs(p[j] - q[j])

                    if self.output.getXYInt(_x, _y) > 0:
                        localR = self._localRanges.getXYByte(_x, _y)

                        tmpFlag2 = True
                        for k in range(len(self._diff)):
                            if self._diff[k] > alpha2:
                                tmpFlag2 = False

                        if tmpFlag2 and localR <= alpha2:
                            for t in self._list2:
                                self._temp.setXYBoolean(t.x, t.y, False)
                            self._list2.clear()

                            return -1
                    elif not self._temp.getXYBoolean(_x, _y):
                        tmpFlag3 = True
                        for k in range(len(self._diff)):
                            if self._diff[k] > alpha2:
                                tmpFlag3 = False

                        if tmpFlag3:
                            t = Point(_x, _y)
                            self._stack.append(t)

                            self._temp.setXYBoolean(t.x, t.y, True)
                            self._list2.append(t)

        for t in self._list2:
            self._temp.setXYBoolean(t.x, t.y, False)
        self._list2.clear()

        return 1

    @staticmethod
    def invoke(img: Image, alpha: int, omega: int) -> Image | None:
        try:
            return QFZMultiMarginal().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

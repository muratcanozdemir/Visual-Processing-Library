"""Port of ``vpt.algorithms.flatzones.color.ColorQFZRGBMarginal``: the RGB
analogue of ``flatzones.gray.GrayQFZ``, but "marginal" -- each of the
three channels is checked independently against the same scalar
``alpha``/``omega`` (all three must individually satisfy the bound), not a
single vectorial distance the way ``ColorQFZRGB`` uses an
``AlgebraicOrdering`` for."""

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


class ColorQFZRGBMarginal(Algorithm):
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
        self._max = [0, 0, 0]
        self._min = [0, 0, 0]
        self._diff = [0, 0, 0]
        self._temp: BooleanImage | None = None
        self._localRanges: ByteImage | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if self.input.getCDim() != 3:
            raise GlobalException("This implementation is only for color images.")
        if self.alpha < 0 or self.alpha > 254 or self.omega < 0 or self.omega > 254:
            raise GlobalException("Arguments out of range.")

        self.output = IntegerImage(self.xdim, self.ydim, 1)
        self.output.fill(0)

        self._temp = BooleanImage(self.xdim, self.ydim, 1)
        self._temp.fill(False)

        self._localRanges = ByteImage(self.xdim, self.ydim, 1)
        self._localRanges.fill(255)

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
        self._max[0] = 0
        self._max[1] = 0
        self._max[2] = 0

        self._min[0] = 255
        self._min[1] = 255
        self._min[2] = 255

    def _createQCC(self, r: Point, alpha2: int) -> int:
        self._stack.clear()
        self._stack.append(r)
        while self._stack:
            tmp = self._stack.pop()
            x = tmp.x
            y = tmp.y

            p = self.input.getVXYByte(x, y)

            self._max[0] = p[0] if self._max[0] < p[0] else self._max[0]
            self._max[1] = p[1] if self._max[1] < p[1] else self._max[1]
            self._max[2] = p[2] if self._max[2] < p[2] else self._max[2]

            self._min[0] = p[0] if self._min[0] > p[0] else self._min[0]
            self._min[1] = p[1] if self._min[1] > p[1] else self._min[1]
            self._min[2] = p[2] if self._min[2] > p[2] else self._min[2]

            self._diff[0] = abs(self._max[0] - self._min[0])
            self._diff[1] = abs(self._max[1] - self._min[1])
            self._diff[2] = abs(self._max[2] - self._min[2])
            if self.omega < self._diff[0] or self.omega < self._diff[1] or self.omega < self._diff[2]:
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

                    self._diff[0] = abs(p[0] - q[0])
                    self._diff[1] = abs(p[1] - q[1])
                    self._diff[2] = abs(p[2] - q[2])
                    if self.output.getXYInt(_x, _y) > 0:
                        localR = self._localRanges.getXYByte(_x, _y)
                        if (
                            self._diff[0] <= alpha2
                            and self._diff[1] <= alpha2
                            and self._diff[2] <= alpha2
                            and localR <= alpha2
                        ):
                            for t in self._list2:
                                self._temp.setXYBoolean(t.x, t.y, False)
                            self._list2.clear()

                            return -1
                    elif not self._temp.getXYBoolean(_x, _y):
                        if (
                            self._diff[0] <= alpha2
                            and self._diff[1] <= alpha2
                            and self._diff[2] <= alpha2
                        ):
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
            return ColorQFZRGBMarginal().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

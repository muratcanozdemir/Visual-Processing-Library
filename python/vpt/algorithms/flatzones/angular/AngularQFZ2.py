"""Port of ``vpt.algorithms.flatzones.angular.AngularQFZ2``: the same
grow/shrink-alpha-and-retry structure as ``AngularQFZ``, but the per-pixel
maximum-hue-distance check is now a genuine exhaustive O(n^2) scan over
every pair of points already in the growing zone (``list``) instead of
``AngularQFZ``'s 360-bucket nearest-occupied-neighbour approximation --
this is the "exact but slower" sibling. Two more differences from
``AngularQFZ``, confirmed against the Java source: the retry step is
``-0.0001`` (ten times finer than ``AngularQFZ``'s ``-0.001``), and
``angle1``/``angle2`` are dead fields (always reset to ``-1.0``, never
read) kept for structural parity."""

from __future__ import annotations

import sys
import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....BooleanImage import BooleanImage
from ....DoubleImage import DoubleImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class AngularQFZ2(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: IntegerImage | None = None
        self.alpha: float | None = None
        self.omega: float | None = None
        self.inputFields = "input,alpha,omega"
        self.outputFields = "output"

        self._list: list[Point] = []
        self._list2: list[Point] = []
        self._stack: list[Point] = []
        self.xdim = 0
        self.ydim = 0
        self._label = 1
        self._angle1 = 0.0
        self._angle2 = 0.0
        self._temp: BooleanImage | None = None
        self._localRanges: DoubleImage | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if self.input.getCDim() != 1:
            raise GlobalException(
                "This implementation is only for monochannel angular data images."
            )
        if self.alpha <= 0.0 or self.alpha >= 1.0 or self.omega <= 0.0 or self.omega >= 1.0:
            raise GlobalException("Arguments out of range.")

        self.output = IntegerImage(self.xdim, self.ydim, 1)
        self.output.fill(0)

        self._temp = BooleanImage(self.xdim, self.ydim, 1)
        self._temp.fill(False)

        self._localRanges = DoubleImage(self.input, False)
        self._localRanges.fill(1.0)

        print("Computing angular qfz...", file=sys.stderr)
        for x in range(self.xdim - 1, -1, -1):
            for y in range(self.ydim - 1, -1, -1):
                if self.output.getXYInt(x, y) <= 0:
                    t = Point(x, y)

                    self._angle1 = self._angle2 = -1.0

                    if self._createQCC(t, self.alpha) > 0:
                        for s in self._list:
                            self._localRanges.setXYDouble(s.x, s.y, self.alpha)
                    else:
                        tmpAlpha = self.alpha
                        while True:
                            for p in self._list:
                                self.output.setXYInt(p.x, p.y, 0)
                            self._angle1 = self._angle2 = -1.0

                            self._list.clear()

                            tmpAlpha -= 0.0001
                            if self._createQCC(t, tmpAlpha) > 0:
                                break
                        for s in self._list:
                            self._localRanges.setXYDouble(s.x, s.y, tmpAlpha)

                    self._list.clear()

                    self._label += 1

        print(f"Total number of quasi flat zones: {self._label - 1}", file=sys.stderr)

    def _createQCC(self, r: Point, alpha2: float) -> int:
        self._stack.clear()
        self._stack.append(r)
        self._temp.setXYBoolean(r.x, r.y, True)
        self._list2.append(r)

        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y

            p = self.input.getXYDouble(x, y)

            self._list.append(tmp)

            maxDist = 0.0
            for c in self._list:
                for d in self._list:
                    tmpDist = Tools.hueDistance(
                        self.input.getXYDouble(c.x, c.y), self.input.getXYDouble(d.x, d.y)
                    )
                    if tmpDist > maxDist:
                        maxDist = tmpDist

            if maxDist > self.omega:
                for t in self._list2:
                    self._temp.setXYBoolean(t.x, t.y, False)
                self._list2.clear()

                return -1

            self.output.setXYInt(x, y, self._label)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    q = self.input.getXYDouble(_x, _y)
                    if self.output.getXYInt(_x, _y) > 0:
                        localR = self._localRanges.getXYDouble(_x, _y)
                        if Tools.hueDistance(p, q) <= alpha2 and localR <= alpha2:
                            for t in self._list2:
                                self._temp.setXYBoolean(t.x, t.y, False)
                            self._list2.clear()

                            return -1
                    elif not self._temp.getXYBoolean(_x, _y):
                        if Tools.hueDistance(p, q) <= alpha2:
                            t = Point(_x, _y)
                            self._stack.append(t)

                            self._temp.setXYBoolean(t.x, t.y, True)
                            self._list2.append(t)

        for t in self._list2:
            self._temp.setXYBoolean(t.x, t.y, False)
        self._list2.clear()

        return 1

    @staticmethod
    def invoke(img: Image, alpha: float, omega: float) -> Image | None:
        try:
            return AngularQFZ2().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

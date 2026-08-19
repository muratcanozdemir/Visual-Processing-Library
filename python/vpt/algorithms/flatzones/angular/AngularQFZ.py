"""Port of ``vpt.algorithms.flatzones.angular.AngularQFZ``: the circular
(hue-like, ``[0, 1)``-valued) analogue of ``flatzones.gray.GrayQFZ`` --
same grow-then-shrink-alpha-and-retry structure, but neighbour/zone
distance is ``Tools.hueDistance`` (circular, wraps at 1.0) instead of a
plain byte difference, and the outer pixel scan runs **in reverse**
(``xdim-1`` down to ``0``, ``ydim-1`` down to ``0``) unlike ``GrayQFZ``'s
forward scan -- confirmed against the Java source, not a transcription
slip.

The per-zone maximum-distance check doesn't scan every already-accepted
zone member's hue against the candidate: it keeps a 360-bucket histogram
(``angles[k]`` = every hue seen so far whose ``floor(hue*360)`` is ``k``)
and, for a candidate hue ``p``, only checks the hues in the *nearest
occupied bucket* on each side of ``p``'s antipodal angle (``(index +
180) % 360``, scanning outward until a non-empty bucket is found in each
direction) -- not an exhaustive max over the whole zone. Ported exactly as
found; this looks like a deliberate (if approximate) way to bound the
check's cost rather than a bug, but it means ``maxDist`` is not
necessarily the *true* maximum hue distance within the growing zone."""

from __future__ import annotations

import math
import sys
import traceback

from ...._javacompat import Point, jint
from ....Algorithm import Algorithm
from ....BooleanImage import BooleanImage
from ....DoubleImage import DoubleImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class AngularQFZ(Algorithm):
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
        self._angles: list[list[float]] = [[] for _ in range(360)]
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

        for k in range(360):
            self._angles[k].clear()

        for x in range(self.xdim - 1, -1, -1):
            print(f"Sutun {x}", file=sys.stderr)
            for y in range(self.ydim - 1, -1, -1):
                if self.output.getXYInt(x, y) <= 0:
                    t = Point(x, y)

                    for k in range(360):
                        self._angles[k].clear()

                    if self._createQCC(t, self.alpha) > 0:
                        for s in self._list:
                            self._localRanges.setXYDouble(s.x, s.y, self.alpha)
                    else:
                        tmpAlpha = self.alpha
                        while True:
                            for p in self._list:
                                self.output.setXYInt(p.x, p.y, 0)
                            for k in range(360):
                                self._angles[k].clear()
                            self._list.clear()

                            tmpAlpha -= 0.001
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

        maxDist = 0.0
        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y

            p = self.input.getXYDouble(x, y)
            self._list.append(tmp)

            index = jint(math.floor(p * 360.0))
            antiIndex = (index + 180) % 360

            self._angles[index].append(p)

            k = antiIndex
            while not self._angles[k]:
                k = (k + 1) % 360
            for h in self._angles[k]:
                tmpDist = Tools.hueDistance(h, p)
                if tmpDist > maxDist:
                    maxDist = tmpDist

            k = antiIndex - 1
            while True:
                if k < 0:
                    k += 360
                if self._angles[k]:
                    for h in self._angles[k]:
                        tmpDist = Tools.hueDistance(h, p)
                        if tmpDist > maxDist:
                            maxDist = tmpDist
                    break
                k -= 1

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
            return AngularQFZ().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

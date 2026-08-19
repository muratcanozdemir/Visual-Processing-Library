"""Port of ``vpt.algorithms.flatzones.angular.AngularQFZ5``: the same
grow/shrink-alpha-and-retry structure as ``AngularQFZ``, but each of the 360
hue buckets is a ``SortedList`` (ascending by hue value) instead of a plain
list, so the nearest-occupied-bucket-on-each-side search can binary-search
*within* a bucket for the local minimum of ``hueDistance(bucket value, p)``
rather than scanning every value in it. ``contains`` (also binary search)
dedupes a hue before inserting it into its bucket, so a bucket never holds
the same hue value twice.

The bucket-local search's final ``else`` branch (debug prints to stderr,
no state update) is dead code: given ``Tools.cmpr``'s antisymmetry
(``cmpr(a, b) == -cmpr(b, a)``), the three ``if``/``elif``/``elif`` branches
above it are already an exhaustive partition, so it can never be reached --
confirmed by hand, not just assumed, since an unreachable ``else`` whose
condition happened to be reachable would otherwise spin the surrounding
``do``/``while`` forever (``first``/``last`` are never updated in it).
Ported verbatim, including the two forward/backward bucket-scan blocks,
which are independent copies in the Java (not calls to a shared helper) --
kept duplicated here for the same reason."""

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
from ....util.SortedList import SortedList
from ....util.Tools import Tools


def _doubleComparator(a: float, b: float) -> int:
    if a < b:
        return -1
    if a > b:
        return 1
    return 0


class AngularQFZ5(Algorithm):
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
        self._angles: list[SortedList] = [SortedList(_doubleComparator) for _ in range(360)]
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

        for x in range(self.xdim):
            print(f"Sutun {x}", file=sys.stderr)
            for y in range(self.ydim):
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
            if not self._contains(self._angles[index], p):
                self._angles[index].add(p)

            searchedBin = 0

            k = antiIndex
            while True:
                if self._angles[k]:
                    searchedBin = k

                    size = self._angles[k].size()
                    if size == 1:
                        tmp1 = Tools.hueDistance(self._angles[k].get(0), p)
                        if tmp1 <= maxDist:
                            break
                        maxDist = tmp1
                        break

                    if k == index:
                        tmp1 = Tools.hueDistance(self._angles[k].get(0), p)
                        tmp2 = Tools.hueDistance(self._angles[k].get(size - 1), p)
                        tmp1 = max(tmp1, tmp2)
                        if tmp1 <= maxDist:
                            break
                        maxDist = tmp1
                        break

                    first = 0
                    last = size - 1
                    found = False
                    leftDist = 0.0
                    rightDist = 0.0
                    while True:
                        mid = first + (last - first) // 2

                        midDist = Tools.hueDistance(self._angles[k].get(mid), p)
                        if mid > first:
                            leftDist = Tools.hueDistance(self._angles[k].get(mid - 1), p)
                        else:
                            leftDist = Tools.hueDistance(self._angles[k].get(first), p)
                        if mid < last:
                            rightDist = Tools.hueDistance(self._angles[k].get(mid + 1), p)
                        else:
                            rightDist = Tools.hueDistance(self._angles[k].get(last), p)

                        if Tools.cmpr(rightDist, midDist) <= 0 and Tools.cmpr(leftDist, midDist) <= 0:
                            found = True
                            if Tools.cmpr(midDist, maxDist) > 0:
                                maxDist = midDist
                        elif Tools.cmpr(leftDist, midDist) > 0:
                            last = mid - 1
                        elif Tools.cmpr(midDist, rightDist) < 0:
                            first = mid + 1
                        else:
                            print(f"{first} {mid} {last}", file=sys.stderr)
                            print(f"{leftDist} {rightDist}", file=sys.stderr)

                        if found:
                            break
                        if first > last:
                            break
                    break
                k = (k + 1) % 360

            k = antiIndex - 1
            while True:
                if k < 0:
                    k += 360
                if self._angles[k]:
                    if searchedBin == k:
                        break

                    size = self._angles[k].size()
                    if size == 1:
                        tmp1 = Tools.hueDistance(self._angles[k].get(0), p)
                        if tmp1 <= maxDist:
                            break
                        maxDist = tmp1
                        break

                    if k == index:
                        tmp1 = Tools.hueDistance(self._angles[k].get(0), p)
                        tmp2 = Tools.hueDistance(self._angles[k].get(size - 1), p)
                        tmp1 = max(tmp1, tmp2)
                        if tmp1 <= maxDist:
                            break
                        maxDist = tmp1
                        break

                    first = 0
                    last = size - 1
                    found = False
                    leftDist = 0.0
                    rightDist = 0.0
                    while True:
                        mid = first + (last - first) // 2

                        midDist = Tools.hueDistance(self._angles[k].get(mid), p)
                        if mid > first:
                            leftDist = Tools.hueDistance(self._angles[k].get(mid - 1), p)
                        else:
                            leftDist = Tools.hueDistance(self._angles[k].get(first), p)
                        if mid < last:
                            rightDist = Tools.hueDistance(self._angles[k].get(mid + 1), p)
                        else:
                            rightDist = Tools.hueDistance(self._angles[k].get(last), p)

                        if Tools.cmpr(rightDist, midDist) <= 0 and Tools.cmpr(leftDist, midDist) <= 0:
                            found = True
                            if Tools.cmpr(midDist, maxDist) > 0:
                                maxDist = midDist
                        elif Tools.cmpr(leftDist, midDist) > 0:
                            last = mid - 1
                        elif Tools.cmpr(midDist, rightDist) < 0:
                            first = mid + 1
                        else:
                            print(f"{first} {mid} {last}", file=sys.stderr)
                            print(f"{leftDist} {rightDist}", file=sys.stderr)

                        if found:
                            break
                        if first > last:
                            break
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
    def _contains(s: SortedList, p: float) -> bool:
        first = 0
        last = s.size() - 1
        found = False
        while not found and first <= last:
            mid = first + (last - first) // 2
            v = s.get(mid)
            if v == p:
                found = True
            elif v > p:
                last = mid - 1
            elif v < p:
                first = mid + 1
        return found

    @staticmethod
    def invoke(img: Image, alpha: float, omega: float) -> Image | None:
        try:
            return AngularQFZ5().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

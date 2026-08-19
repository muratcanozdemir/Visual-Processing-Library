"""Port of ``vpt.algorithms.segmentation.OSR``: the over-segmentation ratio
between two label maps, ``numberOfFZ(map) / numberOfFZ(reference)``.

**Preserved bug**: ``rn1``/``rn2`` are Java ``int`` locals, so ``rn1 / rn2``
is *integer* division (truncated toward zero) before the result is boxed
into the ``Double`` output field -- not the true ratio a name like "OSR"
implies. E.g. 5 flat zones vs. 3 gives ``1``, not ``1.666...``. Transcribed
via ``idiv`` (which also reproduces Java's behaviour of *raising* on a
zero divisor here, unlike float division elsewhere in this port -- ``int /
0`` throws ``ArithmeticException`` in Java, `idiv`'s zero-division raises a
Python ``ZeroDivisionError`` for the same reason).

``createFZ`` is a DFS flood fill (Java ``java.util.Stack``, LIFO) unlike
the BFS queues (``ArrayDeque``/``LinkedList``) used elsewhere in this
package -- ported as a plain Python list used as a stack (``append``/
``pop``) to preserve that same traversal order, not a queue."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, idiv
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...IntegerImage import IntegerImage

_N = [
    Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1),
    Point(1, 1), Point(-1, -1), Point(-1, 1), Point(1, -1),
]  # fmt: skip


class OSR(Algorithm):
    def __init__(self):
        super().__init__()
        self.map: Image | None = None
        self.reference: Image | None = None
        self.osr: float | None = None
        self.inputFields = "map,reference"
        self.outputFields = "osr"

    def execute(self) -> None:
        rn1 = self._numberOfFZ(self.map)
        rn2 = self._numberOfFZ(self.reference)

        self.osr = float(idiv(rn1, rn2))

    def _numberOfFZ(self, img: Image) -> int:
        xdim = img.getXDim()
        ydim = img.getYDim()

        temp2 = IntegerImage(xdim, ydim, 1)
        temp2.fill(0)

        temp = BooleanImage(xdim, ydim, 1)
        temp.fill(False)

        label = 1
        for x in range(xdim):
            for y in range(ydim):
                if temp2.getXYInt(x, y) <= 0:
                    t = Point(x, y)

                    self._createFZ(img, t, label, temp, temp2, xdim, ydim)
                    label += 1
        return label - 1

    def _createFZ(
        self,
        input: Image,
        r: Point,
        label: int,
        temp: Image,
        temp2: Image,
        xdim: int,
        ydim: int,
    ) -> None:
        list2 = []
        s = []

        s.clear()
        s.append(r)
        temp.setXYBoolean(r.x, r.y, True)
        list2.append(r)

        while s:
            tmp = s.pop()

            x = tmp.x
            y = tmp.y

            p = input.getXYInt(x, y)
            temp2.setXYInt(x, y, label)

            for i in range(len(_N)):
                _x = x + _N[i].x
                _y = y + _N[i].y
                if _x >= 0 and _x < xdim and _y >= 0 and _y < ydim:
                    if not temp.getXYBoolean(_x, _y):
                        q = input.getXYInt(_x, _y)
                        if q == p:
                            t = Point(_x, _y)
                            s.append(t)

                            temp.setXYBoolean(t.x, t.y, True)
                            list2.append(t)

        for t in list2:
            temp.setXYBoolean(t.x, t.y, False)

    @staticmethod
    def invoke(map: Image, reference: Image) -> float | None:
        try:
            return OSR().preprocess(map, reference)
        except GlobalException:
            traceback.print_exc()
        return None

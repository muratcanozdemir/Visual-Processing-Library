"""Port of ``vpt.algorithms.segmentation.MP``: the "matching pixels" ratio
between a label map and a reference segmentation -- each connected region
of ``map`` votes (by majority) for the ``reference`` label its pixels most
often overlap, then the fraction of pixels where the two maps agree is
returned.

**Preserved bug**, same shape as ``OSR``'s: ``count``/``xdim*ydim`` are both
Java ``int`` locals, so ``count / (xdim * ydim)`` is *integer* division --
truncated toward zero -- before being boxed into the ``Double`` output.
Since ``count <= xdim*ydim`` always, this makes the result almost always
exactly ``0.0`` (or ``1.0`` for a perfect match), not the true agreement
fraction. Routed through ``idiv`` (which, like in ``OSR``, would raise on
a zero-area image, matching Java's ``ArithmeticException`` for `int / 0`).

``numberOfFZ``/``createFZ`` duplicate ``OSR``'s implementation verbatim in
the Java (not delegated) -- kept as an independent copy here too, per this
port's "structural 1:1, no premature sharing" convention (see
``linear.RotationInvariantGabor`` for the same call on a near-identical
precedent)."""

from __future__ import annotations

import traceback

import numpy as np

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


class MP(Algorithm):
    def __init__(self):
        super().__init__()
        self.map: Image | None = None
        self.reference: Image | None = None
        self.mp: float | None = None
        self.inputFields = "map,reference"
        self.outputFields = "mp"

    def execute(self) -> None:
        xdim = self.map.getXDim()
        ydim = self.map.getYDim()

        regionLabels = IntegerImage(xdim, ydim, 1)
        regionLabels.fill(-1)

        numRegions = self._numberOfFZ(self.reference)
        votes = [0] * numRegions

        list_ = []
        list2 = []
        s = []

        temp = BooleanImage(xdim, ydim, 1)
        temp.fill(False)

        for y in range(ydim):
            for x in range(xdim):
                p = self.map.getXYInt(x, y)
                if regionLabels.getXYInt(x, y) <= -1:
                    for i in range(len(votes)):
                        votes[i] = 0

                    r = Point(x, y)

                    s.clear()
                    s.append(r)
                    temp.setXYBoolean(r.x, r.y, True)
                    list_.append(r)

                    while s:
                        tmp = s.pop()

                        _p = self.reference.getXYInt(tmp.x, tmp.y)
                        votes[_p] += 1
                        list2.append(tmp)

                        for i in range(len(_N)):
                            _x = tmp.x + _N[i].x
                            _y = tmp.y + _N[i].y
                            if _x >= 0 and _x < xdim and _y >= 0 and _y < ydim:
                                if not temp.getXYBoolean(_x, _y):
                                    q = self.map.getXYInt(_x, _y)
                                    if q == p:
                                        t = Point(_x, _y)
                                        s.append(t)

                                        temp.setXYBoolean(t.x, t.y, True)
                                        list_.append(t)

                    for t in list_:
                        temp.setXYBoolean(t.x, t.y, False)
                    list_.clear()

                    region = 0
                    for i in range(1, len(votes)):
                        if votes[i] > votes[region]:
                            region = i

                    for t in list2:
                        regionLabels.setXYInt(t.x, t.y, region)
                    list2.clear()

        # Final agreement-count only; the flood-fill above it (both the
        # inline region-voting loop and _numberOfFZ/_createFZ) is genuinely
        # sequential and stays untouched. A pure equality count is
        # order-independent.
        count = int(
            np.count_nonzero(regionLabels.getIntArray() == self.reference.getIntArray())
        )
        self.mp = float(idiv(count, xdim * ydim))

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
        list2.clear()

    @staticmethod
    def invoke(map: Image, reference: Image) -> float | None:
        try:
            return MP().preprocess(map, reference)
        except GlobalException:
            traceback.print_exc()
        return None

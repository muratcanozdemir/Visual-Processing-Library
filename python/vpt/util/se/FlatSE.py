"""Port of ``vpt.util.se.FlatSE``.

A flat (binary) structuring element: a BooleanImage plus a centre point. The
morphology operators consume it through :meth:`getCoords`, which returns the
set pixels as offsets from the centre, with (0, 0) first when the centre
itself is set.

``BOX`` marks a rectangle, which lets the dilation/erosion operators take
their separable fast path (an hLine pass followed by a vLine pass) instead of
scanning the full neighbourhood.
"""

from __future__ import annotations

import sys

from ..._javacompat import Point, idiv, jcos, jround, jsin
from ...BooleanImage import BooleanImage
from ..Distance import Distance

TWO_PI = 6.283185307179586


class FlatSE(BooleanImage):
    __slots__ = ("center", "coords", "listCoords", "BOX")

    def __init__(self, xdim: int, ydim: int, center: Point):
        super().__init__(xdim, ydim, 1)
        self.center = center
        self.coords = None
        self.listCoords = None
        self.BOX = False

    # ------------------------------------------------------------------
    @staticmethod
    def _sizeControl(size: int) -> int:
        newSize = size
        if newSize < 3:
            print("Warning: square SE size increased to 3", file=sys.stderr)
            newSize = 3
        if newSize % 2 == 0:
            newSize += 1
            print(f"Warning: square SE modified to odd size {newSize}", file=sys.stderr)
        return newSize

    # ------------------------------------------------------------------
    @staticmethod
    def square(size: int, x: int = None, y: int = None) -> "FlatSE":
        """square(size) or square(size, x, y) with an explicit centre."""
        size = FlatSE._sizeControl(size)
        if x is None:
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(True)
            se.BOX = True
            return se
        se = FlatSE(size, size, Point(x, y))
        se.fillBoolean(True)
        if x == idiv(size, 2) and y == idiv(size, 2):
            se.BOX = True
        return se

    @staticmethod
    def hollowSquare(size: int) -> "FlatSE":
        size = FlatSE._sizeControl(size)
        se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
        se.fillBoolean(False)
        for x in range(size):
            for y in range(size):
                if x == 0 or x == size - 1 or y == 0 or y == size - 1:
                    se.setXYBoolean(x, y, True)
        return se

    @staticmethod
    def disk(radius: int) -> "FlatSE":
        if radius < 1:
            radius = 1
        size = radius * 2 + 1
        se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
        se.fillBoolean(False)
        for x in range(size):
            for y in range(size):
                if Distance.EuclideanDistance(x, y, se.center.x, se.center.y) <= radius:
                    se.setXYBoolean(x, y, True)
        return se

    @staticmethod
    def partiallyFilledDisk(radius: int, radius2: int) -> "FlatSE":
        se = FlatSE.circle(radius)
        size = radius * 2 + 1
        for x in range(size):
            for y in range(size):
                if Distance.EuclideanDistance(x, y, se.center.x, se.center.y) <= radius2:
                    se.setXYBoolean(x, y, True)
        return se

    @staticmethod
    def cross(size: int) -> "FlatSE":
        size = FlatSE._sizeControl(size)
        se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
        se.fillBoolean(False)
        for x in range(se.xdim):
            for y in range(se.ydim):
                if x == idiv(size, 2) or y == idiv(size, 2):
                    se.setXYBoolean(x, y, True)
        return se

    @staticmethod
    def hLine(size: int, x: int = None) -> "FlatSE":
        size = FlatSE._sizeControl(size)
        if x is None:
            x = idiv(size, 2)
        se = FlatSE(size, 1, Point(x, 0))
        se.fillBoolean(True)
        return se

    @staticmethod
    def vLine(size: int, y: int = None) -> "FlatSE":
        size = FlatSE._sizeControl(size)
        if y is None:
            y = idiv(size, 2)
        se = FlatSE(1, size, Point(0, y))
        se.fillBoolean(True)
        return se

    # ------------------------------------------------------------------
    # Circle-based elements. All of these walk a perimeter of 8*radius steps
    # and round the resulting coordinates, so adjacent steps frequently land
    # on the same pixel -- that is intended, not a bug.
    # ------------------------------------------------------------------
    @staticmethod
    def _arcPoint(se: "FlatSE", radius: int, i: int, perimeter: int) -> tuple[int, int]:
        x = se.center.x + int(jround(radius * jcos(TWO_PI * i / perimeter)))
        y = se.center.y + int(jround(radius * jsin(TWO_PI * i / perimeter)))
        return x, y

    @staticmethod
    def circle(radius: int) -> "FlatSE":
        if radius < 1:
            radius = 1
        size = radius * 2 + 1
        se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
        se.fillBoolean(False)
        se.setXYBoolean(radius, radius, True)

        perimeter = 8 * radius
        for i in range(perimeter):
            x, y = FlatSE._arcPoint(se, radius, i, perimeter)
            se.setXYBoolean(x, y, True)
        return se

    @staticmethod
    def centerlessCircle(radius: int) -> "FlatSE":
        if radius < 1:
            radius = 1
        size = radius * 2 + 1
        se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
        se.fillBoolean(False)
        se.listCoords = []

        perimeter = 8 * radius
        for i in range(perimeter):
            x, y = FlatSE._arcPoint(se, radius, i, perimeter)
            se.setXYBoolean(x, y, True)
            FlatSE._addListCoord(se.listCoords, x - se.center.x, y - se.center.y)
        return se

    @staticmethod
    def _addListCoord(a: list, x: int, y: int) -> None:
        for p in a:
            if p.x == x and p.y == y:
                return
        a.append(Point(x, y))

    @staticmethod
    def partialArcs(radius: int) -> list["FlatSE"]:
        return FlatSE._partialArcs(radius, 4)

    @staticmethod
    def partialArcs2(radius: int) -> list["FlatSE"]:
        return FlatSE._partialArcs(radius, 8)

    @staticmethod
    def _partialArcs(radius: int, n: int) -> list["FlatSE"]:
        """Shared body of partialArcs (n=4) and partialArcs2 (n=8); the Java
        duplicates it verbatim apart from the divisor."""
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        ses = []
        for j in range(n):
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(False)
            se.setXYBoolean(radius, radius, True)

            perimeter = 8 * radius
            q = idiv(perimeter, n)
            for i in range(j * q, q * (j + 1)):
                x, y = FlatSE._arcPoint(se, radius, i, perimeter)
                se.setXYBoolean(x, y, True)
            ses.append(se)
        return ses

    @staticmethod
    def partialCircles(radius: int) -> list["FlatSE"]:
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        ses = []
        for j in range(4):
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(False)
            se.setXYBoolean(radius, radius, True)

            perimeter = 8 * radius
            q = idiv(perimeter, 4)
            for i in range(perimeter):
                if i < j * q or i >= (j + 1) * q:
                    x, y = FlatSE._arcPoint(se, radius, i, perimeter)
                    se.setXYBoolean(x, y, True)
            ses.append(se)
        return ses

    @staticmethod
    def partialCircles2(radius: int) -> list["FlatSE"]:
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        perimeter = 8 * radius
        q = idiv(perimeter, 4)
        r = idiv(perimeter, 8)

        def blank() -> "FlatSE":
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(False)
            se.setXYBoolean(radius, radius, True)
            return se

        # The four predicates are transcribed exactly as written, including
        # ses[0]'s `i < 0` disjunct, which is never true.
        preds = [
            lambda i: (i < 0 or i >= q) and (i < 2 * q or i >= 3 * q),
            lambda i: (i < q or i >= 2 * q) and (i < 3 * q or i >= 4 * q),
            lambda i: i < 7 * r and i >= r and (i < 3 * r or i >= 5 * r),
            lambda i: (i < r or i >= 3 * r) and (i < 5 * r or i >= 7 * r),
        ]

        ses = []
        for pred in preds:
            se = blank()
            for i in range(perimeter):
                if pred(i):
                    x, y = FlatSE._arcPoint(se, radius, i, perimeter)
                    se.setXYBoolean(x, y, True)
            ses.append(se)
        return ses

    @staticmethod
    def pointCouples(radius: int) -> list["FlatSE"]:
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        ses = []
        perimeter = 8 * radius

        prev = None
        for i in range(idiv(perimeter, 2)):
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(False)
            se.setXYBoolean(se.center.x, se.center.y, True)

            x1, y1 = FlatSE._arcPoint(se, radius, i, perimeter)
            se.setXYBoolean(x1, y1, True)

            j = (i + idiv(perimeter, 2)) % perimeter
            x2, y2 = FlatSE._arcPoint(se, radius, j, perimeter)
            se.setXYBoolean(x2, y2, True)

            if x1 != x2 or y1 != y2:
                if prev is not None:
                    if (prev.getCoords()[1].x != se.getCoords()[1].x
                            or prev.getCoords()[1].y != se.getCoords()[1].y):
                        ses.append(se)
                        prev = se
                else:
                    ses.append(se)
                    prev = se
        return ses

    @staticmethod
    def pointCouples2(radius: int) -> list["FlatSE"]:
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        ses = []
        perimeter = 8 * radius

        prev = None
        for i in range(perimeter):
            se = FlatSE(size, size, Point(idiv(size, 2), idiv(size, 2)))
            se.fillBoolean(False)
            se.setXYBoolean(se.center.x, se.center.y, True)

            x1, y1 = FlatSE._arcPoint(se, radius, i, perimeter)
            se.setXYBoolean(x1, y1, True)

            if x1 != se.center.x or y1 != se.center.y:
                if prev is not None:
                    if (prev.getCoords()[1].x != se.getCoords()[1].x
                            or prev.getCoords()[1].y != se.getCoords()[1].y):
                        ses.append(se)
                        prev = se
                else:
                    ses.append(se)
                    prev = se
        return ses

    # ------------------------------------------------------------------
    def getHLine(self) -> "FlatSE":
        if not self.BOX:
            return self
        return FlatSE.hLine(self.getXDim(), self.center.x)

    def getVLine(self) -> "FlatSE":
        if not self.BOX:
            return self
        return FlatSE.vLine(self.getYDim(), self.center.y)

    def reflection(self) -> "FlatSE":
        se = FlatSE(self.getXDim(), self.getYDim(),
                    Point(self.getXDim() - 1 - self.center.x,
                          self.getYDim() - 1 - self.center.y))
        se.fillBoolean(False)
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                se.setXYBoolean(self.getXDim() - 1 - x, self.getYDim() - 1 - y,
                                self.getXYBoolean(x, y))
        return se

    def getCoords(self):
        """Set pixels as (dx, dy) offsets from the centre, centre first.

        Cached after the first call, as in Java. Callers must not mutate the
        returned Points.
        """
        if self.coords is not None:
            return self.coords

        size = 0
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                if self.getXYBoolean(x, y):
                    size += 1

        self.coords = [None] * size

        k = 0
        if self.getXYBoolean(self.center.x, self.center.y):
            self.coords[k] = Point(0, 0)
            k += 1
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                if self.getXYBoolean(x, y) and (x != self.center.x or y != self.center.y):
                    self.coords[k] = Point(x - self.center.x, y - self.center.y)
                    k += 1
        return self.coords

    def getListCoords(self):
        return self.listCoords

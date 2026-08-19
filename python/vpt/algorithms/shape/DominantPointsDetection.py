"""Port of ``vpt.algorithms.shape.DominantPointsDetection``: thresholds
and opens the input to a mask, pads it by one pixel on every side (so the
border walk below never needs a bounds check against the image edge),
walks its outer 8-connected border into an ordered point list, then
simplifies that polyline with the Ramer-Douglas-Peucker algorithm
(``properRDP``) at the given ``epsilon``. Output points are shifted back
by ``(-1, -1)`` to undo the padding.

``findPerpendicularDistance``'s ``area / bottom`` is routed through
``jdiv``: ``bottom`` is the distance between the RDP segment's endpoints,
which is exactly zero when they coincide (a padded single-pixel border
segment), and Java's double division never raises where Python's ``/``
would try to -- it doesn't here since ``area`` is also ``0.0`` in that
case (``0.0/0.0`` => ``NaN``, still needs ``jdiv`` to avoid
``ZeroDivisionError``)."""

from __future__ import annotations

import math
import traceback

from ..._javacompat import Point, jdiv
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ..mm.binary.BInternGradient import BInternGradient
from ..mm.binary.BOpening import BOpening
from ..segmentation.Threshold import Threshold


class DominantPointsDetection(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[Point] | None = None
        self.input: Image | None = None
        self.epsilon: float | None = None
        self.inputFields = "input, epsilon"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        mask = Threshold.invoke(self.input, 0.00392156862745098)

        mask = BOpening.invoke(mask, FlatSE.square(3))

        xdim = mask.getXDim()
        ydim = mask.getYDim()
        temp = BooleanImage(xdim + 2, ydim + 2, 1)
        for x in range(xdim):
            for y in range(ydim):
                if mask.getXYBoolean(x, y):
                    temp.setXYBoolean(x + 1, y + 1, True)
        mask = temp

        outerBorders = BInternGradient.invoke(mask, FlatSE.square(3))

        points: list[Point] = []

        p = None

        xdim = outerBorders.getXDim()
        ydim = outerBorders.getYDim()
        for x in range(xdim):
            for y in range(ydim):
                if outerBorders.getXYBoolean(x, y):
                    p = Point(x, y)
                    break
            if p is not None:
                break

        flag = outerBorders.newInstance(False)
        flag.fill(False)
        while True:
            points.append(Point(p.x, p.y))
            flag.setXYBoolean(p.x, p.y, True)
            if (
                p.x + 1 < xdim
                and outerBorders.getXYBoolean(p.x + 1, p.y)
                and not flag.getXYBoolean(p.x + 1, p.y)
            ):
                p.x += 1
            elif (
                p.y + 1 < ydim
                and outerBorders.getXYBoolean(p.x, p.y + 1)
                and not flag.getXYBoolean(p.x, p.y + 1)
            ):
                p.y += 1
            elif (
                p.x - 1 >= 0
                and outerBorders.getXYBoolean(p.x - 1, p.y)
                and not flag.getXYBoolean(p.x - 1, p.y)
            ):
                p.x -= 1
            else:
                if (
                    p.y - 1 < 0
                    or not outerBorders.getXYBoolean(p.x, p.y - 1)
                    or flag.getXYBoolean(p.x, p.y - 1)
                ):
                    break
                p.y -= 1

        points = self._properRDP(points, self.epsilon)

        self.output = [Point(q.x - 1, q.y - 1) for q in points]

    def _properRDP(self, points: list[Point], epsilon: float) -> list[Point]:
        firstPoint = points[0]
        lastPoint = points[len(points) - 1]
        if len(points) < 3:
            return points

        index = -1
        dist = 0.0
        for i in range(1, len(points) - 1):
            cDist = self._findPerpendicularDistance(points[i], firstPoint, lastPoint)
            if cDist > dist:
                dist = cDist
                index = i

        if dist >= epsilon:
            r1 = self._properRDP(self._getSubList(points, 0, index), epsilon)
            r2 = self._properRDP(self._getSubList(points, index, len(points) - 1), epsilon)

            result = r1
            self._join(result, r2)
        else:
            result = [firstPoint, lastPoint]

        return result

    def _join(self, list1: list[Point], list2: list[Point]) -> None:
        for p in list2:
            exists = False
            for q in list1:
                if q.x == p.x and q.y == p.y:
                    exists = True
                    break
            if not exists:
                list1.append(p)

    def _getSubList(self, points: list[Point], beg: int, end: int) -> list[Point]:
        return [points[i] for i in range(beg, end + 1)]

    def _findPerpendicularDistance(self, p: Point, p1: Point, p2: Point) -> float:
        area = abs(
            0.5
            * (p1.x * p2.y + p2.x * p.y + p.x * p1.y - p2.x * p1.y - p.x * p2.y - p1.x * p.y)
        )
        bottom = math.sqrt((p1.x - p2.x) ** 2.0 + (p1.y - p2.y) ** 2.0)
        return jdiv(area, bottom) * 2.0

    @staticmethod
    def invoke(img: Image, epsilon: float) -> list[Point] | None:
        try:
            return DominantPointsDetection().preprocess(img, epsilon)
        except GlobalException:
            traceback.print_exc()
        return None

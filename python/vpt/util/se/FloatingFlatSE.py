"""Port of ``vpt.util.se.FloatingFlatSE``.

A structuring element whose coordinates are real-valued rather than snapped
to the pixel grid, sampled from ``Image.pgetXYCDouble``'s bilinear read. Used
by the ``mm/gray/floating`` operators.
"""

from __future__ import annotations

import math

from ..._javacompat import Point2D, jround

TWO_PI = 6.283185307179586


class FloatingFlatSE:
    __slots__ = ("center", "coords")

    def __init__(self):
        self.center = None
        self.coords = None

    @staticmethod
    def circle(radius: int, perimeter: int) -> "FloatingFlatSE":
        se = FloatingFlatSE()
        se.center = Point2D(0.0, 0.0)
        se.coords = [None] * perimeter
        for i in range(perimeter):
            # Note the axes: x is driven by -sin and y by +cos, so the walk
            # starts at (0, radius) and turns clockwise. Preserved as written.
            x = -1 * radius * math.sin(TWO_PI * i / perimeter)
            if abs(jround(x) - x) <= 1.0e-8:
                x = float(jround(x))
            y = radius * math.cos(TWO_PI * i / perimeter)
            if abs(jround(y) - y) <= 1.0e-8:
                y = float(jround(y))
            se.coords[i] = Point2D(x, y)
        return se

    def reflection(self) -> "FloatingFlatSE":
        """Returns self, not a reflected copy -- as in the Java."""
        return self

    def getCoords(self):
        return self.coords

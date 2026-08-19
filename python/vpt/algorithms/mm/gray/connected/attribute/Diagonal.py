"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Diagonal``: passes
zones whose bounding-box diagonal is at least ``threshold``."""

from __future__ import annotations

from ......util.Distance import Distance
from .Attribute import Attribute


class Diagonal(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        if len(pixels) >= 2:
            leftest = img.getXDim()
            rightest = 0
            highest = img.getYDim()
            lowest = 0
            for p in pixels:
                if p.x < leftest:
                    leftest = p.x
                if p.x > rightest:
                    rightest = p.x
                if p.y > lowest:
                    lowest = p.y
                if p.y < highest:
                    highest = p.y
            diagonal = int(Distance.EuclideanDistance(leftest, highest, rightest, lowest))
        else:
            diagonal = 0

        if diagonal < self.threshold:
            return False
        return True

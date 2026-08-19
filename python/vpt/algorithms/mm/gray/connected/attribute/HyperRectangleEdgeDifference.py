"""Port of ``vpt.algorithms.mm.gray.connected.attribute.HyperRectangleEdgeDifference``:
passes zones whose bounding-box side-length spread (longest minus shortest
per-channel edge) is at least ``threshold``."""

from __future__ import annotations

from ......_javacompat import INT_MAX
from .Attribute import Attribute


class HyperRectangleEdgeDifference(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        cdim = img.getCDim()

        minimum = [255] * cdim
        maximum = [0] * cdim

        for p in pixels:
            pixel = img.getVXYByte(p.x, p.y)
            for c in range(len(maximum)):
                if pixel[c] < minimum[c]:
                    minimum[c] = pixel[c]
                if pixel[c] > maximum[c]:
                    maximum[c] = pixel[c]

        maxEdge = 0
        minEdge = INT_MAX
        for c in range(len(maximum)):
            if maxEdge < maximum[c] - minimum[c]:
                maxEdge = maximum[c] - minimum[c]
            if minEdge > maximum[c] - minimum[c]:
                minEdge = maximum[c] - minimum[c]

        diff = maxEdge - minEdge
        if diff < self.threshold:
            return False
        return True

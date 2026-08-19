"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Sdev``: passes zones
whose intensity standard deviation is at least ``threshold``."""

from __future__ import annotations

import math

from .Attribute import Attribute


class Sdev(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        mean = 0.0
        sdev = 0.0
        for p in pixels:
            mean += img.getXYByte(p.x, p.y)
        mean /= len(pixels)
        for p in pixels:
            tmp = img.getXYByte(p.x, p.y)
            sdev += (tmp - mean) * (tmp - mean)
        sdev = math.sqrt(sdev / len(pixels))
        if sdev < self.threshold:
            return False
        return True

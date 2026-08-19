"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Mean``: passes zones
whose mean intensity is at least ``threshold``."""

from __future__ import annotations

from .Attribute import Attribute


class Mean(Attribute):
    def __init__(self, threshold: float):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        mean = 0.0
        for p in pixels:
            mean += img.getXYDouble(p.x, p.y)
        mean /= len(pixels)
        if mean < self.threshold:
            return False
        return True

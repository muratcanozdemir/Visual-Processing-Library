"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Volume``: passes
zones whose total pixel intensity is at least ``threshold``."""

from __future__ import annotations

from .Attribute import Attribute


class Volume(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        volume = 0
        for p in pixels:
            volume += img.getXYByte(p.x, p.y)
        if volume < self.threshold:
            return False
        return True

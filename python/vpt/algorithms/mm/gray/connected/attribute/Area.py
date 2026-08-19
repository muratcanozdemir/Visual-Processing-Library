"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Area``: passes zones
with at least ``threshold`` pixels."""

from __future__ import annotations

from .Attribute import Attribute


class Area(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        if len(pixels) < self.threshold:
            return False
        return True

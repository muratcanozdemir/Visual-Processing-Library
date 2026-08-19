"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiArea``: passes
zones with at least ``threshold`` pixels."""

from __future__ import annotations

from .MultiAttribute import MultiAttribute


class MultiArea(MultiAttribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        if pixels.size() < self.threshold:
            return False
        return True

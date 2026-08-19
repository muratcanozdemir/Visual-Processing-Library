"""Port of ``vpt.algorithms.mm.gray.connected.attribute.MultiSdev``: passes
zones whose mean per-pixel Euclidean distance from the zone's per-channel
mean vector (scaled to a byte range) is at least ``threshold``."""

from __future__ import annotations

from ......util.Distance import Distance
from .Attribute import Attribute


class MultiSdev(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        mean = [0.0] * img.getCDim()
        sdev = 0.0

        for p in pixels:
            for c in range(len(mean)):
                mean[c] += img.getXYCDouble(p.x, p.y, c)
        for c in range(len(mean)):
            mean[c] /= len(pixels)

        for p in pixels:
            vector = img.getVXYDouble(p.x, p.y)
            sdev += Distance.euclidean(mean, vector)

        sdev *= 255.0
        sdev /= len(pixels)

        if sdev < self.threshold:
            return False
        return True

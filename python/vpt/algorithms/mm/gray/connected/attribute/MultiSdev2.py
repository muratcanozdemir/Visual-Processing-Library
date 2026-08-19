"""Port of ``vpt.algorithms.mm.gray.connected.attribute.MultiSdev2``: like
``MultiSdev``, but computes an actual standard deviation of per-pixel
distances from the centre (``MultiSdev`` computes a mean distance, not a
deviation -- the two are genuinely different statistics, transcribed as
named)."""

from __future__ import annotations

from ......util.Distance import Distance
from .Attribute import Attribute


class MultiSdev2(Attribute):
    def __init__(self, threshold: float):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        center = [0.0] * img.getCDim()
        sdev = 0.0
        mean = 0.0

        for p in pixels:
            for c in range(len(center)):
                center[c] += img.getXYCDouble(p.x, p.y, c)
        for c in range(len(center)):
            center[c] /= len(pixels)

        for p in pixels:
            vector = img.getVXYDouble(p.x, p.y)
            mean += Distance.euclidean(center, vector)
        mean /= len(pixels)

        for p in pixels:
            vector = img.getVXYDouble(p.x, p.y)
            tmp = Distance.euclidean(center, vector)
            sdev += (mean - tmp) * (mean - tmp)

        sdev *= 255.0
        sdev /= len(pixels)

        if sdev < self.threshold:
            return False
        return True

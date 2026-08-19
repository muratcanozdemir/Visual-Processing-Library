"""Port of ``vpt.algorithms.mm.gray.connected.attribute.SphereRadius``:
passes zones whose maximum per-channel-vector distance from the zone's
per-channel mean ("centre") is at least ``threshold``."""

from __future__ import annotations

from ......util.Distance import Distance
from .Attribute import Attribute


class SphereRadius(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        dim = img.getCDim()
        maxDistance = 0.0

        center = [0.0] * dim
        for p in pixels:
            pixel = img.getVXYByte(p.x, p.y)
            for i in range(dim):
                center[i] += pixel[i]
        for i in range(dim):
            center[i] /= len(pixels)

        for p in pixels:
            pixel = img.getVXYDouble(p.x, p.y)
            tmpDist = Distance.euclidean(center, pixel)
            if tmpDist > maxDistance:
                maxDistance = tmpDist

        if maxDistance < self.threshold:
            return False
        return True

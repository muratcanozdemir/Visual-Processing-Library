"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiSdev``: passes
zones whose mean euclidean distance from the per-channel mean vector,
scaled by 255, reaches ``threshold``."""

from __future__ import annotations

from ......util.Distance import Distance
from .MultiAttribute import MultiAttribute


class MultiSdev(MultiAttribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        mean = [0.0] * img.getCDim()
        sdev = 0.0
        n = pixels.getHead()
        while n is not None:
            p = n.getDatum()
            for c in range(len(mean)):
                mean[c] += img.getXYCDouble(p.x, p.y, c)
            n = n.getNext()

        for c in range(len(mean)):
            mean[c] /= pixels.size()

        n = pixels.getHead()
        while n is not None:
            p = n.getDatum()
            vector = img.getVXYDouble(p.x, p.y)
            sdev += Distance.euclidean(mean, vector)
            n = n.getNext()

        sdev *= 255.0

        sdev /= pixels.size()
        if sdev < self.threshold:
            return False
        return True

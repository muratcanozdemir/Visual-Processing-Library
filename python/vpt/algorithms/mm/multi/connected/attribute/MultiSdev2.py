"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiSdev2``:
passes zones whose standard deviation *across channel means* (not across
pixels, unlike ``MultiSdev``), scaled by 255, reaches ``threshold``."""

from __future__ import annotations

import math

from .MultiAttribute import MultiAttribute


class MultiSdev2(MultiAttribute):
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

        interDimensionMean = 0.0
        for c in range(len(mean)):
            interDimensionMean += mean[c]

        interDimensionMean /= len(mean)

        for c in range(len(mean)):
            sdev += (interDimensionMean - mean[c]) * (interDimensionMean - mean[c])

        sdev /= len(mean)

        sdev = math.sqrt(sdev)

        sdev *= 255.0
        if sdev < self.threshold:
            return False
        return True

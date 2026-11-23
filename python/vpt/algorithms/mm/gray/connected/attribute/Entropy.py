"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Entropy``: passes
zones whose (base-2, via a log10 change of base) intensity-histogram
entropy is at least ``threshold``."""

from __future__ import annotations


from ......_javacompat import jlog10
from .Attribute import Attribute


class Entropy(Attribute):
    def __init__(self, threshold: float):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        histo = [0.0] * 256
        for p in pixels:
            histo[img.getXYByte(p.x, p.y)] += 1.0
        for i in range(len(histo)):
            histo[i] /= len(pixels)

        entropy = 0.0
        logBaseChange = jlog10(2.0)
        for p in pixels:
            q = img.getXYByte(p.x, p.y)
            entropy += histo[q] * (jlog10(histo[q]) / logBaseChange)

        if entropy < 0.0:
            entropy *= -1.0
        if entropy < self.threshold:
            return False
        return True

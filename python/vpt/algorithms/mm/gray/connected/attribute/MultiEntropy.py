"""Port of ``vpt.algorithms.mm.gray.connected.attribute.MultiEntropy``:
``Entropy`` over a scalar collapse of each multichannel pixel (its Euclidean
norm, rescaled to a byte bin via the channel count)."""

from __future__ import annotations

import math

from ......util.Tools import Tools
from .Attribute import Attribute


class MultiEntropy(Attribute):
    def __init__(self, threshold: float):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        histo = [0.0] * 256
        for p in pixels:
            pixelVal = int(255.0 * Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y)) / math.sqrt(img.getCDim()))
            histo[pixelVal] += 1.0
        for i in range(len(histo)):
            histo[i] /= len(pixels)

        entropy = 0.0
        logBaseChange = math.log10(2.0)
        for p in pixels:
            q = int(255.0 * Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y)) / math.sqrt(img.getCDim()))
            entropy += histo[q] * (math.log10(histo[q]) / logBaseChange)

        if entropy < 0.0:
            entropy *= -1.0
        if entropy < self.threshold:
            return False
        return True

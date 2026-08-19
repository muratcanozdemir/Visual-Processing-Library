"""Port of ``vpt.algorithms.mm.gray.connected.attribute.GeometricVolume``:
passes zones whose per-channel bounding-box side lengths multiply to at
least ``threshold``."""

from __future__ import annotations

from .Attribute import Attribute


class GeometricVolume(Attribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        cdim = img.getCDim()
        distances = [0.0] * cdim

        minimum = [255] * cdim
        maximum = [0] * cdim

        volume = 1
        for p in pixels:
            pixel = img.getVXYByte(p.x, p.y)
            for c in range(len(maximum)):
                if pixel[c] < minimum[c]:
                    minimum[c] = pixel[c]
                if pixel[c] > maximum[c]:
                    maximum[c] = pixel[c]

        for c in range(len(distances)):
            distances[c] = maximum[c] - minimum[c]
        for c in range(len(distances)):
            volume = int(volume * distances[c])

        if volume < self.threshold:
            return False
        return True

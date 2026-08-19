"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiGeometricVolume``:
passes zones whose per-channel value range, multiplied across channels,
reaches ``threshold``."""

from __future__ import annotations

from .MultiAttribute import MultiAttribute


class MultiGeometricVolume(MultiAttribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        distances = [0.0] * img.getCDim()

        minimum = [255] * img.getCDim()
        maximum = [0] * img.getCDim()

        volume = 1
        n = pixels.getHead()
        while n is not None:
            p = n.getDatum()
            pixel = img.getVXYByte(p.x, p.y)

            for c in range(len(maximum)):
                if pixel[c] < minimum[c]:
                    minimum[c] = pixel[c]
                if pixel[c] > maximum[c]:
                    maximum[c] = pixel[c]
            n = n.getNext()

        for c in range(len(distances)):
            distances[c] = maximum[c] - minimum[c]

        for c in range(len(distances)):
            volume = int(volume * distances[c])

        if volume < self.threshold:
            return False
        return True

"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiSpectralVolume``:
passes zones whose summed per-pixel, per-channel byte value (across every
pixel and every band) reaches ``threshold``.

The Java reads ``img.getVXYByte(p.x, p.y)`` into ``val`` purely to get
``val.length`` for the inner loop bound, then re-reads each channel one at a
time via ``img.getXYCByte`` instead of indexing ``val`` -- transcribed as
found (two reads per channel instead of one), not simplified to reuse
``val``.
"""

from __future__ import annotations

from .MultiAttribute import MultiAttribute


class MultiSpectralVolume(MultiAttribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        volume = 0
        n = pixels.getHead()
        while n is not None:
            p = n.getDatum()

            val = img.getVXYByte(p.x, p.y)
            for c in range(len(val)):
                volume += img.getXYCByte(p.x, p.y, c)
            n = n.getNext()

        if volume < self.threshold:
            return False
        return True

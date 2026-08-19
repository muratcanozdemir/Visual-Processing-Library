"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiDiagonal``:
passes zones whose bounding-box diagonal reaches ``threshold``.

PRESERVED BUG, transcribed as found: ``highest``/``lowest`` are seeded and
updated backwards relative to their names -- ``highest`` starts at
``img.getYDim()`` and only ever decreases (via ``p.y < highest``), while
``lowest`` starts at 0 and only ever increases (via ``p.y > lowest``). So
``highest`` ends up holding the *smallest* y seen and ``lowest`` the
*largest* -- the opposite of what a screen-space "highest point has the
smallest y" convention would suggest the names mean, though the resulting
diagonal length (``leftest``/``highest`` to ``rightest``/``lowest``) is
still a correct bounding-box diagonal regardless of what the intermediate
variables are named.
"""

from __future__ import annotations

from ......util.Distance import Distance
from .MultiAttribute import MultiAttribute


class MultiDiagonal(MultiAttribute):
    def __init__(self, threshold: int):
        self.threshold = threshold

    def test(self, pixels, img) -> bool:
        diagonal = 0
        if pixels.size() >= 2:
            leftest = img.getXDim()
            rightest = 0
            highest = img.getYDim()
            lowest = 0

            n = pixels.getHead()
            while n is not None:
                p = n.getDatum()
                if p.x < leftest:
                    leftest = p.x
                if p.x > rightest:
                    rightest = p.x
                if p.y > lowest:
                    lowest = p.y
                if p.y < highest:
                    highest = p.y
                n = n.getNext()

            diagonal = int(Distance.EuclideanDistance(leftest, highest, rightest, lowest))
        else:
            diagonal = 0

        if diagonal < self.threshold:
            return False
        return True

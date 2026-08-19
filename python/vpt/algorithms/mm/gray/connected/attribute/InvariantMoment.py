"""Port of ``vpt.algorithms.mm.gray.connected.attribute.InvariantMoment``:
passes zones whose Hu-style rotation/scale-invariant moment (``order`` in
``[1, 8]``) is at least ``threshold``.

``moment``/``centralMoment``/``spatialMoment`` build up the raw, central,
and normalised (scale-invariant) moments; ``invariantMoment`` combines pairs
of normalised moments into the eight classic Hu invariants. The pixel
coordinates are offset by 1 (``p.x + 1``, not ``p.x``) throughout, matching
the Java exactly.

``Math.pow`` is translated to Python's ``**``; both are IEEE-754 ``pow``
implementations that need not agree bit-for-bit in the last ULP, the same
caveat already documented for ``Math.cos``/``Math.sin`` vs NumPy's in
``oracle/DumpSE.java``. Most calls here use small integer exponents (where
this is very unlikely to matter), but ``spatialMoment``'s ``m00 ** alpha``
uses a genuinely fractional exponent (``alpha`` is 2.0 or 2.5 for every
order this class supports) -- a real, if narrow, bit-exactness risk worth
knowing about if a golden test for this class ever shows an off-by-one-ULP
failure.
"""

from __future__ import annotations

import sys

from .Attribute import Attribute


class InvariantMoment(Attribute):
    def __init__(self, threshold: float, order: int):
        self.threshold = threshold
        self.order = order

    def test(self, pixels, img) -> bool:
        moment = self._invariantMoment(pixels, img, self.order)
        if moment < self.threshold:
            return False
        return True

    def _moment(self, pixels, img, momentX: int, momentY: int) -> float:
        output = 0.0
        for p in pixels:
            tmp = img.getXYDouble(p.x, p.y)
            output += (p.x + 1) ** momentX * (p.y + 1) ** momentY * tmp
        return output

    def _centralMoment(self, pixels, img, momentX: int, momentY: int) -> float:
        m00 = self._moment(pixels, img, 0, 0)
        m01 = self._moment(pixels, img, 0, 1)
        m10 = self._moment(pixels, img, 1, 0)

        centroidX = m10 / m00
        centroidY = m01 / m00

        output = 0.0
        for p in pixels:
            tmp = img.getXYDouble(p.x, p.y)
            output += (p.x + 1 - centroidX) ** momentX * (p.y + 1 - centroidY) ** momentY * tmp
        return output

    def _spatialMoment(self, pixels, img, momentX: int, momentY: int) -> float:
        if momentX + momentY < 2:
            print("The sum of momentX and momentY must be >= 2 !", file=sys.stderr)

        muXY = self._centralMoment(pixels, img, momentX, momentY)
        m00 = self._moment(pixels, img, 0, 0)
        alpha = (momentX + momentY) / 2.0 + 1.0

        m00 = m00**alpha
        return muXY / m00

    def _invariantMoment(self, pixels, img, order: int) -> float:
        if order < 1 or order > 8:
            print("The order must be \\in [1,8] !", file=sys.stderr)

        sm = self._spatialMoment

        if order == 1:
            n20 = sm(pixels, img, 2, 0)
            n02 = sm(pixels, img, 0, 2)
            return n20 + n02

        if order == 2:
            n20 = sm(pixels, img, 2, 0)
            n02 = sm(pixels, img, 0, 2)
            n11 = sm(pixels, img, 1, 1)
            return (n20 - n02) * (n20 - n02) + 4.0 * n11 * n11

        if order == 3:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            return (n30 - 3.0 * n12) * (n30 - 3.0 * n12) + (3.0 * n21 - n03) * (3.0 * n21 - n03)

        if order == 4:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            return (n30 + n12) * (n30 + n12) + (n03 + n21) * (n03 + n21)

        if order == 5:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            return (n30 - 3.0 * n12) * (n30 + n12) * (
                (n30 + n12) * (n30 + n12) - 3.0 * (n21 + n03) * (n21 + n03)
            ) + (3.0 * n21 - n03) * (n21 + n03) * (
                3.0 * (n30 + n12) * (n30 + n12) - (n21 - n03) * (n21 - n03)
            )

        if order == 6:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            n20 = sm(pixels, img, 2, 0)
            n02 = sm(pixels, img, 0, 2)
            n11 = sm(pixels, img, 1, 1)
            return (n20 - n02) * (
                (n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03)
            ) + 4.0 * n11 * (n30 + n12) * (n21 + n03)

        if order == 7:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            return (3.0 * n21 - n03) * (n30 + n12) * (
                (n30 + n12) * (n30 + n12) - 3.0 * (n21 + n03) * (n21 + n03)
            ) - (n03 - 3.0 * n12) * (n21 + n03) * (
                3.0 * (n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03)
            )

        if order == 8:
            n30 = sm(pixels, img, 3, 0)
            n03 = sm(pixels, img, 0, 3)
            n21 = sm(pixels, img, 2, 1)
            n12 = sm(pixels, img, 1, 2)
            n20 = sm(pixels, img, 2, 0)
            n02 = sm(pixels, img, 0, 2)
            n11 = sm(pixels, img, 1, 1)
            return n11 * (
                (n30 + n12) * (n30 + n12) - (n03 + n21) * (n03 + n21)
            ) - (n20 - n02) * (n30 + n12) * (n03 + n21)

        print("Never supposed to be here!", file=sys.stderr)
        return 0.0

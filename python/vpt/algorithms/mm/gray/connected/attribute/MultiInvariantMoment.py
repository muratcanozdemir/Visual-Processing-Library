"""Port of ``vpt.algorithms.mm.gray.connected.attribute.MultiInvariantMoment``.

Identical to ``InvariantMoment`` except ``moment``/``centralMoment`` collapse
each multichannel pixel to a scalar via its Euclidean norm
(``Tools.EuclideanNorm``) instead of reading a single-channel double value.
Kept as a separate file mirroring the Java structure (also duplicated
there) rather than factored to share code with ``InvariantMoment``.

Same ``Math.pow``-vs-``**`` bit-exactness caveat as ``InvariantMoment``
applies to ``spatialMoment``'s fractional-exponent call.
"""

from __future__ import annotations

import sys

from ......_javacompat import jpow
from ......util.Tools import Tools
from .Attribute import Attribute


class MultiInvariantMoment(Attribute):
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
            tmp = Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y))
            output += jpow(float(p.x + 1), momentX) * jpow(float(p.y + 1), momentY) * tmp
        return output

    def _centralMoment(self, pixels, img, momentX: int, momentY: int) -> float:
        m00 = self._moment(pixels, img, 0, 0)
        m01 = self._moment(pixels, img, 0, 1)
        m10 = self._moment(pixels, img, 1, 0)

        centroidX = m10 / m00
        centroidY = m01 / m00

        output = 0.0
        for p in pixels:
            tmp = Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y))
            output += jpow(p.x + 1 - centroidX, momentX) * jpow(p.y + 1 - centroidY, momentY) * tmp
        return output

    def _spatialMoment(self, pixels, img, momentX: int, momentY: int) -> float:
        if momentX + momentY < 2:
            print("The sum of momentX and momentY must be >= 2 !", file=sys.stderr)

        muXY = self._centralMoment(pixels, img, momentX, momentY)
        m00 = self._moment(pixels, img, 0, 0)
        alpha = (momentX + momentY) / 2.0 + 1.0

        m00 = jpow(m00, alpha)
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

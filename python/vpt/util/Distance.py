"""Port of ``vpt.util.Distance``.

Vector distance measures. Every method prints to stderr and returns -1.0 on a
length mismatch rather than raising; preserved from the Java.

Note the float-arithmetic detail in :meth:`cosine` and :meth:`bhattacharyya`:
the Java normalises by ``Math.sqrt(sumP * sumQ)``, which is not bit-identical
to ``sqrt(sumP) * sqrt(sumQ)``. The grouping is preserved exactly.
"""

from __future__ import annotations

import math
import sys

from .._javacompat import jacos, jdiv, jsqrt


class Distance:
    @staticmethod
    def _mismatch() -> float:
        print("Distance: Incompatible vector lengths", file=sys.stderr)
        return -1.0

    # ------------------------------------------------------------------
    @staticmethod
    def euclidean(p, q) -> float:
        """Euclidean distance.

        Java declares this twice, for ``double[]`` and ``int[]``, with
        identical bodies. The int overload accumulates each squared term in
        int arithmetic before widening, so it could in principle overflow;
        it is only ever called with pixel values and coordinates, where the
        squared difference cannot approach 2^31.
        """
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        for i in range(len(p)):
            distance += (p[i] - q[i]) * (p[i] - q[i])
        return math.sqrt(distance)

    @staticmethod
    def EuclideanDistance(*args) -> float:
        """EuclideanDistance(x1, y1, x2, y2) or EuclideanDistance(p1, p2)."""
        if len(args) == 2:
            p1, p2 = args
            return Distance.EuclideanDistance(p1.x, p1.y, p2.x, p2.y)
        x1, y1, x2, y2 = args
        return math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))

    # ------------------------------------------------------------------
    @staticmethod
    def intersection(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        for i in range(len(p)):
            distance += min(p[i], q[i])
        return 1.0 - distance

    @staticmethod
    def bhattacharyya(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        sumP = 0.0
        for i in range(len(p)):
            sumP += p[i]
        sumQ = 0.0
        for i in range(len(p)):
            sumQ += q[i]
        temp = jsqrt(sumP * sumQ)
        for i in range(len(p)):
            distance += jdiv(jsqrt(p[i] * q[i]), temp)
        # 1.0 - distance goes negative for non-normalised inputs, where
        # Java's sqrt yields NaN rather than raising.
        return jsqrt(1.0 - distance)

    @staticmethod
    def cosine(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        sumP = 0.0
        for i in range(len(p)):
            sumP += p[i] * p[i]
        sumQ = 0.0
        for i in range(len(p)):
            sumQ += q[i] * q[i]
        temp = jsqrt(sumP * sumQ)
        for i in range(len(p)):
            distance += p[i] * q[i]
        # temp is 0 for a zero vector; Java returns +-Infinity or NaN.
        return jdiv(distance, temp)

    @staticmethod
    def spectralAngleDistance(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        from .Tools import Tools

        dotProduct = Tools.dotProduct(p, q)
        norm1 = Tools.norm(p)
        norm2 = Tools.norm(q)

        # Both norms are 0 for zero vectors, and accumulated rounding can
        # push the ratio just outside [-1, 1]; Java yields NaN for both.
        return jacos(jdiv(dotProduct, norm1 * norm2))

    @staticmethod
    def innerProduct(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        for i in range(len(p)):
            distance += p[i] * q[i]
        return distance

    @staticmethod
    def manhattan(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        for i in range(len(p)):
            distance += abs(p[i] - q[i])
        return distance

    @staticmethod
    def chebyshev(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        # Seeded from element 0 rather than 0.0, so an empty input raises
        # (IndexError here, ArrayIndexOutOfBoundsException in Java).
        distance = abs(p[0] - q[0])
        for i in range(1, len(p)):
            tmp = abs(p[i] - q[i])
            if tmp > distance:
                distance = tmp
        return distance

    @staticmethod
    def chiSquare(p, q) -> float:
        if len(p) != len(q):
            return Distance._mismatch()
        distance = 0.0
        for i in range(len(p)):
            if p[i] > 0.0 or q[i] > 0.0:
                distance += jdiv((p[i] - q[i]) * (p[i] - q[i]), p[i] + q[i])
        return distance

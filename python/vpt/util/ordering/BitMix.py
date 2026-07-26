"""Port of ``vpt.util.ordering.BitMix``.

Bit-interleaved (Morton-style) ordering of an RGB triple: compares the most
significant bit of all three channels, then the next, and so on.

The Java keeps its scratch arrays as instance fields rather than locals, so a
single BitMix is not safe to share across threads. Preserved.
"""

from __future__ import annotations

from ..._javacompat import int32, iushr, jround
from .AlgebraicOrdering import AlgebraicOrdering


class BitMix(AlgebraicOrdering):
    def __init__(self):
        self.p1 = [0, 0, 0]
        self.p2 = [0, 0, 0]
        self.s1 = [[0] * 8 for _ in range(3)]
        self.s2 = [[0] * 8 for _ in range(3)]

    def compare(self, v1, v2) -> int:
        for i in range(3):
            self.p1[i] = int32(jround(v1[i] * 255.0))
            self.p2[i] = int32(jround(v2[i] * 255.0))

        for i in range(3):
            tmp = self.p1[i]
            for j in range(7, -1, -1):
                self.s1[i][7 - j] = tmp & 0x1
                tmp = iushr(tmp, 1)
            tmp = self.p2[i]
            for j in range(7, -1, -1):
                self.s2[i][7 - j] = tmp & 0x1
                tmp = iushr(tmp, 1)

        for i in range(8):
            for ch in range(3):
                a = self.s1[ch][7 - i]
                b = self.s2[ch][7 - i]
                if a > b:
                    return 1
                if a < b:
                    return -1
        return 0

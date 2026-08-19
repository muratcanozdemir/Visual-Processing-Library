"""Port of ``vpt.util.ordering.LexicoCIELAB``."""

from __future__ import annotations

from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering


class LexicoCIELAB(AlgebraicOrdering):
    """Lightness first, then chroma magnitude, then a and b individually."""

    def compare(self, v1, v2) -> int:
        tmp = Tools.cmpr(v1[0], v2[0])
        if tmp != 0:
            return tmp

        norm1 = v1[1] * v1[1] + v1[2] * v1[2]
        norm2 = v2[1] * v2[1] + v2[2] * v2[2]

        tmp = Tools.cmpr(norm1, norm2)
        if tmp != 0:
            return tmp
        tmp = Tools.cmpr(v1[1], v2[1])
        if tmp != 0:
            return tmp
        tmp = Tools.cmpr(v1[2], v2[2])
        if tmp != 0:
            return tmp
        return 0

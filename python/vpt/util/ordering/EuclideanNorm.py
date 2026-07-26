"""Port of ``vpt.util.ordering.EuclideanNorm``."""

from __future__ import annotations

from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering
from .Lexico import Lexico


class EuclideanNorm(AlgebraicOrdering):
    """Order by vector magnitude, breaking ties lexicographically."""

    def __init__(self):
        self.lex = Lexico()

    def compare(self, v1, v2) -> int:
        norm1 = Tools.EuclideanNorm(v1)
        norm2 = Tools.EuclideanNorm(v2)

        tmp = Tools.cmpr(norm1, norm2)
        if tmp != 0:
            return tmp
        return self.lex.compare(v1, v2)

"""Port of ``vpt.util.ordering.Lexico``."""

from __future__ import annotations

from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering


class Lexico(AlgebraicOrdering):
    """Lexicographic order: first differing channel decides."""

    def compare(self, v1, v2) -> int:
        for i in range(len(v1)):
            tmp = Tools.cmpr(v1[i], v2[i])
            if tmp != 0:
                return tmp
        return 0

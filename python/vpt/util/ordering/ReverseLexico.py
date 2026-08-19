"""Port of ``vpt.util.ordering.ReverseLexico``.

RECONSTRUCTED CLASS. Referenced by QuantaLexLSH but absent from the published
decompile — the JAR was built from a larger source tree than was released, so
there is no original to port. The Java oracle carries the same reconstruction:
Lexico with the channel priority reversed, which is the standard reverse
lexicographic vector ordering.
"""

from __future__ import annotations

from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering


class ReverseLexico(AlgebraicOrdering):
    def compare(self, v1, v2) -> int:
        for i in range(len(v1) - 1, -1, -1):
            tmp = Tools.cmpr(v1[i], v2[i])
            if tmp != 0:
                return tmp
        return 0

"""Port of ``vpt.util.ordering.SpectralAngular``."""

from __future__ import annotations

from ..Distance import Distance
from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering
from .Lexico import Lexico


class SpectralAngular(AlgebraicOrdering):
    """Order by spectral angle from a fixed reference, ties broken by Lexico.

    The reference is the zero vector, hard-coded to 4 components, and the
    behaviour depends on how many channels the caller has:

    - 4-channel input: the spectral angle to a zero vector is
      ``acos(0 / (norm * 0))`` = ``acos(NaN)`` = NaN. Tools.cmpr then sees
      NaN on both sides, all its comparisons are false, and it returns -1 --
      so ``compare`` returns -1 for every pair and the Lexico tie-break is
      unreachable.
    - any other channel count: Distance.spectralAngleDistance takes its
      length-mismatch branch, prints to stderr, and returns -1.0 for both
      operands, so cmpr returns 0 and the ordering *is* just Lexico.

    Both are preserved. This is why the port needs jdiv/jacos rather than
    Python's operators: the 4-channel path relies on IEEE NaN propagation,
    and Python would raise ZeroDivisionError instead.
    """

    def __init__(self):
        self.lex = Lexico()
        self.ref = [0.0, 0.0, 0.0, 0.0]

    def compare(self, v1, v2) -> int:
        norm1 = Distance.spectralAngleDistance(v1, self.ref)
        norm2 = Distance.spectralAngleDistance(v2, self.ref)

        tmp = Tools.cmpr(norm1, norm2)
        if tmp != 0:
            return tmp
        return self.lex.compare(v1, v2)

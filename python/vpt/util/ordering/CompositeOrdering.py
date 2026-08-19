"""Port of ``vpt.util.ordering.CompositeOrdering``."""

from __future__ import annotations

from .AlgebraicOrdering import AlgebraicOrdering


class CompositeOrdering(AlgebraicOrdering):
    """Majority vote over exactly three sub-orderings.

    Indexes ors[0..2] unconditionally, so a shorter array raises — as in the
    Java, which would throw ArrayIndexOutOfBoundsException.
    """

    def __init__(self, ors):
        self.ors = ors

    def compare(self, v1, v2) -> int:
        tmp0 = self.ors[0].compare(v1, v2)
        tmp1 = self.ors[1].compare(v1, v2)
        tmp2 = self.ors[2].compare(v1, v2)

        if tmp0 + tmp1 + tmp2 >= 2:
            return 1
        if tmp0 + tmp1 + tmp2 <= -2:
            return -1
        if tmp0 != 0:
            return tmp0
        if tmp1 != 0:
            return tmp1
        return tmp2

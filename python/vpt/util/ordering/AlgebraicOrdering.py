"""Port of ``vpt.util.ordering.AlgebraicOrdering``.

Base for orderings defined by a pairwise ``compare``. Java has this implement
both Ordering and Comparator<double[]>; here ``compare`` plays the Comparator
role directly and is adapted with ``functools.cmp_to_key`` where a sort key is
needed.
"""

from __future__ import annotations

from abc import abstractmethod
from functools import cmp_to_key

from .Ordering import Ordering


class AlgebraicOrdering(Ordering):
    @abstractmethod
    def compare(self, v1, v2) -> int:
        """Three-way compare of two vectors, as java.util.Comparator."""

    # ------------------------------------------------------------------
    def max(self, *args):
        """max(v1, v2) -> the greater, or max(vectors) -> the greatest."""
        if len(args) == 2:
            v1, v2 = args
            return v1 if self.compare(v1, v2) > 0 else v2
        v = args[0]
        mx = v[0]
        for i in range(1, len(v)):
            if self.compare(v[i], mx) > 0:
                mx = v[i]
        return mx

    def min(self, *args):
        """min(v1, v2) -> the lesser, or min(vectors) -> the least."""
        if len(args) == 2:
            v1, v2 = args
            return v2 if self.compare(v1, v2) > 0 else v1
        v = args[0]
        mn = v[0]
        for i in range(1, len(v)):
            if self.compare(v[i], mn) < 0:
                mn = v[i]
        return mn

    def rank(self, v, r: int):
        """The r-th vector in ascending order.

        Java calls Arrays.sort(v, this), which sorts the caller's array IN
        PLACE; callers that reuse `v` afterwards see it reordered. That side
        effect is preserved. Both languages use a stable TimSort, so vectors
        the comparator calls equal keep their relative order on either side.
        """
        order = sorted(range(len(v)), key=cmp_to_key(
            lambda i, j: self.compare(v[i], v[j])))
        rows = [v[i] for i in order]
        for i, row in enumerate(rows):
            v[i] = row
        return v[r]

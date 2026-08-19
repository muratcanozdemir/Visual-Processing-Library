"""Port of ``vpt.util.SortedList``.

A list that keeps itself ordered by inserting each element at the first
position where the comparator returns negative — an insertion sort spread
across the adds. Java subclasses ArrayList; here it subclasses ``list``, so
the inherited read API (indexing, iteration, ``len``) carries over the same
way.

The Java overrides the two-argument ``add(int, Object)`` with an empty body,
silently discarding positional inserts. That is preserved: ``insert`` is a
no-op.
"""

from __future__ import annotations


class SortedList(list):
    def __init__(self, comparator):
        """`comparator` is a two-argument function returning <0, 0 or >0,
        matching ``java.util.Comparator.compare``."""
        super().__init__()
        self.comparator = comparator

    def insert(self, index: int, element) -> None:
        """No-op, mirroring Java's empty ``add(int index, Object element)``."""

    def add(self, o) -> bool:
        i = 0
        found = False
        while not found and i < len(self):
            found = self.comparator(o, self[i]) < 0
            if not found:
                i += 1
        super().insert(i, o)
        return True

    def append(self, o) -> None:
        """Alias for :meth:`add`, so ordinary list usage stays sorted."""
        self.add(o)

    def size(self) -> int:
        return len(self)

    def get(self, i: int):
        return self[i]

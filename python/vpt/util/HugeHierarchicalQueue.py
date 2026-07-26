"""Port of ``vpt.util.HugeHierarchicalQueue``.

Sparse variant of :class:`~vpt.util.HierarchicalQueue.HierarchicalQueue`:
levels are allocated on first use and mapped through a dict, so the value
range may be far larger than the number of levels actually touched.

The Java uses a HashMap purely as a value lookup — it never iterates it — so
the difference between Java's and Python's hash ordering cannot affect
results here.
"""

from __future__ import annotations

from collections import deque


class HugeHierarchicalQueue:
    __slots__ = ("queue", "top", "map")

    def __init__(self, size: int):
        self.queue = [None] * size
        self.map = {}
        self.top = 0

    def add(self, p, val: int) -> None:
        listIndex = self.map.get(val)
        if listIndex is None:
            self.map[val] = self.top
            lst = deque()
            lst.append(p)
            self.queue[self.top] = lst
            self.top += 1
        else:
            self.queue[listIndex].append(p)

    def get(self, val: int):
        listIndex = self.map.get(val)
        if listIndex is None:
            return None
        if self.queue[listIndex] is None:
            return None
        return self.queue[listIndex].popleft()

    def isEmpty(self, val: int) -> bool:
        listIndex = self.map.get(val)
        if listIndex is None:
            return True
        return not self.queue[listIndex]

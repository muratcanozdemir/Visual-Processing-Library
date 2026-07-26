"""Port of ``vpt.util.HierarchicalQueue``.

An array of FIFO queues indexed by priority level, used by the watershed and
max-tree algorithms. Allocates every level up front, so ``size`` must cover
the full value range; :class:`~vpt.util.HugeHierarchicalQueue.HugeHierarchicalQueue`
is the sparse alternative for wide ranges.
"""

from __future__ import annotations

from collections import deque


class HierarchicalQueue:
    __slots__ = ("queue",)

    def __init__(self, size: int):
        self.queue = [deque() for _ in range(size)]

    def add(self, p, val: int) -> None:
        self.queue[val].append(p)

    def get(self, val: int):
        """Remove and return the head of level `val`.

        Raises IndexError on an empty level, matching Java's
        ``LinkedList.remove()`` throwing NoSuchElementException.
        """
        return self.queue[val].popleft()

    def isEmpty(self, val: int) -> bool:
        return not self.queue[val]

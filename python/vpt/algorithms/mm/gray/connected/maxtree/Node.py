"""Port of ``vpt.algorithms.mm.gray.connected.maxtree.Node``.

A max-tree node: a grey level, its parent/children in the tree, and the
pixels that flat-zone at that level owns (after canonicalisation -- see
``MaxTree``, which merges same-level parent/child pairs).
"""

from __future__ import annotations

from ......_javacompat import Point


class Node:
    __slots__ = ("level", "parent", "children", "pixels")

    def __init__(self, val: int):
        self.level = val
        self.parent: Node | None = None
        self.children: list[Node] = []
        self.pixels: list[Point] = []

    def newInstance(self) -> "Node":
        n = Node(self.level)
        for m in self.children:
            n.children.append(m.newInstance())
        for m in self.pixels:
            n.pixels.append(Point(m.x, m.y))
        return n

"""Port of ``vpt.algorithms.mm.multi.connected.maxtree.MaxTreeNode``: the
vectorial analogue of ``mm.gray.connected.maxtree.Node``. ``level`` here is
not a raw pixel value but a rank index into ``MultivariateMaxTree``'s
sorted-unique-vectors table -- see that module's docstring."""

from __future__ import annotations

from .MyList import MyList


class MaxTreeNode:
    __slots__ = ("children", "level", "parent", "pixels")

    def __init__(self, val: int):
        self.level = val
        self.parent: MaxTreeNode | None = None
        self.children: MyList = MyList()
        self.pixels: MyList = MyList()

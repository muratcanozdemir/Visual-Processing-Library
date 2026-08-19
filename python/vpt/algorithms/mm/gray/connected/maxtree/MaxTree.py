"""Port of ``vpt.algorithms.mm.gray.connected.maxtree.MaxTree``.

Salembier's flooding algorithm for building a max-tree over a grayscale
image: a hierarchical queue processes pixels in increasing grey-level order,
recursively flooding into any newly-discovered higher level before
continuing the current one, so that by the time a level finishes every pixel
reachable from it at a higher level has already been assigned a component
root. ``parent`` (an ``IntegerImage``) is a union-find over flat pixel
indices (``y*xdim+x``) built during flooding; the second pass below turns
that into the actual ``Node`` tree, and the two passes after canonicalise it
by merging any parent/child pair that ended up at the same level (a node and
its child can't have equal level after canonicalisation, by construction of
a max-tree, so this collapses the redundant duplicate the flood pass leaves
behind).

Recursion depth is bounded by the number of distinct grey levels between
the current level and the next discovered one (at most 256, one per byte
value), not by pixel count, so this does not risk Python's recursion limit
the way a naive per-pixel-recursive flood fill would.
"""

from __future__ import annotations

import traceback

from ......_javacompat import Point, idiv, imod
from ......Algorithm import Algorithm
from ......ByteImage import ByteImage
from ......GlobalException import GlobalException
from ......Image import Image
from ......IntegerImage import IntegerImage
from ......util.HierarchicalQueue import HierarchicalQueue
from ......util.Tools import Tools
from .Node import Node

NOT_ANALYZED = -1
IN_THE_QUEUE = -2
GRAY_LEVELS = 256


class MaxTree(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.root: Node | None = None
        self.inputFields = "input"
        self.outputFields = "root"

        self._queue: HierarchicalQueue | None = None
        self._nodes: list[list[Node | None]] | None = None
        self._S: list[Point] = []
        self._levroot: list[Point | None] = []
        self._parent: Image | None = None
        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Only grayscale images for now")
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self._levroot = [None] * GRAY_LEVELS

        self._parent = IntegerImage(self.input, False)
        self._parent.fill(-1)

        self._S = []

        self._nodes = [[None] * self.xdim for _ in range(self.ydim)]

        self._queue = HierarchicalQueue(GRAY_LEVELS)

        pMin = self.input.getXYCMinimum(0)

        lMin = self.input.getXYByte(pMin.x, pMin.y)
        self._queue.add(pMin, lMin)

        self._levroot[lMin] = pMin
        if self._flood(lMin, pMin) != -1:
            raise GlobalException("Flooding did not terminate with -1..something is wrong :(")

        for y in range(self.ydim):
            for x in range(self.xdim):
                p = self._parent.getXYInt(x, y)

                _x = imod(p, self.xdim)
                _y = idiv(p, self.xdim)

                n = self._nodes[y][x]

                n.pixels.append(Point(x, y))
                if y != _y or x != _x:
                    n.parent = self._nodes[_y][_x]
                    if n.level > self._nodes[_y][_x].level:
                        self._nodes[_y][_x].children.append(n)

        last = self._S[-1]
        self._nodes[last.y][last.x].parent = None
        self.root = self._nodes[last.y][last.x]

        for i in range(len(self._S) - 1, -1, -1):
            r = self._S[i]

            p = self._nodes[r.y][r.x]
            q = p.parent
            if q is not None and q.level == p.level:
                q.pixels.extend(p.pixels)
                q.children.extend(p.children)

                self._nodes[r.y][r.x] = None

        for i in range(len(self._S) - 1):
            r = self._S[i]

            p = self._nodes[r.y][r.x]
            if p is not None:
                q = p.parent
                q.pixels.extend(p.pixels)

    def _flood(self, lambda_: int, r: Point) -> int:
        while not self._queue.isEmpty(lambda_):
            p = self._queue.get(lambda_)

            self._parent.setXYInt(p.x, p.y, r.y * self.xdim + r.x)
            if self._nodes[p.y][p.x] is None:
                self._nodes[p.y][p.x] = Node(self.input.getXYByte(p.x, p.y))

            if p != r:
                self._S.append(p)

            for n in Tools.N:
                _x = p.x + n.x
                _y = p.y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    if self._parent.getXYInt(_x, _y) == -1:
                        pt = Point(_x, _y)

                        l = self.input.getXYByte(pt.x, pt.y)
                        if self._levroot[l] is None:
                            self._levroot[l] = pt
                        self._queue.add(pt, l)

                        self._parent.setXYInt(pt.x, pt.y, -2)
                        while l > lambda_:
                            l = self._flood(l, self._levroot[l])

        self._levroot[lambda_] = None

        lpar = lambda_ - 1
        while lpar >= 0 and self._levroot[lpar] is None:
            lpar -= 1

        if lpar != -1:
            self._parent.setXYInt(
                r.x, r.y, self._levroot[lpar].y * self.xdim + self._levroot[lpar].x
            )

        self._S.append(r)
        if self._nodes[r.y][r.x] is None:
            self._nodes[r.y][r.x] = Node(self.input.getXYByte(r.x, r.y))

        return lpar

    # ------------------------------------------------------------------
    @staticmethod
    def printTree(root: Node | None) -> None:
        import sys

        if root is not None:
            print(f"I am pixel {root.level}", file=sys.stderr)
            print(f"I have {len(root.children)} children", file=sys.stderr)
            print(f"I have {len(root.pixels)} pixels", file=sys.stderr)
            for childNode in root.children:
                MaxTree.printTree(childNode)

    @staticmethod
    def tree2Image(root: Node, xdim: int, ydim: int) -> Image:
        img = ByteImage(xdim, ydim, 1)
        img.fill(0)
        MaxTree._fillImage(img, root)
        return img

    @staticmethod
    def _fillImage(img: Image, node: Node | None) -> None:
        if node is not None:
            for p in node.pixels:
                img.setXYByte(p.x, p.y, node.level)
            for childNode in node.children:
                MaxTree._fillImage(img, childNode)

    @staticmethod
    def invoke(image: Image) -> Node | None:
        try:
            return MaxTree().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

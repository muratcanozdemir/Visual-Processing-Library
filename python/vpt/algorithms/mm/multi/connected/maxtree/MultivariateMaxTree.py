"""Port of ``vpt.algorithms.mm.multi.connected.maxtree.MultivariateMaxTree``.

The vectorial analogue of ``mm.gray.connected.maxtree.MaxTree``: same
Salembier flooding algorithm over a ``HierarchicalQueue``, but a pixel's
"level" can't be a raw value the way a grayscale byte is, since there's no
canonical total order on colour vectors. Instead, every distinct pixel
vector (compared for equality via ``Tools.cmpr``, and totally ordered via
the caller-supplied ``AlgebraicOrdering``) gets a *rank*: pixels are sorted
once by ``vo``, duplicates collapse to one entry each, and each
``MaxTreeNode.level`` is an index into that rank sequence rather than a
value. ``tree2Image`` reverses the mapping (``ranks[actualRanksArray[level]]
.val``) to recover the actual vector when rebuilding an image.

HAZARD, faithfully preserved: ``ranks`` and ``actualRanksArray`` are Java
``public static`` fields on the class, not instance state -- ``tree2Image``
is a separate static method that reads them after the fact rather than
having them passed in with the tree. Ported as Python *class* attributes for
the same reason: building a second tree before calling ``tree2Image`` on an
earlier one's root silently rebinds these tables out from under it. Nothing
in this package does that (every ``MMaxTree*`` caller builds one tree,
processes it, then immediately calls ``tree2Image`` before building
another), but it is not guarded against, matching the Java.

Two behavioural divergences from the scalar ``MaxTree``, both original-
library decisions rather than porting choices: pixel/child lists are built
with ``addFirst`` (prepend) here where the scalar version uses ``add``
(append) -- affecting iteration order but not any order-independent
attribute predicate in this package -- and the ``HierarchicalQueue`` is
sized to the number of *distinct* vectors rather than a fixed 256 gray
levels, since there's no fixed vector-value range to size it to.

The private ``Pixel`` inner class becomes a plain module-level dataclass-
like object plus a ``functools.cmp_to_key``-wrapped sort, since Python has
no direct analogue of a non-static inner class closing over the enclosing
``MultivariateMaxTree`` instance's ``vo`` -- the closure is expressed
explicitly by capturing ``self`` in a local comparison function instead.
"""

from __future__ import annotations

import traceback
from functools import cmp_to_key

from ......_javacompat import Point, idiv, imod
from ......Algorithm import Algorithm
from ......ByteImage import ByteImage
from ......GlobalException import GlobalException
from ......Image import Image
from ......IntegerImage import IntegerImage
from ......util.HierarchicalQueue import HierarchicalQueue
from ......util.ordering import AlgebraicOrdering
from ......util.Tools import Tools
from .MaxTreeNode import MaxTreeNode

NOT_ANALYZED = -1
IN_THE_QUEUE = -2


class _Pixel:
    __slots__ = ("val", "x", "y")

    def __init__(self, x: int, y: int, val):
        self.x = x
        self.y = y
        self.val = val

    def __eq__(self, other: object) -> bool:
        equal = True
        for i in range(len(self.val)):
            if Tools.cmpr(other.val[i], self.val[i]) != 0:
                equal = False
        return equal


class MultivariateMaxTree(Algorithm):
    ranks: list[_Pixel] | None = None
    actualRanksArray: list[int] | None = None

    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.root: MaxTreeNode | None = None
        self.vo: AlgebraicOrdering | None = None
        self.inputFields = "input, vo"
        self.outputFields = "root"

        self._queue: HierarchicalQueue | None = None
        self._nodes: list[list[MaxTreeNode | None]] | None = None
        self._S: list[Point] = []
        self._levroot: list[Point | None] = []
        self._parent: Image | None = None
        self.xdim = 0
        self.ydim = 0
        self._rankImage: IntegerImage | None = None
        self._actualRanks: list[int] = []

    def execute(self) -> None:
        if self.input.getCDim() < 2:
            raise GlobalException("It needs at an image with at least two bands")
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self._rankImage = IntegerImage(self.xdim, self.ydim, 1)

        ranks = [None] * (self.xdim * self.ydim)
        for y in range(self.ydim):
            for x in range(self.xdim):
                ranks[y * self.xdim + x] = _Pixel(x, y, self.input.getVXYDouble(x, y))
        ranks.sort(key=cmp_to_key(lambda a, b: self.vo.compare(a.val, b.val)))
        MultivariateMaxTree.ranks = ranks

        lastSeenVector = None

        self._actualRanks = []
        for i in range(len(ranks)):
            r = ranks[i]
            if lastSeenVector is None or r != lastSeenVector:
                lastSeenVector = r
                self._actualRanks.append(i)

            self._rankImage.setXYInt(r.x, r.y, len(self._actualRanks) - 1)

        actualRanksArray = list(self._actualRanks)
        MultivariateMaxTree.actualRanksArray = actualRanksArray

        self._levroot = [None] * len(actualRanksArray)

        self._parent = IntegerImage(self.xdim, self.ydim, 1)
        self._parent.fill(-1)

        self._S = []

        self._nodes = [[None] * self.xdim for _ in range(self.ydim)]

        self._queue = HierarchicalQueue(len(actualRanksArray))

        pMin = self._rankImage.getXYCMinimum(0)

        lMin = self._rankImage.getXYInt(pMin.x, pMin.y)
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

                n.pixels.addFirst(Point(x, y))
                if y != _y or x != _x:
                    n.parent = self._nodes[_y][_x]
                    if n.level > self._nodes[_y][_x].level:
                        self._nodes[_y][_x].children.addFirst(n)

        last = self._S[-1]
        self._nodes[last.y][last.x].parent = None
        self.root = self._nodes[last.y][last.x]

        for i in range(len(self._S) - 1, -1, -1):
            r = self._S[i]

            p = self._nodes[r.y][r.x]
            q = p.parent
            if q is not None and q.level == p.level:
                q.pixels.addAll(p.pixels)
                q.children.addAll(p.children)

                self._nodes[r.y][r.x] = None

    def _flood(self, lambda_: int, r: Point) -> int:
        while not self._queue.isEmpty(lambda_):
            p = self._queue.get(lambda_)

            self._parent.setXYInt(p.x, p.y, r.y * self.xdim + r.x)
            if self._nodes[p.y][p.x] is None:
                self._nodes[p.y][p.x] = MaxTreeNode(self._rankImage.getXYInt(p.x, p.y))

            if p != r:
                self._S.append(p)

            for n in Tools.N:
                _x = p.x + n.x
                _y = p.y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    if self._parent.getXYInt(_x, _y) == -1:
                        pt = Point(_x, _y)

                        l = self._rankImage.getXYInt(pt.x, pt.y)
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
            self._nodes[r.y][r.x] = MaxTreeNode(self._rankImage.getXYInt(r.x, r.y))

        return lpar

    # ------------------------------------------------------------------
    @staticmethod
    def tree2Image(root: MaxTreeNode, xdim: int, ydim: int, cdim: int) -> Image:
        img = ByteImage(xdim, ydim, cdim)
        img.fill(0)

        st = [root]

        MultivariateMaxTree._fillImage(img, st)

        return img

    @staticmethod
    def _fillImage(img: Image, st: list) -> None:
        ranks = MultivariateMaxTree.ranks
        actualRanksArray = MultivariateMaxTree.actualRanksArray
        while st:
            node = st.pop()
            n = node.pixels.getHead()
            while n is not None:
                p = n.datum
                img.setVXYDouble(p.x, p.y, ranks[actualRanksArray[node.level]].val)
                n = n.next

            n = node.children.getHead()
            while n is not None:
                st.append(n.datum)
                n = n.next

    @staticmethod
    def invoke(image: Image, vo: AlgebraicOrdering) -> MaxTreeNode | None:
        try:
            return MultivariateMaxTree().preprocess(image, vo)
        except GlobalException:
            traceback.print_exc()
        return None

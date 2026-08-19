"""Port of ``vpt.algorithms.mm.multi.connected.MMaxTreeIncrAttrOpening``.

The vectorial analogue of ``mm.gray.connected.GMaxTreeIncrAttrOpening``, but
structured differently in the Java: rather than a top-down recursive prune,
it does an explicit DFS (via ``stack``) to linearize the tree into
``queue``, then walks ``queue`` back-to-front so every node is visited
after all of its descendants (a manual post-order), pruning a node into its
parent's pixel list when it fails ``crit``. For an *increasing* predicate
(the type this class is for) both orderings prune the same nodes, since a
failing node's descendants fail too either way.

Java's ``LinkedList.add``/``removeLast`` both operate on the list's tail, so
``queue`` here is really a second stack -- ported as a plain Python list
with ``append``/``pop()``, which is exactly that.

Removal (``n.parent.children.remove(n)``) happens only after the DFS
collection phase is completely finished, never while something is actively
iterating ``n.parent.children`` -- so this does not depend on ``MyList``'s
survives-removal-mid-iteration property the way some hypothetical
interleaved traversal might; see ``maxtree/MyList.py`` for that mechanism
anyway, since it's relied on elsewhere in this family.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .attribute.MultiAttribute import MultiAttribute
from .maxtree.MaxTreeNode import MaxTreeNode
from .maxtree.MultivariateMaxTree import MultivariateMaxTree


class MMaxTreeIncrAttrOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: MultiAttribute | None = None
        self.vo: AlgebraicOrdering | None = None
        self.output: Image | None = None
        self.inputFields = "input, crit, vo"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MultivariateMaxTree.invoke(self.input, self.vo)
        root = self._processNode(root)
        self.output = MultivariateMaxTree.tree2Image(
            root, self.input.getXDim(), self.input.getYDim(), self.input.getCDim()
        )

    def _processNode(self, root: MaxTreeNode) -> MaxTreeNode | None:
        queue: list[MaxTreeNode] = []
        stack: list[MaxTreeNode] = [root]

        while stack:
            n = stack.pop()

            queue.append(n)
            if n.children.size() > 0:
                tmp = n.children.getHead()
                while tmp is not None:
                    child = tmp.getDatum()
                    stack.append(child)
                    tmp = tmp.getNext()

        while queue:
            n = queue.pop()
            if n.children.size() == 0:
                if not self.crit.test(n.pixels, self.input):
                    if n.parent is None:
                        return None

                    n.parent.pixels.addAll(n.pixels)

                    n.parent.children.remove(n)

        return root

    @staticmethod
    def invoke(image: Image, c: MultiAttribute, vo: AlgebraicOrdering) -> Image | None:
        try:
            return MMaxTreeIncrAttrOpening().preprocess(image, c, vo)
        except GlobalException:
            traceback.print_exc()
        return None

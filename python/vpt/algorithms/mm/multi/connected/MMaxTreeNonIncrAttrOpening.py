"""Port of ``vpt.algorithms.mm.multi.connected.MMaxTreeNonIncrAttrOpening``.

The vectorial analogue of ``mm.gray.connected.GMaxTreeNonIncrAttrOpening``,
but the Java here only actually implements one of the four declared
filtering strategies -- transcribed exactly, not filled in or "fixed":

- ``DIRECT`` (0): prints "Not yet supported" to stderr and falls straight
  through to ``tree2Image`` with the tree unmodified -- a documented no-op,
  not a bug.
- ``SUBTRACTIVE`` (1): falls into the same ``else`` branch as any other
  unrecognised value and **raises** ``GlobalException("Invalid filtering
  strategy")``. This is a real behavioural asymmetry with ``MIN`` below,
  not a decompiler artifact -- the Java is a plain ``if``/``else if`` chain,
  nothing about its shape suggests JD-Core mangling.
- ``MIN`` (2): the ``elif filteringStrategy != MIN`` guard means this value
  skips the whole chain silently -- also an undocumented no-op, but *without*
  ``DIRECT``'s stderr message, and without raising the way ``SUBTRACTIVE``
  does despite being equally unimplemented.
- ``MAX`` (3, the default): the only strategy that actually prunes, via
  ``_processByMaxRule`` -- a manual post-order DFS identical in shape to
  ``MMaxTreeIncrAttrOpening``'s, differing only in that a *non*-increasing
  predicate means a node is evaluated (and possibly pruned into its parent)
  only once all its children have already been resolved, since a failing
  child doesn't imply its parent would fail too.

As in ``MMaxTreeIncrAttrOpening``, ``n.parent.children.remove(n)`` only ever
runs after the DFS-collection phase into ``queue`` is fully finished, so
this doesn't lean on ``MyList``'s survives-removal-mid-iteration guarantee
either -- see ``maxtree/MyList.py``.
"""

from __future__ import annotations

import sys
import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering
from .attribute.MultiAttribute import MultiAttribute
from .maxtree.MaxTreeNode import MaxTreeNode
from .maxtree.MultivariateMaxTree import MultivariateMaxTree

DIRECT = 0
SUBTRACTIVE = 1
MIN = 2
MAX = 3


class MMaxTreeNonIncrAttrOpening(Algorithm):
    DIRECT = DIRECT
    SUBTRACTIVE = SUBTRACTIVE
    MIN = MIN
    MAX = MAX

    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: MultiAttribute | None = None
        self.vo: AlgebraicOrdering | None = None
        self.output: Image | None = None
        self.filteringStrategy: int | None = None
        self.inputFields = "input, crit, vo, filteringStrategy"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MultivariateMaxTree.invoke(self.input, self.vo)
        if root is None:
            raise GlobalException("Tree is null..WTF")

        if self.filteringStrategy is None:
            self.filteringStrategy = MAX

        if self.filteringStrategy == DIRECT:
            print("Not yet supported", file=sys.stderr)
        elif self.filteringStrategy != MIN:
            if self.filteringStrategy == MAX:
                root = self._processByMaxRule(root)
            else:
                raise GlobalException("Invalid filtering strategy")

        self.output = MultivariateMaxTree.tree2Image(
            root, self.input.getXDim(), self.input.getYDim(), self.input.getCDim()
        )

    def _processByMaxRule(self, root: MaxTreeNode) -> MaxTreeNode | None:
        queue: list[MaxTreeNode] = []
        stack: list[MaxTreeNode] = [root]

        while stack:
            n = stack.pop()

            queue.append(n)
            if n.children.size() > 0:
                size = n.children.size()
                tmp = n.children.getHead()
                for _ in range(size):
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
    def invoke(
        image: Image,
        c: MultiAttribute,
        vo: AlgebraicOrdering,
        filteringStrategy: int | None = None,
    ) -> Image | None:
        try:
            return MMaxTreeNonIncrAttrOpening().preprocess(image, c, vo, filteringStrategy)
        except GlobalException:
            traceback.print_exc()
        return None

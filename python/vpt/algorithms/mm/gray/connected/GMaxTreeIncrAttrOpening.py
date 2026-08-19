"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeIncrAttrOpening``.

Attribute opening via max-tree pruning, for an *increasing* (monotone)
attribute predicate: build the max-tree, and top-down, prune any child
subtree whose root zone fails ``crit`` -- an increasing predicate
guarantees a failing node's descendants would fail too, so pruning the
whole subtree (rather than checking each descendant) is safe. If the root
itself fails, the whole image collapses (``output = None``).

The Java prunes via ``Iterator.remove()`` while walking ``node.children``;
here that's a filtered-list rebuild (``node.children = [n for n in
node.children if ...]``), which is the natural Python equivalent -- the
predicate is a pure read (no iteration-order-dependent side effects), so
this is behaviourally identical.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .attribute.Attribute import Attribute
from .maxtree.MaxTree import MaxTree
from .maxtree.Node import Node


class GMaxTreeIncrAttrOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.inputFields = "input, crit"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MaxTree.invoke(self.input)
        if not self.crit.test(root.pixels, self.input):
            self.output = None
            return

        self._processNode(root)

        self.output = MaxTree.tree2Image(root, self.input.getXDim(), self.input.getYDim())

    def _processNode(self, node: Node) -> None:
        node.children = [n for n in node.children if self.crit.test(n.pixels, self.input)]
        for n in node.children:
            self._processNode(n)

    @staticmethod
    def invoke(image: Image, c: Attribute) -> Image | None:
        try:
            return GMaxTreeIncrAttrOpening().preprocess(image, c)
        except GlobalException:
            traceback.print_exc()
        return None

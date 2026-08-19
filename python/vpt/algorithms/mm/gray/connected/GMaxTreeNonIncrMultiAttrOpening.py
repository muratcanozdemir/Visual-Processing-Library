"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrMultiAttrOpening``.

The multichannel analogue of ``GMaxTreeNonIncrAttrOpening``, but only the
``MAX`` (post-order cascading) strategy is implemented here -- passing any
other ``filteringStrategy`` (there is no ``DIRECT``/``SUBTRACTIVE``/``MIN``
branch at all in the Java) raises "Invalid filtering strategy". See
``GMaxTreeNonIncrAttrOpening`` for the ``_maxRule`` mechanism and why
returning ``None`` (filtered by the caller) is the correct Python
equivalent of the Java's iterator-based removal.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .attribute.Attribute import Attribute
from .maxtree.MaxTree import MaxTree
from .maxtree.Node import Node

MAX = 3


class GMaxTreeNonIncrMultiAttrOpening(Algorithm):
    MAX = MAX

    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.channel: int | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.filteringStrategy: int | None = None
        self.inputFields = "input, channel, crit, filteringStrategy"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MaxTree.invoke(self.input.getChannel(self.channel))
        if root is None:
            raise GlobalException("Tree is null..WTF")

        if self.filteringStrategy is None:
            self.filteringStrategy = MAX

        if self.filteringStrategy == MAX:
            root = self._maxRule(root)
        else:
            raise GlobalException("Invalid filtering strategy")

        self.output = MaxTree.tree2Image(root, self.input.getXDim(), self.input.getYDim())

    def _maxRule(self, node: Node) -> Node | None:
        if node.children:
            node.children = [
                kept
                for kept in (self._maxRule(child) for child in node.children)
                if kept is not None
            ]
        if not node.children:
            if not self.crit.test(node.pixels, self.input):
                return None
        return node

    @staticmethod
    def invoke(image: Image, channel: int, c: Attribute) -> Image | None:
        try:
            return GMaxTreeNonIncrMultiAttrOpening().preprocess(image, channel, c, None)
        except GlobalException:
            traceback.print_exc()
        return None

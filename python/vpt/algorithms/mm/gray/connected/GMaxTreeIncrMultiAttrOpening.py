"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeIncrMultiAttrOpening``:
``GMaxTreeIncrAttrOpening``, but the max-tree is built over one selected
``channel`` of a multichannel ``input`` while the ``crit`` predicate is still
tested against the full multichannel ``input`` (so attributes like
``SphereRadius``/``MultiSdev`` that read every channel still see all of
them, even though the tree topology comes from a single channel)."""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .attribute.Attribute import Attribute
from .maxtree.MaxTree import MaxTree
from .maxtree.Node import Node


class GMaxTreeIncrMultiAttrOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.channel: int | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.inputFields = "input, channel, crit"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MaxTree.invoke(self.input.getChannel(self.channel))
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
    def invoke(image: Image, channel: int, c: Attribute) -> Image | None:
        try:
            return GMaxTreeIncrMultiAttrOpening().preprocess(image, channel, c)
        except GlobalException:
            traceback.print_exc()
        return None

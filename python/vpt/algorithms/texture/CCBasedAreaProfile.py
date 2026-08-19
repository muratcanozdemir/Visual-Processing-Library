"""Port of ``vpt.algorithms.texture.CCBasedAreaProfile``: a max-tree-node
area-ratio histogram. Builds the max-tree, then for every node computes the
ratio of its own flat-zone size to its parent's (``1.0`` for the root,
which has no parent), buckets ``ceil(ratio * size)`` clamped to
``[1, size]`` into a histogram of ``size`` bins, and normalises by the
total node count at the end."""

from __future__ import annotations

import math
import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..mm.gray.connected.maxtree.MaxTree import MaxTree
from ..mm.gray.connected.maxtree.Node import Node


class CCBasedAreaProfile(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.size: int | None = None
        self.output: list[float] | None = None
        self._numberOfNodes: int = 0
        self.inputFields = "input, size"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Only Grayscale Images for now...")

        self.output = [0.0] * self.size

        self._numberOfNodes = 0

        root = MaxTree.invoke(self.input)

        self._processNode(root)
        for i in range(len(self.output)):
            self.output[i] /= self._numberOfNodes

    def _processNode(self, node: Node) -> None:
        area = float(len(node.pixels))
        ratio = 1.0
        if node.parent is not None:
            ratio = area / len(node.parent.pixels)

        index = math.ceil(ratio * self.size)
        if index > self.size:
            index = self.size
        if index < 1:
            index = 1
        self.output[index - 1] += 1.0
        self._numberOfNodes += 1
        for n in node.children:
            self._processNode(n)

    @staticmethod
    def invoke(image: Image, size: int) -> list[float] | None:
        try:
            return CCBasedAreaProfile().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

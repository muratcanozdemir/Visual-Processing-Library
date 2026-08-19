"""Port of ``vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrOpening``.

Attribute opening via max-tree pruning for a *non-increasing* (non-monotone)
predicate, where -- unlike ``GMaxTreeIncrAttrOpening`` -- a failing node's
descendants might still pass, so whole-subtree pruning isn't safe. Three
distinct restructuring strategies (a fourth, ``SUBTRACTIVE``, is declared as
a constant but not implemented -- passing it raises "Invalid filtering
strategy", preserved as found; not a porting bug):

- ``DIRECT``: drop a failing node but reparent its children onto its own
  parent, splicing it out of the tree rather than removing its subtree.
- ``MIN``: drop a failing node and its entire subtree (same shape as
  ``GMaxTreeIncrAttrOpening``, just applied without the increasing-predicate
  justification).
- ``MAX`` (the default): post-order leaf pruning that cascades upward -- a
  leaf is dropped if it fails; an internal node is then evaluated the same
  way once *all* of its children have been pruned away, so failure
  propagates up through chains of now-childless ancestors.

The Java implements node removal via ``Iterator.remove()`` called on a
*parent's* children iterator, passed down into the recursive call on the
child -- a child can delete itself from its parent's list mid-traversal.
Python has no equivalent mid-iteration removal-by-reference, so ``_maxRule``
returns ``None`` instead of the pruned node and the caller filters its
children list on that; this is behaviourally identical because the Java
return value is discarded by every non-root caller anyway (only the
top-level call assigns it), so the removal mechanism (iterator vs return
value) is a plumbing detail, not an observable difference.

**Known divergence** on ``_directNode`` (``DIRECT`` strategy), confirmed
against the JDK 21 oracle: the Java calls ``node.children.addAll(n.children)``
on the *same* ``ArrayList`` an ``Iterator`` is actively walking, without going
through that iterator -- for a failing node ``n`` that itself has children,
this trips ``ArrayList``'s fail-fast check and throws
``ConcurrentModificationException`` on the ``Iterator``'s next call (only
``Iterator.remove()``/``Iterator.add()`` are safe to call on the same list
mid-traversal). Verified directly: ``oracle/DumpConnected.java``'s
``GMaxTreeNonIncrAttrOpening(g,Area5,DIRECT)`` call throws exactly this,
for *any* tree with internal structure -- not a rare edge case, the ordinary
case for a real image. This port implements the clearly-*intended* semantics
instead -- splice a failing node's children into its parent in one pass,
without re-testing the newly-spliced grandchildren in that same pass --
rather than reproduce a crash. See ``python/README.md``'s "Known divergence"
section; ``test_connected_golden.py`` special-cases this row accordingly.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .attribute.Attribute import Attribute
from .maxtree.MaxTree import MaxTree
from .maxtree.Node import Node

DIRECT = 0
SUBTRACTIVE = 1
MIN = 2
MAX = 3


class GMaxTreeNonIncrAttrOpening(Algorithm):
    DIRECT = DIRECT
    SUBTRACTIVE = SUBTRACTIVE
    MIN = MIN
    MAX = MAX

    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.crit: Attribute | None = None
        self.output: Image | None = None
        self.filteringStrategy: int | None = None
        self.inputFields = "input, crit, filteringStrategy"
        self.outputFields = "output"

    def execute(self) -> None:
        root = MaxTree.invoke(self.input)
        if root is None:
            raise GlobalException("Tree is null..WTF")

        if self.filteringStrategy is None:
            self.filteringStrategy = MAX

        if self.filteringStrategy == DIRECT:
            if not self.crit.test(root.pixels, self.input):
                root = None
            else:
                self._directNode(root)
        elif self.filteringStrategy == MIN:
            if not self.crit.test(root.pixels, self.input):
                root = None
            else:
                self._minRuleNode(root)
        elif self.filteringStrategy == MAX:
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

    def _minRuleNode(self, node: Node) -> None:
        node.children = [n for n in node.children if self.crit.test(n.pixels, self.input)]
        for n in node.children:
            self._minRuleNode(n)

    def _directNode(self, node: Node) -> None:
        new_children = []
        for n in node.children:
            if not self.crit.test(n.pixels, self.input):
                new_children.extend(n.children)
                for m in n.children:
                    m.parent = node
            else:
                new_children.append(n)
        node.children = new_children
        for n in node.children:
            self._directNode(n)

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, c: Attribute, filteringStrategy: int | None = None) -> Image | None:
        try:
            return GMaxTreeNonIncrAttrOpening().preprocess(image, c, filteringStrategy)
        except GlobalException:
            traceback.print_exc()
        return None

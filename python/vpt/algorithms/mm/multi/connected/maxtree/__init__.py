"""Port of the ``vpt.algorithms.mm.multi.connected.maxtree`` package --
``MaxTreeNode``, ``MyList``/``MyListNode``/``MyListIterator``, and
``MultivariateMaxTree``. ``MultivariateMaxTreeEntireGamut`` is not ported:
it is unreferenced dead code in the Java source (no caller anywhere in
``src/``) that pre-sorts and binary-searches a 256^3-entry RGB gamut table
via the caller's ``AlgebraicOrdering`` -- porting and exercising it would
mean sorting 16.7M Python objects with a Python-level comparator, which is
both prohibitively slow to even smoke-test here and not something any
``MMaxTree*`` file in this port's scope depends on."""

from .MaxTreeNode import MaxTreeNode
from .MultivariateMaxTree import MultivariateMaxTree
from .MyList import MyList
from .MyListIterator import MyListIterator
from .MyListNode import MyListNode

__all__ = [
    "MaxTreeNode",
    "MultivariateMaxTree",
    "MyList",
    "MyListIterator",
    "MyListNode",
]

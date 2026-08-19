"""Port of the ``vpt.algorithms.mm.gray.connected`` package -- complete
(10/10 top-level files; see also the ``attribute`` and ``maxtree``
subpackages)."""

from .GAreaClosing import GAreaClosing
from .GAreaOpening import GAreaOpening
from .GMaxTreeIncrAttrClosing import GMaxTreeIncrAttrClosing
from .GMaxTreeIncrAttrOpening import GMaxTreeIncrAttrOpening
from .GMaxTreeIncrMultiAttrClosing import GMaxTreeIncrMultiAttrClosing
from .GMaxTreeIncrMultiAttrOpening import GMaxTreeIncrMultiAttrOpening
from .GMaxTreeNonIncrAttrClosing import GMaxTreeNonIncrAttrClosing
from .GMaxTreeNonIncrAttrOpening import GMaxTreeNonIncrAttrOpening
from .GMaxTreeNonIncrMultiAttrClosing import GMaxTreeNonIncrMultiAttrClosing
from .GMaxTreeNonIncrMultiAttrOpening import GMaxTreeNonIncrMultiAttrOpening

__all__ = [
    "GAreaClosing",
    "GAreaOpening",
    "GMaxTreeIncrAttrClosing",
    "GMaxTreeIncrAttrOpening",
    "GMaxTreeIncrMultiAttrClosing",
    "GMaxTreeIncrMultiAttrOpening",
    "GMaxTreeNonIncrAttrClosing",
    "GMaxTreeNonIncrAttrOpening",
    "GMaxTreeNonIncrMultiAttrClosing",
    "GMaxTreeNonIncrMultiAttrOpening",
]

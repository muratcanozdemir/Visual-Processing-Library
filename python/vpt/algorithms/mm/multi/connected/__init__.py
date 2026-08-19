"""Port of the ``vpt.algorithms.mm.multi.connected`` package -- top-level
complete (4/4; see also the ``attribute`` and ``maxtree`` subpackages)."""

from .MMaxTreeIncrAttrClosing import MMaxTreeIncrAttrClosing
from .MMaxTreeIncrAttrOpening import MMaxTreeIncrAttrOpening
from .MMaxTreeNonIncrAttrClosing import MMaxTreeNonIncrAttrClosing
from .MMaxTreeNonIncrAttrOpening import MMaxTreeNonIncrAttrOpening

__all__ = [
    "MMaxTreeIncrAttrClosing",
    "MMaxTreeIncrAttrOpening",
    "MMaxTreeNonIncrAttrClosing",
    "MMaxTreeNonIncrAttrOpening",
]

"""Port of ``vpt.util.ordering.CustomLexico``."""

from __future__ import annotations

from ..Tools import Tools
from .AlgebraicOrdering import AlgebraicOrdering


class CustomLexico(AlgebraicOrdering):
    """Lexicographic order over an explicit channel priority."""

    def __init__(self, channelOrder):
        """CustomLexico(channelNumber) -> identity order, or
        CustomLexico(channelOrder) -> the given permutation."""
        if isinstance(channelOrder, int):
            self.cOrder = list(range(channelOrder))
        else:
            # Java keeps the caller's array by reference, so later mutation of
            # it is visible here. Preserved.
            self.cOrder = channelOrder

    def compare(self, v1, v2) -> int:
        for i in range(len(v1)):
            tmp = Tools.cmpr(v1[self.cOrder[i]], v2[self.cOrder[i]])
            if tmp != 0:
                return tmp
        return 0

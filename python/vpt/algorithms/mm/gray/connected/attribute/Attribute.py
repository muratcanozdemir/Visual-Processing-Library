"""Port of ``vpt.algorithms.mm.gray.connected.attribute.Attribute``.

The predicate interface every attribute filter implements: given a flat
zone's pixel list and the image it came from, decide whether the zone
passes (``True``, kept) or fails (``False``, pruned) -- used by the
max-tree attribute-opening/closing family in ``mm/gray/connected``.
"""

from __future__ import annotations

from abc import ABC, abstractmethod


class Attribute(ABC):
    @abstractmethod
    def test(self, pixels, img) -> bool:
        """Whether the flat zone `pixels` (a list of Point) in `img` passes."""

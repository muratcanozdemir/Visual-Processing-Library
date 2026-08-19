"""Port of ``vpt.algorithms.mm.multi.connected.attribute.MultiAttribute``.

The vectorial analogue of ``mm.gray.connected.attribute.Attribute``: given a
flat zone's pixel list (a ``MyList`` here, not a plain Python list -- see
``maxtree/MyList.py``) and the image it came from, decide whether the zone
passes (``True``, kept) or fails (``False``, pruned).
"""

from __future__ import annotations

from abc import ABC, abstractmethod


class MultiAttribute(ABC):
    @abstractmethod
    def test(self, pixels, img) -> bool:
        """Whether the flat zone `pixels` (a MyList of Point) in `img` passes."""

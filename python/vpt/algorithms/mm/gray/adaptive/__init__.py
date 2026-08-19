"""Port of the ``vpt.algorithms.mm.gray.adaptive`` package -- complete
(5/5)."""

from .AClosing import AClosing
from .ADilation import ADilation
from .AErosion import AErosion
from .AOpening import AOpening
from .ComputeSEs import ComputeSEs

__all__ = ["AClosing", "ADilation", "AErosion", "AOpening", "ComputeSEs"]

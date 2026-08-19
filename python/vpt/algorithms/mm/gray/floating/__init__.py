"""Port of the ``vpt.algorithms.mm.gray.floating`` package -- complete
(6/6)."""

from .FloatingCovariance import FloatingCovariance
from .FloatingOpeningBasedCovariance import FloatingOpeningBasedCovariance
from .GFloatingClosing import GFloatingClosing
from .GFloatingDilation import GFloatingDilation
from .GFloatingErosion import GFloatingErosion
from .GFloatingOpening import GFloatingOpening

__all__ = [
    "FloatingCovariance",
    "FloatingOpeningBasedCovariance",
    "GFloatingClosing",
    "GFloatingDilation",
    "GFloatingErosion",
    "GFloatingOpening",
]

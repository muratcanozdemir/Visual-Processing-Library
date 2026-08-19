"""Port of the ``vpt.algorithms.mm.multi.connected.attribute`` package --
complete: the ``MultiAttribute`` predicate interface and every implementer."""

from .MultiArea import MultiArea
from .MultiAttribute import MultiAttribute
from .MultiDiagonal import MultiDiagonal
from .MultiGeometricVolume import MultiGeometricVolume
from .MultiSdev import MultiSdev
from .MultiSdev2 import MultiSdev2
from .MultiSpectralVolume import MultiSpectralVolume

__all__ = [
    "MultiArea",
    "MultiAttribute",
    "MultiDiagonal",
    "MultiGeometricVolume",
    "MultiSdev",
    "MultiSdev2",
    "MultiSpectralVolume",
]

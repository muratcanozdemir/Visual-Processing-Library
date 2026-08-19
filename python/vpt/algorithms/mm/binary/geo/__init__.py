"""Port of the ``vpt.algorithms.mm.binary.geo`` package -- complete (8/8).

``BGeodesicDilation``/``BReconstructionByDilation``/``BFillHole`` were ported
first, as ``ConvexHull``'s filled-path dependencies. ``BGeodesicErosion``/
``BReconstructionByErosion``/``BOpeningByReconstruction``/
``BClosingByReconstruction``/``BClearBorderObjects`` complete the package.
Every reconstruction-loop entry point here (``BReconstructionBy{Dilation,
Erosion}`` and everything built on them) hangs forever on ``BooleanImage``
input -- the type this whole package exists for -- per the NOR/NAND hazards
documented in ``BGeodesicDilation.py``/``BGeodesicErosion.py``.
"""

from .BClearBorderObjects import BClearBorderObjects
from .BClosingByReconstruction import BClosingByReconstruction
from .BFillHole import BFillHole
from .BGeodesicDilation import BGeodesicDilation
from .BGeodesicErosion import BGeodesicErosion
from .BOpeningByReconstruction import BOpeningByReconstruction
from .BReconstructionByDilation import BReconstructionByDilation
from .BReconstructionByErosion import BReconstructionByErosion

__all__ = [
    "BClearBorderObjects",
    "BClosingByReconstruction",
    "BFillHole",
    "BGeodesicDilation",
    "BGeodesicErosion",
    "BOpeningByReconstruction",
    "BReconstructionByDilation",
    "BReconstructionByErosion",
]

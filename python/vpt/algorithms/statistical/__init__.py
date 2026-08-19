"""Port of the ``vpt.algorithms.statistical`` package -- complete (15/15).

Basic per-image statistics (``Mean``, ``SDev``, ``Variance``, ``Kurtosis``,
``Skewness``, ``ContrastRatio``, ``NMSE``, ``Normalize``), spatial/central/
invariant moments (``Moment``, ``CentralMoment``, ``SpatialMoment``,
``InvariantMoment``, ``ZernikeMoment``), and channel-space PCA (``PCA``,
``ReversePCA``). Ported ahead of `shape`'s Fourier/Zernike descriptor
subset, which depends on this package.

Notable preserved findings (see each file's docstring for the full case):
``CentralMoment`` never actually centres anything (bytecode-confirmed dead
``centroidX``/``centroidY`` arrays) -- so ``SpatialMoment`` and
``InvariantMoment``, both built on it, inherit the same defect. ``NMSE``
is effectively always ``0.0``/``NaN``, a consequence of the already-
documented ``Tools.VSub`` bug. ``PCA.getVariances`` has the same
allocate-fresh-zeros-mutate-input-instead shape as ``Tools.VAddC``/``VSub``.
``PCA``'s eigendecomposition (Jama vs. NumPy/LAPACK) is a new accepted
divergence, the same category as ``NonFlatSE.decompose``'s SVD."""

from .CentralMoment import CentralMoment
from .ContrastRatio import ContrastRatio
from .InvariantMoment import InvariantMoment
from .Kurtosis import Kurtosis
from .Mean import Mean
from .Moment import Moment
from .NMSE import NMSE
from .Normalize import Normalize
from .PCA import PCA
from .ReversePCA import ReversePCA
from .SDev import SDev
from .Skewness import Skewness
from .SpatialMoment import SpatialMoment
from .Variance import Variance
from .ZernikeMoment import ZernikeMoment

__all__ = [
    "NMSE",
    "PCA",
    "CentralMoment",
    "ContrastRatio",
    "InvariantMoment",
    "Kurtosis",
    "Mean",
    "Moment",
    "Normalize",
    "ReversePCA",
    "SDev",
    "Skewness",
    "SpatialMoment",
    "Variance",
    "ZernikeMoment",
]

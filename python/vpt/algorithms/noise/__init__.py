"""Port of the ``vpt.algorithms.noise`` package -- complete (2/2). Both
files use an unseeded RNG (non-deterministic, same as
``segmentation.MeanShift``), so neither is golden-verified -- smoke-tested
in Python only."""

from .Gaussian import Gaussian
from .Speckle import Speckle

__all__ = ["Gaussian", "Speckle"]

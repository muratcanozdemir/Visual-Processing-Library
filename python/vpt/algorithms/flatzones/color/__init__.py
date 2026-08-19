"""Port of the ``vpt.algorithms.flatzones.color`` package -- 4/7 in scope.

**Not ported**: ``ColorQFZRGB``, ``ColorQFZLSH``, ``ColorQFZRGBByte``. All
three pre-build and sort a 256x256x256 (16,777,216-entry) RGB gamut table
through a caller-supplied ``AlgebraicOrdering`` before doing any actual
zone growth -- the exact same shape as ``mm/multi/connected/maxtree.
MultivariateMaxTreeEntireGamut``, already excluded from this port for the
same reason (see ``python/README.md``): sorting 16.7M Python objects with a
Python-level comparator is prohibitively slow to even smoke-test, let alone
golden-verify, for algorithms nothing else in the library calls."""

from .ColorFZRGB import ColorFZRGB
from .ColorQFZRGBMarginal import ColorQFZRGBMarginal
from .ColorQFZRGBMarginalFromMarkers import ColorQFZRGBMarginalFromMarkers
from .ColorQFZSoille import ColorQFZSoille

__all__ = [
    "ColorFZRGB",
    "ColorQFZRGBMarginal",
    "ColorQFZRGBMarginalFromMarkers",
    "ColorQFZSoille",
]

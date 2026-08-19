"""Port of the ``vpt.algorithms.experimental`` package -- 2/8 in scope
(``AreaLimits``, ``Weird``). The other 6 files, all under
``experimental/fz``, are not ported: 5 build the same 16.7M-entry RGB
gamut table already excluded from ``flatzones.color`` (``ColorQFZCIELAB``,
``ColorQFZRGBByte2``, ``ColorQFZRGBByteNoLabelControl``,
``ColorQFZRGBNoLabelControl``, ``ColorFZSoille``), and the sixth
(``ColorQFZRGBMarginalNoLabelControl``) is a known-incomplete
``flatzones.color.ColorQFZRGBMarginal`` variant -- its own Java has a
debug ``System.err.println("LAN")`` at the exact site where dropping the
already-labelled-neighbour check lets a growing zone silently overwrite
an earlier one."""

from .AreaLimits import AreaLimits
from .Weird import Weird

__all__ = ["AreaLimits", "Weird"]

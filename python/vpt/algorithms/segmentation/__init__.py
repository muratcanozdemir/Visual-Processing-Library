"""Port of the ``vpt.algorithms.segmentation`` package -- complete (16/16
in-scope files): ``Threshold``, ``OtsuThreshold``, ``HysteresisThreshold``,
``Otsu``, ``SuperposeBorders``, ``BorderToDominantNeighborLabel``,
``NumberOfSingletons``, ``LabelsToMeanValue``, ``LabelsToMeanHue``,
``MergeSingletons``, ``OSR``, ``Watershed``, ``MP``,
``MarkerBasedWatershed``, ``MeanShift`` (not golden-tested -- genuinely
non-deterministic, see its docstring), and ``Seg2Image`` (ported from
``segmentation.berkeley.Seg2Image`` -- the one genuinely portable file in
that Java subpackage, see its docstring). The rest of ``berkeley`` (6
files: a gamut-table ``ColorQFZRGB``/``ColorVector`` pair, four
``main()``-only test harnesses) and ``segmentation/texture/
TestOutexAngular.java`` (1 file) remain out of scope (dataset-driven test
harnesses / prohibitively-slow gamut table, same categories as the
already-excluded texture/Weka classifier tests and
``flatzones.color.ColorQFZRGB``) -- 16/23 files on disk, 16/16 of what's
in scope."""

from .BorderToDominantNeighborLabel import BorderToDominantNeighborLabel
from .HysteresisThreshold import HysteresisThreshold
from .LabelsToMeanHue import LabelsToMeanHue
from .LabelsToMeanValue import LabelsToMeanValue
from .MarkerBasedWatershed import MarkerBasedWatershed
from .MeanShift import MeanShift
from .MergeSingletons import MergeSingletons
from .MP import MP
from .NumberOfSingletons import NumberOfSingletons
from .OSR import OSR
from .Otsu import Otsu
from .OtsuThreshold import OtsuThreshold
from .Seg2Image import Seg2Image
from .SuperposeBorders import SuperposeBorders
from .Threshold import Threshold
from .Watershed import Watershed

__all__ = [
    "MP",
    "OSR",
    "BorderToDominantNeighborLabel",
    "HysteresisThreshold",
    "LabelsToMeanHue",
    "LabelsToMeanValue",
    "MarkerBasedWatershed",
    "MeanShift",
    "MergeSingletons",
    "NumberOfSingletons",
    "Otsu",
    "OtsuThreshold",
    "Seg2Image",
    "SuperposeBorders",
    "Threshold",
    "Watershed",
]

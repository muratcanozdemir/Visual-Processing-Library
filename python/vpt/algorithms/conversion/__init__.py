"""Port of the ``vpt.algorithms.conversion`` package -- complete (21/21)."""

from .Angular2Color import Angular2Color
from .DistanceFromHSYColor import DistanceFromHSYColor
from .EuclideanDistanceFromLABColor import EuclideanDistanceFromLABColor
from .EuclideanDistanceFromRGBColor import EuclideanDistanceFromRGBColor
from .HSY2RGB import HSY2RGB
from .Hue2Distance import Hue2Distance
from .LAB2XYZ import LAB2XYZ
from .NonUniformHSY733 import NonUniformHSY733
from .NonUniformHSY744 import NonUniformHSY744
from .NonUniformHSY833 import NonUniformHSY833
from .RGB2AVG import RGB2AVG
from .RGB2cRGB import RGB2cRGB
from .RGB2Gray import RGB2Gray
from .RGB2HSY import RGB2HSY
from .RGB2NUHSY733 import RGB2NUHSY733
from .RGB2RGB64 import RGB2RGB64
from .RGB2XYZ import RGB2XYZ
from .RGB2YUV import RGB2YUV
from .XYZ2LAB import XYZ2LAB
from .XYZ2RGB import XYZ2RGB
from .YUV2RGB import YUV2RGB

__all__ = [
    "HSY2RGB",
    "LAB2XYZ",
    "RGB2AVG",
    "RGB2HSY",
    "RGB2NUHSY733",
    "RGB2RGB64",
    "RGB2XYZ",
    "RGB2YUV",
    "XYZ2LAB",
    "XYZ2RGB",
    "YUV2RGB",
    "Angular2Color",
    "DistanceFromHSYColor",
    "EuclideanDistanceFromLABColor",
    "EuclideanDistanceFromRGBColor",
    "Hue2Distance",
    "NonUniformHSY733",
    "NonUniformHSY744",
    "NonUniformHSY833",
    "RGB2Gray",
    "RGB2cRGB",
]

"""Visual Processing Library -- Python port.

A port of the VPT Java library. The Java tree in ``src/`` is the behavioural
oracle: it compiles, runs, and every algorithm here is verified against golden
values dumped from it.

The public API mirrors the Java one method-for-method (``getXYCDouble``,
``FlatSE.square(3)``, ``GDilation.invoke(img, se)``) so that each Python file
can be diffed against its Java counterpart during review. Where Java overloads
on static argument type -- something Python cannot reproduce -- the overloads
are split into explicitly named methods and a runtime-dispatching wrapper is
provided; see ``Image.fill``.
"""

from ._javacompat import Point
from .Algorithm import Algorithm
from .BooleanImage import BooleanImage
from .ByteImage import ByteImage
from .DoubleImage import DoubleImage
from .GlobalException import GlobalException
from .Image import Image
from .IntegerImage import IntegerImage

__all__ = [
    "Algorithm",
    "BooleanImage",
    "ByteImage",
    "DoubleImage",
    "GlobalException",
    "Image",
    "IntegerImage",
    "Point",
]

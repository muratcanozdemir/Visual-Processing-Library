"""Port of ``vpt.BooleanImage``.

PRESERVED DEFECT: the non-boolean *getters* are inverted with respect to what
their names suggest -- ``getByte`` returns 255 for a **false** pixel and 0 for
true, and ``getDouble`` likewise returns 1.0 for false. The README documents
this as matching the class's internal convention and leaves it as found, so
the port reproduces it. Do not "fix" it without regenerating the goldens: the
Java oracle behaves this way and callers depend on it.

The setters were the ones the decompiler broke (mangled boolean ternaries
producing a runtime ClassCastException, README item 2). They are ported from
the repaired source.

DOWNSTREAM CONSEQUENCE: because ``getDouble`` is inverted but ``setDouble``
is not, any generic double-view algorithm that both reads and writes through
it stops being self-consistent on this type. ``vpt.algorithms.arithmetic.
Minimum``, for instance, computes NOR instead of AND on a pair of
``BooleanImage``s -- see ``vpt/algorithms/mm/binary/geo/BGeodesicDilation.py``
for the mechanism and ``BReconstructionByDilation.py`` for the resulting
infinite loop in ``BFillHole``/``ConvexHull(..., filled=True)``.
"""

from __future__ import annotations

import numpy as np

from ._javacompat import INT_MAX, INT_MIN
from .Image import Image


class BooleanImage(Image):
    __slots__ = ()

    _DTYPE = np.bool_

    def __init__(self, *args):
        """BooleanImage(xdim, ydim) | (xdim, ydim, cdim) | (Image, deepCopy)."""
        super().__init__()
        if len(args) == 2 and isinstance(args[0], Image):
            img, deepCopy = args
            self._initDims(img.getXDim(), img.getYDim(), img.getCDim())
            if deepCopy:
                self.setBooleanArray(img.getBooleanArray())
        elif len(args) == 2:
            self._initDims(args[0], args[1], 1)
        elif len(args) == 3:
            self._initDims(args[0], args[1], args[2])
        else:
            raise TypeError(f"BooleanImage takes 2 or 3 arguments, got {len(args)}")

    # ------------------------------------------------------------------
    def newInstanceCopy(self, deepCopy: bool) -> Image:
        return BooleanImage(self, deepCopy)

    def newInstanceDims(self, xdim: int, ydim: int, cdim: int) -> Image:
        return BooleanImage(xdim, ydim, cdim)

    # ------------------------------------------------------------------
    def fillDouble(self, p: float) -> None:
        self._pixels[:] = p > 0.0

    def fillInt(self, p: int) -> None:
        self._pixels[:] = p > 0

    def fillBoolean(self, p: bool) -> None:
        self._pixels[:] = bool(p)

    # ------------------------------------------------------------------
    def getBoolean(self, i: int) -> bool:
        return bool(self._pixels[i])

    def setBoolean(self, i: int, p: bool) -> None:
        self._pixels[i] = bool(p)

    def getInt(self, i: int) -> int:
        return INT_MAX if not self._pixels[i] else INT_MIN

    def setInt(self, i: int, p: int) -> None:
        self._pixels[i] = p != INT_MIN

    def getByte(self, i: int) -> int:
        return 255 if not self._pixels[i] else 0

    def setByte(self, i: int, p: int) -> None:
        self._pixels[i] = p > 0

    def getDouble(self, i: int) -> float:
        return 1.0 if not self._pixels[i] else 0.0

    def setDouble(self, i: int, p: float) -> None:
        self._pixels[i] = p > 0.0

    # ------------------------------------------------------------------
    def getDoubleArray(self) -> np.ndarray:
        return np.where(self._pixels, 0.0, 1.0)

    def setDoubleArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.asarray(a, np.float64) > 0.0

    def getByteArray(self) -> np.ndarray:
        return np.where(self._pixels, 0, 255).astype(np.int64)

    def setByteArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.asarray(a, np.int64) > 0

    def getBooleanArray(self) -> np.ndarray:
        return self._pixels.copy()

    def setBooleanArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.asarray(a, bool)

"""Port of ``vpt.IntegerImage``.

Storage is signed 32-bit. Used mostly for label images, where the full int
range is needed and the byte/double views are incidental.
"""

from __future__ import annotations

import sys

import numpy as np

from ._javacompat import INT_MAX, INT_MIN, int32, int32_arr, ishr, jint, jint_arr
from .Image import Image


class IntegerImage(Image):
    __slots__ = ()

    _DTYPE = np.int32

    MAX_VALUE = INT_MAX
    MIN_VALUE = INT_MIN
    doubleToInt = 4294967295.0

    def __init__(self, *args):
        """IntegerImage(xdim, ydim) | (xdim, ydim, cdim) | (Image, deepCopy)."""
        super().__init__()
        if len(args) == 2 and isinstance(args[0], Image):
            img, deepCopy = args
            self._initDims(img.getXDim(), img.getYDim(), img.getCDim())
            if deepCopy:
                for i in range(self._pixels.size):
                    self.setInt(i, img.getInt(i))
        elif len(args) == 2:
            self._initDims(args[0], args[1], 1)
        elif len(args) == 3:
            self._initDims(args[0], args[1], args[2])
        else:
            raise TypeError(f"IntegerImage takes 2 or 3 arguments, got {len(args)}")

    def getPixels(self) -> np.ndarray:
        return self._pixels

    # ------------------------------------------------------------------
    def newInstanceCopy(self, deepCopy: bool) -> Image:
        return IntegerImage(self, deepCopy)

    def newInstanceDims(self, xdim: int, ydim: int, cdim: int) -> Image:
        return IntegerImage(xdim, ydim, cdim)

    # ------------------------------------------------------------------
    def fillDouble(self, p: float) -> None:
        # Java prints to stderr and leaves the buffer untouched. Preserved.
        print("Not yet implemented!!!", file=sys.stderr)

    def fillInt(self, p: int) -> None:
        self._pixels[:] = int32(p)

    def fillBoolean(self, p: bool) -> None:
        self._pixels[:] = INT_MAX if p else INT_MIN

    # ------------------------------------------------------------------
    def getBoolean(self, i: int) -> bool:
        return bool(self._pixels[i] > INT_MIN)

    def setBoolean(self, i: int, p: bool) -> None:
        self._pixels[i] = INT_MAX if p else INT_MIN

    def getByte(self, i: int) -> int:
        return ishr(int(self._pixels[i]), 24) - -128

    def setByte(self, i: int, p: int) -> None:
        self._pixels[i] = int32((p + -128) << 24)

    def getInt(self, i: int) -> int:
        return int(self._pixels[i])

    def setInt(self, i: int, p: int) -> None:
        self._pixels[i] = int32(p)

    def getDouble(self, i: int) -> float:
        return int(self._pixels[i]) * 2.328306437080797e-10 + 0.5

    def setDouble(self, i: int, p: float) -> None:
        self._pixels[i] = jint((p - 0.5) * 4294967295.0)

    # ------------------------------------------------------------------
    def getDoubleArray(self) -> np.ndarray:
        return self._pixels.astype(np.int64) * 2.328306437080797e-10 + 0.5

    def setDoubleArray(self, a: np.ndarray) -> None:
        self._pixels[:] = jint_arr((np.asarray(a, np.float64) - 0.5) * 4294967295.0)

    def getByteArray(self) -> np.ndarray:
        return (self._pixels.astype(np.int64) >> 24) + 128

    def setByteArray(self, a: np.ndarray) -> None:
        self._pixels[:] = int32_arr((np.asarray(a, np.int64) - 128) << 24)

    def getBooleanArray(self) -> np.ndarray:
        return self._pixels > INT_MIN

    def setBooleanArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.where(np.asarray(a, bool), INT_MAX, INT_MIN).astype(np.int32)

    def getIntArray(self) -> np.ndarray:
        return self._pixels.astype(np.int64)

    def setIntArray(self, a: np.ndarray) -> None:
        self._pixels[:] = int32_arr(a)

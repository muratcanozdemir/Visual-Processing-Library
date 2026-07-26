"""Port of ``vpt.ByteImage``.

Storage is a *signed* 8-bit array holding the pixel value biased by -128, so
byte value 0 is stored as -128 and 255 as +127. Every accessor adds or
subtracts that bias. NumPy's int8 supplies the wraparound Java gets from its
``(byte)`` cast; a plain Python int list would not.
"""

from __future__ import annotations

import numpy as np

from ._javacompat import ishl, ishr, jbyte, jbyte_arr, jround, jround_arr
from .Image import Image


class ByteImage(Image):
    __slots__ = ()

    _DTYPE = np.int8

    doubleToByte = 255.0
    MAX_VALUE = 255
    MIN_VALUE = 0

    def __init__(self, *args):
        """ByteImage(xdim, ydim) | (xdim, ydim, cdim) | (Image, deepCopy)."""
        super().__init__()
        if len(args) == 2 and isinstance(args[0], Image):
            img, deepCopy = args
            self._initDims(img.getXDim(), img.getYDim(), img.getCDim())
            if deepCopy:
                self.setByteArray(img.getByteArray())
        elif len(args) == 2:
            self._initDims(args[0], args[1], 1)
        elif len(args) == 3:
            self._initDims(args[0], args[1], args[2])
        else:
            raise TypeError(f"ByteImage takes 2 or 3 arguments, got {len(args)}")

    # ------------------------------------------------------------------
    def newInstanceCopy(self, deepCopy: bool) -> Image:
        return ByteImage(self, deepCopy)

    def newInstanceDims(self, xdim: int, ydim: int, cdim: int) -> Image:
        return ByteImage(xdim, ydim, cdim)

    # ------------------------------------------------------------------
    def fillDouble(self, p: float) -> None:
        self._pixels[:] = jbyte(jround(255.0 * p + -128.0))

    def fillInt(self, p: int) -> None:
        self._pixels[:] = jbyte(p + -128)

    def fillBoolean(self, p: bool) -> None:
        self._pixels[:] = 127 if p else -128

    # ------------------------------------------------------------------
    def getBoolean(self, i: int) -> bool:
        return bool(self._pixels[i] > -128)

    def setBoolean(self, i: int, p: bool) -> None:
        self._pixels[i] = 127 if p else -128

    def getByte(self, i: int) -> int:
        return int(self._pixels[i]) - -128

    def setByte(self, i: int, p: int) -> None:
        self._pixels[i] = jbyte(p + -128)

    def getInt(self, i: int) -> int:
        return ishl(int(self._pixels[i]), 24)

    def setInt(self, i: int, p: int) -> None:
        self._pixels[i] = jbyte(ishr(p, 24))

    def getDouble(self, i: int) -> float:
        return 0.00392156862745098 * (int(self._pixels[i]) - -128)

    def setDouble(self, i: int, p: float) -> None:
        self._pixels[i] = jbyte(jround(255.0 * p + -128.0))

    # ------------------------------------------------------------------
    def getDoubleArray(self) -> np.ndarray:
        return 0.00392156862745098 * (self._pixels.astype(np.int64) + 128)

    def setDoubleArray(self, a: np.ndarray) -> None:
        self._pixels[:] = jbyte_arr(jround_arr(255.0 * np.asarray(a, np.float64) - 128.0))

    def getByteArray(self) -> np.ndarray:
        return self._pixels.astype(np.int64) + 128

    def setByteArray(self, a: np.ndarray) -> None:
        self._pixels[:] = jbyte_arr(np.asarray(a, np.int64) - 128)

    def getBooleanArray(self) -> np.ndarray:
        return self._pixels > -128

    def setBooleanArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.where(np.asarray(a, bool), 127, -128).astype(np.int8)

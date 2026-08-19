"""Port of ``vpt.DoubleImage``.

The canonical floating-point image: storage is float64 in [0, 1] by
convention, though nothing enforces the range and several algorithms rely on
being able to exceed it.
"""

from __future__ import annotations

import numpy as np

from ._javacompat import int32, int32_arr, jint, jint_arr, jround, jround_arr
from .Image import Image


class DoubleImage(Image):
    __slots__ = ()

    _DTYPE = np.float64

    byteToDouble = 0.00392156862745098
    MAX_VALUE = 1.0
    MIN_VALUE = 0.0
    intToDouble = 2.328306437080797e-10
    intToDoubleOffset = 0.5

    def __init__(self, *args):
        """DoubleImage(xdim, ydim) | (xdim, ydim, cdim) | (Image, deepCopy)
        | (pixels2d), where pixels2d is indexed [y][x]."""
        super().__init__()
        if len(args) == 1:
            pixels = np.asarray(args[0], dtype=np.float64)
            if pixels.ndim != 2:
                raise TypeError("DoubleImage(pixels) requires a 2-D array")
            self._initDims(pixels.shape[1], pixels.shape[0], 1)
            # Java loops setXYDouble(x, y, pixels[y][x]); index is x + xdim*y,
            # so a C-order ravel of the [y][x] array lands in the right place.
            self._pixels[:] = pixels.ravel(order="C")
        elif len(args) == 2 and isinstance(args[0], Image):
            img, deepCopy = args
            self._initDims(img.getXDim(), img.getYDim(), img.getCDim())
            if deepCopy:
                self.setDoubleArray(img.getDoubleArray())
        elif len(args) == 2:
            self._initDims(args[0], args[1], 1)
        elif len(args) == 3:
            self._initDims(args[0], args[1], args[2])
        else:
            raise TypeError(f"DoubleImage takes 1 to 3 arguments, got {len(args)}")

    # ------------------------------------------------------------------
    def newInstanceCopy(self, deepCopy: bool) -> Image:
        return DoubleImage(self, deepCopy)

    def newInstanceDims(self, xdim: int, ydim: int, cdim: int) -> Image:
        return DoubleImage(xdim, ydim, cdim)

    # ------------------------------------------------------------------
    def fillDouble(self, p: float) -> None:
        self._pixels[:] = p

    def fillInt(self, p: int) -> None:
        self._pixels[:] = 0.00392156862745098 * p

    def fillBoolean(self, p: bool) -> None:
        self._pixels[:] = 1.0 if p else 0.0

    # ------------------------------------------------------------------
    def getBoolean(self, i: int) -> bool:
        return bool(self._pixels[i] > 0.0)

    def setBoolean(self, i: int, p: bool) -> None:
        self._pixels[i] = 1.0 if p else 0.0

    def getInt(self, i: int) -> int:
        return jint(4294967295.0 * (float(self._pixels[i]) - 0.5))

    def setInt(self, i: int, p: int) -> None:
        self._pixels[i] = 2.328306437080797e-10 * p + 0.5

    def getByte(self, i: int) -> int:
        return int32(jround(255.0 * float(self._pixels[i])))

    def setByte(self, i: int, p: int) -> None:
        self._pixels[i] = 0.00392156862745098 * p

    def getDouble(self, i: int) -> float:
        return float(self._pixels[i])

    def setDouble(self, i: int, p: float) -> None:
        self._pixels[i] = p

    # ------------------------------------------------------------------
    def getDoubleArray(self) -> np.ndarray:
        return self._pixels.copy()

    def setDoubleArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.asarray(a, np.float64)

    def getByteArray(self) -> np.ndarray:
        return int32_arr(jround_arr(255.0 * self._pixels)).astype(np.int64)

    def setByteArray(self, a: np.ndarray) -> None:
        self._pixels[:] = 0.00392156862745098 * np.asarray(a, np.float64)

    def getBooleanArray(self) -> np.ndarray:
        return self._pixels > 0.0

    def setBooleanArray(self, a: np.ndarray) -> None:
        self._pixels[:] = np.where(np.asarray(a, bool), 1.0, 0.0)

    def getIntArray(self) -> np.ndarray:
        return jint_arr(4294967295.0 * (self._pixels - 0.5)).astype(np.int64)

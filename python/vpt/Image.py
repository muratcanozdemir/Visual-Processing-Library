"""Port of ``vpt.Image``.

Abstract base of the image hierarchy. Pixels live in one flat array indexed
``c + cdim * (x + xdim * y)``; the four concrete subclasses differ only in
storage type and in how they convert between that storage and the four
accessor views (boolean, byte, int, double).

Java overloads that dispatch on *static* argument type are split into
explicitly named methods here, because Python cannot reproduce static
dispatch. ``fill(int)`` and ``fill(double)`` are genuinely different
operations on a ByteImage -- ``fill(1)`` stores 1 while ``fill(1.0)`` stores
255 -- so collapsing them would silently corrupt results. The dispatching
``fill()`` wrapper is provided for convenience and keys off the Python
runtime type, which preserves the distinction as long as callers write
``0`` versus ``0.0`` the way the Java source does.
"""

from __future__ import annotations

import numbers
from abc import ABC, abstractmethod

import numpy as np

from ._javacompat import Point, jround


class Image(ABC):
    """Abstract base class for all images."""

    __slots__ = ("name", "xdim", "ydim", "cdim", "_pixels")

    # Storage dtype, set by each concrete subclass.
    _DTYPE: np.dtype = None

    def __init__(self):
        self.name = None
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0
        self._pixels = None

    # ------------------------------------------------------------------
    # Abstract accessors: flat-index views onto the storage
    # ------------------------------------------------------------------
    @abstractmethod
    def getBoolean(self, i: int) -> bool: ...

    @abstractmethod
    def setBoolean(self, i: int, p: bool) -> None: ...

    @abstractmethod
    def getByte(self, i: int) -> int: ...

    @abstractmethod
    def setByte(self, i: int, p: int) -> None: ...

    @abstractmethod
    def getInt(self, i: int) -> int: ...

    @abstractmethod
    def setInt(self, i: int, p: int) -> None: ...

    @abstractmethod
    def getDouble(self, i: int) -> float: ...

    @abstractmethod
    def setDouble(self, i: int, p: float) -> None: ...

    # ------------------------------------------------------------------
    # (x, y) accessors -- single channel
    # ------------------------------------------------------------------
    def getXYByte(self, x: int, y: int) -> int:
        return self.getByte(x + self.xdim * y)

    def setXYByte(self, x: int, y: int, p: int) -> None:
        self.setByte(x + self.xdim * y, p)

    def getXYBoolean(self, x: int, y: int) -> bool:
        return self.getBoolean(x + self.xdim * y)

    def setXYBoolean(self, x: int, y: int, p: bool) -> None:
        self.setBoolean(x + self.xdim * y, p)

    def setXYDouble(self, x: int, y: int, p: float) -> None:
        self.setDouble(x + self.xdim * y, p)

    def getXYDouble(self, x: int, y: int) -> float:
        return self.getDouble(x + self.xdim * y)

    def setXYInt(self, x: int, y: int, p: int) -> None:
        self.setInt(x + self.xdim * y, p)

    def getXYInt(self, x: int, y: int) -> int:
        return self.getInt(x + self.xdim * y)

    # ------------------------------------------------------------------
    # (x, y, c) accessors -- multi channel
    # ------------------------------------------------------------------
    def getXYCByte(self, x: int, y: int, c: int) -> int:
        return self.getByte(c + self.cdim * (x + self.xdim * y))

    def setXYCByte(self, x: int, y: int, c: int, p: int) -> None:
        self.setByte(c + self.cdim * (x + self.xdim * y), p)

    def getXYCBoolean(self, x: int, y: int, c: int) -> bool:
        return self.getBoolean(c + self.cdim * (x + self.xdim * y))

    def setXYCBoolean(self, x: int, y: int, c: int, p: bool) -> None:
        self.setBoolean(c + self.cdim * (x + self.xdim * y), p)

    def setXYCDouble(self, x: int, y: int, c: int, p: float) -> None:
        self.setDouble(c + self.cdim * (x + self.xdim * y), p)

    def getXYCDouble(self, x: int, y: int, c: int) -> float:
        return self.getDouble(c + self.cdim * (x + self.xdim * y))

    def setXYCInt(self, x: int, y: int, c: int, p: int) -> None:
        self.setInt(c + self.cdim * (x + self.xdim * y), p)

    def getXYCInt(self, x: int, y: int, c: int) -> int:
        return self.getInt(c + self.cdim * (x + self.xdim * y))

    # ------------------------------------------------------------------
    def pgetXYCDouble(self, x: float, y: float, c: int) -> float:
        """Bilinearly interpolated read at fractional coordinates."""
        from .util.Tools import Tools

        if Tools.cmpr(abs(jround(x) - x), 0.0) == 0 and Tools.cmpr(abs(jround(y) - y), 0.0) == 0:
            return self.getXYCDouble(int(x), int(y), c)

        import math

        ll = math.floor(x)
        k = math.floor(y)

        a = x - ll
        b = y - k

        p1 = self.getXYCDouble(ll, k, c)
        p2 = self.getXYCDouble(min(ll + 1, self.xdim - 1), k, c)
        p3 = self.getXYCDouble(ll, min(k + 1, self.ydim - 1), c)
        p4 = self.getXYCDouble(min(ll + 1, self.xdim - 1), min(k + 1, self.ydim - 1), c)

        return (
            (1.0 - a) * (1.0 - b) * p1
            + a * (1.0 - b) * p2
            + (1.0 - a) * b * p3
            + a * b * p4
        )

    def setVXYDouble(self, x: int, y: int, p) -> None:
        for c in range(self.cdim):
            self.setXYCDouble(x, y, c, p[c])

    def getVXYDouble(self, x: int, y: int) -> np.ndarray:
        p = np.empty(self.cdim, dtype=np.float64)
        for c in range(self.cdim):
            p[c] = self.getXYCDouble(x, y, c)
        return p

    def setVXYByte(self, x: int, y: int, p) -> None:
        for c in range(self.cdim):
            self.setXYCByte(x, y, c, p[c])

    def getVXYByte(self, x: int, y: int) -> np.ndarray:
        p = np.empty(self.cdim, dtype=np.int64)
        for c in range(self.cdim):
            p[c] = self.getXYCByte(x, y, c)
        return p

    # ------------------------------------------------------------------
    def getSize(self) -> int:
        return self.xdim * self.ydim * self.cdim

    def getChannel(self, c: int) -> "Image":
        output = self.newInstanceDims(self.xdim, self.ydim, 1)
        for x in range(self.xdim):
            for y in range(self.ydim):
                output.setXYDouble(x, y, self.getXYCDouble(x, y, c))
        return output

    def setChannel(self, img: "Image", c: int) -> None:
        for x in range(self.xdim):
            for y in range(self.ydim):
                self.setXYCByte(x, y, c, img.getXYByte(x, y))

    # ------------------------------------------------------------------
    # Construction. Java has newInstance(boolean) and newInstance(int,int,int);
    # the argument count disambiguates, so the dispatcher is exact.
    # ------------------------------------------------------------------
    @abstractmethod
    def newInstanceCopy(self, deepCopy: bool) -> "Image": ...

    @abstractmethod
    def newInstanceDims(self, xdim: int, ydim: int, cdim: int) -> "Image": ...

    def newInstance(self, *args) -> "Image":
        if len(args) == 1:
            return self.newInstanceCopy(bool(args[0]))
        if len(args) == 3:
            return self.newInstanceDims(*args)
        raise TypeError(f"newInstance takes 1 or 3 arguments, got {len(args)}")

    def hasSameDimensionsAs(self, img: "Image") -> bool:
        return not (
            self.xdim != img.xdim or self.ydim != img.ydim or self.cdim != img.cdim
        )

    # ------------------------------------------------------------------
    def getXYCMaximum(self, c: int) -> Point:
        p = Point(0, 0)
        # PRESERVED DEFECT: the Java source reads 1.797693134862316E+30, with
        # exponent E+30 rather than E+308. It compiles, so it is not a
        # compile-time artefact of the decompile, and it silently makes this
        # sentinel ~278 orders of magnitude too small. Reproduced verbatim so
        # the port matches the oracle; see README "Decompiler damage", item 3.
        maximum = -1.797693134862316e30
        for x in range(self.xdim):
            for y in range(self.ydim):
                tmp = self.getXYCDouble(x, y, c)
                if tmp > maximum:
                    maximum = tmp
                    p.x = x
                    p.y = y
        return p

    def getXYCMinimum(self, c: int) -> Point:
        p = Point(0, 0)
        minimum = 1.7976931348623157e308
        for x in range(self.xdim):
            for y in range(self.ydim):
                tmp = self.getXYCDouble(x, y, c)
                if tmp < minimum:
                    minimum = tmp
                    p.x = x
                    p.y = y
        return p

    def isEmpty(self, c: int) -> bool:
        for x in range(self.xdim):
            for y in range(self.ydim):
                if self.getXYCDouble(x, y, c) > 0.0:
                    return False
        return True

    def isFull(self, c: int) -> bool:
        for x in range(self.xdim):
            for y in range(self.ydim):
                if not self.getXYCBoolean(x, y, c):
                    return False
        return True

    def getDiagonal(self) -> int:
        import math

        return int(jround(math.sqrt(self.xdim * self.xdim + self.ydim * self.ydim)))

    # ------------------------------------------------------------------
    # fill: Java overloads on static type; split explicitly.
    # ------------------------------------------------------------------
    @abstractmethod
    def fillDouble(self, p: float) -> None: ...

    @abstractmethod
    def fillInt(self, p: int) -> None: ...

    @abstractmethod
    def fillBoolean(self, p: bool) -> None: ...

    def fill(self, p) -> None:
        """Dispatch to fillBoolean/fillInt/fillDouble by runtime type.

        bool is checked first because ``bool`` subclasses ``int`` in Python.
        """
        if isinstance(p, (bool, np.bool_)):
            self.fillBoolean(bool(p))
        elif isinstance(p, (numbers.Integral, np.integer)):
            self.fillInt(int(p))
        elif isinstance(p, (numbers.Real, np.floating)):
            self.fillDouble(float(p))
        else:
            raise TypeError(f"cannot fill with {type(p).__name__}")

    # ------------------------------------------------------------------
    def getXDim(self) -> int:
        return self.xdim

    def getYDim(self) -> int:
        return self.ydim

    def getCDim(self) -> int:
        return self.cdim

    def size(self) -> int:
        return self._pixels.size

    # ------------------------------------------------------------------
    # Vectorisation hooks. These are not part of the Java API; they exist so
    # algorithms can operate on whole buffers instead of per-pixel calls, and
    # are required to be elementwise-identical to the scalar accessors above.
    # ------------------------------------------------------------------
    @abstractmethod
    def getDoubleArray(self) -> np.ndarray:
        """Flat float64 array, elementwise equal to ``getDouble(i)``."""

    @abstractmethod
    def setDoubleArray(self, a: np.ndarray) -> None:
        """Bulk equivalent of ``setDouble(i, a[i])`` for every i."""

    @abstractmethod
    def getByteArray(self) -> np.ndarray:
        """Flat int64 array, elementwise equal to ``getByte(i)``."""

    @abstractmethod
    def setByteArray(self, a: np.ndarray) -> None:
        """Bulk equivalent of ``setByte(i, a[i])`` for every i."""

    @abstractmethod
    def getBooleanArray(self) -> np.ndarray:
        """Flat bool array, elementwise equal to ``getBoolean(i)``."""

    @abstractmethod
    def setBooleanArray(self, a: np.ndarray) -> None:
        """Bulk equivalent of ``setBoolean(i, a[i])`` for every i."""

    def view3d(self) -> np.ndarray:
        """Storage reshaped to ``[y][x][c]``, matching the index formula."""
        return self._pixels.reshape(self.ydim, self.xdim, self.cdim)

    def _initDims(self, xdim: int, ydim: int, cdim: int) -> None:
        self.name = None
        self.xdim = int(xdim)
        self.ydim = int(ydim)
        self.cdim = int(cdim)
        self._pixels = np.zeros(self.xdim * self.ydim * self.cdim, dtype=self._DTYPE)

    def __repr__(self) -> str:
        return (
            f"{type(self).__name__}(xdim={self.xdim}, ydim={self.ydim}, "
            f"cdim={self.cdim})"
        )

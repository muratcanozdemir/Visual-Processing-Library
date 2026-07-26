"""Java language semantics that Python does not share.

Every function here exists because a naive Python translation of the Java
source silently produces different numbers. Nothing in this module is a
convenience; each entry corresponds to a concrete divergence found in the
oracle tree. See ``docs/porting-notes.md`` for the survey.

The scalar helpers take and return Python ints/floats. The ``_arr`` variants
take and return NumPy arrays and are semantically identical elementwise.
"""

from __future__ import annotations

import math

import numpy as np

INT_MIN = -2147483648
INT_MAX = 2147483647
LONG_MIN = -(2**63)
LONG_MAX = 2**63 - 1

# Java's Double.MAX_VALUE, used as a sentinel throughout the morphology code.
DOUBLE_MAX_VALUE = 1.7976931348623157e308


# --------------------------------------------------------------------------
# Rounding
# --------------------------------------------------------------------------
# Java's Math.round(double) is round-half-UP (toward positive infinity):
# Math.round(2.5) == 3, Math.round(-2.5) == -2.
# Python's round() and np.round() are round-half-to-EVEN:
# round(2.5) == 2, round(0.5) == 0.
#
# Naive `math.floor(x + 0.5)` matches Java 6 but NOT Java 8+, which fixed the
# double-rounding bug where Math.round(0.49999999999999994) returned 1. The
# oracle builds on JDK 17/21, so we implement the fixed semantics: compare the
# exact fractional part against 1/2.
#
# `x - math.floor(x)` is exact in IEEE-754 (Sterbenz: for x >= 1 we have
# floor(x) <= x <= 2*floor(x); for |x| < 1 the floor is 0 or -1 and the
# subtraction is trivially exact), so no Fraction arithmetic is needed.


def jround(a: float) -> int:
    """Java ``Math.round(double)`` -> long. Round half toward +infinity."""
    if a != a:  # NaN
        return 0
    if a == math.inf:
        return LONG_MAX
    if a == -math.inf:
        return LONG_MIN
    f = math.floor(a)
    if a - f >= 0.5:
        f += 1
    return _clamp_long(f)


def jround_arr(a: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jround` over a float array, returning int64."""
    a = np.asarray(a, dtype=np.float64)
    finite = np.isfinite(a)
    # Compute only where finite; infinities and NaN are patched in by the
    # clamp below, and evaluating floor()/subtraction on them raises warnings.
    safe = np.where(finite, a, 0.0)
    f = np.floor(safe)
    out = np.where(safe - f >= 0.5, f + 1.0, f)
    out = np.where(finite, out, a)  # restore +-inf / NaN for the clamp
    return _clamp_long_arr(out)


def _clamp_long(v: int) -> int:
    if v < LONG_MIN:
        return LONG_MIN
    if v > LONG_MAX:
        return LONG_MAX
    return v


def _clamp_long_arr(v: np.ndarray) -> np.ndarray:
    # float(LONG_MAX) rounds UP to 2**63, which is not representable as int64
    # and wraps to LONG_MIN on cast. Saturate explicitly instead of relying on
    # a float clip boundary.
    two63 = 9223372036854775808.0  # 2**63, exactly representable
    hi = v >= two63
    lo = v <= -two63
    nan = np.isnan(v)
    safe = np.where(hi | lo | nan, 0.0, v).astype(np.int64)
    safe = np.where(hi, LONG_MAX, safe)
    safe = np.where(lo, LONG_MIN, safe)
    return np.where(nan, 0, safe)


# --------------------------------------------------------------------------
# Narrowing casts
# --------------------------------------------------------------------------
# Java's `(int) someDouble` truncates toward zero, maps NaN to 0, and
# saturates at Integer.MIN_VALUE / MAX_VALUE rather than wrapping.
# Python's int() truncates toward zero too, but raises on NaN/inf and never
# saturates.


def jint(a: float) -> int:
    """Java ``(int)`` cast applied to a double."""
    if a != a:  # NaN
        return 0
    if a >= INT_MAX:
        return INT_MAX
    if a <= INT_MIN:
        return INT_MIN
    return int(a)  # Python int() truncates toward zero, like Java


def jint_arr(a: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jint` over a float array, returning int32."""
    a = np.asarray(a, dtype=np.float64)
    out = np.trunc(a)
    out = np.where(np.isnan(a), 0.0, out)
    out = np.clip(out, float(INT_MIN), float(INT_MAX))
    return out.astype(np.int32)


def jbyte(v: int) -> int:
    """Java ``(byte)`` cast applied to an int: wrap to signed 8 bits."""
    return ((int(v) + 128) & 0xFF) - 128


def jbyte_arr(a: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jbyte` over an integer array, returning int8."""
    a = np.asarray(a, dtype=np.int64)
    return (((a + 128) & 0xFF) - 128).astype(np.int8)


def int32(v: int) -> int:
    """Wrap an arbitrary Python int to Java ``int`` (signed 32-bit)."""
    return ((int(v) + 0x80000000) & 0xFFFFFFFF) - 0x80000000


def int32_arr(a: np.ndarray) -> np.ndarray:
    """Elementwise :func:`int32` over an integer array, returning int32."""
    a = np.asarray(a, dtype=np.int64)
    return (((a + 0x80000000) & 0xFFFFFFFF) - 0x80000000).astype(np.int32)


def ishl(v: int, n: int) -> int:
    """Java ``<<`` on ints: shift count is masked to 5 bits, result wraps."""
    return int32(int(v) << (n & 31))


def ishr(v: int, n: int) -> int:
    """Java ``>>`` on ints: arithmetic shift, shift count masked to 5 bits."""
    return int32(v) >> (n & 31)


def iushr(v: int, n: int) -> int:
    """Java ``>>>`` on ints: logical shift, shift count masked to 5 bits."""
    return int32((int32(v) & 0xFFFFFFFF) >> (n & 31))


# --------------------------------------------------------------------------
# Integer division and remainder
# --------------------------------------------------------------------------
# Java's `/` on ints truncates toward zero and `%` takes the sign of the
# dividend. Python's `//` floors and `%` takes the sign of the divisor.
# These agree for non-negative operands and diverge silently otherwise.


def idiv(a: int, b: int) -> int:
    """Java integer ``/``: truncate toward zero."""
    q = abs(a) // abs(b)
    return -q if (a < 0) != (b < 0) else q


def imod(a: int, b: int) -> int:
    """Java integer ``%``: remainder takes the sign of the dividend."""
    return a - idiv(a, b) * b


# --------------------------------------------------------------------------
# java.awt.Point
# --------------------------------------------------------------------------
# Used by 155 files in the Java tree, frequently mutated in place after
# construction and also used as a hash key. Java allows both; so do we.


class Point:
    """Mutable 2-D integer point, mirroring ``java.awt.Point``."""

    __slots__ = ("x", "y")

    def __init__(self, x: int = 0, y: int = 0):
        if isinstance(x, Point):
            self.x, self.y = x.x, x.y
        else:
            self.x = int(x)
            self.y = int(y)

    def setLocation(self, x: int, y: int) -> None:
        self.x = int(x)
        self.y = int(y)

    def getX(self) -> float:
        return float(self.x)

    def getY(self) -> float:
        return float(self.y)

    def distance(self, other: "Point") -> float:
        return math.hypot(self.x - other.x, self.y - other.y)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Point) and self.x == other.x and self.y == other.y

    def __hash__(self) -> int:
        return hash((self.x, self.y))

    def __repr__(self) -> str:
        return f"Point(x={self.x},y={self.y})"


__all__ = [
    "INT_MIN",
    "INT_MAX",
    "LONG_MIN",
    "LONG_MAX",
    "DOUBLE_MAX_VALUE",
    "Point",
    "Point2D",
    "jround",
    "jround_arr",
    "jint",
    "jint_arr",
    "jbyte",
    "jbyte_arr",
    "int32",
    "int32_arr",
    "ishl",
    "ishr",
    "iushr",
    "idiv",
    "imod",
]


class Point2D:
    """Mutable 2-D floating point, mirroring ``java.awt.geom.Point2D.Double``."""

    __slots__ = ("x", "y")

    def __init__(self, x: float = 0.0, y: float = 0.0):
        self.x = float(x)
        self.y = float(y)

    def getX(self) -> float:
        return self.x

    def getY(self) -> float:
        return self.y

    def setLocation(self, x: float, y: float) -> None:
        self.x = float(x)
        self.y = float(y)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Point2D) and self.x == other.x and self.y == other.y

    def __hash__(self) -> int:
        return hash((self.x, self.y))

    def __repr__(self) -> str:
        return f"Point2D(x={self.x},y={self.y})"

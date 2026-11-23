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


def jsplit(s: str, sep: str) -> list[str]:
    """Java ``String.split(regex)`` with no limit argument, for a plain
    (non-regex-metacharacter) literal separator like ``","``: splits on
    every occurrence, then drops *all trailing* empty strings from the
    result, however many -- unlike Python's ``str.split``, which keeps
    them (``"1,2,".split(",")`` is ``["1", "2"]`` in Java, ``["1", "2",
    ""]`` in Python; ``",".split(",")`` is `[]` in Java -- verified
    directly against a JVM, not assumed -- stripping all the way down to
    an empty result, not `[""]`). Leading/middle empty strings are
    preserved by both. Splitting the empty string is a separate,
    hardcoded JDK special case (``"".split(",")`` is ``[""]``, never
    `[]`) handled explicitly, since the general trailing-strip rule alone
    would give the wrong answer for it."""
    if s == "":
        return [""]
    parts = s.split(sep)
    while parts and parts[-1] == "":
        parts.pop()
    return parts


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


def jfloat(a: float) -> float:
    """Java ``(float)`` cast applied to a double: round to IEEE-754 binary32
    precision, then widen back to a Python (binary64) float -- the same
    round-trip Java performs implicitly whenever a ``float`` expression is
    used somewhere a ``double`` is expected. NaN/Infinity/sign are
    preserved; a magnitude beyond float range overflows to +-Infinity,
    matching Java (this is standard IEEE-754 narrowing, not a Java quirk,
    but Python has no native 32-bit float so it doesn't happen for free)."""
    with np.errstate(over="ignore"):
        # A magnitude beyond float range is expected to silently overflow to
        # +-Infinity here (same as Java) rather than warn -- pytest's
        # filterwarnings turns RuntimeWarning into an error project-wide.
        return float(np.float32(a))


def jfloat_div(a: float, b: float) -> float:
    """Java ``float / float`` (or ``float / int``, promoted to float by
    JLS 5.6.2 binary numeric promotion since neither operand is ``double``):
    a *single* correctly-rounded IEEE-754 binary32 division, not
    double-precision division with the result rounded down to float
    afterwards -- those two can disagree (classic double rounding). Also
    doesn't raise on a zero divisor, matching Java."""
    with np.errstate(over="ignore", divide="ignore", invalid="ignore"):
        return float(np.float32(a) / np.float32(b))


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
# Floating-point arithmetic that does not raise
# --------------------------------------------------------------------------
# Java follows IEEE-754 for doubles and never throws on a bad operand:
#
#     0.0 / 0.0   -> NaN            Python: ZeroDivisionError
#     1.0 / 0.0   -> Infinity       Python: ZeroDivisionError
#     Math.sqrt(-1) -> NaN          Python: ValueError
#     Math.log(0) -> -Infinity      Python: ValueError
#     Math.acos(2) -> NaN           Python: ValueError
#
# The library relies on this. Degenerate inputs -- an empty region with zero
# volume, a zero-norm vector, an out-of-domain cosine from accumulated
# rounding -- propagate quietly as NaN and are filtered downstream by
# comparisons, which are all false against NaN. A Python port that raises
# instead turns those into crashes on inputs the Java handles.
#
# Use these wherever the Java divides by a *computed* quantity or takes a
# root/log/inverse-trig of one. A literal or provably non-zero divisor needs
# no wrapper.


def jdiv(a: float, b: float) -> float:
    """Java ``/`` on doubles: IEEE-754, no exception on a zero divisor."""
    if b == 0.0:
        a = float(a)
        if a != a or a == 0.0:
            return math.nan
        # Sign of the result follows the signs of both operands, including
        # the sign of a negative zero divisor.
        neg = (math.copysign(1.0, a) < 0) != (math.copysign(1.0, b) < 0)
        return -math.inf if neg else math.inf
    return a / b


def jdiv_arr(a: np.ndarray, b: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jdiv` over float arrays. Verified against the
    scalar version across the full sign/zero/inf/nan cross product (signed
    zero divisors included) before use, since plain ``a / b`` alone only
    reproduces the ``b != 0`` case -- the zero-divisor branch needs the
    same sign-of-both-operands logic reproduced explicitly."""
    a = np.asarray(a, dtype=np.float64)
    b = np.asarray(b, dtype=np.float64)
    with np.errstate(divide="ignore", invalid="ignore"):
        out = a / b
    zero_b = b == 0.0
    if np.any(zero_b):
        nan_or_zero_a = np.isnan(a) | (a == 0.0)
        sign = np.copysign(1.0, a) * np.copysign(1.0, b)
        special = np.where(nan_or_zero_a, np.nan, np.inf * sign)
        out = np.where(zero_b, special, out)
    return out


def _libm_map(fn, *arrays: np.ndarray) -> np.ndarray:
    """Apply scalar ``fn`` (a ``math``-backed Java-semantics wrapper) to
    every element, broadcasting like a ufunc, and return a float64 array.

    Why not the numpy ufunc (``np.sin``, ``np.exp``, ``np.log10``,
    ``np.arctan2``, ``np.power``, ...): numpy picks its transcendental
    kernels by CPU at runtime. On AVX-512 machines it uses SIMD
    implementations that differ from libm -- and from the Java oracle --
    by one to several ulps, while on other CPUs it matches libm. That made
    the bit-exact golden tests pass or fail depending on which GitHub
    runner a job landed on. ``math.*`` always calls libm, so it gives the
    same answer on every x86_64 CPU. Only ``np.sqrt`` and the basic
    arithmetic operators are exempt: IEEE 754 requires them to be
    correctly rounded, so every implementation agrees.

    ``np.frompyfunc`` still calls ``fn`` once per element in Python, so this
    is slower than a ufunc; correctness over speed, as everywhere in this
    port."""
    arrs = [np.asarray(a, dtype=np.float64) for a in arrays]
    # `fn` already returns Java's value for every special case; numpy would
    # otherwise re-raise the FPU flags libm set along the way (e.g. overflow
    # in exp) as RuntimeWarnings, which the test config turns into errors.
    with np.errstate(all="ignore"):
        out = np.frompyfunc(fn, len(arrs), 1)(*arrs)
    return np.asarray(out, dtype=np.float64)


def jsqrt(x: float) -> float:
    """Java ``Math.sqrt``: NaN for a negative operand."""
    if x != x or x < 0.0:
        return math.nan
    return math.sqrt(x)


def jlog(x: float) -> float:
    """Java ``Math.log``: -Infinity at 0, NaN below it."""
    if x != x or x < 0.0:
        return math.nan
    if x == 0.0:
        return -math.inf
    return math.log(x)


def jlog10(x: float) -> float:
    """Java ``Math.log10``: -Infinity at 0, NaN below it -- same shape as
    ``jlog``, just base 10."""
    if x != x or x < 0.0:
        return math.nan
    if x == 0.0:
        return -math.inf
    return math.log10(x)


def jlog10_arr(x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jlog10`, through libm (see :func:`_libm_map`), not
    ``np.log10``, whose SIMD kernels are CPU-dependent."""
    return _libm_map(jlog10, x)


def jpow(a: float, b: float) -> float:
    """Java ``Math.pow``: total on the extended reals (NaN/Infinity/zero
    inputs never raise), per the special cases documented on
    ``Math.pow(double, double)``. Needed anywhere the exponent or base can be
    an arbitrary pixel value or user parameter rather than a known-benign
    (non-negative base, or integer exponent) quantity -- unlike Python's
    ``**``, which silently promotes a negative base with a non-integer
    exponent to a *complex* result instead of NaN, and unlike
    ``math.pow``, which raises ``ValueError``/``OverflowError`` instead of
    returning NaN/Infinity.

    One real behavioural divergence from ``math.pow`` beyond raise-vs-return:
    Java defines ``pow(+-1.0, +-Infinity) == NaN``, where C99/Python's
    ``math.pow`` (and IEEE 754-2008) return ``1.0``. Handled explicitly
    below; everything else here is just un-raising Python's exceptions.
    """
    if b == 0.0:
        return 1.0
    if b == 1.0:
        return a
    if b != b:
        return math.nan
    if a != a:
        return math.nan
    if (a == 1.0 or a == -1.0) and math.isinf(b):
        return math.nan
    if math.isinf(b):
        if abs(a) > 1.0:
            return math.inf if b > 0.0 else 0.0
        return 0.0 if b > 0.0 else math.inf
    if math.isinf(a):
        if a > 0.0:
            return math.inf if b > 0.0 else 0.0
        odd = _is_odd_integer(b)
        if b > 0.0:
            return -math.inf if odd else math.inf
        return -0.0 if odd else 0.0
    if a == 0.0:
        neg_zero = math.copysign(1.0, a) < 0.0
        if not neg_zero:
            return 0.0 if b > 0.0 else math.inf
        odd = _is_odd_integer(b)
        if b > 0.0:
            return -0.0 if odd else 0.0
        return -math.inf if odd else math.inf
    if a < 0.0 and b != math.floor(b):
        return math.nan
    try:
        return math.pow(a, b)
    except OverflowError:
        if a < 0.0 and _is_odd_integer(b):
            return -math.inf
        return math.inf
    except ValueError:
        return math.nan


def _is_odd_integer(b: float) -> bool:
    return b == math.floor(b) and (abs(b) % 2.0) == 1.0


def jpow_arr(a: np.ndarray, b: float) -> np.ndarray:
    """Elementwise :func:`jpow` over a float array base with a single fixed
    scalar exponent ``b`` -- the only shape any caller needs (``point.Power``:
    one user-supplied exponent applied to every pixel). Because ``b`` is a
    plain Python float rather than array-valued, every one of ``jpow``'s
    ``b``-only branches (``b == 0``, ``b == 1``, NaN, +-Infinity, odd-integer,
    sign) collapses to a scalar Python bool/float computed once outside the
    array expression; the branches that also inspect ``a`` (NaN, +-Infinity,
    zero, negative-non-integer-exponent) are vectorised via ``np.where``
    selects over precomputed scalar leaf values. The remaining "generic"
    elements (finite, nonzero, exponentiable) are deliberately *not* run
    through ``np.power``: swept against ``math.pow`` across a wide base/
    exponent grid, the two disagreed by 1 ulp for a small but nonzero
    fraction of inputs (observed e.g. at exponent -1.0 and 2.0) -- NumPy's
    ufunc takes a different code path than CPython's ``float.__pow__``/
    ``math.pow`` (verified to always agree with each other bit-exactly, so
    either could stand in for the scalar ``jpow`` this replaces, but
    ``np.power`` could not). They go through a per-element ``math.pow`` call
    instead, with the same ``OverflowError``/``ValueError`` fallback as the
    scalar version -- everything else about this function is fully
    vectorised, only the base of the final exponentiation itself is not.
    Verified bit-exact against the scalar ``jpow`` swept over a base grid
    (0, -0, +-1, +-inf, nan, subnormal, general positive/negative
    magnitudes, values beyond double range) crossed with a representative
    exponent set (0, 1, nan, +-inf, positive/negative odd and even integers,
    non-integers) before use."""
    a = np.asarray(a, dtype=np.float64)
    b = float(b)

    if b == 0.0:
        return np.ones_like(a)
    if b == 1.0:
        return a.copy()
    if b != b:  # NaN exponent
        return np.full_like(a, math.nan)

    nan_a = np.isnan(a)

    if math.isinf(b):
        pos_branch = math.inf if b > 0.0 else 0.0
        neg_branch = 0.0 if b > 0.0 else math.inf
        with np.errstate(invalid="ignore"):
            abs_gt1 = np.abs(a) > 1.0
        one_mask = (a == 1.0) | (a == -1.0)
        out = np.where(abs_gt1, pos_branch, neg_branch)
        out = np.where(one_mask, math.nan, out)
        out = np.where(nan_a, math.nan, out)
        return out

    # b is finite, nonzero, and not one: every b-only quantity below is a
    # scalar, computed once.
    b_odd = _is_odd_integer(b)
    b_is_integer = b == math.floor(b)
    b_pos = b > 0.0

    pos_inf_val = math.inf if b_pos else 0.0
    neg_inf_val = (-math.inf if b_odd else math.inf) if b_pos else (-0.0 if b_odd else 0.0)
    pos_zero_val = 0.0 if b_pos else math.inf
    neg_zero_val = (-0.0 if b_odd else 0.0) if b_pos else (-math.inf if b_odd else math.inf)

    inf_a = np.isinf(a)
    zero_a = a == 0.0
    neg_zero_a = np.signbit(a)
    neg_noninteger = (a < 0.0) & (not b_is_integer)

    # The remaining ("generic") elements go through `math.pow` one at a time
    # rather than `np.power`, deliberately not vectorised: `np.power` was
    # checked against `math.pow` across a wide base/exponent sweep and the
    # two disagree by 1 ulp for a small but nonzero fraction of inputs (seen
    # e.g. at exponent -1.0 and 2.0) -- NumPy's ufunc takes a different
    # code path than CPython's `float.__pow__`/`math.pow` (both of which
    # agree with each other bit-exactly). Since the whole point is to stay
    # bit-identical to the scalar `jpow` this replaces, the per-element
    # `math.pow` call is kept; only the surrounding branch dispatch and the
    # NaN/Infinity/zero/negative-non-integer special cases are vectorised.
    special = nan_a | inf_a | zero_a | neg_noninteger
    out = np.empty_like(a)
    generic_idx = np.flatnonzero(~special)
    # `.flat` so any array shape works (flatnonzero yields flat indices).
    a_flat, out_flat = a.flat, out.flat
    for j in generic_idx:
        av = float(a_flat[j])
        try:
            out_flat[j] = math.pow(av, b)
        except OverflowError:
            out_flat[j] = -math.inf if (av < 0.0 and b_odd) else math.inf
        except ValueError:
            out_flat[j] = math.nan

    out = np.where(neg_noninteger, math.nan, out)
    out = np.where(zero_a, np.where(neg_zero_a, neg_zero_val, pos_zero_val), out)
    out = np.where(inf_a, np.where(a > 0.0, pos_inf_val, neg_inf_val), out)
    out = np.where(nan_a, math.nan, out)
    return out


def jacos(x: float) -> float:
    """Java ``Math.acos``: NaN outside [-1, 1]."""
    if x != x or x < -1.0 or x > 1.0:
        return math.nan
    return math.acos(x)


def jacos_arr(x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jacos`, through libm (see :func:`_libm_map`)."""
    return _libm_map(jacos, x)


def jasin(x: float) -> float:
    """Java ``Math.asin``: NaN outside [-1, 1]."""
    if x != x or x < -1.0 or x > 1.0:
        return math.nan
    return math.asin(x)


def jatan(x: float) -> float:
    """Java ``Math.atan``: total on the extended reals, unlike Python's for
    infinite input only in that Python already handles it -- provided for
    symmetry and to keep call sites uniform."""
    if x != x:
        return math.nan
    return math.atan(x)


def jatan2(y: float, x: float) -> float:
    """Java ``Math.atan2``. Domain-total on both platforms, so this wrapper
    adds no NaN/edge-case handling -- it exists only so call sites go through
    `_javacompat` uniformly, and to anchor this comment:

    ``java.lang.Math`` (unlike ``StrictMath``) is *not* specified to be
    bit-reproducible across platforms -- the JLS only requires results within
    1-2 ulp of correctly rounded and semi-monotonic, deliberately leaving
    room for a JIT/platform-specific implementation. ``Math.atan2`` hits that
    slack in practice: verified against the oracle (JDK 21, x86_64 Linux),
    ``linear.Sobel``'s THETA branch disagrees with Python's ``math.atan2``
    (glibc) by 1 ulp at pixels where the convolved x-gradient is a tiny
    nonzero residual (not exactly 0.0) rather than a true axis-aligned case
    -- e.g. atan2(2.039..., -8.33e-17) rounds to a different double than
    atan2(2.039..., 0.0) would, and the two libraries' general-case
    algorithms round that last bit differently. `numpy.arctan2` was checked
    too and agrees with `math.atan2`, not Java, so this is a genuine
    fdlibm-vs-glibc divergence, not a Python-specific one. Because
    ``Math.atan2`` isn't even guaranteed to reproduce across *Java* platforms,
    chasing bit-exact parity with this one oracle run isn't a well-defined
    target the way ``Math.sqrt`` (IEEE-754 mandates correct rounding) is.
    Accepted as a known divergence -- see ``python/README.md``.
    """
    if y != y or x != x:
        return math.nan
    return math.atan2(y, x)


def jatan2_arr(y: np.ndarray, x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jatan2`, through libm (see :func:`_libm_map`).
    Callers inherit the platform-atan2 caveat ``jatan2`` documents above."""
    return _libm_map(jatan2, y, x)


def jsin(x: float) -> float:
    """Java ``Math.sin``: NaN for NaN or +-Infinity (``math.sin`` raises
    ``ValueError`` on infinity instead)."""
    if x != x or math.isinf(x):
        return math.nan
    return math.sin(x)


def jcos(x: float) -> float:
    """Java ``Math.cos``: NaN for NaN or +-Infinity."""
    if x != x or math.isinf(x):
        return math.nan
    return math.cos(x)


def jexp(x: float) -> float:
    """Java ``Math.exp``: +Infinity on overflow (``math.exp`` raises
    ``OverflowError`` instead)."""
    try:
        return math.exp(x)
    except OverflowError:
        return math.inf


def jsin_arr(x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jsin`, through libm (see :func:`_libm_map`)."""
    return _libm_map(jsin, x)


def jcos_arr(x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jcos`, through libm (see :func:`_libm_map`)."""
    return _libm_map(jcos, x)


def jexp_arr(x: np.ndarray) -> np.ndarray:
    """Elementwise :func:`jexp`, through libm (see :func:`_libm_map`)."""
    return _libm_map(jexp, x)


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

    def distance(self, other: Point) -> float:
        return math.hypot(self.x - other.x, self.y - other.y)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Point) and self.x == other.x and self.y == other.y

    def __hash__(self) -> int:
        return hash((self.x, self.y))

    def __repr__(self) -> str:
        return f"Point(x={self.x},y={self.y})"


__all__ = [
    "DOUBLE_MAX_VALUE",
    "INT_MAX",
    "INT_MIN",
    "LONG_MAX",
    "LONG_MIN",
    "Point",
    "Point2D",
    "idiv",
    "imod",
    "int32",
    "int32_arr",
    "ishl",
    "ishr",
    "iushr",
    "jacos",
    "jacos_arr",
    "jasin",
    "jatan",
    "jatan2",
    "jatan2_arr",
    "jbyte",
    "jbyte_arr",
    "jdiv",
    "jdiv_arr",
    "jfloat",
    "jfloat_div",
    "jint",
    "jint_arr",
    "jlog",
    "jlog10",
    "jlog10_arr",
    "jpow",
    "jpow_arr",
    "jround",
    "jround_arr",
    "jsplit",
    "jsqrt",
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

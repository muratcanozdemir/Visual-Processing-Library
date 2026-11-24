"""The correctly rounded functions in ``vpt._native`` (``python/src/cm``,
Rust translations of CORE-MATH) against outputs frozen from the original
CORE-MATH C, bit for bit.

``tests/goldens/crmath.txt`` was generated once, before the C was removed,
by calling the C build of each function on 2,000 inputs per function --
weighted towards the inputs that reach CORE-MATH's accurate phases
(multiples of pi/2^k for sin/cos/tan, arguments near 1 for log/log10/pow)
as well as uniform ranges and random bit patterns. It can't be regenerated
from this tree; the translation itself was verified against the C far more
extensively (IMPROVEMENTS.md section 9.1). This test keeps checking it on
every platform the wheels are built for -- including that ``f64::mul_add``
is a true fused multiply-add there, which the accurate phases depend on.
"""

from __future__ import annotations

import struct
from pathlib import Path

import numpy as np
import pytest

from vpt import _accel

GOLDEN = Path(__file__).parent / "goldens" / "crmath.txt"
native = _accel.native

pytestmark = pytest.mark.skipif(native is None, reason="needs vpt._native")


def _f(h: str) -> float:
    return struct.unpack("<d", struct.pack("<Q", int(h, 16)))[0]


def _bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def _rows():
    for line in GOLDEN.read_text().splitlines():
        fn, x, y, r = line.split("\t")
        yield fn, _f(x), _f(y), _f(r)


def _same(a: float, b: float) -> bool:
    return (a != a and b != b) or _bits(a) == _bits(b)


def test_scalar():
    bad = []
    for fn, x, y, want in _rows():
        got = native.pow(x, y) if fn == "pow" else getattr(native, fn)(x)
        if not _same(got, want):
            bad.append(f"{fn}({x!r}, {y!r}): got {got.hex()} want {want.hex()}")
    assert not bad, f"{len(bad)} mismatches:\n  " + "\n  ".join(bad[:40])


def test_arrays():
    by_fn: dict[str, list] = {}
    for fn, x, y, want in _rows():
        by_fn.setdefault(fn, []).append((x, y, want))
    bad = []
    for fn, rows in by_fn.items():
        if fn == "pow":
            for x, y, want in rows:
                got = np.frombuffer(native.pow_arr(np.array([x]).tobytes(), y))[0]
                if not _same(float(got), want):
                    bad.append(f"pow_arr({x!r}, {y!r})")
            continue
        xs = np.array([r[0] for r in rows])
        got = np.frombuffer(getattr(native, fn + "_arr")(xs.tobytes()))
        bad += [f"{fn}_arr({r[0]!r})" for r, g in zip(rows, got) if not _same(float(g), r[2])]
    assert not bad, f"{len(bad)} mismatches:\n  " + "\n  ".join(bad[:40])


def test_fma_is_fused():
    """The accurate phases rely on f64::mul_add rounding once. On targets
    without hardware FMA it calls the platform's fma(); these cases come
    out differently if that is not truly fused."""
    # (1 + 2^-30)^2 - 1 - 2^-29 = 2^-60 exactly with one rounding; a
    # separate multiply rounds the 2^-60 term away and gives 0.
    a = 1.0 + 2.0**-30
    assert native.fma(a, a, -(1.0 + 2.0**-29)) == 2.0**-60
    # Error term of a product: fma(x, y, -x*y) recovers the rounding error.
    x, y = 0.1, 10.0
    assert native.fma(x, y, -(x * y)) == 5.551115123125783e-17

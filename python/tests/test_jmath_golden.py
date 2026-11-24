"""``java.lang.Math`` transcendentals vs the port's ``j*`` functions, bit
for bit, on every platform.

Golden produced by ``oracle/DumpJMath.java`` (JDK 21, x86_64 HotSpot):
special values plus seeded random inputs for sin, cos, tan, exp, log,
log10, pow, asin, acos, atan and atan2.

Through ``vpt._native`` (``python/src/jmath.rs``) every row must match
exactly, except the ``INTEL_MISROUNDED`` rows below: hard-to-round cases
(the exact value lies within ~1% of an ulp of the midpoint between two
doubles) where HotSpot's ``sin``/``cos``/``tan``/``exp`` intrinsics round
to the farther double. Checked at 200-bit precision with mpmath: in every
one the port's correctly rounded value is the nearer one. Those rows must
give exactly the recorded correctly-rounded value -- so the set is fixed,
and any platform where it differs fails.

Skipped without the extension: the ``math`` fallback is the platform libm,
which matches the other goldens only on Linux/glibc.
"""

from __future__ import annotations

import struct
from pathlib import Path

import numpy as np
import pytest

from vpt import _accel
from vpt._javacompat import (
    jacos,
    jacos_arr,
    jasin,
    jasin_arr,
    jatan,
    jatan2,
    jatan2_arr,
    jatan_arr,
    jcos,
    jcos_arr,
    jexp,
    jexp_arr,
    jlog,
    jlog10,
    jlog10_arr,
    jlog_arr,
    jpow,
    jsin,
    jsin_arr,
    jtan,
    jtan_arr,
)

GOLDEN = Path(__file__).parent / "goldens" / "jmath.txt"

pytestmark = pytest.mark.skipif(
    _accel.native is None, reason="needs vpt._native (platform libm otherwise)"
)

UNARY = {
    "sin": (jsin, jsin_arr),
    "cos": (jcos, jcos_arr),
    "tan": (jtan, jtan_arr),
    "exp": (jexp, jexp_arr),
    "log": (jlog, jlog_arr),
    "log10": (jlog10, jlog10_arr),
    "asin": (jasin, jasin_arr),
    "acos": (jacos, jacos_arr),
    "atan": (jatan, jatan_arr),
}

# (fn, x bits) -> the port's (correctly rounded) result bits; Java's
# intrinsic gives the neighbouring double.
INTEL_MISROUNDED = {
    ("tan", "bfe4c0facdf6f900"): "bfe840f6a75d39de",
    ("cos", "4021664b891b81ae"): "bfe7f3ccd0032e08",
    ("tan", "c00d4578cb5797c8"): "bfe2356b20dad93d",
    ("exp", "4043745764fef17c"): "43718dbe0c2e7c43",
    ("cos", "40521a26dc2dbf40"): "bfefa18f3ad3ead0",
    ("cos", "c075e5669892f060"): "3fa9928bdd561659",
    ("sin", "c0195c53d5f75037"): "bfad2836874dfd51",
    ("exp", "c03ab8e62b9c1a12"): "3d85d342472214ef",
    ("tan", "c08ce227e9c462c7"): "bfe7e337b479a3d8",
    ("tan", "408594df1d341b6e"): "bfe33e977d74ae69",
    ("sin", "c018882592571890"): "3fc328394e201a6b",
}


def _f(h: str) -> float:
    return struct.unpack("<d", struct.pack("<Q", int(h, 16)))[0]


def _bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def _same(got: float, want: float) -> bool:
    # Java's NaN bit pattern isn't specified; any NaN matches NaN.
    return (got != got and want != want) or _bits(got) == _bits(want)


def _rows():
    for line in GOLDEN.read_text().splitlines():
        fn, x, y, r = line.split("\t")
        want = INTEL_MISROUNDED.get((fn, x), r)
        yield fn, x, _f(x), _f(y), _f(want)


def test_scalar_matches_java():
    failures = []
    for fn, xh, x, y, want in _rows():
        if fn == "pow":
            got = jpow(x, y)
        elif fn == "atan2":
            got = jatan2(x, y)
        else:
            got = UNARY[fn][0](x)
        if not _same(got, want):
            failures.append(f"{fn}({x!r}, {y!r}): got {got.hex()} want {want.hex()} [{xh}]")
    assert not failures, f"{len(failures)} mismatches:\n  " + "\n  ".join(failures[:40])


def test_arrays_match_scalars():
    """The vectorised ``*_arr`` paths give the same bits as the scalars."""
    by_fn: dict[str, list[tuple[float, float, float]]] = {}
    for fn, _, x, y, want in _rows():
        by_fn.setdefault(fn, []).append((x, y, want))
    failures = []
    for fn, rows in by_fn.items():
        xs = np.array([r[0] for r in rows])
        ys = np.array([r[1] for r in rows])
        if fn == "pow":
            continue  # jpow_arr takes one scalar exponent; covered below
        got = jatan2_arr(xs, ys) if fn == "atan2" else UNARY[fn][1](xs)
        for (x, y, want), g in zip(rows, got):
            if not _same(float(g), want):
                failures.append(f"{fn}_arr({x!r}, {y!r}): got {float(g).hex()} want {want.hex()}")
    assert not failures, f"{len(failures)} mismatches:\n  " + "\n  ".join(failures[:40])


def test_pow_arr_matches_scalar():
    from vpt._javacompat import jpow_arr

    rows = [(x, y, w) for fn, _, x, y, w in _rows() if fn == "pow"]
    exps = sorted({y for _, y, _ in rows}, key=_bits)
    bases = np.array(sorted({x for x, _, _ in rows}, key=_bits))
    failures = []
    for y in exps:
        got = jpow_arr(bases, y)
        for x, g in zip(bases, got):
            if not _same(float(g), jpow(float(x), y)):
                failures.append(f"pow_arr({x!r}, {y!r})")
    assert not failures, f"{len(failures)} mismatches:\n  " + "\n  ".join(failures[:40])

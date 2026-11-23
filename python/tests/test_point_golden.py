"""Differential test of the vpt.algorithms.point package (Inversion, Log,
Power) against the Java oracle.

The golden file is produced by ``oracle/DumpPoint.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from vpt import ByteImage, DoubleImage
from vpt.algorithms.point import Inversion, Log, Power

GOLDEN = Path(__file__).parent / "goldens" / "point.txt"


def parse_double(s: str) -> float:
    if s == "Infinity":
        return float("inf")
    if s == "-Infinity":
        return float("-inf")
    if s == "NaN":
        return float("nan")
    return float.fromhex(s)


def bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def double_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def make_bi() -> ByteImage:
    bi = ByteImage(4, 4, 1)
    for x in range(4):
        for y in range(4):
            bi.setXYCByte(x, y, 0, x * 4 + y)
    return bi


def make_di() -> DoubleImage:
    vals = [
        -2.0, -1.0, -0.5, 0.0,
        0.5, 1.0, 2.0, 3.0,
        -4.0, 10.0, 100.0, -0.0,
        1e150, -3.0, 0.25, -0.25,
    ]
    di = DoubleImage(4, 4, 1)
    i = 0
    for y in range(4):
        for x in range(4):
            di.setXYCDouble(x, y, 0, vals[i])
            i += 1
    return di


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_point_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_double(name, result, max_ulp=0):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = double_vals(result)
        if len(g_vals) != len(a_vals):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, a_vals):
            want = parse_double(g)
            if want != want:  # NaN: golden text carries no sign/payload bits
                if a == a:
                    failures.append(f"{name}: value mismatch python={a} java={g}")
                continue
            if bits(a) == bits(want):
                continue
            if (
                max_ulp
                and math.isfinite(a)
                and math.isfinite(want)
                and (a < 0) == (want < 0)
                and abs(bits(a) - bits(want)) <= max_ulp
            ):
                continue
            failures.append(f"{name}: value mismatch python={a} java={g}")

    bi = make_bi()
    check_double("Inversion(bi)", Inversion.invoke(bi))
    check_double("Log(bi)", Log.invoke(bi))
    check_double("Power(bi,2.0)", Power.invoke(bi, 2.0))
    check_double("Power(bi,0.5)", Power.invoke(bi, 0.5))

    di = make_di()
    check_double("Inversion(di)", Inversion.invoke(di))
    # Math.log10, like Math.atan2 (see Sobel's THETA branch), is not
    # specified to be bit-reproducible across platforms -- JDK 21/x86_64
    # Linux vs. glibc's log10 disagree by 1 ulp at log10(11) and
    # log10(0.75); see python/README.md "Known divergence".
    check_double("Log(di)", Log.invoke(di), max_ulp=1)
    check_double("Power(di,2.0)", Power.invoke(di, 2.0))
    check_double("Power(di,0.5)", Power.invoke(di, 0.5))
    check_double("Power(di,-1.0)", Power.invoke(di, -1.0))
    check_double("Power(di,3.0)", Power.invoke(di, 3.0))
    check_double("Power(di,0.0)", Power.invoke(di, 0.0))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

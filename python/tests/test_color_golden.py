"""Differential test of the vpt.algorithms.color package against the Java
oracle.

The golden file is produced by ``oracle/DumpColor.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.color import (
    BIC,
    AutoCorrelogramHSY733,
    ColorMomentInvariants,
    ColorMoments,
    ColorStructureRGB64,
)

GOLDEN = Path(__file__).parent / "goldens" / "color.txt"


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


def make_rgb() -> ByteImage:
    rgb = ByteImage(12, 12, 3)
    for x in range(12):
        for y in range(12):
            rgb.setXYCByte(x, y, 0, (x * 20) % 256)
            rgb.setXYCByte(x, y, 1, (y * 20) % 256)
            rgb.setXYCByte(x, y, 2, ((x + y) * 10) % 256)
    return rgb


def make_hsy733() -> ByteImage:
    hsy733 = ByteImage(12, 12, 3)
    for x in range(12):
        for y in range(12):
            hsy733.setXYCByte(x, y, 0, x % 3)
            hsy733.setXYCByte(x, y, 1, y % 3)
            hsy733.setXYCByte(x, y, 2, (x + y) % 7)
    return hsy733


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_color_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_vector(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[2].split(",") if len(row) > 2 and row[2] else []
        if len(g_vals) != len(result):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, result):
            if bits(parse_double(g)) != bits(a):
                failures.append(f"{name}: value mismatch python={a} java={g}")

    rgb = make_rgb()
    check_vector("ColorMoments(rgb)", ColorMoments.invoke(rgb))
    check_vector("ColorMomentInvariants(rgb)", ColorMomentInvariants.invoke(rgb))
    check_vector("BIC(rgb)", BIC.invoke(rgb))
    check_vector("ColorStructureRGB64(rgb)", ColorStructureRGB64.invoke(rgb))

    hsy733 = make_hsy733()
    check_vector(
        "AutoCorrelogramHSY733(hsy733)", AutoCorrelogramHSY733.invoke(hsy733)
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

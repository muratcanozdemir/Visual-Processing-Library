"""Differential test of the mm/gray/floating family (GFloatingDilation/
Erosion/Opening/Closing, FloatingCovariance, FloatingOpeningBasedCovariance)
against the Java oracle.

The golden file is produced by ``oracle/DumpFloating.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.mm.gray.floating import (
    FloatingCovariance,
    FloatingOpeningBasedCovariance,
    GFloatingClosing,
    GFloatingDilation,
    GFloatingErosion,
    GFloatingOpening,
)
from vpt.util.se import FloatingFlatSE

GOLDEN = Path(__file__).parent / "goldens" / "floating.txt"


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


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, str(img.getXDim()), str(img.getYDim()), str(img.getCDim()), ",".join(vals))


def make_img() -> ByteImage:
    img = ByteImage(20, 20, 1)
    img.fill(0)
    for x in range(8, 13):
        for y in range(8, 13):
            img.setXYByte(x, y, 200)
    return img


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_floating_matches_java_oracle():
    golden = load_golden()
    img = make_img()
    se = FloatingFlatSE.circle(3, 24)

    failures = []

    def check_gray(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        _, x, y, c, vals = gray_row(name, result)
        if (x, y, c) != (row[1], row[2], row[3]):
            failures.append(f"{name}: dims differ")
            return
        if vals != (row[4] if len(row) > 4 else ""):
            failures.append(f"{name}: pixel mismatch")

    def check_vector(name, values):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        if len(g_vals) != len(values):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, values):
            if bits(parse_double(g)) != bits(a):
                failures.append(f"{name}: value mismatch python={a} java={g}")

    check_gray("GFloatingDilation(img,circle3)", GFloatingDilation.invoke(img, se))
    check_gray("GFloatingErosion(img,circle3)", GFloatingErosion.invoke(img, se))
    check_gray("GFloatingOpening(img,circle3)", GFloatingOpening.invoke(img, se))
    check_gray("GFloatingClosing(img,circle3)", GFloatingClosing.invoke(img, se))

    check_vector("FloatingCovariance(img,4,2)", FloatingCovariance.invoke(img, 4, 2))
    check_vector(
        "FloatingOpeningBasedCovariance(img,4,2)",
        FloatingOpeningBasedCovariance.invoke(img, 4, 2),
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

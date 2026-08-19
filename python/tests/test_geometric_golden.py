"""Differential test of vpt.algorithms.geometric.{Crop,Flip,ImageCentroid,
ToPolar,Rotation,Resample,KeepLargestCC} against the Java oracle. ConvexHull
is golden-verified separately via test_recovered_golden.py.

The golden file is produced by ``oracle/DumpGeometric.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage, Point
from vpt.algorithms.geometric import (
    Crop,
    Flip,
    ImageCentroid,
    KeepLargestCC,
    Resample,
    Rotation,
    ToPolar,
)

GOLDEN = Path(__file__).parent / "goldens" / "geometric.txt"


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


def double_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def make_rgb() -> ByteImage:
    rgb = ByteImage(10, 8, 3)
    for x in range(10):
        for y in range(8):
            rgb.setXYCByte(x, y, 0, x * 20)
            rgb.setXYCByte(x, y, 1, y * 25)
            rgb.setXYCByte(x, y, 2, (x + y) * 5)
    return rgb


def make_g() -> ByteImage:
    g = ByteImage(10, 8, 1)
    for x in range(10):
        for y in range(8):
            g.setXYCByte(x, y, 0, 200 if x >= 5 and y >= 4 else 20)
    return g


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_geometric_matches_java_oracle():
    golden = load_golden()
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

    def check_double(name, result):
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
            if bits(parse_double(g)) != bits(a):
                failures.append(f"{name}: value mismatch python={a} java={g}")

    def check_point(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if (str(result.x), str(result.y)) != (row[1], row[2]):
            failures.append(f"{name}: mismatch python=({result.x},{result.y}) java=({row[1]},{row[2]})")

    def check_binary(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        bits_str = "".join(
            "1" if result.getXYCBoolean(x, y, c) else "0"
            for y in range(result.getYDim())
            for x in range(result.getXDim())
            for c in range(result.getCDim())
        )
        if bits_str != (row[4] if len(row) > 4 else ""):
            failures.append(f"{name}: pixel mismatch")

    rgb = make_rgb()

    check_gray("Flip(rgb,horizontal)", Flip.invoke(rgb, 0))
    check_gray("Flip(rgb,vertical)", Flip.invoke(rgb, 1))

    check_gray("Crop(rgb,(2,1),(8,6))", Crop.invoke(rgb, Point(2, 1), Point(8, 6)))
    check_gray("Crop(rgb,(8,6),(2,1))", Crop.invoke(rgb, Point(8, 6), Point(2, 1)))

    check_double("ToPolar(rgb)", ToPolar.invoke(rgb))

    g = make_g()
    check_point("ImageCentroid(g)", ImageCentroid.invoke(g))

    check_double("Rotation(g,30,NEAREST_NEIGHBOR)", Rotation.invoke(g, 30.0, 0))
    check_double("Rotation(g,30,BILINEAR)", Rotation.invoke(g, 30.0, 1))

    check_double("Resample(g,1.5,0.5,NEAREST_NEIGHBOR)", Resample.invoke(g, 1.5, 0.5, 0))
    check_double("Resample(g,1.5,1.5,BILINEAR)", Resample.invoke(g, 1.5, 1.5, 1))
    check_double("Resample(g,1.5,1.5,BICUBIC)", Resample.invoke(g, 1.5, 1.5, 2))

    bi = BooleanImage(10, 8, 1)
    bi.fill(False)
    for x in range(3):
        bi.setXYBoolean(x, 0, True)
    for x in range(5, 9):
        for y in range(3, 7):
            bi.setXYBoolean(x, y, True)
    check_binary("KeepLargestCC(bi)", KeepLargestCC.invoke(bi))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

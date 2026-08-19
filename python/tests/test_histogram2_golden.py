"""Differential test of the remaining vpt.algorithms.histogram operators
against the Java oracle: Equalization, CustomHistogram, Histogram2Image,
RGB27Histogram, RGB64Histogram, HSY{733,744,833}Histogram,
SaturationWeightedHueHistogram. Histogram and ContrastStretch are golden-
verified separately via test_linhist_golden.py.

The golden file is produced by ``oracle/DumpHistogram2.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.conversion import (
    RGB2HSY,
    NonUniformHSY733,
    NonUniformHSY744,
    NonUniformHSY833,
)
from vpt.algorithms.histogram import (
    CustomHistogram,
    Equalization,
    HSY733Histogram,
    HSY744Histogram,
    HSY833Histogram,
    Histogram,
    Histogram2Image,
    RGB27Histogram,
    RGB64Histogram,
    SaturationWeightedHueHistogram,
)

GOLDEN = Path(__file__).parent / "goldens" / "histogram2.txt"


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


def make_rgb() -> ByteImage:
    rgb = ByteImage(8, 8, 3)
    for x in range(8):
        for y in range(8):
            rgb.setXYCByte(x, y, 0, x * 30)
            rgb.setXYCByte(x, y, 1, y * 30)
            rgb.setXYCByte(x, y, 2, (x + y) * 15)
    return rgb


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_histogram2_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_double_img(name, result):
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

    def check_binary_img(name, result):
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

    check_double_img("Equalization(rgb)", Equalization.invoke(rgb))

    check_vector("CustomHistogram(rgb,false,256)", CustomHistogram.invoke(rgb, False, 256))
    check_vector("CustomHistogram(rgb,true,256)", CustomHistogram.invoke(rgb, True, 256))

    histo = Histogram.invoke(rgb, False)
    check_binary_img("Histogram2Image(histo)", Histogram2Image.invoke(histo))

    check_vector("RGB27Histogram(rgb,false)", RGB27Histogram.invoke(rgb, False))
    check_vector("RGB27Histogram(rgb,true)", RGB27Histogram.invoke(rgb, True))
    check_vector("RGB64Histogram(rgb,false)", RGB64Histogram.invoke(rgb, False))
    check_vector("RGB64Histogram(rgb,true)", RGB64Histogram.invoke(rgb, True))

    hsy733 = NonUniformHSY733.invoke(rgb)
    check_vector("HSY733Histogram(hsy733,false)", HSY733Histogram.invoke(hsy733, False))
    hsy744 = NonUniformHSY744.invoke(rgb)
    check_vector("HSY744Histogram(hsy744,false)", HSY744Histogram.invoke(hsy744, False))
    hsy833 = NonUniformHSY833.invoke(rgb)
    check_vector("HSY833Histogram(hsy833,false)", HSY833Histogram.invoke(hsy833, False))

    hsy = RGB2HSY.invoke(rgb)
    check_vector("SaturationWeightedHueHistogram(hsy)", SaturationWeightedHueHistogram.invoke(hsy))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

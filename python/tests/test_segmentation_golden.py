"""Differential test of vpt.algorithms.segmentation.{HysteresisThreshold,
Otsu,SuperposeBorders,BorderToDominantNeighborLabel,NumberOfSingletons,
LabelsToMeanValue,LabelsToMeanHue,MergeSingletons,OSR,Watershed,MP,
MarkerBasedWatershed} against the Java oracle. Threshold and OtsuThreshold
are golden-verified separately via test_linhist_golden.py. MeanShift has no
golden -- it's genuinely non-deterministic (calls Math.random() directly);
see its docstring.

The golden file is produced by ``oracle/DumpSegmentation.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage, DoubleImage, IntegerImage
from vpt.algorithms.segmentation import (
    MP,
    OSR,
    BorderToDominantNeighborLabel,
    HysteresisThreshold,
    LabelsToMeanHue,
    LabelsToMeanValue,
    MarkerBasedWatershed,
    MergeSingletons,
    NumberOfSingletons,
    Otsu,
    SuperposeBorders,
    Watershed,
)

GOLDEN = Path(__file__).parent / "goldens" / "segmentation.txt"


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


def gray_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return vals


def int_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCInt(x, y, c)))
    return vals


def make_hysteresis_g() -> DoubleImage:
    g = DoubleImage(16, 16, 1)
    for x in range(16):
        for y in range(16):
            d = max(abs(x - 8), abs(y - 8))
            if d <= 2:
                v = 0.9
            elif d <= 5:
                v = 0.5
            else:
                v = 0.1
            g.setXYCDouble(x, y, 0, v)
    return g


def make_bimodal() -> ByteImage:
    bimodal = ByteImage(12, 12, 1)
    for x in range(12):
        for y in range(12):
            inside = 3 <= x < 9 and 3 <= y < 9
            bimodal.setXYCByte(x, y, 0, 200 if inside else 20)
    return bimodal


def make_labels() -> IntegerImage:
    labels = IntegerImage(10, 8, 1)
    for x in range(10):
        for y in range(8):
            labels.setXYInt(x, y, 1 if x < 5 else 2)
    for y in range(8):
        labels.setXYInt(5, y, 0)
    labels.setXYInt(1, 1, 9)
    labels.setXYInt(8, 6, 9)
    return labels


def make_rgb() -> ByteImage:
    rgb = ByteImage(10, 8, 3)
    for x in range(10):
        for y in range(8):
            rgb.setXYCByte(x, y, 0, x * 20)
            rgb.setXYCByte(x, y, 1, y * 25)
            rgb.setXYCByte(x, y, 2, (x + y) * 10)
    return rgb


def make_hue() -> ByteImage:
    hue = ByteImage(10, 8, 1)
    for x in range(10):
        for y in range(8):
            hue.setXYCByte(x, y, 0, (x * 8 + y) % 256)
    return hue


def make_map1() -> IntegerImage:
    m = IntegerImage(8, 6, 1)
    for x in range(8):
        for y in range(6):
            m.setXYInt(x, y, x // 2)
    return m


def make_map2() -> IntegerImage:
    m = IntegerImage(8, 6, 1)
    for x in range(8):
        for y in range(6):
            m.setXYInt(x, y, x // 4)
    return m


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_segmentation_matches_java_oracle():
    golden = load_golden()
    failures = []

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

    def check_gray(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = gray_vals(result)
        if g_vals != a_vals:
            failures.append(f"{name}: pixel mismatch")

    def check_int(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = int_vals(result)
        if g_vals != a_vals:
            failures.append(f"{name}: pixel mismatch")

    def check_double_value(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if bits(parse_double(row[1])) != bits(result):
            failures.append(f"{name}: value mismatch python={result} java={row[1]}")

    def check_int_value(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if str(result) != row[1]:
            failures.append(f"{name}: value mismatch python={result} java={row[1]}")

    g = make_hysteresis_g()

    check_double("HysteresisThreshold(g,0.3,0.8)", HysteresisThreshold.invoke(g, 0.3, 0.8))
    check_double("HysteresisThreshold(g,-0.5,1.5)", HysteresisThreshold.invoke(g, -0.5, 1.5))

    bimodal = make_bimodal()
    check_int_value("Otsu(bimodal)", Otsu.invoke(bimodal))

    labels = make_labels()
    rgb = make_rgb()

    check_gray(
        "SuperposeBorders(labels,rgb,red)", SuperposeBorders.invoke(labels, rgb, [255, 0, 0])
    )
    check_int(
        "BorderToDominantNeighborLabel(labels)", BorderToDominantNeighborLabel.invoke(labels)
    )
    check_int_value("NumberOfSingletons(labels)", NumberOfSingletons.invoke(labels))
    check_double("LabelsToMeanValue(labels,rgb)", LabelsToMeanValue.invoke(labels, rgb))

    hue = make_hue()
    check_double("LabelsToMeanHue(labels,hue)", LabelsToMeanHue.invoke(labels, hue))

    check_int("MergeSingletons(labels,rgb)", MergeSingletons.invoke(labels, rgb))

    map1 = make_map1()
    map2 = make_map2()
    check_double_value("OSR(map1,map2)", OSR.invoke(map1, map2))
    check_double_value("MP(map1,map2)", MP.invoke(map1, map2))

    check_int("Watershed(bimodal)", Watershed.invoke(bimodal))
    check_int("MarkerBasedWatershed(bimodal)", MarkerBasedWatershed.invoke(bimodal))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

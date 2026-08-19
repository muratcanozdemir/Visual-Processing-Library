"""Differential test of the vpt.algorithms.conversion package against the
Java oracle. RGB2Gray is golden-verified separately via
test_linhist_golden.py.

The golden file is produced by ``oracle/DumpConversion.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from vpt import ByteImage, DoubleImage
from vpt.algorithms.conversion import (
    HSY2RGB,
    LAB2XYZ,
    RGB2AVG,
    RGB2HSY,
    RGB2NUHSY733,
    RGB2RGB64,
    RGB2XYZ,
    RGB2YUV,
    XYZ2LAB,
    XYZ2RGB,
    YUV2RGB,
    Angular2Color,
    DistanceFromHSYColor,
    EuclideanDistanceFromLABColor,
    EuclideanDistanceFromRGBColor,
    Hue2Distance,
    NonUniformHSY733,
    NonUniformHSY744,
    NonUniformHSY833,
    RGB2cRGB,
)

GOLDEN = Path(__file__).parent / "goldens" / "conversion.txt"


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


def make_angular() -> DoubleImage:
    angular = DoubleImage(8, 8, 1)
    for x in range(8):
        for y in range(8):
            angular.setXYCDouble(x, y, 0, (x * 8 + y) / 64.0)
    return angular


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_conversion_matches_java_oracle():
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
            if want != want:  # NaN
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

    rgb = make_rgb()

    check_double("RGB2AVG(rgb)", RGB2AVG.invoke(rgb))
    check_double("RGB2RGB64(rgb)", RGB2RGB64.invoke(rgb))
    check_double("RGB2cRGB(rgb)", RGB2cRGB.invoke(rgb))
    check_double("RGB2XYZ(rgb)", RGB2XYZ.invoke(rgb))
    check_double("RGB2YUV(rgb)", RGB2YUV.invoke(rgb))
    # Math.acos, like Math.atan2/Math.log10 elsewhere in this port, isn't
    # specified to be bit-reproducible across platforms -- JDK 21/x86_64
    # Linux vs. glibc's acos disagree by up to 2 ulp at some inputs; see
    # python/README.md "Known divergence".
    check_double("RGB2HSY(rgb)", RGB2HSY.invoke(rgb), max_ulp=2)
    check_double("RGB2NUHSY733(rgb)", RGB2NUHSY733.invoke(rgb), max_ulp=2)
    check_double("NonUniformHSY733(rgb)", NonUniformHSY733.invoke(rgb))
    check_double("NonUniformHSY744(rgb)", NonUniformHSY744.invoke(rgb))
    check_double("NonUniformHSY833(rgb)", NonUniformHSY833.invoke(rgb))

    xyz = RGB2XYZ.invoke(rgb)
    check_double("XYZ2RGB(xyz)", XYZ2RGB.invoke(xyz))
    check_double("XYZ2LAB(xyz)", XYZ2LAB.invoke(xyz))

    yuv = RGB2YUV.invoke(rgb)
    check_double("YUV2RGB(yuv)", YUV2RGB.invoke(yuv))

    hsy = RGB2HSY.invoke(rgb)
    check_double("HSY2RGB(hsy)", HSY2RGB.invoke(hsy))

    lab = XYZ2LAB.invoke(xyz)
    check_double("LAB2XYZ(lab)", LAB2XYZ.invoke(lab))
    check_double("XYZ2LAB.scaleToByte(lab)", XYZ2LAB.scaleToByte(lab))

    angular = make_angular()
    check_double("Angular2Color(angular)", Angular2Color.invoke(angular))
    check_double("Hue2Distance(angular,0.3)", Hue2Distance.invoke(angular, 0.3))

    check_double(
        "DistanceFromHSYColor(hsy,0.8,0.2,0.1)",
        DistanceFromHSYColor.invoke(hsy, 0.8, 0.2, 0.1),
        max_ulp=2,
    )

    check_double(
        "EuclideanDistanceFromRGBColor(rgb,0.5,0.5,0.5,false)",
        EuclideanDistanceFromRGBColor.invoke(rgb, 0.5, 0.5, 0.5, False),
    )
    check_double(
        "EuclideanDistanceFromRGBColor(rgb,0.5,0.5,0.5,true)",
        EuclideanDistanceFromRGBColor.invoke(rgb, 0.5, 0.5, 0.5, True),
    )

    check_double(
        "EuclideanDistanceFromLABColor(rgb,0.8,0.2,0.1)",
        EuclideanDistanceFromLABColor.invoke(rgb, 0.8, 0.2, 0.1),
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

"""Differential test of the vpt.algorithms.texture package (top-level 23
files) against the Java oracle.

Not covered here: ``SpatialLineGranulometry`` with ``valuation=MOMENT``
(4) -- a confirmed bug (see that file's docstring) where the switch
computes the spatial moment and then unconditionally discards it and
raises ``GlobalException``, so Java's own ``invoke()`` catches it and
returns ``null``. Exercised directly below instead of through the golden
file (a null oracle result has nothing to bit-compare against).

``OrientationMap`` is checked with ``max_ulp=2``, not bit-exact: its two
``atan2`` calls hit the already-documented fdlibm-vs-glibc 1-ULP
divergence (see ``_javacompat.jatan2``'s docstring) for ordinary interior
pixels, not just a rare edge case, because its output is a raw per-pixel
double rather than byte-quantized like most other ``atan2`` call sites in
the port.

The golden file is produced by ``oracle/DumpTexture.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.mm.gray.connected.attribute.Area import Area
from vpt.algorithms.texture import (
    AreaGranulometry,
    AttributeProfile,
    CCBasedAreaProfile,
    CircularGranulometry,
    ExtendedMorphologicalCovariance,
    HorizontalMorphologicalCovariance,
    LineGranulometry,
    MorphologicalCovariance,
    MorphologicalCovarianceFFT,
    MultiCircleFFTCovariance,
    MultiCircleFFTGranulometry,
    MultiResolutionHistogram,
    MultiTextonHistogram,
    OrderedAngularVolumesHistogram,
    OrientationHistogram,
    OrientationMap,
    RotationInvariantGaborFeatures,
    RotationInvariantMorphologicalCovariance,
    SmoothnessFactor,
    SpatialCircularGranulometry,
    SpatialCircularGranulometryFFT,
    SpatialLineGranulometry,
    SpatialMorphologicalCovariance,
    SpatialMorphologicalCovarianceFFT,
)

GOLDEN = Path(__file__).parent / "goldens" / "texture.txt"


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


def make_gray() -> ByteImage:
    gray = ByteImage(12, 12, 1)
    for x in range(12):
        for y in range(12):
            gray.setXYCByte(x, y, 0, (x * 7 + y * 3) % 256)
    return gray


def make_rgb() -> ByteImage:
    rgb = ByteImage(12, 12, 3)
    for x in range(12):
        for y in range(12):
            rgb.setXYCByte(x, y, 0, (x * 7 + y * 3) % 256)
            rgb.setXYCByte(x, y, 1, (x * 3 + y * 11) % 256)
            rgb.setXYCByte(x, y, 2, (x * 13 + y) % 256)
    return rgb


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_texture_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_vector(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[2].split(",") if len(row) > 2 and row[2] else []
        if len(g_vals) != len(result):
            failures.append(f"{name}: length mismatch python={len(result)} java={len(g_vals)}")
            return
        for g, a in zip(g_vals, result):
            gb = bits(parse_double(g))
            ab = bits(a)
            if gb == ab:
                continue
            # NaN has many bit patterns; compare "is NaN" instead of bits.
            if math.isnan(parse_double(g)) and math.isnan(a):
                continue
            failures.append(f"{name}: value mismatch python={a} java={g}")

    def check_value(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        want = parse_double(row[1])
        if bits(result) != bits(want):
            failures.append(f"{name}: value mismatch python={result} java={row[1]}")

    def check_double(name, img, max_ulp=0):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = []
        for y in range(img.getYDim()):
            for x in range(img.getXDim()):
                for c in range(img.getCDim()):
                    a_vals.append(img.getXYCDouble(x, y, c))
        if len(g_vals) != len(a_vals):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, a_vals):
            want = parse_double(g)
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

    gray = make_gray()
    rgb = make_rgb()

    check_vector("AreaGranulometry(gray)", AreaGranulometry.invoke(gray, 3, 2, 0))

    criteria = [Area(5), Area(10)]
    check_vector("AttributeProfile(gray)", AttributeProfile.invoke(gray, criteria, 0))

    check_vector("CCBasedAreaProfile(gray)", CCBasedAreaProfile.invoke(gray, 8))

    check_vector("CircularGranulometry(gray)", CircularGranulometry.invoke(gray, 3, 2, 5))

    check_vector(
        "ExtendedMorphologicalCovariance(gray)",
        ExtendedMorphologicalCovariance.invoke(gray, 3),
    )

    check_vector(
        "HorizontalMorphologicalCovariance(gray,VOLUME)",
        HorizontalMorphologicalCovariance.invoke(gray, 3, 0),
    )
    check_vector(
        "HorizontalMorphologicalCovariance(gray,LOCATIONS)",
        HorizontalMorphologicalCovariance.invoke(gray, 3, 4),
    )

    check_vector("LineGranulometry(gray)", LineGranulometry.invoke(gray, 3, 0))

    check_vector("MorphologicalCovariance(gray)", MorphologicalCovariance.invoke(gray, 3))

    check_vector("MorphologicalCovarianceFFT(gray)", MorphologicalCovarianceFFT.invoke(gray, 2))

    check_vector(
        "MultiCircleFFTCovariance(gray)", MultiCircleFFTCovariance.invoke(gray, 2, 2, 3)
    )
    check_vector(
        "MultiCircleFFTGranulometry(gray)", MultiCircleFFTGranulometry.invoke(gray, 2, 2, 3)
    )

    check_vector("MultiResolutionHistogram(gray)", MultiResolutionHistogram.invoke(gray))

    check_vector("MultiTextonHistogram(rgb)", MultiTextonHistogram.invoke(rgb))

    check_vector(
        "OrderedAngularVolumesHistogram(gray)",
        OrderedAngularVolumesHistogram.invoke(gray, 4),
    )

    check_vector("OrientationHistogram(gray)", OrientationHistogram.invoke(gray, 8, 3))

    check_double("OrientationMap(gray)", OrientationMap.invoke(gray, 3), max_ulp=2)

    check_vector(
        "RotationInvariantGaborFeatures(gray)", RotationInvariantGaborFeatures.invoke(gray, 2)
    )

    check_vector(
        "RotationInvariantMorphologicalCovariance(gray,VOLUME)",
        RotationInvariantMorphologicalCovariance.invoke(gray, 2, 2, 0),
    )
    check_vector(
        "RotationInvariantMorphologicalCovariance(gray,LOCATIONS)",
        RotationInvariantMorphologicalCovariance.invoke(gray, 2, 2, 4),
    )

    check_value("SmoothnessFactor(gray)", SmoothnessFactor.invoke(gray, 3))

    check_vector(
        "SpatialCircularGranulometry(gray)",
        SpatialCircularGranulometry.invoke(gray, 2, 2, 2, 0),
    )
    check_vector(
        "SpatialCircularGranulometryFFT(gray)",
        SpatialCircularGranulometryFFT.invoke(gray, 2, 2, 2, 0),
    )

    check_vector(
        "SpatialLineGranulometry(gray,VOLUME)",
        SpatialLineGranulometry.invoke(gray, 2, 0, 0, 0),
    )

    check_vector(
        "SpatialMorphologicalCovariance(gray)",
        SpatialMorphologicalCovariance.invoke(gray, 2, 2, 0),
    )
    check_vector(
        "SpatialMorphologicalCovarianceFFT(gray)",
        SpatialMorphologicalCovarianceFFT.invoke(gray, 2, 2, 0),
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )


def test_spatial_line_granulometry_moment_is_always_broken():
    gray = make_gray()
    assert SpatialLineGranulometry.invoke(gray, 2, 4, 2, 0) is None

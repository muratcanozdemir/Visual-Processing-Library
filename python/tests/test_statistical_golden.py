"""Differential test of the vpt.algorithms.statistical package against the
Java oracle, except PCA: its eigendecomposition (Jama vs. NumPy/LAPACK) is
a known accepted divergence, the same category as NonFlatSE.decompose's
SVD -- see PCA.py's docstring. Smoke-tested in test_statistical_golden.py
only, not golden-verified.

The golden file is produced by ``oracle/DumpStatistical.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from _platform import libm_ulp
from vpt import BooleanImage, ByteImage
from vpt.algorithms.statistical import (
    NMSE,
    CentralMoment,
    ContrastRatio,
    InvariantMoment,
    Kurtosis,
    Mean,
    Moment,
    Normalize,
    ReversePCA,
    SDev,
    Skewness,
    SpatialMoment,
    Variance,
    ZernikeMoment,
)

GOLDEN = Path(__file__).parent / "goldens" / "statistical.txt"


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


def make_g() -> ByteImage:
    g = ByteImage(10, 10, 1)
    for x in range(10):
        for y in range(10):
            inside = 3 <= x < 7 and 3 <= y < 7
            g.setXYCByte(x, y, 0, 200 if inside else 20)
    return g


def make_g2() -> ByteImage:
    g2 = ByteImage(10, 10, 1)
    for x in range(10):
        for y in range(10):
            g2.setXYCByte(x, y, 0, 200 if x < 5 else 20)
    return g2


def make_mask() -> BooleanImage:
    mask = BooleanImage(10, 10, 1)
    mask.fill(False)
    for x in range(5):
        for y in range(5):
            mask.setXYBoolean(x, y, True)
    return mask


def make_rgb() -> ByteImage:
    rgb = ByteImage(6, 6, 3)
    for x in range(6):
        for y in range(6):
            rgb.setXYCByte(x, y, 0, x * 40)
            rgb.setXYCByte(x, y, 1, y * 40)
            rgb.setXYCByte(x, y, 2, (x + y) * 20)
    return rgb


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_statistical_matches_java_oracle():
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

    def check_value(name, result, max_ulp=0):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        want = parse_double(row[1])
        if bits(result) == bits(want):
            return
        if (
            max_ulp
            and math.isfinite(result)
            and math.isfinite(want)
            and (result < 0) == (want < 0)
            and abs(bits(result) - bits(want)) <= max_ulp
        ):
            return
        failures.append(f"{name}: value mismatch python={result} java={row[1]}")

    g = make_g()

    check_value("Mean(g)", Mean.invoke(g))
    check_value("SDev(g)", SDev.invoke(g))
    check_value("Variance(g)", Variance.invoke(g))
    check_vector("Moment(g,1,0)", Moment.invoke(g, 1, 0))
    check_vector("Moment(g,2,1)", Moment.invoke(g, 2, 1))
    check_double("Normalize(g,0.5,0.1)", Normalize.invoke(g, 0.5, 0.1))
    check_vector("SpatialMoment(g,2,0)", SpatialMoment.invoke(g, 2, 0))
    check_vector("CentralMoment(g,2,0)", CentralMoment.invoke(g, 2, 0))
    check_value("Kurtosis(g)", Kurtosis.invoke(g))
    check_value("Skewness(g)", Skewness.invoke(g))
    check_value("ContrastRatio(g)", ContrastRatio.invoke(g))

    mask = make_mask()
    check_value("ContrastRatio(g,mask)", ContrastRatio.invoke(g, mask))

    g2 = make_g2()
    check_value("NMSE(g,g2)", NMSE.invoke(g, g2))

    for order in range(1, 9):
        check_vector(f"InvariantMoment(g,{order})", InvariantMoment.invoke(g, order))

    # ZernikeMoment chains Math.atan2/cos/sin/pow; like the other
    # transcendental-function divergences in this port (Math.atan2,
    # Math.acos, Math.log10), up to a couple of these can disagree with
    # Java's by a few ulp at specific inputs. See python/README.md.
    check_value("ZernikeMoment(g,4,2)", ZernikeMoment.invoke(g, 4, 2), max_ulp=libm_ulp(2))
    check_value("ZernikeMoment(g,6,0)", ZernikeMoment.invoke(g, 6, 0), max_ulp=libm_ulp(2))
    check_value("ZernikeMoment(g,5,3)", ZernikeMoment.invoke(g, 5, 3), max_ulp=libm_ulp(2))

    rgb = make_rgb()
    eigenvectors = [
        [0.7071067811865476, 0.7071067811865476, 0.0],
        [-0.7071067811865476, 0.7071067811865476, 0.0],
        [0.0, 0.0, 1.0],
    ]
    mean = [0.4, 0.4, 0.4]
    check_double(
        "ReversePCA(rgb,eigenvectors,mean)", ReversePCA.invoke(rgb, eigenvectors, mean)
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

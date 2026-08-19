"""Differential test of the vpt.algorithms.shape package against the Java
oracle.

Not covered here (confirmed via a live, timeout-bounded JDK run to hang
forever through the same BFillHole/ConvexHull(filled=true) mechanism
documented on mm/binary/geo/BFillHole.py): ``ConvexAreaRatio``,
``ConvexPerimeterRatio``, ``Dispersion``, ``EdgeBackForeRatioHistogram``.
``AxisOfSymmetry`` hangs the oracle too, for an independent reason (a live
``Display2D`` GUI popup call at the end of ``execute()``, same category as
the already-documented ``Canny``/``BReconstructionByErosion`` hangs) -- it
is smoke-tested here only, asserting ``invoke() is None``, since this
port's own version already drops that call and is safe to run directly.
``DFTZernikeMoments`` is smoke-tested only (asserts an all-``NaN`` 32-vector,
matching its inherited FFT/IFFT-always-zero-then-0/0 bug) rather than
bit-exact golden-compared: a NaN's bit pattern isn't guaranteed to round-trip
identically across the Java/NumPy boundary the way a normal double is.
``ConnectedComponents`` itself is already golden-verified via
``oracle/Recovered.java``/``test_core_golden.py`` (or equivalent), not
duplicated here.

The golden file is produced by ``oracle/DumpShape.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from vpt import ByteImage, IntegerImage
from vpt._javacompat import Point
from vpt.algorithms.shape import (
    FPFT,
    AngleCodeHistogram,
    AreaWidthFactor,
    AxisOfSymmetry,
    BasicShapeStatistics,
    CentroidContourDistance,
    CircularWidthFactor,
    CircularWidthFactor2,
    CircularWidthFactor3,
    Compactness,
    ConnectedComponentFromSeed,
    ContourPointDistributionHistogram,
    DFTZernikeMoments,
    DominantPointsDetection,
    FourierDescriptor,
    GrayConnectedComponents,
    GrayConnectedComponents2,
    MeanDistanceToCentroid,
    MorphologicalElongation,
    MultiLobeMeanShapeFeatures,
    MultiScaleCCCount,
    NormalizedWidthFactor,
    Orientation,
    RegionalMomentsOfInertia,
    RegionalMomentsOfInertia2,
    VarianceOfDistanceToCentroid,
    WidthFactor,
    ZernikeMoments,
)
from vpt.util.se import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "shape.txt"


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


def make_shape() -> ByteImage:
    shape = ByteImage(24, 24, 1)
    shape.fill(False)
    for x in range(3, 13):
        for y in range(4, 10):
            shape.setXYBoolean(x, y, True)
    for x in range(9, 17):
        for y in range(10, 16):
            shape.setXYBoolean(x, y, True)
    return shape


def make_labels() -> IntegerImage:
    labels = IntegerImage(12, 12, 1)
    labels.fillInt(0)
    for x in range(1, 5):
        for y in range(1, 5):
            labels.setXYInt(x, y, 5)
    for x in range(7, 10):
        for y in range(6, 10):
            labels.setXYInt(x, y, 9)
    return labels


def make_rect() -> ByteImage:
    rect = ByteImage(20, 20, 1)
    rect.fill(False)
    for x in range(4, 16):
        for y in range(5, 13):
            rect.setXYBoolean(x, y, True)
    return rect


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_shape_matches_java_oracle():
    golden = load_golden()
    failures = []

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

    def check_null(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        want_null = row[1] == "NULL"
        if want_null != (result is None):
            failures.append(f"{name}: nullness mismatch python={result} java={row[1]}")

    def check_points(name, result: list[Point]):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        want = row[3].split(";") if len(row) > 3 and row[3] else []
        if int(row[1]) != len(result):
            failures.append(f"{name}: length mismatch")
            return
        got = [f"{p.x},{p.y}" for p in result]
        if got != want:
            failures.append(f"{name}: point mismatch python={got} java={want}")

    shape = make_shape()

    check_value("Compactness(shape)", Compactness.invoke(shape))
    check_value(
        "MorphologicalElongation(shape,sq3)",
        MorphologicalElongation.invoke(shape, FlatSE.square(3)),
    )
    check_value("MeanDistanceToCentroid(shape)", MeanDistanceToCentroid.invoke(shape))
    check_value(
        "VarianceOfDistanceToCentroid(shape)", VarianceOfDistanceToCentroid.invoke(shape)
    )
    check_value("Orientation(shape)", Orientation.invoke(shape))

    check_vector(
        "MultiScaleCCCount(shape,3,1,1)", MultiScaleCCCount.invoke(shape, 3, 1, 1)
    )

    check_vector("WidthFactor(shape,4)", WidthFactor.invoke(shape, 4))
    check_vector("AreaWidthFactor(shape,4)", AreaWidthFactor.invoke(shape, 4))
    check_vector("NormalizedWidthFactor(shape,4)", NormalizedWidthFactor.invoke(shape, 4))
    check_vector("CircularWidthFactor(shape,4)", CircularWidthFactor.invoke(shape, 4))
    check_vector("CircularWidthFactor2(shape,4)", CircularWidthFactor2.invoke(shape, 4))
    check_vector("CircularWidthFactor3(shape,4)", CircularWidthFactor3.invoke(shape, 4))

    check_vector(
        "RegionalMomentsOfInertia(shape,4)", RegionalMomentsOfInertia.invoke(shape, 4)
    )
    check_vector(
        "RegionalMomentsOfInertia2(shape,4)", RegionalMomentsOfInertia2.invoke(shape, 4)
    )
    check_vector(
        "ContourPointDistributionHistogram(shape,4)",
        ContourPointDistributionHistogram.invoke(shape, 4),
    )

    # ZernikeMoments chains Math.atan2/cos/sin/pow per pixel per moment, the
    # same fdlibm-vs-glibc divergence family as statistical.ZernikeMoment,
    # but summed over many more terms -- the drift compounds further (up to
    # ~7 ulp observed here vs. 2 ulp for a single ZernikeMoment call).
    zm = ZernikeMoments.invoke(shape)
    zm_row = golden.get("ZernikeMoments(shape)")
    if zm_row is None:
        failures.append("ZernikeMoments(shape): missing from golden")
    else:
        g_vals = zm_row[2].split(",")
        if len(g_vals) != len(zm):
            failures.append("ZernikeMoments(shape): length mismatch")
        else:
            for i, (g, a) in enumerate(zip(g_vals, zm)):
                want = parse_double(g)
                if bits(a) == bits(want):
                    continue
                if (
                    math.isfinite(a)
                    and math.isfinite(want)
                    and (a < 0) == (want < 0)
                    and abs(bits(a) - bits(want)) <= 8
                ):
                    continue
                failures.append(f"ZernikeMoments(shape)[{i}]: python={a} java={g}")

    # DFTZernikeMoments inherits FFT/IFFT's always-all-zero bug -> a 0/0
    # NaN throughout; smoke-tested, not bit-exact golden-compared (see
    # module docstring).
    dzm = DFTZernikeMoments.invoke(shape)
    assert len(dzm) == 32
    assert all(math.isnan(v) for v in dzm)

    check_double("FPFT(shape)", FPFT.invoke(shape))
    check_vector("FourierDescriptor(shape,8)", FourierDescriptor.invoke(shape, 8))

    # AxisOfSymmetry: smoke-tested only, not golden-compared. The Java
    # oracle's execute() ends with a live Display2D.invoke(input) GUI popup
    # call that hangs the JVM forever in any environment with a real
    # display (confirmed directly -- see oracle/DumpShape.java's docstring),
    # so it's excluded from that driver entirely. This port already drops
    # that call (see AxisOfSymmetry.py), so calling it here is safe -- but
    # execute() also mutates its input Image in place (documented), so it
    # gets its own deep copy rather than the shared `shape` every other
    # check below reads: reusing `shape` here previously corrupted the
    # BasicShapeStatistics/AngleCodeHistogram/ConnectedComponentFromSeed
    # golden rows further down (they silently picked up the post-mutation
    # shape), which is what regenerated those three rows.
    assert AxisOfSymmetry.invoke(shape.newInstance(True)) is None
    check_null(
        "MultiLobeMeanShapeFeatures(shape)", MultiLobeMeanShapeFeatures.invoke(shape)
    )
    check_null(
        "CentroidContourDistance(shape,8)", CentroidContourDistance.invoke(shape, 8)
    )

    check_vector("BasicShapeStatistics(shape)", BasicShapeStatistics.invoke(shape))
    check_vector(
        "AngleCodeHistogram(shape,8,0.5)", AngleCodeHistogram.invoke(shape, 8, 0.5)
    )

    check_points(
        "ConnectedComponentFromSeed(shape,(5,6))",
        ConnectedComponentFromSeed.invoke(shape, Point(5, 6)),
    )

    labels = make_labels()
    gcc = GrayConnectedComponents.invoke(labels)
    gcc_count_row = golden.get("GrayConnectedComponents(labels)-count")
    if gcc_count_row is None:
        failures.append("GrayConnectedComponents(labels)-count: missing from golden")
    elif int(gcc_count_row[3]) != len(gcc):
        failures.append(
            f"GrayConnectedComponents(labels)-count: python={len(gcc)} "
            f"java={gcc_count_row[3]}"
        )
    else:
        for i, cc in enumerate(gcc):
            check_points(f"GrayConnectedComponents(labels)[{i}]", cc)

    gcc2 = GrayConnectedComponents2.invoke(labels)
    seen_ids = {id(v) for row in gcc2 for v in row if v is not None}
    distinct_row = golden.get("GrayConnectedComponents2(labels)-distinct")
    if distinct_row is None:
        failures.append("GrayConnectedComponents2(labels)-distinct: missing from golden")
    elif int(distinct_row[3]) != len(seen_ids):
        failures.append(
            f"GrayConnectedComponents2(labels)-distinct: python={len(seen_ids)} "
            f"java={distinct_row[3]}"
        )
    for x, y in ((2, 2), (8, 7), (0, 0)):
        name = f"GrayConnectedComponents2(labels)@({x},{y})-size"
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            continue
        got = len(gcc2[x][y])
        if int(row[3]) != got:
            failures.append(f"{name}: python={got} java={row[3]}")

    rect = make_rect()
    check_points(
        "DominantPointsDetection(rect,0.5)", DominantPointsDetection.invoke(rect, 0.5)
    )
    check_points(
        "DominantPointsDetection(rect,3.0)", DominantPointsDetection.invoke(rect, 3.0)
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

"""Differential test of the mm/gray/geo reconstruction family (and the
Maximum/AdditionC/SubtractionC/Subtraction arithmetic ops it depends on)
against the Java oracle.

The golden file is produced by ``oracle/DumpGeo.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.arithmetic import AdditionC, Maximum, Subtraction, SubtractionC
from vpt.algorithms.mm.gray.geo import (
    CONNEXITY4,
    CONNEXITY8,
    FastGrayReconstruction,
    GBotHatByReconstruction,
    GClosingByReconstruction,
    GFillHole,
    GGeodesicDilation,
    GGeodesicErosion,
    GHMinima,
    GOpeningByReconstruction,
    GReconstructionByDilation,
    GReconstructionByErosion,
    GRegionalMaxima,
    GRegionalMinima,
    GTopHatByReconstruction,
)
from vpt.util.se import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "geo.txt"


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, img.getXDim(), img.getYDim(), img.getCDim(), ",".join(vals))


def make_g() -> ByteImage:
    g = ByteImage(16, 16, 1)
    g.fill(50)
    for x in range(4, 12):
        for y in range(4, 12):
            g.setXYByte(x, y, 180)
    return g


def make_peaks() -> ByteImage:
    peaks = ByteImage(16, 16, 1)
    peaks.fill(20)
    for x in range(2, 6):
        for y in range(2, 6):
            peaks.setXYByte(x, y, 200)
    for x in range(10, 13):
        for y in range(10, 13):
            peaks.setXYByte(x, y, 120)
    return peaks


def generate() -> list[tuple]:
    g = make_g()
    peaks = make_peaks()
    se = FlatSE.square(3)

    rows = [
        gray_row("Maximum(g,peaks)", Maximum.invoke(g, peaks)),
        gray_row("AdditionC(g,0.2)", AdditionC.invoke(g, 0.2)),
        gray_row("SubtractionC(g,0.2)", SubtractionC.invoke(g, 0.2)),
        gray_row("Subtraction(g,peaks)", Subtraction.invoke(g, peaks)),
        gray_row("GGeodesicDilation(peaks,g)", GGeodesicDilation.invoke(peaks, se, g)),
        gray_row("GGeodesicErosion(peaks,g)", GGeodesicErosion.invoke(peaks, se, g)),
        gray_row(
            "GReconstructionByDilation(peaks,g)",
            GReconstructionByDilation.invoke(peaks, se, g),
        ),
        gray_row(
            "GReconstructionByErosion(peaks,g)", GReconstructionByErosion.invoke(peaks, se, g)
        ),
        gray_row("GOpeningByReconstruction(g,square3)", GOpeningByReconstruction.invoke(g, se)),
        gray_row("GClosingByReconstruction(g,square3)", GClosingByReconstruction.invoke(g, se)),
        gray_row("GBotHatByReconstruction(g,square3)", GBotHatByReconstruction.invoke(g, se)),
        gray_row("GTopHatByReconstruction(g,square3)", GTopHatByReconstruction.invoke(g, se)),
        gray_row("GRegionalMaxima(peaks)", GRegionalMaxima.invoke(peaks)),
        gray_row("GRegionalMinima(peaks)", GRegionalMinima.invoke(peaks)),
        gray_row("GHMinima(peaks,50)", GHMinima.invoke(peaks, 50)),
        gray_row(
            "FastGrayReconstruction(peaks,g,CONNEXITY4,false)",
            FastGrayReconstruction.invoke(peaks, g, CONNEXITY4, False),
        ),
        gray_row(
            "FastGrayReconstruction(peaks,g,CONNEXITY8,true)",
            FastGrayReconstruction.invoke(peaks, g, CONNEXITY8, True),
        ),
        # GFillHole.execute() mutates its input image in place, and the Java
        # driver dumps it last for exactly that reason -- keep this last too.
        gray_row("GFillHole(g)", GFillHole.invoke(g)),
    ]
    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_geo_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpGeo.java"
    )

    failures = []
    for n, (g_row, a_row) in enumerate(zip(golden, actual), start=1):
        g_name, g_x, g_y, g_c, g_vals = g_row
        a_name, a_x, a_y, a_c, a_vals = a_row

        if g_name != a_name:
            failures.append(f"line {n}: replay out of step: java={g_name} python={a_name}")
            continue
        if (g_x, g_y, g_c) != (str(a_x), str(a_y), str(a_c)):
            failures.append(f"line {n} {g_name}: dims differ")
            continue
        if g_vals != a_vals:
            failures.append(f"line {n} {g_name}: pixel mismatch")

    assert not failures, (
        f"{len(failures)} of {len(golden)} golden rows differ from the Java "
        f"oracle:\n  " + "\n  ".join(failures[:40])
    )

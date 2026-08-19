"""Differential test of GNFDilation/GNFErosion/GNFOpening/GNFClosing
(mm/gray/nonflat) against the Java oracle.

The golden file is produced by ``oracle/DumpNonflat.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage, Point
from vpt.algorithms.mm.gray.nonflat import GNFClosing, GNFDilation, GNFErosion, GNFOpening
from vpt.util.se import NonFlatSE

GOLDEN = Path(__file__).parent / "goldens" / "nonflat.txt"


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


def generate() -> list[tuple]:
    g = make_g()

    flat = NonFlatSE(Point(1, 1), [[0, 0, 0], [0, 0, 0], [0, 0, 0]])
    rows = [
        gray_row("GNFDilation(g,flat3)", GNFDilation.invoke(g, flat)),
        gray_row("GNFErosion(g,flat3)", GNFErosion.invoke(g, flat)),
        gray_row("GNFOpening(g,flat3)", GNFOpening.invoke(g, flat)),
        gray_row("GNFClosing(g,flat3)", GNFClosing.invoke(g, flat)),
    ]

    a, b = 30.0 / 255.0, -10.0 / 255.0
    weighted = NonFlatSE(Point(1, 1), [[a, 0, b], [0, 0, 0], [b, 0, a]])
    rows += [
        gray_row("GNFDilation(g,weighted)", GNFDilation.invoke(g, weighted)),
        gray_row("GNFErosion(g,weighted)", GNFErosion.invoke(g, weighted)),
        gray_row("GNFOpening(g,weighted)", GNFOpening.invoke(g, weighted)),
        gray_row("GNFClosing(g,weighted)", GNFClosing.invoke(g, weighted)),
    ]

    cone = NonFlatSE.cone(2, 40.0 / 255.0)
    rows += [
        gray_row("GNFDilation(g,cone2)", GNFDilation.invoke(g, cone)),
        gray_row("GNFErosion(g,cone2)", GNFErosion.invoke(g, cone)),
    ]

    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_nonflat_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpNonflat.java"
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

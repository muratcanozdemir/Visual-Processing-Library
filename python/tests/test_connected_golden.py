"""Differential test of the mm/gray/connected top-level family (GAreaOpening/
Closing, GMaxTree{Incr,NonIncr}{,Multi}Attr{Opening,Closing}) against the
Java oracle.

The golden file is produced by ``oracle/DumpConnected.java`` (see
``python/tests/regen-goldens.sh``).

One row is a known, accepted divergence rather than a bit-exact comparison:
``GMaxTreeNonIncrAttrOpening(g,Area5,DIRECT)``. The Java throws
``ConcurrentModificationException`` for this exact call (confirmed against
the JDK 21 oracle -- see ``oracle/DumpConnected.java``'s ``dumpGraySafe``
and the golden row's ``THREW`` sentinel), while the Python port
deliberately implements the intended splice-out-and-continue semantics
instead of reproducing a crash. See ``GMaxTreeNonIncrAttrOpening.py`` and
``python/README.md``'s "Known divergence" section.
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.mm.gray.connected import (
    GAreaClosing,
    GAreaOpening,
    GMaxTreeIncrAttrClosing,
    GMaxTreeIncrAttrOpening,
    GMaxTreeIncrMultiAttrClosing,
    GMaxTreeIncrMultiAttrOpening,
    GMaxTreeNonIncrAttrClosing,
    GMaxTreeNonIncrAttrOpening,
    GMaxTreeNonIncrMultiAttrClosing,
    GMaxTreeNonIncrMultiAttrOpening,
)
from vpt.algorithms.mm.gray.connected.attribute import Area

GOLDEN = Path(__file__).parent / "goldens" / "connected.txt"

# Known, accepted divergence: the Java throws for this exact call (see the
# module docstring above and GMaxTreeNonIncrAttrOpening.py). Compared
# separately below instead of via bit-exact row equality.
DIRECT_ROW_NAME = "GMaxTreeNonIncrAttrOpening(g,Area5,DIRECT)"


def gray_row(name: str, img) -> tuple:
    if img is None:
        return (name, "NULL")
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, str(img.getXDim()), str(img.getYDim()), str(img.getCDim()), ",".join(vals))


def make_g() -> ByteImage:
    g = ByteImage(16, 16, 1)
    g.fill(50)
    for x in range(4, 12):
        for y in range(4, 12):
            g.setXYByte(x, y, 180)
    g.setXYByte(1, 1, 220)
    return g


def make_rgb(g) -> ByteImage:
    rgb = ByteImage(16, 16, 2)
    for x in range(16):
        for y in range(16):
            rgb.setXYCByte(x, y, 0, g.getXYByte(x, y))
            rgb.setXYCByte(x, y, 1, 100)
    return rgb


def generate() -> list[tuple]:
    g = make_g()
    rgb = make_rgb(g)
    crit = Area(5)

    rows = [
        gray_row("GAreaOpening(g,5)", GAreaOpening.invoke(g, 5)),
        gray_row("GAreaClosing(g,5)", GAreaClosing.invoke(g, 5)),
        gray_row("GMaxTreeIncrAttrOpening(g,Area5)", GMaxTreeIncrAttrOpening.invoke(g, crit)),
        gray_row("GMaxTreeIncrAttrClosing(g,Area5)", GMaxTreeIncrAttrClosing.invoke(g, crit)),
        gray_row(
            "GMaxTreeIncrMultiAttrOpening(rgb,0,Area5)",
            GMaxTreeIncrMultiAttrOpening.invoke(rgb, 0, crit),
        ),
        gray_row(
            "GMaxTreeIncrMultiAttrClosing(rgb,0,Area5)",
            GMaxTreeIncrMultiAttrClosing.invoke(rgb, 0, crit),
        ),
        gray_row(
            "GMaxTreeNonIncrAttrOpening(g,Area5,DIRECT)",
            GMaxTreeNonIncrAttrOpening.invoke(g, crit, 0),
        ),
        gray_row(
            "GMaxTreeNonIncrAttrOpening(g,Area5,MIN)",
            GMaxTreeNonIncrAttrOpening.invoke(g, crit, 2),
        ),
        gray_row(
            "GMaxTreeNonIncrAttrOpening(g,Area5,MAX)",
            GMaxTreeNonIncrAttrOpening.invoke(g, crit, 3),
        ),
        gray_row(
            "GMaxTreeNonIncrAttrOpening(g,Area5,SUBTRACTIVE)",
            GMaxTreeNonIncrAttrOpening.invoke(g, crit, 1),
        ),
        gray_row("GMaxTreeNonIncrAttrClosing(g,Area5)", GMaxTreeNonIncrAttrClosing.invoke(g, crit)),
        gray_row(
            "GMaxTreeNonIncrMultiAttrOpening(rgb,0,Area5)",
            GMaxTreeNonIncrMultiAttrOpening.invoke(rgb, 0, crit),
        ),
        gray_row(
            "GMaxTreeNonIncrMultiAttrClosing(rgb,0,Area5)",
            GMaxTreeNonIncrMultiAttrClosing.invoke(rgb, 0, crit),
        ),
    ]
    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_connected_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpConnected.java"
    )

    failures = []
    for n, (g_row, a_row) in enumerate(zip(golden, actual), start=1):
        if g_row[0] != a_row[0]:
            failures.append(f"line {n}: replay out of step: java={g_row[0]} python={a_row[0]}")
            continue
        if g_row[0] == DIRECT_ROW_NAME:
            if list(g_row[1:3]) != ["THREW", "java.util.ConcurrentModificationException"]:
                failures.append(
                    f"line {n} {g_row[0]}: golden no longer shows the expected Java crash "
                    f"({g_row[1:]}) -- the known divergence may need re-checking"
                )
            elif a_row[1] == "NULL" or len(a_row) < 5:
                failures.append(
                    f"line {n} {g_row[0]}: Python produced no usable output ({a_row}), "
                    "expected the splice-out-and-continue result"
                )
            continue
        if list(g_row) != list(a_row):
            failures.append(f"line {n} {g_row[0]}: mismatch")

    assert not failures, (
        f"{len(failures)} of {len(golden)} golden rows differ from the Java "
        f"oracle:\n  " + "\n  ".join(failures[:40])
    )

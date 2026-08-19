"""Differential test of BAreaOpening/BAreaClosing (mm/binary/attribute)
against the Java oracle.

The golden file is produced by ``oracle/DumpAttr.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import BooleanImage
from vpt.algorithms.mm.binary.attribute import BAreaClosing, BAreaOpening

GOLDEN = Path(__file__).parent / "goldens" / "attr.txt"


def binary_row(name: str, img) -> tuple:
    bits = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            bits.append("1" if img.getXYBoolean(x, y) else "0")
    return (name, img.getXDim(), img.getYDim(), "".join(bits))


def generate() -> list[tuple]:
    bi = BooleanImage(12, 12, 1)
    bi.fill(False)
    for x in range(1, 4):
        for y in range(1, 4):
            bi.setXYBoolean(x, y, True)
    for x in range(7, 9):
        for y in range(7, 9):
            bi.setXYBoolean(x, y, True)

    rows = [
        binary_row("BAreaOpening(bi,1)", BAreaOpening.invoke(bi, 1)),
        binary_row("BAreaOpening(bi,5)", BAreaOpening.invoke(bi, 5)),
        binary_row("BAreaOpening(bi,10)", BAreaOpening.invoke(bi, 10)),
    ]

    bi2 = BooleanImage(10, 10, 1)
    bi2.fill(False)
    for x in range(1, 9):
        for y in range(1, 9):
            bi2.setXYBoolean(x, y, True)
    bi2.setXYBoolean(4, 4, False)
    bi2.setXYBoolean(2, 2, False)
    bi2.setXYBoolean(2, 3, False)
    bi2.setXYBoolean(3, 2, False)

    rows += [
        binary_row("BAreaClosing(bi2,1)", BAreaClosing.invoke(bi2, 1)),
        binary_row("BAreaClosing(bi2,3)", BAreaClosing.invoke(bi2, 3)),
        binary_row("BAreaClosing(bi2,5)", BAreaClosing.invoke(bi2, 5)),
    ]

    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_attr_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpAttr.java"
    )

    failures = []
    for n, (g_row, a_row) in enumerate(zip(golden, actual), start=1):
        g_name, g_x, g_y, g_bits = g_row
        a_name, a_x, a_y, a_bits = a_row

        if g_name != a_name:
            failures.append(f"line {n}: replay out of step: java={g_name} python={a_name}")
            continue
        if (g_x, g_y) != (str(a_x), str(a_y)):
            failures.append(f"line {n} {g_name}: dims differ")
            continue
        if g_bits != a_bits:
            failures.append(f"line {n} {g_name}: pixel mismatch")

    assert not failures, (
        f"{len(failures)} of {len(golden)} golden rows differ from the Java "
        f"oracle:\n  " + "\n  ".join(failures[:40])
    )

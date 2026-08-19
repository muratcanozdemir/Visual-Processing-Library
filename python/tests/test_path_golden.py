"""Differential test of BPathOpening (mm/binary/path) and GPathOpening
(mm/gray/path) against the Java oracle.

The golden file is produced by ``oracle/DumpPath.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage
from vpt.algorithms.mm.binary.path import BPathOpening
from vpt.algorithms.mm.gray.path import GPathOpening

GOLDEN = Path(__file__).parent / "goldens" / "path.txt"


def binary_row(name: str, img) -> tuple:
    bits = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            bits.append("1" if img.getXYBoolean(x, y) else "0")
    return (name, str(img.getXDim()), str(img.getYDim()), "".join(bits))


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            vals.append(str(img.getXYByte(x, y)))
    return (name, str(img.getXDim()), str(img.getYDim()), ",".join(vals))


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_path_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check(name, row_tuple):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if list(row_tuple) != row:
            failures.append(f"{name}: mismatch")

    bi = BooleanImage(20, 20, 1)
    bi.fill(False)
    for x in range(2, 12):
        bi.setXYBoolean(x, 5, True)
    for y in range(10, 13):
        bi.setXYBoolean(15, y, True)

    check("BPathOpening(bi,8)", binary_row("BPathOpening(bi,8)", BPathOpening.invoke(bi, 8)))
    check("BPathOpening(bi,2)", binary_row("BPathOpening(bi,2)", BPathOpening.invoke(bi, 2)))

    biNWSE = BooleanImage(20, 20, 1)
    biNWSE.fill(False)
    for i in range(10):
        biNWSE.setXYBoolean(2 + i, 2 + i, True)
    check(
        "BPathOpening(NWSE,8)",
        binary_row("BPathOpening(NWSE,8)", BPathOpening.invoke(biNWSE, 8)),
    )

    biSWNE = BooleanImage(20, 20, 1)
    biSWNE.fill(False)
    for i in range(10):
        biSWNE.setXYBoolean(2 + i, 11 - i, True)
    check(
        "BPathOpening(SWNE,8)",
        binary_row("BPathOpening(SWNE,8)", BPathOpening.invoke(biSWNE, 8)),
    )

    g = ByteImage(20, 20, 1)
    g.fill(30)
    for x in range(2, 12):
        g.setXYByte(x, 5, 200)
    for y in range(10, 13):
        g.setXYByte(15, y, 200)
    check("GPathOpening(g,8)", gray_row("GPathOpening(g,8)", GPathOpening.invoke(g, 8)))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

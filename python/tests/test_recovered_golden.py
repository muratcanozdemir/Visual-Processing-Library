"""Differential test of ConnectedComponents, ConvexHull(filled=false),
GrayQFZ, and MaxTree against the Java oracle -- the decompiler-recovered
algorithms oracle/Recovered.java exercises (see the root README's
"Decompiler damage repaired" section).

The golden file is produced by ``oracle/DumpRecovered.java`` (see
``python/tests/regen-goldens.sh``).

``ConvexHull(..., filled=True)`` is deliberately not exercised here -- see
``python/vpt/algorithms/mm/binary/geo/BFillHole.py`` for why it appears to
hang forever on both sides of the port, not just this one.
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage
from vpt.algorithms.flatzones.gray import GrayQFZ
from vpt.algorithms.geometric import ConvexHull
from vpt.algorithms.mm.gray.connected.maxtree import MaxTree
from vpt.algorithms.shape import ConnectedComponents

GOLDEN = Path(__file__).parent / "goldens" / "recovered.txt"


def binary_bits(img) -> str:
    bits = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            bits.append("1" if img.getXYBoolean(x, y) else "0")
    return "".join(bits)


def cc_key(cc) -> str:
    return ";".join(f"{p.x},{p.y}" for p in cc)


def int_csv(img) -> str:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            vals.append(str(img.getXYInt(x, y)))
    return ",".join(vals)


def tree_key(node) -> str:
    parts = [f"{node.level}:{len(node.pixels)}:{len(node.children)}"]
    for c in node.children:
        parts.append(tree_key(c))
    return ";".join(parts)


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_recovered_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_cc(prefix: str, bi: BooleanImage):
        cc = ConnectedComponents.invoke(bi)
        count_row = golden.get(f"{prefix}-count")
        if count_row is None:
            failures.append(f"{prefix}-count: missing from golden")
            return
        if str(len(cc)) != count_row[3]:
            failures.append(f"{prefix}-count: python={len(cc)} java={count_row[3]}")
            return
        for i, comp in enumerate(cc):
            row = golden.get(f"{prefix}[{i}]")
            if row is None:
                failures.append(f"{prefix}[{i}]: missing from golden")
                continue
            if str(len(comp)) != row[1]:
                failures.append(f"{prefix}[{i}]: size python={len(comp)} java={row[1]}")
                continue
            want = row[3] if len(row) > 3 else ""
            if cc_key(comp) != want:
                failures.append(f"{prefix}[{i}]: pixel order/content mismatch")

    def check_hull(name: str, bi: BooleanImage):
        hull = ConvexHull.invoke(bi, False)
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if (str(hull.getXDim()), str(hull.getYDim())) != (row[1], row[2]):
            failures.append(f"{name}: dims mismatch")
            return
        if binary_bits(hull) != row[3]:
            failures.append(f"{name}: pixel mismatch")

    bi = BooleanImage(12, 12, 1)
    bi.fill(False)
    for x in range(1, 4):
        for y in range(1, 4):
            bi.setXYBoolean(x, y, True)
    for x in range(7, 10):
        for y in range(7, 10):
            bi.setXYBoolean(x, y, True)
    check_cc("ConnectedComponents", bi)

    bi2 = BooleanImage(10, 10, 1)
    bi2.fill(False)
    for x in range(3):
        for y in range(3):
            bi2.setXYBoolean(x, y, True)
    bi2.setXYBoolean(3, 3, True)
    bi2.setXYBoolean(4, 4, True)
    check_cc("ConnectedComponents2", bi2)

    tri = BooleanImage(20, 20, 1)
    tri.fill(False)
    tri.setXYBoolean(2, 2, True)
    tri.setXYBoolean(15, 3, True)
    tri.setXYBoolean(8, 16, True)
    tri.setXYBoolean(8, 8, True)
    check_hull("ConvexHull(tri,false)", tri)

    penta = BooleanImage(24, 24, 1)
    penta.fill(False)
    for x, y in [(3, 3), (20, 5), (21, 18), (10, 21), (2, 12), (11, 11)]:
        penta.setXYBoolean(x, y, True)
    check_hull("ConvexHull(penta,false)", penta)

    def check_qfz(name: str, img: ByteImage, alpha: int, omega: int):
        result = GrayQFZ.invoke(img, alpha, omega)
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if (str(result.getXDim()), str(result.getYDim())) != (row[1], row[2]):
            failures.append(f"{name}: dims mismatch")
            return
        if int_csv(result) != (row[3] if len(row) > 3 else ""):
            failures.append(f"{name}: label mismatch")

    def check_maxtree(name: str, img: ByteImage):
        root = MaxTree.invoke(img)
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if tree_key(root) != (row[3] if len(row) > 3 else ""):
            failures.append(f"{name}: tree structure mismatch")

    gi = ByteImage(10, 10, 1)
    gi.fill(10)
    for x in range(5, 10):
        for y in range(10):
            gi.setXYByte(x, y, 200)
    check_qfz("GrayQFZ(gi,0,255)", gi, 0, 255)

    ramp = ByteImage(9, 9, 1)
    for x in range(9):
        for y in range(9):
            ramp.setXYByte(x, y, (x // 3) * 40)
    check_qfz("GrayQFZ(ramp,15,255)", ramp, 15, 255)
    check_qfz("GrayQFZ(ramp,15,30)", ramp, 15, 30)

    check_maxtree("MaxTree(gi)", gi)
    check_maxtree("MaxTree(ramp)", ramp)

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

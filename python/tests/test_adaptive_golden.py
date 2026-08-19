"""Differential test of the mm/gray/adaptive family (ComputeSEs,
ADilation/AErosion/AOpening/AClosing) against the Java oracle.

The golden file is produced by ``oracle/DumpAdaptive.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.mm.gray.adaptive import AClosing, ADilation, AErosion, AOpening, ComputeSEs

GOLDEN = Path(__file__).parent / "goldens" / "adaptive.txt"


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, str(img.getXDim()), str(img.getYDim()), str(img.getCDim()), ",".join(vals))


def make_img() -> ByteImage:
    img = ByteImage(24, 24, 1)
    img.fill(50)
    for x in range(8, 17):
        for y in range(8, 17):
            img.setXYByte(x, y, 180)
    return img


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_adaptive_matches_java_oracle():
    golden = load_golden()
    failures = []

    img = make_img()
    se = ComputeSEs.invoke(img, 10)

    for x, y in [(12, 12), (2, 2), (8, 8), (0, 0), (23, 23)]:
        name = f"ComputeSEs.count({x},{y})"
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            continue
        if str(len(se[x][y].getCoords())) != row[3]:
            failures.append(f"{name}: python={len(se[x][y].getCoords())} java={row[3]}")

    row = golden.get("ComputeSEs.coords(12,12)")
    if row is None:
        failures.append("ComputeSEs.coords(12,12): missing from golden")
    else:
        want = row[3] if len(row) > 3 else ""
        got = ";".join(f"{p.x},{p.y}" for p in se[12][12].getCoords())
        if got != want:
            failures.append("ComputeSEs.coords(12,12): coordinate list mismatch")

    def check_gray(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        _, x, y, c, vals = gray_row(name, result)
        if (x, y, c) != (row[1], row[2], row[3]):
            failures.append(f"{name}: dims differ")
            return
        if vals != (row[4] if len(row) > 4 else ""):
            failures.append(f"{name}: pixel mismatch")

    check_gray("ADilation(img,se10)", ADilation.invoke(img, se))
    check_gray("AErosion(img,se10)", AErosion.invoke(img, se))
    check_gray("AOpening(img,se10)", AOpening.invoke(img, se))
    check_gray("AClosing(img,se10)", AClosing.invoke(img, se))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

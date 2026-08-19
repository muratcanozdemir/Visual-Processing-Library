"""Differential test of the vpt.algorithms.saliency package against the
Java oracle.

The golden file is produced by ``oracle/DumpSaliency.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.saliency import SaliencyMap, SaliencyMapSlow

GOLDEN = Path(__file__).parent / "goldens" / "saliency.txt"


def gray_vals(img) -> list[int]:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCByte(x, y, c))
    return vals


def make_rgb() -> ByteImage:
    rgb = ByteImage(20, 20, 3)
    for x in range(20):
        for y in range(20):
            rgb.setXYCByte(x, y, 0, (x * 10) % 256)
            rgb.setXYCByte(x, y, 1, (y * 10) % 256)
            rgb.setXYCByte(x, y, 2, ((x + y) * 5) % 256)
    return rgb


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_saliency_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_gray(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [int(v) for v in row[4].split(",")] if len(row) > 4 and row[4] else []
        a_vals = gray_vals(img)
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    rgb = make_rgb()
    check_gray("SaliencyMap(rgb)", SaliencyMap.invoke(rgb))
    check_gray("SaliencyMapSlow(rgb)", SaliencyMapSlow.invoke(rgb))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

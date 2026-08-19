"""Differential test of the vpt.algorithms.hough package against the Java
oracle.

Not covered here: ``HoughTransformLine2``/``HoughTransformCircle`` both
call ``Display2D`` in the Java, confirmed directly (a standalone
timeout-bounded harness run of the unmodified Java, not just inferred
from the ``linear.Canny`` precedent -- see ``HoughTransformLine2.py``'s
docstring) to hang the JVM forever. Smoke-tested here only: each is
called once to confirm it returns without error, and
``HoughTransformCircle`` gets two additional calls on deliberately
non-square images to exercise its confirmed `getXDim`-instead-of-
`getYDim` bug (see that file's docstring) -- one shaped to trigger the
resulting crash (`xdim > ydim`), one shaped to hit the silent-vote-drop
path instead (`ydim > xdim`), both asserted against directly since
there's no Java oracle counterpart to golden-compare against for the
non-square, `Display2D`-hanging case.

The golden file is produced by ``oracle/DumpHough.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.hough import HoughTransformCircle, HoughTransformLine, HoughTransformLine2

GOLDEN = Path(__file__).parent / "goldens" / "hough.txt"


def gray_vals(img) -> list[int]:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCByte(x, y, c))
    return vals


def make_img() -> ByteImage:
    img = ByteImage(16, 12, 1)
    img.fill(False)
    for x in range(16):
        img.setXYBoolean(x, 6, True)
    for y in range(12):
        img.setXYBoolean(8, y, True)
    return img


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_hough_matches_java_oracle():
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

    img = make_img()
    check_gray("HoughTransformLine(img)", HoughTransformLine.invoke(img))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )


def test_hough_display2d_files_smoke():
    img = make_img()
    assert HoughTransformLine2.invoke(img) is not None

    # xdim > ydim: the bounds-check bug lets an in-range `a` (< xdim) but
    # out-of-range `b` (>= ydim, < xdim) slip past the check, crashing the
    # write -- matches the Java's own ArrayIndexOutOfBoundsException.
    wide = ByteImage(20, 10, 1)
    wide.fill(False)
    for x in range(20):
        wide.setXYBoolean(x, 5, True)
    with pytest.raises(IndexError):
        HoughTransformCircle.invoke(wide)

    # ydim > xdim: the same bug now only rejects otherwise-valid `b`
    # values in [xdim, ydim) -- silently drops votes, doesn't crash.
    tall = ByteImage(10, 20, 1)
    tall.fill(False)
    for y in range(20):
        tall.setXYBoolean(5, y, True)
    assert HoughTransformCircle.invoke(tall) is not None

    # Square: the bug is inert (xdim == ydim), ordinary completion.
    square = ByteImage(14, 14, 1)
    square.fill(False)
    for x in range(14):
        square.setXYBoolean(x, 7, True)
    assert HoughTransformCircle.invoke(square) is not None

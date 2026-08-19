"""Differential test of vpt.algorithms.linear.{Laplacian,MeanFilter,Prewitt,
Gabor,RotationInvariantGabor} against the Java oracle. Convolution and Sobel
are golden-verified separately via test_linhist_golden.py. Canny has no
golden check -- see ``oracle/DumpLinear2.java``'s docstring for why (its
Java execute() opens a real Swing/JAI window) -- and is only smoke-tested
here.

The golden file is produced by ``oracle/DumpLinear2.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.linear import (
    Canny,
    Gabor,
    Laplacian,
    MeanFilter,
    Prewitt,
    RotationInvariantGabor,
)

GOLDEN = Path(__file__).parent / "goldens" / "linear2.txt"


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, str(img.getXDim()), str(img.getYDim()), str(img.getCDim()), ",".join(vals))


def make_g() -> ByteImage:
    g = ByteImage(12, 12, 1)
    for x in range(12):
        for y in range(12):
            inside = 3 <= x < 9 and 3 <= y < 9
            g.setXYCByte(x, y, 0, 200 if inside else 20)
    return g


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_linear2_matches_java_oracle():
    golden = load_golden()
    failures = []

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

    g = make_g()

    check_gray("Laplacian(g,false)", Laplacian.invoke(g, False))
    check_gray("Laplacian(g,true)", Laplacian.invoke(g, True))

    check_gray("MeanFilter(g,3)", MeanFilter.invoke(g, 3))
    check_gray("MeanFilter(g,5)", MeanFilter.invoke(g, 5))
    check_gray("MeanFilter(g,1)", MeanFilter.invoke(g, 1))

    for t in range(4):
        check_gray(f"Prewitt(g,{t})", Prewitt.invoke(g, t))

    orientations1 = [0.0]
    orientations2 = [0.0, 0.7853981633974483]

    check_gray(
        "Gabor(g,1.0,[0],0,0.5,1,5,5)",
        Gabor.invoke(g, 1.0, orientations1, 0.0, 0.5, 1.0, 5, 5),
    )
    check_gray(
        "Gabor(g,1.0,[0,pi/4],0,0.5,1,5,5)",
        Gabor.invoke(g, 1.0, orientations2, 0.0, 0.5, 1.0, 5, 5),
    )

    check_gray(
        "RotationInvariantGabor(g,1.0,[0,pi/4],0,0.5,1,5,5)",
        RotationInvariantGabor.invoke(g, 1.0, orientations2, 0.0, 0.5, 1.0, 5, 5),
    )

    # Canny: no golden (see module docstring); just check it runs and
    # produces a sane binary-ish [0,1] output of the right shape.
    canny_out = Canny.invoke(g, 0.2, 0.5)
    assert canny_out is not None
    assert (canny_out.getXDim(), canny_out.getYDim(), canny_out.getCDim()) == (12, 12, 1)

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

"""Differential test of the remaining vpt.algorithms.arithmetic operators
(Absolute, Addition, Average, Multiplication, VectorialMaximum,
VectorialMinimum) against the Java oracle.

The golden file is produced by ``oracle/DumpArith2.java`` (see
``python/tests/regen-goldens.sh``). AbsSubtraction, Minimum, Maximum, Equal,
AdditionC, SubtractionC, and Subtraction are already covered by
test_mm_golden.py / test_geo_golden.py.
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage, DoubleImage
from vpt.algorithms.arithmetic import (
    Absolute,
    Addition,
    Average,
    Multiplication,
    VectorialMaximum,
    VectorialMinimum,
)
from vpt.util.ordering import Lexico

GOLDEN = Path(__file__).parent / "goldens" / "arith2.txt"


def parse_double(s: str) -> float:
    if s == "Infinity":
        return float("inf")
    if s == "-Infinity":
        return float("-inf")
    if s == "NaN":
        return float("nan")
    return float.fromhex(s)


def bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, img.getXDim(), img.getYDim(), img.getCDim(), ",".join(vals))


def double_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def generate_gray() -> list[tuple]:
    a = ByteImage(2, 2, 1)
    for i in range(4):
        a.setByte(i, i * 80)
    b = ByteImage(2, 2, 1)
    for i in range(4):
        b.setByte(i, (3 - i) * 80)

    rgb1 = ByteImage(1, 1, 2)
    rgb1.setXYCByte(0, 0, 0, 100)
    rgb1.setXYCByte(0, 0, 1, 50)
    rgb2 = ByteImage(1, 1, 2)
    rgb2.setXYCByte(0, 0, 0, 80)
    rgb2.setXYCByte(0, 0, 1, 200)
    lex = Lexico()

    return [
        gray_row("Addition(a,b)", Addition.invoke(a, b)),
        gray_row("Average(a,b)", Average.invoke(a, b)),
        gray_row("Multiplication(a,b)", Multiplication.invoke(a, b)),
        gray_row("VectorialMaximum(rgb1,rgb2,lex)", VectorialMaximum.invoke(rgb1, rgb2, lex)),
        gray_row("VectorialMinimum(rgb1,rgb2,lex)", VectorialMinimum.invoke(rgb1, rgb2, lex)),
    ]


def generate_absolute():
    d = DoubleImage(2, 2, 1)
    d.setXYDouble(0, 0, -0.5)
    d.setXYDouble(1, 0, 0.75)
    d.setXYDouble(0, 1, -1.0)
    d.setXYDouble(1, 1, 0.0)
    return Absolute.invoke(d)


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_arith2_matches_java_oracle():
    golden = load_golden()
    failures = []

    for name, x, y, c, vals in generate_gray():
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            continue
        if (str(x), str(y), str(c)) != (row[1], row[2], row[3]):
            failures.append(f"{name}: dims differ")
            continue
        if vals != (row[4] if len(row) > 4 else ""):
            failures.append(f"{name}: pixel mismatch")

    abs_img = generate_absolute()
    row = golden.get("Absolute(d)")
    if row is None:
        failures.append("Absolute(d): missing from golden")
    else:
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = double_vals(abs_img)
        if len(g_vals) != len(a_vals):
            failures.append("Absolute(d): length mismatch")
        else:
            for g, a in zip(g_vals, a_vals):
                if bits(parse_double(g)) != bits(a):
                    failures.append(f"Absolute(d): value mismatch python={a} java={g}")

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

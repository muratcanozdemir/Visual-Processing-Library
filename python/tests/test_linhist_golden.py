"""Differential test of RGB2Gray, Histogram, ContrastStretch, Threshold,
OtsuThreshold, Convolution and Sobel against the Java oracle.

The golden file is produced by ``oracle/DumpLinHist.java`` (see
``python/tests/regen-goldens.sh``). Doubles are compared by IEEE-754 bit
pattern, as in ``test_core_golden.py``.
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from _platform import libm_ulp
from vpt import ByteImage, Point
from vpt.algorithms.conversion import RGB2Gray
from vpt.algorithms.histogram import ContrastStretch, Histogram
from vpt.algorithms.linear import Convolution, Sobel
from vpt.algorithms.segmentation import OtsuThreshold, Threshold
from vpt.util.se import NonFlatSE

GOLDEN = Path(__file__).parent / "goldens" / "linhist.txt"

_GX = [[-1.0, 0.0, 1.0], [-2.0, 0.0, 2.0], [-1.0, 0.0, 1.0]]
_GY = [[-1.0, -2.0, -1.0], [0.0, 0.0, 0.0], [1.0, 2.0, 1.0]]


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


def doubles_agree(golden_csv: str, values, max_ulp: int = 0) -> bool:
    g_vals = golden_csv.split(",") if golden_csv else []
    if len(g_vals) != len(values):
        return False
    for g, a in zip(g_vals, values):
        want = parse_double(g)
        if want != want:  # NaN
            if a == a:
                return False
            continue
        a = float(a)
        if bits(a) == bits(want):
            continue
        if max_ulp and math.isfinite(a) and math.isfinite(want) and (a < 0) == (want < 0):
            if abs(bits(a) - bits(want)) <= max_ulp:
                continue
        return False
    return True


def make_g() -> ByteImage:
    g = ByteImage(16, 16, 1)
    g.fill(50)
    for x in range(4, 12):
        for y in range(4, 12):
            g.setXYByte(x, y, 180)
    return g


def make_rgb() -> ByteImage:
    rgb = ByteImage(16, 16, 3)
    for x in range(16):
        for y in range(16):
            rgb.setXYCByte(x, y, 0, x * 15)
            rgb.setXYCByte(x, y, 1, y * 15)
            rgb.setXYCByte(x, y, 2, 120)
    return rgb


def gray_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def binary_row(name: str, img) -> tuple:
    bitstr = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                bitstr.append("1" if img.getXYCBoolean(x, y, c) else "0")
    return (name, img.getXDim(), img.getYDim(), img.getCDim(), "".join(bitstr))


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_linhist_matches_java_oracle():
    golden = load_golden()
    g = make_g()
    rgb = make_rgb()

    failures = []

    def check_vector(name, values):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if not doubles_agree(row[4] if len(row) > 4 else "", values):
            failures.append(f"{name}: vector mismatch")

    def check_gray(name, img, max_ulp=0):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        if (str(img.getXDim()), str(img.getYDim()), str(img.getCDim())) != (row[1], row[2], row[3]):
            failures.append(f"{name}: dims mismatch")
            return
        if not doubles_agree(row[4] if len(row) > 4 else "", gray_vals(img), max_ulp=max_ulp):
            failures.append(f"{name}: pixel mismatch")

    def check_binary(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        _, a_x, a_y, a_c, a_bits = binary_row(name, img)
        if (row[1], row[2], row[3], row[4]) != (str(a_x), str(a_y), str(a_c), a_bits):
            failures.append(f"{name}: mismatch")

    check_vector("Histogram(g,false)", Histogram.invoke(g, False))
    check_vector("Histogram(g,true)", Histogram.invoke(g, True))
    check_vector("Histogram(rgb,false)", Histogram.invoke(rgb, False))

    check_gray("RGB2Gray(rgb)", RGB2Gray.invoke(rgb))

    check_gray("ContrastStretch(g)", ContrastStretch.invoke(g))
    check_gray("ContrastStretch(rgb)", ContrastStretch.invoke(rgb))

    check_binary("Threshold(g,0.5)", Threshold.invoke(g, 0.5))
    check_binary("Threshold(g,0.7)", Threshold.invoke(g, 0.7))
    check_binary("OtsuThreshold(g)", OtsuThreshold.invoke(g))

    gx = NonFlatSE(Point(1, 1), _GX)
    gy = NonFlatSE(Point(1, 1), _GY)
    check_gray("Convolution(g,gx)", Convolution.invoke(g, gx))
    check_gray("Convolution(g,gy)", Convolution.invoke(g, gy))
    check_gray("Convolution(rgb,gx)", Convolution.invoke(rgb, gx))

    for t in range(4):
        # THETA (t=3), un-normalized: known 1-ulp divergence in Math.atan2's
        # general-case rounding vs glibc's, at pixels with a near-zero but
        # nonzero x-gradient. Not reproducible even across Java platforms
        # (java.lang.Math isn't required to be bit-portable, unlike
        # StrictMath) -- see _javacompat.jatan2 and python/README.md.
        # Contrast-stretching (t=3, normalized=True) is unaffected: verified
        # separately that all 256 pixels agree bit-exactly, since the affine
        # rescale doesn't preserve the 1-ulp gap at these pixel values.
        max_ulp = libm_ulp(1) if t == Sobel.THETA else 0
        check_gray(f"Sobel(g,{t},false)", Sobel.invoke(g, t, False), max_ulp=max_ulp)
        check_gray(f"Sobel(g,{t},true)", Sobel.invoke(g, t, True))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

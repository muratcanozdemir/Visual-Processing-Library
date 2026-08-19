"""Differential test of the vpt.algorithms.frequential package against the
Java oracle. All four files are confirmed non-functional in the Java
itself (verified directly against the JDK 21 oracle) -- this test exists
to confirm the port reproduces that exact broken behaviour bit-for-bit,
not to validate a working transform. See each file's docstring.

The golden file is produced by ``oracle/DumpFrequential.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.frequential import DFT2D, FFT, IDFT2D, IFFT

GOLDEN = Path(__file__).parent / "goldens" / "frequential.txt"


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


def double_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def make_g() -> ByteImage:
    g = ByteImage(4, 4, 1)
    for x in range(4):
        for y in range(4):
            g.setXYCByte(x, y, 0, 100 if (x == 1 and y == 1) else 10)
    return g


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_frequential_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_double(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = double_vals(result)
        if len(g_vals) != len(a_vals):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, a_vals):
            if bits(parse_double(g)) != bits(a):
                failures.append(f"{name}: value mismatch python={a} java={g}")

    g = make_g()

    fft = FFT.invoke(g, False, 1)
    check_double("FFT(g,false,REAL_IMAG)[0]", fft[0])
    check_double("FFT(g,false,REAL_IMAG)[1]", fft[1])

    fftMag = FFT.invoke(g, True, 3)
    check_double("FFT(g,true,MAG_PHASE)[0]", fftMag[0])
    check_double("FFT(g,true,MAG_PHASE)[1]", fftMag[1])

    check_double("IFFT(fft[0],fft[1])", IFFT.invoke(fft[0], fft[1]))

    dft = DFT2D.invoke(g, False)
    check_double("DFT2D(g,false)[0]", dft[0])
    check_double("DFT2D(g,false)[1]", dft[1])
    check_double("DFT2D(g,false)[2]", dft[2])

    dftShifted = DFT2D.invoke(g, True)
    check_double("DFT2D(g,true)[2]", dftShifted[2])

    check_double("IDFT2D(dft[0],dft[1])", IDFT2D.invoke(dft[0], dft[1]))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

"""Differential test of the small packages ported this session:
``logical.Quantization``, ``features.QFZBasedStableRegions``,
``io.{LoadRAW,LoadFLO}``, ``experimental.{AreaLimits,Weird}``,
``plants.*`` (``Angles``, ``DominantPointsCharacterization``,
``PetioleRemoval``, ``VeinProfile``), and
``segmentation.berkeley.Seg2Image`` (ported to
``vpt.algorithms.segmentation.Seg2Image`` -- the one genuinely portable
file in that Java subpackage; see that file's docstring).

Not covered here: ``noise.{Gaussian,Speckle}`` both use an unseeded RNG
(non-deterministic like ``segmentation.MeanShift``), so there's no
bit-exact target -- see ``vpt/algorithms/noise/__init__.py``.
``classifier.NearestNeighbour`` isn't ported at all (a ``main()``-only
hardcoded-dataset-path demo script, no reusable API). ``io.{Load,Save}``
aren't ported either (need an image-codec dependency this project
otherwise has none of). ``segmentation.berkeley``'s other 6 files
(a gamut-table ``ColorQFZRGB``/``ColorVector`` pair, four
``main()``-only test harnesses) and ``segmentation/texture/
TestOutexAngular.java`` remain excluded for the same reasons.

``LoadRAW``/``LoadFLO``/``Seg2Image`` read fixture files checked in at
``oracle/fixtures/`` -- shared with the Java driver so both sides read
byte-identical input.

The golden file is produced by ``oracle/DumpMisc.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import math
import struct
from pathlib import Path

import pytest

from _platform import libm_ulp
from vpt import ByteImage
from vpt.algorithms.experimental import AreaLimits, Weird
from vpt.algorithms.features import QFZBasedStableRegions
from vpt.algorithms.io import LoadFLO, LoadRAW
from vpt.algorithms.logical import Quantization
from vpt.algorithms.plants import (
    Angles,
    DominantPointsCharacterization,
    PetioleRemoval,
    VeinProfile,
)
from vpt.algorithms.segmentation.Seg2Image import Seg2Image
from vpt.util.se import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "misc.txt"
FIXTURES = Path(__file__).parent.parent.parent / "oracle" / "fixtures"


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


def make_g() -> ByteImage:
    g = ByteImage(8, 8, 1)
    for x in range(8):
        for y in range(8):
            g.setXYByte(x, y, (x * 32 + y * 7) % 256)
    return g


def make_stable() -> ByteImage:
    stable = ByteImage(24, 24, 1)
    for x in range(24):
        for y in range(24):
            stable.setXYByte(x, y, 40 if x < 12 else 190)
    return stable


def make_leaf() -> ByteImage:
    leaf = ByteImage(50, 50, 1)
    leaf.fill(0)
    for x in range(50):
        for y in range(50):
            dx = (x - 25) / 16.0
            dy = (y - 18) / 12.0
            if dx * dx + dy * dy <= 1.0:
                stripe = 220 if ((x + y) // 3) % 2 == 0 else 140
                leaf.setXYByte(x, y, stripe)
    for y in range(26, 46):
        for x in range(23, 27):
            leaf.setXYByte(x, y, 200)
    return leaf


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_misc_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_gray(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [int(v) for v in row[4].split(",")] if row[4] else []
        a_vals = []
        for y in range(img.getYDim()):
            for x in range(img.getXDim()):
                for c in range(img.getCDim()):
                    a_vals.append(img.getXYCByte(x, y, c))
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    def check_int(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [int(v) for v in row[4].split(",")] if row[4] else []
        a_vals = []
        for y in range(img.getYDim()):
            for x in range(img.getXDim()):
                for c in range(img.getCDim()):
                    a_vals.append(img.getXYCInt(x, y, c))
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    def check_bool(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [c == "1" for c in row[4].split(",")] if row[4] else []
        a_vals = []
        for y in range(img.getYDim()):
            for x in range(img.getXDim()):
                a_vals.append(img.getXYBoolean(x, y))
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    def check_vector(name, result):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[2].split(",") if len(row) > 2 and row[2] else []
        if len(g_vals) != len(result):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, result):
            if bits(parse_double(g)) != bits(a):
                failures.append(f"{name}: value mismatch python={a} java={g}")

    def check_double(name, img, max_ulp=0):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = row[4].split(",") if len(row) > 4 and row[4] else []
        a_vals = []
        for y in range(img.getYDim()):
            for x in range(img.getXDim()):
                for c in range(img.getCDim()):
                    a_vals.append(img.getXYCDouble(x, y, c))
        if len(g_vals) != len(a_vals):
            failures.append(f"{name}: length mismatch")
            return
        for g, a in zip(g_vals, a_vals):
            want = parse_double(g)
            if bits(a) == bits(want):
                continue
            if (
                max_ulp
                and math.isfinite(a)
                and math.isfinite(want)
                and (a < 0) == (want < 0)
                and abs(bits(a) - bits(want)) <= max_ulp
            ):
                continue
            failures.append(f"{name}: value mismatch python={a} java={g}")

    g = make_g()
    for bit in range(1, 9):
        check_gray(f"Quantization(g,{bit})", Quantization.invoke(g, bit))

    check_gray("AreaLimits(g,10,3)", AreaLimits.invoke(g, 10, 3))
    check_gray("Weird(g,sq3)", Weird.invoke(g, FlatSE.square(3)))

    stable = make_stable()
    check_bool("QFZBasedStableRegions(stable)", QFZBasedStableRegions.invoke(stable))

    leaf = make_leaf()
    check_vector("Angles(leaf)", Angles.invoke(leaf))
    check_vector(
        "DominantPointsCharacterization(leaf,6)",
        DominantPointsCharacterization.invoke(leaf, 6),
    )
    check_bool("PetioleRemoval(leaf)", PetioleRemoval.invoke(leaf))
    check_vector("VeinProfile(leaf,4,5,4)", VeinProfile.invoke(leaf, 4, 5, 4))

    check_gray(
        "LoadRAW(fixtures/test.raw)", LoadRAW.invoke(str(FIXTURES / "test.raw"))
    )
    # LoadFLO's normalised angle is built on Math.atan2, the same
    # fdlibm-vs-glibc divergence family as Sobel's THETA/ZernikeMoment
    # elsewhere in this port -- tolerate a couple ulp.
    check_double(
        "LoadFLO(fixtures/test.flo)",
        LoadFLO.invoke(str(FIXTURES / "test.flo")),
        max_ulp=libm_ulp(2),
    )

    check_int(
        "Seg2Image(fixtures/test.seg)", Seg2Image.invoke(str(FIXTURES / "test.seg"))
    )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

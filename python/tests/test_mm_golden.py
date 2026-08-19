"""Differential test of the basic gray/binary morphology operators against
the Java oracle.

The golden file is produced by ``oracle/DumpMM.java`` run against the
compiled Java tree (see ``python/tests/regen-goldens.sh``). This test
replays the identical sequence of operations in Python and asserts
byte-exact (grayscale) / boolean-exact (binary) agreement.

Committing the golden means this test runs in CI without a JDK.
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage
from vpt.algorithms.arithmetic import AbsSubtraction
from vpt.algorithms.mm.binary import (
    BBotHat,
    BClosing,
    BDilation,
    BErosion,
    BExternGradient,
    BGradient,
    BInternGradient,
    BOpening,
    BTopHat,
)
from vpt.algorithms.mm.gray import (
    GDMP,
    GOCCO,
    GBotHat,
    GClosing,
    GContrastEnhancement,
    GDilation,
    GErosion,
    GExternGradient,
    GGradient,
    GInternGradient,
    GInterpolation,
    GLambdaLeveling,
    GLaplacian,
    GMedianImage,
    GOpening,
    GRankFilter,
    GTopHat,
)
from vpt.util.se import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "mm.txt"


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


def make_b() -> BooleanImage:
    b = BooleanImage(16, 16, 1)
    b.fill(False)
    for x in range(4, 12):
        for y in range(4, 12):
            b.setXYBoolean(x, y, True)
    return b


def gray_row(name: str, img) -> tuple:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(str(img.getXYCByte(x, y, c)))
    return (name, img.getXDim(), img.getYDim(), img.getCDim(), ",".join(vals))


def binary_row(name: str, img) -> tuple:
    bits = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                bits.append("1" if img.getXYCBoolean(x, y, c) else "0")
    return (name, img.getXDim(), img.getYDim(), img.getCDim(), "".join(bits))


def generate() -> list[tuple]:
    g = make_g()
    rgb = make_rgb()
    b = make_b()

    ses = [FlatSE.square(3), FlatSE.square(5), FlatSE.disk(2), FlatSE.cross(3)]
    senames = ["square3", "square5", "disk2", "cross3"]

    rows = []
    for se, s in zip(ses, senames):
        rows.append(gray_row(f"GDilation(g,{s})", GDilation.invoke(g, se)))
        rows.append(gray_row(f"GErosion(g,{s})", GErosion.invoke(g, se)))
        rows.append(gray_row(f"GOpening(g,{s})", GOpening.invoke(g, se)))
        rows.append(gray_row(f"GClosing(g,{s})", GClosing.invoke(g, se)))
        rows.append(gray_row(f"GGradient(g,{s})", GGradient.invoke(g, se)))
        rows.append(gray_row(f"GInternGradient(g,{s})", GInternGradient.invoke(g, se)))
        rows.append(gray_row(f"GExternGradient(g,{s})", GExternGradient.invoke(g, se)))
        rows.append(gray_row(f"GBotHat(g,{s})", GBotHat.invoke(g, se)))
        rows.append(gray_row(f"GTopHat(g,{s})", GTopHat.invoke(g, se)))

        rows.append(binary_row(f"BDilation(b,{s})", BDilation.invoke(b, se)))
        rows.append(binary_row(f"BErosion(b,{s})", BErosion.invoke(b, se)))
        rows.append(binary_row(f"BOpening(b,{s})", BOpening.invoke(b, se)))
        rows.append(binary_row(f"BClosing(b,{s})", BClosing.invoke(b, se)))
        rows.append(binary_row(f"BGradient(b,{s})", BGradient.invoke(b, se)))
        rows.append(binary_row(f"BInternGradient(b,{s})", BInternGradient.invoke(b, se)))
        rows.append(binary_row(f"BExternGradient(b,{s})", BExternGradient.invoke(b, se)))
        rows.append(binary_row(f"BBotHat(b,{s})", BBotHat.invoke(b, se)))
        rows.append(binary_row(f"BTopHat(b,{s})", BTopHat.invoke(b, se)))

        rows.append(binary_row(f"BErosion(b,{s},false)", BErosion.invoke(b, se, False)))
        rows.append(
            binary_row(f"BInternGradient(b,{s},false)", BInternGradient.invoke(b, se, False))
        )

    se3 = FlatSE.square(3)
    rows.append(gray_row("GDilation(rgb,square3)", GDilation.invoke(rgb, se3)))
    rows.append(gray_row("GErosion(rgb,square3)", GErosion.invoke(rgb, se3)))
    rows.append(gray_row("GGradient(rgb,square3)", GGradient.invoke(rgb, se3)))

    rows.append(
        gray_row("AbsSubtraction(g,rgbChannel0)", AbsSubtraction.invoke(g, rgb.getChannel(0)))
    )

    # -- previously-unexercised mm/gray classes (see python/IMPROVEMENTS.md 1.1) --
    square3 = FlatSE.square(3)
    disk2 = FlatSE.disk(2)

    rows.append(gray_row("GRankFilter(g,square3,4)", GRankFilter.invoke(g, square3, 4)))
    rows.append(gray_row("GRankFilter(g,disk2,0)", GRankFilter.invoke(g, disk2, 0)))

    dilatedG = GDilation.invoke(g, square3)
    interp = GInterpolation.invoke(g, dilatedG, 1)
    for i, frame in enumerate(interp):
        rows.append(gray_row(f"GInterpolation(g,dilatedG,1)[{i}]", frame))

    rows.append(gray_row("GMedianImage(g,dilatedG)", GMedianImage.invoke(g, dilatedG)))

    erodedG = GErosion.invoke(g, square3)
    rows.append(
        gray_row(
            "GLambdaLeveling(g,erodedG,square3,20)",
            GLambdaLeveling.invoke(g, erodedG, square3, 20),
        )
    )

    rows.append(gray_row("GOCCO(g,square3)", GOCCO.invoke(g, square3)))
    rows.append(gray_row("GLaplacian(g,square3)", GLaplacian.invoke(g, square3)))
    rows.append(
        gray_row("GContrastEnhancement(g,square3)", GContrastEnhancement.invoke(g, square3))
    )

    # GDMP degenerates to all-zero on the flat two-plateau "g" image (marker
    # never fully detaches within 2 erosion/dilation steps, so every
    # reconstruction reconverges and every AbsSubtraction is 0) -- same
    # shape as MDMP's degeneracy on "rgb" in test_multi_golden.py. Use a
    # concentric-ring profile instead.
    profile = ByteImage(16, 16, 1)
    for x in range(16):
        for y in range(16):
            ring = min(x, y, 15 - x, 15 - y)
            profile.setXYByte(x, y, 20 + ring * 20)

    dmp = GDMP.invoke(profile, 2)
    for i, frame in enumerate(dmp):
        rows.append(gray_row(f"GDMP(profile,2)[{i}]", frame))

    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_mm_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpMM.java"
    )

    failures = []
    for n, (g_row, a_row) in enumerate(zip(golden, actual), start=1):
        g_name, g_x, g_y, g_c, g_vals = g_row
        a_name, a_x, a_y, a_c, a_vals = a_row

        if g_name != a_name:
            failures.append(f"line {n}: replay out of step: java={g_name} python={a_name}")
            continue

        if (g_x, g_y, g_c) != (str(a_x), str(a_y), str(a_c)):
            failures.append(
                f"line {n} {g_name}: dims differ: python={a_x}x{a_y}x{a_c} "
                f"java={g_x}x{g_y}x{g_c}"
            )
            continue

        if g_vals != a_vals:
            failures.append(f"line {n} {g_name}: pixel mismatch")

    assert not failures, (
        f"{len(failures)} of {len(golden)} golden rows differ from the Java "
        f"oracle:\n  " + "\n  ".join(failures[:40])
        + (f"\n  ... and {len(failures) - 40} more" if len(failures) > 40 else "")
    )

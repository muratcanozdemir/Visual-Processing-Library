"""Differential test of the mm/multi (vectorial morphology) family against
the Java oracle.

The golden file is produced by ``oracle/DumpMulti.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import ByteImage
from vpt.algorithms.mm.multi import (
    MDMP,
    MOCCO,
    MBotHat,
    MClosing,
    MDilation,
    MErosion,
    MEuclideanGradient,
    MHSYMaxGradient,
    MLambdaLeveling,
    MMaxGradient,
    MOpening,
    MRankFilter,
    MSaturationWeightedColorGradient,
    MTopHat,
)
from vpt.algorithms.mm.multi.connected import (
    MMaxTreeIncrAttrClosing,
    MMaxTreeIncrAttrOpening,
    MMaxTreeNonIncrAttrClosing,
    MMaxTreeNonIncrAttrOpening,
)
from vpt.algorithms.mm.multi.connected.attribute import (
    MultiArea,
    MultiDiagonal,
    MultiGeometricVolume,
    MultiSdev,
    MultiSdev2,
    MultiSpectralVolume,
)
from vpt.algorithms.mm.multi.geo import (
    CONNEXITY4,
    CONNEXITY8,
    MClosingByReconstruction,
    MClosingByReconstructionClassic,
    MFastReconstruction,
    MGeodesicDilation,
    MGeodesicErosion,
    MOpeningByReconstruction,
    MOpeningByReconstructionClassic,
    MReconstructionByDilation,
    MReconstructionByErosion,
)
from vpt.util.ordering import Lexico
from vpt.util.se import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "multi.txt"


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
    return (name, str(img.getXDim()), str(img.getYDim()), str(img.getCDim()), ",".join(vals))


def double_vals(img) -> list:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCDouble(x, y, c))
    return vals


def make_rgb() -> ByteImage:
    rgb = ByteImage(16, 16, 3)
    for x in range(16):
        for y in range(16):
            inside = 4 <= x < 12 and 4 <= y < 12
            rgb.setXYCByte(x, y, 0, 50 if inside else 10)
            rgb.setXYCByte(x, y, 1, 150 if inside else 60)
            rgb.setXYCByte(x, y, 2, 200 if inside else 90)
    return rgb


def make_profile() -> ByteImage:
    """Concentric square rings, each ring a distinct plateau -- see
    ``oracle/DumpMulti.java`` for why ``MDMP`` needs this rather than
    ``make_rgb()``'s single-edge block image (trivially all-zero at
    ``size=3`` there)."""
    profile = ByteImage(16, 16, 3)
    for x in range(16):
        for y in range(16):
            ring = min(x, y, 15 - x, 15 - y)
            profile.setXYCByte(x, y, 0, 20 + ring * 20)
            profile.setXYCByte(x, y, 1, 30 + ring * 15)
            profile.setXYCByte(x, y, 2, 40 + ring * 10)
    return profile


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_multi_matches_java_oracle():
    golden = load_golden()
    failures = []

    rgb = make_rgb()
    se = FlatSE.square(3)
    lex = Lexico()

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

    check_gray("MDilation(rgb,square3,lex)", MDilation.invoke(rgb, se, lex))
    check_gray("MErosion(rgb,square3,lex)", MErosion.invoke(rgb, se, lex))
    check_gray("MOpening(rgb,square3,lex)", MOpening.invoke(rgb, se, lex))
    check_gray("MClosing(rgb,square3,lex)", MClosing.invoke(rgb, se, lex))
    check_gray("MBotHat(rgb,square3,lex)", MBotHat.invoke(rgb, se, lex))
    check_gray("MTopHat(rgb,square3,lex)", MTopHat.invoke(rgb, se, lex))
    check_gray("MOCCO(rgb,square3,lex)", MOCCO.invoke(rgb, se, lex))

    check_double("MEuclideanGradient(rgb,square3)", MEuclideanGradient.invoke(rgb, se))
    check_gray("MMaxGradient(rgb,square3)", MMaxGradient.invoke(rgb, se))
    check_gray("MHSYMaxGradient(rgb,square3)", MHSYMaxGradient.invoke(rgb, se))
    check_gray(
        "MSaturationWeightedColorGradient(rgb,square3)",
        MSaturationWeightedColorGradient.invoke(rgb, se),
    )

    check_gray("MRankFilter(rgb,square3,lex,4)", MRankFilter.invoke(rgb, se, lex, 4))

    marker = rgb.newInstance(True)
    check_gray(
        "MLambdaLeveling(rgb,marker,square3,lex,10)",
        MLambdaLeveling.invoke(rgb, marker, se, lex, 10),
    )

    # -- mm/multi/geo --------------------------------------------------
    eroded = MErosion.invoke(rgb, se, lex)
    dilated = MDilation.invoke(rgb, se, lex)

    check_gray(
        "MGeodesicDilation(eroded,square3,rgb,lex)",
        MGeodesicDilation.invoke(eroded, se, rgb, lex),
    )
    check_gray(
        "MGeodesicErosion(dilated,square3,rgb,lex)",
        MGeodesicErosion.invoke(dilated, se, rgb, lex),
    )

    check_gray(
        "MReconstructionByDilation(eroded,square3,rgb,lex)",
        MReconstructionByDilation.invoke(eroded, se, rgb, lex),
    )
    check_gray(
        "MReconstructionByErosion(dilated,square3,rgb,lex)",
        MReconstructionByErosion.invoke(dilated, se, rgb, lex),
    )

    check_gray(
        "MFastReconstruction(eroded,rgb,CONNEXITY8,false,lex)",
        MFastReconstruction.invoke(eroded, rgb, CONNEXITY8, False, lex),
    )
    check_gray(
        "MFastReconstruction(dilated,rgb,CONNEXITY8,true,lex)",
        MFastReconstruction.invoke(dilated, rgb, CONNEXITY8, True, lex),
    )
    check_gray(
        "MFastReconstruction(eroded,rgb,CONNEXITY4,false,lex)",
        MFastReconstruction.invoke(eroded, rgb, CONNEXITY4, False, lex),
    )

    check_gray(
        "MOpeningByReconstruction(rgb,square3,lex)",
        MOpeningByReconstruction.invoke(rgb, se, lex),
    )
    check_gray(
        "MClosingByReconstruction(rgb,square3,lex)",
        MClosingByReconstruction.invoke(rgb, se, lex),
    )
    check_gray(
        "MOpeningByReconstructionClassic(rgb,square3,lex)",
        MOpeningByReconstructionClassic.invoke(rgb, se, lex),
    )
    check_gray(
        "MClosingByReconstructionClassic(rgb,square3,lex)",
        MClosingByReconstructionClassic.invoke(rgb, se, lex),
    )

    # -- MDMP ------------------------------------------------------------
    profile = make_profile()
    dmp = MDMP.invoke(profile, 3, lex)
    for i, img in enumerate(dmp):
        check_gray(f"MDMP(profile,3,lex)[{i}]", img)

    # -- mm/multi/connected -----------------------------------------------
    area2 = MultiArea(2)

    check_gray(
        "MMaxTreeIncrAttrOpening(rgb,MultiArea(2),lex)",
        MMaxTreeIncrAttrOpening.invoke(rgb, area2, lex),
    )
    check_gray(
        "MMaxTreeIncrAttrClosing(rgb,MultiArea(2),lex)",
        MMaxTreeIncrAttrClosing.invoke(rgb, area2, lex),
    )
    check_gray(
        "MMaxTreeNonIncrAttrOpening(rgb,MultiArea(2),lex)",
        MMaxTreeNonIncrAttrOpening.invoke(rgb, area2, lex),
    )
    check_gray(
        "MMaxTreeNonIncrAttrClosing(rgb,MultiArea(2),lex)",
        MMaxTreeNonIncrAttrClosing.invoke(rgb, area2, lex),
    )

    # The other three MultiAttribute predicates (see python/IMPROVEMENTS.md
    # 1.2) can't be golden-checked by calling .test(...) directly: it needs
    # the internal MyList<Point> the Java max-tree builder constructs, which
    # isn't reachable from outside that package. Routing them through
    # MMaxTreeIncrAttrOpening (which builds that structure itself) is also a
    # stronger check: a bit-exact whole-image match means .test() agreed on
    # every zone, not just one hand-picked one.
    vol = MultiSpectralVolume(3000)
    check_gray(
        "MMaxTreeIncrAttrOpening(rgb,MultiSpectralVolume(3000),lex)",
        MMaxTreeIncrAttrOpening.invoke(rgb, vol, lex),
    )

    diag = MultiDiagonal(2)
    check_gray(
        "MMaxTreeIncrAttrOpening(rgb,MultiDiagonal(2),lex)",
        MMaxTreeIncrAttrOpening.invoke(rgb, diag, lex),
    )

    geo = MultiGeometricVolume(1)
    check_gray(
        "MMaxTreeIncrAttrOpening(rgb,MultiGeometricVolume(1),lex)",
        MMaxTreeIncrAttrOpening.invoke(rgb, geo, lex),
    )

    # The vectorial MultiSdev/MultiSdev2 (unrelated to mm.gray's classes of
    # the same names), through the same MMaxTreeIncrAttrOpening route.
    for t in (1, 20):
        check_gray(
            f"MMaxTreeIncrAttrOpening(rgb,MultiSdev({t}),lex)",
            MMaxTreeIncrAttrOpening.invoke(rgb, MultiSdev(t), lex),
        )
        check_gray(
            f"MMaxTreeIncrAttrOpening(rgb,MultiSdev2({t}),lex)",
            MMaxTreeIncrAttrOpening.invoke(rgb, MultiSdev2(t), lex),
        )

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

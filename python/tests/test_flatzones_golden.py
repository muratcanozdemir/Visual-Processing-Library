"""Differential test of the vpt.algorithms.flatzones package against the
Java oracle.

Not covered here: ``ColorQFZRGB``, ``ColorQFZLSH``, ``ColorQFZRGBByte`` --
all three pre-sort a 16.7M-entry RGB gamut table through a caller-supplied
``AlgebraicOrdering``, the same category already excluded elsewhere in this
port (``mm/multi/connected/maxtree.MultivariateMaxTreeEntireGamut``); see
``flatzones/color/__init__.py``.

The golden file is produced by ``oracle/DumpFlatzones.java`` (see
``python/tests/regen-goldens.sh``). That driver's own stdout is not fully
clean: several of these Java classes call ``System.out.println`` directly
for progress/debug output ("Sutun N", "Total number of quasi flat zones:
N"), which lands in the captured golden alongside the real tab-separated
result rows. ``load_golden`` tolerates this: a line with no matching first
field is simply never looked up.
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage, DoubleImage
from vpt._javacompat import Point
from vpt.algorithms.flatzones.angular import (
    AngularQFZ,
    AngularQFZ2,
    AngularQFZ5,
    AngularQFZ6,
    LabelBasedAngularQFZ,
)
from vpt.algorithms.flatzones.color import (
    ColorFZRGB,
    ColorQFZRGBMarginal,
    ColorQFZRGBMarginalFromMarkers,
    ColorQFZSoille,
)
from vpt.algorithms.flatzones.gray import (
    GrayFZ,
    GrayFZ2,
    GrayQFZ,
    GrayQFZLocalRanges,
    GrayQFZSoille,
)
from vpt.algorithms.flatzones.multi import QFZMultiMarginal

GOLDEN = Path(__file__).parent / "goldens" / "flatzones.txt"


def int_vals(img) -> list[int]:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCInt(x, y, c))
    return vals


def gray_vals(img) -> list[int]:
    vals = []
    for y in range(img.getYDim()):
        for x in range(img.getXDim()):
            for c in range(img.getCDim()):
                vals.append(img.getXYCByte(x, y, c))
    return vals


def make_g() -> ByteImage:
    g = ByteImage(16, 16, 1)
    for x in range(16):
        for y in range(16):
            g.setXYByte(x, y, 30 if x < 12 else 180)
    for y in range(16):
        g.setXYByte(15, y, 30 + y * 10)
    return g


def make_ang() -> DoubleImage:
    ang = DoubleImage(12, 12, 1)
    for x in range(12):
        for y in range(12):
            ang.setXYDouble(x, y, 0.02 if x < 6 else 0.98)
    for y in range(12):
        ang.setXYDouble(11, y, y / 12.0)
    return ang


def make_rgb() -> ByteImage:
    rgb = ByteImage(10, 10, 3)
    for x in range(10):
        for y in range(10):
            left = x < 5
            rgb.setXYCByte(x, y, 0, 40 if left else 200)
            rgb.setXYCByte(x, y, 1, 60 if left else 90)
            rgb.setXYCByte(x, y, 2, 80 if left else 30)
    for y in range(10):
        rgb.setXYCByte(9, y, 0, 40 + y * 15)
        rgb.setXYCByte(9, y, 1, 60)
        rgb.setXYCByte(9, y, 2, 80)
    return rgb


def make_mask() -> BooleanImage:
    mask = BooleanImage(10, 10, 1)
    mask.fill(False)
    for x in range(10):
        for y in range(5):
            mask.setXYBoolean(x, y, True)
    return mask


def make_multi4() -> ByteImage:
    multi4 = ByteImage(8, 8, 4)
    for x in range(8):
        for y in range(8):
            for c in range(4):
                multi4.setXYCByte(x, y, c, 20 + c * 5 if x < 4 else 200 - c * 5)
    return multi4


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_flatzones_matches_java_oracle():
    golden = load_golden()
    failures = []

    def check_int(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [int(v) for v in row[4].split(",")] if len(row) > 4 and row[4] else []
        a_vals = int_vals(img)
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    def check_gray(name, img):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        g_vals = [int(v) for v in row[4].split(",")] if len(row) > 4 and row[4] else []
        a_vals = gray_vals(img)
        if g_vals != a_vals:
            failures.append(f"{name}: value mismatch python={a_vals} java={g_vals}")

    def check_points(name, result: list[Point]):
        row = golden.get(name)
        if row is None:
            failures.append(f"{name}: missing from golden")
            return
        want = row[3].split(";") if len(row) > 3 and row[3] else []
        if int(row[1]) != len(result):
            failures.append(f"{name}: length mismatch")
            return
        got = [f"{p.x},{p.y}" for p in result]
        if got != want:
            failures.append(f"{name}: point mismatch python={got} java={want}")

    def check_components(name, ccs):
        count_row = golden.get(f"{name}-count")
        if count_row is None:
            failures.append(f"{name}-count: missing from golden")
            return
        if int(count_row[3]) != len(ccs):
            failures.append(
                f"{name}-count: python={len(ccs)} java={count_row[3]}"
            )
            return
        for i, cc in enumerate(ccs):
            check_points(f"{name}[{i}]", cc)

    g = make_g()
    check_int("GrayFZ(g)", GrayFZ.invoke(g))
    check_components("GrayFZ2(g)", GrayFZ2.invoke(g))
    check_int("GrayQFZ(g,10,60)", GrayQFZ.invoke(g, 10, 60))
    check_gray("GrayQFZLocalRanges(g,10,60)", GrayQFZLocalRanges.invoke(g, 10, 60))
    check_int("GrayQFZSoille(g,10,60)", GrayQFZSoille.invoke(g, 10, 60))

    ang = make_ang()
    check_int("AngularQFZ(ang,0.1,0.3)", AngularQFZ.invoke(ang, 0.1, 0.3))
    check_int("AngularQFZ2(ang,0.1,0.3)", AngularQFZ2.invoke(ang, 0.1, 0.3))
    check_int("AngularQFZ5(ang,0.1,0.3)", AngularQFZ5.invoke(ang, 0.1, 0.3))
    check_int("AngularQFZ6(ang,0.1,0.3)", AngularQFZ6.invoke(ang, 0.1, 0.3))
    check_int(
        "LabelBasedAngularQFZ(ang,10,60,0.02)",
        LabelBasedAngularQFZ.invoke(ang, 10, 60, 0.02),
    )

    rgb = make_rgb()
    check_int("ColorFZRGB(rgb)", ColorFZRGB.invoke(rgb))
    check_int("ColorQFZRGBMarginal(rgb,10,60)", ColorQFZRGBMarginal.invoke(rgb, 10, 60))

    mask = make_mask()
    check_int(
        "ColorQFZRGBMarginalFromMarkers(rgb,10,60,mask)",
        ColorQFZRGBMarginalFromMarkers.invoke(rgb, 10, 60, mask),
    )

    check_int("ColorQFZSoille(rgb,10,60)", ColorQFZSoille.invoke(rgb, 10, 60))

    multi4 = make_multi4()
    check_int("QFZMultiMarginal(multi4,10,60)", QFZMultiMarginal.invoke(multi4, 10, 60))

    assert not failures, (
        f"{len(failures)} golden checks failed:\n  " + "\n  ".join(failures[:40])
    )

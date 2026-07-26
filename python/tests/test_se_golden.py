"""Differential test of FlatSE against the Java oracle.

Golden produced by ``oracle/DumpSE.java``.

What this pins is the exact pixel mask of every factory, the BOX flag that
selects the separable fast path in dilation/erosion, and the ``getCoords()``
ordering the morphology operators iterate. It also pins NumPy's cos/sin
against Java's Math.cos/Math.sin, which are separate implementations and need
not agree in the last ULP.

It does *not* meaningfully exercise the rounding mode: none of the 640 arc
coordinates dumped here lands on a .5 boundary, so half-up and half-even
agree throughout. That is measured, not assumed -- see DumpSE.java. The
rounding mode is covered by test_core_golden instead.
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt.util.se.FlatSE import FlatSE

GOLDEN = Path(__file__).parent / "goldens" / "se.txt"


def describe(se: FlatSE) -> tuple[str, str, str, str, str]:
    """Same five dumped fields as DumpSE.dump, in the same order."""
    mask = "".join(
        "1" if se.getXYBoolean(x, y) else "0"
        for y in range(se.getYDim())
        for x in range(se.getXDim())
    )
    coords = ";".join(f"{p.x},{p.y}" for p in se.getCoords())
    return (str(se.getXDim()), str(se.getYDim()), "-",
            str(se.BOX).lower(), mask, coords)


def generate() -> list[tuple]:
    """Replay DumpSE.java's sequence. Order must match the Java exactly."""
    rows: list[tuple] = []

    def dump(name, se):
        rows.append((name,) + describe(se))

    def dumpAll(name, ses):
        rows.append((f"{name}-count", "0", "0", "-", "false", str(len(ses)), ""))
        for i, se in enumerate(ses):
            dump(f"{name}[{i}]", se)

    for size in [1, 2, 3, 4, 5, 7, 9, 16]:
        dump(f"square({size})", FlatSE.square(size))
        dump(f"hollowSquare({size})", FlatSE.hollowSquare(size))
        dump(f"cross({size})", FlatSE.cross(size))
        dump(f"hLine({size})", FlatSE.hLine(size))
        dump(f"vLine({size})", FlatSE.vLine(size))

    for size in [5, 7]:
        for x in range(size):
            for y in range(size):
                dump(f"square({size},{x},{y})", FlatSE.square(size, x, y))
        dump(f"hLine({size},1)", FlatSE.hLine(size, 1))
        dump(f"vLine({size},1)", FlatSE.vLine(size, 1))

    for r in [0, 1, 2, 3, 4, 5, 6, 8, 11]:
        dump(f"disk({r})", FlatSE.disk(r))
        dump(f"circle({r})", FlatSE.circle(r))
        dump(f"centerlessCircle({r})", FlatSE.centerlessCircle(r))
        dump(f"partiallyFilledDisk({r},{r // 2})", FlatSE.partiallyFilledDisk(r, r // 2))
        dumpAll(f"partialArcs({r})", FlatSE.partialArcs(r))
        dumpAll(f"partialArcs2({r})", FlatSE.partialArcs2(r))
        dumpAll(f"partialCircles({r})", FlatSE.partialCircles(r))
        dumpAll(f"partialCircles2({r})", FlatSE.partialCircles2(r))
        dumpAll(f"pointCouples({r})", FlatSE.pointCouples(r))
        dumpAll(f"pointCouples2({r})", FlatSE.pointCouples2(r))

    for r in [1, 3, 5]:
        dump(f"disk({r}).reflection", FlatSE.disk(r).reflection())
        dump(f"square({2 * r + 1}).reflection", FlatSE.square(2 * r + 1).reflection())
        dump(f"square({2 * r + 1}).getHLine", FlatSE.square(2 * r + 1).getHLine())
        dump(f"square({2 * r + 1}).getVLine", FlatSE.square(2 * r + 1).getVLine())
        dump(f"disk({r}).getHLine", FlatSE.disk(r).getHLine())
        dump(f"disk({r}).getVLine", FlatSE.disk(r).getVLine())

    for r in [1, 3, 5]:
        se = FlatSE.centerlessCircle(r)
        lc = se.getListCoords()
        rows.append((f"centerlessCircle({r}).listCoords", "0", "0", "-", "false",
                     str(len(lc)), ";".join(f"{p.x},{p.y}" for p in lc)))

    return rows


FIELDS = ["xdim", "ydim", "center", "BOX", "mask", "coords"]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_se_matches_java_oracle():
    golden = [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, golden has "
        f"{len(golden)} -- the replay has drifted from DumpSE.java"
    )

    failures = []
    for n, (g, a) in enumerate(zip(golden, actual), start=1):
        # Java's trailing empty coords column is dropped by split() when the
        # line ends with a tab; pad both sides to a fixed width.
        g = list(g) + [""] * (7 - len(g))
        a = list(a) + [""] * (7 - len(a))

        if g[0] != a[0]:
            failures.append(f"line {n}: replay out of step: java={g[0]} python={a[0]}")
            continue
        for field, gv, av in zip(FIELDS, g[1:], a[1:]):
            if gv != av:
                failures.append(f"line {n} {g[0]} {field}: python={av!r} java={gv!r}")

    assert not failures, (
        f"{len(failures)} of {len(golden)} structuring elements differ from the "
        f"Java oracle:\n  " + "\n  ".join(failures[:30])
        + (f"\n  ... and {len(failures) - 30} more" if len(failures) > 30 else "")
    )

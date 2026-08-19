"""Differential test of NonFlatSE and FloatingFlatSE against the Java oracle.

Golden produced by ``oracle/DumpNonFlatSE.java``.

Everything here is compared bit-exactly except ``decompose``, which is
compared sign-invariantly. That is not a weakened assertion for convenience:
Java's Jama and NumPy's LAPACK both return valid SVDs that may differ by a
joint sign flip on the singular vectors, and Jama's own sign is inconsistent
across inputs (positive for Gaussian(1), negative for Gaussian(2)). The sign
is unobservable through the library -- ``Convolution``, the only caller,
discards the result on the next line -- so the test pins what is actually
determined: the outer product, the magnitudes, and the shape.
"""

from __future__ import annotations

import math
from pathlib import Path

import numpy as np
import pytest

from vpt.util.se.FloatingFlatSE import FloatingFlatSE
from vpt.util.se.NonFlatSE import NonFlatSE

GOLDEN = Path(__file__).parent / "goldens" / "nonflatse.txt"


def parse_double(s: str) -> float:
    if s in ("Infinity", "-Infinity", "NaN"):
        return {"Infinity": float("inf"), "-Infinity": float("-inf"),
                "NaN": float("nan")}[s]
    return float.fromhex(s)


def split_values(s: str) -> list[str]:
    """Split a dumped value column.

    NonFlatSE rows separate values with ';'. FloatingFlatSE rows dump
    coordinate *pairs*, 'x,y;x,y', so both separators have to be honoured.
    """
    return [t for t in s.replace(",", ";").split(";") if t]


def describe(se: NonFlatSE) -> tuple:
    vals = [se.getXYDouble(x, y)
            for y in range(se.getYDim())
            for x in range(se.getXDim())]
    coords = ";".join(
        "null" if p is None else f"{p.x},{p.y}" for p in se.getCoords()
    )
    return (str(se.getXDim()), str(se.getYDim()),
            f"{se.center.x},{se.center.y}", vals, coords)


def generate() -> list[tuple]:
    """Replay DumpNonFlatSE.java's sequence. Order must match exactly."""
    rows: list[tuple] = []

    def dump(name, se):
        rows.append((name,) + describe(se))

    for r in [0, 1, 2, 3, 5, 7]:
        dump(f"cylinder({r},1.0)", NonFlatSE.cylinder(r, 1.0))
        dump(f"cylinder({r},0.5)", NonFlatSE.cylinder(r, 0.5))
        dump(f"cone({r},1.0)", NonFlatSE.cone(r, 1.0))
        dump(f"cone({r},2.5)", NonFlatSE.cone(r, 2.5))
    for r in [1, 2, 3, 4, 6]:
        dump(f"Gaussian({r})", NonFlatSE.Gaussian(r))
        dump(f"Gaussian({r}).reflection", NonFlatSE.Gaussian(r).reflection())
    for w in range(1, 5):
        for h in range(1, 5):
            dump(f"box({w},{h},0.25)", NonFlatSE.box(w, h, 0.25))

    for r in [1, 2, 3, 4, 6]:
        d = NonFlatSE.Gaussian(r).decompose()
        if d is None:
            rows.append((f"Gaussian({r}).decompose", "0", "0", "-", "null", ""))
        else:
            dump(f"Gaussian({r}).decompose[0]", d[0])
            dump(f"Gaussian({r}).decompose[1]", d[1])
    for r in [2, 3]:
        d = NonFlatSE.cone(r, 1.0).decompose()
        rows.append((f"cone({r},1.0).decompose", "0", "0", "-",
                     "null" if d is None else "pair", ""))
    for w in range(1, 5):
        d = NonFlatSE.box(w, w, 0.25).decompose()
        if d is None:
            rows.append((f"box({w}).decompose", "0", "0", "-", "null", ""))
        else:
            dump(f"box({w}).decompose[0]", d[0])
            dump(f"box({w}).decompose[1]", d[1])

    for r in [1, 2, 3, 5]:
        for per in [4, 8, 12]:
            se = FloatingFlatSE.circle(r, per)
            vals = []
            for p in se.getCoords():
                vals.append(p.x)
                vals.append(p.y)
            rows.append((f"FloatingFlatSE.circle({r},{per})", "0", "0", "-",
                         vals, ""))
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_nonflatse_matches_java_oracle():
    golden = [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, golden has "
        f"{len(golden)} -- the replay has drifted from DumpNonFlatSE.java"
    )

    failures = []
    for n, (g, a) in enumerate(zip(golden, actual), start=1):
        g = list(g) + [""] * (6 - len(g))
        name, g_x, g_y, g_c, g_vals, g_coords = g[:6]
        a_name, a_x, a_y, a_c, a_vals, a_coords = a

        if name != a_name:
            failures.append(f"line {n}: replay out of step: java={name} python={a_name}")
            continue

        sign_invariant = ".decompose[" in name

        fields = [("xdim", g_x, a_x), ("ydim", g_y, a_y), ("center", g_c, a_c)]
        if not sign_invariant:
            fields.append(("coords", g_coords, a_coords))
        for field, gv, av in fields:
            if gv != av:
                failures.append(f"line {n} {name} {field}: python={av!r} java={gv!r}")

        if isinstance(a_vals, str):  # the "null"/"pair" marker rows
            if g_vals != a_vals:
                failures.append(f"line {n} {name}: python={a_vals!r} java={g_vals!r}")
            continue

        want = [parse_double(t) for t in split_values(g_vals)]
        if len(want) != len(a_vals):
            failures.append(f"line {n} {name}: {len(a_vals)} values, java has {len(want)}")
            continue

        if sign_invariant:
            # getCoords' output is itself sign-dependent: a negative centre
            # value suppresses the leading (0,0) and leaves a trailing null
            # (the preserved abs() inconsistency documented on getCoords).
            # Demanding the two sides agree would be demanding they agree on
            # the SVD sign, which is not determined. Instead assert each side
            # produced the pattern ITS OWN sign implies -- that verifies the
            # mechanism is faithfully ported without inventing a convention.
            for side, vals, coords in (("java", want, g_coords),
                                       ("python", a_vals, a_coords)):
                centre = vals[len(vals) // 2]
                got = coords.split(";")
                if centre >= 0.0:
                    ok = got[0] == "0,0" and "null" not in got
                    shape = "lead with 0,0 and contain no null"
                else:
                    ok = got[-1] == "null" and "0,0" not in got
                    shape = "end with null and omit 0,0"
                if not ok:
                    failures.append(
                        f"line {n} {name} coords ({side}, centre={centre!r}): "
                        f"expected to {shape}, got {coords!r}"
                    )

        for i, (wv, av) in enumerate(zip(want, a_vals)):
            if sign_invariant:
                ok = math.isclose(abs(wv), abs(av), rel_tol=1e-12, abs_tol=1e-300)
            else:
                ok = (wv == av) or (wv != wv and av != av)
            if not ok:
                failures.append(
                    f"line {n} {name} value[{i}]: python={av!r} java={wv!r}"
                    + (" (compared on |value|)" if sign_invariant else "")
                )

    assert not failures, (
        f"{len(failures)} differences from the Java oracle:\n  "
        + "\n  ".join(failures[:30])
        + (f"\n  ... and {len(failures) - 30} more" if len(failures) > 30 else "")
    )


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_decompose_outer_product_reconstructs_kernel():
    """The property `decompose` actually guarantees, independent of sign.

    This is what a caller would rely on if the separable path in Convolution
    were still live, so it is worth asserting directly rather than inferring
    from the sign-invariant value comparison above.
    """
    for r in [1, 2, 3, 4, 6]:
        se = NonFlatSE.Gaussian(r)
        d = se.decompose()
        assert d is not None, f"Gaussian({r}) should be rank 1"
        h, v = d

        hv = np.array([h.getXYDouble(i, 0) for i in range(h.getXDim())])
        vv = np.array([v.getXYDouble(0, i) for i in range(v.getYDim())])
        product = np.outer(vv, hv)  # [y][x]

        original = np.array([[se.getXYDouble(x, y) for x in range(se.getXDim())]
                             for y in range(se.getYDim())])

        assert np.allclose(product, original, rtol=1e-10, atol=1e-14), (
            f"Gaussian({r}): outer product of the decomposition does not "
            f"reconstruct the kernel (max err "
            f"{np.max(np.abs(product - original)):.3e})"
        )

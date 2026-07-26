"""Differential test of the Image hierarchy against the Java oracle.

The golden file is produced by ``oracle/DumpCore.java`` run against the
compiled Java tree (see ``python/tests/regen-goldens.sh``). This test replays
exactly the same sequence of operations in Python and asserts bit-exact
agreement -- doubles are compared by their IEEE-754 bit pattern, not by
tolerance, because the failure modes being guarded against (round-half-even
instead of round-half-up, an unbiased byte store, a non-wrapping shift)
produce values that are close enough to survive any reasonable epsilon.

Committing the golden means this test runs in CI without a JDK.
"""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt import BooleanImage, ByteImage, DoubleImage, Image, IntegerImage

GOLDEN = Path(__file__).parent / "goldens" / "core.txt"

TYPES = ["ByteImage", "BooleanImage", "IntegerImage", "DoubleImage"]

BYTE_VALS = [0, 1, 2, 63, 64, 127, 128, 129, 200, 254, 255,
             256, 257, -1, -2, -128, -129, 300, 1000, -1000]

INT_VALS = [0, 1, -1, 255, 256, 65536, 1 << 24, (1 << 24) - 1,
            -(1 << 24), 2147483647, -2147483648,
            2147483646, -2147483647,
            123456789, -123456789]

DBL_VALS = [0.0, 1.0, 0.5, 0.25, 0.75, -0.0, -1.0, 2.0, -2.0,
            0.1, 0.2, 0.3, 1.0 / 3.0, 2.0 / 3.0,
            1.0 / 255.0, 2.0 / 255.0, 127.0 / 255.0, 128.0 / 255.0,
            254.0 / 255.0, 0.5 / 255.0, 1.5 / 255.0, 2.5 / 255.0,
            0.49999999999999994, 1e-300, 1e300, -1e300,
            0.0019607843137254902, 0.5019607843137255]


def fresh(t: str) -> Image:
    return {"ByteImage": ByteImage, "BooleanImage": BooleanImage,
            "IntegerImage": IntegerImage, "DoubleImage": DoubleImage}[t](1, 1, 1)


def parse_double(s: str) -> float:
    """Parse a Java Double.toHexString value."""
    if s == "Infinity":
        return float("inf")
    if s == "-Infinity":
        return float("-inf")
    if s == "NaN":
        return float("nan")
    return float.fromhex(s)


def bits(x: float) -> int:
    """IEEE-754 bit pattern, for exact comparison including signed zero."""
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def cells_agree(golden: str, actual) -> bool:
    """Compare one dumped cell against its Python counterpart.

    Most rows carry (boolean, byte, int, double), but the summary rows reuse
    those four columns for other types, so the comparison keys off what the
    Java actually printed rather than off the column position. Floats are
    compared by bit pattern; everything else by Java's string form.
    """
    if isinstance(actual, bool):
        return golden == str(actual).lower()
    if isinstance(actual, float):
        try:
            want = parse_double(golden)
        except ValueError:
            return False
        if want != want:  # NaN
            return actual != actual
        return bits(actual) == bits(want)
    return golden == str(int(actual))


def sweep(img: Image) -> tuple:
    return (img.getBoolean(0), img.getByte(0), img.getInt(0), img.getDouble(0))


def generate() -> list[tuple]:
    """Replay DumpCore.java's sequence. Order must match the Java exactly."""
    rows: list[tuple] = []

    def row(t, op, arg, quad):
        rows.append((t, op, arg, quad[0], quad[1], quad[2], quad[3]))

    for t in TYPES:
        for v in BYTE_VALS:
            img = fresh(t)
            img.setByte(0, v)
            row(t, "setByte", str(v), sweep(img))

    for t in TYPES:
        for v in INT_VALS:
            img = fresh(t)
            img.setInt(0, v)
            row(t, "setInt", str(v), sweep(img))

    for t in TYPES:
        for v in DBL_VALS:
            img = fresh(t)
            img.setDouble(0, v)
            row(t, "setDouble", v, sweep(img))

    for t in TYPES:
        for v in (True, False):
            img = fresh(t)
            img.setBoolean(0, v)
            row(t, "setBoolean", str(v).lower(), sweep(img))

    for t in TYPES:
        for v in [0, 1, 127, 128, 255, 256, -1]:
            img = fresh(t)
            img.fillInt(v)
            row(t, "fillInt", str(v), sweep(img))
        for v in [0.0, 0.5, 1.0, 2.0, -1.0, 0.00392156862745098]:
            img = fresh(t)
            img.fillDouble(v)
            row(t, "fillDouble", v, sweep(img))
        for v in (True, False):
            img = fresh(t)
            img.fillBoolean(v)
            row(t, "fillBoolean", str(v).lower(), sweep(img))

    for t in TYPES:
        img = fresh(t).newInstance(3, 4, 2)
        n = 0
        for y in range(4):
            for x in range(3):
                for c in range(2):
                    img.setXYCByte(x, y, c, (n * 37) % 256)
                    n += 1
        for y in range(4):
            for x in range(3):
                for c in range(2):
                    row(t, "index", f"{x},{y},{c}",
                        (img.getXYCBoolean(x, y, c), img.getXYCByte(x, y, c),
                         img.getXYCInt(x, y, c), img.getXYCDouble(x, y, c)))

    for t in TYPES:
        img = fresh(t).newInstance(5, 5, 1)
        for y in range(5):
            for x in range(5):
                img.setXYByte(x, y, (x * 5 + y) * 7 % 256)
        row(t, "helpers", "-",
            (img.getSize(), img.getDiagonal(), img.isEmpty(0), img.isFull(0)))
        row(t, "maximum", "-",
            (img.getXYCMaximum(0).x, img.getXYCMaximum(0).y,
             img.getXYCMinimum(0).x, img.getXYCMinimum(0).y))

    d = DoubleImage(3, 1, 1)
    d.setXYDouble(0, 0, -1e250)
    d.setXYDouble(1, 0, -1e200)
    d.setXYDouble(2, 0, -1e100)
    row("DoubleImage", "maxSentinel", "-",
        (d.getXYCMaximum(0).x, d.getXYCMaximum(0).y, 0, 0.0))
    d2 = DoubleImage(3, 1, 1)
    for i in range(3):
        d2.setXYDouble(i, 0, -1e35)
    row("DoubleImage", "maxSentinelBelow", "-",
        (d2.getXYCMaximum(0).x, d2.getXYCMaximum(0).y, 0, 0.0))

    d = DoubleImage(4, 4, 1)
    for y in range(4):
        for x in range(4):
            d.setXYDouble(x, y, (x * 4 + y) / 16.0)
    coords = [0.0, 0.5, 1.25, 2.75, 3.0, 2.999999]
    for px in coords:
        for py in coords:
            row("DoubleImage", "pget", (px, py),
                (False, 0, 0, d.pgetXYCDouble(px, py, 0)))

    for src in TYPES:
        a = fresh(src).newInstance(2, 2, 1)
        for i in range(4):
            a.setByte(i, i * 60)
        for dst in TYPES:
            b = {"ByteImage": ByteImage, "BooleanImage": BooleanImage,
                 "IntegerImage": IntegerImage, "DoubleImage": DoubleImage}[dst](a, True)
            for i in range(4):
                row(f"{src}->{dst}", "copy", str(i),
                    (b.getBoolean(i), b.getByte(i), b.getInt(i), b.getDouble(i)))

    for t in TYPES:
        img = fresh(t).newInstance(2, 2, 3)
        n = 0
        for y in range(2):
            for x in range(2):
                for c in range(3):
                    img.setXYCByte(x, y, c, (n * 23) % 256)
                    n += 1
        for c in range(3):
            ch = img.getChannel(c)
            for y in range(2):
                for x in range(2):
                    row(t, "getChannel", f"{c}:{x},{y}",
                        (ch.getXYBoolean(x, y), ch.getXYByte(x, y),
                         ch.getXYInt(x, y), ch.getXYDouble(x, y)))

    return rows


def load_golden() -> list[list[str]]:
    return [ln.rstrip("\n").split("\t") for ln in GOLDEN.read_text().splitlines()]


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_core_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    assert len(actual) == len(golden), (
        f"row count differs: python produced {len(actual)}, "
        f"golden has {len(golden)} -- the replay has drifted from DumpCore.java"
    )

    failures = []
    for n, (g, a) in enumerate(zip(golden, actual), start=1):
        g_type, g_op, g_arg, g_bool, g_byte, g_int, g_dbl = g
        a_type, a_op, a_arg, a_bool, a_byte, a_int, a_dbl = a

        if (g_type, g_op) != (a_type, a_op):
            failures.append(f"line {n}: replay out of step: java={g[:3]} python={a[:3]}")
            continue

        for col, (g_cell, a_cell) in enumerate(
            zip((g_bool, g_byte, g_int, g_dbl), (a_bool, a_byte, a_int, a_dbl))
        ):
            if not cells_agree(g_cell, a_cell):
                failures.append(
                    f"line {n} {g_type}.{g_op}({g_arg}) col{col}: "
                    f"python={a_cell!r} java={g_cell}"
                )

    assert not failures, (
        f"{len(failures)} of {len(golden)} golden rows differ from the Java "
        f"oracle:\n  " + "\n  ".join(failures[:40])
        + (f"\n  ... and {len(failures) - 40} more" if len(failures) > 40 else "")
    )

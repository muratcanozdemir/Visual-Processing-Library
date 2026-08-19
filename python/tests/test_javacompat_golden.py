"""Differential test of the Java-semantics compat layer.

Golden produced by ``oracle/DumpJavaCompat.java``. These are the primitives
every other ported file is built on, so they are checked in both their scalar
and their vectorised form -- an algorithm that vectorises a loop must not
change the arithmetic.
"""

from __future__ import annotations

import struct
from pathlib import Path

import numpy as np
import pytest

from vpt._javacompat import (
    idiv,
    imod,
    ishl,
    ishr,
    jacos,
    jasin,
    jatan,
    jbyte,
    jbyte_arr,
    jdiv,
    jint,
    jint_arr,
    jlog,
    jround,
    jround_arr,
    jsqrt,
)

UNARY = {"fsqrt": jsqrt, "flog": jlog, "facos": jacos,
         "fasin": jasin, "fatan": jatan}

GOLDEN = Path(__file__).parent / "goldens" / "javacompat.txt"


def _bits(x: float) -> int:
    """IEEE-754 bit pattern, so NaN, +-Infinity and signed zero all differ."""
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def parse_double(s: str) -> float:
    return {"Infinity": float("inf"), "-Infinity": float("-inf"),
            "NaN": float("nan")}.get(s) or float.fromhex(s)


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_javacompat_matches_java():
    failures = []
    rows = 0

    for n, line in enumerate(GOLDEN.read_text().splitlines(), start=1):
        f = line.split("\t")
        kind = f[0]
        rows += 1

        if kind == "round":
            d = parse_double(f[1])
            want = int(f[2])
            got = jround(d)
            got_vec = int(jround_arr(np.array([d]))[0])
        elif kind == "int":
            d = parse_double(f[1])
            want = int(f[2])
            got = jint(d)
            got_vec = int(jint_arr(np.array([d]))[0])
        elif kind == "byte":
            want = int(f[2])
            got = jbyte(int(f[1]))
            got_vec = int(jbyte_arr(np.array([int(f[1])]))[0])
        elif kind == "shl24":
            want = int(f[2])
            got = got_vec = ishl(int(f[1]), 24)
        elif kind == "shr24":
            want = int(f[2])
            got = got_vec = ishr(int(f[1]), 24)
        elif kind == "div":
            a, b = int(f[1]), int(f[2])
            want = (int(f[3]), int(f[4]))
            got = got_vec = (idiv(a, b), imod(a, b))
        elif kind == "fdiv":
            # Doubles are compared by bit pattern so that NaN, +-Infinity and
            # signed zero are all distinguished. Python raises where Java
            # returns these, which is the whole point of jdiv.
            want = _bits(parse_double(f[3]))
            got = got_vec = _bits(jdiv(parse_double(f[1]), parse_double(f[2])))
        elif kind in UNARY:
            want = _bits(parse_double(f[2]))
            got = got_vec = _bits(UNARY[kind](parse_double(f[1])))
        else:
            failures.append(f"line {n}: unknown row kind {kind!r}")
            continue

        if got != want:
            failures.append(f"line {n} {kind}({f[1]}) scalar: python={got} java={want}")
        if got_vec != want:
            failures.append(f"line {n} {kind}({f[1]}) vectorised: "
                            f"python={got_vec} java={want}")

    assert rows > 0, "golden file is empty"
    assert not failures, f"{len(failures)} of {rows} rows differ:\n  " + "\n  ".join(failures)

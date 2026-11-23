"""Golden test for ``vpt.util`` pieces no other driver reaches, replaying
``oracle/DumpUtil.java``: ``Distance`` (every metric and overload), the
orderings not exercised elsewhere (``BitMix``, ``CompositeOrdering``,
``CustomLexico``, ``EuclideanNorm``, ``LexicoCIELAB``, ``ReverseLexico``,
``SpectralAngular``), and ``HugeHierarchicalQueue``. Mirrors the driver call
for call; see it for why each input was chosen."""

from __future__ import annotations

import struct
from pathlib import Path

import pytest

from vpt._javacompat import Point
from vpt.util.Distance import Distance
from vpt.util.HugeHierarchicalQueue import HugeHierarchicalQueue
from vpt.util.ordering.BitMix import BitMix
from vpt.util.ordering.CompositeOrdering import CompositeOrdering
from vpt.util.ordering.CustomLexico import CustomLexico
from vpt.util.ordering.EuclideanNorm import EuclideanNorm
from vpt.util.ordering.LexicoCIELAB import LexicoCIELAB
from vpt.util.ordering.ReverseLexico import ReverseLexico
from vpt.util.ordering.SpectralAngular import SpectralAngular

GOLDEN = Path(__file__).parent / "goldens" / "util.txt"

VS = [
    [0.0, 0.0, 0.0],
    [0.5, 0.2, 0.9],
    [0.5, 0.2, 0.1],
    [0.5, 0.7, 0.1],
    [0.1, 0.9, 0.9],
    [0.9, 0.1, 0.1],
    [0.5, 0.2, 0.9],
    [0.25, 0.5, 0.75],
    [1.0, 1.0, 1.0],
    [0.3, 0.4, 0.0],
    [0.3, 0.0, 0.4],
    [0.2, 0.4, 0.6],
]


def parse_double(s: str) -> float:
    if s == "NaN":
        return float("nan")
    if s == "Infinity":
        return float("inf")
    if s == "-Infinity":
        return float("-inf")
    return float.fromhex(s)


def bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def same(a: float, want: float) -> bool:
    if want != want:  # NaN: the golden text carries no sign/payload bits
        return a != a
    return bits(a) == bits(want)


def load_golden() -> dict[str, list[str]]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        parts = ln.split("\t")
        rows[parts[0]] = parts[1:]
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_util_matches_java_oracle():
    golden = load_golden()
    failures: list[str] = []

    def row(name):
        r = golden.get(name)
        if r is None:
            failures.append(f"{name}: missing from golden")
        return r

    def check_value(name, a, max_ulp=0):
        r = row(name)
        if r is None:
            return
        want = parse_double(r[0])
        if same(float(a), want):
            return
        if max_ulp and abs(bits(float(a)) - bits(want)) <= max_ulp and (a < 0) == (want < 0):
            return
        failures.append(f"{name}: python={a!r} java={r[0]}")

    def check_vector(name, vec):
        r = row(name)
        if r is None:
            return
        want = [parse_double(v) for v in r[1].split(",")] if r[1] else []
        if int(r[0]) != len(vec) or not all(same(float(a), w) for a, w in zip(vec, want)):
            failures.append(f"{name}: python={list(vec)} java={r[1]}")

    def check_text(name, text):
        r = row(name)
        if r is not None and r[0] != text:
            failures.append(f"{name}: python={text} java={r[0]}")

    p = [0.1, 0.4, 0.2, 0.3]
    q = [0.25, 0.25, 0.3, 0.2]
    r_ = [2.0, -1.0, 0.5, 3.0]
    z = [0.0, 0.0, 0.0, 0.0]
    shortv = [1.0, 2.0]
    pairs = [(p, q), (p, r_), (r_, q), (p, p), (z, z), (p, z)]
    names = ["p,q", "p,r", "r,q", "p,p", "z,z", "p,z"]
    metrics = [
        "euclidean", "intersection", "bhattacharyya", "cosine",
        "spectralAngleDistance", "innerProduct", "manhattan", "chebyshev",
        "chiSquare",
    ]
    for (a, b), n in zip(pairs, names):
        for m in metrics:
            # spectralAngleDistance ends in acos. Java's Math.acos is fdlibm
            # (== StrictMath.acos); Python's is glibc. With bit-identical
            # arguments they can still differ by 1 ulp (seen at r,q) -- the
            # same accepted fdlibm-vs-glibc divergence as jatan2 documents.
            tol = 1 if m == "spectralAngleDistance" else 0
            check_value(f"{m}({n})", getattr(Distance, m)(a, b), max_ulp=tol)

    check_value("euclidean(mismatch)", Distance.euclidean(p, shortv))
    check_value("chiSquare(mismatch)", Distance.chiSquare(p, shortv))
    check_value("euclidean(int)", Distance.euclidean([3, 250, 7], [10, 0, 7]))
    check_value("EuclideanDistance(int)", Distance.EuclideanDistance(1, 2, 4, 6))
    check_value("EuclideanDistance(double)", Distance.EuclideanDistance(0.5, -1.5, 2.25, 3.0))
    check_value(
        "EuclideanDistance(Point)", Distance.EuclideanDistance(Point(3, 4), Point(-1, 1))
    )

    def check_ordering(name, order):
        matrix = ",".join(str(order.compare(a, b)) for a in VS for b in VS)
        check_text(f"{name}.compare", matrix)
        check_vector(f"{name}.max", order.max(VS))
        check_vector(f"{name}.min", order.min(VS))
        # rank() sorts in place, like Java's Arrays.sort: pass a copy.
        check_vector(f"{name}.rank(4)", order.rank(list(VS), 4))
        check_vector(f"{name}.max2", order.max(VS[1], VS[3]))
        check_vector(f"{name}.min2", order.min(VS[1], VS[3]))

    check_ordering("BitMix", BitMix())
    check_ordering("EuclideanNorm", EuclideanNorm())
    check_ordering("LexicoCIELAB", LexicoCIELAB())
    check_ordering("ReverseLexico", ReverseLexico())
    check_ordering("SpectralAngular", SpectralAngular())
    check_ordering("CustomLexico(3)", CustomLexico(3))
    check_ordering("CustomLexico(2,0,1)", CustomLexico([2, 0, 1]))
    # compare() is a majority vote that indexes exactly three orderings.
    check_ordering(
        "CompositeOrdering(EuclideanNorm,ReverseLexico,BitMix)",
        CompositeOrdering([EuclideanNorm(), ReverseLexico(), BitMix()]),
    )

    hq = HugeHierarchicalQueue(4)
    hq.add(Point(1, 1), 1000000)
    hq.add(Point(2, 2), 5)
    hq.add(Point(3, 3), 1000000)
    hq.add(Point(4, 4), -7)
    parts = []
    for v in (1000000, 5, 1000000, 42, 5, -7, 1000000):
        s = f"{v}{':E:' if hq.isEmpty(v) else ':N:'}"
        try:
            pt = hq.get(v)
            s += "null" if pt is None else f"{pt.x},{pt.y}"
        except IndexError:  # Java: LinkedList.remove() -> NoSuchElementException
            s += "EXC"
        parts.append(s)
    check_text("HugeHierarchicalQueue.script", ";".join(parts))

    assert not failures, f"{len(failures)} golden checks failed:\n  " + "\n  ".join(
        failures[:40]
    )

"""Differential test of the mm/gray/connected/attribute predicate classes
against the Java oracle.

The golden file is produced by ``oracle/DumpMTAttr.java`` (see
``python/tests/regen-goldens.sh``).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from vpt import ByteImage, Point
from vpt.algorithms.mm.gray.connected.attribute import (
    Area,
    Diagonal,
    Entropy,
    GeometricVolume,
    HyperRectangleEdgeDifference,
    InvariantMoment,
    Mean,
    MultiEntropy,
    MultiInvariantMoment,
    MultiSdev,
    MultiSdev2,
    Sdev,
    SphereRadius,
    Volume,
)

GOLDEN = Path(__file__).parent / "goldens" / "mtattr.txt"


def make_img() -> ByteImage:
    img = ByteImage(10, 10, 1)
    img.fill(50)
    for x in range(2, 6):
        for y in range(2, 6):
            img.setXYByte(x, y, 200)
    return img


def make_img2() -> ByteImage:
    img2 = ByteImage(10, 10, 2)
    for x in range(2, 6):
        for y in range(2, 6):
            img2.setXYCByte(x, y, 0, 200)
            img2.setXYCByte(x, y, 1, 100)
    return img2


def make_pixels() -> list[Point]:
    return [Point(x, y) for x in range(2, 6) for y in range(2, 6)]


def generate() -> list[tuple]:
    img = make_img()
    img2 = make_img2()
    pixels = make_pixels()

    rows = [
        ("Area(10)", Area(10).test(pixels, img)),
        ("Area(20)", Area(20).test(pixels, img)),
        ("Volume(3000)", Volume(3000).test(pixels, img)),
        ("Volume(4000)", Volume(4000).test(pixels, img)),
        ("Mean(0.5)", Mean(0.5).test(pixels, img)),
        ("Mean(0.9)", Mean(0.9).test(pixels, img)),
        ("Sdev(0)", Sdev(0).test(pixels, img)),
        ("Sdev(1)", Sdev(1).test(pixels, img)),
        ("Entropy(0.0)", Entropy(0.0).test(pixels, img)),
        ("Entropy(1.0)", Entropy(1.0).test(pixels, img)),
        ("Diagonal(1)", Diagonal(1).test(pixels, img)),
        ("Diagonal(100)", Diagonal(100).test(pixels, img)),
        ("GeometricVolume(1)", GeometricVolume(1).test(pixels, img)),
        (
            "HyperRectangleEdgeDifference(0)",
            HyperRectangleEdgeDifference(0).test(pixels, img),
        ),
        (
            "HyperRectangleEdgeDifference(1)",
            HyperRectangleEdgeDifference(1).test(pixels, img),
        ),
        ("SphereRadius(0)", SphereRadius(0).test(pixels, img2)),
        ("SphereRadius(1)", SphereRadius(1).test(pixels, img2)),
        ("MultiEntropy(0.0)", MultiEntropy(0.0).test(pixels, img2)),
        ("MultiSdev(0)", MultiSdev(0).test(pixels, img2)),
        ("MultiSdev(1)", MultiSdev(1).test(pixels, img2)),
        ("MultiSdev2(0.0)", MultiSdev2(0.0).test(pixels, img2)),
    ]

    for order in range(1, 9):
        rows.append(
            (f"InvariantMoment(0.0,{order})", InvariantMoment(0.0, order).test(pixels, img))
        )
    rows.append(
        ("MultiInvariantMoment(0.0,1)", MultiInvariantMoment(0.0, 1).test(pixels, img2))
    )

    return rows


def load_golden() -> dict[str, str]:
    rows = {}
    for ln in GOLDEN.read_text().splitlines():
        name, val = ln.split("\t")
        rows[name] = val
    return rows


@pytest.mark.skipif(not GOLDEN.exists(), reason="golden not generated")
def test_mtattr_matches_java_oracle():
    golden = load_golden()
    actual = generate()

    failures = []
    for name, value in actual:
        want = golden.get(name)
        if want is None:
            failures.append(f"{name}: missing from golden")
            continue
        if str(value).lower() != want:
            failures.append(f"{name}: python={value} java={want}")

    assert not failures, (
        f"{len(failures)} of {len(actual)} golden checks failed:\n  "
        + "\n  ".join(failures[:40])
    )

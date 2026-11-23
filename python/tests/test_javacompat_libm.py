"""Unit checks for the libm-backed array helpers in ``vpt._javacompat``.

These helpers exist because numpy's transcendental ufuncs pick CPU-specific
kernels (SIMD on AVX-512) that are not bit-identical to libm or to the Java
oracle. Each ``*_arr`` helper must agree bit-for-bit with its scalar
counterpart (which calls ``math.*``) on every element, keep Java's special
cases, and preserve the input's shape.
"""

from __future__ import annotations

import math
import struct

import numpy as np
import pytest

from vpt._javacompat import (
    jacos,
    jacos_arr,
    jatan2,
    jatan2_arr,
    jcos,
    jcos_arr,
    jexp,
    jexp_arr,
    jlog10,
    jlog10_arr,
    jpow,
    jpow_arr,
    jsin,
    jsin_arr,
)

EDGE = [
    0.0, -0.0, 1.0, -1.0, 0.5, -0.5, 1e-300, -1e-300, 5e-324,
    math.pi, -math.pi, 1e6, -1e6, 1e22, 709.0, 710.0, -745.0, -1000.0,
    math.inf, -math.inf, math.nan,
]


def bits(x: float) -> int:
    return struct.unpack("<Q", struct.pack("<d", x))[0]


def same(a: float, b: float) -> bool:
    if a != a and b != b:
        return True
    return bits(a) == bits(b)


@pytest.mark.parametrize(
    "scalar, arr",
    [(jsin, jsin_arr), (jcos, jcos_arr), (jexp, jexp_arr), (jlog10, jlog10_arr), (jacos, jacos_arr)],
)
def test_unary_arr_matches_scalar(scalar, arr):
    xs = np.array(EDGE + list(np.linspace(-50.0, 50.0, 1001)))
    out = arr(xs)
    assert out.dtype == np.float64 and out.shape == xs.shape
    for x, got in zip(xs, out):
        assert same(float(got), scalar(float(x))), (scalar.__name__, x)


def test_atan2_arr_matches_scalar():
    ys, xs = np.meshgrid(np.array(EDGE), np.array(EDGE))
    out = jatan2_arr(ys, xs)
    for y, x, got in zip(ys.ravel(), xs.ravel(), out.ravel()):
        assert same(float(got), jatan2(float(y), float(x)))


def test_pow_arr_matches_scalar_2d():
    base = np.array(EDGE).reshape(3, 7)
    for b in (0.333333, 3.0, 2.0, 0.5, -1.0):
        out = jpow_arr(base, b)
        assert out.shape == base.shape
        for a, got in zip(base.ravel(), out.ravel()):
            assert same(float(got), jpow(float(a), b)), (a, b)


def test_java_special_cases():
    assert math.isnan(jsin(math.inf)) and math.isnan(jcos(-math.inf))
    assert jexp(1000.0) == math.inf and jexp(-math.inf) == 0.0
    assert jlog10(0.0) == -math.inf and math.isnan(jlog10(-1.0))
    assert math.isnan(jacos(1.5))


def test_shapes_preserved():
    x = np.linspace(0.0, 1.0, 12).reshape(2, 3, 2)
    assert jsin_arr(x).shape == (2, 3, 2)
    assert jsin_arr(np.float64(0.5)).shape == ()
    assert jatan2_arr(x, np.float64(1.0)).shape == (2, 3, 2)

"""Property tests for ``vpt.algorithms.noise``.

The Java originals draw from an unseeded ``java.util.Random``, so there is no
golden to compare against; the port uses Python's ``random``. These tests
seed it and check the properties that define each generator instead.
"""

from __future__ import annotations

import random
import statistics

import numpy as np
import pytest

from vpt.ByteImage import ByteImage
from vpt.DoubleImage import DoubleImage
from vpt.algorithms.noise import Gaussian, Speckle

W = H = 64


def gray(value: float) -> DoubleImage:
    img = DoubleImage(W, H, 1)
    img.fill(value)
    return img


def ramp() -> DoubleImage:
    img = DoubleImage(W, H, 1)
    for y in range(H):
        for x in range(W):
            img.setXYCDouble(x, y, 0, (x + y) / (W + H - 2))
    return img


@pytest.fixture(autouse=True)
def _seed():
    random.seed(12345)


def test_gaussian_leaves_input_untouched_and_clamps():
    src = ramp()
    before = src.getDoubleArray().copy()
    out = Gaussian.invoke(src, 0.3)
    assert np.array_equal(src.getDoubleArray(), before)
    vals = out.getDoubleArray()
    assert vals.min() >= 0.0 and vals.max() <= 1.0
    # With deviation 0.3 on a ramp, some pixels must hit each clamp bound.
    assert (vals == 0.0).any() and (vals == 1.0).any()


def test_gaussian_zero_deviation_is_identity():
    src = ramp()
    out = Gaussian.invoke(src, 0.0)
    assert np.array_equal(out.getDoubleArray(), src.getDoubleArray())


def test_gaussian_statistics_on_mid_gray():
    dev = 0.05
    vals = Gaussian.invoke(gray(0.5), dev).getDoubleArray()
    # 4096 samples, |noise| ~never reaches the 0.5 clamp distance: the mean is
    # within a few standard errors (dev / 64) of 0.5 and the spread of dev.
    assert abs(statistics.fmean(vals) - 0.5) < 4 * dev / 64
    assert abs(statistics.pstdev(vals) - dev) < 0.1 * dev


def test_gaussian_keeps_image_type():
    src = ByteImage(8, 8, 3)
    out = Gaussian.invoke(src, 0.1)
    assert type(out) is ByteImage
    assert (out.getXDim(), out.getYDim(), out.getCDim()) == (8, 8, 3)


def test_speckle_flips_expected_fraction_to_black_or_white():
    prob = 0.2
    src = gray(0.5)
    out = Speckle.invoke(src, prob).getDoubleArray()
    changed = out != 0.5
    assert set(np.unique(out[changed])) <= {0.0, 1.0}
    frac = changed.mean()
    # Binomial(4096, 0.2): standard deviation of the fraction ~0.006.
    assert abs(frac - prob) < 0.03
    white = (out == 1.0).sum() / changed.sum()
    assert 0.4 < white < 0.6
    assert np.array_equal(src.getDoubleArray(), np.full(W * H, 0.5))


@pytest.mark.parametrize("prob, expect_all", [(0.0, False), (1.0, True)])
def test_speckle_probability_bounds(prob, expect_all):
    out = Speckle.invoke(gray(0.5), prob).getDoubleArray()
    changed = out != 0.5
    assert changed.all() if expect_all else not changed.any()

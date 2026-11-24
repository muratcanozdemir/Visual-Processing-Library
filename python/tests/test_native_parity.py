"""The Rust kernels (``vpt._native``) against the pure-Python code they
replace.

Each kernel must return exactly what the Python returns, so every case here
runs an algorithm twice -- with ``vpt._accel.native`` switched on and off --
and compares the full results: image type, dimensions and raw storage, or
every ``FlatSE``'s pixels, centre and ``BOX`` flag. The inputs cover all
four image types (including multi-channel ones, whose kernels read only the
first ``xdim * ydim`` entries), degenerate sizes, runs longer than 255
(``BPathOpening``'s byte wraparound), out-of-range ``area`` values, and
``ComputeSEs`` sizes that push the ``tmpImg`` byte wraparound and the
negative-index behaviour of ``FlatSE`` storage.

The golden tests check the same algorithms against the Java oracle; these
add inputs the goldens don't have. Skipped when the extension isn't built
(``VPT_PURE_PYTHON=1`` or a pure-Python install).
"""

from __future__ import annotations

import numpy as np
import pytest

from vpt import BooleanImage, ByteImage, DoubleImage, IntegerImage, _accel
from vpt.algorithms.mm.binary.attribute import BAreaClosing, BAreaOpening
from vpt.algorithms.mm.binary.path import BPathOpening
from vpt.algorithms.mm.gray.adaptive import ComputeSEs
from vpt.algorithms.mm.gray.connected import GAreaClosing, GAreaOpening
from vpt.algorithms.mm.gray.path import GPathOpening

NATIVE = _accel.native

pytestmark = pytest.mark.skipif(NATIVE is None, reason="vpt._native not built or disabled")

TYPES = [BooleanImage, ByteImage, IntegerImage, DoubleImage]


def make(cls, xdim, ydim, cdim, rng, density=0.5):
    img = cls(xdim, ydim, cdim)
    size = xdim * ydim * cdim
    fg = rng.random(size) < density
    if cls is BooleanImage:
        img._pixels[:] = fg
    elif cls is ByteImage:
        img._pixels[:] = np.where(fg, rng.integers(-127, 128, size), -128)
    elif cls is IntegerImage:
        img._pixels[:] = np.where(fg, rng.integers(-(2**31) + 1, 2**31, size), -(2**31))
    else:
        img._pixels[:] = np.where(fg, rng.uniform(1e-9, 1.0, size), rng.uniform(-1.0, 0.0, size))
    return img


def gray(xdim, ydim, rng, levels=256):
    img = ByteImage(xdim, ydim, 1)
    img.setByteArray(rng.integers(0, levels, xdim * ydim))
    return img


def lines(xdim, ydim, rng):
    """Long straight runs (longer than 255 on the long axis), so path
    lengths wrap in the ByteImage, plus diagonal and random noise."""
    img = BooleanImage(xdim, ydim, 1)
    a = np.zeros((ydim, xdim), bool)
    a[ydim // 2, :] = True
    a[:, xdim // 3] = True
    for i in range(min(xdim, ydim)):
        a[i, i] = True
    a |= rng.random((ydim, xdim)) < 0.2
    img._pixels[:] = a.ravel()
    return img


def run_both(monkeypatch, fn):
    native = fn()
    with monkeypatch.context() as m:
        m.setattr(_accel, "native", None)
        pure = fn()
    return native, pure


def assert_same_image(a, b):
    assert type(a) is type(b)
    assert (a.getXDim(), a.getYDim(), a.getCDim()) == (b.getXDim(), b.getYDim(), b.getCDim())
    assert a._pixels.dtype == b._pixels.dtype
    assert a._pixels.tobytes() == b._pixels.tobytes()


SHAPES = [(1, 1), (1, 7), (7, 1), (2, 2), (5, 3), (17, 11), (40, 33)]


@pytest.mark.parametrize("cls", TYPES)
@pytest.mark.parametrize("cdim", [1, 3])
def test_bpathopening(monkeypatch, cls, cdim):
    rng = np.random.default_rng(1)
    for xdim, ydim in SHAPES:
        for density in (0.0, 0.3, 0.7, 1.0):
            img = make(cls, xdim, ydim, cdim, rng, density)
            for length in (0, 1, 3, 6, 300):
                a, b = run_both(monkeypatch, lambda: BPathOpening.invoke(img, length))
                assert_same_image(a, b)


def test_bpathopening_long_runs(monkeypatch):
    rng = np.random.default_rng(2)
    for xdim, ydim in [(300, 7), (7, 300), (280, 270)]:
        img = lines(xdim, ydim, rng)
        for length in (2, 50, 260):
            a, b = run_both(monkeypatch, lambda: BPathOpening.invoke(img, length))
            assert_same_image(a, b)


@pytest.mark.parametrize("alg", [BAreaOpening, BAreaClosing])
@pytest.mark.parametrize("cls", TYPES)
@pytest.mark.parametrize("cdim", [1, 3])
def test_barea(monkeypatch, alg, cls, cdim):
    rng = np.random.default_rng(3)
    for xdim, ydim in SHAPES:
        for density in (0.0, 0.2, 0.45, 0.6, 1.0):
            img = make(cls, xdim, ydim, cdim, rng, density)
            for area in (-(2**70), -1, 0, 1, 2, 5, 40, xdim * ydim, xdim * ydim + 1, 2**70):
                a, b = run_both(monkeypatch, lambda: alg.invoke(img, area))
                assert_same_image(a, b)


def test_barea_non_int_area_falls_back(monkeypatch):
    # A float area isn't handed to the kernel (it takes an integer); the
    # Python path runs either way, so results agree trivially -- but it
    # must not raise.
    rng = np.random.default_rng(4)
    img = make(BooleanImage, 12, 9, 1, rng)
    a, b = run_both(monkeypatch, lambda: BAreaOpening.invoke(img, 3.5))
    assert_same_image(a, b)


def assert_same_ses(a, b):
    assert len(a) == len(b)
    for col_a, col_b in zip(a, b):
        assert len(col_a) == len(col_b)
        for sa, sb in zip(col_a, col_b):
            assert_same_image(sa, sb)
            assert (sa.center.x, sa.center.y) == (sb.center.x, sb.center.y)
            assert sa.BOX == sb.BOX


@pytest.mark.parametrize("size", [-2, -1, 0, 1, 2, 3, 4, 7, 12])
def test_computeses(monkeypatch, size):
    rng = np.random.default_rng(10 + size)
    for xdim, ydim in [(1, 1), (1, 5), (6, 1), (5, 4), (13, 9)]:
        for levels in (2, 8, 256):
            img = gray(xdim, ydim, rng, levels)
            a, b = run_both(monkeypatch, lambda: ComputeSEs.invoke(img, size))
            assert_same_ses(a, b)


def test_computeses_byte_wraparound(monkeypatch):
    # size // 2 >= 256 lets tmpImg costs exceed 255 and wrap in the
    # ByteImage. Columns alternating 0/255 make every horizontal or diagonal
    # hop cost 1 + 255 // 4 = 64, so four hops already reach 257 (stored
    # as 1); random noise adds irregular paths.
    rng = np.random.default_rng(20)
    stripes = np.tile(np.arange(12) % 2 * 255, 4)
    for vals in (stripes, np.where(rng.random(48) < 0.5, 0, 255)):
        img = gray(12, 4, rng)
        img.setByteArray(vals)
        for size in (520, 600):
            a, b = run_both(monkeypatch, lambda: ComputeSEs.invoke(img, size))
            assert_same_ses(a, b)


@pytest.mark.parametrize("cls", [BooleanImage, IntegerImage, DoubleImage])
def test_computeses_other_types(monkeypatch, cls):
    # getXYByte of Integer/Double images can lie far outside 0..255.
    rng = np.random.default_rng(30)
    img = make(cls, 9, 7, 1, rng, 0.6)
    if cls is DoubleImage:
        img._pixels[:] = rng.uniform(-3.0, 3.0, 63)
    for size in (2, 5, 9):
        a, b = run_both(monkeypatch, lambda: ComputeSEs.invoke(img, size))
        assert_same_ses(a, b)


def test_computeses_out_of_range_index_raises_like_python(monkeypatch):
    # size -3: the SE is 3x3 and the root lands on flat index -12, out of
    # range even counting from the end; numpy raises IndexError there.
    img = gray(3, 3, np.random.default_rng(40))
    with pytest.raises(IndexError):
        ComputeSEs.invoke(img, -3)
    monkeypatch.setattr(_accel, "native", None)
    with pytest.raises(IndexError):
        ComputeSEs.invoke(img, -3)


@pytest.mark.parametrize(
    "fn",
    [
        lambda img: GPathOpening.invoke(img, 4),
        lambda img: GAreaOpening.invoke(img, 6),
        lambda img: GAreaClosing.invoke(img, 6),
    ],
    ids=["GPathOpening", "GAreaOpening", "GAreaClosing"],
)
def test_gray_operators(monkeypatch, fn):
    rng = np.random.default_rng(50)
    for xdim, ydim in [(1, 1), (9, 7), (24, 19)]:
        img = gray(xdim, ydim, rng, 16)
        a, b = run_both(monkeypatch, lambda: fn(img))
        assert_same_image(a, b)

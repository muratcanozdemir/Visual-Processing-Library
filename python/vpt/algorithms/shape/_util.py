"""Shared helpers for ``vpt.algorithms.shape``, factored out of the
identical boolean bounding-box scan duplicated in ``WidthFactor``,
``NormalizedWidthFactor``, ``AreaWidthFactor``, ``RegionalMomentsOfInertia``
and ``RegionalMomentsOfInertia2`` (same precedent as
``vpt.algorithms.segmentation._util.isSingle``, section 4.1)."""

from __future__ import annotations

import numpy as np

from ...Image import Image


def booleanBBox(img: Image) -> tuple[int, int, int, int]:
    """Vectorized equivalent of the identical

    .. code-block:: python

        top = ydim
        bottom = 0
        left = xdim
        right = 0
        for y in range(ydim):
            for x in range(xdim):
                if img.getXYBoolean(x, y):
                    if y < top: top = y
                    if y > bottom: bottom = y
                    if x < left: left = x
                    if x > right: right = x

    duplicated (in full or in a top/bottom-only subset) across five
    ``shape`` algorithms. Returns ``(top, bottom, left, right)``.

    Purely boolean and associative -- running min/max over coordinates
    gated by a boolean test, no summation-order or NaN subtlety involved
    -- so a single ``np.nonzero`` pass over the whole mask is bit-exact
    regardless of scan order. Matches the scalar loop's seed values
    (``top=ydim``, ``bottom=0``, ``left=xdim``, ``right=0``) when the mask
    is entirely ``False``, since coordinates are plain Python ints (no
    floating-point rounding involved either)."""
    xdim = img.getXDim()
    ydim = img.getYDim()
    mask = img.getBooleanArray().reshape(ydim, xdim)
    ys, xs = np.nonzero(mask)
    if ys.size == 0:
        return ydim, 0, xdim, 0
    return int(ys.min()), int(ys.max()), int(xs.min()), int(xs.max())

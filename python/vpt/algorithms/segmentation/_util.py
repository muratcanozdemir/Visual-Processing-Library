"""Shared helpers for ``vpt.algorithms.segmentation``, factored out of
duplicate ``_isSingle`` bodies in ``NumberOfSingletons`` and
``MergeSingletons`` (both implement the identical 8-neighbour
same-label test)."""

from __future__ import annotations

import numpy as np

from ...Image import Image


def isSingle(x: int, y: int, xdim: int, ydim: int, img: Image) -> bool:
    """True if pixel ``(x, y)`` shares its label with none of its (up to 8)
    in-bounds neighbours.

    Kept as a scalar, per-pixel function: ``MergeSingletons`` calls this
    against its own ``self.output`` buffer while mutating that same buffer
    in scan order, so a later call can observe an already-relabelled
    neighbour from earlier in the same pass -- a whole-image vectorization
    would have to read a stale snapshot instead. Not vectorizable there;
    ``isSingleArray`` below is the whole-image form for callers (such as
    ``NumberOfSingletons``) that only ever read an untouched label image.
    """
    p = img.getXYInt(x, y)
    for _x in range(x - 1, x + 2):
        for _y in range(y - 1, y + 2):
            if (_x != x or _y != y) and _x >= 0 and _y >= 0 and _x <= xdim - 1 and _y <= ydim - 1:
                q = img.getXYInt(_x, _y)
                if p == q:
                    return False
    return True


def isSingleArray(img: Image) -> np.ndarray:
    """Vectorized equivalent of calling ``isSingle(x, y, xdim, ydim, img)``
    for every pixel of ``img`` (assumed single-channel), returning a
    ``(ydim, xdim)`` boolean array.

    8-offset shift-and-compare: for each of the 8 neighbour offsets, mark
    every in-bounds centre pixel whose label equals that neighbour's label
    (``hasSame``), then invert. Equality/OR are associative and
    order-independent, so this is safe even though the scalar version's
    inner double loop and early ``return False`` visit offsets in a
    particular order -- the *result* (whether *any* neighbour matches) does
    not depend on which offset is found first, only whether one exists.
    Requires ``img`` to expose ``getIntArray`` (``IntegerImage``,
    ``DoubleImage``), matching the label-image types this package's
    algorithms actually pass in.
    """
    xdim, ydim = img.getXDim(), img.getYDim()
    labels = img.getIntArray().reshape(ydim, xdim)
    hasSame = np.zeros((ydim, xdim), dtype=bool)
    for dy in (-1, 0, 1):
        for dx in (-1, 0, 1):
            if dx == 0 and dy == 0:
                continue
            ys, ye = max(0, -dy), min(ydim, ydim - dy)
            xs, xe = max(0, -dx), min(xdim, xdim - dx)
            if ye <= ys or xe <= xs:
                continue
            center = labels[ys:ye, xs:xe]
            neighbor = labels[ys + dy:ye + dy, xs + dx:xe + dx]
            hasSame[ys:ye, xs:xe] |= center == neighbor
    return ~hasSame

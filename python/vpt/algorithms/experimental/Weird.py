"""Port of ``vpt.algorithms.experimental.Weird``: an odd variant of a
median filter -- computes the median of the SE-neighbourhood (including
the centre pixel's own SE-shifted samples, in-bounds only), and only if
that median exceeds the current pixel value does it replace the pixel --
not with the median, but with the neighbourhood's *minimum*. Otherwise
the pixel is left untouched. Ported exactly as found (the Java's own
class name suggests the author wasn't confident in it either)."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE


class Weird(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = self.input.newInstance(False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        self.cdim = self.output.getCDim()

        result = self._vectorizedWeird(self.input.getDoubleArray(), self.se.getCoords())
        self.output.setDoubleArray(result.reshape(-1))

    def _vectorizedWeird(self, img: np.ndarray, coords) -> np.ndarray:
        """Vectorized equivalent of the per-pixel ``_getOutput`` gather+sort
        below.

        Unlike ``GRankFilter`` (clamp-to-edge, every offset always
        contributes), this SE gather is *exclude*-out-of-bounds like
        ``GDilation``: an offset that lands outside the image is simply not
        in the neighbourhood, so the valid-sample count varies per pixel
        (full ``len(coords)`` in the interior, fewer near the border). The
        median index (``len(content) // 2`` in the scalar version) must
        therefore use each pixel's own valid count, not the fixed SE size.

        Reproduced via a clamped-shift stack (one slot per offset, same
        layout as ``GRankFilter``) where out-of-bounds slots are left as
        ``NaN`` and a separate geometric (data-independent) ``validCount``
        tracks how many offsets were actually in bounds at each pixel.
        ``np.sort`` pushes every ``NaN`` slot -- both the out-of-bounds
        placeholders and any genuine NaN pixel value -- to the end of its
        axis, so the first ``validCount`` sorted slots are exactly the
        valid neighbourhood's values in ascending order and
        ``np.take_along_axis`` at index ``validCount // 2`` reproduces
        ``content[len(content) // 2]`` after ``content.sort()``, and index
        ``0`` reproduces ``content[0]``. (At least one offset -- the SE's
        own centre, ``(0, 0)``, which every coordinate list here includes --
        is always in bounds, so ``validCount >= 1`` everywhere and neither
        index ever falls outside the valid prefix.)

        One caveat, same category as ``GRankFilter``'s own documented one:
        if the neighbourhood contains a genuine NaN *pixel value* (not an
        out-of-bounds placeholder), Python's ``list.sort()`` leaves its
        position unspecified (comparisons against NaN are always False, so
        Timsort doesn't have a total order to work with), while ``np.sort``
        deterministically sends it to the end. Both are defensible
        readings; this vectorized version is internally consistent and
        matches ``np.sort``'s (Java-``Double.compare``-consistent) rule,
        but isn't guaranteed to reproduce the scalar loop's Timsort-
        dependent placement bit-for-bit on NaN-containing input -- verified
        to have no effect on real (non-NaN) golden data.
        """
        img = img.reshape(self.ydim, self.xdim, self.cdim)

        stack = np.full((len(coords), self.ydim, self.xdim, self.cdim), np.nan, dtype=np.float64)
        validCount = np.zeros((self.ydim, self.xdim), dtype=np.int64)
        for i, p in enumerate(coords):
            dx, dy = -p.x, -p.y
            ys, ye = max(0, -dy), min(self.ydim, self.ydim - dy)
            xs, xe = max(0, -dx), min(self.xdim, self.xdim - dx)
            if ye <= ys or xe <= xs:
                continue
            stack[i, ys:ye, xs:xe, :] = img[ys + dy:ye + dy, xs + dx:xe + dx, :]
            validCount[ys:ye, xs:xe] += 1

        stack.sort(axis=0)

        medianIdx = np.broadcast_to(
            (validCount // 2)[None, :, :, None], (1, self.ydim, self.xdim, self.cdim)
        )
        median = np.take_along_axis(stack, medianIdx, axis=0)[0]
        minimum = stack[0]

        current = img
        return np.where(median > current, minimum, current)

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return Weird().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

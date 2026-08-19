"""Port of ``vpt.algorithms.texture.MultiTextonHistogram``: a colour-edge-
orientation/texton co-occurrence histogram (64 quantized-colour "textons"
plus 18 gradient-orientation bins, each counted in 4 directions -
horizontal, vertical, and both diagonals - and averaged).

``ImageX`` (the 6-bit R/G/B-quantized colour index) and ``Texton`` (a
same-colour-pair vote across each 2x2 tile's diagonals/edges) are both
plain nested-list arrays, matching the Java's raw ``int[][]`` -- not
``Image`` objects. ``Texton``'s four independent (non-``elif``) ``if``
checks are preserved as-is: more than one can match a tile, in which case
later checks silently overwrite earlier ones (Java has no ``break``/
``else`` here either), so the final tile value is whichever of the four
same-colour relations was checked last. Border rows/columns of ``Texton``
that a tile's ``i``/``j`` range never reaches (last row/column when
``Width``/``Height`` is odd) stay at the Java default of ``0``, same as
``RGB``'s and ``theta``'s untouched border pixels (loops run ``1..dim-2``
inclusive, leaving index ``0`` and ``dim-1`` at their zero-init default).

Vectorized (no sequential per-pixel dependency anywhere in this file):

- ``RGB``/``theta`` -- computed on shifted whole-array views of an
  explicitly zero-bordered R/G/B stack (``_RGB``), *not* on the raw input
  image, so a near-border interior pixel's Sobel-like taps that reach one
  step past the interior range see the same phantom zeros the scalar
  ``RGB[c][i][j]`` list would (rather than the real, edge-clamped pixel
  value a naive "shift the raw image" vectorization would introduce).
- ``ImageX`` -- 4-way range quantization per channel via ``np.select``;
  the Java version's sequential non-``elif`` ifs only matter if a value
  ever falls outside ``[0, 255]`` (in which case ``VI``/``SI``/``HI``
  Java-side keep whatever the *previous pixel* left them at, since they're
  declared outside the loop) -- not reachable for actual byte-quantized
  RGB input, so not reproduced; ``np.select``'s ``default=0`` covers that
  unreachable case instead.
- ``Texton`` -- all four of a tile's ``if`` bodies copy the identical 2x2
  block from ``ImageX`` to ``Texton`` regardless of *which* same-colour
  relation matched, so the only thing that varies is whether *any* of the
  four matched; reduces to one OR-of-four-comparisons mask over the tile
  grid, broadcast back to pixels and used to select between the ``ImageX``
  block and the untouched (implicitly zero) default.
- ``MatrixH``/``V``/``RD``/``LD`` -- each is a masked-equality count over
  a fixed pixel offset (``D=1``: horizontal, vertical, and both
  diagonals), i.e. the same shift-and-histogram idea as ``Histogram.py``
  (3.2): boolean-mask a shifted-equality array, ``bincount`` the winning
  *other* array's (``Texton``'s or ``Ori``'s) values under that mask.
  Integer counts, so the scatter order ``bincount`` imposes doesn't matter
  (unlike an order-sensitive floating-point sum)."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jacos_arr, jround_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image

CSA = 64
CSB = 18


class MultiTextonHistogram(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 3:
            raise GlobalException("Only color data please")

        width = self.input.getXDim()
        height = self.input.getYDim()

        # All arrays below are held in [y, x] axis order (matching the
        # flat-index formula ``c + cdim * (x + xdim * y)`` -- a C-order
        # reshape to (height, width, cdim) lands pixel (x, y) at [y, x]),
        # the opposite of the Java/scalar port's [i][j] == [x][y] nesting.
        full = self.input.getByteArray().reshape(height, width, self.input.getCDim())

        # Explicitly zero-bordered per-channel stack, matching the Java
        # ``RGB[c][i][j]`` list (only ``1..dim-2`` ever written).
        RGB = np.zeros((3, height, width), dtype=np.float64)
        if width > 2 and height > 2:
            RGB[:, 1:-1, 1:-1] = np.moveaxis(full[1:-1, 1:-1, :3], -1, 0)

        theta = np.zeros((height, width), dtype=np.float64)
        if width > 2 and height > 2:
            top = RGB[:, 2:, 0:-2] + 2.0 * RGB[:, 2:, 1:-1] + RGB[:, 2:, 2:]
            bottom = RGB[:, 0:-2, 0:-2] + 2.0 * RGB[:, 0:-2, 1:-1] + RGB[:, 0:-2, 2:]
            h = top - bottom  # rh, gh, bh stacked on axis 0

            right = RGB[:, 0:-2, 2:] + 2.0 * RGB[:, 1:-1, 2:] + RGB[:, 2:, 2:]
            left = RGB[:, 0:-2, 0:-2] + 2.0 * RGB[:, 1:-1, 0:-2] + RGB[:, 2:, 0:-2]
            v = right - left  # rv, gv, bv stacked on axis 0

            gxx = np.sqrt(np.sum(h * h, axis=0))
            gyy = np.sqrt(np.sum(v * v, axis=0))
            gxy = np.sum(h * v, axis=0)

            with np.errstate(invalid="ignore"):
                theta[1:-1, 1:-1] = jacos_arr(gxy / (gxx * gyy + 0.0001)) * 180.0 / math.pi

        # ImageX: per-channel 4-way range quantization over every pixel
        # (not just the interior, and not border-zeroed -- raw byte
        # values). Ranges [0,64]/[65,128]/[129,192]/[193,255] exhaust the
        # full byte range with no gaps or overlaps.
        def quantize(channel: np.ndarray) -> np.ndarray:
            return np.select(
                [
                    (channel >= 0) & (channel <= 64),
                    (channel >= 65) & (channel <= 128),
                    (channel >= 129) & (channel <= 192),
                    (channel >= 193) & (channel <= 255),
                ],
                [0, 1, 2, 3],
                default=0,
            )

        VI = quantize(full[:, :, 0])
        SI = quantize(full[:, :, 1])
        HI = quantize(full[:, :, 2])
        ImageX = 16 * VI + 4 * SI + HI

        Ori = jround_arr(theta * CSB / 180.0)
        Ori = np.where(Ori >= CSB - 1, CSB - 1, Ori).astype(np.int64)

        # Texton: every one of a tile's four independent ``if`` bodies
        # performs the identical copy, so only "did any of the four
        # same-colour relations match" matters (see module docstring).
        nTilesX = width // 2
        nTilesY = height // 2
        Texton = np.zeros((height, width), dtype=np.int64)
        if nTilesX > 0 and nTilesY > 0:
            TL = ImageX[0 : 2 * nTilesY : 2, 0 : 2 * nTilesX : 2]
            TR = ImageX[0 : 2 * nTilesY : 2, 1 : 2 * nTilesX : 2]
            BL = ImageX[1 : 2 * nTilesY : 2, 0 : 2 * nTilesX : 2]
            BR = ImageX[1 : 2 * nTilesY : 2, 1 : 2 * nTilesX : 2]

            anyMatch = (TL == BR) | (BL == TR) | (TL == TR) | (TL == BL)
            maskBlock = np.repeat(np.repeat(anyMatch, 2, axis=0), 2, axis=1)
            region = ImageX[0 : 2 * nTilesY, 0 : 2 * nTilesX]
            Texton[0 : 2 * nTilesY, 0 : 2 * nTilesX] = np.where(maskBlock, region, 0)

        n_bins = CSA + CSB
        D = 1

        def scatter(orimatch_idx: np.ndarray, textonmatch_idx: np.ndarray) -> np.ndarray:
            counts = np.bincount(orimatch_idx.ravel(), minlength=n_bins)
            counts = counts + np.bincount(CSA + textonmatch_idx.ravel(), minlength=n_bins)
            return counts[:n_bins]

        # Horizontal (y + D)
        condH1 = Ori[D:, :] == Ori[:-D, :]
        condH2 = Texton[D:, :] == Texton[:-D, :]
        MatrixH = scatter(Texton[:-D, :][condH1], Ori[:-D, :][condH2])

        # Vertical (x + D)
        condV1 = Ori[:, D:] == Ori[:, :-D]
        condV2 = Texton[:, D:] == Texton[:, :-D]
        MatrixV = scatter(Texton[:, :-D][condV1], Ori[:, :-D][condV2])

        # Right diagonal (x + D, y + D)
        condRD1 = Ori[D:, D:] == Ori[:-D, :-D]
        condRD2 = Texton[D:, D:] == Texton[:-D, :-D]
        MatrixRD = scatter(Texton[:-D, :-D][condRD1], Ori[:-D, :-D][condRD2])

        # Left diagonal (x - D, y + D)
        A_ori = Ori[D:, 0 : width - D]
        B_ori = Ori[0 : height - D, D:width]
        A_tex = Texton[D:, 0 : width - D]
        B_tex = Texton[0 : height - D, D:width]
        condLD1 = A_ori == B_ori
        condLD2 = A_tex == B_tex
        MatrixLD = scatter(B_tex[condLD1], B_ori[condLD2])

        self.output = [0.0] * n_bins
        for i in range(n_bins):
            self.output[i] = (
                int(MatrixH[i]) + int(MatrixV[i]) + int(MatrixRD[i]) + int(MatrixLD[i])
            ) / 4.0

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return MultiTextonHistogram().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

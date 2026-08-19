"""Port of ``vpt.algorithms.hough.HoughTransformLine``: a classic
(rho, theta) Hough line-vote accumulator, run per channel over the
morphological gradient (``mm.gray.GExternGradient``) of the input.
``output`` is ``180 x 2*diagonal`` (one column per whole degree of
theta, one row per rho offset by the image diagonal so negative rho is
representable), each cell saturating at 255 votes.

The Java's own ``inputFields = "input,"`` has a trailing comma. Java's
``String.split(",")`` (no limit) drops the resulting trailing empty
token, so this parses as the single field `input` in the oracle; this
port's ``Algorithm._getFields`` previously used plain ``str.split``,
which doesn't drop it, and would have made *this file's* `invoke()`
always fail with a bogus "Requires 2 parameters!" -- a genuine Python-
port infrastructure bug, not a Java behaviour, now fixed via the new
``_javacompat.jsplit`` (see `Algorithm.py`).

The theta loop accumulates a `double` step (``phi += 1 degree in
radians``) 180 times rather than reconstructing each angle from the loop
index -- ported as the same repeated-addition accumulation, since
Python's `float` is IEEE-754 binary64 just like Java's `double` and the
two languages round identically, so this isn't a source of drift the
way it would be across, say, float32 vs float64."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ...util.Tools import Tools
from ..mm.gray.GExternGradient import GExternGradient


class HoughTransformLine(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input,"
        self.outputFields = "output"

    def execute(self) -> None:
        diag = self.input.getDiagonal()
        self.output = ByteImage(180, 2 * diag, self.input.getCDim())
        self.output.fill(0)

        edges = GExternGradient.invoke(self.input, FlatSE.square(3))

        # The 180 candidate thetas don't depend on the pixel at all, so
        # precompute them once via the identical repeated-addition
        # accumulation as the original loop (`phi += 1 degree in radians`,
        # 180 times) -- IEEE-754 binary64 addition is the same in Python as
        # Java, so this reproduces the exact same 180 values, not just
        # values close to them. `r` is then an outer product over
        # edge-pixel coords x thetas. Every vote is a uniform +1, so the
        # final `min(_, 255)` saturating clamp is order-independent -- a
        # plain vote-count histogram (np.bincount), clipped once at the
        # end, is bit-exact to the scalar per-vote saturating increment.
        phis = []
        phi = -1.570796326794897
        while phi < 1.570796326794897:
            phi2 = phi
            if phi2 < 0.0:
                phi2 += 3.141592653589793
            phis.append(phi2)
            phi += 0.0174532925199433
        phi2_arr = np.array(phis, dtype=np.float64)
        cos_arr = np.cos(phi2_arr)
        sin_arr = np.sin(phi2_arr)

        out_xdim = self.output.getXDim()
        out_ydim = self.output.getYDim()
        deg_arr = Tools.radian2degree(phi2_arr)
        col_arr = np.minimum(deg_arr, out_xdim - 1)
        col_idx = jint_arr(col_arr)

        xdim = edges.getXDim()
        ydim = edges.getYDim()
        cdim = edges.getCDim()
        pixels = edges.getByteArray().reshape(ydim, xdim, cdim)

        X, Y = np.meshgrid(
            np.arange(xdim, dtype=np.float64), np.arange(ydim, dtype=np.float64)
        )

        full = np.zeros((out_ydim, out_xdim, cdim), dtype=np.int64)
        for c in range(cdim):
            mask = pixels[:, :, c] != 0
            if not mask.any():
                continue
            xs = X[mask]
            ys = Y[mask]

            r = xs[:, None] * cos_arr[None, :] + ys[:, None] * sin_arr[None, :]
            row_idx = jint_arr(r) + diag
            col_idx_full = np.broadcast_to(col_idx, r.shape)

            flat = row_idx.ravel() * out_xdim + col_idx_full.ravel()
            counts = np.bincount(flat, minlength=out_xdim * out_ydim)
            full[:, :, c] = counts.reshape(out_ydim, out_xdim)

        full = np.minimum(full, 255)
        self.output.setByteArray(full.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return HoughTransformLine().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

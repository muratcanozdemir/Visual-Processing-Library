"""Port of ``vpt.algorithms.hough.HoughTransformLine2``: the same
(rho, theta) accumulator as ``HoughTransformLine``, but sourcing edge
strength and gradient direction from two separate ``linear.Sobel`` calls
(magnitude, then direction) instead of a morphological gradient plus
per-pixel angle sweep -- each edge pixel votes for exactly one (rho,
theta) cell (its own gradient direction) rather than sweeping all 180
possible thetas.

Drops the Java's ``Display2D.invoke(edges, "", false)`` call between
computing `edges` and using it -- the identical call signature already
confirmed to hang the JVM forever in `linear.Canny` (see
`python/README.md`). Confirmed hanging here too, not just assumed from
that precedent: a standalone harness called the unmodified Java
`invoke()` for both this file and `HoughTransformCircle` with a 30s
timeout -- both printed their full output (`invoke()` genuinely
returns), but `main()` itself never exited and the JVM had to be killed.
Not exercised by the oracle driver, smoke-tested only in Python.

Same trailing-comma ``inputFields = "input,"`` as `HoughTransformLine`
-- see that file's docstring and the `Algorithm._getFields` fix this
required."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..linear.Sobel import Sobel


class HoughTransformLine2(Algorithm):
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

        edges = Sobel.invoke(self.input, 2, True)
        # Display2D.invoke(edges, "", False) dropped -- see module docstring.

        theta = Sobel.invoke(self.input, 3, False)

        # Each edge pixel votes for exactly one (rho, theta) cell (its own
        # gradient direction), so this is a plain elementwise computation
        # over edge pixels rather than an outer product over thetas. Every
        # vote is a uniform +1, so the final `min(_, 255)` saturating clamp
        # is order-independent -- a plain vote-count histogram
        # (np.bincount), clipped once at the end, is bit-exact to the
        # scalar per-vote saturating increment.
        out_xdim = self.output.getXDim()
        out_ydim = self.output.getYDim()

        xdim = edges.getXDim()
        ydim = edges.getYDim()
        cdim = edges.getCDim()
        edge_pixels = edges.getByteArray().reshape(ydim, xdim, cdim)
        theta_pixels = theta.getDoubleArray().reshape(ydim, xdim, cdim)

        X, Y = np.meshgrid(
            np.arange(xdim, dtype=np.float64), np.arange(ydim, dtype=np.float64)
        )

        full = np.zeros((out_ydim, out_xdim, cdim), dtype=np.int64)
        for c in range(cdim):
            mask = edge_pixels[:, :, c] != 0
            if not mask.any():
                continue
            xs = X[mask]
            ys = Y[mask]
            phi = theta_pixels[:, :, c][mask]

            phi2 = np.where(phi < 0.0, phi + 3.141592653589793, phi)
            r = xs * np.cos(phi2) + ys * np.sin(phi2)
            row_idx = jint_arr(r) + diag

            deg = Tools.radian2degree(phi2)
            col = np.minimum(deg, out_xdim - 1)
            col_idx = jint_arr(col)

            flat = row_idx * out_xdim + col_idx
            counts = np.bincount(flat, minlength=out_xdim * out_ydim)
            full[:, :, c] = counts.reshape(out_ydim, out_xdim)

        full = np.minimum(full, 255)
        self.output.setByteArray(full.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return HoughTransformLine2().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

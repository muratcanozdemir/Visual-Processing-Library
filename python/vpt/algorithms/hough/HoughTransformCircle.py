"""Port of ``vpt.algorithms.hough.HoughTransformCircle``: a Hough circle
accumulator using the gradient-direction shortcut (each edge pixel votes
along the line through it in its own gradient direction, for every
candidate radius up to the image diagonal, rather than a full 3D
(a, b, r) parameter search).

**Preserved bug, bytecode-confirmed** (`javap`: both bound checks --
`a < output.getXDim()` and `b < output.getXDim()` -- load `output` and
call `getXDim`, never `getYDim()`): the `b` (row/y) bounds check should
almost certainly use `output.getYDim()`, but uses `getXDim()` again
instead. Inert for a square input (`output` copies `input`'s dims, so
`xdim == ydim`), but for a non-square image this either silently drops
valid votes (when `ydim > xdim`, rejecting in-range `b` values between
`xdim` and `ydim`) or lets an out-of-bounds `b` slip past the check
(when `ydim < xdim`, crashing on the write) -- reproduced verbatim.

Drops the Java's ``Display2D.invoke(edges, "", false)`` call -- confirmed
hanging via the same standalone-harness run described in
``HoughTransformLine2.py``'s docstring (both files' unmodified Java
`invoke()` were exercised together there).

Same trailing-comma ``inputFields = "input,"`` as the other two `hough`
files -- see `HoughTransformLine.py`'s docstring and the
`Algorithm._getFields` fix this required."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image
from ..linear.Sobel import Sobel


class HoughTransformCircle(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input,"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = ByteImage(self.input, False)
        self.output.fill(0)

        edges = Sobel.invoke(self.input, 2, True)
        # Display2D.invoke(edges, "", False) dropped -- see module docstring.

        theta = Sobel.invoke(self.input, 3, False)

        out_xdim = self.output.getXDim()
        out_ydim = self.output.getYDim()
        diag = self.input.getDiagonal()

        xdim = edges.getXDim()
        ydim = edges.getYDim()
        cdim = edges.getCDim()
        edge_pixels = edges.getByteArray().reshape(ydim, xdim, cdim)
        theta_pixels = theta.getDoubleArray().reshape(ydim, xdim, cdim)

        X, Y = np.meshgrid(
            np.arange(xdim, dtype=np.float64), np.arange(ydim, dtype=np.float64)
        )

        # **Preserved bug, verbatim**: both bound checks compare against
        # output.getXDim(), never getYDim() for `b` -- see module
        # docstring. Since every vote is a uniform +1 with a final
        # min(_, 255) saturating clamp, per-r/per-pixel accumulation order
        # doesn't matter (same argument as HoughTransformLine), so votes
        # for each candidate radius are scattered straight into the
        # real-shaped (out_ydim, out_xdim) accumulator via np.add.at. That
        # keeps the crash behaviour authentic too: for xdim > ydim inputs
        # the buggy bound lets an in-range `a` but out-of-range `b` slip
        # through, and np.add.at raises the same IndexError a real
        # out-of-bounds array write would.
        full = np.zeros((out_ydim, out_xdim, cdim), dtype=np.int64)
        for c in range(cdim):
            mask = edge_pixels[:, :, c] != 0
            if not mask.any():
                continue
            xs = X[mask]
            ys = Y[mask]
            phi = theta_pixels[:, :, c][mask]
            cos_phi = np.cos(phi)
            sin_phi = np.sin(phi)

            sub = full[:, :, c]
            for r in range(1, diag):
                a = jround_arr(xs - r * cos_phi)
                b = jround_arr(ys - r * sin_phi)
                valid = (a < out_xdim) & (b < out_xdim) & (a >= 0) & (b >= 0)
                if not np.any(valid):
                    continue
                np.add.at(sub, (b[valid], a[valid]), 1)

        full = np.minimum(full, 255)
        self.output.setByteArray(full.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return HoughTransformCircle().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

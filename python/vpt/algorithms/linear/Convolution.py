"""Port of ``vpt.algorithms.linear.Convolution``.

Correlation-style convolution against a ``NonFlatSE`` kernel, with the SE
reflected first so it behaves as true convolution. Out-of-bounds samples wrap
around the image (``|coord mod dim|``) rather than being padded or clamped.
A near-zero kernel coefficient sum (``Tools.cmpr``) leaves the raw
accumulated sum unnormalised instead of dividing by ~0.

The Java computes ``se.decompose()`` and immediately discards the result
(``subKernels = null``, unconditionally, before the null check) -- the
separable-kernel fast path is dead code in the shipped library, confirmed
in ``python/README.md`` ("Known divergence from the Java"). Only the general
reflected-coords path below is reachable, so that's the only path ported.
"""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import NonFlatSE
from ...util.Tools import Tools


class Convolution(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: NonFlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = DoubleImage(self.input, False)

        self.xdim = self.output.getXDim()
        self.ydim = self.output.getYDim()
        self.cdim = self.output.getCDim()

        self.se = self.se.reflection()
        coords = self.se.getCoords()

        # The kernel value (and hence coeffSum) depends only on the offset
        # p, never on the pixel being convolved -- the original recomputed
        # it identically on every pixel. Hoisting it out is an exact replay
        # of the same floating-point accumulation, done once.
        coeffSum = 0.0
        kernelValues = []
        for p in coords:
            kernel = self.se.getXYDouble(p.x + self.se.center.x, p.y + self.se.center.y)
            coeffSum += kernel
            kernelValues.append(kernel)

        # X[y, x] == x, Y[y, x] == y: pixel-position grids matching the
        # (ydim, xdim, cdim) layout used throughout.
        X, Y = np.meshgrid(np.arange(self.xdim), np.arange(self.ydim))
        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)

        output = np.zeros((self.ydim, self.xdim, self.cdim), dtype=np.float64)
        for p, kernel in zip(coords, kernelValues):
            # abs(imod(a, b)) for b > 0 (b is always xdim/ydim here): Java
            # truncate-toward-zero remainder then abs, NOT Python's
            # floor-mod, which disagrees for negative a (e.g. a=-1, b=5:
            # imod gives -1, abs 1; Python's -1 % 5 gives 4). The original
            # only applied this when out of [0, dim), but for an in-bounds
            # coordinate the formula is a no-op (imod of an in-range value
            # returns it unchanged), so applying it unconditionally to every
            # pixel is equivalent and avoids a branch per pixel per offset.
            ax, ay = X - p.x, Y - p.y
            qx = np.abs(ax) // self.xdim
            _x = np.abs(ax - np.where(ax < 0, -qx, qx) * self.xdim)
            qy = np.abs(ay) // self.ydim
            _y = np.abs(ay - np.where(ay < 0, -qy, qy) * self.ydim)

            output += img[_y, _x, :] * kernel

        if Tools.cmpr(coeffSum, 0.0) != 0:
            output = output / coeffSum

        self.output.setDoubleArray(output.reshape(-1))

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, se: NonFlatSE) -> Image | None:
        try:
            return Convolution().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

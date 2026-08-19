"""Port of ``vpt.algorithms.geometric.Resample``: resize by independent
X/Y ratios, via one of nearest-neighbour, bilinear, or bicubic (Catmull-Rom
style, per-axis two-pass) interpolation, clamping the bicubic result back
into ``[0, 1]`` (the only method whose overshoot can leave that range)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint, jint_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Resample(Algorithm):
    NEAREST_NEIGHBOR = 0
    BILINEAR = 1
    BICUBIC = 2

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.ratioX: float | None = None
        self.ratioY: float | None = None
        self.method: int | None = None
        self.inputFields = "input,ratioX,ratioY,method"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.ratioX <= 0.0 or self.ratioY <= 0.0:
            raise GlobalException("Invalid ratio value")
        if self.method != 1 and self.method != 0 and self.method != 2:
            raise GlobalException("Invalid interpolation method")

        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        newXdim = jint(xdim * self.ratioX)
        newYdim = jint(ydim * self.ratioY)

        self.output = self.input.newInstance(newXdim, newYdim, cdim)

        # Same shape as Rotation: the output-pixel grid is built once via
        # broadcasting (matching the output's own [y][x][c] storage order),
        # the source-sample coordinate is one elementwise formula per
        # method, and every method's neighbourhood gather is a fixed,
        # non-cross-pixel-dependent tap pattern -- nearest is a 1-tap
        # gather, bilinear a 4-tap blend, bicubic a 4x4-tap two-pass
        # (x-direction cubic blend per row, then a y-direction cubic blend
        # across the four row results) Catmull-Rom-style blend. None of the
        # three methods bounds-check their sample coordinate (the scalar
        # loop doesn't either -- ``newXdim``/``newYdim`` are derived from
        # ``ratioX``/``ratioY`` precisely so every sample stays in range),
        # so the gather is unconditional, same as the scalar accessor calls
        # it replaces. The final ``[0, 1]`` clamp is applied unconditionally
        # to every method's result, exactly as the scalar loop's
        # unconditional ``max(min(p, 1.0), 0.0)`` does (a no-op for
        # already-in-range nearest/bilinear results).
        X = np.arange(newXdim, dtype=np.float64)[None, :]
        Y = np.arange(newYdim, dtype=np.float64)[:, None]
        X = np.broadcast_to(X, (newYdim, newXdim))
        Y = np.broadcast_to(Y, (newYdim, newXdim))

        srcX = X / self.ratioX
        srcY = Y / self.ratioY

        img = self.input.getDoubleArray().reshape(ydim, xdim, cdim)

        if self.method == 0:
            sx = jint_arr(srcX)
            sy = jint_arr(srcY)
            result = img[sy, sx, :]

        elif self.method == 1:
            left = jint_arr(np.floor(srcX))
            k = jint_arr(np.floor(srcY))

            a = srcX - left
            b = srcY - k

            cx0 = left
            cx1 = np.minimum(left + 1, xdim - 1)
            cy0 = k
            cy1 = np.minimum(k + 1, ydim - 1)

            p1 = img[cy0, cx0, :]
            p2 = img[cy0, cx1, :]
            p3 = img[cy1, cx0, :]
            p4 = img[cy1, cx1, :]

            aB = a[:, :, None]
            bB = b[:, :, None]
            result = (
                (1.0 - aB) * (1.0 - bB) * p1
                + aB * (1.0 - bB) * p2
                + (1.0 - aB) * bB * p3
                + aB * bB * p4
            )

        else:
            left = jint_arr(np.floor(srcX))
            k = jint_arr(np.floor(srcY))
            dx = srcX - left
            dy = srcY - k
            dx2 = dx * dx
            dx3 = dx2 * dx
            dy2 = dy * dy
            dy3 = dy2 * dy

            cols = [
                np.maximum(left - 1, 0),
                left,
                np.minimum(left + 1, xdim - 1),
                np.minimum(left + 2, xdim - 1),
            ]
            rows = [
                np.maximum(k - 1, 0),
                k,
                np.minimum(k + 1, ydim - 1),
                np.minimum(k + 2, ydim - 1),
            ]

            dxB = dx[:, :, None]
            dx2B = dx2[:, :, None]
            dx3B = dx3[:, :, None]

            cc = [None, None, None, None]
            for j in range(4):
                n0 = img[rows[j], cols[0], :]
                n1 = img[rows[j], cols[1], :]
                n2 = img[rows[j], cols[2], :]
                n3 = img[rows[j], cols[3], :]
                d0 = n0 - n1
                d2 = n2 - n1
                d3 = n3 - n1
                a0 = n1
                a1 = -0.3333333333333333 * d0 + d2 - 0.1666666666666667 * d3
                a2 = 0.5 * d0 + 0.5 * d2
                a3 = -0.1666666666666667 * d0 - 0.5 * d2 + 0.1666666666666667 * d3
                cc[j] = a0 + a1 * dxB + a2 * dx2B + a3 * dx3B

            dyB = dy[:, :, None]
            dy2B = dy2[:, :, None]
            dy3B = dy3[:, :, None]
            d0 = cc[0] - cc[1]
            d2 = cc[2] - cc[1]
            d3 = cc[3] - cc[1]
            a0 = cc[1]
            a1 = -0.3333333333333333 * d0 + d2 - 0.1666666666666667 * d3
            a2 = 0.5 * d0 + 0.5 * d2
            a3 = -0.1666666666666667 * d0 - 0.5 * d2 + 0.1666666666666667 * d3
            result = a0 + a1 * dyB + a2 * dy2B + a3 * dy3B

        result = np.clip(result, 0.0, 1.0)
        self.output.setDoubleArray(result.reshape(-1))

    @staticmethod
    def invoke(image: Image, ratioX: float, ratioY: float, method: int) -> Image | None:
        try:
            return Resample().preprocess(image, ratioX, ratioY, method)
        except GlobalException:
            traceback.print_exc()
        return None

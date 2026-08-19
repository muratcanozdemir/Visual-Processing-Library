"""Port of ``vpt.algorithms.geometric.Rotation``: rotate an image by
``theta`` degrees about its centre, into a freshly-sized canvas that fits
the whole rotated bounding box, via inverse-mapping with either
nearest-neighbour or bilinear interpolation."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import idiv, jint, jint_arr, jround_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Rotation(Algorithm):
    NEAREST_NEIGHBOR = 0
    BILINEAR = 1

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.theta: float | None = None
        self.interpolation: int | None = None
        self.cdim = 0
        self.xdim = 0
        self.ydim = 0
        self.inputFields = "input,theta,interpolation"
        self.outputFields = "output"

    def execute(self) -> None:
        self.cdim = self.input.getCDim()
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        if self.interpolation != 1 and self.interpolation != 0:
            raise GlobalException("Unsupported interpolation type")

        self.theta = self.theta * math.pi / 180.0
        mtheta = -1.0 * self.theta

        cos = math.cos(self.theta)
        sin = math.sin(self.theta)

        newXdim = jint(math.ceil(self.xdim * abs(math.cos(mtheta)) + self.ydim * abs(math.sin(mtheta))))
        newYdim = jint(math.ceil(self.xdim * abs(math.sin(mtheta)) + self.ydim * abs(math.cos(mtheta))))

        xpadding = idiv(newXdim - self.xdim, 2)
        ypadding = idiv(newYdim - self.ydim, 2)

        xMiddle = idiv(self.xdim, 2)
        yMiddle = idiv(self.ydim, 2)

        self.output = self.input.newInstance(newXdim, newYdim, self.cdim)

        # Inverse-mapping affine transform, vectorized: the output-pixel
        # grid is built once via broadcasting (matching the output's own
        # [y][x][c] storage order), the source coordinate ``(_x, _y)`` is
        # one elementwise affine formula (no cross-pixel dependency, so
        # trivially bit-exact under broadcasting), and the source pixel is
        # gathered via fancy indexing. Both interpolation modes bound-check
        # on the SAME quantity the scalar loop does -- nearest checks the
        # rounded integer sample coordinate itself; bilinear checks only the
        # floored top-left corner, not ``_x``/``_y`` directly, and only
        # clamps the "+1" corner, matching the asymmetric bounds check
        # below verbatim. An out-of-bounds output pixel must stay at 0.0
        # (the fill(0.0) value) rather than receive whatever the clamped
        # gather would otherwise produce, so the mask is applied via
        # ``np.where``, not baked into the gather itself.
        xIdx = np.arange(newXdim, dtype=np.float64)
        yIdx = np.arange(newYdim, dtype=np.float64)
        X = xIdx[None, :]
        Y = yIdx[:, None]

        _x = (X - xMiddle - xpadding) * cos + (Y - yMiddle - ypadding) * sin + xMiddle
        _y = -1 * (X - xMiddle - xpadding) * sin + (Y - yMiddle - ypadding) * cos + yMiddle

        img = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.cdim)

        if self.interpolation == 0:
            px = jint_arr(jround_arr(_x))
            py = jint_arr(jround_arr(_y))
            valid = (px >= 0) & (px < self.xdim) & (py >= 0) & (py < self.ydim)

            pxSafe = np.clip(px, 0, self.xdim - 1)
            pySafe = np.clip(py, 0, self.ydim - 1)
            gathered = img[pySafe, pxSafe, :]
            result = np.where(valid[:, :, None], gathered, 0.0)

        else:
            left = jint_arr(np.floor(_x))
            k = jint_arr(np.floor(_y))

            a = _x - left
            b = _y - k

            valid = (left >= 0) & (k >= 0) & (left < self.xdim) & (k < self.ydim)

            cx0 = np.clip(left, 0, self.xdim - 1)
            cx1 = np.clip(left + 1, 0, self.xdim - 1)
            cy0 = np.clip(k, 0, self.ydim - 1)
            cy1 = np.clip(k + 1, 0, self.ydim - 1)

            p1 = img[cy0, cx0, :]
            p2 = img[cy0, cx1, :]
            p3 = img[cy1, cx0, :]
            p4 = img[cy1, cx1, :]

            aB = a[:, :, None]
            bB = b[:, :, None]
            bilinear = (
                (1.0 - aB) * (1.0 - bB) * p1
                + aB * (1.0 - bB) * p2
                + (1.0 - aB) * bB * p3
                + aB * bB * p4
            )
            result = np.where(valid[:, :, None], bilinear, 0.0)

        self.output.setDoubleArray(result.reshape(-1))

    @staticmethod
    def invoke(image: Image, theta: float, interpolation: int) -> Image | None:
        try:
            return Rotation().preprocess(image, theta, interpolation)
        except GlobalException:
            traceback.print_exc()
        return None

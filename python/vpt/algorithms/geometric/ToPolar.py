"""Port of ``vpt.algorithms.geometric.ToPolar``: resample an image into
polar coordinates (360 columns of angle x half-diagonal rows of radius),
centred on the image, clamping any out-of-bounds sample to the border."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import idiv, jint_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class ToPolar(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        diagonal = idiv(self.input.getDiagonal(), 2)

        self.output = self.input.newInstance(360, diagonal, cdim)

        # Pure per-(theta,rho) gather: the sample coordinate depends only on
        # (theta, rho), not on the channel or the pixel data, so it's built
        # once as a (diagonal, 360) grid -- matching the output's own
        # [rho(y)][theta(x)][c] storage order -- then reused for every
        # channel via a single fancy-indexed gather. ``Tools.degree2radian``
        # is reproduced inline (plain arithmetic, no Java-vs-Python
        # divergence to guard against).
        thetaIdx = np.arange(360, dtype=np.float64)
        rhoIdx = np.arange(diagonal, dtype=np.float64)

        rad = thetaIdx * 3.141592653589793 / 180.0
        cosT = np.cos(rad)
        sinT = np.sin(rad)

        x = rhoIdx[:, None] * cosT[None, :] + idiv(xdim, 2)
        y = rhoIdx[:, None] * sinT[None, :] + idiv(ydim, 2)

        x = np.minimum(x, xdim - 1)
        y = np.minimum(y, ydim - 1)

        x = np.maximum(x, 0.0)
        y = np.maximum(y, 0.0)

        xi = jint_arr(x)
        yi = jint_arr(y)

        img = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        gathered = img[yi, xi, :]
        self.output.setDoubleArray(gathered.reshape(-1))

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return ToPolar().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.geometric.ImageCentroid``: the intensity-weighted
centroid of a mono-channel image, ``(sum(x*p)/volume, sum(y*p)/volume)``."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import Point, jint
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class ImageCentroid(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Point | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel for now...")

        # Weighted-sum reduction: elementwise ``x*p``/``y*p`` grids, summed
        # via ``np.cumsum(...)[-1]`` (genuinely sequential, unlike
        # ``np.sum``'s pairwise reduction) in x-outer/y-inner order --
        # matching the nested loop's traversal, not the flat storage's
        # native y-outer/x-inner order -- to reproduce the exact
        # left-to-right double summation. Same trick as
        # ``ColorMoments._moment``.
        img = self.input.getDoubleArray().reshape(ydim, xdim).T  # [x][y]
        xIdx = np.arange(xdim, dtype=np.float64)[:, None]
        yIdx = np.arange(ydim, dtype=np.float64)[None, :]

        xp = (xIdx * img).reshape(-1)
        yp = (yIdx * img).reshape(-1)

        x_c = float(np.cumsum(xp)[-1]) if xp.size else 0.0
        y_c = float(np.cumsum(yp)[-1]) if yp.size else 0.0

        volume = Tools.volume(self.input, 0)

        self.output = Point(jint(x_c / volume), jint(y_c / volume))

    @staticmethod
    def invoke(image: Image) -> Point | None:
        try:
            return ImageCentroid().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

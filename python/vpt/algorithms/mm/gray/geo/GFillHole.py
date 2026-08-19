"""Port of ``vpt.algorithms.mm.gray.geo.GFillHole``.

Fills grayscale "holes": suppresses any regional minimum not touching the
image border (via ``GRegionalMinima``), builds a marker that is 255 minus
the border pixel value on the border and 0 everywhere else, reconstructs by
dilation of that marker under the inverted image with a 5x5 box SE, then
inverts back.
"""

from __future__ import annotations

import traceback

import numpy as np

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.se import FlatSE
from ....point.Inversion import Inversion
from .GReconstructionByDilation import GReconstructionByDilation
from .GRegionalMinima import GRegionalMinima


class GFillHole(Algorithm):
    def __init__(self):
        super().__init__()
        self.inputImage: Image | None = None
        self.output: Image | None = None
        self.inputFields = "inputImage"
        self.outputFields = "output"

    def execute(self) -> None:
        marker = self.inputImage.newInstance(False)

        xdim = marker.getXDim()
        ydim = marker.getYDim()
        cdim = marker.getCDim()

        rmin = GRegionalMinima.invoke(self.inputImage)
        # Vectorized equivalent of the per-pixel zero-out below: a pure
        # elementwise mask assignment with no cross-pixel dependency.
        rminArr = rmin.getByteArray()
        imgArr = self.inputImage.getByteArray()
        imgArr[rminArr > 0] = 0
        self.inputImage.setByteArray(imgArr)

        # Vectorized equivalent of the per-pixel border-marker build below:
        # both the border test and the ``255 - p``/``p`` choice are pure
        # elementwise formulas over (x, y), so build the border mask once
        # and select with ``np.where`` in place of the scalar if/else.
        img = self.inputImage.getByteArray().reshape(ydim, xdim, cdim)
        borderMask = np.zeros((ydim, xdim), dtype=bool)
        borderMask[0, :] = True
        borderMask[ydim - 1, :] = True
        borderMask[:, 0] = True
        borderMask[:, xdim - 1] = True
        borderMask = borderMask[:, :, None]
        markerArr = np.where(borderMask, np.where(img > 0, 255 - img, img), 0)
        marker.setByteArray(markerArr.reshape(-1))

        self.output = GReconstructionByDilation.invoke(
            marker, FlatSE.square(5), Inversion.invoke(self.inputImage)
        )
        self.output = Inversion.invoke(self.output)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return GFillHole().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

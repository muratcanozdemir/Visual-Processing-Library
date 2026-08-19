"""Port of ``vpt.algorithms.segmentation.SuperposeBorders``: paint a fixed
colour wherever a label image is ``0`` (the watershed-line convention;
everything else is left untouched from ``baseImage``)."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image

WSHED = 0


class SuperposeBorders(Algorithm):
    def __init__(self):
        super().__init__()
        self.labelImage: Image | None = None
        self.baseImage: Image | None = None
        self.outputImage: Image | None = None
        self.color: list[int] | None = None
        self.inputFields = "labelImage,baseImage,color"
        self.outputFields = "outputImage"

    def execute(self) -> None:
        self.outputImage = self.baseImage.newInstance(True)

        xDim = self.outputImage.getXDim()
        yDim = self.outputImage.getYDim()
        cDim = self.outputImage.getCDim()
        if cDim > 3:
            raise GlobalException("Only color or gray images can be processed.")
        if cDim != len(self.color):
            raise GlobalException(
                "Color dimensions do not correspond to the original image's channel number."
            )
        # labelImage is single-channel (a label map); getIntArray reshaped to
        # (yDim, xDim) matches getXYInt elementwise.
        labels = self.labelImage.getIntArray().reshape(yDim, xDim)
        mask = labels == WSHED

        # The scalar loop only ever touches masked pixels via setXYCByte,
        # leaving every other pixel's stored value completely untouched --
        # round-tripping the *whole* image through get/setByteArray would
        # lose precision on those (e.g. a DoubleImage's byte encoding is
        # lossy). Instead, encode the fill colour once into a same-type,
        # same-shape scratch image via setByteArray (a pure, position-
        # independent per-channel encoding), then copy only the masked
        # pixels from its storage view into outputImage's storage view --
        # both are direct reshapes of the underlying flat array (no copy),
        # so this mutates exactly the masked entries in place.
        colorFill = self.outputImage.newInstance(False)
        colorFill.setByteArray(np.tile(np.asarray(self.color, dtype=np.int64), xDim * yDim))
        outView = self.outputImage.view3d()
        outView[mask] = colorFill.view3d()[mask]

    @staticmethod
    def invoke(labels: Image, original: Image, color: list[int]) -> Image | None:
        try:
            return SuperposeBorders().preprocess(labels, original, color)
        except GlobalException:
            traceback.print_exc()
        return None

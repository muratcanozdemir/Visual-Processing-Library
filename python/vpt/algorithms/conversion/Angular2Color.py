"""Port of ``vpt.algorithms.conversion.Angular2Color``: maps a mono-channel
angular image to a fixed-saturation, fixed-luma HSY image (hue shifted by
``-0.25``), then converts to RGB via ``HSY2RGB``."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from .HSY2RGB import HSY2RGB


class Angular2Color(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("The input must be a mono-channel angular image")

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        self.output = DoubleImage(xdim, ydim, 3)
        self.output.fill(0)

        out = self.output.getDoubleArray().reshape(ydim, xdim, 3)
        out[:, :, 0] = self.input.getDoubleArray().reshape(ydim, xdim) - 0.25
        out[:, :, 1] = 0.8
        out[:, :, 2] = 0.6
        self.output.setDoubleArray(out.reshape(-1))

        self.output = HSY2RGB.invoke(self.output)

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return Angular2Color().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

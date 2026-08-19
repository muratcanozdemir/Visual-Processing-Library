"""Port of ``vpt.algorithms.shape.FPFT``: Fourier-Polar FT -- magnitude
spectrum of the input's ``FFT``, clamped to ``[0, 1]``, resampled into
polar coordinates via ``ToPolar``, then FFT'd and clamped again.

Since ``frequential.FFT`` is confirmed non-functional (always returns
all-zero output -- see that package's docstring), both FFT calls here
produce an all-zero image, so ``FPFT.invoke`` always returns an all-zero
image too. Faithfully inherited, not fixed."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..frequential.FFT import FFT
from ..geometric.ToPolar import ToPolar


class FPFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        self.output = FFT.invoke(self.input, True, 2)[0]
        for i in range(self.output.getSize()):
            if self.output.getDouble(i) > 1.0 or self.output.getDouble(i) < 0.0:
                self.output.setDouble(i, 1.0)

        self.output = ToPolar.invoke(self.output)

        self.output = FFT.invoke(self.output, True, 2)[0]
        for i in range(self.output.getSize()):
            if self.output.getDouble(i) > 1.0 or self.output.getDouble(i) < 0.0:
                self.output.setDouble(i, 1.0)

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return FPFT().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

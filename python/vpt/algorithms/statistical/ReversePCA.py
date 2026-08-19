"""Port of ``vpt.algorithms.statistical.ReversePCA``: reconstructs an image
from a PCA-style (eigenvectors, mean) decomposition, ``V @ pixels + mean``.

Matrix multiplication goes through ``_jama.times`` rather than
``numpy.matmul``/``@`` -- see that module's docstring for why."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from . import _jama


class ReversePCA(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.eigenvectors: list[list[float]] | None = None
        self.mean: list[float] | None = None
        self.output: Image | None = None
        self.inputFields = "input,eigenvectors,mean"
        self.outputFields = "output"

    def execute(self) -> None:
        cDim = self.input.getCDim()
        xDim = self.input.getXDim()
        yDim = self.input.getYDim()

        size = xDim * yDim

        self.output = self.input.newInstance(False)

        pixels = [[0.0] * size for _ in range(cDim)]
        for c in range(cDim):
            for x in range(xDim):
                for y in range(yDim):
                    pixels[c][y * xDim + x] = self.input.getXYCDouble(x, y, c)

        E = pixels
        V = self.eigenvectors

        M = _jama.times(V, E)

        sonuc = M
        for c in range(cDim):
            for x in range(xDim):
                for y in range(yDim):
                    d = sonuc[c][y * xDim + x]
                    self.output.setXYCDouble(x, y, c, d + self.mean[c])

    @staticmethod
    def invoke(
        input: Image, eigenvectors: list[list[float]], mean: list[float]
    ) -> Image | None:
        try:
            return ReversePCA().preprocess(input, eigenvectors, mean)
        except GlobalException:
            traceback.print_exc()
        return None

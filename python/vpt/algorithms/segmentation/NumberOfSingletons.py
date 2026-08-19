"""Port of ``vpt.algorithms.segmentation.NumberOfSingletons``: counts pixels
whose label differs from every one of their (up to 8) neighbours."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ._util import isSingleArray


class NumberOfSingletons(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: int = 0
        self.labels: Image | None = None
        self.xdim = 0
        self.ydim = 0
        self.inputFields = "labels"
        self.outputFields = "output"

    def execute(self) -> None:
        self.xdim = self.labels.getXDim()
        self.ydim = self.labels.getYDim()
        cdim = self.labels.getCDim()
        if cdim != 1:
            raise GlobalException("A label image cannot have more than one channel!")

        self.output = int(np.count_nonzero(isSingleArray(self.labels)))

    @staticmethod
    def invoke(labels: Image) -> int | None:
        try:
            return NumberOfSingletons().preprocess(labels)
        except GlobalException:
            traceback.print_exc()
        return None

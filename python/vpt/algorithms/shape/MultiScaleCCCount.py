"""Port of ``vpt.algorithms.shape.MultiScaleCCCount``: connected-component
count under progressively larger morphological openings (a granulometry-
style multi-scale shape signature)."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE
from ..mm.binary.BOpening import BOpening
from .ConnectedComponents import ConnectedComponents


class MultiScaleCCCount(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.length: int | None = None
        self.step: int | None = None
        self.startSize: int | None = None
        self.inputFields = "input, length, step, startSize"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        self.output = [0.0] * self.length

        tmp = BOpening.invoke(self.input, FlatSE.square(self.startSize))
        self.output[0] = self._getNumberOfCCs(tmp)
        for i in range(1, self.length):
            tmp = BOpening.invoke(tmp, FlatSE.square(self.startSize + self.step * i))
            self.output[i] = self._getNumberOfCCs(tmp)

    @staticmethod
    def _getNumberOfCCs(img: Image) -> int:
        CCs = ConnectedComponents.invoke(img)
        return len(CCs)

    @staticmethod
    def invoke(img: Image, length: int, step: int, startSize: int) -> list[float] | None:
        try:
            return MultiScaleCCCount().preprocess(img, length, step, startSize)
        except GlobalException:
            traceback.print_exc()
        return None

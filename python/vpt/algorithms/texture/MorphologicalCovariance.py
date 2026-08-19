"""Port of ``vpt.algorithms.texture.MorphologicalCovariance``: erodes by
four two-point structuring elements per scale ``i`` (vertical, main
diagonal, horizontal, anti-diagonal -- each a 2-pixel SE spanning
``side = 2i+1`` pixels) and records the fraction of original volume that
survives, per channel, per direction.

``output`` layout: ``[channel][vertical (len) ++ diag (len) ++ horizontal
(len) ++ anti-diag (len)]``, flattened -- index
``c*4*len + dir*len + i-1``."""

from __future__ import annotations

import traceback

from ..._javacompat import Point, jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ...util.Tools import Tools
from ..mm.gray.GErosion import GErosion


class MorphologicalCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.len: int | None = None
        self.inputFields = "input,len"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        size = self.len * 4 * cdim
        self.output = [0.0] * size

        originalVolumes = [0.0] * cdim
        for c in range(cdim):
            originalVolumes[c] = Tools.volume(self.input, c)

        for i in range(1, self.len + 1):
            side = i * 2 + 1

            se = FlatSE(1, side, Point(0, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * 4 * self.len + i - 1] = jdiv(
                    Tools.volume(tmp, c), originalVolumes[c]
                )

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, side - 1, True)

            tmp = GErosion.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * 4 * self.len + self.len + i - 1] = jdiv(
                    Tools.volume(tmp, c), originalVolumes[c]
                )

            se = FlatSE(side, 1, Point(i, 0))
            se.setXYBoolean(0, 0, True)
            se.setXYBoolean(side - 1, 0, True)

            tmp = GErosion.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * 4 * self.len + 2 * self.len + i - 1] = jdiv(
                    Tools.volume(tmp, c), originalVolumes[c]
                )

            se = FlatSE(side, side, Point(i, i))
            se.setXYBoolean(side - 1, 0, True)
            se.setXYBoolean(0, side - 1, True)

            tmp = GErosion.invoke(self.input, se)
            for c in range(cdim):
                self.output[c * 4 * self.len + 3 * self.len + i - 1] = jdiv(
                    Tools.volume(tmp, c), originalVolumes[c]
                )

    @staticmethod
    def invoke(image: Image, length: int) -> list[float] | None:
        try:
            return MorphologicalCovariance().preprocess(image, length)
        except GlobalException:
            traceback.print_exc()
        return None

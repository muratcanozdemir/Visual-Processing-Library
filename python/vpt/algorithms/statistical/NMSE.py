"""Port of ``vpt.algorithms.statistical.NMSE`` (normalised mean squared
error between two images).

**Effectively always returns 0.0 (or NaN)**, a direct consequence of the
already-documented ``util.Tools.VSub`` defect (mutates its first argument
in place, returns a freshly allocated all-zero array): ``sum1`` accumulates
``EuclideanNorm(VSub(p1, p2))**2``, and since ``VSub``'s return value is
always the zero vector, every term is exactly ``0.0`` regardless of how
different ``filtered`` actually is from ``original``. ``output`` therefore
ends up ``0.0 / sum2`` -- ``0.0`` for any image with nonzero energy, ``NaN``
(via ``jdiv``) for an all-zero ``original``. Transcribed as found; the
metric is not usable for its stated purpose."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class NMSE(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.filtered: Image | None = None
        self.original: Image | None = None
        self.inputFields = "original, filtered"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.original.getXDim()
        ydim = self.original.getYDim()

        sum1 = 0.0
        sum2 = 0.0
        for x in range(xdim):
            for y in range(ydim):
                p1 = self.original.getVXYDouble(x, y)
                p2 = self.filtered.getVXYDouble(x, y)

                norm = Tools.EuclideanNorm(Tools.VSub(p1, p2))
                sum1 += norm * norm

                norm = Tools.EuclideanNorm(p1)
                sum2 += norm * norm
        self.output = jdiv(sum1, sum2)

    @staticmethod
    def invoke(original: Image, filtered: Image) -> float | None:
        try:
            return NMSE().preprocess(original, filtered)
        except GlobalException:
            traceback.print_exc()
        return None

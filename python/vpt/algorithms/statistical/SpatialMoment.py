"""Port of ``vpt.algorithms.statistical.SpatialMoment``: a scale-invariant
moment, ``CentralMoment(momentX, momentY) / Moment(0,0)^alpha`` where
``alpha = (momentX+momentY)/2 + 1``. Built on ``CentralMoment``, which (see
its docstring) never actually centres anything -- a genuine, bytecode-
confirmed bug in the Java, transcribed as found rather than fixed.

``alpha`` is frequently fractional (e.g. 2.5), and ``m00`` (``Moment(0,0)``,
a raw sum of pixel values) is not guaranteed non-negative for arbitrary
image input -- unlike ``mm/gray/connected/attribute/InvariantMoment``'s
near-identical formula, where ``m00`` sums non-negative flat-zone pixel
counts and a plain ``**`` was accepted as low-risk. Routed through
``jpow`` here since a negative base with a fractional exponent is a real
possibility, not just a theoretical one."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv, jpow
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .CentralMoment import CentralMoment
from .Moment import Moment


class SpatialMoment(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.momentX: int | None = None
        self.momentY: int | None = None
        self.inputFields = "input, momentX, momentY"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.momentX + self.momentY < 2:
            raise GlobalException("The sum of momentX and momentY must be >= 2 !")
        cdim = self.input.getCDim()

        self.output = [0.0] * cdim

        muXY = CentralMoment.invoke(self.input, self.momentX, self.momentY)
        m00 = Moment.invoke(self.input, 0, 0)
        alpha = (self.momentX + self.momentY) / 2.0 + 1.0
        for c in range(cdim):
            m00[c] = jpow(m00[c], alpha)
            self.output[c] = jdiv(muXY[c], m00[c])

    @staticmethod
    def invoke(input: Image, momentX: int, momentY: int) -> list[float] | None:
        try:
            return SpatialMoment().preprocess(input, momentX, momentY)
        except GlobalException:
            traceback.print_exc()
        return None

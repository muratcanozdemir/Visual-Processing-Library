"""Port of ``vpt.algorithms.statistical.InvariantMoment``: the eight
classic Hu rotation/scale-invariant moments, computed per channel over the
whole image via this package's ``SpatialMoment`` (itself built on the
never-actually-centred ``CentralMoment`` -- see that file's docstring; the
bug propagates all the way up through here too).

This is the whole-image, multi-channel analogue of
``mm/gray/connected/attribute/InvariantMoment`` (same Hu formulas, but a
single zone's flat-zone pixel list and one scalar output there, vs. the
whole image and one output per channel here) -- an independent Java class,
not shared code, so ported as its own file rather than reusing that one."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .SpatialMoment import SpatialMoment


class InvariantMoment(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.order: int | None = None
        self.inputFields = "input, order"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.order < 1 or self.order > 8:
            raise GlobalException("The order must be \\in [1,8] !")
        cdim = self.input.getCDim()

        self.output = [0.0] * cdim

        sm = SpatialMoment.invoke

        if self.order == 1:
            n20 = sm(self.input, 2, 0)
            n02 = sm(self.input, 0, 2)
            for c in range(cdim):
                self.output[c] = n20[c] + n02[c]

        elif self.order == 2:
            n20 = sm(self.input, 2, 0)
            n02 = sm(self.input, 0, 2)
            n11 = sm(self.input, 1, 1)
            for c in range(cdim):
                self.output[c] = (n20[c] - n02[c]) * (n20[c] - n02[c]) + 4.0 * n11[c] * n11[c]

        elif self.order == 3:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            for c in range(cdim):
                self.output[c] = (n30[c] - 3.0 * n12[c]) * (n30[c] - 3.0 * n12[c]) + (
                    3.0 * n21[c] - n03[c]
                ) * (3.0 * n21[c] - n03[c])

        elif self.order == 4:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            for c in range(cdim):
                self.output[c] = (n30[c] + n12[c]) * (n30[c] + n12[c]) + (
                    n03[c] + n21[c]
                ) * (n03[c] + n21[c])

        elif self.order == 5:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            for c in range(cdim):
                self.output[c] = (n30[c] - 3.0 * n12[c]) * (n30[c] + n12[c]) * (
                    (n30[c] + n12[c]) * (n30[c] + n12[c])
                    - 3.0 * (n21[c] + n03[c]) * (n21[c] + n03[c])
                ) + (3.0 * n21[c] - n03[c]) * (n21[c] + n03[c]) * (
                    3.0 * (n30[c] + n12[c]) * (n30[c] + n12[c])
                    - (n21[c] - n03[c]) * (n21[c] - n03[c])
                )

        elif self.order == 6:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            n20 = sm(self.input, 2, 0)
            n02 = sm(self.input, 0, 2)
            n11 = sm(self.input, 1, 1)
            for c in range(cdim):
                self.output[c] = (n20[c] - n02[c]) * (
                    (n30[c] + n12[c]) * (n30[c] + n12[c]) - (n21[c] + n03[c]) * (n21[c] + n03[c])
                ) + 4.0 * n11[c] * (n30[c] + n12[c]) * (n21[c] + n03[c])

        elif self.order == 7:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            for c in range(cdim):
                self.output[c] = (3.0 * n21[c] - n03[c]) * (n30[c] + n12[c]) * (
                    (n30[c] + n12[c]) * (n30[c] + n12[c])
                    - 3.0 * (n21[c] + n03[c]) * (n21[c] + n03[c])
                ) - (n03[c] - 3.0 * n12[c]) * (n21[c] + n03[c]) * (
                    3.0 * (n30[c] + n12[c]) * (n30[c] + n12[c])
                    - (n21[c] + n03[c]) * (n21[c] + n03[c])
                )

        elif self.order == 8:
            n30 = sm(self.input, 3, 0)
            n03 = sm(self.input, 0, 3)
            n21 = sm(self.input, 2, 1)
            n12 = sm(self.input, 1, 2)
            n20 = sm(self.input, 2, 0)
            n02 = sm(self.input, 0, 2)
            n11 = sm(self.input, 1, 1)
            for c in range(cdim):
                self.output[c] = n11[c] * (
                    (n30[c] + n12[c]) * (n30[c] + n12[c]) - (n03[c] + n21[c]) * (n03[c] + n21[c])
                ) - (n20[c] - n02[c]) * (n30[c] + n12[c]) * (n03[c] + n21[c])

        else:
            raise GlobalException("Never supposed to be here!")

    @staticmethod
    def invoke(input: Image, order: int) -> list[float] | None:
        try:
            return InvariantMoment().preprocess(input, order)
        except GlobalException:
            traceback.print_exc()
        return None

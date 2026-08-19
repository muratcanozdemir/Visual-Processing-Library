"""Port of ``vpt.algorithms.shape.Orientation``: the principal-axis angle
of a shape via its second-order (central) moments, ``0.5 * atan2(2*m11/m00,
m20/m00 - m02/m00)``.

Uses ``statistical.CentralMoment``, which (see that file's docstring) never
actually centres anything -- a bytecode-confirmed bug in the Java -- so
``m11``/``m02``/`m20`` here are really raw (uncentred) moments. Inherited
as-is, not fixed. The three divisions by ``m00`` each go through ``jdiv``
since ``m00`` (a raw pixel-intensity sum) isn't guaranteed nonzero for an
all-zero image."""

from __future__ import annotations

import traceback

from ..._javacompat import jatan2, jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..statistical.CentralMoment import CentralMoment


class Orientation(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: float | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        m00 = CentralMoment.invoke(self.input, 0, 0)[0]
        m11 = CentralMoment.invoke(self.input, 1, 1)[0]
        m02 = CentralMoment.invoke(self.input, 0, 2)[0]
        m20 = CentralMoment.invoke(self.input, 2, 0)[0]

        self.output = 0.5 * jatan2(jdiv(2.0 * m11, m00), jdiv(m20, m00) - jdiv(m02, m00))

    @staticmethod
    def invoke(img: Image) -> float | None:
        try:
            return Orientation().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

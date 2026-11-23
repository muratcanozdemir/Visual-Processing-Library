"""Port of ``vpt.algorithms.frequential.DFT2D``: a naive O(N^4) direct 2D
DFT, or so the code intends.

**Confirmed non-functional, bytecode-verified**: the phase term
``(_x*x/xdim + _y*y/ydim) * -2*pi`` is computed entirely in Java ``int``
arithmetic (`javap` shows `imul`/`idiv` on all four operands, with the
`int -> double` widening happening only *after* both divisions and the
addition) -- not the fractional frequency ratio the formula needs. Since
Java integer division truncates toward zero, every `(_x*x/xdim +
_y*y/ydim)` result is some plain integer `k`, so the phase is always an
exact integer multiple of `-2*pi`: `cos(k * -2*pi) == 1` and
`sin(k * -2*pi) == ~0` for every single `(_x, _y, x, y)` term, regardless
of position. The double loop therefore just sums the (unweighted-by-
frequency) input over and over -- confirmed directly against the JDK 21
oracle that `output[0]` (the "real" part) comes out *constant* across
every `(x, y)`, equal to `sum(all pixels) / sqrt(xdim*ydim)`, and
`output[1]` ("imag") is uniformly ~0 (residual floating-point noise from
`sin` of a not-quite-exact multiple of 2*pi, not true zero). Reproduced
via `idiv` for both truncating divisions rather than the true division
the formula needs -- not "fixed" into a working DFT."""

from __future__ import annotations

import math
import sys
import traceback

from ..._javacompat import idiv, jcos, jsin
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


class DFT2D(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.shifted: bool | None = None
        self.output: list[DoubleImage] | None = None
        self.inputFields = "input, shifted"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim > 1:
            raise GlobalException("Mono-channel images only!")

        self.output = [
            DoubleImage(self.input, False),
            DoubleImage(self.input, False),
            DoubleImage(self.input, False),
        ]
        for x in range(xdim):
            for y in range(ydim):
                real = 0.0
                imag = 0.0
                for _x in range(xdim):
                    for _y in range(ydim):
                        tmp = (idiv(_x * x, xdim) + idiv(_y * y, ydim)) * -6.283185307179586
                        real += self.input.getXYDouble(_x, _y) * jcos(tmp)
                        imag += self.input.getXYDouble(_x, _y) * jsin(tmp)

                real /= math.sqrt(xdim * ydim)
                imag /= math.sqrt(xdim * ydim)

                self.output[0].setXYDouble(x, y, real)
                self.output[1].setXYDouble(x, y, imag)
                self.output[2].setXYDouble(x, y, math.sqrt(real * real + imag * imag))
            print(x, file=sys.stderr)

        if self.shifted:
            self.output[2] = Tools.shiftOrigin(self.output[2])

    @staticmethod
    def invoke(image: Image, shifted: bool) -> list[DoubleImage] | None:
        try:
            return DFT2D().preprocess(image, shifted)
        except GlobalException:
            traceback.print_exc()
        return None

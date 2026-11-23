"""Port of ``vpt.algorithms.frequential.IDFT2D``: the inverse of ``DFT2D``,
same structure.

**Confirmed non-functional, same cause as ``DFT2D``**: the phase term uses
the identical Java-``int``-arithmetic truncating division (`javap`-verified
the same way) -- see ``DFT2D``'s docstring for the full mechanism. Every
phase ends up an exact integer multiple of ``2*pi``, so ``cos == 1``/
``sin == ~0`` everywhere and the output collapses to a position-independent
function of the input's magnitude/phase sums rather than a true inverse
transform. Reproduced via `idiv`, not fixed."""

from __future__ import annotations

import math
import traceback

from ..._javacompat import idiv, jcos, jsin
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class IDFT2D(Algorithm):
    def __init__(self):
        super().__init__()
        self.real: DoubleImage | None = None
        self.imag: DoubleImage | None = None
        self.output: Image | None = None
        self.inputFields = "real, imag"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.real.getXDim()
        ydim = self.real.getYDim()

        self.output = self.real.newInstance(False)
        for x in range(xdim):
            for y in range(ydim):
                pReal = 0.0
                pImag = 0.0
                for _x in range(xdim):
                    for _y in range(ydim):
                        tmp = (idiv(_x * x, xdim) + idiv(_y * y, ydim)) * 6.283185307179586
                        rp = self.real.getXYDouble(_x, _y)
                        ip = self.imag.getXYDouble(_x, _y)

                        pReal += rp * jcos(tmp) - ip * jsin(tmp)
                        pImag += ip * jcos(tmp) + rp * jsin(tmp)

                pReal /= math.sqrt(xdim * ydim)
                pImag /= math.sqrt(xdim * ydim)

                self.output.setXYDouble(x, y, math.sqrt(pReal * pReal + pImag * pImag))

    @staticmethod
    def invoke(real: DoubleImage, imag: DoubleImage) -> Image | None:
        try:
            return IDFT2D().preprocess(real, imag)
        except GlobalException:
            traceback.print_exc()
        return None

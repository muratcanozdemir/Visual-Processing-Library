"""Port of ``vpt.algorithms.logical.Quantization``: masks off the low bits
of each pixel's byte view, keeping only the top ``bit`` bits (``bit=8``
leaves every bit; ``bit=1`` keeps only the sign/high bit). The mask table
is negative two's-complement literals (``-128 == 0xFFFFFF80``, etc.);
Python's ``&`` on a non-negative ``v`` (a byte view is always ``0..255``)
and a negative ``int`` already matches Java's fixed-width ``int & int``
bit pattern, so no ``_javacompat`` helper is needed here."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image

_MASK = [-128, -64, -32, -16, -8, -4, -2, -1]


class Quantization(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.bit: int | None = None
        self.output: Image | None = None
        self.inputFields = "input,bit"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(False)
        if self.bit < 1 or self.bit > 8:
            raise GlobalException("The bit number must be in [1, 8]")

        self.output.setByteArray(self.input.getByteArray() & _MASK[self.bit - 1])

    @staticmethod
    def invoke(img: Image, bit: int) -> Image | None:
        try:
            return Quantization().preprocess(img, bit)
        except GlobalException:
            traceback.print_exc()
        return None

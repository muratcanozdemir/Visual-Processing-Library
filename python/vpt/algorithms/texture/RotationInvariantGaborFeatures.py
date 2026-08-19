"""Port of ``vpt.algorithms.texture.RotationInvariantGaborFeatures``: at
``scales`` growing Gabor filter sizes (``i*60/scales + 1`` for ``i`` in
``1..scales``, both width and height), applies
``linear.RotationInvariantGabor`` over 6 fixed orientations (0 to 5pi/6 in
pi/6 steps) and records the mean/stddev of the result per scale.

``output`` layout: ``[mean_1, sdev_1, mean_2, sdev_2, ...]``, interleaved
per scale (index ``2*(i-1)`` / ``2*(i-1)+1``)."""

from __future__ import annotations

import traceback

from ..._javacompat import idiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..linear.RotationInvariantGabor import RotationInvariantGabor
from ..statistical.Mean import Mean
from ..statistical.SDev import SDev

_ORIENTATIONS = [
    0.0,
    1.047197551196598,
    2.094395102393195,
    3.141592653589793,
    4.188790204786391,
    5.235987755982989,
]


class RotationInvariantGaborFeatures(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.scales: int | None = None
        self.inputFields = "input,scales"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Only grayscale data please...")
        if self.scales < 1:
            raise GlobalException("Invalid scale count")

        self.output = [0.0] * (self.scales * 2)
        for i in range(1, self.scales + 1):
            dim = idiv(i * 60, self.scales) + 1
            tmp = RotationInvariantGabor.invoke(
                self.input, 1.0, _ORIENTATIONS, 0.0, 0.5, 1.0, dim, dim
            )
            mean = Mean.invoke(tmp)
            sdev = SDev.invoke(tmp)

            self.output[2 * (i - 1)] = mean
            self.output[2 * (i - 1) + 1] = sdev

    @staticmethod
    def invoke(image: Image, scales: int) -> list[float] | None:
        try:
            return RotationInvariantGaborFeatures().preprocess(image, scales)
        except GlobalException:
            traceback.print_exc()
        return None

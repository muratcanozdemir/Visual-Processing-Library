"""Port of ``vpt.algorithms.geometric.Flip``: mirror an image horizontally
or vertically, through the byte view."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Flip(Algorithm):
    horizontal = 0
    vertical = 1

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.direction: int | None = None
        self.inputFields = "input, direction"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        self.output = self.input.newInstance(False)

        # Pure axis-reversal copy: the flat storage's index formula
        # ``c + cdim * (x + xdim * y)`` reshapes to ``[y][x][c]``, so
        # mirroring is a single reversed-slice assignment on that view --
        # no per-pixel formula involved.
        img = self.input.getByteArray().reshape(ydim, xdim, cdim)
        if self.direction == Flip.horizontal:
            flipped = img[:, ::-1, :]
        elif self.direction == Flip.vertical:
            flipped = img[::-1, :, :]
        else:
            raise GlobalException("Invalid flipping direction")
        self.output.setByteArray(flipped.reshape(-1))

    @staticmethod
    def invoke(image: Image, direction: int) -> Image | None:
        try:
            return Flip().preprocess(image, direction)
        except GlobalException:
            traceback.print_exc()
        return None

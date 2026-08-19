"""Port of ``vpt.algorithms.geometric.Crop``: extract the axis-aligned
rectangle between two corner points (order-independent -- the min/max of
each coordinate is taken)."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class Crop(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.p1: Point | None = None
        self.p2: Point | None = None
        self.inputFields = "input,p1,p2"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        if (
            self.p1.x < 0
            or self.p2.x < 0
            or self.p1.x > xdim
            or self.p2.x > xdim
            or self.p1.y < 0
            or self.p2.y < 0
            or self.p1.y > ydim
            or self.p2.y > ydim
        ):
            raise GlobalException("Error: coordinates outside of image")

        upLeftX = min(self.p1.x, self.p2.x)
        upLeftY = min(self.p1.y, self.p2.y)

        downRightX = max(self.p1.x, self.p2.x)
        downRightY = max(self.p1.y, self.p2.y)

        self.output = self.input.newInstance(downRightX - upLeftX, downRightY - upLeftY, cdim)

        # Pure sub-rectangle copy: the flat storage's index formula
        # ``c + cdim * (x + xdim * y)`` reshapes to ``[y][x][c]``, so the
        # cropped rectangle is a single contiguous-after-copy slice -- no
        # per-pixel formula, so no ordering/rounding subtlety to preserve.
        src = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        cropped = src[upLeftY:downRightY, upLeftX:downRightX, :]
        self.output.setDoubleArray(cropped.reshape(-1))

    @staticmethod
    def invoke(image: Image, p1: Point, p2: Point) -> Image | None:
        try:
            return Crop().preprocess(image, p1, p2)
        except GlobalException:
            traceback.print_exc()
        return None

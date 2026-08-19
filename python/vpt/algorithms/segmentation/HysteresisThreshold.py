"""Port of ``vpt.algorithms.segmentation.HysteresisThreshold``: classic
Canny-style hysteresis thresholding -- seed from any pixel at or above
``highThreshold``, then flood-fill 8-connected through any neighbour at or
above ``lowThreshold``, marking each visited pixel ``1.0``; everything else
ends up ``0.0``."""

from __future__ import annotations

import traceback
from collections import deque

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class HysteresisThreshold(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.lowThreshold: float | None = None
        self.highThreshold: float | None = None
        self.inputFields = "input,lowThreshold,highThreshold"
        self.outputFields = "output"

    def execute(self) -> None:
        self.lowThreshold = max(0.0, min(self.lowThreshold, 1.0))
        self.highThreshold = max(0.0, min(self.highThreshold, 1.0))
        if self.lowThreshold >= self.highThreshold:
            raise GlobalException("Low threshold must be inferior to high threshold")

        self.output = self.input.newInstance(True)

        chosen = deque()
        for c in range(self.output.getCDim()):
            for x in range(self.output.getXDim()):
                for y in range(self.output.getYDim()):
                    pixel = self.output.getXYCDouble(x, y, c)
                    if pixel >= self.highThreshold and pixel < 1.0:
                        chosen.append(Point(x, y))
                        while chosen:
                            p = chosen.popleft()
                            if (
                                p.x >= 0
                                and p.y >= 0
                                and p.x < self.output.getXDim()
                                and p.y < self.output.getYDim()
                            ):
                                tmp = self.output.getXYCDouble(p.x, p.y, c)
                                if tmp >= self.lowThreshold:
                                    if tmp < 1.0:
                                        self.output.setXYCDouble(p.x, p.y, c, 1.0)

                                        chosen.append(Point(p.x - 1, p.y - 1))
                                        chosen.append(Point(p.x - 1, p.y))
                                        chosen.append(Point(p.x - 1, p.y + 1))
                                        chosen.append(Point(p.x, p.y - 1))
                                        chosen.append(Point(p.x, p.y + 1))
                                        chosen.append(Point(p.x + 1, p.y - 1))
                                        chosen.append(Point(p.x + 1, p.y))
                                        chosen.append(Point(p.x + 1, p.y + 1))

        # Tail cleanup only -- the BFS seed/propagate loop above this is
        # genuinely sequential (queue-driven flood fill) and stays as-is.
        # Every pixel not marked 1.0 by that flood fill becomes 0.0;
        # elementwise and order-independent.
        pixels = self.output.getDoubleArray()
        pixels[pixels < 1.0] = 0.0
        self.output.setDoubleArray(pixels)

    @staticmethod
    def invoke(image: Image, lowThreshold: float, highThreshold: float) -> Image | None:
        try:
            return HysteresisThreshold().preprocess(image, lowThreshold, highThreshold)
        except GlobalException:
            traceback.print_exc()
        return None

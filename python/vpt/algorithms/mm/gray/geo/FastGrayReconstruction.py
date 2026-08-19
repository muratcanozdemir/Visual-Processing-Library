"""Port of ``vpt.algorithms.mm.gray.geo.FastGrayReconstruction``.

Vincent's hybrid grayscale reconstruction algorithm: a raster-order forward
scan, an anti-raster-order backward scan seeding a FIFO queue wherever a
pixel's backward-scan value could still be pulled up (down, in the inverse
case) by a not-yet-visited neighbour, then queue-driven propagation until
stable. This converges in far fewer passes than the naive fixpoint loop
``GReconstructionByDilation``/``GReconstructionByErosion`` use.

Method names describe the *outer* clamp the caller applies (``output =
min(minForward(...), mask)`` etc.), not what each one computes internally --
``minForward`` internally takes a running ``max`` over already-visited
neighbours (propagating the reconstruction-by-dilation forward pass) while
``maxForward`` internally takes a running ``min`` (the erosion/``inverse``
dual). Transcribed as named in the Java; this is original naming, not a
decompiler artefact.

The private ``Fifo`` inner class is just a list-backed FIFO; folded into a
plain ``collections.deque`` here rather than kept as a nested class, since
Python has no analogous need for an inner class to close over ``self``.
"""

from __future__ import annotations

import traceback
from collections import deque

from ....._javacompat import Point
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image

CONNEXITY4 = 0
CONNEXITY8 = 1


class FastGrayReconstruction(Algorithm):
    CONNEXITY4 = CONNEXITY4
    CONNEXITY8 = CONNEXITY8

    def __init__(self):
        super().__init__()
        self.marker: Image | None = None
        self.mask: Image | None = None
        self.connexity: int = CONNEXITY8
        self.inverse: bool = False
        self.output: Image | None = None
        self.inputFields = "marker,mask,connexity,inverse"
        self.outputFields = "output"

        self.xdim = 0
        self.ydim = 0
        self.cdim = 0

    def execute(self) -> None:
        self.output = self.marker.newInstance(True)

        self.xdim = self.marker.getXDim()
        self.ydim = self.marker.getYDim()
        self.cdim = self.marker.getCDim()

        for c in range(self.cdim):
            f: deque[Point] = deque()

            for y in range(self.ydim):
                for x in range(self.xdim):
                    if not self.inverse:
                        self.output.setXYCByte(
                            x, y, c, min(self._minForward(x, y, c), self.mask.getXYCByte(x, y, c))
                        )
                    else:
                        self.output.setXYCByte(
                            x, y, c, max(self._maxForward(x, y, c), self.mask.getXYCByte(x, y, c))
                        )

            for y in range(self.ydim - 1, -1, -1):
                for x in range(self.xdim - 1, -1, -1):
                    if not self.inverse:
                        self.output.setXYCByte(
                            x, y, c, min(self._minBackward(x, y, c), self.mask.getXYCByte(x, y, c))
                        )
                    else:
                        self.output.setXYCByte(
                            x, y, c, max(self._maxBackward(x, y, c), self.mask.getXYCByte(x, y, c))
                        )
                    if self._updateFifo(x, y, c, self.inverse):
                        f.append(Point(x, y))

            while f:
                p = f.popleft()
                self._checkNeighbours(p.x, p.y, c, f, self.output.getXYCByte(p.x, p.y, c), self.inverse)

    # ------------------------------------------------------------------
    def _maxForward(self, x: int, y: int, c: int) -> int:
        val = self.output.getXYCByte(x, y, c)
        if x > 0:
            val = min(val, self.output.getXYCByte(x - 1, y, c))
        if y > 0:
            val = min(val, self.output.getXYCByte(x, y - 1, c))
        if self.connexity == CONNEXITY4:
            return val
        if x > 0 and y > 0:
            val = min(val, self.output.getXYCByte(x - 1, y - 1, c))
        if x < self.xdim - 1 and y > 0:
            val = min(val, self.output.getXYCByte(x + 1, y - 1, c))
        return val

    def _maxBackward(self, x: int, y: int, c: int) -> int:
        val = self.output.getXYCByte(x, y, c)
        if x < self.xdim - 1:
            val = min(val, self.output.getXYCByte(x + 1, y, c))
        if y < self.ydim - 1:
            val = min(val, self.output.getXYCByte(x, y + 1, c))
        if self.connexity == CONNEXITY4:
            return val
        if x < self.xdim - 1 and y < self.ydim - 1:
            val = min(val, self.output.getXYCByte(x + 1, y + 1, c))
        if x > 0 and y < self.ydim - 1:
            val = min(val, self.output.getXYCByte(x - 1, y + 1, c))
        return val

    def _minForward(self, x: int, y: int, c: int) -> int:
        val = self.output.getXYCByte(x, y, c)
        if x > 0:
            val = max(val, self.output.getXYCByte(x - 1, y, c))
        if y > 0:
            val = max(val, self.output.getXYCByte(x, y - 1, c))
        if self.connexity == CONNEXITY4:
            return val
        if x > 0 and y > 0:
            val = max(val, self.output.getXYCByte(x - 1, y - 1, c))
        if x < self.xdim - 1 and y > 0:
            val = max(val, self.output.getXYCByte(x + 1, y - 1, c))
        return val

    def _minBackward(self, x: int, y: int, c: int) -> int:
        val = self.output.getXYCByte(x, y, c)
        if x < self.xdim - 1:
            val = max(val, self.output.getXYCByte(x + 1, y, c))
        if y < self.ydim - 1:
            val = max(val, self.output.getXYCByte(x, y + 1, c))
        if self.connexity == CONNEXITY4:
            return val
        if x < self.xdim - 1 and y < self.ydim - 1:
            val = max(val, self.output.getXYCByte(x + 1, y + 1, c))
        if x > 0 and y < self.ydim - 1:
            val = max(val, self.output.getXYCByte(x - 1, y + 1, c))
        return val

    # ------------------------------------------------------------------
    def _checkFifo(self, x: int, y: int, c: int, val: int, inverse: bool) -> bool:
        if not inverse:
            return (
                self.output.getXYCByte(x, y, c) < val
                and self.output.getXYCByte(x, y, c) < self.mask.getXYCByte(x, y, c)
            )
        return (
            self.output.getXYCByte(x, y, c) > val
            and self.output.getXYCByte(x, y, c) > self.mask.getXYCByte(x, y, c)
        )

    def _updateFifo(self, x: int, y: int, c: int, inverse: bool) -> bool:
        val = self.output.getXYCByte(x, y, c)
        if x < self.xdim - 1 and self._checkFifo(x + 1, y, c, val, inverse):
            return True
        if y < self.ydim - 1 and self._checkFifo(x, y + 1, c, val, inverse):
            return True
        if self.connexity == CONNEXITY4:
            return False
        if x < self.xdim - 1 and y < self.ydim - 1 and self._checkFifo(x + 1, y + 1, c, val, inverse):
            return True
        if x > 0 and y < self.ydim - 1 and self._checkFifo(x - 1, y + 1, c, val, inverse):
            return True
        return False

    # ------------------------------------------------------------------
    def _checkNeighbours(self, x: int, y: int, c: int, f: "deque[Point]", val: int, inverse: bool) -> None:
        if x > 0:
            self._checkPixel(x - 1, y, c, f, val, inverse)
        if y > 0:
            self._checkPixel(x, y - 1, c, f, val, inverse)
        if x < self.xdim - 1:
            self._checkPixel(x + 1, y, c, f, val, inverse)
        if y < self.ydim - 1:
            self._checkPixel(x, y + 1, c, f, val, inverse)
        if self.connexity == CONNEXITY4:
            return
        if x > 0 and y > 0:
            self._checkPixel(x - 1, y - 1, c, f, val, inverse)
        if x > 0 and y < self.ydim - 1:
            self._checkPixel(x - 1, y + 1, c, f, val, inverse)
        if x < self.xdim - 1 and y > 0:
            self._checkPixel(x + 1, y - 1, c, f, val, inverse)
        if x < self.xdim - 1 and y < self.ydim - 1:
            self._checkPixel(x + 1, y + 1, c, f, val, inverse)

    def _checkPixel(self, x: int, y: int, c: int, f: "deque[Point]", val: int, inverse: bool) -> None:
        if (
            not inverse
            and self.output.getXYCByte(x, y, c) < val
            and self.mask.getXYCByte(x, y, c) != self.output.getXYCByte(x, y, c)
        ):
            self.output.setXYCByte(x, y, c, min(val, self.mask.getXYCByte(x, y, c)))
            f.append(Point(x, y))
        elif (
            inverse
            and self.output.getXYCByte(x, y, c) > val
            and self.mask.getXYCByte(x, y, c) != self.output.getXYCByte(x, y, c)
        ):
            self.output.setXYCByte(x, y, c, max(val, self.mask.getXYCByte(x, y, c)))
            f.append(Point(x, y))

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(marker: Image, mask: Image, connexity: int, flag: bool) -> Image | None:
        try:
            return FastGrayReconstruction().preprocess(marker, mask, connexity, flag)
        except GlobalException:
            traceback.print_exc()
        return None

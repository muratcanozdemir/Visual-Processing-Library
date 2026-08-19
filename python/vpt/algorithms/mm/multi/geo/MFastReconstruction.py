"""Port of ``vpt.algorithms.mm.multi.geo.MFastReconstruction``.

Vincent's hybrid reconstruction algorithm (see
``mm.gray.geo.FastGrayReconstruction`` for the scalar/per-channel twin), but
operating on whole pixel vectors at once through ``getVXYDouble``/
``setVXYDouble`` and a caller-supplied ``AlgebraicOrdering`` in place of
scalar ``min``/``max`` -- there's no single loop over ``cdim`` here because
the ordering already compares full vectors.

The private ``Fifo`` inner class is just a list-backed FIFO; folded into a
plain ``collections.deque`` here, as in the gray-scale port.

Preserved bug: in the Java, ``minForward``'s two CONNEXITY8 diagonal branches
call ``this.or.max(val, ...)`` without assigning the result back to ``val``
(``maxForward``/``maxBackward``/``minBackward`` all assign in every branch;
only ``minForward``'s diagonals don't). So under 8-connexity, ``minForward``
effectively only takes the 4-connexity neighbours into account, and the
diagonal comparisons are computed and discarded. Transcribed as found --
not "fixed" to match the other three methods.
"""

from __future__ import annotations

import traceback
from collections import deque

from ....._javacompat import Point
from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from .....util.ordering import AlgebraicOrdering

CONNEXITY4 = 0
CONNEXITY8 = 1


class MFastReconstruction(Algorithm):
    CONNEXITY4 = CONNEXITY4
    CONNEXITY8 = CONNEXITY8

    def __init__(self):
        super().__init__()
        self.marker: Image | None = None
        self.mask: Image | None = None
        self.connexity: int = CONNEXITY8
        self.inverse: bool = False
        self.output: Image | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "marker,mask,connexity,inverse,or_"
        self.outputFields = "output"

        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.output = self.marker.newInstance(True)

        self.xdim = self.marker.getXDim()
        self.ydim = self.marker.getYDim()

        f: deque[Point] = deque()

        for y in range(self.ydim):
            for x in range(self.xdim):
                if not self.inverse:
                    self.output.setVXYDouble(
                        x, y, self.or_.min(self._minForward(x, y), self.mask.getVXYDouble(x, y))
                    )
                else:
                    self.output.setVXYDouble(
                        x, y, self.or_.max(self._maxForward(x, y), self.mask.getVXYDouble(x, y))
                    )

        for y in range(self.ydim - 1, -1, -1):
            for x in range(self.xdim - 1, -1, -1):
                if not self.inverse:
                    self.output.setVXYDouble(
                        x, y, self.or_.min(self._minBackward(x, y), self.mask.getVXYDouble(x, y))
                    )
                else:
                    self.output.setVXYDouble(
                        x, y, self.or_.max(self._maxBackward(x, y), self.mask.getVXYDouble(x, y))
                    )
                if self._updateFifo(x, y, self.inverse):
                    f.append(Point(x, y))

        while f:
            p = f.popleft()
            self._checkNeighbours(p.x, p.y, f, self.output.getVXYDouble(p.x, p.y), self.inverse)

    # ------------------------------------------------------------------
    def _maxForward(self, x: int, y: int):
        val = self.output.getVXYDouble(x, y)
        if x > 0:
            val = self.or_.min(val, self.output.getVXYDouble(x - 1, y))
        if y > 0:
            val = self.or_.min(val, self.output.getVXYDouble(x, y - 1))
        if self.connexity == CONNEXITY4:
            return val
        if x > 0 and y > 0:
            val = self.or_.min(val, self.output.getVXYDouble(x - 1, y - 1))
        if x < self.xdim - 1 and y > 0:
            val = self.or_.min(val, self.output.getVXYDouble(x + 1, y - 1))
        return val

    def _maxBackward(self, x: int, y: int):
        val = self.output.getVXYDouble(x, y)
        if x < self.xdim - 1:
            val = self.or_.min(val, self.output.getVXYDouble(x + 1, y))
        if y < self.ydim - 1:
            val = self.or_.min(val, self.output.getVXYDouble(x, y + 1))
        if self.connexity == CONNEXITY4:
            return val
        if x < self.xdim - 1 and y < self.ydim - 1:
            val = self.or_.min(val, self.output.getVXYDouble(x + 1, y + 1))
        if x > 0 and y < self.ydim - 1:
            val = self.or_.min(val, self.output.getVXYDouble(x - 1, y + 1))
        return val

    def _minForward(self, x: int, y: int):
        val = self.output.getVXYDouble(x, y)
        if x > 0:
            val = self.or_.max(val, self.output.getVXYDouble(x - 1, y))
        if y > 0:
            val = self.or_.max(val, self.output.getVXYDouble(x, y - 1))
        if self.connexity == CONNEXITY4:
            return val
        if x > 0 and y > 0:
            self.or_.max(val, self.output.getVXYDouble(x - 1, y - 1))
        if x < self.xdim - 1 and y > 0:
            self.or_.max(val, self.output.getVXYDouble(x + 1, y - 1))
        return val

    def _minBackward(self, x: int, y: int):
        val = self.output.getVXYDouble(x, y)
        if x < self.xdim - 1:
            val = self.or_.max(val, self.output.getVXYDouble(x + 1, y))
        if y < self.ydim - 1:
            val = self.or_.max(val, self.output.getVXYDouble(x, y + 1))
        if self.connexity == CONNEXITY4:
            return val
        if x < self.xdim - 1 and y < self.ydim - 1:
            val = self.or_.max(val, self.output.getVXYDouble(x + 1, y + 1))
        if x > 0 and y < self.ydim - 1:
            val = self.or_.max(val, self.output.getVXYDouble(x - 1, y + 1))
        return val

    # ------------------------------------------------------------------
    def _checkFifo(self, x: int, y: int, val, inverse: bool) -> bool:
        if not inverse:
            return (
                self.or_.compare(self.output.getVXYDouble(x, y), val) < 0
                and self.or_.compare(self.output.getVXYDouble(x, y), self.mask.getVXYDouble(x, y)) < 0
            )
        return (
            self.or_.compare(self.output.getVXYDouble(x, y), val) > 0
            and self.or_.compare(self.output.getVXYDouble(x, y), self.mask.getVXYDouble(x, y)) > 0
        )

    def _updateFifo(self, x: int, y: int, inverse: bool) -> bool:
        val = self.output.getVXYDouble(x, y)
        if x < self.xdim - 1 and self._checkFifo(x + 1, y, val, inverse):
            return True
        if y < self.ydim - 1 and self._checkFifo(x, y + 1, val, inverse):
            return True
        if self.connexity == CONNEXITY4:
            return False
        if x < self.xdim - 1 and y < self.ydim - 1 and self._checkFifo(x + 1, y + 1, val, inverse):
            return True
        if x > 0 and y < self.ydim - 1 and self._checkFifo(x - 1, y + 1, val, inverse):
            return True
        return False

    # ------------------------------------------------------------------
    def _checkNeighbours(self, x: int, y: int, f: "deque[Point]", val, inverse: bool) -> None:
        if x > 0:
            self._checkPixel(x - 1, y, f, val, inverse)
        if y > 0:
            self._checkPixel(x, y - 1, f, val, inverse)
        if x < self.xdim - 1:
            self._checkPixel(x + 1, y, f, val, inverse)
        if y < self.ydim - 1:
            self._checkPixel(x, y + 1, f, val, inverse)
        if self.connexity == CONNEXITY4:
            return
        if x > 0 and y > 0:
            self._checkPixel(x - 1, y - 1, f, val, inverse)
        if x > 0 and y < self.ydim - 1:
            self._checkPixel(x - 1, y + 1, f, val, inverse)
        if x < self.xdim - 1 and y > 0:
            self._checkPixel(x + 1, y - 1, f, val, inverse)
        if x < self.xdim - 1 and y < self.ydim - 1:
            self._checkPixel(x + 1, y + 1, f, val, inverse)

    def _checkPixel(self, x: int, y: int, f: "deque[Point]", val, inverse: bool) -> None:
        if (
            not inverse
            and self.or_.compare(self.output.getVXYDouble(x, y), val) < 0
            and self.or_.compare(self.mask.getVXYDouble(x, y), self.output.getVXYDouble(x, y)) != 0
        ):
            self.output.setVXYDouble(x, y, self.or_.min(val, self.mask.getVXYDouble(x, y)))
            f.append(Point(x, y))
        elif (
            inverse
            and self.or_.compare(self.output.getVXYDouble(x, y), val) > 0
            and self.or_.compare(self.mask.getVXYDouble(x, y), self.output.getVXYDouble(x, y)) != 0
        ):
            self.output.setVXYDouble(x, y, self.or_.max(val, self.mask.getVXYDouble(x, y)))
            f.append(Point(x, y))

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(
        marker: Image, mask: Image, connexity: int, flag: bool, or_: AlgebraicOrdering
    ) -> Image | None:
        try:
            return MFastReconstruction().preprocess(marker, mask, connexity, flag, or_)
        except GlobalException:
            traceback.print_exc()
        return None

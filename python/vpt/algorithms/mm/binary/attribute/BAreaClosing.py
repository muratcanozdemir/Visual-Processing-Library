"""Port of ``vpt.algorithms.mm.binary.attribute.BAreaClosing``.

Fills every background (False) 8-connected component with fewer than
``area`` pixels -- the dual of ``BAreaOpening``: same stack-based flood
fill, with the boolean sense of every check and write inverted.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....._javacompat import Point
from ..... import _accel
from .....Algorithm import Algorithm
from .....BooleanImage import BooleanImage
from .....GlobalException import GlobalException
from .....Image import Image


class BAreaClosing(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.area: int | None = None
        self.output: Image | None = None
        self.inputFields = "input, area"
        self.outputFields = "output"

        self._mask: BooleanImage | None = None
        self.xdim = 0
        self.ydim = 0

    def _filter(self, s: list[Point]) -> None:
        vp: list[Point] = []
        size = 0
        while s:
            p = s.pop()
            if (
                0 <= p.x < self.xdim
                and 0 <= p.y < self.ydim
                and not self._mask.getXYBoolean(p.x, p.y)
            ):
                self._mask.setXYBoolean(p.x, p.y, True)
                vp.append(p)
                size += 1
                s.append(Point(p.x + 1, p.y - 1))
                s.append(Point(p.x + 1, p.y))
                s.append(Point(p.x + 1, p.y + 1))
                s.append(Point(p.x - 1, p.y + 1))
                s.append(Point(p.x - 1, p.y))
                s.append(Point(p.x - 1, p.y - 1))
                s.append(Point(p.x, p.y - 1))
                s.append(Point(p.x, p.y + 1))

        if size < self.area:
            for p in vp:
                self.output.setXYBoolean(p.x, p.y, True)

    def execute(self) -> None:
        self.output = self.input.newInstance(True)
        if self._executeNative():
            return
        self._mask = BooleanImage(self.input, True)

        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        s: list[Point] = []
        for x in range(self.xdim):
            for y in range(self.ydim):
                if not self._mask.getXYBoolean(x, y):
                    s.clear()
                    s.append(Point(x, y))
                    self._filter(s)

    def _executeNative(self) -> bool:
        """The same result from the Rust kernel (``python/src/area.rs``),
        when available: the pixels of every 8-connected background component
        of fewer than ``area`` pixels (read through the flat index
        ``x + xdim * y``, like ``getXYBoolean``) are set to True. Which
        pixels those are doesn't depend on the order the flood fills run
        in. Returns False to fall back to the Python below."""
        native = _accel.native
        if native is None or type(self.area) is not int:
            return False
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        n = self.xdim * self.ydim
        mask = ~self.input.getBooleanArray()[:n]
        small = native.small_components(
            mask.astype(np.uint8).tobytes(), self.xdim, self.ydim, _accel.clamp_i64(self.area)
        )
        idx = np.flatnonzero(np.frombuffer(small, dtype=np.uint8))
        _accel.set_booleans(self.output, idx, True)
        return True

    @staticmethod
    def invoke(image: Image, area: int) -> Image | None:
        try:
            return BAreaClosing().preprocess(image, area)
        except GlobalException:
            traceback.print_exc()
        return None

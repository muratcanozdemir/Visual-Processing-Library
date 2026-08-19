"""Port of ``vpt.algorithms.flatzones.gray.GrayFZ``.

Strict flat zones: 8-connected components of pixels with *exactly* equal
value (unlike ``GrayQFZ``'s tolerance-``alpha``/range-``omega`` quasi-flat
zones). Same stack-based flood-fill shape as ``GrayQFZ``/``shape.
ConnectedComponents``. Preserves the Java's per-column progress print and
final zone-count print (Turkish debug strings from the original author,
also seen in ``segmentation.MeanShift``/``conversion.RGB2NUHSY733``),
routed to stderr like every other such print in this port."""

from __future__ import annotations

import sys
import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....BooleanImage import BooleanImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class GrayFZ(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: IntegerImage | None = None
        self.inputFields = "input"
        self.outputFields = "output"

        self._list2: list = []
        self._stack: list = []
        self.xdim = 0
        self.ydim = 0
        self._temp: BooleanImage | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        if self.input.getCDim() != 1:
            raise GlobalException("This implementation is only for grayscale images.")

        self.output = IntegerImage(self.xdim, self.ydim, 1)
        self.output.fill(0)

        self._temp = BooleanImage(self.xdim, self.ydim, 1)
        self._temp.fill(False)

        label = 1
        for x in range(self.xdim):
            print(f"Sutun {x}", file=sys.stderr)
            for y in range(self.ydim):
                if self.output.getXYInt(x, y) <= 0:
                    t = Point(x, y)

                    self._createFZ(t, label)
                    label += 1

        print(f"Total number of quasi flat zones: {label - 1}", file=sys.stderr)

    def _createFZ(self, r: Point, label: int) -> None:
        self._stack.clear()
        self._stack.append(r)
        self._temp.setXYBoolean(r.x, r.y, True)
        self._list2.append(r)

        while self._stack:
            tmp = self._stack.pop()

            x = tmp.x
            y = tmp.y

            p = self.input.getXYByte(x, y)
            self.output.setXYInt(x, y, label)

            for n in Tools.N:
                _x = x + n.x
                _y = y + n.y
                if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                    if not self._temp.getXYBoolean(_x, _y):
                        q = self.input.getXYByte(_x, _y)
                        if q == p:
                            t = Point(_x, _y)
                            self._stack.append(t)

                            self._temp.setXYBoolean(t.x, t.y, True)
                            self._list2.append(t)

        for t in self._list2:
            self._temp.setXYBoolean(t.x, t.y, False)
        self._list2.clear()

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return GrayFZ().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

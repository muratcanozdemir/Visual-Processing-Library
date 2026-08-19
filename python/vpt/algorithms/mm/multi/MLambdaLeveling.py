"""Port of ``vpt.algorithms.mm.multi.MLambdaLeveling``.

Vectorial lambda-leveling: repeatedly pulls ``marker`` toward ``input``
through the SE neighbourhood -- expanding upward via a geodesic-style
dilation-then-min when ``input`` is more than ``lambda`` "above" ``marker``,
or downward via erosion-then-max when ``marker`` is more than ``lambda``
"above" ``input`` -- until a fixpoint (``Equal``) is reached.

HAZARD, inherited from ``Tools.VAddC``: that helper's docstring documents
a preserved original-library defect -- it mutates its input vector in
place but *returns a freshly allocated all-zero vector*, not the sum. Both
comparisons here (``or_.compare(i, Tools.VAddC(m, ...))`` and the ``elif``)
are therefore comparing against the zero vector, not ``marker`` or ``input``
offset by ``lambda`` as the algorithm's name implies. This is not
introduced by this port -- see ``Tools.VAddC``'s docstring for the full
explanation -- but it means ``lambda`` likely has little to no effect on
which branch is taken for any pixel where the compared vector isn't itself
all-zero.
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.ordering import AlgebraicOrdering
from ....util.se import FlatSE
from ....util.Tools import Tools
from ...arithmetic.Equal import Equal

_BYTE_TO_DOUBLE = 0.00392156862745098


class MLambdaLeveling(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.lambda_: int | None = None
        self.marker: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.or_: AlgebraicOrdering | None = None
        self.inputFields = "input,marker,se,or_,lambda_"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0
        self._pixelArray: list | None = None

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if self.lambda_ < 0 or self.lambda_ > 254:
            raise GlobalException("The lambda value must be \\in [0,254]")

        self._pixelArray = [None] * len(self.se.getCoords())
        while True:
            tmp = self.marker
            self.marker = self._level(self.marker, self.input)
            if Equal.invoke(self.marker, tmp):
                break
        self.output = self.marker

    def _level(self, marker: Image, input: Image) -> Image:
        tmp = marker.newInstance(True)
        for x in range(self.xdim):
            for y in range(self.ydim):
                m = marker.getVXYDouble(x, y)
                i = input.getVXYDouble(x, y)
                if self.or_.compare(i, Tools.VAddC(m, self.lambda_ * _BYTE_TO_DOUBLE)) > 0:
                    tmp.setVXYDouble(x, y, self.or_.min(i, self._getMax(marker, x, y, self.se.getCoords())))
                elif self.or_.compare(m, Tools.VAddC(i, self.lambda_ * _BYTE_TO_DOUBLE)) > 0:
                    tmp.setVXYDouble(x, y, self.or_.max(i, self._getMin(marker, x, y, self.se.getCoords())))
        return tmp

    def _getMin(self, img: Image, x: int, y: int, coords):
        index = 0
        for p in coords:
            _x = x + p.x
            _y = y + p.y
            if _x < 0:
                _x = 0
            if _y < 0:
                _y = 0
            if _x >= self.xdim:
                _x = self.xdim - 1
            if _y >= self.ydim:
                _y = self.ydim - 1
            self._pixelArray[index] = img.getVXYDouble(_x, _y)
            index += 1
        return self.or_.min(self._pixelArray) if index > 0 else img.getVXYDouble(x, y)

    def _getMax(self, img: Image, x: int, y: int, coords):
        index = 0
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if _x < 0:
                _x = 0
            if _y < 0:
                _y = 0
            if _x >= self.xdim:
                _x = self.xdim - 1
            if _y >= self.ydim:
                _y = self.ydim - 1
            self._pixelArray[index] = img.getVXYDouble(_x, _y)
            index += 1
        return self.or_.max(self._pixelArray) if index > 0 else img.getVXYDouble(x, y)

    @staticmethod
    def invoke(
        image: Image, marker: Image, se: FlatSE, or_: AlgebraicOrdering, lambda_: int
    ) -> Image | None:
        try:
            return MLambdaLeveling().preprocess(image, marker, se, or_, lambda_)
        except GlobalException:
            traceback.print_exc()
        return None

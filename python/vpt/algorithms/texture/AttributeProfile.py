"""Port of ``vpt.algorithms.texture.AttributeProfile``: a max-tree
attribute-filter profile. For each ``Attribute`` predicate in ``criteria``,
applies ``GMaxTreeNonIncrAttrOpening``/``GMaxTreeNonIncrAttrClosing`` to
each channel independently and records a per-channel valuation of the
result, normalised by the valuation of the unfiltered input.

``output`` layout: ``[channel][opening per criterion (len(criteria)) ++
closing per criterion]``, flattened -- index ``c * len(criteria) * 2 + i``
for openings, ``c * len(criteria) * 2 + len(criteria) + i`` for closings.

Java defaults ``valuation`` to ``VOLUME`` (0) when the caller passes
``null`` -- reproduced with the same ``if self.valuation is None`` default
in ``execute``. Java's two ``invoke`` overloads (3-arg, and a 2-arg one
that passes ``null`` for ``valuation``) collapse into one Python `invoke`
with `valuation: int | None = None`, matching the
``GMaxTreeNonIncrAttrOpening``/``Closing`` overload-collapsing precedent
already used elsewhere in the port."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..mm.gray.connected.attribute.Attribute import Attribute
from ..mm.gray.connected.GMaxTreeNonIncrAttrClosing import GMaxTreeNonIncrAttrClosing
from ..mm.gray.connected.GMaxTreeNonIncrAttrOpening import GMaxTreeNonIncrAttrOpening
from ..statistical.InvariantMoment import InvariantMoment
from ..statistical.Variance import Variance

VOLUME = 0
MOMENT1 = 1
VARIANCE = 2


class AttributeProfile(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.criteria: list[Attribute] | None = None
        self.valuation: int | None = None
        self.inputFields = "input,criteria,valuation"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        n = len(self.criteria)
        size = n * cdim * 2
        self.output = [0.0] * size

        originalValues = [0.0] * cdim
        if self.valuation is None:
            self.valuation = VOLUME
        for c in range(cdim):
            originalValues[c] = self._valuation(self.input, c)

        for i in range(n):
            for c in range(cdim):
                tmp = GMaxTreeNonIncrAttrOpening.invoke(self.input.getChannel(c), self.criteria[i])
                self.output[c * n * 2 + i] = jdiv(self._valuation(tmp, 0), originalValues[c])

            for c in range(cdim):
                tmp = GMaxTreeNonIncrAttrClosing.invoke(self.input.getChannel(c), self.criteria[i])
                self.output[c * n * 2 + n + i] = jdiv(self._valuation(tmp, 0), originalValues[c])

    def _valuation(self, img: Image, channel: int) -> float:
        if self.valuation == VOLUME:
            return Tools.volume(img, channel)
        if self.valuation == VARIANCE:
            return Variance.invoke(img)
        if self.valuation == MOMENT1:
            return InvariantMoment.invoke(img, 1)[0]
        raise GlobalException("Invalid valuation type")

    @staticmethod
    def invoke(
        image: Image, criteria: list[Attribute], valuation: int | None = None
    ) -> list[float] | None:
        try:
            return AttributeProfile().preprocess(image, criteria, valuation)
        except GlobalException:
            traceback.print_exc()
        return None

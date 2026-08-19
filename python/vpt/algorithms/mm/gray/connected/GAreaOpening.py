"""Port of ``vpt.algorithms.mm.gray.connected.GAreaOpening``.

Grayscale area opening via 256 binary thresholds: at each level ``t``, the
binary area-opened threshold set is written into the output wherever it's
still True -- since later (higher) thresholds only shrink the set, later
writes at a pixel always win, so each pixel ends up at the highest ``t``
whose area-opened threshold set still contains it. Not the efficient
max-tree route (that's ``GMaxTreeIncrAttrOpening`` with an ``Area``
predicate); transcribed as the Java actually does it.
"""

from __future__ import annotations

import traceback

import numpy as np

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....segmentation.Threshold import Threshold
from ...binary.attribute.BAreaOpening import BAreaOpening

_BYTE_TO_DOUBLE = 0.00392156862745098


class GAreaOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.area: int | None = None
        self.output: Image | None = None
        self.inputFields = "input, area"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(False)
        # Vectorized equivalent of the per-pixel ``setByte`` overwrite below:
        # each threshold's boolean mask is written into a flat byte buffer
        # with plain fancy-index assignment instead of a Python ``for p``
        # loop. Later (higher) ``t`` always wins at a pixel it still covers
        # since the mask-overwrite order is preserved (loop still runs
        # 0..255 in order), matching the scalar semantics exactly.
        out = np.zeros(self.output.size(), dtype=np.int64)
        for t in range(256):
            stack = Threshold.invoke(self.input, _BYTE_TO_DOUBLE * t)

            stack = BAreaOpening.invoke(stack, self.area)
            mask = stack.getBooleanArray()
            out[mask] = t
        self.output.setByteArray(out)

    @staticmethod
    def invoke(image: Image, area: int) -> Image | None:
        try:
            return GAreaOpening().preprocess(image, area)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.gray.path.GPathOpening``.

Grayscale path opening via 256 binary thresholds, exactly the same pattern
as ``GAreaOpening``/``mm.gray.connected``: at each level ``t``, the
binary path-opened threshold set is written into the output wherever it's
still True, so higher thresholds (processed later) overwrite lower ones and
each pixel ends up at the highest surviving threshold.

Not ported: the Java ``main`` (a ``Load``/``Display2D`` demo, out of scope
for this port).
"""

from __future__ import annotations

import traceback

import numpy as np

from .....Algorithm import Algorithm
from .....GlobalException import GlobalException
from .....Image import Image
from ....segmentation.Threshold import Threshold
from ...binary.path.BPathOpening import BPathOpening

_BYTE_TO_DOUBLE = 0.00392156862745098


class GPathOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.length: int | None = None
        self.inputFields = "input,length"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = self.input.newInstance(False)
        self.output.fill(0)
        # Vectorized equivalent of the per-pixel ``setByte`` overwrite below
        # -- see ``GAreaOpening.execute`` for the identical pattern and
        # reasoning (fancy-index mask assignment in threshold order in
        # place of the Python ``for p`` loop, later ``t`` still winning).
        out = np.zeros(self.output.size(), dtype=np.int64)
        for t in range(1, 256):
            stackLevel = Threshold.invoke(self.input, _BYTE_TO_DOUBLE * t)

            stackLevel = BPathOpening.invoke(stackLevel, self.length)
            mask = stackLevel.getBooleanArray()
            out[mask] = t
        self.output.setByteArray(out)

    @staticmethod
    def invoke(image: Image, length: int) -> Image | None:
        try:
            return GPathOpening().preprocess(image, length)
        except GlobalException:
            traceback.print_exc()
        return None

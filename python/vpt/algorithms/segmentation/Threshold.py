"""Port of ``vpt.algorithms.segmentation.Threshold``.

Global threshold through the double view: a pixel is ``True`` when
``pixel >= threshold`` (compared via ``Tools.cmpr`` for the epsilon-aware
equality case).
"""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image


class Threshold(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: BooleanImage | None = None
        self.input: Image | None = None
        self.threshold: float | None = None
        self.inputFields = "input, threshold"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = BooleanImage(self.input, False)
        # Tools.cmpr(pixel, threshold) >= 0 is equivalent to
        # (pixel - threshold) >= -1e-8: within the epsilon band cmpr returns
        # 0 (kept), above it returns 1 (kept), below it returns -1 (dropped).
        # NaN pixels compare False either way (cmpr's else-branch returns -1
        # for a NaN diff since both epsilon-band comparisons are False, and
        # NumPy's `>=` on NaN is also False), so no extra NaN handling needed.
        pixels = self.input.getDoubleArray()
        self.output.setBooleanArray((pixels - self.threshold) >= -1.0e-8)

    @staticmethod
    def invoke(image: Image, threshold: float) -> BooleanImage | None:
        try:
            return Threshold().preprocess(image, threshold)
        except GlobalException:
            traceback.print_exc()
        return None

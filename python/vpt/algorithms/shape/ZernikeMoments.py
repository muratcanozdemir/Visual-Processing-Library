"""Port of ``vpt.algorithms.shape.ZernikeMoments``: a fixed bank of 32
Zernike moments (orders 3 through 10) computed directly on the input
image. ``n`` is a declared field never read in ``execute()`` -- not wired
into ``inputFields`` either -- a dead field in the Java, kept for
structural parity but with no effect."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..statistical.ZernikeMoment import ZernikeMoment


class ZernikeMoments(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.n: int | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        self.output = [0.0] * 32

        self.output[0] = ZernikeMoment.invoke(self.input, 3, 1)
        self.output[1] = ZernikeMoment.invoke(self.input, 3, 3)

        self.output[2] = ZernikeMoment.invoke(self.input, 4, 0)
        self.output[3] = ZernikeMoment.invoke(self.input, 4, 2)
        self.output[4] = ZernikeMoment.invoke(self.input, 4, 4)

        self.output[5] = ZernikeMoment.invoke(self.input, 5, 1)
        self.output[6] = ZernikeMoment.invoke(self.input, 5, 3)
        self.output[7] = ZernikeMoment.invoke(self.input, 5, 5)

        self.output[8] = ZernikeMoment.invoke(self.input, 6, 0)
        self.output[9] = ZernikeMoment.invoke(self.input, 6, 2)
        self.output[10] = ZernikeMoment.invoke(self.input, 6, 4)
        self.output[11] = ZernikeMoment.invoke(self.input, 6, 6)

        self.output[12] = ZernikeMoment.invoke(self.input, 7, 1)
        self.output[13] = ZernikeMoment.invoke(self.input, 7, 3)
        self.output[14] = ZernikeMoment.invoke(self.input, 7, 5)
        self.output[15] = ZernikeMoment.invoke(self.input, 7, 7)

        self.output[16] = ZernikeMoment.invoke(self.input, 8, 0)
        self.output[17] = ZernikeMoment.invoke(self.input, 8, 2)
        self.output[18] = ZernikeMoment.invoke(self.input, 8, 4)
        self.output[19] = ZernikeMoment.invoke(self.input, 8, 6)
        self.output[20] = ZernikeMoment.invoke(self.input, 8, 8)

        self.output[21] = ZernikeMoment.invoke(self.input, 9, 1)
        self.output[22] = ZernikeMoment.invoke(self.input, 9, 3)
        self.output[23] = ZernikeMoment.invoke(self.input, 9, 5)
        self.output[24] = ZernikeMoment.invoke(self.input, 9, 7)
        self.output[25] = ZernikeMoment.invoke(self.input, 9, 9)

        self.output[26] = ZernikeMoment.invoke(self.input, 10, 0)
        self.output[27] = ZernikeMoment.invoke(self.input, 10, 2)
        self.output[28] = ZernikeMoment.invoke(self.input, 10, 4)
        self.output[29] = ZernikeMoment.invoke(self.input, 10, 6)
        self.output[30] = ZernikeMoment.invoke(self.input, 10, 8)
        self.output[31] = ZernikeMoment.invoke(self.input, 10, 10)

    @staticmethod
    def invoke(img: Image) -> list[float] | None:
        try:
            return ZernikeMoments().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

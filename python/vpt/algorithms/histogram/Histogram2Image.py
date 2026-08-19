"""Port of ``vpt.algorithms.histogram.Histogram2Image``: renders a flat
``cdim * 256`` histogram (as produced by ``Histogram``) as a 256x256 bar
chart per channel, ``False`` pixels forming the bars against a ``True``
background.

**Preserved quirk**: if any channel's histogram is entirely zero (``max ==
0.0``), the Java bare-``return``s out of ``execute()`` immediately -- not
just skipping that channel, but abandoning every *later* channel too (their
bars never get drawn, though the whole canvas was already filled ``True``
up front). Transcribed as found via the same early ``return``."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image


class Histogram2Image(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.histo: list[float] | None = None
        self.inputFields = "histo"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = len(self.histo) // 256
        height = 256

        self.output = BooleanImage(256, height, cdim)
        self.output.fill(True)
        for c in range(cdim):
            maximum = 0.0
            for i in range(256):
                if self.histo[c * 256 + i] > maximum:
                    maximum = self.histo[c * 256 + i]

            if maximum == 0.0:
                return

            for x in range(self.output.getXDim()):
                p = height * self.histo[c * 256 + x] / maximum
                y = 0
                while y < height - p:
                    self.output.setXYCBoolean(x, y, c, False)
                    y += 1

    @staticmethod
    def invoke(histo: list[float]) -> Image | None:
        try:
            return Histogram2Image().preprocess(histo)
        except GlobalException:
            traceback.print_exc()
        return None

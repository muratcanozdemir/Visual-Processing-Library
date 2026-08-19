"""Port of ``vpt.algorithms.saliency.SaliencyMapSlow``: the same
Achanta-style multi-scale saliency map as ``SaliencyMap``, but computing
each scale's box average with a brute-force O(window²) sum per pixel
instead of an integral-image table -- the "slow" reference
implementation. Also differs from ``SaliencyMap`` in its final
region-averaging ``ColorQFZSoille`` call: ``(25, 25)`` here vs.
``(50, 50)`` there, confirmed against the Java source, not a
transcription slip."""

from __future__ import annotations

import math
import traceback

from ..._javacompat import idiv, jdiv, jround
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ..conversion.RGB2XYZ import RGB2XYZ
from ..conversion.XYZ2LAB import XYZ2LAB
from ..flatzones.color.ColorQFZSoille import ColorQFZSoille


class SaliencyMapSlow(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        if cdim != 3:
            raise GlobalException("Sorry, only color images are accepted !")

        minDim = min(xdim, ydim)

        R2 = [idiv(minDim, 8), idiv(minDim, 4), idiv(minDim, 2)]

        inputOriginal = self.input.newInstance(True)

        self.input = RGB2XYZ.invoke(self.input)
        self.input = XYZ2LAB.invoke(self.input)
        self.input = XYZ2LAB.scaleToByte(self.input)

        scales: list[Image] = [None] * len(R2)
        for i in range(len(R2)):
            scales[i] = ByteImage(xdim, ydim, 1)
            for x in range(xdim):
                for y in range(ydim):
                    total = [0.0, 0.0, 0.0]
                    counter = 0

                    for _x in range(-1 * idiv(R2[i], 2), idiv(R2[i], 2) + 1):
                        for _y in range(-1 * idiv(R2[i], 2), idiv(R2[i], 2) + 1):
                            if (
                                0 <= x + _x <= xdim - 1
                                and 0 <= y + _y <= ydim - 1
                            ):
                                total[0] += self.input.getXYCDouble(x + _x, y + _y, 0)
                                total[1] += self.input.getXYCDouble(x + _x, y + _y, 1)
                                total[2] += self.input.getXYCDouble(x + _x, y + _y, 2)
                                counter += 1

                    total[0] = jdiv(total[0], counter)
                    total[1] = jdiv(total[1], counter)
                    total[2] = jdiv(total[2], counter)

                    dist = Distance.euclidean(
                        total, self.input.getVXYDouble(x, y)
                    ) / math.sqrt(3.0)
                    scales[i].setXYByte(x, y, jround(dist * 255.0))

        saliencyMap = ByteImage(xdim, ydim, 1)
        for x in range(xdim):
            for y in range(ydim):
                total = 0
                for i in range(len(R2)):
                    total += scales[i].getXYByte(x, y)
                total = idiv(total, len(R2))

                saliencyMap.setXYByte(x, y, total)

        labelImage = ColorQFZSoille.invoke(inputOriginal, 25, 25)

        p = labelImage.getXYCMaximum(0)
        labels = [[0, 0] for _ in range(labelImage.getXYInt(p.x, p.y) + 1)]
        for x in range(xdim):
            for y in range(ydim):
                label = labelImage.getXYInt(x, y)

                labels[label][0] += 1
                labels[label][1] += saliencyMap.getXYByte(x, y)

        for i in range(1, len(labels)):
            labels[i][1] = idiv(labels[i][1], labels[i][0])

        self.output = ByteImage(xdim, ydim, 1)
        for x in range(xdim):
            for y in range(ydim):
                label = labelImage.getXYInt(x, y)
                self.output.setXYByte(x, y, labels[label][1])

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return SaliencyMapSlow().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

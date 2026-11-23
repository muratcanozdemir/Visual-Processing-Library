"""Port of ``vpt.algorithms.shape.AxisOfSymmetry``.

**Genuinely always returns ``None``**: bytecode-confirmed (`javap` shows
exactly one ``putfield`` to a field named ``output``, and it's the
constructor's ``this.outputFields = "output"`` *string* assignment, not
the ``Image output`` field itself -- that field is declared but never
written anywhere in the class). The method still does real work as a side
effect: it searches candidate reflection angles for the one maximizing
left/right symmetry about the centroid, then mutates ``input`` in place
(clearing everything on one side of that best axis) -- just never
publishes the result through ``output``. Reproduced with the same
mutate-and-never-assign shape, not "fixed" to return the mutated image.

Drops the Java's trailing ``Display2D.invoke(input)`` -- a GUI debug
popup with no effect on the (unreturned) result, same category as the
``Display2D`` calls already dropped elsewhere in this port."""

from __future__ import annotations

import traceback

from ..._javacompat import jtan
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools
from ..geometric.ImageCentroid import ImageCentroid


class AxisOfSymmetry(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel images for now.")

        centroid = ImageCentroid.invoke(self.input)

        maxSymmetry = 0.0
        maxSymmetryTheta = 0.0
        for theta in range(1, 179, 3):
            imageDiff = 0.0
            tan = jtan(Tools.degree2radian(theta))
            for x in range(self.input.getXDim()):
                for y in range(self.input.getYDim()):
                    left = self.input.getXYBoolean(x, y)
                    right = False
                    if left:
                        if y - centroid.y - tan * (x - centroid.x) < 0.0:
                            A = 1.0
                            B = -1.0 * tan
                            C = -1 * centroid.y + tan * centroid.x
                            D = -1.0 * B * x + A * y

                            symX = (A * C - B * D) / (A * A + B * B)
                            symY = (B * C - A * D) / (A * A + B * B)

                            symX += x - symX
                            symY += y - symY
                            if (
                                symX < self.input.getXDim()
                                and symX >= 0.0
                                and symY < self.input.getYDim()
                                and symY >= 0.0
                            ):
                                right = self.input.getXYBoolean(int(symX), int(symY))

                                imageDiff += 1 if right else 0

            if imageDiff > maxSymmetry:
                maxSymmetry = imageDiff
                maxSymmetryTheta = theta

        tan = jtan(Tools.degree2radian(maxSymmetryTheta))
        for x in range(self.input.getXDim()):
            for y in range(self.input.getYDim()):
                if y - centroid.y - tan * (x - centroid.x) < 0.0:
                    self.input.setXYBoolean(x, y, False)

    @staticmethod
    def invoke(img: Image) -> Image | None:
        try:
            return AxisOfSymmetry().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

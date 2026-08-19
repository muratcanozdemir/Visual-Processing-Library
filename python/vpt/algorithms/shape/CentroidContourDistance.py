"""Port of ``vpt.algorithms.shape.CentroidContourDistance``.

**Genuinely always raises internally and returns ``None``, verified two
ways**: (1) ``execute()`` never assigns ``output`` (it walks the contour
into a local ``borders`` list -- the same walk as ``FourierDescriptor``'s
-- and then just discards it, same shape as ``AxisOfSymmetry``'s bug);
(2) ``invoke`` calls ``preprocess(img, length)`` with *two* arguments
against ``inputFields = "input"`` (one name) -- ``Algorithm``'s own
parameter-count check (``"Requires N parameters!"``) fires before
``execute()`` even runs, so the contour walk in (1) is unreachable from
any real caller regardless. Both defects reproduced exactly: ``execute()``
keeps its full (dead) body for structural parity, and ``invoke`` keeps the
extra ``length`` parameter it never wires into ``inputFields`` -- calling
it always raises ``GlobalException`` internally (caught, printed, returns
``None``), via this port's own already-faithful
``Algorithm.preprocess`` parameter-count check, the same mechanism as the
Java."""

from __future__ import annotations

import traceback

from ..._javacompat import Point
from ...Algorithm import Algorithm
from ...BooleanImage import BooleanImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Distance import Distance
from ...util.se import FlatSE
from ..geometric.ImageCentroid import ImageCentroid
from ..mm.binary.BInternGradient import BInternGradient


class CentroidContourDistance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        ydim = self.input.getYDim()
        xdim = self.input.getXDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        centroid = ImageCentroid.invoke(self.input)

        borderImage = BInternGradient.invoke(self.input, FlatSE.square(3))

        borders = []

        mask = BooleanImage(self.input, False)
        mask.fill(False)
        for x in range(xdim):
            for y in range(ydim):
                r = borderImage.getXYBoolean(x, y)
                if r:
                    m = mask.getXYBoolean(x, y)
                    if not m:
                        p = Point(x, y)
                        while True:
                            mask.setXYBoolean(p.x, p.y, True)
                            borders.append(
                                Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)
                            )
                            if (
                                p.x + 1 < xdim
                                and borderImage.getXYBoolean(p.x + 1, p.y)
                                and not mask.getXYBoolean(p.x + 1, p.y)
                            ):
                                p.x += 1
                            elif (
                                p.y + 1 < ydim
                                and borderImage.getXYBoolean(p.x, p.y + 1)
                                and not mask.getXYBoolean(p.x, p.y + 1)
                            ):
                                p.y += 1
                            elif (
                                p.x - 1 >= 0
                                and borderImage.getXYBoolean(p.x - 1, p.y)
                                and not mask.getXYBoolean(p.x - 1, p.y)
                            ):
                                p.x -= 1
                            else:
                                if (
                                    p.y - 1 < 0
                                    or not borderImage.getXYBoolean(p.x, p.y - 1)
                                    or mask.getXYBoolean(p.x, p.y - 1)
                                ):
                                    break
                                p.y -= 1

    @staticmethod
    def invoke(img: Image, length: int) -> list[float] | None:
        try:
            return CentroidContourDistance().preprocess(img, length)
        except GlobalException:
            traceback.print_exc()
        return None

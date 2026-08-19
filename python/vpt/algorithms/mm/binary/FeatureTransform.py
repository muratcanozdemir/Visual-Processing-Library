"""Port of ``vpt.algorithms.mm.binary.FeatureTransform``: the dilation dual
of ``DistanceTransform`` -- for each background pixel, the number of dilation
steps (chessboard or cityblock, per ``distance``) before the foreground
reaches it, computed per channel by repeatedly dilating that channel and
stamping the current iteration count onto every pixel the dilation just
added.

HAZARD (original-library defect, not a porting bug), on ``BooleanImage``
input -- the type this whole ``mm.binary`` package exists for: unlike
``DistanceTransform``'s ``isEmpty``, ``isFull(c)`` reads through
``getXYCBoolean`` directly rather than the inverted double view, so it
does *not* hang here. But the loop body's ``Subtraction.invoke(tmp, tmp2)``
does go through the double view, and ``BooleanImage.getDouble`` is
documented as inverted (``True`` -> ``0.0``, ``False`` -> ``1.0``). Since
dilation only ever adds ``True`` pixels, a newly-added pixel reads ``0.0``
in the new ``tmp`` and ``1.0`` in the old ``tmp2``; ``p1 - p2`` clamped to
``[0, 1]`` is then always ``0.0`` there (and everywhere else the two agree),
so ``diff.getXYBoolean(x, y)`` is ``False`` for *every* pixel, every
iteration -- verified directly with a 2-pixel repro and confirmed on a
16x16 synthetic mask: the method runs to completion without error but
**silently stamps nothing at all**, leaving ``output`` entirely ``0``. Same
root cause as ``DistanceTransform``'s hang and the already-documented
``Minimum``/NOR and ``Maximum``/NAND hazards elsewhere in ``mm/binary``:
``BooleanImage``'s inverted ``getDouble`` breaking a generic-double-view
helper. Would behave as intended on a ``ByteImage`` 0/255 mask, where
neither ``getDouble`` nor ``Subtraction`` is inverted (not verified
independently, since the idiomatic call here is with a ``BooleanImage``).

Secondary HAZARD, same mechanism as ``DistanceTransform.py``'s: for a
(hypothetical or ``ByteImage``) multi-channel input, ``tmp.isFull(c)``
re-queries the single-channel (``cdim == 1``) ``tmp = input.getChannel(c)``
with the original multi-channel ``c`` instead of ``0``, running the flat
index ``c + cdim * (x + xdim * y)`` past the end of the backing array for
``c >= 1`` -- verified directly that a ``cdim > 1`` ``ByteImage`` raises
``IndexError``.
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.Subtraction import Subtraction
from .BDilation import BDilation

CHESSBOARD = 0
CITYBLOCK = 1


class FeatureTransform(Algorithm):
    CHESSBOARD = CHESSBOARD
    CITYBLOCK = CITYBLOCK

    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.distance: int | None = None
        self.inputFields = "input,distance"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.distance == CHESSBOARD:
            se = FlatSE.square(3)
        elif self.distance == CITYBLOCK:
            se = FlatSE.cross(3)
        else:
            raise GlobalException("Unsupported distance type, quitting")

        self.output = ByteImage(self.input, False)
        self.output.fill(0)

        cdim = self.output.getCDim()
        xdim = self.output.getXDim()
        ydim = self.output.getYDim()
        for c in range(cdim):
            tmp = self.input.getChannel(c)
            i = 1
            while not tmp.isFull(c):
                tmp2 = tmp

                tmp = BDilation.invoke(tmp, se)

                diff = Subtraction.invoke(tmp, tmp2)
                for x in range(xdim):
                    for y in range(ydim):
                        p = diff.getXYBoolean(x, y)
                        if p:
                            self.output.setXYCByte(x, y, c, i)
                i += 1

    @staticmethod
    def invoke(image: Image, distance: int) -> Image | None:
        try:
            return FeatureTransform().preprocess(image, distance)
        except GlobalException:
            traceback.print_exc()
        return None

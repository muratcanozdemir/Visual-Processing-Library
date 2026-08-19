"""Port of ``vpt.algorithms.mm.binary.DistanceTransform``: for each
foreground pixel, the number of erosion steps (chessboard or cityblock,
per ``distance``) it survives before disappearing -- computed per channel by
repeatedly eroding that channel and stamping the current iteration count
onto every pixel the erosion just removed.

HAZARD (original-library defect, not a porting bug), and the primary one:
on a ``BooleanImage`` -- the type this whole ``mm.binary`` package exists
for -- ``isEmpty(c)`` re-derives "empty" from the generic double view
(``getXYCDouble(x, y, c) > 0.0``), and ``BooleanImage.getDouble`` is
documented as inverted (``True`` -> ``0.0``, ``False`` -> ``1.0``). So
``isEmpty`` actually reports "is every pixel True" instead: verified
directly that it returns ``True`` only for an all-``True`` image and
``False`` the instant *any* pixel is ``False`` -- the opposite of what its
name and this loop need. Since a real segmentation mask always has some
background, ``while not tmp.isEmpty(c)`` never becomes ``False`` no matter
how many times ``tmp`` gets eroded away to nothing, and **this hangs
forever** on its intended input, mirroring ``BFillHole``'s already-documented
hang from the same ``BooleanImage`` double-view inversion. Even setting
that aside, the loop body's own ``Subtraction.invoke(tmp2, tmp)`` is broken
on ``BooleanImage`` too -- see ``FeatureTransform.py``'s docstring, which
hits this same defect without also hanging first, so its effect is directly
observable there.

Both hazards are specific to ``BooleanImage`` input: on a ``ByteImage``
0/255 mask, neither ``getDouble`` nor ``Subtraction`` is inverted, so this
method should behave as intended there (not verified independently, since
the intended/idiomatic call for this package is with a ``BooleanImage``).

Secondary HAZARD, only reachable on a hypothetical multi-channel ``ByteImage``
input (moot for ``BooleanImage``, which never gets past the hang above):
``tmp = input.getChannel(c)`` extracts channel ``c`` into a genuinely
single-channel (``cdim == 1``) image, but ``tmp.isEmpty(c)`` re-queries it
with the *original* multi-channel ``c`` instead of ``0``. Since pixels are
addressed by the flat index ``c + cdim * (x + xdim * y)``, this
reads/misaligns into a neighbouring pixel's data for ``c >= 1``, and once
the scan reaches the image's last few pixels the computed index runs past
the end of the (``xdim * ydim``-sized) backing array entirely -- verified
directly that a ``cdim > 1`` ``ByteImage`` raises ``IndexError`` (the numpy
analogue of Java's ``ArrayIndexOutOfBoundsException``, an unchecked
exception in Java too).
"""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ...arithmetic.Subtraction import Subtraction
from .BErosion import BErosion

CHESSBOARD = 0
CITYBLOCK = 1


class DistanceTransform(Algorithm):
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
        # Vectorized equivalent of the per-pixel stamp loop below: a pure
        # boolean-mask assignment (``output[diff_mask] = i``) with no
        # cross-pixel dependency, mutated in place across iterations and
        # written back once at the end. NOTE: the outer ``while not
        # tmp.isEmpty(c)`` loop above hangs forever on this method's
        # intended ``BooleanImage`` input (see module docstring) -- that is
        # a pre-existing defect in the original library, not touched here;
        # this only vectorizes the inner stamp, which is cheap and correct
        # on its own regardless of how many times the outer loop runs.
        outArr = self.output.getByteArray().reshape(ydim, xdim, cdim)
        for c in range(cdim):
            tmp = self.input.getChannel(c)
            i = 1
            while not tmp.isEmpty(c):
                tmp2 = tmp

                tmp = BErosion.invoke(tmp, se)

                diff = Subtraction.invoke(tmp2, tmp)
                diffMask = diff.getBooleanArray().reshape(ydim, xdim)
                outArr[diffMask, c] = i
                i += 1
        self.output.setByteArray(outArr.reshape(-1))

    @staticmethod
    def invoke(image: Image, distance: int) -> Image | None:
        try:
            return DistanceTransform().preprocess(image, distance)
        except GlobalException:
            traceback.print_exc()
        return None

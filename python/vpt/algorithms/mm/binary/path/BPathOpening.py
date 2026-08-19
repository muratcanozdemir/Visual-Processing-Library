"""Port of ``vpt.algorithms.mm.binary.path.BPathOpening``.

Path opening: keeps a foreground pixel only if it lies on some straight
run of foreground pixels at least ``length`` long, in any of 4 directions
(vertical SN, horizontal WE, and the two diagonals SWNE/NWSE). For each
direction, ``computeUpwardX``/``computeDownwardX`` are a memoized recursion
(memo in ``aux``, an ``IntegerImage`` with one channel per direction) that
computes, for each pixel, the longest run of foreground pixels extending
one way along that axis -- summing the upward and downward run lengths (via
the ``lambda = 1 + max(l1, l2, l3)`` step, which grows the run count
"through" whichever of the 3 forward-diagonal-adjacent neighbours already
has the longest run) plus 1 for the pixel itself gives the total run length
through that pixel. The 4 per-direction images are combined with
elementwise ``Maximum`` and thresholded at ``length``.

The 8 recursive helpers are nearly identical (same shape, different
neighbour offsets and ``aux`` channel/axis) and are transcribed one-to-one
rather than factored, matching the Java source structure exactly for
diffability -- this is dense, easy-to-mistranslate code, where the ability
to line up each Python method against its Java counterpart matters more
than avoiding the repetition.

Recursion depth is bounded by the image's larger dimension (a run can be at
most that long), unlike a decompiler-damage recursion risk -- this is
original structure, not a translation artefact.
"""

from __future__ import annotations

import traceback

from .....Algorithm import Algorithm
from .....ByteImage import ByteImage
from .....GlobalException import GlobalException
from .....Image import Image
from .....IntegerImage import IntegerImage
from ....arithmetic.Maximum import Maximum
from ....segmentation.Threshold import Threshold

_BYTE_TO_DOUBLE = 0.00392156862745098


class BPathOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.length: int | None = None
        self.inputFields = "input,length"
        self.outputFields = "output"

        self._aux: IntegerImage | None = None
        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        outputSN = ByteImage(self.input, False)
        outputSN.fill(0)

        self._aux = IntegerImage(self.xdim, self.ydim, 2)
        self._aux.fill(-1)
        for y in range(self.ydim):
            for x in range(self.xdim):
                if self.input.getXYBoolean(x, y):
                    upward = self._computeUpwardSN(x, y)
                    downward = self._computeDownwardSN(x, y)
                    outputSN.setXYByte(x, y, upward + 1 + downward)

        outputWE = ByteImage(self.input, False)
        outputWE.fill(0)

        self._aux.fill(-1)
        for y in range(self.ydim):
            for x in range(self.xdim):
                if self.input.getXYBoolean(x, y):
                    upward = self._computeUpwardWE(x, y)
                    downward = self._computeDownwardWE(x, y)
                    outputWE.setXYByte(x, y, upward + 1 + downward)

        outputSWNE = ByteImage(self.input, False)
        outputSWNE.fill(0)

        self._aux.fill(-1)
        for y in range(self.ydim):
            for x in range(self.xdim):
                if self.input.getXYBoolean(x, y):
                    upward = self._computeUpwardSWNE(x, y)
                    downward = self._computeDownwardSWNE(x, y)
                    outputSWNE.setXYByte(x, y, upward + 1 + downward)

        outputNWSE = ByteImage(self.input, False)
        outputNWSE.fill(0)

        self._aux.fill(-1)
        for y in range(self.ydim):
            for x in range(self.xdim):
                if self.input.getXYBoolean(x, y):
                    upward = self._computeUpwardNWSE(x, y)
                    downward = self._computeDownwardNWSE(x, y)
                    outputNWSE.setXYByte(x, y, upward + 1 + downward)

        self.output = ByteImage(self.input, False)
        self.output.fill(0)

        self.output = Maximum.invoke(outputSN, outputWE)
        self.output = Maximum.invoke(self.output, outputSWNE)
        self.output = Maximum.invoke(self.output, outputNWSE)

        self.output = Threshold.invoke(self.output, self.length * _BYTE_TO_DOUBLE)

    # ------------------------------------------------------------------
    # South-North (vertical)
    # ------------------------------------------------------------------
    def _computeUpwardSN(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 0, 0)
            return 0

        l1 = self._aux.getXYCInt(x - 1, y + 1, 0) if (x - 1 >= 0 and y + 1 <= self.ydim - 1) else 0
        l2 = self._aux.getXYCInt(x, y + 1, 0) if y + 1 <= self.ydim - 1 else 0
        l3 = (
            self._aux.getXYCInt(x + 1, y + 1, 0)
            if (x + 1 <= self.xdim - 1 and y + 1 <= self.ydim - 1)
            else 0
        )

        if l1 == -1:
            l1 = self._computeUpwardSN(x - 1, y + 1)
        if l2 == -1:
            l2 = self._computeUpwardSN(x, y + 1)
        if l3 == -1:
            l3 = self._computeUpwardSN(x + 1, y + 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 0, lamda)
        return lamda

    def _computeDownwardSN(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 1, 0)
            return 0

        l1 = self._aux.getXYCInt(x - 1, y - 1, 1) if (x - 1 >= 0 and y - 1 >= 0) else 0
        l2 = self._aux.getXYCInt(x, y - 1, 1) if y - 1 >= 0 else 0
        l3 = self._aux.getXYCInt(x + 1, y - 1, 1) if (x + 1 <= self.xdim - 1 and y - 1 >= 0) else 0

        if l1 == -1:
            l1 = self._computeDownwardSN(x - 1, y - 1)
        if l2 == -1:
            l2 = self._computeDownwardSN(x, y - 1)
        if l3 == -1:
            l3 = self._computeDownwardSN(x + 1, y - 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 1, lamda)
        return lamda

    # ------------------------------------------------------------------
    # West-East (horizontal)
    # ------------------------------------------------------------------
    def _computeUpwardWE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 0, 0)
            return 0

        l1 = self._aux.getXYCInt(x - 1, y - 1, 0) if (x - 1 >= 0 and y - 1 >= 0) else 0
        l2 = self._aux.getXYCInt(x - 1, y, 0) if x - 1 >= 0 else 0
        l3 = self._aux.getXYCInt(x - 1, y + 1, 0) if (x - 1 >= 0 and y + 1 <= self.ydim - 1) else 0

        if l1 == -1:
            l1 = self._computeUpwardWE(x - 1, y - 1)
        if l2 == -1:
            l2 = self._computeUpwardWE(x - 1, y)
        if l3 == -1:
            l3 = self._computeUpwardWE(x - 1, y + 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 0, lamda)
        return lamda

    def _computeDownwardWE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 1, 0)
            return 0

        l1 = self._aux.getXYCInt(x + 1, y - 1, 1) if (x + 1 <= self.xdim - 1 and y - 1 >= 0) else 0
        l2 = self._aux.getXYCInt(x + 1, y, 1) if x + 1 <= self.xdim - 1 else 0
        l3 = (
            self._aux.getXYCInt(x + 1, y + 1, 1)
            if (x + 1 <= self.xdim - 1 and y + 1 <= self.ydim - 1)
            else 0
        )

        if l1 == -1:
            l1 = self._computeDownwardWE(x + 1, y - 1)
        if l2 == -1:
            l2 = self._computeDownwardWE(x + 1, y)
        if l3 == -1:
            l3 = self._computeDownwardWE(x + 1, y + 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 1, lamda)
        return lamda

    # ------------------------------------------------------------------
    # SW-NE diagonal
    # ------------------------------------------------------------------
    def _computeUpwardSWNE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 0, 0)
            return 0

        l1 = self._aux.getXYCInt(x, y - 1, 0) if y - 1 >= 0 else 0
        l2 = self._aux.getXYCInt(x + 1, y - 1, 0) if (x + 1 <= self.xdim - 1 and y - 1 >= 0) else 0
        l3 = self._aux.getXYCInt(x + 1, y, 0) if x + 1 <= self.xdim - 1 else 0

        if l1 == -1:
            l1 = self._computeUpwardSWNE(x, y - 1)
        if l2 == -1:
            l2 = self._computeUpwardSWNE(x + 1, y - 1)
        if l3 == -1:
            l3 = self._computeUpwardSWNE(x + 1, y)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 0, lamda)
        return lamda

    def _computeDownwardSWNE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 1, 0)
            return 0

        l1 = self._aux.getXYCInt(x - 1, y, 1) if x - 1 >= 0 else 0
        l2 = self._aux.getXYCInt(x - 1, y + 1, 1) if (x - 1 >= 0 and y + 1 <= self.ydim - 1) else 0
        l3 = self._aux.getXYCInt(x, y + 1, 1) if y + 1 <= self.ydim - 1 else 0

        if l1 == -1:
            l1 = self._computeDownwardSWNE(x - 1, y)
        if l2 == -1:
            l2 = self._computeDownwardSWNE(x - 1, y + 1)
        if l3 == -1:
            l3 = self._computeDownwardSWNE(x, y + 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 1, lamda)
        return lamda

    # ------------------------------------------------------------------
    # NW-SE diagonal
    # ------------------------------------------------------------------
    def _computeUpwardNWSE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 0, 0)
            return 0

        l1 = self._aux.getXYCInt(x, y - 1, 0) if y - 1 >= 0 else 0
        l2 = self._aux.getXYCInt(x - 1, y - 1, 0) if (x - 1 >= 0 and y - 1 >= 0) else 0
        l3 = self._aux.getXYCInt(x - 1, y, 0) if x - 1 >= 0 else 0

        if l1 == -1:
            l1 = self._computeUpwardNWSE(x, y - 1)
        if l2 == -1:
            l2 = self._computeUpwardNWSE(x - 1, y - 1)
        if l3 == -1:
            l3 = self._computeUpwardNWSE(x - 1, y)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 0, lamda)
        return lamda

    def _computeDownwardNWSE(self, x: int, y: int) -> int:
        if not self.input.getXYBoolean(x, y):
            self._aux.setXYCInt(x, y, 1, 0)
            return 0

        l1 = self._aux.getXYCInt(x + 1, y, 1) if x + 1 <= self.xdim - 1 else 0
        l2 = (
            self._aux.getXYCInt(x + 1, y + 1, 1)
            if (x + 1 <= self.xdim - 1 and y + 1 <= self.ydim - 1)
            else 0
        )
        l3 = self._aux.getXYCInt(x, y + 1, 1) if y + 1 <= self.ydim - 1 else 0

        if l1 == -1:
            l1 = self._computeDownwardNWSE(x + 1, y)
        if l2 == -1:
            l2 = self._computeDownwardNWSE(x + 1, y + 1)
        if l3 == -1:
            l3 = self._computeDownwardNWSE(x, y + 1)

        lamda = 1 + max(l1, max(l2, l3))
        self._aux.setXYCInt(x, y, 1, lamda)
        return lamda

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, length: int) -> Image | None:
        try:
            return BPathOpening().preprocess(image, length)
        except GlobalException:
            traceback.print_exc()
        return None

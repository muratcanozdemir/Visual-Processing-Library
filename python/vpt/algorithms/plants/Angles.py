"""Port of ``vpt.algorithms.plants.Angles``: locates four landmark points
on a leaf silhouette (stalk/tip/left/right) via ``PetioleRemoval`` and its
intermediate top-hat "petiole" mask, then reports the interior angles at
three of those landmarks plus a stalk-to-tip/left-to-right aspect ratio.

**Two preserved bugs, both bytecode-confirmed**: `getAngleABC`'s
orientation-sign helper `det` should be the standard 2D cross product
`ab.x*cb.y - ab.y*cb.x`, but `javap` shows both `imul`s read `cb.y` --
`ab.x*cb.y - ab.y*cb.y` (`cb.y*(ab.x - ab.y)`, not a cross product at
all), a real authoring typo confirmed directly in the bytecode's operand
sequence, not decompiler damage. Separately, `output[4]`'s
`(stalk.y - tip.y) / (right.x - left.x)` is Java `int` division
(`javap`: `isub`/`isub`/`idiv`/`i2d`, widening to `double` only after the
truncating division) despite living in a `double[]` -- routed through
``idiv`` to match (which also reproduces Java's `ArithmeticException` if
`left`/`right` ever coincide in `x`). Both reproduced verbatim.

`getAngleABC`'s `cosSqr = dot*dot/abSqr/cbSqr` is genuine Java double
division (bytecode: `ddiv`/`ddiv`), never `int`, but still routed through
``jdiv`` here: `abSqr`/`cbSqr` are squared-vector-lengths that are
legitimately `0.0` if two of the three angle points coincide (a
degenerate silhouette), and Java's total float division doesn't raise
where Python's `/` on two floats does."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import Point, idiv, jacos, jdiv, jfloat
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ..arithmetic.Minimum import Minimum
from ..point.Inversion import Inversion
from .PetioleRemoval import PetioleRemoval


class Angles(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

        inputPR = PetioleRemoval.invoke(self.input)
        petiole = Minimum.invoke(self.input, Inversion.invoke(inputPR))

        stalk = Point(0, 0)
        tip = Point(0, 0)
        left = Point(0, 0)
        right = Point(0, 0)

        # The four landmark scans below are vectorized argmin/argmax over a
        # boolean mask, but each reproduces a Java-loop-order-dependent
        # tie-break, not just "the" extreme point -- see the derivation in
        # IMPROVEMENTS.md 7.14. All four loops are `for x: for y:` (x-outer,
        # y-inner) with a *strict* `<`/`>` update, which collapses to:
        #   - "y < best.y" (stalk, tip): only a column whose own topmost
        #     True pixel beats the running global-min y can ever update, so
        #     the final point is (smallest x among the columns achieving the
        #     global-min y) -- i.e. min-y-then-min-x.
        #   - "x < best.x" (left): best.x only ever shrinks, and x increases
        #     monotonically through the scan, so once any column sets it,
        #     every later (larger-x) column fails the check forever -- only
        #     the *first* column (in x order) with a True pixel ever wins,
        #     at its own topmost True row -- i.e. min-x-then-min-y.
        #   - "x > best.x" (right): symmetric to `left`, but best.x only
        #     ever grows, so *every* column with a True pixel re-wins over
        #     the previous one -- the *last* (largest-x) column with a True
        #     pixel is the final winner, again at its own topmost True row
        #     -- i.e. max-x-then-min-y.
        # Masks are (ydim, xdim) to match the [y][x] storage layout;
        # np.argmax on a boolean array returns the *first* True index,
        # which is exactly the tie-break each case above needs.
        if not petiole.isEmpty(0):
            # The scalar loop's own pre-loop sentinel (x=0, y=600), used
            # verbatim if the mask turns out to have no True pixel at all --
            # `isEmpty(0)` reads the *double* view (channel 0 > 0.0), which
            # only coincides with "some pixel is True" for types where
            # getDouble/getBoolean agree (e.g. ByteImage, the type actually
            # used here in practice); guarding on the boolean mask itself
            # keeps this correct even if that ever isn't the case (e.g.
            # BooleanImage's getDouble is an inverted view).
            stalk.x, stalk.y = 0, 600
            petioleMask = petiole.getBooleanArray().reshape(petiole.getYDim(), petiole.getXDim())
            if petioleMask.any():
                rowsWithTrue = np.any(petioleMask, axis=1)
                yStar = int(np.argmax(rowsWithTrue))
                xStar = int(np.argmax(petioleMask[yStar]))
                stalk.x, stalk.y = xStar, yStar
        else:
            stalk.x = self.input.getXDim() // 2
            stalk.y = self.input.getYDim() - 1

        tip.x = 0
        tip.y = 600

        left.x = 1000
        left.y = 0

        right.x = 0
        right.y = 0

        prMask = inputPR.getBooleanArray().reshape(inputPR.getYDim(), inputPR.getXDim())
        if prMask.any():
            rowsWithTrue = np.any(prMask, axis=1)
            tipY = int(np.argmax(rowsWithTrue))
            tipX = int(np.argmax(prMask[tipY]))
            tip.x, tip.y = tipX, tipY

            colsWithTrue = np.any(prMask, axis=0)
            leftX = int(np.argmax(colsWithTrue))
            leftY = int(np.argmax(prMask[:, leftX]))
            left.x, left.y = leftX, leftY

            # `right.x`'s own sentinel is 0 -- unlike `tip`/`stalk`'s 600 or
            # `left`'s 1000, that's a legitimate column index, not a value
            # far outside the image. Column 0 can *never* pass the scalar
            # loop's strict `x > right.x` check (0 > 0 is always False), so
            # if every True pixel is confined to column 0, `right` must stay
            # at its untouched sentinel (0, 0) -- not (0, that column's
            # topmost True row).
            rightX = len(colsWithTrue) - 1 - int(np.argmax(colsWithTrue[::-1]))
            if rightX > 0:
                rightY = int(np.argmax(prMask[:, rightX]))
                right.x, right.y = rightX, rightY

        self.output = [0.0] * 5

        self.output[0] = abs(self._getAngleABC(left, tip, right))
        self.output[1] = abs(self._getAngleABC(stalk, left, tip))
        self.output[2] = abs(self._getAngleABC(right, stalk, left))
        self.output[3] = abs(self._getAngleABC(tip, right, stalk))
        self.output[4] = float(idiv(stalk.y - tip.y, right.x - left.x))

    @staticmethod
    def _getAngleABC(a: Point, b: Point, c: Point) -> float:
        ab = Point(b.x - a.x, b.y - a.y)
        cb = Point(b.x - c.x, b.y - c.y)

        dot = ab.x * cb.x + ab.y * cb.y

        abSqr = ab.x * ab.x + ab.y * ab.y
        cbSqr = cb.x * cb.x + cb.y * cb.y

        cosSqr = jdiv(jdiv(dot * dot, abSqr), cbSqr)

        cos2 = 2.0 * cosSqr - 1.0

        if cos2 >= 1.0:
            alpha2 = 0.0
        elif cos2 <= -1.0:
            alpha2 = math.pi
        else:
            alpha2 = jacos(cos2)

        rslt = alpha2 / 2.0

        rs = rslt * 180.0 / math.pi
        if dot < 0.0:
            rs = 180.0 - rs

        det = jfloat(ab.x * cb.y - ab.y * cb.y)
        if det < 0.0:
            rs = -rs

        return math.floor(rs + 0.5)

    @staticmethod
    def invoke(img: Image) -> list[float] | None:
        try:
            return Angles().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None

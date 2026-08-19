"""Port of ``vpt.algorithms.color.ColorStructureRGB64``: nominally an
MPEG-7-style "colour structure descriptor" -- for each pixel, slide a
9x9 SE over its neighbourhood and count each *distinct* quantised colour
seen at most once into a 64-bin (4x4x4) histogram, normalised by the
total distinct-colour tally ``syc``.

**Preserved bug, bytecode-confirmed** (`javap -l`: the three
``getXYCByte`` calls inside the neighbour loop all load local slots 5/6
-- mapped by the class file's own `LocalVariableTable` to `x`/`y`, the
*outer* pixel loop variables -- never slots 9/10, which are `_x`/`_y`,
the actual bounds-checked neighbour coordinates): the neighbour offset is
used only to decide *whether* to read a pixel (the bounds check), never
*which* pixel to read -- every read is the fixed centre pixel `(x, y)`
itself. Since the per-pixel `flags` array is fresh each iteration and the
colour index is therefore constant across the whole inner loop, this
collapses to: increment `output[quantised colour of (x,y)]` (and `syc`)
at most once per pixel, precisely when at least one SE offset stays
in-bounds around it -- effectively a plain quantised-colour histogram
gated by "does this pixel have a neighbour", not the colour-structure
descriptor the class name and MPEG-7 lineage suggest. Reproduced
verbatim: the neighbour loop still computes and bounds-checks `_x`/`_y`,
but the pixel read stays `(x, y)`."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE


class ColorStructureRGB64(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: list[float] | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("Why is the input not in color ?")

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        se = FlatSE.square(9)
        coords = se.getCoords()

        # Per the preserved bytecode-confirmed bug documented above, the
        # neighbour read always re-reads the centre pixel and the per-pixel
        # `flags` array is fresh each outer iteration, so this collapses to:
        # increment output[quantised colour of (x, y)] (and syc) at most
        # once per pixel, precisely when at least one SE offset stays
        # in-bounds around it. Compute that in-bounds-for-some-offset mask
        # directly (an OR-reduce over the fixed, small SE coordinate list)
        # rather than assuming it's trivially always true, then a single
        # np.bincount does the rest -- same shift-and-histogram pattern as
        # the already-vectorized Histogram.py (3.2).
        pixels = self.input.getByteArray().reshape(ydim, xdim, cdim)
        q = pixels // 64
        colorIndex = q[:, :, 0] * 16 + q[:, :, 1] * 4 + q[:, :, 2]

        X, Y = np.meshgrid(np.arange(xdim), np.arange(ydim))
        has_neighbor = np.zeros((ydim, xdim), dtype=bool)
        for coord in coords:
            _X = X - coord.x
            _Y = Y - coord.y
            has_neighbor |= (_X >= 0) & (_X < xdim) & (_Y >= 0) & (_Y < ydim)
            if has_neighbor.all():
                break

        counts = np.bincount(colorIndex[has_neighbor], minlength=64).astype(np.float64)
        syc = float(np.count_nonzero(has_neighbor))

        self.output = jdiv_arr(counts, syc).tolist()

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return ColorStructureRGB64().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

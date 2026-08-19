"""Port of ``vpt.algorithms.color.BIC``: the Border/Interior pixel
Classification colour descriptor -- quantises RGB to 4 levels per channel
(64 colours), classifies each pixel as "interior" (all 4-neighbours share
its quantised colour) or "border", and reports two normalised 64-bin
colour histograms (interior first, then border).

``new double['\\u0080']`` in the Java source is the same
JD-Core-renders-an-array-size-int-as-a-char-literal artefact already
documented on ``segmentation.Otsu``'s ``new double['þ']`` -- it's
`new double[128]` (64 interior bins + 64 border bins), not a 1-character
array. ``interiorPixels``/``borderPixels`` are `long` counters that
legitimately could be `0` for a pathological input (e.g. every pixel
classified the same way), so both final normalising loops are routed
through ``jdiv``."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class BIC(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: list[float] | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("BIC operates only on RGB color images")

        # Quantize to a 4x4x4 (64-colour) index, then classify each pixel
        # interior/border by comparing to its 4-neighbours -- out-of-bounds
        # neighbours are *skipped* (not mismatches), reproduced here by
        # seeding each direction's match mask True and only overwriting the
        # rows/columns that do have a neighbour on that side.
        pixels = self.input.getByteArray().reshape(ydim, xdim, cdim)
        q = pixels // 64
        colorIndex = q[:, :, 0] * 16 + q[:, :, 1] * 4 + q[:, :, 2]

        match_up = np.ones((ydim, xdim), dtype=bool)
        match_up[1:, :] = colorIndex[1:, :] == colorIndex[:-1, :]

        match_down = np.ones((ydim, xdim), dtype=bool)
        match_down[:-1, :] = colorIndex[:-1, :] == colorIndex[1:, :]

        match_left = np.ones((ydim, xdim), dtype=bool)
        match_left[:, 1:] = colorIndex[:, 1:] == colorIndex[:, :-1]

        match_right = np.ones((ydim, xdim), dtype=bool)
        match_right[:, :-1] = colorIndex[:, :-1] == colorIndex[:, 1:]

        interior = match_up & match_down & match_left & match_right

        interiorPixels = int(np.count_nonzero(interior))
        borderPixels = int(np.count_nonzero(~interior))

        interior_counts = np.bincount(colorIndex[interior], minlength=64).astype(np.float64)
        border_counts = np.bincount(colorIndex[~interior], minlength=64).astype(np.float64)

        output = np.empty(128, dtype=np.float64)
        output[:64] = jdiv_arr(interior_counts, float(interiorPixels))
        output[64:] = jdiv_arr(border_counts, float(borderPixels))

        self.output = output.tolist()

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return BIC().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

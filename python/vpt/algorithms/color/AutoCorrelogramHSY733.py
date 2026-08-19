"""Port of ``vpt.algorithms.color.AutoCorrelogramHSY733``: an
autocorrelogram over a 63-colour (3x3x7) quantised space, at four
neighbourhood distances (1, 3, 5, 7 -- each via a hollow-square SE ring).
Reads the three channels directly through the byte view and combines
them as ``p0*9 + p1*3 + p2`` into a single 0..62 colour index -- the
input is expected to already be quantised into that 3x3x7 space (e.g. by
``conversion.NonUniformHSY733`` upstream), not raw RGB; this class does
no colour-space conversion itself despite the name."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se import FlatSE


class AutoCorrelogramHSY733(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: list[float] | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        distances = [1, 3, 5, 7]

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        # Combined 3x3x7 colour index (for the output/histogram bin only),
        # one shift-and-compare pass per SE ring coordinate -- same
        # shift-and-histogram pattern as the already-vectorized Histogram.py
        # (3.2). Every match is a plain +1.0 vote, so the accumulation
        # order across coords/pixels doesn't matter (unlike a weighted
        # scatter-add). The colour *match* test itself must stay
        # component-wise (p0==p0' and p1==p1' and p2==p2'), exactly like
        # the scalar loop's `p[0]==p2[0] and p[1]==p2[1] and p[2]==p2[2]`
        # -- comparing the *combined* index instead is not equivalent
        # whenever a channel isn't actually range-restricted to its
        # expected digit width (e.g. p2 >= 3 lets a p1 digit "carry" into
        # it), which would silently manufacture false-positive matches.
        pixels = self.input.getByteArray().reshape(ydim, xdim, 3)
        p0 = pixels[:, :, 0]
        p1 = pixels[:, :, 1]
        p2 = pixels[:, :, 2]
        colorIndex = p0 * 9 + p1 * 3 + p2

        histo = np.bincount(colorIndex.ravel(), minlength=63).astype(np.float64)

        X, Y = np.meshgrid(np.arange(xdim), np.arange(ydim))

        output = np.zeros((len(distances), 63), dtype=np.float64)
        for d, dist in enumerate(distances):
            se = FlatSE.hollowSquare(dist * 2 + 1)
            coords = se.getCoords()

            bin_counts = np.zeros(63, dtype=np.float64)
            for coord in coords:
                _X = X - coord.x
                _Y = Y - coord.y
                valid = (_X >= 0) & (_X < xdim) & (_Y >= 0) & (_Y < ydim)
                _Xc = np.clip(_X, 0, xdim - 1)
                _Yc = np.clip(_Y, 0, ydim - 1)
                match = (
                    valid
                    & (p0[_Yc, _Xc] == p0)
                    & (p1[_Yc, _Xc] == p1)
                    & (p2[_Yc, _Xc] == p2)
                )
                if np.any(match):
                    bin_counts += np.bincount(colorIndex[match], minlength=63)

            mask = histo > 0.0
            divided = np.zeros(63, dtype=np.float64)
            divided[mask] = bin_counts[mask] / (8 * dist * histo[mask])
            output[d] = np.where(mask, divided, bin_counts)

        self.output = output.reshape(-1).tolist()

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return AutoCorrelogramHSY733().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

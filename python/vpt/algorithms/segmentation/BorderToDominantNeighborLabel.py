"""Port of ``vpt.algorithms.segmentation.BorderToDominantNeighborLabel``:
fills every ``0``-labelled (watershed-line) pixel with its first nonzero
8-neighbour, scanning ``labelImage`` (the original) rather than
``outputImage`` (the in-progress result) -- so results never cascade from
one just-filled pixel to the next, and despite the class name this is
"first found", not "dominant" (majority-vote): the scan stops at the first
nonzero neighbour it sees."""

from __future__ import annotations

import traceback

import numpy as np

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class BorderToDominantNeighborLabel(Algorithm):
    def __init__(self):
        super().__init__()
        self.labelImage: Image | None = None
        self.outputImage: Image | None = None
        self.inputFields = "labelImage"
        self.outputFields = "outputImage"

    def execute(self) -> None:
        self.outputImage = self.labelImage.newInstance(True)

        xDim = self.outputImage.getXDim()
        yDim = self.outputImage.getYDim()
        if self.outputImage.getCDim() != 1:
            raise GlobalException("The label image must be in grayscale...")

        # Reads only the untouched labelImage (no cascading). 9-offset
        # shift-and-reduce, scanned in the exact same dx-outer/dy-inner
        # priority order as the nested while loops above (dx=0,dy=0 -- the
        # centre pixel, always 0 here since we only fill where label==0 --
        # is a no-op either way): the first offset whose in-bounds,
        # not-yet-filled candidate is nonzero wins, matching "first found",
        # not "dominant"/majority-vote, per the class docstring.
        labels = self.labelImage.getIntArray().reshape(yDim, xDim)
        found = np.zeros((yDim, xDim), dtype=labels.dtype)
        filled = np.zeros((yDim, xDim), dtype=bool)
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                if dx == 0 and dy == 0:
                    continue
                ys, ye = max(0, -dy), min(yDim, yDim - dy)
                xs, xe = max(0, -dx), min(xDim, xDim - dx)
                if ye <= ys or xe <= xs:
                    continue
                candidate = labels[ys + dy:ye + dy, xs + dx:xe + dx]
                targetFound = found[ys:ye, xs:xe]
                targetFilled = filled[ys:ye, xs:xe]
                take = (~targetFilled) & (candidate != 0)
                targetFound[take] = candidate[take]
                targetFilled |= candidate != 0

        result = np.where(labels == 0, found, labels)
        self.outputImage.setIntArray(result.reshape(-1))

    @staticmethod
    def invoke(labels: Image) -> Image | None:
        try:
            return BorderToDominantNeighborLabel().preprocess(labels)
        except GlobalException:
            traceback.print_exc()
        return None

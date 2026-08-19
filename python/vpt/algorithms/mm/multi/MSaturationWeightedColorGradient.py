"""Port of ``vpt.algorithms.mm.multi.MSaturationWeightedColorGradient``.

A gradient over an HSY-like (0=hue, 1=saturation, 2=luminance) image:
per-neighbour, tracks the running max luminance distance and the running
max hue distance separately, combines them as a saturation-weighted blend
(``lumDist*(1-sat) + hueDist*sat``) at *each* neighbour step, and keeps the
max combined value seen. Note this differs from a "combine the two final
maxima" approach -- the combination happens incrementally inside the loop,
using whatever running maxima have accumulated so far at that point,
exactly as the Java computes it.

Same possible original-source hue/luminance channel-index oddity as
``MHSYMaxGradient`` (``hueDistance(centralHue, ...getXYCDouble(_x,_y,2))``
reads channel 2, not the neighbour's hue channel 0) -- preserved as found.
"""

from __future__ import annotations

import traceback

import numpy as np

from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....util.se import FlatSE
from ....util.Tools import Tools


class MSaturationWeightedColorGradient(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.se: FlatSE | None = None
        self.inputFields = "input,se"
        self.outputFields = "output"

        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        self.output = ByteImage(self.xdim, self.ydim, 1)
        self._executeGeneral()

    def _executeGeneral(self) -> None:
        """Vectorized equivalent of the per-pixel ``_getGradient`` scan
        below. Unlike ``MHSYMaxGradient``, the per-offset step here isn't
        independent: ``combined`` is recomputed at *every* offset from
        whatever ``maxLumDist``/``maxHueDist`` have accumulated so far (not
        just the current offset's own contribution), and ``maxDist`` tracks
        the running max of that. So this keeps three whole-image running
        arrays (mirroring the three scalar accumulators) and processes SE
        offsets in the same order as the loop below, updating each running
        array in-place over just the current offset's valid destination
        region -- an offset outside image bounds for a given pixel leaves
        all three of that pixel's running values untouched, exactly
        matching the scalar ``if 0 <= _x < xdim ...`` guard wrapping the
        entire per-offset body, not just one accumulator update. Every
        comparison uses ``candidate > running`` + ``np.where`` (not
        ``np.maximum``), so a NaN candidate is skipped rather than
        propagated -- see ``GDilation._executeGeneral``'s docstring."""
        inputArr = self.input.getDoubleArray().reshape(self.ydim, self.xdim, self.input.getCDim())
        centralHue = inputArr[:, :, 0]
        centralSaturation = inputArr[:, :, 1]
        centralLuminance = inputArr[:, :, 2]
        neighborLumFull = centralLuminance

        maxLumDist = np.zeros((self.ydim, self.xdim), dtype=np.float64)
        maxHueDist = np.zeros((self.ydim, self.xdim), dtype=np.float64)
        maxDist = np.zeros((self.ydim, self.xdim), dtype=np.float64)

        with np.errstate(over="ignore", invalid="ignore"):
            for p in self.se.getCoords():
                px, py = p.x, p.y
                dy0, dy1 = max(0, py), min(self.ydim, py + self.ydim)
                dx0, dx1 = max(0, px), min(self.xdim, px + self.xdim)
                if dy1 <= dy0 or dx1 <= dx0:
                    continue
                sy0, sy1 = dy0 - py, dy1 - py
                sx0, sx1 = dx0 - px, dx1 - px

                cl = centralLuminance[dy0:dy1, dx0:dx1]
                cs = centralSaturation[dy0:dy1, dx0:dx1]
                ch = centralHue[dy0:dy1, dx0:dx1]
                nl = neighborLumFull[sy0:sy1, sx0:sx1]

                lumCandidate = np.abs(cl - nl)
                mld = maxLumDist[dy0:dy1, dx0:dx1]
                mld = np.where(lumCandidate > mld, lumCandidate, mld)

                a = np.abs(nl - ch)
                hueDistVal = np.where(a <= 0.5, a, 1.0 - a)
                hueCandidate = hueDistVal * 2.0
                mhd = maxHueDist[dy0:dy1, dx0:dx1]
                mhd = np.where(hueCandidate > mhd, hueCandidate, mhd)

                combined = mld * (1.0 - cs) + mhd * cs
                md = maxDist[dy0:dy1, dx0:dx1]
                md = np.where(combined > md, combined, md)

                maxLumDist[dy0:dy1, dx0:dx1] = mld
                maxHueDist[dy0:dy1, dx0:dx1] = mhd
                maxDist[dy0:dy1, dx0:dx1] = md

        self.output.setDoubleArray(maxDist.reshape(-1))

    def _executeGeneralScalar(self) -> None:
        for y in range(self.ydim):
            for x in range(self.xdim):
                self.output.setXYDouble(x, y, self._getGradient(x, y, self.se.getCoords()))

    def _getGradient(self, x: int, y: int, coords) -> float:
        maxLumDist = 0.0
        maxHueDist = 0.0
        maxDist = 0.0

        centralLuminance = self.input.getXYCDouble(x, y, 2)
        centralSaturation = self.input.getXYCDouble(x, y, 1)
        centralHue = self.input.getXYCDouble(x, y, 0)
        for p in coords:
            _x = x - p.x
            _y = y - p.y
            if 0 <= _x < self.xdim and 0 <= _y < self.ydim:
                tmp = abs(centralLuminance - self.input.getXYCDouble(_x, _y, 2))
                if tmp > maxLumDist:
                    maxLumDist = tmp

                tmp = Tools.hueDistance(centralHue, self.input.getXYCDouble(_x, _y, 2)) * 2.0
                if tmp > maxHueDist:
                    maxHueDist = tmp

                combined = maxLumDist * (1.0 - centralSaturation) + maxHueDist * centralSaturation
                if combined > maxDist:
                    maxDist = combined

        return maxDist

    @staticmethod
    def invoke(image: Image, se: FlatSE) -> Image | None:
        try:
            return MSaturationWeightedColorGradient().preprocess(image, se)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.texture.OrientationMap``: a per-pixel dominant
local-gradient-orientation map. Computes a simple centred-difference
gradient (``theta``, ``mod``) over the interior, then for each interior
pixel accumulates a doubled-angle vector sum (``sin(2*(theta+pi))``,
``cos(2*(theta+pi))``, weighted by squared gradient modulus) over a
``maskSize``-square neighbourhood and folds the resulting dominant angle
into ``[0, 1)``.

``self.max``/``self.min`` track the global gradient-modulus extremes while
building ``mod``, exactly as the Java does -- but, also exactly as the
Java does, neither is ever read afterward; ``getDominantOrientation``
doesn't reference them. Kept as dead bookkeeping fields for fidelity, not
because anything depends on them.

Both ``atan2`` calls go through ``_javacompat.jatan2``, not the bare
``math.atan2`` -- this file's output is a raw per-pixel double (not
byte-quantized like most other ``atan2`` call sites in the port), so it's
one of the few places the already-documented fdlibm-vs-glibc 1-ULP
``atan2`` divergence (see ``jatan2``'s docstring) is directly visible;
golden-verified with a small ULP tolerance rather than bit-exact for that
reason."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jatan2, jatan2_arr
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE

_DOUBLE_MAX = 1.7976931348623157e308


class OrientationMap(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.maskSize: int | None = None
        self._xdim: int = 0
        self._ydim: int = 0
        self._max: float = -_DOUBLE_MAX
        self._min: float = _DOUBLE_MAX
        self.inputFields = "input, maskSize"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("The input image must be grayscale")
        if self.maskSize < 3:
            raise GlobalException("Invalid mask size")

        self._xdim = self.input.getXDim()
        self._ydim = self.input.getYDim()

        theta_arr, mod_arr = self._computeGradient()

        theta = DoubleImage(self.input, False)
        theta.setDoubleArray(theta_arr.reshape(-1))

        mod = DoubleImage(self.input, False)
        mod.setDoubleArray(mod_arr.reshape(-1))

        self.output = DoubleImage(self.input, False)
        self.output.fill(0.0)

        se = FlatSE.square(self.maskSize)
        dist = self._computeDominantOrientation(theta_arr, mod_arr, se.getCoords())
        outArr = np.zeros((self._ydim, self._xdim), dtype=np.float64)
        outArr[1:-1, 1:-1] = dist[1:-1, 1:-1]
        self.output.setDoubleArray(outArr.reshape(-1))

    def _computeGradient(self) -> tuple[np.ndarray, np.ndarray]:
        """Vectorized equivalent of the per-pixel scan below: the
        centred-difference gradient at each interior pixel only reads its
        four axis-neighbours, with no dependency between pixels, so it is
        computed for the whole interior in one pass via shifted array
        views. ``self._max``/``self._min`` (dead bookkeeping -- see this
        file's module docstring) are updated with ``np.max``/``np.min``,
        which is safe regardless of visitation order since max/min are
        commutative and associative."""
        inputArr = self.input.getDoubleArray().reshape(self._ydim, self._xdim)

        theta_arr = np.zeros((self._ydim, self._xdim), dtype=np.float64)
        mod_arr = np.zeros((self._ydim, self._xdim), dtype=np.float64)

        if self._xdim > 2 and self._ydim > 2:
            dx = inputArr[1:-1, 0:-2] - inputArr[1:-1, 2:]
            dy = inputArr[0:-2, 1:-1] - inputArr[2:, 1:-1]

            with np.errstate(over="ignore", invalid="ignore"):
                theta_arr[1:-1, 1:-1] = jatan2_arr(dy, dx)
                m = np.sqrt(dx * dx + dy * dy)
            mod_arr[1:-1, 1:-1] = m

            self._max = max(self._max, float(np.max(m)))
            self._min = min(self._min, float(np.min(m)))

        return theta_arr, mod_arr

    def _computeGradientScalar(self) -> tuple[Image, Image]:
        theta = DoubleImage(self.input, False)
        theta.fill(0.0)

        mod = DoubleImage(self.input, False)
        mod.fill(0.0)
        for y in range(1, self._ydim - 1):
            for x in range(1, self._xdim - 1):
                dx = self.input.getXYDouble(x - 1, y) - self.input.getXYDouble(x + 1, y)
                dy = self.input.getXYDouble(x, y - 1) - self.input.getXYDouble(x, y + 1)

                theta.setXYDouble(x, y, jatan2(dy, dx))

                m = math.sqrt(dx * dx + dy * dy)

                mod.setXYDouble(x, y, m)
                if m > self._max:
                    self._max = m
                if m < self._min:
                    self._min = m

        return theta, mod

    def _computeDominantOrientation(
        self, theta_arr: np.ndarray, mod_arr: np.ndarray, coords
    ) -> np.ndarray:
        """Vectorized equivalent of ``_getDominantOrientation`` below. For a
        fixed SE offset, the in-bounds destination region is the same
        exclude-out-of-image-window used by ``GDilation``/``BErosion``'s
        general paths, so accumulating one offset at a time -- in the same
        order ``coords`` is iterated below -- into whole-array running
        totals reproduces the exact left-to-right ``tmp1 += .../tmp2 +=
        ...`` sequence per pixel, just batched across all pixels at once."""
        t = theta_arr + math.pi
        with np.errstate(over="ignore", invalid="ignore"):
            m2 = mod_arr * mod_arr
            contrib1 = m2 * np.sin(t * 2.0)
            contrib2 = m2 * np.cos(t * 2.0)

        tmp1 = np.zeros((self._ydim, self._xdim), dtype=np.float64)
        tmp2 = np.zeros((self._ydim, self._xdim), dtype=np.float64)

        for p in coords:
            px, py = p.x, p.y
            dx0, dx1 = max(0, px), min(self._xdim, px + self._xdim)
            dy0, dy1 = max(0, py), min(self._ydim, py + self._ydim)
            if dx1 <= dx0 or dy1 <= dy0:
                continue
            sx0, sx1 = dx0 - px, dx1 - px
            sy0, sy1 = dy0 - py, dy1 - py
            tmp1[dy0:dy1, dx0:dx1] += contrib1[sy0:sy1, sx0:sx1]
            tmp2[dy0:dy1, dx0:dx1] += contrib2[sy0:sy1, sx0:sx1]

        with np.errstate(over="ignore", invalid="ignore"):
            return (jatan2_arr(tmp1, tmp2) + math.pi) / (2.0 * math.pi)

    def _getDominantOrientation(
        self, theta: Image, modulus: Image, x: int, y: int, coords
    ) -> float:
        tmp1 = 0.0
        tmp2 = 0.0
        for i in range(len(coords)):
            _x = x - coords[i].x
            _y = y - coords[i].y
            if 0 <= _x < self._xdim and 0 <= _y < self._ydim:
                t = theta.getXYDouble(_x, _y) + math.pi

                m = modulus.getXYDouble(_x, _y)

                tmp1 += m * m * math.sin(t * 2.0)
                tmp2 += m * m * math.cos(t * 2.0)

        return (jatan2(tmp1, tmp2) + math.pi) / (2.0 * math.pi)

    @staticmethod
    def invoke(image: Image, maskSize: int) -> Image | None:
        try:
            return OrientationMap().preprocess(image, maskSize)
        except GlobalException:
            traceback.print_exc()
        return None

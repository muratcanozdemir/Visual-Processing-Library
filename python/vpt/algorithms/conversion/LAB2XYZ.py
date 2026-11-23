"""Port of ``vpt.algorithms.conversion.LAB2XYZ``: CIE L*a*b* back to CIE
XYZ, the inverse of ``XYZ2LAB.convert``.

``Math.pow(fy, 3.0)`` (etc.) needs no ``jpow``: Python's ``**`` correctly
stays real for a negative base with an *integer-valued* float exponent
(``(-4.0) ** 3.0 == -64.0``, verified) -- it only misbehaves for a
non-integer exponent, which doesn't occur here."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jpow_arr
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image

_DELTA = 0.2068965517241379
_DELTA_OFFSET = 0.1379310344827586


class LAB2XYZ(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = DoubleImage(self.input, False)

        xdim = self.input.getXDim()
        ydim = self.input.getYDim()
        cdim = self.input.getCDim()
        if cdim != 3:
            raise GlobalException("The input must be a tristumulus LAB image")

        # Elementwise per-channel formula, each branch selected only by that
        # pixel's own value -- vectorizes losslessly. ``x**3.0`` (an
        # integer-valued exponent) stays real for a negative base under
        # numpy too, so no errstate guard is needed (unlike XYZ2LAB's
        # non-integer cube root). The cube still goes through jpow_arr
        # (per-element math.pow) rather than numpy `**`, whose SIMD kernels
        # on AVX-512 CPUs are not bit-identical to Java's Math.pow.
        lab = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        L, A, B = lab[:, :, 0], lab[:, :, 1], lab[:, :, 2]

        Xn, Yn, Zn = 0.950456, 1.0, 1.088754
        delta = _DELTA

        fy = (L + 16.0) / 116.0
        fx = fy + A / 500.0
        fz = fy - B / 200.0

        xyz = np.empty((ydim, xdim, cdim), dtype=np.float64)
        xyz[:, :, 1] = np.where(
            fy > delta, Yn * jpow_arr(fy, 3.0), (fy - _DELTA_OFFSET) * 3.0 * delta * delta * Yn
        )
        xyz[:, :, 0] = np.where(
            fx > delta, Xn * jpow_arr(fx, 3.0), (fx - _DELTA_OFFSET) * 3.0 * delta * delta * Xn
        )
        xyz[:, :, 2] = np.where(
            fz > delta, Zn * jpow_arr(fz, 3.0), (fz - _DELTA_OFFSET) * 3.0 * delta * delta * Zn
        )

        self.output.setDoubleArray(xyz.reshape(-1))

    @staticmethod
    def convert(L: float, A: float, B: float) -> list[float]:
        Xn = 0.950456
        Yn = 1.0
        Zn = 1.088754

        delta = _DELTA

        fy = (L + 16.0) / 116.0
        fx = fy + A / 500.0
        fz = fy - B / 200.0

        xyz = [0.0, 0.0, 0.0]
        if fy > delta:
            xyz[1] = Yn * fy**3.0
        else:
            xyz[1] = (fy - _DELTA_OFFSET) * 3.0 * delta * delta * Yn
        if fx > delta:
            xyz[0] = Xn * fx**3.0
        else:
            xyz[0] = (fx - _DELTA_OFFSET) * 3.0 * delta * delta * Xn
        if fz > delta:
            xyz[2] = Zn * fz**3.0
        else:
            xyz[2] = (fz - _DELTA_OFFSET) * 3.0 * delta * delta * Zn

        return xyz

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return LAB2XYZ().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

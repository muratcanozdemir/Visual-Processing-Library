"""Port of ``vpt.algorithms.conversion.XYZ2LAB``: CIE XYZ to CIE L*a*b*,
plus a ``scaleToByte`` helper other conversions reuse to fit L*a*b* into a
``ByteImage``.

``Math.pow(Xfrac, 0.333333)`` uses a domain-safe base: only reached when
``Xfrac > 0.008856``, so a plain ``**`` is fine here (no ``jpow`` needed --
contrast with ``point.Power``, whose exponent/base are both caller-supplied
and unconstrained)."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jint_arr, jpow_arr, jround_arr
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image

_THRESHOLD = 0.008855999999999999


class XYZ2LAB(Algorithm):
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
            raise GlobalException("The input must be a color image")

        # Elementwise per-channel formula, each branch selected only by that
        # pixel's own value -- vectorizes losslessly. The cube-root branch
        # (a non-integer exponent) is only valid for Xfrac/Yfrac/Zfrac > 0,
        # which the `> _THRESHOLD` guard ensures for the *selected* result,
        # but np.where still evaluates both branches eagerly, so the power
        # is computed under an errstate guard to silence the (discarded)
        # invalid-value warning from the not-taken branch's <=0 inputs.
        # The power goes through jpow_arr (per-element math.pow), not numpy
        # `**`: np.power is not bit-identical to libm/Java Math.pow, and on
        # AVX-512 CPUs numpy dispatches to SIMD kernels that drift by several
        # ulps -- which broke the bit-exact golden test on some CI runners.
        xyz = self.input.getDoubleArray().reshape(ydim, xdim, cdim)
        x_, y_, z_ = xyz[:, :, 0], xyz[:, :, 1], xyz[:, :, 2]

        Xn, Yn, Zn = 0.95047, 1.0, 1.08883
        Xfrac, Yfrac, Zfrac = x_ / Xn, y_ / Yn, z_ / Zn

        with np.errstate(invalid="ignore"):
            Xfrac = np.where(
                Xfrac > _THRESHOLD, jpow_arr(Xfrac, 0.333333), (903.29999999999995 * Xfrac + 16.0) / 116.0
            )
            Yfrac = np.where(
                Yfrac > _THRESHOLD, jpow_arr(Yfrac, 0.333333), (903.29999999999995 * Yfrac + 16.0) / 116.0
            )
            Zfrac = np.where(
                Zfrac > _THRESHOLD, jpow_arr(Zfrac, 0.333333), (903.29999999999995 * Zfrac + 16.0) / 116.0
            )

        lab = np.empty((ydim, xdim, cdim), dtype=np.float64)
        lab[:, :, 0] = 116.0 * Yfrac - 16.0
        lab[:, :, 1] = 500.0 * (Xfrac - Yfrac)
        lab[:, :, 2] = 200.0 * (Yfrac - Zfrac)

        self.output.setDoubleArray(lab.reshape(-1))

    @staticmethod
    def convert(x: float, y: float, z: float) -> list[float]:
        Xn = 0.95047
        Yn = 1.0
        Zn = 1.08883

        Xfrac = x / Xn
        Yfrac = y / Yn
        Zfrac = z / Zn
        if Xfrac > _THRESHOLD:
            Xfrac = Xfrac**0.333333
        else:
            Xfrac = (903.29999999999995 * Xfrac + 16.0) / 116.0
        if Yfrac > _THRESHOLD:
            Yfrac = Yfrac**0.333333
        else:
            Yfrac = (903.29999999999995 * Yfrac + 16.0) / 116.0
        if Zfrac > _THRESHOLD:
            Zfrac = Zfrac**0.333333
        else:
            Zfrac = (903.29999999999995 * Zfrac + 16.0) / 116.0

        return [
            116.0 * Yfrac - 16.0,
            500.0 * (Xfrac - Yfrac),
            200.0 * (Yfrac - Zfrac),
        ]

    @staticmethod
    def scaleToByte(lab: Image) -> Image:
        bimg = ByteImage(lab, False)

        xdim, ydim, cdim = lab.getXDim(), lab.getYDim(), lab.getCDim()
        doubles = lab.getDoubleArray().reshape(ydim, xdim, cdim)

        out = np.empty((ydim, xdim, cdim), dtype=np.int64)
        out[:, :, 0] = jint_arr(jround_arr(doubles[:, :, 0] * 2.55))
        out[:, :, 1] = jint_arr(jround_arr(doubles[:, :, 1] + 128.0))
        out[:, :, 2] = jint_arr(jround_arr(doubles[:, :, 2] + 128.0))

        bimg.setByteArray(out.reshape(-1))
        return bimg

    @staticmethod
    def invoke(image: Image) -> Image | None:
        try:
            return XYZ2LAB().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

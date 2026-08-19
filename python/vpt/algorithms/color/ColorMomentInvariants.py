"""Port of ``vpt.algorithms.color.ColorMomentInvariants``: 20
colour-moment invariants built from the same joint spatial/colour
``moment(p, q, r, g, b)`` as ``ColorMoments`` (an independently-duplicated
private helper, byte-for-byte identical to that file's -- ported as its
own copy, not shared, per this port's convention for duplicated Java
code). Every division here is routed through ``jdiv``: unlike
``ColorMoments``'s single ``avg`` normaliser (provably `xdim*ydim`,
non-zero for any real image), these denominators are raw colour moments
that legitimately hit `0.0` for an unremarkable input -- e.g. `m00100`
(the mean red channel) is exactly `0` for any image with a completely
black red channel."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv, jpow
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


def _powArr(a: np.ndarray, n: int) -> np.ndarray:
    """``jpow(a, n)`` for ``n`` in ``{0, 1, 2}``, vectorized -- byte-for-byte
    duplicated from ``ColorMoments._powArr``, per this port's convention for
    duplicated Java code (see this file's module docstring). See
    ``ColorMoments._moment``'s docstring for the bit-exactness argument."""
    if n == 0:
        return np.ones_like(a)
    if n == 1:
        return a
    if n == 2:
        return a * a
    raise ValueError(f"_powArr only supports exponents 0, 1, 2; got {n}")


class ColorMomentInvariants(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: list[float] | None = None
        self.xdim = 0
        self.ydim = 0
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if cdim != 3:
            raise GlobalException("Why is the input not in color ?")

        tmp = DoubleImage(self.input, True)

        m00000 = self._moment(0, 0, tmp, 0, 0, 0)

        m00100 = self._moment(0, 0, tmp, 1, 0, 0)
        m00010 = self._moment(0, 0, tmp, 0, 1, 0)
        m00001 = self._moment(0, 0, tmp, 0, 0, 1)
        m00200 = self._moment(0, 0, tmp, 2, 0, 0)
        m00020 = self._moment(0, 0, tmp, 0, 2, 0)
        m00002 = self._moment(0, 0, tmp, 0, 0, 2)
        m00110 = self._moment(0, 0, tmp, 1, 1, 0)
        m00101 = self._moment(0, 0, tmp, 1, 0, 1)
        m00011 = self._moment(0, 0, tmp, 0, 1, 1)

        m01100 = self._moment(0, 1, tmp, 1, 0, 0)
        m01010 = self._moment(0, 1, tmp, 0, 1, 0)
        m01001 = self._moment(0, 1, tmp, 0, 0, 1)
        m01200 = self._moment(0, 1, tmp, 2, 0, 0)
        m01020 = self._moment(0, 1, tmp, 0, 2, 0)
        m01002 = self._moment(0, 1, tmp, 0, 0, 2)
        m01110 = self._moment(0, 1, tmp, 1, 1, 0)
        m01101 = self._moment(0, 1, tmp, 1, 0, 1)
        m01011 = self._moment(0, 1, tmp, 0, 1, 1)

        m10100 = self._moment(1, 0, tmp, 1, 0, 0)
        m10010 = self._moment(1, 0, tmp, 0, 1, 0)
        m10001 = self._moment(1, 0, tmp, 0, 0, 1)
        m10200 = self._moment(1, 0, tmp, 2, 0, 0)
        m10020 = self._moment(1, 0, tmp, 0, 2, 0)
        m10002 = self._moment(1, 0, tmp, 0, 0, 2)
        m10110 = self._moment(1, 0, tmp, 1, 1, 0)
        m10101 = self._moment(1, 0, tmp, 1, 0, 1)
        m10011 = self._moment(1, 0, tmp, 0, 1, 1)

        m01000 = self._moment(0, 1, tmp, 0, 0, 0)
        m10000 = self._moment(1, 0, tmp, 0, 0, 0)

        self.output = [0.0] * 20

        self.output[0] = jdiv(m00200 * m00000, m00100 * m00100)
        self.output[1] = jdiv(m00020 * m00000, m00010 * m00010)
        self.output[2] = jdiv(m00002 * m00000, m00001 * m00001)
        self.output[3] = jdiv(m00110 * m00000, m00100 * m00010)
        self.output[4] = jdiv(m00101 * m00000, m00100 * m00001)
        self.output[5] = jdiv(m00011 * m00000, m00010 * m00001)

        self.output[6] = jdiv(
            m10100 * m01010 * m00000 + m10010 * m01000 * m00100 + m10000 * m01100 * m00010,
            m00100 * m00010 * m00000,
        ) - jdiv(
            m10100 * m01000 * m00010 + m10010 * m01100 * m00000 + m10000 * m01010 * m00100,
            m00100 * m00010 * m00000,
        )

        self.output[7] = jdiv(
            m10100 * m01001 * m00000 + m10001 * m01000 * m00100 + m10000 * m01100 * m00001,
            m00100 * m00001 * m00000,
        ) - jdiv(
            m10100 * m01000 * m00001 + m10001 * m01100 * m00000 + m10000 * m01001 * m00100,
            m00100 * m00001 * m00000,
        )

        self.output[8] = jdiv(
            m10010 * m01001 * m00000 + m10001 * m01000 * m00010 + m10000 * m01010 * m00001,
            m00010 * m00001 * m00000,
        ) - jdiv(
            m10010 * m01000 * m00001 + m10001 * m01010 * m00000 + m10000 * m01001 * m00010,
            m00010 * m00001 * m00000,
        )

        self.output[9] = jdiv(
            m10200 * m01000 * m00100 + m10100 * m01200 * m00000 + m10000 * m01100 * m00200,
            m00200 * m00100 * m00000,
        ) - jdiv(
            m10200 * m01100 * m00000 + m10100 * m01000 * m00200 + m10000 * m01200 * m00100,
            m00200 * m00100 * m00000,
        )

        self.output[10] = jdiv(
            m10020 * m01000 * m00010 + m10010 * m01020 * m00000 + m10000 * m01010 * m00020,
            m00020 * m00010 * m00000,
        ) - jdiv(
            m10020 * m01010 * m00000 + m10010 * m01000 * m00020 + m10000 * m01020 * m00010,
            m00020 * m00010 * m00000,
        )

        self.output[11] = jdiv(
            m10002 * m01000 * m00001 + m10001 * m01002 * m00000 + m10000 * m01001 * m00002,
            m00002 * m00001 * m00000,
        ) - jdiv(
            m10002 * m01001 * m00000 + m10001 * m01000 * m00002 + m10000 * m01002 * m00001,
            m00002 * m00001 * m00000,
        )

        self.output[12] = jdiv(
            m10110 * m01000 * m00100 + m10100 * m01110 * m00000 + m10000 * m01100 * m00110,
            m00110 * m00100 * m00000,
        ) - jdiv(
            m10110 * m01100 * m00000 + m10100 * m01000 * m00110 + m10000 * m01110 * m00100,
            m00110 * m00100 * m00000,
        )

        self.output[13] = jdiv(
            m10101 * m01000 * m00100 + m10100 * m01101 * m00000 + m10000 * m01100 * m00101,
            m00101 * m00100 * m00000,
        ) - jdiv(
            m10101 * m01100 * m00000 + m10100 * m01000 * m00101 + m10000 * m01101 * m00100,
            m00101 * m00100 * m00000,
        )

        self.output[14] = jdiv(
            m10011 * m01000 * m00010 + m10010 * m01011 * m00000 + m10000 * m01010 * m00011,
            m00011 * m00010 * m00000,
        ) - jdiv(
            m10011 * m01010 * m00000 + m10010 * m01000 * m00011 + m10000 * m01011 * m00010,
            m00011 * m00010 * m00000,
        )

        self.output[15] = jdiv(
            m10110 * m01000 * m00010 + m10010 * m01110 * m00000 + m10000 * m01010 * m00110,
            m00110 * m00010 * m00000,
        ) - jdiv(
            m10110 * m01010 * m00000 + m10010 * m01000 * m00110 + m10000 * m01110 * m00010,
            m00110 * m00010 * m00000,
        )

        self.output[16] = jdiv(
            m10101 * m01000 * m00001 + m10001 * m01101 * m00000 + m10000 * m01001 * m00101,
            m00101 * m00001 * m00000,
        ) - jdiv(
            m10101 * m01001 * m00000 + m10001 * m01000 * m00101 + m10000 * m01101 * m00001,
            m00101 * m00001 * m00000,
        )

        self.output[17] = jdiv(
            m10011 * m01000 * m00001 + m10001 * m01011 * m00000 + m10000 * m01001 * m00011,
            m00011 * m00001 * m00000,
        ) - jdiv(
            m10011 * m01001 * m00000 + m10001 * m01000 * m00011 + m10000 * m01011 * m00001,
            m00011 * m00001 * m00000,
        )

        self.output[18] = jdiv(
            m10020 * m01000 * m00100 + m10100 * m01020 * m00000 + m10000 * m01100 * m00020,
            m00020 * m00100 * m00000,
        ) - jdiv(
            m10020 * m01100 * m00000 + m10100 * m01000 * m00020 + m10000 * m01020 * m00100,
            m00020 * m00100 * m00000,
        )

        self.output[19] = jdiv(
            m10002 * m01000 * m00010 + m10010 * m01002 * m00000 + m10000 * m01010 * m00002,
            m00002 * m00010 * m00000,
        ) - jdiv(
            m10002 * m01010 * m00000 + m10010 * m01000 * m00002 + m10000 * m01002 * m00010,
            m00002 * m00010 * m00000,
        )

    def _moment(self, p: int, q: int, tmp: Image, r: int, g: int, b: int) -> float:
        """Vectorized equivalent of the per-pixel scan below -- see
        ``ColorMoments._moment``'s docstring for the full bit-exactness
        argument (identical here: ``p``/``q`` are always 0 or 1 and
        ``r``/``g``/``b`` always 0, 1 or 2 at every call site in
        ``execute``)."""
        arr = tmp.getDoubleArray().reshape(self.ydim, self.xdim, 3).transpose(1, 0, 2)
        red, green, blue = arr[..., 0], arr[..., 1], arr[..., 2]

        x = np.arange(self.xdim, dtype=np.float64).reshape(-1, 1)
        y = np.arange(self.ydim, dtype=np.float64).reshape(1, -1)

        with np.errstate(over="ignore", invalid="ignore"):
            term = (
                _powArr(x + 1, p)
                * _powArr(y + 1, q)
                * _powArr(red, r)
                * _powArr(green, g)
                * _powArr(blue, b)
            )

            flat = term.reshape(-1)
            return float(np.cumsum(flat)[-1]) if flat.size else 0.0

    def _momentScalar(self, p: int, q: int, tmp: Image, r: int, g: int, b: int) -> float:
        moment = 0.0
        for x in range(self.xdim):
            for y in range(self.ydim):
                red = tmp.getXYCDouble(x, y, 0)
                green = tmp.getXYCDouble(x, y, 1)
                blue = tmp.getXYCDouble(x, y, 2)

                moment += (
                    jpow(x + 1, p)
                    * jpow(y + 1, q)
                    * jpow(red, r)
                    * jpow(green, g)
                    * jpow(blue, b)
                )
        return moment

    @staticmethod
    def invoke(image: Image) -> list[float] | None:
        try:
            return ColorMomentInvariants().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

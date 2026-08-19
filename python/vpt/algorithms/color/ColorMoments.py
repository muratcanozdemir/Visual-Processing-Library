"""Port of ``vpt.algorithms.color.ColorMoments``: 27 joint spatial/colour
moments (`moment(p, q, r, g, b) = sum((x+1)^p * (y+1)^q * red^r * green^g
* blue^b)` over every pixel), each normalised by the plain intensity sum
``avg = moment(0,0,0,0,0)``. ``avg`` is provably ``xdim*ydim`` (every
term collapses to `1`), so it can only be zero for a zero-sized image;
routed through ``jdiv`` anyway for the same total-double-division parity
every other normalising division in this port gets."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv, jpow
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


def _powArr(a: np.ndarray, n: int) -> np.ndarray:
    """``jpow(a, n)`` for ``n`` in ``{0, 1, 2}``, vectorized -- see
    ``ColorMoments._moment``'s docstring for the bit-exactness argument."""
    if n == 0:
        return np.ones_like(a)
    if n == 1:
        return a
    if n == 2:
        return a * a
    raise ValueError(f"_powArr only supports exponents 0, 1, 2; got {n}")


class ColorMoments(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: list[float] | None = None
        self.inputFields = "input"
        self.outputFields = "output"
        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        cdim = self.input.getCDim()
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        if cdim != 3:
            raise GlobalException("Why is the input not in color ?")

        tmp = DoubleImage(self.input, True)

        self.output = [0.0] * 27

        avg = self._moment(0, 0, tmp, 0, 0, 0)

        self.output[0] = jdiv(self._moment(0, 0, tmp, 1, 0, 0), avg)
        self.output[1] = jdiv(self._moment(0, 0, tmp, 0, 1, 0), avg)
        self.output[2] = jdiv(self._moment(0, 0, tmp, 0, 0, 1), avg)
        self.output[3] = jdiv(self._moment(0, 0, tmp, 2, 0, 0), avg)
        self.output[4] = jdiv(self._moment(0, 0, tmp, 0, 2, 0), avg)
        self.output[5] = jdiv(self._moment(0, 0, tmp, 0, 0, 2), avg)
        self.output[6] = jdiv(self._moment(0, 0, tmp, 1, 1, 0), avg)
        self.output[7] = jdiv(self._moment(0, 0, tmp, 1, 0, 1), avg)
        self.output[8] = jdiv(self._moment(0, 0, tmp, 0, 1, 1), avg)

        self.output[9] = jdiv(self._moment(0, 1, tmp, 1, 0, 0), avg)
        self.output[10] = jdiv(self._moment(0, 1, tmp, 0, 1, 0), avg)
        self.output[11] = jdiv(self._moment(0, 1, tmp, 0, 0, 1), avg)
        self.output[12] = jdiv(self._moment(0, 1, tmp, 2, 0, 0), avg)
        self.output[13] = jdiv(self._moment(0, 1, tmp, 0, 2, 0), avg)
        self.output[14] = jdiv(self._moment(0, 1, tmp, 0, 0, 2), avg)
        self.output[15] = jdiv(self._moment(0, 1, tmp, 1, 1, 0), avg)
        self.output[16] = jdiv(self._moment(0, 1, tmp, 1, 0, 1), avg)
        self.output[17] = jdiv(self._moment(0, 1, tmp, 0, 1, 1), avg)

        self.output[18] = jdiv(self._moment(1, 0, tmp, 1, 0, 0), avg)
        self.output[19] = jdiv(self._moment(1, 0, tmp, 0, 1, 0), avg)
        self.output[20] = jdiv(self._moment(1, 0, tmp, 0, 0, 1), avg)
        self.output[21] = jdiv(self._moment(1, 0, tmp, 2, 0, 0), avg)
        self.output[22] = jdiv(self._moment(1, 0, tmp, 0, 2, 0), avg)
        self.output[23] = jdiv(self._moment(1, 0, tmp, 0, 0, 2), avg)
        self.output[24] = jdiv(self._moment(1, 0, tmp, 1, 1, 0), avg)
        self.output[25] = jdiv(self._moment(1, 0, tmp, 1, 0, 1), avg)
        self.output[26] = jdiv(self._moment(1, 0, tmp, 0, 1, 1), avg)

    def _moment(self, p: int, q: int, tmp: Image, r: int, g: int, b: int) -> float:
        """Vectorized equivalent of the per-pixel scan below. ``p``/``q`` are
        always 0 or 1 and ``r``/``g``/``b`` always 0, 1 or 2 at every call
        site in ``execute`` -- for exactly those exponents, ``jpow(a, n)`` is
        bit-exact to ``1.0`` / ``a`` / ``a*a`` for *every* double ``a``,
        including NaN, +-Infinity, +-0.0 and overflowing magnitudes (verified
        by exhaustive comparison against ``jpow`` before use; ``jpow``'s own
        special-casing for those inputs only changes behaviour for exponents
        this file never passes, e.g. negative or infinite ones). So each
        factor is built with :func:`_powArr` instead of a per-element
        ``jpow`` call. The five factors are then multiplied left-to-right,
        matching the Java expression's evaluation order (float multiplication
        isn't associative, but broadcasting an elementwise ``a * b`` is the
        same pairwise operation the scalar loop performs, just batched).
        Finally, ``np.cumsum`` -- genuinely sequential, unlike ``np.sum``'s
        pairwise reduction -- accumulates the flattened terms in the same
        x-outer/y-inner order the nested loop below uses, reproducing the
        exact left-to-right double summation."""
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
            return ColorMoments().preprocess(image)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.statistical.ZernikeMoment``: the magnitude of a
single (n, m) Zernike moment of a mono-channel image over its inscribed
unit disc.

``i_0``/``j_0``/``radius`` (``xdim / 2`` etc.) are Java ``int / int``
divisions widened to ``double`` afterwards, not true halves -- routed
through ``idiv`` to match (an odd ``xdim`` truncates down, same shape as
the already-documented ``OSR``/``MP`` integer-division findings, though
here it's a deliberate "centre pixel" computation rather than an
unintended ratio bug).

``zer_pol_R``'s incremental factorial-ratio trick (``a``, ``b``, ``c``,
``d``) uses Java ``int`` locals that can genuinely overflow for larger
``n`` (``a`` reaches ``n!``, unbounded by any validation on ``n`` here) --
Java wraps silently on overflow rather than raising, so every multiply
into these four variables is routed through ``int32`` to reproduce that
wraparound rather than Python's arbitrary-precision ``int`` silently
"fixing" it into a different (correct, but non-Java) value."""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import idiv, int32, jatan2_arr, jcos_arr, jdiv, jpow_arr, jsin_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.ComplexNumber import ComplexNumber


class ZernikeMoment(Algorithm):
    def __init__(self):
        super().__init__()
        self.moment: float | None = None
        self.input: Image | None = None
        self.m: int | None = None
        self.n: int | None = None
        self.xdim = 0
        self.ydim = 0
        self.inputFields = "input, n, m"
        self.outputFields = "moment"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Only grayscale images please!")
        if (self.n - abs(self.m)) % 2 != 0 or abs(self.m) > self.n:
            raise GlobalException("Invalid n, m values!")
        self.m = abs(self.m)

        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()

        diff = self.n - abs(self.m)
        if self.n < 0 or abs(self.m) > self.n or diff % 2 != 0:
            raise ValueError(f"zer_mom: n = {self.n}, m = {self.m}, n - |m| = {diff}")

        i_0 = float(idiv(self.xdim, 2))
        j_0 = float(idiv(self.ydim, 2))

        radius = float(idiv(self.xdim, 2))
        i_scale = math.sqrt(2.0) * radius

        radius = float(idiv(self.ydim, 2))
        j_scale = math.sqrt(2.0) * radius

        # Vectorized, bit-identical to the Java per-pixel loop:
        # - the per-s radial coefficients don't depend on the pixel, so they
        #   are computed once, with the same int32-wrapping scalar code;
        # - pow/atan2/cos/sin go through the libm-backed *_arr helpers, which
        #   give each pixel exactly the scalar math.* result (np.power and the
        #   other ufuncs would not -- see _javacompat._libm_map);
        # - the pixel sums use np.cumsum, which adds strictly left to right in
        #   the loop's y-outer/x-inner order (np.sum's pairwise summation
        #   would round differently). A leading 0.0 reproduces the loop's
        #   `0.0 + first` (which turns a lone -0.0 into 0.0).
        _y, _x = np.mgrid[0 : self.ydim, 0 : self.xdim]
        # A 1-pixel-wide or -tall image makes i_scale/j_scale 0.0: Java's
        # double division gives NaN/Infinity there (no pixel is inside the
        # disc, so the moment comes out NaN), which numpy matches -- only
        # its warnings need silencing. (The old scalar port raised
        # ZeroDivisionError instead.)
        with np.errstate(divide="ignore", invalid="ignore"):
            x = (_x - i_0) / i_scale
            y = (_y - j_0) / j_scale
            r2 = x * x + y * y
            inside = r2 <= 1.0
        x, y, r2 = x[inside], y[inside], r2[inside]
        p = self.input.getDoubleArray().reshape(self.ydim, self.xdim)[inside]

        r = np.zeros_like(r2)
        for coef, exponent in self._zer_pol_R_terms(self.n, self.m):
            r = r + coef * jpow_arr(r2, exponent)
        arg = self.m * jatan2_arr(y, x)
        v_real = r * jcos_arr(arg)
        v_imag = r * jsin_arr(arg)

        res = ComplexNumber()
        res.real = _sequential_sum(p * v_real)
        res.imag = _sequential_sum(p * v_imag)
        lambdaN = _sequential_sum(p)

        res.real = jdiv(res.real * (self.n + 1), lambdaN)
        res.imag = jdiv(res.imag * (self.n + 1), lambdaN)

        self.moment = res.magnitude()

    @staticmethod
    def _zer_pol_R_terms(n: int, m_in: int) -> list[tuple[float, float]]:
        """The Java ``zer_pol_R``'s terms as ``(coefficient, exponent)``
        pairs: ``R(r2) = sum(coef * r2 ** exponent)``, summed in this order.
        ``coef`` is ``sign * (a * 1.0 / (b * c * d))`` with the Java ``int``
        wraparound preserved -- the same value the per-pixel loop computed,
        evaluated left to right the same way."""
        m = abs(m_in)
        if (n - m) % 2 != 0:
            raise ValueError("zer_pol_R: n - |m| is odd")
        terms = []
        sign = 1
        a = 1
        for i in range(2, n + 1):
            a = int32(a * i)

        b = 1
        c = 1
        for i in range(2, idiv(n + m, 2) + 1):
            c = int32(c * i)

        d = 1
        for i in range(2, idiv(n - m, 2) + 1):
            d = int32(d * i)

        for s in range(idiv(n - m, 2) + 1):
            terms.append((sign * jdiv(a * 1.0, int32(int32(b * c) * d)), n / 2.0 - s))
            if s < idiv(n - m, 2):
                sign *= -1
                a = int32(idiv(a, n - s))
                b = int32(b * (s + 1))
                c = int32(idiv(c, idiv(n + m, 2) - s))
                d = int32(idiv(d, idiv(n - m, 2) - s))
        return terms

    @staticmethod
    def invoke(input: Image, n: int, m: int) -> float | None:
        try:
            return ZernikeMoment().preprocess(input, n, m)
        except GlobalException:
            traceback.print_exc()
        return None


def _sequential_sum(a: np.ndarray) -> float:
    """``total = 0.0; for v in a: total += v`` -- same rounding, in C."""
    return float(np.cumsum(np.concatenate(([0.0], a)))[-1])

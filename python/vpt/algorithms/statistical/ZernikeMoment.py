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

from ..._javacompat import idiv, int32, jatan2, jdiv
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

        res = ComplexNumber()
        lambdaN = 0.0
        for _y in range(self.ydim):
            for _x in range(self.xdim):
                x = (_x - i_0) / i_scale
                y = (_y - j_0) / j_scale

                p = self.input.getXYDouble(_x, _y)
                if x * x + y * y <= 1.0:
                    v = self._zer_pol(self.n, self.m, x, y)
                    res.real += p * v.real
                    res.imag += p * v.imag
                    lambdaN += p

        res.real = jdiv(res.real * (self.n + 1), lambdaN)
        res.imag = jdiv(res.imag * (self.n + 1), lambdaN)

        self.moment = res.magnitude()

    def _zer_pol(self, n: int, m: int, x: float, y: float) -> ComplexNumber:
        if x * x + y * y > 1.0:
            return ComplexNumber(0.0, 0.0)

        r = self._zer_pol_R(n, m, x, y)

        arg = m * jatan2(y, x)
        real = r * math.cos(arg)
        imag = r * math.sin(arg)

        return ComplexNumber(real, imag)

    def _zer_pol_R(self, n: int, m_in: int, x: float, y: float) -> float:
        m = abs(m_in)
        if (n - m) % 2 != 0:
            raise ValueError("zer_pol_R: n - |m| is odd")
        res = 0.0
        if x * x + y * y <= 1.0:
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
                res += (
                    sign
                    * jdiv(a * 1.0, int32(int32(b * c) * d))
                    * (x * x + y * y) ** (n / 2.0 - s)
                )
                if s < idiv(n - m, 2):
                    sign *= -1
                    a = int32(idiv(a, n - s))
                    b = int32(b * (s + 1))
                    c = int32(idiv(c, idiv(n + m, 2) - s))
                    d = int32(idiv(d, idiv(n - m, 2) - s))
        return res

    @staticmethod
    def invoke(input: Image, n: int, m: int) -> float | None:
        try:
            return ZernikeMoment().preprocess(input, n, m)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.util.Complex2DArray``: a square, power-of-two-padded grid
of ``ComplexNumber``s, used by ``frequential.{FFT,IFFT}`` as the working
array for their column/row-wise radix-2 recursive transform.

Only the constructors and methods ``frequential.FFT``/``IFFT`` actually use
are ported here (the ``(double[], w, h)`` and ``(ComplexNumber[][], w, h)``
content constructors, the size-only constructor, ``getColumn``/``putColumn``,
``getRow``/``putRow``, and ``getReals``/``getImags``/``getMagnitudes``/
``getPhases``) -- the Java class also has a copy constructor, an
``(int[][], w, h)`` constructor, ``DCToCentre``/``DCToTopLeft`` (2 overloads
each) and ``getPhase``, none of which any ported caller reaches yet. Add
them here if a future caller needs them, following this file's pattern.

``width``/``height`` keep the caller's original (possibly non-power-of-two,
possibly non-square) dimensions; ``size`` is the padded square side
(``2**n``, the smallest power of two >= ``max(width, height)``), and
``values`` is always ``size x size`` -- ``getColumn``/``getRow`` (etc.)
iterate the full padded ``size``, not ``width``/``height``."""

from __future__ import annotations

from .._javacompat import jint, jpow
from .ComplexNumber import ComplexNumber


class Complex2DArray:
    __slots__ = ("height", "size", "values", "width")

    def __init__(self, *args):
        """Complex2DArray(w, h) | (p: list[float], w, h) |
        (v: list[list[ComplexNumber]], w, h)."""
        if len(args) == 2:
            w, h = args
            content = None
        elif len(args) == 3:
            first, w, h = args
            content = first
        else:
            raise TypeError(f"Complex2DArray takes 2 or 3 arguments, got {len(args)}")

        self.width = w
        self.height = h
        n = 0
        while jpow(2.0, n) < max(w, h):
            n += 1
        self.size = jint(jpow(2.0, n))
        self.values = [[ComplexNumber(0.0, 0.0) for _ in range(self.size)] for _ in range(self.size)]

        if content is None:
            return
        if isinstance(content, list) and content and isinstance(content[0], list):
            # ComplexNumber[][] v
            for j in range(h):
                for i in range(w):
                    self.values[i][j] = ComplexNumber(content[i][j])
        else:
            # double[] p, row-major: p[i + j*w]
            for j in range(h):
                for i in range(w):
                    self.values[i][j] = ComplexNumber(content[i + j * w], 0.0)

    # ------------------------------------------------------------------
    def getColumn(self, n: int) -> list[ComplexNumber]:
        return [ComplexNumber(self.values[n][i]) for i in range(self.size)]

    def putColumn(self, n: int, c: list[ComplexNumber]) -> None:
        for i in range(self.size):
            self.values[n][i] = ComplexNumber(c[i])

    def putRow(self, n: int, c: list[ComplexNumber]) -> None:
        for i in range(self.size):
            self.values[i][n] = ComplexNumber(c[i])

    def getRow(self, n: int) -> list[ComplexNumber]:
        return [ComplexNumber(self.values[i][n]) for i in range(self.size)]

    # ------------------------------------------------------------------
    def getReals(self) -> list[list[float]]:
        return [[self.values[i][j].real for j in range(self.size)] for i in range(self.size)]

    def getImags(self) -> list[list[float]]:
        return [[self.values[i][j].imag for j in range(self.size)] for i in range(self.size)]

    def getMagnitudes(self) -> list[list[float]]:
        return [
            [self.values[i][j].magnitude() for j in range(self.size)] for i in range(self.size)
        ]

    def getPhases(self) -> list[list[float]]:
        return [
            [self.values[i][j].phaseAngle() for j in range(self.size)] for i in range(self.size)
        ]

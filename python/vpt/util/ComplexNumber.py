"""Port of ``vpt.util.ComplexNumber``.

A mutable complex value. Python has a perfectly good built-in ``complex``,
but this class is ported as-is because two of its static methods have
observable side effects that the built-in cannot reproduce — see cSum/cDiff.
"""

from __future__ import annotations

import math


class ComplexNumber:
    __slots__ = ("real", "imag")

    def __init__(self, *args):
        """ComplexNumber() | (real, imag) | (ComplexNumber)."""
        if not args:
            self.real = 0.0
            self.imag = 0.0
        elif len(args) == 1:
            c = args[0]
            self.real = c.real
            self.imag = c.imag
        else:
            self.real = float(args[0])
            self.imag = float(args[1])

    # ------------------------------------------------------------------
    def magnitude(self) -> float:
        return math.sqrt(self.cNorm())

    def phaseAngle(self) -> float:
        if self.real == 0.0 and self.imag == 0.0:
            return 0.0
        # Java uses atan, not atan2, so the result collapses to (-pi/2, pi/2)
        # and loses the quadrant. Preserved.
        return math.atan(self.imag / self.real)

    def cNorm(self) -> float:
        return self.real * self.real + self.imag * self.imag

    # ------------------------------------------------------------------
    @staticmethod
    def cExp(z: "ComplexNumber") -> "ComplexNumber":
        x = ComplexNumber(math.exp(z.real), 0.0)
        y = ComplexNumber(math.cos(z.imag), math.sin(z.imag))
        return ComplexNumber.cMult(x, y)

    @staticmethod
    def cMult(z1: "ComplexNumber", z2: "ComplexNumber") -> "ComplexNumber":
        z3 = ComplexNumber()
        z3.real = z1.real * z2.real - z1.imag * z2.imag
        z3.imag = z1.real * z2.imag + z1.imag * z2.real
        return z3

    @staticmethod
    def cSum(z1: "ComplexNumber", z2: "ComplexNumber") -> "ComplexNumber":
        """ORIGINAL DEFECT: accumulates into `z1` in place and returns a
        freshly constructed zero. Callers using the return value get 0+0i;
        callers reading `z1` afterwards get the sum. Preserved — the same
        pattern appears in Tools.VAddC and Tools.VSub."""
        z3 = ComplexNumber()
        z1.real += z2.real
        z1.imag += z2.imag
        return z3

    @staticmethod
    def cDiff(z1: "ComplexNumber", z2: "ComplexNumber") -> "ComplexNumber":
        """ORIGINAL DEFECT: subtracts from `z1` in place and returns a
        freshly constructed zero. Preserved; see :meth:`cSum`."""
        z3 = ComplexNumber()
        z1.real -= z2.real
        z1.imag -= z2.imag
        return z3

    @staticmethod
    def cDiv(z1: "ComplexNumber", z2: "ComplexNumber") -> "ComplexNumber":
        z3 = ComplexNumber()
        n = z2.cNorm()
        z3.real = (z1.real * z2.real + z1.imag * z2.imag) / n
        z3.imag = (z2.real * z1.imag - z1.real * z2.imag) / n
        return z3

    def __repr__(self) -> str:
        return f"ComplexNumber({self.real}, {self.imag})"

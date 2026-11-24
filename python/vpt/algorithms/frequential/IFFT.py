"""Port of ``vpt.algorithms.frequential.IFFT``: the inverse of ``FFT``, via
the same ``recursiveInverseFFT`` butterfly structure.

**Confirmed non-functional, same cause as ``FFT``**: verified directly
against the JDK 21 oracle that ``IFFT.invoke`` returns all-zero output for
any (real, imag) input. ``recursiveInverseFFT`` is built on the same
``ComplexNumber.cSum``/``cDiff`` calls as ``FFT._recursiveFFT`` -- see that
file's docstring for the full mechanism (both mutate their first argument
in place and return a freshly-constructed zero, so every ``sum``/``diff``
element is always ``0+0i``)."""

from __future__ import annotations

import traceback

from ..._javacompat import jpow
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Complex2DArray import Complex2DArray
from ...util.ComplexNumber import ComplexNumber


class IFFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.real: DoubleImage | None = None
        self.imag: DoubleImage | None = None
        self.inputFields = "real, imag"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.real.getXDim()
        ydim = self.real.getYDim()

        content = [[None] * ydim for _ in range(xdim)]

        n = 0
        while jpow(2.0, n) < max(xdim, ydim):
            n += 1
        for x in range(xdim):
            for y in range(ydim):
                realP = self.real.getXYDouble(x, y) * jpow(2.0, n)
                imagP = self.imag.getXYDouble(x, y) * jpow(2.0, n)
                content[x][y] = ComplexNumber(realP, imagP)

        inputArray = Complex2DArray(content, xdim, ydim)
        intermediateArray = Complex2DArray(xdim, ydim)
        outputArray = Complex2DArray(xdim, ydim)

        for i in range(inputArray.size):
            intermediateArray.putColumn(i, self._recursiveInverseFFT(inputArray.getColumn(i)))
        for i in range(intermediateArray.size):
            outputArray.putRow(i, self._recursiveInverseFFT(intermediateArray.getRow(i)))

        self.output = DoubleImage(self.real, False)

        mags = outputArray.getReals()
        for x in range(xdim):
            for y in range(ydim):
                self.output.setXYDouble(x, y, mags[x][y])

    def _recursiveInverseFFT(self, x: list[ComplexNumber]) -> list[ComplexNumber]:
        n = len(x)
        m = n // 2

        result: list[ComplexNumber | None] = [None] * n
        sum_: list[ComplexNumber | None] = [None] * m
        diff: list[ComplexNumber | None] = [None] * m

        cTwo = ComplexNumber(2.0, 0.0)
        if n == 1:
            result[0] = x[0]
        else:
            z1 = ComplexNumber(0.0, 6.283185307179586 / n)
            tmp = ComplexNumber.cExp(z1)
            z1 = ComplexNumber(1.0, 0.0)
            for i in range(m):
                z3 = ComplexNumber.cSum(x[i], x[i + m])
                sum_[i] = ComplexNumber.cDiv(z3, cTwo)

                z3 = ComplexNumber.cDiff(x[i], x[i + m])
                z4 = ComplexNumber.cMult(z3, z1)
                diff[i] = ComplexNumber.cDiv(z4, cTwo)

                z2 = ComplexNumber.cMult(z1, tmp)
                z1 = ComplexNumber(z2)

            even = self._recursiveInverseFFT(sum_)
            odd = self._recursiveInverseFFT(diff)
            for i in range(m):
                result[i * 2] = ComplexNumber(even[i])
                result[i * 2 + 1] = ComplexNumber(odd[i])

        return result

    @staticmethod
    def invoke(real: DoubleImage, imag: DoubleImage) -> Image | None:
        try:
            return IFFT().preprocess(real, imag)
        except GlobalException:
            traceback.print_exc()
        return None

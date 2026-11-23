"""Port of ``vpt.algorithms.frequential.FFT``: a recursive radix-2 2D FFT
(column-wise then row-wise), or so the code intends.

**Confirmed non-functional**: verified directly against the JDK 21 oracle
that ``FFT.invoke`` returns all-zero output for every ``outputType``,
regardless of input. Cause: ``recursiveFFT``'s per-butterfly ``sum``/
``diff`` values are built from ``ComplexNumber.cSum``/``cDiff``'s return
value -- and both of those (already documented on ``ComplexNumber.cSum``)
mutate their first argument in place and return a *freshly constructed
zero*. So every element of both ``sum`` and ``diff`` is exactly ``0+0i``
at every recursion level except the unreachable-in-practice ``n==1`` base
case on a length-1 input, which nothing here ever calls with. The
recursion therefore always bottoms out to all zeros and stays there.
Reproduced by calling ``cSum``/``cDiff`` exactly as the Java does --
not "fixed" into a working FFT."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Complex2DArray import Complex2DArray
from ...util.ComplexNumber import ComplexNumber
from ...util.Tools import Tools

REAL = 0
REAL_IMAG = 1
MAG = 2
MAG_PHASE = 3


class FFT(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[DoubleImage] | None = None
        self.input: Image | None = None
        self.shifted: bool | None = None
        self.outputType: int | None = None
        self.inputFields = "input, shifted, outputType"
        self.outputFields = "output"

    def execute(self) -> None:
        pixels = [0.0] * self.input.getSize()

        size = self.input.getSize()
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        n = 0
        while 2.0**n < max(xdim, ydim):
            n += 1

        # Shortcut for MAG, the only output type any caller uses. As the
        # module docstring explains, once the padded transform size 2**n is
        # at least 2 every butterfly is built from cSum/cDiff's fresh-zero
        # return values, so every output element is a signed zero whatever
        # the input (NaN/Infinity included), and its magnitude
        # sqrt(+-0**2 + +-0**2) is exactly +0.0. DoubleImage(input, False)
        # is zero-filled, so it already holds those values; skip the
        # O(size**2 log size) ComplexNumber work. shiftOrigin still runs, so
        # the result is the same object the full path builds. Size 1 (a 1x1
        # image) is excluded: there the base case returns the pixel itself.
        # The other output types expose the zeros' signs (REAL/IMAG parts,
        # phases), so they keep the full transcription.
        if self.outputType == MAG and 2.0**n >= 2.0:
            self.output = [DoubleImage(self.input, False)]
            if self.shifted:
                self.output[0] = Tools.shiftOrigin(self.output[0])
            return

        for i in range(size):
            pixels[i] = self.input.getDouble(i) * 2.0**n

        inputArray = Complex2DArray(pixels, xdim, ydim)
        intermediateArray = Complex2DArray(pixels, xdim, ydim)
        outputArray = Complex2DArray(pixels, xdim, ydim)

        for i in range(inputArray.size):
            intermediateArray.putColumn(i, self._recursiveFFT(inputArray.getColumn(i)))
        for i in range(intermediateArray.size):
            outputArray.putRow(i, self._recursiveFFT(intermediateArray.getRow(i)))

        if self.outputType == REAL:
            self.output = [DoubleImage(self.input, False)]

            reals = outputArray.getReals()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[0].setXYDouble(x, y, reals[x][y])

        elif self.outputType == REAL_IMAG:
            self.output = [DoubleImage(self.input, False), DoubleImage(self.input, False)]

            reals = outputArray.getReals()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[0].setXYDouble(x, y, reals[x][y])
            imags = outputArray.getImags()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[1].setXYDouble(x, y, imags[x][y])

        elif self.outputType == MAG:
            self.output = [DoubleImage(self.input, False)]

            mags = outputArray.getMagnitudes()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[0].setXYDouble(x, y, mags[x][y])
            if self.shifted:
                self.output[0] = Tools.shiftOrigin(self.output[0])

        elif self.outputType == MAG_PHASE:
            self.output = [DoubleImage(self.input, False), DoubleImage(self.input, False)]

            mags = outputArray.getMagnitudes()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[0].setXYDouble(x, y, mags[x][y])
            phases = outputArray.getPhases()
            for x in range(xdim):
                for y in range(ydim):
                    self.output[1].setXYDouble(x, y, phases[x][y])
            if self.shifted:
                self.output[0] = Tools.shiftOrigin(self.output[0])

        else:
            raise GlobalException("Invalid output type")

    def _recursiveFFT(self, x: list[ComplexNumber]) -> list[ComplexNumber]:
        n = len(x)
        m = n // 2
        result: list[ComplexNumber | None] = [None] * n
        sum_: list[ComplexNumber | None] = [None] * m
        diff: list[ComplexNumber | None] = [None] * m

        cTwo = ComplexNumber(2.0, 0.0)
        if n == 1:
            result[0] = x[0]
        else:
            z1 = ComplexNumber(0.0, -6.283185307179586 / n)
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

            even = self._recursiveFFT(sum_)
            odd = self._recursiveFFT(diff)
            for i in range(m):
                result[i * 2] = ComplexNumber(even[i])
                result[i * 2 + 1] = ComplexNumber(odd[i])

        return result

    @staticmethod
    def invoke(image: Image, shifted: bool, outputType: int) -> list[DoubleImage] | None:
        try:
            return FFT().preprocess(image, shifted, outputType)
        except GlobalException:
            traceback.print_exc()
        return None

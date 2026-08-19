"""Port of ``vpt.algorithms.statistical.PCA``: principal component analysis
across an image's channels (not spatial PCA) -- decorrelates the ``cDim``
channels via the eigendecomposition of their ``cDim x cDim`` covariance
matrix, and returns the projected "eigenimages" alongside the eigenvectors/
eigenvalues/channel means needed to invert it (``ReversePCA``).

**New accepted divergence, same shape as ``NonFlatSE.decompose``'s SVD
note**: the eigendecomposition itself (``Jama.EigenvalueDecomposition`` in
the Java, ``numpy.linalg.eigh`` here) is not expected to agree bit-for-bit.
Both use fundamentally different algorithms (Jama: Householder
tridiagonalisation + implicit-shift QL; LAPACK, which NumPy delegates to:
typically divide-and-conquer or MRRR) that need not round identically, and
even a perfectly-agreeing algorithm has a sign ambiguity per eigenvector
(the same ``+-`` freedom SVD has) plus, for any repeated eigenvalue, a
completely unconstrained choice of basis within that eigenvalue's
eigenspace. This port's own ``sort`` step re-orders by eigenvalue afterward
(descending, Java's own selection sort re-implemented verbatim, including
its ``>=`` tie-break), which does *not* resolve either ambiguity. Matrix
multiplication elsewhere in this class (``_jama.times``, not
``numpy.matmul``) and the mean/covariance computation upstream of the
eigendecomposition remain genuinely bit-exact, unlike the decomposition
itself.

``getVariances`` has the same defect shape as ``Tools.VAddC``/``VSub``:
allocates a fresh zero ``variances`` array, then divides *into* ``vals``
in place, and returns the never-written (still all-zero) ``variances``.
Preserved."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from . import _jama


class PCA(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.eigenvectors: list[list[float]] | None = None
        self.eigenvalues: list[float] | None = None
        self.mean: list[float] | None = None
        self.inputFields = "input"
        self.outputFields = "output, eigenvectors, eigenvalues, mean"

    def execute(self) -> None:
        cDim = self.input.getCDim()
        xDim = self.input.getXDim()
        yDim = self.input.getYDim()

        size = xDim * yDim
        if cDim == 1:
            self.output = self.input.newInstance(True)
            return

        pixels = [[0.0] * (xDim * yDim) for _ in range(cDim)]
        for c in range(cDim):
            for x in range(xDim):
                for y in range(yDim):
                    pixels[c][y * xDim + x] = self.input.getXYCDouble(x, y, c)

        self.mean = [0.0] * cDim
        for i in range(cDim):
            self.mean[i] = 0.0
            for j in range(xDim * yDim):
                self.mean[i] += pixels[i][j]
            self.mean[i] /= size

        for i in range(cDim):
            for j in range(size):
                pixels[i][j] -= self.mean[i]

        M = pixels
        Mt = [list(row) for row in zip(*M)]
        M = _jama.times(M, Mt)

        M = [[v * jdiv(1.0, size - 1) for v in row] for row in M]

        eigenvalues, V = np.linalg.eigh(np.array(M, dtype=np.float64))
        self.eigenvalues = list(eigenvalues)
        self.eigenvectors = [list(row) for row in V]

        self._sort(self.eigenvalues, self.eigenvectors, self.mean, cDim)

        V = self.eigenvectors

        M = _jama.times(Mt, V)
        M = [list(row) for row in zip(*M)]

        eigenImages = M
        for j in range(len(eigenImages)):
            if self.eigenvalues[j] < 0.0:
                self.eigenvalues[j] = 0.0
                for i in range(len(eigenImages[j])):
                    eigenImages[j][i] = 0.0

        self.output = DoubleImage(self.input, False)
        for c in range(cDim):
            for x in range(xDim):
                for y in range(yDim):
                    d = eigenImages[c][y * xDim + x]
                    self.output.setXYCDouble(x, y, c, d)

    @staticmethod
    def invoke(input: Image) -> Image | None:
        try:
            return PCA().preprocess(input)
        except GlobalException:
            traceback.print_exc()
        return None

    @staticmethod
    def invokeAll(input: Image) -> list | None:
        try:
            return PCA().preprocessAll(input)
        except GlobalException:
            traceback.print_exc()
        return None

    @staticmethod
    def getVariances(vals: list[float]) -> list[float]:
        totalVariance = 0.0
        variances = [0.0] * len(vals)
        for j in range(len(vals)):
            totalVariance += vals[j]
        for j in range(len(vals)):
            vals[j] /= totalVariance
        return variances

    def _sort(
        self, vals: list[float], eigenvectors: list[list[float]], mean: list[float], n: int
    ) -> None:
        for i in range(n):
            k = i
            p = vals[k]
            for j in range(i + 1, n):
                if vals[j] >= p:
                    k = j
                    p = vals[k]
            if k != i:
                vals[k] = vals[i]
                vals[i] = p
                for j in range(n):
                    p = eigenvectors[j][i]
                    eigenvectors[j][i] = eigenvectors[j][k]
                    eigenvectors[j][k] = p

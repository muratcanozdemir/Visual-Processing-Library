"""Port of ``vpt.util.se.NonFlatSE``.

A structuring element carrying a real-valued weight per pixel (a kernel).
Extends DoubleImage.

The Java uses Jama for :meth:`decompose`; this port uses ``numpy.linalg``.
See the note on that method: the two libraries agree on the factorisation but
not necessarily on the sign of the singular vectors, which is checked against
the oracle rather than assumed.
"""

from __future__ import annotations

import math

import numpy as np

from ..._javacompat import Point, idiv, jcos, jdiv, jexp, jround, jsin
from ...DoubleImage import DoubleImage
from ..Distance import Distance

TWO_PI = 6.283185307179586


class NonFlatSE(DoubleImage):
    __slots__ = ("center", "coords")

    def __init__(self, *args):
        """NonFlatSE(center, DoubleImage) | (center, kernel2d) | (NonFlatSE)."""
        if len(args) == 1:
            se = args[0]
            super().__init__(se, False)
            self.center = Point(se.center)
        else:
            center, src = args
            if isinstance(src, DoubleImage):
                super().__init__(src, True)
            else:
                super().__init__(src)  # 2-D kernel, indexed [y][x]
            self.center = center
        self.coords = None

    # ------------------------------------------------------------------
    @staticmethod
    def box(*args) -> "NonFlatSE":
        """box(width, height, value) or box(DoubleImage)."""
        if len(args) == 1:
            img = args[0]
            return NonFlatSE(Point(idiv(img.getXDim(), 2), idiv(img.getYDim(), 2)), img)
        width, height, d = args
        dimg = DoubleImage(width, height, 1)
        dimg.fillDouble(d)
        return NonFlatSE(Point(idiv(dimg.getXDim(), 2), idiv(dimg.getYDim(), 2)), dimg)

    @staticmethod
    def cylinder(radius: int, height: float) -> "NonFlatSE":
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        se = NonFlatSE.box(size, size, -1.0)
        se.setXYDouble(se.center.x, se.center.y, height)

        perimeter = 8 * radius
        for i in range(perimeter):
            x = se.center.x + int(jround(radius * jcos(TWO_PI * i / perimeter)))
            y = se.center.y + int(jround(radius * jsin(TWO_PI * i / perimeter)))
            se.setXYDouble(x, y, height)
        return se

    @staticmethod
    def cone(radius: int, height: float) -> "NonFlatSE":
        if radius < 1:
            radius = 1
        size = radius * 2 + 1

        se = NonFlatSE.box(size, size, 0.0)

        maxDist = Distance.EuclideanDistance(0, 0, radius, radius)
        for x in range(size):
            for y in range(size):
                dist = Distance.EuclideanDistance(x, y, se.center.x, se.center.y)
                if dist <= radius:
                    se.setXYDouble(x, y, height * (maxDist - dist) / maxDist)
                else:
                    se.setXYDouble(x, y, -1.0)
        return se

    @staticmethod
    def Gaussian(radius: int) -> "NonFlatSE":
        r = radius * 2 + 1

        dimg = DoubleImage(r, r, 1)
        vector = np.zeros(r, dtype=np.float64)

        sigma = radius / 3.0
        sigma22 = 2.0 * sigma * sigma
        sigmaPi2 = TWO_PI * sigma
        sqrtSigmaPi2 = math.sqrt(sigmaPi2)
        radius2 = radius * radius

        index = 0
        for x in range(-1 * radius, radius + 1):
            distance = x * x
            if distance > radius2:
                vector[index] = 0.0
            else:
                vector[index] = jexp(-1.0 * distance / sigma22) / sqrtSigmaPi2
            index += 1

        total = 0.0
        for x in range(r):
            for y in range(r):
                tmp = vector[x] * vector[y]
                dimg.setXYDouble(x, y, tmp)
                total += tmp

        for x in range(r):
            for y in range(r):
                dimg.setXYDouble(x, y, jdiv(dimg.getXYDouble(x, y), total))

        return NonFlatSE(Point(idiv(r, 2), idiv(r, 2)), dimg)

    # ------------------------------------------------------------------
    def decompose(self):
        """Split a rank-1 kernel into an h/v pair whose outer product is it.

        Returns None if the kernel is not rank 1, as in the Java.

        SIGN CAVEAT. Java uses Jama's SingularValueDecomposition; this uses
        numpy.linalg.svd. An SVD is unique only up to a *joint* sign flip of
        the leading singular vectors, so both libraries are correct and they
        need not agree. Measured against the oracle, Jama's sign is not even
        self-consistent across inputs: Gaussian(1), Gaussian(3), Gaussian(4)
        come out positive while Gaussian(2), Gaussian(6) and box(1) come out
        negative. Reproducing that exactly would mean reimplementing Jama's
        Golub-Reinsch iteration, sign quirks included.

        That is not worth doing, because the result is unobservable. The only
        caller in the library is ``algorithms.linear.Convolution``, which
        assigns ``subKernels = null`` on the line immediately after calling
        this and then takes the ``== null`` branch -- the separable
        convolution path is dead code in the shipped library, and has been
        since the original JAR (line 47 of Convolution's source). Nothing
        consumes the vectors, and the outer product h(x)v, which is what a
        real caller would use, is sign-invariant anyway.

        The golden test therefore checks this sign-invariantly: it compares
        the outer product and the |values|, not the raw signs.
        """
        M = np.empty((self.getYDim(), self.getXDim()), dtype=np.float64)
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                M[y, x] = self.getXYDouble(x, y)

        if np.linalg.matrix_rank(M) != 1:
            return None

        U, S, Vt = np.linalg.svd(M)
        s = S[0]
        u0 = U[:, 0]
        v0 = Vt[0, :]

        hKernel = np.empty((1, self.getXDim()), dtype=np.float64)
        for i in range(self.getXDim()):
            hKernel[0][i] = -1.0 * v0[i] * math.sqrt(s)
        h = NonFlatSE(Point(idiv(self.getXDim(), 2), 0), hKernel)

        vKernel = np.empty((self.getYDim(), 1), dtype=np.float64)
        for i in range(self.getYDim()):
            vKernel[i][0] = -1.0 * u0[i] * math.sqrt(s)
        v = NonFlatSE(Point(0, idiv(self.getYDim(), 2)), vKernel)

        return [h, v]

    # ------------------------------------------------------------------
    def reflection(self) -> "NonFlatSE":
        se = NonFlatSE(self)
        se.center = Point(self.getXDim() - 1 - self.center.x,
                          self.getYDim() - 1 - self.center.y)
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                se.setXYDouble(self.getXDim() - 1 - x, self.getYDim() - 1 - y,
                               self.getXYDouble(x, y))
        return se

    def getCoords(self):
        """Kernel pixels as centre-relative offsets, centre first.

        ORIGINAL DEFECT: the membership test is ``Math.abs(value) >= 0.0``,
        which is true for every non-NaN value, so *all* pixels are included
        and the -1.0 sentinel that cone/cylinder write to mark "outside" has
        no effect here. The centre test omits the abs, making it a genuine
        ``value >= 0.0``, so the two are inconsistent. Preserved.
        """
        if self.coords is not None:
            return self.coords

        size = 0
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                if abs(self.getXYDouble(x, y)) >= 0.0:
                    size += 1

        self.coords = [None] * size

        k = 0
        if self.getXYDouble(self.center.x, self.center.y) >= 0.0:
            self.coords[k] = Point(0, 0)
            k += 1
        for x in range(self.getXDim()):
            for y in range(self.getYDim()):
                if (abs(self.getXYDouble(x, y)) >= 0.0
                        and (x != self.center.x or y != self.center.y)):
                    self.coords[k] = Point(x - self.center.x, y - self.center.y)
                    k += 1
        return self.coords

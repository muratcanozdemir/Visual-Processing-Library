"""Port of ``vpt.util.Tools``.

General helpers used across the library. Several methods carry defects that
are *original* to the library rather than decompiler damage -- they compile
and always have behaved this way -- so they are reproduced exactly. Each is
flagged inline; do not repair one without regenerating the goldens, because
callers depend on the observed behaviour.
"""

from __future__ import annotations

import math
import random
import sys

import numpy as np

from .._javacompat import Point, idiv, jacos, jdiv, jint, jround
from .Distance import Distance


class Tools:
    epsilon = 1.0e-8

    # 4-connected neighbours first, then the 4 diagonals.
    N = [Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1),
         Point(1, 1), Point(-1, -1), Point(-1, 1), Point(1, -1)]

    # ------------------------------------------------------------------
    @staticmethod
    def radian2degree(radian: float) -> float:
        return radian * 180.0 / 3.141592653589793

    @staticmethod
    def degree2radian(degree: float) -> float:
        return degree * 3.141592653589793 / 180.0

    @staticmethod
    def factorial(n: int) -> float:
        if n < 0:
            return 0.0
        if n == 0:
            return 1.0
        return n * Tools.factorial(n - 1)

    @staticmethod
    def cmpr(d1: float, d2: float) -> int:
        """Three-way compare with an absolute epsilon of 1e-8."""
        diff = d1 - d2
        if -1.0e-8 <= diff <= 1.0e-8:
            return 0
        if diff > 0.0:
            return 1
        return -1

    # ------------------------------------------------------------------
    @staticmethod
    def VAddC(v, c: float):
        """ORIGINAL DEFECT: adds `c` to `v` in place, but returns a freshly
        allocated all-zero array of the same length. Callers that use the
        return value get zeros; callers that rely on the mutation get the
        sum. Preserved."""
        tmp = np.zeros(len(v), dtype=np.float64)
        for i in range(len(v)):
            v[i] += c
        return tmp

    @staticmethod
    def VSub(v1, v2):
        """ORIGINAL DEFECT: subtracts in place from `v1` and returns a
        freshly allocated all-zero array. Preserved; see :meth:`VAddC`."""
        if len(v1) != len(v2):
            print("Incompatible vector lengths", file=sys.stderr)
            return None
        diff = np.zeros(len(v1), dtype=np.float64)
        for i in range(len(v1)):
            v1[i] -= v2[i]
        return diff

    @staticmethod
    def shuffle(array) -> None:
        """Fisher-Yates in place. Uses an unseeded RNG, exactly as Java's
        ``new Random()`` does, so results are not reproducible across runs
        and cannot be golden-tested."""
        for i in range(len(array) - 1, 0, -1):
            j = random.randint(0, i)
            array[j], array[i] = array[i], array[j]

    @staticmethod
    def Vext(v, dim: int):
        newVector = np.empty(len(v) - dim, dtype=np.float64)
        for i in range(len(newVector)):
            newVector[i] = v[dim + i]
        return newVector

    # ------------------------------------------------------------------
    @staticmethod
    def EuclideanDistance(v, w) -> float:
        """Integer-vector Euclidean distance (Java's ``EuclideanDistance``,
        capital E, distinct from the lowercase double[] overload)."""
        total = 0
        for i in range(len(v)):
            total += (v[i] - w[i]) * (v[i] - w[i])
        return math.sqrt(total)

    @staticmethod
    def euclideanDistance(v, w) -> float:
        total = 0.0
        for i in range(len(v)):
            total += (v[i] - w[i]) * (v[i] - w[i])
        return math.sqrt(total)

    @staticmethod
    def EuclideanNorm(v) -> float:
        norm = 0.0
        for i in range(len(v)):
            norm += v[i] * v[i]
        return math.sqrt(norm)

    @staticmethod
    def norm(a) -> float:
        d = 0.0
        for i in range(len(a)):
            d += a[i] * a[i]
        return math.sqrt(d)

    @staticmethod
    def dotProduct(a, b) -> float:
        d = 0.0
        for i in range(len(a)):
            d += a[i] * b[i]
        return d

    # ------------------------------------------------------------------
    @staticmethod
    def volumeAll(img) -> float:
        d = 0.0
        for c in range(img.getCDim()):
            d += Tools.volume(img, c)
        return d

    @staticmethod
    def volume(img, channel: int) -> float:
        d = 0.0
        for x in range(img.getXDim()):
            for y in range(img.getYDim()):
                d += img.getXYCDouble(x, y, channel)
        return d

    @staticmethod
    def hueDistance(h: float, ref: float) -> float:
        a = abs(ref - h)
        if a <= 0.5:
            return a
        return 1.0 - a

    # ------------------------------------------------------------------
    @staticmethod
    def circularVolume(img, channel: int, circleIndex: int, howManyCircles: int) -> float:
        centerX = idiv(img.getXDim(), 2)
        centerY = idiv(img.getYDim(), 2)

        radius = idiv((circleIndex + 1) * min(centerX, centerY), howManyCircles)
        if circleIndex + 1 == howManyCircles:
            radius = 2147483647

        d = 0.0
        for x in range(img.getXDim()):
            for y in range(img.getYDim()):
                if Distance.EuclideanDistance(x, y, centerX, centerY) <= radius:
                    d += img.getXYCDouble(x, y, channel)
        return d

    @staticmethod
    def circularMoment(img, channel: int, circleIndex: int, howManyCircles: int,
                       moment: int) -> float:
        from ..algorithms.statistical.InvariantMoment import InvariantMoment

        centerX = idiv(img.getXDim(), 2)
        centerY = idiv(img.getYDim(), 2)

        radius = idiv((circleIndex + 1) * min(centerX, centerY), howManyCircles)
        if circleIndex + 1 == howManyCircles:
            radius = 2147483647

        xdim = img.getXDim()
        ydim = img.getYDim()

        tmp = img.getChannel(channel)
        tmp.fillInt(0)
        for x in range(xdim):
            for y in range(ydim):
                if Distance.EuclideanDistance(x, y, centerX, centerY) <= radius:
                    tmp.setXYDouble(x, y, img.getXYCDouble(x, y, channel))
        return InvariantMoment.invoke(tmp, moment)[0]

    @staticmethod
    def circularBandVolume(img, channel: int, circleIndex: int,
                           howManyCircles: int) -> float:
        centerX = idiv(img.getXDim(), 2)
        centerY = idiv(img.getYDim(), 2)

        outerRadius = idiv((circleIndex + 1) * min(centerX, centerY), howManyCircles)
        innerRadius = idiv(circleIndex * min(centerX, centerY), howManyCircles)
        if circleIndex + 1 == howManyCircles:
            outerRadius = 2147483647

        d = 0.0
        for x in range(img.getXDim()):
            for y in range(img.getYDim()):
                dist = Distance.EuclideanDistance(x, y, centerX, centerY)
                if dist <= outerRadius and dist >= innerRadius:
                    d += img.getXYCDouble(x, y, channel)
        return d

    @staticmethod
    def irregularCircularBandVolumes(img, bandIndex: int) -> float:
        centerX = idiv(img.getXDim(), 2)
        centerY = idiv(img.getYDim(), 2)

        outerRadius = math.pow(2.0, bandIndex)
        innerRadius = math.pow(2.0, bandIndex - 1)
        if bandIndex == 0 or bandIndex == 1:
            outerRadius = 2.0
            innerRadius = 0.0

        d = 0.0
        for x in range(img.getXDim()):
            for y in range(img.getYDim()):
                dist = Distance.EuclideanDistance(x, y, centerX, centerY)
                if dist <= outerRadius and dist >= innerRadius:
                    d += img.getXYDouble(x, y)
        return d

    @staticmethod
    def irregularCircularBandVolumes2(img, bandIndex: int, howManyCircles: int) -> float:
        xdim = img.getXDim()
        ydim = img.getYDim()

        centerX = idiv(xdim, 2)
        centerY = idiv(ydim, 2)

        maxRadius = math.sqrt(2.0) * min(idiv(xdim, 2), idiv(ydim, 2))

        outerRadius = 1.0 / math.pow(2.0, howManyCircles - bandIndex) * maxRadius
        innerRadius = 1.0 / math.pow(2.0, howManyCircles - bandIndex + 1) * maxRadius
        if bandIndex <= 1:
            innerRadius = 0.0

        d = 0.0
        for x in range(xdim):
            for y in range(ydim):
                dist = Distance.EuclideanDistance(x, y, centerX, centerY)
                if dist <= outerRadius and dist >= innerRadius:
                    d += img.getXYDouble(x, y)
        return d

    @staticmethod
    def regularAngularVolumes(img, angleIndex: int, howManyAngles: int) -> float:
        xdim = img.getXDim()
        ydim = img.getYDim()

        theta = 3.141592653589793 / howManyAngles

        angle1 = (angleIndex + 1) % howManyAngles * theta
        angle2 = angleIndex * theta
        if angleIndex == howManyAngles - 1:
            angle1 = 3.141592653589793

        d = 0.0
        for x in range(idiv(xdim, 2), xdim):
            for y in range(ydim):
                _x = x - idiv(xdim, 2)
                if y <= idiv(ydim, 2):
                    _y = idiv(ydim, 2) - y
                else:
                    _y = -1 * (y - idiv(ydim, 2))
                myAngle = math.atan2(_y, _x)
                if myAngle < 0.0:
                    myAngle += 3.141592653589793
                if angle1 >= myAngle >= angle2:
                    d += img.getXYDouble(x, y)
        return d

    # ------------------------------------------------------------------
    @staticmethod
    def normalize(feature):
        mean = Tools.mean(feature)
        sdev = Tools.sdev(feature)

        newFeature = np.empty(len(feature), dtype=np.float64)
        if Tools.cmpr(sdev, 0.0) == 0:
            return None
        for i in range(len(feature)):
            newFeature[i] = (feature[i] - mean) / sdev
        return newFeature

    @staticmethod
    def sdev(feature) -> float:
        mean = Tools.mean(feature)
        sdev = 0.0
        if len(feature) == 0:
            return 0.0
        for i in range(len(feature)):
            sdev += (feature[i] - mean) * (feature[i] - mean)
        sdev /= len(feature)
        return math.sqrt(sdev)

    @staticmethod
    def mean(feature) -> float:
        mean = 0.0
        if len(feature) == 0:
            return 0.0
        for i in range(len(feature)):
            mean += feature[i]
        return mean / len(feature)

    # ------------------------------------------------------------------
    @staticmethod
    def histogramOfDistancesFromCoG(img, histoLength: int):
        xdim = img.getXDim()
        ydim = img.getYDim()
        if img.getCDim() != 1 or histoLength == 0:
            return None

        cog = Tools.centerOfGravity(img)

        numberOfNonZeroPixels = 0
        histo = np.zeros(histoLength + 1, dtype=np.float64)

        # ORIGINAL DEFECT: min/max are updated in an if/else-if chain, so a
        # value cannot update `max` on the same iteration it fails to update
        # `min`. The first non-zero pixel always sets `min` and never `max`.
        # Preserved.
        minimum = 1.7976931348623157e308
        maximum = 4.9e-324
        for x in range(xdim):
            for y in range(ydim):
                p = img.getXYDouble(x, y)
                if p > 0.0:
                    numberOfNonZeroPixels += 1
                    tmp = Distance.EuclideanDistance(cog.x, cog.y, x, y)
                    if tmp < minimum:
                        minimum = tmp
                    elif tmp > maximum:
                        maximum = tmp

        coeff = (maximum - minimum) / histoLength
        if numberOfNonZeroPixels == 0:
            return None

        for x in range(xdim):
            for y in range(ydim):
                p = img.getXYDouble(x, y)
                if p > 0.0:
                    tmp = Distance.EuclideanDistance(cog.x, cog.y, x, y)
                    histo[jint(jdiv(tmp - minimum, coeff))] += 1.0

        for i in range(len(histo)):
            histo[i] /= numberOfNonZeroPixels
        return histo

    @staticmethod
    def avgDistanceFromCoG(img) -> float:
        xdim = img.getXDim()
        ydim = img.getYDim()
        if img.getCDim() != 1:
            return -1.0

        cog = Tools.centerOfGravity(img)

        avgDistance = 0.0
        numberOfNonZeroPixels = 0
        for x in range(xdim):
            for y in range(ydim):
                p = img.getXYDouble(x, y)
                if p > 0.0:
                    numberOfNonZeroPixels += 1
                    avgDistance += Distance.EuclideanDistance(cog.x, cog.y, x, y)

        if numberOfNonZeroPixels != 0:
            return avgDistance / numberOfNonZeroPixels
        return -1.0

    @staticmethod
    def centerOfGravity(img):
        xdim = img.getXDim()
        ydim = img.getYDim()
        if img.getCDim() != 1:
            return None

        cog = Point(0, 0)
        cx = 0.0
        cy = 0.0

        volume = Tools.volume(img, 0)
        for x in range(xdim):
            for y in range(ydim):
                p = img.getXYDouble(x, y)
                if p > 0.0:
                    cx += x * p
                    cy += y * p

        # volume is 0 for an all-zero image; Java gives NaN, and
        # (int) NaN is 0, so the centre of gravity lands at (0, 0).
        cog.x = jint(jdiv(cx, volume))
        cog.y = jint(jdiv(cy, volume))
        return cog

    # ------------------------------------------------------------------
    @staticmethod
    def numberOfNonZeroPixels(img) -> int:
        if img.getCDim() != 1:
            return -1
        counter = 0
        for x in range(img.getXDim()):
            for y in range(img.getYDim()):
                if img.getXYByte(x, y) > 0:
                    counter += 1
        return counter

    @staticmethod
    def numberOfDifferentPixels(img1, img2) -> int:
        if not img1.hasSameDimensionsAs(img2):
            return -1
        counter = 0
        for x in range(img1.getXDim()):
            for y in range(img1.getYDim()):
                if img2.getXYByte(x, y) != img1.getXYByte(x, y):
                    counter += 1
        return counter

    @staticmethod
    def shiftOrigin(img):
        """Swap diagonal quadrants (an fftshift)."""
        output = img.newInstance(False)

        xdim = img.getXDim()
        ydim = img.getYDim()
        hx = idiv(xdim, 2)
        hy = idiv(ydim, 2)

        for x in range(hx):
            for y in range(hy):
                output.setXYDouble(x + hx, y + hy, img.getXYDouble(x, y))
        for x in range(hx):
            for y in range(hy, ydim):
                output.setXYDouble(x + hx, y - hy, img.getXYDouble(x, y))
        for x in range(hx, xdim):
            for y in range(hy):
                output.setXYDouble(x - hx, y + hy, img.getXYDouble(x, y))
        for x in range(hx, xdim):
            for y in range(hy, ydim):
                output.setXYDouble(x - hx, y - hy, img.getXYDouble(x, y))
        return output

    # ------------------------------------------------------------------
    @staticmethod
    def DftMagVol(d) -> float:
        n = len(d)
        real = np.zeros(n, dtype=np.float64)
        imag = np.zeros(n, dtype=np.float64)
        vol = 0.0
        for i in range(n):
            real[i] = imag[i] = 0.0
            for j in range(n):
                real[i] += d[j] * math.cos(-6.283185307179586 * j * i / n)
                imag[i] += d[j] * math.sin(-6.283185307179586 * j * i / n)
            real[i] /= math.sqrt(n)
            imag[i] /= math.sqrt(n)
        for i in range(n):
            vol += math.sqrt(real[i] * real[i] + imag[i] * imag[i])
        return vol

    @staticmethod
    def dftMag1d(d):
        n = len(d)
        real = np.zeros(n, dtype=np.float64)
        imag = np.zeros(n, dtype=np.float64)
        output = np.zeros(n, dtype=np.float64)
        for i in range(n):
            real[i] = imag[i] = 0.0
            for j in range(n):
                real[i] += d[j] * math.cos(-6.283185307179586 * j * i / n)
                imag[i] += d[j] * math.sin(-6.283185307179586 * j * i / n)
            real[i] /= math.sqrt(n)
            imag[i] /= math.sqrt(n)
        for i in range(n):
            output[i] = math.sqrt(real[i] * real[i] + imag[i] * imag[i])
        return output

    # ------------------------------------------------------------------
    @staticmethod
    def superpose(mask, support):
        tmp = support.newInstance(True)
        for x in range(support.getXDim()):
            for y in range(support.getYDim()):
                if not mask.getXYBoolean(x, y):
                    tmp.setXYCByte(x, y, 0, 0)
                    tmp.setXYCByte(x, y, 1, 0)
                    tmp.setXYCByte(x, y, 2, 0)
        return tmp

    @staticmethod
    def getAngleABC(a, b, c) -> float:
        ab = Point(b.x - a.x, b.y - a.y)
        cb = Point(b.x - c.x, b.y - c.y)

        dot = ab.x * cb.x + ab.y * cb.y

        abSqr = ab.x * ab.x + ab.y * ab.y
        cbSqr = cb.x * cb.x + cb.y * cb.y

        # A degenerate triangle makes abSqr or cbSqr zero.
        cosSqr = jdiv(jdiv(dot * dot, abSqr), cbSqr)

        cos2 = 2.0 * cosSqr - 1.0

        if cos2 >= 1.0:
            alpha2 = 0.0
        elif cos2 <= -1.0:
            alpha2 = 3.141592653589793
        else:
            alpha2 = jacos(cos2)

        rslt = alpha2 / 2.0
        rs = rslt * 180.0 / 3.141592653589793
        if dot < 0.0:
            rs = 180.0 - rs

        # ORIGINAL DEFECT: the cross-product determinant reads `cb.y` twice;
        # it should almost certainly be `ab.x * cb.y - ab.y * cb.x`. As
        # written the sign flip triggers on the wrong condition. Preserved.
        det = np.float32(ab.x * cb.y - ab.y * cb.y)
        if det < 0.0:
            rs = -rs
        return math.floor(rs + 0.5)

    # ------------------------------------------------------------------
    @staticmethod
    def centroid(points):
        p = Point(0, 0)
        for q in points:
            p.x += q.x
            p.y += q.y
        p.x = idiv(p.x, len(points))
        p.y = idiv(p.y, len(points))
        return p

    @staticmethod
    def drawLine(p1, p2):
        """Bresenham-ish line rasteriser.

        ORIGINAL DEFECT: the slope is computed as ``float m = dy / dx`` where
        dy and dx are ints, so the division truncates to an integer *before*
        being widened to float. Any line with |dy| < |dx| gets slope 0 and
        comes out horizontal. Preserved, including the float32 (not double)
        arithmetic that follows.
        """
        line = []

        x0, x1 = p1.x, p2.x
        y0, y1 = p1.y, p2.y

        dx = x1 - x0
        dy = y1 - y0

        line.append(Point(x0, y0))
        if abs(dx) > abs(dy):
            m = np.float32(idiv(dy, dx))
            b = np.float32(y0 - m * x0)
            dx = -1 if dx < 0 else 1
            while x0 != x1:
                x0 += dx
                line.append(Point(x0, _jroundf(m * np.float32(x0) + b)))
        elif dy != 0:
            m = np.float32(idiv(dx, dy))
            b = np.float32(x0 - m * y0)
            dy = -1 if dy < 0 else 1
            while y0 != y1:
                y0 += dy
                line.append(Point(_jroundf(m * np.float32(y0) + b), y0))
        return line

    # ------------------------------------------------------------------
    @staticmethod
    def Image2BufferedImage(img):
        raise NotImplementedError(
            "Tools.Image2BufferedImage depends on the JAI/AWT raster shims and "
            "is not part of the Tier 0/1 port; use the Pillow-based io package."
        )

    @staticmethod
    def BufferedImage2Image(bimage):
        raise NotImplementedError(
            "Tools.BufferedImage2Image depends on the JAI/AWT raster shims and "
            "is not part of the Tier 0/1 port; use the Pillow-based io package."
        )

    # `Tools.cleanupProcess(Process)` (src/vpt/util/Tools.java) has no Python
    # counterpart -- intentionally: it's dead code in the Java oracle itself
    # (zero callers anywhere in src/vpt), unrelated Process-lifecycle
    # plumbing rather than an image algorithm.


def _jroundf(x) -> int:
    """Java ``Math.round(float)`` -> int: round half toward +infinity."""
    f = math.floor(float(x))
    if float(x) - f >= 0.5:
        f += 1
    return int(f)

"""Port of ``vpt.algorithms.segmentation.MeanShift``: colour-space mean-
shift clustering (RGB, quantised to bytes) via random restarts, merging
newly-converged clusters into existing ones that are close enough.

**Not golden-tested, ever** -- this algorithm calls ``Math.random()``
directly (no seed anywhere in reach), so even two runs of the *Java*
oracle itself would disagree; there is no bit-exact target to verify
against. Ported for structural/API completeness and smoke-tested only, the
same treatment already given to ``linear.Canny`` for a different
undumpable reason (a real GUI window there; genuine non-determinism here).

**Preserved bug**, the same shape as the already-documented
``util.Tools.VAddC``/``VSub`` defect: ``vectorDifference`` allocates a
fresh zero array ``fark``, then computes into ``p1`` *in place*
(``p1[i] -= p2[i]``) instead of into ``fark``, and returns the untouched
(all-zero) ``fark``. Since Java arrays (and Python lists) are reference
types, this means ``vectorDifference(mean, tmpMean)`` silently mutates the
caller's own ``mean`` (subtracting ``tmpMean`` from it) as a side effect,
while its return value -- used only via ``Tools.EuclideanNorm(...)`` for
a "is this close enough to merge" check -- is always ``0.0``. Since
``0.0 < band/2.0`` is true for any positive bandwidth, this means the merge
check effectively always succeeds against the *first* existing cluster
checked, and ``mean`` gets corrupted (silently overwritten with
``mean - tmpMean``) as a side effect of merely checking. A very consequential
bug for anyone relying on this class's clustering quality, transcribed
exactly rather than fixed.

The Java's 256x256x256 lookup tables (``renkler``/``flags``/``flags2``,
one entry per representable RGB colour) are ported to NumPy arrays rather
than nested Python lists -- 16.7M-element nested lists would be
impractically slow and memory-heavy for what is a dense fixed-size lookup
table, the same reasoning ``Image`` storage already uses NumPy for.

The Java's ``main`` (a manual demo: load a hardcoded local JPEG path via
``io.Load``, run ``flatzones.color.ColorQFZSoille``, display through
``Display2D``) is not ported -- ``Load``/``Display2D`` are GUI/IO-dependent
and ``ColorQFZSoille`` is unported (Tier 2), and it's a developer smoke
test, not part of the public API surface reachable via ``invoke()``."""

from __future__ import annotations

import random
import sys
import traceback

import numpy as np

from ..._javacompat import Point, idiv, jint, jround
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.Tools import Tools


def _colorIndex(v: float) -> int:
    k = jint(jround(v * 255.0))
    if k < 0 or k > 255:
        raise IndexError(f"color component {v} out of [0,1] range")
    return k


class _VectorPixel:
    def __init__(self, vector: list[float], x: int, y: int):
        self.x = x
        self.y = y
        self.vector = vector

    def getVector(self) -> list[float]:
        return list(self.vector)

    def getX(self) -> int:
        return self.x

    def getY(self) -> int:
        return self.y


class MeanShift(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.band: float | None = None
        self.inputFields = "input, band"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        size = xdim * ydim

        bandwidth = self.band * self.band

        esik = 0.001 * bandwidth

        pixels: list[_VectorPixel] = []

        flags = np.zeros((256, 256, 256), dtype=np.uint8)
        flags2 = np.zeros((256, 256, 256), dtype=np.uint8)

        renkler = np.zeros((256, 256, 256), dtype=np.int64)

        points: list[list[Point] | None] = [None] * size

        renkSyc = 1
        for x in range(xdim):
            for y in range(ydim):
                p = self.input.getVXYDouble(x, y)
                i0, i1, i2 = _colorIndex(p[0]), _colorIndex(p[1]), _colorIndex(p[2])
                if renkler[i0, i1, i2] == 0:
                    d = [Point(x, y)]
                    points[renkSyc] = d
                    renkler[i0, i1, i2] = renkSyc
                    renkSyc += 1
                    pixels.append(_VectorPixel(list(p), x, y))
                else:
                    index = renkler[i0, i1, i2]
                    points[index].append(Point(x, y))

        size = len(pixels)
        print(f"Renk sayisi {size}", file=sys.stderr)

        clusters: list[list[_VectorPixel]] = []
        means: list[list[float]] = []

        islenmisler = 0
        while islenmisler < size:
            tmpIndex = -1
            for _ in range(10):
                tmp = jint(np.floor(random.random() * size))

                coord = pixels[tmp].getVector()
                if flags[_colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])] == 0:
                    tmpIndex = tmp
                    break

            if tmpIndex == -1:
                for i in range(size):
                    coord = pixels[i].getVector()
                    if (
                        flags[_colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])]
                        == 0
                    ):
                        tmpIndex = i
                        break

            if tmpIndex == -1:
                print(f"Uyari : {islenmisler} {size}", file=sys.stderr)
                break

            mean = pixels[tmpIndex].getVector()

            cluster: list[_VectorPixel] = []
            while True:
                eskiMean = mean
                mean = [0.0, 0.0, 0.0]

                syc = 0
                for i in range(size):
                    if Tools.euclideanDistance(eskiMean, pixels[i].vector) < bandwidth:
                        mean[0] += pixels[i].vector[0]
                        mean[1] += pixels[i].vector[1]
                        mean[2] += pixels[i].vector[2]

                        syc += 1

                        coord = pixels[i].getVector()
                        c0, c1, c2 = _colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])
                        if flags2[c0, c1, c2] == 0 and flags[c0, c1, c2] == 0:
                            cluster.append(pixels[i])
                            flags2[c0, c1, c2] = 1

                mean[0] /= syc
                mean[1] /= syc
                mean[2] /= syc
                if not Tools.euclideanDistance(mean, eskiMean) >= esik:
                    break

            mergeWith = -1
            tmpSize = len(clusters)
            for i in range(tmpSize):
                tmpMean = means[i]
                if Tools.EuclideanNorm(self._vectorDifference(mean, tmpMean)) < self.band / 2.0:
                    mergeWith = i
                    break

            tmpSize = len(cluster)
            for i in range(tmpSize):
                vp = cluster[i]
                coord = vp.getVector()
                flags2[_colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])] = 0

            if mergeWith > -1:
                mergedMean = means[mergeWith]

                means[mergeWith] = self._vectorAverage(mean, mergedMean)

                tmpSize = len(cluster)

                mergedCluster = clusters[mergeWith]
                for i in range(tmpSize):
                    p = cluster[i]
                    coord = p.getVector()
                    if flags[_colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])] == 0:
                        mergedCluster.append(p)
                        islenmisler += 1
            else:
                means.append(mean)
                clusters.append(cluster)
                islenmisler += len(cluster)

            for i in range(tmpSize):
                vp = cluster[i]
                coord = vp.getVector()
                flags[_colorIndex(coord[0]), _colorIndex(coord[1]), _colorIndex(coord[2])] = 1

            print(
                f"{islenmisler} {size} percent classified : "
                f"{idiv(100 * islenmisler, size)}% + cluster sayisi {len(clusters)}",
                file=sys.stderr,
            )
        print(
            "Siniflandirma tamamlandi sira goruntulemede.."
            "eskiden cok uzun suruyodu..simdiki gencler cok sansli peeeh",
            file=sys.stderr,
        )

        self.output = ByteImage(xdim, ydim, 3)
        size = len(clusters)

        syc2 = 0
        for i in range(size):
            cluster = clusters[i]

            renk = means[i]
            for j in range(len(cluster)):
                p = cluster[j]

                d = p.vector

                index = renkler[_colorIndex(d[0]), _colorIndex(d[1]), _colorIndex(d[2])]
                for k in range(len(points[index])):
                    pixel = points[index][k]
                    self.output.setVXYDouble(pixel.x, pixel.y, renk)
                    syc2 += 1
        print(f"siniflandirilan piksel sayisi {syc2}", file=sys.stderr)

    def _vectorDifference(self, p1: list[float], p2: list[float]) -> list[float] | None:
        if len(p1) != len(p2):
            print("Incompatible vector lengths", file=sys.stderr)
            return None
        fark = [0.0] * len(p1)
        for i in range(len(p1)):
            p1[i] -= p2[i]
        return fark

    def _vectorAverage(self, p1: list[float], p2: list[float]) -> list[float] | None:
        if len(p1) != len(p2):
            print("Incompatible vector lengths", file=sys.stderr)
            return None
        ort = [0.0] * len(p1)
        for i in range(len(p1)):
            ort[i] = (p1[i] + p2[i]) / 2.0
        return ort

    @staticmethod
    def invoke(image: Image, band: float) -> Image | None:
        try:
            return MeanShift().preprocess(image, band)
        except GlobalException:
            traceback.print_exc()
        return None

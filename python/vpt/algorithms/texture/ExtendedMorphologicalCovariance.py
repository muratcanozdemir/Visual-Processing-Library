"""Port of ``vpt.algorithms.texture.ExtendedMorphologicalCovariance``: a
large bundle of morphological-covariance-style feature histograms computed
at ``size`` circular scales (radius ``1..size``), all normalised at the end
by dividing every ``output`` slot by ``input.getSize()``.

Two families of per-scale intermediate images feed the seven histogram
sub-methods:

- ``tmpsO``/``tmpsC``: per-scale erosion/dilation (then, after
  ``histoED``, re-dilated/re-eroded by the same SE -- an opening/closing
  via two half-steps) -- feeds ``histoED`` and ``histoOCDouble``.
- ``tmpsO2``/``tmpsC2``: per-scale *lists* of openings/closings, one per
  ``FlatSE.pointCouples(radius)`` element (a "rotating" 2-point SE family)
  -- feeds ``locHistoOC``... no, ``locHistoOC`` uses the first family; the
  ``rit*`` methods (``ritMaxMin``, ``ritMinMin``, ``ritMedMed``,
  ``ritVarVar``, ``ritAngleDiffAngleDiff``) use this second family, each
  reducing the per-scale point-couple list to one image via a different
  statistic (max/min, min/min, median/median, variance/variance, and a
  circular-angle-spread-derived value) before histogramming which scale
  wins per pixel.

``ritMaxMin``/``ritMinMin`` are asymmetric by name only for the first:
``ritMaxMin`` reduces the opening family by ``Maximum`` and the closing
family by ``Minimum``; every other ``rit*`` method (including
``ritMinMin``) reduces both families the same way. Ported verbatim --
this is the source algorithm's actual design (there is no corresponding
``ritMaxMax``), not a transcription slip.

The output-size formula and the ``histoED``/``histoOCDouble``/
``locHistoOC``/``ritMaxMin``/``ritMinMin``/``ritMedMed``/``ritVarVar``/
``ritAngleDiffAngleDiff`` offset bookkeeping in ``execute`` reproduce the
Java arithmetic exactly, including the intermediate per-method slot counts
that don't individually line up with the size formula's own term grouping
(both reduce to the same ``18*size + 2`` total, which is what actually
matters).
"""

from __future__ import annotations

import math
import traceback

import numpy as np

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image
from ...util.se.FlatSE import FlatSE
from ..arithmetic.Maximum import Maximum
from ..arithmetic.Minimum import Minimum
from ..mm.gray.GClosing import GClosing
from ..mm.gray.GDilation import GDilation
from ..mm.gray.GErosion import GErosion
from ..mm.gray.GOpening import GOpening

_DOUBLE_MAX = 1.7976931348623157e308


def _runningExtreme(stack: np.ndarray, init: float, greater: bool) -> np.ndarray:
    """Vectorized equivalent of the shape repeated (with small variations)
    across every histogram sub-method below: ``best = init; idx = 0; for k
    in range(n): if (d > best if greater else d < best): idx = k; best =
    d`` -- evaluated once per output pixel over the leading axis of
    ``stack`` (shape ``(n, ...)``). Returns just the winning index array
    (``int64``), since every call site here immediately ``bincount``s it.

    Preserves the Java-sentinel tie rule exactly: the update only fires on
    a *strict* ``>``/``<`` against the running extreme, seeded at ``init``
    (``0.0`` for the max-style scans, ``DOUBLE_MAX`` for the min-style
    ones) rather than ``+-inf`` -- so a candidate merely *tying* the
    current best does not overwrite it (first-strict-win, not last), and a
    pixel where nothing ever beats the seed keeps index ``0``, matching
    the scalar loop's ``indexO = 0`` default exactly. No NaN special-
    casing is needed: Java's ``>``/``<`` and NumPy's are both false for
    any NaN operand, so a NaN candidate is simply never selected, on
    either side identically."""
    n = stack.shape[0]
    best = np.full(stack.shape[1:], init, dtype=np.float64)
    idx = np.zeros(stack.shape[1:], dtype=np.int64)
    for k in range(n):
        d = stack[k]
        cond = d > best if greater else d < best
        idx = np.where(cond, k, idx)
        best = np.where(cond, d, best)
    return idx


class ExtendedMorphologicalCovariance(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.size: int | None = None
        self._imageSize: int = 0
        self.inputFields = "input, size"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()
        if cdim != 1:
            raise GlobalException("Sorry, only grayscale images for now...")

        size = self.size
        self.output = [0.0] * (
            2 * size * 2 + size * 2 + (size + 1) * 2 + 2 * size + 2 * size + 2 * size + 2 * size + 2 * size
        )

        self._imageSize = self.input.getSize()

        tmpsO: list[Image | None] = [None] * size
        tmpsC: list[Image | None] = [None] * size

        ses: list[FlatSE | None] = [None] * size
        for i in range(1, size + 1):
            ses[i - 1] = FlatSE.circle(i)

            tmpsO[i - 1] = GErosion.invoke(self.input, ses[i - 1])
            tmpsC[i - 1] = GDilation.invoke(self.input, ses[i - 1])

        offset = 0

        self._histoED(tmpsO, tmpsC, ses, offset)
        for i in range(1, size + 1):
            tmpsO[i - 1] = GDilation.invoke(tmpsO[i - 1], ses[i - 1])
            tmpsC[i - 1] = GErosion.invoke(tmpsC[i - 1], ses[i - 1])
        offset += size * 2

        self._histoOCDouble(tmpsO, tmpsC, ses, offset)

        offset += 4 * size

        self._locHistoOC(tmpsO, tmpsC, ses, offset)

        offset += 2 * (size + 1)

        tmpsO2: list[list[Image]] = [[] for _ in range(size)]
        tmpsC2: list[list[Image]] = [[] for _ in range(size)]
        for i in range(1, size + 1):
            sesPc = FlatSE.pointCouples(i)
            tmpsO2[i - 1] = []
            tmpsC2[i - 1] = []
            for k in range(len(sesPc)):
                tmpsO2[i - 1].append(GOpening.invoke(self.input, sesPc[k]))
                tmpsC2[i - 1].append(GClosing.invoke(self.input, sesPc[k]))

        self._ritMaxMin(tmpsO2, tmpsC2, offset)
        offset += size * 2

        self._ritMinMin(tmpsO2, tmpsC2, offset)
        offset += size * 2

        self._ritMedMed(tmpsO2, tmpsC2, offset)
        offset += size * 2

        self._ritVarVar(tmpsO2, tmpsC2, offset)
        offset += size * 2

        self._ritAngleDiffAngleDiff(tmpsO2, tmpsC2, offset)
        for i in range(len(self.output)):
            self.output[i] = jdiv(self.output[i], self._imageSize)

    def _ritAngleDiffAngleDiff(
        self, tmpsO2: list[list[Image]], tmpsC2: list[list[Image]], offset: int
    ) -> None:
        """Vectorized. Two nested instances of the running-extreme shape:
        for each scale ``j``, the per-pixel ``indexMax``/``indexMin`` over
        the ``n`` point-couple images is a running-argmax (seed ``0.0``)
        and a running-argmin (seed ``DOUBLE_MAX``) over the same stack,
        via :func:`_runningExtreme`; then the outer per-pixel reduction
        over the ``size`` per-scale ``diff`` images is a running-argmin
        (seed ``DOUBLE_MAX``) of its own. ``diff * math.pi / n`` is kept
        in the same left-to-right grouping (``(diff * pi) / n``) so the
        floating-point result matches the scalar loop bit-for-bit."""
        size = self.size
        tmpsO = [None] * size
        tmpsC = [None] * size
        for j in range(size):
            n = len(tmpsO2[j])

            stackO = np.stack([tmpsO2[j][k].getDoubleArray() for k in range(n)])
            idxMaxO = _runningExtreme(stackO, 0.0, True)
            idxMinO = _runningExtreme(stackO, _DOUBLE_MAX, False)
            diffO = np.abs(idxMaxO - idxMinO).astype(np.float64)
            tmpsO[j] = diffO * math.pi / n

            stackC = np.stack([tmpsC2[j][k].getDoubleArray() for k in range(n)])
            idxMaxC = _runningExtreme(stackC, 0.0, True)
            idxMinC = _runningExtreme(stackC, _DOUBLE_MAX, False)
            diffC = np.abs(idxMaxC - idxMinC).astype(np.float64)
            tmpsC[j] = diffC * math.pi / n

        dO_stack = np.stack(tmpsO)
        dC_stack = np.stack(tmpsC)
        idxO = _runningExtreme(dO_stack, _DOUBLE_MAX, False)
        idxC = _runningExtreme(dC_stack, _DOUBLE_MAX, False)

        countsO = np.bincount(idxO, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsC[k])

    def _ritVarVar(
        self, tmpsO2: list[list[Image]], tmpsC2: list[list[Image]], offset: int
    ) -> None:
        size = self.size
        tmpsO: list[Image] = [None] * size
        tmpsC: list[Image] = [None] * size
        for j in range(size):
            tmpsO[j] = DoubleImage(self.input, False)
            tmpsC[j] = DoubleImage(self.input, False)

            n = len(tmpsO2[j])
            for i in range(self._imageSize):
                mean = 0.0
                var = 0.0
                for k in range(n):
                    mean += tmpsO2[j][k].getDouble(i)
                mean = jdiv(mean, n)
                for k in range(n):
                    var += (mean - tmpsO2[j][k].getDouble(i)) * (mean - tmpsO2[j][k].getDouble(i))
                var = jdiv(var, n)

                tmpsO[j].setDouble(i, var)

                mean = 0.0
                var = 0.0
                for k in range(n):
                    mean += tmpsC2[j][k].getDouble(i)
                mean = jdiv(mean, n)
                for k in range(n):
                    var += (mean - tmpsC2[j][k].getDouble(i)) * (mean - tmpsC2[j][k].getDouble(i))
                var = jdiv(var, n)

                tmpsC[j].setDouble(i, var)

        for i in range(self._imageSize):
            indexO = 0
            maxO = 0.0

            indexC = 0
            maxC = 0.0
            for j in range(size):
                dO = tmpsO[j].getDouble(i)
                dC = tmpsC[j].getDouble(i)

                tmp = dO
                if tmp > maxO:
                    maxO = tmp
                    indexO = j

                tmp = dC
                if tmp > maxC:
                    maxC = tmp
                    indexC = j

            self.output[offset + indexO] += 1.0
            self.output[offset + size + indexC] += 1.0

    def _ritMedMed(
        self, tmpsO2: list[list[Image]], tmpsC2: list[list[Image]], offset: int
    ) -> None:
        """Vectorized. The per-pixel median-of-``n`` reuses the
        ``GRankFilter`` (section 6.3) pattern: stack the ``n`` point-couple
        images and ``np.sort`` along that axis, then index the middle
        (``n // 2``, matching Java's integer-truncating ``n/2`` for both
        even and odd ``n``) row -- the rank-``n//2`` order statistic of a
        multiset doesn't depend on gather order, so this is bit-exact with
        the scalar gather+``list.sort()`` for any finite input (same
        NaN-ordering caveat as ``GRankFilter``, not exercised by golden
        coverage). The final per-pixel reduction over the ``size`` medians
        is the same running-argmax shape as the rest of this file."""
        size = self.size
        tmpsO = [None] * size
        tmpsC = [None] * size
        for j in range(size):
            n = len(tmpsO2[j])

            stackO = np.stack([tmpsO2[j][k].getDoubleArray() for k in range(n)])
            stackO.sort(axis=0)
            tmpsO[j] = stackO[n // 2]

            stackC = np.stack([tmpsC2[j][k].getDoubleArray() for k in range(n)])
            stackC.sort(axis=0)
            tmpsC[j] = stackC[n // 2]

        dO_stack = np.stack(tmpsO)
        dC_stack = np.stack(tmpsC)
        idxO = _runningExtreme(dO_stack, 0.0, True)
        idxC = _runningExtreme(dC_stack, 0.0, True)

        countsO = np.bincount(idxO, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsC[k])

    def _ritMinMin(
        self, tmpsO2: list[list[Image]], tmpsC2: list[list[Image]], offset: int
    ) -> None:
        """The per-scale ``Minimum.invoke`` reduction (arithmetic package,
        section 7.1, not yet vectorized) is left as-is -- only the final
        per-pixel running-argmax over the ``size`` reduced images is
        vectorized here, via :func:`_runningExtreme`."""
        size = self.size
        tmpsO: list[Image] = [None] * size
        tmpsC: list[Image] = [None] * size
        for i in range(size):
            n = len(tmpsO2[i])
            tmpsO[i] = tmpsO2[i][0]
            for j in range(1, n):
                tmpsO[i] = Minimum.invoke(tmpsO[i], tmpsO2[i][j])

            tmpsC[i] = tmpsC2[i][0]
            for j in range(1, n):
                tmpsC[i] = Minimum.invoke(tmpsC[i], tmpsC2[i][j])

        dO_stack = np.stack([tmpsO[j].getDoubleArray() for j in range(size)])
        dC_stack = np.stack([tmpsC[j].getDoubleArray() for j in range(size)])
        idxO = _runningExtreme(dO_stack, 0.0, True)
        idxC = _runningExtreme(dC_stack, 0.0, True)

        countsO = np.bincount(idxO, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsC[k])

    def _ritMaxMin(
        self, tmpsO2: list[list[Image]], tmpsC2: list[list[Image]], offset: int
    ) -> None:
        """Same shape as :meth:`_ritMinMin`; only the opening family is
        reduced by ``Maximum`` here instead of ``Minimum`` (see this
        module's docstring on the asymmetric naming)."""
        size = self.size
        tmpsO: list[Image] = [None] * size
        tmpsC: list[Image] = [None] * size
        for i in range(size):
            n = len(tmpsO2[i])
            tmpsO[i] = tmpsO2[i][0]
            for j in range(1, n):
                tmpsO[i] = Maximum.invoke(tmpsO[i], tmpsO2[i][j])

            tmpsC[i] = tmpsC2[i][0]
            for j in range(1, n):
                tmpsC[i] = Minimum.invoke(tmpsC[i], tmpsC2[i][j])

        dO_stack = np.stack([tmpsO[j].getDoubleArray() for j in range(size)])
        dC_stack = np.stack([tmpsC[j].getDoubleArray() for j in range(size)])
        idxO = _runningExtreme(dO_stack, 0.0, True)
        idxC = _runningExtreme(dC_stack, 0.0, True)

        countsO = np.bincount(idxO, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsC[k])

    def _locHistoOC(
        self, tmpsO: list[Image], tmpsC: list[Image], ses: list[FlatSE], offset: int
    ) -> None:
        """Vectorized. This is a different shape from the rest of the
        file: for each pixel, count across the ``size`` scales how many
        times the scale image disagrees with the original
        (``Tools.cmpr(d, d1) != 0``, a +-1e-8 epsilon compare), then
        histogram that 0..size count. ``Tools.cmpr``'s ``!= 0`` branch is
        reproduced as "the difference is *not* within +-1e-8" -- the sign
        of the difference is irrelevant to a ``!= 0`` test, so ``dO -
        original`` vs. the scalar loop's ``original - dO`` (Java's
        ``cmpr(d, d1)`` argument order) give identical mismatch masks.
        NaN needs no special-casing either: both comparisons in the
        ``-1e-8 <= diff <= 1e-8`` in-range test are false for a NaN
        ``diff``, so it falls through to "mismatch", matching
        ``Tools.cmpr``'s own NaN fallthrough (neither branch taken,
        function returns -1, i.e. != 0) exactly. No ``ByteImage``
        intermediate is needed -- the counts are plain per-pixel integers
        in ``[0, size]``, directly ``bincount``-able."""
        size = self.size
        original = self.input.getDoubleArray()

        countO = np.zeros(original.shape, dtype=np.int64)
        countC = np.zeros(original.shape, dtype=np.int64)
        for i in range(size):
            diffO = tmpsO[i].getDoubleArray() - original
            mismatchO = ~((diffO >= -1.0e-8) & (diffO <= 1.0e-8))
            countO += mismatchO

            diffC = tmpsC[i].getDoubleArray() - original
            mismatchC = ~((diffC >= -1.0e-8) & (diffC <= 1.0e-8))
            countC += mismatchC

        countsO = np.bincount(countO, minlength=size + 1)
        countsC = np.bincount(countC, minlength=size + 1)
        for k in range(size + 1):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + 1 + k] += float(countsC[k])

    def _histoED(
        self, tmpsO: list[Image], tmpsC: list[Image], ses: list[FlatSE], offset: int
    ) -> None:
        """Vectorized: build the ``(size, imageSize)`` stack of per-scale
        ``jdiv(original - dO, |coords|)`` values (the divisor is always a
        positive integer, so ``jdiv``'s zero-divisor branch never fires
        and plain NumPy division matches it exactly), then reduce with
        :func:`_runningExtreme`."""
        size = self.size
        original = self.input.getDoubleArray()
        divisors = np.array(
            [len(ses[j].getCoords()) for j in range(size)], dtype=np.float64
        ).reshape(size, 1)

        dO_stack = np.stack([tmpsO[j].getDoubleArray() for j in range(size)])
        dC_stack = np.stack([tmpsC[j].getDoubleArray() for j in range(size)])

        tmpO_stack = (original[None, :] - dO_stack) / divisors
        tmpC_stack = (dC_stack - original[None, :]) / divisors

        idxO = _runningExtreme(tmpO_stack, 0.0, True)
        idxC = _runningExtreme(tmpC_stack, 0.0, True)

        countsO = np.bincount(idxO, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsC[k])

    def _histoOCDouble(
        self, tmpsO: list[Image], tmpsC: list[Image], ses: list[FlatSE], offset: int
    ) -> None:
        """Vectorized. The first pass is the same running-argmax shape as
        :meth:`_histoED`, on ``abs(...)`` values. The second pass repeats
        it with the pixel's own first-pass winner excluded (``if j !=
        indexO``) -- reproduced by overwriting each pixel's winning
        column with ``-1.0`` before rerunning :func:`_runningExtreme`:
        every real candidate is an absolute value (``>= 0``), so ``-1.0``
        can never satisfy the running-argmax's strict ``>`` against a
        sentinel that starts at ``0.0`` and only grows, meaning the masked
        scale can never be picked -- exactly equivalent to the scalar
        loop never comparing it at all, including when it reproduces
        Java's unconditional ``j != indexO`` masking even for an
        ``indexO`` that was never legitimately updated (still ``0`` by
        default)."""
        size = self.size
        original = self.input.getDoubleArray()
        n = original.shape[0]
        divisors = np.array(
            [len(ses[j].getCoords()) for j in range(size)], dtype=np.float64
        ).reshape(size, 1)

        dO_stack = np.stack([tmpsO[j].getDoubleArray() for j in range(size)])
        dC_stack = np.stack([tmpsC[j].getDoubleArray() for j in range(size)])

        absO = np.abs(original[None, :] - dO_stack) / divisors
        absC = np.abs(dC_stack - original[None, :]) / divisors

        idxO = _runningExtreme(absO, 0.0, True)
        idxC = _runningExtreme(absC, 0.0, True)

        pix = np.arange(n)
        absO2 = absO.copy()
        absO2[idxO, pix] = -1.0
        absC2 = absC.copy()
        absC2[idxC, pix] = -1.0

        idxO2 = _runningExtreme(absO2, 0.0, True)
        idxC2 = _runningExtreme(absC2, 0.0, True)

        countsO = np.bincount(idxO, minlength=size)
        countsO2 = np.bincount(idxO2, minlength=size)
        countsC = np.bincount(idxC, minlength=size)
        countsC2 = np.bincount(idxC2, minlength=size)
        for k in range(size):
            self.output[offset + k] += float(countsO[k])
            self.output[offset + size + k] += float(countsO2[k])
            self.output[offset + 2 * size + k] += float(countsC[k])
            self.output[offset + 3 * size + k] += float(countsC2[k])

    @staticmethod
    def invoke(image: Image, size: int) -> list[float] | None:
        try:
            return ExtendedMorphologicalCovariance().preprocess(image, size)
        except GlobalException:
            traceback.print_exc()
        return None

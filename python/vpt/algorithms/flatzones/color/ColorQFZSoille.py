"""Port of ``vpt.algorithms.flatzones.color.ColorQFZSoille``: the RGB
marginal analogue of ``flatzones.gray.GrayQFZSoille`` -- same priority-flood
structure (``TreeMap``-as-priority-queue ported the same way, see that
file's docstring), but each channel's byte difference is tracked
separately and the zone's "range level" used for priority-queue ordering
and comparisons is ``maxDim`` (the *largest* of the three per-channel
differences), not a single scalar.

**Two compounding preserved bugs, bytecode-confirmed** (`javap -l` mapping
each ``iastore`` back to its declared local by slot): the local array
``diff`` (``self._diff`` here) is declared but **never written anywhere in
the method** -- every ``iastore`` in the whole body targets ``mincc``,
``maxcc``, or ``rlval``, confirmed by walking every store site's preceding
`aload <slot>` -- so the omega/range-budget check
``omega < diff[0] or omega < diff[1] or omega < diff[2]`` is dead: `diff`
stays all-zero forever, and since `omega` is validated non-negative this
condition can never be true. The *intended* check was almost certainly
against the freshly computed per-channel range, which the Java computes
into `maxcc` itself one statement earlier (`maxcc[i] -= mincc[i]`) and then
never reads back out under its new meaning. That in turn is bug #2: because
`maxcc` is overwritten in place with `maxcc - mincc` rather than written to
a separate range variable, the *next* point popped from the queue compares
its channel value against this already-decremented "range" instead of the
zone's true running maximum, corrupting every subsequent max/min update for
the rest of that zone's growth. Both reproduced verbatim: `self._diff`
stays `[0, 0, 0]`, and `self._maxcc[i] -= self._mincc[i]` mutates the same
list the running-max ternary reads from on the next iteration."""

from __future__ import annotations

import sys
import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class ColorQFZSoille(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: IntegerImage | None = None
        self.alpha: int | None = None
        self.omega: int | None = None
        self.neighbors4 = [Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1)]
        self.neighbors8 = list(Tools.N)
        self.inputFields = "input,alpha,omega"
        self.outputFields = "output"

    def execute(self) -> None:
        xdim = self.input.getXDim()
        ydim = self.input.getYDim()

        N = self.neighbors8
        if self.input.getCDim() != 3:
            raise GlobalException("This implementation is only for color images.")
        if self.alpha < 0 or self.alpha > 254 or self.omega < 0 or self.omega > 254:
            raise GlobalException("Arguments out of range.")

        lbl = IntegerImage(xdim, ydim, 1)
        lbl.fill(0)

        rl = ByteImage(xdim, ydim, 1)
        rl.fill(255)

        mincc = [0, 0, 0]
        maxcc = [0, 0, 0]
        rlval = [0, 0, 0]
        diff = [0, 0, 0]

        stack: list[Point] = []

        pq: dict[int, list[Point]] = {}

        lblval = 1
        for x in range(xdim):
            for y in range(ydim):
                if lbl.getXYInt(x, y) == 0:
                    lbl.setXYInt(x, y, lblval)

                    fp = self.input.getVXYByte(x, y)

                    maxcc[0] = fp[0]
                    maxcc[1] = fp[1]
                    maxcc[2] = fp[2]

                    mincc[0] = fp[0]
                    mincc[1] = fp[1]
                    mincc[2] = fp[2]

                    rlcrt = self.alpha
                    for n in N:
                        qx = x + n.x
                        qy = y + n.y
                        if 0 <= qx < xdim and 0 <= qy < ydim:
                            fq = self.input.getVXYByte(qx, qy)

                            rlval[0] = abs(fp[0] - fq[0])
                            rlval[1] = abs(fp[1] - fq[1])
                            rlval[2] = abs(fp[2] - fq[2])
                            if lbl.getXYInt(qx, qy) > 0:
                                if rlcrt >= rlval[0] and rlcrt >= rlval[1] and rlcrt >= rlval[2]:
                                    rlcrt = self._maxDim(rlval) - 1
                            elif rlval[0] <= rlcrt and rlval[1] <= rlcrt and rlval[2] <= rlcrt:
                                rl.setXYByte(qx, qy, self._maxDim(rlval))
                                m = self._maxDim(rlval)
                                if m in pq:
                                    pq[m].append(Point(qx, qy))
                                else:
                                    pq[m] = [Point(qx, qy)]

                    rcrt = min(pq) if pq else 0

                    while pq:
                        datumPrio = min(pq)
                        tmp = pq[datumPrio]
                        datumP = tmp.pop(0)
                        if not tmp:
                            del pq[datumPrio]

                        if lbl.getXYInt(datumP.x, datumP.y) <= 0:
                            if datumPrio > rcrt:
                                while stack:
                                    sPoint = stack.pop()
                                    lbl.setXYInt(sPoint.x, sPoint.y, lblval)
                                rcrt = datumPrio
                                # Java: `if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}` --
                                # empty body, no effect.
                            else:
                                stack.append(datumP)

                                fDatumP = self.input.getVXYByte(datumP.x, datumP.y)

                                mincc[0] = fDatumP[0] if mincc[0] > fDatumP[0] else mincc[0]
                                mincc[1] = fDatumP[1] if mincc[1] > fDatumP[1] else mincc[1]
                                mincc[2] = fDatumP[2] if mincc[2] > fDatumP[2] else mincc[2]

                                maxcc[0] = fDatumP[0] if maxcc[0] < fDatumP[0] else maxcc[0]
                                maxcc[1] = fDatumP[1] if maxcc[1] < fDatumP[1] else maxcc[1]
                                maxcc[2] = fDatumP[2] if maxcc[2] < fDatumP[2] else maxcc[2]

                                maxcc[0] -= mincc[0]
                                maxcc[1] -= mincc[1]
                                maxcc[2] -= mincc[2]

                                if (
                                    self.omega < diff[0]
                                    or self.omega < diff[1]
                                    or self.omega < diff[2]
                                    or rcrt > rlcrt
                                ):
                                    for pp in stack:
                                        rl.setXYByte(pp.x, pp.y, 255)
                                    stack.clear()

                                    for k in sorted(pq):
                                        for p in pq[k]:
                                            rl.setXYByte(p.x, p.y, 255)
                                    pq.clear()
                                    break

                                for n in N:
                                    qx = datumP.x + n.x
                                    qy = datumP.y + n.y
                                    if 0 <= qx < xdim and 0 <= qy < ydim:
                                        fq = self.input.getVXYByte(qx, qy)

                                        rlval[0] = abs(fDatumP[0] - fq[0])
                                        rlval[1] = abs(fDatumP[1] - fq[1])
                                        rlval[2] = abs(fDatumP[2] - fq[2])

                                        lblq = lbl.getXYInt(qx, qy)
                                        if (
                                            lblq > 0
                                            and lblq != lblval
                                            and rlcrt >= rlval[0]
                                            and rlcrt >= rlval[1]
                                            and rlcrt >= rlval[2]
                                        ):
                                            rlcrt = self._maxDim(rlval) - 1
                                            if rcrt > rlcrt:
                                                for pp in stack:
                                                    rl.setXYByte(pp.x, pp.y, 255)
                                                stack.clear()

                                                for k in sorted(pq):
                                                    for p in pq[k]:
                                                        rl.setXYByte(p.x, p.y, 255)
                                                pq.clear()
                                                break
                                        else:
                                            rlq = rl.getXYByte(qx, qy)
                                            if (
                                                rlval[0] <= rlcrt
                                                or rlval[1] <= rlcrt
                                                or rlval[2] <= rlcrt
                                            ) and (
                                                rlval[0] < rlq or rlval[1] < rlq or rlval[2] < rlq
                                            ):
                                                if (
                                                    rlval[0] < rlq
                                                    and rlval[1] < rlq
                                                    and rlval[2] < rlq
                                                ):
                                                    rl.setXYByte(qx, qy, self._maxDim(rlval))
                                                    m = self._maxDim(rlval)
                                                    if m in pq:
                                                        pq[m].append(Point(qx, qy))
                                                    else:
                                                        pq[m] = [Point(qx, qy)]

                    while stack:
                        sPoint = stack.pop()
                        lbl.setXYInt(sPoint.x, sPoint.y, lblval)

                    lblval += 1

        print(f"Marginal Soille : Number of labels {lblval - 1}", file=sys.stderr)

        self.output = lbl

    @staticmethod
    def _maxDim(d: list[int]) -> int:
        m = 0
        for v in d:
            if v > m:
                m = v
        return m

    @staticmethod
    def _minDim(d: list[int]) -> int:
        m = 2147483647
        for v in d:
            if v < m:
                m = v
        return m

    @staticmethod
    def invoke(img: Image, alpha: int, omega: int) -> Image | None:
        try:
            return ColorQFZSoille().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.flatzones.gray.GrayQFZSoille``: a Soille-style
priority-flood quasi-flat-zone labelling. Unlike ``GrayQFZ``'s grow-then-
retry-with-smaller-alpha approach, this grows each zone outward strictly in
order of increasing "range level" (``rlval``, the abs difference to the
zone's already-known border), backtracking (clearing the pending-point
queue and resetting the in-progress zone's border to unassigned) whenever
that would blow either the tolerance ``alpha`` or the range budget
``omega``.

``java.util.TreeMap<Integer, ArrayList<Point>>`` (used as a priority queue:
``firstKey``/``pollFirstEntry`` for the minimum-key bucket, each bucket a
FIFO of same-priority points) is ported as a plain ``dict[int, list[Point]]``
plus ``min()``/``sorted()`` on the (small, ``0..254``-bounded) key set,
rather than pulling in a third-party sorted-container dependency for one
algorithm -- the same "use a plain builtin for a Java collection" precedent
as ``Stack`` -> ``list`` and ``LinkedList`` -> ``collections.deque``
elsewhere in this port.

``neighbors4`` is a dead public field (declared, never read -- only
``neighbors8`` is used, aliased to local ``N``); kept for structural parity.
One line has a **preserved no-op**: an empty-bodied
``if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}`` right after ``rcrt =
datumPrio`` in the Java -- a decompiler-rendered dead check with zero
side effects either way, so it's simply not translated into code (there is
nothing for it to do). A second occurrence in the Java of the condition
``rlval < rl.getXYByte(qx, qy)`` written twice in the same ``&&`` chain is
collapsed to one evaluation here -- calling a getter with no side effects a
second time changes nothing."""

from __future__ import annotations

import traceback

from ...._javacompat import Point
from ....Algorithm import Algorithm
from ....ByteImage import ByteImage
from ....GlobalException import GlobalException
from ....Image import Image
from ....IntegerImage import IntegerImage
from ....util.Tools import Tools


class GrayQFZSoille(Algorithm):
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
        if self.input.getCDim() != 1:
            raise GlobalException("This implementation is only for grayscale images.")
        if self.alpha < 0 or self.alpha > 254 or self.omega < 0 or self.omega > 254:
            raise GlobalException("Arguments out of range.")

        lbl = IntegerImage(self.input, False)
        lbl.fill(0)

        rl = ByteImage(self.input, False)
        rl.fill(255)

        stack: list[Point] = []

        pq: dict[int, list[Point]] = {}

        lblval = 1
        for x in range(xdim):
            for y in range(ydim):
                if lbl.getXYInt(x, y) == 0:
                    lbl.setXYInt(x, y, lblval)
                    fp = self.input.getXYByte(x, y)
                    maxcc = fp
                    mincc = maxcc
                    rlcrt = self.alpha

                    for n in N:
                        qx = x + n.x
                        qy = y + n.y
                        if 0 <= qx < xdim and 0 <= qy < ydim:
                            rlval = abs(fp - self.input.getXYByte(qx, qy))
                            if lbl.getXYInt(qx, qy) > 0:
                                if rlcrt >= rlval:
                                    rlcrt = rlval - 1
                            elif rlval <= rlcrt:
                                rl.setXYByte(qx, qy, rlval)
                                if rlval in pq:
                                    pq[rlval].append(Point(qx, qy))
                                else:
                                    pq[rlval] = [Point(qx, qy)]

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

                                fDatumP = self.input.getXYByte(datumP.x, datumP.y)
                                if fDatumP < mincc:
                                    mincc = fDatumP
                                if fDatumP > maxcc:
                                    maxcc = fDatumP

                                if self.omega < maxcc - mincc or rcrt > rlcrt:
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
                                        rlval = abs(fDatumP - self.input.getXYByte(qx, qy))

                                        lblq = lbl.getXYInt(qx, qy)
                                        if lblq > 0 and lblq != lblval and rlcrt >= rlval:
                                            rlcrt = rlval - 1
                                            if rcrt > rlcrt:
                                                for pp in stack:
                                                    rl.setXYByte(pp.x, pp.y, 255)
                                                stack.clear()

                                                for k in sorted(pq):
                                                    for p in pq[k]:
                                                        rl.setXYByte(p.x, p.y, 255)
                                                pq.clear()
                                                break
                                        elif rlval <= rlcrt and rlval < rl.getXYByte(qx, qy):
                                            rl.setXYByte(qx, qy, rlval)
                                            if rlval in pq:
                                                pq[rlval].append(Point(qx, qy))
                                            else:
                                                pq[rlval] = [Point(qx, qy)]

                    while stack:
                        sPoint = stack.pop()
                        lbl.setXYInt(sPoint.x, sPoint.y, lblval)

                    lblval += 1

        self.output = lbl

    @staticmethod
    def invoke(img: Image, alpha: int, omega: int) -> Image | None:
        try:
            return GrayQFZSoille().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

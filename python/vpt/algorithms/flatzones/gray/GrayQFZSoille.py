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
from collections import deque

import numpy as np

from ...._javacompat import Point
from ....Algorithm import Algorithm
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

        # Same algorithm, step for step, on plain Python lists indexed by the
        # flat pixel position `x + xdim * y` instead of per-pixel Image
        # getter/setter calls and Point objects (which dominated the
        # runtime). `rl` is internal scratch (values 0..255 round-trip
        # exactly through ByteImage), so it never needs to be an image;
        # `lbl` is written to the output IntegerImage once at the end.
        # Each pq bucket is a deque: the Java ArrayList.remove(0) is a FIFO
        # pop, which list.pop(0) made O(n).
        f = [int(v) for v in self.input.getByteArray()[: xdim * ydim]]
        lbl = [0] * (xdim * ydim)
        rl = [255] * (xdim * ydim)
        offsets = [(n.x, n.y) for n in N]
        alpha = self.alpha
        omega = self.omega

        stack: list[int] = []

        pq: dict[int, deque[int]] = {}

        def clear_pending() -> None:
            for i in stack:
                rl[i] = 255
            stack.clear()
            for bucket in pq.values():
                for i in bucket:
                    rl[i] = 255
            pq.clear()

        lblval = 1
        for x in range(xdim):
            for y in range(ydim):
                idx = x + xdim * y
                if lbl[idx] == 0:
                    lbl[idx] = lblval
                    fp = f[idx]
                    maxcc = fp
                    mincc = maxcc
                    rlcrt = alpha

                    for dx, dy in offsets:
                        qx = x + dx
                        qy = y + dy
                        if 0 <= qx < xdim and 0 <= qy < ydim:
                            q = qx + xdim * qy
                            rlval = abs(fp - f[q])
                            if lbl[q] > 0:
                                if rlcrt >= rlval:
                                    rlcrt = rlval - 1
                            elif rlval <= rlcrt:
                                rl[q] = rlval
                                if rlval in pq:
                                    pq[rlval].append(q)
                                else:
                                    pq[rlval] = deque((q,))

                    rcrt = min(pq) if pq else 0

                    while pq:
                        datumPrio = min(pq)
                        tmp = pq[datumPrio]
                        datum = tmp.popleft()
                        if not tmp:
                            del pq[datumPrio]

                        if lbl[datum] <= 0:
                            if datumPrio > rcrt:
                                while stack:
                                    lbl[stack.pop()] = lblval
                                rcrt = datumPrio
                                # Java: `if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}` --
                                # empty body, no effect.
                            else:
                                stack.append(datum)

                                fDatumP = f[datum]
                                if fDatumP < mincc:
                                    mincc = fDatumP
                                if fDatumP > maxcc:
                                    maxcc = fDatumP

                                if omega < maxcc - mincc or rcrt > rlcrt:
                                    clear_pending()
                                    break

                                dxp = datum % xdim
                                dyp = datum // xdim
                                for dx, dy in offsets:
                                    qx = dxp + dx
                                    qy = dyp + dy
                                    if 0 <= qx < xdim and 0 <= qy < ydim:
                                        q = qx + xdim * qy
                                        rlval = abs(fDatumP - f[q])

                                        lblq = lbl[q]
                                        if lblq > 0 and lblq != lblval and rlcrt >= rlval:
                                            rlcrt = rlval - 1
                                            if rcrt > rlcrt:
                                                clear_pending()
                                                break
                                        elif rlval <= rlcrt and rlval < rl[q]:
                                            rl[q] = rlval
                                            if rlval in pq:
                                                pq[rlval].append(q)
                                            else:
                                                pq[rlval] = deque((q,))

                    while stack:
                        lbl[stack.pop()] = lblval

                    lblval += 1

        self.output = IntegerImage(self.input, False)
        self.output.setIntArray(np.array(lbl, dtype=np.int64))

    @staticmethod
    def invoke(img: Image, alpha: int, omega: int) -> Image | None:
        try:
            return GrayQFZSoille().preprocess(img, alpha, omega)
        except GlobalException:
            traceback.print_exc()
        return None

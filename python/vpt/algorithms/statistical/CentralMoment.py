"""Port of ``vpt.algorithms.statistical.CentralMoment``.

**Preserved bug, bytecode-confirmed**: despite the name and the intent
visible in the source (``centroidX``/``centroidY`` arrays are allocated
and ``m10[c] /= m00[c]``/``m01[c] /= m00[c]`` compute exactly the
centroid), the computed centroid is stored back into ``m10``/``m01``
themselves -- ``centroidX``/``centroidY`` are never written to and stay
``0.0`` for every channel, for the whole method. `javap` disassembly
confirms only two ``dastore`` sites in the method, both writing into
``m10``/``m01``; no bytecode ever stores into the arrays that end up as
``centroidX``/``centroidY``. The formula therefore reduces to exactly
``Moment(momentX, momentY)`` (raw, not centred) -- transcribed with the
same dead arrays rather than "fixed" into an actual central moment.

**Vectorization note**: since ``centroidX``/``centroidY`` never leave
``0.0``, ``(x + 1 - centroidX[c])`` is exactly ``(x + 1)`` for every
channel -- so the main loop is not just mathematically but *computationally*
identical to ``Moment``'s, and reuses the same vectorized
``_momentAllChannels`` helper (see ``Moment.py``) rather than duplicating
the weight-grid vectorization here. The ``m00``/``m01``/``m10``/``jdiv``
calls above are dead-store computations with no effect on ``self.output``,
same as in the original Java, but are kept verbatim (including their
``Moment.invoke`` calls and any exception they could raise) since they are
observable side effects of ``execute()``, just not ones that reach the
output."""

from __future__ import annotations

import traceback

from ..._javacompat import jdiv
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from .Moment import Moment, _momentAllChannels


class CentralMoment(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.momentX: int | None = None
        self.momentY: int | None = None
        self.inputFields = "input, momentX, momentY"
        self.outputFields = "output"

    def execute(self) -> None:
        cdim = self.input.getCDim()

        m00 = Moment.invoke(self.input, 0, 0)
        m01 = Moment.invoke(self.input, 0, 1)
        m10 = Moment.invoke(self.input, 1, 0)

        for c in range(cdim):
            m10[c] = jdiv(m10[c], m00[c])
            m01[c] = jdiv(m01[c], m00[c])

        # centroidX/centroidY are always 0.0 here (preserved bug -- see
        # docstring above), so this reduces exactly to Moment(momentX,
        # momentY) for every channel.
        self.output = _momentAllChannels(self.input, self.momentX, self.momentY)

    @staticmethod
    def invoke(input: Image, momentX: int, momentY: int) -> list[float] | None:
        try:
            return CentralMoment().preprocess(input, momentX, momentY)
        except GlobalException:
            traceback.print_exc()
        return None

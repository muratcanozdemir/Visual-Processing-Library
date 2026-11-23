"""Port of ``vpt.algorithms.statistical.Moment``: the raw spatial moment
``sum((x+1)^momentX * (y+1)^momentY * pixel)`` per channel, over the whole
image. ``Math.pow``'s base is always ``x+1``/``y+1`` (>= 1, positive), so
plain ``**`` is domain-safe here -- no ``jpow`` needed.

Vectorized like ``ColorMoments._moment`` (section 6.4): build the
``(x+1)**momentX * (y+1)**momentY`` weight grid once, multiply it by each
channel plane, flatten in the scalar loop's own x-outer/y-inner order, and
reduce with the sequential ``np.cumsum(...)[-1]`` trick to reproduce the
exact left-to-right double summation. ``momentX``/``momentY`` are always
small non-negative ints at every call site in this codebase (0-3, see
``InvariantMoment``/``Orientation``), so every term stays far under
2**53 -- computing ``(x+1)**momentX`` directly as a float64 array is
bit-exact to Python's ``int**int`` (exact, arbitrary precision) followed
by the implicit float conversion the scalar loop performs when multiplying
by ``tmp``, since both paths land on the same exactly-representable
double."""

from __future__ import annotations

import traceback

import numpy as np

from ..._javacompat import jpow_arr
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


def _momentAllChannels(input: Image, momentX: int, momentY: int) -> list[float]:
    """Vectorized equivalent of the per-pixel scan below, computed for every
    channel at once. Shared by ``Moment.execute`` and ``CentralMoment``,
    whose main loop is mathematically identical to this one (see
    ``CentralMoment.py``'s docstring: its ``centroidX``/``centroidY`` stay
    ``0.0`` for every channel, a preserved bug)."""
    xdim = input.getXDim()
    ydim = input.getYDim()
    cdim = input.getCDim()

    if xdim == 0 or ydim == 0:
        return [0.0] * cdim

    x = np.arange(xdim, dtype=np.float64).reshape(-1, 1)
    y = np.arange(ydim, dtype=np.float64).reshape(1, -1)
    # jpow_arr (per-element math.pow), not numpy `**`: see _javacompat._libm_map.
    weight = jpow_arr(x + 1, momentX) * jpow_arr(y + 1, momentY)  # shape (xdim, ydim)

    # getDoubleArray() is flat [y][x][c] (elementwise equal to
    # getXYCDouble(x, y, c), so no DoubleImage conversion needed); reshape
    # then transpose to [x][y][c] to match the scalar loop's x-outer/
    # y-inner iteration order before flattening.
    arr = input.getDoubleArray().reshape(ydim, xdim, cdim).transpose(1, 0, 2)

    output = []
    for c in range(cdim):
        term = weight * arr[..., c]
        flat = term.reshape(-1)
        output.append(float(np.cumsum(flat)[-1]) if flat.size else 0.0)
    return output


class Moment(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.momentX: int | None = None
        self.momentY: int | None = None
        self.inputFields = "input, momentX, momentY"
        self.outputFields = "output"

    def execute(self) -> None:
        self.output = _momentAllChannels(self.input, self.momentX, self.momentY)

    @staticmethod
    def invoke(input: Image, momentX: int, momentY: int) -> list[float] | None:
        try:
            return Moment().preprocess(input, momentX, momentY)
        except GlobalException:
            traceback.print_exc()
        return None

"""Port of ``vpt.algorithms.mm.binary.path.BPathOpening``.

Path opening: keeps a foreground pixel only if it lies on some straight
run of foreground pixels at least ``length`` long, in any of 4 directions
(vertical SN, horizontal WE, and the two diagonals SWNE/NWSE). For each
direction, ``computeUpwardX``/``computeDownwardX`` are a memoized recursion
(memo in ``aux``, an ``IntegerImage`` with one channel per direction) that
computes, for each pixel, the longest run of foreground pixels extending
one way along that axis -- summing the upward and downward run lengths (via
the ``lambda = 1 + max(l1, l2, l3)`` step, which grows the run count
"through" whichever of the 3 forward-diagonal-adjacent neighbours already
has the longest run) plus 1 for the pixel itself gives the total run length
through that pixel. The 4 per-direction images are combined with
elementwise ``Maximum`` and thresholded at ``length``.

**Restructured for speed, same results.** The Java computes each run
length with 8 memoized recursive helpers (``computeUpwardSN`` etc.; memo in
an ``IntegerImage`` ``aux``, ``-1`` = not computed yet, out-of-image
neighbours read as 0, background pixels as 0). That recursion is a longest-
path computation over a DAG, so its result is a pure function of the
foreground mask, independent of visiting order:

    up(x, y)   = 0 if background, else 1 + max(up of its 3 "upward" neighbours)
    value(x,y) = up(x, y) + 1 + down(x, y)        (foreground pixels only)

(An isolated foreground pixel therefore scores 3, not 1 -- preserved as the
Java computes it.) ``path_run_lengths`` evaluates exactly that recurrence
iteratively, scanning each direction in an order that visits every
neighbour before the pixel needing it. It works on plain data (a flat mask
in, flat ints out), which made it a self-contained target for a native
port: when the ``vpt._native`` extension is available, ``execute`` uses its
``path_run_lengths`` (``python/src/path.rs``) instead -- see ``vpt._accel``
and ``python/IMPROVEMENTS.md`` section 8.2. The surrounding steps are
unchanged: the mask is read exactly like ``getXYBoolean`` (flat index
``x + xdim * y``), the run lengths go into ``ByteImage``s (which wrap values
mod 256, as the Java's do), then ``Maximum`` and ``Threshold``. Unlike the
recursion, the iterative form cannot hit Python's recursion limit on large
images (the Java's deeper stack never did).
"""

from __future__ import annotations

import traceback

import numpy as np

from ..... import _accel
from .....Algorithm import Algorithm
from .....ByteImage import ByteImage
from .....GlobalException import GlobalException
from .....Image import Image
from ....arithmetic.Maximum import Maximum
from ....segmentation.Threshold import Threshold

_BYTE_TO_DOUBLE = 0.00392156862745098


def path_run_lengths(mask: list[bool], xdim: int, ydim: int) -> list[int]:
    """Per-pixel path run lengths in the 4 directions, from a flat
    foreground mask (index ``x + xdim * y``). Returns ``4 * xdim * ydim``
    ints: the SN, WE, SWNE and NWSE arrays one after another, each holding
    ``up + 1 + down`` for foreground pixels and 0 for background.

    ``up``/``down`` follow the Java helpers' recurrences exactly (same three
    neighbours each, out-of-image neighbours = 0); each loop nest visits a
    pixel only after the neighbours its value depends on."""
    n = xdim * ydim
    out = [0] * (4 * n)

    def fg(x: int, y: int) -> bool:
        return mask[x + xdim * y]

    def get(a: list[int], x: int, y: int) -> int:
        # Out-of-image neighbours count as 0, like the Java's bounds checks.
        if 0 <= x < xdim and 0 <= y < ydim:
            return a[x + xdim * y]
        return 0

    def run(neigh, order) -> list[int]:
        a = [0] * n
        for y, x in order:
            if fg(x, y):
                a[x + xdim * y] = 1 + max(get(a, x + dx, y + dy) for dx, dy in neigh)
        return a

    ys_up = range(ydim)
    ys_down = range(ydim - 1, -1, -1)
    xs_up = range(xdim)
    xs_down = range(xdim - 1, -1, -1)

    def rows(ys, xs):
        return [(y, x) for y in ys for x in xs]

    def cols(xs, ys):
        return [(y, x) for x in xs for y in ys]

    directions = [
        # SN: upward reads row y+1, downward reads row y-1.
        (run([(-1, 1), (0, 1), (1, 1)], rows(ys_down, xs_up)),
         run([(-1, -1), (0, -1), (1, -1)], rows(ys_up, xs_up))),
        # WE: upward reads column x-1, downward reads column x+1.
        (run([(-1, -1), (-1, 0), (-1, 1)], cols(xs_up, ys_up)),
         run([(1, -1), (1, 0), (1, 1)], cols(xs_down, ys_up))),
        # SWNE: upward reads (x, y-1), (x+1, y-1), (x+1, y).
        (run([(0, -1), (1, -1), (1, 0)], rows(ys_up, xs_down)),
         run([(-1, 0), (-1, 1), (0, 1)], rows(ys_down, xs_up))),
        # NWSE: upward reads (x, y-1), (x-1, y-1), (x-1, y).
        (run([(0, -1), (-1, -1), (-1, 0)], rows(ys_up, xs_up)),
         run([(1, 0), (1, 1), (0, 1)], rows(ys_down, xs_down))),
    ]
    for d, (up, down) in enumerate(directions):
        base = d * n
        for i in range(n):
            if mask[i]:
                out[base + i] = up[i] + 1 + down[i]
    return out


class BPathOpening(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.input: Image | None = None
        self.length: int | None = None
        self.inputFields = "input,length"
        self.outputFields = "output"

        self.xdim = 0
        self.ydim = 0

    def execute(self) -> None:
        self.xdim = self.input.getXDim()
        self.ydim = self.input.getYDim()
        n = self.xdim * self.ydim

        # getXYBoolean(x, y) reads flat index x + xdim * y; getBooleanArray
        # agrees with getBoolean element for element on every image type.
        mask = self.input.getBooleanArray()[:n]
        native = _accel.native
        if native is not None:
            # Same values from the Rust kernel (python/src/path.rs).
            raw = native.path_run_lengths(mask.astype(np.uint8).tobytes(), self.xdim, self.ydim)
            lengths = np.frombuffer(raw, dtype="<u4").astype(np.int64)
        else:
            lengths = path_run_lengths(mask.tolist(), self.xdim, self.ydim)

        # One ByteImage per direction, shaped like the input (the Java's
        # `new ByteImage(input, false)`), with the run lengths in the first
        # xdim * ydim entries -- where setXYByte(x, y, .) wrote them -- and
        # zeros elsewhere. setByteArray wraps mod 256, like setXYByte.
        outputs = []
        for d in range(4):
            img = ByteImage(self.input, False)
            vals = np.zeros(img.getSize(), dtype=np.int64)
            vals[:n] = lengths[d * n : (d + 1) * n]
            img.setByteArray(vals)
            outputs.append(img)
        outputSN, outputWE, outputSWNE, outputNWSE = outputs

        self.output = Maximum.invoke(outputSN, outputWE)
        self.output = Maximum.invoke(self.output, outputSWNE)
        self.output = Maximum.invoke(self.output, outputNWSE)

        self.output = Threshold.invoke(self.output, self.length * _BYTE_TO_DOUBLE)

    # ------------------------------------------------------------------
    @staticmethod
    def invoke(image: Image, length: int) -> Image | None:
        try:
            return BPathOpening().preprocess(image, length)
        except GlobalException:
            traceback.print_exc()
        return None

"""A from-scratch, bit-matching reimplementation of the two ``Jama.Matrix``
operations ``PCA``/``ReversePCA`` need (``times``, a plain matrix product).

Not a general Jama port -- just enough to keep those two files' arithmetic
identical to Jama's own algorithm, verified against Jama's decompiled
bytecode (``javap -c Jama/Matrix.class``): for each output cell ``C[i][j]``,
a left-to-right accumulation ``s = 0.0; for k: s += A[i][k]*B[k][j]``.
``numpy.matmul``/``@`` would very likely agree numerically for the small
matrices this port's own tests use, but isn't guaranteed to: a BLAS-backed
matmul can block/reorder its summation, and float addition isn't
associative, so a different order can round differently in the last bit --
the same "must match Jama's actual algorithm, not just 'a' correct one"
reasoning already documented for ``NonFlatSE.decompose``'s SVD."""

from __future__ import annotations


def times(A: list[list[float]], B: list[list[float]]) -> list[list[float]]:
    """``Jama.Matrix.times(Matrix)``: ``A`` is m x n, ``B`` is n x p."""
    m = len(A)
    n = len(A[0]) if m else 0
    p = len(B[0]) if B else 0

    C = [[0.0] * p for _ in range(m)]
    for i in range(m):
        Arowi = A[i]
        for j in range(p):
            s = 0.0
            for k in range(n):
                s += Arowi[k] * B[k][j]
            C[i][j] = s
    return C

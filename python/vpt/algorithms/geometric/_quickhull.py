"""Port of ``org.concord.swing.QuickHull`` (``src/org/concord/swing/QuickHull.java``).

That Java file is itself a from-scratch replacement for the original
(missing) ``org.concord.swing.QuickHull`` dependency -- Andrew's monotone
chain algorithm, O(n log n), written fresh for this repo rather than
decompiled. There is no decompiler-damage fidelity risk here, only a
straight transcription. Points are returned in counter-clockwise order.

``None`` entries in the input are filtered out (matching the Java
constructor's explicit null-check), because the sole caller, ConvexHull,
passes an array oversized due to a preserved ``BooleanImage.getDouble``
inversion defect -- see ``ConvexHull.py`` for the full explanation.
"""

from __future__ import annotations

from ..._javacompat import Point


def _cross(o: Point, a: Point, b: Point) -> int:
    return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)


def quickhull(points) -> list[Point]:
    """Convex hull of ``points`` (may contain ``None``), CCW order."""
    pts = sorted((p for p in points if p is not None), key=lambda p: (p.x, p.y))
    n = len(pts)
    if n < 3:
        return list(pts)

    h: list[Point | None] = [None] * (2 * n)
    k = 0
    for i in range(n):
        while k >= 2 and _cross(h[k - 2], h[k - 1], pts[i]) <= 0:
            k -= 1
        h[k] = pts[i]
        k += 1

    t = k + 1
    for i in range(n - 2, -1, -1):
        while k >= t and _cross(h[k - 2], h[k - 1], pts[i]) <= 0:
            k -= 1
        h[k] = pts[i]
        k += 1

    return h[: k - 1]

"""Optional native (Rust) kernels.

``vpt._native`` is a compiled extension (``python/src``, built by maturin)
that replaces a few pure-Python hot loops -- ``BPathOpening``'s run
lengths, ``BAreaOpening``/``BAreaClosing``'s component filter and
``ComputeSEs``'s amoeba flood fill. Each kernel computes exactly what the
Python code computes (integer/boolean work only, no float rounding to
reproduce); the Python code stays in place as the reference and the
fallback.

``native`` is the extension module, or ``None`` when it isn't available or
is switched off. Callers look it up at call time (``_accel.native``), so
tests can switch it off by patching this attribute.

Environment:

* ``VPT_PURE_PYTHON=1`` -- never use the extension.
* ``VPT_REQUIRE_NATIVE=1`` -- raise if the extension can't be imported
  (CI uses it so a broken build can't silently fall back to Python).
"""

from __future__ import annotations

import os
from types import ModuleType

native: ModuleType | None = None

if not os.environ.get("VPT_PURE_PYTHON"):
    try:
        from . import _native as native
    except ImportError:
        if os.environ.get("VPT_REQUIRE_NATIVE"):
            raise
        native = None


_I64_BOUND = 1 << 62


def clamp_i64(v: int) -> int:
    """Clamp a Python int into i64 range for a kernel argument that is only
    compared against pixel counts (which never come near the bounds)."""
    return max(-_I64_BOUND, min(_I64_BOUND, v))


def set_booleans(img, idx, value: bool) -> None:
    """``img.setBoolean(i, value)`` for every flat index in ``idx``, in one
    array assignment where that is equivalent (``BooleanImage`` and its
    subclasses store exactly the booleans written)."""
    from .BooleanImage import BooleanImage

    if isinstance(img, BooleanImage):
        arr = img.getBooleanArray()
        arr[idx] = value
        img.setBooleanArray(arr)
    else:
        for i in idx.tolist():
            img.setBoolean(i, value)

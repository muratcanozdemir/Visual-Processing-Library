"""Test helpers shared by the golden tests."""

from __future__ import annotations

from vpt import _accel


def libm_ulp(n: int) -> int:
    """The ulp tolerance for a golden check whose only source of error is
    the platform math library.

    Through ``vpt._native`` (fdlibm atan2/acos/asin/atan, correctly rounded
    log/log10/...) these checks are bit-exact on every platform, so 0. The
    pure-Python fallback computes through ``math``/libm, which on Linux/glibc
    disagrees with Java by the ``n`` ulp recorded at each call site."""
    return 0 if _accel.native is not None else n

"""Port of the ``vpt.algorithms.frequential`` package -- complete (4/4):
``FFT``, ``IFFT``, ``DFT2D``, ``IDFT2D``.

**All four are confirmed non-functional in the original Java** (verified
directly against the JDK 21 oracle, not just static analysis -- see each
file's docstring): `FFT`/`IFFT` always return all-zero output (a
`ComplexNumber.cSum`/`cDiff` in-place-mutation-plus-zero-return defect,
the same shape as `Tools.VAddC`/`VSub`); `DFT2D`/`IDFT2D` compute their
phase term in truncating Java `int` arithmetic, collapsing every phase to
an exact multiple of `2*pi` and the whole transform to a position-
independent constant. `Smoke.java`/`Sweep.java` import `FFT` but never
call it, so this appears to have gone unnoticed. Ported for completeness
and because `shape`'s Fourier-based descriptors (`FourierDescriptor`,
`FPFT`, `DFTZernikeMoments`) depend on this package -- those descriptors
will inherit the same non-functionality, which is faithful, not a new
bug introduced by the port."""

from .DFT2D import DFT2D
from .FFT import FFT
from .IDFT2D import IDFT2D
from .IFFT import IFFT

__all__ = ["DFT2D", "FFT", "IDFT2D", "IFFT"]

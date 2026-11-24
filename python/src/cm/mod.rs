//! Correctly rounded `sin`, `cos`, `tan`, `exp`, `log`, `log10`, `pow`:
//! Rust translations of CORE-MATH (https://core-math.gitlabpages.inria.fr/,
//! MIT; the notice is at the top of every file and in THIRD_PARTY_NOTICES).
//!
//! Each was translated from the C and verified bit-identical to it before
//! the C was removed -- hundreds of millions of random inputs per function,
//! tens of millions more with the fast path forced off so the accurate
//! phases are exercised, and the special-value grids; see
//! python/IMPROVEMENTS.md section 9.1. `tests/goldens/crmath.txt`, frozen
//! from the C, keeps checking the translation on every platform.

pub mod explog;
pub mod pow;
pub mod trig;

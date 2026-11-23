# Vendored CORE-MATH (correctly rounded libm functions)

Seven binary64 functions from [CORE-MATH](https://core-math.gitlabpages.inria.fr/)
(MIT, see `LICENSE`): `sin`, `cos`, `tan`, `exp`, `log`, `log10`, `pow`.
Each directory is copied unmodified from `src/binary64/<fn>/` (plus the
headers it includes) of the CORE-MATH tree vendored in the
[`core-math-sys`](https://crates.io/crates/core-math-sys) crate, version
1.4.0 (git `c27f24ada7845827dc5e79069b2d090d7c408ece`).

`../../build.rs` compiles them into `vpt._native`; `../../src/jmath.rs`
explains why these functions and why correct rounding. To update, copy the
same files from a newer CORE-MATH and re-run `tests/test_jmath_golden.py`.

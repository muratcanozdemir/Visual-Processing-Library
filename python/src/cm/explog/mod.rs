// Rust translation of CORE-MATH's exp, log and log10 (https://core-math.gitlabpages.inria.fr/).
//
// Original exp.c: Copyright (c) 2022-2025 Alexei Sibidanov.
// Original log.c: Copyright (c) 2022 INRIA and CERN.
// Original log10.c: Copyright (c) 2022-2025 INRIA and CERN.
// Original dint.h: Copyright (c) 2022 CERN. Author: Tom Hubrecht
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

//! Pure-Rust, `unsafe`-free translations of CORE-MATH's correctly rounded
//! binary64 `exp`, `log` and `log10` (round-to-nearest only).
//!
//! The results are bit-identical to the CORE-MATH C code: every operation of
//! the C is reproduced in the same order. Rust never contracts `a * b + c`
//! into an FMA, and every unconditional `__builtin_fma` of the C is
//! `f64::mul_add`, so the results do not depend on the target CPU.

#![forbid(unsafe_code)]

mod dint;
pub mod exp;
pub mod log;
pub mod log10;

pub use exp::exp;
pub use log::log;
pub use log10::log10;

/// The C's fast-path acceptance tests always run (the translation's
/// verification forced them off to cover the accurate path; see
/// python/IMPROVEMENTS.md section 9.1).
const FORCE_SLOW_PATH: bool = false;

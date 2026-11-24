// Rust translation of CORE-MATH's sin.c, cos.c and tan.c
// (https://core-math.gitlabpages.inria.fr/).
// Original: Copyright (c) 2022-2025 Paul Zimmermann and Tom Hubrecht
//
// The CORE-MATH code is distributed under the MIT license:
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

//! Correctly-rounded `sin`, `cos` and `tan` for `f64` (round to nearest),
//! translated to safe Rust from CORE-MATH
//! (<https://core-math.gitlabpages.inria.fr/>, MIT license, see the header of
//! each module). Results are bit-identical to CORE-MATH's `cr_sin`, `cr_cos`
//! and `cr_tan` and do not depend on hardware FMA.

#![forbid(unsafe_code)]

mod common;
mod cos;
mod sin;
mod tan;

pub use cos::cos;
pub use sin::sin;
pub use tan::tan;

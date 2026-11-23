// Rust translation of CORE-MATH's cos.c (https://core-math.gitlabpages.inria.fr/).
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

//! Correctly-rounded cosine (round to nearest).

// Faithful translation: keep the C's late initialisations, and x - x to
// produce the default NaN for infinite inputs.
#![allow(clippy::eq_op, clippy::needless_late_init)]

use crate::cm::trig::common::{
    C, Dint, MAGIC, S, SC, a_mul, add_dint, dint_fromd, dint_tod, eval_pc, eval_pc_fast, eval_ps,
    eval_ps_fast, fast_two_sum, mul_dint, reduce, reduce_fast, reduce2, s_mul,
};

/* Assume x is a regular number and x > 0x1.6a09e667f3bccp-27,
return (h, l, err) with a bound on the maximal absolute error err:
| h + l - cos(x) | < err */
#[inline(always)]
fn cos_fast(x: f64) -> (f64, f64, f64) {
    let mut neg: i32 = 0;
    let mut is_cos: i32 = 1;

    let (mut i, mut h, mut l, err1) = reduce_fast(x);
    /* | i/2^11 + h + l - frac(x/(2pi)) | < err1 */

    // if i >= 2^10: 1/2 <= frac(x/(2pi)) < 1 thus pi <= x <= 2pi
    // we use cos(pi+x) = -cos(x)
    neg ^= i >> 10;
    i &= 0x3ff;

    // now i < 2^10
    // if i >= 2^9: 1/4 <= frac(x/(2pi)) < 1/2 thus pi/2 <= x <= pi
    // we use cos(pi/2+x) = -sin(x)
    is_cos ^= i >> 9;
    neg ^= i >> 9;
    i &= 0x1ff;

    // now 0 <= i < 2^9
    // if i >= 2^8: 1/8 <= frac(x/(2pi)) < 1/4
    // we use cos(pi/2-x) = sin(x)
    if i & 0x100 != 0 {
        // case pi/4 <= x_red <= pi/2
        is_cos = i32::from(is_cos == 0);
        i = 0x1ff - i;
        /* 0x1p-11 - h is exact (see the C source) */
        h = f64::from_bits(0x3f40000000000000) - h; // 0x1p-11
        l = -l;
    }

    /* Now 0 <= i < 256 and 0 <= h+l < 2^-11 */
    let i = i as usize;
    h -= SC[i][0];
    // now -2^-24 < h < 2^-11+2^-24
    let (uh, mut ul) = a_mul(h, h);
    ul = (h + h).mul_add(l, ul);
    // uh+ul approximates (h+l)^2
    let (mut sh, mut sl) = eval_ps_fast(h, l, uh, ul);
    let (mut ch, mut cl) = eval_pc_fast(uh, ul);
    let err: f64;
    if is_cos == 0 {
        (sh, sl) = s_mul(SC[i][2], sh, sl);
        (ch, cl) = s_mul(SC[i][1], ch, cl);
        (h, l) = fast_two_sum(ch, sh);
        l += sl + cl;
        err = f64::from_bits(0x3ba5500000000000); // 0x1.55p-69
    } else {
        (ch, cl) = s_mul(SC[i][2], ch, cl);
        (sh, sl) = s_mul(SC[i][1], sh, sl);
        (h, l) = fast_two_sum(ch, -sh);
        l += cl - sl;
        err = f64::from_bits(0x3ba8100000000000); // 0x1.81p-69
    }
    const SGN: [f64; 2] = [1.0, -1.0];
    h *= SGN[neg as usize];
    l *= SGN[neg as usize];
    (h, l, err + err1)
}

/* Assume x is a regular number and x > 0x1.6a09e667f3bccp-27. */
#[cold]
#[inline(never)]
fn cos_accurate(x: f64) -> f64 {
    let mut xd: Dint = dint_fromd(x);

    /* reduce argument */
    reduce(&mut xd);

    // now |X - x/(2pi) mod 1| < 2^-126.67*X, with 0 <= X < 1.

    let mut neg = false;
    let mut is_cos = true;

    // Write X = i/2^11 + r with 0 <= r < 2^11.
    let mut i = reduce2(&mut xd); // exact

    if i & 0x400 != 0 {
        // pi <= x < 2*pi: cos(x) = -cos(x-pi)
        neg = true;
        i &= 0x3ff;
    }

    // now i < 2^10

    if i & 0x200 != 0 {
        // pi/2 <= x < pi: cos(x) = -sin(x-pi/2)
        neg = !neg;
        is_cos = false;
        i &= 0x1ff;
    }

    // now 0 <= i < 2^9

    if i & 0x100 != 0 {
        // pi/4 <= x < pi/2: cos(x) = sin(pi/2-x), sin(x) = cos(pi/2-x)
        is_cos = !is_cos;
        xd.sgn = 1; // negate X
        xd = add_dint(&MAGIC, &xd); // X -> 2^-11 - X
        // here: 256 <= i <= 511
        i = 0x1ff - i;
        // now 0 <= i < 256
    }

    // now 0 <= i < 256 and 0 <= X < 2^-11
    let i = i as usize;

    let x2 = mul_dint(&xd, &xd); // X2 approximates X^2
    let mut u = eval_pc(&x2); // cos2pi(X)
    let mut v = eval_ps(&xd, &x2); // sin2pi(X)
    if is_cos {
        // cos2pi(R) ~ cos2pi(i/2^11)*cos2pi(X)-sin2pi(i/2^11)*sin2pi(X)
        u = mul_dint(&C[i], &u);
        v = mul_dint(&S[i], &v);
        v.sgn = 1 - v.sgn; // negate V
    } else {
        // sin2pi(R) ~ sin2pi(i/2^11)*cos2pi(X)+cos2pi(i/2^11)*sin2pi(X)
        u = mul_dint(&S[i], &u);
        v = mul_dint(&C[i], &v);
    }
    u = add_dint(&u, &v);
    /* In all cases the total error is bounded by |U| * 2^-122.650,
    i.e., at most 41 ulps relatively to U->lo. */
    let err: u64 = 41;
    let lo0 = u.lo.wrapping_sub(err);
    let hi0 = u.hi.wrapping_sub(u64::from(lo0 > u.lo));
    let lo1 = u.lo.wrapping_add(err);
    let hi1 = u.hi.wrapping_add(u64::from(lo1 < u.lo));
    /* check the upper 54 bits are equal */
    if (hi0 >> 10) != (hi1 >> 10) {
        static EXCEPTIONS: [[f64; 3]; 5] = [
            [
                f64::from_bits(0x3e88000000000009), // 0x1.8000000000009p-23
                f64::from_bits(0x3fefffffffffff70), // 0x1.fffffffffff7p-1
                f64::from_bits(0x370b56666666666c), // 0x1.b56666666666cp-143
            ],
            [
                f64::from_bits(0x3e98000000000024), // 0x1.8000000000024p-22
                f64::from_bits(0x3feffffffffffdc0), // 0x1.ffffffffffdcp-1
                f64::from_bits(0x376b56666666667e), // 0x1.b56666666667ep-137
            ],
            [
                f64::from_bits(0x3ea8000000000090), // 0x1.800000000009p-21
                f64::from_bits(0x3feffffffffff700), // 0x1.ffffffffff7p-1
                f64::from_bits(0x37cb5666666666c4), // 0x1.b5666666666c4p-131
            ],
            [
                f64::from_bits(0x3eb20000000000f3), // 0x1.20000000000f3p-20
                f64::from_bits(0x3fefffffffffebc0), // 0x1.fffffffffebcp-1
                f64::from_bits(0x38037642666666fd), // 0x1.37642666666fdp-127
            ],
            [
                f64::from_bits(0x3eb8000000000240), // 0x1.800000000024p-20
                f64::from_bits(0x3fefffffffffdc00), // 0x1.fffffffffdcp-1
                f64::from_bits(0x382b5666666667dd), // 0x1.b5666666667ddp-125
            ],
        ];
        for ex in &EXCEPTIONS {
            if x.abs() == ex[0] {
                return ex[1] + ex[2];
            }
        }
        /* if we go here, we have a hard-to-round case, but since all hard-to-round
        cases are known and pass all tests, we are ok */
    }

    if neg {
        u.sgn = 1 - u.sgn;
    }

    dint_tod(&mut u)
}

#[inline(always)]
pub(crate) fn cos_impl<const FORCE_SLOW: bool>(x: f64) -> f64 {
    let tu = x.to_bits();
    let e = ((tu >> 52) & 0x7ff) as i32;

    if e == 0x7ff {
        /* NaN, +Inf and -Inf. */
        if (tu << 1) == 0x7ffu64 << 53 {
            // Inf: the C returns 0.0 / 0.0 (evaluated at run time, giving the
            // platform's default NaN). x - x yields the same default NaN at
            // run time, whereas a Rust 0.0 / 0.0 would be constant-folded.
            return x - x;
        }
        return x + x; // return qNaN
    }

    /* now x is a regular number */

    /* For |x| <= 0x1.6a09e667f3bccp-27, cos(x) rounds to 1 (to nearest). */
    let ax = f64::from_bits(tu & 0x7fffffffffffffff);
    if ax.to_bits() <= 0x3e46a09e667f3bcc {
        // |x| <= 0x1.6a09e667f3bccp-27
        return ax.mul_add(f64::from_bits(0xbe30000000000000), 1.0); // -0x1p-28
    }

    let (h, l, err) = cos_fast(ax);
    let left = h + (l - err);
    let right = h + (l + err);
    if !FORCE_SLOW && left == right {
        return left;
    }

    cos_accurate(ax)
}

/// Correctly-rounded cosine (round to nearest), bit-identical to CORE-MATH's
/// `cr_cos`.
#[must_use]
pub fn cos(x: f64) -> f64 {
    cos_impl::<false>(x)
}

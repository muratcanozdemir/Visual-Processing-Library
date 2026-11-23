// Rust translation of CORE-MATH's sin.c (https://core-math.gitlabpages.inria.fr/).
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

//! Correctly-rounded sine (round to nearest).

// Faithful translation: keep the C's late initialisations, and x - x to
// produce the default NaN for infinite inputs.
#![allow(clippy::eq_op, clippy::needless_late_init)]

use crate::cm::trig::common::{
    C, Dint, MAGIC, S, SC, a_mul, add_dint, dint_fromd, dint_tod, eval_pc, eval_pc_fast, eval_ps,
    eval_ps_fast, fast_two_sum, mul_dint, reduce, reduce_fast, reduce2, s_mul,
};

/* return the maximal absolute error; (h, l) approximates sin(x) */
#[inline(always)]
fn sin_fast(x: f64) -> (f64, f64, f64) {
    let neg0 = x < 0.0;
    let mut is_sin: i32 = 1;
    let absx = if neg0 { -x } else { x };

    /* now x > 0x1.7137449123ef6p-26 */
    let (mut i, mut h, mut l, err1) = reduce_fast(absx);
    /* err1 is an absolute bound for | i/2^11 + h + l - frac(x/(2pi)) | */

    // if i >= 2^10: 1/2 <= frac(x/(2pi)) < 1 thus pi <= x <= 2pi
    // we use sin(pi+x) = -sin(x)
    let neg = i32::from(neg0) ^ (i >> 10);
    i &= 0x3ff;

    // now i < 2^10
    // if i >= 2^9: 1/4 <= frac(x/(2pi)) < 1/2 thus pi/2 <= x <= pi
    // we use sin(pi/2+x) = cos(x)
    is_sin ^= i >> 9;
    i &= 0x1ff;

    // now 0 <= i < 2^9
    // if i >= 2^8: 1/8 <= frac(x/(2pi)) < 1/4
    // we use sin(pi/2-x) = cos(x)
    if i & 0x100 != 0 {
        // case pi/4 <= x_red <= pi/2
        is_sin = i32::from(is_sin == 0);
        i = 0x1ff - i;
        /* 0x1p-11 - h is exact (see the C source) */
        h = f64::from_bits(0x3f40000000000000) - h; // 0x1p-11
        l = -l;
    }

    /* Now 0 <= i < 256 and 0 <= h+l < 2^-11 */
    let i = i as usize;
    /* since the SC[] table evaluates at i/2^11 + SC[i][0] and not at i/2^11,
    we must subtract SC[i][0] from h+l (exact) */
    h -= SC[i][0];
    // now -2^-24 < h < 2^-11+2^-24
    let (uh, mut ul) = a_mul(h, h);
    ul = (h + h).mul_add(l, ul);
    // uh+ul approximates (h+l)^2
    let (mut sh, mut sl) = eval_ps_fast(h, l, uh, ul);
    let (mut ch, mut cl) = eval_pc_fast(uh, ul);
    let err: f64;
    const SGN: [f64; 2] = [1.0, -1.0];
    let sg = SGN[neg as usize];
    if is_sin != 0 {
        (sh, sl) = s_mul(sg * SC[i][2], sh, sl);
        (ch, cl) = s_mul(sg * SC[i][1], ch, cl);
        (h, l) = fast_two_sum(ch, sh);
        l += sl + cl;
        err = f64::from_bits(0x3ba5500000000000); // 0x1.55p-69
    } else {
        (ch, cl) = s_mul(sg * SC[i][2], ch, cl);
        (sh, sl) = s_mul(sg * SC[i][1], sh, sl);
        (h, l) = fast_two_sum(ch, -sh);
        l += cl - sl;
        err = f64::from_bits(0x3ba8100000000000); // 0x1.81p-69
    }
    (h, l, err + err1)
}

/* Assume x is a regular number, and |x| > 0x1.7137449123ef6p-26. */
#[cold]
#[inline(never)]
fn sin_accurate(x: f64) -> f64 {
    let absx = if x > 0.0 { x } else { -x };

    let mut xd: Dint = dint_fromd(absx);

    /* reduce argument */
    reduce(&mut xd);

    // now |X - x/(2pi) mod 1| < 2^-126.67*X, with 0 <= X < 1.

    let mut neg = x < 0.0;
    let mut is_sin = true;

    // Write X = i/2^11 + r with 0 <= r < 2^11.
    let mut i = reduce2(&mut xd); // exact

    if i & 0x400 != 0 {
        // pi <= x < 2*pi: sin(x) = -sin(x-pi)
        neg = !neg;
        i &= 0x3ff;
    }

    // now i < 2^10

    if i & 0x200 != 0 {
        // pi/2 <= x < pi: sin(x) = cos(x-pi/2)
        is_sin = false;
        i &= 0x1ff;
    }

    // now 0 <= i < 2^9

    if i & 0x100 != 0 {
        // pi/4 <= x < pi/2: sin(x) = cos(pi/2-x), cos(x) = sin(pi/2-x)
        is_sin = !is_sin;
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
    if is_sin {
        // sin2pi(R) ~ sin2pi(i/2^11)*cos2pi(X)+cos2pi(i/2^11)*sin2pi(X)
        u = mul_dint(&S[i], &u);
        v = mul_dint(&C[i], &v);
    } else {
        // cos2pi(R) ~ cos2pi(i/2^11)*cos2pi(X)-sin2pi(i/2^11)*sin2pi(X)
        u = mul_dint(&C[i], &u);
        v = mul_dint(&S[i], &v);
        v.sgn = 1 - v.sgn; // negate V
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
        static EXCEPTIONS: [[f64; 3]; 2] = [
            [
                f64::from_bits(0x3ebe0000000001c2), // 0x1.e0000000001c2p-20
                f64::from_bits(0x3ebdfffffffff02e), // 0x1.dfffffffff02ep-20
                f64::from_bits(0x36ddcba692492527), // 0x1.dcba692492527p-146
            ],
            /* the following worst case was reported by Erik E., it has 68
            identical bits after the round bit */
            [
                f64::from_bits(0x7506ac5b262ca1ff), // 0x1.6ac5b262ca1ffp+849
                f64::from_bits(0x3ff0000000000000), // 0x1p+0
                f64::from_bits(0xb842b089ea1e692b), // -0x1.2b089ea1e692bp-123
            ],
        ];
        for ex in &EXCEPTIONS {
            if x.abs() == ex[0] {
                return if x > 0.0 {
                    ex[1] + ex[2]
                } else {
                    -ex[1] - ex[2]
                };
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
pub(crate) fn sin_impl<const FORCE_SLOW: bool>(x: f64) -> f64 {
    let tu = x.to_bits();
    let e = ((tu >> 52) & 0x7ff) as i32;

    if e == 0x7ff {
        /* NaN, +Inf and -Inf. */
        if (tu << 1) == 0x7ffu64 << 53 {
            // +/-Inf
            return x - x; // NaN
        }
        return x + x;
    }

    /* now x is a regular number */

    /* For |x| <= 0x1.7137449123ef6p-26, sin(x) rounds to x (to nearest). */
    let ux = tu & 0x7fffffffffffffff;
    // 0x3e57137449123ef6 = 0x1.7137449123ef6p-26
    if ux <= 0x3e57137449123ef6 {
        if x == 0.0 {
            return x;
        }
        // Taylor expansion of sin(x) is x - x^3/6 around zero
        return x.mul_add(f64::from_bits(0xbc90000000000000), x); // -0x1p-54
    }

    let (h, l, err) = sin_fast(x);
    let left = h + (l - err);
    let right = h + (l + err);
    if !FORCE_SLOW && left == right {
        return left;
    }

    sin_accurate(x)
}

/// Correctly-rounded sine (round to nearest), bit-identical to CORE-MATH's
/// `cr_sin`.
#[must_use]
pub fn sin(x: f64) -> f64 {
    sin_impl::<false>(x)
}

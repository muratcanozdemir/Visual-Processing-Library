// Rust translation of CORE-MATH's tan.c (https://core-math.gitlabpages.inria.fr/).
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

//! Correctly-rounded tangent (round to nearest).

// Faithful translation: keep x - x to produce the default NaN for infinite
// inputs.
#![allow(clippy::eq_op)]

mod tinv;

use crate::cm::trig::common::{
    C, CH, CL, Dint, MAGIC, ONE, S, SC, T, TWO_PI_DOWN, a_mul, add_dint, dint_fromd, dint_tod,
    eval_pc, eval_pc_fast, eval_ps, eval_ps_fast, fast_two_sum, mul_dint, mul_dint_21, reduce,
    reduce_fast_split, reduce2, s_mul, set_dd,
};
use tinv::TINV;

/* put in r an approximation of 1/a, assuming a is not zero,
with error bounded by 4.003 ulps (relative error < 2^-124.999) */
#[inline(always)]
fn inv_dint(a: &Dint) -> Dint {
    let h: u64 = a.hi; /* 2^63 <= h < 2^64 */
    /* First compute a 64-bit inverse t of h, such that t*h ~ 2^127. */
    let i = ((h >> 55) & 0xff) as usize;
    let mut t: u64 = TINV[i];

    /* first Newton iteration */
    let mut e: u128 = (1u128 << 127).wrapping_sub((h as u128) * (t as u128)); // exact
    e = (t as u128).wrapping_mul(e >> 55);
    t = t.wrapping_add((e >> 72) as u64);

    /* second Newton iteration */
    e = (1u128 << 127).wrapping_sub((h as u128) * (t as u128)); // 0 <= e < 2^111
    e = (t as u128).wrapping_mul(e >> 47);
    t = t.wrapping_add((e >> 80) as u64);

    /* third Newton iteration */
    e = (1u128 << 127).wrapping_sub((h as u128) * (t as u128)); // 0 <= e < 2^95
    e = (t as u128).wrapping_mul(e >> 31);
    t = t.wrapping_add((e >> 96) as u64);

    /* if a->ex = 0, then 1/2 <= a < 1, thus we should have
    1 < 1/a <= 2, thus r->ex = 1 */
    let mut r = Dint::new(t, 0, 1i64.wrapping_sub(a.ex), 1);
    /* we use Newton's iteration: r -> r + r*(1-a*r) */
    let mut q = mul_dint_21(a, &r); /* -a*r, error <= 2 ulps */
    r.sgn = 0; /* restore sign */
    q = add_dint(&ONE, &q); /* 1-a*r, error <= 2 ulps */
    q = mul_dint(&r, &q); /* r*(1-a*r), error <= 6 ulps */
    add_dint(&r, &q) /* error <= 2 ulps */
}

/* put in r an approximation of b/a, assuming a is not zero,
with relative error < 2^-123.67 */
#[inline(always)]
fn div_dint(b: &Dint, a: &Dint) -> Dint {
    let r = inv_dint(a); // relative error bounded by 2^-124.999
    mul_dint(&r, b) // error bounded by 6 ulps
}

#[inline(always)]
fn mul64(a: u64, b: u64) -> u128 {
    (a as u128) * (b as u128)
}

/* Assuming 0x1.d12ed0af1a27ep-27 < x < +Inf,
return i and set h,l such that i/2^11+h+l approximates frac(x/(2pi)):
| i/2^11 + h + l - frac(x/(2pi)) | < 2^-104.815,
with |h| < 2^-11 and |l| < 2^-52.36.
This is tan.c's version of reduce_fast(): unlike sin.c/cos.c it has no err1
output and, for x > 2pi, also takes into account the upper part of the next
product m*T[...] (the v terms below), so it gives different h, l. */
#[inline(always)]
fn reduce_fast(x: f64) -> (i32, f64, f64) {
    let h: f64;
    let l: f64;
    if x <= TWO_PI_DOWN {
        // x < 2*pi
        let (hh, ll) = a_mul(CH, x); // exact
        h = hh;
        l = CL.mul_add(x, ll);
    } else {
        // x > 0x1.921fb54442d17p+2
        let tu = x.to_bits();
        let mut e = ((tu >> 52) & 0x7ff) as i32; /* 1025 <= e <= 2046 */
        let m: u64 = (1u64 << 52) | (tu & 0xfffffffffffff);
        let mut c = [0u64; 3];
        let mut u: u128;
        let v: u128;
        // x = m/2^53 * 2^(e-1022)
        if e <= 1074 {
            // 1025 <= e <= 1074: 2^2 <= x < 2^52
            v = mul64(m, T[2]);
            u = mul64(m, T[1]);
            c[0] = (u as u64).wrapping_add((v >> 64) as u64);
            c[1] = ((u >> 64) as u64).wrapping_add(u64::from(c[0] < u as u64));
            u = mul64(m, T[0]);
            c[1] = c[1].wrapping_add(u as u64);
            c[2] = ((u >> 64) as u64).wrapping_add(u64::from(c[1] < u as u64));
            e = 1075 - e; // 1 <= e <= 50
        } else {
            // 1075 <= e <= 2046, 2^52 <= x < 2^1024
            let i = ((e - 1138 + 63) / 64) as usize; // i = ceil((e-1138)/64), 0 <= i <= 15
            v = mul64(m, T[i + 3]);
            u = mul64(m, T[i + 2]);
            c[0] = (u as u64).wrapping_add((v >> 64) as u64);
            c[1] = ((u >> 64) as u64).wrapping_add(u64::from(c[0] < u as u64));
            u = mul64(m, T[i + 1]);
            c[1] = c[1].wrapping_add(u as u64);
            c[2] = ((u >> 64) as u64).wrapping_add(u64::from(c[1] < u as u64));
            u = mul64(m, T[i]);
            c[2] = c[2].wrapping_add(u as u64);
            e = 1139 + ((i as i32) << 6) - e; // 1 <= e <= 64
        }
        // e is the number of bits of c[2] to ignore
        if e == 64 {
            c[0] = c[1];
            c[1] = c[2];
        } else {
            let e = e as u32;
            c[0] = (c[1] << (64 - e)) | c[0] >> e;
            c[1] = (c[2] << (64 - e)) | c[1] >> e;
        }
        (h, l) = set_dd(c[1], c[0]);
    }

    let (i, h) = reduce_fast_split(h);
    (i, h, l)
}

/* h + l <- (bh + bl) / (ah + al) with |bl|, |al| < 2^-49.47
with relative error bounded by 2^-96.99 */
#[inline(always)]
fn fast_div(bh: f64, bl: f64, ah: f64, al: f64) -> (f64, f64) {
    let y = 1.0 / ah;
    let h = bh * y;
    let eh = ah.mul_add(-h, bh);
    let el = al.mul_add(-h, bl);
    let l = y * (eh + el);
    (h, l)
}

/* given a finite input x with |x| > 0x1.d12ed0af1a27ep-27,
return (h, l, err) with h + l an approximation of tan(x) and
the maximal absolute error err such that | h + l - tan(x) | < err */
#[inline(always)]
fn tan_fast(x: f64) -> (f64, f64, f64) {
    let neg0 = x < 0.0;
    let mut is_tan: i32 = 1;
    let absx = if neg0 { -x } else { x };

    /* now absx > 0x1.d12ed0af1a27ep-27 */
    let (mut i, mut h, mut l) = reduce_fast(absx);
    /* | i/2^11 + h + l - frac(x/(2pi)) | < 2^-104.815 */

    // if i >= 2^10: 1/2 <= frac(x/(2pi)) < 1 thus pi <= x <= 2pi
    // we use tan(pi+x) = tan(x)
    i &= 0x3ff;

    // now i < 2^10
    // if i >= 2^9: 1/4 <= frac(x/(2pi)) < 1/2 thus pi/2 <= x <= pi
    // we use tan(pi/2+x) = -cot(x)
    is_tan ^= i >> 9;
    let neg = i32::from(neg0) ^ (i >> 9);
    i &= 0x1ff;

    // now 0 <= i < 2^9
    // if i >= 2^8: 1/8 <= frac(x/(2pi)) < 1/4
    // we use tan(pi/2-x) = cot(x)
    if i & 0x100 != 0 {
        // case pi/4 <= x_red <= pi/2
        is_tan = i32::from(is_tan == 0);
        i = 0x1ff - i;
        /* 0x1p-11 - h is exact (see the C source) */
        h = f64::from_bits(0x3f40000000000000) - h; // 0x1p-11
        l = -l;
    }

    /* For i=0 and h<2^-37, we defer to the slow path. */
    const TWO_M37: f64 = f64::from_bits(0x3da0000000000000); // 0x1p-37
    if i == 0 && h < TWO_M37 {
        return (h, l, f64::from_bits(0x3ff0000000000000)); // 0x1p0
    }

    let i = i as usize;
    /* since the SC[] table evaluates at i/2^11 + SC[i][0] and not at i/2^11,
    we must subtract SC[i][0] from h+l (exact) */
    h -= SC[i][0];
    /* the following fast_two_sum() guarantees that |l| <= ulp(h) */
    (h, l) = fast_two_sum(h, l);
    // now -2^-24 < h < 2^-11+2^-24
    let (uh, mut ul) = a_mul(h, h);
    ul = (h + h).mul_add(l, ul);
    // uh+ul approximates (h+l)^2
    let (mut sh, mut sl) = eval_ps_fast(h, l, uh, ul);
    let (mut ch, mut cl) = eval_pc_fast(uh, ul);

    let (sh0, sl0) = s_mul(SC[i][2], sh, sl);
    let (ch0, cl0) = s_mul(SC[i][1], ch, cl);
    let (h1, mut l1) = fast_two_sum(ch0, sh0);
    l1 += sl0 + cl0;
    /* | h1 + l1 - sin2pi (R) | < 2^-67.777 * |sin2pi(R)| */

    (ch, cl) = s_mul(SC[i][2], ch, cl);
    (sh, sl) = s_mul(SC[i][1], sh, sl);
    let (h2, mut l2) = fast_two_sum(ch, -sh);
    l2 += cl - sl;
    /* | h2 + l2 - cos2pi (R) | < 2^-68.073 * |cos2pi(R)| */

    if is_tan != 0 {
        (h, l) = fast_div(h1, l1, h2, l2);
    } else {
        (h, l) = fast_div(h2, l2, h1, l1);
    }

    const SGN: [f64; 2] = [1.0, -1.0];
    h *= SGN[neg as usize];
    l *= SGN[neg as usize];
    (h, l, h * f64::from_bits(0x3bd1a00000000000)) // 0x1.1ap-66; 2^-65.864 < 0x1.1ap-66
}

/* Assume x is a regular number, and |x| > 0x1.d12ed0af1a27ep-27. */
#[cold]
#[inline(never)]
fn tan_accurate(x: f64) -> f64 {
    let absx = if x > 0.0 { x } else { -x };

    let mut xd: Dint = dint_fromd(absx);

    /* reduce argument */
    reduce(&mut xd);

    // now |X - x/(2pi) mod 1| < 2^-126.67*X, with 0 <= X < 1.

    let mut is_tan = true;
    let mut neg = x < 0.0;

    // Write X = i/2^11 + r with 0 <= r < 2^11.
    let mut i = reduce2(&mut xd); // exact

    i &= 0x3ff; // if pi <= x < 2*pi, tan(x) = tan(x-pi)

    // now i < 2^10

    if i & 0x200 != 0 {
        // pi/2 <= x < pi: tan(x) = -1/tan(x-pi/2)
        is_tan = false;
        neg = !neg;
        i &= 0x1ff;
    }

    // now 0 <= i < 2^9

    if i & 0x100 != 0 {
        // pi/4 <= x < pi/2: tan(x) = 1/tan(pi/2-x)
        is_tan = !is_tan;
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

    // sin2pi(R) ~ sin2pi(i/2^11)*cos2pi(X)+cos2pi(i/2^11)*sin2pi(X)
    let uu = mul_dint(&S[i], &u);
    let vv = mul_dint(&C[i], &v);
    let sin = add_dint(&uu, &vv);

    // cos2pi(R) ~ cos2pi(i/2^11)*cos2pi(X)-sin2pi(i/2^11)*sin2pi(X)
    u = mul_dint(&C[i], &u);
    v = mul_dint(&S[i], &v);
    v.sgn = 1 - v.sgn; // negate V
    let cos = add_dint(&u, &v);

    /* Now if is_tan=1 we compute S/C, otherwise we compute C/S. */
    u = if is_tan {
        div_dint(&sin, &cos)
    } else {
        div_dint(&cos, &sin)
    };

    /* Total relative error < 2^-121.578, i.e., less than 86 ulps. */
    let err: u64 = 86;
    let lo0 = u.lo.wrapping_sub(err);
    let hi0 = u.hi.wrapping_sub(u64::from(lo0 > u.lo));
    let lo1 = u.lo.wrapping_add(err);
    let hi1 = u.hi.wrapping_add(u64::from(lo1 < u.lo));
    /* check the upper 54 bits are equal */
    if (hi0 >> 10) != (hi1 >> 10) {
        static EXCEPTIONS: [[f64; 3]; 2] = [
            /* the following has 78 identical bits after the round bit */
            [
                f64::from_bits(0x3e9dffffffffff1f), // 0x1.dffffffffff1fp-22
                f64::from_bits(0x3e9e000000000151), // 0x1.e000000000151p-22
                f64::from_bits(0x3b3fffffffffffff), // 0x1.fffffffffffffp-76
            ],
            /* the following has 72 identical bits after the round bit */
            [
                f64::from_bits(0x3eadfffffffffc7c), // 0x1.dfffffffffc7cp-21
                f64::from_bits(0x3eae000000000546), // 0x1.e000000000546p-21
                f64::from_bits(0xb6c658bcedb6e1d4), // -0x1.658bcedb6e1d4p-147
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
pub(crate) fn tan_impl<const FORCE_SLOW: bool>(x: f64) -> f64 {
    let tu = x.to_bits();
    let e = ((tu >> 52) & 0x7ff) as i32;

    if e == 0x7ff {
        /* NaN, +Inf and -Inf. */
        if (tu << 1) == 0x7ffu64 << 53 {
            // +/-Inf
            return x - x; // NaN
        }
        return x + x; // NaN
    }

    /* now x is a regular number */

    /* For |x| <= 0x1.d12ed0af1a27ep-27, tan(x) rounds to x (to nearest). */
    let ux = tu & 0x7fffffffffffffff;
    if ux <= 0x3e4d12ed0af1a27e {
        // |x| <= 0x1.d12ed0af1a27ep-27
        if x == 0.0 {
            return x;
        }
        // Taylor expansion of tan(x) is x + x^3/3 around zero
        return x.mul_add(f64::from_bits(0x3c90000000000000), x); // 0x1p-54
    }

    let (h, l, err) = tan_fast(x);
    let left = h + (l - err);
    let right = h + (l + err);
    if !FORCE_SLOW && left == right {
        return left;
    }

    tan_accurate(x)
}

/// Correctly-rounded tangent (round to nearest), bit-identical to CORE-MATH's
/// `cr_tan`.
#[must_use]
pub fn tan(x: f64) -> f64 {
    tan_impl::<false>(x)
}

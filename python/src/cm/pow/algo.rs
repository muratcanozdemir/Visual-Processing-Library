// Rust translation of CORE-MATH's pow.c and pow.h (https://core-math.gitlabpages.inria.fr/).
// Original: Copyright (c) 2022-2025 CERN and Inria
// Authors of the original: Tom Hubrecht and Paul Zimmermann.
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

//! Correctly-rounded power function for two binary64 values
//! (round-to-nearest only).
//!
//! This is a line-by-line translation of CORE-MATH's `cr_pow` (pow.c) and
//! the helpers of pow.h. It corresponds to reference [5]:
//! T. Hubrecht, C.-P. Jeannerod, P. Zimmermann, "Towards a correctly-rounded
//! and fast power function in binary64 arithmetic", ARITH 2023,
//! <https://inria.hal.science/hal-04159652>.
//!
//! The error analyses are those of the C source and are not repeated here.
//! Deviations from the C (all documented at the point of use):
//! * only round-to-nearest is implemented (fegetround() branches removed);
//! * errno and floating-point exception flags are not maintained;
//! * the "unexpected worst case" diagnostic (printf + exit) is replaced by
//!   returning the third-phase rounding of the result.

#![allow(
    clippy::excessive_precision,
    clippy::unreadable_literal,
    clippy::too_many_lines,
    clippy::many_single_char_names,
    clippy::similar_names,
    clippy::cast_possible_truncation,
    clippy::cast_possible_wrap,
    clippy::cast_sign_loss,
    clippy::cast_precision_loss,
    clippy::float_cmp,
    clippy::suboptimal_flops
)]

use crate::cm::pow::dint::{
    Dint, add_dint, add_dint_11, cmp_dint_abs, dint_fromd, fast_extract, mul_dint, mul_dint_11,
    mul_dint_21, mul_dint_int64,
};
use crate::cm::pow::dint_tables::{
    INVERSE_2_1, INVERSE_2_2, LOG_INV_2_1, LOG_INV_2_2, LOG2, LOG2_INV, M_ONE, P_2, Q_2, T1_2, T2_2,
};
use crate::cm::pow::pow_tables::{INVERSE, LOG_INV, P_1, Q_1, T1, T2};
use crate::cm::pow::qint::{
    Qint, add_qint, add_qint_22, mul_qint, mul_qint_2, mul_qint_11, mul_qint_21, mul_qint_22,
    mul_qint_31, mul_qint_33, mul_qint_41,
};
use crate::cm::pow::qint_tables::{
    INVERSE_3_1, INVERSE_3_2, LOG_INV_3_1, LOG_INV_3_2, LOG2_INV_Q, LOG2_Q, M_ONE_Q, P_3, Q_3,
    T1_3, T2_3,
};

/// Which code path produced the result (used by the verification harness).
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash, PartialOrd, Ord)]
pub enum Phase {
    /// Special input (NaN, infinity, zero or negative base handled up front).
    Special,
    /// First (double-double) phase rounding test succeeded.
    Fast,
    /// First phase failed, handled by the easy cases y = 1, 2, 1/2.
    Easy,
    /// Second phase: underflow below 2^-1075.
    DintUnderflow,
    /// Second (128-bit) phase rounding test succeeded.
    Dint,
    /// Exact or midpoint case detected by `exact_pow`.
    Exact,
    /// Third (256-bit) phase rounding test succeeded.
    Qint,
    /// Third phase: result extremely close to 1.
    QintNearOne,
    /// Third phase rounding test failed (the C prints a diagnostic and exits).
    Unexpected,
}

/// Bit 0: force the first-phase rounding test to fail.
pub(crate) const FORCE_PHASE1_FAIL: u32 = 1;
/// Bit 1: force the second-phase rounding test to fail.
pub(crate) const FORCE_PHASE2_FAIL: u32 = 2;

// hex-float constants of pow.c / pow.h (bits computed with Python's float.fromhex)
const P2_M100: f64 = f64::from_bits(0x39b0000000000000); // 0x1p-100
const P2_M24: f64 = f64::from_bits(0x3e70000000000000); // 0x1p-24
const P2_M53: f64 = f64::from_bits(0x3ca0000000000000); // 0x1p-53
const P2_M54: f64 = f64::from_bits(0x3c90000000000000); // 0x1p-54
const P2_M1074: f64 = f64::from_bits(0x0000000000000001); // 0x1p-1074 = 0x0.0000000000001p-1022
const P2_P1023: f64 = f64::from_bits(0x7fe0000000000000); // 0x1p+1023
const P2_53: f64 = f64::from_bits(0x4340000000000000); // 0x1p53
const DBL_MAX: f64 = f64::from_bits(0x7fefffffffffffff); // 0x1.fffffffffffffp+1023

/*
  Utility functions (pow.h)
*/

// When x is a NaN, returns true if x is an sNaN and false if it is a qNaN
#[inline]
fn is_signaling(x: f64) -> bool {
    (x.to_bits() & (1u64 << 51)) == 0
}

/* Add a + b, such that hi + lo approximates a + b.
Assumes |a| >= |b|.
For rounding to nearest we have hi + lo = a + b exactly. */
#[inline]
fn fast_two_sum(a: f64, b: f64) -> (f64, f64) {
    let hi = a + b;
    let e = hi - a; /* exact */
    (hi, b - e) /* exact */
}

// Add a + (bh + bl), assuming |a| >= |bh|
#[inline]
fn fast_sum(a: f64, bh: f64, bl: f64) -> (f64, f64) {
    let (hi, lo) = fast_two_sum(a, bh);
    (hi, lo + bl)
}

// Multiply exactly a and b, such that hi + lo = a * b.
// (the C uses __builtin_fma unconditionally: f64::mul_add is the same
// correctly-rounded fused operation)
#[inline]
fn a_mul(a: f64, b: f64) -> (f64, f64) {
    let hi = a * b;
    (hi, a.mul_add(b, -hi))
}

// Multiply a double with a double double : a * (bh + bl)
#[inline]
fn s_mul(a: f64, bh: f64, bl: f64) -> (f64, f64) {
    let (hi, s) = a_mul(a, bh); /* exact */
    (hi, a.mul_add(bl, s))
}

// Returns (ah + al) * (bh + bl) - (al * bl)
#[inline]
fn d_mul(ah: f64, al: f64, bh: f64, bl: f64) -> (f64, f64) {
    let (hi, s) = a_mul(ah, bh);
    let t = al.mul_add(bh, s);
    (hi, ah.mul_add(bl, t))
}

// roundeven_finite: round to nearest integer, ties to even
// (__builtin_roundeven in the C)
#[inline]
fn roundeven_finite(x: f64) -> f64 {
    x.round_ties_even()
}

// Returns true if x is an integer
#[inline]
fn is_int(x: f64) -> bool {
    x == roundeven_finite(x)
}

// Returns (e, m) such that m is odd and x = 2^e * m
#[inline]
fn extract(x: f64) -> (i64, u64) {
    let u = x.to_bits();
    let e = ((u >> 52) & 0x7ff) as i64;
    let mut m = (u & (!0u64 >> 12)) + if e != 0 { 1u64 << 52 } else { 0 };
    let t = m.trailing_zeros();
    m >>= t;
    (e + i64::from(t) - (0x433 - i64::from(e == 0)), m)
}

// Rounds a Dint value to 54 bits, a shortcut is taken as in `exact_pow`, we
// only consider numbers that end with only ones or only zeroes.
// Returns (G, k).
#[inline]
fn round_54(x: &Dint) -> (i64, i64) {
    (x.ex - 53, ((x.hi >> 10) + ((x.hi >> 9) & 0x1)) as i64)
}

// Multiply x by 2^e
#[inline]
fn pow2(mut x: f64, e: i64) -> f64 {
    if (e & 0x1) != 0 {
        x *= 2.0;
    }

    let e2 = f64::from_bits(((((e >> 1).wrapping_add(0x3ff)) as u64) & 0x7ff) << 52);
    (x * e2) * e2
}

// Convert a Dint value to an integer, rounding towards zero
#[inline]
fn dint_toi(a: &Dint) -> i64 {
    if a.ex < 0 {
        return 0;
    }

    let r = (a.hi >> ((63 - a.ex) as u32)) as i64;

    if a.sgn != 0 { r.wrapping_neg() } else { r }
}

// round a, assuming a is in the subnormal range (round-to-nearest branch of
// the C; the C's `exact` argument only controls the underflow flag/errno)
#[inline]
fn dint_tod_subnormal(a: &Dint) -> f64 {
    let ex = (-(1011 + a.ex)) as u64; // ex >= 12
    // we have to shift right hi,lo by ex bits so that the least significant
    // bit of hi corresponds to 2^-1074 (the number of extra bits is
    // -1022 - a->ex, and we add 11 = 64 - 53 since hi has 64 bits)

    if ex >= 64 {
        // all bits disappear: |a| < 2^-1074
        let rb = a.hi >> 63; // only used when e=64
        let sb = (a.hi << 1) | a.lo; // idem
        let ret = if ex > 64 || rb == 0 || sb == 0 {
            0.0
        } else {
            P2_M1074
        };
        return if a.sgn != 0 { -ret } else { ret };
    }

    // now ex < 64
    let mut hi = a.hi >> ex;
    let rb = (a.hi >> (ex - 1)) & 0x1; // round bit
    let sb = (a.hi << (65 - ex)) != 0 || a.lo != 0; // sticky bit

    hi += if sb { rb } else { hi & rb };

    // now hi <= 2^52 stores the low bits of the result (up to sign)
    // (if hi has overflowed in 2^52 this is exactly what we want)

    f64::from_bits(hi | (a.sgn << 63))
}

// Convert a Dint value to a double (round-to-nearest)
#[inline]
fn dint_tod(a: &Dint) -> f64 {
    if a.ex < -1022 {
        return dint_tod_subnormal(a);
    }

    // r is the significand in [1,2)
    let mut r = f64::from_bits((a.hi >> 11) | (0x3ffu64 << 52));

    // round r
    let mut rd = 0.0;
    if ((a.hi >> 10) & 0x1) != 0 {
        rd += P2_M53;
    }

    if (a.hi & 0x3ff) != 0 || a.lo != 0 {
        rd += P2_M54;
    }

    if a.sgn != 0 {
        rd = -rd;
    }

    r = f64::from_bits(r.to_bits() | (a.sgn << 63));
    r += rd;

    let e: f64;

    if a.ex > -1023 {
        // The result is a normal double
        if a.ex > 1023 {
            if a.ex == 1024 {
                // 2^1024 <= |a| < 2^1025
                r *= 2.0;
                e = P2_P1023;
            } else {
                // |a| >= 2^1025
                r = DBL_MAX;
                e = DBL_MAX;
            }
        } else {
            e = f64::from_bits((((a.ex + 1023) & 0x7ff) as u64) << 52);
        }
    } else {
        // subnormal case (unreachable: a.ex >= -1022 here, kept as in the C)
        if a.ex < -1074 {
            if a.ex == -1075 {
                r *= 0.5;
                e = P2_M1074;
            } else {
                r = P2_M1074;
                e = P2_M1074;
            }
        } else {
            e = f64::from_bits(1u64 << (a.ex + 1074));
        }
    }

    r * e
}

// Convert a double to the corresponding Qint value
#[inline]
fn qint_fromd(b: f64) -> Qint {
    let (ex, hh) = fast_extract(b);

    /* |b| = 2^(ex-52)*hi */

    let t = hh.leading_zeros();

    Qint {
        hh: hh << t,
        hl: 0,
        lh: 0,
        ll: 0,
        ex: ex - i64::from(if t > 11 { t - 12 } else { 0 }),
        sgn: u64::from(b < 0.0),
    }
    /* b = 2^ex*hh/2^64 where 1 <= hh/2^63 < 2 */
}

// Convert a Qint value to an integer.
// Note: the C shift a->hh >> (63 - a->ex) is undefined for a->ex > 63; we use
// wrapping_shr, which masks the count to 6 bits exactly as the x86-64 SHR
// instruction does (this can only matter on overflowing inputs that already
// went through the earlier phases, and is believed unreachable).
#[inline]
fn qint_toi(a: &Qint) -> i64 {
    if a.ex < 0 {
        return 0;
    }

    let r = a.hh.wrapping_shr((63 - a.ex) as u32) as i64;

    if a.sgn != 0 { r.wrapping_neg() } else { r }
}

// Round-to-nearest branch of subnormalize_qint.
// The shifts by ex use wrapping (x86-64 count masking) semantics: ex can
// only reach 64 or more if a->ex < -1075, where the C has undefined
// behaviour; this is believed unreachable.
#[inline]
fn subnormalize_qint(a: &mut Qint) {
    if a.ex > -1023 {
        return;
    }

    let ex = (-(1011 + a.ex)) as u64;

    let mut hi = a.hh.wrapping_shr(ex as u32);
    let md = a.hh.wrapping_shr((ex - 1) as u32) & 0x1;
    let lo = (a.hh & (!0u64).wrapping_shr(ex as u32)) != 0 || a.hl != 0 || a.lh != 0 || a.ll != 0;

    hi += if lo { md } else { hi & md };

    a.hh = hi.wrapping_shl(ex as u32);
    a.hl = 0;
    a.lh = 0;
    a.ll = 0;

    if a.hh == 0 {
        a.ex += 1;
        a.hh = 1u64 << 63;
    }
}

// Convert a Qint value to a double (round-to-nearest)
#[inline]
fn qint_tod(mut a: Qint) -> f64 {
    subnormalize_qint(&mut a);

    let mut r = f64::from_bits((a.hh >> 11) | (0x3ffu64 << 52));

    let mut rd = 0.0;
    if (a.hh & 0x400) != 0 {
        rd += P2_M53;
    }

    if (a.hh & 0x3ff) != 0 || a.hl != 0 || a.lh != 0 || a.ll != 0 {
        rd += P2_M54;
    }

    if a.sgn != 0 {
        rd = -rd;
    }

    r = f64::from_bits(r.to_bits() | (a.sgn << 63));
    r += rd;

    let e: f64;

    if a.ex > -1023 {
        // The result is a normal double
        if a.ex > 1023 {
            if a.ex == 1024 {
                r *= 2.0;
                e = P2_P1023;
            } else {
                r = DBL_MAX;
                e = DBL_MAX;
            }
        } else {
            e = f64::from_bits((((a.ex + 1023) & 0x7ff) as u64) << 52);
        }
    } else {
        // subnormal case
        if a.ex < -1074 {
            if a.ex == -1075 {
                r *= 0.5;
                e = P2_M1074;
            } else {
                r = P2_M1074;
                e = P2_M1074;
            }
        } else {
            e = f64::from_bits(1u64 << (a.ex + 1074));
        }
    }

    r * e
}

/***************** polynomial approximations of exp(z) ***********************/

/* Given z such that |z| < 2^-12.905,
returns (qh, ql) approximating exp(z) with
| (qh+ql) / exp(z) - 1 | < 2^-64.902632
and |ql| <= 2^-51.999. See Lemma 6 from reference [5]. */
#[inline]
fn q_1(z: f64) -> (f64, f64) {
    let mut q = Q_1[4].mul_add(z, Q_1[3]);

    q = q.mul_add(z, Q_1[2]);

    let h0 = q.mul_add(z, Q_1[1]);

    let (h1, l1) = a_mul(z, h0);

    fast_sum(Q_1[0], h1, l1)
}

/* Given |y| < 0.00016923 < 2^-12.52, returns an approximation of exp(y),
with relative error bounded by 2^-122.29. */
#[inline]
fn q_2(y: &Dint) -> Dint {
    let mut r = mul_dint_11(y, &Q_2[0]);
    r = add_dint_11(&Q_2[1], &r);

    r = mul_dint_11(y, &r);
    r = add_dint_11(&Q_2[2], &r);

    r = mul_dint_11(y, &r);
    r = add_dint(&Q_2[3], &r);

    r = mul_dint(y, &r);
    r = add_dint(&Q_2[4], &r);

    r = mul_dint(y, &r);
    r = add_dint(&Q_2[5], &r);

    r = mul_dint(y, &r);
    r = add_dint(&Q_2[6], &r);

    r = mul_dint(y, &r);
    add_dint(&Q_2[7], &r)
}

/* Given |y| < 0.00016923 < 2^-12.52, returns an approximation of exp(y),
with 0.999830 < r < 1.000170, absolute/relative error bounded by 2^-241.11. */
fn q_3(y: &Qint) -> Qint {
    /* the coefficients of degree 13 and 14 (Q_3[0-1]) have 64 bits only */

    let mut r = mul_qint_11(y, &Q_3[0]);
    r = add_qint_22(&Q_3[1], &r);

    /* the coefficients of degree 8 to 12 (Q_3[2-6]) have 128 bits only */

    for q in &Q_3[2..7] {
        r = mul_qint_22(y, &r);
        r = add_qint_22(q, &r);
    }

    /* the coefficients of degree 0 to 7 (Q_3[7-14]) have 256 bits */

    for q in &Q_3[7..12] {
        r = mul_qint_33(y, &r);
        r = add_qint(q, &r);
    }

    for q in &Q_3[12..15] {
        r = mul_qint(y, &r);
        r = add_qint(q, &r);
    }
    r
}

/**************** polynomial approximations of log(1+x) **********************/

/* Given |z| <= 33*2^-13, with z an integer multiple of 2^-61,
returns (ph, pl) approximating log(1+z)-z with
| ph + pl - (log(1 + z) - z) | < 2^-75.492.
See Lemma 2 from reference [5]. */
#[inline]
fn p_1(z: f64) -> (f64, f64) {
    let (wh, wl) = a_mul(z, z);
    let t = P_1[5].mul_add(z, P_1[4]);
    let mut u = P_1[3].mul_add(z, P_1[2]);
    let mut v = P_1[1].mul_add(z, P_1[0]);
    u = t.mul_add(wh, u);
    v = u.mul_add(wh, v);
    u = v * wh;
    let ph = -0.5 * wh;
    let pl = u.mul_add(z, -0.5 * wl);
    (ph, pl)
}

// Approximation for the second iteration
// Return an approximation of log(1+z) for |z| <= 2^-13
// The low part of z is assumed to be 0.
// with relative error bounded by 2^-124.82 and absolute error by 2^-137.95
#[inline]
fn p_2(z: &Dint) -> Dint {
    let mut r = mul_dint_11(z, &P_2[0]);
    r = add_dint_11(&P_2[1], &r);

    r = mul_dint_11(z, &r);
    r = add_dint_11(&P_2[2], &r);

    r = mul_dint_11(z, &r);
    r = add_dint_11(&P_2[3], &r);

    r = mul_dint_11(z, &r);
    r = add_dint(&P_2[4], &r);

    r = mul_dint_21(&r, z);
    r = add_dint(&P_2[5], &r);

    r = mul_dint_21(&r, z);
    r = add_dint(&P_2[6], &r);

    r = mul_dint_21(&r, z);
    r = add_dint(&P_2[7], &r);

    r = mul_dint_21(&r, z);
    r = add_dint(&P_2[8], &r);

    mul_dint_21(&r, z)
}

/* Approximation of log(1+z) for the last iteration, with |z| <= 2^-13
and z having only its upper limb being non-zero.
Maximal relative error: 2^-252.66, maximal absolute error: 2^-265.67,
and |r| < 0.0001221. */
fn p_3(z: &Qint) -> Qint {
    let mut r = mul_qint_11(&P_3[0], z); /* coefficient of degree 18 */
    r = add_qint_22(&P_3[1], &r); /* coefficient of degree 17 */

    for p in &P_3[2..4] {
        r = mul_qint_11(&r, z);
        r = add_qint_22(p, &r);
    }

    for p in &P_3[4..8] {
        r = mul_qint_21(&r, z);
        r = add_qint_22(p, &r);
    }

    for p in &P_3[8..14] {
        r = mul_qint_31(&r, z);
        r = add_qint(p, &r);
    }

    for p in &P_3[14..18] {
        r = mul_qint_41(&r, z);
        r = add_qint(p, &r);
    }

    /* since the polynomial has zero constant coefficient, we multiply by z */
    mul_qint_41(&r, z)
}

/* Given 2^-1074 <= x <= 0x1.fffffffffffffp+1023, returns (h, l, cancel)
with h+l an approximation of log(x) such that |l| < 2^-23.89*|h| and
| h + l - log(x) | <= elog * |log x|
with elog = 2^-73.527  if x < 1/sqrt(2) or sqrt(2) < x,
and  elog = 2^-67.0544 if 1/sqrt(2) < x < sqrt(2).
See Lemma 4 from reference [5]. */
#[inline]
fn log_1(x: f64) -> (f64, f64, usize) {
    const LOG2_H: f64 = f64::from_bits(0x3fe62e42fefa3800); // 0x1.62e42fefa38p-1
    const LOG2_L: f64 = f64::from_bits(0x3d2ef35793c76730); // 0x1.ef35793c7673p-45
    const CY: [f64; 2] = [1.0, 0.5];
    const CM: [u64; 2] = [44, 45];

    let xu = x.to_bits();
    let mut m = xu & (!0u64 >> 12);
    let mut e = ((xu >> 52) & 0x7ff) as i64;

    let tu: u64;

    if e != 0 {
        tu = m | (0x3ffu64 << 52);
        m += 1u64 << 52;
        e -= 0x3ff;
    } else {
        /* x is a subnormal double  */
        let k = m.leading_zeros() - 11;

        e = -0x3fe - i64::from(k);
        m <<= k;
        tu = m | (0x3ffu64 << 52);
    }

    /* now |x| = 2^_e*_t = 2^(_e-52)*m with 1 <= _t < 2,
    and 2^52 <= _m < 2^53 */

    //   log(x) = log(t) + E · log(2)
    let mut t = f64::from_bits(tu);

    // If m > sqrt(2) we divide it by 2 so ensure 1/sqrt(2) < t < sqrt(2)
    let c = usize::from(m >= 0x16a09e667f3bcd);

    e += c as i64;
    let ee = e as f64;
    let i = m >> CM[c]; /* i/2^8 <= t < (i+1)/2^8, 181 <= i <= 362 */
    t *= CY[c];

    let idx = (i - 181) as usize;
    let r = INVERSE[idx];
    let l1 = LOG_INV[idx][0];
    let l2 = LOG_INV[idx][1];

    let z = r.mul_add(t, -1.0);

    let th = ee.mul_add(LOG2_H, l1);
    let tl = ee.mul_add(LOG2_L, l2);

    let (h, l) = fast_sum(th, z, tl);
    let (ph, pl) = p_1(z);
    let (h, l) = fast_sum(h, ph, l + pl);

    if e == 0 && l.abs() > h.abs() * P2_M24 {
        let (h, l) = fast_two_sum(h, l);
        return (h, l, 1);
    }

    (h, l, 0)
}

/* Returns an approximation of log(x), with relative error bounded by
2^-122.88. (The C modifies x->ex in place; x is a local copy here, and the
caller does not use it afterwards.) */
fn log_2(x: &Dint) -> Dint {
    let mut x = *x;
    let mut e = x.ex;

    /* x = 2^(E-63) * hi */

    // find the 1st lookup index i = floor(x*2^7)
    let i: u16 = if x.hi > 0xb504f333f9de6484 {
        /* hi/2^63 > sqrt(2) */
        e += 1;
        (x.hi >> (63 + 1 - 7)) as u16 // the +1 accounts for the division x/2
    } else {
        (x.hi >> (63 - 7)) as u16
    };

    /* now 90 <= i <= 181 */

    x.ex -= e;

    /* now sqrt(2)/2 < x < sqrt(2) */

    let mut z = mul_dint_11(&x, &INVERSE_2_1[usize::from(i) - 90]); /* exact */

    // find the 2nd lookup index j = floor(x*2^13)
    let j = (z.hi >> ((63 - 13 - z.ex) as u32)) as u16;

    z = mul_dint_11(&z, &INVERSE_2_2[usize::from(j) - 8128]); /* exact */

    // subtract 1, since 1/2 < z < 2 this is exact
    z = add_dint(&M_ONE, &z);

    /* now |z| <= 0.0001220703125 = 2^-13 */

    // E·log(2)
    let r = mul_dint_int64(&LOG2, e);

    let mut p = p_2(&z); /* relative error < 2^-124.82, absolute error < 2^-137.95,
    and |p| < 0.0001221 */

    p = add_dint(&LOG_INV_2_2[usize::from(j) - 8128], &p);

    p = add_dint(&LOG_INV_2_1[usize::from(i) - 90], &p);

    add_dint(&p, &r)
}

/* returns an approximation of log(x), with relative error < 2^-250.74 */
fn log_3(x: &Qint) -> Qint {
    let mut x = *x;
    let mut e = x.ex;

    // Find the lookup index: upper 8 bits of x
    /* x = 2^(E-63) * hh */
    let i: u16 = if x.hh > 0xb504f333f9de6484 {
        e += 1;
        (x.hh >> (63 + 1 - 7)) as u16 // the +1 accounts for the division x/2
    } else {
        (x.hh >> (63 - 7)) as u16
    };

    /* now 90 <= i <= 181 */

    x.ex -= e;

    /* now sqrt(2)/2 < x < sqrt(2) */

    let mut z = mul_qint(&x, &INVERSE_3_1[usize::from(i) - 90]);

    // find the 2nd lookup index j = floor(x*2^13)
    let j = (z.hh >> ((63 - 13 - z.ex) as u32)) as u16;

    z = mul_qint(&z, &INVERSE_3_2[usize::from(j) - 8128]); /* exact */

    // subtract 1, since 1/2 < z < 2 this is exact
    z = add_qint(&M_ONE_Q, &z);

    /* now |z| <= 0.0001220703125 = 2^-13 */

    // E·log(2)
    let r = mul_qint_2(e, &LOG2_Q);

    let mut p = p_3(&z); /* relative error < 2^-252.66, and |p| < 0.0001221 */

    p = add_qint(&LOG_INV_3_2[usize::from(j) - 8128], &p);

    p = add_qint(&LOG_INV_3_1[usize::from(i) - 90], &p);

    add_qint(&p, &r)
}

/* Given RHO1 <= rh <= RHO2, |rl/rh| < 2^-23.8899 and |rl| < 2^-14.4187,
returns an approximation eh+el of exp(rh+rl) such that:
| (eh+el) / exp(rh+rl) - 1 | < 2^-63.78597.
Moreover |el/eh| <= 2^-49.2999.
See Lemma 7 from reference [5].
The result eh+el is multiplied by s (which is +1 or -1). */
#[inline]
fn exp_1(rh: f64, rl: f64, s: f64) -> (f64, f64) {
    const RHO0: f64 = f64::from_bits(0xc0874910ee4e8a27); // -0x1.74910ee4e8a27p+9
    const RHO1: f64 = f64::from_bits(0xc08483b8cca421af); // -0x1.483b8cca421afp+9
    const RHO2: f64 = f64::from_bits(0x40862e42e709a95b); // 0x1.62e42e709a95bp+9
    const RHO3: f64 = f64::from_bits(0x40862e4316ea5df9); // 0x1.62e4316ea5df9p+9
    const INVLOG2: f64 = f64::from_bits(0x40b71547652b82fe); // 0x1.71547652b82fep+12
    const LOG2H: f64 = f64::from_bits(0x3f262e42fefa39ef); // 0x1.62e42fefa39efp-13
    const LOG2L: f64 = f64::from_bits(0x3bbabc9e3b39803f); // 0x1.abc9e3b39803fp-68

    if rh.is_nan() || rh > RHO2 {
        if !rh.is_nan() && rh > RHO3 {
            /* If rh > RHO3, we are sure there is overflow */
            return (DBL_MAX * s, DBL_MAX * s);
        }
        /* If RHO2 < rh <= RHO3, we are in the intermediate region
        where there might be overflow or not, thus we set eh = el = NaN,
        which will set res_min = res_max = NaN, the comparison
        res_min == res_max will fail: we defer to the 2nd phase. */
        return (f64::NAN, f64::NAN);
    }

    if rh < RHO1 {
        if rh < RHO0 {
            return (0.0 * s, P2_M1074 * (0.5 * s));
        }
        /* RHO0 <= rh < RHO1 or s < 0: we defer to the 2nd phase */
        return (f64::NAN, f64::NAN);
    }

    let k = roundeven_finite(rh * INVLOG2);

    let zh = LOG2H.mul_add(-k, rh);
    let zl = LOG2L.mul_add(-k, rl);

    let kk = k as i64; /* Note: k is an integer, this is just a conversion. */
    let mm = (kk >> 12) + 0x3ff;
    let i2 = ((kk >> 6) & 0x3f) as usize;
    let i1 = (kk & 0x3f) as usize;

    let (t1h, t1l, t2h, t2l) = (T1[i2][0], T1[i2][1], T2[i1][0], T2[i1][1]);
    let (eh, el) = d_mul(t2h, t2l, t1h, t1l);

    let (qh, ql) = q_1(zh + zl);
    let (eh, el) = d_mul(eh, el, qh, ql);

    /* we should have 1 < M < 2047 here, since we filtered out
    potential underflow/overflow cases at the beginning of this function */
    let d = f64::from_bits((mm as u64) << 52) * s;
    (eh * d, el * d)
}

/* returns an approximation of exp(x), for |x| < 744.45,
with relative error < 2^-121.70 */
fn exp_2(x: &Dint) -> Dint {
    if x.ex >= 10 {
        // underflow or overflow
        let mut r = *x;
        r.ex = if x.sgn == 0x1 { -1076 } else { 1025 };
        r.sgn = 0;
        return r;
    }

    /* the following multiplication does not need to be fully accurate,
    since it is only used to round x*2^12/log(2) to the nearest integer k */
    let kd = mul_dint_11(x, &LOG2_INV);

    let k = dint_toi(&kd); /* k = trunc(K) [rounded towards zero, exact] */

    /* the following multiplication needs to be fully accurate, since we need
    to compute x - k*log(2)/2^12 to full accuracy */
    let mut kd = mul_dint_int64(&LOG2, k);

    kd.ex -= 12;
    kd.sgn = u64::from(kd.sgn == 0);

    let y = add_dint(x, &kd); /* exact because Sterbenz theorem applies */

    let mm = k >> 12;
    let i2 = ((k >> 6) & 0x3f) as usize;
    let i1 = (k & 0x3f) as usize;

    let mut r = q_2(&y); /* relative error bounded by 2^-122.29, with |r| < 1.0002 */

    r = mul_dint(&T1_2[i2], &r);

    r = mul_dint(&T2_2[i1], &r);

    r.ex += mm; /* exact */
    r
}

/* returns an approximation of exp(x), for |x| < 744.45,
with relative error < 2^-241.10 */
fn exp_3(x: &Qint) -> Qint {
    /* the following multiplication does not need to be fully accurate,
    since it is only used to round x*2^12/log(2) to the nearest integer k */
    let kq = mul_qint_11(x, &LOG2_INV_Q);

    let k = qint_toi(&kq); /* k = trunc(K) [rounded towards zero, exact] */

    /* the following multiplication needs to be fully accurate, since we need
    to compute x - k*log(2)/2^12 to full accuracy */
    let mut kq = mul_qint_2(k, &LOG2_Q);

    kq.ex -= 12;
    kq.sgn = u64::from(kq.sgn == 0);

    let y = add_qint(x, &kq); /* exact because Sterbenz theorem applies */

    let mm = k >> 12;
    let i2 = ((k >> 6) & 0x3f) as usize;
    let i1 = (k & 0x3f) as usize;

    let mut r = q_3(&y); /* relative error bounded by 2^-241.11, with |r| < 1.0002 */

    r = mul_qint(&T1_3[i2], &r);

    r = mul_qint(&T2_3[i1], &r);

    r.ex = r.ex.wrapping_add(mm); /* exact */
    r
}

// (int64_t) of a double as compiled for x86-64 (cvttsd2si): truncation, and
// 0x8000000000000000 for NaN or out-of-range values (where C has undefined
// behaviour). Rust's `as` saturates instead, hence this helper. Only used
// where the value might be out of range.
#[inline]
fn f64_to_i64_x86(x: f64) -> i64 {
    // -2^63 <= x < 2^63
    if (-9223372036854775808.0..9223372036854775808.0).contains(&x) {
        x as i64
    } else {
        i64::MIN
    }
}

/*
  Computes x^y and returns Some(x^y) if the result fits into 54 bits, i.e.
  computes exactly x^y for exact and midpoint cases.
  Implements Algorithm detectRoundingBoundaryCase from [4].
  Requires that the relative error between x^y and the approximation z of
  x^y is less than 2^-117: z = x^y * (1 + eps) with |eps| < 2^-117.
  Can return Some only when (x,y) are in the set S from [4]:
  (a) either y is an integer, 2 <= y <= 34, or
  (b) x=2^E*m with m odd and y = 2^F*n with -5 <= F < 0, n odd, 3 <= n <= 34
  exact is true iff x^y is exactly representable in binary64.
*/
fn exact_pow(x: f64, y: f64, z: &Dint, exact: bool) -> Option<f64> {
    let s: i64 = if z.sgn != 0 { -1 } else { 1 };

    // Check if x = 2^E
    let (e, mut m) = extract(x); /* x = 2^E*m with m odd */

    /* x is a power of 2 */
    if m == 1 {
        let gd = (e as f64) * y;
        if is_int(gd) {
            let r = if z.sgn != 0 { -1.0 } else { 1.0 };
            let g = f64_to_i64_x86(gd);
            return Some(pow2(r, g));
        }
        return None;
    }

    if !(0.0..=34.0).contains(&y) {
        // C: if (y < 0.0 || y > 34.0) return 0; (y is not NaN here)
        return None;
    }

    let (f, n) = extract(y); /* y = 2^F*n with n odd */

    /* since y <= 34, if F >= 0, we have 2^F*n <= 34 */
    if n > 34 || f < -5 {
        return None;
    }

    if f < 0 {
        /* case (b) */
        /* check that E is divisible by 2^-F */
        if ((e as u64) & (!0u64 >> (64 + f))) != 0 {
            return None;
        }
        // since F < 0, the shift by -F is ok; C: int64 * uint64 -> uint64 -> int64
        let g: i64 = (e >> -f).wrapping_mul(n as i64);
        /* g = E*y */
        let (gg, k) = round_54(z); /* z is rounded to k*2^G */

        if !exact {
            let cnt = k.leading_zeros();
            let mut d = Dint {
                hi: (k as u64) << cnt,
                lo: 0,
                ex: gg + 63 - i64::from(cnt),
                sgn: 1 - z.sgn,
            };
            d = add_dint(z, &d); /* exact by Sterbenz theorem */
            /* multiply d by 2^116 */
            d.ex += 116;
            /* compare in absolute value with z */
            if cmp_dint_abs(&d, z) >= 0 {
                return None;
            }
        }

        if gg > g {
            return None;
        }

        /* The following code is used when k is a multiple of a power of 2,
        to reduce to 2^X*r with odd r. It checks whether k is an odd number
        multiplied by 2^(g-G). */
        // g - G >= 0 here, and in practice <= 53; the C shifts are undefined
        // beyond 63, so mirror x86-64 count masking with wrapping shifts.
        let sh = (g - gg) as u32;
        if ((k as u64) & !((!1u64).wrapping_shl(sh))) == 1u64.wrapping_shl(sh) {
            let r = (k.wrapping_shr(sh).wrapping_mul(s)) as f64;
            return Some(pow2(r, g));
        }
        return None;
    }

    /* case (a) */

    /* no overflow in n << F since 2^F*n <= 34 */
    let mut t: u64 = n << f;
    let mut k: i64 = 1;

    /* Compute k = m^t which should fit into 54 bits. */
    while t != 0 {
        if (t & 0x1) != 0 {
            // k = m * k; (__builtin_mul_overflow (m, k, &k), m unsigned, k signed)
            let p = i128::from(m) * i128::from(k);
            if p > i128::from(i64::MAX) || p < i128::from(i64::MIN) {
                return None; /* m*k overflows */
            }
            k = p as i64;
        }
        t >>= 1;
        // m *= m;
        if t != 0 {
            m = m.checked_mul(m)?; /* returns None if m*m overflows */
        }
    }

    /* check k has at most 54 bits */
    if (k >> 54) != 0 {
        return None;
    }

    let r = (k.wrapping_mul(s)) as f64;
    // C: int64_t G = E * (n << F); (uint64 arithmetic, converted to int64)
    let gg = (e as u64).wrapping_mul(n << f) as i64;
    Some(pow2(r, gg))
}

// return true if x^y is exact (and exactly representable as a double)
fn is_exact(x: f64, y: f64) -> bool {
    /* All cases such that x^y might be exact are:
    (a) |x| = 1
    (b) y integer, 0 <= y <= 33
    (c) y<0: x=1 or (x=2^e and |y|=n*2^-k with 2^k dividing e)
    (d) y>0: y=n*2^f with -5 <= f <= -1 and 1 <= n <= 33
    In cases (b)-(d), the low 42 bits of the encoding of y are zero,
    thus we use that for an early exit test. */

    // xmax[y] for 1<=y<=33 is the largest odd m such that m^y fits in 53 bits
    const XMAX: [u64; 34] = [
        0,
        0xffffffffffffffff,
        94906265,
        208063,
        9741,
        1551,
        455,
        189,
        97,
        59,
        39,
        27,
        21,
        15,
        13,
        11,
        9,
        7,
        7,
        5,
        5,
        5,
        5,
        4,
        3,
        3,
        3,
        3,
        3,
        3,
        3,
        3,
        3,
        3,
    ];

    let vu = x.to_bits();
    let wu = y.to_bits();
    if (vu << 1) != 0x7fe0000000000000 && (wu << 22) != 0 {
        return false;
    }

    if (vu << 1) == 0x7fe0000000000000 {
        // |x| = 1
        return true;
    }

    if y >= 0.0 && is_int(y) {
        /* let x = m*2^e with m an odd integer, x^y is exact when
        - y = 0 or y = 1
        - m = 1 or -1 and -1074 <= e*y < 1024
        - if |x| is not a power of 2, 2 <= y <= 33 and
          m^y should fit in 53 bits */
        let mut m = vu & 0xfffffffffffff;
        let mut e = ((vu << 1) >> 53) as i64 - 0x433;
        if e >= -1074 {
            m |= 0x10000000000000;
        } else {
            // subnormal numbers
            e += 1;
        }
        let t = m.trailing_zeros();
        m >>= t;
        e += i64::from(t);
        /* For normal numbers, we have x = m*2^e. */
        if y == 0.0 || y == 1.0 {
            return true;
        }
        if m == 1 {
            let ye = y * (e as f64);
            return (-1074.0..1024.0).contains(&ye);
        }
        // now for y < 0 or 33 < y it cannot be exact
        if y < 0.0 || 33.0 < y {
            return false;
        }
        // now 2 <= y <= 33
        let y_int = y as i32;
        if m > XMAX[y_int as usize] {
            return false;
        }
        // |x^y| = m^y * 2^(e*y)
        // (C computes my = m^y here but only uses m below)
        let t = 64 - i64::from(m.leading_zeros());
        // 2^(t-1) <= m^y < 2^t thus 2^(e*y + t - 1) <= |x^y| < 2^(e*y + t)
        let ez = e * i64::from(y_int) + t;
        if ez <= -1074 || 1024 < ez {
            return false;
        }
        // since m is odd, x^y is an odd multiple of 2^(e*y)
        return e * i64::from(y_int) >= -1074;
    }

    let mut n = wu & 0xfffffffffffff;
    let mut f = ((wu << 1) >> 53) as i64 - 0x433;
    if f >= -1074 {
        n |= 0x10000000000000;
    } else {
        // subnormal numbers
        f += 1;
    }
    let t = n.trailing_zeros();
    n >>= t;
    f += i64::from(t);
    // |y| = n*2^f with n odd

    let mut m = vu & 0xfffffffffffff;
    let mut e = ((vu << 1) >> 53) as i64 - 0x433;
    if e >= -1074 {
        m |= 0x10000000000000;
    } else {
        // subnormal numbers
        e += 1;
    }
    let t = m.trailing_zeros();
    m >>= t;
    e += i64::from(t);
    // |x| = m*2^e with m odd

    /* if y < 0 and y is not an integer, the only case where x^y might be
    exact is when x = 2^e and n*e*2^f is an integer */
    if y < 0.0 {
        if m != 1 {
            return false;
        }
        // now e <> 0 since the case |x|=1 has already been treated
        let ez: i64 = if f >= 0 {
            // if f >= 12, since n*e <> 0, (n*e)<<f cannot be in [-1074,1024)
            // C: (-n * e) << f in uint64 arithmetic, converted to int64
            if f < 12 {
                (n.wrapping_neg().wrapping_mul(e as u64) << f) as i64
            } else {
                1024
            }
        } else {
            // f < 0 thus 2^-f should divide e
            let t = e.trailing_zeros();
            if -f > i64::from(t) {
                return false; // 2^-f does not divide e
            }
            // C: (-e >> (-f)) * n in uint64 arithmetic, converted to int64
            ((-e >> (-f)) as u64).wrapping_mul(n) as i64
        };
        return (-1074..1024).contains(&ez);
    }

    /* now y > 0, y is not a integer, y = n*2^f with n odd and f < 0.
    Since x^(n*2^f) = (x^(2^f))^n, and n is odd, necessarily
    x is an exact (2^k)th power with k=-f.
    This implies x is a square. Since x = m*2^e with m odd,
    necessarily m is a square, and e is even. */
    while f != 0 {
        f += 1;
        // try to extract a square from m*2^e
        if (e & 1) != 0 {
            return false;
        }
        e /= 2;
        let dm = m as f64;
        let s = dm.sqrt().round();
        if s * s != dm {
            return false;
        }
        m = s as u64; // m remains odd (square root of an odd number)
    }

    // Now |x^y| = (m*2^e)^n with m, n odd integers
    // now for 33 < n it cannot be exact, unless m=1
    if m > 1 {
        if 33 < n {
            return false;
        }
        // now n <= 33
        if m > XMAX[n as usize] {
            return false;
        }
    }
    // |x^y| = m^n * 2^(e*n) with m odd
    let mut my = m;
    let mut n0 = n;
    while n0 > 1 {
        n0 -= 1;
        my = my.wrapping_mul(m);
    }
    // |x^y| = my * 2^(e*n)
    let t = 64 - i64::from(my.leading_zeros()); // number of significant bits of m^n
    let en = e * i64::from(n as i32);
    -1074 <= en && en + t <= 1024
}

/// Correctly rounded power function (round-to-nearest), with the code path
/// that produced the result. `FORCE` is a bit set of `FORCE_PHASE1_FAIL` and
/// `FORCE_PHASE2_FAIL`, used only by the verification harness to exercise
/// the accurate phases; the public `pow` uses `FORCE = 0`.
#[inline]
pub(crate) fn pow_impl<const FORCE: u32>(x: f64, y: f64) -> (f64, Phase) {
    let mut x = x;
    let mut s = 1.0; /* sign of the result */
    let x0 = x; // original value of x

    let xu = x.to_bits();
    let yu = y.to_bits();

    if xu >= 0x7ff0000000000000 || yu >= 0x7ff0000000000000 {
        if x.is_nan() {
            // IEEE 754-2019: pow(x,+/-0) = 1 if x is not a signaling NaN
            if y == 0.0 && !is_signaling(x) {
                return (1.0, Phase::Special);
            }

            /* pow(sNaN, y) = qNaN. Returning x+x quiets the signaling bit. */
            return (x + x, Phase::Special);
        }

        if y.is_nan() {
            // IEEE 754-2019: pow(1,y) = 1 for any y (even a quiet NaN)
            if x == 1.0 && !is_signaling(y) {
                return (1.0, Phase::Special);
            }

            // pow(x, sNaN) = qNaN (see above)
            return (y + y, Phase::Special);
        }

        match xu {
            // x = +inf
            0x7ff0000000000000 => {
                if y == 0.0 {
                    return (1.0, Phase::Special);
                }

                if y < 0.0 {
                    return (0.0, Phase::Special);
                }

                if y > 0.0 {
                    return (f64::INFINITY, Phase::Special);
                }
            }

            // x = -inf
            0xfff0000000000000 => {
                /* first check y=+/-inf since is_int uses roundeven_finite */
                if yu == 0x7ff0000000000000 {
                    return (y, Phase::Special); // -Inf^Inf = Inf
                }

                if yu == 0xfff0000000000000 {
                    return (0.0, Phase::Special); // -Inf^-Inf = +0
                }

                // y is an odd integer
                if is_int(y) && !is_int(y * 0.5) {
                    // y is a negative odd integer
                    if y < 0.0 {
                        return (-0.0, Phase::Special);
                    }
                    // y is a positive odd integer
                    return (f64::NEG_INFINITY, Phase::Special);
                }

                // y is a negative even integer or is negative non-integer
                if y < 0.0 {
                    return (0.0, Phase::Special);
                }

                // y is a positive even integer or is positive non-integer
                if y > 0.0 {
                    return (f64::INFINITY, Phase::Special);
                }
            }
            _ => {}
        }

        match yu {
            // y = +inf
            0x7ff0000000000000 => {
                if x == 0.0 {
                    return (0.0, Phase::Special);
                }

                if x == -1.0 || x == 1.0 {
                    return (1.0, Phase::Special);
                }

                if -1.0 < x && x < 1.0 {
                    return (0.0, Phase::Special);
                }

                if x < -1.0 || 1.0 < x {
                    return (f64::INFINITY, Phase::Special);
                }
            }

            // y = -inf
            0xfff0000000000000 => {
                if x == 0.0 {
                    return (f64::INFINITY, Phase::Special);
                }

                if x == -1.0 || x == 1.0 {
                    return (1.0, Phase::Special);
                }

                if -1.0 < x && x < 1.0 {
                    return (f64::INFINITY, Phase::Special);
                }

                if x < -1.0 || 1.0 < x {
                    return (0.0, Phase::Special);
                }
            }
            _ => {}
        }
    } // From now on, x and y are finite values

    /* first deal with the case x <= 0 */
    if x <= 0.0 {
        /* pow(x,+/-0) is 1 if x is not a signaling NaN. */
        if y == 0.0 {
            return (1.0, Phase::Special);
        }

        match xu {
            // x = +0.0
            0x0 => {
                // y is an odd integer
                if is_int(y) && !is_int(y * 0.5) {
                    // y is a negative odd integer
                    if y < 0.0 {
                        return (f64::INFINITY, Phase::Special);
                    }

                    // y is a positive odd integer
                    return (0.0, Phase::Special);
                }

                // y is positive (non-integer or a positive even integer)
                if y > 0.0 {
                    return (0.0, Phase::Special);
                }

                // y is negative, finite and an even integer or a non-integer
                return (f64::INFINITY, Phase::Special);
            }

            // x = -0.0
            0x8000000000000000 => {
                // y is an odd integer
                if is_int(y) && !is_int(y * 0.5) {
                    // y is a negative odd integer
                    if y < 0.0 {
                        return (f64::NEG_INFINITY, Phase::Special);
                    }

                    // y is a positive odd integer
                    return (-0.0, Phase::Special);
                }

                // y is positive (non-integer or a positive even integer)
                if y > 0.0 {
                    return (0.0, Phase::Special);
                }

                // y is negative, finite and an even integer or a non-integer
                return (f64::INFINITY, Phase::Special);
            }
            _ => {}
        }

        if !is_int(y) {
            return (f64::NAN, Phase::Special);
        }

        const CS: [f64; 2] = [1.0, -1.0];

        // set sign to 1 for y even, to -1 for y odd
        let y_parity = if y.abs() >= P2_53 {
            0
        } else {
            ((y as i64) & 0x1) as usize
        };
        s = CS[y_parity];

        // Set x to |x| for the rest of the computation
        x = -x;
    } /* end of case x <= 0 */

    /* This is Algorithm phase_1 from reference [5]. */

    // approximate log(x)
    let (mut lh, mut ll, cancel) = log_1(x);

    /* We should avoid a spurious underflow/overflow in y*log(x).
    Underflow: we require |y| >= 2^-969. Overflow: we require |y| < 2^1014. */
    let ey = (yu >> 52) & 0x7ff;
    if !(0x36..0x7f5).contains(&ey) {
        lh = f64::NAN;
        ll = f64::NAN;
    }

    // approximate y * log(x)
    let (rh, rl) = s_mul(y, lh, ll);

    let (res_h, res_l) = exp_1(rh, rl, s); /* 1 <= res_h < 2 */

    /* The error bounds 2^-63.797 and 2^-57.579 are those from Algorithm
    phase_1 from reference [5]. */
    const ERR: [f64; 2] = [
        f64::from_bits(0x3bf2700000000000), /* 0x1.27p-64: 2^-63.797 < 0x1.27p-64 */
        f64::from_bits(0x3c55700000000000), /* 0x1.57p-58: 2^-57.579 < 0x1.57p-58 */
    ];
    let res_min = res_h + ERR[cancel].mul_add(-res_h, res_l);
    let res_max = res_h + ERR[cancel].mul_add(res_h, res_l);

    let exact = is_exact(x, y);
    // (the C restores the inexact flag here when exact; flags are not modelled)

    if (FORCE & FORCE_PHASE1_FAIL) == 0 && res_min == res_max {
        return (res_max, Phase::Fast);
    }

    // Easy cases
    if y == 1.0 {
        return (s * x, Phase::Easy);
    }

    if y == 2.0 {
        return (x * x, Phase::Easy);
    }

    if y == 0.5 {
        return (x.sqrt(), Phase::Easy);
    }

    if y == 0.0 {
        return (1.0, Phase::Easy);
    }

    // Second iteration of rounding
    let mut xd = dint_fromd(x); /* exact: |x| = 2^(X->ex-63) * X->hi (X->lo = 0) */
    let yd = dint_fromd(y); /* exact: |y| = 2^(Y->ex-63) * Y->hi (Y->lo = 0) */

    xd.sgn = 0x0; /* force the sign of X to +1 */

    let mut r = log_2(&xd); /* relative error bounded by 2^-122.88 */

    r = mul_dint_21(&r, &yd);

    r = exp_2(&r); /* relative error < 2^-121.70 */

    // Rounding test

    // 2^R.ex <= R < 2^(R.ex+1)

    /* case R < 2^-1075: underflow case */
    if r.ex < -1075 {
        return (0.5 * (s * P2_M1074), Phase::DintUnderflow);
    }

    let rd: bool = if r.ex < -1022 {
        /* subnormal case */
        /* -1075 <= R.ex <= -1023 thus 2^-1075 <= R < 2^-1022 */
        let ex = (-(1022 + r.ex)) as u64; /* 1 <= ex <= 53 */
        // the significand has to be shifted right by ex bits
        let m = (r.lo >> (10 + ex)) | (r.hi << (54 - ex));
        m.wrapping_add(14) > (2 * 14)
    } else {
        const ERR_BND_2: u64 = 28;
        let lo = (r.lo >> 10) | (r.hi << 54);
        /* lo contains the 64 bits after the round bit */
        lo.wrapping_add(ERR_BND_2) > (2 * ERR_BND_2)
    };

    r.sgn = u64::from(s == -1.0);

    if (FORCE & FORCE_PHASE2_FAIL) == 0 && rd {
        return (dint_tod(&r), Phase::Dint);
    }

    // Detect rounding boundary cases
    if let Some(e) = exact_pow(x0, y, &r, exact) {
        return (e, Phase::Exact);
    }

    // Hard to round cases
    let mut qx = qint_fromd(x); /* exact */
    let qy = qint_fromd(y); /* exact */

    qx.sgn = 0x0; /* force the sign of X to +1 */

    let mut qr = log_3(&qx); /* relative error bounded by 2^-250.74 */

    qr = mul_qint_41(&qr, &qy);

    let mut qz = exp_3(&qr); /* relative error < 2^-241.10 */

    /* extra rounding test */
    const ERR_BND_3: u64 = 60; /* floor(62433/2^10) */
    let r1 = (qz.hh << 54) | (qz.hl >> 10);
    let r2 = (qz.hl << 54) | (qz.lh >> 10);
    let r3 = (qz.lh << 54) | (qz.ll >> 10);
    let rd3 = !((r1 == 0 && r2 == 0 && r3 <= ERR_BND_3)
        || (!r1 == 0 && !r2 == 0 && r3.wrapping_add(2 * ERR_BND_3) <= ERR_BND_3));

    qz.sgn = u64::from(s == -1.0);
    qz.ll &= !0u64 << 10;

    if rd3 {
        return (qint_tod(qz), Phase::Qint);
    }

    /* We can end up here for x^y very close to 1. For |qR| < 2^-55,
    we have 1-2^-54 < exp(qR) < 1+2^-53, thus exp(qR) rounds either
    to nextbelow(1), to 1 or to nextabove(1). */
    if qr.ex < -56 {
        /* the upper limb h of qR encodes h/2^63, thus a number in [1, 2) */
        let v = if qr.sgn == 0x0 {
            1.0 + P2_M100
        } else {
            1.0 - P2_M100
        };
        return (v, Phase::QintNearOne);
    }

    // DEVIATION: the C prints "Unexpected worst-case found" and calls
    // exit(1) here. This is not expected to be reachable (no such input is
    // known); a library cannot exit, so we return the third-phase rounding.
    (qint_tod(qz), Phase::Unexpected)
}

/// Correctly rounded `x^y` (round-to-nearest), bit-identical to CORE-MATH's
/// `cr_pow`.
#[inline]
#[must_use]
pub fn pow(x: f64, y: f64) -> f64 {
    pow_impl::<0>(x, y).0
}

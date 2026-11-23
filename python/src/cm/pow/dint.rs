// Rust translation of CORE-MATH's dint.h (https://core-math.gitlabpages.inria.fr/).
// Original: Copyright (c) 2022, 2023 CERN and Inria
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

//! 128-bit floating-point ("dint") arithmetic used by the second phase of pow.
//!
//! A value is `(-1)^sgn * (hi + lo/2^64) * 2^(ex-63)`; the binary point sits
//! after the most significant bit of `hi`. Every function mirrors the C
//! routine of the same name bit for bit; the C versions write through an
//! output pointer, these return the result (all call sites were checked:
//! each C routine reads every input field it needs before writing the
//! output, so aliasing of input and output does not change the result).

use crate::cm::pow::dint_tables::ZERO;

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub(crate) struct Dint {
    pub(crate) hi: u64,
    pub(crate) lo: u64,
    pub(crate) ex: i64,
    pub(crate) sgn: u64,
}

impl Dint {
    /// The 128-bit significand (the `r` member of the C union).
    #[inline]
    pub(crate) fn r(&self) -> u128 {
        (u128::from(self.hi) << 64) | u128::from(self.lo)
    }

    #[inline]
    pub(crate) fn set_r(&mut self, r: u128) {
        self.hi = (r >> 64) as u64;
        self.lo = r as u64;
    }
}

#[inline]
fn cmp(a: i64, b: i64) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

#[inline]
fn cmpu(a: u64, b: u64) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

#[inline]
fn cmpu128(a: u128, b: u128) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

// Return true if a = 0
#[inline]
fn dint_zero_p(a: &Dint) -> bool {
    a.hi == 0
}

// Compare the absolute values of a and b
// Return -1 if |a| < |b|
// Return  0 if |a| = |b|
// Return +1 if |a| > |b|
#[inline]
pub(crate) fn cmp_dint_abs(a: &Dint, b: &Dint) -> i32 {
    if dint_zero_p(a) {
        return if dint_zero_p(b) { 0 } else { -1 };
    }
    if dint_zero_p(b) {
        return 1;
    }
    let c1 = cmp(a.ex, b.ex);
    if c1 != 0 { c1 } else { cmpu128(a.r(), b.r()) }
}

#[inline]
fn cmp_dint_11(a: &Dint, b: &Dint) -> i32 {
    let c1 = cmp(a.ex, b.ex);
    if c1 != 0 { c1 } else { cmpu(a.hi, b.hi) }
}

// Add two Dint values, with error bounded by 2 ulps (ulp_128)
// (more precisely 1 ulp when a and b have same sign, 2 ulps otherwise)
// Moreover, when Sterbenz theorem applies, i.e., |b| <= |a| <= 2|b|
// and a,b are of different signs, there is no error, i.e., r = a-b.
#[inline]
pub(crate) fn add_dint(a: &Dint, b: &Dint) -> Dint {
    if (a.hi | a.lo) == 0 {
        return *b;
    }

    let (a, b) = match cmp_dint_abs(a, b) {
        0 => {
            if (a.sgn ^ b.sgn) != 0 {
                return ZERO;
            }
            let mut r = *a;
            r.ex += 1;
            return r;
        }
        -1 => (b, a), // |A| < |B|: swap operands
        _ => (a, b),
    };

    // From now on, |A| > |B| thus a.ex >= b.ex

    let a_r = a.r();
    let mut b_r = b.r();
    let k = a.ex.wrapping_sub(b.ex) as u64;

    if k > 0 {
        b_r = if k < 128 { b_r >> k } else { 0 };
    }

    let mut c: u128;
    let sgn = a.sgn as u8; // C: unsigned char sgn = a->sgn

    let mut r_ex = a.ex; /* tentative exponent for the result */

    if (a.sgn ^ b.sgn) != 0 {
        /* a and b have different signs C = A + (-B) */
        c = a_r.wrapping_sub(b_r);
        let ch = (c >> 64) as u64;
        let mut ex: u64 = if ch != 0 {
            u64::from(ch.leading_zeros())
        } else {
            64 + u64::from((c as u64).leading_zeros())
        };
        if ex > 0 {
            if k == 1 {
                /* Sterbenz case */
                c = (a_r << ex).wrapping_sub(b.r() << (ex - 1));
            } else {
                c = (a_r << ex).wrapping_sub(b_r << ex);
            }
            r_ex = r_ex.wrapping_sub(ex as i64);
            ex = u64::from(((c >> 64) as u64).leading_zeros());
            /* Fall through with the code for ex = 0. */
        }
        c <<= ex;
        r_ex = r_ex.wrapping_sub(ex as i64);
    } else {
        c = a_r.wrapping_add(b_r);
        if c < a_r {
            c = (1u128 << 127) | (c >> 1);
            r_ex += 1;
        }
    }

    let mut r = Dint {
        hi: 0,
        lo: 0,
        ex: r_ex,
        sgn: u64::from(sgn),
    };
    r.set_r(c);
    r
}

// same as add_dint, but assumes the lower limbs and a and b are zero
// error is bounded by 2 ulps (ulp_64)
//
// Note: outside of the early-return paths, the C routine only writes r->hi,
// r->ex and r->sgn, leaving r->lo untouched. Every call site in pow.c passes
// the same pointer for r and b (add_dint_11 (r, &C[k], r)), so the low limb
// of the result is the low limb of the original b; we reproduce that here.
#[inline]
pub(crate) fn add_dint_11(a: &Dint, b: &Dint) -> Dint {
    let r_lo = b.lo;
    if a.hi == 0 {
        return *b;
    }

    if b.hi == 0 {
        return *a;
    }

    let (a, b) = match cmp_dint_11(a, b) {
        0 => {
            if (a.sgn ^ b.sgn) != 0 {
                return ZERO;
            }
            let mut r = *a;
            r.ex += 1;
            return r;
        }
        -1 => (b, a), // |A| < |B|: swap operands
        _ => (a, b),
    };

    // From now on, |A| > |B| thus a.ex >= b.ex

    // In C, A, B and the intermediate values are uint64_t (stored in a u128
    // whose upper half is always zero, then truncated into r->hi), so plain
    // 64-bit arithmetic is equivalent.
    let aa = a.hi;
    let mut bb = b.hi;

    if a.ex > b.ex {
        let k = a.ex.wrapping_sub(b.ex) as u64;
        bb = if k < 64 { bb >> k } else { 0 };
    }

    let mut c: u64;
    let sgn = a.sgn as u8;

    let mut r_ex = a.ex; /* tentative exponent for the result */

    if (a.sgn ^ b.sgn) != 0 {
        // a and b have different signs C = A + (-B)
        c = aa.wrapping_sub(bb);
        let mut ex = u64::from(c.leading_zeros());
        if ex > 0 {
            c = (aa << ex).wrapping_sub(bb << ex);
            r_ex = r_ex.wrapping_sub(ex as i64);
            ex = u64::from(c.leading_zeros());
            /* Fall through with the code for ex = 0. */
        }
        c <<= ex;
        r_ex = r_ex.wrapping_sub(ex as i64);
    } else {
        c = aa.wrapping_add(bb);
        if c < aa {
            c = (1u64 << 63) | (c >> 1);
            r_ex += 1;
        }
    }

    Dint {
        hi: c,
        lo: r_lo,
        ex: r_ex,
        sgn: u64::from(sgn),
    }
}

// Multiply two Dint numbers, with error bounded by 6 ulps
// on the 128-bit floating-point numbers.
#[inline]
pub(crate) fn mul_dint(a: &Dint, b: &Dint) -> Dint {
    let bh = u128::from(b.hi);
    let bl = u128::from(b.lo);

    /* compute the two middle terms */
    let m1 = u128::from(a.hi) * bl;
    let m2 = u128::from(a.lo) * bh;

    /* put the 128-bit product of the high terms in r */
    let mut r = u128::from(a.hi) * bh;

    /* there can be no overflow in the following addition since r <= (B-1)^2
    with B=2^64, (m1>>64) <= B-1 and (m2>>64) <= B-1, thus the sum is
    bounded by (B-1)^2+2*(B-1) = B^2-1 */
    r += (m1 >> 64) + (m2 >> 64);

    // Ensure that r->hi starts with a 1
    let ex = (r >> 127) as u64;
    r <<= 1 - ex;

    let mut out = Dint {
        hi: 0,
        lo: 0,
        ex: a.ex + b.ex + ex as i64,
        sgn: a.sgn ^ b.sgn,
    };
    out.set_r(r);
    out
}

// Multiply two Dint numbers, assuming the low part of b is zero
// with error bounded by 2 ulps
#[inline]
pub(crate) fn mul_dint_21(a: &Dint, b: &Dint) -> Dint {
    let bh = u128::from(b.hi);
    let hi = u128::from(a.hi) * bh;
    let lo = u128::from(a.lo) * bh;

    /* put the 128-bit product of the high terms in r */
    let mut r = hi;

    /* add the middle term */
    r += lo >> 64;

    // Ensure that r->hi starts with a 1
    let ex = (r >> 127) as u64;
    r <<= 1 - ex;

    let mut out = Dint {
        hi: 0,
        lo: 0,
        ex: a.ex + b.ex + ex as i64,
        sgn: a.sgn ^ b.sgn,
    };
    out.set_r(r);
    out
}

/* Same as mul_dint_21, but assumes the low part of a and b is zero.
This operation is exact. */
#[inline]
pub(crate) fn mul_dint_11(a: &Dint, b: &Dint) -> Dint {
    /* put the 128-bit product of the high terms in r */
    let mut r = u128::from(a.hi) * u128::from(b.hi);

    // Ensure that r->hi starts with a 1
    let ex = (r >> 127) as u64;
    r <<= 1 - ex;

    let mut out = Dint {
        hi: 0,
        lo: 0,
        ex: a.ex + b.ex + ex as i64,
        sgn: a.sgn ^ b.sgn,
    };
    out.set_r(r);
    out
}

// Multiply an integer with a Dint variable, with error < 1 ulp
#[inline]
pub(crate) fn mul_dint_int64(a: &Dint, b: i64) -> Dint {
    if b == 0 {
        return ZERO;
    }

    let c: u64 = if b < 0 {
        (b as u64).wrapping_neg()
    } else {
        b as u64
    };
    let sgn = if b < 0 { u64::from(a.sgn == 0) } else { a.sgn };
    let mut ex = a.ex + 64;

    let mut r = u128::from(a.hi) * u128::from(c);

    // Warning: if c=1, we might have r->hi=0
    let rhi = (r >> 64) as u64;
    let m: u32 = if rhi != 0 { rhi.leading_zeros() } else { 64 };
    r <<= m;
    ex -= i64::from(m);

    let mut l = u128::from(a.lo) * u128::from(c);
    /* We have to shift l by 64 bits to the right to align with hi*c,
    and by m bits to the left to align with t.r << m. Since hi*c < 2^(128-m)
    and hi >= 2^63, we know that c < 2^(65-m) thus
    l*2^(m-1) < 2^64*2^(65-m)*2^(m-1) = 2^128, and l << (m - 1) will
    not overflow. */
    l = (l << (m - 1)) >> 63;

    r = r.wrapping_add(l);
    if r < l {
        r = (1u128 << 127) | (r >> 1);
        ex += 1;
    }

    let mut out = Dint {
        hi: 0,
        lo: 0,
        ex,
        sgn,
    };
    out.set_r(r);
    out
}

// Extract both the mantissa and exponent of a double (fast_extract in pow.h)
#[inline]
pub(crate) fn fast_extract(x: f64) -> (i64, u64) {
    let u = x.to_bits();
    let e = ((u >> 52) & 0x7ff) as i64;
    let m = (u & (!0u64 >> 12)) + if e != 0 { 1u64 << 52 } else { 0 };
    (e - 0x3ff, m)
}

// Convert a non-zero double to the corresponding Dint value
#[inline]
pub(crate) fn dint_fromd(b: f64) -> Dint {
    let (ex, hi) = fast_extract(b);

    /* |b| = 2^(ex-52)*hi */

    let t = hi.leading_zeros();

    Dint {
        hi: hi << t,
        lo: 0,
        ex: ex - i64::from(if t > 11 { t - 12 } else { 0 }),
        sgn: u64::from(b < 0.0),
    }
    /* b = 2^ex*hi/2^63 where 1 <= hi/2^63 < 2 */
}

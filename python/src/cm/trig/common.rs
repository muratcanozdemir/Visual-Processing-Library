// Rust translation of the code shared by CORE-MATH's sin.c, cos.c and tan.c
// (https://core-math.gitlabpages.inria.fr/): the dint64_t arithmetic, range
// reduction and tables.
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

//! Shared code: `dint64_t` 128-bit arithmetic (copied in CORE-MATH from
//! dint.h and pow.[ch]), the accurate and fast range reductions, the
//! double-double helpers and the polynomial evaluations used by sin, cos and
//! tan. Only rounding to nearest is supported.

// Faithful translation: keep the C's late initialisations.
#![allow(clippy::needless_late_init)]

mod tables;

pub(crate) use tables::{C, PC, PCFAST, PS, PSFAST, S, SC, T};

/// The C `dint64_t`: represents `(-1)^sgn*(hi/2^64+lo/2^128)*2^ex`.
#[derive(Clone, Copy, Debug)]
pub(crate) struct Dint {
    pub(crate) hi: u64,
    pub(crate) lo: u64,
    pub(crate) ex: i64,
    pub(crate) sgn: u64,
}

impl Dint {
    pub(crate) const fn new(hi: u64, lo: u64, ex: i64, sgn: u64) -> Self {
        Self { hi, lo, ex, sgn }
    }

    /// The C union member `r` (hi:lo as a 128-bit integer).
    #[inline(always)]
    const fn r(&self) -> u128 {
        ((self.hi as u128) << 64) | self.lo as u128
    }

    #[inline(always)]
    fn set_r(&mut self, r: u128) {
        self.hi = (r >> 64) as u64;
        self.lo = r as u64;
    }
}

// Extract both the mantissa and exponent of a double
#[inline(always)]
fn fast_extract(x: f64) -> (i64, u64) {
    let u = x.to_bits();
    let e = ((u >> 52) & 0x7ff) as i64;
    let m = (u & (!0u64 >> 12)) + if e != 0 { 1u64 << 52 } else { 0 };
    (e - 0x3fe, m)
}

// Return non-zero if a = 0
#[inline(always)]
fn dint_zero_p(a: &Dint) -> bool {
    a.hi == 0
}

#[inline(always)]
fn cmp(a: i64, b: i64) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

#[inline(always)]
fn cmpu128(a: u128, b: u128) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

/* ZERO is a dint64_t representation of 0, which ensures that
dint_tod(ZERO) = 0 */
const ZERO: Dint = Dint::new(0x0, 0x0, -1076, 0x0);
// ONE is a dint64_t representation of 1 (tan.c only)
pub(crate) const ONE: Dint = Dint::new(0x8000000000000000, 0x0, 1, 0x0);
// MAGIC is a dint64_t representation of 1/2^11
pub(crate) const MAGIC: Dint = Dint::new(0x8000000000000000, 0x0, -10, 0x0);

// Compare the absolute values of a and b
// Return -1 if |a| < |b|
// Return  0 if |a| = |b|
// Return +1 if |a| > |b|
#[inline(always)]
fn cmp_dint_abs(a: &Dint, b: &Dint) -> i32 {
    if dint_zero_p(a) {
        return if dint_zero_p(b) { 0 } else { -1 };
    }
    if dint_zero_p(b) {
        return 1;
    }
    let c1 = cmp(a.ex, b.ex);
    if c1 != 0 { c1 } else { cmpu128(a.r(), b.r()) }
}

/* Add two dint64_t values, with error bounded by 2 ulps (ulp_128)
(more precisely 1 ulp when a and b have same sign, 2 ulps otherwise).
Moreover, when Sterbenz theorem applies, i.e., |b| <= |a| <= 2|b|
and a,b are of different signs, there is no error, i.e., r = a-b.
(The C version takes pointers and allows r to alias a or b; it only writes
r->r after its last read of a/b, so the by-value form is equivalent.) */
#[inline(always)]
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
            r.ex = r.ex.wrapping_add(1);
            return r;
        }
        -1 => (b, a), // |A| < |B|: swap operands
        _ => (a, b),
    };

    // From now on, |A| > |B| thus a->ex >= b->ex

    let a_r = a.r();
    let mut bb = b.r();
    let k: u64 = (a.ex as u64).wrapping_sub(b.ex as u64);

    if k > 0 {
        bb = if k < 128 { bb >> k } else { 0 };
    }

    let mut c: u128;
    // C: unsigned char sgn = a->sgn;
    let sgn = u64::from(a.sgn as u8);

    let mut r_ex = a.ex; /* tentative exponent for the result */

    if (a.sgn ^ b.sgn) != 0 {
        c = a_r.wrapping_sub(bb);
        let ch = (c >> 64) as u64;
        /* We can't have C=0 here since we excluded the case |A| = |B|,
        thus __builtin_clzll(C) is well-defined below.
        (C passes the 128-bit C to clzll, i.e. its low 64 bits.) */
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
                c = (a_r << ex).wrapping_sub(bb << ex);
            }
            r_ex = r_ex.wrapping_sub(ex as i64);
            ex = u64::from(((c >> 64) as u64).leading_zeros());
            /* Fall through with the code for ex = 0. */
        }
        c <<= ex;
        r_ex = r_ex.wrapping_sub(ex as i64);
    } else {
        c = a_r.wrapping_add(bb);
        if c < a_r {
            c = (1u128 << 127) | (c >> 1);
            r_ex = r_ex.wrapping_add(1);
        }
    }

    let mut r = Dint::new(0, 0, r_ex, sgn);
    r.set_r(c);
    r
}

/* Multiply two dint64_t numbers, with error bounded by 6 ulps
on the 128-bit floating-point numbers. */
#[inline(always)]
pub(crate) fn mul_dint(a: &Dint, b: &Dint) -> Dint {
    let bh = b.hi as u128;
    let bl = b.lo as u128;

    /* compute the two middle terms */
    let m1 = (a.hi as u128) * bl;
    let m2 = (a.lo as u128) * bh;

    /* put the 128-bit product of the high terms in r */
    let mut r = (a.hi as u128) * bh;

    /* there can be no overflow in the following addition */
    r = r.wrapping_add((m1 >> 64) + (m2 >> 64));

    // Ensure that r->hi starts with a 1
    let ex = (r >> 127) as u64;
    r <<= 1 - ex;

    // Exponent and sign
    let r_ex =
        a.ex.wrapping_add(b.ex)
            .wrapping_add(ex as i64)
            .wrapping_sub(1);
    let mut out = Dint::new(0, 0, r_ex, a.sgn ^ b.sgn);
    out.set_r(r);
    out
}

/* Multiply two dint64_t numbers, assuming the low part of b is zero
with error bounded by 2 ulps */
#[inline(always)]
pub(crate) fn mul_dint_21(a: &Dint, b: &Dint) -> Dint {
    let bh = b.hi as u128;
    let hi = (a.hi as u128) * bh;
    let lo = (a.lo as u128) * bh;

    /* put the 128-bit product of the high terms in r */
    let mut r = hi;

    /* add the middle term */
    r = r.wrapping_add(lo >> 64);

    // Ensure that r->hi starts with a 1
    let ex = (r >> 127) as u64;
    r <<= 1 - ex;

    // Exponent and sign
    let r_ex =
        a.ex.wrapping_add(b.ex)
            .wrapping_add(ex as i64)
            .wrapping_sub(1);
    let mut out = Dint::new(0, 0, r_ex, a.sgn ^ b.sgn);
    out.set_r(r);
    out
}

// Convert a non-zero double to the corresponding dint64_t value
#[inline(always)]
pub(crate) fn dint_fromd(b: f64) -> Dint {
    let (ex, hi) = fast_extract(b);

    /* |b| = 2^(ex-52)*hi */

    let t: u32 = hi.leading_zeros();

    let sgn = u64::from(b < 0.0);
    let hi = hi << t;
    let ex = ex - if t > 11 { i64::from(t - 12) } else { 0 };
    /* b = 2^ex*hi/2^64 where 1/2 <= hi/2^64 < 1 */
    Dint::new(hi, 0, ex, sgn)
}

// Rounding to nearest only (the C switches on fegetround()).
#[inline(always)]
fn subnormalize_dint(a: &mut Dint) {
    if a.ex > -1023 {
        return;
    }

    let ex: u64 = (1011i64.wrapping_add(a.ex) as u64).wrapping_neg();

    let mut hi = a.hi.wrapping_shr(ex as u32);
    let md = a.hi.wrapping_shr(ex.wrapping_sub(1) as u32) & 0x1;
    let lo = (a.hi & (!0u64).wrapping_shr(ex as u32)) != 0 || a.lo != 0;

    // FE_TONEAREST
    hi = hi.wrapping_add(if lo { md } else { hi & md });

    a.hi = hi.wrapping_shl(ex as u32);
    a.lo = 0;

    if a.hi == 0 {
        a.ex = a.ex.wrapping_add(1);
        a.hi = 1u64 << 63;
    }
}

// Convert a dint64_t value to a double
#[inline(always)]
pub(crate) fn dint_tod(a: &mut Dint) -> f64 {
    subnormalize_dint(a);

    let mut r = f64::from_bits((a.hi >> 11) | (0x3ffu64 << 52));

    let mut rd = 0.0;
    if (a.hi >> 10) & 0x1 != 0 {
        rd += f64::from_bits(0x3ca0000000000000); // 0x1p-53
    }

    if (a.hi & 0x3ff) != 0 || a.lo != 0 {
        rd += f64::from_bits(0x3c90000000000000); // 0x1p-54
    }

    if a.sgn != 0 {
        rd = -rd;
    }

    r = f64::from_bits(r.to_bits() | a.sgn << 63);
    r += rd;

    let e: f64;

    if a.ex > -1022 {
        // The result is a normal double
        if a.ex > 1024 {
            if a.ex == 1025 {
                r *= f64::from_bits(0x4000000000000000); // 0x1p+1
                e = f64::from_bits(0x7fe0000000000000); // 0x1p+1023
            } else {
                r = f64::from_bits(0x7fefffffffffffff); // 0x1.fffffffffffffp+1023
                e = f64::from_bits(0x7fefffffffffffff); // 0x1.fffffffffffffp+1023
            }
        } else {
            e = f64::from_bits(((a.ex.wrapping_add(1022) & 0x7ff) as u64) << 52);
        }
    } else if a.ex < -1073 {
        if a.ex == -1074 {
            r *= f64::from_bits(0x3fe0000000000000); // 0x1p-1
            e = f64::from_bits(0x0000000000000001); // 0x1p-1074
        } else {
            r = f64::from_bits(0x0000000000000001); // 0x0.0000000000001p-1022
            e = f64::from_bits(0x0000000000000001); // 0x0.0000000000001p-1022
        }
    } else {
        e = f64::from_bits(1u64.wrapping_shl(a.ex.wrapping_add(1073) as u32));
    }

    r * e
}

// Multiply exactly a and b, such that *hi + *lo = a * b.
#[inline(always)]
pub(crate) fn a_mul(a: f64, b: f64) -> (f64, f64) {
    let hi = a * b;
    let lo = a.mul_add(b, -hi);
    (hi, lo)
}

/* Multiply a double with a double double : a * (bh + bl)
with error bounded by ulp(lo) */
#[inline(always)]
pub(crate) fn s_mul(a: f64, bh: f64, bl: f64) -> (f64, f64) {
    let (hi, lo) = a_mul(a, bh); /* exact */
    let lo = a.mul_add(bl, lo);
    (hi, lo)
}

// Returns (ah + al) * (bh + bl) - (al * bl)
#[inline(always)]
fn d_mul(ah: f64, al: f64, bh: f64, bl: f64) -> (f64, f64) {
    let (hi, s) = a_mul(ah, bh);
    let t = al.mul_add(bh, s);
    let lo = ah.mul_add(bl, t);
    (hi, lo)
}

#[inline(always)]
pub(crate) fn fast_two_sum(a: f64, b: f64) -> (f64, f64) {
    let hi = a + b;
    let e = hi - a; /* exact */
    let lo = b - e; /* exact */
    (hi, lo)
}

/* Put in h+l an approximation of sin2pi(xh+xl).
Assume uh + ul approximates (xh+xl)^2. */
#[inline(always)]
pub(crate) fn eval_ps_fast(xh: f64, xl: f64, uh: f64, ul: f64) -> (f64, f64) {
    let mut h = PSFAST[4]; // degree 7
    h = h.mul_add(uh, PSFAST[3]); // degree 5
    h = h.mul_add(uh, PSFAST[2]); // degree 3
    let (h, mut l) = s_mul(h, uh, ul);
    let (h, t) = fast_two_sum(PSFAST[0], h);
    l += PSFAST[1] + t;
    // multiply by xh+xl
    d_mul(h, l, xh, xl)
}

/* Put in h+l an approximation of cos2pi(xh+xl).
Assume uh + ul approximates (xh+xl)^2. */
#[inline(always)]
pub(crate) fn eval_pc_fast(uh: f64, ul: f64) -> (f64, f64) {
    let mut h = PCFAST[4]; // degree 6
    h = h.mul_add(uh, PCFAST[3]); // degree 4
    h = h.mul_add(uh, PCFAST[2]); // degree 2
    let (h, mut l) = s_mul(h, uh, ul);
    let (h, t) = fast_two_sum(PCFAST[0], h);
    l += PCFAST[1] + t;
    (h, l)
}

/* Put in Y an approximation of sin2pi(X), for 0 <= X < 2^-11,
where X2 approximates X^2. */
pub(crate) fn eval_ps(x: &Dint, x2: &Dint) -> Dint {
    let mut y = mul_dint_21(x2, &PS[5]); // degree 11
    y = add_dint(&y, &PS[4]); // degree 9
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PS[3]); // degree 7
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PS[2]); // degree 5
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PS[1]); // degree 3
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PS[0]); // degree 1
    mul_dint(&y, x) // multiply by X
}

/* Put in Y an approximation of cos2pi(X), for 0 <= X < 2^-11,
where X2 approximates X^2. */
pub(crate) fn eval_pc(x2: &Dint) -> Dint {
    let mut y = mul_dint_21(x2, &PC[5]); // degree 10
    y = add_dint(&y, &PC[4]); // degree 8
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PC[3]); // degree 6
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PC[2]); // degree 4
    y = mul_dint(&y, x2);
    y = add_dint(&y, &PC[1]); // degree 2
    y = mul_dint(&y, x2);
    add_dint(&y, &PC[0]) // degree 0
}

// normalize X such that X->hi has its most significant bit set (if X <> 0)
fn normalize(x: &mut Dint) {
    if x.hi != 0 {
        let cnt = x.hi.leading_zeros();
        if cnt != 0 {
            x.hi = (x.hi << cnt) | (x.lo >> (64 - cnt));
            x.lo <<= cnt;
        }
        x.ex -= i64::from(cnt);
    } else if x.lo != 0 {
        let cnt = x.lo.leading_zeros();
        x.hi = x.lo << cnt;
        x.lo = 0;
        x.ex -= 64 + i64::from(cnt);
    }
}

#[inline(always)]
fn mul64(a: u64, b: u64) -> u128 {
    (a as u128) * (b as u128)
}

/* Approximate X/(2pi) mod 1. If Xin is the input value, and Xout the
output value, we have:
|Xout - (Xin/(2pi) mod 1)| < 2^-126.67*|Xout|
Assert X is normalized at input, and normalize X at output. */
pub(crate) fn reduce(x: &mut Dint) {
    let e = x.ex as i32;
    let mut u: u128;

    if e <= 1 {
        // |X| < 2
        u = mul64(x.hi, T[1]);
        let tiny = u as u64;
        x.lo = (u >> 64) as u64;
        u = mul64(x.hi, T[0]);
        x.lo = x.lo.wrapping_add(u as u64);
        x.hi = ((u >> 64) as u64).wrapping_add(u64::from(x.lo < u as u64));
        let e0 = x.ex;
        normalize(x);
        let e1 = e0 - x.ex;
        // put the upper e bits of tiny into X->lo
        if e1 != 0 {
            x.lo |= tiny.wrapping_shr((64 - e1) as u32);
        }
        return;
    }

    // now 2 <= e <= 1024

    let i: usize = if e < 127 {
        0
    } else {
        ((e - 127 + 64 - 1) / 64) as usize
    }; // ceil((e-127)/64)
    // 0 <= i <= 15
    let mut c = [0u64; 5];
    u = mul64(x.hi, T[i + 3]); // i+3 <= 18
    c[0] = u as u64;
    c[1] = (u >> 64) as u64;
    u = mul64(x.hi, T[i + 2]);
    c[1] = c[1].wrapping_add(u as u64);
    c[2] = ((u >> 64) as u64).wrapping_add(u64::from(c[1] < u as u64));
    u = mul64(x.hi, T[i + 1]);
    c[2] = c[2].wrapping_add(u as u64);
    c[3] = ((u >> 64) as u64).wrapping_add(u64::from(c[2] < u as u64));
    u = mul64(x.hi, T[i]);
    c[3] = c[3].wrapping_add(u as u64);
    c[4] = ((u >> 64) as u64).wrapping_add(u64::from(c[3] < u as u64));

    let f = e - 64 * i as i32; // hi*T[i]/2^128 is multiplied by 2^f
    // now shift c[0..4] by f bits to the left
    let tiny: u64;
    if f < 64 {
        let f = f as u32;
        x.hi = (c[4] << f) | (c[3] >> (64 - f));
        x.lo = (c[3] << f) | (c[2] >> (64 - f));
        tiny = (c[2] << f) | (c[1] >> (64 - f));
    } else if f == 64 {
        x.hi = c[3];
        x.lo = c[2];
        tiny = c[1];
    } else {
        /* 65 <= f <= 127: this case can only occur when e >= 65 */
        let g = (f - 64) as u32; /* 1 <= g <= 63 */
        /* we compute an extra term */
        u = mul64(x.hi, T[i + 4]); // i+4 <= 19
        u >>= 64;
        let u = u as u64;
        c[0] = c[0].wrapping_add(u);
        let carry = c[0] < u;
        c[1] = c[1].wrapping_add(u64::from(carry));
        c[2] = c[2].wrapping_add(u64::from(carry && c[1] == 0));
        c[3] = c[3].wrapping_add(u64::from(carry && c[1] == 0 && c[2] == 0));
        c[4] = c[4].wrapping_add(u64::from(carry && c[1] == 0 && c[2] == 0 && c[3] == 0));
        x.hi = (c[3] << g) | (c[2] >> (64 - g));
        x.lo = (c[2] << g) | (c[1] >> (64 - g));
        tiny = (c[1] << g) | (c[0] >> (64 - g));
    }
    x.ex = 0;
    normalize(x);
    /* the worst case (for 2^25 <= x < 2^1024) is X->ex = -61, attained
    for |x| = 0x1.6ac5b262ca1ffp+851 */
    if x.ex < 0 {
        // put the upper -ex bits of tiny into low bits of lo
        x.lo |= tiny.wrapping_shr((64 + x.ex) as u32);
    }
}

/* Given Xin:=X with 0 <= Xin < 1, return i and modify X such that
Xin = i/2^11 + Xout, with 0 <= Xout < 2^-11.
This operation is exact. */
pub(crate) fn reduce2(x: &mut Dint) -> i32 {
    if x.ex <= -11 {
        return 0;
    }
    let sh = (64 - 11 - x.ex) as u32;
    let i = (x.hi >> sh) as i32;
    x.hi &= (1u64 << sh) - 1;
    normalize(x);
    i
}

/* h+l <- c1/2^64 + c0/2^128 */
pub(crate) fn set_dd(mut c1: u64, mut c0: u64) -> (f64, f64) {
    let h: f64;
    let l: f64;
    if c1 != 0 {
        let e = u64::from(c1.leading_zeros());
        if e != 0 {
            c1 = (c1 << e) | (c0 >> (64 - e));
            c0 <<= e;
        }
        let f: u64 = 0x3fe - e;
        h = f64::from_bits((f << 52) | ((c1 << 1) >> 12));
        c0 = (c1 << 53) | (c0 >> 11);
        if c0 != 0 {
            let g = u64::from(c0.leading_zeros());
            if g != 0 {
                c0 <<= g;
            }
            l = f64::from_bits((f.wrapping_sub(53).wrapping_sub(g) << 52) | ((c0 << 1) >> 12));
        } else {
            l = 0.0;
        }
    } else if c0 != 0 {
        let e = u64::from(c0.leading_zeros());
        let f: u64 = 0x3fe - 64 - e;
        c0 = c0.wrapping_shl((e + 1) as u32); // most significant bit shifted out
        // put the upper 52 bits of c0 into h
        h = f64::from_bits((f << 52) | (c0 >> 12));
        /* put the lower 12 bits of c0 into l */
        c0 <<= 52;
        if c0 != 0 {
            let g = u64::from(c0.leading_zeros());
            c0 = c0.wrapping_shl((g + 1) as u32);
            l = f64::from_bits((f.wrapping_sub(64).wrapping_sub(g) << 52) | (c0 >> 12));
        } else {
            l = 0.0;
        }
    } else {
        h = 0.0;
        l = 0.0;
    }
    (h, l)
}

// 0x1.921fb54442d17p+2
pub(crate) const TWO_PI_DOWN: f64 = f64::from_bits(0x401921fb54442d17); // 0x1.921fb54442d17p+2
/* | CH+CL - 1/(2pi) | < 2^-110.523 */
pub(crate) const CH: f64 = f64::from_bits(0x3fc45f306dc9c883); // 0x1.45f306dc9c883p-3
pub(crate) const CL: f64 = f64::from_bits(0xbc66b01ec5417056); // -0x1.6b01ec5417056p-57

/// Final step shared by both `reduce_fast` versions:
/// i = floor(h*2^11), h = fma(i, -2^-11, h).
#[inline(always)]
pub(crate) fn reduce_fast_split(h: f64) -> (i32, f64) {
    let i = (h * f64::from_bits(0x40a0000000000000)).floor(); // 0x1p11
    let h = i.mul_add(f64::from_bits(0xbf40000000000000), h); // -0x1p-11
    (i as i32, h)
}

/* Assuming 0x1.7137449123ef6p-26 < x < +Inf (sin) or
0x1.6a09e667f3bccp-27 < x < +Inf (cos),
return i and set h,l such that i/2^11+h+l approximates frac(x/(2pi)),
and err1, a bound for the absolute error | i/2^11 + h + l - frac(x/(2pi)) |.
This is the sin.c/cos.c version (tan.c has its own, more accurate one).
Returns (i, h, l, err1). */
#[inline(always)]
pub(crate) fn reduce_fast(x: f64) -> (i32, f64, f64, f64) {
    let mut h: f64;
    let l: f64;
    let err1: f64;
    if x <= TWO_PI_DOWN {
        // x < 2*pi
        let (hh, ll) = a_mul(CH, x); // exact
        h = hh;
        l = CL.mul_add(x, ll);
        err1 = f64::from_bits(0x396d900000000000) * h; // 0x1.d9p-105, error < 2^-104.116 * h
    } else {
        // x > 0x1.921fb54442d17p+2
        let tu = x.to_bits();
        let mut e = ((tu >> 52) & 0x7ff) as i32; /* 1025 <= e <= 2046 */
        let m: u64 = (1u64 << 52) | (tu & 0xfffffffffffff);
        let mut c = [0u64; 3];
        let mut u: u128;
        // x = m/2^53 * 2^(e-1022)
        if e <= 1074 {
            // 1025 <= e <= 1074: 2^2 <= x < 2^52
            u = mul64(m, T[1]);
            c[0] = u as u64;
            c[1] = (u >> 64) as u64;
            u = mul64(m, T[0]);
            c[1] = c[1].wrapping_add(u as u64);
            c[2] = ((u >> 64) as u64).wrapping_add(u64::from(c[1] < u as u64));
            e = 1075 - e; // 1 <= e <= 50
        } else {
            // 1075 <= e <= 2046, 2^52 <= x < 2^1024
            let i = ((e - 1138 + 63) / 64) as usize; // i = ceil((e-1138)/64), 0 <= i <= 15
            u = mul64(m, T[i + 2]);
            c[0] = u as u64;
            c[1] = (u >> 64) as u64;
            u = mul64(m, T[i + 1]);
            c[1] = c[1].wrapping_add(u as u64);
            c[2] = ((u >> 64) as u64).wrapping_add(u64::from(c[1] < u as u64));
            u = mul64(m, T[i]);
            c[2] = c[2].wrapping_add(u as u64);
            e = 1139 + ((i as i32) << 6) - e; // 1 <= e <= 64
        }
        if e == 64 {
            c[0] = c[1];
            c[1] = c[2];
        } else {
            let e = e as u32;
            c[0] = (c[1] << (64 - e)) | c[0] >> e;
            c[1] = (c[2] << (64 - e)) | c[1] >> e;
        }
        let (hh, ll) = set_dd(c[1], c[0]);
        h = hh;
        l = ll;
        err1 = f64::from_bits(0x3b30100000000000); // 0x1.01p-76
    }

    let (i, hh) = reduce_fast_split(h);
    h = hh;
    (i, h, l, err1)
}

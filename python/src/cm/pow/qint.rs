// Rust translation of CORE-MATH's qint.h (https://core-math.gitlabpages.inria.fr/).
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

//! 256-bit floating-point ("qint") arithmetic used by the third phase of pow.
//!
//! A value is `(-1)^sgn * (hh + hl/2^64 + lh/2^128 + ll/2^192) * 2^(ex-63)`.
//! Every function mirrors the C routine of the same name bit for bit; the C
//! versions write through an output pointer, these return the result (each C
//! routine reads every input field it needs before writing the output, and
//! writes every field of the output, so aliasing does not change results).

use crate::cm::pow::qint_tables::ZERO_Q;

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub(crate) struct Qint {
    pub(crate) hh: u64,
    pub(crate) hl: u64,
    pub(crate) lh: u64,
    pub(crate) ll: u64,
    pub(crate) ex: i64,
    pub(crate) sgn: u64,
}

impl Qint {
    /// Upper 128 bits (the `rh` member of the C union).
    #[inline]
    pub(crate) fn rh(&self) -> u128 {
        (u128::from(self.hh) << 64) | u128::from(self.hl)
    }

    /// Lower 128 bits (the `rl` member of the C union).
    #[inline]
    pub(crate) fn rl(&self) -> u128 {
        (u128::from(self.lh) << 64) | u128::from(self.ll)
    }

    #[inline]
    fn from_parts(rh: u128, rl: u128, ex: i64, sgn: u64) -> Qint {
        Qint {
            hh: (rh >> 64) as u64,
            hl: rh as u64,
            lh: (rl >> 64) as u64,
            ll: rl as u64,
            ex,
            sgn,
        }
    }
}

#[inline]
fn cmp(a: i64, b: i64) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

#[inline]
fn cmpu128(a: u128, b: u128) -> i32 {
    i32::from(a > b) - i32::from(a < b)
}

// Add two 128-bit integers and return (sum, carry)
#[inline]
fn addu128(a: u128, b: u128) -> (u128, u128) {
    let r = a.wrapping_add(b);
    (r, u128::from(r < a))
}

// Subtract two 128-bit integers and return (difference, borrow)
#[inline]
fn subu128(a: u128, b: u128) -> (u128, bool) {
    let r = a.wrapping_sub(b);
    (r, r > a)
}

/* Compare the absolute values of a and b:
return +1 if |a| > |b|, 0 if |a| = |b|, -1 if |a| < |b| */
#[inline]
fn cmp_qint(a: &Qint, b: &Qint) -> i32 {
    let c = cmp(a.ex, b.ex);
    if c != 0 {
        return c;
    }
    let c = cmpu128(a.rh(), b.rh());
    if c != 0 {
        return c;
    }
    cmpu128(a.rl(), b.rl())
}

/* same as cmp_qint, but only compare the upper 2 limbs */
#[inline]
fn cmp_qint_22(a: &Qint, b: &Qint) -> i32 {
    let c = cmp(a.ex, b.ex);
    if c != 0 {
        return c;
    }
    cmpu128(a.rh(), b.rh())
}

// Number of leading zeros of the 256-bit value (ch:cl), computed exactly as
// the C nested __builtin_clzll expression.
#[inline]
fn clz256(ch: u128, cl: u128) -> u64 {
    let chh = (ch >> 64) as u64;
    let clh = (cl >> 64) as u64;
    if chh != 0 {
        u64::from(chh.leading_zeros())
    } else {
        64 + if ch != 0 {
            u64::from((ch as u64).leading_zeros())
        } else {
            64 + if clh != 0 {
                u64::from(clh.leading_zeros())
            } else {
                64 + u64::from((cl as u64).leading_zeros())
            }
        }
    }
}

/* Add two Qint values, with error bounded by 2 ulps (ulp_256).
If Sterbenz theorem applies, i.e., a and b are of opposite signs
with |a|/2 <= |b| <= |a|, then the operation is exact. */
pub(crate) fn add_qint(a: &Qint, b: &Qint) -> Qint {
    if a.rh() == 0 && a.rl() == 0 {
        return *b; // exact
    }

    if b.rh() == 0 && b.rl() == 0 {
        return *a; // exact
    }

    /* compare the absolute values of a and b */
    match cmp_qint(a, b) {
        0 => {
            /* |a| = |b| */
            if (a.sgn ^ b.sgn) != 0 {
                /* signs differ */
                return ZERO_Q; // exact
            }
            let mut r = *a;
            r.ex += 1; // exact
            return r;
        }
        -1 => {
            /* |a| < |b| */
            return add_qint(b, a);
        }
        _ => {}
    }

    // From now on, |A| > |B|

    let mut ah = a.rh();
    let mut al = a.rl();
    let mut bh = b.rh();
    let mut bl = b.rl();

    let m_ex = a.ex;
    let k: i64 = a.ex.wrapping_sub(b.ex);

    if k > 0 {
        if k >= 128 {
            bl = if k < 256 { bh >> (k - 128) } else { 0 };
            bh = 0;
        } else {
            /* 1 <= k <= 127 */
            bl = (bl >> k) | (bh << (128 - k));
            bh >>= k;
        }
    }

    /* now we have to add (ah,al) + (bh,bl), with error <= 1 ulp
    corresponding to the ignored part of (bh,bl) */

    let sgn = a.sgn as u8;
    let mut ex: u64;
    let mut ch: u128;
    let mut cl: u128;

    let mut r_ex = m_ex;

    if (a.sgn ^ b.sgn) != 0 {
        // subtraction case
        /* a and b have different signs: C = A + (-B) */
        ch = ah.wrapping_sub(bh);

        let (d, borrow) = subu128(al, bl);
        cl = d;
        if borrow {
            ch = ch.wrapping_sub(1);
        }
        /* we cannot have C=0 since |A| > |B| */
        ex = clz256(ch, cl);
        /* ex < 256 since |A| > |B| */

        /* If ex=0 or ex=1, the rounding error is bounded by 2 ulps. */
        if ex > 0 {
            /* shift A by ex bits to the left, and B by ex-k bits to the left */
            if ex >= 128 {
                ah = al << (ex - 128);
                al = 0;
            } else {
                /* 1 <= ex < 128 */
                ah = (ah << ex) | (al >> (128 - ex));
                al <<= ex;
            }
            // C: int sh = ex - k; (uint64_t arithmetic, converted to int)
            let sh: i32 = ex.wrapping_sub(k as u64) as i32;
            bh = b.rh();
            bl = b.rl();
            if sh >= 0 {
                if sh >= 128 {
                    bh = bl << (sh - 128);
                    bl = 0;
                } else if sh > 0 {
                    /* 1 <= sh < 128 */
                    bh = (bh << sh) | (bl >> (128 - sh));
                    bl <<= sh;
                }
            } else {
                /* sh < 0: shift b by -sh bits to the right */
                let j = -sh;
                if j >= 128 {
                    bl = bh >> (j - 128);
                    bh = 0;
                } else {
                    /* 0 < j < 128 (j cannot be 0 since sh < 0) */
                    bl = (bh << (128 - j)) | (bl >> j);
                    bh >>= j;
                }
            }
            r_ex = r_ex.wrapping_sub(ex as i64);
            ch = ah.wrapping_sub(bh);

            let (d, borrow) = subu128(al, bl);
            cl = d;
            if borrow {
                ch = ch.wrapping_sub(1);
            }
            /* we cannot have C=0 since |A| > |B| */
            ex = clz256(ch, cl);
        }
        if ex != 0 {
            ch = (ch << ex) | (cl >> (128 - ex));
            cl <<= ex;
        }
        r_ex = r_ex.wrapping_sub(ex as i64);
    } else {
        // addition case
        let (s, c1) = addu128(ah, bh);
        ch = s;
        let mut cy = c1 as u8; // C: char cy

        let (s, c2) = addu128(al, bl);
        cl = s;
        if c2 != 0 {
            ch = ch.wrapping_add(1);
            cy += u8::from(ch == 0);
        }

        /* 0 <= cy <= 1 */

        if cy != 0 {
            // carry in the 256-bit addition
            cl = (ch << 127) | (cl >> 1);
            ch = (1u128 << 127) | (ch >> 1);
            r_ex += 1;
        }
        /* In the addition case, the rounding error is bounded by 1 ulp. */
    }

    Qint::from_parts(ch, cl, r_ex, u64::from(sgn))
}

/* same as add_qint, but only considers the upper 2 limbs of a and b,
with rounding error < 2 ulps(128) */
pub(crate) fn add_qint_22(a: &Qint, b: &Qint) -> Qint {
    if a.rh() == 0 {
        return *b; // exact
    }

    if b.rh() == 0 {
        return *a; // exact
    }

    /* compare the absolute values of a and b */
    match cmp_qint_22(a, b) {
        0 => {
            /* |a| = |b| */
            if (a.sgn ^ b.sgn) != 0 {
                /* signs differ */
                return ZERO_Q; // exact
            }
            let mut r = *a;
            r.ex += 1; // exact
            return r;
        }
        -1 => {
            /* |a| < |b| */
            return add_qint_22(b, a);
        }
        _ => {}
    }

    // From now on, |A| > |B|

    let mut ah = a.rh();
    let mut bh = b.rh();

    let m_ex = a.ex;
    let k = a.ex.wrapping_sub(b.ex) as u64;

    if k > 0 {
        bh = if k >= 128 { 0 } else { bh >> k };
    }

    /* now we have to add ah + bh, with error <= 1 ulp
    corresponding to the ignored part of bh */

    let sgn = a.sgn as u8;
    let mut ex: u64;
    let mut ch: u128;

    let mut r_ex = m_ex;

    if (a.sgn ^ b.sgn) != 0 {
        // subtraction case
        /* a and b have different signs: C = A + (-B) */
        ch = ah.wrapping_sub(bh);

        /* we cannot have ch=0 since |A| > |B| */
        let chh = (ch >> 64) as u64;
        ex = if chh != 0 {
            u64::from(chh.leading_zeros())
        } else {
            64 + u64::from((ch as u64).leading_zeros())
        };

        /* ex < 128 since |A| > |B| */

        if ex > 0 {
            /* shift A and B by ex bits to the left */
            ah <<= ex;
            /* for B, we have to shift by k bits to the right and ex to the left */
            if ex >= k {
                bh = b.rh() << (ex - k); // since ex < 128, the shift is well defined
            } else {
                bh = b.rh() >> (k - ex);
            }
            r_ex = r_ex.wrapping_sub(ex as i64);
            ch = ah.wrapping_sub(bh);

            /* we cannot have C=0 since |A| > |B| */
            let chh = (ch >> 64) as u64;
            ex = if chh != 0 {
                u64::from(chh.leading_zeros())
            } else {
                64 + u64::from((ch as u64).leading_zeros())
            };
            /* rounding error is bounded by 1 ulp(128) */
        }
        ch <<= ex;
        /* if ex=1, the rounding error is multiplied by 2, thus < 2 ulp(128) */
        r_ex = r_ex.wrapping_sub(ex as i64);
    } else {
        // addition case
        let (s, cy) = addu128(ah, bh);
        ch = s;

        if cy != 0 {
            // carry in the 128-bit addition
            ch = (1u128 << 127) | (ch >> 1);
            r_ex += 1;
        }
        /* In the addition case, the rounding error is bounded by 1 ulp(128) */
    }

    Qint::from_parts(ch, 0, r_ex, u64::from(sgn))
}

const LO64: u128 = 0xffff_ffff_ffff_ffff;

// Common normalisation tail shared by the mul_qint* routines.
#[inline]
fn mul_finish(t6: u128, t5: u128, a: &Qint, b: &Qint) -> Qint {
    let ex = u64::from((t6 >> 127) == 0);
    let (rh, rl) = if ex != 0 {
        /* ex=1 */
        ((t6 << 1) | (t5 >> 127), t5 << 1)
    } else {
        /* ex=0 */
        (t6, t5)
    };
    Qint::from_parts(rh, rl, a.ex + b.ex + 1 - ex as i64, a.sgn ^ b.sgn)
}

#[inline]
fn m(x: u64, y: u64) -> u128 {
    u128::from(x) * u128::from(y)
}

// Multiply two Qint numbers, with error < 14 ulps
pub(crate) fn mul_qint(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);

    let r32 = m(a.hh, b.hl);
    let r23 = m(a.hl, b.hh);

    let r31 = m(a.hh, b.lh);
    let r13 = m(a.lh, b.hh);
    let r22 = m(a.hl, b.hl);

    let r30 = m(a.hh, b.ll);
    let r03 = m(a.ll, b.hh);
    let r21 = m(a.hl, b.lh);
    let r12 = m(a.lh, b.hl);

    let t3 = (r12 >> 64) + (r21 >> 64) + (r03 >> 64) + (r30 >> 64);
    /* no overflow since each term is < 2^64, thus the sum < 2^66 */

    let (mut t4, mut c4) = addu128(r22, t3);
    let (s, c) = addu128(r13, t4);
    t4 = s;
    c4 += c;
    let (s, c) = addu128(r31, t4);
    t4 = s;
    c4 += c;

    let (mut t5, mut c5) = addu128(r23, t4 >> 64);
    let (s, c) = addu128(r32, t5);
    t5 = s;
    c5 += c;

    let t6 = r33 + ((c5 << 64) | (t5 >> 64)) + c4;

    let t5 = (t5 << 64) | (t4 & LO64);
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the upper 3 limbs from a and b,
and with error < 6 ulps */
pub(crate) fn mul_qint_33(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);

    let r32 = m(a.hh, b.hl);
    let r23 = m(a.hl, b.hh);

    let r31 = m(a.hh, b.lh);
    let r13 = m(a.lh, b.hh);
    let r22 = m(a.hl, b.hl);

    let r21 = m(a.hl, b.lh);
    let r12 = m(a.lh, b.hl);

    let t3 = (r12 >> 64) + (r21 >> 64);

    let (mut t4, mut c4) = addu128(r22, t3);
    let (s, c) = addu128(r13, t4);
    t4 = s;
    c4 += c;
    let (s, c) = addu128(r31, t4);
    t4 = s;
    c4 += c;

    let (mut t5, mut c5) = addu128(r23, t4 >> 64);
    let (s, c) = addu128(r32, t5);
    t5 = s;
    c5 += c;

    let t6 = r33 + ((c5 << 64) | (t5 >> 64)) + c4;

    let t5 = (t5 << 64) | (t4 & LO64);
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the upper limb from b,
and with error < 2 ulps */
pub(crate) fn mul_qint_41(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);
    let r23 = m(a.hl, b.hh);
    let r13 = m(a.lh, b.hh);
    let r03 = m(a.ll, b.hh);

    let t3 = r03 >> 64;

    let (t4, c4) = addu128(r13, t3);

    let (t5, c5) = addu128(r23, t4 >> 64);

    let t6 = r33 + ((c5 << 64) | (t5 >> 64)) + c4;

    let t5 = (t5 << 64) | (t4 & LO64);
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the 3 upper limbs from a,
and the upper limb from b, with no error (exact product) */
pub(crate) fn mul_qint_31(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);
    let r23 = m(a.hl, b.hh);
    let r13 = m(a.lh, b.hh);

    let t4 = r13;

    let (t5, c5) = addu128(r23, t4 >> 64);

    let t6 = r33 + ((c5 << 64) | (t5 >> 64));

    let t5 = (t5 << 64) | (t4 & LO64);
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the 2 upper limbs from a and b,
with no error (exact product) */
pub(crate) fn mul_qint_22(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);

    let r32 = m(a.hh, b.hl);
    let r23 = m(a.hl, b.hh);

    let r22 = m(a.hl, b.hl);

    let t4 = r22;

    let (mut t5, mut c5) = addu128(r23, t4 >> 64);
    let (s, c) = addu128(r32, t5);
    t5 = s;
    c5 += c;

    let t6 = r33 + ((c5 << 64) | (t5 >> 64));

    let t5 = (t5 << 64) | (t4 & LO64);
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the 2 upper limbs from a, and the
upper limb of b, with no error (exact product) */
pub(crate) fn mul_qint_21(a: &Qint, b: &Qint) -> Qint {
    let r33 = m(a.hh, b.hh);
    let r23 = m(a.hl, b.hh);

    let t6 = r33 + (r23 >> 64);

    let t5 = r23 << 64;
    mul_finish(t6, t5, a, b)
}

/* same as mul_qint, but considering only the upper limb from a and b,
with no error (exact product) */
pub(crate) fn mul_qint_11(a: &Qint, b: &Qint) -> Qint {
    let t6 = m(a.hh, b.hh);
    let ex = u64::from((t6 >> 127) == 0);

    /* ex can be 0 or 1 */

    Qint::from_parts(t6 << ex, 0, a.ex + b.ex + 1 - ex as i64, a.sgn ^ b.sgn)
}

// Multiply an integer with a Qint variable, with error < 2 ulps
pub(crate) fn mul_qint_2(b: i64, a: &Qint) -> Qint {
    if b == 0 {
        return ZERO_Q; // exact
    }

    let mut c: u64 = if b < 0 {
        (b as u64).wrapping_neg()
    } else {
        b as u64
    };
    let sgn = u64::from(b < 0) ^ a.sgn;
    if c == 1 {
        let mut r = *a; // exact
        r.sgn = sgn;
        return r;
    }

    let mut ex = a.ex + 64;

    /* scale c so that 2^63 <= c < 2^64 */
    let k = c.leading_zeros();
    c <<= k;
    ex -= i64::from(k);

    let mut t3 = m(a.hh, c);
    let mut t2 = m(a.hl, c);
    let mut t1 = m(a.lh, c);
    let t0 = m(a.ll, c);

    let t = t0 >> 64;

    /* t:64 is the term of degree 0 (divided by 2^64) */

    let (s, cy) = addu128(t, t1);
    t1 = s;
    /* (cy:1,t1:128) is the sum of the terms of degree 0 and 1 */

    let t = (cy << 64) | (t1 >> 64);
    let (s, cy) = addu128(t, t2);
    t2 = s;
    /* (cy:1,t2:128,low(t1):64) is the sum of the terms of degree 0 to 2 */

    t3 += (cy << 64) | (t2 >> 64);
    /* (t3,low(t2):64,low(t1):64) is the sum of the terms of degree 0 to 3 */

    let exs = ((t3 >> 64) as u64).leading_zeros();

    t2 = (t2 << 64) | (t1 & LO64);

    /* ex is 0 or 1 because a and c are normalized (2^63 <= a->hh, c < 2^64) */

    if exs != 0 {
        Qint::from_parts((t3 << 1) | (t2 >> 127), t2 << 1, ex - 1, sgn)
    } else {
        Qint::from_parts(t3, t2, ex, sgn)
    }
}

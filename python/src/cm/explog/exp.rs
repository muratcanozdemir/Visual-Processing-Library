// Rust translation of CORE-MATH's exp.c (https://core-math.gitlabpages.inria.fr/).
//
// Original: Copyright (c) 2022-2025 Alexei Sibidanov.
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

//! Correctly rounded exponential function for binary64 values
//! (round-to-nearest only).
//!
//! Faithful translation of `cr_exp` from CORE-MATH `exp.c`. Differences from
//! the C: no errno, no floating-point exception flags, no directed rounding.
//! `__builtin_fma` becomes `f64::mul_add`; the two `a * b + c` expressions in
//! `FastFMA_DW` that the C lets the compiler contract are kept as a separate
//! multiply and add (the C semantics under `-ffp-contract=off`).

#![allow(clippy::excessive_precision, clippy::unreadable_literal)]

use crate::cm::explog::FORCE_SLOW_PATH;

#[inline(always)]
fn fasttwosum(x: f64, y: f64) -> (f64, f64) {
    let s = x + y;
    let z = s - x;
    (s, y - z)
}

#[inline(always)]
fn fastsum(xh: f64, xl: f64, yh: f64, yl: f64) -> (f64, f64) {
    let (sh, sl) = fasttwosum(xh, yh);
    (sh, (xl + yl) + sl)
}

#[inline(always)]
fn muldd(xh: f64, xl: f64, ch: f64, cl: f64) -> (f64, f64) {
    let ahhh = ch * xh;
    let l = (ch * xl + cl * xh) + ch.mul_add(xh, -ahhh);
    (ahhh, l)
}

/// Algorithm 4 of Jeannerod, Joldes, Louvet, Muller (Arith 2026), with the
/// last two FMAs replaced by multiply-and-add, as in the C.
#[inline(always)]
#[allow(clippy::too_many_arguments)]
fn fast_fma_dw(ah: f64, al: f64, bh: f64, bl: f64, ch: f64, cl: f64) -> (f64, f64) {
    let dh = ah.mul_add(bh, ch);
    let t = ch - dh;
    let e = ah.mul_add(bh, t);
    let f = e + cl;
    let g = ah * bl + f;
    let l = al * bh + g;
    (dh, l)
}

#[inline(always)]
fn opolydd(xh: f64, xl: f64, c: &[[f64; 2]]) -> (f64, f64) {
    let mut i = c.len() - 1;
    let (mut ch, mut cl) = (c[i][0], c[i][1]);
    while i > 0 {
        i -= 1;
        (ch, cl) = fast_fma_dw(xh, xl, ch, cl, c[i][0], c[i][1]);
    }
    (ch, cl)
}

#[inline(always)]
fn as_ldexp(x: f64, i: i64) -> f64 {
    f64::from_bits(x.to_bits().wrapping_add((i as u64) << 52))
}

/// Sets the exponent of a binary64 number to 0 (subnormal range). The C also
/// raises the underflow flag here; flags are not modelled.
#[inline(always)]
fn as_todenormal(x: f64) -> f64 {
    f64::from_bits(x.to_bits() & (!0u64 >> 12))
}

#[cold]
#[inline(never)]
fn as_exp_database(x: f64, f: f64) -> f64 {
    let ix = x.to_bits();
    let mut a: i32 = 0;
    let mut b: i32 = DB.len() as i32 - 1;
    let mut m = (a + b) / 2;
    while a <= b {
        let cm = DB[m as usize].to_bits();
        if cm < ix {
            a = m + 1;
        } else if cm == ix {
            const S2: [u64; 2] = [0x57f5fe2e5bde4075, 0x3c1f16b8ed];
            const S: u64 = 333811522313371;
            let jf = f.to_bits();
            let dr = f64::from_bits(((S >> m) << 63) | 0x3c90000000000000);
            let t = (S2[(m >> 5) as usize] >> ((m << 1) & 63)) & 3;
            for k in -1i64..=1 {
                let r = jf.wrapping_add(k as u64);
                if (r & 3) == t {
                    return f64::from_bits(r) + dr;
                }
            }
            break;
        } else {
            b = m - 1;
        }
        m = (a + b) >> 1;
    }
    f
}

#[cold]
#[inline(never)]
fn as_exp_accurate(x: f64) -> f64 {
    let mut ix = x.to_bits();
    if ((ix >> 52) & 0x7ff) < 0x3c9 {
        return 1.0 + x;
    }
    let t = (x * S).round_ties_even();
    let jt = t as i64;
    let i0 = ((jt >> 6) & 0x3f) as usize;
    let i1 = (jt & 0x3f) as usize;
    let ie = jt >> 12;
    let (t0h, t0l) = (T0[i0][1], T0[i0][0]);
    let (t1h, t1l) = (T1[i1][1], T1[i1][0]);
    let (th, tl) = muldd(t0h, t0l, t1h, t1l);

    // Cody-Waite argument reduction: |t| < 2^23 and L2H has 29 bits,
    // thus L2H*t is exact.
    let dx = x - L2H * t;
    let mut dxl = L2L * t;
    let dxll = L2LL * t + L2L.mul_add(t, -dxl);
    let dxh = dx + dxl;
    dxl = (dx - dxh) + dxl + dxll;
    let (mut fh, mut fl) = opolydd(dxh, dxl, &CH_ACC);
    (fh, fl) = muldd(dxh, dxl, fh, fl);
    if ix > 0xc086232bdd7abcd2 {
        // x < -0x1.6232bdd7abcd2p+9
        ix = ((1 - ie) as u64) << 52;
        (fh, fl) = muldd(fh, fl, th, tl);
        (fh, fl) = fastsum(th, tl, fh, fl);
        let e;
        (fh, e) = fasttwosum(f64::from_bits(ix), fh);
        fl += e;
        fh = as_todenormal(fh + fl);
    } else {
        if th == 1.0 {
            let mut e;
            (fh, e) = fasttwosum(th, fh);
            (fl, e) = fasttwosum(e, fl);
            ix = fl.to_bits();
            if (ix & (!0u64 >> 12)) == 0 {
                let v = e.to_bits();
                let d = ((((ix as i64) >> 63) ^ ((v as i64) >> 63)) as u64)
                    .wrapping_shl(1)
                    .wrapping_add(1);
                ix = ix.wrapping_add(d);
                fl = f64::from_bits(ix);
            }
        } else {
            (fh, fl) = muldd(fh, fl, th, tl);
            (fh, fl) = fastsum(th, tl, fh, fl);
        }
        (fh, fl) = fasttwosum(fh, fl);
        ix = fl.to_bits();
        let d = ix.wrapping_add(2) & (!0u64 >> 12);
        if d <= 2 {
            fh = as_exp_database(x, fh);
        }
        fh = as_ldexp(fh, ie);
    }
    fh
}

/// Correctly rounded `e^x` (round-to-nearest).
pub fn exp(x: f64) -> f64 {
    let mut ix = x.to_bits();
    let aix = ix & (!0u64 >> 1);
    // exp(x) rounds to 1 to nearest for |x| <= 0x1p-54
    if aix <= 0x3c90000000000000 {
        return 1.0 + x;
    }
    if aix >= 0x40862e42fefa39f0 {
        // |x| >= 0x1.62e42fefa39fp+9
        if aix > 0x7ff0000000000000 {
            return x + x; // nan
        }
        if aix == 0x7ff0000000000000 {
            // |x| = inf
            return if (ix >> 63) != 0 { 0.0 } else { x };
        }
        if (ix >> 63) == 0 {
            // x >= 0x1.62e42fefa39fp+9: the C returns 0x1p1023 * 0x1p1023
            return f64::INFINITY;
        }
        if aix >= 0x40874910d52d3052 {
            // x <= -0x1.74910d52d3052p+9: the C returns 0x1p-1022 * 0x1p-1022
            return 0.0;
        }
    }
    let t = (x * S).round_ties_even();
    let jt = t as i64;
    let i0 = ((jt >> 6) & 0x3f) as usize;
    let i1 = (jt & 0x3f) as usize;
    let ie = jt >> 12;
    let (t0h, t0l) = (T0[i0][1], T0[i0][0]);
    let (t1h, t1l) = (T1[i1][1], T1[i1][0]);
    let (th, tl) = muldd(t0h, t0l, t1h, t1l);
    // Cody-Waite argument reduction: |t| < 2^23 and L2H has 29 bits,
    // thus L2H*t is exact.
    let dx = (x - L2H * t) + L2L * t;
    let dx2 = dx * dx;
    // 1 + x*(c0 + c1*x + ... + c3*x^3) approximates exp(x) on
    // [-log(2)/2^13,log(2)/2^13] with absolute error < 2^-76.173
    let p = (CH_FAST[0] + dx * CH_FAST[1]) + dx2 * (CH_FAST[2] + dx * CH_FAST[3]);
    let mut fh = th;
    let tx = th * dx;
    let mut fl = tl + tx * p;
    if ix > 0xc086232bdd7abcd2 {
        // subnormal case: x < -0x1.6232bdd7abcd2p+9
        ix = ((1 - ie) as u64) << 52;
        let e;
        (fh, e) = fasttwosum(f64::from_bits(ix), fh);
        fl += e;
        let ub = fh + (fl + EPS);
        let lb = fh + (fl - EPS);
        if FORCE_SLOW_PATH || ub != lb {
            return as_exp_accurate(x);
        }
        fh = as_todenormal(lb);
    } else {
        let ub = fh + (fl + EPS);
        let lb = fh + (fl - EPS);
        if FORCE_SLOW_PATH || ub != lb {
            return as_exp_accurate(x);
        }
        fh = as_ldexp(lb, ie);
    }
    fh
}

// BEGIN GENERATED TABLES (tools/gen_tables.py)
#[rustfmt::skip]
const DB: [f64; 51] = [
    f64::from_bits(0x3cafffffffffffff), // 0x1.fffffffffffffp-53
    f64::from_bits(0x3f1ba07d73250de7), // 0x1.ba07d73250de7p-14
    f64::from_bits(0x3f76a4d1af9cc989), // 0x1.6a4d1af9cc989p-8
    f64::from_bits(0x3f95a75293a5dcda), // 0x1.5a75293a5dcdap-6
    f64::from_bits(0x3fa42ea46949b3c7), // 0x1.42ea46949b3c7p-5
    f64::from_bits(0x3fa7c8bb0cf5d160), // 0x1.7c8bb0cf5d16p-5
    f64::from_bits(0x3fc0948d39a41695), // 0x1.0948d39a41695p-3
    f64::from_bits(0x3fca065fefae814f), // 0x1.a065fefae814fp-3
    f64::from_bits(0x3fcf6e4c3ced7c72), // 0x1.f6e4c3ced7c72p-3
    f64::from_bits(0x3fd1a0408712e00a), // 0x1.1a0408712e00ap-2
    f64::from_bits(0x3fdbcab27d05abde), // 0x1.bcab27d05abdep-2
    f64::from_bits(0x3fe005ae04256bab), // 0x1.005ae04256babp-1
    f64::from_bits(0x401273c188aa7b14), // 0x1.273c188aa7b14p+2
    f64::from_bits(0x40183d4bcdebb3f4), // 0x1.83d4bcdebb3f4p+2
    f64::from_bits(0x40308f51434652c3), // 0x1.08f51434652c3p+4
    f64::from_bits(0x4031d5c2daebe367), // 0x1.1d5c2daebe367p+4
    f64::from_bits(0x403c44ce0d716a1a), // 0x1.c44ce0d716a1ap+4
    f64::from_bits(0x404e07e71bfcf06f), // 0x1.e07e71bfcf06fp+5
    f64::from_bits(0x404f7216c4b435c9), // 0x1.f7216c4b435c9p+5
    f64::from_bits(0x40654cd1fea7663a), // 0x1.54cd1fea7663ap+7
    f64::from_bits(0x407d6479eba7c971), // 0x1.d6479eba7c971p+8
    f64::from_bits(0xbf1664716b68a409), // -0x1.664716b68a409p-14
    f64::from_bits(0xbf2a2fefefd580df), // -0x1.a2fefefd580dfp-13
    f64::from_bits(0xbf3ce3f638d0c742), // -0x1.ce3f638d0c742p-12
    f64::from_bits(0xbf3ceff32831e2c2), // -0x1.ceff32831e2c2p-12
    f64::from_bits(0xbf433accae78b371), // -0x1.33accae78b371p-11
    f64::from_bits(0xbf4d792b60084f92), // -0x1.d792b60084f92p-11
    f64::from_bits(0xbf77fb235d76cce7), // -0x1.7fb235d76cce7p-8
    f64::from_bits(0xbf81ff9b8e8b38be), // -0x1.1ff9b8e8b38bep-7
    f64::from_bits(0xbf854511e930898c), // -0x1.54511e930898cp-7
    f64::from_bits(0xbf95c5ed0ec83666), // -0x1.5c5ed0ec83666p-6
    f64::from_bits(0xbf98c56ff5326197), // -0x1.8c56ff5326197p-6
    f64::from_bits(0xbf9a4187f2ca71f9), // -0x1.a4187f2ca71f9p-6
    f64::from_bits(0xbfba8f783d749a8f), // -0x1.a8f783d749a8fp-4
    f64::from_bits(0xbfbbd44fdaed819f), // -0x1.bd44fdaed819fp-4
    f64::from_bits(0xbfbdaf693d64fada), // -0x1.daf693d64fadap-4
    f64::from_bits(0xbfc290ea09e36479), // -0x1.290ea09e36479p-3
    f64::from_bits(0xbfc8aeb636f3ce35), // -0x1.8aeb636f3ce35p-3
    f64::from_bits(0xbfcd3f3799439415), // -0x1.d3f3799439415p-3
    f64::from_bits(0xbfcea16274b0109b), // -0x1.ea16274b0109bp-3
    f64::from_bits(0xbfe22e24fa3d5cf9), // -0x1.22e24fa3d5cf9p-1
    f64::from_bits(0xbfe85068c07fbbf6), // -0x1.85068c07fbbf6p-1
    f64::from_bits(0xbfebdc7955d1482c), // -0x1.bdc7955d1482cp-1
    f64::from_bits(0xbff2a9cad9998262), // -0x1.2a9cad9998262p+0
    f64::from_bits(0xbffcc37ef7de7501), // -0x1.cc37ef7de7501p+0
    f64::from_bits(0xc0002393d5976769), // -0x1.02393d5976769p+1
    f64::from_bits(0xc0065061daf79a78), // -0x1.65061daf79a78p+1
    f64::from_bits(0xc02e8bdbfcd9144e), // -0x1.e8bdbfcd9144ep+3
    f64::from_bits(0xc038f80e06f3a04c), // -0x1.8f80e06f3a04cp+4
    f64::from_bits(0xc0559f038076039c), // -0x1.59f038076039cp+6
    f64::from_bits(0xc06981587ad4542f), // -0x1.981587ad4542fp+7
];
#[rustfmt::skip]
const T0: [[f64; 2]; 64] = [
    [f64::from_bits(0x0000000000000000), f64::from_bits(0x3ff0000000000000)], // {0x0p+0, 0x1p+0}
    [f64::from_bits(0xbc719083535b085e), f64::from_bits(0x3ff02c9a3e778061)], // {-0x1.19083535b085ep-56, 0x1.02c9a3e778061p+0}
    [f64::from_bits(0x3c8d73e2a475b466), f64::from_bits(0x3ff059b0d3158574)], // {0x1.d73e2a475b466p-55, 0x1.059b0d3158574p+0}
    [f64::from_bits(0x3c6186be4bb28500), f64::from_bits(0x3ff0874518759bc8)], // {0x1.186be4bb285p-57, 0x1.0874518759bc8p+0}
    [f64::from_bits(0x3c98a62e4adc610a), f64::from_bits(0x3ff0b5586cf9890f)], // {0x1.8a62e4adc610ap-54, 0x1.0b5586cf9890fp+0}
    [f64::from_bits(0x3c403a1727c57b52), f64::from_bits(0x3ff0e3ec32d3d1a2)], // {0x1.03a1727c57b52p-59, 0x1.0e3ec32d3d1a2p+0}
    [f64::from_bits(0xbc96c51039449b3a), f64::from_bits(0x3ff11301d0125b51)], // {-0x1.6c51039449b3ap-54, 0x1.11301d0125b51p+0}
    [f64::from_bits(0xbc932fbf9af1369e), f64::from_bits(0x3ff1429aaea92de0)], // {-0x1.32fbf9af1369ep-54, 0x1.1429aaea92dep+0}
    [f64::from_bits(0xbc819041b9d78a76), f64::from_bits(0x3ff172b83c7d517b)], // {-0x1.19041b9d78a76p-55, 0x1.172b83c7d517bp+0}
    [f64::from_bits(0x3c8e5b4c7b4968e4), f64::from_bits(0x3ff1a35beb6fcb75)], // {0x1.e5b4c7b4968e4p-55, 0x1.1a35beb6fcb75p+0}
    [f64::from_bits(0x3c9e016e00a2643c), f64::from_bits(0x3ff1d4873168b9aa)], // {0x1.e016e00a2643cp-54, 0x1.1d4873168b9aap+0}
    [f64::from_bits(0x3c8dc775814a8494), f64::from_bits(0x3ff2063b88628cd6)], // {0x1.dc775814a8494p-55, 0x1.2063b88628cd6p+0}
    [f64::from_bits(0x3c99b07eb6c70572), f64::from_bits(0x3ff2387a6e756238)], // {0x1.9b07eb6c70572p-54, 0x1.2387a6e756238p+0}
    [f64::from_bits(0x3c82bd339940e9da), f64::from_bits(0x3ff26b4565e27cdd)], // {0x1.2bd339940e9dap-55, 0x1.26b4565e27cddp+0}
    [f64::from_bits(0x3c8612e8afad1256), f64::from_bits(0x3ff29e9df51fdee1)], // {0x1.612e8afad1256p-55, 0x1.29e9df51fdee1p+0}
    [f64::from_bits(0x3c90024754db41d4), f64::from_bits(0x3ff2d285a6e4030b)], // {0x1.0024754db41d4p-54, 0x1.2d285a6e4030bp+0}
    [f64::from_bits(0x3c86f46ad23182e4), f64::from_bits(0x3ff306fe0a31b715)], // {0x1.6f46ad23182e4p-55, 0x1.306fe0a31b715p+0}
    [f64::from_bits(0x3c932721843659a6), f64::from_bits(0x3ff33c08b26416ff)], // {0x1.32721843659a6p-54, 0x1.33c08b26416ffp+0}
    [f64::from_bits(0xbc963aeabf42eae2), f64::from_bits(0x3ff371a7373aa9cb)], // {-0x1.63aeabf42eae2p-54, 0x1.371a7373aa9cbp+0}
    [f64::from_bits(0xbc75e436d661f5e2), f64::from_bits(0x3ff3a7db34e59ff7)], // {-0x1.5e436d661f5e2p-56, 0x1.3a7db34e59ff7p+0}
    [f64::from_bits(0x3c8ada0911f09ebc), f64::from_bits(0x3ff3dea64c123422)], // {0x1.ada0911f09ebcp-55, 0x1.3dea64c123422p+0}
    [f64::from_bits(0xbc5ef3691c309278), f64::from_bits(0x3ff4160a21f72e2a)], // {-0x1.ef3691c309278p-58, 0x1.4160a21f72e2ap+0}
    [f64::from_bits(0x3c489b7a04ef80d0), f64::from_bits(0x3ff44e086061892d)], // {0x1.89b7a04ef80dp-59, 0x1.44e086061892dp+0}
    [f64::from_bits(0x3c73c1a3b69062f0), f64::from_bits(0x3ff486a2b5c13cd0)], // {0x1.3c1a3b69062fp-56, 0x1.486a2b5c13cdp+0}
    [f64::from_bits(0x3c7d4397afec42e2), f64::from_bits(0x3ff4bfdad5362a27)], // {0x1.d4397afec42e2p-56, 0x1.4bfdad5362a27p+0}
    [f64::from_bits(0xbc94b309d25957e4), f64::from_bits(0x3ff4f9b2769d2ca7)], // {-0x1.4b309d25957e4p-54, 0x1.4f9b2769d2ca7p+0}
    [f64::from_bits(0xbc807abe1db13cac), f64::from_bits(0x3ff5342b569d4f82)], // {-0x1.07abe1db13cacp-55, 0x1.5342b569d4f82p+0}
    [f64::from_bits(0x3c99bb2c011d93ac), f64::from_bits(0x3ff56f4736b527da)], // {0x1.9bb2c011d93acp-54, 0x1.56f4736b527dap+0}
    [f64::from_bits(0x3c96324c054647ac), f64::from_bits(0x3ff5ab07dd485429)], // {0x1.6324c054647acp-54, 0x1.5ab07dd485429p+0}
    [f64::from_bits(0x3c9ba6f93080e65e), f64::from_bits(0x3ff5e76f15ad2148)], // {0x1.ba6f93080e65ep-54, 0x1.5e76f15ad2148p+0}
    [f64::from_bits(0xbc9383c17e40b496), f64::from_bits(0x3ff6247eb03a5585)], // {-0x1.383c17e40b496p-54, 0x1.6247eb03a5585p+0}
    [f64::from_bits(0xbc9bb60987591c34), f64::from_bits(0x3ff6623882552225)], // {-0x1.bb60987591c34p-54, 0x1.6623882552225p+0}
    [f64::from_bits(0xbc9bdd3413b26456), f64::from_bits(0x3ff6a09e667f3bcd)], // {-0x1.bdd3413b26456p-54, 0x1.6a09e667f3bcdp+0}
    [f64::from_bits(0xbc6bbe3a683c88aa), f64::from_bits(0x3ff6dfb23c651a2f)], // {-0x1.bbe3a683c88aap-57, 0x1.6dfb23c651a2fp+0}
    [f64::from_bits(0xbc816e4786887a9a), f64::from_bits(0x3ff71f75e8ec5f74)], // {-0x1.16e4786887a9ap-55, 0x1.71f75e8ec5f74p+0}
    [f64::from_bits(0xbc90245957316dd4), f64::from_bits(0x3ff75feb564267c9)], // {-0x1.0245957316dd4p-54, 0x1.75feb564267c9p+0}
    [f64::from_bits(0xbc841577ee049930), f64::from_bits(0x3ff7a11473eb0187)], // {-0x1.41577ee04993p-55, 0x1.7a11473eb0187p+0}
    [f64::from_bits(0x3c705d02ba15797e), f64::from_bits(0x3ff7e2f336cf4e62)], // {0x1.05d02ba15797ep-56, 0x1.7e2f336cf4e62p+0}
    [f64::from_bits(0xbc9d4c1dd41532d8), f64::from_bits(0x3ff82589994cce13)], // {-0x1.d4c1dd41532d8p-54, 0x1.82589994cce13p+0}
    [f64::from_bits(0xbc9fc6f89bd4f6ba), f64::from_bits(0x3ff868d99b4492ed)], // {-0x1.fc6f89bd4f6bap-54, 0x1.868d99b4492edp+0}
    [f64::from_bits(0x3c96e9f156864b26), f64::from_bits(0x3ff8ace5422aa0db)], // {0x1.6e9f156864b26p-54, 0x1.8ace5422aa0dbp+0}
    [f64::from_bits(0x3c85cc13a2e3976c), f64::from_bits(0x3ff8f1ae99157736)], // {0x1.5cc13a2e3976cp-55, 0x1.8f1ae99157736p+0}
    [f64::from_bits(0xbc675fc781b57ebc), f64::from_bits(0x3ff93737b0cdc5e5)], // {-0x1.75fc781b57ebcp-57, 0x1.93737b0cdc5e5p+0}
    [f64::from_bits(0xbc9d185b7c1b85d0), f64::from_bits(0x3ff97d829fde4e50)], // {-0x1.d185b7c1b85dp-54, 0x1.97d829fde4e5p+0}
    [f64::from_bits(0x3c7c7c46b071f2be), f64::from_bits(0x3ff9c49182a3f090)], // {0x1.c7c46b071f2bep-56, 0x1.9c49182a3f09p+0}
    [f64::from_bits(0xbc9359495d1cd532), f64::from_bits(0x3ffa0c667b5de565)], // {-0x1.359495d1cd532p-54, 0x1.a0c667b5de565p+0}
    [f64::from_bits(0xbc9d2f6edb8d41e2), f64::from_bits(0x3ffa5503b23e255d)], // {-0x1.d2f6edb8d41e2p-54, 0x1.a5503b23e255dp+0}
    [f64::from_bits(0x3c90fac90ef7fd32), f64::from_bits(0x3ffa9e6b5579fdbf)], // {0x1.0fac90ef7fd32p-54, 0x1.a9e6b5579fdbfp+0}
    [f64::from_bits(0x3c97a1cd345dcc82), f64::from_bits(0x3ffae89f995ad3ad)], // {0x1.7a1cd345dcc82p-54, 0x1.ae89f995ad3adp+0}
    [f64::from_bits(0xbc62805e3084d708), f64::from_bits(0x3ffb33a2b84f15fb)], // {-0x1.2805e3084d708p-57, 0x1.b33a2b84f15fbp+0}
    [f64::from_bits(0xbc75584f7e54ac3a), f64::from_bits(0x3ffb7f76f2fb5e47)], // {-0x1.5584f7e54ac3ap-56, 0x1.b7f76f2fb5e47p+0}
    [f64::from_bits(0x3c823dd07a2d9e84), f64::from_bits(0x3ffbcc1e904bc1d2)], // {0x1.23dd07a2d9e84p-55, 0x1.bcc1e904bc1d2p+0}
    [f64::from_bits(0x3c811065895048de), f64::from_bits(0x3ffc199bdd85529c)], // {0x1.11065895048dep-55, 0x1.c199bdd85529cp+0}
    [f64::from_bits(0x3c92884dff483cac), f64::from_bits(0x3ffc67f12e57d14b)], // {0x1.2884dff483cacp-54, 0x1.c67f12e57d14bp+0}
    [f64::from_bits(0x3c7503cbd1e949dc), f64::from_bits(0x3ffcb720dcef9069)], // {0x1.503cbd1e949dcp-56, 0x1.cb720dcef9069p+0}
    [f64::from_bits(0xbc9cbc3743797a9c), f64::from_bits(0x3ffd072d4a07897c)], // {-0x1.cbc3743797a9cp-54, 0x1.d072d4a07897cp+0}
    [f64::from_bits(0x3c82ed02d75b3706), f64::from_bits(0x3ffd5818dcfba487)], // {0x1.2ed02d75b3706p-55, 0x1.d5818dcfba487p+0}
    [f64::from_bits(0x3c9c2300696db532), f64::from_bits(0x3ffda9e603db3285)], // {0x1.c2300696db532p-54, 0x1.da9e603db3285p+0}
    [f64::from_bits(0xbc91a5cd4f184b5c), f64::from_bits(0x3ffdfc97337b9b5f)], // {-0x1.1a5cd4f184b5cp-54, 0x1.dfc97337b9b5fp+0}
    [f64::from_bits(0x3c839e8980a9cc90), f64::from_bits(0x3ffe502ee78b3ff6)], // {0x1.39e8980a9cc9p-55, 0x1.e502ee78b3ff6p+0}
    [f64::from_bits(0xbc9e9c23179c2894), f64::from_bits(0x3ffea4afa2a490da)], // {-0x1.e9c23179c2894p-54, 0x1.ea4afa2a490dap+0}
    [f64::from_bits(0x3c9dc7f486a4b6b0), f64::from_bits(0x3ffefa1bee615a27)], // {0x1.dc7f486a4b6bp-54, 0x1.efa1bee615a27p+0}
    [f64::from_bits(0x3c99d3e12dd8a18a), f64::from_bits(0x3fff50765b6e4540)], // {0x1.9d3e12dd8a18ap-54, 0x1.f50765b6e454p+0}
    [f64::from_bits(0x3c874853f3a5931e), f64::from_bits(0x3fffa7c1819e90d8)], // {0x1.74853f3a5931ep-55, 0x1.fa7c1819e90d8p+0}
];
#[rustfmt::skip]
const T1: [[f64; 2]; 64] = [
    [f64::from_bits(0x0000000000000000), f64::from_bits(0x3ff0000000000000)], // {0x0p+0, 0x1p+0}
    [f64::from_bits(0x3c9ae8e38c59c72a), f64::from_bits(0x3ff000b175effdc7)], // {0x1.ae8e38c59c72ap-54, 0x1.000b175effdc7p+0}
    [f64::from_bits(0xbc57b5d0d58ea8f4), f64::from_bits(0x3ff00162f3904052)], // {-0x1.7b5d0d58ea8f4p-58, 0x1.00162f3904052p+0}
    [f64::from_bits(0x3c94115cb6b16a8e), f64::from_bits(0x3ff0021478e11ce6)], // {0x1.4115cb6b16a8ep-54, 0x1.0021478e11ce6p+0}
    [f64::from_bits(0xbc8d7c96f201bb2e), f64::from_bits(0x3ff002c605e2e8cf)], // {-0x1.d7c96f201bb2ep-55, 0x1.002c605e2e8cfp+0}
    [f64::from_bits(0x3c984711d4c35ea0), f64::from_bits(0x3ff003779a95f959)], // {0x1.84711d4c35eap-54, 0x1.003779a95f959p+0}
    [f64::from_bits(0xbc80484245243778), f64::from_bits(0x3ff0042936faa3d8)], // {-0x1.0484245243778p-55, 0x1.0042936faa3d8p+0}
    [f64::from_bits(0xbc94b237da2025fa), f64::from_bits(0x3ff004dadb113da0)], // {-0x1.4b237da2025fap-54, 0x1.004dadb113dap+0}
    [f64::from_bits(0xbc75e00e62d6b30e), f64::from_bits(0x3ff0058c86da1c0a)], // {-0x1.5e00e62d6b30ep-56, 0x1.0058c86da1c0ap+0}
    [f64::from_bits(0x3c9a1d6cedbb9480), f64::from_bits(0x3ff0063e3a559473)], // {0x1.a1d6cedbb948p-54, 0x1.0063e3a559473p+0}
    [f64::from_bits(0xbc94acf197a00142), f64::from_bits(0x3ff006eff583fc3d)], // {-0x1.4acf197a00142p-54, 0x1.006eff583fc3dp+0}
    [f64::from_bits(0xbc6eaf2ea42391a6), f64::from_bits(0x3ff007a1b865a8ca)], // {-0x1.eaf2ea42391a6p-57, 0x1.007a1b865a8cap+0}
    [f64::from_bits(0x3c7da93f90835f76), f64::from_bits(0x3ff0085382faef83)], // {0x1.da93f90835f76p-56, 0x1.0085382faef83p+0}
    [f64::from_bits(0xbc86a79084ab093c), f64::from_bits(0x3ff00905554425d4)], // {-0x1.6a79084ab093cp-55, 0x1.00905554425d4p+0}
    [f64::from_bits(0x3c986364f8fbe8f8), f64::from_bits(0x3ff009b72f41a12b)], // {0x1.86364f8fbe8f8p-54, 0x1.009b72f41a12bp+0}
    [f64::from_bits(0xbc882e8e14e3110e), f64::from_bits(0x3ff00a6910f3b6fd)], // {-0x1.82e8e14e3110ep-55, 0x1.00a6910f3b6fdp+0}
    [f64::from_bits(0xbc84f6b2a7609f72), f64::from_bits(0x3ff00b1afa5abcbf)], // {-0x1.4f6b2a7609f72p-55, 0x1.00b1afa5abcbfp+0}
    [f64::from_bits(0xbc7e1a258ea8f71a), f64::from_bits(0x3ff00bcceb7707ec)], // {-0x1.e1a258ea8f71ap-56, 0x1.00bcceb7707ecp+0}
    [f64::from_bits(0x3c74362ca5bc26f2), f64::from_bits(0x3ff00c7ee448ee02)], // {0x1.4362ca5bc26f2p-56, 0x1.00c7ee448ee02p+0}
    [f64::from_bits(0x3c9095a56c919d02), f64::from_bits(0x3ff00d30e4d0c483)], // {0x1.095a56c919d02p-54, 0x1.00d30e4d0c483p+0}
    [f64::from_bits(0xbc6406ac4e81a646), f64::from_bits(0x3ff00de2ed0ee0f5)], // {-0x1.406ac4e81a646p-57, 0x1.00de2ed0ee0f5p+0}
    [f64::from_bits(0x3c9b5a6902767e08), f64::from_bits(0x3ff00e94fd0398e0)], // {0x1.b5a6902767e08p-54, 0x1.00e94fd0398ep+0}
    [f64::from_bits(0xbc991b2060859320), f64::from_bits(0x3ff00f4714af41d3)], // {-0x1.91b206085932p-54, 0x1.00f4714af41d3p+0}
    [f64::from_bits(0x3c8427068ab22306), f64::from_bits(0x3ff00ff93412315c)], // {0x1.427068ab22306p-55, 0x1.00ff93412315cp+0}
    [f64::from_bits(0x3c9c1d0660524e08), f64::from_bits(0x3ff010ab5b2cbd11)], // {0x1.c1d0660524e08p-54, 0x1.010ab5b2cbd11p+0}
    [f64::from_bits(0xbc9e7bdfb3204be8), f64::from_bits(0x3ff0115d89ff3a8b)], // {-0x1.e7bdfb3204be8p-54, 0x1.0115d89ff3a8bp+0}
    [f64::from_bits(0x3c8843aa8b9cbbc6), f64::from_bits(0x3ff0120fc089ff63)], // {0x1.843aa8b9cbbc6p-55, 0x1.0120fc089ff63p+0}
    [f64::from_bits(0xbc734104ee7edae8), f64::from_bits(0x3ff012c1fecd613b)], // {-0x1.34104ee7edae8p-56, 0x1.012c1fecd613bp+0}
    [f64::from_bits(0xbc72b6aeb6176892), f64::from_bits(0x3ff0137444c9b5b5)], // {-0x1.2b6aeb6176892p-56, 0x1.0137444c9b5b5p+0}
    [f64::from_bits(0x3c7a8cd33b8a1bb2), f64::from_bits(0x3ff01426927f5278)], // {0x1.a8cd33b8a1bb2p-56, 0x1.01426927f5278p+0}
    [f64::from_bits(0x3c72edc08e5da99a), f64::from_bits(0x3ff014d8e7ee8d2f)], // {0x1.2edc08e5da99ap-56, 0x1.014d8e7ee8d2fp+0}
    [f64::from_bits(0x3c857ba2dc7e0c72), f64::from_bits(0x3ff0158b4517bb88)], // {0x1.57ba2dc7e0c72p-55, 0x1.0158b4517bb88p+0}
    [f64::from_bits(0x3c9b61299ab8cdb8), f64::from_bits(0x3ff0163da9fb3335)], // {0x1.b61299ab8cdb8p-54, 0x1.0163da9fb3335p+0}
    [f64::from_bits(0xbc990565902c5f44), f64::from_bits(0x3ff016f0169949ed)], // {-0x1.90565902c5f44p-54, 0x1.016f0169949edp+0}
    [f64::from_bits(0x3c870fc41c5c2d54), f64::from_bits(0x3ff017a28af25567)], // {0x1.70fc41c5c2d54p-55, 0x1.017a28af25567p+0}
    [f64::from_bits(0x3c94b9a6e145d76c), f64::from_bits(0x3ff018550706ab62)], // {0x1.4b9a6e145d76cp-54, 0x1.018550706ab62p+0}
    [f64::from_bits(0xbc7008eff5142bfa), f64::from_bits(0x3ff019078ad6a19f)], // {-0x1.008eff5142bfap-56, 0x1.019078ad6a19fp+0}
    [f64::from_bits(0xbc977669f033c7de), f64::from_bits(0x3ff019ba16628de2)], // {-0x1.77669f033c7dep-54, 0x1.019ba16628de2p+0}
    [f64::from_bits(0xbc909bb78eeead0a), f64::from_bits(0x3ff01a6ca9aac5f3)], // {-0x1.09bb78eeead0ap-54, 0x1.01a6ca9aac5f3p+0}
    [f64::from_bits(0x3c9371231477ece6), f64::from_bits(0x3ff01b1f44af9f9e)], // {0x1.371231477ece6p-54, 0x1.01b1f44af9f9ep+0}
    [f64::from_bits(0x3c75e7626621eb5a), f64::from_bits(0x3ff01bd1e77170b4)], // {0x1.5e7626621eb5ap-56, 0x1.01bd1e77170b4p+0}
    [f64::from_bits(0xbc9bc72b100828a4), f64::from_bits(0x3ff01c8491f08f08)], // {-0x1.bc72b100828a4p-54, 0x1.01c8491f08f08p+0}
    [f64::from_bits(0xbc6ce39cbbab8bbe), f64::from_bits(0x3ff01d37442d5070)], // {-0x1.ce39cbbab8bbep-57, 0x1.01d37442d507p+0}
    [f64::from_bits(0x3c816996709da2e2), f64::from_bits(0x3ff01de9fe280ac8)], // {0x1.16996709da2e2p-55, 0x1.01de9fe280ac8p+0}
    [f64::from_bits(0xbc8c11f5239bf536), f64::from_bits(0x3ff01e9cbfe113ef)], // {-0x1.c11f5239bf536p-55, 0x1.01e9cbfe113efp+0}
    [f64::from_bits(0x3c8e1d4eb5edc6b4), f64::from_bits(0x3ff01f4f8958c1c6)], // {0x1.e1d4eb5edc6b4p-55, 0x1.01f4f8958c1c6p+0}
    [f64::from_bits(0xbc9afb99946ee3f0), f64::from_bits(0x3ff020025a8f6a35)], // {-0x1.afb99946ee3fp-54, 0x1.020025a8f6a35p+0}
    [f64::from_bits(0xbc98f06d8a148a32), f64::from_bits(0x3ff020b533856324)], // {-0x1.8f06d8a148a32p-54, 0x1.020b533856324p+0}
    [f64::from_bits(0xbc82bf310fc54eb6), f64::from_bits(0x3ff02168143b0281)], // {-0x1.2bf310fc54eb6p-55, 0x1.02168143b0281p+0}
    [f64::from_bits(0xbc9c95a035eb4176), f64::from_bits(0x3ff0221afcb09e3e)], // {-0x1.c95a035eb4176p-54, 0x1.0221afcb09e3ep+0}
    [f64::from_bits(0xbc9491793e46834c), f64::from_bits(0x3ff022cdece68c4f)], // {-0x1.491793e46834cp-54, 0x1.022cdece68c4fp+0}
    [f64::from_bits(0xbc73e8d0d9c49090), f64::from_bits(0x3ff02380e4dd22ad)], // {-0x1.3e8d0d9c4909p-56, 0x1.02380e4dd22adp+0}
    [f64::from_bits(0xbc9314aa16278aa4), f64::from_bits(0x3ff02433e494b755)], // {-0x1.314aa16278aa4p-54, 0x1.02433e494b755p+0}
    [f64::from_bits(0x3c848daf888e9650), f64::from_bits(0x3ff024e6ec0da046)], // {0x1.48daf888e965p-55, 0x1.024e6ec0da046p+0}
    [f64::from_bits(0x3c856dc8046821f4), f64::from_bits(0x3ff02599fb483385)], // {0x1.56dc8046821f4p-55, 0x1.02599fb483385p+0}
    [f64::from_bits(0x3c945b42356b9d46), f64::from_bits(0x3ff0264d1244c719)], // {0x1.45b42356b9d46p-54, 0x1.0264d1244c719p+0}
    [f64::from_bits(0xbc7082ef51b61d7e), f64::from_bits(0x3ff027003103b10e)], // {-0x1.082ef51b61d7ep-56, 0x1.027003103b10ep+0}
    [f64::from_bits(0x3c72106ed0920a34), f64::from_bits(0x3ff027b357854772)], // {0x1.2106ed0920a34p-56, 0x1.027b357854772p+0}
    [f64::from_bits(0xbc9fd4cf26ea5d0e), f64::from_bits(0x3ff0286685c9e059)], // {-0x1.fd4cf26ea5d0ep-54, 0x1.0286685c9e059p+0}
    [f64::from_bits(0xbc909f8775e78084), f64::from_bits(0x3ff02919bbd1d1d8)], // {-0x1.09f8775e78084p-54, 0x1.02919bbd1d1d8p+0}
    [f64::from_bits(0x3c564cbba902ca28), f64::from_bits(0x3ff029ccf99d720a)], // {0x1.64cbba902ca28p-58, 0x1.029ccf99d720ap+0}
    [f64::from_bits(0x3c94383ef231d206), f64::from_bits(0x3ff02a803f2d170d)], // {0x1.4383ef231d206p-54, 0x1.02a803f2d170dp+0}
    [f64::from_bits(0x3c94a47a505b3a46), f64::from_bits(0x3ff02b338c811703)], // {0x1.4a47a505b3a46p-54, 0x1.02b338c811703p+0}
    [f64::from_bits(0x3c9e471202234680), f64::from_bits(0x3ff02be6e199c811)], // {0x1.e47120223468p-54, 0x1.02be6e199c811p+0}
];
#[rustfmt::skip]
const CH_ACC: [[f64; 2]; 7] = [
    [f64::from_bits(0x3ff0000000000000), f64::from_bits(0x0000000000000000)], // {0x1p+0, 0}
    [f64::from_bits(0x3fe0000000000000), f64::from_bits(0x39c712f72ecec2cf)], // {0x1p-1, 0x1.712f72ecec2cfp-99}
    [f64::from_bits(0x3fc5555555555555), f64::from_bits(0x3c65555555554d07)], // {0x1.5555555555555p-3, 0x1.5555555554d07p-57}
    [f64::from_bits(0x3fa5555555555555), f64::from_bits(0x3c455194d28275da)], // {0x1.5555555555555p-5, 0x1.55194d28275dap-59}
    [f64::from_bits(0x3f81111111111111), f64::from_bits(0x3c012faa0e1c0f7b)], // {0x1.1111111111111p-7, 0x1.12faa0e1c0f7bp-63}
    [f64::from_bits(0x3f56c16c16da6973), f64::from_bits(0xbbf4ba45ab25d2a3)], // {0x1.6c16c16da6973p-10, -0x1.4ba45ab25d2a3p-64}
    [f64::from_bits(0x3f2a01a019eb7f31), f64::from_bits(0xbbc9091d845ecd36)], // {0x1.a01a019eb7f31p-13, -0x1.9091d845ecd36p-67}
];
#[rustfmt::skip]
const CH_FAST: [f64; 4] = [
    f64::from_bits(0x3ff0000000000000), // 0x1p+0
    f64::from_bits(0x3fe0000000000000), // 0x1p-1
    f64::from_bits(0x3fc55555557e54ff), // 0x1.55555557e54ffp-3
    f64::from_bits(0x3fa55555553a12f4), // 0x1.55555553a12f4p-5
];
const S: f64 = f64::from_bits(0x40b71547652b82fe); // 0x1.71547652b82fep+12
const L2H: f64 = f64::from_bits(0x3f262e42ff000000); // 0x1.62e42ffp-13
const L2L: f64 = f64::from_bits(0x3d0718432a1b0e26); // 0x1.718432a1b0e26p-47
const L2LL: f64 = f64::from_bits(0x3999ff0342542fc3); // 0x1.9ff0342542fc3p-102
const EPS: f64 = f64::from_bits(0x3c0833beace2b6fe); // 1.64e-19
// END GENERATED TABLES

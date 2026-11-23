// Rust translation of CORE-MATH's pow.h (https://core-math.gitlabpages.inria.fr/).
// Original: Copyright (c) 2022-2025 CERN and Inria
// Authors of the original: Tom Hubrecht and Paul Zimmermann.
// This file was generated mechanically by tools/gen_tables.py from pow.h;
// do not edit by hand.
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

#![allow(clippy::unreadable_literal, clippy::excessive_precision)]
// ONE and ONE_Q are defined by the C headers but unused by pow.
#![allow(dead_code)]

pub(crate) static INVERSE: [f64; 182] = [
    f64::from_bits(0x3ff6900000000000), /* 0x1.69p+0 */
    f64::from_bits(0x3ff6700000000000), /* 0x1.67p+0 */
    f64::from_bits(0x3ff6500000000000), /* 0x1.65p+0 */
    f64::from_bits(0x3ff6300000000000), /* 0x1.63p+0 */
    f64::from_bits(0x3ff6100000000000), /* 0x1.61p+0 */
    f64::from_bits(0x3ff5f00000000000), /* 0x1.5fp+0 */
    f64::from_bits(0x3ff5e00000000000), /* 0x1.5ep+0 */
    f64::from_bits(0x3ff5c00000000000), /* 0x1.5cp+0 */
    f64::from_bits(0x3ff5a00000000000), /* 0x1.5ap+0 */
    f64::from_bits(0x3ff5800000000000), /* 0x1.58p+0 */
    f64::from_bits(0x3ff5600000000000), /* 0x1.56p+0 */
    f64::from_bits(0x3ff5400000000000), /* 0x1.54p+0 */
    f64::from_bits(0x3ff5300000000000), /* 0x1.53p+0 */
    f64::from_bits(0x3ff5100000000000), /* 0x1.51p+0 */
    f64::from_bits(0x3ff4f00000000000), /* 0x1.4fp+0 */
    f64::from_bits(0x3ff4e00000000000), /* 0x1.4ep+0 */
    f64::from_bits(0x3ff4c00000000000), /* 0x1.4cp+0 */
    f64::from_bits(0x3ff4a00000000000), /* 0x1.4ap+0 */
    f64::from_bits(0x3ff4800000000000), /* 0x1.48p+0 */
    f64::from_bits(0x3ff4700000000000), /* 0x1.47p+0 */
    f64::from_bits(0x3ff4500000000000), /* 0x1.45p+0 */
    f64::from_bits(0x3ff4400000000000), /* 0x1.44p+0 */
    f64::from_bits(0x3ff4200000000000), /* 0x1.42p+0 */
    f64::from_bits(0x3ff4000000000000), /* 0x1.4p+0 */
    f64::from_bits(0x3ff3f00000000000), /* 0x1.3fp+0 */
    f64::from_bits(0x3ff3d00000000000), /* 0x1.3dp+0 */
    f64::from_bits(0x3ff3c00000000000), /* 0x1.3cp+0 */
    f64::from_bits(0x3ff3a00000000000), /* 0x1.3ap+0 */
    f64::from_bits(0x3ff3900000000000), /* 0x1.39p+0 */
    f64::from_bits(0x3ff3700000000000), /* 0x1.37p+0 */
    f64::from_bits(0x3ff3600000000000), /* 0x1.36p+0 */
    f64::from_bits(0x3ff3400000000000), /* 0x1.34p+0 */
    f64::from_bits(0x3ff3300000000000), /* 0x1.33p+0 */
    f64::from_bits(0x3ff3200000000000), /* 0x1.32p+0 */
    f64::from_bits(0x3ff3000000000000), /* 0x1.3p+0 */
    f64::from_bits(0x3ff2f00000000000), /* 0x1.2fp+0 */
    f64::from_bits(0x3ff2d00000000000), /* 0x1.2dp+0 */
    f64::from_bits(0x3ff2c00000000000), /* 0x1.2cp+0 */
    f64::from_bits(0x3ff2b00000000000), /* 0x1.2bp+0 */
    f64::from_bits(0x3ff2900000000000), /* 0x1.29p+0 */
    f64::from_bits(0x3ff2800000000000), /* 0x1.28p+0 */
    f64::from_bits(0x3ff2700000000000), /* 0x1.27p+0 */
    f64::from_bits(0x3ff2500000000000), /* 0x1.25p+0 */
    f64::from_bits(0x3ff2400000000000), /* 0x1.24p+0 */
    f64::from_bits(0x3ff2300000000000), /* 0x1.23p+0 */
    f64::from_bits(0x3ff2100000000000), /* 0x1.21p+0 */
    f64::from_bits(0x3ff2000000000000), /* 0x1.2p+0 */
    f64::from_bits(0x3ff1f00000000000), /* 0x1.1fp+0 */
    f64::from_bits(0x3ff1e00000000000), /* 0x1.1ep+0 */
    f64::from_bits(0x3ff1c00000000000), /* 0x1.1cp+0 */
    f64::from_bits(0x3ff1b00000000000), /* 0x1.1bp+0 */
    f64::from_bits(0x3ff1a00000000000), /* 0x1.1ap+0 */
    f64::from_bits(0x3ff1900000000000), /* 0x1.19p+0 */
    f64::from_bits(0x3ff1700000000000), /* 0x1.17p+0 */
    f64::from_bits(0x3ff1600000000000), /* 0x1.16p+0 */
    f64::from_bits(0x3ff1500000000000), /* 0x1.15p+0 */
    f64::from_bits(0x3ff1400000000000), /* 0x1.14p+0 */
    f64::from_bits(0x3ff1300000000000), /* 0x1.13p+0 */
    f64::from_bits(0x3ff1200000000000), /* 0x1.12p+0 */
    f64::from_bits(0x3ff1000000000000), /* 0x1.1p+0 */
    f64::from_bits(0x3ff0f00000000000), /* 0x1.0fp+0 */
    f64::from_bits(0x3ff0e00000000000), /* 0x1.0ep+0 */
    f64::from_bits(0x3ff0d00000000000), /* 0x1.0dp+0 */
    f64::from_bits(0x3ff0c00000000000), /* 0x1.0cp+0 */
    f64::from_bits(0x3ff0b00000000000), /* 0x1.0bp+0 */
    f64::from_bits(0x3ff0a00000000000), /* 0x1.0ap+0 */
    f64::from_bits(0x3ff0900000000000), /* 0x1.09p+0 */
    f64::from_bits(0x3ff0800000000000), /* 0x1.08p+0 */
    f64::from_bits(0x3ff0700000000000), /* 0x1.07p+0 */
    f64::from_bits(0x3ff0600000000000), /* 0x1.06p+0 */
    f64::from_bits(0x3ff0500000000000), /* 0x1.05p+0 */
    f64::from_bits(0x3ff0400000000000), /* 0x1.04p+0 */
    f64::from_bits(0x3ff0300000000000), /* 0x1.03p+0 */
    f64::from_bits(0x3ff0200000000000), /* 0x1.02p+0 */
    f64::from_bits(0x3ff0000000000000), /* 0x1.00p+0 */
    f64::from_bits(0x3ff0000000000000), /* 0x1.00p+0 */
    f64::from_bits(0x3fefd00000000000), /* 0x1.fdp-1 */
    f64::from_bits(0x3fefb00000000000), /* 0x1.fbp-1 */
    f64::from_bits(0x3fef900000000000), /* 0x1.f9p-1 */
    f64::from_bits(0x3fef700000000000), /* 0x1.f7p-1 */
    f64::from_bits(0x3fef500000000000), /* 0x1.f5p-1 */
    f64::from_bits(0x3fef300000000000), /* 0x1.f3p-1 */
    f64::from_bits(0x3fef100000000000), /* 0x1.f1p-1 */
    f64::from_bits(0x3fef000000000000), /* 0x1.fp-1 */
    f64::from_bits(0x3feee00000000000), /* 0x1.eep-1 */
    f64::from_bits(0x3feec00000000000), /* 0x1.ecp-1 */
    f64::from_bits(0x3feea00000000000), /* 0x1.eap-1 */
    f64::from_bits(0x3fee800000000000), /* 0x1.e8p-1 */
    f64::from_bits(0x3fee600000000000), /* 0x1.e6p-1 */
    f64::from_bits(0x3fee500000000000), /* 0x1.e5p-1 */
    f64::from_bits(0x3fee300000000000), /* 0x1.e3p-1 */
    f64::from_bits(0x3fee100000000000), /* 0x1.e1p-1 */
    f64::from_bits(0x3fedf00000000000), /* 0x1.dfp-1 */
    f64::from_bits(0x3fedd00000000000), /* 0x1.ddp-1 */
    f64::from_bits(0x3fedc00000000000), /* 0x1.dcp-1 */
    f64::from_bits(0x3feda00000000000), /* 0x1.dap-1 */
    f64::from_bits(0x3fed800000000000), /* 0x1.d8p-1 */
    f64::from_bits(0x3fed700000000000), /* 0x1.d7p-1 */
    f64::from_bits(0x3fed500000000000), /* 0x1.d5p-1 */
    f64::from_bits(0x3fed300000000000), /* 0x1.d3p-1 */
    f64::from_bits(0x3fed200000000000), /* 0x1.d2p-1 */
    f64::from_bits(0x3fed000000000000), /* 0x1.dp-1 */
    f64::from_bits(0x3fece00000000000), /* 0x1.cep-1 */
    f64::from_bits(0x3fecd00000000000), /* 0x1.cdp-1 */
    f64::from_bits(0x3fecb00000000000), /* 0x1.cbp-1 */
    f64::from_bits(0x3fec900000000000), /* 0x1.c9p-1 */
    f64::from_bits(0x3fec800000000000), /* 0x1.c8p-1 */
    f64::from_bits(0x3fec600000000000), /* 0x1.c6p-1 */
    f64::from_bits(0x3fec500000000000), /* 0x1.c5p-1 */
    f64::from_bits(0x3fec300000000000), /* 0x1.c3p-1 */
    f64::from_bits(0x3fec200000000000), /* 0x1.c2p-1 */
    f64::from_bits(0x3fec000000000000), /* 0x1.cp-1 */
    f64::from_bits(0x3febf00000000000), /* 0x1.bfp-1 */
    f64::from_bits(0x3febd00000000000), /* 0x1.bdp-1 */
    f64::from_bits(0x3febc00000000000), /* 0x1.bcp-1 */
    f64::from_bits(0x3feba00000000000), /* 0x1.bap-1 */
    f64::from_bits(0x3feb900000000000), /* 0x1.b9p-1 */
    f64::from_bits(0x3feb700000000000), /* 0x1.b7p-1 */
    f64::from_bits(0x3feb600000000000), /* 0x1.b6p-1 */
    f64::from_bits(0x3feb400000000000), /* 0x1.b4p-1 */
    f64::from_bits(0x3feb300000000000), /* 0x1.b3p-1 */
    f64::from_bits(0x3feb100000000000), /* 0x1.b1p-1 */
    f64::from_bits(0x3feb000000000000), /* 0x1.bp-1 */
    f64::from_bits(0x3feae00000000000), /* 0x1.aep-1 */
    f64::from_bits(0x3fead00000000000), /* 0x1.adp-1 */
    f64::from_bits(0x3feac00000000000), /* 0x1.acp-1 */
    f64::from_bits(0x3feaa00000000000), /* 0x1.aap-1 */
    f64::from_bits(0x3fea900000000000), /* 0x1.a9p-1 */
    f64::from_bits(0x3fea700000000000), /* 0x1.a7p-1 */
    f64::from_bits(0x3fea600000000000), /* 0x1.a6p-1 */
    f64::from_bits(0x3fea500000000000), /* 0x1.a5p-1 */
    f64::from_bits(0x3fea300000000000), /* 0x1.a3p-1 */
    f64::from_bits(0x3fea200000000000), /* 0x1.a2p-1 */
    f64::from_bits(0x3fea100000000000), /* 0x1.a1p-1 */
    f64::from_bits(0x3fe9f00000000000), /* 0x1.9fp-1 */
    f64::from_bits(0x3fe9e00000000000), /* 0x1.9ep-1 */
    f64::from_bits(0x3fe9d00000000000), /* 0x1.9dp-1 */
    f64::from_bits(0x3fe9c00000000000), /* 0x1.9cp-1 */
    f64::from_bits(0x3fe9a00000000000), /* 0x1.9ap-1 */
    f64::from_bits(0x3fe9900000000000), /* 0x1.99p-1 */
    f64::from_bits(0x3fe9800000000000), /* 0x1.98p-1 */
    f64::from_bits(0x3fe9600000000000), /* 0x1.96p-1 */
    f64::from_bits(0x3fe9500000000000), /* 0x1.95p-1 */
    f64::from_bits(0x3fe9400000000000), /* 0x1.94p-1 */
    f64::from_bits(0x3fe9300000000000), /* 0x1.93p-1 */
    f64::from_bits(0x3fe9100000000000), /* 0x1.91p-1 */
    f64::from_bits(0x3fe9000000000000), /* 0x1.9p-1 */
    f64::from_bits(0x3fe8f00000000000), /* 0x1.8fp-1 */
    f64::from_bits(0x3fe8e00000000000), /* 0x1.8ep-1 */
    f64::from_bits(0x3fe8d00000000000), /* 0x1.8dp-1 */
    f64::from_bits(0x3fe8b00000000000), /* 0x1.8bp-1 */
    f64::from_bits(0x3fe8a00000000000), /* 0x1.8ap-1 */
    f64::from_bits(0x3fe8900000000000), /* 0x1.89p-1 */
    f64::from_bits(0x3fe8800000000000), /* 0x1.88p-1 */
    f64::from_bits(0x3fe8700000000000), /* 0x1.87p-1 */
    f64::from_bits(0x3fe8600000000000), /* 0x1.86p-1 */
    f64::from_bits(0x3fe8400000000000), /* 0x1.84p-1 */
    f64::from_bits(0x3fe8300000000000), /* 0x1.83p-1 */
    f64::from_bits(0x3fe8200000000000), /* 0x1.82p-1 */
    f64::from_bits(0x3fe8100000000000), /* 0x1.81p-1 */
    f64::from_bits(0x3fe8000000000000), /* 0x1.8p-1 */
    f64::from_bits(0x3fe7f00000000000), /* 0x1.7fp-1 */
    f64::from_bits(0x3fe7e00000000000), /* 0x1.7ep-1 */
    f64::from_bits(0x3fe7c00000000000), /* 0x1.7cp-1 */
    f64::from_bits(0x3fe7b00000000000), /* 0x1.7bp-1 */
    f64::from_bits(0x3fe7a00000000000), /* 0x1.7ap-1 */
    f64::from_bits(0x3fe7900000000000), /* 0x1.79p-1 */
    f64::from_bits(0x3fe7800000000000), /* 0x1.78p-1 */
    f64::from_bits(0x3fe7700000000000), /* 0x1.77p-1 */
    f64::from_bits(0x3fe7600000000000), /* 0x1.76p-1 */
    f64::from_bits(0x3fe7500000000000), /* 0x1.75p-1 */
    f64::from_bits(0x3fe7400000000000), /* 0x1.74p-1 */
    f64::from_bits(0x3fe7300000000000), /* 0x1.73p-1 */
    f64::from_bits(0x3fe7200000000000), /* 0x1.72p-1 */
    f64::from_bits(0x3fe7100000000000), /* 0x1.71p-1 */
    f64::from_bits(0x3fe7000000000000), /* 0x1.7p-1 */
    f64::from_bits(0x3fe6f00000000000), /* 0x1.6fp-1 */
    f64::from_bits(0x3fe6e00000000000), /* 0x1.6ep-1 */
    f64::from_bits(0x3fe6d00000000000), /* 0x1.6dp-1 */
    f64::from_bits(0x3fe6c00000000000), /* 0x1.6cp-1 */
    f64::from_bits(0x3fe6b00000000000), /* 0x1.6bp-1 */
    f64::from_bits(0x3fe6a00000000000), /* 0x1.6ap-1 */
];

pub(crate) static LOG_INV: [[f64; 2]; 182] = [
    [
        f64::from_bits(0xbfd5ff3070a79000), /* -0x1.5ff3070a79p-2 */
        f64::from_bits(0xbd2e9e439f105039), /* -0x1.e9e439f105039p-45 */
    ],
    [
        f64::from_bits(0xbfd5a42ab0f4d000), /* -0x1.5a42ab0f4dp-2 */
        f64::from_bits(0x3cde63af2df7ba69), /* 0x1.e63af2df7ba69p-50 */
    ],
    [
        f64::from_bits(0xbfd548a2c3add000), /* -0x1.548a2c3addp-2 */
        f64::from_bits(0xbd23167e63081cf7), /* -0x1.3167e63081cf7p-45 */
    ],
    [
        f64::from_bits(0xbfd4ec9732600000), /* -0x1.4ec97326p-2 */
        f64::from_bits(0xbd234d7aaf04d104), /* -0x1.34d7aaf04d104p-45 */
    ],
    [
        f64::from_bits(0xbfd4900680401000), /* -0x1.4900680401p-2 */
        f64::from_bits(0x3d38bccffe1a0f8c), /* 0x1.8bccffe1a0f8cp-44 */
    ],
    [
        f64::from_bits(0xbfd432ef2a04f000), /* -0x1.432ef2a04fp-2 */
        f64::from_bits(0x3d3fb129931715ad), /* 0x1.fb129931715adp-44 */
    ],
    [
        f64::from_bits(0xbfd404308686a000), /* -0x1.404308686ap-2 */
        f64::from_bits(0xbd3f8ef43049f7d3), /* -0x1.f8ef43049f7d3p-44 */
    ],
    [
        f64::from_bits(0xbfd3a64c55694000), /* -0x1.3a64c55694p-2 */
        f64::from_bits(0xbd37a71cbcd735d0), /* -0x1.7a71cbcd735dp-44 */
    ],
    [
        f64::from_bits(0xbfd347dd9a988000), /* -0x1.347dd9a988p-2 */
        f64::from_bits(0x3d25594dd4c58092), /* 0x1.5594dd4c58092p-45 */
    ],
    [
        f64::from_bits(0xbfd2e8e2bae12000), /* -0x1.2e8e2bae12p-2 */
        f64::from_bits(0x3d267b1e99b72bd8), /* 0x1.67b1e99b72bd8p-45 */
    ],
    [
        f64::from_bits(0xbfd2895a13de8000), /* -0x1.2895a13de8p-2 */
        f64::from_bits(0xbd3a8d7ad24c13f0), /* -0x1.a8d7ad24c13fp-44 */
    ],
    [
        f64::from_bits(0xbfd22941fbcf8000), /* -0x1.22941fbcf8p-2 */
        f64::from_bits(0x3d3a6976f5eb0963), /* 0x1.a6976f5eb0963p-44 */
    ],
    [
        f64::from_bits(0xbfd1f8ff9e48a000), /* -0x1.1f8ff9e48ap-2 */
        f64::from_bits(0xbd27946c040cbe77), /* -0x1.7946c040cbe77p-45 */
    ],
    [
        f64::from_bits(0xbfd1980d2dd42000), /* -0x1.1980d2dd42p-2 */
        f64::from_bits(0xbd2b7b3a7a361c9a), /* -0x1.b7b3a7a361c9ap-45 */
    ],
    [
        f64::from_bits(0xbfd136870293b000), /* -0x1.136870293bp-2 */
        f64::from_bits(0x3d3d3e8499d67123), /* 0x1.d3e8499d67123p-44 */
    ],
    [
        f64::from_bits(0xbfd1058bf9ae5000), /* -0x1.1058bf9ae5p-2 */
        f64::from_bits(0x3d34ab9d817d52cd), /* 0x1.4ab9d817d52cdp-44 */
    ],
    [
        f64::from_bits(0xbfd0a324e2739000), /* -0x1.0a324e2739p-2 */
        f64::from_bits(0xbd0c6bee7ef4030e), /* -0x1.c6bee7ef4030ep-47 */
    ],
    [
        f64::from_bits(0xbfd0402594b4d000), /* -0x1.0402594b4dp-2 */
        f64::from_bits(0xbcf036b89ef42d7f), /* -0x1.036b89ef42d7fp-48 */
    ],
    [
        f64::from_bits(0xbfcfb9186d5e4000), /* -0x1.fb9186d5e4p-3 */
        f64::from_bits(0x3d0d572aab993c87), /* 0x1.d572aab993c87p-47 */
    ],
    [
        f64::from_bits(0xbfcf550a564b8000), /* -0x1.f550a564b8p-3 */
        f64::from_bits(0x3d2323e3a09202fe), /* 0x1.323e3a09202fep-45 */
    ],
    [
        f64::from_bits(0xbfce8c0252aa6000), /* -0x1.e8c0252aa6p-3 */
        f64::from_bits(0x3d26805b80e8e6ff), /* 0x1.6805b80e8e6ffp-45 */
    ],
    [
        f64::from_bits(0xbfce27076e2b0000), /* -0x1.e27076e2bp-3 */
        f64::from_bits(0x3d3a342c2af0003c), /* 0x1.a342c2af0003cp-44 */
    ],
    [
        f64::from_bits(0xbfcd5c216b4fc000), /* -0x1.d5c216b4fcp-3 */
        f64::from_bits(0x3d21ba91bbca681b), /* 0x1.1ba91bbca681bp-45 */
    ],
    [
        f64::from_bits(0xbfcc8ff7c79aa000), /* -0x1.c8ff7c79aap-3 */
        f64::from_bits(0x3d27794f689f8434), /* 0x1.7794f689f8434p-45 */
    ],
    [
        f64::from_bits(0xbfcc2968558c2000), /* -0x1.c2968558c2p-3 */
        f64::from_bits(0x3d2cfd73dee38a40), /* 0x1.cfd73dee38a4p-45 */
    ],
    [
        f64::from_bits(0xbfcb5b519e8fc000), /* -0x1.b5b519e8fcp-3 */
        f64::from_bits(0x3d34b722ec011f31), /* 0x1.4b722ec011f31p-44 */
    ],
    [
        f64::from_bits(0xbfcaf3c94e80c000), /* -0x1.af3c94e80cp-3 */
        f64::from_bits(0x3cba4e633fcd9066), /* 0x1.a4e633fcd9066p-52 */
    ],
    [
        f64::from_bits(0xbfca23bc1fe2c000), /* -0x1.a23bc1fe2cp-3 */
        f64::from_bits(0x3d3539cd91dc9f0b), /* 0x1.539cd91dc9f0bp-44 */
    ],
    [
        f64::from_bits(0xbfc9bb362e7e0000), /* -0x1.9bb362e7ep-3 */
        f64::from_bits(0x3d21f2a8a1ce0ffc), /* 0x1.1f2a8a1ce0ffcp-45 */
    ],
    [
        f64::from_bits(0xbfc8e928de886000), /* -0x1.8e928de886p-3 */
        f64::from_bits(0xbd3a8154b13d72d5), /* -0x1.a8154b13d72d5p-44 */
    ],
    [
        f64::from_bits(0xbfc87fa06520c000), /* -0x1.87fa06520cp-3 */
        f64::from_bits(0xbd322120401202fc), /* -0x1.22120401202fcp-44 */
    ],
    [
        f64::from_bits(0xbfc7ab890210e000), /* -0x1.7ab890210ep-3 */
        f64::from_bits(0x3d2bdb9072534a58), /* 0x1.bdb9072534a58p-45 */
    ],
    [
        f64::from_bits(0xbfc740f8f5404000), /* -0x1.740f8f5404p-3 */
        f64::from_bits(0x3d30b66c99018aa1), /* 0x1.0b66c99018aa1p-44 */
    ],
    [
        f64::from_bits(0xbfc6d60fe719e000), /* -0x1.6d60fe719ep-3 */
        f64::from_bits(0x3d3bc6e557134767), /* 0x1.bc6e557134767p-44 */
    ],
    [
        f64::from_bits(0xbfc5ff3070a7a000), /* -0x1.5ff3070a7ap-3 */
        f64::from_bits(0x3d38586f183bebf2), /* 0x1.8586f183bebf2p-44 */
    ],
    [
        f64::from_bits(0xbfc59338d9982000), /* -0x1.59338d9982p-3 */
        f64::from_bits(0xbcf0ba68b7555d4a), /* -0x1.0ba68b7555d4ap-48 */
    ],
    [
        f64::from_bits(0xbfc4ba36f39a6000), /* -0x1.4ba36f39a6p-3 */
        f64::from_bits(0x3d34354bb3f219e5), /* 0x1.4354bb3f219e5p-44 */
    ],
    [
        f64::from_bits(0xbfc44d2b6ccb8000), /* -0x1.44d2b6ccb8p-3 */
        f64::from_bits(0x3d170cc16135783c), /* 0x1.70cc16135783cp-46 */
    ],
    [
        f64::from_bits(0xbfc3dfc2b0ecc000), /* -0x1.3dfc2b0eccp-3 */
        f64::from_bits(0xbd28a72a62b8c13f), /* -0x1.8a72a62b8c13fp-45 */
    ],
    [
        f64::from_bits(0xbfc303d718e48000), /* -0x1.303d718e48p-3 */
        f64::from_bits(0x3cd680b5ce3ecb05), /* 0x1.680b5ce3ecb05p-50 */
    ],
    [
        f64::from_bits(0xbfc29552f8200000), /* -0x1.29552f82p-3 */
        f64::from_bits(0x3d35b967f4471dfc), /* 0x1.5b967f4471dfcp-44 */
    ],
    [
        f64::from_bits(0xbfc2266f190a6000), /* -0x1.2266f190a6p-3 */
        f64::from_bits(0x3d24d20ab840e7f6), /* 0x1.4d20ab840e7f6p-45 */
    ],
    [
        f64::from_bits(0xbfc1478584674000), /* -0x1.1478584674p-3 */
        f64::from_bits(0xbd1563451027c750), /* -0x1.563451027c75p-46 */
    ],
    [
        f64::from_bits(0xbfc0d77e7cd08000), /* -0x1.0d77e7cd08p-3 */
        f64::from_bits(0xbd3cb2cd2ee2f482), /* -0x1.cb2cd2ee2f482p-44 */
    ],
    [
        f64::from_bits(0xbfc0671512ca6000), /* -0x1.0671512ca6p-3 */
        f64::from_bits(0x3d2a47579cdc0a3d), /* 0x1.a47579cdc0a3dp-45 */
    ],
    [
        f64::from_bits(0xbfbf0a30c0118000), /* -0x1.f0a30c0118p-4 */
        f64::from_bits(0x3d3d599e83368e91), /* 0x1.d599e83368e91p-44 */
    ],
    [
        f64::from_bits(0xbfbe27076e2b0000), /* -0x1.e27076e2bp-4 */
        f64::from_bits(0x3d2a342c2af0003c), /* 0x1.a342c2af0003cp-45 */
    ],
    [
        f64::from_bits(0xbfbd4313d66cc000), /* -0x1.d4313d66ccp-4 */
        f64::from_bits(0x3d29454379135713), /* 0x1.9454379135713p-45 */
    ],
    [
        f64::from_bits(0xbfbc5e548f5bc000), /* -0x1.c5e548f5bcp-4 */
        f64::from_bits(0xbd1d0c57585fbe06), /* -0x1.d0c57585fbe06p-46 */
    ],
    [
        f64::from_bits(0xbfba926d3a4ac000), /* -0x1.a926d3a4acp-4 */
        f64::from_bits(0xbd3563650bd22a9c), /* -0x1.563650bd22a9cp-44 */
    ],
    [
        f64::from_bits(0xbfb9ab4246204000), /* -0x1.9ab4246204p-4 */
        f64::from_bits(0x3d28a64826787061), /* 0x1.8a64826787061p-45 */
    ],
    [
        f64::from_bits(0xbfb8c345d6318000), /* -0x1.8c345d6318p-4 */
        f64::from_bits(0xbd3b20f5acb42a66), /* -0x1.b20f5acb42a66p-44 */
    ],
    [
        f64::from_bits(0xbfb7da766d7b0000), /* -0x1.7da766d7bp-4 */
        f64::from_bits(0xbd32cc844480c89b), /* -0x1.2cc844480c89bp-44 */
    ],
    [
        f64::from_bits(0xbfb60658a9374000), /* -0x1.60658a9374p-4 */
        f64::from_bits(0xbd30c3b1dee9c4f8), /* -0x1.0c3b1dee9c4f8p-44 */
    ],
    [
        f64::from_bits(0xbfb51b073f060000), /* -0x1.51b073f06p-4 */
        f64::from_bits(0xbd383f69278e686a), /* -0x1.83f69278e686ap-44 */
    ],
    [
        f64::from_bits(0xbfb42edcbea64000), /* -0x1.42edcbea64p-4 */
        f64::from_bits(0xbd1bc0eeea7c9acd), /* -0x1.bc0eeea7c9acdp-46 */
    ],
    [
        f64::from_bits(0xbfb341d7961bc000), /* -0x1.341d7961bcp-4 */
        f64::from_bits(0xbd31d09299837610), /* -0x1.1d0929983761p-44 */
    ],
    [
        f64::from_bits(0xbfb253f62f0a0000), /* -0x1.253f62f0ap-4 */
        f64::from_bits(0xbd3416f8fb69a701), /* -0x1.416f8fb69a701p-44 */
    ],
    [
        f64::from_bits(0xbfb16536eea38000), /* -0x1.16536eea38p-4 */
        f64::from_bits(0x3d147c5e768fa309), /* 0x1.47c5e768fa309p-46 */
    ],
    [
        f64::from_bits(0xbfaf0a30c0118000), /* -0x1.f0a30c0118p-5 */
        f64::from_bits(0x3d2d599e83368e91), /* 0x1.d599e83368e91p-45 */
    ],
    [
        f64::from_bits(0xbfad276b8adb0000), /* -0x1.d276b8adbp-5 */
        f64::from_bits(0xbd16a423c78a64b0), /* -0x1.6a423c78a64bp-46 */
    ],
    [
        f64::from_bits(0xbfab42dd71198000), /* -0x1.b42dd71198p-5 */
        f64::from_bits(0x3d1c827ae5d6704c), /* 0x1.c827ae5d6704cp-46 */
    ],
    [
        f64::from_bits(0xbfa95c830ec90000), /* -0x1.95c830ec9p-5 */
        f64::from_bits(0x3d2c148297c5feb8), /* 0x1.c148297c5feb8p-45 */
    ],
    [
        f64::from_bits(0xbfa77458f6330000), /* -0x1.77458f633p-5 */
        f64::from_bits(0x3d3181dce586af09), /* 0x1.181dce586af09p-44 */
    ],
    [
        f64::from_bits(0xbfa58a5bafc90000), /* -0x1.58a5bafc9p-5 */
        f64::from_bits(0x3d2b2b739570ad39), /* 0x1.b2b739570ad39p-45 */
    ],
    [
        f64::from_bits(0xbfa39e87b9fe8000), /* -0x1.39e87b9fe8p-5 */
        f64::from_bits(0xbd3eafd480ad9015), /* -0x1.eafd480ad9015p-44 */
    ],
    [
        f64::from_bits(0xbfa1b0d989240000), /* -0x1.1b0d98924p-5 */
        f64::from_bits(0x3d33401e9ae889bb), /* 0x1.3401e9ae889bbp-44 */
    ],
    [
        f64::from_bits(0xbf9f829b0e780000), /* -0x1.f829b0e78p-6 */
        f64::from_bits(0xbd2980267c7e09e4), /* -0x1.980267c7e09e4p-45 */
    ],
    [
        f64::from_bits(0xbf9b9fc027b00000), /* -0x1.b9fc027bp-6 */
        f64::from_bits(0x3d3b9a010ae6922a), /* 0x1.b9a010ae6922ap-44 */
    ],
    [
        f64::from_bits(0xbf97b91b07d60000), /* -0x1.7b91b07d6p-6 */
        f64::from_bits(0x3d33b955b602ace4), /* 0x1.3b955b602ace4p-44 */
    ],
    [
        f64::from_bits(0xbf93cea443470000), /* -0x1.3cea44347p-6 */
        f64::from_bits(0x3d36a2c432d6a40b), /* 0x1.6a2c432d6a40bp-44 */
    ],
    [
        f64::from_bits(0xbf8fc0a8b0fc0000), /* -0x1.fc0a8b0fcp-7 */
        f64::from_bits(0xbcdf1e7cf6d3a69c), /* -0x1.f1e7cf6d3a69cp-50 */
    ],
    [
        f64::from_bits(0xbf87dc475f820000), /* -0x1.7dc475f82p-7 */
        f64::from_bits(0x3d3eb1245b5da1f5), /* 0x1.eb1245b5da1f5p-44 */
    ],
    [
        f64::from_bits(0xbf7fe02a6b100000), /* -0x1.fe02a6b1p-8 */
        f64::from_bits(0xbd19e23f0dda40e4), /* -0x1.9e23f0dda40e4p-46 */
    ],
    [
        f64::from_bits(0x0000000000000000), /* 0 */
        f64::from_bits(0x0000000000000000), /* 0 */
    ],
    [
        f64::from_bits(0x0000000000000000), /* 0 */
        f64::from_bits(0x0000000000000000), /* 0 */
    ],
    [
        f64::from_bits(0x3f78121214580000), /* 0x1.812121458p-8 */
        f64::from_bits(0x3d1ad50382973f27), /* 0x1.ad50382973f27p-46 */
    ],
    [
        f64::from_bits(0x3f841929f9680000), /* 0x1.41929f968p-7 */
        f64::from_bits(0x3d1977c755d01368), /* 0x1.977c755d01368p-46 */
    ],
    [
        f64::from_bits(0x3f8c317384c80000), /* 0x1.c317384c8p-7 */
        f64::from_bits(0xbd341f33fcefb9fe), /* -0x1.41f33fcefb9fep-44 */
    ],
    [
        f64::from_bits(0x3f9228fb1fea0000), /* 0x1.228fb1feap-6 */
        f64::from_bits(0x3d2713e3284991fe), /* 0x1.713e3284991fep-45 */
    ],
    [
        f64::from_bits(0x3f963d6178690000), /* 0x1.63d617869p-6 */
        f64::from_bits(0x3d07abf389596542), /* 0x1.7abf389596542p-47 */
    ],
    [
        f64::from_bits(0x3f9a55f548c60000), /* 0x1.a55f548c6p-6 */
        f64::from_bits(0xbd2de0709f2d03c9), /* -0x1.de0709f2d03c9p-45 */
    ],
    [
        f64::from_bits(0x3f9e72bf28140000), /* 0x1.e72bf2814p-6 */
        f64::from_bits(0xbd28d75149774d47), /* -0x1.8d75149774d47p-45 */
    ],
    [
        f64::from_bits(0x3fa0415d89e78000), /* 0x1.0415d89e78p-5 */
        f64::from_bits(0xbd3dddc7f461c516), /* -0x1.dddc7f461c516p-44 */
    ],
    [
        f64::from_bits(0x3fa252f32f8d0000), /* 0x1.252f32f8dp-5 */
        f64::from_bits(0x3d283e9ae021b67b), /* 0x1.83e9ae021b67bp-45 */
    ],
    [
        f64::from_bits(0x3fa466aed42e0000), /* 0x1.466aed42ep-5 */
        f64::from_bits(0xbd2c167375bdfd28), /* -0x1.c167375bdfd28p-45 */
    ],
    [
        f64::from_bits(0x3fa67c94f2d48000), /* 0x1.67c94f2d48p-5 */
        f64::from_bits(0x3d3dac20827cca0c), /* 0x1.dac20827cca0cp-44 */
    ],
    [
        f64::from_bits(0x3fa894aa149f8000), /* 0x1.894aa149f8p-5 */
        f64::from_bits(0x3d39a19a8be97661), /* 0x1.9a19a8be97661p-44 */
    ],
    [
        f64::from_bits(0x3faaaef2d0fb0000), /* 0x1.aaef2d0fbp-5 */
        f64::from_bits(0x3d20fc1a353bb42e), /* 0x1.0fc1a353bb42ep-45 */
    ],
    [
        f64::from_bits(0x3fabbcebfc690000), /* 0x1.bbcebfc69p-5 */
        f64::from_bits(0xbd17bf868c317c2a), /* -0x1.7bf868c317c2ap-46 */
    ],
    [
        f64::from_bits(0x3fadda8adc680000), /* 0x1.dda8adc68p-5 */
        f64::from_bits(0xbd21b1ac64d9e42f), /* -0x1.1b1ac64d9e42fp-45 */
    ],
    [
        f64::from_bits(0x3faffa6911ab8000), /* 0x1.ffa6911ab8p-5 */
        f64::from_bits(0x3d23008c98381a8f), /* 0x1.3008c98381a8fp-45 */
    ],
    [
        f64::from_bits(0x3fb10e45b3cb0000), /* 0x1.10e45b3cbp-4 */
        f64::from_bits(0xbd37cf69284a3465), /* -0x1.7cf69284a3465p-44 */
    ],
    [
        f64::from_bits(0x3fb2207b5c784000), /* 0x1.2207b5c784p-4 */
        f64::from_bits(0x3d349d8cfc10c7bf), /* 0x1.49d8cfc10c7bfp-44 */
    ],
    [
        f64::from_bits(0x3fb2aa04a4470000), /* 0x1.2aa04a447p-4 */
        f64::from_bits(0x3d37a48ba8b1cb41), /* 0x1.7a48ba8b1cb41p-44 */
    ],
    [
        f64::from_bits(0x3fb3bdf5a7d20000), /* 0x1.3bdf5a7d2p-4 */
        f64::from_bits(0xbd319bd0ad125895), /* -0x1.19bd0ad125895p-44 */
    ],
    [
        f64::from_bits(0x3fb4d3115d208000), /* 0x1.4d3115d208p-4 */
        f64::from_bits(0xbcf53a2582f4e1ef), /* -0x1.53a2582f4e1efp-48 */
    ],
    [
        f64::from_bits(0x3fb55e10050e0000), /* 0x1.55e10050ep-4 */
        f64::from_bits(0x3d0c1d740c53c72e), /* 0x1.c1d740c53c72ep-47 */
    ],
    [
        f64::from_bits(0x3fb674f089364000), /* 0x1.674f089364p-4 */
        f64::from_bits(0x3d3a79994c9d3302), /* 0x1.a79994c9d3302p-44 */
    ],
    [
        f64::from_bits(0x3fb78d02263d8000), /* 0x1.78d02263d8p-4 */
        f64::from_bits(0x3d069b5794b69fb7), /* 0x1.69b5794b69fb7p-47 */
    ],
    [
        f64::from_bits(0x3fb8197e2f410000), /* 0x1.8197e2f41p-4 */
        f64::from_bits(0xbd3c0fe460d20041), /* -0x1.c0fe460d20041p-44 */
    ],
    [
        f64::from_bits(0x3fb9335e5d594000), /* 0x1.9335e5d594p-4 */
        f64::from_bits(0x3d23115c3abd47da), /* 0x1.3115c3abd47dap-45 */
    ],
    [
        f64::from_bits(0x3fba4e7640b1c000), /* 0x1.a4e7640b1cp-4 */
        f64::from_bits(0xbd0e42b6b94407c8), /* -0x1.e42b6b94407c8p-47 */
    ],
    [
        f64::from_bits(0x3fbadc77ee5b0000), /* 0x1.adc77ee5bp-4 */
        f64::from_bits(0xbd3573b209c31904), /* -0x1.573b209c31904p-44 */
    ],
    [
        f64::from_bits(0x3fbbf968769fc000), /* 0x1.bf968769fcp-4 */
        f64::from_bits(0x3d24218c8d824283), /* 0x1.4218c8d824283p-45 */
    ],
    [
        f64::from_bits(0x3fbd179788218000), /* 0x1.d179788218p-4 */
        f64::from_bits(0x3d336433b5efbeed), /* 0x1.36433b5efbeedp-44 */
    ],
    [
        f64::from_bits(0x3fbda72763844000), /* 0x1.da72763844p-4 */
        f64::from_bits(0x3d1a89401fa71733), /* 0x1.a89401fa71733p-46 */
    ],
    [
        f64::from_bits(0x3fbec739830a0000), /* 0x1.ec739830ap-4 */
        f64::from_bits(0x3d311fcba80cdd10), /* 0x1.11fcba80cdd1p-44 */
    ],
    [
        f64::from_bits(0x3fbf57bc7d900000), /* 0x1.f57bc7d9p-4 */
        f64::from_bits(0x3d176a6c9ea8b04e), /* 0x1.76a6c9ea8b04ep-46 */
    ],
    [
        f64::from_bits(0x3fc03cdc0a51e000), /* 0x1.03cdc0a51ep-3 */
        f64::from_bits(0x3d381a9cf169fc5c), /* 0x1.81a9cf169fc5cp-44 */
    ],
    [
        f64::from_bits(0x3fc08598b59e4000), /* 0x1.08598b59e4p-3 */
        f64::from_bits(0xbd27e5dd7009902c), /* -0x1.7e5dd7009902cp-45 */
    ],
    [
        f64::from_bits(0x3fc1178e8227e000), /* 0x1.1178e8227ep-3 */
        f64::from_bits(0x3d21ef78ce2d07f2), /* 0x1.1ef78ce2d07f2p-45 */
    ],
    [
        f64::from_bits(0x3fc160c8024b2000), /* 0x1.160c8024b2p-3 */
        f64::from_bits(0x3d2ec2d2a9009e3d), /* 0x1.ec2d2a9009e3dp-45 */
    ],
    [
        f64::from_bits(0x3fc1f3b925f26000), /* 0x1.1f3b925f26p-3 */
        f64::from_bits(0xbd15f74e9b083633), /* -0x1.5f74e9b083633p-46 */
    ],
    [
        f64::from_bits(0x3fc23d712a49c000), /* 0x1.23d712a49cp-3 */
        f64::from_bits(0x3d100d238fd3df5c), /* 0x1.00d238fd3df5cp-46 */
    ],
    [
        f64::from_bits(0x3fc2d1610c868000), /* 0x1.2d1610c868p-3 */
        f64::from_bits(0x3d039d6ccb81b4a1), /* 0x1.39d6ccb81b4a1p-47 */
    ],
    [
        f64::from_bits(0x3fc31b994d3a4000), /* 0x1.31b994d3a4p-3 */
        f64::from_bits(0x3d3f098ee3a50810), /* 0x1.f098ee3a5081p-44 */
    ],
    [
        f64::from_bits(0x3fc3b08b67580000), /* 0x1.3b08b6758p-3 */
        f64::from_bits(0xbd3aade8f29320fb), /* -0x1.aade8f29320fbp-44 */
    ],
    [
        f64::from_bits(0x3fc3fb45a5992000), /* 0x1.3fb45a5992p-3 */
        f64::from_bits(0x3d319713c0cae559), /* 0x1.19713c0cae559p-44 */
    ],
    [
        f64::from_bits(0x3fc4913d8333c000), /* 0x1.4913d8333cp-3 */
        f64::from_bits(0xbd353e43558124c4), /* -0x1.53e43558124c4p-44 */
    ],
    [
        f64::from_bits(0x3fc4dc7b897bc000), /* 0x1.4dc7b897bcp-3 */
        f64::from_bits(0x3d0c79b60ae1ff0f), /* 0x1.c79b60ae1ff0fp-47 */
    ],
    [
        f64::from_bits(0x3fc5737cc9018000), /* 0x1.5737cc9018p-3 */
        f64::from_bits(0x3d39baa7a6b887f6), /* 0x1.9baa7a6b887f6p-44 */
    ],
    [
        f64::from_bits(0x3fc5bf406b544000), /* 0x1.5bf406b544p-3 */
        f64::from_bits(0xbd127023eb68981c), /* -0x1.27023eb68981cp-46 */
    ],
    [
        f64::from_bits(0x3fc6574ebe8c2000), /* 0x1.6574ebe8c2p-3 */
        f64::from_bits(0xbd398c1d34f0f462), /* -0x1.98c1d34f0f462p-44 */
    ],
    [
        f64::from_bits(0x3fc6a399dabbe000), /* 0x1.6a399dabbep-3 */
        f64::from_bits(0xbd38f934e66a15a6), /* -0x1.8f934e66a15a6p-44 */
    ],
    [
        f64::from_bits(0x3fc6f0128b756000), /* 0x1.6f0128b756p-3 */
        f64::from_bits(0x3d3577390d31ef0f), /* 0x1.577390d31ef0fp-44 */
    ],
    [
        f64::from_bits(0x3fc7898d85444000), /* 0x1.7898d85444p-3 */
        f64::from_bits(0x3d38e67be3dbaf3f), /* 0x1.8e67be3dbaf3fp-44 */
    ],
    [
        f64::from_bits(0x3fc7d6903caf6000), /* 0x1.7d6903caf6p-3 */
        f64::from_bits(0xbd24c06b17c301d7), /* -0x1.4c06b17c301d7p-45 */
    ],
    [
        f64::from_bits(0x3fc871213750e000), /* 0x1.871213750ep-3 */
        f64::from_bits(0x3d3328eb42f9af75), /* 0x1.328eb42f9af75p-44 */
    ],
    [
        f64::from_bits(0x3fc8beafeb390000), /* 0x1.8beafeb39p-3 */
        f64::from_bits(0xbd073d54aae92cd1), /* -0x1.73d54aae92cd1p-47 */
    ],
    [
        f64::from_bits(0x3fc90c6db9fcc000), /* 0x1.90c6db9fccp-3 */
        f64::from_bits(0xbd1935f57718d7ca), /* -0x1.935f57718d7cap-46 */
    ],
    [
        f64::from_bits(0x3fc9a8778deba000), /* 0x1.9a8778debap-3 */
        f64::from_bits(0x3d3470fa3efec390), /* 0x1.470fa3efec39p-44 */
    ],
    [
        f64::from_bits(0x3fc9f6c40708a000), /* 0x1.9f6c40708ap-3 */
        f64::from_bits(0xbd3337d94bcd3f43), /* -0x1.337d94bcd3f43p-44 */
    ],
    [
        f64::from_bits(0x3fca454082e6a000), /* 0x1.a454082e6ap-3 */
        f64::from_bits(0x3d360a77c81f7171), /* 0x1.60a77c81f7171p-44 */
    ],
    [
        f64::from_bits(0x3fcae2ca6f672000), /* 0x1.ae2ca6f672p-3 */
        f64::from_bits(0x3d37a8d5ae54f550), /* 0x1.7a8d5ae54f55p-44 */
    ],
    [
        f64::from_bits(0x3fcb31d8575bc000), /* 0x1.b31d8575bcp-3 */
        f64::from_bits(0x3d3c794e562a63cb), /* 0x1.c794e562a63cbp-44 */
    ],
    [
        f64::from_bits(0x3fcb811730b82000), /* 0x1.b811730b82p-3 */
        f64::from_bits(0x3d1e90683b9cd768), /* 0x1.e90683b9cd768p-46 */
    ],
    [
        f64::from_bits(0x3fcbd087383be000), /* 0x1.bd087383bep-3 */
        f64::from_bits(0xbd2d4bc4595412b6), /* -0x1.d4bc4595412b6p-45 */
    ],
    [
        f64::from_bits(0x3fcc6ffbc6f00000), /* 0x1.c6ffbc6fp-3 */
        f64::from_bits(0x3d3ee138d3a69d43), /* 0x1.ee138d3a69d43p-44 */
    ],
    [
        f64::from_bits(0x3fccc000c9db4000), /* 0x1.cc000c9db4p-3 */
        f64::from_bits(0xbd1d6d585d57aff9), /* -0x1.d6d585d57aff9p-46 */
    ],
    [
        f64::from_bits(0x3fcd1037f2656000), /* 0x1.d1037f2656p-3 */
        f64::from_bits(0xbd084a7e75b6f6e4), /* -0x1.84a7e75b6f6e4p-47 */
    ],
    [
        f64::from_bits(0x3fcdb13db0d48000), /* 0x1.db13db0d48p-3 */
        f64::from_bits(0x3d32806a847527e6), /* 0x1.2806a847527e6p-44 */
    ],
    [
        f64::from_bits(0x3fce020cc6236000), /* 0x1.e020cc6236p-3 */
        f64::from_bits(0xbd252b00adb91424), /* -0x1.52b00adb91424p-45 */
    ],
    [
        f64::from_bits(0x3fce530effe72000), /* 0x1.e530effe72p-3 */
        f64::from_bits(0xbd3fdbdbb13f7c18), /* -0x1.fdbdbb13f7c18p-44 */
    ],
    [
        f64::from_bits(0x3fcea4449f04a000), /* 0x1.ea4449f04ap-3 */
        f64::from_bits(0x3d35e91663732a36), /* 0x1.5e91663732a36p-44 */
    ],
    [
        f64::from_bits(0x3fcf474b134e0000), /* 0x1.f474b134ep-3 */
        f64::from_bits(0xbd3bae49f1df7b5e), /* -0x1.bae49f1df7b5ep-44 */
    ],
    [
        f64::from_bits(0x3fcf991c6cb3c000), /* 0x1.f991c6cb3cp-3 */
        f64::from_bits(0xbd390d04cd7cc834), /* -0x1.90d04cd7cc834p-44 */
    ],
    [
        f64::from_bits(0x3fcfeb2233ea0000), /* 0x1.feb2233eap-3 */
        f64::from_bits(0x3d2f3418de00938b), /* 0x1.f3418de00938bp-45 */
    ],
    [
        f64::from_bits(0x3fd01eae5626c000), /* 0x1.01eae5626cp-2 */
        f64::from_bits(0x3d3a43dcfade85ae), /* 0x1.a43dcfade85aep-44 */
    ],
    [
        f64::from_bits(0x3fd047e60cde8000), /* 0x1.047e60cde8p-2 */
        f64::from_bits(0x3d2dbdf10d397f3c), /* 0x1.dbdf10d397f3cp-45 */
    ],
    [
        f64::from_bits(0x3fd09aa572e6c000), /* 0x1.09aa572e6cp-2 */
        f64::from_bits(0x3d3b50a1e1734342), /* 0x1.b50a1e1734342p-44 */
    ],
    [
        f64::from_bits(0x3fd0c42d67616000), /* 0x1.0c42d67616p-2 */
        f64::from_bits(0x3d27188b163ceae9), /* 0x1.7188b163ceae9p-45 */
    ],
    [
        f64::from_bits(0x3fd0edd060b78000), /* 0x1.0edd060b78p-2 */
        f64::from_bits(0x3d0019b52d8435f5), /* 0x1.019b52d8435f5p-47 */
    ],
    [
        f64::from_bits(0x3fd1178e8227e000), /* 0x1.1178e8227ep-2 */
        f64::from_bits(0x3d31ef78ce2d07f2), /* 0x1.1ef78ce2d07f2p-44 */
    ],
    [
        f64::from_bits(0x3fd14167ef367000), /* 0x1.14167ef367p-2 */
        f64::from_bits(0x3d3e0c07824daaf5), /* 0x1.e0c07824daaf5p-44 */
    ],
    [
        f64::from_bits(0x3fd16b5ccbad0000), /* 0x1.16b5ccbadp-2 */
        f64::from_bits(0xbd323299042d74bf), /* -0x1.23299042d74bfp-44 */
    ],
    [
        f64::from_bits(0x3fd1bf99635a7000), /* 0x1.1bf99635a7p-2 */
        f64::from_bits(0xbd31ac89575c2125), /* -0x1.1ac89575c2125p-44 */
    ],
    [
        f64::from_bits(0x3fd1e9e16788a000), /* 0x1.1e9e16788ap-2 */
        f64::from_bits(0xbd382eaed3c8b65e), /* -0x1.82eaed3c8b65ep-44 */
    ],
    [
        f64::from_bits(0x3fd214456d0ec000), /* 0x1.214456d0ecp-2 */
        f64::from_bits(0xbd3caf0428b728a3), /* -0x1.caf0428b728a3p-44 */
    ],
    [
        f64::from_bits(0x3fd23ec5991ec000), /* 0x1.23ec5991ecp-2 */
        f64::from_bits(0xbd36dbe448a2e522), /* -0x1.6dbe448a2e522p-44 */
    ],
    [
        f64::from_bits(0x3fd269621134e000), /* 0x1.269621134ep-2 */
        f64::from_bits(0xbd31b61f10522625), /* -0x1.1b61f10522625p-44 */
    ],
    [
        f64::from_bits(0x3fd2941afb187000), /* 0x1.2941afb187p-2 */
        f64::from_bits(0xbd3210c2b730e28b), /* -0x1.210c2b730e28bp-44 */
    ],
    [
        f64::from_bits(0x3fd2bef07cdc9000), /* 0x1.2bef07cdc9p-2 */
        f64::from_bits(0x3d2a9cfa4a5004f4), /* 0x1.a9cfa4a5004f4p-45 */
    ],
    [
        f64::from_bits(0x3fd314f1e1d36000), /* 0x1.314f1e1d36p-2 */
        f64::from_bits(0xbd28e27ad3213cb8), /* -0x1.8e27ad3213cb8p-45 */
    ],
    [
        f64::from_bits(0x3fd3401e12aed000), /* 0x1.3401e12aedp-2 */
        f64::from_bits(0xbd317c73556e291d), /* -0x1.17c73556e291dp-44 */
    ],
    [
        f64::from_bits(0x3fd36b6776be1000), /* 0x1.36b6776be1p-2 */
        f64::from_bits(0x3d116ecdb0f177c8), /* 0x1.16ecdb0f177c8p-46 */
    ],
    [
        f64::from_bits(0x3fd396ce359bc000), /* 0x1.396ce359bcp-2 */
        f64::from_bits(0xbd05839c5663663d), /* -0x1.5839c5663663dp-47 */
    ],
    [
        f64::from_bits(0x3fd3c25277333000), /* 0x1.3c25277333p-2 */
        f64::from_bits(0x3d183b54b606bd5c), /* 0x1.83b54b606bd5cp-46 */
    ],
    [
        f64::from_bits(0x3fd3edf463c17000), /* 0x1.3edf463c17p-2 */
        f64::from_bits(0xbd3f067c297f2c3f), /* -0x1.f067c297f2c3fp-44 */
    ],
    [
        f64::from_bits(0x3fd419b423d5f000), /* 0x1.419b423d5fp-2 */
        f64::from_bits(0xbd3ce379226de3ec), /* -0x1.ce379226de3ecp-44 */
    ],
    [
        f64::from_bits(0x3fd44591e053a000), /* 0x1.44591e053ap-2 */
        f64::from_bits(0xbd06e95892923d88), /* -0x1.6e95892923d88p-47 */
    ],
    [
        f64::from_bits(0x3fd4718dc271c000), /* 0x1.4718dc271cp-2 */
        f64::from_bits(0x3d306c18fb4c14c5), /* 0x1.06c18fb4c14c5p-44 */
    ],
    [
        f64::from_bits(0x3fd49da7f3bcc000), /* 0x1.49da7f3bccp-2 */
        f64::from_bits(0x3d307b334daf4b9a), /* 0x1.07b334daf4b9ap-44 */
    ],
    [
        f64::from_bits(0x3fd4c9e09e173000), /* 0x1.4c9e09e173p-2 */
        f64::from_bits(0xbd2e20891b0ad8a4), /* -0x1.e20891b0ad8a4p-45 */
    ],
    [
        f64::from_bits(0x3fd4f637ebbaa000), /* 0x1.4f637ebbaap-2 */
        f64::from_bits(0xbd3fc158cb3124b9), /* -0x1.fc158cb3124b9p-44 */
    ],
    [
        f64::from_bits(0x3fd522ae0738a000), /* 0x1.522ae0738ap-2 */
        f64::from_bits(0x3d2ebe708164c759), /* 0x1.ebe708164c759p-45 */
    ],
    [
        f64::from_bits(0x3fd54f431b7be000), /* 0x1.54f431b7bep-2 */
        f64::from_bits(0x3d1a8954c0910952), /* 0x1.a8954c0910952p-46 */
    ],
    [
        f64::from_bits(0x3fd57bf753c8d000), /* 0x1.57bf753c8dp-2 */
        f64::from_bits(0x3d1fadedee5d40ef), /* 0x1.fadedee5d40efp-46 */
    ],
    [
        f64::from_bits(0x3fd5a8cadbbee000), /* 0x1.5a8cadbbeep-2 */
        f64::from_bits(0xbcf7c79b0af7ecf8), /* -0x1.7c79b0af7ecf8p-48 */
    ],
    [
        f64::from_bits(0x3fd5d5bddf596000), /* 0x1.5d5bddf596p-2 */
        f64::from_bits(0xbd0a0b2a08a465dc), /* -0x1.a0b2a08a465dcp-47 */
    ],
    [
        f64::from_bits(0x3fd602d08af09000), /* 0x1.602d08af09p-2 */
        f64::from_bits(0x3d1ebe9176df3f65), /* 0x1.ebe9176df3f65p-46 */
    ],
    [
        f64::from_bits(0x3fd630030b3ab000), /* 0x1.630030b3abp-2 */
        f64::from_bits(0xbd2db623e731ae00), /* -0x1.db623e731aep-45 */
    ],
];

pub(crate) static T1: [[f64; 2]; 64] = [
    [
        f64::from_bits(0x3ff0000000000000), /* 0x1p+0 */
        f64::from_bits(0x0000000000000000), /* 0x0p+0 */
    ],
    [
        f64::from_bits(0x3ff02c9a3e778061), /* 0x1.02c9a3e778061p+0 */
        f64::from_bits(0xbc719083535b085d), /* -0x1.19083535b085dp-56 */
    ],
    [
        f64::from_bits(0x3ff059b0d3158574), /* 0x1.059b0d3158574p+0 */
        f64::from_bits(0x3c8d73e2a475b465), /* 0x1.d73e2a475b465p-55 */
    ],
    [
        f64::from_bits(0x3ff0874518759bc8), /* 0x1.0874518759bc8p+0 */
        f64::from_bits(0x3c6186be4bb284ff), /* 0x1.186be4bb284ffp-57 */
    ],
    [
        f64::from_bits(0x3ff0b5586cf9890f), /* 0x1.0b5586cf9890fp+0 */
        f64::from_bits(0x3c98a62e4adc610b), /* 0x1.8a62e4adc610bp-54 */
    ],
    [
        f64::from_bits(0x3ff0e3ec32d3d1a2), /* 0x1.0e3ec32d3d1a2p+0 */
        f64::from_bits(0x3c403a1727c57b53), /* 0x1.03a1727c57b53p-59 */
    ],
    [
        f64::from_bits(0x3ff11301d0125b51), /* 0x1.11301d0125b51p+0 */
        f64::from_bits(0xbc96c51039449b3a), /* -0x1.6c51039449b3ap-54 */
    ],
    [
        f64::from_bits(0x3ff1429aaea92de0), /* 0x1.1429aaea92dep+0 */
        f64::from_bits(0xbc932fbf9af1369e), /* -0x1.32fbf9af1369ep-54 */
    ],
    [
        f64::from_bits(0x3ff172b83c7d517b), /* 0x1.172b83c7d517bp+0 */
        f64::from_bits(0xbc819041b9d78a76), /* -0x1.19041b9d78a76p-55 */
    ],
    [
        f64::from_bits(0x3ff1a35beb6fcb75), /* 0x1.1a35beb6fcb75p+0 */
        f64::from_bits(0x3c8e5b4c7b4968e4), /* 0x1.e5b4c7b4968e4p-55 */
    ],
    [
        f64::from_bits(0x3ff1d4873168b9aa), /* 0x1.1d4873168b9aap+0 */
        f64::from_bits(0x3c9e016e00a2643c), /* 0x1.e016e00a2643cp-54 */
    ],
    [
        f64::from_bits(0x3ff2063b88628cd6), /* 0x1.2063b88628cd6p+0 */
        f64::from_bits(0x3c8dc775814a8495), /* 0x1.dc775814a8495p-55 */
    ],
    [
        f64::from_bits(0x3ff2387a6e756238), /* 0x1.2387a6e756238p+0 */
        f64::from_bits(0x3c99b07eb6c70573), /* 0x1.9b07eb6c70573p-54 */
    ],
    [
        f64::from_bits(0x3ff26b4565e27cdd), /* 0x1.26b4565e27cddp+0 */
        f64::from_bits(0x3c82bd339940e9d9), /* 0x1.2bd339940e9d9p-55 */
    ],
    [
        f64::from_bits(0x3ff29e9df51fdee1), /* 0x1.29e9df51fdee1p+0 */
        f64::from_bits(0x3c8612e8afad1255), /* 0x1.612e8afad1255p-55 */
    ],
    [
        f64::from_bits(0x3ff2d285a6e4030b), /* 0x1.2d285a6e4030bp+0 */
        f64::from_bits(0x3c90024754db41d5), /* 0x1.0024754db41d5p-54 */
    ],
    [
        f64::from_bits(0x3ff306fe0a31b715), /* 0x1.306fe0a31b715p+0 */
        f64::from_bits(0x3c86f46ad23182e4), /* 0x1.6f46ad23182e4p-55 */
    ],
    [
        f64::from_bits(0x3ff33c08b26416ff), /* 0x1.33c08b26416ffp+0 */
        f64::from_bits(0x3c932721843659a6), /* 0x1.32721843659a6p-54 */
    ],
    [
        f64::from_bits(0x3ff371a7373aa9cb), /* 0x1.371a7373aa9cbp+0 */
        f64::from_bits(0xbc963aeabf42eae2), /* -0x1.63aeabf42eae2p-54 */
    ],
    [
        f64::from_bits(0x3ff3a7db34e59ff7), /* 0x1.3a7db34e59ff7p+0 */
        f64::from_bits(0xbc75e436d661f5e3), /* -0x1.5e436d661f5e3p-56 */
    ],
    [
        f64::from_bits(0x3ff3dea64c123422), /* 0x1.3dea64c123422p+0 */
        f64::from_bits(0x3c8ada0911f09ebc), /* 0x1.ada0911f09ebcp-55 */
    ],
    [
        f64::from_bits(0x3ff4160a21f72e2a), /* 0x1.4160a21f72e2ap+0 */
        f64::from_bits(0xbc5ef3691c309278), /* -0x1.ef3691c309278p-58 */
    ],
    [
        f64::from_bits(0x3ff44e086061892d), /* 0x1.44e086061892dp+0 */
        f64::from_bits(0x3c489b7a04ef80d0), /* 0x1.89b7a04ef80dp-59 */
    ],
    [
        f64::from_bits(0x3ff486a2b5c13cd0), /* 0x1.486a2b5c13cdp+0 */
        f64::from_bits(0x3c73c1a3b69062f0), /* 0x1.3c1a3b69062fp-56 */
    ],
    [
        f64::from_bits(0x3ff4bfdad5362a27), /* 0x1.4bfdad5362a27p+0 */
        f64::from_bits(0x3c7d4397afec42e2), /* 0x1.d4397afec42e2p-56 */
    ],
    [
        f64::from_bits(0x3ff4f9b2769d2ca7), /* 0x1.4f9b2769d2ca7p+0 */
        f64::from_bits(0xbc94b309d25957e3), /* -0x1.4b309d25957e3p-54 */
    ],
    [
        f64::from_bits(0x3ff5342b569d4f82), /* 0x1.5342b569d4f82p+0 */
        f64::from_bits(0xbc807abe1db13cad), /* -0x1.07abe1db13cadp-55 */
    ],
    [
        f64::from_bits(0x3ff56f4736b527da), /* 0x1.56f4736b527dap+0 */
        f64::from_bits(0x3c99bb2c011d93ad), /* 0x1.9bb2c011d93adp-54 */
    ],
    [
        f64::from_bits(0x3ff5ab07dd485429), /* 0x1.5ab07dd485429p+0 */
        f64::from_bits(0x3c96324c054647ad), /* 0x1.6324c054647adp-54 */
    ],
    [
        f64::from_bits(0x3ff5e76f15ad2148), /* 0x1.5e76f15ad2148p+0 */
        f64::from_bits(0x3c9ba6f93080e65e), /* 0x1.ba6f93080e65ep-54 */
    ],
    [
        f64::from_bits(0x3ff6247eb03a5585), /* 0x1.6247eb03a5585p+0 */
        f64::from_bits(0xbc9383c17e40b497), /* -0x1.383c17e40b497p-54 */
    ],
    [
        f64::from_bits(0x3ff6623882552225), /* 0x1.6623882552225p+0 */
        f64::from_bits(0xbc9bb60987591c34), /* -0x1.bb60987591c34p-54 */
    ],
    [
        f64::from_bits(0x3ff6a09e667f3bcd), /* 0x1.6a09e667f3bcdp+0 */
        f64::from_bits(0xbc9bdd3413b26456), /* -0x1.bdd3413b26456p-54 */
    ],
    [
        f64::from_bits(0x3ff6dfb23c651a2f), /* 0x1.6dfb23c651a2fp+0 */
        f64::from_bits(0xbc6bbe3a683c88ab), /* -0x1.bbe3a683c88abp-57 */
    ],
    [
        f64::from_bits(0x3ff71f75e8ec5f74), /* 0x1.71f75e8ec5f74p+0 */
        f64::from_bits(0xbc816e4786887a99), /* -0x1.16e4786887a99p-55 */
    ],
    [
        f64::from_bits(0x3ff75feb564267c9), /* 0x1.75feb564267c9p+0 */
        f64::from_bits(0xbc90245957316dd3), /* -0x1.0245957316dd3p-54 */
    ],
    [
        f64::from_bits(0x3ff7a11473eb0187), /* 0x1.7a11473eb0187p+0 */
        f64::from_bits(0xbc841577ee04992f), /* -0x1.41577ee04992fp-55 */
    ],
    [
        f64::from_bits(0x3ff7e2f336cf4e62), /* 0x1.7e2f336cf4e62p+0 */
        f64::from_bits(0x3c705d02ba15797e), /* 0x1.05d02ba15797ep-56 */
    ],
    [
        f64::from_bits(0x3ff82589994cce13), /* 0x1.82589994cce13p+0 */
        f64::from_bits(0xbc9d4c1dd41532d8), /* -0x1.d4c1dd41532d8p-54 */
    ],
    [
        f64::from_bits(0x3ff868d99b4492ed), /* 0x1.868d99b4492edp+0 */
        f64::from_bits(0xbc9fc6f89bd4f6ba), /* -0x1.fc6f89bd4f6bap-54 */
    ],
    [
        f64::from_bits(0x3ff8ace5422aa0db), /* 0x1.8ace5422aa0dbp+0 */
        f64::from_bits(0x3c96e9f156864b27), /* 0x1.6e9f156864b27p-54 */
    ],
    [
        f64::from_bits(0x3ff8f1ae99157736), /* 0x1.8f1ae99157736p+0 */
        f64::from_bits(0x3c85cc13a2e3976c), /* 0x1.5cc13a2e3976cp-55 */
    ],
    [
        f64::from_bits(0x3ff93737b0cdc5e5), /* 0x1.93737b0cdc5e5p+0 */
        f64::from_bits(0xbc675fc781b57ebc), /* -0x1.75fc781b57ebcp-57 */
    ],
    [
        f64::from_bits(0x3ff97d829fde4e50), /* 0x1.97d829fde4e5p+0 */
        f64::from_bits(0xbc9d185b7c1b85d1), /* -0x1.d185b7c1b85d1p-54 */
    ],
    [
        f64::from_bits(0x3ff9c49182a3f090), /* 0x1.9c49182a3f09p+0 */
        f64::from_bits(0x3c7c7c46b071f2be), /* 0x1.c7c46b071f2bep-56 */
    ],
    [
        f64::from_bits(0x3ffa0c667b5de565), /* 0x1.a0c667b5de565p+0 */
        f64::from_bits(0xbc9359495d1cd533), /* -0x1.359495d1cd533p-54 */
    ],
    [
        f64::from_bits(0x3ffa5503b23e255d), /* 0x1.a5503b23e255dp+0 */
        f64::from_bits(0xbc9d2f6edb8d41e1), /* -0x1.d2f6edb8d41e1p-54 */
    ],
    [
        f64::from_bits(0x3ffa9e6b5579fdbf), /* 0x1.a9e6b5579fdbfp+0 */
        f64::from_bits(0x3c90fac90ef7fd31), /* 0x1.0fac90ef7fd31p-54 */
    ],
    [
        f64::from_bits(0x3ffae89f995ad3ad), /* 0x1.ae89f995ad3adp+0 */
        f64::from_bits(0x3c97a1cd345dcc81), /* 0x1.7a1cd345dcc81p-54 */
    ],
    [
        f64::from_bits(0x3ffb33a2b84f15fb), /* 0x1.b33a2b84f15fbp+0 */
        f64::from_bits(0xbc62805e3084d708), /* -0x1.2805e3084d708p-57 */
    ],
    [
        f64::from_bits(0x3ffb7f76f2fb5e47), /* 0x1.b7f76f2fb5e47p+0 */
        f64::from_bits(0xbc75584f7e54ac3b), /* -0x1.5584f7e54ac3bp-56 */
    ],
    [
        f64::from_bits(0x3ffbcc1e904bc1d2), /* 0x1.bcc1e904bc1d2p+0 */
        f64::from_bits(0x3c823dd07a2d9e84), /* 0x1.23dd07a2d9e84p-55 */
    ],
    [
        f64::from_bits(0x3ffc199bdd85529c), /* 0x1.c199bdd85529cp+0 */
        f64::from_bits(0x3c811065895048dd), /* 0x1.11065895048ddp-55 */
    ],
    [
        f64::from_bits(0x3ffc67f12e57d14b), /* 0x1.c67f12e57d14bp+0 */
        f64::from_bits(0x3c92884dff483cad), /* 0x1.2884dff483cadp-54 */
    ],
    [
        f64::from_bits(0x3ffcb720dcef9069), /* 0x1.cb720dcef9069p+0 */
        f64::from_bits(0x3c7503cbd1e949db), /* 0x1.503cbd1e949dbp-56 */
    ],
    [
        f64::from_bits(0x3ffd072d4a07897c), /* 0x1.d072d4a07897cp+0 */
        f64::from_bits(0xbc9cbc3743797a9c), /* -0x1.cbc3743797a9cp-54 */
    ],
    [
        f64::from_bits(0x3ffd5818dcfba487), /* 0x1.d5818dcfba487p+0 */
        f64::from_bits(0x3c82ed02d75b3707), /* 0x1.2ed02d75b3707p-55 */
    ],
    [
        f64::from_bits(0x3ffda9e603db3285), /* 0x1.da9e603db3285p+0 */
        f64::from_bits(0x3c9c2300696db532), /* 0x1.c2300696db532p-54 */
    ],
    [
        f64::from_bits(0x3ffdfc97337b9b5f), /* 0x1.dfc97337b9b5fp+0 */
        f64::from_bits(0xbc91a5cd4f184b5c), /* -0x1.1a5cd4f184b5cp-54 */
    ],
    [
        f64::from_bits(0x3ffe502ee78b3ff6), /* 0x1.e502ee78b3ff6p+0 */
        f64::from_bits(0x3c839e8980a9cc8f), /* 0x1.39e8980a9cc8fp-55 */
    ],
    [
        f64::from_bits(0x3ffea4afa2a490da), /* 0x1.ea4afa2a490dap+0 */
        f64::from_bits(0xbc9e9c23179c2893), /* -0x1.e9c23179c2893p-54 */
    ],
    [
        f64::from_bits(0x3ffefa1bee615a27), /* 0x1.efa1bee615a27p+0 */
        f64::from_bits(0x3c9dc7f486a4b6b0), /* 0x1.dc7f486a4b6bp-54 */
    ],
    [
        f64::from_bits(0x3fff50765b6e4540), /* 0x1.f50765b6e454p+0 */
        f64::from_bits(0x3c99d3e12dd8a18b), /* 0x1.9d3e12dd8a18bp-54 */
    ],
    [
        f64::from_bits(0x3fffa7c1819e90d8), /* 0x1.fa7c1819e90d8p+0 */
        f64::from_bits(0x3c874853f3a5931e), /* 0x1.74853f3a5931ep-55 */
    ],
];

pub(crate) static T2: [[f64; 2]; 64] = [
    [
        f64::from_bits(0x3ff0000000000000), /* 0x1p+0 */
        f64::from_bits(0x0000000000000000), /* 0x0p+0 */
    ],
    [
        f64::from_bits(0x3ff000b175effdc7), /* 0x1.000b175effdc7p+0 */
        f64::from_bits(0x3c9ae8e38c59c72a), /* 0x1.ae8e38c59c72ap-54 */
    ],
    [
        f64::from_bits(0x3ff00162f3904052), /* 0x1.00162f3904052p+0 */
        f64::from_bits(0xbc57b5d0d58ea8f4), /* -0x1.7b5d0d58ea8f4p-58 */
    ],
    [
        f64::from_bits(0x3ff0021478e11ce6), /* 0x1.0021478e11ce6p+0 */
        f64::from_bits(0x3c94115cb6b16a8e), /* 0x1.4115cb6b16a8ep-54 */
    ],
    [
        f64::from_bits(0x3ff002c605e2e8cf), /* 0x1.002c605e2e8cfp+0 */
        f64::from_bits(0xbc8d7c96f201bb2f), /* -0x1.d7c96f201bb2fp-55 */
    ],
    [
        f64::from_bits(0x3ff003779a95f959), /* 0x1.003779a95f959p+0 */
        f64::from_bits(0x3c984711d4c35e9f), /* 0x1.84711d4c35e9fp-54 */
    ],
    [
        f64::from_bits(0x3ff0042936faa3d8), /* 0x1.0042936faa3d8p+0 */
        f64::from_bits(0xbc80484245243777), /* -0x1.0484245243777p-55 */
    ],
    [
        f64::from_bits(0x3ff004dadb113da0), /* 0x1.004dadb113dap+0 */
        f64::from_bits(0xbc94b237da2025f9), /* -0x1.4b237da2025f9p-54 */
    ],
    [
        f64::from_bits(0x3ff0058c86da1c0a), /* 0x1.0058c86da1c0ap+0 */
        f64::from_bits(0xbc75e00e62d6b30d), /* -0x1.5e00e62d6b30dp-56 */
    ],
    [
        f64::from_bits(0x3ff0063e3a559473), /* 0x1.0063e3a559473p+0 */
        f64::from_bits(0x3c9a1d6cedbb9481), /* 0x1.a1d6cedbb9481p-54 */
    ],
    [
        f64::from_bits(0x3ff006eff583fc3d), /* 0x1.006eff583fc3dp+0 */
        f64::from_bits(0xbc94acf197a00142), /* -0x1.4acf197a00142p-54 */
    ],
    [
        f64::from_bits(0x3ff007a1b865a8ca), /* 0x1.007a1b865a8cap+0 */
        f64::from_bits(0xbc6eaf2ea42391a5), /* -0x1.eaf2ea42391a5p-57 */
    ],
    [
        f64::from_bits(0x3ff0085382faef83), /* 0x1.0085382faef83p+0 */
        f64::from_bits(0x3c7da93f90835f75), /* 0x1.da93f90835f75p-56 */
    ],
    [
        f64::from_bits(0x3ff00905554425d4), /* 0x1.00905554425d4p+0 */
        f64::from_bits(0xbc86a79084ab093c), /* -0x1.6a79084ab093cp-55 */
    ],
    [
        f64::from_bits(0x3ff009b72f41a12b), /* 0x1.009b72f41a12bp+0 */
        f64::from_bits(0x3c986364f8fbe8f8), /* 0x1.86364f8fbe8f8p-54 */
    ],
    [
        f64::from_bits(0x3ff00a6910f3b6fd), /* 0x1.00a6910f3b6fdp+0 */
        f64::from_bits(0xbc882e8e14e3110e), /* -0x1.82e8e14e3110ep-55 */
    ],
    [
        f64::from_bits(0x3ff00b1afa5abcbf), /* 0x1.00b1afa5abcbfp+0 */
        f64::from_bits(0xbc84f6b2a7609f71), /* -0x1.4f6b2a7609f71p-55 */
    ],
    [
        f64::from_bits(0x3ff00bcceb7707ec), /* 0x1.00bcceb7707ecp+0 */
        f64::from_bits(0xbc7e1a258ea8f71b), /* -0x1.e1a258ea8f71bp-56 */
    ],
    [
        f64::from_bits(0x3ff00c7ee448ee02), /* 0x1.00c7ee448ee02p+0 */
        f64::from_bits(0x3c74362ca5bc26f1), /* 0x1.4362ca5bc26f1p-56 */
    ],
    [
        f64::from_bits(0x3ff00d30e4d0c483), /* 0x1.00d30e4d0c483p+0 */
        f64::from_bits(0x3c9095a56c919d02), /* 0x1.095a56c919d02p-54 */
    ],
    [
        f64::from_bits(0x3ff00de2ed0ee0f5), /* 0x1.00de2ed0ee0f5p+0 */
        f64::from_bits(0xbc6406ac4e81a645), /* -0x1.406ac4e81a645p-57 */
    ],
    [
        f64::from_bits(0x3ff00e94fd0398e0), /* 0x1.00e94fd0398ep+0 */
        f64::from_bits(0x3c9b5a6902767e09), /* 0x1.b5a6902767e09p-54 */
    ],
    [
        f64::from_bits(0x3ff00f4714af41d3), /* 0x1.00f4714af41d3p+0 */
        f64::from_bits(0xbc991b2060859321), /* -0x1.91b2060859321p-54 */
    ],
    [
        f64::from_bits(0x3ff00ff93412315c), /* 0x1.00ff93412315cp+0 */
        f64::from_bits(0x3c8427068ab22306), /* 0x1.427068ab22306p-55 */
    ],
    [
        f64::from_bits(0x3ff010ab5b2cbd11), /* 0x1.010ab5b2cbd11p+0 */
        f64::from_bits(0x3c9c1d0660524e08), /* 0x1.c1d0660524e08p-54 */
    ],
    [
        f64::from_bits(0x3ff0115d89ff3a8b), /* 0x1.0115d89ff3a8bp+0 */
        f64::from_bits(0xbc9e7bdfb3204be8), /* -0x1.e7bdfb3204be8p-54 */
    ],
    [
        f64::from_bits(0x3ff0120fc089ff63), /* 0x1.0120fc089ff63p+0 */
        f64::from_bits(0x3c8843aa8b9cbbc6), /* 0x1.843aa8b9cbbc6p-55 */
    ],
    [
        f64::from_bits(0x3ff012c1fecd613b), /* 0x1.012c1fecd613bp+0 */
        f64::from_bits(0xbc734104ee7edae9), /* -0x1.34104ee7edae9p-56 */
    ],
    [
        f64::from_bits(0x3ff0137444c9b5b5), /* 0x1.0137444c9b5b5p+0 */
        f64::from_bits(0xbc72b6aeb6176892), /* -0x1.2b6aeb6176892p-56 */
    ],
    [
        f64::from_bits(0x3ff01426927f5278), /* 0x1.01426927f5278p+0 */
        f64::from_bits(0x3c7a8cd33b8a1bb3), /* 0x1.a8cd33b8a1bb3p-56 */
    ],
    [
        f64::from_bits(0x3ff014d8e7ee8d2f), /* 0x1.014d8e7ee8d2fp+0 */
        f64::from_bits(0x3c72edc08e5da99a), /* 0x1.2edc08e5da99ap-56 */
    ],
    [
        f64::from_bits(0x3ff0158b4517bb88), /* 0x1.0158b4517bb88p+0 */
        f64::from_bits(0x3c857ba2dc7e0c73), /* 0x1.57ba2dc7e0c73p-55 */
    ],
    [
        f64::from_bits(0x3ff0163da9fb3335), /* 0x1.0163da9fb3335p+0 */
        f64::from_bits(0x3c9b61299ab8cdb7), /* 0x1.b61299ab8cdb7p-54 */
    ],
    [
        f64::from_bits(0x3ff016f0169949ed), /* 0x1.016f0169949edp+0 */
        f64::from_bits(0xbc990565902c5f44), /* -0x1.90565902c5f44p-54 */
    ],
    [
        f64::from_bits(0x3ff017a28af25567), /* 0x1.017a28af25567p+0 */
        f64::from_bits(0x3c870fc41c5c2d53), /* 0x1.70fc41c5c2d53p-55 */
    ],
    [
        f64::from_bits(0x3ff018550706ab62), /* 0x1.018550706ab62p+0 */
        f64::from_bits(0x3c94b9a6e145d76c), /* 0x1.4b9a6e145d76cp-54 */
    ],
    [
        f64::from_bits(0x3ff019078ad6a19f), /* 0x1.019078ad6a19fp+0 */
        f64::from_bits(0xbc7008eff5142bf9), /* -0x1.008eff5142bf9p-56 */
    ],
    [
        f64::from_bits(0x3ff019ba16628de2), /* 0x1.019ba16628de2p+0 */
        f64::from_bits(0xbc977669f033c7de), /* -0x1.77669f033c7dep-54 */
    ],
    [
        f64::from_bits(0x3ff01a6ca9aac5f3), /* 0x1.01a6ca9aac5f3p+0 */
        f64::from_bits(0xbc909bb78eeead0a), /* -0x1.09bb78eeead0ap-54 */
    ],
    [
        f64::from_bits(0x3ff01b1f44af9f9e), /* 0x1.01b1f44af9f9ep+0 */
        f64::from_bits(0x3c9371231477ece5), /* 0x1.371231477ece5p-54 */
    ],
    [
        f64::from_bits(0x3ff01bd1e77170b4), /* 0x1.01bd1e77170b4p+0 */
        f64::from_bits(0x3c75e7626621eb5b), /* 0x1.5e7626621eb5bp-56 */
    ],
    [
        f64::from_bits(0x3ff01c8491f08f08), /* 0x1.01c8491f08f08p+0 */
        f64::from_bits(0xbc9bc72b100828a5), /* -0x1.bc72b100828a5p-54 */
    ],
    [
        f64::from_bits(0x3ff01d37442d5070), /* 0x1.01d37442d507p+0 */
        f64::from_bits(0xbc6ce39cbbab8bbe), /* -0x1.ce39cbbab8bbep-57 */
    ],
    [
        f64::from_bits(0x3ff01de9fe280ac8), /* 0x1.01de9fe280ac8p+0 */
        f64::from_bits(0x3c816996709da2e2), /* 0x1.16996709da2e2p-55 */
    ],
    [
        f64::from_bits(0x3ff01e9cbfe113ef), /* 0x1.01e9cbfe113efp+0 */
        f64::from_bits(0xbc8c11f5239bf535), /* -0x1.c11f5239bf535p-55 */
    ],
    [
        f64::from_bits(0x3ff01f4f8958c1c6), /* 0x1.01f4f8958c1c6p+0 */
        f64::from_bits(0x3c8e1d4eb5edc6b3), /* 0x1.e1d4eb5edc6b3p-55 */
    ],
    [
        f64::from_bits(0x3ff020025a8f6a35), /* 0x1.020025a8f6a35p+0 */
        f64::from_bits(0xbc9afb99946ee3f0), /* -0x1.afb99946ee3fp-54 */
    ],
    [
        f64::from_bits(0x3ff020b533856324), /* 0x1.020b533856324p+0 */
        f64::from_bits(0xbc98f06d8a148a32), /* -0x1.8f06d8a148a32p-54 */
    ],
    [
        f64::from_bits(0x3ff02168143b0281), /* 0x1.02168143b0281p+0 */
        f64::from_bits(0xbc82bf310fc54eb6), /* -0x1.2bf310fc54eb6p-55 */
    ],
    [
        f64::from_bits(0x3ff0221afcb09e3e), /* 0x1.0221afcb09e3ep+0 */
        f64::from_bits(0xbc9c95a035eb4175), /* -0x1.c95a035eb4175p-54 */
    ],
    [
        f64::from_bits(0x3ff022cdece68c4f), /* 0x1.022cdece68c4fp+0 */
        f64::from_bits(0xbc9491793e46834d), /* -0x1.491793e46834dp-54 */
    ],
    [
        f64::from_bits(0x3ff02380e4dd22ad), /* 0x1.02380e4dd22adp+0 */
        f64::from_bits(0xbc73e8d0d9c49091), /* -0x1.3e8d0d9c49091p-56 */
    ],
    [
        f64::from_bits(0x3ff02433e494b755), /* 0x1.02433e494b755p+0 */
        f64::from_bits(0xbc9314aa16278aa3), /* -0x1.314aa16278aa3p-54 */
    ],
    [
        f64::from_bits(0x3ff024e6ec0da046), /* 0x1.024e6ec0da046p+0 */
        f64::from_bits(0x3c848daf888e9651), /* 0x1.48daf888e9651p-55 */
    ],
    [
        f64::from_bits(0x3ff02599fb483385), /* 0x1.02599fb483385p+0 */
        f64::from_bits(0x3c856dc8046821f4), /* 0x1.56dc8046821f4p-55 */
    ],
    [
        f64::from_bits(0x3ff0264d1244c719), /* 0x1.0264d1244c719p+0 */
        f64::from_bits(0x3c945b42356b9d47), /* 0x1.45b42356b9d47p-54 */
    ],
    [
        f64::from_bits(0x3ff027003103b10e), /* 0x1.027003103b10ep+0 */
        f64::from_bits(0xbc7082ef51b61d7e), /* -0x1.082ef51b61d7ep-56 */
    ],
    [
        f64::from_bits(0x3ff027b357854772), /* 0x1.027b357854772p+0 */
        f64::from_bits(0x3c72106ed0920a34), /* 0x1.2106ed0920a34p-56 */
    ],
    [
        f64::from_bits(0x3ff0286685c9e059), /* 0x1.0286685c9e059p+0 */
        f64::from_bits(0xbc9fd4cf26ea5d0f), /* -0x1.fd4cf26ea5d0fp-54 */
    ],
    [
        f64::from_bits(0x3ff02919bbd1d1d8), /* 0x1.02919bbd1d1d8p+0 */
        f64::from_bits(0xbc909f8775e78084), /* -0x1.09f8775e78084p-54 */
    ],
    [
        f64::from_bits(0x3ff029ccf99d720a), /* 0x1.029ccf99d720ap+0 */
        f64::from_bits(0x3c564cbba902ca27), /* 0x1.64cbba902ca27p-58 */
    ],
    [
        f64::from_bits(0x3ff02a803f2d170d), /* 0x1.02a803f2d170dp+0 */
        f64::from_bits(0x3c94383ef231d207), /* 0x1.4383ef231d207p-54 */
    ],
    [
        f64::from_bits(0x3ff02b338c811703), /* 0x1.02b338c811703p+0 */
        f64::from_bits(0x3c94a47a505b3a47), /* 0x1.4a47a505b3a47p-54 */
    ],
    [
        f64::from_bits(0x3ff02be6e199c811), /* 0x1.02be6e199c811p+0 */
        f64::from_bits(0x3c9e47120223467f), /* 0x1.e47120223467fp-54 */
    ],
];

pub(crate) static P_1: [f64; 6] = [
    f64::from_bits(0x3fd5555555555558), /* 0x1.5555555555558p-2 */
    f64::from_bits(0xbfd0000000000003), /* -0x1.0000000000003p-2 */
    f64::from_bits(0x3fc999999981f535), /* 0x1.999999981f535p-3 */
    f64::from_bits(0xbfc55555553d1eb4), /* -0x1.55555553d1eb4p-3 */
    f64::from_bits(0x3fc2494526fd4a06), /* 0x1.2494526fd4a06p-3 */
    f64::from_bits(0xbfc0001f0c80e8ce), /* -0x1.0001f0c80e8cep-3 */
];

pub(crate) static Q_1: [f64; 5] = [
    f64::from_bits(0x3ff0000000000000), /* 0x1p0 */
    f64::from_bits(0x3ff0000000000000), /* 0x1p0 */
    f64::from_bits(0x3fe0000000000000), /* 0x1p-1 */
    f64::from_bits(0x3fc5555555997996), /* 0x1.5555555997996p-3 */
    f64::from_bits(0x3fa5555555849d8d), /* 0x1.5555555849d8dp-5 */
];

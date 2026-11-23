// Rust translation of CORE-MATH's sin.c, cos.c and tan.c tables (https://core-math.gitlabpages.inria.fr/).
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

// Tables generated mechanically by tools/gen_tables.py from CORE-MATH's
// sin.c (identical copies in cos.c and tan.c). Do not edit by hand.

use super::Dint;

/// 1/(2pi) downwards with precision 1280 (C: `T`).
pub(crate) static T: [u64; 20] = [
    0x28be60db9391054a,
    0x7f09d5f47d4d3770,
    0x36d8a5664f10e410,
    0x7f9458eaf7aef158,
    0x6dc91b8e909374b8,
    0x01924bba82746487,
    0x3f877ac72c4a69cf,
    0xba208d7d4baed121,
    0x3a671c09ad17df90,
    0x4e64758e60d4ce7d,
    0x272117e2ef7e4a0e,
    0xc7fe25fff7816603,
    0xfbcbc462d6829b47,
    0xdb4d9fb3c9f2c26d,
    0xd3d18fd9a797fa8b,
    0x5d49eeb1faf97c5e,
    0xcf41ce7de294a4ba,
    0x9afed7ec47e35742,
    0x1580cc11bf1edaea,
    0xfc33ef0826bd0d87,
];

/// sin2pi(i/2^11), 0 <= i < 256, 128-bit (C: `S`).
pub(crate) static S: [Dint; 256] = [
    Dint::new(0x0000000000000000, 0x0000000000000000, 128, 0),
    Dint::new(0xc90fc5f66525d257, 0x480f7956b6470765, -8, 0),
    Dint::new(0xc90f87f3380388d5, 0xcb3ff35bd4d81baa, -7, 0),
    Dint::new(0x96cb587284b81770, 0xb767005691b9d9d1, -6, 0),
    Dint::new(0xc90e8fe6f63c2330, 0xf1d7d06db39ea9fc, -6, 0),
    Dint::new(0xfb514b55ccbe541a, 0xd784e031f9af76d6, -6, 0),
    Dint::new(0x96c9b5df1877e9b5, 0xf91ee371d6467dca, -5, 0),
    Dint::new(0xafea690fd5912ef3, 0xf56e3c87ae3c56df, -5, 0),
    Dint::new(0xc90aafbd1b33efc9, 0xc539edcbfda0cf2c, -5, 0),
    Dint::new(0xe22a7a6729d8e453, 0x850021e392744a4f, -5, 0),
    Dint::new(0xfb49b98e8e7807f6, 0x00b21ccebc9caac3, -5, 0),
    Dint::new(0x8a342eda160bf5ae, 0xde5b1068d174be9c, -4, 0),
    Dint::new(0x96c32baca2ae68b4, 0x37b2dd49d5fca3c0, -4, 0),
    Dint::new(0xa351cb7fc30bc889, 0xb56007d16d4ad5a3, -4, 0),
    Dint::new(0xafe00694866a1b44, 0xcd34d2751c2e1da7, -4, 0),
    Dint::new(0xbc6dd52c3a342eb5, 0xf10bfca3d6464012, -4, 0),
    Dint::new(0xc8fb2f886ec09f37, 0x6a17954b2b7c5171, -4, 0),
    Dint::new(0xd5880deafc18b534, 0x73d1472472f4a390, -4, 0),
    Dint::new(0xe214689606bf1676, 0x438b4a73aecd2541, -4, 0),
    Dint::new(0xeea037cc04764844, 0xc4e92d01a2f42935, -4, 0),
    Dint::new(0xfb2b73cfc106ff68, 0xf0a0e36a000c7350, -4, 0),
    Dint::new(0x83db0a7231831d8f, 0x60e782313f6161af, -3, 0),
    Dint::new(0x8a2009a6b84d9402, 0x77724a2b2a669bc4, -3, 0),
    Dint::new(0x9064b3a76a22640c, 0x56e0a8b0d177b55d, -3, 0),
    Dint::new(0x96a9049670cfae65, 0xf77574094d3c35c4, -3, 0),
    Dint::new(0x9cecf8962d14c822, 0x50ffe4f5caa7f1fa, -3, 0),
    Dint::new(0xa3308bc93904ad69, 0xdec1b7f2768bdafa, -3, 0),
    Dint::new(0xa973ba526a6850d9, 0x76f8c63986598c79, -3, 0),
    Dint::new(0xafb68054d520c60b, 0xfdd2fc0936594c2d, -3, 0),
    Dint::new(0xb5f8d9f3cd8945d6, 0x924bef13600f9852, -3, 0),
    Dint::new(0xbc3ac352ead90abe, 0xeb13e106732687f1, -3, 0),
    Dint::new(0xc27c389609850433, 0xb228a03916371f6f, -3, 0),
    Dint::new(0xc8bd35e14da15f0e, 0xc7396c894bbf7389, -3, 0),
    Dint::new(0xcefdb7592542e1e9, 0x6b47b8c44e5b037e, -3, 0),
    Dint::new(0xd53db9224ae01bca, 0x7337412cf70716cb, -3, 0),
    Dint::new(0xdb7d3761c7b263b6, 0xbb286d23e11c8337, -3, 0),
    Dint::new(0xe1bc2e3cf616a7ac, 0x31883b30137c6e62, -3, 0),
    Dint::new(0xe7fa99d983ee098f, 0xeeb8f9c33340a2f2, -3, 0),
    Dint::new(0xee38765d74fe4897, 0xed16b994af6c18ae, -3, 0),
    Dint::new(0xf475bfef2551f5b9, 0x14e1a5488eaeab96, -3, 0),
    Dint::new(0xfab272b54b9871a2, 0x704729ae56d78a37, -3, 0),
    Dint::new(0x8077456b7dc2d967, 0x3eac8308f1113e5e, -2, 0),
    Dint::new(0x8395023dd418e919, 0xdb1f70118c9c2198, -2, 0),
    Dint::new(0x86b26de5933c2e8e, 0xc5a9decdfaad4db5, -2, 0),
    Dint::new(0x89cf8676d7abb55b, 0x97965c9860c34e44, -2, 0),
    Dint::new(0x8cec4a05f12739e8, 0xdcdca90cc73b116a, -2, 0),
    Dint::new(0x9008b6a763de75b7, 0xa6e3df5975cca9da, -2, 0),
    Dint::new(0x9324ca6fe9a04b4e, 0x899c4de737feec22, -2, 0),
    Dint::new(0x964083747309d113, 0x000a89a11e07c1fe, -2, 0),
    Dint::new(0x995bdfca28b53a54, 0x49c4863de522b217, -2, 0),
    Dint::new(0x9c76dd866c689dcc, 0xe7bc08111d0bfca4, -2, 0),
    Dint::new(0x9f917abeda4498df, 0xf3ff913a4aadb85e, -2, 0),
    Dint::new(0xa2abb58949f2ced7, 0xa5dbee6084ee1260, -2, 0),
    Dint::new(0xa5c58bfbcfd4436a, 0x69fcb11e19f58619, -2, 0),
    Dint::new(0xa8defc2cbe2f8fcc, 0x0cd12a1f6ab6b095, -2, 0),
    Dint::new(0xabf80432a65ef190, 0x8c95c4c91179176b, -2, 0),
    Dint::new(0xaf10a22459fe32a6, 0x3feef3bb58b1f10d, -2, 0),
    Dint::new(0xb228d418ec1869ad, 0x16031a34d4fc855d, -2, 0),
    Dint::new(0xb5409827b25591f0, 0xcd73fb5d8d45d302, -2, 0),
    Dint::new(0xb857ec684627fa4c, 0x187e26d290714d70, -2, 0),
    Dint::new(0xbb6ecef285f98a3a, 0xbddd8a0365d6b1d3, -2, 0),
    Dint::new(0xbe853dde9658dc60, 0xdfe1b074e22fc666, -2, 0),
    Dint::new(0xc19b3744e3262dcd, 0xad5a41de48f6b26f, -2, 0),
    Dint::new(0xc4b0b93e20c0213f, 0xdab4e426409b23a0, -2, 0),
    Dint::new(0xc7c5c1e34d3055b2, 0x5cc8c00e4fccd850, -2, 0),
    Dint::new(0xcada4f4db157cf77, 0xfa6171200ab2efc3, -2, 0),
    Dint::new(0xcdee5f96e21b332c, 0x65a3132adfb7dfd5, -2, 0),
    Dint::new(0xd101f0d8c18ed1c1, 0xaadb580a1eba209f, -2, 0),
    Dint::new(0xd415012d802284f0, 0xdf4005ef6a64aa02, -2, 0),
    Dint::new(0xd7278eaf9dcd5b55, 0x1779df36d1cc8912, -2, 0),
    Dint::new(0xda399779eb391377, 0xcbabaeb97af8e8aa, -2, 0),
    Dint::new(0xdd4b19a78aed6515, 0xece7f445cecf1e28, -2, 0),
    Dint::new(0xe05c1353f27b17e5, 0x0ebc61ade6ca83cd, -2, 0),
    Dint::new(0xe36c829aeba6e720, 0x26a0eecdb4f16266, -2, 0),
    Dint::new(0xe67c659895943123, 0x82b0aecadf808123, -2, 0),
    Dint::new(0xe98bba6965ef725f, 0xb91caf23416e7e80, -2, 0),
    Dint::new(0xec9a7f2a2a188aeb, 0x7244ee20f591983b, -2, 0),
    Dint::new(0xefa8b1f8084ccdfc, 0x1050cdf22f34182f, -2, 0),
    Dint::new(0xf2b650f080d0da8d, 0x587f3fa044e2d27d, -2, 0),
    Dint::new(0xf5c35a316f1a3c80, 0x643720de93ba81bd, -2, 0),
    Dint::new(0xf8cfcbd90af8d57a, 0x4221dc4ba772598d, -2, 0),
    Dint::new(0xfbdba405e9c00cca, 0xd24d3023da491920, -2, 0),
    Dint::new(0xfee6e0d6ff6fc5a4, 0x8b74fe2508ab8fc2, -2, 0),
    Dint::new(0x80f8c035cfee8d76, 0xfd958d68e8b49e6b, -1, 0),
    Dint::new(0x827dc071bfed6ffa, 0xfb4c92369f0cf008, -1, 0),
    Dint::new(0x8402702f5b30f2a9, 0xcb07b25a7b0372a7, -1, 0),
    Dint::new(0x8586ce7ededc809d, 0x9d3dc689006896f4, -1, 0),
    Dint::new(0x870ada70ba4e6d49, 0x009d52755ece3f70, -1, 0),
    Dint::new(0x888e93158fb3bb04, 0x984156f553344306, -1, 0),
    Dint::new(0x8a11f77e349bc245, 0xa66d1d936c38c329, -1, 0),
    Dint::new(0x8b9506bbb28bb922, 0x575f33366be0afef, -1, 0),
    Dint::new(0x8d17bfdf47921ac8, 0xcb590d74f64e77c9, -1, 0),
    Dint::new(0x8e9a21fa66d9ee8d, 0xf2be3ecae62789d4, -1, 0),
    Dint::new(0x901c2c1eb93dee39, 0x632b9cff5cfee724, -1, 0),
    Dint::new(0x919ddd5e1ddb8b33, 0x609c464b3dd676ec, -1, 0),
    Dint::new(0x931f34caaaa5d23a, 0x6a1ff8bfe6396e28, -1, 0),
    Dint::new(0x94a03176acf82d45, 0xae4ba773da6bf754, -1, 0),
    Dint::new(0x9620d274aa290339, 0xe06a955a5b8e301d, -1, 0),
    Dint::new(0x97a116d7601c3515, 0xfc8b7184b21f2d50, -1, 0),
    Dint::new(0x9920fdb1c5d5783d, 0x9dd1eedf18a2e4df, -1, 0),
    Dint::new(0x9aa086170c0a8d86, 0x9ffa0d23f3c26c62, -1, 0),
    Dint::new(0x9c1faf1a9db554af, 0xdab6b478577e7be5, -1, 0),
    Dint::new(0x9d9e77d020a5bbe6, 0xdb895384528d0d60, -1, 0),
    Dint::new(0x9f1cdf4b76138b02, 0x98dbd3555ebcdefe, -1, 0),
    Dint::new(0xa09ae4a0bb300a19, 0x2f895f44a303cc0b, -1, 0),
    Dint::new(0xa21886e449b78316, 0xd29d23a624acd00c, -1, 0),
    Dint::new(0xa395c52ab8829dfc, 0x2be036401ba87cc2, -1, 0),
    Dint::new(0xa5129e88dc17976a, 0x82d9495ead5be348, -1, 0),
    Dint::new(0xa68f1213c73b5124, 0x17218792857f4c5a, -1, 0),
    Dint::new(0xa80b1ee0cb823c27, 0x3269f4702b88324a, -1, 0),
    Dint::new(0xa986c40579e11c0a, 0x8e3bdf8085321556, -1, 0),
    Dint::new(0xab020097a33da341, 0xc1654b64a0081b46, -1, 0),
    Dint::new(0xac7cd3ad58fee7f0, 0x811f953984eff83e, -1, 0),
    Dint::new(0xadf73c5ced9db0f3, 0x9a5318ac6fe94e4d, -1, 0),
    Dint::new(0xaf7139bcf5349ac6, 0x9fe5f4ea48965e2c, -1, 0),
    Dint::new(0xb0eacae4461013ed, 0x63c66682bae74898, -1, 0),
    Dint::new(0xb263eee9f93e3088, 0x695a5332090bb09b, -1, 0),
    Dint::new(0xb3dca4e56b1e54bb, 0x992d96e5021e3c37, -1, 0),
    Dint::new(0xb554ebee3bf0b58e, 0x971f4da709ad4378, -1, 0),
    Dint::new(0xb6ccc31c5065afee, 0x35ebacd79f209137, -1, 0),
    Dint::new(0xb8442987d22cf576, 0x9cc3ef36746de3b8, -1, 0),
    Dint::new(0xb9bb1e4930848ead, 0xcdb0531c4e58484b, -1, 0),
    Dint::new(0xbb31a07920c7b256, 0x55b92083658bb897, -1, 0),
    Dint::new(0xbca7af309efd7182, 0x0a4b0d21fc5036a5, -1, 0),
    Dint::new(0xbe1d4988ee67380c, 0xd1f90f79f46c7e01, -1, 0),
    Dint::new(0xbf926e9b9a0f2127, 0x91a1b5eb79658c67, -1, 0),
    Dint::new(0xc1071d8275561f9b, 0x721853f8e528a934, -1, 0),
    Dint::new(0xc27b55579c81f96d, 0xcdc2bd470675104d, -1, 0),
    Dint::new(0xc3ef1535754b168d, 0x3122c2a59efddc37, -1, 0),
    Dint::new(0xc5625c36af6a222f, 0xf4ff2895ab6ebe89, -1, 0),
    Dint::new(0xc6d5297645257e8d, 0x14d24739de27e2e9, -1, 0),
    Dint::new(0xc8477c0f7bde8a98, 0x004ce0246ad4fa74, -1, 0),
    Dint::new(0xc9b9531de49eb968, 0x4319e5ad5b0dcb84, -1, 0),
    Dint::new(0xcb2aadbd5ca47af5, 0xfaa3dfe675a65ee2, -1, 0),
    Dint::new(0xcc9b8b0a0deff5d4, 0x2e663b3c7555a6c3, -1, 0),
    Dint::new(0xce0bea206fcf9192, 0x3c540a9eec47af38, -1, 0),
    Dint::new(0xcf7bca1d476c516d, 0xa81290bdbaad62e4, -1, 0),
    Dint::new(0xd0eb2a1da855fefd, 0xb9302788604e88f1, -1, 0),
    Dint::new(0xd25a093ef50f2482, 0x721fc87ba1d42456, -1, 0),
    Dint::new(0xd3c8669edf98d680, 0x87967926fdcecec4, -1, 0),
    Dint::new(0xd536415b69fe4c54, 0x1df22346611c6b4b, -1, 0),
    Dint::new(0xd6a39892e6e04764, 0x3090d44db12c418c, -1, 0),
    Dint::new(0xd8106b63fa0048a0, 0xa573f2aa90434ba5, -1, 0),
    Dint::new(0xd97cb8ed98cb93f5, 0x2e349483e3fb2a6a, -1, 0),
    Dint::new(0xdae8804f0ae6015b, 0x362cb974182e3030, -1, 0),
    Dint::new(0xdc53c0a7eab49b35, 0x3ccca3982328ed8b, -1, 0),
    Dint::new(0xddbe791825e8099e, 0x1a5bd9269d408d7e, -1, 0),
    Dint::new(0xdf28a8bffe06ca56, 0xcce2634be2bf54df, -1, 0),
    Dint::new(0xe0924ec008f734fd, 0x8aa895d5bf3e84ea, -1, 0),
    Dint::new(0xe1fb6a3931894b38, 0xf7a1f9bd9ba13b6b, -1, 0),
    Dint::new(0xe363fa4cb8005482, 0x7b32c72e31824e51, -1, 0),
    Dint::new(0xe4cbfe1c329c453a, 0xd40e9e6b989f89e5, -1, 0),
    Dint::new(0xe63374c98e22f0b4, 0x2872ce1bfc7ad1cd, -1, 0),
    Dint::new(0xe79a5d770e6905dc, 0xf1b65cc5fd780262, -1, 0),
    Dint::new(0xe900b7474edad637, 0x431626c10485bdda, -1, 0),
    Dint::new(0xea66815d4304e6c8, 0x0cc39cfcc29960b1, -1, 0),
    Dint::new(0xebcbbadc371c4aaa, 0x1d90f780ae951140, -1, 0),
    Dint::new(0xed3062e7d086c6f0, 0xc71debc372b6f9d4, -1, 0),
    Dint::new(0xee9478a40e62bf86, 0x2a24164daec85ccb, -1, 0),
    Dint::new(0xeff7fb354a0eecb1, 0x527233b40d3432bb, -1, 0),
    Dint::new(0xf15ae9c037b1d8f0, 0x6c48e9e3420b0f1e, -1, 0),
    Dint::new(0xf2bd4369e6c126d3, 0x7f232aee178c6323, -1, 0),
    Dint::new(0xf41f0757c2889e84, 0x3c7f10db458c337c, -1, 0),
    Dint::new(0xf58034af92b102a7, 0x93fa6107c4327527, -1, 0),
    Dint::new(0xf6e0ca977bc6ac45, 0xe1079824233fef46, -1, 0),
    Dint::new(0xf840c835ffbfed66, 0xa9a56012067c570c, -1, 0),
    Dint::new(0xf9a02cb1fe833a0d, 0x08da894471de1a18, -1, 0),
    Dint::new(0xfafef732b66d1742, 0x0343fbf4a7d42af3, -1, 0),
    Dint::new(0xfc5d26dfc4d5cfda, 0x27c07c911290b8d1, -1, 0),
    Dint::new(0xfdbabae12696eea4, 0x02377c3799c052fa, -1, 0),
    Dint::new(0xff17b25f38907dad, 0x0a9c6ba50490539f, -1, 0),
    Dint::new(0x803a06415c170525, 0x6f53873e2f1477ff, 0, 0),
    Dint::new(0x80e7e43a61f5b6cb, 0x5ca183dc973abc22, 0, 0),
    Dint::new(0x819572af6decac84, 0x9fba97fdf0c4d24c, 0, 0),
    Dint::new(0x8242b1357110d372, 0x6fb2123fedfa6e22, 0, 0),
    Dint::new(0x82ef9f618dc5b70e, 0x91a965931f1a200a, 0, 0),
    Dint::new(0x839c3cc917ff6cb4, 0xbfd79717f2880abf, 0, 0),
    Dint::new(0x8448890195846099, 0x246efcff30cb064a, 0, 0),
    Dint::new(0x84f483a0be2f0403, 0x51917cac857fd5f5, 0, 0),
    Dint::new(0x85a02c3c7c2f5ca5, 0x327888fe4b62687b, 0, 0),
    Dint::new(0x864b826aec4c74e5, 0x85043222c9bdd18d, 0, 0),
    Dint::new(0x86f685c25e25acf5, 0x7e0b9b07548471a2, 0, 0),
    Dint::new(0x87a135d95473ec89, 0x4e091160e2430712, 0, 0),
    Dint::new(0x884b9246854ab50b, 0x4f14c8afe4560291, 0, 0),
    Dint::new(0x88f59aa0da591421, 0xb892ca8361d8c84c, 0, 0),
    Dint::new(0x899f4e7f712a765e, 0xc88302a31afce54a, 0, 0),
    Dint::new(0x8a48ad799b6759f3, 0x660558a02136130a, 0, 0),
    Dint::new(0x8af1b726df15e13c, 0x545f7d79ead8fa19, 0, 0),
    Dint::new(0x8b9a6b1ef6da4502, 0x21a6675f51580bc4, 0, 0),
    Dint::new(0x8c42c8f9d2372644, 0x101a5adbcb9ffb43, 0, 0),
    Dint::new(0x8cead04f95cdbf66, 0x4d49cbaf15aecd80, 0, 0),
    Dint::new(0x8d9280b89b9df49b, 0xde2d43c6b67a7cbe, 0, 0),
    Dint::new(0x8e39d9cd73464364, 0xbba4cfecbff54867, 0, 0),
    Dint::new(0x8ee0db26e24390f8, 0xaf0e2345f3bd24b4, 0, 0),
    Dint::new(0x8f87845de430d777, 0x9311a82459aa0f72, 0, 0),
    Dint::new(0x902dd50bab06b1b7, 0xb144016c7a30b39a, 0, 0),
    Dint::new(0x90d3ccc99f5ac58b, 0x09d1072e09b72292, 0, 0),
    Dint::new(0x91796b31609f0c54, 0x6714fe6925b78cc4, 0, 0),
    Dint::new(0x921eafdcc560f9c5, 0x33d0a284a8c954ad, 0, 0),
    Dint::new(0x92c39a65db88809d, 0x1f8481e704e4a767, 0, 0),
    Dint::new(0x93682a66e896f544, 0xb17821911e71c16e, 0, 0),
    Dint::new(0x940c5f7a69e5ce1c, 0x0001489a97671a42, 0, 0),
    Dint::new(0x94b0393b14e54156, 0xd6c7af02d5c16fd9, 0, 0),
    Dint::new(0x9553b743d75ac03f, 0xac0106650f4ef023, 0, 0),
    Dint::new(0x95f6d92fd79f4fba, 0xd9f8e1a446e973b9, 0, 0),
    Dint::new(0x96999e9a74ddbde3, 0xa7a7556c3b33abc1, 0, 0),
    Dint::new(0x973c071f4750b49c, 0xc0a03934f0cce19b, 0, 0),
    Dint::new(0x97de125a2080a8ed, 0xd243aa0843a2c144, 0, 0),
    Dint::new(0x987fbfe70b81a708, 0x19cec845ac87a5c6, 0, 0),
    Dint::new(0x99210f624d30facb, 0xc4b992a37fb9b9bd, 0, 0),
    Dint::new(0x99c200686472b4a8, 0x1ab42d43235757b6, 0, 0),
    Dint::new(0x9a6292960a6f0ab0, 0x7e92c655656e6b85, 0, 0),
    Dint::new(0x9b02c58832cf95c0, 0x698b94f50326a043, 0, 0),
    Dint::new(0x9ba298dc0bfc6a88, 0x9a5614e8ffbeac6f, 0, 0),
    Dint::new(0x9c420c2eff590e5f, 0xc7fd954194e6d8aa, 0, 0),
    Dint::new(0x9ce11f1eb18147b1, 0x3e93627de8fd5779, 0, 0),
    Dint::new(0x9d7fd1490285c9e3, 0xe25e39549638ae68, 0, 0),
    Dint::new(0x9e1e224c0e28bc94, 0x2cad377d5c9c35d8, 0, 0),
    Dint::new(0x9ebc11c62c1a1dfb, 0xcc141e10c6460c8b, 0, 0),
    Dint::new(0x9f599f55f0340061, 0xa88d5f46834bbf8d, 0, 0),
    Dint::new(0x9ff6ca9a2ab6a26d, 0x22cc118a0c118aa0, 0, 0),
    Dint::new(0xa0939331e8846237, 0x7cec6df5bea167cf, 0, 0),
    Dint::new(0xa12ff8bc735d8af6, 0x71acea2819360c35, 0, 0),
    Dint::new(0xa1cbfad9521bfd1b, 0x166c36e7bb3c402f, 0, 0),
    Dint::new(0xa267992848eeb0c0, 0x3b5167ee359a234e, 0, 0),
    Dint::new(0xa302d34959951243, 0x9443372e20d4377c, 0, 0),
    Dint::new(0xa39da8dcc39a38e5, 0x0ca9a8a720d4c69c, 0, 0),
    Dint::new(0xa4381983048ff747, 0xbf623cf5301a2dde, 0, 0),
    Dint::new(0xa4d224dcd849c5b0, 0x23d251cc8d7975cc, 0, 0),
    Dint::new(0xa56bca8b391785db, 0x189d39ffe11aaa2b, 0, 0),
    Dint::new(0xa6050a2f60002049, 0x8c33ebf3aa8501fb, 0, 0),
    Dint::new(0xa69de36ac4fbfadc, 0x9b3ad6e4022183d9, 0, 0),
    Dint::new(0xa73655df1f2f489e, 0x149f6e75993468a3, 0, 0),
    Dint::new(0xa7ce612e65243291, 0x6b2a39f856a69781, 0, 0),
    Dint::new(0xa86604facd04d969, 0x3463a2c2e6e9cc55, 0, 0),
    Dint::new(0xa8fd40e6ccd52ffd, 0x6cc14c4f53e2e82d, 0, 0),
    Dint::new(0xa99414951aacae5e, 0xd147625fda929af8, 0, 0),
    Dint::new(0xaa2a7fa8acefdd63, 0xb714ee81b53b4b9d, 0, 0),
    Dint::new(0xaac081c4ba89ba8a, 0xe1b3dfc4dbda9bfd, 0, 0),
    Dint::new(0xab561a8cbb24f410, 0xf17cee69b0d2ecde, 0, 0),
    Dint::new(0xabeb49a46764fd15, 0x1becda8089c1a94c, 0, 0),
    Dint::new(0xac800eafb91ef9a9, 0xf86ba0dde982fb59, 0, 0),
    Dint::new(0xad146952eb9282af, 0x44bf16268608db96, 0, 0),
    Dint::new(0xada859327ba24151, 0x9d30d4cfeb04f1fb, 0, 0),
    Dint::new(0xae3bddf3280c620d, 0x3d53817865422565, 0, 0),
    Dint::new(0xaecef739f1a2df10, 0xf74d099042e8f326, 0, 0),
    Dint::new(0xaf61a4ac1b83a1de, 0xa89a9b8f726b95bf, 0, 0),
    Dint::new(0xaff3e5ef2b507c06, 0x8c679e67fc462d51, 0, 0),
    Dint::new(0xb085baa8e966f6da, 0xe4cad00d5c94bcd2, 0, 0),
    Dint::new(0xb117227f6117f9f9, 0x8d8be132d576e614, 0, 0),
    Dint::new(0xb1a81d18e0df4889, 0x24784f32c3e3e5bd, 0, 0),
    Dint::new(0xb238aa1bfa9ad507, 0x8cc7d4bd05ffd5ae, 0, 0),
    Dint::new(0xb2c8c92f83c1eb87, 0xac9f7ebbc469ef59, 0, 0),
    Dint::new(0xb35879fa959c323c, 0x5d6635109164f740, 0, 0),
    Dint::new(0xb3e7bc248d78802e, 0xa156468ef6c18c60, 0, 0),
    Dint::new(0xb4768f550ce389fd, 0x4a85350f69018c55, 0, 0),
];

/// cos2pi(i/2^11), 0 <= i < 256, 128-bit (C: `C`).
pub(crate) static C: [Dint; 256] = [
    Dint::new(0x8000000000000000, 0x0000000000000000, 1, 0),
    Dint::new(0xffffb10b10e80e95, 0x3031437d7eccb9df, 0, 0),
    Dint::new(0xfffec42c7454926b, 0x38e310779edfec68, 0, 0),
    Dint::new(0xfffd3964bc6275ba, 0x69fff9ae0dedb047, 0, 0),
    Dint::new(0xfffb10b4dc96dabb, 0xb47903f7a19f8ee2, 0, 0),
    Dint::new(0xfff84a1e29de8571, 0x8cc193c5d508e13f, 0, 0),
    Dint::new(0xfff4e5a25a8d095b, 0x43366df666fd54ff, 0, 0),
    Dint::new(0xfff0e343865bbb13, 0x5428ed0647c9e5d1, 0, 0),
    Dint::new(0xffec4304266865d9, 0x5657552366961732, 0, 0),
    Dint::new(0xffe704e71533c508, 0x53aa9423bb0adc21, 0, 0),
    Dint::new(0xffe128ef8e9fc17a, 0x7d209f32d42d864e, 0, 0),
    Dint::new(0xffdaaf212fed72db, 0x4fd8f038449ec436, 0, 0),
    Dint::new(0xffd3977ff7bae4e9, 0x664649b4d541b9c5, 0, 0),
    Dint::new(0xffcbe2104600a0a9, 0x5595ca3f421ae09c, 0, 0),
    Dint::new(0xffc38ed6dc0ef98b, 0x1c676208aa3be545, 0, 0),
    Dint::new(0xffba9dd8dc8b1e83, 0xccfed60a91097c48, 0, 0),
    Dint::new(0xffb10f1bcb6bef1d, 0x421e8edaaf59453e, 0, 0),
    Dint::new(0xffa6e2a58df6947d, 0xd2c665c2da3e7844, 0, 0),
    Dint::new(0xff9c187c6abade6a, 0x1e1862cca089938b, 0, 0),
    Dint::new(0xff90b0a7098f6443, 0x2dabd3195a05710f, 0, 0),
    Dint::new(0xff84ab2c738d6a03, 0x519c314973ccae6b, 0, 0),
    Dint::new(0xff780814130c893c, 0x3ea4f30adda3016f, 0, 0),
    Dint::new(0xff6ac765b39e1e19, 0x1b9d5851979f28fb, 0, 0),
    Dint::new(0xff5ce92982087867, 0x50a7bb6a6ee3b0f1, 0, 0),
    Dint::new(0xff4e6d680c41d0a9, 0x0f668633f1ab858a, 0, 0),
    Dint::new(0xff3f542a416b0134, 0xb085c1828f69296a, 0, 0),
    Dint::new(0xff2f9d7971ca0364, 0x27e31939e2eec09c, 0, 0),
    Dint::new(0xff1f495f4ec430d7, 0xf5971326a3540ea9, 0, 0),
    Dint::new(0xff0e57e5ead848d1, 0x1f1901544271c3f8, 0, 0),
    Dint::new(0xfefcc917b99839a5, 0xe0abd3a9b64df725, 0, 0),
    Dint::new(0xfeea9cff8fa2ae54, 0xec34413e87ef2740, 0, 0),
    Dint::new(0xfed7d3a8a29c603b, 0x2f88b949a72ff96c, 0, 0),
    Dint::new(0xfec46d1e89292cf0, 0x41390efdc726e9ef, 0, 0),
    Dint::new(0xfeb0696d3ae4f04d, 0xb7b6cc53c3abc817, 0, 0),
    Dint::new(0xfe9bc8a1105c22a5, 0xd3af6ee4f2101c20, 0, 0),
    Dint::new(0xfe868ac6c3043b2e, 0x0b4f70c910505e10, 0, 0),
    Dint::new(0xfe70afeb6d33d6a2, 0x2907cf2b3f6feac2, 0, 0),
    Dint::new(0xfe5a381c8a1aa224, 0xd54faa364b7da8f6, 0, 0),
    Dint::new(0xfe432367f5b90a62, 0x87b8875373a818a4, 0, 0),
    Dint::new(0xfe2b71dbecd7aefc, 0x008598c2c429caf7, 0, 0),
    Dint::new(0xfe1323870cfe9a3d, 0x90cd1d959db674ef, 0, 0),
    Dint::new(0xfdfa3878546c3d28, 0x9bfe5c51e91cbdcd, 0, 0),
    Dint::new(0xfde0b0bf220c2fd4, 0xe276d247626a23fd, 0, 0),
    Dint::new(0xfdc68c6b356db62f, 0x499ddb331d19539d, 0, 0),
    Dint::new(0xfdabcb8caeba091b, 0xfac7397cc07a6470, 0, 0),
    Dint::new(0xfd906e340eaa6401, 0xd6e270740a186977, 0, 0),
    Dint::new(0xfd747472367dd6c5, 0x61beb8cd2696fc78, 0, 0),
    Dint::new(0xfd57de5867eedc39, 0x6c696582f346fd91, 0, 0),
    Dint::new(0xfd3aabf84528b50b, 0xeae6bd951c1dabbe, 0, 0),
    Dint::new(0xfd1cdd63d0bc8735, 0x863b87258f11ad7e, 0, 0),
    Dint::new(0xfcfe72ad6d9641f2, 0xa06fab9f9d106709, 0, 0),
    Dint::new(0xfcdf6be7def1464c, 0xa4e064308f4999f4, 0, 0),
    Dint::new(0xfcbfc926484cd43a, 0xa3e22b4d38917e73, 0, 0),
    Dint::new(0xfc9f8a7c2d603c60, 0x5d582cac7cb4391c, 0, 0),
    Dint::new(0xfc7eaffd720ed673, 0x02880268f2e62955, 0, 0),
    Dint::new(0xfc5d39be5a5bbc4b, 0x1c0d254b6c8da4bd, 0, 0),
    Dint::new(0xfc3b27d38a5d49ab, 0x256778ffcb5c1769, 0, 0),
    Dint::new(0xfc187a52063060c2, 0x9433b49289417ea2, 0, 0),
    Dint::new(0xfbf5314f31eb7375, 0x25aafd7fdba12c5f, 0, 0),
    Dint::new(0xfbd14ce0d191516e, 0x7190c94899dff1b8, 0, 0),
    Dint::new(0xfbaccd1d0903bb09, 0xe63ae8632b84473c, 0, 0),
    Dint::new(0xfb87b21a5bf5b917, 0x75df66f0ec3dd459, 0, 0),
    Dint::new(0xfb61fbefadddb985, 0x61ce9d5ef5a81487, 0, 0),
    Dint::new(0xfb3baab441e770f7, 0xb4b54683879c9c17, 0, 0),
    Dint::new(0xfb14be7fbae58156, 0x2172a361fd2a722f, 0, 0),
    Dint::new(0xfaed376a1b42e559, 0x2079880c450348ac, 0, 0),
    Dint::new(0xfac5158bc4f4211f, 0x4a188aa367f90ab1, 0, 0),
    Dint::new(0xfa9c58fd796837d4, 0x10655ecd5cc771d8, 0, 0),
    Dint::new(0xfa7301d859796671, 0x1fe196a53fb5b237, 0, 0),
    Dint::new(0xfa491035e55da3a3, 0xd24377c77a591e24, 0, 0),
    Dint::new(0xfa1e842ffc96e4e0, 0x431c393c7f62da65, 0, 0),
    Dint::new(0xf9f35de0dde328ab, 0xba5dbf4510eddc8f, 0, 0),
    Dint::new(0xf9c79d63272c4628, 0x4504ae08d19b2980, 0, 0),
    Dint::new(0xf99b42d1d57781eb, 0x78685d850f80ecdc, 0, 0),
    Dint::new(0xf96e4e4844d4e82a, 0x80e8c17bf80e8f02, 0, 0),
    Dint::new(0xf940bfe2304e6c45, 0xc0e2a1352ed7f292, 0, 0),
    Dint::new(0xf91297bbb1d6cdbe, 0x68fc6e4d6a920bd2, 0, 0),
    Dint::new(0xf8e3d5f1423842a0, 0x9701914c7f8fbcd7, 0, 0),
    Dint::new(0xf8b47a9fb902e76c, 0xac9f07f54ff5bc14, 0, 0),
    Dint::new(0xf88485e44c7af48a, 0xb36a9dfaadafc1e1, 0, 0),
    Dint::new(0xf853f7dc9186b952, 0xc7adc6b4988891bb, 0, 0),
    Dint::new(0xf822d0a67b9c5cb5, 0xa776175bd284fe05, 0, 0),
    Dint::new(0xf7f110605caf6390, 0xa76f7efc19aed41c, 0, 0),
    Dint::new(0xf7beb728e51dfcb8, 0x730785813f78aa1e, 0, 0),
    Dint::new(0xf78bc51f239e12c6, 0x214cffcee9dd33ca, 0, 0),
    Dint::new(0xf7583a62852a23b2, 0x4becad887680c197, 0, 0),
    Dint::new(0xf7241712d4edde49, 0xf99107e50d631330, 0, 0),
    Dint::new(0xf6ef5b503c328589, 0x50ca117eb18beed7, 0, 0),
    Dint::new(0xf6ba073b424b19e8, 0x2c791f59cc1ffc23, 0, 0),
    Dint::new(0xf6841af4cc8048a4, 0xce8c455197cdf8a7, 0, 0),
    Dint::new(0xf64d969e1dfc2119, 0x119d358de0493956, 0, 0),
    Dint::new(0xf6167a58d7b59026, 0x9dc7e5954c5a8f24, 0, 0),
    Dint::new(0xf5dec646f85ba1c6, 0xc8c615e72768d6b5, 0, 0),
    Dint::new(0xf5a67a8adc4088ca, 0xed0dd4bf62edd13f, 0, 0),
    Dint::new(0xf56d97473d446cda, 0x275a2bbb2bab6c8a, 0, 0),
    Dint::new(0xf5341c9f32bffeb9, 0x8da64484aaa0febc, 0, 0),
    Dint::new(0xf4fa0ab6316ed2ec, 0x163c5c7f03b718c5, 0, 0),
    Dint::new(0xf4bf61b00b5982b7, 0x890ac4aafa6a37bf, 0, 0),
    Dint::new(0xf48421b0efbf939b, 0xf8f9d3b87d11fd52, 0, 0),
    Dint::new(0xf4484add6b01254b, 0x667e06866c07c369, 0, 0),
    Dint::new(0xf40bdd5a6688662f, 0x5019794a1f5896e5, 0, 0),
    Dint::new(0xf3ced94d28b2ce8a, 0x18ef535a7ffa7a3d, 0, 0),
    Dint::new(0xf3913edb54ba2242, 0x50f29b4b49f31c37, 0, 0),
    Dint::new(0xf3530e2aea9d3966, 0x0d981acdcf6bc3e4, 0, 0),
    Dint::new(0xf314476247088f74, 0xa5486bdc455d56a2, 0, 0),
    Dint::new(0xf2d4eaa8233e997d, 0x431be53f92ece9e6, 0, 0),
    Dint::new(0xf294f82394ffe320, 0xebadcdbf915e8f6c, 0, 0),
    Dint::new(0xf2546ffc0e72f286, 0xaf0eed81e8c51e55, 0, 0),
    Dint::new(0xf21352595e0bf350, 0xe7112e89103cc0c7, 0, 0),
    Dint::new(0xf1d19f63ae7428a2, 0x844e6a35ddc2b713, 0, 0),
    Dint::new(0xf18f574386712643, 0x8f6bac72988088b0, 0, 0),
    Dint::new(0xf14c7a21c8cbd0f4, 0x2730081c758fb42b, 0, 0),
    Dint::new(0xf1090827b43725fd, 0x67127db35b287316, 0, 0),
    Dint::new(0xf0c5017ee336ca0f, 0xc4e557b119ef3185, 0, 0),
    Dint::new(0xf08066514c055f7e, 0x973ea9903ed5125f, 0, 0),
    Dint::new(0xf03b36c9407aa3e8, 0x992d39ec5c561d28, 0, 0),
    Dint::new(0xeff573116df1555d, 0x62aef7b55319d1d4, 0, 0),
    Dint::new(0xefaf1b54dd2cdf0f, 0xf03a18a5e16ab641, 0, 0),
    Dint::new(0xef682fbef23ecda6, 0x767c0e8ad33bc085, 0, 0),
    Dint::new(0xef20b07b6c6c0b37, 0xe2398bf0eeb28cde, 0, 0),
    Dint::new(0xeed89db66611e307, 0x86f8c20fb664b01b, 0, 0),
    Dint::new(0xee8ff79c548acd0f, 0xa1d2c3d018a9279f, 0, 0),
    Dint::new(0xee46be5a0813016b, 0x7872773830d368be, 0, 0),
    Dint::new(0xedfcf21cabacd3b1, 0xfee6a1eebfa13b4a, 0, 0),
    Dint::new(0xedb29311c504d652, 0x11815196b9fbf5df, 0, 0),
    Dint::new(0xed67a1673455c601, 0x7289102076a125e5, 0, 0),
    Dint::new(0xed1c1d4b344c3d4f, 0xddffe98c4f8aa031, 0, 0),
    Dint::new(0xecd006ec59ea306f, 0xa8392eb238578ab0, 0, 0),
    Dint::new(0xec835e79946a3145, 0x7e610231ac1d6181, 0, 0),
    Dint::new(0xec3624222d227bd1, 0x0278047ae3dd0889, 0, 0),
    Dint::new(0xebe85815c767cb00, 0x1e99ccb9adc62ca6, 0, 0),
    Dint::new(0xeb99fa84606ff5ff, 0x0dae311e656e0661, 0, 0),
    Dint::new(0xeb4b0b9e4f345617, 0x39e39c6c2ab3655d, 0, 0),
    Dint::new(0xeafb8b944453f52f, 0x3383bbb5156bf1d7, 0, 0),
    Dint::new(0xeaab7a9749f584fe, 0x24db98ad3a0647a1, 0, 0),
    Dint::new(0xea5ad8d8c3a91f05, 0x4a0ca5ea449b1c83, 0, 0),
    Dint::new(0xea09a68a6e49cd62, 0x15ad45b4a1b5e823, 0, 0),
    Dint::new(0xe9b7e3de5fdedc8b, 0xcd24d4bd1056c826, 0, 0),
    Dint::new(0xe9659107077cf60f, 0x89a92b199adfbafa, 0, 0),
    Dint::new(0xe912ae372d27045d, 0xacb1c26a06e5ae02, 0, 0),
    Dint::new(0xe8bf3ba1f1aedfbb, 0xf8972affb3d98e1f, 0, 0),
    Dint::new(0xe86b397ace95c46f, 0x9fec1e78c4376186, 0, 0),
    Dint::new(0xe816a7f595ec9232, 0xbfe8378abfb87b6f, 0, 0),
    Dint::new(0xe7c187467233d508, 0xdbfb0fe56c6f80fe, 0, 0),
    Dint::new(0xe76bd7a1e63b9786, 0x125129529d48a92f, 0, 0),
    Dint::new(0xe715993ccd02fe9c, 0xe2ba81b9ce96e02e, 0, 0),
    Dint::new(0xe6becc4c5997af06, 0x82fcedb4c6434d76, 0, 0),
    Dint::new(0xe667710616f4fc59, 0xdd2a3e32c3859960, 0, 0),
    Dint::new(0xe60f879fe7e2e1e5, 0x7613b68f6ab03130, 0, 0),
    Dint::new(0xe5b7105006d4c560, 0x9b695cd67c93bd79, 0, 0),
    Dint::new(0xe55e0b4d05c80388, 0x5a7c210a3a15e7ea, 0, 0),
    Dint::new(0xe50478cdce2246bc, 0xe1f5a58c80292554, 0, 0),
    Dint::new(0xe4aa5909a08fa7b4, 0x122785ae67f5515d, 0, 0),
    Dint::new(0xe44fac3814e09856, 0x20d63b5b9e3cd6ac, 0, 0),
    Dint::new(0xe3f4729119e798d9, 0x56992551ae074e99, 0, 0),
    Dint::new(0xe398ac4cf556b732, 0x0d1197dc12c63176, 0, 0),
    Dint::new(0xe33c59a4439cd8ec, 0x36563e2ffad8351a, 0, 0),
    Dint::new(0xe2df7acff7c2cf83, 0xd6fe4dd22e60a4a2, 0, 0),
    Dint::new(0xe28210095b483751, 0xfd39138aa2d508ed, 0, 0),
    Dint::new(0xe224198a0e002123, 0xe0521df01a1be6f5, 0, 0),
    Dint::new(0xe1c5978c05ed8691, 0xf4e8a8372f8c5810, 0, 0),
    Dint::new(0xe1668a498f1f892c, 0xe2f9d4600f4d0325, 0, 0),
    Dint::new(0xe106f1fd4b8d7c96, 0x6ba8a9d9ba877899, 0, 0),
    Dint::new(0xe0a6cee232f2bb9c, 0x6d6c98fe79817946, 0, 0),
    Dint::new(0xe046213392aa486c, 0x55ff6038a5197367, 0, 0),
    Dint::new(0xdfe4e92d0d8a37f5, 0x720588ff6547d884, 0, 0),
    Dint::new(0xdf83270a9bbee890, 0xab01350f013d78dd, 0, 0),
    Dint::new(0xdf20db088aa60404, 0x64a58b2f103485dd, 0, 0),
    Dint::new(0xdebe05637ca94cfb, 0x4b19aa71fec3ae6d, 0, 0),
    Dint::new(0xde5aa65869193805, 0x04248f15548f69ca, 0, 0),
    Dint::new(0xddf6be249c075037, 0xd597b10a01676659, 0, 0),
    Dint::new(0xdd924d05b620678a, 0x739c45b982193b5e, 0, 0),
    Dint::new(0xdd2d5339ac8692fd, 0x49c6e0ea76cbcaac, 0, 0),
    Dint::new(0xdcc7d0fec8aaf2aa, 0xb2069fd0b482b4e8, 0, 0),
    Dint::new(0xdc61c693a82745d5, 0xaca8017e375b64e5, 0, 0),
    Dint::new(0xdbfb34373c974b0e, 0xccb7fd40d543f4a1, 0, 0),
    Dint::new(0xdb941a28cb71ec87, 0x2c19b63253da43fc, 0, 0),
    Dint::new(0xdb2c78a7ede238a9, 0x5a98479cbef2ecbc, 0, 0),
    Dint::new(0xdac44ff490a02710, 0x5b267c1bcff0ab62, 0, 0),
    Dint::new(0xda5ba04ef3c929f4, 0xe257bde73d83dc1a, 0, 0),
    Dint::new(0xd9f269f7aab88c29, 0x28e81dcb6dab91ac, 0, 0),
    Dint::new(0xd988ad2f9bdf9bbb, 0xc4e4dc69fc2fff6f, 0, 0),
    Dint::new(0xd91e6a38009da15a, 0x1bb35ad6d2e74b67, 0, 0),
    Dint::new(0xd8b3a1526517a48b, 0x1ed1a8ff78f1b632, 0, 0),
    Dint::new(0xd84852c0a80ffcdb, 0x24b9fe00663574a4, 0, 0),
    Dint::new(0xd7dc7ec4fabdb011, 0xced12d2899b803db, 0, 0),
    Dint::new(0xd77025a1e0a39d8b, 0x0cb78e80e67ba1b8, 0, 0),
    Dint::new(0xd703479a2f6776cc, 0x6cb3bfd65b38562b, 0, 0),
    Dint::new(0xd695e4f10ea88570, 0x083f082b570611d7, 0, 0),
    Dint::new(0xd627fde9f7d63e7e, 0x7afbefc05e9f7d99, 0, 0),
    Dint::new(0xd5b992c8b606a351, 0x7190b755535d4f18, 0, 0),
    Dint::new(0xd54aa3d165cc7018, 0x7d00ae97abaa4096, 0, 0),
    Dint::new(0xd4db3148750d1819, 0xf630e8b6dac83e69, 0, 0),
    Dint::new(0xd46b3b72a2d68fc9, 0xdc4663a3168698d2, 0, 0),
    Dint::new(0xd3fac294ff34e4d0, 0xb77d4f6bd0ee8591, 0, 0),
    Dint::new(0xd389c6f4eb07a41c, 0xa8faac741a6394dc, 0, 0),
    Dint::new(0xd31848d817d70e16, 0xeeeaddb72f00e0dd, 0, 0),
    Dint::new(0xd2a6488487a91918, 0x4300fd1c1ce507e5, 0, 0),
    Dint::new(0xd233c6408cd64236, 0x981ba7e42537275f, 0, 0),
    Dint::new(0xd1c0c252c9de2c86, 0xda7485a5aeffeb4c, 0, 0),
    Dint::new(0xd14d3d02313c0eed, 0x744fea20e8abef92, 0, 0),
    Dint::new(0xd0d93696053af098, 0x77a18eb13d2ecde5, 0, 0),
    Dint::new(0xd064af55d7c9b43e, 0x6b8a685f6cb61c21, 0, 0),
    Dint::new(0xcfefa7898a4ef23c, 0xdaf200dd81212d10, 0, 0),
    Dint::new(0xcf7a1f794d7ca1b1, 0xdfcb60445c1bf973, 0, 0),
    Dint::new(0xcf04176da12390ac, 0x04d27090f10c454e, 0, 0),
    Dint::new(0xce8d8faf5406ab8b, 0xf5babff66def7892, 0, 0),
    Dint::new(0xce16888783ae13b3, 0x93e391861a034684, 0, 0),
    Dint::new(0xcd9f023f9c3a059e, 0x23af31db7179a4aa, 0, 0),
    Dint::new(0xcd26fd2158358e7d, 0x649474e36b8db9d3, 0, 0),
    Dint::new(0xccae7976c0691177, 0x83e907fbd7aaf0b0, 0, 0),
    Dint::new(0xcc35778a2bac9ca1, 0xf839ce18e08bfb50, 0, 0),
    Dint::new(0xcbbbf7a63eba0dd5, 0x70cbb7f3343451be, 0, 0),
    Dint::new(0xcb41fa15ebff0777, 0x2293661be51140ab, 0, 0),
    Dint::new(0xcac77f24736eb553, 0xd9944be1631846d8, 0, 0),
    Dint::new(0xca4c871d625361a9, 0x5328edeb3e6784de, 0, 0),
    Dint::new(0xc9d1124c931fda7a, 0x8335241be1693225, 0, 0),
    Dint::new(0xc95520fe2d40a74b, 0x83b0e96e1249c2b0, 0, 0),
    Dint::new(0xc8d8b37ea4ed0f62, 0x0b562c00b34ee771, 0, 0),
    Dint::new(0xc85bca1abaf7f0a7, 0x65862939b83382e0, 0, 0),
    Dint::new(0xc7de651f7ca06749, 0x02b31bc86877fd2c, 0, 0),
    Dint::new(0xc76084da43624634, 0xd5c149509e9059f1, 0, 0),
    Dint::new(0xc6e22998b4c6608e, 0xcfe6c1b1a6b4e2a4, 0, 0),
    Dint::new(0xc66353a8c232a43c, 0xe993503baf5afb41, 0, 0),
    Dint::new(0xc5e40358a8ba05a7, 0x43da25d99267326b, 0, 0),
    Dint::new(0xc56438f6f0ec3cca, 0x0ab4906075507e74, 0, 0),
    Dint::new(0xc4e3f4d26ea553b6, 0xdd40950cf1ed92fa, 0, 0),
    Dint::new(0xc463373a40dd06a3, 0x9dd768f30ca8e85c, 0, 0),
    Dint::new(0xc3e2007dd175f5a4, 0xa87e78136665cdb2, 0, 0),
    Dint::new(0xc36050ecd50ca830, 0x8ac9e1386e4cbabb, 0, 0),
    Dint::new(0xc2de28d74ac6628b, 0x74c8f010d986a9e0, 0, 0),
    Dint::new(0xc25b888d7c1fcd38, 0xb7041e9bc8c18b0d, 0, 0),
    Dint::new(0xc1d8705ffcbb6e90, 0xbdf0715cb8b20bd7, 0, 0),
    Dint::new(0xc154e09faa2ff69a, 0x17858573216e0a22, 0, 0),
    Dint::new(0xc0d0d99dabd65d44, 0x2bda5328933c854a, 0, 0),
    Dint::new(0xc04c5bab7297d322, 0x6dd06968e0ed1957, 0, 0),
    Dint::new(0xbfc7671ab8bb84c6, 0xe4e62d86dd136e78, 0, 0),
    Dint::new(0xbf41fc3d81b430db, 0x0d46655d6b012455, 0, 0),
    Dint::new(0xbebc1b6619ed9116, 0x2715ef03f8543355, 0, 0),
    Dint::new(0xbe35c4e716999630, 0x29d7f7b67d43b177, 0, 0),
    Dint::new(0xbdaef913557d76f0, 0xac85320f528d6d5d, 0, 0),
    Dint::new(0xbd27b83dfcbe9279, 0x2ea36923d5d8e213, 0, 0),
    Dint::new(0xbca002ba7aaf25ea, 0x4a48496734be336d, 0, 0),
    Dint::new(0xbc17d8dc859ad583, 0x727c405ffc73af56, 0, 0),
    Dint::new(0xbb8f3af81b93095c, 0xfce8d84068e825b6, 0, 0),
    Dint::new(0xbb062961823b1ddc, 0x5120e35e1c1a250c, 0, 0),
    Dint::new(0xba7ca46d46946802, 0x33201477347447d8, 0, 0),
    Dint::new(0xb9f2ac703cca0db3, 0x39db32d014440024, 0, 0),
    Dint::new(0xb96841bf7ffcb21a, 0x9de1e3b22b8bf4db, 0, 0),
    Dint::new(0xb8dd64b0720df647, 0xa726f4f0828585c9, 0, 0),
    Dint::new(0xb8521598bb6bce26, 0x1c041d1ea5fb3fdb, 0, 0),
    Dint::new(0xb7c654ce4adba9f2, 0x2e7a35723f3ed035, 0, 0),
    Dint::new(0xb73a22a755457448, 0x7f86f63bb23f496a, 0, 0),
    Dint::new(0xb6ad7f7a557e64f2, 0xeb2d28ef943dc88c, 0, 0),
    Dint::new(0xb6206b9e0c13a892, 0xea7c015f12b987f7, 0, 0),
    Dint::new(0xb592e7697f14dd4a, 0x737dd2824b608d13, 0, 0),
];

/// Fast-path odd polynomial for sin2pi (C: `PSfast`).
pub(crate) static PSFAST: [f64; 5] = [
    f64::from_bits(0x401921fb54442d18),
    f64::from_bits(0x3cb1a62645446203),
    f64::from_bits(0xc044abbce625be53),
    f64::from_bits(0x405466bc678d8d63),
    f64::from_bits(0xc05331554ca19669),
];

/// Fast-path even polynomial for cos2pi (C: `PCfast`).
pub(crate) static PCFAST: [f64; 5] = [
    f64::from_bits(0x3ff0000000000000),
    f64::from_bits(0xbb2923015c000000),
    f64::from_bits(0xc033bd3cc9be45de),
    f64::from_bits(0x40503c1f080ad892),
    f64::from_bits(0xc0555a5c590f9e6a),
];

/// Accurate odd polynomial for sin2pi (C: `PS`).
pub(crate) static PS: [Dint; 6] = [
    Dint::new(0xc90fdaa22168c234, 0xc4c6628b80dc1cd1, 3, 0),
    Dint::new(0xa55de7312df295f5, 0x5dc72f712aa57db4, 6, 1),
    Dint::new(0xa335e33bad570e92, 0x3f33be0021aa54d2, 7, 0),
    Dint::new(0x9969667315ec2d9d, 0xe59d6ab8509a2025, 7, 1),
    Dint::new(0xa83c1a43bf1c6485, 0x7d5f8f76fa7d74ed, 6, 0),
    Dint::new(0xf16ab2898eae62f9, 0xa7f0339113b8b3c5, 4, 1),
];

/// Accurate even polynomial for cos2pi (C: `PC`).
pub(crate) static PC: [Dint; 6] = [
    Dint::new(0x8000000000000000, 0x0000000000000000, 1, 0),
    Dint::new(0x9de9e64df22ef2d2, 0x56e26cd9808c1949, 5, 1),
    Dint::new(0x81e0f840dad61d9a, 0x9980f00630cb655e, 7, 0),
    Dint::new(0xaae9e3f1e5ffcfe2, 0xa508509534006249, 7, 1),
    Dint::new(0xf0fa83448dd1e094, 0x0e0603ce7044eeba, 6, 0),
    Dint::new(0xd368f6f4207cfe49, 0x0ec63157807ebffa, 5, 1),
];

/// SC[i] = {xi - i/2^11, sin2pi(xi), cos2pi(xi)} (C: `SC`).
pub(crate) static SC: [[f64; 3]; 256] = [
    [
        f64::from_bits(0x0000000000000000),
        f64::from_bits(0x0000000000000000),
        f64::from_bits(0x3ff0000000000000),
    ], // 0
    [
        f64::from_bits(0xbdcc0f6c00000000),
        f64::from_bits(0x3f6921f892b900fe),
        f64::from_bits(0x3feffff621623fa0),
    ], // 1
    [
        f64::from_bits(0xbdc9c7935e000000),
        f64::from_bits(0x3f7921f0ea27ce01),
        f64::from_bits(0x3fefffd8858eca2e),
    ], // 2
    [
        f64::from_bits(0xbddd14d1ac000000),
        f64::from_bits(0x3f82d96af779b0bb),
        f64::from_bits(0x3fefffa72c986392),
    ], // 3
    [
        f64::from_bits(0xbdedba8f6a800000),
        f64::from_bits(0x3f8921d1ce2d0a1c),
        f64::from_bits(0x3fefff62169dddaa),
    ], // 4
    [
        f64::from_bits(0x3dfa6b7cdf000000),
        f64::from_bits(0x3f8f6a29bdb73770),
        f64::from_bits(0x3fefff0943c02419),
    ], // 5
    [
        f64::from_bits(0x3deb49618d000000),
        f64::from_bits(0x3f92d936d1506f3d),
        f64::from_bits(0x3feffe9cb44829c0),
    ], // 6
    [
        f64::from_bits(0xbdc398d6fc000000),
        f64::from_bits(0x3f95fd4d1e21de6d),
        f64::from_bits(0x3feffe1c687174b1),
    ], // 7
    [
        f64::from_bits(0xbe0e9e9a8c800000),
        f64::from_bits(0x3f99215597791e0a),
        f64::from_bits(0x3feffd886097afcf),
    ], // 8
    [
        f64::from_bits(0xbdf34e844c000000),
        f64::from_bits(0x3f9c454f2e9480c7),
        f64::from_bits(0x3feffce09ce95933),
    ], // 9
    [
        f64::from_bits(0xbdf989a8a4000000),
        f64::from_bits(0x3f9f693709b94f92),
        f64::from_bits(0x3feffc251dfbac0c),
    ], // 10
    [
        f64::from_bits(0x3e104a9b99000000),
        f64::from_bits(0x3fa146860e69a571),
        f64::from_bits(0x3feffb55e40a5c43),
    ], // 11
    [
        f64::from_bits(0xbdb56947c0000000),
        f64::from_bits(0x3fa2d865748774ad),
        f64::from_bits(0x3feffa72efff95d1),
    ], // 12
    [
        f64::from_bits(0xbdcc348768000000),
        f64::from_bits(0x3fa46a396d34121a),
        f64::from_bits(0x3feff97c420a8451),
    ], // 13
    [
        f64::from_bits(0x3df9e80552000000),
        f64::from_bits(0x3fa5fc00e6e4c65c),
        f64::from_bits(0x3feff871dacd8761),
    ], // 14
    [
        f64::from_bits(0x3dd3f11d74000000),
        f64::from_bits(0x3fa78dbaa97099eb),
        f64::from_bits(0x3feff753bb18af95),
    ], // 15
    [
        f64::from_bits(0x3dec039af4000000),
        f64::from_bits(0x3fa91f65fc0abc0a),
        f64::from_bits(0x3feff621e370ca7a),
    ], // 16
    [
        f64::from_bits(0x3dc53e1f80000000),
        f64::from_bits(0x3faab101bf74ac2e),
        f64::from_bits(0x3feff4dc54b00181),
    ], // 17
    [
        f64::from_bits(0x3e2114a649000000),
        f64::from_bits(0x3fac428d7de920e9),
        f64::from_bits(0x3feff3830f2e9043),
    ], // 18
    [
        f64::from_bits(0x3e0adf0ef4000000),
        f64::from_bits(0x3fadd40723a3cdfb),
        f64::from_bits(0x3feff21614b9d9ad),
    ], // 19
    [
        f64::from_bits(0xbe1d21f591800000),
        f64::from_bits(0x3faf656e1e9e59cd),
        f64::from_bits(0x3feff09565e83d77),
    ], // 20
    [
        f64::from_bits(0xbe14f54d70800000),
        f64::from_bits(0x3fb07b612d6be078),
        f64::from_bits(0x3fefef0102c634e3),
    ], // 21
    [
        f64::from_bits(0xbe11efec9a000000),
        f64::from_bits(0x3fb1440118ba7bd0),
        f64::from_bits(0x3fefed58ecf342da),
    ], // 22
    [
        f64::from_bits(0x3e2cc17ba8800000),
        f64::from_bits(0x3fb20c96cf0a7eed),
        f64::from_bits(0x3fefeb9d24646fa6),
    ], // 23
    [
        f64::from_bits(0x3de121dbe4000000),
        f64::from_bits(0x3fb2d5209628edf0),
        f64::from_bits(0x3fefe9cdacf99cff),
    ], // 24
    [
        f64::from_bits(0xbdd9ecf610000000),
        f64::from_bits(0x3fb39d9f103bf7f7),
        f64::from_bits(0x3fefe7ea854e6b08),
    ], // 25
    [
        f64::from_bits(0xbe004ede8e000000),
        f64::from_bits(0x3fb466116c629e5c),
        f64::from_bits(0x3fefe5f3af4ee201),
    ], // 26
    [
        f64::from_bits(0xbe01821cec000000),
        f64::from_bits(0x3fb52e773c9920c7),
        f64::from_bits(0x3fefe3e92c0e4108),
    ], // 27
    [
        f64::from_bits(0x3e0cdec726000000),
        f64::from_bits(0x3fb5f6d02131f0b2),
        f64::from_bits(0x3fefe1cafc7f1a24),
    ], // 28
    [
        f64::from_bits(0xbe0edece4d000000),
        f64::from_bits(0x3fb6bf1b2653648c),
        f64::from_bits(0x3fefdf99233c230c),
    ], // 29
    [
        f64::from_bits(0xbe02aa4d1c000000),
        f64::from_bits(0x3fb787585bc45f0f),
        f64::from_bits(0x3fefdd53a01d11d9),
    ], // 30
    [
        f64::from_bits(0x3dfd461592000000),
        f64::from_bits(0x3fb84f871e32cf68),
        f64::from_bits(0x3fefdafa74f16482),
    ], // 31
    [
        f64::from_bits(0x3e2f0cbd72800000),
        f64::from_bits(0x3fb917a71d3d2956),
        f64::from_bits(0x3fefd88da29f302e),
    ], // 32
    [
        f64::from_bits(0xbe15832470000000),
        f64::from_bits(0x3fb9dfb6c9865b06),
        f64::from_bits(0x3fefd60d2e14a6b1),
    ], // 33
    [
        f64::from_bits(0xbe12e81bf4000000),
        f64::from_bits(0x3fbaa7b706bfdbba),
        f64::from_bits(0x3fefd3791484ff50),
    ], // 34
    [
        f64::from_bits(0xbe31394141800000),
        f64::from_bits(0x3fbb6fa680a05c27),
        f64::from_bits(0x3fefd0d15a4b8471),
    ], // 35
    [
        f64::from_bits(0x3e171098ff000000),
        f64::from_bits(0x3fbc3785eba12b42),
        f64::from_bits(0x3fefce15fceddccf),
    ], // 36
    [
        f64::from_bits(0xbdfc3519e8000000),
        f64::from_bits(0x3fbcff53302f0590),
        f64::from_bits(0x3fefcb4703b969e1),
    ], // 37
    [
        f64::from_bits(0x3e42f522a5000000),
        f64::from_bits(0x3fbdc70fb84af16e),
        f64::from_bits(0x3fefc8646987fc1d),
    ], // 38
    [
        f64::from_bits(0xbdeae9bed8000000),
        f64::from_bits(0x3fbe8eb7f8a589e2),
        f64::from_bits(0x3fefc56e3b91ca3a),
    ], // 39
    [
        f64::from_bits(0x3e1f8868b2000000),
        f64::from_bits(0x3fbf564e87d2330f),
        f64::from_bits(0x3fefc264701f9a09),
    ], // 40
    [
        f64::from_bits(0xbe2b07985f800000),
        f64::from_bits(0x3fc00ee8835051f4),
        f64::from_bits(0x3fefbf47105f7439),
    ], // 41
    [
        f64::from_bits(0x3e1cbdaa94000000),
        f64::from_bits(0x3fc072a05e1d4d8e),
        f64::from_bits(0x3fefbc16172a9e36),
    ], // 42
    [
        f64::from_bits(0x3e337c5b90800000),
        f64::from_bits(0x3fc0d64df9619f0d),
        f64::from_bits(0x3fefb8d18b635327),
    ], // 43
    [
        f64::from_bits(0xbe3068b5fc800000),
        f64::from_bits(0x3fc139f09bc617f5),
        f64::from_bits(0x3fefb5797351da85),
    ], // 44
    [
        f64::from_bits(0xbe28ea6681800000),
        f64::from_bits(0x3fc19d8919fa4ec8),
        f64::from_bits(0x3fefb20dc7da8aff),
    ], // 45
    [
        f64::from_bits(0x3e36278ceb800000),
        f64::from_bits(0x3fc2011719d50b87),
        f64::from_bits(0x3fefae8e8bd4427f),
    ], // 46
    [
        f64::from_bits(0xbe2096df84000000),
        f64::from_bits(0x3fc264993433763a),
        f64::from_bits(0x3fefaafbcbfca356),
    ], // 47
    [
        f64::from_bits(0x3e29b2534f000000),
        f64::from_bits(0x3fc2c810967bbf70),
        f64::from_bits(0x3fefa7557d8d987e),
    ], // 48
    [
        f64::from_bits(0x3dd215b4e0000000),
        f64::from_bits(0x3fc32b7bfa25c91b),
        f64::from_bits(0x3fefa39bac71954b),
    ], // 49
    [
        f64::from_bits(0xbe194db891000000),
        f64::from_bits(0x3fc38edb9d29b39d),
        f64::from_bits(0x3fef9fce56700a6d),
    ], // 50
    [
        f64::from_bits(0x3e27727f7b800000),
        f64::from_bits(0x3fc3f22f7c3cce3a),
        f64::from_bits(0x3fef9bed7b8c8d8c),
    ], // 51
    [
        f64::from_bits(0xbe20cb3303800000),
        f64::from_bits(0x3fc45576971dd530),
        f64::from_bits(0x3fef97f925d53c83),
    ], // 52
    [
        f64::from_bits(0xbe09071106000000),
        f64::from_bits(0x3fc4b8b175c71e22),
        f64::from_bits(0x3fef93f14feb8022),
    ], // 53
    [
        f64::from_bits(0x3e262741e7800000),
        f64::from_bits(0x3fc51bdfa7ea30d5),
        f64::from_bits(0x3fef8fd5fe3efac8),
    ], // 54
    [
        f64::from_bits(0x3e3f8e16d0c00000),
        f64::from_bits(0x3fc57f00e80e6e12),
        f64::from_bits(0x3fef8ba733a1ceb1),
    ], // 55
    [
        f64::from_bits(0xbe076acbca000000),
        f64::from_bits(0x3fc5e2143b7bc1c2),
        f64::from_bits(0x3fef8764fad5e9bf),
    ], // 56
    [
        f64::from_bits(0xbe10a0f73a000000),
        f64::from_bits(0x3fc6451a76411746),
        f64::from_bits(0x3fef830f4ad232d8),
    ], // 57
    [
        f64::from_bits(0x3e3ca11d1bc00000),
        f64::from_bits(0x3fc6a8135d7bd143),
        f64::from_bits(0x3fef7ea625eb5af7),
    ], // 58
    [
        f64::from_bits(0xbe202f2362800000),
        f64::from_bits(0x3fc70afd74071191),
        f64::from_bits(0x3fef7a299d3f182a),
    ], // 59
    [
        f64::from_bits(0x3e2b34dcb8000000),
        f64::from_bits(0x3fc76dda08544b5c),
        f64::from_bits(0x3fef7599a1ac7ecd),
    ], // 60
    [
        f64::from_bits(0x3df161ff40000000),
        f64::from_bits(0x3fc7d0a7bf2d4aba),
        f64::from_bits(0x3fef70f64322da74),
    ], // 61
    [
        f64::from_bits(0xbe0c49b8b4000000),
        f64::from_bits(0x3fc83366ddb3de23),
        f64::from_bits(0x3fef6c3f7e7c2707),
    ], // 62
    [
        f64::from_bits(0x3e221da851000000),
        f64::from_bits(0x3fc8961743b14290),
        f64::from_bits(0x3fef6775552a6ba2),
    ], // 63
    [
        f64::from_bits(0x3e1ac63eda000000),
        f64::from_bits(0x3fc8f8b851098588),
        f64::from_bits(0x3fef6297cef0cdd6),
    ], // 64
    [
        f64::from_bits(0x3e427ef489c00000),
        f64::from_bits(0x3fc95b4a5b9f2ceb),
        f64::from_bits(0x3fef5da6e7820551),
    ], // 65
    [
        f64::from_bits(0x3e1ae89370000000),
        f64::from_bits(0x3fc9bdcc07900146),
        f64::from_bits(0x3fef58a2b0689c82),
    ], // 66
    [
        f64::from_bits(0x3e2eb48c7e000000),
        f64::from_bits(0x3fca203e4a4f950e),
        f64::from_bits(0x3fef538b1d392049),
    ], // 67
    [
        f64::from_bits(0xbe2bfd282f000000),
        f64::from_bits(0x3fca829ffaad0d79),
        f64::from_bits(0x3fef4e603d51f1aa),
    ], // 68
    [
        f64::from_bits(0x3e27ccf638000000),
        f64::from_bits(0x3fcae4f1fa80e1b5),
        f64::from_bits(0x3fef492204c5ef9e),
    ], // 69
    [
        f64::from_bits(0xbe32435c57800000),
        f64::from_bits(0x3fcb4732b72ebc86),
        f64::from_bits(0x3fef43d0890e1e72),
    ], // 70
    [
        f64::from_bits(0x3e10293fec000000),
        f64::from_bits(0x3fcba9634155f866),
        f64::from_bits(0x3fef3e6bbb6c2ea4),
    ], // 71
    [
        f64::from_bits(0xbe27bb1f92000000),
        f64::from_bits(0x3fcc0b82461f65e0),
        f64::from_bits(0x3fef38f3ae6f9afc),
    ], // 72
    [
        f64::from_bits(0x3e227aaebc000000),
        f64::from_bits(0x3fcc6d906faacf65),
        f64::from_bits(0x3fef3368589e17a2),
    ], // 73
    [
        f64::from_bits(0xbe42e2bcd5000000),
        f64::from_bits(0x3fcccf8c3f74a6c9),
        f64::from_bits(0x3fef2dc9cfb5fa74),
    ], // 74
    [
        f64::from_bits(0xbe16f070ac000000),
        f64::from_bits(0x3fcd31773ba218a8),
        f64::from_bits(0x3fef2817fd4d045b),
    ], // 75
    [
        f64::from_bits(0x3e2469adfc000000),
        f64::from_bits(0x3fcd935004779e57),
        f64::from_bits(0x3fef2252f59c122d),
    ], // 76
    [
        f64::from_bits(0x3df4f51c18000000),
        f64::from_bits(0x3fcdf5164301377a),
        f64::from_bits(0x3fef1c7abdeaa3ef),
    ], // 77
    [
        f64::from_bits(0x3e278e44da000000),
        f64::from_bits(0x3fce56ca4202807c),
        f64::from_bits(0x3fef168f51c5d5d5),
    ], // 78
    [
        f64::from_bits(0x3df49bb5f8000000),
        f64::from_bits(0x3fceb86b4a1b7e9b),
        f64::from_bits(0x3fef1090bc4b6800),
    ], // 79
    [
        f64::from_bits(0xbe367ba541000000),
        f64::from_bits(0x3fcf19f9369d5e93),
        f64::from_bits(0x3fef0a7effdc937f),
    ], // 80
    [
        f64::from_bits(0x3e2c0cab95000000),
        f64::from_bits(0x3fcf7b74ab7219d2),
        f64::from_bits(0x3fef045a1219e594),
    ], // 81
    [
        f64::from_bits(0xbe12b77e32000000),
        f64::from_bits(0x3fcfdcdc0ca3288d),
        f64::from_bits(0x3feefe220cf5c751),
    ], // 82
    [
        f64::from_bits(0xbdee0d8cb0000000),
        f64::from_bits(0x3fd01f18054c8362),
        f64::from_bits(0x3feef7d6e54c347d),
    ], // 83
    [
        f64::from_bits(0xbe2ecd5b9c000000),
        f64::from_bits(0x3fd04fb7f6d35d68),
        f64::from_bits(0x3feef178a6f9a987),
    ], // 84
    [
        f64::from_bits(0x3e2eb24de5000000),
        f64::from_bits(0x3fd0804e1d369ff2),
        f64::from_bits(0x3feeeb074934fdf0),
    ], // 85
    [
        f64::from_bits(0x3e14a897c4000000),
        f64::from_bits(0x3fd0b0d9d7b0d042),
        f64::from_bits(0x3feee482e14bcde0),
    ], // 86
    [
        f64::from_bits(0x3e1336c376000000),
        f64::from_bits(0x3fd0e15b555e7bec),
        f64::from_bits(0x3feeddeb6908ca8c),
    ], // 87
    [
        f64::from_bits(0xbe03952d90000000),
        f64::from_bits(0x3fd111d25efd48b8),
        f64::from_bits(0x3feed740e7eb8dd6),
    ], // 88
    [
        f64::from_bits(0x3e0fc2a5d4000000),
        f64::from_bits(0x3fd1423ef5c7e1bd),
        f64::from_bits(0x3feed0835dc24e89),
    ], // 89
    [
        f64::from_bits(0x3e2a88ed37000000),
        f64::from_bits(0x3fd172a0eb8361da),
        f64::from_bits(0x3feec9b2d0ec8288),
    ], // 90
    [
        f64::from_bits(0xbe48ca4cb9400000),
        f64::from_bits(0x3fd1a2f7b10b6d70),
        f64::from_bits(0x3feec2cf55d6117c),
    ], // 91
    [
        f64::from_bits(0x3e40144524000000),
        f64::from_bits(0x3fd1d3446fd0cd3f),
        f64::from_bits(0x3feebbd8c1d62f96),
    ], // 92
    [
        f64::from_bits(0xbe3abf810c000000),
        f64::from_bits(0x3fd203855b85f89a),
        f64::from_bits(0x3feeb4cf57454132),
    ], // 93
    [
        f64::from_bits(0x3e35d4c5d5800000),
        f64::from_bits(0x3fd233bbcca40561),
        f64::from_bits(0x3feeadb2e40746ca),
    ], // 94
    [
        f64::from_bits(0xbe2a1b0c58000000),
        f64::from_bits(0x3fd263e685b1d714),
        f64::from_bits(0x3feea68396d87754),
    ], // 95
    [
        f64::from_bits(0xbe277c8dac000000),
        f64::from_bits(0x3fd294061d2eb611),
        f64::from_bits(0x3fee9f41597393c8),
    ], // 96
    [
        f64::from_bits(0x3e1915540e000000),
        f64::from_bits(0x3fd2c41a580014cf),
        f64::from_bits(0x3fee97ec348fb87f),
    ], // 97
    [
        f64::from_bits(0xbe3abb6d9b000000),
        f64::from_bits(0x3fd2f422b2d0990c),
        f64::from_bits(0x3fee90843c55b996),
    ], // 98
    [
        f64::from_bits(0xbe3b8ee5d5800000),
        f64::from_bits(0x3fd3241f8cea2836),
        f64::from_bits(0x3fee890962268c49),
    ], // 99
    [
        f64::from_bits(0xbe31cd2982800000),
        f64::from_bits(0x3fd35410a8396266),
        f64::from_bits(0x3fee817baf85c094),
    ], // 100
    [
        f64::from_bits(0xbdfe216af0000000),
        f64::from_bits(0x3fd383f5e08283e2),
        f64::from_bits(0x3fee79db2a188b0a),
    ], // 101
    [
        f64::from_bits(0xbe024afc30000000),
        f64::from_bits(0x3fd3b3cef6993c0b),
        f64::from_bits(0x3fee7227dbf82004),
    ], // 102
    [
        f64::from_bits(0xbe0aa1657c000000),
        f64::from_bits(0x3fd3e39be4767224),
        f64::from_bits(0x3fee6a61c62d5274),
    ], // 103
    [
        f64::from_bits(0xbe1c5b65fa000000),
        f64::from_bits(0x3fd4135c898485bb),
        f64::from_bits(0x3fee6288ee07fea5),
    ], // 104
    [
        f64::from_bits(0x3df23e8978000000),
        f64::from_bits(0x3fd44310de3c284b),
        f64::from_bits(0x3fee5a9d54bbd26c),
    ], // 105
    [
        f64::from_bits(0xbe22b1d77a000000),
        f64::from_bits(0x3fd472b8976d498d),
        f64::from_bits(0x3fee529f06cb187d),
    ], // 106
    [
        f64::from_bits(0xbe0daaa348000000),
        f64::from_bits(0x3fd4a253cb97efd1),
        f64::from_bits(0x3fee4a8e007231a2),
    ], // 107
    [
        f64::from_bits(0xbe3322f570800000),
        f64::from_bits(0x3fd4d1e2260c3422),
        f64::from_bits(0x3fee426a500f6e33),
    ], // 108
    [
        f64::from_bits(0x3e264758e8000000),
        f64::from_bits(0x3fd50163eca0b337),
        f64::from_bits(0x3fee3a33e996b722),
    ], // 109
    [
        f64::from_bits(0x3e31248627800000),
        f64::from_bits(0x3fd530d89a17e007),
        f64::from_bits(0x3fee31eae3fb917b),
    ], // 110
    [
        f64::from_bits(0xbe46c3416cc00000),
        f64::from_bits(0x3fd5603fcf8cd8a3),
        f64::from_bits(0x3fee298f502a579b),
    ], // 111
    [
        f64::from_bits(0x3e2ab481ff000000),
        f64::from_bits(0x3fd58f9a896aa209),
        f64::from_bits(0x3fee2121016e14fc),
    ], // 112
    [
        f64::from_bits(0xbe26eb838b000000),
        f64::from_bits(0x3fd5bee77aaf890b),
        f64::from_bits(0x3fee18a032eb4df5),
    ], // 113
    [
        f64::from_bits(0xbdfd159b80000000),
        f64::from_bits(0x3fd5ee2734efeef5),
        f64::from_bits(0x3fee100ccaa6bd78),
    ], // 114
    [
        f64::from_bits(0xbdda42e4a0000000),
        f64::from_bits(0x3fd61d595bedeabc),
        f64::from_bits(0x3fee0766d944915e),
    ], // 115
    [
        f64::from_bits(0xbe143d0dc0000000),
        f64::from_bits(0x3fd64c7dd5cc0cd1),
        f64::from_bits(0x3fedfeae63903034),
    ], // 116
    [
        f64::from_bits(0xbe48c7bdb7000000),
        f64::from_bits(0x3fd67b9453ca2122),
        f64::from_bits(0x3fedf5e378482eae),
    ], // 117
    [
        f64::from_bits(0x3e11c0ead6000000),
        f64::from_bits(0x3fd6aa9d844c980a),
        f64::from_bits(0x3feded05f6a23a52),
    ], // 118
    [
        f64::from_bits(0x3e07d52600000000),
        f64::from_bits(0x3fd6d99867e90d92),
        f64::from_bits(0x3fede4160e97b2e2),
    ], // 119
    [
        f64::from_bits(0x3e3924e036800000),
        f64::from_bits(0x3fd7088555d3c816),
        f64::from_bits(0x3feddb13afb14e37),
    ], // 120
    [
        f64::from_bits(0xbe174b7c3e000000),
        f64::from_bits(0x3fd73763c09fba09),
        f64::from_bits(0x3fedd1fef5335416),
    ], // 121
    [
        f64::from_bits(0xbe17943ad0000000),
        f64::from_bits(0x3fd766340685c982),
        f64::from_bits(0x3fedc8d7ccf2567a),
    ], // 122
    [
        f64::from_bits(0x3e279dd614000000),
        f64::from_bits(0x3fd794f5f7522b88),
        f64::from_bits(0x3fedbf9e402aa5c3),
    ], // 123
    [
        f64::from_bits(0x3e17b64f32000000),
        f64::from_bits(0x3fd7c3a939c32d81),
        f64::from_bits(0x3fedb652607e0db1),
    ], // 124
    [
        f64::from_bits(0xbe32bea5ce800000),
        f64::from_bits(0x3fd7f24db825141c),
        f64::from_bits(0x3fedacf43268b5b0),
    ], // 125
    [
        f64::from_bits(0x3e1733c024000000),
        f64::from_bits(0x3fd820e3b8bf15a0),
        f64::from_bits(0x3feda383a7aed887),
    ], // 126
    [
        f64::from_bits(0xbe4eac0fc9400000),
        f64::from_bits(0x3fd84f6a51d077b3),
        f64::from_bits(0x3fed9a00efd84537),
    ], // 127
    [
        f64::from_bits(0x3e4aca3733800000),
        f64::from_bits(0x3fd87de2f4704f98),
        f64::from_bits(0x3fed906bbf17f4da),
    ], // 128
    [
        f64::from_bits(0xbe1910c4f0000000),
        f64::from_bits(0x3fd8ac4b7dc0d986),
        f64::from_bits(0x3fed86c4862b5d6e),
    ], // 129
    [
        f64::from_bits(0xbe033bb860000000),
        f64::from_bits(0x3fd8daa52b4dc041),
        f64::from_bits(0x3fed7d0b0374a559),
    ], // 130
    [
        f64::from_bits(0xbe469e1507000000),
        f64::from_bits(0x3fd908ef408ad220),
        f64::from_bits(0x3fed733f5e71c3bc),
    ], // 131
    [
        f64::from_bits(0x3e4cffacf0800000),
        f64::from_bits(0x3fd9372ab7784d36),
        f64::from_bits(0x3fed696161d786c9),
    ], // 132
    [
        f64::from_bits(0xbe58629d9f000000),
        f64::from_bits(0x3fd965552b0849ab),
        f64::from_bits(0x3fed5f7190eeae23),
    ], // 133
    [
        f64::from_bits(0x3e14150000000000),
        f64::from_bits(0x3fd99371687c64f3),
        f64::from_bits(0x3fed556f5155d9dd),
    ], // 134
    [
        f64::from_bits(0xbe4bd37aad800000),
        f64::from_bits(0x3fd9c17cf40715cb),
        f64::from_bits(0x3fed4b5b2caf8386),
    ], // 135
    [
        f64::from_bits(0x3e5d02cde7000000),
        f64::from_bits(0x3fd9ef79ea4d995d),
        f64::from_bits(0x3fed4134ac5eb246),
    ], // 136
    [
        f64::from_bits(0xbe110547ac000000),
        f64::from_bits(0x3fda1d653d9adf5e),
        f64::from_bits(0x3fed36fc7d291602),
    ], // 137
    [
        f64::from_bits(0xbe401a1a22800000),
        f64::from_bits(0x3fda4b40f9c0120b),
        f64::from_bits(0x3fed2cb22b45236b),
    ], // 138
    [
        f64::from_bits(0x3e23ce2bac000000),
        f64::from_bits(0x3fda790ce2056b9a),
        f64::from_bits(0x3fed2255c3ae11a5),
    ], // 139
    [
        f64::from_bits(0xbdfccb4a60000000),
        f64::from_bits(0x3fdaa6c828db4ea8),
        f64::from_bits(0x3fed17e774d4e3e2),
    ], // 140
    [
        f64::from_bits(0x3e25db4b00000000),
        f64::from_bits(0x3fdad47321f29847),
        f64::from_bits(0x3fed0d672bc0b122),
    ], // 141
    [
        f64::from_bits(0x3e232f6a6e000000),
        f64::from_bits(0x3fdb020d7a285e23),
        f64::from_bits(0x3fed02d4fb84d334),
    ], // 142
    [
        f64::from_bits(0x3e5cf8e39bc00000),
        f64::from_bits(0x3fdb2f97c27f7494),
        f64::from_bits(0x3fecf830c2248c5e),
    ], // 143
    [
        f64::from_bits(0x3e18927bb0000000),
        f64::from_bits(0x3fdb5d10129a750a),
        f64::from_bits(0x3feced7af22cb105),
    ], // 144
    [
        f64::from_bits(0xbe33dec3c1000000),
        f64::from_bits(0x3fdb8a77f8d0bbc5),
        f64::from_bits(0x3fece2b32e50d6cd),
    ], // 145
    [
        f64::from_bits(0xbe326ba536000000),
        f64::from_bits(0x3fdbb7cf08f0290d),
        f64::from_bits(0x3fecd7d98fcf3b1e),
    ], // 146
    [
        f64::from_bits(0x3e223c568e000000),
        f64::from_bits(0x3fdbe51524e3aa53),
        f64::from_bits(0x3fecccee1da3d56e),
    ], // 147
    [
        f64::from_bits(0xbe2f3b3af0000000),
        f64::from_bits(0x3fdc1249c1f5f2f6),
        f64::from_bits(0x3fecc1f0f95e1e24),
    ], // 148
    [
        f64::from_bits(0xbe31286a47000000),
        f64::from_bits(0x3fdc3f6d2ef7054b),
        f64::from_bits(0x3fecb6e20ff37e81),
    ], // 149
    [
        f64::from_bits(0x3e2641214e000000),
        f64::from_bits(0x3fdc6c7f594003d9),
        f64::from_bits(0x3fecabc165bf1b60),
    ], // 150
    [
        f64::from_bits(0x3e40cda7c9000000),
        f64::from_bits(0x3fdc997ff2bffccb),
        f64::from_bits(0x3feca08f0dee434c),
    ], // 151
    [
        f64::from_bits(0xbe35557ac9000000),
        f64::from_bits(0x3fdcc66e7b42e8f1),
        f64::from_bits(0x3fec954b28bca62e),
    ], // 152
    [
        f64::from_bits(0x3e3555eb62000000),
        f64::from_bits(0x3fdcf34bccc567a1),
        f64::from_bits(0x3fec89f57f6e20f3),
    ], // 153
    [
        f64::from_bits(0xbe34e0e361000000),
        f64::from_bits(0x3fdd2016cbb5e39a),
        f64::from_bits(0x3fec7e8e59999e1f),
    ], // 154
    [
        f64::from_bits(0x3e2446da1e000000),
        f64::from_bits(0x3fdd4cd039d0ed05),
        f64::from_bits(0x3fec731585f970eb),
    ], // 155
    [
        f64::from_bits(0x3e2103d328000000),
        f64::from_bits(0x3fdd797767638dec),
        f64::from_bits(0x3fec678b3174afe1),
    ], // 156
    [
        f64::from_bits(0x3e35814d60000000),
        f64::from_bits(0x3fdda60c7ae9dc22),
        f64::from_bits(0x3fec5bef522be6fb),
    ], // 157
    [
        f64::from_bits(0xbe25e2321e000000),
        f64::from_bits(0x3fddd28f054cbb3f),
        f64::from_bits(0x3fec5042052c8c42),
    ], // 158
    [
        f64::from_bits(0xbe2a259ffe000000),
        f64::from_bits(0x3fddfeff54854631),
        f64::from_bits(0x3fec44833611bc7d),
    ], // 159
    [
        f64::from_bits(0xbe04f28d80000000),
        f64::from_bits(0x3fde2b5d34665b35),
        f64::from_bits(0x3fec38b2f278ea7e),
    ], // 160
    [
        f64::from_bits(0xbdbde57100000000),
        f64::from_bits(0x3fde57a86d137f20),
        f64::from_bits(0x3fec2cd1493d05c2),
    ], // 161
    [
        f64::from_bits(0x3e2e0d8d14000000),
        f64::from_bits(0x3fde83e0ffb7bfb4),
        f64::from_bits(0x3fec20de3a08ea07),
    ], // 162
    [
        f64::from_bits(0xbe312a858e000000),
        f64::from_bits(0x3fdeb0067e48baf4),
        f64::from_bits(0x3fec14d9e2bd511e),
    ], // 163
    [
        f64::from_bits(0x3e49a17403000000),
        f64::from_bits(0x3fdedc19997a4431),
        f64::from_bits(0x3fec08c413089b2e),
    ], // 164
    [
        f64::from_bits(0x3e268c8636000000),
        f64::from_bits(0x3fdf0819163d1bc0),
        f64::from_bits(0x3febfc9d21568f32),
    ], // 165
    [
        f64::from_bits(0x3e24cc5eb8000000),
        f64::from_bits(0x3fdf3405a482e11d),
        f64::from_bits(0x3febf064dd580fc9),
    ], // 166
    [
        f64::from_bits(0xbe4fce7cd8000000),
        f64::from_bits(0x3fdf5fde8f3f11d4),
        f64::from_bits(0x3febe41b798f6b97),
    ], // 167
    [
        f64::from_bits(0xbe2af81690000000),
        f64::from_bits(0x3fdf8ba4c98a9816),
        f64::from_bits(0x3febd7c0b1a7f14b),
    ], // 168
    [
        f64::from_bits(0x3de6e39e20000000),
        f64::from_bits(0x3fdfb7575d1ea750),
        f64::from_bits(0x3febcb54cac5dde5),
    ], // 169
    [
        f64::from_bits(0x3e330f9256000000),
        f64::from_bits(0x3fdfe2f665dcd168),
        f64::from_bits(0x3febbed7bd1e17b0),
    ], // 170
    [
        f64::from_bits(0x3e0626de20000000),
        f64::from_bits(0x3fe00740ca0d5fbb),
        f64::from_bits(0x3febb2499f9fe7a3),
    ], // 171
    [
        f64::from_bits(0x3e15cc7030000000),
        f64::from_bits(0x3fe01cfc8afeea0e),
        f64::from_bits(0x3feba5aa650dd495),
    ], // 172
    [
        f64::from_bits(0xbdf6191e60000000),
        f64::from_bits(0x3fe032ae54fe4057),
        f64::from_bits(0x3feb98fa2065a5e6),
    ], // 173
    [
        f64::from_bits(0xbe06b14850000000),
        f64::from_bits(0x3fe0485624c328c8),
        f64::from_bits(0x3feb8c38d39737bc),
    ], // 174
    [
        f64::from_bits(0xbe211fbc3a000000),
        f64::from_bits(0x3fe05df3e66a716d),
        f64::from_bits(0x3feb7f668a580fd0),
    ], // 175
    [
        f64::from_bits(0xbe40eca7f0000000),
        f64::from_bits(0x3fe07387825589ec),
        f64::from_bits(0x3feb728352c44517),
    ], // 176
    [
        f64::from_bits(0xbe68073bc9e00000),
        f64::from_bits(0x3fe089109ef1284d),
        f64::from_bits(0x3feb658f630112ed),
    ], // 177
    [
        f64::from_bits(0xbe49dcf0ad000000),
        f64::from_bits(0x3fe09e9051603e29),
        f64::from_bits(0x3feb588a13ab750f),
    ], // 178
    [
        f64::from_bits(0xbe206ea9f0000000),
        f64::from_bits(0x3fe0b405820e78e7),
        f64::from_bits(0x3feb4b740d3cc07b),
    ], // 179
    [
        f64::from_bits(0xbe136a8d0c000000),
        f64::from_bits(0x3fe0c9704a1ea4e5),
        f64::from_bits(0x3feb3e4d40f5524d),
    ], // 180
    [
        f64::from_bits(0x3e163d1f30000000),
        f64::from_bits(0x3fe0ded0bc01a533),
        f64::from_bits(0x3feb3115a3a628af),
    ], // 181
    [
        f64::from_bits(0x3e5f3181f1400000),
        f64::from_bits(0x3fe0f4270e4787bf),
        f64::from_bits(0x3feb23cd1314c779),
    ], // 182
    [
        f64::from_bits(0xbe2f269b78000000),
        f64::from_bits(0x3fe109723e75c5cf),
        f64::from_bits(0x3feb167430cfebdb),
    ], // 183
    [
        f64::from_bits(0x3e41d84dc0800000),
        f64::from_bits(0x3fe11eb36bc9db52),
        f64::from_bits(0x3feb090a4915ee88),
    ], // 184
    [
        f64::from_bits(0xbe408e6006800000),
        f64::from_bits(0x3fe133e9ba0061d8),
        f64::from_bits(0x3feafb8fe69a6527),
    ], // 185
    [
        f64::from_bits(0x3e4cda72ab000000),
        f64::from_bits(0x3fe14915d557a7c9),
        f64::from_bits(0x3feaee049bc0aee0),
    ], // 186
    [
        f64::from_bits(0xbe1f32f950000000),
        f64::from_bits(0x3fe15e36dfb6bb55),
        f64::from_bits(0x3feae068f6991699),
    ], // 187
    [
        f64::from_bits(0x3e3138092d000000),
        f64::from_bits(0x3fe1734d6f34d7f0),
        f64::from_bits(0x3fead2bc96c1e1f5),
    ], // 188
    [
        f64::from_bits(0x3e56b382dd400000),
        f64::from_bits(0x3fe188595ae376a5),
        f64::from_bits(0x3feac4ff962bdb6d),
    ], // 189
    [
        f64::from_bits(0xbe3f12fafa000000),
        f64::from_bits(0x3fe19d59f592a587),
        f64::from_bits(0x3feab7326685eb57),
    ], // 190
    [
        f64::from_bits(0xbe32909e5a000000),
        f64::from_bits(0x3fe1b2500aed7ac6),
        f64::from_bits(0x3feaa954823cf815),
    ], // 191
    [
        f64::from_bits(0xbe6d66a897800000),
        f64::from_bits(0x3fe1c73aa0150cf9),
        f64::from_bits(0x3fea9b668fb0503f),
    ], // 192
    [
        f64::from_bits(0x3e4311ea86000000),
        f64::from_bits(0x3fe1dc1b7db74db1),
        f64::from_bits(0x3fea8d675d9c6cc8),
    ], // 193
    [
        f64::from_bits(0xbe041c02b8000000),
        f64::from_bits(0x3fe1f0f08a1a06a4),
        f64::from_bits(0x3fea7f5853bb4309),
    ], // 194
    [
        f64::from_bits(0xbe5ca1f4ed000000),
        f64::from_bits(0x3fe205ba57211271),
        f64::from_bits(0x3fea71391146958f),
    ], // 195
    [
        f64::from_bits(0xbe3910ce77000000),
        f64::from_bits(0x3fe21a7988f8326b),
        f64::from_bits(0x3fea63092626202f),
    ], // 196
    [
        f64::from_bits(0x3e62bfadbee00000),
        f64::from_bits(0x3fe22f2dc71afab6),
        f64::from_bits(0x3fea54c8cd9fd0d9),
    ], // 197
    [
        f64::from_bits(0xbe45f1c02a800000),
        f64::from_bits(0x3fe243d5df4afb93),
        f64::from_bits(0x3fea4678dbbe5e73),
    ], // 198
    [
        f64::from_bits(0xbe1db12b90000000),
        f64::from_bits(0x3fe2587347f493a4),
        f64::from_bits(0x3fea38184db0df23),
    ], // 199
    [
        f64::from_bits(0xbe17b29e00000000),
        f64::from_bits(0x3fe26d05490f2f61),
        f64::from_bits(0x3fea29a7a2f40b49),
    ], // 200
    [
        f64::from_bits(0xbe2b3ddca4000000),
        f64::from_bits(0x3fe2818be6930629),
        f64::from_bits(0x3fea1b26d8f070d7),
    ], // 201
    [
        f64::from_bits(0x3e2e112744000000),
        f64::from_bits(0x3fe2960730ff2bcd),
        f64::from_bits(0x3fea0c95e3df5e0e),
    ], // 202
    [
        f64::from_bits(0xbe35269766000000),
        f64::from_bits(0x3fe2aa76dafcbbf4),
        f64::from_bits(0x3fe9fdf4fae1df6f),
    ], // 203
    [
        f64::from_bits(0xbe309777e1000000),
        f64::from_bits(0x3fe2bedb1b6b4e15),
        f64::from_bits(0x3fe9ef43f6cbe162),
    ], // 204
    [
        f64::from_bits(0x3e3ae2051f000000),
        f64::from_bits(0x3fe2d333e4617f25),
        f64::from_bits(0x3fe9e082e148680e),
    ], // 205
    [
        f64::from_bits(0xbe436f6ced800000),
        f64::from_bits(0x3fe2e780cb47180e),
        f64::from_bits(0x3fe9d1b207f383c3),
    ], // 206
    [
        f64::from_bits(0xbe323fdc6b000000),
        f64::from_bits(0x3fe2fbc23fba2f44),
        f64::from_bits(0x3fe9c2d1197130a7),
    ], // 207
    [
        f64::from_bits(0x3debc540e0000000),
        f64::from_bits(0x3fe30ff7fd6d967d),
        f64::from_bits(0x3fe9b3e0478b961b),
    ], // 208
    [
        f64::from_bits(0xbe3cfb4ed7000000),
        f64::from_bits(0x3fe32421da0bf0e9),
        f64::from_bits(0x3fe9a4dfb1c89326),
    ], // 209
    [
        f64::from_bits(0x3e555802aec00000),
        f64::from_bits(0x3fe3384042a92b1d),
        f64::from_bits(0x3fe995cf06920d11),
    ], // 210
    [
        f64::from_bits(0x3e360719e4000000),
        f64::from_bits(0x3fe34c52608e3a92),
        f64::from_bits(0x3fe986aee6d6837e),
    ], // 211
    [
        f64::from_bits(0xbe1cbf2e48000000),
        f64::from_bits(0x3fe36058ac8863b6),
        f64::from_bits(0x3fe9777ef832c986),
    ], // 212
    [
        f64::from_bits(0x3e49061c32000000),
        f64::from_bits(0x3fe374533ab707d0),
        f64::from_bits(0x3fe9683f2ad7e2ec),
    ], // 213
    [
        f64::from_bits(0xbe4da84dfe000000),
        f64::from_bits(0x3fe3884160f9488f),
        f64::from_bits(0x3fe958f000fdd50a),
    ], // 214
    [
        f64::from_bits(0x3e292e8a74000000),
        f64::from_bits(0x3fe39c23eba6b22a),
        f64::from_bits(0x3fe94990dd9cee51),
    ], // 215
    [
        f64::from_bits(0xbe2bff5d9a000000),
        f64::from_bits(0x3fe3affa20756bdd),
        f64::from_bits(0x3fe93a225056084a),
    ], // 216
    [
        f64::from_bits(0x3db4c46200000000),
        f64::from_bits(0x3fe3c3c4498e98eb),
        f64::from_bits(0x3fe92aa41fbb951c),
    ], // 217
    [
        f64::from_bits(0xbe3e4613e9000000),
        f64::from_bits(0x3fe3d782261dff62),
        f64::from_bits(0x3fe91b167e92d706),
    ], // 218
    [
        f64::from_bits(0x3e10eb2964000000),
        f64::from_bits(0x3fe3eb33ed579bbe),
        f64::from_bits(0x3fe90b794146043c),
    ], // 219
    [
        f64::from_bits(0xbe260abec2000000),
        f64::from_bits(0x3fe3fed94c834d8a),
        f64::from_bits(0x3fe8fbcca9583479),
    ], // 220
    [
        f64::from_bits(0x3e46954977000000),
        f64::from_bits(0x3fe4127281ddac03),
        f64::from_bits(0x3fe8ec1085083553),
    ], // 221
    [
        f64::from_bits(0x3e2a16fec2000000),
        f64::from_bits(0x3fe425ff1f841235),
        f64::from_bits(0x3fe8dc452ca328d3),
    ], // 222
    [
        f64::from_bits(0xbe427bcdd3000000),
        f64::from_bits(0x3fe4397f44aa44f2),
        f64::from_bits(0x3fe8cc6a8771e165),
    ], // 223
    [
        f64::from_bits(0xbe360dded4000000),
        f64::from_bits(0x3fe44cf317a563db),
        f64::from_bits(0x3fe8bc8076122736),
    ], // 224
    [
        f64::from_bits(0xbe59a8f405c00000),
        f64::from_bits(0x3fe4605a2b02d705),
        f64::from_bits(0x3fe8ac875232f3ef),
    ], // 225
    [
        f64::from_bits(0x3e432777dc000000),
        f64::from_bits(0x3fe473b532bc5a67),
        f64::from_bits(0x3fe89c7e8713120c),
    ], // 226
    [
        f64::from_bits(0xbe51418a7b000000),
        f64::from_bits(0x3fe4870306ca20e2),
        f64::from_bits(0x3fe88c670a0ea774),
    ], // 227
    [
        f64::from_bits(0xbe3fed182e000000),
        f64::from_bits(0x3fe49a44886b5340),
        f64::from_bits(0x3fe87c401fdf05e5),
    ], // 228
    [
        f64::from_bits(0x3e486144d8000000),
        f64::from_bits(0x3fe4ad796ea1410c),
        f64::from_bits(0x3fe86c0a04dbacc5),
    ], // 229
    [
        f64::from_bits(0x3de1bc2e60000000),
        f64::from_bits(0x3fe4c0a14640d2af),
        f64::from_bits(0x3fe85bc51aa114c2),
    ], // 230
    [
        f64::from_bits(0xbe3f53d2fe000000),
        f64::from_bits(0x3fe4d3bc5aaa8cd5),
        f64::from_bits(0x3fe84b7121b30a13),
    ], // 231
    [
        f64::from_bits(0xbe12e100a0000000),
        f64::from_bits(0x3fe4e6cab91556be),
        f64::from_bits(0x3fe83b0e0e6b6ccc),
    ], // 232
    [
        f64::from_bits(0xbe2fa58c62000000),
        f64::from_bits(0x3fe4f9cc1c69fdde),
        f64::from_bits(0x3fe82a9c1c1ab463),
    ], // 233
    [
        f64::from_bits(0x3debb491e0000000),
        f64::from_bits(0x3fe50cc09fdcbd92),
        f64::from_bits(0x3fe81a1b3342f858),
    ], // 234
    [
        f64::from_bits(0x3e3a115410000000),
        f64::from_bits(0x3fe51fa82c3aa029),
        f64::from_bits(0x3fe8098b67ea8509),
    ], // 235
    [
        f64::from_bits(0x3e4ab0a5d3000000),
        f64::from_bits(0x3fe53282b20b96b6),
        f64::from_bits(0x3fe7f8ecc7919530),
    ], // 236
    [
        f64::from_bits(0xbe3cba0438000000),
        f64::from_bits(0x3fe5454fe43a7d7c),
        f64::from_bits(0x3fe7e83f96af78a0),
    ], // 237
    [
        f64::from_bits(0xbe20dd83a4000000),
        f64::from_bits(0x3fe5581033a81573),
        f64::from_bits(0x3fe7d783712e20ec),
    ], // 238
    [
        f64::from_bits(0xbe3e9a8299000000),
        f64::from_bits(0x3fe56ac33fbb8253),
        f64::from_bits(0x3fe7c6b8acf90fa6),
    ], // 239
    [
        f64::from_bits(0x3e2225c4aa000000),
        f64::from_bits(0x3fe57d6939d4b513),
        f64::from_bits(0x3fe7b5df1da18065),
    ], // 240
    [
        f64::from_bits(0xbe482e66e0000000),
        f64::from_bits(0x3fe59001b9e64d79),
        f64::from_bits(0x3fe7a4f72157cfdf),
    ], // 241
    [
        f64::from_bits(0x3e551a6a35400000),
        f64::from_bits(0x3fe5a28d5b36d597),
        f64::from_bits(0x3fe794002a7c9023),
    ], // 242
    [
        f64::from_bits(0x3e513917f4000000),
        f64::from_bits(0x3fe5b50b4e10bec1),
        f64::from_bits(0x3fe782faf6dc7ba2),
    ], // 243
    [
        f64::from_bits(0x3e149310cc000000),
        f64::from_bits(0x3fe5c77bc15ab4ef),
        f64::from_bits(0x3fe771e75c43942e),
    ], // 244
    [
        f64::from_bits(0x3e124d493c000000),
        f64::from_bits(0x3fe5d9dee9de49db),
        f64::from_bits(0x3fe760c529bc17b0),
    ], // 245
    [
        f64::from_bits(0xbe504638f7000000),
        f64::from_bits(0x3fe5ec347044e0f4),
        f64::from_bits(0x3fe74f94b0af9720),
    ], // 246
    [
        f64::from_bits(0xbe23f41b28000000),
        f64::from_bits(0x3fe5fe7cb834600c),
        f64::from_bits(0x3fe73e55936a5160),
    ], // 247
    [
        f64::from_bits(0xbe1a5f6f5c000000),
        f64::from_bits(0x3fe610b7515d1562),
        f64::from_bits(0x3fe72d083b8214eb),
    ], // 248
    [
        f64::from_bits(0x3e319fb2e0000000),
        f64::from_bits(0x3fe622e459eafbc1),
        f64::from_bits(0x3fe71bac8c7b0592),
    ], // 249
    [
        f64::from_bits(0xbe356d2c2b000000),
        f64::from_bits(0x3fe6350396fe4e62),
        f64::from_bits(0x3fe70a42bec51665),
    ], // 250
    [
        f64::from_bits(0xbe33c156c2000000),
        f64::from_bits(0x3fe64715385bed93),
        f64::from_bits(0x3fe6f8caa4969708),
    ], // 251
    [
        f64::from_bits(0xbe2f23e576000000),
        f64::from_bits(0x3fe659191d2fd57f),
        f64::from_bits(0x3fe6e7445d74f711),
    ], // 252
    [
        f64::from_bits(0x3e11e4be38000000),
        f64::from_bits(0x3fe66b0f41d484c4),
        f64::from_bits(0x3fe6d5afecd4938d),
    ], // 253
    [
        f64::from_bits(0xbe4397cc8d800000),
        f64::from_bits(0x3fe67cf76eac73df),
        f64::from_bits(0x3fe6c40d89625f63),
    ], // 254
    [
        f64::from_bits(0xbe3202f686000000),
        f64::from_bits(0x3fe68ed1e0990551),
        f64::from_bits(0x3fe6b25cf728c350),
    ], // 255
];

//! Compiles the vendored CORE-MATH functions (csrc/core-math, see its
//! README) into the extension.
//!
//! Flags matter here, because the whole point is the same bits on every
//! platform:
//! - no `-march=native` (core-math-sys's default): the wheels must run on
//!   any CPU of their target, and CORE-MATH is correctly rounded with or
//!   without FMA, so the baseline ISA loses nothing but speed;
//! - `-ffp-contract=off`: the compiler must not fuse `a * b + c` into an
//!   FMA on its own. CORE-MATH's error-free transformations assume the
//!   roundings written in the source (it uses explicit `__builtin_fma`
//!   where it wants one), and GCC contracts by default;
//! - no `-ffast-math` or anything like it.
//!
//! MSVC can't compile CORE-MATH (`__int128`, GCC builtins), so Windows
//! builds use clang-cl, which links into the same MSVC-ABI extension.

fn main() {
    const FUNCS: [&str; 7] = ["sin", "cos", "tan", "exp", "log", "log10", "pow"];
    let mut build = cc::Build::new();
    for f in FUNCS {
        build.file(format!("csrc/core-math/{f}/{f}.c"));
    }
    let target_env = std::env::var("CARGO_CFG_TARGET_ENV").unwrap_or_default();
    if target_env == "msvc" {
        build.compiler("clang-cl");
        // clang-cl spells GCC-style options with /clang:.
        build.flag("/clang:-ffp-contract=off");
        build.flag("/clang:-fno-fast-math");
    } else {
        build.flag("-ffp-contract=off");
        build.flag("-fno-fast-math");
    }
    build.opt_level(2).warnings(false).cargo_warnings(false);
    build.compile("core_math");
    println!("cargo:rerun-if-changed=csrc/core-math");
}

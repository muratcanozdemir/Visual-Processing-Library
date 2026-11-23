//! `java.lang.Math` transcendentals, the same on every platform.
//!
//! The Java oracle (HotSpot, x86_64) computes these two ways:
//! - `asin`, `acos`, `atan`, `atan2` delegate to `StrictMath`, i.e. fdlibm.
//!   The `libm` crate (musl's fdlibm-derived code) reproduces `asin`,
//!   `acos` and `atan` bit for bit: 0 differences against JDK 21 in 900,000
//!   inputs each spread over every exponent. Its `atan2` does not (see
//!   `atan2` below), so that one is fdlibm's, transcribed. The golden file
//!   `tests/goldens/jmath.txt` pins all four.
//! - `sin`, `cos`, `tan`, `exp`, `log`, `log10`, `pow` are HotSpot
//!   intrinsics (Intel's libm), which are not fdlibm and not public
//!   outside HotSpot. They are within a fraction of an ulp of the exact
//!   result, so the closest portable match is the correctly rounded
//!   result, which CORE-MATH computes (vendored in csrc/core-math). It
//!   agrees with Java on every `log`/`log10` input tested and on 99.76% to
//!   99.95% of `sin`/`cos`/`tan`/`exp`/`pow` inputs; the remaining cases
//!   are where Intel's code misrounds. Unlike the platform libm, the
//!   answer is the same on every OS and CPU.
//!
//! `sqrt` needs nothing: IEEE 754 makes it exact everywhere.
//!
//! These are the raw functions with C99 special-case semantics, which
//! match Java's for all of them except `pow` (Java's `pow(+-1, +-inf)` and
//! `pow(1, NaN)` are NaN); `vpt._javacompat` applies the Java special
//! cases before calling in.

use pyo3::exceptions::PyValueError;
use pyo3::prelude::*;
use pyo3::types::PyBytes;

unsafe extern "C" {
    fn cr_sin(x: f64) -> f64;
    fn cr_cos(x: f64) -> f64;
    fn cr_tan(x: f64) -> f64;
    fn cr_exp(x: f64) -> f64;
    fn cr_log(x: f64) -> f64;
    fn cr_log10(x: f64) -> f64;
    fn cr_pow(x: f64, y: f64) -> f64;
}

// The CORE-MATH functions are pure (they read the rounding mode and may
// set floating-point flags/errno, nothing else), hence the safe wrappers.
pub fn sin(x: f64) -> f64 {
    unsafe { cr_sin(x) }
}
pub fn cos(x: f64) -> f64 {
    unsafe { cr_cos(x) }
}
pub fn tan(x: f64) -> f64 {
    unsafe { cr_tan(x) }
}
pub fn exp(x: f64) -> f64 {
    unsafe { cr_exp(x) }
}
pub fn log(x: f64) -> f64 {
    unsafe { cr_log(x) }
}
pub fn log10(x: f64) -> f64 {
    unsafe { cr_log10(x) }
}
pub fn pow(x: f64, y: f64) -> f64 {
    unsafe { cr_pow(x, y) }
}
pub fn asin(x: f64) -> f64 {
    libm::asin(x)
}
pub fn acos(x: f64) -> f64 {
    libm::acos(x)
}
pub fn atan(x: f64) -> f64 {
    libm::atan(x)
}
/// fdlibm 5.3's `__ieee754_atan2` (e_atan2.c), transcribed, with
/// `libm::atan` (bit-identical to fdlibm's, see the module note) for the
/// general case. Not `libm::atan2`: musl short-circuits `|y/x| > 2^64` to
/// `+-pi/2`, where fdlibm -- and so Java -- computes `pi - (z - pi_lo)`,
/// one ulp away when `x < 0` (e.g. `atan2(1.0, -1e-300)`).
pub fn atan2(y: f64, x: f64) -> f64 {
    const TINY: f64 = 1.0e-300;
    const PI_O_4: f64 = f64::from_bits(0x3FE9_21FB_5444_2D18);
    const PI_O_2: f64 = f64::from_bits(0x3FF9_21FB_5444_2D18);
    const PI: f64 = f64::from_bits(0x4009_21FB_5444_2D18);
    const PI_LO: f64 = f64::from_bits(0x3CA1_A626_3314_5C07);

    if x.is_nan() || y.is_nan() {
        return x + y;
    }
    let (xb, yb) = (x.to_bits(), y.to_bits());
    let (hx, lx) = ((xb >> 32) as i32, xb as u32);
    let (hy, ly) = ((yb >> 32) as i32, yb as u32);
    let (ix, iy) = (hx & 0x7fff_ffff, hy & 0x7fff_ffff);

    if x == 1.0 {
        return libm::atan(y);
    }
    // 2 * sign(x) + sign(y)
    let m = ((hy >> 31) & 1) | ((hx >> 30) & 2);

    // y = 0
    if (iy as u32 | ly) == 0 {
        return match m {
            0 | 1 => y,
            2 => PI + TINY,
            _ => -PI - TINY,
        };
    }
    // x = 0
    if (ix as u32 | lx) == 0 {
        return if hy < 0 {
            -PI_O_2 - TINY
        } else {
            PI_O_2 + TINY
        };
    }
    // x = +-inf
    if ix == 0x7ff0_0000 {
        return if iy == 0x7ff0_0000 {
            match m {
                0 => PI_O_4 + TINY,
                1 => -PI_O_4 - TINY,
                2 => 3.0 * PI_O_4 + TINY,
                _ => -3.0 * PI_O_4 - TINY,
            }
        } else {
            match m {
                0 => 0.0,
                1 => -0.0,
                2 => PI + TINY,
                _ => -PI - TINY,
            }
        };
    }
    // y = +-inf
    if iy == 0x7ff0_0000 {
        return if hy < 0 {
            -PI_O_2 - TINY
        } else {
            PI_O_2 + TINY
        };
    }

    let k = (iy - ix) >> 20;
    let z = if k > 60 {
        // |y/x| > 2^60
        PI_O_2 + 0.5 * PI_LO
    } else if hx < 0 && k < -60 {
        // |y|/x < -2^60
        0.0
    } else {
        libm::atan((y / x).abs())
    };
    match m {
        0 => z,
        1 => -z,
        2 => PI - (z - PI_LO),
        _ => (z - PI_LO) - PI,
    }
}

fn read_f64s(b: &[u8]) -> PyResult<Vec<f64>> {
    let (chunks, rest) = b.as_chunks::<8>();
    if !rest.is_empty() {
        return Err(PyValueError::new_err("length is not a multiple of 8"));
    }
    Ok(chunks.iter().map(|c| f64::from_le_bytes(*c)).collect())
}

fn write_f64s<'py>(py: Python<'py>, v: impl Iterator<Item = f64>) -> Bound<'py, PyBytes> {
    let bytes: Vec<u8> = v.flat_map(f64::to_le_bytes).collect();
    PyBytes::new(py, &bytes)
}

macro_rules! unary {
    ($($name:ident / $arr:ident),* $(,)?) => {
        $(
            #[pyfunction]
            fn $name(x: f64) -> f64 {
                super::$name(x)
            }

            /// Elementwise over little-endian f64 bytes.
            #[pyfunction]
            fn $arr<'py>(py: Python<'py>, x: &[u8]) -> PyResult<Bound<'py, PyBytes>> {
                let v = read_f64s(x)?;
                Ok(write_f64s(py, v.into_iter().map(super::$name)))
            }
        )*

        pub fn register(m: &Bound<'_, PyModule>) -> PyResult<()> {
            $(
                m.add_function(wrap_pyfunction!(py::$name, m)?)?;
                m.add_function(wrap_pyfunction!(py::$arr, m)?)?;
            )*
            m.add_function(wrap_pyfunction!(py::pow, m)?)?;
            m.add_function(wrap_pyfunction!(py::pow_arr, m)?)?;
            m.add_function(wrap_pyfunction!(py::atan2, m)?)?;
            m.add_function(wrap_pyfunction!(py::atan2_arr, m)?)?;
            Ok(())
        }
    };
}

/// The Python-facing functions: `vpt._native.sin(x)`, and `sin_arr(bytes)`
/// over little-endian f64s, and so on.
mod py {
    use super::*;

    unary!(
        sin / sin_arr,
        cos / cos_arr,
        tan / tan_arr,
        exp / exp_arr,
        log / log_arr,
        log10 / log10_arr,
        asin / asin_arr,
        acos / acos_arr,
        atan / atan_arr,
    );

    #[pyfunction]
    pub fn pow(x: f64, y: f64) -> f64 {
        super::pow(x, y)
    }

    /// `x` elementwise (little-endian f64 bytes) to the scalar power `y`.
    #[pyfunction]
    pub fn pow_arr<'py>(py: Python<'py>, x: &[u8], y: f64) -> PyResult<Bound<'py, PyBytes>> {
        let v = read_f64s(x)?;
        Ok(write_f64s(py, v.into_iter().map(|a| super::pow(a, y))))
    }

    #[pyfunction]
    pub fn atan2(y: f64, x: f64) -> f64 {
        super::atan2(y, x)
    }

    /// Elementwise over two equally long little-endian f64 byte strings.
    #[pyfunction]
    pub fn atan2_arr<'py>(py: Python<'py>, y: &[u8], x: &[u8]) -> PyResult<Bound<'py, PyBytes>> {
        let (vy, vx) = (read_f64s(y)?, read_f64s(x)?);
        if vy.len() != vx.len() {
            return Err(PyValueError::new_err("y and x differ in length"));
        }
        Ok(write_f64s(
            py,
            vy.into_iter().zip(vx).map(|(a, b)| super::atan2(a, b)),
        ))
    }
}

pub use py::register;

//! Native kernels behind `vpt._native`.
//!
//! Each kernel is a drop-in replacement for one pure-Python hot loop and
//! must return *exactly* what the Python code returns (this port's rule is
//! bit-exact parity with the Java oracle). All of them are integer/boolean
//! computations, so there is no floating-point rounding to reproduce. The
//! Python code stays the reference: `vpt._accel` picks the kernel when the
//! extension is available, and `tests/test_native_parity.py` compares the
//! two on randomized inputs.
//!
//! Data crosses the boundary as `bytes` (one byte per mask pixel, or
//! little-endian integers), which numpy reads and writes without copies on
//! the Python side.

use pyo3::exceptions::{PyIndexError, PyValueError};
use pyo3::prelude::*;
use pyo3::types::PyBytes;

mod amoeba;
mod area;
mod cm;
mod jmath;
mod path;

fn check_len(name: &str, got: usize, want: usize) -> PyResult<()> {
    if got < want {
        return Err(PyValueError::new_err(format!(
            "{name}: expected at least {want} elements, got {got}"
        )));
    }
    Ok(())
}

/// Path-opening run lengths (`BPathOpening.path_run_lengths`).
///
/// `mask`: `xdim * ydim` bytes, nonzero = foreground, index `x + xdim * y`.
/// Returns `4 * xdim * ydim` little-endian `u32`s: the SN, WE, SWNE and NWSE
/// run lengths one after another (0 on background).
#[pyfunction]
fn path_run_lengths<'py>(
    py: Python<'py>,
    mask: &[u8],
    xdim: usize,
    ydim: usize,
) -> PyResult<Bound<'py, PyBytes>> {
    let n = xdim
        .checked_mul(ydim)
        .ok_or_else(|| PyValueError::new_err("image too large"))?;
    check_len("mask", mask.len(), n)?;
    let mask = &mask[..n];
    let out = py.detach(|| path::run_lengths(mask, xdim, ydim));
    let bytes: Vec<u8> = out.iter().flat_map(|v| v.to_le_bytes()).collect();
    Ok(PyBytes::new(py, &bytes))
}

/// Pixels of the 8-connected foreground components smaller than `area`
/// (`BAreaOpening`; `BAreaClosing` passes the inverted mask).
///
/// `mask`: `xdim * ydim` bytes, nonzero = foreground. Returns `xdim * ydim`
/// bytes, 1 where the pixel belongs to a component of fewer than `area`
/// pixels.
#[pyfunction]
fn small_components<'py>(
    py: Python<'py>,
    mask: &[u8],
    xdim: usize,
    ydim: usize,
    area: i64,
) -> PyResult<Bound<'py, PyBytes>> {
    let n = xdim
        .checked_mul(ydim)
        .ok_or_else(|| PyValueError::new_err("image too large"))?;
    check_len("mask", mask.len(), n)?;
    let mask = &mask[..n];
    let out = py.detach(|| area::small_components(mask, xdim, ydim, area));
    Ok(PyBytes::new(py, &out))
}

/// Amoeba structuring-element masks (`ComputeSEs`).
///
/// `pixels`: `xdim * ydim` little-endian `i64`s, the input's `getXYByte`
/// values. Returns `xdim * ydim` masks of `side * side` bytes each (pixel
/// order `x + xdim * y`; mask order as the `FlatSE`'s flat storage), where
/// `side` is the size `FlatSE.square(2 * size + 1)` ends up with.
#[pyfunction]
fn amoeba_masks<'py>(
    py: Python<'py>,
    pixels: &[u8],
    xdim: usize,
    ydim: usize,
    size: i64,
) -> PyResult<Bound<'py, PyBytes>> {
    let n = xdim
        .checked_mul(ydim)
        .ok_or_else(|| PyValueError::new_err("image too large"))?;
    check_len("pixels", pixels.len() / 8, n)?;
    let pix: Vec<i64> = pixels[..n * 8]
        .as_chunks::<8>()
        .0
        .iter()
        .map(|c| i64::from_le_bytes(*c))
        .collect();
    let out = py
        .detach(|| amoeba::masks(&pix, xdim, ydim, size))
        .map_err(PyIndexError::new_err)?;
    Ok(PyBytes::new(py, &out))
}

#[pymodule]
fn _native(m: &Bound<'_, PyModule>) -> PyResult<()> {
    m.add_function(wrap_pyfunction!(path_run_lengths, m)?)?;
    m.add_function(wrap_pyfunction!(small_components, m)?)?;
    m.add_function(wrap_pyfunction!(amoeba_masks, m)?)?;
    jmath::register(m)?;
    Ok(())
}

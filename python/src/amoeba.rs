//! `ComputeSEs._computeAmoeba` for every pixel: a transcription of the
//! Python loop, step for step (same LIFO stack, same neighbour order
//! `Tools.N`, same `tmpImg` bookkeeping including its byte wraparound), so
//! the result is identical even where the visiting order matters.
//!
//! Arithmetic: the Python computes
//! `L = int(tmp + 1 + 0.25 * abs(p - q))` in doubles. `p`, `q` are
//! `getXYByte` values (32-bit at most), so every intermediate is exact in a
//! double and `int()` truncates a non-negative value: `L` equals the integer
//! `tmp + 1 + |p - q| / 4` (floor division) computed here.

const N: [(i64, i64); 8] = [
    (1, 0),
    (0, 1),
    (-1, 0),
    (0, -1),
    (1, 1),
    (-1, -1),
    (-1, 1),
    (1, -1),
];

/// The side length `FlatSE.square(s)` produces: at least 3, and odd.
pub fn se_side(s: i64) -> i64 {
    let mut side = s;
    if side < 3 {
        side = 3;
    }
    if side % 2 == 0 {
        side += 1;
    }
    side
}

/// Truncating division, like Java's `/` (and `_javacompat.idiv`). Rust's
/// `/` on integers already truncates toward zero.
fn idiv(a: i64, b: i64) -> i64 {
    a / b
}

pub fn masks(pix: &[i64], xdim: usize, ydim: usize, size: i64) -> Result<Vec<u8>, String> {
    let n = xdim * ydim;
    let side = se_side(size * 2 + 1);
    let ss = (side * side) as usize;
    let half = idiv(size, 2);
    let (xd, yd) = (xdim as i64, ydim as i64);

    let mut out = vec![0u8; n * ss];
    // `tmpImg`: a ByteImage, so it stores values mod 256 and reads them
    // back as 0..=255.
    let mut tmp = vec![0u8; n];
    let mut stack: Vec<(i64, i64)> = Vec::new();
    let mut list: Vec<usize> = Vec::new();

    // `for y: for x:` as in ComputeSEs.execute.
    for ry in 0..yd {
        for rx in 0..xd {
            let base = (rx + xd * ry) as usize * ss;
            let se = &mut out[base..base + ss];
            stack.clear();
            stack.push((rx, ry));

            while let Some((x, y)) = stack.pop() {
                let x_p = size - (x - rx);
                let y_p = size - (y - ry);
                // adaptedSE.setXYBoolean(x_p, y_p, True): numpy indexing,
                // so a negative flat index counts from the end.
                let mut f = x_p + side * y_p;
                if f < 0 {
                    f += ss as i64;
                }
                if f < 0 || f >= ss as i64 {
                    return Err(format!(
                        "index {} is out of bounds for axis 0 with size {ss}",
                        x_p + side * y_p
                    ));
                }
                se[f as usize] = 1;

                let ci = (x + xd * y) as usize;
                if tmp[ci] == 0 {
                    tmp[ci] = 1;
                    list.push(ci);
                }
                let p = pix[ci];

                for (dx, dy) in N {
                    let (nx, ny) = (x + dx, y + dy);
                    if 0 <= nx && nx < xd && 0 <= ny && ny < yd {
                        let ni = (nx + xd * ny) as usize;
                        let q = pix[ni];
                        let l = tmp[ci] as i64 + 1 + (p - q).abs() / 4;
                        let t = tmp[ni] as i64;
                        if (t <= 0 || t > l) && l <= half {
                            // setXYByte wraps mod 256; l >= 1 here.
                            tmp[ni] = (l & 0xFF) as u8;
                            stack.push((nx, ny));
                            list.push(ni);
                        }
                    }
                }
            }

            for &i in &list {
                tmp[i] = 0;
            }
            list.clear();
        }
    }
    Ok(out)
}

//! `BPathOpening.path_run_lengths`: for each foreground pixel and each of
//! the 4 directions, `up + 1 + down`, where `up(p) = 1 + max(up of p's 3
//! "upward" neighbours)` (0 on background and outside the image) and `down`
//! likewise. The Python evaluates the recurrence in a scan order that visits
//! every neighbour before the pixel needing it; this does the same scans,
//! on a zero-padded buffer so out-of-image neighbours read 0 without bounds
//! checks. (Started from a fast-anything generated port, see
//! IMPROVEMENTS.md section 8.2, then adapted to this crate's interface.)

/// One recurrence pass. `rows`: outer loop over y (else over x); `y_rev` /
/// `x_rev`: descending ranges. `neigh`: the 3 (dx, dy) neighbours read.
#[allow(clippy::too_many_arguments)]
#[inline(always)]
fn run(
    pmask: &[bool],
    buf: &mut [u32],
    xdim: usize,
    ydim: usize,
    rows: bool,
    y_rev: bool,
    x_rev: bool,
    neigh: [(isize, isize); 3],
) {
    let w = xdim + 2;
    buf.fill(0);
    let d = neigh.map(|(dx, dy)| dx + dy * w as isize);
    let mut visit = |x: usize, y: usize| {
        let p = (x + 1) + w * (y + 1);
        if pmask[p] {
            let a = buf[p.wrapping_add_signed(d[0])];
            let b = buf[p.wrapping_add_signed(d[1])];
            let c = buf[p.wrapping_add_signed(d[2])];
            buf[p] = 1 + a.max(b).max(c);
        }
    };
    if rows {
        for i in 0..ydim {
            let y = if y_rev { ydim - 1 - i } else { i };
            for j in 0..xdim {
                visit(if x_rev { xdim - 1 - j } else { j }, y);
            }
        }
    } else {
        for j in 0..xdim {
            let x = if x_rev { xdim - 1 - j } else { j };
            for i in 0..ydim {
                visit(x, if y_rev { ydim - 1 - i } else { i });
            }
        }
    }
}

type Pass = (bool, bool, bool, [(isize, isize); 3]);

/// (upward pass, downward pass) per direction, in output order. Each tuple
/// is (rows, y_rev, x_rev, neighbours), matching the Python's loop nests.
const DIRS: [(Pass, Pass); 4] = [
    // SN: upward reads row y+1, downward reads row y-1.
    (
        (true, true, false, [(-1, 1), (0, 1), (1, 1)]),
        (true, false, false, [(-1, -1), (0, -1), (1, -1)]),
    ),
    // WE: upward reads column x-1, downward reads column x+1.
    (
        (false, false, false, [(-1, -1), (-1, 0), (-1, 1)]),
        (false, false, true, [(1, -1), (1, 0), (1, 1)]),
    ),
    // SWNE
    (
        (true, false, true, [(0, -1), (1, -1), (1, 0)]),
        (true, true, false, [(-1, 0), (-1, 1), (0, 1)]),
    ),
    // NWSE
    (
        (true, false, false, [(0, -1), (-1, -1), (-1, 0)]),
        (true, true, true, [(1, 0), (1, 1), (0, 1)]),
    ),
];

pub fn run_lengths(mask: &[u8], xdim: usize, ydim: usize) -> Vec<u32> {
    let n = xdim * ydim;
    let mut out = vec![0u32; 4 * n];
    if n == 0 {
        return out;
    }
    let w = xdim + 2;
    let psize = w * (ydim + 2);

    let mut pmask = vec![false; psize];
    for y in 0..ydim {
        let dst = (y + 1) * w + 1;
        for x in 0..xdim {
            pmask[dst + x] = mask[x + xdim * y] != 0;
        }
    }

    let mut up = vec![0u32; psize];
    let mut down = vec![0u32; psize];
    for (d, (u, dn)) in DIRS.iter().enumerate() {
        run(&pmask, &mut up, xdim, ydim, u.0, u.1, u.2, u.3);
        run(&pmask, &mut down, xdim, ydim, dn.0, dn.1, dn.2, dn.3);
        let o = &mut out[d * n..(d + 1) * n];
        for y in 0..ydim {
            let prow = (y + 1) * w + 1;
            for x in 0..xdim {
                if pmask[prow + x] {
                    o[x + xdim * y] = up[prow + x] + 1 + down[prow + x];
                }
            }
        }
    }
    out
}

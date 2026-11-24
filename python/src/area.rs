//! `BAreaOpening` / `BAreaClosing`: the pixels of every 8-connected
//! foreground component with fewer than `area` pixels.
//!
//! The Python (a transcription of the Java) flood-fills one component at a
//! time from a stack of `Point`s and clears the component in the output if
//! it is too small. Which pixels end up cleared depends only on the
//! components, not on the order they are filled in, so this computes the
//! same set with an index stack and a visited array.

const NEIGH: [(isize, isize); 8] = [
    (1, -1),
    (1, 0),
    (1, 1),
    (-1, 1),
    (-1, 0),
    (-1, -1),
    (0, -1),
    (0, 1),
];

pub fn small_components(mask: &[u8], xdim: usize, ydim: usize, area: i64) -> Vec<u8> {
    let n = xdim * ydim;
    let mut out = vec![0u8; n];
    let mut seen = vec![false; n];
    let mut stack: Vec<usize> = Vec::new();
    let mut comp: Vec<usize> = Vec::new();

    for start in 0..n {
        if mask[start] == 0 || seen[start] {
            continue;
        }
        seen[start] = true;
        stack.push(start);
        comp.clear();
        while let Some(p) = stack.pop() {
            comp.push(p);
            let (x, y) = ((p % xdim) as isize, (p / xdim) as isize);
            for (dx, dy) in NEIGH {
                let (nx, ny) = (x + dx, y + dy);
                if nx < 0 || ny < 0 || nx >= xdim as isize || ny >= ydim as isize {
                    continue;
                }
                let q = nx as usize + xdim * ny as usize;
                if mask[q] != 0 && !seen[q] {
                    seen[q] = true;
                    stack.push(q);
                }
            }
        }
        // `size < self.area` in the Python, size being the component's
        // pixel count.
        if (comp.len() as i64) < area {
            for &p in &comp {
                out[p] = 1;
            }
        }
    }
    out
}

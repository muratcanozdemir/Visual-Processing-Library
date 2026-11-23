# Python port — improvement work breakdown

Findings from a code-quality audit of `python/vpt/` (2026-08-18), scoped to the
Python port only (not `src/`). Organized as a work breakdown structure so work
can be picked up incrementally. Update status inline as items are completed.

Status legend: `[ ]` not started · `[~]` in progress · `[x]` done

---

## 1. Test/golden coverage gaps

### 1.1 `mm/gray` golden verification — README overclaims coverage
**Status:** `[x]` — extended `oracle/DumpMM.java` to exercise all 8, ran
`./build.sh` + `./scripts/run-tests.sh` (all Java harnesses still pass),
regenerated goldens via `python/tests/regen-goldens.sh` (confirmed via
`git diff --stat` that only `mm.txt`/`multi.txt` changed — no other golden
drifted), and extended `python/tests/test_mm_golden.py` to replay the same
calls. `GDMP` needed a concentric-ring profile image instead of the file's
existing flat two-plateau test image (degenerates to all-zero on it, same
shape as `MDMP`'s existing documented degeneracy in `test_multi_golden.py`).
All rows verified non-degenerate (≥2 distinct output values) before
committing to the golden. `test_mm_golden.py` passes bit-exact.
**Priority:** High — README says "fully golden-verified," it isn't.

The 8 files this closed: `GRankFilter`, `GInterpolation`, `GMedianImage`,
`GLambdaLeveling`, `GOCCO`, `GLaplacian`, `GContrastEnhancement`, `GDMP`
(all in `python/vpt/algorithms/mm/gray/`).

### 1.2 `mm/multi/connected/attribute/` — only structurally checked
**Status:** `[x]` — `MultiArea` already had golden coverage. The other 3
(`MultiSpectralVolume`, `MultiDiagonal`, `MultiGeometricVolume`) can't be
golden-checked by calling `.test(...)` directly the way
`test_mtattr_golden.py` does for the (differently-named-but-unrelated)
`mm.gray.connected.attribute` predicates: `MultiAttribute.test` takes a
`MyList<Point>`, and `MyList`'s constructor/`addFirst`/`addLast` are
package-private to `vpt.algorithms.mm.multi.connected.maxtree` (confirmed
by a failed test-compile), so an external oracle driver can't build one.
Routed through `MMaxTreeIncrAttrOpening.invoke` instead (which builds that
`MyList` internally as part of max-tree construction) in
`oracle/DumpMulti.java` + `test_multi_golden.py`, matching `MultiArea`'s
existing pattern — arguably a *stronger* check than a direct `.test()` call
would be, since a bit-exact whole-image match means `.test()` agreed on
every zone the max-tree produced, not one hand-picked pixel list. Also
surfaced a real naming collision worth knowing about: Java has two
unrelated classes both named `MultiSdev`/`MultiSdev2` in different packages
(`mm.gray.connected.attribute`, using `ArrayList<Point>`/`Attribute`, vs.
`mm.multi.connected.attribute`, using `MyList<Point>`/`MultiAttribute`) —
confirmed the Python port already mirrors both correctly as separate
classes in their respective packages, so no bug, just a note for next time
someone greps for "MultiSdev" and finds two hits.
**Priority:** Medium

The 3 files this closed: `MultiSpectralVolume`, `MultiDiagonal`,
`MultiGeometricVolume` (`python/vpt/algorithms/mm/multi/connected/attribute/`).
`MultiAttribute` itself is the predicate interface, not directly invokable.

### 1.3 `mm/binary/` + `mm/binary/geo/` — intentionally untested (no action needed)
**Status:** N/A, documented
8 files (`DistanceTransform`, `FeatureTransform`, `BOpeningByReconstruction`,
`BReconstructionByDilation`, `BGeodesicErosion`, `BGeodesicDilation`,
`BClosingByReconstruction`, `BClearBorderObjects`) hang on `BooleanImage`
input due to the known `getDouble` inversion bug (see `python/README.md`).
Already documented as an accepted gap — leave as is unless that bug is ever
fixed.

### 1.4 No coverage tooling configured
**Status:** `[x]` — added `pytest-cov>=4` to the `test` optional-dependency
group and a `[tool.coverage.run]`/`[tool.coverage.report]` section to
`pyproject.toml`. Run `pytest --cov=vpt --cov-report=term-missing` to see
the current report (baseline: ~84% line coverage repo-wide).
**Priority:** Low

**Deliberately not done**: no `--cov-fail-under` threshold is set. A
meaningful chunk of the ~16% gap is structurally unreachable rather than a
real gap -- e.g. the many `else: print("sorun var ...")` debug branches in
the HSY conversion family, proven dead for any in-range byte input (see
`NonUniformHSY733`/etc.'s vectorization notes above), and assorted
error/exception paths. Setting a single repo-wide number would either fail
CI on code that can't be tested, or be picked low enough to be meaningless.
Worth a follow-up pass to look at the report package-by-package and pick
real per-package floors, not a number invented without that review.

---

## 2. Java-semantics compat layer bugs

### 2.1 `Normalize.py` unguarded division — real bug
**Status:** `[x]` — both divisions now route through `jdiv`; `test_statistical_golden.py` still passes.
**Priority:** High — crashes where Java degrades gracefully.

`python/vpt/algorithms/statistical/Normalize.py:39-41`:
```python
self.output.setDouble(
    i, (self.input.getDouble(i) - imageMean + self.mean) / (imageSdev / self.sdev)
)
```
Plain Python `/` raises `ZeroDivisionError` when the caller passes
`sdev=0.0`; Java's `double` division silently yields `Infinity`/`NaN` and
continues (`src/vpt/algorithms/statistical/Normalize.java:36`). Fix: route
both divisions through `jdiv` (`python/vpt/_javacompat.py`), matching the
guard pattern already used next door in `ContrastStretch.py`.

### 2.2 `PCA.py` unguarded division — edge case
**Status:** `[x]` — now uses `jdiv`; `test_statistical_golden.py` still passes.
**Priority:** Low — only triggers on a degenerate 1×1-pixel input image.

`python/vpt/algorithms/statistical/PCA.py:84`:
```python
M = [[v * (1.0 / (size - 1)) for v in row] for row in M]
```
Same divide-by-zero-raises-instead-of-Infinity pattern as 2.1. Fix: use
`jdiv`, or at minimum document why it's considered low-risk if left as is.

---

## 3. NumPy vectorization (performance, no semantic change)

All of these are elementwise/order-independent operations where converting
scalar per-pixel Python loops to vectorized NumPy is safe — verify each with
a before/after golden-test run to confirm bit-exact agreement is preserved.

### 3.1 `algorithms/conversion/*.py` — highest-traffic package, biggest win
**Status:** `[x]` — all 21/21 files vectorized.
**Priority:** High (broad impact — touches every file in the package)

Nested `x,y` loops doing per-pixel scalar `getXYCDouble`/`setXYDouble` calls
for what's structurally an elementwise (or per-pixel-independent) formula —
vectorized via `getDoubleArray()/getByteArray().reshape(ydim, xdim, cdim)`
and `setDoubleArray()/setByteArray()`. All 12 done below are exercised by
`tests/test_conversion_golden.py` (`RGB2Gray` via `test_linhist_golden.py`)
and verified bit-exact after vectorizing:

- **Done:** `RGB2Gray`, `RGB2XYZ`, `XYZ2RGB` (using the existing
  `jint_arr`/`jround_arr` twins), `RGB2YUV`, `YUV2RGB` (also fixed a stale,
  self-contradicting inline comment on the clamp-to-0 behavior — it said
  ">255 is left alone" while the code beneath it clamped >255 to 0 too,
  matching the file's own correct top-level docstring), `RGB2AVG`,
  `RGB2RGB64` (divisor is a positive constant and inputs are always
  non-negative bytes, so plain `//` matches `idiv` exactly here — see the
  inline comment; didn't need an `idiv_arr` helper), `LAB2XYZ` (the `**3.0`
  power is a safe integer exponent, stays real for a negative base under
  numpy too — verified directly, no `errstate` needed), `XYZ2LAB` +
  `XYZ2LAB.scaleToByte` (the `**0.333333` cube root needed an
  `np.errstate(invalid="ignore")` guard since `np.where` evaluates both
  branches eagerly, including the not-taken branch's possibly-negative
  input — the discarded NaN never reaches the output), `RGB2cRGB` (proved
  the `jdiv(channel, s)` divide-by-zero case only ever hits the `jdiv(0,0)
  -> NaN` branch here, since `s == r+g+b == 0` implies every channel is 0
  given bytes are non-negative — collapses to a plain `np.where` without a
  general `jdiv_arr`), `Hue2Distance`, `Angular2Color`.

- **Also done, the remaining 9:** most of these turned out lower-risk than
  first assessed on a closer read -- only `RGB2HSY`, `HSY2RGB`, and
  `RGB2NUHSY733` (which duplicates `RGB2HSY.convert`'s formula) actually
  use `jacos`/`jdiv` per-pixel; `NonUniformHSY733`/`744`/`833` are pure
  floor+bucket quantization (no trig at all, same `np.select`-for-`elif`-
  chain shape as `XYZ2LAB`'s branches), and `DistanceFromHSYColor`/
  `EuclideanDistanceFromLABColor`/`EuclideanDistanceFromRGBColor` are
  elementwise distance formulas with at most a sigmoid (`math.exp`'s array
  twin `np.exp`, verified bit-exact against the golden before trusting it,
  same for `np.arccos`/`math.acos`).
  - Added `jdiv_arr`/`jacos_arr` to `_javacompat.py` as general primitives
    (not domain-restricted like `RGB2cRGB`'s `np.where` trick) since
    `RGB2HSY`'s `C1/C` ratio can hit a genuine `jdiv(0,0)` *or*
    `jdiv(nonzero,0)` case depending on pixel color, not just the single
    `jdiv(0,0)`-only shape `RGB2cRGB` got away with. Each verified against
    every scalar edge case (signed zero, ±inf, NaN cross product for
    `jdiv_arr`; out-of-[-1,1] and NaN for `jacos_arr`) via a standalone
    script before use, not just trusted by construction.
  - `RGB2HSY`'s existing `max_ulp=2` golden tolerance (a pre-existing,
    already-accepted 1-ulp divergence from Java, unrelated to vectorization)
    was confirmed unchanged: temporarily tightened to `max_ulp=0`, the
    *same* single mismatched value appeared on both the pre-vectorization
    and post-vectorization code (verified via `git stash`), proving the
    vectorization introduced zero additional precision drift.
  - `np.select` reproduces the Java `if`/`elif` chains exactly (first-match
    priority, same as `elif`); all branch arrays are safe to compute eagerly
    for every pixel since `jdiv_arr`/`jacos_arr` never raise or warn.

### 3.2 `algorithms/histogram/Histogram.py:31-35`
**Status:** `[x]` — vectorized with `np.bincount` per channel; golden tests pass, manual sanity check confirms per-channel counts sum to pixel count.
**Priority:** Medium

Per-pixel Python counting loop into a 256-bin array. Counting is
order-independent → replace with `np.bincount(channel_bytes, minlength=256)`
per channel.

### 3.3 `algorithms/histogram/Equalization.py:30-34`, `ContrastStretch.py:34-39`
**Status:** `[x]` — `Equalization` uses `np.cumsum` (same left-to-right accumulation, bit-exact) + array gather; `ContrastStretch` uses elementwise array division under the same `maximum != minimum` guard. Golden tests pass.
**Priority:** Medium

Per-pixel Python loops applying a per-channel LUT / min-max rescale — both
pure elementwise gather/arithmetic, vectorizable via `table[byte_array]` /
elementwise array ops.

### 3.4 `algorithms/linear/Convolution.py:49-54`
**Status:** `[x]` — restructured to loop over kernel offsets (not pixels),
accumulating a wraparound-shifted whole-image array per offset in the same
order as the original per-pixel `coords` loop (preserves float summation
order exactly). Also hoisted `coeffSum`/per-offset kernel value out of the
per-pixel loop -- it never depended on the pixel, only the offset, so the
original recomputed an identical value on every pixel. The wraparound
formula (`abs(imod(a, b))`) is applied unconditionally now instead of only
out-of-bounds, since it's a no-op for an already-in-bounds coordinate;
verified against `abs(imod(-1,5))==1` (Java-style) vs Python's `-1 % 5 ==
4` to confirm plain `np.mod` would have been wrong here. Golden tests
(`test_linhist_golden.py`, `test_linear2_golden.py`,
`test_nonflatse_golden.py`) pass bit-exact, including edge pixels of a
16×16 image against a 3×3 kernel (definitely exercises the wraparound
path).

---

## 4. Minor duplication

### 4.1 `_isSingle` copy-pasted
**Status:** `[x]` — extracted to `algorithms/segmentation/_util.py:isSingle`; `test_segmentation_golden.py` still passes.
**Priority:** Low

`python/vpt/algorithms/segmentation/NumberOfSingletons.py:35-49` and
`python/vpt/algorithms/segmentation/MergeSingletons.py:65-79` have a
verbatim-duplicated 8-neighbor same-label test. Extract to a shared module
function in `algorithms/segmentation/` taking `(img, x, y, xdim, ydim)`.

---

## 5. Trivial omission

### 5.1 `Tools.cleanupProcess` has no Python counterpart
**Status:** `[x]` — documented as an intentional exclusion in `python/vpt/util/Tools.py` (no code needed).
**Priority:** Very low

`src/vpt/util/Tools.java:123` `cleanupProcess(Process p)` is dead code in
Java (zero callers anywhere in `src/vpt`) and has no Python port. Low risk
either way — either port it for completeness or add a one-line docstring
note in `python/vpt/util/Tools.py` calling out the intentional exclusion,
consistent with how every other exclusion in that file is documented.

---

## 6. Second-round vectorization audit (2026-08-18, after this file's original scope closed)

A follow-up `Explore` audit, run after every item above was `[x]`, to look
specifically for more per-pixel Python-loop vectorization opportunities
across `python/vpt/` (not re-auditing sections 1-5's territory). All 7
findings closed; also surfaced and fixed a real correctness bug along the
way (6.8).

### 6.1 `mm/gray/GDilation.py`/`GErosion.py` — non-BOX shift-and-reduce
**Status:** `[x]` — `_executeGeneral()` added, non-BOX path routed through
it; original `_getMax`/`_getMin` kept as unused scalar reference twins.
Golden tests pass; verified bit-exact against scalar via a standalone
comparison script.

### 6.2 `mm/binary/BDilation.py`/`BErosion.py` — both paths
**Status:** `[x]` — discovered the "BOX" path wasn't the true O(n) van Herk
algorithm (still a per-pixel scan against the smaller hline/vline coordinate
sets), so vectorized both the general and BOX paths with the same
`_vectorizedMax`/`_vectorizedMin` helper.

### 6.3 `mm/gray/GRankFilter.py` — per-pixel gather+sort
**Status:** `[x]` — vectorized via clamped-shift stack + `np.sort`-based
order statistic. Also corrected a docstring overclaim: Python's
`list.sort()` has no total order over NaN (Timsort placement is an
implementation detail), while `np.sort`'s deterministic NaN-last actually
agrees with Java's `Double.compare` — flagged, not "fixed", since fixing it
would mean revisiting the scalar Python reference too, not just this file.

### 6.4 `algorithms/color/ColorMoments.py`/`ColorMomentInvariants.py` — duplicated `_moment()`
**Status:** `[x]` — the shared 27-/20-call `_moment(p,q,tmp,r,g,b)` helper
vectorized via a `_powArr` twin of `jpow` restricted to (and verified
bit-exact for) the only exponents ever passed (`p,q ∈ {0,1}`,
`r,g,b ∈ {0,1,2}`), plus `np.cumsum` (not `np.sum`) to preserve the exact
left-to-right double summation order of the original nested loop.

### 6.5 `algorithms/saliency/SaliencyMap.py` — integral-image build + per-scale distance
**Status:** `[x]` — both the summed-area-table build and the per-scale
box-average/colour-distance loop vectorized. The integral-image build
looked like a genuine sequential dependency but reduces to two independent
`np.cumsum` passes (over y, then over x) once you notice neither `s` nor
`ii`'s recurrence crosses the other's axis.

### 6.6 `algorithms/texture/OrientationMap.py` — gradient + dominant-orientation accumulation
**Status:** `[x]` — the centred-difference gradient vectorized via shifted
array views; the windowed doubled-angle accumulation restructured
offset-at-a-time (like `Convolution.py`) to preserve summation order. Added
`jatan2_arr` to `_javacompat.py` (verified bit-exact against `jatan2`).

### 6.7 `mm/gray/{nonflat,floating,adaptive}/*.py` — 6 weighted/adaptive files
**Status:** `[x]` — `GNFDilation`/`GNFErosion` (per-offset additive weight,
same shift-and-reduce shape as 6.1) and `GFloatingDilation`/
`GFloatingErosion` (real-valued SE offsets sampled via
`pgetXYCDouble`'s bilinear interpolation — vectorizable because the
fractional part of `x - p.x` is the same for every pixel `x` at a given
offset `p`, collapsing the per-pixel interpolation branch into one
decision per SE offset) all done with the flat-SE shift-and-reduce
pattern. `ADilation`/`AErosion` are genuinely per-pixel-adaptive (a
different SE per pixel, from `ComputeSEs`) so the uniform-shift trick
doesn't apply directly; vectorized the SE-coordinate-index dimension
instead (pad every pixel's coordinate list to the longest one in the
image, process one slot at a time across the whole image with per-pixel
offset arrays) — still pays one Python-level pass to gather each pixel's
coordinate list, but moves the O(coords) inner scan out of Python.

### 6.8 NaN-handling bug in `np.maximum`/`np.minimum`-based reductions — found and fixed
**Status:** `[x]` — while building 6.7's floating-SE file, noticed
`np.maximum(a, b)`/`np.minimum(a, b)` propagate a NaN operand, but the
scalar loops they replace (`if v > maximum: maximum = v`) silently *skip*
a NaN candidate (any comparison with NaN is false in Java/Python doubles),
so the vectorized max/min already committed for 6.1's `GDilation`/
`GErosion` and 6.7's `GNFDilation`/`GNFErosion` would diverge from the
scalar reference on NaN-containing input. Fixed by replacing
`np.maximum(dst, candidate, out=dst)` with
`np.where(candidate > dst, candidate, dst)` (elementwise IEEE-754 `>` has
the identical false-on-NaN behavior as Python's scalar `>`) in all four
files, and built 6.7's remaining files with the correct pattern from the
start. Verified via a standalone script injecting NaN pixels into
randomized images — 0 mismatches after the fix, reproducible mismatches
before it.
**Priority:** was High — a silent correctness regression in
already-committed code, not just a missed optimization.

---

## 7. Third-round vectorization audit (2026-08-19, after section 6 closed)

A follow-up 5-way parallel `Explore` audit covering every `python/vpt/algorithms/`
subpackage not yet swept by sections 1-6: `arithmetic/`, `logical/`, `point/`,
`geometric/`, `flatzones/`, `segmentation/`, `texture/`, `shape/`,
`statistical/`, `histogram/`, `frequential/`, `hough/`, `noise/`, `plants/`,
`features/`, `experimental/`, `color/`, and the remainder of `mm/`. Findings
below, grouped by package, roughly in priority order within each group.
Status legend as above; unmarked findings are `[ ]` not started.

### 7.1 `arithmetic/*.py` — 11 trivial elementwise files
**Status:** `[x]` — all 11 files plus `Equal.py` vectorized. Found a real
NaN-ordering divergence between files during implementation: two-arg Python
`min`/`max` favour whichever operand isn't NaN, and *which* operand wins
depends on argument order, so `Addition`'s `max(min(x,1),0)` happens to
match `np.clip` bit-exact but `Subtraction`'s `min(1,max(x,0))` does not
(a NaN pixel would come out `1.0`, not NaN) — each file reproduces its own
exact nesting via targeted `np.where` rather than a shared `np.clip`/
`np.minimum`/`np.maximum` call. Also discovered `np.power`/raw arithmetic
warnings needed `np.errstate(invalid="ignore")` since this project's
`pyproject.toml` promotes `RuntimeWarning` to an error. Full suite passes.
`Absolute`, `AbsSubtraction`, `AdditionC`, `Addition`, `Average`, `Maximum`,
`Minimum`, `Multiplication`, `SubtractionC`, `Subtraction` all loop
`for i in range(size): out[i] = f(in1[i], in2[i])` for a fixed scalar
formula (`+`, `-`, `*`, `abs`, `min`, `max`, clamped variants). All trivially
vectorizable via `getDoubleArray()`/`setDoubleArray()` and plain NumPy
elementwise ops — `min`/`max` need `np.minimum`/`np.maximum` *only* where
the Java equivalent is a real min/max (safe here, no NaN-skip semantics
apply since these are simple two-operand clamps, not accumulating
reductions like section 6.8). `Minimum.py` compares via `Tools.cmpr`'s
1e-8 epsilon rather than plain `<` — preserve via `(p1 - p2) <= 1e-8`-style
elementwise comparison, not `np.minimum`. `Equal.py` short-circuits with a
`print(..., file=sys.stderr)` on the *first* mismatch — needs care:
vectorize the mismatch detection (`np.where(~np.isclose(a, b, atol=1e-8))`
or equivalent `cmpr`-consistent comparison) but keep the single
first-mismatch print, e.g. via the first `True` index from the mismatch
mask, to preserve observable stderr behaviour bit-for-bit.
**Not in scope**: `VectorialMaximum.py`/`VectorialMinimum.py` — reduce via
an arbitrary caller-supplied `AlgebraicOrdering` over whole colour vectors,
same exclusion as `MDilation`/`MErosion`/`MRankFilter` (section 6 note).
**Priority:** High — highest-traffic package left unvectorized, all easy
wins, no correctness subtlety beyond `Equal`'s print and `Minimum`'s epsilon.

### 7.2 `logical/Quantization.py`
**Status:** `[x]` — vectorized as described; full suite passes.
Per-pixel byte-mask (`v &= _MASK[bit-1]`) over `getByteArray()`. Trivial
elementwise `&` vectorization (Python's `&` on a NumPy int array matches
the existing scalar semantics the file's own docstring already argues for).
**Priority:** Low (small file, cheap either way).

### 7.3 `point/{Inversion,Log,Power}.py`
**Status:** `[x]` — added `jlog10_arr` and `jpow_arr` to `_javacompat.py`.
`jpow_arr` resolves `jpow`'s exponent-only branches as one-time Python
scalars and vectorizes the base-dependent branches via `np.where`; sweep
testing found `np.power` disagrees with `math.pow` by 1 ulp for a small
fraction of inputs (e.g. exponent `-1.0`, `2.0`), so the generic bucket
deliberately calls per-element `math.pow()` (via a masked loop) instead of
`np.power`, needed to pass the existing `Power(di,-1.0)` golden test.
Full suite passes.
`Inversion` (`1.0 - p`) is trivial. `Log` (`jlog10(1+p)`) needs a
`jlog10_arr` added to `_javacompat.py` (mirrors the existing `jacos_arr`
pattern: `np.log10` already returns `-inf`/`nan` in the right places,
verify bit-exact before use, same as `jacos_arr`'s own docstring notes).
`Power` (`jpow(p, power)`) is the one with real work: `power` is a single
scalar per call, so most of `jpow`'s branches (`b==0`, `b==1`, `isnan(b)`,
`isinf(b)`) can be resolved *once* per call rather than per-pixel, leaving
only the `a`-dependent branches (`a` is nan/inf/zero/negative-non-integer-
exponent) to vectorize via `np.where` cascades — needs care to preserve
`jpow`'s Java-`Math.pow` special-casing (the `pow(±1, ±Infinity) == NaN`
divergence from IEEE 754 is the trickiest one) but is a bounded, well-
specified piece of work given the docstring's own case list.
**Priority:** Medium — `Power` is worth doing given `jpow`'s already-solved
scalar reference; `Inversion`/`Log` are cheap fill-ins alongside it.

### 7.4 `geometric/*.py`
**Status:** `[x]` — all 7 items done (`Crop`, `Flip`, `Rotation`, `Resample`,
`ImageCentroid`, `ToPolar`, `ConvexHull`'s border-scan loop); `KeepLargestCC`
correctly left untouched. Verified via standalone scripts (500+ randomized
cases for Rotation/Resample/ConvexHull) with 0 mismatches against the
original scalar loops. Full suite passes.
- `Crop.py` — pure sub-rectangle copy, no formula at all: replace the
  triple-nested loop with a single NumPy slice assignment on the
  `(cdim, xdim, ydim)`-shaped array view. Easiest possible win.
- `Flip.py` — pure axis-reversal copy: `np.flip`/reversed slicing on the
  byte array view. Trivial.
- `Rotation.py` — inverse-mapping per-pixel affine transform + optional
  bilinear interpolation. Needs care but fully vectorizable: build the
  `(x,y)` output-grid via `np.meshgrid` once, compute `_x`/`_y` arrays via
  the same affine formula, then gather via fancy indexing under the same
  in-bounds mask (nearest) or the same 4-tap bilinear blend (already an
  elementwise formula, no cross-pixel dependency) — output pixels outside
  the mapped-source bounds must stay at the `fill(0.0)` value, i.e. the
  scatter is masked, not unconditional.
- `Resample.py` — same shape as `Rotation`, three interpolation modes
  (nearest/bilinear/bicubic). The bicubic tap-and-blend is a fixed 4x4
  neighbourhood gather per output pixel (all elementwise formulas on
  clamped indices) — vectorizable the same way, just more taps. Real
  effort given the method-selection branching and 4x4 neighbour gather,
  but no sequential dependency anywhere.
- `ImageCentroid.py` — weighted-sum reduction (`sum(x*p)`, `sum(y*p)`)
  over the whole image. Vectorize via elementwise `x*p`/`y*p` grids +
  `np.cumsum(...)[-1]` (order-preserving, same trick as section 6's
  `Mean`/`Moment` family) in x-outer/y-inner order to match the loop.
- `ToPolar.py` — pure per-`(theta,rho)` gather formula (polar→cartesian
  sample coordinates, clamped to image bounds), no cross-cell dependency.
  Vectorizable via `np.meshgrid` over `(theta, rho)` + fancy-indexed gather.
- `ConvexHull.py` — only the per-pixel `borderImage` boolean-scan-and-collect
  loop (lines 68-72) is a whole-image loop; vectorizable via
  `np.argwhere`/`np.nonzero` in x-outer/y-inner order, writing into the
  pre-sized (intentionally oversized, see the file's own preserved-defect
  docstring) `border` list — must not change the oversizing behaviour.
  The rest of the file (`quickhull`, `drawLine`, dilation/fill-hole calls)
  is small-N or already-vectorized/out-of-scope elsewhere.
- **Not vectorizable** (confirmed genuine sequential/graph dependency, no
  action): `KeepLargestCC.py`'s 8-connectivity BFS flood-fill — classic
  queue-based connected-component labelling, same category as this
  project's other flood-fill/graph exclusions (`flatzones/`, max-tree,
  geodesic reconstruction).
**Priority:** `Crop`/`Flip` high (trivial, real cost savings on large
images); `Rotation`/`Resample`/`ToPolar`/`ImageCentroid`/`ConvexHull`
medium (real but bounded effort, no correctness subtlety beyond careful
masking).

### 7.5 `flatzones/` — no findings
**Status:** N/A, audited, nothing to do
All 20 files (`GrayFZ*`, `ColorFZ*`, `ColorQFZ*`, `AngularQFZ*`,
`QFZMultiMarginal`) are stack-based flood-fill with global mutable retry
state — genuine sequential dependency throughout. Don't re-audit.

### 7.6 `segmentation/*.py`
**Status:** `[x]` — all items done. `SuperposeBorders` needed care to avoid
a lossy whole-array byte round-trip on untouched pixels (used a
scratch-encoded image, copying only masked entries). `LabelsToMeanValue`/
`LabelsToMeanHue` transpose to x-outer/y-inner before `np.bincount` to
preserve the scalar loop's FP summation order. Full suite passes.
- `Threshold.py` — `output[i] = cmpr(input[i], threshold) >= 0`; vectorize
  via `(input_arr - threshold) >= -1e-8` (IEEE `>=` on NaN is already False
  in both Java and NumPy, no extra handling needed). Trivial.
- `SuperposeBorders.py` — `mask = (labelArr == 0)`, per-channel
  `outputImage[mask, c] = color[c]`. Trivial.
- `BorderToDominantNeighborLabel.py` — 9-offset shift-and-reduce reading
  only the untouched `labelImage` (no cascading), same shape as
  `GDilation._executeGeneral`: first nonzero shifted value wins, in
  scan order.
- `_util.py:isSingle` (used by `NumberOfSingletons.py`) — vectorize as an
  8-offset shift-and-compare: `~OR_over_8_offsets(labelArr == shifted &
  in_bounds)`.
- `LabelsToMeanValue.py` / `LabelsToMeanHue.py` — `np.bincount`-by-label,
  same pattern as `Histogram.py` (3.2); `LabelsToMeanHue` additionally
  reuses `jatan2_arr` (added in 6.6) on the per-label sin/cos sums. Needs
  care to match Java's x-outer/y-inner raster summation order.
- `HysteresisThreshold.py` — only the final `pixel < 1.0 → 0.0` cleanup
  loop is vectorizable (`output[output < 1.0] = 0.0`); the BFS seed/
  propagate core above it is genuinely sequential, leave as is.
- `MP.py` — final agreement-count loop → `np.count_nonzero(a == b)`;
  trivial but dwarfed by the flood-fill above it in the same file, low
  priority on its own.
- **Not vectorizable** (confirmed, don't re-flag): `MergeSingletons.py`
  (in-place Gauss-Seidel-style relabelling, later pixels see earlier
  writes), `OSR.py`/`MP.py`'s flood-fill cores, `Watershed.py`/
  `MarkerBasedWatershed.py` (hierarchical-queue propagation), `MeanShift.py`
  (iterative trajectory convergence). `Otsu.py`/`OtsuThreshold.py`'s
  O(254x256) variance table is bounded by the fixed histogram size
  regardless of image size — not a hot path, skip.
**Priority:** `Threshold`/`SuperposeBorders`/`BorderToDominantNeighborLabel`/
`_util.isSingle`/`HysteresisThreshold` tail high (trivial, reusable);
`LabelsToMeanValue`/`LabelsToMeanHue` medium.

### 7.7 `texture/*.py`
**Status:** `[x]` — all 3 items done. `ExtendedMorphologicalCovariance`
got a shared `_runningExtreme(stack, init, greater)` helper reproducing the
Java sentinel tie rule across all 7 methods (`_ritVarVar` intentionally
untouched, it was never one of the 7 targeted methods). Verified via 180+
randomized/tie-heavy/NaN-injected comparison cases; the only mismatches
were in `_ritMedMed` under NaN injection, matching the already-accepted
`np.sort`-vs-`list.sort()` NaN-ordering divergence documented for
`GRankFilter` (6.3) — not a new regression, not exercised by golden
coverage. Full suite passes.
- `ExtendedMorphologicalCovariance.py` — largest remaining per-pixel gap in
  the port. One recurring shape repeated across 7 internal methods
  (`_histoED`, `_histoOCDouble`, `_locHistoOC`, `_ritMinMin`, `_ritMaxMin`,
  `_ritMedMed`, `_ritAngleDiffAngleDiff`): per-pixel argmax/argmin across a
  small per-scale image stack under a **Java-sentinel tie rule**
  (`maxV` starts at `0.0` not `-inf`, `minV` starts at `DOUBLE_MAX`,
  first-strict-`>`-wins, not a plain `np.argmax`), then `np.bincount` the
  winning index into a histogram. Vectorize by stacking the `size`
  per-scale images into `(size, imageSize)` and doing a small sequential
  `np.where`-based running-argmax/argmin over that axis (preserving the
  sentinel/tie semantics exactly), then `bincount`. `_ritMedMed` also needs
  a per-pixel median-of-n via `np.sort` first (reuse the `GRankFilter`
  pattern from 6.3). Medium-large effort, one reusable helper across all 7.
- `MultiTextonHistogram.py` — fully vectorizable, no sequential dependency:
  Sobel-like RGB gradients via fixed offsets → `theta` via
  `gxy/(gxx*gyy+0.0001)` then `jacos_arr` → 4-level per-channel
  quantization (`np.select`) → 2x2-block texton majority-merge via boolean
  masks on strided slices (`[::2,::2]` etc.) → 4-direction co-occurrence
  histograms via `np.bincount` on masked shifted-equality arrays. Needs
  care: gradients are computed only over the interior (`range(1,dim-1)`)
  and implicitly zero on the border — must reproduce that exact
  zero-border rather than computing real values there via edge-clamped
  shifts.
- `OrientationHistogram.py` — `np.bincount(np.floor(orientation*bins), 
  minlength=bins)` after the existing `==1.0` nudge. Trivial.
- **No findings** (orchestration-only, all per-pixel work already delegates
  to vectorized `mm/gray` primitives): `MorphologicalCovariance.py`,
  `HorizontalMorphologicalCovariance.py`,
  `RotationInvariantMorphologicalCovariance.py`,
  `MorphologicalCovarianceFFT.py`, `SpatialMorphologicalCovariance*.py`
  (x3), `AreaGranulometry.py`, `CircularGranulometry.py`,
  `LineGranulometry.py`, `SpatialCircularGranulometry*.py` (x2),
  `SpatialLineGranulometry.py`, `MultiCircleFFT*.py` (x2),
  `MultiResolutionHistogram.py`, `AttributeProfile.py`,
  `RotationInvariantGaborFeatures.py`, `OrderedAngularVolumesHistogram.py`,
  `SmoothnessFactor.py`.
- **Not vectorizable**: `CCBasedAreaProfile.py` — recursive max-tree node
  traversal, same category as this project's other max-tree exclusions.
**Priority:** `ExtendedMorphologicalCovariance` and `MultiTextonHistogram`
high-value (large/clean wins respectively); `OrientationHistogram` trivial
fill-in.

### 7.8 `shape/*.py`
**Status:** `[x]` — **scope correction found during implementation**: only
5 of the originally-listed 8 files actually contain the duplicated
bbox-scan loop (`WidthFactor.py`, `NormalizedWidthFactor.py`,
`AreaWidthFactor.py`, `RegionalMomentsOfInertia.py`,
`RegionalMomentsOfInertia2.py`) — direct inspection showed
`CircularWidthFactor.py`/`CircularWidthFactor2.py` share a different,
order-sensitive distance-thresholded volume-accumulation loop, and
`CircularWidthFactor3.py` has a third distinct running-max-distance loop;
none of the three contain the bbox pattern. The 5 real files were extracted
to a new `python/vpt/algorithms/shape/_util.py:booleanBBox()` helper (same
precedent as `segmentation/_util.py:isSingle`); the 3 `CircularWidthFactor*`
files were left untouched rather than force-fit — they remain unaudited
for vectorization opportunities and could be a small follow-up someday.
Verified via 500 randomized boolean images (multiple fill densities,
including all-False) against the original scalar loop: 0 mismatches.
- **Original (partially incorrect) finding, preserved for context**: this
  round's audit described an "8-file" pattern including
  `CircularWidthFactor.py`, `CircularWidthFactor2.py`,
  `CircularWidthFactor3.py` — each starts with
  an identical nested `for y: for x:` loop finding the boolean foreground's
  `top`/`bottom` y-bounds via running min/max. Purely boolean/associative
  (no NaN/order concern) — vectorize via `np.nonzero`/`np.any` over the
  boolean array once, extract to a shared helper (same precedent as
  `_util.isSingle`, finding 4.1) rather than duplicating 8x.
- `Mean`/`Variance`/`SDev`/`Skewness`/`Kurtosis` (statistical, see 7.9) are
  called from many shape descriptors — no shape-side change needed once
  those are vectorized, just noting the dependency.
- **Not worth it** (small-N contour/boundary-point loops, not whole-image):
  `BasicShapeStatistics.py`, `FourierDescriptor.py`, chain-code traversal
  files — sequential by construction (each step depends on prior mask
  state) and operate on small point lists, not image-sized arrays.
- `Dispersion.py` — has the same running max/min-distance shape as
  `ContrastRatio.py` (7.9) but is already documented as hanging forever
  (calls the known-buggy `BFillHole` path) — not practically testable,
  skip until that separate bug is addressed.
**Priority:** the 8-file bbox pattern is the only real win here — mechanical,
zero subtlety, applies identically everywhere.

### 7.9 `statistical/*.py`
**Status:** `[x]` for `Mean`/`Variance`/`SDev`/`Skewness`/`Kurtosis`/
`Moment`/`CentralMoment`/`ContrastRatio`; `[x]` for `ZernikeMoment.py`
too (2026-11-23, see the update at the end of this item). `Moment`'s shared computation was factored into
`_momentAllChannels`, with `CentralMoment` calling it directly since its
`centroidX`/`centroidY` are provably always `0.0` (documented dead-code
bug), making its loop computationally identical to `Moment`'s, not just
mathematically. `ContrastRatio` handles the all-NaN/all-False-mask edge
case explicitly (unlike `np.nanmax`, must leave seeds `0.0`/`1.0`
untouched). `ZernikeMoment.py` was investigated and found to have a real
correctness landmine: NumPy's array `**` silently uses sqrt/square
fast-path shortcuts at exponents exactly `0.5`/`2.0` that diverge by 1 ulp
from CPython's scalar `**` (which the reference loop uses via
`(x*x+y*y)**(n/2.0-s)`) on ~0.1% of inputs — these exact exponents are
common in this formula. Left unmodified rather than risk a silent
divergence; still tracked as not-done. Full suite passes.
**Update 2026-11-23 — done.** The `**` landmine no longer applies: the
radial power now goes through `jpow_arr`, which calls `math.pow` per element
(never `np.power`), and atan2/cos/sin through the libm-backed `*_arr`
helpers (`_javacompat._libm_map`). The per-`s` coefficients are pixel-
independent and computed once with the original int32-wrapping code; the
pixel sums use `np.cumsum` with a leading `0.0` to keep the loop's strict
left-to-right rounding. Verified bit-identical to the previous scalar loop
on 1862 cases (byte/double/signed-zero images, odd and even sizes, every
valid `(n, m)` up to 14 plus `n` up to 40 for int32 overflow), ~6x faster
at 128x128. One deliberate behaviour change: a 1-pixel-wide/tall image
used to raise `ZeroDivisionError`; it now returns NaN, as Java does.
- `Mean.py` — `np.cumsum(getDoubleArray())[-1] / size`, same order-preserving
  trick as `Equalization.py`/`SaliencyMap.py`. Called from many other
  algorithms (shape descriptors, `Normalize`, `PCA`) — real hot path.
- `Variance.py`, `SDev.py` — same shape, `sum((p-mean)**2)` via `np.cumsum`.
- `Skewness.py`, `Kurtosis.py` — two independent running sums (`s1`, `s2`)
  off the same `p` in one loop; two separate `np.cumsum` calls reproduce
  each sum's own FP order independently (splitting doesn't perturb either).
- `Moment.py`, `CentralMoment.py` — triple-nested `c/x/y` loop,
  `sum((x+1)^mx * (y+1)^my * pixel)`. Vectorize like `ColorMoments` (6.4):
  build the `(x+1)**mx * (y+1)**my` weight grid, multiply by the channel
  plane, flatten in matching (x-outer/y-inner) order, `np.cumsum(...)[-1]`.
  `CentralMoment` is mathematically identical to `Moment` (dead centroid
  arrays, already documented) — same fix applies verbatim to both.
- `ContrastRatio.py` — running max/min over pixels (optionally masked),
  Java-`>`/`<`-skip-on-NaN semantics. Max/min are associative so order
  doesn't matter (unlike the sums above), but needs care: the seeded
  initial `max=0.0`/`min=1.0` and the all-NaN-input edge case (scalar loop
  leaves seeds untouched; `np.nanmax` on an all-NaN array warns/returns NaN
  — must special-case), same correctness category as section 6.8.
- `ZernikeMoment.py` — per-pixel disc test + complex accumulation with an
  `int32`-overflow-aware factorial-ratio recurrence per pixel. Real
  vectorization work (not a quick win): both the `int32` wraparound trick
  and the order-sensitive running complex sum need preserving exactly.
  Medium priority, likely low call frequency.
- **Not worth it**: `NMSE.py` (already-documented dead code, always
  returns 0.0/NaN via a preserved `Tools.VSub` bug), `ReversePCA.py`
  (data-marshaling copy loops, not compute).
**Priority:** `Mean`/`Variance`/`SDev`/`Skewness`/`Kurtosis` highest (trivial,
real hot path via other callers); `Moment`/`CentralMoment` same class as
already-closed `ColorMoments`; `ContrastRatio` smaller but same
correctness-sensitive category as 6.8, do alongside for consistency;
`ZernikeMoment` lowest (real effort, uncertain call frequency).

### 7.10 `histogram/*.py`
**Status:** `[x]` — all items done, including `SaturationWeightedHueHistogram`
via `np.add.at` (verified `.sum()`'s pairwise summation diverges from
naive sequential summation in the last bit, so `np.add.at` was required
for bit-exactness, not just index-order correctness) with the uncaught
out-of-range-index raise preserved as an authentic `IndexError`. Full
suite passes.
- `RGB64Histogram.py`, `RGB27Histogram.py`, `CustomHistogram.py`,
  `HSY744Histogram.py`, `HSY733Histogram.py`, `HSY833Histogram.py` — pure
  `output[bin] += 1.0` count-only scatter loops. `np.bincount` on the
  vectorized combined bin-index array, same pattern as the already-fixed
  `Histogram.py` (3.2). Integer counts, so summation order is irrelevant
  — no subtlety.
- `SaturationWeightedHueHistogram.py` — weighted scatter-add (`coeff`, not
  `+1`), gated by a saturation/luma mask. Needs care: per-bin sums are
  FP-order-sensitive (unlike the count-only files above, use `np.add.at`
  to preserve index-order accumulation), and the code relies on an
  uncaught out-of-range-index raise (`h` rounding to 12) — a manual bounds
  check must reproduce that raise, not silently clip.
- **Skip**: `Histogram2Image.py` — not a hot per-pixel loop (256x256 bar
  rendering), has its own documented preserved-quirk early-return.
**Priority:** the 6 count-only histograms are trivial, high-value;
`SaturationWeightedHueHistogram` medium (real correctness care needed).

### 7.11 `frequential/*.py` — no findings
**Status:** N/A, documented, don't re-audit
`DFT2D`/`IDFT2D`/`FFT`/`IFFT` all have expensive nested loops but are
already bytecode-confirmed non-functional (DFT2D/IDFT2D collapse to a
position-independent constant; FFT/IFFT always return all-zero via
`ComplexNumber.cSum`/`cDiff`'s mutate-and-return-zero bug). Optimizing dead
code whose output is already known in closed form isn't worth doing.

### 7.12 `hough/*.py`
**Status:** `[x]` — all 3 files done. `HoughTransformCircle.py`'s
`getXDim()`-instead-of-`getYDim()` bug preserved verbatim: votes scatter
into the real `(out_ydim, out_xdim)`-shaped array via `np.add.at`, which
naturally raises the same `IndexError` a genuine out-of-bounds write would
for `xdim > ydim` inputs, matching the existing golden test's
`pytest.raises(IndexError)` expectation. Full suite passes.
- `HoughTransformLine.py` — per edge pixel, sweeps 180 fixed (pixel-
  independent) theta values computing `r = x*cos(phi)+y*sin(phi)` and
  scatter-incrementing a saturating (`min(_,255)`) vote accumulator.
  Precompute the 180 `phi`/index values once, vectorize `r` as an outer
  product over edge-pixel coords x thetas, replace the saturating scatter
  with a plain vote-count histogram (`np.add.at`/`bincount`) clipped to
  255 at the end — safe because every vote is a uniform +1, so the final
  saturated value is order-independent.
- `HoughTransformLine2.py` — same accumulator shape, one vote per edge
  pixel (its own gradient-direction cell from a `Sobel` map, no theta
  sweep). Vectorizable via boolean-masked coordinates + 2D histogram
  scatter, same saturation argument as above.
- `HoughTransformCircle.py` — per edge pixel, loops candidate radii
  computing `(a,b) = (x-r*cosφ, y-r*sinφ)`, saturating-scatter vote.
  Same pattern, vectorize over `r` then edge pixels. **Preserve verbatim**:
  a real bug where the `b`-bounds check uses `getXDim()` instead of
  `getYDim()` — do not "fix" while vectorizing.
**Priority:** Medium-high — all 3 share one scatter-histogram-with-
saturation vectorization idea, worth doing together.

### 7.13 `noise/*.py` — low priority, not golden-tested
**Status:** N/A, documented
`Gaussian.py`/`Speckle.py` are per-pixel but unseeded/non-deterministic RNG,
not golden-verified — vectorizable via `np.random.normal`/`np.random.random`
but low value given no correctness harness exists to verify against.

### 7.14 `plants/Angles.py`
**Status:** `[x]` — vectorized with tie-break order preserved. Fuzz-testing
(4000+ random trials plus explicit tie constructions) surfaced two genuine
edge-case bugs in the first vectorization attempt, both fixed: (1) the
`right`-scan's sentinel is `0`, a legitimate coordinate, so it must not
update when every `True` pixel is confined to column 0; (2) the `stalk`
branch's guard used `isEmpty(0)` (double view) as a proxy for "some pixel
is True", which can diverge from the boolean view for certain `Image`
subtypes — fixed by guarding directly on the boolean mask. Full suite
passes.
Two per-pixel-over-image argmin/argmax scans with Java-loop-order-dependent
tie-breaking (min-y-then-min-x, min-x-then-min-y, max-x-then-min-y). Needs
care: vectorizable via masked `np.where`/`argmin`, but the tie-break order
must reproduce first-occurrence-in-scan-order semantics exactly, not just
"the" min/max point. Other `plants/` files (`PetioleRemoval`, `VeinProfile`,
`DominantPointsCharacterization`) have no whole-image per-pixel loop — no
findings.
**Priority:** Low-medium — smaller win, real tie-break care needed.

### 7.15 `color/*.py`
**Status:** `[x]` — all 3 files done. **Found and fixed a real bug during
implementation** in `AutoCorrelogramHSY733.py`: the scalar code's
colour-match test is component-wise (`p0==p0' and p1==p1' and p2==p2'`),
not a comparison of the combined index — comparing combined indices alone
is *not* equivalent when a channel isn't range-restricted to its expected
digit width, producing false-positive matches (the golden test's own
fixture exercises exactly this). Caught via the golden test failing,
diagnosed by diffing against the pre-vectorization scalar code bin-by-bin,
fixed by comparing the three shifted channel planes separately instead of
a combined index. Full suite passes.
- `AutoCorrelogramHSY733.py` — per distance/per SE-ring-offset shift-and-
  reduce: shift the quantized-colour-index image, compare in-bounds
  matches, accumulate into a 63-bin histogram via weighted `np.bincount`,
  same pattern as `Histogram.py` (3.2).
- `BIC.py` — quantize RGB (`idiv(_,64)`, trivial since inputs are
  non-negative bytes), classify each pixel interior/border via 4-neighbour
  shifted comparison (needs care: out-of-bounds neighbours are *skipped*,
  not treated as mismatches), then two 64-bin histograms.
- `ColorStructureRGB64.py` — already-documented preserved bug: the
  neighbour-offset read always re-reads the *centre* pixel, collapsing the
  whole SE loop to "increment this pixel's bin once if it has >=1
  in-bounds SE offset." Vectorize via quantized-colour-index + an
  almost-always-true in-bounds mask, one `np.bincount` call.
**Priority:** Medium — all 3 are the established shift-and-histogram
pattern, do together; `ColorMoments`/`ColorMomentInvariants` already
closed (6.4).

### 7.16 `experimental/Weird.py`
**Status:** `[x]` — vectorized. Unlike `GRankFilter`'s clamp-to-edge, this
file *excludes* out-of-bounds offsets, so neighbourhood size varies per
pixel — solved with NaN-padded stack slots plus a separate geometric
`validCount`. Standalone verification confirmed 0 mismatches on real data;
NaN-injected data showed mismatches only in the same accepted category
`GRankFilter` already documents (Python `list.sort()`'s unspecified NaN
placement vs. `np.sort`'s deterministic one). Full suite passes.
Per-pixel/per-channel clamped-SE-offset gather (in-bounds only) + sort +
median-vs-min selection — structurally identical to `GRankFilter.py`
(6.3). Reuse that exact clamped-shift-stack + `np.sort` pattern directly.
`AreaLimits.py` has no whole-image per-pixel loop — no finding.
**Priority:** High — direct reuse of an already-proven pattern, near-free.

### 7.17 `features/QFZBasedStableRegions.py`
**Status:** `[x]` (2026-11-23). The per-pixel scale scan is vectorized: a
`(scale, x, y)` component-area array, a run-length scan per scale pair with
a vectorized Java-int-division `relativeAreaDiff`, `argmax` (first
maximum, matching the strict-`>` scan), and the painting loop visits each
kept zone object once in the original x-outer/y-inner order, preserving the
skip-if-first-point-painted quirk. Verified identical output to the
previous loop on 20 images (checker/ramp/noise/blobs/flat, 17x53 to 64x64,
outputs ranging from all-False through partial to all-True). Honest
payoff: small. Profiling a 64x64 image showed the 15 upstream
`GrayQFZSoille` calls take ~70% of the runtime and this loop ~15%, so the
end-to-end gain is ~10%. `GrayQFZSoille` is the real hot path if this
algorithm ever needs to be faster.
**Follow-up 2026-11-23:** `GrayQFZSoille` itself was then sped up ~2.5x
(same priority-flood, step for step, on flat Python lists instead of
per-pixel Image accessors and `Point` objects, with `deque` FIFO buckets
instead of `list.pop(0)`). Label images identical to the previous code on
792 cases (byte/double input, 1x1 to 31x17, alpha/omega from 0 to 254).
`QFZBasedStableRegions` on a 64x64 image went from ~3.2s to ~1.0s.
`flatzones/color/ColorQFZSoille.py` has the same shape and would take the
same treatment.
Data-dependent variable-length per-pixel scan over irregular connected-
component-list structures (`zones[s][x][y]` is a shared Python list per
component, size varies per pixel/scale). Real structural work: would first
need `GrayConnectedComponents2`'s output converted to an array-based
per-scale component-size lookup (labeled image + `bincount`), then the
per-pixel while-loop reduces to a run-length-of-consecutive-True scan over
a small (15-scale) axis, outer-looped over scales with vectorized inner
`(x,y)` work — not a drop-in change. Also gated by an already-documented
preserved bug (`relativeAreaDiff`'s truncating `idiv` mostly forces the
stability check true), which somewhat caps the practical value of
vectorizing it.
**Priority:** Lowest in this round — real effort, moderate value, real
structural refactor required. Defer unless it shows up as a hot path.

### 7.18 `mm/` remainder (everything not closed by sections 1-6)
**Status:** `[x]` for `GLambdaLeveling`/`GAreaOpening`/`GPathOpening`/
`GFillHole`/`MOCCO`/`GOCCO`/`MMaxGradient`/`MEuclideanGradient`/
`DistanceTransform`'s inner stamp loop; `[ ]` **deliberately deferred**
for the `mm/gray/connected/attribute/*.py` (14 files) and
`mm/multi/connected/attribute/*.py` (6 files) predicate families — lowest
priority of this section's findings, real per-call NumPy overhead vs.
typically-small max-tree node regions, only worth it if profiling shows
attribute-opening/closing as a hot path. **Profiled 2026-11-23, closed as
not worth doing:** on a 96x96 structured image, time inside attribute
`test()` was 0% of `GMaxTreeIncrAttrOpening` + `Area`, 2% of
`GMaxTreeNonIncrAttrOpening` + `Sdev`, and 11% with `Entropy`. Vectorizing
would still need a Python pass over each node's point list to gather
coordinates, so the most it could save is a fraction of those
percentages. The max-tree construction/filtering around them is where the
time goes. `GLambdaLeveling`'s two threshold
branches (`i > m+lambda`, `m > i+lambda`) are mathematically mutually
exclusive for `lambda >= 0`, so the scalar `if`/`elif` and unordered
`np.where` selection are provably equivalent. `DistanceTransform`'s outer
loop remains a documented pre-existing hang-forever bug (not golden-tested,
per section 1.3) — only its inner loop was vectorized. Full suite passes.
- `mm/gray/GLambdaLeveling.py` — **highest-value mm finding this round**.
  Fixed-scalar-formula (not arbitrary-Ordering) neighbourhood min/max scan,
  identical shape to the already-closed `GDilation`/`GErosion`
  shift-and-reduce, but run inside a `while True:` fixpoint loop until
  `Equal.invoke` converges — today's per-iteration Python cost is
  `O(width*height*cdim*|SE|)`, repeated an unbounded number of times.
  Directly vectorizable with the established technique; the fixpoint
  wrapper amplifies the win.
- `mm/gray/connected/GAreaOpening.py`, `mm/gray/path/GPathOpening.py` —
  identical pattern: per-threshold (0-255) binary-mask overwrite
  (`output[mask] = t`), later (higher) thresholds always win since the
  area/path-opened sets only shrink. Same shape as the already-fixed
  `Equalization`/`ContrastStretch` LUT pattern (3.3).
- `mm/gray/geo/GFillHole.py` — two loops: a boolean-mask zero-out
  (`arr[rmin_arr > 0] = 0`) and a border-marker build (`marker =
  np.where(border_mask, np.where(img>0, 255-img, img), 0)`), both pure
  elementwise formulas with no cross-pixel dependency.
- `mm/multi/MOCCO.py`, `mm/gray/GOCCO.py` — trivial elementwise average
  of two already-computed images, `(p1+p2)/2.0`. Do both together
  (identical shape).
- `mm/multi/MMaxGradient.py` — trivial `np.max(gradientArr, axis=-1)` over
  channels of a `ByteImage` — safe here specifically because byte channels
  can never hold NaN (unlike the NaN-hazard files fixed in 6.8).
- `mm/multi/MEuclideanGradient.py` — per-pixel Euclidean norm over
  channels; needs the `np.cumsum`-for-summation-order treatment (matches
  `Tools.EuclideanNorm`'s left-to-right accumulation) rather than plain
  `np.sum`.
- `mm/binary/DistanceTransform.py` inner stamp loop — trivial boolean-mask
  assignment (`output[diff_mask] = i`), but low practical value: the
  file's own docstring documents the outer loop hangs forever on its
  intended `BooleanImage` input (same `getDouble`-inversion bug as
  `BFillHole`, section 1.3) — vectorizing the inner loop alone doesn't fix
  that.
- `mm/gray/connected/attribute/*.py` (14 files) and
  `mm/multi/connected/attribute/*.py` (6 files) — flat, non-incremental
  per-max-tree-node scans (sum/mean/histogram/min-max) over a per-region
  `pixels` list, called once per node during tree pruning. Vectorizable in
  principle (same `np.cumsum`-preserved-summation approach as
  `ColorMoments._moment`, 6.4) but each call operates on a typically-small
  region inside a per-node Python callback — per-call NumPy overhead
  likely exceeds the benefit. Lowest priority of the mm findings; only
  worth it if profiling shows attribute-opening/closing as a hot path.
- **Not vectorizable — genuine sequential/graph dependency (confirmed, no
  action, don't re-flag)**: the entire geodesic-reconstruction family
  (`gray/geo/FastGrayReconstruction.py`, `G{Reconstruction,Geodesic}By*`,
  `GFillHole`'s own reconstruction call, `G{Regional,H}{Maxima,Minima}`,
  `G{Opening,Closing,TopHat,BotHat}ByReconstruction`, and the `binary/geo/`
  and `multi/geo/` equivalents — Vincent's raster/anti-raster + FIFO
  propagation, each pixel depends on already-updated same-pass neighbours);
  max-tree construction and traversal (`MaxTree.py`,
  `MultivariateMaxTree.py`, all `G{,Non}IncrAttr*`/`GMaxTreeIncrMultiAttr*`/
  `MMaxTreeIncrAttr*`/`GAreaOpening`'s tree-walking siblings — union-find/
  DFS, not per-pixel formulas); `gray/adaptive/ComputeSEs.py`'s per-pixel
  weighted flood-fill ("amoeba" SE construction, Dijkstra-like graph
  search); arbitrary-`Ordering`/`AlgebraicOrdering` files
  (`mm/multi/MLambdaLeveling.py`, `mm/multi/geo/*` — same exclusion class
  as `MDilation`/`MErosion`/`MRankFilter`); `binary/path/BPathOpening.py`/
  `gray/path/GPathOpening.py`'s *path-tracing* SE construction (distinct
  from the already-flagged threshold-overwrite loop above) — sequential
  neighbour-chases-neighbour, same category as the reconstruction family.
- **Preserved quirk, not a new bug**: `mm/multi/geo/MFastReconstruction.py`
  `_minForward`'s two CONNEXITY8 diagonal branches call `or_.max(val, ...)`
  without assigning back (unlike the other three direction methods) —
  already documented in the file's own docstring as transcribed-as-found.
**Priority:** `GLambdaLeveling` highest (fixpoint amplifies the win);
`GAreaOpening`/`GPathOpening`/`GFillHole`/`MOCCO`/`GOCCO`/`MMaxGradient`/
`MEuclideanGradient` all clean trivial-to-easy wins; `DistanceTransform`
and the attribute-predicate files lowest (real but marginal value).

---

## 8. Profiler-driven hotspot scan (2026-11-23, fast-anything)

**Status:** `[x]` 8.1-8.3 implemented in pure Python, each bit-identical
to the code it replaced (2026-11-23); full suite 9.7s -> 3.7s. Second scan
the same day: the remaining hotspots (8.2's kernel, 8.4, 8.5) now have
**Rust kernels**, shipped as the `vpt._native` extension built by maturin
(8.6); suite 3.5s -> 1.4s.

**How it was found.** [`fast-anything`](https://github.com/EL4CTEO/fast-anything)
v0.8.1 (commit `445abd1`, reviewed before use) profiled the golden test
suite, which exercises every ported algorithm on the Java-verified inputs:

```sh
cd python
fast-anything scan -- .venv/bin/python -m pytest -q -p no:cacheprovider
```

Its state lands in `python/.fast/` (gitignored). Its shares are inclusive
(a caller's share counts its callees) and measured under a profiler, so
they add up to more than 100% and overstate pure-Python overhead.
Unprofiled, `pytest --durations` puts `test_shape_golden.py` at 5.9s of a
9.5s suite. The scan also ranked three test functions
(`test_shape_…`/`test_misc_…`/`test_flatzones_…`); those aren't targets,
since their time is spent inside the library functions below.

**Caveat on fast-anything itself.** It ports functions to Rust and checks
the port by replaying recorded calls, accepting floats within a *relative
tolerance of 1e-9* (`src/python.rs`). This port's rule is bit-exact parity
with the Java oracle, so a fast-anything port is only acceptable where that
tolerance can't hide anything (integer/boolean code), and must still pass
the golden suite. Native code would also turn the single pure-Python wheel
into per-platform wheels. A port should therefore be an optional fast path,
with the Python code kept as the reference.

### 8.1 `frequential/FFT.py` (+ `util/ComplexNumber.cDiv`, 4.8M calls)
**Share:** ~57% of profiled suite time (39 calls; `shape.FPFT` and ~15
texture algorithms call it).
**Why slow:** recursive radix-2 FFT over millions of `ComplexNumber`
objects.
**Key fact:** the module docstring already documents it as
*non-functional*: `ComplexNumber.cSum`/`cDiff` (preserved Java defect)
return a fresh zero, so every butterfly is `0+0i` and the output is all
zeros for any input. Every caller asks for `outputType=MAG` (2), whose
values are then exactly `+0.0` everywhere (magnitude of a signed zero is
`+0.0`; `shiftOrigin` of zeros is zeros).
**Done — pure Python, not Rust:** `MAG` output now short-circuits to the
zero-filled `DoubleImage(input, False)` (then `shiftOrigin` if `shifted`,
as before), keeping the full transcription for `REAL`/`REAL_IMAG`/
`MAG_PHASE`, where the signed zeros the butterflies produce
(`0 * negative twiddle = -0.0`) are observable. **Correction to the
original finding:** when the padded transform size is 1 (a 1x1 image) the
recursion's base case returns the pixel itself, so the output is *not*
zero there; the shortcut requires a padded size >= 2. Verified bit-for-bit
against the full transcription on 624 outputs (all four image types,
1x1/1x2/2x1/odd/multi-channel sizes, NaN/+-inf/-0.0 pixels, both `shifted`
values, all four output types). Porting it to Rust would have spent effort
re-implementing a bug.

### 8.2 `mm/binary/path/BPathOpening.py`
**Share:** ~17% (1,279 calls: `mm/gray/path/GPathOpening` calls it once per
gray level, and `plants/VeinProfile` (~15%) is `GPathOpening` underneath).
**Why slow:** memoized per-pixel recursion in four directions.
**Done in Python:** the 8 memoized recursive helpers compute a longest path
over a DAG, a pure function of the foreground mask, so they're replaced by
one module-level kernel, `path_run_lengths(mask, xdim, ydim)`, that
evaluates the same recurrence iteratively in a dependency-respecting scan
order per direction. Everything around it (mask read like `getXYBoolean`,
`ByteImage` storage wrapping mod 256, `Maximum`, `Threshold`) is unchanged.
Identical to the recursive code on 564 cases (Boolean/Byte/Integer/Double
inputs incl. 3-channel, random/line/full masks, runs > 255). 2.4x faster,
and it can no longer hit Python's recursion limit on large images.

**Rust port: shipped (8.6).** The note below is from the first scan; the
kernel now lives in `python/src/path.rs`. The kernel takes
plain data, so fast-anything could port it: `fast-anything port
vpt.algorithms.mm.binary.path.BPathOpening.path_run_lengths --agent claude`
produced a PyO3 crate (reviewed: same scan orders and neighbours, zero-
padded border, no `build.rs`, only `pyo3`) on the first attempt; 508/508
replayed calls identical (integer results, so the 1e-9 tolerance is moot).
With it enabled, the golden suite passes and the 564-case comparison still
matches. Speed: `BPathOpening` 128x128 120ms (recursive) -> 52ms (Python
kernel) -> 4ms (Rust); `GPathOpening` 64x64 2.75s -> 0.23s. fast-anything's
own `enable` shim imports the extension from `python/.fast/` (gitignored),
so it can't ship; the generated code was adapted into the maturin crate
instead (bytes in/out, no fast-anything hook). The `.fast/` port is now
redundant -- leave it disabled.

### 8.3 `flatzones/angular/AngularQFZ2.py`
**Share:** ~7%.
**Why slow:** `_createQCC` recomputes the maximum `Tools.hueDistance` over
*every pair* in the growing zone each time it adds a pixel -- O(n^2) per
step, so O(n^3) per zone.
**Done — pure Python, exact:** a running `maxDist`; each added pixel is
compared only against the points already in the zone. The zone list only
grows within one `_createQCC` call (the caller clears it before every call
and retry) and `max` involves no rounding, so the result is identical:
O(n) per step. `hueDistance` is `abs`/compare/`1.0 - a`, symmetric, and a
NaN distance is skipped by the `>` comparison either way. Identical labels
on 80 cases (random/banded/wraparound/flat hue fields, alpha/omega incl.
retry-forcing ones), ~9x faster; the running max matched the full rescan on
3000 random value lists with NaN/+-inf/signed zeros.

---

### Second scan (2026-11-23, after 8.1-8.3)

Same command, on `main` at `27a68dd`. Unprofiled suite 3.68s, of which
`test_misc_golden` (path openings) took 1.9s. Ranked by cumulative time
under cProfile (12.4s profiled), per `execute`:

| Algorithm | Share | Calls | Notes |
|---|---|---|---|
| `GPathOpening` / `BPathOpening` / `VeinProfile` | ~45% | 5 / 1,279 / 1 | one chain; 36% is 8.2's kernel |
| `GAreaOpening` / `BAreaOpening` | ~17% | 8 / 2,051 | 8.4; also `GAreaClosing` (13%), `AreaGranulometry` (11%) |
| `ComputeSEs` | ~6% | 1 | 8.5 |
| everything else | <2% each | | |

Rust feasibility rule from the caveat above: only integer/boolean kernels,
where "exact" is unambiguous. All three qualify. With them in place the
profile is flat (re-scan, native on): the largest `execute` is `VeinProfile`
at ~10% of a 4.2s profiled run, made of the Python around the kernels
(`Maximum`, `Threshold`, image copies); next are `ADilation`/`GDilation`,
the `RotationInvariantGabor` family and `MFastReconstruction` at ~5% each,
all float code already vectorized in numpy. None is worth a native port on
this evidence; re-profile with real-size inputs before adding more.

### 8.4 `mm/binary/attribute/BAreaOpening.py` / `BAreaClosing.py`
**Share:** ~17% (2,051 calls: `GAreaOpening`/`GAreaClosing` threshold-
decompose at all 256 levels and run one per level; `AreaGranulometry` runs
those per area).
**Why slow:** a flood fill pushing 8 `Point` objects per pixel and reading
and writing one pixel per method call.
**Done in Rust** (`python/src/area.rs`, `small_components`): which pixels a
call clears (opening) or sets (closing) is exactly the union of the
8-connected components of fewer than `area` pixels, independent of fill
order, so the kernel labels components with an index stack and returns
that mask. Read like `getXYBoolean` (first `xdim * ydim` entries, so
multi-channel inputs behave as before); a non-`int` `area` stays on the
Python path. 256x256: 89ms -> 2ms; `GAreaOpening` 64x64: 1.19s -> 26ms.

### 8.5 `mm/gray/adaptive/ComputeSEs.py`
**Share:** ~6% (one call; O(pixels x SE area)).
**Done in Rust** (`python/src/amoeba.rs`, `amoeba_masks`): a step-for-step
transcription of `_computeAmoeba` -- same LIFO stack and neighbour order,
`tmpImg`'s mod-256 byte storage, and numpy's negative-index wrap for the
`FlatSE` write -- since with `size // 2 >= 256` the wraparound makes the
visiting order observable. The cost `int(tmp + 1 + 0.25*abs(p - q))` is
exact in doubles for 32-bit `getXYByte` values, so the kernel's integer
`tmp + 1 + |p - q| / 4` is the same number. `FlatSE`s are still built in
Python (`FlatSE.square` + `fill(False)`, then the mask), which now
dominates: 64x64 at size 5, 32ms -> 9ms.

### 8.6 How the native kernels ship (maturin)
- `python/Cargo.toml` + `python/src/*.rs` build the `vpt._native`
  extension (PyO3, abi3 for CPython >= 3.11, so one wheel per platform).
  The build backend is maturin instead of hatchling; the version still
  lives only in `pyproject.toml` (release-please is unchanged).
- `vpt/_accel.py` imports it; each algorithm uses its kernel when
  `_accel.native` is set and runs the unchanged Python otherwise. The
  Python stays the reference. `VPT_PURE_PYTHON=1` switches the extension
  off; `VPT_REQUIRE_NATIVE=1` makes a failed import an error (CI).
- `tests/test_native_parity.py` runs each algorithm with the kernel on and
  off and compares raw storage on all image types, multi-channel inputs,
  degenerate sizes, runs > 255, out-of-range `area`, and the `ComputeSEs`
  wraparound/negative-index cases. Mutating any kernel (comparison
  operator, a neighbour, the byte wrap, the floor in the cost, the index
  wrap) fails it.
- CI: `python-ci` lints the Rust (`cargo fmt`, `clippy -D warnings`), runs
  the goldens natively on 3.11-3.14 plus once pure-Python, then
  `python-wheels.yml` builds wheels (manylinux x86_64/aarch64, macOS
  arm64/x86_64, Windows x64) and the sdist and re-runs the suite against
  each installed artifact. `release-please.yml` calls the same workflow
  and attaches its output, so releases ship what CI tested.
- Platforms without a wheel install from the sdist, which needs a Rust
  toolchain. There is no pure-Python wheel any more.

## 9. Platform-independent `java.lang.Math` (2026-11-23)

**Status:** `[x]` -- found when PR #12's wheel matrix ran the golden
suite on macOS and Windows for the first time: 2-5 golden tests failed
there (XYZ2LAB/LAB2XYZ, `javacompat` acos/asin/atan, `FloatingFlatSE.circle`,
`FourierDescriptor`, ...), each by 1-2 ulp, because `math.*` is the
platform libm and only glibc happened to agree with the oracle.

**What Java computes** (JDK 21, x86_64 HotSpot; measured with 200,000
random inputs per function, `Math` vs `StrictMath`):
- `asin`, `acos`, `atan`, `atan2` (and `hypot`): `StrictMath`, i.e.
  fdlibm, exactly.
- `sin`, `cos`, `tan`, `exp`, `log`, `log10`, `pow`: HotSpot intrinsics
  (Intel's libm, in HotSpot's source only), not fdlibm.

**Candidates measured against `Math`** (mismatches per 200,000):

| fn | glibc | `libm` crate (musl/fdlibm) | correctly rounded (CORE-MATH) |
|---|---|---|---|
| asin / acos / atan | 7,121-15,132 | **0** | 7,150-15,187 |
| atan2 | 35,464 | 0 random, but see below | 35,475 |
| log / log10 | 11 / 7,541 | 2,378 / 267 | **0 / 0** |
| sin / cos / tan | 499 / 365 / 1,912 | 5,948 / 5,741 / 7,025 | **289 / 237 / 456** |
| exp / pow | 500 / 214 | 19,182 / 18,168 | **473 / 99** |

**Done:** `vpt._native` (`python/src/jmath.rs`) provides them all, and
every `j*` helper in `_javacompat` computes through it:
- `asin`/`acos`/`atan`: the `libm` crate -- 0 differences in a further
  900,000 inputs each spread over every exponent.
- `atan2`: fdlibm's `e_atan2.c` transcribed. The `libm` crate's differs
  where `|y/x| > 2^60` and `x < 0` (musl returns `+-pi/2`, fdlibm
  `pi - (z - pi_lo)`): 83,331 of 900,000 stress inputs, and exactly
  `Sobel.THETA`'s documented divergence. The transcription: 0 in 1.1M.
- `sin`/`cos`/`tan`/`exp`/`log`/`log10`/`pow`: CORE-MATH, vendored
  (`python/csrc/core-math`, MIT) and compiled by `build.rs` with
  `-ffp-contract=off` and no `-march=native`; same mismatch counts as the
  upstream build. The residual mismatches are all Intel misroundings:
  the 11 in `tests/goldens/jmath.txt` were checked at 200-bit precision.
  Matching those too would mean porting HotSpot's intrinsic code
  (`src/hotspot/cpu/x86/macroAssembler_x86_*.cpp`, Intel's, distributed
  under the GPLv2 with the rest of HotSpot) into this MIT package -- a
  licensing decision, not an engineering one, so not done.
- Every direct platform-math call outside `_javacompat` (114 sites in 30
  files: `math.sin`, `np.exp`, float `**`, ...) now goes through a `j*`
  helper; `tests/test_no_platform_math.py` fails on any new one. Along
  the way: `InvariantMoment`/`MultiInvariantMoment` used exact `int ** int`
  where Java rounds each `Math.pow` separately (now `jpow`), and
  `Point.distance` used `hypot` where Java computes
  `sqrt(dx * dx + dy * dy)`.

**Result:** with the extension, the golden suite passes with every libm
tolerance removed (Sobel THETA, ZernikeMoment, OrientationMap, LoadFLO,
point.Log, RGB2HSY/RGB2NUHSY733, spectralAngleDistance), on every platform.
Only `shape.ZernikeMoments` keeps one (<= 8 ulp): 9 of its 8,362 distinct
`sin`/`cos`/`pow` arguments are intrinsic misroundings (replayed through
the JVM). The pure-Python fallback keeps the old tolerances
(`tests/_platform.py`).

## Areas audited with no findings (don't re-audit from scratch)

- Overload-splitting (`fill*`/`newInstance*` across `Image`, `ByteImage`,
  `BooleanImage`, `IntegerImage`, `DoubleImage`) — correct, including the
  `bool`-before-`int` dispatch order footgun.
- Known-bug preservation (`BooleanImage` inverted non-boolean getters,
  `Tools.VAddC`/`VSub` mutate-and-return-zero, `getXYCMaximum` sentinel typo,
  `drawLine` slope truncation, `getAngleABC` double-read determinant) — all
  faithfully reproduced and flagged in `python/README.md`.
- TODOs/stubs/truncated ports — none found; unfinished methods present
  (`MultiLobeMeanShapeFeatures`, some `mm` attribute-opening strategies) are
  faithful transcriptions of the Java oracle's own unfinished/buggy methods,
  not Python-side gaps.
- `jsplit`/`jacos`/`jdiv`/bit-shift usage elsewhere in the port — correct and
  consistently applied outside of the two sites in section 2.

# VPT Java Oracle

A compiling, running build of `muratcanozdemir/Visual-Processing-Library`, repaired
from its decompiled state so it can serve as a behavioural reference for a Python
reimplementation.

## What this is

The upstream repository is not source. It is JD-Core 0.7.0.1 output decompiled from
`name.jar`: every file carries the tell-tale `/* NN:MM */` line-number prefixes
and a `Location: H:\name.jar` footer. It does not compile as published.

This tree is that decompile with the decompiler's damage repaired: **448 sources
compile clean and produce 481 class files.** Original algorithm logic is untouched
except where the decompiler destroyed it and reconstruction was forced.

## Build

```sh
./build.sh        # compile into ./classes
./build.sh test   # compile and run the verification harnesses
```

Jama 1.0.3 is fetched from Maven Central on first run. **Jama must be on the runtime
classpath too**, not just the compile classpath, or `Sobel`, `PCA`, and `NonFlatSE`
throw `NoClassDefFoundError`:

```sh
java -cp classes:oracle:lib/jama.jar Sweep
```

## Verification status

| Harness | Result |
|---|---|
| `Smoke` | Dilation of a lone bright pixel by a 3×3 square fills its neighbourhood; erosion removes it |
| `Recovered` | ConnectedComponents → 2 components of 9px; GrayQFZ → 2 flat zones; MaxTree → root level 10, 100px, 1 child; ConvexHull → 42 outline px |
| `Sweep` | 12/12: GOpening, GClosing, GGradient, BDilation, BErosion, BOpening, OtsuThreshold, Sobel, RGB2Gray, Histogram, Lexico, EuclideanNorm |

## Dependencies

The published decompile has four external dependencies, not zero (imports sit behind
the `/* NN: */` comment prefixes, so a naive `^import` grep misses them).

| Dependency | Used by | Resolution |
|---|---|---|
| Jama 1.0.3 | `PCA`, `ReversePCA`, `NonFlatSE` | Real jar from Maven Central |
| JAI (`javax.media.jai`, `com.sun.media.jai`) | `Save`, `Tools`, `gui/Frame2D*` | Shimmed: thin delegations to `java.awt.image` / `JPanel` |
| Weka | texture `ClassifierTest`s | Excluded (see `EXCLUDED.txt`) |
| Xuggler | `LoadAVI` | Excluded: project dead since ~2013 |
| `org.concord.swing.QuickHull` | `ConvexHull` | Reimplemented (Andrew monotone chain) |

Two classes referenced by the code are **absent from the published repository**:
`vpt.util.ordering.ReverseLexico` and the `vpt.experimental.texture.*` package
(`RotationInvariantPoints2`, `MetaCCH`, `Experimental5`, `rotInv.*`). The JAR was
built from a larger source tree than what was released. `ReverseLexico` has been
reconstructed; the `experimental.texture` dependents are excluded.

## Decompiler damage repaired

Each category below is a distinct JD-Core failure mode. **Read this before porting**:
several of these silently change behaviour.

### 1. Collapsed nested loops (43 sites, 39 files)

The most widespread and most dangerous. JD-Core merged a `while`/`for` pair into one
malformed statement:

```java
// as decompiled: does not compile
for (; !s.isEmpty(); i < N.length) {
  Point tmp = (Point)s.pop();
  i = 0; continue;
  int _x = tmp.x + N[i].x;
  ...
  i++;
}
```

The original is recoverable deterministically, because the second comment column
preserves *original* line numbers and the inner loop's line number appears twice:
once on the `i = 0; continue;` and once on the orphaned `i++;`:

```java
while (!s.isEmpty()) {
  Point tmp = (Point)s.pop();
  for (i = 0; i < N.length; i++) {
    int _x = tmp.x + N[i].x;
    ...
  }
}
```

Four shape variants occur: array-indexed (`i < N.length`), bounded 2-D neighbourhood
scans (`j <= y + 1`, the watershed form), iterator-driven, and inline-increment.
Affected: all flat-zone variants, both watersheds, connected components, `MP`, `OSR`,
several max-tree attribute filters.

### 2. Runtime-only: mangled boolean ternaries (`BooleanImage`)

Compiles cleanly, throws at execution: the worst kind:

```java
this.pixels[i] = (boolean) (p > 0.0D ? 1 : false);   // ClassCastException at runtime
this.pixels[i] = (p > 0.0D);                          // original
```

Three sites in `BooleanImage` (the `int`, `byte`, and `double` setters). Java
autoboxes the mixed `int`/`boolean` ternary to `Integer`, then the cast fails. Any
path writing a non-boolean into a `BooleanImage` was broken.

**When porting, check every `BooleanImage` setter path.** Note also that the getters
look inverted (`this.pixels[i] == false ? 255 : 0` returns 255 for *false*). That has
been left as-is because it matches the class's internal convention, but it warrants
verification against intended behaviour.

### 3. Overflowing double literals (15 sites)

JD-Core dropped a digit of precision, producing a negated literal larger than
`Double.MAX_VALUE`:

```java
private final double MIN = -1.797693134862316E+308D;   // illegal
private final double MIN = -Double.MAX_VALUE;          // intended
```

The positive form `1.7976931348623157E+308D` (31 occurrences) is legal and untouched.
Separately, `Image.java` contains `1.797693134862316E+30D`: note `E+30`, not `E+308`.
That compiles but is **silently the wrong value** in `getXYCMaximum`, and is preserved
here as found.

### 4. Switch-case scope collapse (11 files)

Per-case braces were omitted, so all cases share one scope and every repeated local
declaration collides. Fixed by wrapping each case body in braces, closing on
`break`/`throw`/`return`.

### 5. Dropped imports

JD-Core merged comment prefixes onto one line and dropped import statements outright:

```java
/*  3:   */ /*  6:   */ import vpt.Image;    // and `import vpt.Algorithm;` simply gone
```

Missing `import vpt.{Image,ByteImage,BooleanImage,DoubleImage,IntegerImage,Algorithm,GlobalException};`
re-added across 17 files. Four files also imported `util.Tools` /
`util.ordering.AlgebraicOrdering` without the `vpt.` prefix.

### 6. Mistyped arrays: in both directions

- `MeanShift` declared `boolean[256][256][256] flags/flags2` but assigns `= 1` and
  compares `== 0`. The original was `byte[][][]`; retyped.
- `ColorStructureRGB64` and four `RandomTrainTestFileProduction` files have genuinely
  boolean arrays but compare `== 0` / `!= 0`. Here the *comparisons* were fixed
  (`x[i] == 0` → `!x[i]`), not the type.

Distinguishing these requires reading how each array is written, not just how it is
compared.

### 7. Mistyped iterators with side-effects in the update slot

```java
for (int _p = list.iterator(); _p.hasNext(); temp.setXYBoolean(t.x, t.y, false)) { ... }
```

`Iterator` typed as `int`. The update-slot expression runs *after* each body
iteration, so it must move to the **end of the loop body** to preserve evaluation
order: not the start.

### 8. Smaller categories

- Synthetic inner-class constructor args: `new DoubleComparator(null)` against a
  no-arg private constructor (5 files).
- Consecutive duplicate declarations: literally `int l1;` twice in a row (37 removed
  across 8 files; `BPathOpening` alone had 24).
- Chained declarations flattened: `double R = G = B = 0.0D;`.
- Unrecovered identifiers emitted as literal `???` (`AngularQFZ`, `PickAndPlace`).
- Lost generic casts: `Object` where `ArrayList<Point>` or `Point` was required.

## Exclusions

16 files, listed in `EXCLUDED.txt`:

- **Weka-dependent** texture `ClassifierTest`s and `outex`/`kthtips` harnesses: need
  datasets that are not present regardless.
- **`LoadAVI`, `TestAviLoad`**: Xuggler is abandoned.
- **`CircularFourierPowerSpectra`**: depends on the missing `vpt.experimental.texture`
  package.
- **`stray code` otherwise.

## CI / releases

Two workflows, both driving the same scripts used locally: nothing lives only in YAML.

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | push, PR | Build on JDK 17 and 21, run all harnesses, upload jars as build artifacts |
| `release.yml` | tag `v*` | Build, run harnesses, publish jars to GitHub Releases |

```sh
./scripts/fetch-jama.sh    # fetch + checksum-verify the one dependency
./build.sh                 # compile into ./classes
./scripts/run-tests.sh     # compile + run harnesses, non-zero on failure
./scripts/package.sh       # produce build-out/*.jar + SHA256SUMS
```

Cutting a release:

```sh
git tag v1.0.0 && git push origin v1.0.0
```

Version is derived from the tag via `git describe`; untagged builds get
`0.0.0-dev+<sha>`. Each jar carries build commit, JDK, and timestamp in its
manifest.

### Why the pipeline runs the tests rather than just compiling

`BooleanImage` compiled cleanly for three working sessions while throwing
`ClassCastException` at runtime: a decompiler-mangled ternary that is
type-correct and semantically wrong (category 2 above). A compile-only pipeline
would have gone green on it. The harnesses assert against known-correct values
and exit non-zero on mismatch, so a regression in reconstructed logic fails the
build. That failure path is itself tested: deliberately corrupting an expected
value produces exit code 1.

### Jama and hermeticity

`fetch-jama.sh` downloads Jama 1.0.3 and verifies it against a pinned SHA-256,
because Maven Central artifacts are not immutable by policy alone. The trade-off
is a network dependency on a jar last published in 2013. **If you prefer a
hermetic build, commit `lib/jama.jar` (37 KB, public domain) and the script
becomes a no-op**: that also makes release contents independent of when they
were built. Drop `lib/` from `.gitignore` if you go that way.

Jama must be on the **runtime** classpath, not only at compile time:

```sh
java -cp vpt-1.0.0.jar:jama-1.0.3.jar YourMain
```

## Layout

```
src/vpt/**              the library (package root must be `vpt/`)
src/com/, src/javax/    JAI shims
src/org/concord/swing/  QuickHull replacement
oracle/                 verification harnesses
scripts/                fetch-jama, run-tests, package
.github/workflows/      ci.yml, release.yml
EXCLUDED.txt            files not in the build
build.sh                build driver
```

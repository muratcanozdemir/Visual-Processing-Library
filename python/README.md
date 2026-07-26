# VPT — Python port

A port of the VPT Java library to Python. The Java tree in `../src/` is the
**behavioural oracle**: it compiles, runs, and every ported file is verified
against golden values dumped from it.

## Status

| Tier | Contents | State |
|---|---|---|
| 0 | `Image` hierarchy, `Algorithm`, `util`, `util/se`, `util/ordering` | in progress |
| 1 | the algorithm packages the oracle harnesses cover (~220 files) | not started |
| 2 | texture, saliency, frequential, hough, colour, … | out of scope for now |
| — | Swing GUI, `vpt/testing`, dataset-dependent texture harnesses | excluded |

## Design

**The API mirrors the Java one method-for-method.** `getXYCDouble`,
`FlatSE.square(3)`, `GDilation.invoke(img, se)`. That is deliberate: it lets
each Python file be diffed against its Java counterpart during review, which
is the only practical way to audit a port of this size. A PEP 8 facade can be
layered on later without reworking anything underneath.

**Storage is NumPy; structure is Java.** Each `Image` subclass wraps a typed
ndarray rather than a Python list, so the byte type gets Java's `(byte)`
wraparound from `int8` for free and algorithms have a vectorisation path. The
class and method structure stays 1:1 with the Java.

**Java overloads that dispatch on static type are split.** Python cannot
reproduce static dispatch, and the difference is not cosmetic: on a
`ByteImage`, `fill(1)` stores 1 while `fill(1.0)` stores 255. So `fill` is
split into `fillInt` / `fillDouble` / `fillBoolean`, with a runtime-dispatching
`fill()` wrapper for convenience. Same for `newInstance`.

## Preserved defects

Several behaviours here are wrong but faithful. They are reproduced because
the Java oracle exhibits them and callers depend on the observed results;
each is flagged in a comment at its site.

- `Image.getXYCMaximum` seeds its running maximum with `-1.797693134862316E+30`
  — exponent `E+30`, not `E+308`. Documented in the root README as preserved
  as found.
- `BooleanImage`'s non-boolean getters are inverted: `getByte` returns 255 for
  a **false** pixel.
- `Tools.VAddC` and `Tools.VSub` mutate their input in place and return a
  freshly allocated all-zero array.
- `Tools.drawLine` computes its slope with integer division before widening to
  float, so shallow lines come out horizontal.
- `Tools.getAngleABC` reads `cb.y` twice in its determinant.
- `IntegerImage.fillDouble` prints "Not yet implemented!!!" and does nothing.

Do not "fix" any of these without regenerating the goldens and explaining the
change — the test suite will fail, which is the point.

## Tests

```sh
pip install -e '.[test]'
python -m pytest              # no JDK needed; goldens are committed
```

Regenerating goldens requires the Java build:

```sh
cd .. && ./build.sh && ./python/tests/regen-goldens.sh
```

Run that only when the oracle's behaviour legitimately changes — never to make
a failing test pass.

### Why goldens rather than hand-written expectations

The failure modes this port has to guard against produce numbers that are
*nearly* right: round-half-even instead of round-half-up, an unbiased byte
store, a non-wrapping shift. Those survive any tolerance-based assertion and
most hand-picked test values. Doubles are therefore compared by IEEE-754 bit
pattern against values the Java actually produced.

The harness is mutation-tested: injecting each of those bugs into the port
makes the suite fail. A test that cannot fail is not evidence.

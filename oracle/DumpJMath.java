import java.util.SplittableRandom;

/**
 * Dumps java.lang.Math's transcendental functions on a fixed input set, so
 * the port's vpt._javacompat j* functions (computed through vpt._native,
 * see python/src/jmath.rs) can be checked bit for bit on every platform.
 *
 * Inputs: special values plus seeded pseudo-random ones per function, over
 * the ranges the library uses (angles, colour-space exponents, small
 * integer powers). SplittableRandom's sequence is fixed by its spec, so the
 * inputs don't depend on the JDK.
 *
 * Output: fn, x, y (0 for unary), Math result, each as raw IEEE-754 bits in
 * hex. Verified by python/tests/test_jmath_golden.py.
 *
 * Usage: java -cp oracle DumpJMath > python/tests/goldens/jmath.txt
 */
public class DumpJMath {
  static final int N = 1500;

  static String h(double d) { return Long.toHexString(Double.doubleToRawLongBits(d)); }

  static void out(String fn, double x, double y, double r) {
    System.out.println(fn + "\t" + h(x) + "\t" + h(y) + "\t" + h(r));
  }

  static final double[] SPECIAL = {
    0.0, -0.0, 1.0, -1.0, 0.5, -0.5, 2.0, -2.0, 3.0, 1e-300, -1e-300, 4.9e-324, -4.9e-324,
    Double.MIN_NORMAL, Double.MAX_VALUE, -Double.MAX_VALUE, Math.PI, -Math.PI, Math.PI / 2,
    Math.PI / 4, 1e6, -1e6, 1e22, -1e22, 709.0, 710.0, -745.0, -746.0, 1000.0, -1000.0,
    Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
  };

  public static void main(String[] args) {
    SplittableRandom r = new SplittableRandom(20261123L);

    for (double x : SPECIAL) {
      out("sin", x, 0, Math.sin(x));
      out("cos", x, 0, Math.cos(x));
      out("tan", x, 0, Math.tan(x));
      out("exp", x, 0, Math.exp(x));
      out("log", x, 0, Math.log(x));
      out("log10", x, 0, Math.log10(x));
      out("asin", x, 0, Math.asin(x));
      out("acos", x, 0, Math.acos(x));
      out("atan", x, 0, Math.atan(x));
      for (double y : SPECIAL) {
        out("pow", x, y, Math.pow(x, y));
        out("atan2", x, y, Math.atan2(x, y));
      }
    }

    for (int i = 0; i < N; i++) {
      double t = switch (i % 3) {
        case 0 -> r.nextDouble(-10, 10);
        case 1 -> r.nextDouble(-1000, 1000);
        default -> 2 * Math.PI * r.nextInt(-64, 64) / r.nextInt(1, 64);
      };
      out("sin", t, 0, Math.sin(t));
      out("cos", t, 0, Math.cos(t));
      out("tan", t, 0, Math.tan(t));

      double x = r.nextDouble(-60, 60);
      out("exp", x, 0, Math.exp(x));

      double p = (i % 2 == 0) ? r.nextDouble(0, 300) : Math.exp(r.nextDouble(-30, 30));
      out("log", p, 0, Math.log(p));
      out("log10", p, 0, Math.log10(p));

      double b = (i % 2 == 0) ? r.nextDouble(0, 1) : r.nextDouble(-300, 300);
      double e = switch (i % 7) {
        case 0 -> 0.333333;          // XYZ2LAB
        case 1 -> 3.0;               // LAB2XYZ
        case 2 -> 2.0;               // squares
        case 3 -> 1.5;               // Skewness
        case 4 -> r.nextInt(-8, 9);  // moments, 2^n
        case 5 -> 2.4;               // sRGB gamma
        default -> r.nextDouble(-3, 3);
      };
      out("pow", b, e, Math.pow(b, e));

      double u = r.nextDouble(-1, 1);
      out("asin", u, 0, Math.asin(u));
      out("acos", u, 0, Math.acos(u));
      double w = (i % 2 == 0) ? r.nextDouble(-5, 5) : r.nextDouble(-1e4, 1e4);
      out("atan", w, 0, Math.atan(w));
      double yy = r.nextDouble(-300, 300), xx = r.nextDouble(-300, 300);
      if (i % 10 == 0) xx = r.nextDouble(-1e-15, 1e-15);  // Sobel's near-axis case
      out("atan2", yy, xx, Math.atan2(yy, xx));
    }
  }
}

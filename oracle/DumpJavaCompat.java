/**
 * Dumps Java's exact arithmetic behaviour for the primitives that Python does
 * not share: Math.round's round-half-up, (int) casts that saturate rather than
 * raise, (byte) and shift wraparound, and integer division that truncates
 * toward zero instead of flooring.
 *
 * Verified by python/tests/test_javacompat_golden.py against vpt._javacompat.
 *
 * Usage: java -cp classes:oracle DumpJavaCompat > python/tests/goldens/javacompat.txt
 */
public class DumpJavaCompat {
  public static void main(String[] a) {
    double[] ds = {0.0,0.5,1.5,2.5,-0.5,-1.5,-2.5,0.49999999999999994,
                   -0.49999999999999994, 2.4999999999999996, 1e17, -1e17,
                   0.1,254.5,255.5,-0.1,127.5,-127.5, 1e300, -1e300,
                   Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    for (double d : ds) System.out.println("round\t" + Double.toHexString(d) + "\t" + Math.round(d));
    for (double d : ds) System.out.println("int\t" + Double.toHexString(d) + "\t" + (int) d);
    int[] is = {0,1,127,128,255,256,-1,-128,-129,300,-300,2147483647,-2147483648};
    for (int i : is) System.out.println("byte\t" + i + "\t" + (byte) i);
    for (int i : is) System.out.println("shl24\t" + i + "\t" + (i << 24));
    for (int i : is) System.out.println("shr24\t" + i + "\t" + (i >> 24));
    int[][] pairs = {{7,2},{-7,2},{7,-2},{-7,-2},{1,3},{-1,3},{5,5},{-5,3}};
    for (int[] p : pairs) System.out.println("div\t"+p[0]+"\t"+p[1]+"\t"+(p[0]/p[1])+"\t"+(p[0]%p[1]));
  }
}

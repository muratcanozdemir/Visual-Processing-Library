import vpt.util.se.NonFlatSE;
import vpt.util.se.FloatingFlatSE;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Dumps NonFlatSE and FloatingFlatSE.
 *
 * The interesting case is NonFlatSE.decompose(), which is the only place in
 * the tree that depends on a linear-algebra library: Java uses Jama, the port
 * uses numpy.linalg. An SVD is unique only up to a joint sign flip of the
 * singular vectors, so the two are free to disagree on sign while both being
 * correct. This dump is what decides which convention the port has to adopt —
 * it is not something to reason about from the Java source alone.
 *
 * Usage: java -cp classes:oracle:lib/jama.jar DumpNonFlatSE
 *          > python/tests/goldens/nonflatse.txt
 */
public class DumpNonFlatSE {

    static String hex(double d) { return Double.toHexString(d); }

    static void dump(String name, NonFlatSE se) {
        StringBuilder vals = new StringBuilder();
        for (int y = 0; y < se.getYDim(); y++)
            for (int x = 0; x < se.getXDim(); x++) {
                if (vals.length() > 0) vals.append(';');
                vals.append(hex(se.getXYDouble(x, y)));
            }

        // getCoords() can legitimately contain nulls. It sizes the array by
        // `Math.abs(v) >= 0.0` (true for every non-NaN value) but only emits
        // a leading centre entry when `v >= 0.0` without the abs, so a
        // negative centre leaves the last slot unfilled. That is the
        // preserved inconsistency documented on NonFlatSE.getCoords; the
        // port has to reproduce the nulls, not just the points.
        StringBuilder coords = new StringBuilder();
        for (Point p : se.getCoords()) {
            if (coords.length() > 0) coords.append(';');
            coords.append(p == null ? "null" : (p.x + "," + p.y));
        }

        System.out.println(name + "\t" + se.getXDim() + "\t" + se.getYDim()
            + "\t" + se.center.x + "," + se.center.y + "\t" + vals + "\t" + coords);
    }

    public static void main(String[] args) {
        for (int r : new int[]{0, 1, 2, 3, 5, 7}) {
            dump("cylinder(" + r + ",1.0)", NonFlatSE.cylinder(r, 1.0));
            dump("cylinder(" + r + ",0.5)", NonFlatSE.cylinder(r, 0.5));
            dump("cone(" + r + ",1.0)", NonFlatSE.cone(r, 1.0));
            dump("cone(" + r + ",2.5)", NonFlatSE.cone(r, 2.5));
        }
        for (int r : new int[]{1, 2, 3, 4, 6}) {
            dump("Gaussian(" + r + ")", NonFlatSE.Gaussian(r));
            dump("Gaussian(" + r + ").reflection", NonFlatSE.Gaussian(r).reflection());
        }
        for (int w = 1; w <= 4; w++)
            for (int h = 1; h <= 4; h++)
                dump("box(" + w + "," + h + ",0.25)", NonFlatSE.box(w, h, 0.25));

        // decompose(): Gaussian kernels are separable, hence rank 1, so these
        // return a pair. cone() is not, so it returns null.
        for (int r : new int[]{1, 2, 3, 4, 6}) {
            NonFlatSE[] d = NonFlatSE.Gaussian(r).decompose();
            if (d == null) {
                System.out.println("Gaussian(" + r + ").decompose\t0\t0\t-\tnull\t");
            } else {
                dump("Gaussian(" + r + ").decompose[0]", d[0]);
                dump("Gaussian(" + r + ").decompose[1]", d[1]);
            }
        }
        for (int r : new int[]{2, 3}) {
            NonFlatSE[] d = NonFlatSE.cone(r, 1.0).decompose();
            System.out.println("cone(" + r + ",1.0).decompose\t0\t0\t-\t"
                + (d == null ? "null" : "pair") + "\t");
        }
        for (int w = 1; w <= 4; w++) {
            NonFlatSE[] d = NonFlatSE.box(w, w, 0.25).decompose();
            if (d == null) {
                System.out.println("box(" + w + ").decompose\t0\t0\t-\tnull\t");
            } else {
                dump("box(" + w + ").decompose[0]", d[0]);
                dump("box(" + w + ").decompose[1]", d[1]);
            }
        }

        // FloatingFlatSE
        for (int r : new int[]{1, 2, 3, 5}) {
            for (int per : new int[]{4, 8, 12}) {
                FloatingFlatSE se = FloatingFlatSE.circle(r, per);
                StringBuilder sb = new StringBuilder();
                for (Point2D.Double p : se.getCoords()) {
                    if (sb.length() > 0) sb.append(';');
                    sb.append(hex(p.x)).append(',').append(hex(p.y));
                }
                System.out.println("FloatingFlatSE.circle(" + r + "," + per + ")\t0\t0\t-\t"
                    + sb + "\t");
            }
        }
    }
}

import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.mm.gray.GDilation;
import vpt.algorithms.mm.gray.GErosion;

/**
 * Minimal end-to-end check of the core stack: ByteImage's signed-byte offset
 * arithmetic, FlatSE, the reflection-based Algorithm.preprocess dispatch, and
 * the reconstructed -Double.MAX_VALUE sentinel in GDilation.
 *
 * Exits non-zero on failure so CI can gate on it.
 */
public class Smoke {

    static int fail = 0;

    static void check(String what, int actual, int expected) {
        if (actual == expected) {
            System.out.println("  PASS  " + what + " = " + actual);
        } else {
            System.out.println("  FAIL  " + what + " = " + actual + ", expected " + expected);
            fail++;
        }
    }

    public static void main(String[] args) throws Exception {
        ByteImage img = new ByteImage(8, 8, 1);
        img.fill(0);
        img.setXYByte(4, 4, 200);          // one bright pixel
        FlatSE se = FlatSE.square(3);

        Image d = (Image) GDilation.invoke(img, se);
        Image e = (Image) GErosion.invoke(img, se);

        check("input centre", img.getXYByte(4, 4), 200);

        // dilation by a 3x3 square must fill the whole 3x3 neighbourhood
        for (int y = 3; y <= 5; y++) {
            for (int x = 3; x <= 5; x++) {
                check("dilation (" + x + "," + y + ")", d.getXYByte(x, y), 200);
            }
        }
        // and must not leak outside it
        check("dilation (2,4) outside SE", d.getXYByte(2, 4), 0);
        check("dilation (4,2) outside SE", d.getXYByte(4, 2), 0);

        // erosion removes an isolated pixel
        check("erosion centre", e.getXYByte(4, 4), 0);

        System.out.println(fail == 0 ? "\nSmoke OK" : "\nSmoke FAILED (" + fail + ")");
        if (fail > 0) System.exit(1);
    }
}

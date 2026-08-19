import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.shape.ConnectedComponents;
import vpt.algorithms.flatzones.gray.GrayQFZ;
import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
import vpt.algorithms.mm.gray.connected.maxtree.Node;
import vpt.algorithms.geometric.ConvexHull;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.Point;

/**
 * Exercises the algorithms whose loops were destroyed by the decompiler and had
 * to be reconstructed. These carry the highest fidelity risk in the tree, so
 * they are checked against known-correct expected values rather than printed.
 *
 * Exits non-zero on failure so CI can gate on it.
 */
public class Recovered {

    static int fail = 0;

    static void check(String what, long actual, long expected) {
        if (actual == expected) {
            System.out.println("  PASS  " + what + " = " + actual);
        } else {
            System.out.println("  FAIL  " + what + " = " + actual + ", expected " + expected);
            fail++;
        }
    }

    public static void main(String[] args) throws Exception {

        // --- ConnectedComponents: two disjoint 3x3 blobs ---
        BooleanImage bi = new BooleanImage(12, 12, 1);
        bi.fill(false);
        for (int x = 1; x <= 3; x++) for (int y = 1; y <= 3; y++) bi.setXYBoolean(x, y, true);
        for (int x = 7; x <= 9; x++) for (int y = 7; y <= 9; y++) bi.setXYBoolean(x, y, true);
        ArrayList<ArrayList<Point>> cc = ConnectedComponents.invoke(bi);
        check("ConnectedComponents count", cc.size(), 2);
        check("ConnectedComponents blob 0", cc.get(0).size(), 9);
        check("ConnectedComponents blob 1", cc.get(1).size(), 9);

        // --- GrayQFZ: a two-level image has exactly two flat zones ---
        ByteImage gi = new ByteImage(10, 10, 1);
        gi.fill(10);
        for (int x = 5; x < 10; x++) for (int y = 0; y < 10; y++) gi.setXYByte(x, y, 200);
        Image fz = GrayQFZ.invoke(gi, 0, 255);
        HashSet<Integer> labels = new HashSet<Integer>();
        for (int x = 0; x < 10; x++) for (int y = 0; y < 10; y++) labels.add(fz.getXYInt(x, y));
        check("GrayQFZ flat zones", labels.size(), 2);

        // --- MaxTree: root sits at the image minimum and holds every pixel ---
        Node root = MaxTree.invoke(gi);
        check("MaxTree root level", root.level, 10);
        check("MaxTree root pixels", root.pixels == null ? -1 : root.pixels.size(), 100);
        check("MaxTree root children", root.children == null ? -1 : root.children.size(), 1);

        // --- ConvexHull: triangle with an interior point; hull ignores the interior ---
        BooleanImage tri = new BooleanImage(20, 20, 1);
        tri.fill(false);
        tri.setXYBoolean(2, 2, true);
        tri.setXYBoolean(15, 3, true);
        tri.setXYBoolean(8, 16, true);
        tri.setXYBoolean(8, 8, true);          // interior — must not appear on the hull
        Image hull = ConvexHull.invoke(tri, Boolean.FALSE);
        int on = 0;
        for (int x = 0; x < 20; x++) for (int y = 0; y < 20; y++) if (hull.getXYBoolean(x, y)) on++;
        check("ConvexHull outline pixels", on, 42);

        System.out.println(fail == 0 ? "\nRecovered OK" : "\nRecovered FAILED (" + fail + ")");
        if (fail > 0) System.exit(1);
    }
}

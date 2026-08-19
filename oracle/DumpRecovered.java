import vpt.*;
import vpt.algorithms.shape.ConnectedComponents;
import vpt.algorithms.geometric.ConvexHull;
import vpt.algorithms.flatzones.gray.GrayQFZ;
import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
import vpt.algorithms.mm.gray.connected.maxtree.Node;
import java.util.ArrayList;
import java.awt.Point;

/**
 * Dumps all four decompiler-recovered algorithms oracle/Recovered.java
 * exercises: ConnectedComponents, ConvexHull(filled=false), GrayQFZ, and
 * MaxTree.
 *
 * IMPORTANT: ConvexHull(filled=true) is deliberately NOT exercised here. It
 * routes through BFillHole on a BooleanImage, and BGeodesicDilation's
 * Minimum step computes NOR instead of AND on that type (a consequence of
 * BooleanImage.getDouble's documented inversion), which appears to make the
 * reconstruction fixpoint in BReconstructionByDilation loop forever. See
 * python/vpt/algorithms/mm/binary/geo/BFillHole.py for the full writeup.
 * oracle/Recovered.java itself only ever calls ConvexHull with
 * Boolean.FALSE, so this is consistent with known-good harness coverage --
 * but if you're regenerating this golden and it hangs, that confirms the
 * hazard is real on the Java side too and this file needs to keep avoiding
 * filled=true, not "fix" it by waiting longer.
 *
 * Usage: java -cp classes:oracle DumpRecovered > python/tests/goldens/recovered.txt
 */
public class DumpRecovered {

    static void dumpBinary(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                sb.append(img.getXYBoolean(x, y) ? '1' : '0');
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim() + "\t" + sb);
    }

    static void dumpInt(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++) {
                if (sb.length() > 0) sb.append(',');
                sb.append(img.getXYInt(x, y));
            }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim() + "\t" + sb);
    }

    // level:pixelcount:childcount per node, DFS pre-order over children
    // exactly as stored (insertion order), ';'-joined.
    static void dumpTree(StringBuilder sb, Node n) {
        if (sb.length() > 0) sb.append(';');
        sb.append(n.level).append(':').append(n.pixels.size()).append(':').append(n.children.size());
        for (Node c : n.children) dumpTree(sb, c);
    }

    public static void main(String[] args) {
        // ConnectedComponents: two disjoint 3x3 blobs, same as Recovered.java
        BooleanImage bi = new BooleanImage(12, 12, 1);
        bi.fill(false);
        for (int x = 1; x <= 3; x++) for (int y = 1; y <= 3; y++) bi.setXYBoolean(x, y, true);
        for (int x = 7; x <= 9; x++) for (int y = 7; y <= 9; y++) bi.setXYBoolean(x, y, true);
        ArrayList<ArrayList<Point>> cc = ConnectedComponents.invoke(bi);

        System.out.println("ConnectedComponents-count\t0\t0\t" + cc.size());
        for (int i = 0; i < cc.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (Point p : cc.get(i)) {
                if (sb.length() > 0) sb.append(';');
                sb.append(p.x).append(',').append(p.y);
            }
            System.out.println("ConnectedComponents[" + i + "]\t" + cc.get(i).size() + "\t0\t" + sb);
        }

        // A less trivial case: a single 5x5 blob plus a diagonal-only
        // 2-pixel touch (8-connectivity means these must merge into one
        // component), to pin traversal order and the diagonal-neighbour path.
        BooleanImage bi2 = new BooleanImage(10, 10, 1);
        bi2.fill(false);
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) bi2.setXYBoolean(x, y, true);
        bi2.setXYBoolean(3, 3, true); // diagonal touch to the 3x3 block's corner
        bi2.setXYBoolean(4, 4, true);
        ArrayList<ArrayList<Point>> cc2 = ConnectedComponents.invoke(bi2);
        System.out.println("ConnectedComponents2-count\t0\t0\t" + cc2.size());
        for (int i = 0; i < cc2.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (Point p : cc2.get(i)) {
                if (sb.length() > 0) sb.append(';');
                sb.append(p.x).append(',').append(p.y);
            }
            System.out.println("ConnectedComponents2[" + i + "]\t" + cc2.get(i).size() + "\t0\t" + sb);
        }

        // ConvexHull, filled=false: triangle with an interior point, same as
        // Recovered.java, plus a second, less symmetric point set.
        BooleanImage tri = new BooleanImage(20, 20, 1);
        tri.fill(false);
        tri.setXYBoolean(2, 2, true);
        tri.setXYBoolean(15, 3, true);
        tri.setXYBoolean(8, 16, true);
        tri.setXYBoolean(8, 8, true);
        dumpBinary("ConvexHull(tri,false)", ConvexHull.invoke(tri, Boolean.FALSE));

        BooleanImage penta = new BooleanImage(24, 24, 1);
        penta.fill(false);
        int[][] pts = { {3, 3}, {20, 5}, {21, 18}, {10, 21}, {2, 12}, {11, 11} };
        for (int[] p : pts) penta.setXYBoolean(p[0], p[1], true);
        dumpBinary("ConvexHull(penta,false)", ConvexHull.invoke(penta, Boolean.FALSE));

        // GrayQFZ: two-level image, same as Recovered.java, plus a
        // three-level ramp to exercise the alpha-shrinking retry loop.
        ByteImage gi = new ByteImage(10, 10, 1);
        gi.fill(10);
        for (int x = 5; x < 10; x++) for (int y = 0; y < 10; y++) gi.setXYByte(x, y, 200);
        dumpInt("GrayQFZ(gi,0,255)", (Image) GrayQFZ.invoke(gi, 0, 255));

        ByteImage ramp = new ByteImage(9, 9, 1);
        for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) ramp.setXYByte(x, y, (x / 3) * 40);
        dumpInt("GrayQFZ(ramp,15,255)", (Image) GrayQFZ.invoke(ramp, 15, 255));
        dumpInt("GrayQFZ(ramp,15,30)", (Image) GrayQFZ.invoke(ramp, 15, 30));

        // MaxTree: same two-level image as Recovered.java, plus the ramp.
        Node root = MaxTree.invoke(gi);
        StringBuilder giTree = new StringBuilder();
        dumpTree(giTree, root);
        System.out.println("MaxTree(gi)\t0\t0\t" + giTree);

        Node rampRoot = MaxTree.invoke(ramp);
        StringBuilder rampTree = new StringBuilder();
        dumpTree(rampTree, rampRoot);
        System.out.println("MaxTree(ramp)\t0\t0\t" + rampTree);
    }
}

import java.awt.Point;

import vpt.util.Distance;
import vpt.util.HugeHierarchicalQueue;
import vpt.util.ordering.*;

/**
 * Golden dump for vpt.util pieces no other driver reaches:
 * Distance (every metric and overload), the AlgebraicOrderings not
 * exercised elsewhere (BitMix, CompositeOrdering, CustomLexico,
 * EuclideanNorm, LexicoCIELAB, ReverseLexico, SpectralAngular), and
 * HugeHierarchicalQueue.
 *
 * Orderings are dumped as the full compare() matrix over a vector set
 * (row i, column j = compare(v[i], v[j])), plus max/min/rank over the set,
 * so a mismatch pins down the exact pair that disagrees.
 */
public class DumpUtil {

    static void dumpValue(String name, double v) {
        System.out.println(name + "\t" + Double.toHexString(v));
    }

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    static void dumpText(String name, String s) {
        System.out.println(name + "\t" + s);
    }

    // Vector set for the orderings: RGB-like triples in [0,1], with ties in
    // the first channel(s), exact duplicates, and a zero vector, so every
    // comparison stage of each ordering gets exercised.
    static final double[][] VS = {
        {0.0, 0.0, 0.0},
        {0.5, 0.2, 0.9},
        {0.5, 0.2, 0.1},
        {0.5, 0.7, 0.1},
        {0.1, 0.9, 0.9},
        {0.9, 0.1, 0.1},
        {0.5, 0.2, 0.9},
        {0.25, 0.5, 0.75},
        {1.0, 1.0, 1.0},
        {0.3, 0.4, 0.0},
        {0.3, 0.0, 0.4},
        {0.2, 0.4, 0.6},
    };

    static void dumpOrdering(String name, AlgebraicOrdering or) {
        StringBuilder sb = new StringBuilder();
        for (double[] a : VS)
            for (double[] b : VS) {
                if (sb.length() > 0) sb.append(',');
                sb.append(or.compare(a, b));
            }
        dumpText(name + ".compare", sb.toString());
        dumpVector(name + ".max", or.max(VS));
        dumpVector(name + ".min", or.min(VS));
        // rank() sorts its argument in place (Arrays.sort): pass a copy so
        // every ordering starts from the same VS.
        dumpVector(name + ".rank(4)", or.rank(VS.clone(), 4));
        dumpVector(name + ".max2", or.max(VS[1], VS[3]));
        dumpVector(name + ".min2", or.min(VS[1], VS[3]));
    }

    public static void main(String[] args) {
        double[] p = {0.1, 0.4, 0.2, 0.3};
        double[] q = {0.25, 0.25, 0.3, 0.2};
        double[] r = {2.0, -1.0, 0.5, 3.0};
        double[] z = {0.0, 0.0, 0.0, 0.0};
        double[] shortv = {1.0, 2.0};
        double[][][] pairs = {{p, q}, {p, r}, {r, q}, {p, p}, {z, z}, {p, z}};
        String[] names = {"p,q", "p,r", "r,q", "p,p", "z,z", "p,z"};

        for (int i = 0; i < pairs.length; i++) {
            double[] a = pairs[i][0], b = pairs[i][1];
            String n = "(" + names[i] + ")";
            dumpValue("euclidean" + n, Distance.euclidean(a, b));
            dumpValue("intersection" + n, Distance.intersection(a, b));
            dumpValue("bhattacharyya" + n, Distance.bhattacharyya(a, b));
            dumpValue("cosine" + n, Distance.cosine(a, b));
            dumpValue("spectralAngleDistance" + n, Distance.spectralAngleDistance(a, b));
            dumpValue("innerProduct" + n, Distance.innerProduct(a, b));
            dumpValue("manhattan" + n, Distance.manhattan(a, b));
            dumpValue("chebyshev" + n, Distance.chebyshev(a, b));
            dumpValue("chiSquare" + n, Distance.chiSquare(a, b));
        }
        // Length mismatch: every metric prints to stderr and returns -1.0.
        dumpValue("euclidean(mismatch)", Distance.euclidean(p, shortv));
        dumpValue("chiSquare(mismatch)", Distance.chiSquare(p, shortv));
        dumpValue("euclidean(int)", Distance.euclidean(new int[] {3, 250, 7}, new int[] {10, 0, 7}));
        dumpValue("EuclideanDistance(int)", Distance.EuclideanDistance(1, 2, 4, 6));
        dumpValue("EuclideanDistance(double)", Distance.EuclideanDistance(0.5, -1.5, 2.25, 3.0));
        dumpValue("EuclideanDistance(Point)", Distance.EuclideanDistance(new Point(3, 4), new Point(-1, 1)));

        dumpOrdering("BitMix", new BitMix());
        dumpOrdering("EuclideanNorm", new EuclideanNorm());
        dumpOrdering("LexicoCIELAB", new LexicoCIELAB());
        dumpOrdering("ReverseLexico", new ReverseLexico());
        dumpOrdering("SpectralAngular", new SpectralAngular());
        dumpOrdering("CustomLexico(3)", new CustomLexico(3));
        dumpOrdering("CustomLexico(2,0,1)", new CustomLexico(new int[] {2, 0, 1}));
        // compare() is a majority vote that indexes exactly ors[0..2].
        dumpOrdering("CompositeOrdering(EuclideanNorm,ReverseLexico,BitMix)",
            new CompositeOrdering(new AlgebraicOrdering[] {new EuclideanNorm(), new ReverseLexico(), new BitMix()}));

        // HugeHierarchicalQueue: a fixed script of add/get/isEmpty calls on
        // sparse, large level values; each get records "x,y", "null", or
        // "EXC" (LinkedList.remove() on an emptied level throws).
        HugeHierarchicalQueue hq = new HugeHierarchicalQueue(4);
        StringBuilder sb = new StringBuilder();
        hq.add(new Point(1, 1), 1000000);
        hq.add(new Point(2, 2), 5);
        hq.add(new Point(3, 3), 1000000);
        hq.add(new Point(4, 4), -7);
        int[] gets = {1000000, 5, 1000000, 42, 5, -7, 1000000};
        for (int v : gets) {
            if (sb.length() > 0) sb.append(';');
            sb.append(v).append(hq.isEmpty(v) ? ":E:" : ":N:");
            try {
                Point pt = hq.get(v);
                sb.append(pt == null ? "null" : pt.x + "," + pt.y);
            } catch (java.util.NoSuchElementException e) {
                sb.append("EXC");
            }
        }
        dumpText("HugeHierarchicalQueue.script", sb.toString());
    }
}

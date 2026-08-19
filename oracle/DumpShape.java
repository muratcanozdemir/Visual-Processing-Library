import vpt.*;
import vpt.algorithms.shape.*;
import vpt.util.se.FlatSE;
import java.util.ArrayList;
import java.awt.Point;

/**
 * Dumps the vpt.algorithms.shape package, except the four classes confirmed
 * (via a live, timeout-bounded JDK run) to hang forever through the same
 * BFillHole/ConvexHull(filled=true) mechanism documented on
 * mm/binary/geo/BFillHole.py: ConvexAreaRatio, ConvexPerimeterRatio,
 * Dispersion, EdgeBackForeRatioHistogram; and AxisOfSymmetry, which hangs
 * for an independent reason -- execute() ends with a live
 * Display2D.invoke(input) GUI popup call, confirmed by thread-dumping a
 * hung regen-goldens.sh run and finding live non-daemon AWT-EventQueue-0/
 * AWT-Shutdown threads keeping the JVM alive forever after main() already
 * returned. Same category as the already-documented Canny/
 * BReconstructionByErosion Display2D hangs.
 *
 * DFTZernikeMoments is called but not dumped bit-exact: it inherits
 * FFT/IFFT's always-all-zero bug (ComplexNumber.cSum/cDiff, see
 * frequential.txt/DumpFrequential.java) and its output is entirely NaN from
 * a 0/0 division, and NaN's bit pattern isn't guaranteed to round-trip
 * identically across a Java/NumPy boundary the way a normal double is --
 * smoke-tested in Python only (see test_shape_golden.py).
 *
 * Every test mask is a plain ByteImage built with fill(false)/setXYBoolean,
 * never a BooleanImage: BooleanImage.getDouble is documented-inverted
 * (returns 1.0 for a false pixel), and several of these algorithms
 * (ImageCentroid, Tools.volume, Threshold) read through the double view --
 * a BooleanImage mask would silently invert their foreground/background.
 * ByteImage's getDouble/getBoolean are not inverted, so long as background
 * is real 0 (fill(false)) rather than some other "low" byte value.
 *
 * Usage: java -cp classes:oracle DumpShape > python/tests/goldens/shape.txt
 */
public class DumpShape {

    static void dumpValue(String name, Double v) {
        if (v == null) { System.out.println(name + "\tNULL"); return; }
        System.out.println(name + "\t" + Double.toHexString(v.doubleValue()));
    }

    static void dumpVector(String name, double[] v) {
        if (v == null) { System.out.println(name + "\tNULL"); return; }
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    static void dumpDouble(String name, Image img) {
        if (img == null) { System.out.println(name + "\tNULL"); return; }
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(Double.toHexString(img.getXYCDouble(x, y, c)));
                }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    static void dumpPointList(String name, ArrayList<Point> pts) {
        StringBuilder sb = new StringBuilder();
        for (Point p : pts) {
            if (sb.length() > 0) sb.append(';');
            sb.append(p.x).append(',').append(p.y);
        }
        System.out.println(name + "\t" + pts.size() + "\t0\t" + sb);
    }

    static void dumpPointArray(String name, Point[] pts) {
        if (pts == null) { System.out.println(name + "\tNULL"); return; }
        StringBuilder sb = new StringBuilder();
        for (Point p : pts) {
            if (sb.length() > 0) sb.append(';');
            sb.append(p.x).append(',').append(p.y);
        }
        System.out.println(name + "\t" + pts.length + "\t0\t" + sb);
    }

    static void dumpComponents(String name, ArrayList<ArrayList<Point>> ccs) {
        System.out.println(name + "-count\t0\t0\t" + ccs.size());
        for (int i = 0; i < ccs.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (Point p : ccs.get(i)) {
                if (sb.length() > 0) sb.append(';');
                sb.append(p.x).append(',').append(p.y);
            }
            System.out.println(name + "[" + i + "]\t" + ccs.get(i).size() + "\t0\t" + sb);
        }
    }

    public static void main(String[] args) {
        // Primary test shape: an asymmetric L-blob (rectangle + offset
        // square sharing one edge), off-centre in its canvas, so centroid/
        // orientation/width-profile descriptors all get non-degenerate,
        // non-symmetric input.
        ByteImage shape = new ByteImage(24, 24, 1);
        shape.fill(false);
        for (int x = 3; x < 13; x++)
            for (int y = 4; y < 10; y++)
                shape.setXYBoolean(x, y, true);
        for (int x = 9; x < 17; x++)
            for (int y = 10; y < 16; y++)
                shape.setXYBoolean(x, y, true);

        dumpValue("Compactness(shape)", Compactness.invoke(shape));
        dumpValue("MorphologicalElongation(shape,sq3)", MorphologicalElongation.invoke(shape, FlatSE.square(3)));
        dumpValue("MeanDistanceToCentroid(shape)", MeanDistanceToCentroid.invoke(shape));
        dumpValue("VarianceOfDistanceToCentroid(shape)", VarianceOfDistanceToCentroid.invoke(shape));
        dumpValue("Orientation(shape)", Orientation.invoke(shape));

        dumpVector("MultiScaleCCCount(shape,3,1,1)", MultiScaleCCCount.invoke(shape, Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(1)));

        dumpVector("WidthFactor(shape,4)", WidthFactor.invoke(shape, Integer.valueOf(4)));
        dumpVector("AreaWidthFactor(shape,4)", AreaWidthFactor.invoke(shape, Integer.valueOf(4)));
        dumpVector("NormalizedWidthFactor(shape,4)", NormalizedWidthFactor.invoke(shape, Integer.valueOf(4)));
        dumpVector("CircularWidthFactor(shape,4)", CircularWidthFactor.invoke(shape, Integer.valueOf(4)));
        dumpVector("CircularWidthFactor2(shape,4)", CircularWidthFactor2.invoke(shape, Integer.valueOf(4)));
        dumpVector("CircularWidthFactor3(shape,4)", CircularWidthFactor3.invoke(shape, Integer.valueOf(4)));

        dumpVector("RegionalMomentsOfInertia(shape,4)", RegionalMomentsOfInertia.invoke(shape, Integer.valueOf(4)));
        dumpVector("RegionalMomentsOfInertia2(shape,4)", RegionalMomentsOfInertia2.invoke(shape, Integer.valueOf(4)));
        dumpVector("ContourPointDistributionHistogram(shape,4)", ContourPointDistributionHistogram.invoke(shape, Integer.valueOf(4)));

        dumpVector("ZernikeMoments(shape)", ZernikeMoments.invoke(shape));
        // DFTZernikeMoments: inherited-all-NaN, see class doc comment above -- called for a
        // liveness check only, never compared bit-exact.
        DFTZernikeMoments.invoke(shape);

        dumpDouble("FPFT(shape)", FPFT.invoke(shape));
        dumpVector("FourierDescriptor(shape,8)", FourierDescriptor.invoke(shape, Integer.valueOf(8)));

        // AxisOfSymmetry: NOT called here. Confirmed always null (verified via
        // javap -- see AxisOfSymmetry.py's docstring), but execute() also ends
        // with a live Display2D.invoke(input) GUI popup call that JD-Core
        // didn't strip -- confirmed directly (thread-dumped a hung regen-
        // goldens.sh run and found live non-daemon AWT-EventQueue-0/AWT-
        // Shutdown threads keeping the JVM alive forever after main() already
        // returned), the same category as the already-documented Canny/
        // BReconstructionByErosion Display2D hangs. Smoke-tested in Python
        // only (see test_shape_golden.py), where the port drops that call.
        dumpVector("MultiLobeMeanShapeFeatures(shape)", MultiLobeMeanShapeFeatures.invoke(shape));
        // Genuinely always raises internally (2-arg preprocess call against a
        // 1-name inputFields) -- see CentroidContourDistance.py's docstring.
        double[] ccd = CentroidContourDistance.invoke(shape, Integer.valueOf(8));
        System.out.println("CentroidContourDistance(shape,8)\t" + (ccd == null ? "NULL" : "NOT-NULL"));

        dumpVector("BasicShapeStatistics(shape)", BasicShapeStatistics.invoke(shape));
        dumpVector("AngleCodeHistogram(shape,8,0.5)", AngleCodeHistogram.invoke(shape, Integer.valueOf(8), Double.valueOf(0.5)));

        dumpPointList("ConnectedComponentFromSeed(shape,(5,6))", ConnectedComponentFromSeed.invoke(shape, new Point(5, 6)));

        // GrayConnectedComponents{,2}: partition by exact int-label equality
        // (getXYInt), not boolean foreground -- needs an IntegerImage (a
        // ByteImage's setInt/getInt only round-trips the top byte of a
        // 32-bit value, see IntegerImage.py's docstring for the contrast).
        IntegerImage labels = new IntegerImage(12, 12, 1);
        labels.fill(0);
        for (int x = 1; x <= 4; x++) for (int y = 1; y <= 4; y++) labels.setXYInt(x, y, 5);
        for (int x = 7; x <= 9; x++) for (int y = 6; y <= 9; y++) labels.setXYInt(x, y, 9);
        dumpComponents("GrayConnectedComponents(labels)", GrayConnectedComponents.invoke(labels));

        ArrayList<Point>[][] gcc2 = GrayConnectedComponents2.invoke(labels);
        int distinct = 0;
        java.util.IdentityHashMap<ArrayList<Point>, Boolean> seen = new java.util.IdentityHashMap<>();
        for (int x = 0; x < 12; x++)
            for (int y = 0; y < 12; y++)
                if (gcc2[x][y] != null && seen.put(gcc2[x][y], Boolean.TRUE) == null)
                    distinct++;
        System.out.println("GrayConnectedComponents2(labels)-distinct\t0\t0\t" + distinct);
        System.out.println("GrayConnectedComponents2(labels)@(2,2)-size\t0\t0\t" + gcc2[2][2].size());
        System.out.println("GrayConnectedComponents2(labels)@(8,7)-size\t0\t0\t" + gcc2[8][7].size());
        System.out.println("GrayConnectedComponents2(labels)@(0,0)-size\t0\t0\t" + gcc2[0][0].size());

        // DominantPointsDetection: a clean filled rectangle so the RDP
        // simplification collapses to (close to) the four corners.
        ByteImage rect = new ByteImage(20, 20, 1);
        rect.fill(false);
        for (int x = 4; x < 16; x++)
            for (int y = 5; y < 13; y++)
                rect.setXYBoolean(x, y, true);
        dumpPointArray("DominantPointsDetection(rect,0.5)", DominantPointsDetection.invoke(rect, Double.valueOf(0.5)));
        dumpPointArray("DominantPointsDetection(rect,3.0)", DominantPointsDetection.invoke(rect, Double.valueOf(3.0)));
    }
}

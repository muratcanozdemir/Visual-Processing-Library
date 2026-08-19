import vpt.*;
import vpt.algorithms.statistical.*;

/**
 * Dumps the vpt.algorithms.statistical package, except PCA: its
 * EigenvalueDecomposition (Jama, Householder+QL) is not expected to agree
 * bit-for-bit with NumPy/LAPACK's eigh (different algorithm, plus a sign/
 * degenerate-eigenspace ambiguity even when both are "correct") -- the same
 * accepted-divergence category as NonFlatSE.decompose's SVD. See PCA.py's
 * docstring. ReversePCA is still dumped, fed a hand-picked eigenvectors/
 * mean pair rather than PCA's own output, since ReversePCA itself (just
 * matrix multiplication and addition) is fully deterministic.
 *
 * Usage: java -cp classes:oracle DumpStatistical > python/tests/goldens/statistical.txt
 */
public class DumpStatistical {

    static void dumpDouble(String name, Image img) {
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

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    static void dumpValue(String name, Double v) {
        System.out.println(name + "\t" + Double.toHexString(v.doubleValue()));
    }

    public static void main(String[] args) {
        ByteImage g = new ByteImage(10, 10, 1);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                g.setXYCByte(x, y, 0, (x >= 3 && x < 7 && y >= 3 && y < 7) ? 200 : 20);

        dumpValue("Mean(g)", Mean.invoke(g));
        dumpValue("SDev(g)", SDev.invoke(g));
        dumpValue("Variance(g)", Variance.invoke(g));
        dumpVector("Moment(g,1,0)", Moment.invoke(g, Integer.valueOf(1), Integer.valueOf(0)));
        dumpVector("Moment(g,2,1)", Moment.invoke(g, Integer.valueOf(2), Integer.valueOf(1)));
        dumpDouble("Normalize(g,0.5,0.1)", Normalize.invoke(g, Double.valueOf(0.5), Double.valueOf(0.1)));
        dumpVector("SpatialMoment(g,2,0)", SpatialMoment.invoke(g, Integer.valueOf(2), Integer.valueOf(0)));
        dumpVector("CentralMoment(g,2,0)", CentralMoment.invoke(g, Integer.valueOf(2), Integer.valueOf(0)));
        dumpValue("Kurtosis(g)", Kurtosis.invoke(g));
        dumpValue("Skewness(g)", Skewness.invoke(g));
        dumpValue("ContrastRatio(g)", ContrastRatio.invoke(g));

        BooleanImage mask = new BooleanImage(10, 10, 1);
        mask.fill(false);
        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                mask.setXYBoolean(x, y, true);
        dumpValue("ContrastRatio(g,mask)", ContrastRatio.invoke(g, mask));

        ByteImage g2 = new ByteImage(10, 10, 1);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                g2.setXYCByte(x, y, 0, x < 5 ? 200 : 20);
        dumpValue("NMSE(g,g2)", NMSE.invoke(g, g2));

        for (int order = 1; order <= 8; order++) {
            dumpVector("InvariantMoment(g," + order + ")",
                InvariantMoment.invoke(g, Integer.valueOf(order)));
        }

        dumpValue("ZernikeMoment(g,4,2)", ZernikeMoment.invoke(g, Integer.valueOf(4), Integer.valueOf(2)));
        dumpValue("ZernikeMoment(g,6,0)", ZernikeMoment.invoke(g, Integer.valueOf(6), Integer.valueOf(0)));
        dumpValue("ZernikeMoment(g,5,3)", ZernikeMoment.invoke(g, Integer.valueOf(5), Integer.valueOf(3)));

        // Hand-picked, not from PCA's own (non-golden) eigendecomposition.
        ByteImage rgb = new ByteImage(6, 6, 3);
        for (int x = 0; x < 6; x++)
            for (int y = 0; y < 6; y++) {
                rgb.setXYCByte(x, y, 0, x * 40);
                rgb.setXYCByte(x, y, 1, y * 40);
                rgb.setXYCByte(x, y, 2, (x + y) * 20);
            }
        double[][] eigenvectors = {
            { 0.7071067811865476, 0.7071067811865476, 0.0 },
            { -0.7071067811865476, 0.7071067811865476, 0.0 },
            { 0.0, 0.0, 1.0 },
        };
        double[] mean = { 0.4, 0.4, 0.4 };
        dumpDouble("ReversePCA(rgb,eigenvectors,mean)", ReversePCA.invoke(rgb, eigenvectors, mean));
    }
}

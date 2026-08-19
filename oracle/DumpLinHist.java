import vpt.*;
import vpt.algorithms.conversion.RGB2Gray;
import vpt.algorithms.histogram.ContrastStretch;
import vpt.algorithms.histogram.Histogram;
import vpt.algorithms.linear.Convolution;
import vpt.algorithms.linear.Sobel;
import vpt.algorithms.segmentation.OtsuThreshold;
import vpt.algorithms.segmentation.Threshold;
import vpt.util.se.NonFlatSE;
import java.awt.Point;

/**
 * Dumps RGB2Gray, Histogram, ContrastStretch, Threshold, OtsuThreshold,
 * Convolution and Sobel -- the remaining operators oracle/Sweep.java
 * exercises (Sweep itself only checks "did it throw", not the values; this
 * pins the actual pixel/scalar results).
 *
 * DoubleImage results are dumped via Double.toHexString for bit-exactness;
 * ByteImage/BooleanImage results via getXYByte/getXYBoolean, which is
 * sufficient because those types quantize on write.
 *
 * Usage: java -cp classes:oracle DumpLinHist > python/tests/goldens/linhist.txt
 */
public class DumpLinHist {

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

    static void dumpBinary(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++)
                    sb.append(img.getXYCBoolean(x, y, c) ? '1' : '0');
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t0\t0\t" + sb);
    }

    public static void main(String[] args) {
        ByteImage g = new ByteImage(16, 16, 1);
        g.fill(50);
        for (int x = 4; x < 12; x++)
            for (int y = 4; y < 12; y++)
                g.setXYByte(x, y, 180);

        ByteImage rgb = new ByteImage(16, 16, 3);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                rgb.setXYCByte(x, y, 0, x * 15);
                rgb.setXYCByte(x, y, 1, y * 15);
                rgb.setXYCByte(x, y, 2, 120);
            }

        dumpVector("Histogram(g,false)", Histogram.invoke(g, Boolean.FALSE));
        dumpVector("Histogram(g,true)", Histogram.invoke(g, Boolean.TRUE));
        dumpVector("Histogram(rgb,false)", Histogram.invoke(rgb, Boolean.FALSE));

        dumpDouble("RGB2Gray(rgb)", RGB2Gray.invoke(rgb));

        dumpDouble("ContrastStretch(g)", ContrastStretch.invoke(g));
        dumpDouble("ContrastStretch(rgb)", ContrastStretch.invoke(rgb));

        dumpBinary("Threshold(g,0.5)", Threshold.invoke(g, 0.5));
        dumpBinary("Threshold(g,0.7)", Threshold.invoke(g, 0.7));
        dumpBinary("OtsuThreshold(g)", OtsuThreshold.invoke(g));

        NonFlatSE gx = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0, 0.0, 1.0 }, { -2.0, 0.0, 2.0 }, { -1.0, 0.0, 1.0 } });
        NonFlatSE gy = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0, -2.0, -1.0 }, { 0.0, 0.0, 0.0 }, { 1.0, 2.0, 1.0 } });
        dumpDouble("Convolution(g,gx)", Convolution.invoke(g, gx));
        dumpDouble("Convolution(g,gy)", Convolution.invoke(g, gy));
        dumpDouble("Convolution(rgb,gx)", Convolution.invoke(rgb, gx));

        for (int t = 0; t <= 3; t++) {
            dumpDouble("Sobel(g," + t + ",false)", Sobel.invoke(g, Integer.valueOf(t), Boolean.FALSE));
            dumpDouble("Sobel(g," + t + ",true)", Sobel.invoke(g, Integer.valueOf(t), Boolean.TRUE));
        }
    }
}

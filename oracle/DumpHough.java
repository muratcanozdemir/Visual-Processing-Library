import vpt.*;
import vpt.algorithms.hough.HoughTransformLine;

/**
 * Dumps the vpt.algorithms.hough package -- complete (3/3), except
 * HoughTransformLine2/HoughTransformCircle: both call Display2D in the
 * Java, confirmed directly (not just assumed from the linear.Canny
 * precedent) to hang the JVM forever via a standalone timeout-bounded
 * harness run -- see HoughTransformLine2.py's docstring. Smoke-tested
 * in Python only.
 *
 * Usage: java -cp classes:oracle DumpHough > python/tests/goldens/hough.txt
 */
public class DumpHough {

    static void dumpGray(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(img.getXYCByte(x, y, c));
                }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        // A horizontal and a vertical line crossing, on a 16x12 canvas.
        ByteImage img = new ByteImage(16, 12, 1);
        img.fill(false);
        for (int x = 0; x < 16; x++) img.setXYBoolean(x, 6, true);
        for (int y = 0; y < 12; y++) img.setXYBoolean(8, y, true);

        dumpGray("HoughTransformLine(img)", HoughTransformLine.invoke(img));
    }
}

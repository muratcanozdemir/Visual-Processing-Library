import vpt.*;
import vpt.algorithms.color.*;

/**
 * Dumps the vpt.algorithms.color package -- complete (5/5).
 *
 * Usage: java -cp classes:oracle DumpColor > python/tests/goldens/color.txt
 */
public class DumpColor {

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    public static void main(String[] args) {
        // RGB test image: a smooth gradient across all three channels, no
        // degenerate all-zero channel.
        ByteImage rgb = new ByteImage(12, 12, 3);
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                rgb.setXYCByte(x, y, 0, (x * 20) % 256);
                rgb.setXYCByte(x, y, 1, (y * 20) % 256);
                rgb.setXYCByte(x, y, 2, ((x + y) * 10) % 256);
            }
        }

        dumpVector("ColorMoments(rgb)", ColorMoments.invoke(rgb));
        dumpVector("ColorMomentInvariants(rgb)", ColorMomentInvariants.invoke(rgb));
        dumpVector("BIC(rgb)", BIC.invoke(rgb));
        dumpVector("ColorStructureRGB64(rgb)", ColorStructureRGB64.invoke(rgb));

        // AutoCorrelogramHSY733 expects an already-quantised 3x3x7 (HSY733)
        // image, not raw RGB -- see AutoCorrelogramHSY733.py's docstring.
        ByteImage hsy733 = new ByteImage(12, 12, 3);
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                hsy733.setXYCByte(x, y, 0, x % 3);
                hsy733.setXYCByte(x, y, 1, y % 3);
                hsy733.setXYCByte(x, y, 2, (x + y) % 7);
            }
        }
        dumpVector("AutoCorrelogramHSY733(hsy733)", AutoCorrelogramHSY733.invoke(hsy733));
    }
}

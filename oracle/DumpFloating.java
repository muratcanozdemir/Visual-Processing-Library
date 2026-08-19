import vpt.*;
import vpt.util.se.FloatingFlatSE;
import vpt.algorithms.mm.gray.floating.*;

/**
 * Dumps the mm/gray/floating family: GFloatingDilation/Erosion/Opening/
 * Closing and the two covariance curves, against a circular floating SE.
 *
 * Usage: java -cp classes:oracle DumpFloating > python/tests/goldens/floating.txt
 */
public class DumpFloating {

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

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t0\t0\t" + sb);
    }

    public static void main(String[] args) {
        ByteImage img = new ByteImage(20, 20, 1);
        img.fill(0);
        for (int x = 8; x < 13; x++)
            for (int y = 8; y < 13; y++)
                img.setXYByte(x, y, 200);

        FloatingFlatSE se = FloatingFlatSE.circle(3, 24);

        dumpGray("GFloatingDilation(img,circle3)", GFloatingDilation.invoke(img, se));
        dumpGray("GFloatingErosion(img,circle3)", GFloatingErosion.invoke(img, se));
        dumpGray("GFloatingOpening(img,circle3)", GFloatingOpening.invoke(img, se));
        dumpGray("GFloatingClosing(img,circle3)", GFloatingClosing.invoke(img, se));

        dumpVector("FloatingCovariance(img,4,2)", FloatingCovariance.invoke(img, Integer.valueOf(4), Integer.valueOf(2)));
        dumpVector("FloatingOpeningBasedCovariance(img,4,2)", FloatingOpeningBasedCovariance.invoke(img, Integer.valueOf(4), Integer.valueOf(2)));
    }
}

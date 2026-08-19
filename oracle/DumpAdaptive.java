import vpt.*;
import vpt.algorithms.mm.gray.adaptive.*;
import vpt.util.se.FlatSE;
import java.awt.Point;

/**
 * Dumps the mm/gray/adaptive family: ComputeSEs (pinning both the SE
 * pixel counts at several representative points and the full coordinate
 * list at one of them) and ADilation/AErosion/AOpening/AClosing built
 * from those SEs.
 *
 * Usage: java -cp classes:oracle DumpAdaptive > python/tests/goldens/adaptive.txt
 */
public class DumpAdaptive {

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
        ByteImage img = new ByteImage(24, 24, 1);
        img.fill(50);
        for (int x = 8; x < 17; x++)
            for (int y = 8; y < 17; y++)
                img.setXYByte(x, y, 180);

        FlatSE[][] se = ComputeSEs.invoke(img, Integer.valueOf(10));

        int[][] pts = { {12, 12}, {2, 2}, {8, 8}, {0, 0}, {23, 23} };
        for (int[] p : pts) {
            System.out.println("ComputeSEs.count(" + p[0] + "," + p[1] + ")\t0\t0\t"
                + se[p[0]][p[1]].getCoords().length);
        }

        StringBuilder coordSb = new StringBuilder();
        for (Point c : se[12][12].getCoords()) {
            if (coordSb.length() > 0) coordSb.append(';');
            coordSb.append(c.x).append(',').append(c.y);
        }
        System.out.println("ComputeSEs.coords(12,12)\t0\t0\t" + coordSb);

        dumpGray("ADilation(img,se10)", ADilation.invoke(img, se));
        dumpGray("AErosion(img,se10)", AErosion.invoke(img, se));
        dumpGray("AOpening(img,se10)", AOpening.invoke(img, se));
        dumpGray("AClosing(img,se10)", AClosing.invoke(img, se));
    }
}

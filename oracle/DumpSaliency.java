import vpt.*;
import vpt.algorithms.saliency.SaliencyMap;
import vpt.algorithms.saliency.SaliencyMapSlow;

/**
 * Dumps the vpt.algorithms.saliency package -- complete (2/2).
 *
 * Usage: java -cp classes:oracle DumpSaliency > python/tests/goldens/saliency.txt
 */
public class DumpSaliency {

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
        // A 20x20 RGB gradient, large enough for the minDim/8..minDim/2
        // multi-scale windows to be non-trivial (minDim=20 -> R2={2,5,10}).
        ByteImage rgb = new ByteImage(20, 20, 3);
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                rgb.setXYCByte(x, y, 0, (x * 10) % 256);
                rgb.setXYCByte(x, y, 1, (y * 10) % 256);
                rgb.setXYCByte(x, y, 2, ((x + y) * 5) % 256);
            }
        }

        dumpGray("SaliencyMap(rgb)", SaliencyMap.invoke(rgb));
        dumpGray("SaliencyMapSlow(rgb)", SaliencyMapSlow.invoke(rgb));
    }
}

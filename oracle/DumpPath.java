import vpt.*;
import vpt.algorithms.mm.binary.path.BPathOpening;
import vpt.algorithms.mm.gray.path.GPathOpening;

/**
 * Dumps BPathOpening (mm/binary/path) and GPathOpening (mm/gray/path) --
 * runs in all 4 directions the algorithm checks (horizontal, vertical, and
 * both diagonals), plus a short run that should be filtered out.
 *
 * Usage: java -cp classes:oracle DumpPath > python/tests/goldens/path.txt
 */
public class DumpPath {

    static void dumpBinary(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                sb.append(img.getXYBoolean(x, y) ? '1' : '0');
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim() + "\t" + sb);
    }

    static void dumpGray(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++) {
                if (sb.length() > 0) sb.append(',');
                sb.append(img.getXYByte(x, y));
            }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        BooleanImage bi = new BooleanImage(20, 20, 1);
        bi.fill(false);
        for (int x = 2; x < 12; x++) bi.setXYBoolean(x, 5, true);
        for (int y = 10; y < 13; y++) bi.setXYBoolean(15, y, true);

        dumpBinary("BPathOpening(bi,8)", BPathOpening.invoke(bi, Integer.valueOf(8)));
        dumpBinary("BPathOpening(bi,2)", BPathOpening.invoke(bi, Integer.valueOf(2)));

        BooleanImage biNWSE = new BooleanImage(20, 20, 1);
        biNWSE.fill(false);
        for (int i = 0; i < 10; i++) biNWSE.setXYBoolean(2 + i, 2 + i, true);
        dumpBinary("BPathOpening(NWSE,8)", BPathOpening.invoke(biNWSE, Integer.valueOf(8)));

        BooleanImage biSWNE = new BooleanImage(20, 20, 1);
        biSWNE.fill(false);
        for (int i = 0; i < 10; i++) biSWNE.setXYBoolean(2 + i, 11 - i, true);
        dumpBinary("BPathOpening(SWNE,8)", BPathOpening.invoke(biSWNE, Integer.valueOf(8)));

        ByteImage g = new ByteImage(20, 20, 1);
        g.fill(30);
        for (int x = 2; x < 12; x++) g.setXYByte(x, 5, 200);
        for (int y = 10; y < 13; y++) g.setXYByte(15, y, 200);

        dumpGray("GPathOpening(g,8)", GPathOpening.invoke(g, Integer.valueOf(8)));
    }
}

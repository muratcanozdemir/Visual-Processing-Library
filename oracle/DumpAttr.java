import vpt.*;
import vpt.algorithms.mm.binary.attribute.*;

/**
 * Dumps BAreaOpening / BAreaClosing (mm/binary/attribute).
 *
 * Usage: java -cp classes:oracle DumpAttr > python/tests/goldens/attr.txt
 */
public class DumpAttr {

    static void dumpBinary(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                sb.append(img.getXYBoolean(x, y) ? '1' : '0');
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        BooleanImage bi = new BooleanImage(12, 12, 1);
        bi.fill(false);
        for (int x = 1; x <= 3; x++) for (int y = 1; y <= 3; y++) bi.setXYBoolean(x, y, true);
        for (int x = 7; x <= 8; x++) for (int y = 7; y <= 8; y++) bi.setXYBoolean(x, y, true);

        dumpBinary("BAreaOpening(bi,1)", BAreaOpening.invoke(bi, Integer.valueOf(1)));
        dumpBinary("BAreaOpening(bi,5)", BAreaOpening.invoke(bi, Integer.valueOf(5)));
        dumpBinary("BAreaOpening(bi,10)", BAreaOpening.invoke(bi, Integer.valueOf(10)));

        BooleanImage bi2 = new BooleanImage(10, 10, 1);
        bi2.fill(false);
        for (int x = 1; x < 9; x++) for (int y = 1; y < 9; y++) bi2.setXYBoolean(x, y, true);
        bi2.setXYBoolean(4, 4, false);
        bi2.setXYBoolean(2, 2, false);
        bi2.setXYBoolean(2, 3, false);
        bi2.setXYBoolean(3, 2, false);

        dumpBinary("BAreaClosing(bi2,1)", BAreaClosing.invoke(bi2, Integer.valueOf(1)));
        dumpBinary("BAreaClosing(bi2,3)", BAreaClosing.invoke(bi2, Integer.valueOf(3)));
        dumpBinary("BAreaClosing(bi2,5)", BAreaClosing.invoke(bi2, Integer.valueOf(5)));
    }
}

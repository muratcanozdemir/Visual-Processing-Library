import vpt.*;
import vpt.algorithms.mm.gray.connected.*;
import vpt.algorithms.mm.gray.connected.attribute.Area;

/**
 * Dumps the mm/gray/connected top-level family: GAreaOpening/Closing and
 * the GMaxTree{Incr,NonIncr}{,Multi}Attr{Opening,Closing} filters, all with
 * an Area(5) predicate against a 16x16 image with one large (64px) 180-value
 * block and one small (1px) noise spike that should be filtered out.
 *
 * Usage: java -cp classes:oracle DumpConnected > python/tests/goldens/connected.txt
 */
public class DumpConnected {

    interface ImageSupplier {
        Image get();
    }

    static void dumpGraySafe(String name, ImageSupplier supplier) {
        Image img;
        try {
            img = supplier.get();
        } catch (RuntimeException e) {
            System.out.println(name + "\tTHREW\t" + e.getClass().getName());
            return;
        }
        dumpGray(name, img);
    }

    static void dumpGray(String name, Image img) {
        if (img == null) {
            System.out.println(name + "\tNULL");
            return;
        }
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
        ByteImage g = new ByteImage(16, 16, 1);
        g.fill(50);
        for (int x = 4; x < 12; x++)
            for (int y = 4; y < 12; y++)
                g.setXYByte(x, y, 180);
        g.setXYByte(1, 1, 220);

        Area crit = new Area(Integer.valueOf(5));

        dumpGray("GAreaOpening(g,5)", GAreaOpening.invoke(g, Integer.valueOf(5)));
        dumpGray("GAreaClosing(g,5)", GAreaClosing.invoke(g, Integer.valueOf(5)));
        dumpGray("GMaxTreeIncrAttrOpening(g,Area5)", GMaxTreeIncrAttrOpening.invoke(g, crit));
        dumpGray("GMaxTreeIncrAttrClosing(g,Area5)", GMaxTreeIncrAttrClosing.invoke(g, crit));

        ByteImage rgb = new ByteImage(16, 16, 2);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                rgb.setXYCByte(x, y, 0, g.getXYByte(x, y));
                rgb.setXYCByte(x, y, 1, 100);
            }
        dumpGray("GMaxTreeIncrMultiAttrOpening(rgb,0,Area5)", GMaxTreeIncrMultiAttrOpening.invoke(rgb, Integer.valueOf(0), crit));
        dumpGray("GMaxTreeIncrMultiAttrClosing(rgb,0,Area5)", GMaxTreeIncrMultiAttrClosing.invoke(rgb, Integer.valueOf(0), crit));

        dumpGraySafe("GMaxTreeNonIncrAttrOpening(g,Area5,DIRECT)",
            () -> GMaxTreeNonIncrAttrOpening.invoke(g, crit, Integer.valueOf(0)));
        dumpGray("GMaxTreeNonIncrAttrOpening(g,Area5,MIN)", GMaxTreeNonIncrAttrOpening.invoke(g, crit, Integer.valueOf(2)));
        dumpGray("GMaxTreeNonIncrAttrOpening(g,Area5,MAX)", GMaxTreeNonIncrAttrOpening.invoke(g, crit, Integer.valueOf(3)));
        dumpGray("GMaxTreeNonIncrAttrOpening(g,Area5,SUBTRACTIVE)", GMaxTreeNonIncrAttrOpening.invoke(g, crit, Integer.valueOf(1)));
        dumpGray("GMaxTreeNonIncrAttrClosing(g,Area5)", GMaxTreeNonIncrAttrClosing.invoke(g, crit));

        dumpGray("GMaxTreeNonIncrMultiAttrOpening(rgb,0,Area5)", GMaxTreeNonIncrMultiAttrOpening.invoke(rgb, Integer.valueOf(0), crit));
        dumpGray("GMaxTreeNonIncrMultiAttrClosing(rgb,0,Area5)", GMaxTreeNonIncrMultiAttrClosing.invoke(rgb, Integer.valueOf(0), crit));
    }
}

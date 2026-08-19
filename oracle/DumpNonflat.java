import vpt.*;
import vpt.util.se.NonFlatSE;
import vpt.algorithms.mm.gray.nonflat.*;
import java.awt.Point;

/**
 * Dumps GNFDilation, GNFErosion, GNFOpening, GNFClosing (mm/gray/nonflat) --
 * dilation/erosion by a real-valued (non-flat) structuring element rather
 * than a binary mask.
 *
 * Usage: java -cp classes:oracle DumpNonflat > python/tests/goldens/nonflat.txt
 */
public class DumpNonflat {

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
        ByteImage g = new ByteImage(16, 16, 1);
        g.fill(50);
        for (int x = 4; x < 12; x++)
            for (int y = 4; y < 12; y++)
                g.setXYByte(x, y, 180);

        // flat (all-zero-weight) 3x3 kernel: should behave like the flat
        // FlatSE.square(3) operators
        NonFlatSE flat = new NonFlatSE(new Point(1, 1),
            new double[][] { {0,0,0}, {0,0,0}, {0,0,0} });
        dumpGray("GNFDilation(g,flat3)", GNFDilation.invoke(g, flat));
        dumpGray("GNFErosion(g,flat3)", GNFErosion.invoke(g, flat));
        dumpGray("GNFOpening(g,flat3)", GNFOpening.invoke(g, flat));
        dumpGray("GNFClosing(g,flat3)", GNFClosing.invoke(g, flat));

        // a genuinely weighted (non-flat) 3x3 kernel, asymmetric so the
        // reflection in GNFErosion is actually exercised
        double a = 30.0 / 255.0, b = -10.0 / 255.0;
        NonFlatSE weighted = new NonFlatSE(new Point(1, 1),
            new double[][] { {a, 0, b}, {0, 0, 0}, {b, 0, a} });
        dumpGray("GNFDilation(g,weighted)", GNFDilation.invoke(g, weighted));
        dumpGray("GNFErosion(g,weighted)", GNFErosion.invoke(g, weighted));
        dumpGray("GNFOpening(g,weighted)", GNFOpening.invoke(g, weighted));
        dumpGray("GNFClosing(g,weighted)", GNFClosing.invoke(g, weighted));

        // a cone-shaped kernel from the existing NonFlatSE factory, as a
        // more realistic non-flat SE than a hand-built one
        NonFlatSE cone = NonFlatSE.cone(2, 40.0 / 255.0);
        dumpGray("GNFDilation(g,cone2)", GNFDilation.invoke(g, cone));
        dumpGray("GNFErosion(g,cone2)", GNFErosion.invoke(g, cone));
    }
}

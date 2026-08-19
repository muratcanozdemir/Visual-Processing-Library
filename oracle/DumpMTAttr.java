import vpt.*;
import vpt.algorithms.mm.gray.connected.attribute.*;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Dumps the mm/gray/connected/attribute predicate classes: given a fixed
 * pixel list and image, each Attribute.test(...) is exercised at both a
 * threshold that should pass and one that should fail.
 *
 * Usage: java -cp classes:oracle DumpMTAttr > python/tests/goldens/mtattr.txt
 */
public class DumpMTAttr {

    static void dumpBool(String name, boolean b) {
        System.out.println(name + "\t" + b);
    }

    public static void main(String[] args) {
        ByteImage img = new ByteImage(10, 10, 1);
        img.fill(50);
        for (int x = 2; x < 6; x++)
            for (int y = 2; y < 6; y++)
                img.setXYByte(x, y, 200);

        ArrayList<Point> pixels = new ArrayList<Point>();
        for (int x = 2; x < 6; x++)
            for (int y = 2; y < 6; y++)
                pixels.add(new Point(x, y));

        dumpBool("Area(10)", new Area(Integer.valueOf(10)).test(pixels, img));
        dumpBool("Area(20)", new Area(Integer.valueOf(20)).test(pixels, img));
        dumpBool("Volume(3000)", new Volume(Integer.valueOf(3000)).test(pixels, img));
        dumpBool("Volume(4000)", new Volume(Integer.valueOf(4000)).test(pixels, img));
        dumpBool("Mean(0.5)", new Mean(0.5).test(pixels, img));
        dumpBool("Mean(0.9)", new Mean(0.9).test(pixels, img));
        dumpBool("Sdev(0)", new Sdev(0).test(pixels, img));
        dumpBool("Sdev(1)", new Sdev(1).test(pixels, img));
        dumpBool("Entropy(0.0)", new Entropy(0.0).test(pixels, img));
        dumpBool("Entropy(1.0)", new Entropy(1.0).test(pixels, img));
        dumpBool("Diagonal(1)", new Diagonal(Integer.valueOf(1)).test(pixels, img));
        dumpBool("Diagonal(100)", new Diagonal(Integer.valueOf(100)).test(pixels, img));
        dumpBool("GeometricVolume(1)", new GeometricVolume(Integer.valueOf(1)).test(pixels, img));
        dumpBool("HyperRectangleEdgeDifference(0)", new HyperRectangleEdgeDifference(Integer.valueOf(0)).test(pixels, img));
        dumpBool("HyperRectangleEdgeDifference(1)", new HyperRectangleEdgeDifference(Integer.valueOf(1)).test(pixels, img));

        ByteImage img2 = new ByteImage(10, 10, 2);
        for (int x = 2; x < 6; x++)
            for (int y = 2; y < 6; y++) {
                img2.setXYCByte(x, y, 0, 200);
                img2.setXYCByte(x, y, 1, 100);
            }
        dumpBool("SphereRadius(0)", new SphereRadius(Integer.valueOf(0)).test(pixels, img2));
        dumpBool("SphereRadius(1)", new SphereRadius(Integer.valueOf(1)).test(pixels, img2));
        dumpBool("MultiEntropy(0.0)", new MultiEntropy(0.0).test(pixels, img2));
        dumpBool("MultiSdev(0)", new MultiSdev(0).test(pixels, img2));
        dumpBool("MultiSdev(1)", new MultiSdev(1).test(pixels, img2));
        dumpBool("MultiSdev2(0.0)", new MultiSdev2(0.0).test(pixels, img2));

        for (int order = 1; order <= 8; order++) {
            dumpBool("InvariantMoment(0.0," + order + ")", new InvariantMoment(0.0, order).test(pixels, img));
        }
        dumpBool("MultiInvariantMoment(0.0,1)", new MultiInvariantMoment(0.0, 1).test(pixels, img2));
    }
}

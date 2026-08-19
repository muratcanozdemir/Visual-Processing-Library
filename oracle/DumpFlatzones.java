import vpt.*;
import vpt.algorithms.flatzones.angular.*;
import vpt.algorithms.flatzones.color.*;
import vpt.algorithms.flatzones.gray.*;
import vpt.algorithms.flatzones.multi.*;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Dumps the vpt.algorithms.flatzones package: gray (5/5), angular (5/5),
 * color (4/7 -- ColorQFZRGB/ColorQFZLSH/ColorQFZRGBByte are out of scope,
 * see flatzones/color/__init__.py), multi (1/1).
 *
 * Usage: java -cp classes:oracle DumpFlatzones > python/tests/goldens/flatzones.txt
 */
public class DumpFlatzones {

    static void dumpInt(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(img.getXYCInt(x, y, c));
                }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

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

    static void dumpComponents(String name, ArrayList<ArrayList<Point>> ccs) {
        System.out.println(name + "-count\t0\t0\t" + ccs.size());
        for (int i = 0; i < ccs.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (Point p : ccs.get(i)) {
                if (sb.length() > 0) sb.append(';');
                sb.append(p.x).append(',').append(p.y);
            }
            System.out.println(name + "[" + i + "]\t" + ccs.get(i).size() + "\t0\t" + sb);
        }
    }

    public static void main(String[] args) {
        // Grayscale test image: three plateaus (30/30/180) plus a smooth
        // ramp column, so exact-equality (GrayFZ) and tolerance-based
        // (GrayQFZ/GrayQFZSoille) zone growth diverge meaningfully.
        ByteImage g = new ByteImage(16, 16, 1);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                g.setXYByte(x, y, (x < 6) ? 30 : ((x < 12) ? 30 : 180));
        for (int y = 0; y < 16; y++)
            g.setXYByte(15, y, 30 + y * 10);

        dumpInt("GrayFZ(g)", vpt.algorithms.flatzones.gray.GrayFZ.invoke(g));
        dumpComponents("GrayFZ2(g)", GrayFZ2.invoke(g));
        dumpInt("GrayQFZ(g,10,60)", GrayQFZ.invoke(g, 10, 60));
        dumpGray("GrayQFZLocalRanges(g,10,60)", GrayQFZLocalRanges.invoke(g, 10, 60));
        dumpInt("GrayQFZSoille(g,10,60)", GrayQFZSoille.invoke(g, 10, 60));

        // Angular (hue-like, [0,1)) test image: wraps across the 0/1
        // boundary (right column near 0.98, left column near 0.02) so the
        // circular-distance logic actually gets exercised.
        DoubleImage ang = new DoubleImage(12, 12, 1);
        for (int x = 0; x < 12; x++)
            for (int y = 0; y < 12; y++)
                ang.setXYDouble(x, y, (x < 6) ? 0.02 : 0.98);
        for (int y = 0; y < 12; y++)
            ang.setXYDouble(11, y, (y / 12.0));

        dumpInt("AngularQFZ(ang,0.1,0.3)", AngularQFZ.invoke(ang, 0.1, 0.3));
        dumpInt("AngularQFZ2(ang,0.1,0.3)", AngularQFZ2.invoke(ang, 0.1, 0.3));
        dumpInt("AngularQFZ5(ang,0.1,0.3)", AngularQFZ5.invoke(ang, 0.1, 0.3));
        dumpInt("AngularQFZ6(ang,0.1,0.3)", AngularQFZ6.invoke(ang, 0.1, 0.3));
        dumpInt("LabelBasedAngularQFZ(ang,10,60,0.02)", LabelBasedAngularQFZ.invoke(ang, 10, 60, 0.02));

        // RGB test image: two colour blocks plus a per-pixel ramp column,
        // so exact-equality and tolerance-based marginal growth diverge.
        ByteImage rgb = new ByteImage(10, 10, 3);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++) {
                boolean left = x < 5;
                rgb.setXYCByte(x, y, 0, left ? 40 : 200);
                rgb.setXYCByte(x, y, 1, left ? 60 : 90);
                rgb.setXYCByte(x, y, 2, left ? 80 : 30);
            }
        for (int y = 0; y < 10; y++) {
            rgb.setXYCByte(9, y, 0, 40 + y * 15);
            rgb.setXYCByte(9, y, 1, 60);
            rgb.setXYCByte(9, y, 2, 80);
        }

        dumpInt("ColorFZRGB(rgb)", ColorFZRGB.invoke(rgb));
        dumpInt("ColorQFZRGBMarginal(rgb,10,60)", ColorQFZRGBMarginal.invoke(rgb, 10, 60));

        BooleanImage mask = new BooleanImage(10, 10, 1);
        mask.fill(false);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 5; y++)
                mask.setXYBoolean(x, y, true);
        dumpInt("ColorQFZRGBMarginalFromMarkers(rgb,10,60,mask)",
            ColorQFZRGBMarginalFromMarkers.invoke(rgb, 10, 60, mask));

        dumpInt("ColorQFZSoille(rgb,10,60)", ColorQFZSoille.invoke(rgb, 10, 60));

        // 4-channel test image for the cdim-agnostic QFZMultiMarginal.
        ByteImage multi4 = new ByteImage(8, 8, 4);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                for (int c = 0; c < 4; c++)
                    multi4.setXYCByte(x, y, c, (x < 4) ? 20 + c * 5 : 200 - c * 5);
        dumpInt("QFZMultiMarginal(multi4,10,60)", QFZMultiMarginal.invoke(multi4, 10, 60));
    }
}

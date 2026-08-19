import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.arithmetic.AbsSubtraction;
import vpt.algorithms.mm.gray.*;
import vpt.algorithms.mm.binary.*;

/* Also covers GRankFilter, GInterpolation, GMedianImage, GLambdaLeveling,
 * GOCCO, GLaplacian, GContrastEnhancement, GDMP -- ported but previously
 * not exercised by any Dump driver, contradicting python/README.md's claim
 * of full mm/gray golden verification (see python/IMPROVEMENTS.md 1.1). */

/**
 * Dumps the basic gray/binary morphology operators (dilation, erosion,
 * opening, closing, the three gradients, top-hat, bottom-hat) plus
 * AbsSubtraction, across a box SE (square, hits the separable fast path)
 * and two non-box SEs (disk, cross, hit the general neighbourhood scan).
 *
 * This is the reference for python/tests/test_mm_golden.py. Grayscale
 * results are dumped through getXYByte (the algorithms compute in doubles
 * internally but every value here is byte-exact, so byte comparison is
 * sufficient and keeps the golden readable); binary results through
 * getXYBoolean as 0/1.
 *
 * Usage: java -cp classes:oracle DumpMM > python/tests/goldens/mm.txt
 */
public class DumpMM {

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

    static void dumpBinary(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++)
                    sb.append(img.getXYCBoolean(x, y, c) ? '1' : '0');
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        ByteImage g = new ByteImage(16, 16, 1);
        g.fill(50);
        for (int x = 4; x < 12; x++)
            for (int y = 4; y < 12; y++)
                g.setXYByte(x, y, 180);

        ByteImage rgb = new ByteImage(16, 16, 3);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                rgb.setXYCByte(x, y, 0, x * 15);
                rgb.setXYCByte(x, y, 1, y * 15);
                rgb.setXYCByte(x, y, 2, 120);
            }

        BooleanImage b = new BooleanImage(16, 16, 1);
        b.fill(false);
        for (int x = 4; x < 12; x++)
            for (int y = 4; y < 12; y++)
                b.setXYBoolean(x, y, true);

        FlatSE[] gses = { FlatSE.square(3), FlatSE.square(5), FlatSE.disk(2), FlatSE.cross(3) };
        String[] senames = { "square3", "square5", "disk2", "cross3" };

        for (int i = 0; i < gses.length; i++) {
            FlatSE se = gses[i];
            String s = senames[i];
            dumpGray("GDilation(g," + s + ")", GDilation.invoke(g, se));
            dumpGray("GErosion(g," + s + ")", GErosion.invoke(g, se));
            dumpGray("GOpening(g," + s + ")", GOpening.invoke(g, se));
            dumpGray("GClosing(g," + s + ")", GClosing.invoke(g, se));
            dumpGray("GGradient(g," + s + ")", GGradient.invoke(g, se));
            dumpGray("GInternGradient(g," + s + ")", GInternGradient.invoke(g, se));
            dumpGray("GExternGradient(g," + s + ")", GExternGradient.invoke(g, se));
            dumpGray("GBotHat(g," + s + ")", GBotHat.invoke(g, se));
            dumpGray("GTopHat(g," + s + ")", GTopHat.invoke(g, se));

            dumpBinary("BDilation(b," + s + ")", BDilation.invoke(b, se));
            dumpBinary("BErosion(b," + s + ")", BErosion.invoke(b, se));
            dumpBinary("BOpening(b," + s + ")", BOpening.invoke(b, se));
            dumpBinary("BClosing(b," + s + ")", BClosing.invoke(b, se));
            dumpBinary("BGradient(b," + s + ")", BGradient.invoke(b, se));
            dumpBinary("BInternGradient(b," + s + ")", BInternGradient.invoke(b, se));
            dumpBinary("BExternGradient(b," + s + ")", BExternGradient.invoke(b, se));
            dumpBinary("BBotHat(b," + s + ")", BBotHat.invoke(b, se));
            dumpBinary("BTopHat(b," + s + ")", BTopHat.invoke(b, se));

            // flag=false: out-of-bounds neighbours fail the erosion instead
            // of matching, so the border erodes away.
            dumpBinary("BErosion(b," + s + ",false)", BErosion.invoke(b, se, Boolean.FALSE));
            dumpBinary("BInternGradient(b," + s + ",false)", BInternGradient.invoke(b, se, Boolean.FALSE));
        }

        // multi-channel path (cdim=3)
        FlatSE se3 = FlatSE.square(3);
        dumpGray("GDilation(rgb,square3)", GDilation.invoke(rgb, se3));
        dumpGray("GErosion(rgb,square3)", GErosion.invoke(rgb, se3));
        dumpGray("GGradient(rgb,square3)", GGradient.invoke(rgb, se3));

        // AbsSubtraction directly
        dumpGray("AbsSubtraction(g,rgbChannel0)", AbsSubtraction.invoke(g, rgb.getChannel(0)));

        // -- previously-unexercised mm/gray classes (see python/IMPROVEMENTS.md 1.1) --
        FlatSE square3 = FlatSE.square(3);
        FlatSE disk2 = FlatSE.disk(2);

        dumpGray("GRankFilter(g,square3,4)", GRankFilter.invoke(g, square3, Integer.valueOf(4)));
        dumpGray("GRankFilter(g,disk2,0)", GRankFilter.invoke(g, disk2, Integer.valueOf(0)));

        Image dilatedG = GDilation.invoke(g, square3);
        Image[] interp = GInterpolation.invoke(g, dilatedG, Integer.valueOf(1));
        for (int i = 0; i < interp.length; i++) {
            dumpGray("GInterpolation(g,dilatedG,1)[" + i + "]", interp[i]);
        }

        dumpGray("GMedianImage(g,dilatedG)", GMedianImage.invoke(g, dilatedG));

        Image erodedG = GErosion.invoke(g, square3);
        dumpGray("GLambdaLeveling(g,erodedG,square3,20)", GLambdaLeveling.invoke(g, erodedG, square3, Integer.valueOf(20)));

        dumpGray("GOCCO(g,square3)", GOCCO.invoke(g, square3));
        dumpGray("GLaplacian(g,square3)", GLaplacian.invoke(g, square3));
        dumpGray("GContrastEnhancement(g,square3)", GContrastEnhancement.invoke(g, square3));

        // GDMP degenerates to all-zero on the flat two-plateau "g" image
        // (marker never fully detaches within 2 erosion/dilation steps, so
        // every reconstruction reconverges and every AbsSubtraction is 0)
        // -- same shape as MDMP's degeneracy on the analogous "rgb" block
        // image in DumpMulti.java. Use a concentric-ring profile instead.
        ByteImage profile = new ByteImage(16, 16, 1);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                int ring = Math.min(Math.min(x, y), Math.min(15 - x, 15 - y));
                profile.setXYByte(x, y, 20 + ring * 20);
            }

        Image[] dmp = GDMP.invoke(profile, Integer.valueOf(2));
        for (int i = 0; i < dmp.length; i++) {
            dumpGray("GDMP(profile,2)[" + i + "]", dmp[i]);
        }
    }
}

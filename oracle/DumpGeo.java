import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.arithmetic.*;
import vpt.algorithms.mm.gray.geo.*;

/**
 * Dumps the grayscale geodesic-reconstruction family (mm/gray/geo) plus the
 * new arithmetic ops it depends on (Maximum, AdditionC, SubtractionC,
 * Subtraction) that AbsSubtraction/Minimum/Equal didn't already cover.
 *
 * Unlike mm/binary/geo, none of this hangs: Minimum/Maximum are genuine
 * numeric min/max on ByteImage/DoubleImage (BooleanImage's getDouble
 * inversion, which breaks Minimum into NOR, doesn't apply here).
 *
 * Usage: java -cp classes:oracle DumpGeo > python/tests/goldens/geo.txt
 */
public class DumpGeo {

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

        // a second, less regular image: two separate peaks of different
        // heights, to give GRegionalMaxima/GHMinima something nontrivial
        ByteImage peaks = new ByteImage(16, 16, 1);
        peaks.fill(20);
        for (int x = 2; x < 6; x++) for (int y = 2; y < 6; y++) peaks.setXYByte(x, y, 200);
        for (int x = 10; x < 13; x++) for (int y = 10; y < 13; y++) peaks.setXYByte(x, y, 120);

        FlatSE se = FlatSE.square(3);

        // arithmetic
        dumpGray("Maximum(g,peaks)", Maximum.invoke(g, peaks));
        dumpGray("AdditionC(g,0.2)", AdditionC.invoke(g, 0.2));
        dumpGray("SubtractionC(g,0.2)", SubtractionC.invoke(g, 0.2));
        dumpGray("Subtraction(g,peaks)", Subtraction.invoke(g, peaks));

        // geodesic dilation/erosion and the slow fixpoint reconstruction
        dumpGray("GGeodesicDilation(peaks,g)", GGeodesicDilation.invoke(peaks, se, g));
        dumpGray("GGeodesicErosion(peaks,g)", GGeodesicErosion.invoke(peaks, se, g));
        dumpGray("GReconstructionByDilation(peaks,g)", GReconstructionByDilation.invoke(peaks, se, g));
        dumpGray("GReconstructionByErosion(peaks,g)", GReconstructionByErosion.invoke(peaks, se, g));

        // fast reconstruction family
        dumpGray("GOpeningByReconstruction(g,square3)", GOpeningByReconstruction.invoke(g, se));
        dumpGray("GClosingByReconstruction(g,square3)", GClosingByReconstruction.invoke(g, se));
        dumpGray("GBotHatByReconstruction(g,square3)", GBotHatByReconstruction.invoke(g, se));
        dumpGray("GTopHatByReconstruction(g,square3)", GTopHatByReconstruction.invoke(g, se));
        dumpGray("GRegionalMaxima(peaks)", GRegionalMaxima.invoke(peaks));
        dumpGray("GRegionalMinima(peaks)", GRegionalMinima.invoke(peaks));
        dumpGray("GHMinima(peaks,50)", GHMinima.invoke(peaks, Integer.valueOf(50)));

        dumpGray("FastGrayReconstruction(peaks,g,CONNEXITY4,false)",
            FastGrayReconstruction.invoke(peaks, g, FastGrayReconstruction.CONNEXITY4, false));
        dumpGray("FastGrayReconstruction(peaks,g,CONNEXITY8,true)",
            FastGrayReconstruction.invoke(peaks, g, FastGrayReconstruction.CONNEXITY8, true));

        // GFillHole.execute() mutates its input image in place (see the
        // Java source: `this.inputImage.setByte(i, 0)`), so this must run
        // last -- dumped here, after every other use of `g` above.
        dumpGray("GFillHole(g)", GFillHole.invoke(g));
    }
}

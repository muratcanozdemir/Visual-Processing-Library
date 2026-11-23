import vpt.*;
import vpt.util.se.FlatSE;
import vpt.util.ordering.Lexico;
import vpt.algorithms.mm.multi.*;
import vpt.algorithms.mm.multi.geo.*;
import vpt.algorithms.mm.multi.connected.*;
import vpt.algorithms.mm.multi.connected.attribute.*;

/**
 * Dumps the mm/multi (vectorial morphology) family against a 3-channel
 * image with a bright interior block, ordered by Lexico.
 *
 * Usage: java -cp classes:oracle DumpMulti > python/tests/goldens/multi.txt
 */
public class DumpMulti {

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

    static void dumpDouble(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(Double.toHexString(img.getXYCDouble(x, y, c)));
                }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        ByteImage rgb = new ByteImage(16, 16, 3);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                boolean inside = (x >= 4 && x < 12 && y >= 4 && y < 12);
                rgb.setXYCByte(x, y, 0, inside ? 50 : 10);
                rgb.setXYCByte(x, y, 1, inside ? 150 : 60);
                rgb.setXYCByte(x, y, 2, inside ? 200 : 90);
            }

        // Concentric square rings, each ring a distinct plateau: exercises
        // MDMP across several distinct scales instead of the single-edge
        // "rgb" block image, where a size-3 profile turns out to be
        // trivially all-zero (the marker never fully detaches from either
        // of the block image's two plateaus within 3 erosion/dilation
        // steps, so every reconstructed step reconverges to the same
        // image and every AbsSubtraction is 0).
        ByteImage profile = new ByteImage(16, 16, 3);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                int ring = Math.min(Math.min(x, y), Math.min(15 - x, 15 - y));
                profile.setXYCByte(x, y, 0, 20 + ring * 20);
                profile.setXYCByte(x, y, 1, 30 + ring * 15);
                profile.setXYCByte(x, y, 2, 40 + ring * 10);
            }

        FlatSE se = FlatSE.square(3);
        Lexico lex = new Lexico();

        dumpGray("MDilation(rgb,square3,lex)", MDilation.invoke(rgb, se, lex));
        dumpGray("MErosion(rgb,square3,lex)", MErosion.invoke(rgb, se, lex));
        dumpGray("MOpening(rgb,square3,lex)", MOpening.invoke(rgb, se, lex));
        dumpGray("MClosing(rgb,square3,lex)", MClosing.invoke(rgb, se, lex));
        dumpGray("MBotHat(rgb,square3,lex)", MBotHat.invoke(rgb, se, lex));
        dumpGray("MTopHat(rgb,square3,lex)", MTopHat.invoke(rgb, se, lex));
        dumpGray("MOCCO(rgb,square3,lex)", MOCCO.invoke(rgb, se, lex));

        dumpDouble("MEuclideanGradient(rgb,square3)", MEuclideanGradient.invoke(rgb, se));
        dumpGray("MMaxGradient(rgb,square3)", MMaxGradient.invoke(rgb, se));
        dumpGray("MHSYMaxGradient(rgb,square3)", MHSYMaxGradient.invoke(rgb, se));
        dumpGray("MSaturationWeightedColorGradient(rgb,square3)", MSaturationWeightedColorGradient.invoke(rgb, se));

        dumpGray("MRankFilter(rgb,square3,lex,4)", MRankFilter.invoke(rgb, se, lex, Integer.valueOf(4)));

        Image marker = rgb.newInstance(true);
        dumpGray("MLambdaLeveling(rgb,marker,square3,lex,10)", MLambdaLeveling.invoke(rgb, marker, se, lex, Integer.valueOf(10)));

        // -- mm/multi/geo --------------------------------------------------
        Image eroded = MErosion.invoke(rgb, se, lex);
        Image dilated = MDilation.invoke(rgb, se, lex);

        dumpGray("MGeodesicDilation(eroded,square3,rgb,lex)", MGeodesicDilation.invoke(eroded, se, rgb, lex));
        dumpGray("MGeodesicErosion(dilated,square3,rgb,lex)", MGeodesicErosion.invoke(dilated, se, rgb, lex));

        dumpGray("MReconstructionByDilation(eroded,square3,rgb,lex)", MReconstructionByDilation.invoke(eroded, se, rgb, lex));
        dumpGray("MReconstructionByErosion(dilated,square3,rgb,lex)", MReconstructionByErosion.invoke(dilated, se, rgb, lex));

        dumpGray("MFastReconstruction(eroded,rgb,CONNEXITY8,false,lex)",
            MFastReconstruction.invoke(eroded, rgb, MFastReconstruction.CONNEXITY8, false, lex));
        dumpGray("MFastReconstruction(dilated,rgb,CONNEXITY8,true,lex)",
            MFastReconstruction.invoke(dilated, rgb, MFastReconstruction.CONNEXITY8, true, lex));
        dumpGray("MFastReconstruction(eroded,rgb,CONNEXITY4,false,lex)",
            MFastReconstruction.invoke(eroded, rgb, MFastReconstruction.CONNEXITY4, false, lex));

        dumpGray("MOpeningByReconstruction(rgb,square3,lex)", MOpeningByReconstruction.invoke(rgb, se, lex));
        dumpGray("MClosingByReconstruction(rgb,square3,lex)", MClosingByReconstruction.invoke(rgb, se, lex));
        dumpGray("MOpeningByReconstructionClassic(rgb,square3,lex)", MOpeningByReconstructionClassic.invoke(rgb, se, lex));
        dumpGray("MClosingByReconstructionClassic(rgb,square3,lex)", MClosingByReconstructionClassic.invoke(rgb, se, lex));

        // -- MDMP ------------------------------------------------------------
        Image[] dmp = MDMP.invoke(profile, Integer.valueOf(3), lex);
        for (int i = 0; i < dmp.length; i++) {
            dumpGray("MDMP(profile,3,lex)[" + i + "]", dmp[i]);
        }

        // -- mm/multi/connected -----------------------------------------------
        MultiArea area2 = new MultiArea(Integer.valueOf(2));

        dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiArea(2),lex)", MMaxTreeIncrAttrOpening.invoke(rgb, area2, lex));
        dumpGray("MMaxTreeIncrAttrClosing(rgb,MultiArea(2),lex)", MMaxTreeIncrAttrClosing.invoke(rgb, area2, lex));
        dumpGray("MMaxTreeNonIncrAttrOpening(rgb,MultiArea(2),lex)", MMaxTreeNonIncrAttrOpening.invoke(rgb, area2, lex));
        dumpGray("MMaxTreeNonIncrAttrClosing(rgb,MultiArea(2),lex)", MMaxTreeNonIncrAttrClosing.invoke(rgb, area2, lex));

        // The other three mm.multi.connected.attribute.MultiAttribute
        // predicates (MultiSpectralVolume, MultiDiagonal,
        // MultiGeometricVolume) can't be golden-checked by calling
        // .test(...) directly from here: it takes a MyList<Point>, whose
        // constructor and addFirst/addLast are package-private to
        // vpt.algorithms.mm.multi.connected.maxtree, and MyList only builds
        // one internally during max-tree construction. Routing them through
        // MMaxTreeIncrAttrOpening (which builds that MyList itself) both
        // works and is a stronger check: a bit-exact whole-image match
        // means .test() agreed on every zone, not just one hand-picked one.
        MultiSpectralVolume vol = new MultiSpectralVolume(Integer.valueOf(3000));
        dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiSpectralVolume(3000),lex)", MMaxTreeIncrAttrOpening.invoke(rgb, vol, lex));

        MultiDiagonal diag = new MultiDiagonal(Integer.valueOf(2));
        dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiDiagonal(2),lex)", MMaxTreeIncrAttrOpening.invoke(rgb, diag, lex));

        MultiGeometricVolume geo = new MultiGeometricVolume(Integer.valueOf(1));
        dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiGeometricVolume(1),lex)", MMaxTreeIncrAttrOpening.invoke(rgb, geo, lex));

        // mm.multi.connected.attribute.MultiSdev/MultiSdev2 (the vectorial
        // ones; unrelated to mm.gray.connected.attribute's classes of the
        // same names) -- same MMaxTreeIncrAttrOpening route as above.
        for (int t : new int[] {1, 20}) {
            dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiSdev(" + t + "),lex)",
                MMaxTreeIncrAttrOpening.invoke(rgb, new MultiSdev(Integer.valueOf(t)), lex));
            dumpGray("MMaxTreeIncrAttrOpening(rgb,MultiSdev2(" + t + "),lex)",
                MMaxTreeIncrAttrOpening.invoke(rgb, new MultiSdev2(Integer.valueOf(t)), lex));
        }
    }
}

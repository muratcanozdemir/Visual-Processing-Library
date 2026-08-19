import vpt.*;
import vpt.algorithms.logical.Quantization;
import vpt.algorithms.experimental.AreaLimits;
import vpt.algorithms.experimental.Weird;
import vpt.algorithms.features.QFZBasedStableRegions;
import vpt.algorithms.io.LoadFLO;
import vpt.algorithms.io.LoadRAW;
import vpt.algorithms.plants.Angles;
import vpt.algorithms.plants.DominantPointsCharacterization;
import vpt.algorithms.plants.PetioleRemoval;
import vpt.algorithms.plants.VeinProfile;
import vpt.algorithms.segmentation.berkeley.Seg2Image;
import vpt.util.se.FlatSE;

/**
 * Dumps the small single/few-file packages ported this session:
 * logical.Quantization, features.QFZBasedStableRegions, io.{LoadRAW,
 * LoadFLO}, experimental.{AreaLimits,Weird}, and plants.* (Angles,
 * DominantPointsCharacterization, PetioleRemoval, VeinProfile).
 *
 * noise.{Gaussian,Speckle} are NOT dumped here: both use an unseeded
 * java.util.Random, so even two runs of the Java oracle itself would
 * disagree -- there's no bit-exact target, same treatment as
 * segmentation.MeanShift. Smoke-tested in Python only.
 * classifier.NearestNeighbour is not ported at all (a main()-only
 * hardcoded-dataset-path demo script, no Algorithm subclass / invoke()
 * API -- same category as mpeg7shapeB.FeatureExtractor). io.{Load,Save}
 * are not ported either (need an image-codec dependency this project
 * otherwise has none of -- see io/__init__.py).
 *
 * Also dumps segmentation.berkeley.Seg2Image, the one genuinely portable
 * file in the segmentation.berkeley subpackage (ported to
 * vpt.algorithms.segmentation.Seg2Image on the Python side -- see that
 * file's docstring); its berkeley siblings (a gamut-table ColorQFZRGB/
 * ColorVector pair, four main()-only test harnesses) remain excluded.
 *
 * LoadRAW/LoadFLO/Seg2Image read fixture files checked in at
 * oracle/fixtures/ -- this driver must be run from the repo root
 * (java -cp classes:oracle DumpMisc), same as every other Dump*.java, so
 * the relative paths below resolve.
 *
 * Usage: java -cp classes:oracle DumpMisc > python/tests/goldens/misc.txt
 */
public class DumpMisc {

    static void dumpValue(String name, double v) {
        System.out.println(name + "\t" + Double.toHexString(v));
    }

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
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

    static void dumpBool(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++) {
                if (sb.length() > 0) sb.append(',');
                sb.append(img.getXYBoolean(x, y) ? '1' : '0');
            }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t1\t" + sb);
    }

    public static void main(String[] args) {
        // Quantization: a plain byte gradient.
        ByteImage g = new ByteImage(8, 8, 1);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                g.setXYByte(x, y, (x * 32 + y * 7) % 256);
        for (int bit = 1; bit <= 8; bit++)
            dumpGray("Quantization(g," + bit + ")", Quantization.invoke(g, Integer.valueOf(bit)));

        // QFZBasedStableRegions: two grayscale plateaus, big enough (>= the
        // 250px minArea) for at least one stable region to plausibly form.
        ByteImage stable = new ByteImage(24, 24, 1);
        for (int x = 0; x < 24; x++)
            for (int y = 0; y < 24; y++)
                stable.setXYByte(x, y, (x < 12) ? 40 : 190);
        dumpBool("QFZBasedStableRegions(stable)", QFZBasedStableRegions.invoke(stable));

        // AreaLimits/Weird: reuse the grayscale gradient `g` from Quantization.
        dumpGray("AreaLimits(g,10,3)", AreaLimits.invoke(g, Integer.valueOf(10), Integer.valueOf(3)));
        dumpGray("Weird(g,sq3)", Weird.invoke(g, FlatSE.square(3)));

        // A synthetic "leaf": an elliptical blade with internal diagonal-
        // stripe texture (so VeinProfile's internal gradient finds real
        // vein-like structure, not an empty signal) plus a thin stalk below.
        ByteImage leaf = new ByteImage(50, 50, 1);
        leaf.fill(0);
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                double dx = (x - 25) / 16.0;
                double dy = (y - 18) / 12.0;
                if (dx * dx + dy * dy <= 1.0) {
                    int stripe = (((x + y) / 3) % 2 == 0) ? 220 : 140;
                    leaf.setXYByte(x, y, stripe);
                }
            }
        }
        for (int y = 26; y < 46; y++)
            for (int x = 23; x < 27; x++)
                leaf.setXYByte(x, y, 200);

        dumpVector("Angles(leaf)", Angles.invoke(leaf));
        dumpVector("DominantPointsCharacterization(leaf,6)",
            DominantPointsCharacterization.invoke(leaf, Integer.valueOf(6)));
        dumpBool("PetioleRemoval(leaf)", PetioleRemoval.invoke(leaf));
        dumpVector("VeinProfile(leaf,4,5,4)",
            VeinProfile.invoke(leaf, Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(4)));

        dumpGray("LoadRAW(fixtures/test.raw)", LoadRAW.invoke("oracle/fixtures/test.raw"));
        dumpDouble("LoadFLO(fixtures/test.flo)", LoadFLO.invoke("oracle/fixtures/test.flo"));

        dumpInt("Seg2Image(fixtures/test.seg)", Seg2Image.invoke("oracle/fixtures/test.seg"));
    }
}

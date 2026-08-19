import vpt.*;
import vpt.algorithms.segmentation.*;

/**
 * Dumps vpt.algorithms.segmentation.{HysteresisThreshold,Otsu,
 * SuperposeBorders,BorderToDominantNeighborLabel,NumberOfSingletons,
 * LabelsToMeanValue,LabelsToMeanHue,MergeSingletons,OSR,Watershed,MP,
 * MarkerBasedWatershed}. Threshold and OtsuThreshold are already covered by
 * DumpLinHist.java / linhist.txt. MeanShift is NOT dumped -- it calls
 * Math.random() directly, so even two runs of the Java oracle itself would
 * disagree; there is no bit-exact target. See MeanShift.py's docstring.
 *
 * Usage: java -cp classes:oracle DumpSegmentation > python/tests/goldens/segmentation.txt
 */
public class DumpSegmentation {

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

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    static void dumpDoubleValue(String name, Double v) {
        System.out.println(name + "\t" + Double.toHexString(v.doubleValue()));
    }

    static void dumpIntValue(String name, Integer v) {
        System.out.println(name + "\t" + v);
    }

    public static void main(String[] args) {
        // A gradient with a bright core, a mid-value ring, and a dim
        // background, so the low/high thresholds each have real work to do:
        // the core seeds the flood fill, the ring only survives via
        // hysteresis (below highThreshold, above lowThreshold), and the
        // background is excluded entirely.
        DoubleImage g = new DoubleImage(16, 16, 1);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++) {
                int d = Math.max(Math.abs(x - 8), Math.abs(y - 8));
                double v;
                if (d <= 2) v = 0.9;
                else if (d <= 5) v = 0.5;
                else v = 0.1;
                g.setXYCDouble(x, y, 0, v);
            }

        dumpDouble("HysteresisThreshold(g,0.3,0.8)",
            HysteresisThreshold.invoke(g, Double.valueOf(0.3), Double.valueOf(0.8)));
        // low/high both clamp into [0,1] from outside it.
        dumpDouble("HysteresisThreshold(g,-0.5,1.5)",
            HysteresisThreshold.invoke(g, Double.valueOf(-0.5), Double.valueOf(1.5)));

        // Bimodal byte image for Otsu/Watershed.
        ByteImage bimodal = new ByteImage(12, 12, 1);
        for (int x = 0; x < 12; x++)
            for (int y = 0; y < 12; y++)
                bimodal.setXYCByte(x, y, 0, (x >= 3 && x < 9 && y >= 3 && y < 9) ? 200 : 20);

        dumpIntValue("Otsu(bimodal)", Otsu.invoke(bimodal));

        // Label image with two regions and a watershed line (label 0)
        // running down the middle column, plus a couple of singleton
        // pixels for NumberOfSingletons/MergeSingletons to find.
        IntegerImage labels = new IntegerImage(10, 8, 1);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 8; y++)
                labels.setXYInt(x, y, x < 5 ? 1 : 2);
        for (int y = 0; y < 8; y++)
            labels.setXYInt(5, y, 0);
        labels.setXYInt(1, 1, 9);
        labels.setXYInt(8, 6, 9);

        ByteImage rgb = new ByteImage(10, 8, 3);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 8; y++) {
                rgb.setXYCByte(x, y, 0, x * 20);
                rgb.setXYCByte(x, y, 1, y * 25);
                rgb.setXYCByte(x, y, 2, (x + y) * 10);
            }

        int[] color = { 255, 0, 0 };
        dumpGray("SuperposeBorders(labels,rgb,red)", SuperposeBorders.invoke(labels, rgb, color));
        dumpInt("BorderToDominantNeighborLabel(labels)", BorderToDominantNeighborLabel.invoke(labels));
        dumpIntValue("NumberOfSingletons(labels)", NumberOfSingletons.invoke(labels));
        dumpDouble("LabelsToMeanValue(labels,rgb)", LabelsToMeanValue.invoke(labels, rgb));

        ByteImage hue = new ByteImage(10, 8, 1);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 8; y++)
                hue.setXYCByte(x, y, 0, (x * 8 + y) % 256);
        dumpDouble("LabelsToMeanHue(labels,hue)", LabelsToMeanHue.invoke(labels, hue));

        dumpInt("MergeSingletons(labels,rgb)", MergeSingletons.invoke(labels, rgb));

        // Two label maps sharing structure (map2 is a coarser version of
        // map1: pairs of columns merged) for OSR/MP.
        IntegerImage map1 = new IntegerImage(8, 6, 1);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 6; y++)
                map1.setXYInt(x, y, x / 2);
        IntegerImage map2 = new IntegerImage(8, 6, 1);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 6; y++)
                map2.setXYInt(x, y, x / 4);

        dumpDoubleValue("OSR(map1,map2)", OSR.invoke(map1, map2));
        dumpDoubleValue("MP(map1,map2)", MP.invoke(map1, map2));

        dumpInt("Watershed(bimodal)", Watershed.invoke(bimodal));
        dumpInt("MarkerBasedWatershed(bimodal)", MarkerBasedWatershed.invoke(bimodal));
    }
}

import vpt.*;
import vpt.algorithms.conversion.NonUniformHSY733;
import vpt.algorithms.conversion.NonUniformHSY744;
import vpt.algorithms.conversion.NonUniformHSY833;
import vpt.algorithms.conversion.RGB2HSY;
import vpt.algorithms.histogram.*;

/**
 * Dumps the remaining vpt.algorithms.histogram operators: Equalization,
 * CustomHistogram, Histogram2Image, RGB27Histogram, RGB64Histogram,
 * HSY733Histogram, HSY744Histogram, HSY833Histogram,
 * SaturationWeightedHueHistogram. (Histogram and ContrastStretch are
 * already covered by DumpLinHist.java / linhist.txt.)
 *
 * Usage: java -cp classes:oracle DumpHistogram2 > python/tests/goldens/histogram2.txt
 */
public class DumpHistogram2 {

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
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
        ByteImage rgb = new ByteImage(8, 8, 3);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                rgb.setXYCByte(x, y, 0, x * 30);
                rgb.setXYCByte(x, y, 1, y * 30);
                rgb.setXYCByte(x, y, 2, (x + y) * 15);
            }

        dumpDouble("Equalization(rgb)", Equalization.invoke(rgb));

        dumpVector("CustomHistogram(rgb,false,256)", CustomHistogram.invoke(rgb, Boolean.FALSE, Integer.valueOf(256)));
        dumpVector("CustomHistogram(rgb,true,256)", CustomHistogram.invoke(rgb, Boolean.TRUE, Integer.valueOf(256)));

        double[] histo = Histogram.invoke(rgb, Boolean.FALSE);
        dumpBinary("Histogram2Image(histo)", Histogram2Image.invoke(histo));

        dumpVector("RGB27Histogram(rgb,false)", RGB27Histogram.invoke(rgb, Boolean.FALSE));
        dumpVector("RGB27Histogram(rgb,true)", RGB27Histogram.invoke(rgb, Boolean.TRUE));
        dumpVector("RGB64Histogram(rgb,false)", RGB64Histogram.invoke(rgb, Boolean.FALSE));
        dumpVector("RGB64Histogram(rgb,true)", RGB64Histogram.invoke(rgb, Boolean.TRUE));

        Image hsy733 = NonUniformHSY733.invoke(rgb);
        dumpVector("HSY733Histogram(hsy733,false)", HSY733Histogram.invoke(hsy733, Boolean.FALSE));
        Image hsy744 = NonUniformHSY744.invoke(rgb);
        dumpVector("HSY744Histogram(hsy744,false)", HSY744Histogram.invoke(hsy744, Boolean.FALSE));
        Image hsy833 = NonUniformHSY833.invoke(rgb);
        dumpVector("HSY833Histogram(hsy833,false)", HSY833Histogram.invoke(hsy833, Boolean.FALSE));

        Image hsy = RGB2HSY.invoke(rgb);
        dumpVector("SaturationWeightedHueHistogram(hsy)", SaturationWeightedHueHistogram.invoke(hsy));
    }
}

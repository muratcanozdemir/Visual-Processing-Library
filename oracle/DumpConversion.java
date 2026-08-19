import vpt.*;
import vpt.algorithms.conversion.*;

/**
 * Dumps the vpt.algorithms.conversion package (21/21; RGB2Gray is already
 * covered by DumpLinHist.java / linhist.txt, so it's skipped here).
 *
 * Usage: java -cp classes:oracle DumpConversion > python/tests/goldens/conversion.txt
 */
public class DumpConversion {

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
        // 8x8 RGB gradient, including a pure-black corner (0,0) to exercise
        // RGB2cRGB's r+g+b==0 division hazard.
        ByteImage rgb = new ByteImage(8, 8, 3);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                rgb.setXYCByte(x, y, 0, x * 30);
                rgb.setXYCByte(x, y, 1, y * 30);
                rgb.setXYCByte(x, y, 2, (x + y) * 15);
            }

        dumpDouble("RGB2AVG(rgb)", RGB2AVG.invoke(rgb));
        dumpDouble("RGB2RGB64(rgb)", RGB2RGB64.invoke(rgb));
        dumpDouble("RGB2cRGB(rgb)", RGB2cRGB.invoke(rgb));
        dumpDouble("RGB2XYZ(rgb)", RGB2XYZ.invoke(rgb));
        dumpDouble("RGB2YUV(rgb)", RGB2YUV.invoke(rgb));
        dumpDouble("RGB2HSY(rgb)", RGB2HSY.invoke(rgb));
        dumpDouble("RGB2NUHSY733(rgb)", RGB2NUHSY733.invoke(rgb));
        dumpDouble("NonUniformHSY733(rgb)", NonUniformHSY733.invoke(rgb));
        dumpDouble("NonUniformHSY744(rgb)", NonUniformHSY744.invoke(rgb));
        dumpDouble("NonUniformHSY833(rgb)", NonUniformHSY833.invoke(rgb));

        Image xyz = RGB2XYZ.invoke(rgb);
        dumpDouble("XYZ2RGB(xyz)", XYZ2RGB.invoke(xyz));
        dumpDouble("XYZ2LAB(xyz)", XYZ2LAB.invoke(xyz));

        Image yuv = RGB2YUV.invoke(rgb);
        dumpDouble("YUV2RGB(yuv)", YUV2RGB.invoke(yuv));

        Image hsy = RGB2HSY.invoke(rgb);
        dumpDouble("HSY2RGB(hsy)", HSY2RGB.invoke(hsy));

        Image lab = XYZ2LAB.invoke(xyz);
        dumpDouble("LAB2XYZ(lab)", LAB2XYZ.invoke(lab));
        dumpDouble("XYZ2LAB.scaleToByte(lab)", XYZ2LAB.scaleToByte(lab));

        // Mono-channel "angular" image in [0, 1] for Angular2Color / Hue2Distance.
        DoubleImage angular = new DoubleImage(8, 8, 1);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                angular.setXYCDouble(x, y, 0, (x * 8 + y) / 64.0);

        dumpDouble("Angular2Color(angular)", Angular2Color.invoke(angular));
        dumpDouble("Hue2Distance(angular,0.3)", Hue2Distance.invoke(angular, Double.valueOf(0.3)));

        dumpDouble("DistanceFromHSYColor(hsy,0.8,0.2,0.1)",
            DistanceFromHSYColor.invoke(hsy, Double.valueOf(0.8), Double.valueOf(0.2), Double.valueOf(0.1)));

        dumpDouble("EuclideanDistanceFromRGBColor(rgb,0.5,0.5,0.5,false)",
            EuclideanDistanceFromRGBColor.invoke(rgb, Double.valueOf(0.5), Double.valueOf(0.5), Double.valueOf(0.5), Boolean.FALSE));
        dumpDouble("EuclideanDistanceFromRGBColor(rgb,0.5,0.5,0.5,true)",
            EuclideanDistanceFromRGBColor.invoke(rgb, Double.valueOf(0.5), Double.valueOf(0.5), Double.valueOf(0.5), Boolean.TRUE));

        dumpDouble("EuclideanDistanceFromLABColor(rgb,0.8,0.2,0.1)",
            EuclideanDistanceFromLABColor.invoke(rgb, Double.valueOf(0.8), Double.valueOf(0.2), Double.valueOf(0.1)));
    }
}

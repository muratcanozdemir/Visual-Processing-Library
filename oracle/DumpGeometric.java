import vpt.*;

/**
 * Dumps vpt.algorithms.geometric.{Crop,Flip,ImageCentroid,ToPolar,Rotation,
 * Resample,KeepLargestCC}. ConvexHull is already golden-verified via
 * DumpRecovered.java / recovered.txt.
 *
 * Usage: java -cp classes:oracle DumpGeometric > python/tests/goldens/geometric.txt
 */
public class DumpGeometric {

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

    static void dumpPoint(String name, java.awt.Point p) {
        System.out.println(name + "\t" + p.x + "\t" + p.y);
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
        // Multi-channel test image: an asymmetric ramp so horizontal and
        // vertical flips are each distinguishable from the original and
        // from each other.
        ByteImage rgb = new ByteImage(10, 8, 3);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 8; y++) {
                rgb.setXYCByte(x, y, 0, x * 20);
                rgb.setXYCByte(x, y, 1, y * 25);
                rgb.setXYCByte(x, y, 2, (x + y) * 5);
            }

        dumpGray("Flip(rgb,horizontal)", vpt.algorithms.geometric.Flip.invoke(rgb, Integer.valueOf(0)));
        dumpGray("Flip(rgb,vertical)", vpt.algorithms.geometric.Flip.invoke(rgb, Integer.valueOf(1)));

        dumpGray("Crop(rgb,(2,1),(8,6))",
            vpt.algorithms.geometric.Crop.invoke(rgb, new java.awt.Point(2, 1), new java.awt.Point(8, 6)));
        // p1/p2 reversed -- exercises the min/max normalisation.
        dumpGray("Crop(rgb,(8,6),(2,1))",
            vpt.algorithms.geometric.Crop.invoke(rgb, new java.awt.Point(8, 6), new java.awt.Point(2, 1)));

        dumpDouble("ToPolar(rgb)", vpt.algorithms.geometric.ToPolar.invoke(rgb));

        // Mono-channel, asymmetric weight distribution (heavier bottom-right)
        // for a non-trivial centroid.
        ByteImage g = new ByteImage(10, 8, 1);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 8; y++)
                g.setXYCByte(x, y, 0, x >= 5 && y >= 4 ? 200 : 20);

        dumpPoint("ImageCentroid(g)", vpt.algorithms.geometric.ImageCentroid.invoke(g));

        dumpDouble("Rotation(g,30,NEAREST_NEIGHBOR)",
            vpt.algorithms.geometric.Rotation.invoke(g, Double.valueOf(30.0), Integer.valueOf(0)));
        dumpDouble("Rotation(g,30,BILINEAR)",
            vpt.algorithms.geometric.Rotation.invoke(g, Double.valueOf(30.0), Integer.valueOf(1)));

        dumpDouble("Resample(g,1.5,0.5,NEAREST_NEIGHBOR)",
            vpt.algorithms.geometric.Resample.invoke(g, Double.valueOf(1.5), Double.valueOf(0.5), Integer.valueOf(0)));
        dumpDouble("Resample(g,1.5,1.5,BILINEAR)",
            vpt.algorithms.geometric.Resample.invoke(g, Double.valueOf(1.5), Double.valueOf(1.5), Integer.valueOf(1)));
        dumpDouble("Resample(g,1.5,1.5,BICUBIC)",
            vpt.algorithms.geometric.Resample.invoke(g, Double.valueOf(1.5), Double.valueOf(1.5), Integer.valueOf(2)));

        // Two components, a small one and a larger one, so KeepLargestCC has
        // a real choice to make.
        BooleanImage bi = new BooleanImage(10, 8, 1);
        bi.fill(false);
        for (int x = 0; x < 3; x++)
            bi.setXYBoolean(x, 0, true);
        for (int x = 5; x < 9; x++)
            for (int y = 3; y < 7; y++)
                bi.setXYBoolean(x, y, true);

        dumpBinary("KeepLargestCC(bi)", vpt.algorithms.geometric.KeepLargestCC.invoke(bi));
    }
}

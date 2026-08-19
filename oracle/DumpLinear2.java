import vpt.*;
import vpt.algorithms.linear.Gabor;
import vpt.algorithms.linear.Laplacian;
import vpt.algorithms.linear.MeanFilter;
import vpt.algorithms.linear.Prewitt;
import vpt.algorithms.linear.RotationInvariantGabor;

/**
 * Dumps the remaining vpt.algorithms.linear operators: Laplacian, MeanFilter,
 * Prewitt, Gabor, RotationInvariantGabor. (Convolution and Sobel are already
 * covered by DumpLinHist.java / linhist.txt.)
 *
 * Canny is NOT dumped here: its Java execute() unconditionally calls
 * Display2D.invoke(...), which realises a real Swing/JAI JFrame
 * (vpt.gui.Frame2D) -- that leaves a non-daemon AWT EventQueue thread
 * running, so the JVM never exits and this driver would hang forever.
 * Canny.py is verified structurally in Python only (no golden); see
 * python/README.md.
 *
 * Usage: java -cp classes:oracle DumpLinear2 > python/tests/goldens/linear2.txt
 */
public class DumpLinear2 {

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
        ByteImage g = new ByteImage(12, 12, 1);
        for (int x = 0; x < 12; x++)
            for (int y = 0; y < 12; y++) {
                boolean inside = (x >= 3 && x < 9 && y >= 3 && y < 9);
                g.setXYCByte(x, y, 0, inside ? 200 : 20);
            }

        dumpGray("Laplacian(g,false)", Laplacian.invoke(g, Boolean.FALSE));
        dumpGray("Laplacian(g,true)", Laplacian.invoke(g, Boolean.TRUE));

        dumpGray("MeanFilter(g,3)", MeanFilter.invoke(g, Integer.valueOf(3)));
        dumpGray("MeanFilter(g,5)", MeanFilter.invoke(g, Integer.valueOf(5)));
        // Below the size-3 floor -- exercises the "kernel size too small"
        // clamp-and-warn path; should equal MeanFilter(g,3).
        dumpGray("MeanFilter(g,1)", MeanFilter.invoke(g, Integer.valueOf(1)));

        for (int t = 0; t <= 3; t++) {
            dumpGray("Prewitt(g," + t + ")", Prewitt.invoke(g, Integer.valueOf(t)));
        }

        double[] orientations1 = { 0.0 };
        double[] orientations2 = { 0.0, 0.7853981633974483 };

        dumpGray("Gabor(g,1.0,[0],0,0.5,1,5,5)",
            Gabor.invoke(g, Double.valueOf(1.0), orientations1, Double.valueOf(0.0),
                Double.valueOf(0.5), Double.valueOf(1.0), Integer.valueOf(5), Integer.valueOf(5)));
        dumpGray("Gabor(g,1.0,[0,pi/4],0,0.5,1,5,5)",
            Gabor.invoke(g, Double.valueOf(1.0), orientations2, Double.valueOf(0.0),
                Double.valueOf(0.5), Double.valueOf(1.0), Integer.valueOf(5), Integer.valueOf(5)));

        dumpGray("RotationInvariantGabor(g,1.0,[0,pi/4],0,0.5,1,5,5)",
            RotationInvariantGabor.invoke(g, Double.valueOf(1.0), orientations2, Double.valueOf(0.0),
                Double.valueOf(0.5), Double.valueOf(1.0), Integer.valueOf(5), Integer.valueOf(5)));
    }
}

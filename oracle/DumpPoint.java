import vpt.*;
import vpt.algorithms.point.*;

/**
 * Dumps the vpt.algorithms.point package (Inversion, Log, Power) -- all
 * three operate through Image.getDouble/setDouble, so results are dumped as
 * exact-hex doubles rather than bytes.
 *
 * Log/Power run against a ByteImage (double view normalised to [0,1], the
 * common real-world case) and a DoubleImage carrying negative/zero/large
 * values, to exercise Power's jpow-style edge cases (negative base with a
 * fractional exponent -> NaN, negative-integer-exponent zero -> Infinity,
 * etc.) and Log's domain edge (pixel <= -1 -> non-finite log10).
 *
 * Usage: java -cp classes:oracle DumpPoint > python/tests/goldens/point.txt
 */
public class DumpPoint {

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
        ByteImage bi = new ByteImage(4, 4, 1);
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                bi.setXYCByte(x, y, 0, x * 4 + y);

        dumpDouble("Inversion(bi)", Inversion.invoke(bi));
        dumpDouble("Log(bi)", Log.invoke(bi));
        dumpDouble("Power(bi,2.0)", Power.invoke(bi, Double.valueOf(2.0)));
        dumpDouble("Power(bi,0.5)", Power.invoke(bi, Double.valueOf(0.5)));

        // A DoubleImage with negative, zero, fractional and large values,
        // laid out row-major (16 values, 4x4x1) so Power/Log hit their
        // non-trivial branches directly on the raw (unnormalised) double
        // view: -2, -1, -0.5, 0, 0.5, 1, 2, 3, -4, 10, 100, -0.0, 1e150,
        // -3, 0.25, -0.25.
        double[] vals = {
            -2.0, -1.0, -0.5, 0.0,
            0.5, 1.0, 2.0, 3.0,
            -4.0, 10.0, 100.0, -0.0,
            1e150, -3.0, 0.25, -0.25,
        };
        DoubleImage di = new DoubleImage(4, 4, 1);
        int i = 0;
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                di.setXYCDouble(x, y, 0, vals[i++]);

        dumpDouble("Inversion(di)", Inversion.invoke(di));
        dumpDouble("Log(di)", Log.invoke(di));
        dumpDouble("Power(di,2.0)", Power.invoke(di, Double.valueOf(2.0)));
        dumpDouble("Power(di,0.5)", Power.invoke(di, Double.valueOf(0.5)));
        dumpDouble("Power(di,-1.0)", Power.invoke(di, Double.valueOf(-1.0)));
        dumpDouble("Power(di,3.0)", Power.invoke(di, Double.valueOf(3.0)));
        dumpDouble("Power(di,0.0)", Power.invoke(di, Double.valueOf(0.0)));
    }
}

import vpt.*;
import vpt.algorithms.arithmetic.*;
import vpt.util.ordering.Lexico;

/**
 * Dumps the remaining vpt.algorithms.arithmetic operators: Absolute,
 * Addition, Average, Multiplication, VectorialMaximum, VectorialMinimum.
 * (AbsSubtraction, Minimum, Maximum, Equal, AdditionC, SubtractionC,
 * Subtraction are already covered by DumpMM.java / DumpGeo.java.)
 *
 * Usage: java -cp classes:oracle DumpArith2 > python/tests/goldens/arith2.txt
 */
public class DumpArith2 {

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

    public static void main(String[] args) throws Exception {
        ByteImage a = new ByteImage(2, 2, 1);
        for (int i = 0; i < 4; i++) a.setByte(i, i * 80);
        ByteImage b = new ByteImage(2, 2, 1);
        for (int i = 0; i < 4; i++) b.setByte(i, (3 - i) * 80);

        dumpGray("Addition(a,b)", Addition.invoke(a, b));
        dumpGray("Average(a,b)", Average.invoke(a, b));
        dumpGray("Multiplication(a,b)", Multiplication.invoke(a, b));

        DoubleImage d = new DoubleImage(2, 2, 1);
        d.setXYDouble(0, 0, -0.5);
        d.setXYDouble(1, 0, 0.75);
        d.setXYDouble(0, 1, -1.0);
        d.setXYDouble(1, 1, 0.0);
        dumpDouble("Absolute(d)", Absolute.invoke(d));

        ByteImage rgb1 = new ByteImage(1, 1, 2);
        rgb1.setXYCByte(0, 0, 0, 100);
        rgb1.setXYCByte(0, 0, 1, 50);
        ByteImage rgb2 = new ByteImage(1, 1, 2);
        rgb2.setXYCByte(0, 0, 0, 80);
        rgb2.setXYCByte(0, 0, 1, 200);
        Lexico lex = new Lexico();
        dumpGray("VectorialMaximum(rgb1,rgb2,lex)", VectorialMaximum.invoke(rgb1, rgb2, lex));
        dumpGray("VectorialMinimum(rgb1,rgb2,lex)", VectorialMinimum.invoke(rgb1, rgb2, lex));
    }
}

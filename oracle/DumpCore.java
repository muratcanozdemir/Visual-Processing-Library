import vpt.*;

/**
 * Dumps the exact behaviour of the Image hierarchy to stdout in a flat,
 * diffable text format, for the Python port to be verified against.
 *
 * The image layer is where a naive port goes wrong silently: ByteImage's
 * signed -128 storage bias, Math.round's round-half-up, 32-bit shift
 * overflow in getInt, and the deliberately inverted BooleanImage getters all
 * produce plausible-looking but different numbers under Python's defaults.
 * So every accessor is swept across its full input range rather than spot
 * checked.
 *
 * Doubles are emitted with Double.toHexString for bit-exact round-tripping;
 * %g or toString would lose the low bits that distinguish a correct port from
 * a nearly correct one.
 *
 * Usage: java -cp classes:oracle DumpCore > tests/goldens/core.txt
 */
public class DumpCore {

    static void row(Object... cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) sb.append('\t');
            Object c = cells[i];
            sb.append(c instanceof Double ? Double.toHexString((Double) c) : String.valueOf(c));
        }
        System.out.println(sb);
    }

    /** Every accessor of a one-pixel image, after writing `v` through `setter`. */
    static void sweepSetGet(String type, Image img, String setter, String v) {
        row(type, setter, v,
            img.getBoolean(0), img.getByte(0), img.getInt(0), img.getDouble(0));
    }

    static Image fresh(String type) {
        if (type.equals("ByteImage"))    return new ByteImage(1, 1, 1);
        if (type.equals("BooleanImage")) return new BooleanImage(1, 1, 1);
        if (type.equals("IntegerImage")) return new IntegerImage(1, 1, 1);
        return new DoubleImage(1, 1, 1);
    }

    public static void main(String[] args) {
        String[] types = {"ByteImage", "BooleanImage", "IntegerImage", "DoubleImage"};

        // --- setByte across the full byte range, plus out-of-range values
        // that exercise the (byte) wrap ---
        int[] byteVals = {0, 1, 2, 63, 64, 127, 128, 129, 200, 254, 255,
                          256, 257, -1, -2, -128, -129, 300, 1000, -1000};
        for (String t : types)
            for (int v : byteVals) {
                Image img = fresh(t);
                img.setByte(0, v);
                sweepSetGet(t, img, "setByte", String.valueOf(v));
            }

        // --- setInt across the full int range including both extremes ---
        int[] intVals = {0, 1, -1, 255, 256, 65536, 1 << 24, (1 << 24) - 1,
                         -(1 << 24), Integer.MAX_VALUE, Integer.MIN_VALUE,
                         Integer.MAX_VALUE - 1, Integer.MIN_VALUE + 1,
                         123456789, -123456789};
        for (String t : types)
            for (int v : intVals) {
                Image img = fresh(t);
                img.setInt(0, v);
                sweepSetGet(t, img, "setInt", String.valueOf(v));
            }

        // --- setDouble, concentrating on rounding boundaries: k/255 and the
        // half-way points where round-half-up differs from round-half-even ---
        double[] dblVals = {0.0, 1.0, 0.5, 0.25, 0.75, -0.0, -1.0, 2.0, -2.0,
                            0.1, 0.2, 0.3, 1.0 / 3.0, 2.0 / 3.0,
                            1.0 / 255.0, 2.0 / 255.0, 127.0 / 255.0, 128.0 / 255.0,
                            254.0 / 255.0, 0.5 / 255.0, 1.5 / 255.0, 2.5 / 255.0,
                            0.49999999999999994, 1e-300, 1e300, -1e300,
                            0.0019607843137254902, 0.5019607843137255};
        for (String t : types)
            for (double v : dblVals) {
                Image img = fresh(t);
                img.setDouble(0, v);
                sweepSetGet(t, img, "setDouble", Double.toHexString(v));
            }

        // --- setBoolean ---
        for (String t : types)
            for (boolean v : new boolean[]{true, false}) {
                Image img = fresh(t);
                img.setBoolean(0, v);
                sweepSetGet(t, img, "setBoolean", String.valueOf(v));
            }

        // --- fill(): the three overloads Java picks by static type. This is
        // the trap the Python port has to reproduce by name, since fill(1)
        // and fill(1.0) are different operations. ---
        for (String t : types) {
            for (int v : new int[]{0, 1, 127, 128, 255, 256, -1}) {
                Image img = fresh(t);
                img.fill(v);
                sweepSetGet(t, img, "fillInt", String.valueOf(v));
            }
            for (double v : new double[]{0.0, 0.5, 1.0, 2.0, -1.0, 0.00392156862745098}) {
                Image img = fresh(t);
                img.fill(v);
                sweepSetGet(t, img, "fillDouble", Double.toHexString(v));
            }
            for (boolean v : new boolean[]{true, false}) {
                Image img = fresh(t);
                img.fill(v);
                sweepSetGet(t, img, "fillBoolean", String.valueOf(v));
            }
        }

        // --- indexing: c + cdim*(x + xdim*y) must agree exactly ---
        for (String t : types) {
            Image img = fresh(t).newInstance(3, 4, 2);
            int n = 0;
            for (int y = 0; y < 4; y++)
                for (int x = 0; x < 3; x++)
                    for (int c = 0; c < 2; c++)
                        img.setXYCByte(x, y, c, (n++ * 37) % 256);
            for (int y = 0; y < 4; y++)
                for (int x = 0; x < 3; x++)
                    for (int c = 0; c < 2; c++)
                        row(t, "index", x + "," + y + "," + c,
                            img.getXYCBoolean(x, y, c), img.getXYCByte(x, y, c),
                            img.getXYCInt(x, y, c), img.getXYCDouble(x, y, c));
        }

        // --- Image base-class helpers, including the preserved E+30 sentinel
        // defect in getXYCMaximum ---
        for (String t : types) {
            Image img = fresh(t).newInstance(5, 5, 1);
            for (int y = 0; y < 5; y++)
                for (int x = 0; x < 5; x++)
                    img.setXYByte(x, y, (x * 5 + y) * 7 % 256);
            row(t, "helpers", "-", img.getSize(), img.getDiagonal(),
                img.isEmpty(0), img.isFull(0));
            row(t, "maximum", "-", img.getXYCMaximum(0).x, img.getXYCMaximum(0).y,
                img.getXYCMinimum(0).x, img.getXYCMinimum(0).y);
        }

        // --- getXYCMaximum on an all-negative-double image: the E+30 sentinel
        // is small enough that a correct -Double.MAX_VALUE would give a
        // different answer, so this pins the defect ---
        {
            DoubleImage d = new DoubleImage(3, 1, 1);
            d.setXYDouble(0, 0, -1e250);
            d.setXYDouble(1, 0, -1e200);
            d.setXYDouble(2, 0, -1e100);
            row("DoubleImage", "maxSentinel", "-",
                d.getXYCMaximum(0).x, d.getXYCMaximum(0).y, 0, 0.0);
            DoubleImage d2 = new DoubleImage(3, 1, 1);
            d2.setXYDouble(0, 0, -1e35);   // below the E+30 sentinel
            d2.setXYDouble(1, 0, -1e35);
            d2.setXYDouble(2, 0, -1e35);
            row("DoubleImage", "maxSentinelBelow", "-",
                d2.getXYCMaximum(0).x, d2.getXYCMaximum(0).y, 0, 0.0);
        }

        // --- pgetXYCDouble bilinear interpolation ---
        {
            DoubleImage d = new DoubleImage(4, 4, 1);
            for (int y = 0; y < 4; y++)
                for (int x = 0; x < 4; x++)
                    d.setXYDouble(x, y, (x * 4 + y) / 16.0);
            double[] coords = {0.0, 0.5, 1.25, 2.75, 3.0, 2.999999};
            for (double px : coords)
                for (double py : coords)
                    row("DoubleImage", "pget",
                        Double.toHexString(px) + "," + Double.toHexString(py),
                        false, 0, 0, d.pgetXYCDouble(px, py, 0));
        }

        // --- deep copy across every type pairing ---
        for (String src : types) {
            Image a = fresh(src).newInstance(2, 2, 1);
            for (int i = 0; i < 4; i++) a.setByte(i, i * 60);
            for (String dst : types) {
                Image b;
                if (dst.equals("ByteImage"))         b = new ByteImage(a, true);
                else if (dst.equals("BooleanImage")) b = new BooleanImage(a, true);
                else if (dst.equals("IntegerImage")) b = new IntegerImage(a, true);
                else                                 b = new DoubleImage(a, true);
                for (int i = 0; i < 4; i++)
                    row(src + "->" + dst, "copy", String.valueOf(i),
                        b.getBoolean(i), b.getByte(i), b.getInt(i), b.getDouble(i));
            }
        }

        // --- getChannel / setChannel ---
        for (String t : types) {
            Image img = fresh(t).newInstance(2, 2, 3);
            int n = 0;
            for (int y = 0; y < 2; y++)
                for (int x = 0; x < 2; x++)
                    for (int c = 0; c < 3; c++)
                        img.setXYCByte(x, y, c, (n++ * 23) % 256);
            for (int c = 0; c < 3; c++) {
                Image ch = img.getChannel(c);
                for (int y = 0; y < 2; y++)
                    for (int x = 0; x < 2; x++)
                        row(t, "getChannel", c + ":" + x + "," + y,
                            ch.getXYBoolean(x, y), ch.getXYByte(x, y),
                            ch.getXYInt(x, y), ch.getXYDouble(x, y));
            }
        }
    }
}

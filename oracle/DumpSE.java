import vpt.util.se.FlatSE;
import java.awt.Point;

/**
 * Dumps every FlatSE factory across a range of sizes.
 *
 * The circle-based elements walk an 8*radius perimeter and round
 * cos/sin coordinates to pixels. Measured over every radius dumped here,
 * none of those 640 coordinates lands exactly on a .5 boundary, so the
 * round-half-up/round-half-even distinction happens to be unobservable in
 * this file -- unlike in DumpCore, where it is not. What this dump does pin
 * is the resulting pixel masks, the BOX flag that selects the separable
 * fast path in dilation/erosion, and the getCoords() ordering that the
 * morphology operators iterate: an off-by-one in an arc bound, a wrong
 * centre, or a reordered coords list all show up immediately.
 *
 * It also pins Math.cos/Math.sin against NumPy's, which are separate
 * implementations and need not agree in the last ULP.
 *
 * Usage: java -cp classes:oracle DumpSE > python/tests/goldens/se.txt
 */
public class DumpSE {

    /** Pixel mask as a run of 0/1, plus centre, BOX flag and coord order. */
    static void dump(String name, FlatSE se) {
        StringBuilder mask = new StringBuilder();
        for (int y = 0; y < se.getYDim(); y++)
            for (int x = 0; x < se.getXDim(); x++)
                mask.append(se.getXYBoolean(x, y) ? '1' : '0');

        StringBuilder coords = new StringBuilder();
        for (Point p : se.getCoords()) {
            if (coords.length() > 0) coords.append(';');
            coords.append(p.x).append(',').append(p.y);
        }

        // `center` is private in FlatSE, so it is not dumped directly. The
        // mask gives absolute pixel positions and getCoords() gives the same
        // pixels as centre-relative offsets, so the pair pins the centre
        // exactly as tightly as printing it would.
        System.out.println(name + "\t" + se.getXDim() + "\t" + se.getYDim()
            + "\t-\t" + se.BOX + "\t" + mask + "\t" + coords);
    }

    static void dumpAll(String name, FlatSE[] ses) {
        System.out.println(name + "-count\t0\t0\t-\tfalse\t" + ses.length + "\t");
        for (int i = 0; i < ses.length; i++) dump(name + "[" + i + "]", ses[i]);
    }

    public static void main(String[] args) {
        for (int size : new int[]{1, 2, 3, 4, 5, 7, 9, 16}) {
            dump("square(" + size + ")", FlatSE.square(size));
            dump("hollowSquare(" + size + ")", FlatSE.hollowSquare(size));
            dump("cross(" + size + ")", FlatSE.cross(size));
            dump("hLine(" + size + ")", FlatSE.hLine(size));
            dump("vLine(" + size + ")", FlatSE.vLine(size));
        }

        for (int size : new int[]{5, 7}) {
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    dump("square(" + size + "," + x + "," + y + ")", FlatSE.square(size, x, y));
            dump("hLine(" + size + ",1)", FlatSE.hLine(size, 1));
            dump("vLine(" + size + ",1)", FlatSE.vLine(size, 1));
        }

        for (int r : new int[]{0, 1, 2, 3, 4, 5, 6, 8, 11}) {
            dump("disk(" + r + ")", FlatSE.disk(r));
            dump("circle(" + r + ")", FlatSE.circle(r));
            dump("centerlessCircle(" + r + ")", FlatSE.centerlessCircle(r));
            dump("partiallyFilledDisk(" + r + "," + (r / 2) + ")",
                 FlatSE.partiallyFilledDisk(r, r / 2));
            dumpAll("partialArcs(" + r + ")", FlatSE.partialArcs(r));
            dumpAll("partialArcs2(" + r + ")", FlatSE.partialArcs2(r));
            dumpAll("partialCircles(" + r + ")", FlatSE.partialCircles(r));
            dumpAll("partialCircles2(" + r + ")", FlatSE.partialCircles2(r));
            dumpAll("pointCouples(" + r + ")", FlatSE.pointCouples(r));
            dumpAll("pointCouples2(" + r + ")", FlatSE.pointCouples2(r));
        }

        // reflection / getHLine / getVLine on both BOX and non-BOX elements
        for (int r : new int[]{1, 3, 5}) {
            dump("disk(" + r + ").reflection", FlatSE.disk(r).reflection());
            dump("square(" + (2 * r + 1) + ").reflection", FlatSE.square(2 * r + 1).reflection());
            dump("square(" + (2 * r + 1) + ").getHLine", FlatSE.square(2 * r + 1).getHLine());
            dump("square(" + (2 * r + 1) + ").getVLine", FlatSE.square(2 * r + 1).getVLine());
            dump("disk(" + r + ").getHLine", FlatSE.disk(r).getHLine());
            dump("disk(" + r + ").getVLine", FlatSE.disk(r).getVLine());
        }

        // centerlessCircle also builds listCoords; dump its order
        for (int r : new int[]{1, 3, 5}) {
            FlatSE se = FlatSE.centerlessCircle(r);
            StringBuilder sb = new StringBuilder();
            for (Point p : se.getListCoords()) {
                if (sb.length() > 0) sb.append(';');
                sb.append(p.x).append(',').append(p.y);
            }
            System.out.println("centerlessCircle(" + r + ").listCoords\t0\t0\t-\tfalse\t"
                + se.getListCoords().size() + "\t" + sb);
        }
    }
}

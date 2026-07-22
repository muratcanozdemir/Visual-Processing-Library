import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.shape.ConnectedComponents;
import vpt.algorithms.segmentation.Watershed;
import vpt.algorithms.flatzones.gray.GrayQFZ;
import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
import vpt.algorithms.geometric.ConvexHull;
import java.util.ArrayList;
import java.awt.Point;

public class Recovered {
  public static void main(String[] a) throws Exception {
    // --- ConnectedComponents: two disjoint blobs ---
    BooleanImage bi = new BooleanImage(12, 12, 1);
    bi.fill(false);
    for (int x=1;x<=3;x++) for (int y=1;y<=3;y++) bi.setXYBoolean(x,y,true);
    for (int x=7;x<=9;x++) for (int y=7;y<=9;y++) bi.setXYBoolean(x,y,true);
    ArrayList<ArrayList<Point>> cc = ConnectedComponents.invoke(bi);
    System.out.println("ConnectedComponents: " + cc.size() + " components, sizes="
        + cc.get(0).size() + "," + cc.get(1).size());

    // --- GrayQFZ: flat zones on a 2-level image ---
    ByteImage gi = new ByteImage(10, 10, 1);
    gi.fill(10);
    for (int x=5;x<10;x++) for (int y=0;y<10;y++) gi.setXYByte(x,y,200);
    Image fz = GrayQFZ.invoke(gi, 0, 255);
    java.util.HashSet<Integer> labels = new java.util.HashSet<Integer>();
    for (int x=0;x<10;x++) for (int y=0;y<10;y++) labels.add(fz.getXYInt(x,y));
    System.out.println("GrayQFZ: distinct flat zones = " + labels.size());

    // --- MaxTree: build on the same image ---
    vpt.algorithms.mm.gray.connected.maxtree.Node root = MaxTree.invoke(gi);
    System.out.println("MaxTree: root level=" + root.level + " children=" + (root.children==null?0:root.children.size()) + " pixels=" + (root.pixels==null?0:root.pixels.size()));

    // --- ConvexHull (our QuickHull replacement) ---
    BooleanImage tri = new BooleanImage(20, 20, 1);
    tri.fill(false);
    tri.setXYBoolean(2,2,true); tri.setXYBoolean(15,3,true);
    tri.setXYBoolean(8,16,true); tri.setXYBoolean(8,8,true); // interior point
    Image hull = ConvexHull.invoke(tri, Boolean.FALSE);
    int on=0; for (int x=0;x<20;x++) for (int y=0;y<20;y++) if (hull.getXYBoolean(x,y)) on++;
    System.out.println("ConvexHull: hull outline pixels = " + on);
  }
}

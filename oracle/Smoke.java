import vpt.*;
import vpt.util.se.FlatSE;
import vpt.algorithms.mm.gray.GDilation;
import vpt.algorithms.mm.gray.GErosion;

public class Smoke {
  public static void main(String[] a) throws Exception {
    ByteImage img = new ByteImage(8, 8, 1);
    img.fill(0);
    img.setXYByte(4, 4, 200);          // single bright pixel
    FlatSE se = FlatSE.square(3);

    Image d = (Image) GDilation.invoke(img, se);
    Image e = (Image) GErosion.invoke(img, se);

    System.out.println("input  center = " + img.getXYByte(4,4));
    StringBuilder sb = new StringBuilder();
    for (int y=3; y<=5; y++) { for (int x=3; x<=5; x++) sb.append(d.getXYByte(x,y)).append(" "); sb.append("| "); }
    System.out.println("dilation 3x3 nbhd: " + sb);
    System.out.println("erosion center = " + e.getXYByte(4,4));
  }
}

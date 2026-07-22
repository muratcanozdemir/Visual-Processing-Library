import vpt.*;
import vpt.util.se.FlatSE;
import vpt.util.ordering.*;
import vpt.algorithms.mm.gray.*;
import vpt.algorithms.mm.binary.*;
import vpt.algorithms.segmentation.OtsuThreshold;
import vpt.algorithms.frequential.FFT;
import vpt.algorithms.statistical.PCA;
import vpt.algorithms.linear.Sobel;
import vpt.algorithms.conversion.RGB2Gray;
import vpt.algorithms.histogram.Histogram;

public class Sweep {
  static int pass=0, fail=0;
  static void t(String name, Runnable r) {
    try { r.run(); System.out.println("  PASS  " + name); pass++; }
    catch (Throwable e) { System.out.println("  FAIL  " + name + " :: " + e); fail++; }
  }
  public static void main(String[] a) {
    final ByteImage g = new ByteImage(16,16,1);
    g.fill(50);
    for (int x=4;x<12;x++) for (int y=4;y<12;y++) g.setXYByte(x,y,180);
    final ByteImage rgb = new ByteImage(16,16,3);
    for (int x=0;x<16;x++) for (int y=0;y<16;y++) {
      rgb.setXYCByte(x,y,0,x*15); rgb.setXYCByte(x,y,1,y*15); rgb.setXYCByte(x,y,2,120);
    }
    final BooleanImage b = new BooleanImage(16,16,1);
    b.fill(false);
    for (int x=4;x<12;x++) for (int y=4;y<12;y++) b.setXYBoolean(x,y,true);

    t("GOpening",   () -> { try { GOpening.invoke(g, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("GClosing",   () -> { try { GClosing.invoke(g, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("GGradient",  () -> { try { GGradient.invoke(g, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("BDilation",  () -> { try { BDilation.invoke(b, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("BErosion",   () -> { try { BErosion.invoke(b, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("BOpening",   () -> { try { BOpening.invoke(b, FlatSE.square(3)); } catch(Exception e){throw new RuntimeException(e);} });
    t("OtsuThreshold", () -> { try { OtsuThreshold.invoke(g); } catch(Exception e){throw new RuntimeException(e);} });
    t("Sobel",      () -> { try { Sobel.invoke(g, Integer.valueOf(0), Boolean.FALSE); } catch(Exception e){throw new RuntimeException(e);} });
    t("RGB2Gray",   () -> { try { RGB2Gray.invoke(rgb); } catch(Exception e){throw new RuntimeException(e);} });
    t("Histogram",  () -> { try { Histogram.invoke(g, Boolean.FALSE); } catch(Exception e){throw new RuntimeException(e);} });
    t("Lexico",     () -> { Lexico l=new Lexico();
                            double[] p={1,2,3}, q={1,2,4};
                            if (l.compare(p,q) >= 0) throw new RuntimeException("bad order"); });
    t("EuclideanNorm", () -> { EuclideanNorm e=new EuclideanNorm();
                            double[][] v={{1,1,1},{5,5,5}};
                            double[] mx=e.max(v);
                            if (mx[0]!=5) throw new RuntimeException("bad max"); });
    System.out.println("\n" + pass + " passed, " + fail + " failed");
  }
}

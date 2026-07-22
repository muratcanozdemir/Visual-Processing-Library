/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.DoubleImage;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.algorithms.histogram.ContrastStretch;
/*  9:   */ import vpt.algorithms.io.Load;
/* 10:   */ import vpt.algorithms.statistical.PCA;
/* 11:   */ import vpt.algorithms.statistical.ReversePCA;
/* 12:   */ 
/* 13:   */ public class Test5
/* 14:   */ {
/* 15:   */   public static void main(String[] args)
/* 16:   */   {
/* 17:17 */     Image c0 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0008.png");
/* 18:18 */     Image c1 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0108.png");
/* 19:19 */     Image c2 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0208.png");
/* 20:20 */     Image c3 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0308.png");
/* 21:21 */     Image c4 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0408.png");
/* 22:22 */     Image c5 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0508.png");
/* 23:23 */     Image c6 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0608.png");
/* 24:24 */     Image c7 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0708.png");
/* 25:25 */     Image c8 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0808.png");
/* 26:26 */     Image c9 = Load.invoke("/media/data/arge/veri_yedegi/mitosis_contest/scanner_M/M00/M00_01/M00_01/M00_01a_0908.png");
/* 27:   */     
/* 28:28 */     Image img = new DoubleImage(c0.getXDim(), c0.getYDim(), 10);
/* 29:29 */     img.setChannel(c0, 0);
/* 30:30 */     img.setChannel(c1, 1);
/* 31:31 */     img.setChannel(c2, 2);
/* 32:32 */     img.setChannel(c3, 3);
/* 33:33 */     img.setChannel(c4, 4);
/* 34:34 */     img.setChannel(c5, 5);
/* 35:35 */     img.setChannel(c6, 6);
/* 36:36 */     img.setChannel(c7, 7);
/* 37:37 */     img.setChannel(c8, 8);
/* 38:38 */     img.setChannel(c9, 9);
/* 39:   */     
/* 40:40 */     Display2D.invoke(img, Boolean.valueOf(false));
/* 41:   */     
/* 42:42 */     ArrayList<Object> output = PCA.invokeAll(img);
/* 43:   */     
/* 44:44 */     Image pca = (Image)output.get(0);
/* 45:45 */     double[][] eigenVect = (double[][])output.get(1);
/* 46:46 */     double[] eigenVal = (double[])output.get(2);
/* 47:47 */     double[] means = (double[])output.get(3);
/* 48:   */     
/* 49:49 */     double[] variances = PCA.getVariances(eigenVal);
/* 50:51 */     for (double d : variances) {
/* 51:52 */       System.err.println(d);
/* 52:   */     }
/* 53:54 */     pca = ContrastStretch.invoke(pca);
/* 54:   */     
/* 55:56 */     Display2D.invoke(pca, Boolean.valueOf(false));
/* 56:   */     
/* 57:58 */     Image revPCA = ReversePCA.invoke(pca, eigenVect, means);
/* 58:   */     
/* 59:60 */     revPCA = ContrastStretch.invoke(revPCA);
/* 60:   */     
/* 61:62 */     Display2D.invoke(revPCA, Boolean.valueOf(false));
/* 62:   */   }
/* 63:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.Test5
 * JD-Core Version:    0.7.0.1
 */
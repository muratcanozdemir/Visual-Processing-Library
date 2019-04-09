/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.DoubleImage;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.display.Display2D;
/*  6:   */ import vpt.algorithms.frequential.FFT;
/*  7:   */ import vpt.algorithms.histogram.ContrastStretch;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.io.Save;
/* 10:   */ import vpt.algorithms.point.Power;
/* 11:   */ 
/* 12:   */ public class WatershedTest
/* 13:   */ {
/* 14:   */   public static void main(String[] args)
/* 15:   */   {
/* 16:44 */     Image img2 = Load.invoke("samples/lennaGray.png");
/* 17:45 */     DoubleImage[] fft = FFT.invoke(img2, Boolean.valueOf(true), Integer.valueOf(2));
/* 18:   */     
/* 19:47 */     Image img3 = ContrastStretch.invoke(fft[0]);
/* 20:48 */     img3 = Power.invoke(img3, Double.valueOf(0.2D));
/* 21:49 */     Save.invoke(img3, "lennaFFT.png");
/* 22:   */     
/* 23:51 */     Display2D.invoke(img3);
/* 24:   */   }
/* 25:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.WatershedTest
 * JD-Core Version:    0.7.0.1
 */
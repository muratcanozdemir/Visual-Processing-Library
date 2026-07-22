/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.display.Display2D;
/*  5:   */ import vpt.algorithms.flatzones.color.ColorQFZRGB;
/*  6:   */ import vpt.algorithms.io.Load;
/*  7:   */ import vpt.algorithms.segmentation.LabelsToMeanValue;
/*  8:   */ import vpt.util.ordering.EuclideanNorm;
/*  9:   */ 
/* 10:   */ public class CQFZTest
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:30 */     Image img = Load.invoke("samples/157032.jpg");
/* 15:   */     
/* 16:   */ 
/* 17:   */ 
/* 18:34 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 19:   */     
/* 20:36 */     int[] alpha = { 25, 25, 100 };
/* 21:37 */     int[] omega = { 25, 25, 100 };
/* 22:   */     
/* 23:   */ 
/* 24:   */ 
/* 25:   */ 
/* 26:   */ 
/* 27:   */ 
/* 28:44 */     Image labels = ColorQFZRGB.invoke(img, alpha, omega, new EuclideanNorm());
/* 29:   */     
/* 30:46 */     Image mean = LabelsToMeanValue.invoke(labels, img);
/* 31:47 */     Display2D.invoke(mean, Boolean.valueOf(true));
/* 32:   */   }
/* 33:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.CQFZTest
 * JD-Core Version:    0.7.0.1
 */
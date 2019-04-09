/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.display.Display2D;
/*  5:   */ import vpt.algorithms.io.Load;
/*  6:   */ import vpt.algorithms.point.Inversion;
/*  7:   */ import vpt.algorithms.segmentation.OtsuThreshold;
/*  8:   */ 
/*  9:   */ public class PlantSegmentationTest
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:27 */     Image originalColor = Load.invoke("samples/plants/3681.jpg");
/* 14:   */     
/* 15:29 */     Image original = originalColor.getChannel(1);
/* 16:30 */     Display2D.invoke(originalColor, Boolean.valueOf(true));
/* 17:   */     
/* 18:32 */     Image img = Inversion.invoke(original);
/* 19:33 */     img = OtsuThreshold.invoke(img);
/* 20:34 */     Display2D.invoke(img);
/* 21:   */   }
/* 22:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.PlantSegmentationTest
 * JD-Core Version:    0.7.0.1
 */
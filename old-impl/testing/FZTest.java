/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.conversion.RGB2Gray;
/*  5:   */ import vpt.algorithms.display.Display2D;
/*  6:   */ import vpt.algorithms.flatzones.gray.GrayQFZ;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.algorithms.segmentation.LabelsToMeanValue;
/*  9:   */ 
/* 10:   */ public class FZTest
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:32 */     Image img = Load.invoke("samples/lenna256.png");
/* 15:33 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 16:   */     
/* 17:35 */     img = RGB2Gray.invoke(img);
/* 18:   */     
/* 19:37 */     Image labels = GrayQFZ.invoke(img, 5, 255);
/* 20:38 */     labels = LabelsToMeanValue.invoke(labels, img);
/* 21:39 */     Display2D.invoke(labels);
/* 22:   */   }
/* 23:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.FZTest
 * JD-Core Version:    0.7.0.1
 */
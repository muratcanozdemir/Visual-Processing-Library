/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.conversion.Angular2Color;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.algorithms.io.Save;
/*  9:   */ import vpt.util.Distance;
/* 10:   */ 
/* 11:   */ public class ProduceOrientationImage
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:35 */     Image img = new DoubleImage(256, 256, 1);
/* 16:36 */     img.fill(0);
/* 17:   */     
/* 18:38 */     int centerX = 126;
/* 19:39 */     int centerY = 126;
/* 20:41 */     for (int y = 0; y < 256; y++) {
/* 21:42 */       for (int x = 0; x < 256; x++)
/* 22:   */       {
/* 23:44 */         double dx = x - centerX;
/* 24:45 */         double dy = y - centerY;
/* 25:   */         
/* 26:47 */         img.setXYDouble(x, y, (Math.atan2(dy, dx) + 3.141592653589793D) / 6.283185307179586D);
/* 27:   */       }
/* 28:   */     }
/* 29:51 */     System.err.println((Math.atan2(0.0D, 50.0D) + 3.141592653589793D) / 6.283185307179586D - 0.25D);
/* 30:52 */     System.err.println((Math.atan2(0.0D, -50.0D) + 3.141592653589793D) / 6.283185307179586D - 0.25D);
/* 31:   */     
/* 32:54 */     img = Angular2Color.invoke(img);
/* 33:56 */     for (int y = 0; y < 256; y++) {
/* 34:57 */       for (int x = 0; x < 256; x++) {
/* 35:59 */         if (Distance.EuclideanDistance(x, y, 126, 126) > 127.0D)
/* 36:   */         {
/* 37:60 */           img.setXYCDouble(x, y, 0, 1.0D);
/* 38:61 */           img.setXYCDouble(x, y, 1, 1.0D);
/* 39:62 */           img.setXYCDouble(x, y, 2, 1.0D);
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:67 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 44:68 */     Save.invoke(img, "hueCircle.png");
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.ProduceOrientationImage
 * JD-Core Version:    0.7.0.1
 */
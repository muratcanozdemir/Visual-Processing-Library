/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Sdev
/*  8:   */   implements Attribute
/*  9:   */ {
/* 10:   */   private int threshold;
/* 11:   */   
/* 12:   */   public Sdev(int threshold)
/* 13:   */   {
/* 14:22 */     this.threshold = threshold;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 18:   */   {
/* 19:32 */     double mean = 0.0D;
/* 20:33 */     double sdev = 0.0D;
/* 21:36 */     for (Point p : pixels) {
/* 22:37 */       mean += img.getXYByte(p.x, p.y);
/* 23:   */     }
/* 24:39 */     mean /= pixels.size();
/* 25:41 */     for (Point p : pixels)
/* 26:   */     {
/* 27:42 */       double tmp = img.getXYByte(p.x, p.y);
/* 28:43 */       sdev += (tmp - mean) * (tmp - mean);
/* 29:   */     }
/* 30:46 */     sdev = Math.sqrt(sdev / pixels.size());
/* 31:48 */     if (sdev < this.threshold) {
/* 32:48 */       return false;
/* 33:   */     }
/* 34:49 */     return true;
/* 35:   */   }
/* 36:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Sdev
 * JD-Core Version:    0.7.0.1
 */
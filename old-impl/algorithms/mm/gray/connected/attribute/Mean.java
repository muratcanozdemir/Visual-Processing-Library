/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Mean
/*  8:   */   implements Attribute
/*  9:   */ {
/* 10:   */   private double threshold;
/* 11:   */   
/* 12:   */   public Mean(double threshold)
/* 13:   */   {
/* 14:22 */     this.threshold = threshold;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 18:   */   {
/* 19:32 */     double mean = 0.0D;
/* 20:34 */     for (Point p : pixels) {
/* 21:35 */       mean += img.getXYDouble(p.x, p.y);
/* 22:   */     }
/* 23:37 */     mean /= pixels.size();
/* 24:39 */     if (mean < this.threshold) {
/* 25:39 */       return false;
/* 26:   */     }
/* 27:40 */     return true;
/* 28:   */   }
/* 29:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Mean
 * JD-Core Version:    0.7.0.1
 */
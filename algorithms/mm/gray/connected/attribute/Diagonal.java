/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Distance;
/*  7:   */ 
/*  8:   */ public class Diagonal
/*  9:   */   implements Attribute
/* 10:   */ {
/* 11:   */   private int threshold;
/* 12:   */   
/* 13:   */   public Diagonal(Integer threshold)
/* 14:   */   {
/* 15:23 */     this.threshold = threshold.intValue();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 19:   */   {
/* 20:33 */     int diagonal = 0;
/* 21:35 */     if (pixels.size() >= 2)
/* 22:   */     {
/* 23:37 */       int leftest = img.getXDim();
/* 24:38 */       int rightest = 0;
/* 25:39 */       int highest = img.getYDim();
/* 26:40 */       int lowest = 0;
/* 27:42 */       for (Point p : pixels)
/* 28:   */       {
/* 29:43 */         if (p.x < leftest) {
/* 30:43 */           leftest = p.x;
/* 31:   */         }
/* 32:44 */         if (p.x > rightest) {
/* 33:44 */           rightest = p.x;
/* 34:   */         }
/* 35:45 */         if (p.y > lowest) {
/* 36:45 */           lowest = p.y;
/* 37:   */         }
/* 38:46 */         if (p.y < highest) {
/* 39:46 */           highest = p.y;
/* 40:   */         }
/* 41:   */       }
/* 42:49 */       diagonal = (int)Distance.EuclideanDistance(leftest, highest, rightest, lowest);
/* 43:   */     }
/* 44:   */     else
/* 45:   */     {
/* 46:51 */       diagonal = 0;
/* 47:   */     }
/* 48:53 */     if (diagonal < this.threshold) {
/* 49:53 */       return false;
/* 50:   */     }
/* 51:54 */     return true;
/* 52:   */   }
/* 53:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Diagonal
 * JD-Core Version:    0.7.0.1
 */
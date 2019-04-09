/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  6:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class MultiDiagonal
/* 10:   */   implements MultiAttribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public MultiDiagonal(Integer threshold)
/* 15:   */   {
/* 16:24 */     this.threshold = threshold.intValue();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(MyList<Point> pixels, Image img)
/* 20:   */   {
/* 21:34 */     int diagonal = 0;
/* 22:36 */     if (pixels.size() >= 2)
/* 23:   */     {
/* 24:38 */       int leftest = img.getXDim();
/* 25:39 */       int rightest = 0;
/* 26:40 */       int highest = img.getYDim();
/* 27:41 */       int lowest = 0;
/* 28:43 */       for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 29:   */       {
/* 30:45 */         Point p = (Point)n.getDatum();
/* 31:47 */         if (p.x < leftest) {
/* 32:47 */           leftest = p.x;
/* 33:   */         }
/* 34:48 */         if (p.x > rightest) {
/* 35:48 */           rightest = p.x;
/* 36:   */         }
/* 37:49 */         if (p.y > lowest) {
/* 38:49 */           lowest = p.y;
/* 39:   */         }
/* 40:50 */         if (p.y < highest) {
/* 41:50 */           highest = p.y;
/* 42:   */         }
/* 43:   */       }
/* 44:53 */       diagonal = (int)Distance.EuclideanDistance(leftest, highest, rightest, lowest);
/* 45:   */     }
/* 46:   */     else
/* 47:   */     {
/* 48:55 */       diagonal = 0;
/* 49:   */     }
/* 50:59 */     if (diagonal < this.threshold) {
/* 51:59 */       return false;
/* 52:   */     }
/* 53:60 */     return true;
/* 54:   */   }
/* 55:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiDiagonal
 * JD-Core Version:    0.7.0.1
 */
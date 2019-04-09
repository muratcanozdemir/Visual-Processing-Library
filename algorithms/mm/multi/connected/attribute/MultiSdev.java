/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  6:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class MultiSdev
/* 10:   */   implements MultiAttribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public MultiSdev(int threshold)
/* 15:   */   {
/* 16:24 */     this.threshold = threshold;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(MyList<Point> pixels, Image img)
/* 20:   */   {
/* 21:34 */     double[] mean = new double[img.getCDim()];
/* 22:35 */     double sdev = 0.0D;
/* 23:37 */     for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 24:   */     {
/* 25:38 */       Point p = (Point)n.getDatum();
/* 26:40 */       for (int c = 0; c < mean.length; c++) {
/* 27:41 */         mean[c] += img.getXYCDouble(p.x, p.y, c);
/* 28:   */       }
/* 29:   */     }
/* 30:45 */     for (int c = 0; c < mean.length; c++) {
/* 31:46 */       mean[c] /= pixels.size();
/* 32:   */     }
/* 33:49 */     for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 34:   */     {
/* 35:50 */       Point p = (Point)n.getDatum();
/* 36:51 */       double[] vector = img.getVXYDouble(p.x, p.y);
/* 37:52 */       sdev += Distance.euclidean(mean, vector);
/* 38:   */     }
/* 39:55 */     sdev *= 255.0D;
/* 40:   */     
/* 41:57 */     sdev /= pixels.size();
/* 42:59 */     if (sdev < this.threshold) {
/* 43:59 */       return false;
/* 44:   */     }
/* 45:60 */     return true;
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiSdev
 * JD-Core Version:    0.7.0.1
 */
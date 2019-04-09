/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Iterator;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class MultiSdev
/* 10:   */   implements Attribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public MultiSdev(int threshold)
/* 15:   */   {
/* 16:25 */     this.threshold = threshold;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 20:   */   {
/* 21:35 */     double[] mean = new double[img.getCDim()];
/* 22:36 */     double sdev = 0.0D;
/* 23:   */     int c;
/* 24:38 */     for (Iterator localIterator = pixels.iterator(); localIterator.hasNext(); c < mean.length)
/* 25:   */     {
/* 26:38 */       Point p = (Point)localIterator.next();
/* 27:   */       
/* 28:   */ 
/* 29:41 */       c = 0; continue;
/* 30:42 */       mean[c] += img.getXYCDouble(p.x, p.y, c);c++;
/* 31:   */     }
/* 32:45 */     for (int c = 0; c < mean.length; c++) {
/* 33:46 */       mean[c] /= pixels.size();
/* 34:   */     }
/* 35:49 */     for (Point p : pixels)
/* 36:   */     {
/* 37:50 */       double[] vector = img.getVXYDouble(p.x, p.y);
/* 38:51 */       sdev += Distance.euclidean(mean, vector);
/* 39:   */     }
/* 40:54 */     sdev *= 255.0D;
/* 41:   */     
/* 42:56 */     sdev /= pixels.size();
/* 43:61 */     if (sdev < this.threshold) {
/* 44:61 */       return false;
/* 45:   */     }
/* 46:62 */     return true;
/* 47:   */   }
/* 48:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.MultiSdev
 * JD-Core Version:    0.7.0.1
 */
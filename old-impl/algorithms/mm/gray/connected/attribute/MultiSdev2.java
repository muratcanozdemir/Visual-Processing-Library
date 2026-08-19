/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Iterator;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class MultiSdev2
/* 10:   */   implements Attribute
/* 11:   */ {
/* 12:   */   private double threshold;
/* 13:   */   
/* 14:   */   public MultiSdev2(double threshold)
/* 15:   */   {
/* 16:25 */     this.threshold = threshold;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 20:   */   {
/* 21:35 */     double[] center = new double[img.getCDim()];
/* 22:36 */     double sdev = 0.0D;
/* 23:37 */     double mean = 0.0D;
/* 24:   */     int c;
/* 25:39 */     for (Iterator localIterator = pixels.iterator(); localIterator.hasNext(); c < center.length)
/* 26:   */     {
/* 27:39 */       Point p = (Point)localIterator.next();
/* 28:   */       
/* 29:   */ 
/* 30:42 */       c = 0; continue;
/* 31:43 */       center[c] += img.getXYCDouble(p.x, p.y, c);c++;
/* 32:   */     }
/* 33:46 */     for (int c = 0; c < center.length; c++) {
/* 34:47 */       center[c] /= pixels.size();
/* 35:   */     }
/* 36:50 */     for (Point p : pixels)
/* 37:   */     {
/* 38:51 */       double[] vector = img.getVXYDouble(p.x, p.y);
/* 39:52 */       mean += Distance.euclidean(center, vector);
/* 40:   */     }
/* 41:55 */     mean /= pixels.size();
/* 42:57 */     for (Point p : pixels)
/* 43:   */     {
/* 44:58 */       double[] vector = img.getVXYDouble(p.x, p.y);
/* 45:59 */       double tmp = Distance.euclidean(center, vector);
/* 46:60 */       sdev += (mean - tmp) * (mean - tmp);
/* 47:   */     }
/* 48:63 */     sdev *= 255.0D;
/* 49:   */     
/* 50:65 */     sdev /= pixels.size();
/* 51:70 */     if (sdev < this.threshold) {
/* 52:70 */       return false;
/* 53:   */     }
/* 54:71 */     return true;
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.MultiSdev2
 * JD-Core Version:    0.7.0.1
 */
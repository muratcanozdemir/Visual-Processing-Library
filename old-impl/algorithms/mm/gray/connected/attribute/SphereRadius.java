/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Arrays;
/*  6:   */ import java.util.Iterator;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.util.Distance;
/*  9:   */ 
/* 10:   */ public class SphereRadius
/* 11:   */   implements Attribute
/* 12:   */ {
/* 13:   */   private int threshold;
/* 14:   */   
/* 15:   */   public SphereRadius(Integer threshold)
/* 16:   */   {
/* 17:31 */     this.threshold = threshold.intValue();
/* 18:   */   }
/* 19:   */   
/* 20:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 21:   */   {
/* 22:41 */     int dim = img.getCDim();
/* 23:42 */     double maxDistance = 0.0D;
/* 24:   */     
/* 25:44 */     double[] center = new double[dim];
/* 26:45 */     Arrays.fill(center, 0.0D);
/* 27:   */     int i;
/* 28:48 */     for (Iterator localIterator = pixels.iterator(); localIterator.hasNext(); i < dim)
/* 29:   */     {
/* 30:48 */       Point p = (Point)localIterator.next();
/* 31:   */       
/* 32:50 */       int[] pixel = img.getVXYByte(p.x, p.y);
/* 33:   */       
/* 34:52 */       i = 0; continue;
/* 35:53 */       center[i] += pixel[i];i++;
/* 36:   */     }
/* 37:57 */     for (int i = 0; i < dim; i++) {
/* 38:58 */       center[i] /= pixels.size();
/* 39:   */     }
/* 40:61 */     for (Point p : pixels)
/* 41:   */     {
/* 42:63 */       double[] pixel = img.getVXYDouble(p.x, p.y);
/* 43:   */       
/* 44:65 */       double tmpDist = Distance.euclidean(center, pixel);
/* 45:67 */       if (tmpDist > maxDistance) {
/* 46:67 */         maxDistance = tmpDist;
/* 47:   */       }
/* 48:   */     }
/* 49:70 */     if (maxDistance < this.threshold) {
/* 50:70 */       return false;
/* 51:   */     }
/* 52:71 */     return true;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.SphereRadius
 * JD-Core Version:    0.7.0.1
 */
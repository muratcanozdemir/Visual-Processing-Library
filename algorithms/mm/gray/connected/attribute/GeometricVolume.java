/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Arrays;
/*  6:   */ import java.util.Iterator;
/*  7:   */ import vpt.Image;
/*  8:   */ 
/*  9:   */ public class GeometricVolume
/* 10:   */   implements Attribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public GeometricVolume(Integer threshold)
/* 15:   */   {
/* 16:25 */     this.threshold = threshold.intValue();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 20:   */   {
/* 21:35 */     double[] distances = new double[img.getCDim()];
/* 22:   */     
/* 23:37 */     int[] min = new int[img.getCDim()];
/* 24:38 */     Arrays.fill(min, 255);
/* 25:   */     
/* 26:40 */     int[] max = new int[img.getCDim()];
/* 27:41 */     Arrays.fill(max, 0);
/* 28:   */     
/* 29:43 */     int volume = 1;
/* 30:   */     int c;
/* 31:46 */     for (Iterator localIterator = pixels.iterator(); localIterator.hasNext(); c < max.length)
/* 32:   */     {
/* 33:46 */       Point p = (Point)localIterator.next();
/* 34:   */       
/* 35:48 */       int[] pixel = img.getVXYByte(p.x, p.y);
/* 36:   */       
/* 37:   */ 
/* 38:51 */       c = 0; continue;
/* 39:52 */       if (pixel[c] < min[c]) {
/* 40:52 */         min[c] = pixel[c];
/* 41:   */       }
/* 42:53 */       if (pixel[c] > max[c]) {
/* 43:53 */         max[c] = pixel[c];
/* 44:   */       }
/* 45:51 */       c++;
/* 46:   */     }
/* 47:58 */     for (int c = 0; c < distances.length; c++) {
/* 48:59 */       distances[c] = (max[c] - min[c]);
/* 49:   */     }
/* 50:62 */     for (int c = 0; c < distances.length; c++) {
/* 51:63 */       volume = (int)(volume * distances[c]);
/* 52:   */     }
/* 53:68 */     if (volume < this.threshold) {
/* 54:68 */       return false;
/* 55:   */     }
/* 56:69 */     return true;
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.GeometricVolume
 * JD-Core Version:    0.7.0.1
 */
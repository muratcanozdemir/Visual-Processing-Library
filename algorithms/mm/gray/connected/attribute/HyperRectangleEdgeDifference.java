/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Arrays;
/*  6:   */ import java.util.Iterator;
/*  7:   */ import vpt.Image;
/*  8:   */ 
/*  9:   */ public class HyperRectangleEdgeDifference
/* 10:   */   implements Attribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public HyperRectangleEdgeDifference(Integer threshold)
/* 15:   */   {
/* 16:26 */     this.threshold = threshold.intValue();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 20:   */   {
/* 21:36 */     double[] distances = new double[img.getCDim()];
/* 22:   */     
/* 23:38 */     int[] min = new int[img.getCDim()];
/* 24:39 */     Arrays.fill(min, 255);
/* 25:   */     
/* 26:41 */     int[] max = new int[img.getCDim()];
/* 27:42 */     Arrays.fill(max, 0);
/* 28:   */     
/* 29:44 */     int diff = 0;
/* 30:   */     int c;
/* 31:47 */     for (Iterator localIterator = pixels.iterator(); localIterator.hasNext(); c < max.length)
/* 32:   */     {
/* 33:47 */       Point p = (Point)localIterator.next();
/* 34:   */       
/* 35:49 */       int[] pixel = img.getVXYByte(p.x, p.y);
/* 36:   */       
/* 37:   */ 
/* 38:52 */       c = 0; continue;
/* 39:53 */       if (pixel[c] < min[c]) {
/* 40:53 */         min[c] = pixel[c];
/* 41:   */       }
/* 42:54 */       if (pixel[c] > max[c]) {
/* 43:54 */         max[c] = pixel[c];
/* 44:   */       }
/* 45:52 */       c++;
/* 46:   */     }
/* 47:58 */     int maxEdge = 0;
/* 48:59 */     int minEdge = 2147483647;
/* 49:61 */     for (int c = 0; c < max.length; c++)
/* 50:   */     {
/* 51:62 */       if (maxEdge < max[c] - min[c]) {
/* 52:62 */         maxEdge = max[c] - min[c];
/* 53:   */       }
/* 54:63 */       if (minEdge > max[c] - min[c]) {
/* 55:63 */         minEdge = max[c] - min[c];
/* 56:   */       }
/* 57:   */     }
/* 58:66 */     diff = maxEdge - minEdge;
/* 59:71 */     if (diff < this.threshold) {
/* 60:71 */       return false;
/* 61:   */     }
/* 62:72 */     return true;
/* 63:   */   }
/* 64:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.HyperRectangleEdgeDifference
 * JD-Core Version:    0.7.0.1
 */
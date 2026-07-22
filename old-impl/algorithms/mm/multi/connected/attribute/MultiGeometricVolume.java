/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.Arrays;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  7:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  8:   */ 
/*  9:   */ public class MultiGeometricVolume
/* 10:   */   implements MultiAttribute
/* 11:   */ {
/* 12:   */   private int threshold;
/* 13:   */   
/* 14:   */   public MultiGeometricVolume(Integer threshold)
/* 15:   */   {
/* 16:24 */     this.threshold = threshold.intValue();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean test(MyList<Point> pixels, Image img)
/* 20:   */   {
/* 21:34 */     double[] distances = new double[img.getCDim()];
/* 22:   */     
/* 23:36 */     int[] min = new int[img.getCDim()];
/* 24:37 */     Arrays.fill(min, 255);
/* 25:   */     
/* 26:39 */     int[] max = new int[img.getCDim()];
/* 27:40 */     Arrays.fill(max, 0);
/* 28:   */     
/* 29:42 */     int volume = 1;
/* 30:45 */     for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 31:   */     {
/* 32:47 */       Point p = (Point)n.getDatum();
/* 33:48 */       int[] pixel = img.getVXYByte(p.x, p.y);
/* 34:51 */       for (int c = 0; c < max.length; c++)
/* 35:   */       {
/* 36:52 */         if (pixel[c] < min[c]) {
/* 37:52 */           min[c] = pixel[c];
/* 38:   */         }
/* 39:53 */         if (pixel[c] > max[c]) {
/* 40:53 */           max[c] = pixel[c];
/* 41:   */         }
/* 42:   */       }
/* 43:   */     }
/* 44:58 */     for (int c = 0; c < distances.length; c++) {
/* 45:59 */       distances[c] = (max[c] - min[c]);
/* 46:   */     }
/* 47:62 */     for (int c = 0; c < distances.length; c++) {
/* 48:63 */       volume = (int)(volume * distances[c]);
/* 49:   */     }
/* 50:68 */     if (volume < this.threshold) {
/* 51:68 */       return false;
/* 52:   */     }
/* 53:69 */     return true;
/* 54:   */   }
/* 55:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiGeometricVolume
 * JD-Core Version:    0.7.0.1
 */
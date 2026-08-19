/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  6:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  7:   */ 
/*  8:   */ public class MultiSdev2
/*  9:   */   implements MultiAttribute
/* 10:   */ {
/* 11:   */   private int threshold;
/* 12:   */   
/* 13:   */   public MultiSdev2(int threshold)
/* 14:   */   {
/* 15:23 */     this.threshold = threshold;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean test(MyList<Point> pixels, Image img)
/* 19:   */   {
/* 20:33 */     double[] mean = new double[img.getCDim()];
/* 21:34 */     double sdev = 0.0D;
/* 22:36 */     for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 23:   */     {
/* 24:37 */       Point p = (Point)n.getDatum();
/* 25:39 */       for (int c = 0; c < mean.length; c++) {
/* 26:40 */         mean[c] += img.getXYCDouble(p.x, p.y, c);
/* 27:   */       }
/* 28:   */     }
/* 29:45 */     for (int c = 0; c < mean.length; c++) {
/* 30:46 */       mean[c] /= pixels.size();
/* 31:   */     }
/* 32:48 */     double interDimensionMean = 0.0D;
/* 33:50 */     for (int c = 0; c < mean.length; c++) {
/* 34:51 */       interDimensionMean += mean[c];
/* 35:   */     }
/* 36:53 */     interDimensionMean /= mean.length;
/* 37:55 */     for (int c = 0; c < mean.length; c++) {
/* 38:56 */       sdev += (interDimensionMean - mean[c]) * (interDimensionMean - mean[c]);
/* 39:   */     }
/* 40:58 */     sdev /= mean.length;
/* 41:   */     
/* 42:60 */     sdev = Math.sqrt(sdev);
/* 43:   */     
/* 44:62 */     sdev *= 255.0D;
/* 45:66 */     if (sdev < this.threshold) {
/* 46:66 */       return false;
/* 47:   */     }
/* 48:67 */     return true;
/* 49:   */   }
/* 50:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiSdev2
 * JD-Core Version:    0.7.0.1
 */
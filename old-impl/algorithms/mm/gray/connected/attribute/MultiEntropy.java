/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Tools;
/*  7:   */ 
/*  8:   */ public class MultiEntropy
/*  9:   */   implements Attribute
/* 10:   */ {
/* 11:   */   private double threshold;
/* 12:   */   
/* 13:   */   public MultiEntropy(double threshold)
/* 14:   */   {
/* 15:23 */     this.threshold = threshold;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 19:   */   {
/* 20:34 */     double[] histo = new double[256];
/* 21:37 */     for (Point p : pixels)
/* 22:   */     {
/* 23:38 */       int pixelVal = (int)(255.0D * Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y)) / Math.sqrt(img.getCDim()));
/* 24:39 */       histo[pixelVal] += 1.0D;
/* 25:   */     }
/* 26:43 */     for (int i = 0; i < histo.length; i++) {
/* 27:44 */       histo[i] /= pixels.size();
/* 28:   */     }
/* 29:46 */     double entropy = 0.0D;
/* 30:   */     
/* 31:48 */     double logBaseChange = Math.log10(2.0D);
/* 32:50 */     for (Point p : pixels)
/* 33:   */     {
/* 34:51 */       int q = (int)(255.0D * Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y)) / Math.sqrt(img.getCDim()));
/* 35:52 */       entropy += histo[q] * (Math.log10(histo[q]) / logBaseChange);
/* 36:   */     }
/* 37:55 */     if (entropy < 0.0D) {
/* 38:55 */       entropy *= -1.0D;
/* 39:   */     }
/* 40:60 */     if (entropy < this.threshold) {
/* 41:60 */       return false;
/* 42:   */     }
/* 43:61 */     return true;
/* 44:   */   }
/* 45:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.MultiEntropy
 * JD-Core Version:    0.7.0.1
 */
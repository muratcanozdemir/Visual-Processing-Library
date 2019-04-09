/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Entropy
/*  8:   */   implements Attribute
/*  9:   */ {
/* 10:   */   private double threshold;
/* 11:   */   
/* 12:   */   public Entropy(double threshold)
/* 13:   */   {
/* 14:22 */     this.threshold = threshold;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 18:   */   {
/* 19:33 */     double[] histo = new double[256];
/* 20:36 */     for (Point p : pixels) {
/* 21:37 */       histo[img.getXYByte(p.x, p.y)] += 1.0D;
/* 22:   */     }
/* 23:40 */     for (int i = 0; i < histo.length; i++) {
/* 24:41 */       histo[i] /= pixels.size();
/* 25:   */     }
/* 26:43 */     double entropy = 0.0D;
/* 27:   */     
/* 28:45 */     double logBaseChange = Math.log10(2.0D);
/* 29:47 */     for (Point p : pixels)
/* 30:   */     {
/* 31:48 */       int q = img.getXYByte(p.x, p.y);
/* 32:49 */       entropy += histo[q] * (Math.log10(histo[q]) / logBaseChange);
/* 33:   */     }
/* 34:52 */     if (entropy < 0.0D) {
/* 35:52 */       entropy *= -1.0D;
/* 36:   */     }
/* 37:57 */     if (entropy < this.threshold) {
/* 38:57 */       return false;
/* 39:   */     }
/* 40:58 */     return true;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Entropy
 * JD-Core Version:    0.7.0.1
 */
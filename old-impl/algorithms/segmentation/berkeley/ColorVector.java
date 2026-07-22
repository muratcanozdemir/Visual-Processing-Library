/*  1:   */ package vpt.algorithms.segmentation.berkeley;
/*  2:   */ 
/*  3:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  4:   */ 
/*  5:   */ public class ColorVector
/*  6:   */   implements Comparable
/*  7:   */ {
/*  8:   */   double[] z;
/*  9:   */   AlgebraicOrdering vo;
/* 10:   */   
/* 11:   */   ColorVector(int a, int b, int c, AlgebraicOrdering vo)
/* 12:   */   {
/* 13:12 */     this.z = new double[3];
/* 14:13 */     this.z[0] = (a * 0.00392156862745098D);
/* 15:14 */     this.z[1] = (b * 0.00392156862745098D);
/* 16:15 */     this.z[2] = (c * 0.00392156862745098D);
/* 17:16 */     this.vo = vo;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public int compareTo(Object o)
/* 21:   */   {
/* 22:20 */     ColorVector c = (ColorVector)o;
/* 23:21 */     return this.vo.compare(this.z, c.z);
/* 24:   */   }
/* 25:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.ColorVector
 * JD-Core Version:    0.7.0.1
 */
/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Tools;
/*  4:   */ 
/*  5:   */ public class EuclideanNorm
/*  6:   */   extends AlgebraicOrdering
/*  7:   */ {
/*  8:14 */   private Lexico lex = new Lexico();
/*  9:   */   
/* 10:   */   public int compare(double[] v1, double[] v2)
/* 11:   */   {
/* 12:18 */     double norm1 = Tools.EuclideanNorm(v1);
/* 13:19 */     double norm2 = Tools.EuclideanNorm(v2);
/* 14:   */     
/* 15:21 */     int tmp = Tools.cmpr(norm1, norm2);
/* 16:23 */     if (tmp != 0) {
/* 17:24 */       return tmp;
/* 18:   */     }
/* 19:26 */     return this.lex.compare(v1, v2);
/* 20:   */   }
/* 21:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.EuclideanNorm
 * JD-Core Version:    0.7.0.1
 */
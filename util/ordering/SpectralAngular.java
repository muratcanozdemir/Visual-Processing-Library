/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Distance;
/*  4:   */ import vpt.util.Tools;
/*  5:   */ 
/*  6:   */ public class SpectralAngular
/*  7:   */   extends AlgebraicOrdering
/*  8:   */ {
/*  9:17 */   private Lexico lex = new Lexico();
/* 10:18 */   private double[] ref = { 0.0D, 0.0D, 0.0D, 0.0D };
/* 11:   */   
/* 12:   */   public int compare(double[] v1, double[] v2)
/* 13:   */   {
/* 14:22 */     double norm1 = Distance.spectralAngleDistance(v1, this.ref);
/* 15:23 */     double norm2 = Distance.spectralAngleDistance(v2, this.ref);
/* 16:   */     
/* 17:25 */     int tmp = Tools.cmpr(norm1, norm2);
/* 18:27 */     if (tmp != 0) {
/* 19:28 */       return tmp;
/* 20:   */     }
/* 21:30 */     return this.lex.compare(v1, v2);
/* 22:   */   }
/* 23:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.SpectralAngular
 * JD-Core Version:    0.7.0.1
 */
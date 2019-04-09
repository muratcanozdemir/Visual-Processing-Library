/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ public class CompositeOrdering
/*  4:   */   extends AlgebraicOrdering
/*  5:   */ {
/*  6:11 */   AlgebraicOrdering[] ors = null;
/*  7:   */   
/*  8:   */   public CompositeOrdering(AlgebraicOrdering[] ors)
/*  9:   */   {
/* 10:14 */     this.ors = ors;
/* 11:   */   }
/* 12:   */   
/* 13:   */   public int compare(double[] v1, double[] v2)
/* 14:   */   {
/* 15:23 */     int tmp0 = this.ors[0].compare(v1, v2);
/* 16:24 */     int tmp1 = this.ors[1].compare(v1, v2);
/* 17:25 */     int tmp2 = this.ors[2].compare(v1, v2);
/* 18:28 */     if (tmp0 + tmp1 + tmp2 >= 2) {
/* 19:28 */       return 1;
/* 20:   */     }
/* 21:29 */     if (tmp0 + tmp1 + tmp2 <= -2) {
/* 22:29 */       return -1;
/* 23:   */     }
/* 24:32 */     if (tmp0 != 0) {
/* 25:32 */       return tmp0;
/* 26:   */     }
/* 27:33 */     if (tmp1 != 0) {
/* 28:33 */       return tmp1;
/* 29:   */     }
/* 30:34 */     return tmp2;
/* 31:   */   }
/* 32:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.CompositeOrdering
 * JD-Core Version:    0.7.0.1
 */
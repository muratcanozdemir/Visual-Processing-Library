/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Tools;
/*  4:   */ 
/*  5:   */ public class LexicoCIELAB
/*  6:   */   extends AlgebraicOrdering
/*  7:   */ {
/*  8:   */   public int compare(double[] v1, double[] v2)
/*  9:   */   {
/* 10:19 */     int tmp = Tools.cmpr(v1[0], v2[0]);
/* 11:21 */     if (tmp != 0) {
/* 12:21 */       return tmp;
/* 13:   */     }
/* 14:23 */     double norm1 = v1[1] * v1[1] + v1[2] * v1[2];
/* 15:24 */     double norm2 = v2[1] * v2[1] + v2[2] * v2[2];
/* 16:   */     
/* 17:26 */     tmp = Tools.cmpr(norm1, norm2);
/* 18:28 */     if (tmp != 0) {
/* 19:28 */       return tmp;
/* 20:   */     }
/* 21:31 */     tmp = Tools.cmpr(v1[1], v2[1]);
/* 22:32 */     if (tmp != 0) {
/* 23:32 */       return tmp;
/* 24:   */     }
/* 25:34 */     tmp = Tools.cmpr(v1[2], v2[2]);
/* 26:35 */     if (tmp != 0) {
/* 27:35 */       return tmp;
/* 28:   */     }
/* 29:37 */     return 0;
/* 30:   */   }
/* 31:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.LexicoCIELAB
 * JD-Core Version:    0.7.0.1
 */
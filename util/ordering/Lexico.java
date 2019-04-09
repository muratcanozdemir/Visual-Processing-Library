/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Tools;
/*  4:   */ 
/*  5:   */ public class Lexico
/*  6:   */   extends AlgebraicOrdering
/*  7:   */ {
/*  8:   */   public int compare(double[] v1, double[] v2)
/*  9:   */   {
/* 10:15 */     for (int i = 0; i < v1.length; i++)
/* 11:   */     {
/* 12:16 */       int tmp = Tools.cmpr(v1[i], v2[i]);
/* 13:18 */       if (tmp != 0) {
/* 14:19 */         return tmp;
/* 15:   */       }
/* 16:   */     }
/* 17:22 */     return 0;
/* 18:   */   }
/* 19:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.Lexico
 * JD-Core Version:    0.7.0.1
 */
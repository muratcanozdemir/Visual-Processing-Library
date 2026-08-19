/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.Comparator;
/*  5:   */ 
/*  6:   */ public abstract class AlgebraicOrdering
/*  7:   */   implements Ordering, Comparator<double[]>
/*  8:   */ {
/*  9:   */   public abstract int compare(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
/* 10:   */   
/* 11:   */   public double[] max(double[] v1, double[] v2)
/* 12:   */   {
/* 13:31 */     if (compare(v1, v2) > 0) {
/* 14:31 */       return v1;
/* 15:   */     }
/* 16:32 */     return v2;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public double[] min(double[] v1, double[] v2)
/* 20:   */   {
/* 21:43 */     if (compare(v1, v2) > 0) {
/* 22:43 */       return v2;
/* 23:   */     }
/* 24:44 */     return v1;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public double[] max(double[][] v)
/* 28:   */   {
/* 29:55 */     double[] max = v[0];
/* 30:57 */     for (int i = 1; i < v.length; i++) {
/* 31:58 */       if (compare(v[i], max) > 0) {
/* 32:59 */         max = v[i];
/* 33:   */       }
/* 34:   */     }
/* 35:61 */     return max;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public double[] min(double[][] v)
/* 39:   */   {
/* 40:72 */     double[] min = v[0];
/* 41:74 */     for (int i = 1; i < v.length; i++) {
/* 42:75 */       if (compare(v[i], min) < 0) {
/* 43:76 */         min = v[i];
/* 44:   */       }
/* 45:   */     }
/* 46:78 */     return min;
/* 47:   */   }
/* 48:   */   
/* 49:   */   public double[] rank(double[][] v, int r)
/* 50:   */   {
/* 51:83 */     Arrays.sort(v, this);
/* 52:   */     
/* 53:85 */     return v[r];
/* 54:   */   }
/* 55:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.AlgebraicOrdering
 * JD-Core Version:    0.7.0.1
 */
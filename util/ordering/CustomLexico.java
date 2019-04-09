/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Tools;
/*  4:   */ 
/*  5:   */ public class CustomLexico
/*  6:   */   extends AlgebraicOrdering
/*  7:   */ {
/*  8:13 */   private int[] cOrder = null;
/*  9:   */   
/* 10:   */   public CustomLexico(int channelNumber)
/* 11:   */   {
/* 12:19 */     this.cOrder = new int[channelNumber];
/* 13:21 */     for (int i = 0; i < channelNumber; i++) {
/* 14:22 */       this.cOrder[i] = i;
/* 15:   */     }
/* 16:   */   }
/* 17:   */   
/* 18:   */   public CustomLexico(int[] channelOrder)
/* 19:   */   {
/* 20:29 */     this.cOrder = channelOrder;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public int compare(double[] v1, double[] v2)
/* 24:   */   {
/* 25:34 */     for (int i = 0; i < v1.length; i++)
/* 26:   */     {
/* 27:35 */       int tmp = Tools.cmpr(v1[this.cOrder[i]], v2[this.cOrder[i]]);
/* 28:37 */       if (tmp != 0) {
/* 29:38 */         return tmp;
/* 30:   */       }
/* 31:   */     }
/* 32:41 */     return 0;
/* 33:   */   }
/* 34:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.CustomLexico
 * JD-Core Version:    0.7.0.1
 */
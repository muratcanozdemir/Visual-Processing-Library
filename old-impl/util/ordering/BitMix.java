/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ public class BitMix
/*  4:   */   extends AlgebraicOrdering
/*  5:   */ {
/*  6:15 */   private int[] p1 = new int[3];
/*  7:16 */   private int[] p2 = new int[3];
/*  8:17 */   private int[][] s1 = new int[3][8];
/*  9:18 */   private int[][] s2 = new int[3][8];
/* 10:   */   
/* 11:   */   public int compare(double[] v1, double[] v2)
/* 12:   */   {
/* 13:28 */     this.p1[0] = ((int)Math.round(v1[0] * 255.0D));
/* 14:29 */     this.p1[1] = ((int)Math.round(v1[1] * 255.0D));
/* 15:30 */     this.p1[2] = ((int)Math.round(v1[2] * 255.0D));
/* 16:   */     
/* 17:32 */     this.p2[0] = ((int)Math.round(v2[0] * 255.0D));
/* 18:33 */     this.p2[1] = ((int)Math.round(v2[1] * 255.0D));
/* 19:34 */     this.p2[2] = ((int)Math.round(v2[2] * 255.0D));
/* 20:37 */     for (int i = 0; i < 3; i++)
/* 21:   */     {
/* 22:38 */       int tmp = this.p1[i];
/* 23:40 */       for (int j = 7; j >= 0; j--)
/* 24:   */       {
/* 25:41 */         this.s1[i][(7 - j)] = (tmp & 0x1);
/* 26:42 */         tmp >>>= 1;
/* 27:   */       }
/* 28:45 */       tmp = this.p2[i];
/* 29:47 */       for (int j = 7; j >= 0; j--)
/* 30:   */       {
/* 31:48 */         this.s2[i][(7 - j)] = (tmp & 0x1);
/* 32:49 */         tmp >>>= 1;
/* 33:   */       }
/* 34:   */     }
/* 35:55 */     for (int i = 0; i < 8; i++)
/* 36:   */     {
/* 37:58 */       if (this.s1[0][(7 - i)] > this.s2[0][(7 - i)]) {
/* 38:58 */         return 1;
/* 39:   */       }
/* 40:59 */       if (this.s1[0][(7 - i)] < this.s2[0][(7 - i)]) {
/* 41:59 */         return -1;
/* 42:   */       }
/* 43:62 */       if (this.s1[1][(7 - i)] > this.s2[1][(7 - i)]) {
/* 44:62 */         return 1;
/* 45:   */       }
/* 46:63 */       if (this.s1[1][(7 - i)] < this.s2[1][(7 - i)]) {
/* 47:63 */         return -1;
/* 48:   */       }
/* 49:66 */       if (this.s1[2][(7 - i)] > this.s2[2][(7 - i)]) {
/* 50:66 */         return 1;
/* 51:   */       }
/* 52:67 */       if (this.s1[2][(7 - i)] < this.s2[2][(7 - i)]) {
/* 53:67 */         return -1;
/* 54:   */       }
/* 55:   */     }
/* 56:70 */     return 0;
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.BitMix
 * JD-Core Version:    0.7.0.1
 */
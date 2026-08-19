/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ public class EndMemberExtrema
/*  4:   */   extends AlgebraicOrdering
/*  5:   */ {
/*  6:18 */   private double[] max = null;
/*  7:19 */   private double[] min = null;
/*  8:21 */   private Lexico lex = new Lexico();
/*  9:   */   
/* 10:   */   public EndMemberExtrema(double[][] endMembers)
/* 11:   */   {
/* 12:25 */     this.max = new double[endMembers[0].length];
/* 13:26 */     this.min = new double[endMembers[0].length];
/* 14:28 */     for (int i = 0; i < endMembers.length; i++) {
/* 15:29 */       for (int j = 0; j < endMembers[0].length; j++) {}
/* 16:   */     }
/* 17:   */   }
/* 18:   */   
/* 19:   */   public int compare(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
/* 20:   */   {
/* 21:42 */     throw new Error("Unresolved compilation problems: \n\tendMembers cannot be resolved to a variable\n\tendMembers cannot be resolved to a variable\n\tendMembers cannot be resolved to a variable\n\tendMembers cannot be resolved to a variable\n");
/* 22:   */   }
/* 23:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.EndMemberExtrema
 * JD-Core Version:    0.7.0.1
 */
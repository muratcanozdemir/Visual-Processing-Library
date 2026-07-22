/*  1:   */ package vpt.util.ordering;
/*  2:   */ 
/*  3:   */ import vpt.util.Distance;
/*  4:   */ import vpt.util.Tools;
/*  5:   */ 
/*  6:   */ public class EndMember
/*  7:   */   extends AlgebraicOrdering
/*  8:   */ {
/*  9:16 */   private double[][] endMembers = null;
/* 10:17 */   private Lexico lex = new Lexico();
/* 11:   */   
/* 12:   */   public EndMember(double[][] endMembers)
/* 13:   */   {
/* 14:21 */     this.endMembers = new double[endMembers.length][endMembers[0].length];
/* 15:23 */     for (int i = 0; i < this.endMembers.length; i++) {
/* 16:24 */       for (int j = 0; j < this.endMembers[0].length; j++) {
/* 17:25 */         this.endMembers[i][j] = endMembers[i][j];
/* 18:   */       }
/* 19:   */     }
/* 20:   */   }
/* 21:   */   
/* 22:   */   public int compare(double[] v1, double[] v2)
/* 23:   */   {
/* 24:33 */     double dist1 = 1.7976931348623157E+308D;
/* 25:35 */     for (int i = 0; i < this.endMembers.length; i++)
/* 26:   */     {
/* 27:36 */       double tmpDist = Distance.euclidean(this.endMembers[i], v1);
/* 28:39 */       if (tmpDist < dist1) {
/* 29:39 */         dist1 = tmpDist;
/* 30:   */       }
/* 31:   */     }
/* 32:43 */     double dist2 = 1.7976931348623157E+308D;
/* 33:45 */     for (int i = 0; i < this.endMembers.length; i++)
/* 34:   */     {
/* 35:46 */       double tmpDist = Distance.euclidean(this.endMembers[i], v2);
/* 36:49 */       if (tmpDist < dist2) {
/* 37:49 */         dist2 = tmpDist;
/* 38:   */       }
/* 39:   */     }
/* 40:52 */     int cmpr = Tools.cmpr(dist1, dist2) * -1;
/* 41:54 */     if (cmpr != 0) {
/* 42:54 */       return cmpr;
/* 43:   */     }
/* 44:55 */     return this.lex.compare(v1, v2);
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.EndMember
 * JD-Core Version:    0.7.0.1
 */
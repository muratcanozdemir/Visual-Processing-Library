/*   1:    */ package vpt.util.ordering;
/*   2:    */ 
/*   3:    */ import vpt.util.Distance;
/*   4:    */ import vpt.util.Tools;
/*   5:    */ 
/*   6:    */ public class MarginalEndMember
/*   7:    */   extends AlgebraicOrdering
/*   8:    */ {
/*   9: 16 */   private double[][] endMembers = null;
/*  10: 17 */   private Lexico lex = new Lexico();
/*  11:    */   
/*  12:    */   public MarginalEndMember(double[][] endMembers)
/*  13:    */   {
/*  14: 21 */     this.endMembers = new double[endMembers.length][endMembers[0].length];
/*  15: 23 */     for (int i = 0; i < this.endMembers.length; i++) {
/*  16: 24 */       for (int j = 0; j < this.endMembers[0].length; j++) {
/*  17: 25 */         this.endMembers[i][j] = endMembers[i][j];
/*  18:    */       }
/*  19:    */     }
/*  20:    */   }
/*  21:    */   
/*  22:    */   public int compare(double[] v1, double[] v2)
/*  23:    */   {
/*  24: 33 */     double dist1 = 1.7976931348623157E+308D;
/*  25: 35 */     for (int i = 0; i < this.endMembers.length; i++)
/*  26:    */     {
/*  27: 36 */       double tmpDist = Distance.euclidean(this.endMembers[i], v1);
/*  28: 37 */       if (tmpDist < dist1) {
/*  29: 37 */         dist1 = tmpDist;
/*  30:    */       }
/*  31:    */     }
/*  32: 41 */     double dist2 = 1.7976931348623157E+308D;
/*  33: 43 */     for (int i = 0; i < this.endMembers.length; i++)
/*  34:    */     {
/*  35: 44 */       double tmpDist = Distance.euclidean(this.endMembers[i], v2);
/*  36: 45 */       if (tmpDist < dist2) {
/*  37: 45 */         dist2 = tmpDist;
/*  38:    */       }
/*  39:    */     }
/*  40: 48 */     int cmpr = Tools.cmpr(dist1, dist2) * -1;
/*  41: 50 */     if (cmpr != 0) {
/*  42: 50 */       return cmpr;
/*  43:    */     }
/*  44: 51 */     return this.lex.compare(v1, v2);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public double[] max(double[][] v)
/*  48:    */   {
/*  49: 55 */     double[] maxVector = new double[v[0].length];
/*  50: 58 */     for (int i = 0; i < maxVector.length; i++)
/*  51:    */     {
/*  52: 60 */       double minDistance = 1.7976931348623157E+308D;
/*  53: 61 */       int index = 0;
/*  54: 64 */       for (int j = 0; j < v.length; j++) {
/*  55: 67 */         for (int k = 0; k < this.endMembers.length; k++)
/*  56:    */         {
/*  57: 69 */           double tmpDist = Math.abs(this.endMembers[k][i] - v[j][i]);
/*  58: 71 */           if (tmpDist < minDistance)
/*  59:    */           {
/*  60: 72 */             index = j;
/*  61: 73 */             minDistance = tmpDist;
/*  62:    */           }
/*  63:    */         }
/*  64:    */       }
/*  65: 79 */       maxVector[i] = v[index][i];
/*  66:    */     }
/*  67: 82 */     return maxVector;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public double[] min(double[][] v)
/*  71:    */   {
/*  72: 86 */     double[] minVector = new double[v[0].length];
/*  73: 89 */     for (int i = 0; i < minVector.length; i++)
/*  74:    */     {
/*  75: 91 */       double maxDistance = 0.0D;
/*  76: 92 */       int index = 0;
/*  77: 95 */       for (int j = 0; j < v.length; j++) {
/*  78: 98 */         for (int k = 0; k < this.endMembers.length; k++)
/*  79:    */         {
/*  80:100 */           double tmpDist = Math.abs(this.endMembers[k][i] - v[j][i]);
/*  81:102 */           if (tmpDist > maxDistance)
/*  82:    */           {
/*  83:103 */             index = j;
/*  84:104 */             maxDistance = tmpDist;
/*  85:    */           }
/*  86:    */         }
/*  87:    */       }
/*  88:110 */       minVector[i] = v[index][i];
/*  89:    */     }
/*  90:113 */     return minVector;
/*  91:    */   }
/*  92:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.MarginalEndMember
 * JD-Core Version:    0.7.0.1
 */
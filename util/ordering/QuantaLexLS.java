/*   1:    */ package vpt.util.ordering;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import vpt.util.Tools;
/*   5:    */ 
/*   6:    */ public class QuantaLexLS
/*   7:    */   extends AlgebraicOrdering
/*   8:    */ {
/*   9:    */   private double alpha;
/*  10: 24 */   private Lexico lex = new Lexico();
/*  11:    */   private double[] transformed;
/*  12:    */   
/*  13:    */   public QuantaLexLS(double alpha)
/*  14:    */   {
/*  15: 34 */     if (alpha > 1.0D) {
/*  16: 34 */       this.alpha = alpha;
/*  17:    */     } else {
/*  18: 35 */       this.alpha = 1.0D;
/*  19:    */     }
/*  20: 38 */     alpha = Math.round(alpha);
/*  21:    */     
/*  22: 40 */     this.transformed = new double[256];
/*  23: 41 */     double value = 0.0D;
/*  24: 43 */     for (int z = 0; z < 256;)
/*  25:    */     {
/*  26: 44 */       int k = (int)Math.ceil(alpha * sigma(z) + 1.0E-008D);
/*  27: 46 */       for (int i = z; i < z + k; i++) {
/*  28: 47 */         this.transformed[i] = value;
/*  29:    */       }
/*  30: 49 */       value += 1.0D;
/*  31: 50 */       z += k;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   double sigma(int z)
/*  36:    */   {
/*  37: 56 */     double x = z / 255.0D;
/*  38: 58 */     if (x <= 0.5D) {
/*  39: 58 */       return (1.0D / (1.0D + Math.exp(-10.0D * (x - 0.25D))) - 0.07580000000000001D) / 0.84834D;
/*  40:    */     }
/*  41: 59 */     return (1.0D / (1.0D + Math.exp(10.0D * (x - 0.75D))) - 0.07580000000000001D) / 0.84834D;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double[] max(double[][] p)
/*  45:    */   {
/*  46: 64 */     double[] max = p[0];
/*  47: 66 */     for (int i = 1; i < p.length; i++) {
/*  48: 67 */       if (compare(max, p[i]) < 0) {
/*  49: 67 */         max = p[i];
/*  50:    */       }
/*  51:    */     }
/*  52: 70 */     return max;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public double[] min(double[][] p)
/*  56:    */   {
/*  57: 75 */     double[] min = p[0];
/*  58: 77 */     for (int i = 1; i < p.length; i++) {
/*  59: 78 */       if (compare(min, p[i]) > 0) {
/*  60: 78 */         min = p[i];
/*  61:    */       }
/*  62:    */     }
/*  63: 81 */     return min;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public double[] rank(double[][] p, int r)
/*  67:    */   {
/*  68: 86 */     Arrays.sort(p, this);
/*  69:    */     
/*  70: 88 */     return p[r];
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int compare(double[] p1, double[] p2)
/*  74:    */   {
/*  75:103 */     if (this.alpha > 1.0D)
/*  76:    */     {
/*  77:104 */       double dtmp1 = Math.round(255.0D * p1[2]);
/*  78:105 */       dtmp1 = this.transformed[((int)dtmp1)];
/*  79:    */       
/*  80:107 */       double dtmp2 = Math.round(255.0D * p2[2]);
/*  81:108 */       dtmp2 = this.transformed[((int)dtmp2)];
/*  82:111 */       if (Tools.cmpr(dtmp1, dtmp2) < 0) {
/*  83:111 */         return -1;
/*  84:    */       }
/*  85:112 */       if (Tools.cmpr(dtmp1, dtmp2) > 0) {
/*  86:112 */         return 1;
/*  87:    */       }
/*  88:    */     }
/*  89:    */     else
/*  90:    */     {
/*  91:114 */       if (Tools.cmpr(p1[2], p2[2]) < 0) {
/*  92:114 */         return -1;
/*  93:    */       }
/*  94:115 */       if (Tools.cmpr(p1[2], p2[2]) > 0) {
/*  95:115 */         return 1;
/*  96:    */       }
/*  97:    */     }
/*  98:119 */     if (Tools.cmpr(p1[1], p2[1]) < 0) {
/*  99:119 */       return -1;
/* 100:    */     }
/* 101:120 */     if (Tools.cmpr(p1[1], p2[1]) > 0) {
/* 102:120 */       return 1;
/* 103:    */     }
/* 104:124 */     return this.lex.compare(p1, p2);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public double[] max(double[] p, double[] r)
/* 108:    */   {
/* 109:129 */     if (compare(p, r) == 1) {
/* 110:129 */       return p;
/* 111:    */     }
/* 112:130 */     return r;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public double[] min(double[] p, double[] r)
/* 116:    */   {
/* 117:135 */     if (compare(p, r) == 1) {
/* 118:135 */       return r;
/* 119:    */     }
/* 120:136 */     return p;
/* 121:    */   }
/* 122:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.QuantaLexLS
 * JD-Core Version:    0.7.0.1
 */
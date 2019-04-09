/*   1:    */ package vpt.util.ordering;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import vpt.util.Tools;
/*   5:    */ 
/*   6:    */ public class QuantaLexLAB
/*   7:    */   extends AlgebraicOrdering
/*   8:    */ {
/*   9:    */   private double alpha;
/*  10: 22 */   private Lexico lex = new Lexico();
/*  11:    */   private double[] transformed;
/*  12:    */   
/*  13:    */   public QuantaLexLAB(double alpha)
/*  14:    */   {
/*  15: 32 */     if (alpha > 1.0D) {
/*  16: 32 */       this.alpha = alpha;
/*  17:    */     } else {
/*  18: 33 */       this.alpha = 1.0D;
/*  19:    */     }
/*  20: 36 */     alpha = Math.round(alpha);
/*  21:    */     
/*  22: 38 */     this.transformed = new double[256];
/*  23: 39 */     double value = 0.0D;
/*  24: 41 */     for (int z = 0; z < 256;)
/*  25:    */     {
/*  26: 42 */       int k = (int)Math.ceil(alpha * sigma(z) + 1.0E-008D);
/*  27: 44 */       for (int i = z; i < z + k; i++) {
/*  28: 45 */         this.transformed[i] = value;
/*  29:    */       }
/*  30: 47 */       value += 1.0D;
/*  31: 48 */       z += k;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   double sigma(int z)
/*  36:    */   {
/*  37: 54 */     double x = z / 255.0D;
/*  38: 56 */     if (x <= 0.5D) {
/*  39: 56 */       return (1.0D / (1.0D + Math.exp(-10.0D * (x - 0.25D))) - 0.07580000000000001D) / 0.84834D;
/*  40:    */     }
/*  41: 57 */     return (1.0D / (1.0D + Math.exp(10.0D * (x - 0.75D))) - 0.07580000000000001D) / 0.84834D;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double[] max(double[][] p)
/*  45:    */   {
/*  46: 62 */     double[] max = p[0];
/*  47: 64 */     for (int i = 1; i < p.length; i++) {
/*  48: 65 */       if (compare(max, p[i]) < 0) {
/*  49: 65 */         max = p[i];
/*  50:    */       }
/*  51:    */     }
/*  52: 68 */     return max;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public double[] min(double[][] p)
/*  56:    */   {
/*  57: 73 */     double[] min = p[0];
/*  58: 75 */     for (int i = 1; i < p.length; i++) {
/*  59: 76 */       if (compare(min, p[i]) > 0) {
/*  60: 76 */         min = p[i];
/*  61:    */       }
/*  62:    */     }
/*  63: 79 */     return min;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public double[] rank(double[][] p, int r)
/*  67:    */   {
/*  68: 84 */     Arrays.sort(p, this);
/*  69:    */     
/*  70: 86 */     return p[r];
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int compare(double[] p1, double[] p2)
/*  74:    */   {
/*  75:101 */     if (this.alpha > 1.0D)
/*  76:    */     {
/*  77:102 */       double dtmp1 = Math.round(255.0D * p1[0]);
/*  78:103 */       dtmp1 = this.transformed[((int)dtmp1)];
/*  79:    */       
/*  80:105 */       double dtmp2 = Math.round(255.0D * p2[0]);
/*  81:106 */       dtmp2 = this.transformed[((int)dtmp2)];
/*  82:109 */       if (Tools.cmpr(dtmp1, dtmp2) < 0) {
/*  83:109 */         return -1;
/*  84:    */       }
/*  85:110 */       if (Tools.cmpr(dtmp1, dtmp2) > 0) {
/*  86:110 */         return 1;
/*  87:    */       }
/*  88:    */     }
/*  89:    */     else
/*  90:    */     {
/*  91:112 */       if (Tools.cmpr(p1[0], p2[2]) < 0) {
/*  92:112 */         return -1;
/*  93:    */       }
/*  94:113 */       if (Tools.cmpr(p1[0], p2[0]) > 0) {
/*  95:113 */         return 1;
/*  96:    */       }
/*  97:    */     }
/*  98:117 */     if (Tools.cmpr(p1[1], p2[1]) < 0) {
/*  99:117 */       return -1;
/* 100:    */     }
/* 101:118 */     if (Tools.cmpr(p1[1], p2[1]) > 0) {
/* 102:118 */       return 1;
/* 103:    */     }
/* 104:121 */     if (Tools.cmpr(p1[2], p2[2]) < 0) {
/* 105:121 */       return -1;
/* 106:    */     }
/* 107:122 */     if (Tools.cmpr(p1[2], p2[2]) > 0) {
/* 108:122 */       return 1;
/* 109:    */     }
/* 110:125 */     if (Tools.cmpr(p1[0], p2[0]) < 0) {
/* 111:125 */       return -1;
/* 112:    */     }
/* 113:126 */     if (Tools.cmpr(p1[0], p2[0]) > 0) {
/* 114:126 */       return 1;
/* 115:    */     }
/* 116:128 */     return 0;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double[] max(double[] p, double[] r)
/* 120:    */   {
/* 121:133 */     if (compare(p, r) == 1) {
/* 122:133 */       return p;
/* 123:    */     }
/* 124:134 */     return r;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public double[] min(double[] p, double[] r)
/* 128:    */   {
/* 129:139 */     if (compare(p, r) == 1) {
/* 130:139 */       return r;
/* 131:    */     }
/* 132:140 */     return p;
/* 133:    */   }
/* 134:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.QuantaLexLAB
 * JD-Core Version:    0.7.0.1
 */
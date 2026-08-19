/*   1:    */ package vpt.util.ordering;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import vpt.util.Tools;
/*   6:    */ 
/*   7:    */ public class QuantaLexHLS
/*   8:    */   extends AlgebraicOrdering
/*   9:    */ {
/*  10:    */   private double alpha;
/*  11:    */   private double[] transformedL;
/*  12:    */   private double[] transformedS;
/*  13: 28 */   private Lexico lex = new Lexico();
/*  14:    */   
/*  15:    */   public QuantaLexHLS(double alpha)
/*  16:    */   {
/*  17: 35 */     if (alpha > 1.0D) {
/*  18: 35 */       this.alpha = alpha;
/*  19:    */     } else {
/*  20: 36 */       this.alpha = 1.0D;
/*  21:    */     }
/*  22: 39 */     alpha = Math.round(alpha);
/*  23:    */     
/*  24: 41 */     this.transformedL = new double[256];
/*  25: 42 */     double value = 0.0D;
/*  26: 44 */     for (int z = 0; z < 256;)
/*  27:    */     {
/*  28: 45 */       int k = (int)Math.ceil(alpha * doubleSigma(z) + 1.0E-008D);
/*  29: 47 */       for (int i = z; (i < z + k) && (i < 256); i++) {
/*  30: 48 */         this.transformedL[i] = value;
/*  31:    */       }
/*  32: 52 */       value += 1.0D;
/*  33: 53 */       z += k;
/*  34:    */     }
/*  35: 56 */     this.transformedS = new double[256];
/*  36: 57 */     value = 0.0D;
/*  37: 59 */     for (int z = 0; z < 256;)
/*  38:    */     {
/*  39: 60 */       int k = (int)Math.ceil(alpha * sigma(z) + 1.0E-008D);
/*  40: 62 */       for (int i = z; (i < z + k) && (i < 256); i++)
/*  41:    */       {
/*  42: 63 */         this.transformedS[i] = value;
/*  43: 64 */         System.out.println(value);
/*  44:    */       }
/*  45: 67 */       value += 1.0D;
/*  46: 68 */       z += k;
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   double doubleSigma(int z)
/*  51:    */   {
/*  52: 77 */     double x = z / 255.0D;
/*  53: 79 */     if (x <= 0.5D) {
/*  54: 79 */       return (1.0D / (1.0D + Math.exp(-10.0D * (x - 0.25D))) - 0.07580000000000001D) / 0.84834D;
/*  55:    */     }
/*  56: 80 */     return (1.0D / (1.0D + Math.exp(10.0D * (x - 0.75D))) - 0.07580000000000001D) / 0.84834D;
/*  57:    */   }
/*  58:    */   
/*  59:    */   double sigma(int z)
/*  60:    */   {
/*  61: 89 */     double x = z / 255.0D;
/*  62:    */     
/*  63: 91 */     return 1.0D / (1.0D + Math.exp(-10.0D * (x - 0.5D)));
/*  64:    */   }
/*  65:    */   
/*  66:    */   public double[] max(double[][] p)
/*  67:    */   {
/*  68: 96 */     double[] max = p[0];
/*  69: 98 */     for (int i = 1; i < p.length; i++) {
/*  70: 99 */       if (compare(max, p[i]) < 0) {
/*  71: 99 */         max = p[i];
/*  72:    */       }
/*  73:    */     }
/*  74:102 */     return max;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public double[] min(double[][] p)
/*  78:    */   {
/*  79:107 */     double[] min = p[0];
/*  80:109 */     for (int i = 1; i < p.length; i++) {
/*  81:110 */       if (compare(min, p[i]) > 0) {
/*  82:110 */         min = p[i];
/*  83:    */       }
/*  84:    */     }
/*  85:113 */     return min;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public double[] rank(double[][] p, int r)
/*  89:    */   {
/*  90:118 */     Arrays.sort(p, this);
/*  91:    */     
/*  92:120 */     return p[r];
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int compare(double[] p1, double[] p2)
/*  96:    */   {
/*  97:135 */     if (this.alpha > 1.0D)
/*  98:    */     {
/*  99:136 */       double dtmp1 = Math.round(255.0D * p1[2]);
/* 100:137 */       dtmp1 = this.transformedL[((int)dtmp1)];
/* 101:    */       
/* 102:139 */       double dtmp2 = Math.round(255.0D * p2[2]);
/* 103:140 */       dtmp2 = this.transformedL[((int)dtmp2)];
/* 104:143 */       if (Tools.cmpr(dtmp1, dtmp2) < 0) {
/* 105:143 */         return -1;
/* 106:    */       }
/* 107:144 */       if (Tools.cmpr(dtmp1, dtmp2) > 0) {
/* 108:144 */         return 1;
/* 109:    */       }
/* 110:    */     }
/* 111:    */     else
/* 112:    */     {
/* 113:146 */       if (Tools.cmpr(p1[2], p2[2]) < 0) {
/* 114:146 */         return -1;
/* 115:    */       }
/* 116:147 */       if (Tools.cmpr(p1[2], p2[2]) > 0) {
/* 117:147 */         return 1;
/* 118:    */       }
/* 119:    */     }
/* 120:151 */     if (this.alpha > 1.0D)
/* 121:    */     {
/* 122:152 */       double dtmp1 = Math.round(255.0D * p1[1]);
/* 123:153 */       dtmp1 = this.transformedS[((int)dtmp1)];
/* 124:    */       
/* 125:155 */       double dtmp2 = Math.round(255.0D * p2[1]);
/* 126:156 */       dtmp2 = this.transformedS[((int)dtmp2)];
/* 127:159 */       if (Tools.cmpr(dtmp1, dtmp2) < 0) {
/* 128:159 */         return -1;
/* 129:    */       }
/* 130:160 */       if (Tools.cmpr(dtmp1, dtmp2) > 0) {
/* 131:160 */         return 1;
/* 132:    */       }
/* 133:    */     }
/* 134:    */     else
/* 135:    */     {
/* 136:162 */       if (Tools.cmpr(p1[1], p2[1]) < 0) {
/* 137:162 */         return -1;
/* 138:    */       }
/* 139:163 */       if (Tools.cmpr(p1[1], p2[1]) > 0) {
/* 140:163 */         return 1;
/* 141:    */       }
/* 142:    */     }
/* 143:168 */     double refHue = 0.0D;
/* 144:    */     
/* 145:170 */     double abs = Math.abs(refHue - p1[0]);
/* 146:    */     double dtmp1;
/* 147:    */     double dtmp1;
/* 148:172 */     if (abs <= 0.5D) {
/* 149:172 */       dtmp1 = abs;
/* 150:    */     } else {
/* 151:173 */       dtmp1 = 1.0D - abs;
/* 152:    */     }
/* 153:175 */     abs = Math.abs(refHue - p2[0]);
/* 154:    */     double dtmp2;
/* 155:    */     double dtmp2;
/* 156:177 */     if (abs <= 0.5D) {
/* 157:177 */       dtmp2 = abs;
/* 158:    */     } else {
/* 159:178 */       dtmp2 = 1.0D - abs;
/* 160:    */     }
/* 161:180 */     if (dtmp1 < dtmp2) {
/* 162:180 */       return 1;
/* 163:    */     }
/* 164:181 */     if (dtmp1 > dtmp2) {
/* 165:181 */       return -1;
/* 166:    */     }
/* 167:185 */     return this.lex.compare(p1, p2);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public double[] max(double[] p, double[] r)
/* 171:    */   {
/* 172:190 */     if (compare(p, r) == 1) {
/* 173:190 */       return p;
/* 174:    */     }
/* 175:191 */     return r;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public double[] min(double[] p, double[] r)
/* 179:    */   {
/* 180:196 */     if (compare(p, r) == 1) {
/* 181:196 */       return r;
/* 182:    */     }
/* 183:197 */     return p;
/* 184:    */   }
/* 185:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.QuantaLexHLS
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.util.ordering;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ public class AlphaTrimmedLexico
/*   6:    */   implements Ordering
/*   7:    */ {
/*   8:    */   private double alpha;
/*   9: 17 */   private double[] alphaV = null;
/*  10:    */   
/*  11:    */   public AlphaTrimmedLexico(double alpha)
/*  12:    */   {
/*  13: 25 */     if (alpha > 0.0D) {
/*  14: 25 */       this.alpha = alpha;
/*  15:    */     } else {
/*  16: 26 */       this.alpha = 0.01D;
/*  17:    */     }
/*  18:    */   }
/*  19:    */   
/*  20:    */   public AlphaTrimmedLexico(double[] alpha)
/*  21:    */   {
/*  22: 35 */     this.alphaV = new double[alpha.length];
/*  23: 37 */     for (int i = 0; i < alpha.length; i++) {
/*  24: 38 */       if (alpha[i] <= 0.0D) {
/*  25: 38 */         this.alphaV[i] = 0.01D;
/*  26:    */       } else {
/*  27: 39 */         this.alphaV[i] = alpha[i];
/*  28:    */       }
/*  29:    */     }
/*  30:    */   }
/*  31:    */   
/*  32:    */   private class IndexedDouble
/*  33:    */     implements Comparable
/*  34:    */   {
/*  35:    */     double d;
/*  36:    */     int i;
/*  37:    */     
/*  38:    */     IndexedDouble(double d, int i)
/*  39:    */     {
/*  40: 50 */       this.d = d;this.i = i;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public int compareTo(Object o)
/*  44:    */     {
/*  45: 54 */       IndexedDouble d = (IndexedDouble)o;
/*  46: 56 */       if (this.d < d.d) {
/*  47: 56 */         return -1;
/*  48:    */       }
/*  49: 57 */       if (this.d > d.d) {
/*  50: 57 */         return 1;
/*  51:    */       }
/*  52: 58 */       return 0;
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   private class ReverseIndexedDouble
/*  57:    */     implements Comparable
/*  58:    */   {
/*  59:    */     double d;
/*  60:    */     int i;
/*  61:    */     
/*  62:    */     ReverseIndexedDouble(double d, int i)
/*  63:    */     {
/*  64: 69 */       this.d = d;this.i = i;
/*  65:    */     }
/*  66:    */     
/*  67:    */     public int compareTo(Object o)
/*  68:    */     {
/*  69: 73 */       ReverseIndexedDouble d = (ReverseIndexedDouble)o;
/*  70: 75 */       if (this.d < d.d) {
/*  71: 75 */         return 1;
/*  72:    */       }
/*  73: 76 */       if (this.d > d.d) {
/*  74: 76 */         return -1;
/*  75:    */       }
/*  76: 77 */       return 0;
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public double[] min(double[][] p)
/*  81:    */   {
/*  82: 87 */     if (this.alphaV == null)
/*  83:    */     {
/*  84: 88 */       this.alphaV = new double[p[0].length];
/*  85: 89 */       for (int i = 0; i < this.alphaV.length; i++) {
/*  86: 90 */         this.alphaV[i] = this.alpha;
/*  87:    */       }
/*  88:    */     }
/*  89: 93 */     int dim = p[0].length;
/*  90: 95 */     for (int d = 0; d < dim - 1; d++)
/*  91:    */     {
/*  92: 97 */       IndexedDouble[] ind = new IndexedDouble[p.length];
/*  93:100 */       for (int i = 0; i < ind.length; i++) {
/*  94:101 */         ind[i] = new IndexedDouble(p[i][d], i);
/*  95:    */       }
/*  96:104 */       Arrays.sort(ind);
/*  97:    */       
/*  98:    */ 
/*  99:107 */       int trimSize = (int)Math.ceil(p.length * this.alphaV[d]);
/* 100:    */       
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:112 */       int tmp = trimSize;
/* 105:113 */       for (int i = tmp; i < p.length; i++)
/* 106:    */       {
/* 107:114 */         if (p[i][d] != p[(tmp - 1)][d]) {
/* 108:    */           break;
/* 109:    */         }
/* 110:114 */         trimSize++;
/* 111:    */       }
/* 112:119 */       if (trimSize == 1) {
/* 113:119 */         return p[ind[0].i];
/* 114:    */       }
/* 115:122 */       double[][] tmp2 = new double[trimSize][dim];
/* 116:124 */       for (int i = 0; i < trimSize; i++) {
/* 117:125 */         tmp2[i] = p[ind[i].i];
/* 118:    */       }
/* 119:127 */       p = tmp2;
/* 120:    */     }
/* 121:132 */     IndexedDouble[] ind = new IndexedDouble[p.length];
/* 122:135 */     for (int i = 0; i < ind.length; i++) {
/* 123:136 */       ind[i] = new IndexedDouble(p[i][(dim - 1)], i);
/* 124:    */     }
/* 125:139 */     Arrays.sort(ind);
/* 126:    */     
/* 127:141 */     return p[ind[0].i];
/* 128:    */   }
/* 129:    */   
/* 130:    */   public double[] max(double[][] p)
/* 131:    */   {
/* 132:150 */     if (this.alphaV == null)
/* 133:    */     {
/* 134:151 */       this.alphaV = new double[p[0].length];
/* 135:152 */       for (int i = 0; i < this.alphaV.length; i++) {
/* 136:153 */         this.alphaV[i] = this.alpha;
/* 137:    */       }
/* 138:    */     }
/* 139:156 */     int dim = p[0].length;
/* 140:158 */     for (int d = 0; d < dim - 1; d++)
/* 141:    */     {
/* 142:160 */       ReverseIndexedDouble[] ind = new ReverseIndexedDouble[p.length];
/* 143:163 */       for (int i = 0; i < ind.length; i++) {
/* 144:164 */         ind[i] = new ReverseIndexedDouble(p[i][d], i);
/* 145:    */       }
/* 146:167 */       Arrays.sort(ind);
/* 147:    */       
/* 148:    */ 
/* 149:    */ 
/* 150:    */ 
/* 151:172 */       int trimSize = (int)Math.ceil(p.length * this.alphaV[d]);
/* 152:    */       
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:177 */       int tmp = trimSize;
/* 157:178 */       for (int i = tmp; i < p.length; i++)
/* 158:    */       {
/* 159:179 */         if (p[i][d] != p[(tmp - 1)][d]) {
/* 160:    */           break;
/* 161:    */         }
/* 162:179 */         trimSize++;
/* 163:    */       }
/* 164:184 */       if (trimSize == 1) {
/* 165:184 */         return p[ind[0].i];
/* 166:    */       }
/* 167:187 */       double[][] tmp2 = new double[trimSize][dim];
/* 168:189 */       for (int i = 0; i < trimSize; i++) {
/* 169:190 */         tmp2[i] = p[ind[i].i];
/* 170:    */       }
/* 171:192 */       p = tmp2;
/* 172:    */     }
/* 173:197 */     ReverseIndexedDouble[] ind = new ReverseIndexedDouble[p.length];
/* 174:200 */     for (int i = 0; i < ind.length; i++) {
/* 175:201 */       ind[i] = new ReverseIndexedDouble(p[i][(dim - 1)], i);
/* 176:    */     }
/* 177:204 */     Arrays.sort(ind);
/* 178:    */     
/* 179:206 */     return p[ind[0].i];
/* 180:    */   }
/* 181:    */   
/* 182:    */   public double[] rank(double[][] p, int r)
/* 183:    */   {
/* 184:211 */     int dim = p[0].length;
/* 185:213 */     for (int d = 0; d < dim - 1; d++)
/* 186:    */     {
/* 187:215 */       IndexedDouble[] ind = new IndexedDouble[p.length];
/* 188:218 */       for (int i = 0; i < ind.length; i++) {
/* 189:219 */         ind[i] = new IndexedDouble(p[i][d], i);
/* 190:    */       }
/* 191:222 */       Arrays.sort(ind);
/* 192:    */       
/* 193:    */ 
/* 194:    */ 
/* 195:226 */       int trimSize = (int)Math.floor(p.length * this.alpha);
/* 196:228 */       if (trimSize > 0)
/* 197:    */       {
/* 198:230 */         int lowerIndex = trimSize;
/* 199:231 */         int upperIndex = p.length - trimSize - 1;
/* 200:234 */         if (upperIndex < lowerIndex)
/* 201:    */         {
/* 202:235 */           int tmp2 = upperIndex;
/* 203:    */           
/* 204:237 */           upperIndex = lowerIndex;
/* 205:238 */           lowerIndex = tmp2;
/* 206:    */         }
/* 207:241 */         int total = upperIndex - lowerIndex + 1;
/* 208:244 */         if (upperIndex == lowerIndex) {
/* 209:244 */           return p[ind[upperIndex].i];
/* 210:    */         }
/* 211:247 */         double[][] tmp2 = new double[total][dim];
/* 212:250 */         for (int i = lowerIndex; i <= upperIndex; i++) {
/* 213:251 */           tmp2[(i - lowerIndex)] = p[ind[i].i];
/* 214:    */         }
/* 215:253 */         p = tmp2;
/* 216:    */       }
/* 217:    */     }
/* 218:259 */     IndexedDouble[] ind = new IndexedDouble[p.length];
/* 219:262 */     for (int i = 0; i < ind.length; i++) {
/* 220:263 */       ind[i] = new IndexedDouble(p[i][(dim - 1)], i);
/* 221:    */     }
/* 222:266 */     Arrays.sort(ind);
/* 223:    */     
/* 224:    */ 
/* 225:    */ 
/* 226:270 */     return p[ind[(p.length / 2)].i];
/* 227:    */   }
/* 228:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.AlphaTrimmedLexico
 * JD-Core Version:    0.7.0.1
 */
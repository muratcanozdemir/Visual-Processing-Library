/*   1:    */ package vpt.util;
/*   2:    */ 
/*   3:    */ public class Complex2DArray
/*   4:    */ {
/*   5:    */   public int width;
/*   6:    */   public int height;
/*   7:    */   public int size;
/*   8:    */   public ComplexNumber[][] values;
/*   9:    */   
/*  10:    */   public Complex2DArray(Complex2DArray a)
/*  11:    */   {
/*  12: 28 */     this.width = a.width;
/*  13: 29 */     this.height = a.height;
/*  14: 30 */     this.size = a.size;
/*  15: 31 */     this.values = new ComplexNumber[this.size][this.size];
/*  16: 32 */     for (int j = 0; j < this.size; j++) {
/*  17: 33 */       for (int i = 0; i < this.size; i++)
/*  18:    */       {
/*  19: 34 */         ComplexNumber c = new ComplexNumber(a.values[i][j]);
/*  20: 35 */         this.values[i][j] = c;
/*  21:    */       }
/*  22:    */     }
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Complex2DArray(int w, int h)
/*  26:    */   {
/*  27: 48 */     this.width = w;
/*  28: 49 */     this.height = h;
/*  29: 50 */     int n = 0;
/*  30: 51 */     while (Math.pow(2.0D, n) < Math.max(w, h)) {
/*  31: 52 */       n++;
/*  32:    */     }
/*  33: 54 */     this.size = ((int)Math.pow(2.0D, n));
/*  34: 55 */     this.values = new ComplexNumber[this.size][this.size];
/*  35: 56 */     for (int j = 0; j < this.size; j++) {
/*  36: 57 */       for (int i = 0; i < this.size; i++) {
/*  37: 58 */         this.values[i][j] = new ComplexNumber(0.0D, 0.0D);
/*  38:    */       }
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Complex2DArray(int s)
/*  43:    */   {
/*  44: 70 */     this.width = s;
/*  45: 71 */     this.height = s;
/*  46: 72 */     int n = 0;
/*  47: 73 */     while (Math.pow(2.0D, n) < s) {
/*  48: 74 */       n++;
/*  49:    */     }
/*  50: 76 */     this.size = ((int)Math.pow(2.0D, n));
/*  51: 77 */     this.values = new ComplexNumber[this.size][this.size];
/*  52: 79 */     for (int j = 0; j < this.size; j++) {
/*  53: 80 */       for (int i = 0; i < this.size; i++) {
/*  54: 81 */         this.values[i][j] = new ComplexNumber(0.0D, 0.0D);
/*  55:    */       }
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Complex2DArray(double[] p, int w, int h)
/*  60:    */   {
/*  61: 96 */     this.width = w;
/*  62: 97 */     this.height = h;
/*  63: 98 */     int n = 0;
/*  64:100 */     while (Math.pow(2.0D, n) < Math.max(w, h)) {
/*  65:101 */       n++;
/*  66:    */     }
/*  67:104 */     this.size = ((int)Math.pow(2.0D, n));
/*  68:105 */     this.values = new ComplexNumber[this.size][this.size];
/*  69:107 */     for (int j = 0; j < this.size; j++) {
/*  70:108 */       for (int i = 0; i < this.size; i++) {
/*  71:109 */         this.values[i][j] = new ComplexNumber(0.0D, 0.0D);
/*  72:    */       }
/*  73:    */     }
/*  74:113 */     for (int j = 0; j < h; j++) {
/*  75:114 */       for (int i = 0; i < w; i++) {
/*  76:115 */         this.values[i][j] = new ComplexNumber(p[(i + j * w)], 0.0D);
/*  77:    */       }
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Complex2DArray(int[][] v, int w, int h)
/*  82:    */   {
/*  83:129 */     this.width = w;
/*  84:130 */     this.height = h;
/*  85:131 */     int n = 0;
/*  86:132 */     while (Math.pow(2.0D, n) < Math.max(w, h)) {
/*  87:133 */       n++;
/*  88:    */     }
/*  89:135 */     this.size = ((int)Math.pow(2.0D, n));
/*  90:136 */     this.values = new ComplexNumber[this.size][this.size];
/*  91:138 */     for (int j = 0; j < this.size; j++) {
/*  92:139 */       for (int i = 0; i < this.size; i++) {
/*  93:140 */         this.values[i][j] = new ComplexNumber(0.0D, 0.0D);
/*  94:    */       }
/*  95:    */     }
/*  96:144 */     for (int j = 0; j < h; j++) {
/*  97:145 */       for (int i = 0; i < w; i++) {
/*  98:146 */         this.values[i][j] = new ComplexNumber(v[i][j], 0.0D);
/*  99:    */       }
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Complex2DArray(ComplexNumber[][] v, int w, int h)
/* 104:    */   {
/* 105:159 */     this.width = w;
/* 106:160 */     this.height = h;
/* 107:161 */     int n = 0;
/* 108:163 */     while (Math.pow(2.0D, n) < Math.max(w, h)) {
/* 109:164 */       n++;
/* 110:    */     }
/* 111:166 */     this.size = ((int)Math.pow(2.0D, n));
/* 112:167 */     this.values = new ComplexNumber[this.size][this.size];
/* 113:169 */     for (int j = 0; j < this.size; j++) {
/* 114:170 */       for (int i = 0; i < this.size; i++) {
/* 115:171 */         this.values[i][j] = new ComplexNumber(0.0D, 0.0D);
/* 116:    */       }
/* 117:    */     }
/* 118:173 */     for (int j = 0; j < h; j++) {
/* 119:174 */       for (int i = 0; i < w; i++) {
/* 120:175 */         this.values[i][j] = new ComplexNumber(v[i][j]);
/* 121:    */       }
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public ComplexNumber[] getColumn(int n)
/* 126:    */   {
/* 127:179 */     ComplexNumber[] c = new ComplexNumber[this.size];
/* 128:181 */     for (int i = 0; i < this.size; i++) {
/* 129:182 */       c[i] = new ComplexNumber(this.values[n][i]);
/* 130:    */     }
/* 131:184 */     return c;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void putColumn(int n, ComplexNumber[] c)
/* 135:    */   {
/* 136:189 */     for (int i = 0; i < this.size; i++) {
/* 137:190 */       this.values[n][i] = new ComplexNumber(c[i]);
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void putRow(int n, ComplexNumber[] c)
/* 142:    */   {
/* 143:194 */     for (int i = 0; i < this.size; i++) {
/* 144:195 */       this.values[i][n] = new ComplexNumber(c[i]);
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public ComplexNumber[] getRow(int n)
/* 149:    */   {
/* 150:199 */     ComplexNumber[] r = new ComplexNumber[this.size];
/* 151:201 */     for (int i = 0; i < this.size; i++) {
/* 152:202 */       r[i] = new ComplexNumber(this.values[i][n]);
/* 153:    */     }
/* 154:204 */     return r;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public double[][] DCToCentre(double[][] input)
/* 158:    */   {
/* 159:216 */     double[][] output = new double[this.width][this.height];
/* 160:217 */     int x = this.width / 2;
/* 161:218 */     int y = this.height / 2;
/* 162:220 */     for (int j = 0; j < this.height; j++) {
/* 163:221 */       for (int i = 0; i < this.width; i++)
/* 164:    */       {
/* 165:222 */         int i2 = i + x;
/* 166:223 */         int j2 = j + y;
/* 167:224 */         if (i2 >= this.width) {
/* 168:224 */           i2 %= this.width;
/* 169:    */         }
/* 170:225 */         if (j2 >= this.height) {
/* 171:225 */           j2 %= this.height;
/* 172:    */         }
/* 173:226 */         output[i][j] = input[i2][j2];
/* 174:    */       }
/* 175:    */     }
/* 176:229 */     return output;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public double[][] DCToTopLeft(double[][] input)
/* 180:    */   {
/* 181:240 */     double[][] output = new double[this.width][this.height];
/* 182:    */     
/* 183:242 */     int x = this.width / 2;
/* 184:243 */     for (int j = 0; j < this.height; j++) {
/* 185:244 */       for (int i = 0; i < this.width; i++)
/* 186:    */       {
/* 187:245 */         int i2 = i + x;
/* 188:246 */         int j2 = j + x;
/* 189:247 */         if (i2 >= this.width) {
/* 190:247 */           i2 %= this.width;
/* 191:    */         }
/* 192:248 */         if (j2 >= this.height) {
/* 193:248 */           j2 %= this.height;
/* 194:    */         }
/* 195:249 */         output[i][j] = input[i2][j2];
/* 196:    */       }
/* 197:    */     }
/* 198:252 */     return output;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public ComplexNumber[][] DCToTopLeft(ComplexNumber[][] input)
/* 202:    */   {
/* 203:256 */     ComplexNumber[][] output = new ComplexNumber[this.width][this.height];
/* 204:    */     
/* 205:258 */     int x = this.width / 2;
/* 206:259 */     for (int j = 0; j < this.height; j++) {
/* 207:260 */       for (int i = 0; i < this.width; i++)
/* 208:    */       {
/* 209:261 */         int i2 = i + x;
/* 210:262 */         int j2 = j + x;
/* 211:263 */         if (i2 >= this.width) {
/* 212:263 */           i2 %= this.width;
/* 213:    */         }
/* 214:264 */         if (j2 >= this.height) {
/* 215:264 */           j2 %= this.height;
/* 216:    */         }
/* 217:265 */         output[i][j] = input[i2][j2];
/* 218:    */       }
/* 219:    */     }
/* 220:268 */     return output;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public double[] DCToCentre(double[] input)
/* 224:    */   {
/* 225:280 */     double[][] input2 = new double[this.width][this.height];
/* 226:281 */     double[][] output2 = new double[this.width][this.height];
/* 227:282 */     double[] output = new double[this.width * this.height];
/* 228:283 */     for (int j = 0; j < this.height; j++) {
/* 229:284 */       for (int i = 0; i < this.width; i++) {
/* 230:285 */         input2[i][j] = input[(j * this.width + i)];
/* 231:    */       }
/* 232:    */     }
/* 233:288 */     int x = this.width / 2;
/* 234:289 */     int y = this.height / 2;
/* 235:291 */     for (int j = 0; j < this.height; j++) {
/* 236:292 */       for (int i = 0; i < this.width; i++)
/* 237:    */       {
/* 238:293 */         int i2 = i + x;
/* 239:294 */         int j2 = j + y;
/* 240:295 */         if (i2 >= this.width) {
/* 241:295 */           i2 %= this.width;
/* 242:    */         }
/* 243:296 */         if (j2 >= this.height) {
/* 244:296 */           j2 %= this.height;
/* 245:    */         }
/* 246:297 */         output2[i][j] = input2[i2][j2];
/* 247:    */       }
/* 248:    */     }
/* 249:300 */     for (int j = 0; j < this.height; j++) {
/* 250:301 */       for (int i = 0; i < this.width; i++) {
/* 251:302 */         output[(j * this.width + i)] = output2[i][j];
/* 252:    */       }
/* 253:    */     }
/* 254:305 */     return output;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public double[][] getReals()
/* 258:    */   {
/* 259:309 */     double[][] reals = new double[this.size][this.size];
/* 260:311 */     for (int i = 0; i < this.size; i++) {
/* 261:312 */       for (int j = 0; j < this.size; j++) {
/* 262:313 */         reals[i][j] = this.values[i][j].real;
/* 263:    */       }
/* 264:    */     }
/* 265:317 */     return reals;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public double[][] getImags()
/* 269:    */   {
/* 270:321 */     double[][] imags = new double[this.size][this.size];
/* 271:323 */     for (int i = 0; i < this.size; i++) {
/* 272:324 */       for (int j = 0; j < this.size; j++) {
/* 273:325 */         imags[i][j] = this.values[i][j].imag;
/* 274:    */       }
/* 275:    */     }
/* 276:329 */     return imags;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public double[][] getMagnitudes()
/* 280:    */   {
/* 281:333 */     double[][] mags = new double[this.size][this.size];
/* 282:335 */     for (int i = 0; i < this.size; i++) {
/* 283:336 */       for (int j = 0; j < this.size; j++) {
/* 284:337 */         mags[i][j] = this.values[i][j].magnitude();
/* 285:    */       }
/* 286:    */     }
/* 287:341 */     return mags;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public double[][] getPhases()
/* 291:    */   {
/* 292:345 */     double[][] phases = new double[this.size][this.size];
/* 293:347 */     for (int i = 0; i < this.size; i++) {
/* 294:348 */       for (int j = 0; j < this.size; j++) {
/* 295:349 */         phases[i][j] = this.values[i][j].phaseAngle();
/* 296:    */       }
/* 297:    */     }
/* 298:353 */     return phases;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public double[] getPhase()
/* 302:    */   {
/* 303:363 */     double[] output = new double[this.width * this.height];
/* 304:364 */     for (int j = 0; j < this.height; j++) {
/* 305:365 */       for (int i = 0; i < this.width; i++) {
/* 306:366 */         output[(j * this.width + i)] = this.values[i][j].phaseAngle();
/* 307:    */       }
/* 308:    */     }
/* 309:369 */     return output;
/* 310:    */   }
/* 311:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.Complex2DArray
 * JD-Core Version:    0.7.0.1
 */
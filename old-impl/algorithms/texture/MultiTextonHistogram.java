/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ 
/*   7:    */ public class MultiTextonHistogram
/*   8:    */   extends Algorithm
/*   9:    */ {
/*  10: 18 */   public double[] output = null;
/*  11: 19 */   public Image input = null;
/*  12:    */   
/*  13:    */   public MultiTextonHistogram()
/*  14:    */   {
/*  15: 22 */     this.inputFields = "input";
/*  16: 23 */     this.outputFields = "output";
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void execute()
/*  20:    */     throws GlobalException
/*  21:    */   {
/*  22: 28 */     if (this.input.getCDim() != 3) {
/*  23: 28 */       throw new GlobalException("Only color data please");
/*  24:    */     }
/*  25: 30 */     int Width = this.input.getXDim();
/*  26: 31 */     int Height = this.input.getYDim();
/*  27:    */     
/*  28: 33 */     int CSA = 64;
/*  29: 34 */     int CSB = 18;
/*  30:    */     
/*  31: 36 */     int[][][] RGB = new int[3][Width][Height];
/*  32: 37 */     double[][] theta = new double[Width][Height];
/*  33: 41 */     for (int i = 1; i <= Width - 2; i++) {
/*  34: 42 */       for (int j = 1; j <= Height - 2; j++)
/*  35:    */       {
/*  36: 43 */         int[] p = this.input.getVXYByte(i, j);
/*  37: 44 */         RGB[0][i][j] = p[0];
/*  38: 45 */         RGB[1][i][j] = p[1];
/*  39: 46 */         RGB[2][i][j] = p[2];
/*  40:    */       }
/*  41:    */     }
/*  42: 51 */     int[][] Ori = new int[Width][Height];
/*  43: 52 */     double gxx = 0.0D;double gyy = 0.0D;double gxy = 0.0D;
/*  44: 53 */     double rh = 0.0D;double gh = 0.0D;double bh = 0.0D;
/*  45: 54 */     double rv = 0.0D;double gv = 0.0D;double bv = 0.0D;
/*  46: 56 */     for (int i = 1; i <= Width - 2; i++) {
/*  47: 57 */       for (int j = 1; j <= Height - 2; j++)
/*  48:    */       {
/*  49: 58 */         rh = RGB[0][(i - 1)][(j + 1)] + 2 * RGB[0][i][(j + 1)] + RGB[0][(i + 1)][(j + 1)] - (RGB[0][(i - 1)][(j - 1)] + 2 * RGB[0][i][(j - 1)] + RGB[0][(i + 1)][(j - 1)]);
/*  50: 59 */         gh = RGB[1][(i - 1)][(j + 1)] + 2 * RGB[1][i][(j + 1)] + RGB[1][(i + 1)][(j + 1)] - (RGB[1][(i - 1)][(j - 1)] + 2 * RGB[1][i][(j - 1)] + RGB[1][(i + 1)][(j - 1)]);
/*  51: 60 */         bh = RGB[2][(i - 1)][(j + 1)] + 2 * RGB[2][i][(j + 1)] + RGB[2][(i + 1)][(j + 1)] - (RGB[2][(i - 1)][(j - 1)] + 2 * RGB[2][i][(j - 1)] + RGB[2][(i + 1)][(j - 1)]);
/*  52:    */         
/*  53: 62 */         rv = RGB[0][(i + 1)][(j - 1)] + 2 * RGB[0][(i + 1)][j] + RGB[0][(i + 1)][(j + 1)] - (RGB[0][(i - 1)][(j - 1)] + 2 * RGB[0][(i - 1)][j] + RGB[0][(i - 1)][(j + 1)]);
/*  54: 63 */         gv = RGB[1][(i + 1)][(j - 1)] + 2 * RGB[1][(i + 1)][j] + RGB[1][(i + 1)][(j + 1)] - (RGB[1][(i - 1)][(j - 1)] + 2 * RGB[1][(i - 1)][j] + RGB[1][(i - 1)][(j + 1)]);
/*  55: 64 */         bv = RGB[2][(i + 1)][(j - 1)] + 2 * RGB[2][(i + 1)][j] + RGB[2][(i + 1)][(j + 1)] - (RGB[2][(i - 1)][(j - 1)] + 2 * RGB[2][(i - 1)][j] + RGB[2][(i - 1)][(j + 1)]);
/*  56:    */         
/*  57: 66 */         gxx = Math.sqrt(rh * rh + gh * gh + bh * bh);
/*  58: 67 */         gyy = Math.sqrt(rv * rv + gv * gv + bv * bv);
/*  59: 68 */         gxy = rh * rv + gh * gv + bh * bv;
/*  60:    */         
/*  61: 70 */         theta[i][j] = (Math.acos(gxy / (gxx * gyy + 0.0001D)) * 180.0D / 3.141592653589793D);
/*  62:    */       }
/*  63:    */     }
/*  64: 74 */     int[][] ImageX = new int[Width][Height];
/*  65: 75 */     int R = 0;int G = 0;int B = 0;
/*  66: 76 */     int SI = 0;int VI = 0;int HI = 0;
/*  67: 79 */     for (int i = 0; i <= Width - 1; i++) {
/*  68: 80 */       for (int j = 0; j <= Height - 1; j++)
/*  69:    */       {
/*  70: 82 */         R = this.input.getXYCByte(i, j, 0);
/*  71: 83 */         G = this.input.getXYCByte(i, j, 1);
/*  72: 84 */         B = this.input.getXYCByte(i, j, 2);
/*  73: 86 */         if ((R >= 0) && (R <= 64)) {
/*  74: 86 */           VI = 0;
/*  75:    */         }
/*  76: 87 */         if ((R >= 65) && (R <= 128)) {
/*  77: 87 */           VI = 1;
/*  78:    */         }
/*  79: 88 */         if ((R >= 129) && (R <= 192)) {
/*  80: 88 */           VI = 2;
/*  81:    */         }
/*  82: 89 */         if ((R >= 193) && (R <= 255)) {
/*  83: 89 */           VI = 3;
/*  84:    */         }
/*  85: 91 */         if ((G >= 0) && (G <= 64)) {
/*  86: 91 */           SI = 0;
/*  87:    */         }
/*  88: 92 */         if ((G >= 65) && (G <= 128)) {
/*  89: 92 */           SI = 1;
/*  90:    */         }
/*  91: 93 */         if ((G >= 129) && (G <= 192)) {
/*  92: 93 */           SI = 2;
/*  93:    */         }
/*  94: 94 */         if ((G >= 193) && (G <= 255)) {
/*  95: 94 */           SI = 3;
/*  96:    */         }
/*  97: 96 */         if ((B >= 0) && (B <= 64)) {
/*  98: 96 */           HI = 0;
/*  99:    */         }
/* 100: 97 */         if ((B >= 65) && (B <= 128)) {
/* 101: 97 */           HI = 1;
/* 102:    */         }
/* 103: 98 */         if ((B >= 129) && (B <= 192)) {
/* 104: 98 */           HI = 2;
/* 105:    */         }
/* 106: 99 */         if ((B >= 193) && (B <= 255)) {
/* 107: 99 */           HI = 3;
/* 108:    */         }
/* 109:102 */         ImageX[i][j] = (16 * VI + 4 * SI + HI);
/* 110:    */       }
/* 111:    */     }
/* 112:108 */     for (int i = 0; i <= Width - 1; i++) {
/* 113:109 */       for (int j = 0; j <= Height - 1; j++)
/* 114:    */       {
/* 115:111 */         Ori[i][j] = ((int)Math.round(theta[i][j] * CSB / 180.0D));
/* 116:112 */         if (Ori[i][j] >= CSB - 1) {
/* 117:112 */           Ori[i][j] = (CSB - 1);
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:117 */     int[][] Texton = new int[Width][Height];
/* 122:119 */     for (int i = 0; i <= Width / 2 - 1; i++) {
/* 123:120 */       for (int j = 0; j <= Height / 2 - 1; j++)
/* 124:    */       {
/* 125:122 */         if (ImageX[(2 * i)][(2 * j)] == ImageX[(2 * i + 1)][(2 * j + 1)])
/* 126:    */         {
/* 127:123 */           Texton[(2 * i)][(2 * j)] = ImageX[(2 * i)][(2 * j)];
/* 128:124 */           Texton[(2 * i + 1)][(2 * j)] = ImageX[(2 * i + 1)][(2 * j)];
/* 129:125 */           Texton[(2 * i)][(2 * j + 1)] = ImageX[(2 * i)][(2 * j + 1)];
/* 130:126 */           Texton[(2 * i + 1)][(2 * j + 1)] = ImageX[(2 * i + 1)][(2 * j + 1)];
/* 131:    */         }
/* 132:129 */         if (ImageX[(2 * i)][(2 * j + 1)] == ImageX[(2 * i + 1)][(2 * j)])
/* 133:    */         {
/* 134:130 */           Texton[(2 * i)][(2 * j)] = ImageX[(2 * i)][(2 * j)];
/* 135:131 */           Texton[(2 * i + 1)][(2 * j)] = ImageX[(2 * i + 1)][(2 * j)];
/* 136:132 */           Texton[(2 * i)][(2 * j + 1)] = ImageX[(2 * i)][(2 * j + 1)];
/* 137:133 */           Texton[(2 * i + 1)][(2 * j + 1)] = ImageX[(2 * i + 1)][(2 * j + 1)];
/* 138:    */         }
/* 139:136 */         if (ImageX[(2 * i)][(2 * j)] == ImageX[(2 * i + 1)][(2 * j)])
/* 140:    */         {
/* 141:137 */           Texton[(2 * i)][(2 * j)] = ImageX[(2 * i)][(2 * j)];
/* 142:138 */           Texton[(2 * i + 1)][(2 * j)] = ImageX[(2 * i + 1)][(2 * j)];
/* 143:139 */           Texton[(2 * i)][(2 * j + 1)] = ImageX[(2 * i)][(2 * j + 1)];
/* 144:140 */           Texton[(2 * i + 1)][(2 * j + 1)] = ImageX[(2 * i + 1)][(2 * j + 1)];
/* 145:    */         }
/* 146:143 */         if (ImageX[(2 * i)][(2 * j)] == ImageX[(2 * i)][(2 * j + 1)])
/* 147:    */         {
/* 148:144 */           Texton[(2 * i)][(2 * j)] = ImageX[(2 * i)][(2 * j)];
/* 149:145 */           Texton[(2 * i + 1)][(2 * j)] = ImageX[(2 * i + 1)][(2 * j)];
/* 150:146 */           Texton[(2 * i)][(2 * j + 1)] = ImageX[(2 * i)][(2 * j + 1)];
/* 151:147 */           Texton[(2 * i + 1)][(2 * j + 1)] = ImageX[(2 * i + 1)][(2 * j + 1)];
/* 152:    */         }
/* 153:    */       }
/* 154:    */     }
/* 155:153 */     int[] MatrixH = new int[CSA + CSB];
/* 156:154 */     int[] MatrixV = new int[CSA + CSB];
/* 157:155 */     int[] MatrixRD = new int[CSA + CSB];
/* 158:156 */     int[] MatrixLD = new int[CSA + CSB];
/* 159:    */     
/* 160:158 */     int D = 1;
/* 161:160 */     for (int i = 0; i <= Width - 1; i++) {
/* 162:161 */       for (int j = 0; j <= Height - D - 1; j++)
/* 163:    */       {
/* 164:162 */         if (Ori[i][(j + D)] == Ori[i][j]) {
/* 165:162 */           MatrixH[Texton[i][j]] += 1;
/* 166:    */         }
/* 167:163 */         if (Texton[i][(j + D)] == Texton[i][j]) {
/* 168:163 */           MatrixH[(CSA + Ori[i][j])] += 1;
/* 169:    */         }
/* 170:    */       }
/* 171:    */     }
/* 172:167 */     for (int i = 0; i <= Width - D - 1; i++) {
/* 173:168 */       for (int j = 0; j <= Height - 1; j++)
/* 174:    */       {
/* 175:169 */         if (Ori[(i + D)][j] == Ori[i][j]) {
/* 176:169 */           MatrixV[Texton[i][j]] += 1;
/* 177:    */         }
/* 178:170 */         if (Texton[(i + D)][j] == Texton[i][j]) {
/* 179:170 */           MatrixV[(CSA + Ori[i][j])] += 1;
/* 180:    */         }
/* 181:    */       }
/* 182:    */     }
/* 183:174 */     for (int i = 0; i <= Width - D - 1; i++) {
/* 184:175 */       for (int j = 0; j <= Height - D - 1; j++)
/* 185:    */       {
/* 186:176 */         if (Ori[(i + D)][(j + D)] == Ori[i][j]) {
/* 187:176 */           MatrixRD[Texton[i][j]] += 1;
/* 188:    */         }
/* 189:177 */         if (Texton[(i + D)][(j + D)] == Texton[i][j]) {
/* 190:177 */           MatrixRD[(CSA + Ori[i][j])] += 1;
/* 191:    */         }
/* 192:    */       }
/* 193:    */     }
/* 194:181 */     for (int i = D; i <= Width - 1; i++) {
/* 195:182 */       for (int j = 0; j <= Height - D - 1; j++)
/* 196:    */       {
/* 197:183 */         if (Ori[(i - D)][(j + D)] == Ori[i][j]) {
/* 198:183 */           MatrixLD[Texton[i][j]] += 1;
/* 199:    */         }
/* 200:184 */         if (Texton[(i - D)][(j + D)] == Texton[i][j]) {
/* 201:184 */           MatrixLD[(CSA + Ori[i][j])] += 1;
/* 202:    */         }
/* 203:    */       }
/* 204:    */     }
/* 205:189 */     this.output = new double[CSA + CSB];
/* 206:190 */     for (int i = 0; i <= CSA + CSB - 1; i++) {
/* 207:191 */       this.output[i] = ((MatrixH[i] + MatrixV[i] + MatrixRD[i] + MatrixLD[i]) / 4.0D);
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static double[] invoke(Image image)
/* 212:    */   {
/* 213:    */     try
/* 214:    */     {
/* 215:196 */       return (double[])new MultiTextonHistogram().preprocess(new Object[] { image });
/* 216:    */     }
/* 217:    */     catch (GlobalException e)
/* 218:    */     {
/* 219:198 */       e.printStackTrace();
/* 220:    */     }
/* 221:199 */     return null;
/* 222:    */   }
/* 223:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.MultiTextonHistogram
 * JD-Core Version:    0.7.0.1
 */
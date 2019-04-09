/*   1:    */ package vpt.util.se;
/*   2:    */ 
/*   3:    */ import Jama.Matrix;
/*   4:    */ import Jama.SingularValueDecomposition;
/*   5:    */ import java.awt.Point;
/*   6:    */ import vpt.DoubleImage;
/*   7:    */ import vpt.util.Distance;
/*   8:    */ 
/*   9:    */ public class NonFlatSE
/*  10:    */   extends DoubleImage
/*  11:    */ {
/*  12:    */   public Point center;
/*  13:    */   private Point[] coords;
/*  14:    */   
/*  15:    */   public NonFlatSE(Point center, DoubleImage img)
/*  16:    */   {
/*  17: 19 */     super(img, true);
/*  18: 20 */     this.center = center;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public NonFlatSE(Point center, double[][] kernel)
/*  22:    */   {
/*  23: 24 */     super(kernel);
/*  24: 25 */     this.center = center;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public NonFlatSE(NonFlatSE se)
/*  28:    */   {
/*  29: 29 */     super(se, false);
/*  30: 30 */     this.center = new Point(se.center);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static NonFlatSE box(int width, int height, double d)
/*  34:    */   {
/*  35: 35 */     DoubleImage dimg = new DoubleImage(width, height, 1);
/*  36: 36 */     dimg.fill(d);
/*  37:    */     
/*  38: 38 */     NonFlatSE se = new NonFlatSE(new Point(dimg.getXDim() / 2, dimg.getYDim() / 2), dimg);
/*  39:    */     
/*  40: 40 */     return se;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static NonFlatSE cylinder(int radius, double height)
/*  44:    */   {
/*  45: 52 */     if (radius < 1) {
/*  46: 52 */       radius = 1;
/*  47:    */     }
/*  48: 53 */     int size = radius * 2 + 1;
/*  49:    */     
/*  50: 55 */     NonFlatSE se = box(size, size, -1.0D);
/*  51:    */     
/*  52:    */ 
/*  53: 58 */     se.setXYDouble(se.center.x, se.center.y, height);
/*  54:    */     
/*  55:    */ 
/*  56: 61 */     int perimeter = 8 * radius;
/*  57: 63 */     for (int i = 0; i < perimeter; i++)
/*  58:    */     {
/*  59: 64 */       int x = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/*  60: 65 */       int y = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/*  61: 66 */       se.setXYDouble(x, y, height);
/*  62:    */     }
/*  63: 69 */     return se;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static NonFlatSE cone(int radius, double height)
/*  67:    */   {
/*  68: 73 */     if (radius < 1) {
/*  69: 73 */       radius = 1;
/*  70:    */     }
/*  71: 74 */     int size = radius * 2 + 1;
/*  72:    */     
/*  73: 76 */     NonFlatSE se = box(size, size, 0.0D);
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79: 82 */     double maxDist = Distance.EuclideanDistance(0, 0, radius, radius);
/*  80: 84 */     for (int x = 0; x < size; x++) {
/*  81: 85 */       for (int y = 0; y < size; y++)
/*  82:    */       {
/*  83: 86 */         double dist = Distance.EuclideanDistance(x, y, se.center.x, se.center.y);
/*  84: 88 */         if (dist <= radius) {
/*  85: 89 */           se.setXYDouble(x, y, height * (maxDist - dist) / maxDist);
/*  86:    */         } else {
/*  87: 91 */           se.setXYDouble(x, y, -1.0D);
/*  88:    */         }
/*  89:    */       }
/*  90:    */     }
/*  91: 95 */     return se;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static NonFlatSE Gaussian(int radius)
/*  95:    */   {
/*  96:104 */     int r = radius * 2 + 1;
/*  97:    */     
/*  98:106 */     DoubleImage dimg = new DoubleImage(r, r, 1);
/*  99:107 */     double[] vector = new double[r];
/* 100:    */     
/* 101:109 */     double sigma = radius / 3.0D;
/* 102:110 */     double sigma22 = 2.0D * sigma * sigma;
/* 103:111 */     double sigmaPi2 = 6.283185307179586D * sigma;
/* 104:112 */     double sqrtSigmaPi2 = Math.sqrt(sigmaPi2);
/* 105:113 */     double radius2 = radius * radius;
/* 106:114 */     int index = 0;
/* 107:117 */     for (int x = -1 * radius; x <= radius; x++)
/* 108:    */     {
/* 109:118 */       double distance = x * x;
/* 110:120 */       if (distance > radius2)
/* 111:    */       {
/* 112:121 */         vector[(index++)] = 0.0D;
/* 113:    */       }
/* 114:    */       else
/* 115:    */       {
/* 116:123 */         double tmp = Math.exp(-1.0D * distance / sigma22) / sqrtSigmaPi2;
/* 117:124 */         vector[(index++)] = tmp;
/* 118:    */       }
/* 119:    */     }
/* 120:129 */     double total = 0.0D;
/* 121:130 */     for (int x = 0; x < r; x++) {
/* 122:131 */       for (int y = 0; y < r; y++)
/* 123:    */       {
/* 124:132 */         double tmp = vector[x] * vector[y];
/* 125:    */         
/* 126:134 */         dimg.setXYDouble(x, y, tmp);
/* 127:135 */         total += tmp;
/* 128:    */       }
/* 129:    */     }
/* 130:140 */     for (int x = 0; x < r; x++) {
/* 131:141 */       for (int y = 0; y < r; y++) {
/* 132:142 */         dimg.setXYDouble(x, y, dimg.getXYDouble(x, y) / total);
/* 133:    */       }
/* 134:    */     }
/* 135:146 */     return new NonFlatSE(new Point(r / 2, r / 2), dimg);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public NonFlatSE[] decompose()
/* 139:    */   {
/* 140:162 */     Matrix M = new Matrix(getYDim(), getXDim());
/* 141:164 */     for (int x = 0; x < getXDim(); x++) {
/* 142:165 */       for (int y = 0; y < getYDim(); y++)
/* 143:    */       {
/* 144:166 */         double p = getXYDouble(x, y);
/* 145:167 */         M.set(y, x, p);
/* 146:    */       }
/* 147:    */     }
/* 148:179 */     if (M.rank() != 1) {
/* 149:179 */       return null;
/* 150:    */     }
/* 151:181 */     NonFlatSE[] subKernels = new NonFlatSE[2];
/* 152:    */     
/* 153:    */ 
/* 154:184 */     SingularValueDecomposition svd = new SingularValueDecomposition(M);
/* 155:    */     
/* 156:    */ 
/* 157:187 */     double s = svd.getSingularValues()[0];
/* 158:    */     
/* 159:189 */     Matrix U = svd.getU();
/* 160:190 */     Matrix V = svd.getV();
/* 161:    */     
/* 162:    */ 
/* 163:193 */     double[][] hKernel = new double[1][getXDim()];
/* 164:195 */     for (int i = 0; i < getXDim(); i++) {
/* 165:196 */       hKernel[0][i] = (-1.0D * V.get(i, 0) * Math.sqrt(s));
/* 166:    */     }
/* 167:198 */     subKernels[0] = new NonFlatSE(new Point(getXDim() / 2, 0), hKernel);
/* 168:    */     
/* 169:    */ 
/* 170:201 */     double[][] vKernel = new double[getYDim()][1];
/* 171:203 */     for (int i = 0; i < getYDim(); i++) {
/* 172:204 */       vKernel[i][0] = (-1.0D * U.get(i, 0) * Math.sqrt(s));
/* 173:    */     }
/* 174:206 */     subKernels[1] = new NonFlatSE(new Point(0, getYDim() / 2), vKernel);
/* 175:    */     
/* 176:208 */     return subKernels;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static NonFlatSE box(DoubleImage img)
/* 180:    */   {
/* 181:213 */     NonFlatSE se = new NonFlatSE(new Point(img.getXDim() / 2, img.getYDim() / 2), img);
/* 182:    */     
/* 183:215 */     return se;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public NonFlatSE reflection()
/* 187:    */   {
/* 188:219 */     NonFlatSE se = new NonFlatSE(this);
/* 189:    */     
/* 190:221 */     se.center = new Point(getXDim() - 1 - this.center.x, getYDim() - 1 - this.center.y);
/* 191:223 */     for (int x = 0; x < getXDim(); x++) {
/* 192:224 */       for (int y = 0; y < getYDim(); y++) {
/* 193:225 */         se.setXYDouble(getXDim() - 1 - x, getYDim() - 1 - y, getXYDouble(x, y));
/* 194:    */       }
/* 195:    */     }
/* 196:227 */     return se;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public Point[] getCoords()
/* 200:    */   {
/* 201:232 */     if (this.coords != null) {
/* 202:232 */       return this.coords;
/* 203:    */     }
/* 204:234 */     int size = 0;
/* 205:236 */     for (int x = 0; x < getXDim(); x++) {
/* 206:237 */       for (int y = 0; y < getYDim(); y++) {
/* 207:238 */         if (Math.abs(getXYDouble(x, y)) >= 0.0D) {
/* 208:238 */           size++;
/* 209:    */         }
/* 210:    */       }
/* 211:    */     }
/* 212:240 */     this.coords = new Point[size];
/* 213:    */     
/* 214:242 */     int k = 0;
/* 215:245 */     if (getXYDouble(this.center.x, this.center.y) >= 0.0D) {
/* 216:246 */       this.coords[(k++)] = new Point(0, 0);
/* 217:    */     }
/* 218:249 */     for (int x = 0; x < getXDim(); x++) {
/* 219:250 */       for (int y = 0; y < getYDim(); y++) {
/* 220:251 */         if ((Math.abs(getXYDouble(x, y)) >= 0.0D) && ((x != this.center.x) || (y != this.center.y))) {
/* 221:252 */           this.coords[(k++)] = new Point(x - this.center.x, y - this.center.y);
/* 222:    */         }
/* 223:    */       }
/* 224:    */     }
/* 225:258 */     return this.coords;
/* 226:    */   }
/* 227:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.se.NonFlatSE
 * JD-Core Version:    0.7.0.1
 */
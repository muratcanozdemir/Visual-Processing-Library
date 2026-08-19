/*   1:    */ package vpt.algorithms.experimental.fz;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.DoubleImage;
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ import vpt.algorithms.conversion.RGB2XYZ;
/*  15:    */ import vpt.algorithms.conversion.XYZ2LAB;
/*  16:    */ import vpt.util.Tools;
/*  17:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  18:    */ import vpt.util.ordering.EuclideanNorm;
/*  19:    */ 
/*  20:    */ public class ColorQFZCIELAB
/*  21:    */   extends Algorithm
/*  22:    */ {
/*  23:    */   public Image input;
/*  24:    */   public IntegerImage output;
/*  25:    */   public int[] alphaByte;
/*  26:    */   public int[] omegaByte;
/*  27: 45 */   private double[] alpha = new double[3];
/*  28: 46 */   private double[] omega = new double[3];
/*  29: 48 */   public AlgebraicOrdering vo = new EuclideanNorm();
/*  30: 50 */   private ArrayList<Point> list = new ArrayList();
/*  31: 51 */   private ArrayList<Point> list2 = new ArrayList();
/*  32: 52 */   private Stack<Point> s = new Stack();
/*  33:    */   private int xdim;
/*  34:    */   private int ydim;
/*  35: 55 */   private int label = 1;
/*  36: 56 */   private double[] max = new double[3];
/*  37: 57 */   private double[] min = new double[3];
/*  38: 58 */   private double[] diff = new double[3];
/*  39: 60 */   private ColorVector[] cv = new ColorVector[16777216];
/*  40: 61 */   private int alphaIndex = -1;
/*  41:    */   BooleanImage temp;
/*  42:    */   Image localRanges;
/*  43: 73 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  44: 74 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  45:    */   
/*  46:    */   public ColorQFZCIELAB()
/*  47:    */   {
/*  48: 77 */     this.inputFields = "input,alphaByte,omegaByte";
/*  49: 78 */     this.outputFields = "output";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void execute()
/*  53:    */     throws GlobalException
/*  54:    */   {
/*  55: 83 */     this.xdim = this.input.getXDim();
/*  56: 84 */     this.ydim = this.input.getYDim();
/*  57: 86 */     if (this.input.getCDim() != 3) {
/*  58: 86 */       throw new GlobalException("This implementation is only for color images.");
/*  59:    */     }
/*  60: 93 */     for (int y = 0; y < this.ydim; y++) {
/*  61: 94 */       for (int x = 0; x < this.xdim; x++)
/*  62:    */       {
/*  63: 96 */         double[] lab = this.input.getVXYDouble(x, y);
/*  64:    */         
/*  65:    */ 
/*  66: 99 */         lab[0] /= 100.0D;
/*  67:101 */         if ((lab[0] < 0.0D) || (lab[0] > 1.0D)) {
/*  68:101 */           System.err.println("L " + lab[0]);
/*  69:    */         }
/*  70:104 */         lab[1] = ((lab[1] + 128.0D) / 255.0D);
/*  71:106 */         if ((lab[1] < 0.0D) || (lab[1] > 1.0D)) {
/*  72:106 */           System.err.println("A " + lab[1]);
/*  73:    */         }
/*  74:109 */         lab[2] = ((lab[2] + 128.0D) / 255.0D);
/*  75:111 */         if ((lab[2] < 0.0D) || (lab[2] > 1.0D)) {
/*  76:111 */           System.err.println("B " + lab[2]);
/*  77:    */         }
/*  78:113 */         this.input.setVXYDouble(x, y, lab);
/*  79:    */       }
/*  80:    */     }
/*  81:117 */     if ((this.alphaByte[0] < 0) || (this.alphaByte[1] < 0) || (this.alphaByte[2] < 0) || (this.alphaByte[0] > 254) || (this.alphaByte[1] > 254) || (this.alphaByte[2] > 254) || 
/*  82:118 */       (this.omegaByte[0] < 0) || (this.omegaByte[1] < 0) || (this.omegaByte[2] < 0) || (this.omegaByte[0] > 254) || (this.omegaByte[1] > 254) || (this.omegaByte[2] > 254)) {
/*  83:119 */       throw new GlobalException("Arguments out of range.");
/*  84:    */     }
/*  85:121 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  86:122 */     this.output.fill(0);
/*  87:    */     
/*  88:124 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  89:125 */     this.temp.fill(false);
/*  90:    */     
/*  91:127 */     this.localRanges = new DoubleImage(this.xdim, this.ydim, 3);
/*  92:128 */     this.localRanges.fill(1.0D);
/*  93:    */     
/*  94:    */ 
/*  95:131 */     this.alpha = RGB2XYZ.convert(this.alphaByte[0], this.alphaByte[1], this.alphaByte[2]);
/*  96:132 */     this.alpha = XYZ2LAB.convert(this.alpha[0], this.alpha[1], this.alpha[2]);
/*  97:    */     
/*  98:134 */     this.alpha[0] /= 100.0D;
/*  99:135 */     this.alpha[1] = ((this.alpha[1] + 128.0D) / 255.0D);
/* 100:136 */     this.alpha[2] = ((this.alpha[2] + 128.0D) / 255.0D);
/* 101:    */     
/* 102:138 */     this.omega = RGB2XYZ.convert(this.omegaByte[0], this.omegaByte[1], this.omegaByte[2]);
/* 103:139 */     this.omega = XYZ2LAB.convert(this.omega[0], this.omega[1], this.omega[2]);
/* 104:    */     
/* 105:141 */     this.omega[0] /= 100.0D;
/* 106:142 */     this.omega[1] = ((this.omega[1] + 128.0D) / 255.0D);
/* 107:143 */     this.omega[2] = ((this.omega[2] + 128.0D) / 255.0D);
/* 108:    */     
/* 109:145 */     initColorVectors();
/* 110:147 */     if (this.alphaIndex == -1) {
/* 111:147 */       System.err.println("Rank of alpha not found !!!");
/* 112:    */     } else {
/* 113:148 */       System.err.println("Rank of alpha : " + this.alphaIndex);
/* 114:    */     }
/* 115:151 */     for (int y = 0; y < this.ydim; y++)
/* 116:    */     {
/* 117:153 */       System.out.println("Satir " + y);
/* 118:155 */       for (int x = 0; x < this.xdim; x++)
/* 119:    */       {
/* 120:157 */         System.out.println("Sutun " + x);
/* 121:160 */         if (this.output.getXYInt(x, y) <= 0)
/* 122:    */         {
/* 123:162 */           Point t = new Point(x, y);
/* 124:    */           
/* 125:    */ 
/* 126:165 */           resetMaxMin();
/* 127:168 */           if (createQCC(t, this.alpha) > 0)
/* 128:    */           {
/* 129:173 */             for (Point s : this.list) {
/* 130:174 */               this.localRanges.setVXYDouble(s.x, s.y, this.alpha);
/* 131:    */             }
/* 132:    */           }
/* 133:    */           else
/* 134:    */           {
/* 135:179 */             for (Point p : this.list) {
/* 136:180 */               this.output.setXYInt(p.x, p.y, 0);
/* 137:    */             }
/* 138:183 */             this.list.clear();
/* 139:    */             
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:190 */             int a_1 = 0;
/* 146:191 */             int a_2 = this.alphaIndex;
/* 147:    */             
/* 148:193 */             int tmpAlpha = (a_1 + a_2) / 2;
/* 149:    */             for (;;)
/* 150:    */             {
/* 151:196 */               resetMaxMin();
/* 152:201 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 153:    */               {
/* 154:202 */                 a_1 = tmpAlpha;
/* 155:203 */                 tmpAlpha = (tmpAlpha + a_2) / 2;
/* 156:    */                 
/* 157:205 */                 System.out.println("Satisfied " + a_1);
/* 158:208 */                 if (a_1 == tmpAlpha) {
/* 159:    */                   break;
/* 160:    */                 }
/* 161:    */               }
/* 162:    */               else
/* 163:    */               {
/* 164:211 */                 a_2 = tmpAlpha;
/* 165:212 */                 tmpAlpha = (a_1 + tmpAlpha) / 2;
/* 166:    */                 
/* 167:214 */                 System.out.println("Not satisfied " + a_2);
/* 168:    */               }
/* 169:218 */               for (Point p : this.list) {
/* 170:219 */                 this.output.setXYInt(p.x, p.y, 0);
/* 171:    */               }
/* 172:222 */               this.list.clear();
/* 173:    */             }
/* 174:    */           }
/* 175:228 */           this.list.clear();
/* 176:    */           
/* 177:    */ 
/* 178:231 */           this.label += 1;
/* 179:    */         }
/* 180:    */       }
/* 181:    */     }
/* 182:235 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 183:    */   }
/* 184:    */   
/* 185:    */   private void resetMaxMin()
/* 186:    */   {
/* 187:239 */     this.max[0] = 0.0D;
/* 188:240 */     this.max[1] = 0.0D;
/* 189:241 */     this.max[2] = 0.0D;
/* 190:    */     
/* 191:243 */     this.min[0] = 1.0D;
/* 192:244 */     this.min[1] = 1.0D;
/* 193:245 */     this.min[2] = 1.0D;
/* 194:    */   }
/* 195:    */   
/* 196:    */   private int createQCC(Point r, double[] alpha2)
/* 197:    */   {
/* 198:258 */     this.s.clear();
/* 199:259 */     this.s.add(r);
/* 200:    */     int x;
/* 201:    */     int i;
/* 202:261 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 203:    */     {
/* 204:263 */       Point tmp = (Point)this.s.pop();
/* 205:264 */       x = tmp.x;
/* 206:265 */       int y = tmp.y;
/* 207:    */       
/* 208:267 */       double[] p = this.input.getVXYDouble(x, y);
/* 209:270 */       if (this.vo.compare(this.max, p) < 0)
/* 210:    */       {
/* 211:271 */         this.max[0] = p[0];
/* 212:272 */         this.max[1] = p[1];
/* 213:273 */         this.max[2] = p[2];
/* 214:    */       }
/* 215:276 */       if (this.vo.compare(this.min, p) > 0)
/* 216:    */       {
/* 217:277 */         this.min[0] = p[0];
/* 218:278 */         this.min[1] = p[1];
/* 219:279 */         this.min[2] = p[2];
/* 220:    */       }
/* 221:282 */       this.diff[0] = Math.abs(this.max[0] - this.min[0]);
/* 222:283 */       this.diff[1] = Math.abs(this.max[1] - this.min[1]);
/* 223:284 */       this.diff[2] = Math.abs(this.max[2] - this.min[2]);
/* 224:288 */       if (this.vo.compare(this.omega, this.diff) < 0)
/* 225:    */       {
/* 226:291 */         for (Point t : this.list2) {
/* 227:292 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 228:    */         }
/* 229:293 */         this.list2.clear();
/* 230:    */         
/* 231:295 */         return -1;
/* 232:    */       }
/* 233:299 */       this.output.setXYInt(x, y, this.label);
/* 234:    */       
/* 235:    */ 
/* 236:    */ 
/* 237:303 */       this.list.add(tmp);
/* 238:    */       
/* 239:    */ 
/* 240:306 */       i = 0; continue;
/* 241:307 */       int _x = x + this.N[i].x;
/* 242:308 */       int _y = y + this.N[i].y;
/* 243:311 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 244:    */       {
/* 245:313 */         double[] q = this.input.getVXYDouble(_x, _y);
/* 246:    */         
/* 247:    */ 
/* 248:316 */         this.diff[0] = Math.abs(p[0] - q[0]);
/* 249:317 */         this.diff[1] = Math.abs(p[1] - q[1]);
/* 250:318 */         this.diff[2] = Math.abs(p[2] - q[2]);
/* 251:321 */         if (this.output.getXYInt(_x, _y) > 0)
/* 252:    */         {
/* 253:323 */           double[] localR = this.localRanges.getVXYDouble(_x, _y);
/* 254:325 */           if ((this.vo.compare(this.diff, alpha2) <= 0) && (this.vo.compare(localR, alpha2) <= 0))
/* 255:    */           {
/* 256:327 */             for (Point t : this.list2) {
/* 257:328 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 258:    */             }
/* 259:329 */             this.list2.clear();
/* 260:    */             
/* 261:331 */             return -1;
/* 262:    */           }
/* 263:    */         }
/* 264:338 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 265:    */         {
/* 266:341 */           if (this.vo.compare(this.diff, alpha2) <= 0)
/* 267:    */           {
/* 268:344 */             Point t = new Point(_x, _y);
/* 269:345 */             this.s.add(t);
/* 270:    */             
/* 271:    */ 
/* 272:348 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 273:349 */             this.list2.add(t);
/* 274:    */           }
/* 275:    */         }
/* 276:    */       }
/* 277:306 */       i++;
/* 278:    */     }
/* 279:355 */     for (Point t : this.list2) {
/* 280:356 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 281:    */     }
/* 282:357 */     this.list2.clear();
/* 283:    */     
/* 284:359 */     return 1;
/* 285:    */   }
/* 286:    */   
/* 287:    */   private void initColorVectors()
/* 288:    */   {
/* 289:364 */     double[] d = new double[3];
/* 290:367 */     for (int r = 0; r < 256; r++) {
/* 291:368 */       for (int g = 0; g < 256; g++) {
/* 292:369 */         for (int b = 0; b < 256; b++)
/* 293:    */         {
/* 294:371 */           d = RGB2XYZ.convert(r, g, b);
/* 295:372 */           d = XYZ2LAB.convert(d[0], d[1], d[2]);
/* 296:    */           
/* 297:374 */           d[0] /= 100.0D;
/* 298:375 */           d[1] = ((d[1] + 128.0D) / 255.0D);
/* 299:376 */           d[2] = ((d[2] + 128.0D) / 255.0D);
/* 300:    */           
/* 301:378 */           this.cv[(r * 256 * 256 + g * 256 + b)] = new ColorVector(d[0], d[1], d[2]);
/* 302:380 */           if ((d[0] < 0.0D) || (d[0] > 1.0D)) {
/* 303:380 */             System.err.println("L " + d[0]);
/* 304:    */           }
/* 305:381 */           if ((d[1] < 0.0D) || (d[1] > 1.0D)) {
/* 306:381 */             System.err.println("A " + d[1]);
/* 307:    */           }
/* 308:382 */           if ((d[2] < 0.0D) || (d[2] > 1.0D)) {
/* 309:382 */             System.err.println("B " + d[2]);
/* 310:    */           }
/* 311:    */         }
/* 312:    */       }
/* 313:    */     }
/* 314:387 */     System.err.println("Sorting colors");
/* 315:    */     
/* 316:389 */     Arrays.sort(this.cv);
/* 317:    */     
/* 318:391 */     System.err.println(this.cv[0].z[0] + " " + this.cv[0].z[1] + " " + this.cv[0].z[2]);
/* 319:392 */     System.err.println(this.cv[(this.cv.length - 1)].z[0] + " " + this.cv[(this.cv.length - 1)].z[1] + " " + this.cv[(this.cv.length - 1)].z[2]);
/* 320:395 */     for (int i = 0; i < this.cv.length; i++) {
/* 321:397 */       if ((Tools.cmpr(this.cv[i].z[0], this.alpha[0]) == 0) && (Tools.cmpr(this.cv[i].z[1], this.alpha[1]) == 0) && (Tools.cmpr(this.cv[i].z[2], this.alpha[2]) == 0))
/* 322:    */       {
/* 323:398 */         this.alphaIndex = i;
/* 324:399 */         break;
/* 325:    */       }
/* 326:    */     }
/* 327:    */   }
/* 328:    */   
/* 329:    */   private class ColorVector
/* 330:    */     implements Comparable
/* 331:    */   {
/* 332:    */     double[] z;
/* 333:    */     
/* 334:    */     ColorVector(double a, double b, double c)
/* 335:    */     {
/* 336:409 */       this.z = new double[3];
/* 337:410 */       this.z[0] = a;this.z[1] = b;this.z[2] = c;
/* 338:    */     }
/* 339:    */     
/* 340:    */     public int compareTo(Object o)
/* 341:    */     {
/* 342:414 */       ColorVector c = (ColorVector)o;
/* 343:415 */       return ColorQFZCIELAB.this.vo.compare(this.z, c.z);
/* 344:    */     }
/* 345:    */   }
/* 346:    */   
/* 347:    */   public static Image invoke(Image img, int[] alpha, int[] omega)
/* 348:    */   {
/* 349:    */     try
/* 350:    */     {
/* 351:421 */       return (IntegerImage)new ColorQFZCIELAB().preprocess(new Object[] { img, alpha, omega });
/* 352:    */     }
/* 353:    */     catch (GlobalException e)
/* 354:    */     {
/* 355:423 */       e.printStackTrace();
/* 356:    */     }
/* 357:424 */     return null;
/* 358:    */   }
/* 359:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorQFZCIELAB
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.algorithms.experimental.fz;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.Stack;
/*  10:    */ import java.util.TreeMap;
/*  11:    */ import vpt.Algorithm;
/*  12:    */ import vpt.DoubleImage;
/*  13:    */ import vpt.GlobalException;
/*  14:    */ import vpt.Image;
/*  15:    */ import vpt.IntegerImage;
/*  16:    */ import vpt.util.Tools;
/*  17:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  18:    */ import vpt.util.ordering.EuclideanNorm;
/*  19:    */ 
/*  20:    */ public class ColorFZSoille
/*  21:    */   extends Algorithm
/*  22:    */ {
/*  23:    */   public Image input;
/*  24:    */   public IntegerImage output;
/*  25:    */   public double[] alpha;
/*  26:    */   public double[] omega;
/*  27: 32 */   public AlgebraicOrdering vo = new EuclideanNorm();
/*  28: 33 */   private ColorVector[] cv = new ColorVector[16777216];
/*  29: 34 */   private int alphaIndex = -1;
/*  30: 36 */   public Point[] neighbors4 = { new Point(1, 0), new Point(0, 1), 
/*  31: 37 */     new Point(-1, 0), new Point(0, -1) };
/*  32: 38 */   public Point[] neighbors8 = { new Point(1, 0), new Point(0, 1), 
/*  33: 39 */     new Point(-1, 0), new Point(0, -1), new Point(1, 1), 
/*  34: 40 */     new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  35:    */   
/*  36:    */   public ColorFZSoille()
/*  37:    */   {
/*  38: 43 */     this.inputFields = "input,alpha,omega";
/*  39: 44 */     this.outputFields = "output";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 49 */     int xdim = this.input.getXDim();
/*  46: 50 */     int ydim = this.input.getYDim();
/*  47:    */     
/*  48:    */ 
/*  49: 53 */     Point[] N = this.neighbors8;
/*  50: 55 */     if (this.input.getCDim() != 3) {
/*  51: 55 */       throw new GlobalException("This implementation is only for color images.");
/*  52:    */     }
/*  53: 57 */     if ((this.alpha[0] < 0.0D) || (this.alpha[1] < 0.0D) || (this.alpha[2] < 0.0D) || (this.alpha[0] > 254.0D) || (this.alpha[1] > 254.0D) || (this.alpha[2] > 254.0D) || 
/*  54: 58 */       (this.omega[0] < 0.0D) || (this.omega[1] < 0.0D) || (this.omega[2] < 0.0D) || (this.omega[0] > 254.0D) || (this.omega[1] > 254.0D) || (this.omega[2] > 254.0D)) {
/*  55: 59 */       throw new GlobalException("Arguments out of range.");
/*  56:    */     }
/*  57: 62 */     this.alpha[0] *= 0.00392156862745098D;
/*  58: 63 */     this.alpha[1] *= 0.00392156862745098D;
/*  59: 64 */     this.alpha[2] *= 0.00392156862745098D;
/*  60: 65 */     this.omega[0] *= 0.00392156862745098D;
/*  61: 66 */     this.omega[1] *= 0.00392156862745098D;
/*  62: 67 */     this.omega[2] *= 0.00392156862745098D;
/*  63:    */     
/*  64:    */ 
/*  65: 70 */     initColorVectors();
/*  66: 73 */     if (this.alphaIndex == -1) {
/*  67: 73 */       System.err.println("What the hell, where is the alpha index?");
/*  68:    */     } else {
/*  69: 74 */       System.err.println("Rank found");
/*  70:    */     }
/*  71: 77 */     IntegerImage lbl = new IntegerImage(this.input.getXDim(), this.input.getYDim(), 1);
/*  72: 78 */     lbl.fill(0);
/*  73:    */     
/*  74:    */ 
/*  75: 81 */     DoubleImage rl = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 3);
/*  76: 82 */     rl.fill(1.0D);
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81: 87 */     double[] mincc = new double[3];
/*  82: 88 */     double[] maxcc = new double[3];
/*  83: 89 */     double[] rlval = new double[3];
/*  84: 90 */     double[] diff = new double[3];
/*  85: 91 */     double[] rlcrt = new double[3];
/*  86:    */     
/*  87:    */ 
/*  88: 94 */     Stack<Point> stack = new Stack();
/*  89:    */     
/*  90:    */ 
/*  91:    */ 
/*  92: 98 */     TreeMap<Integer, ArrayList<Point>> pq = new TreeMap();
/*  93:    */     
/*  94:100 */     int lblval = 1;
/*  95:103 */     for (int x = 0; x < xdim; x++)
/*  96:    */     {
/*  97:105 */       if (x % 10 == 0) {
/*  98:105 */         System.err.println("Sutun " + x);
/*  99:    */       }
/* 100:107 */       for (int y = 0; y < ydim; y++) {
/* 101:108 */         if (lbl.getXYInt(x, y) == 0)
/* 102:    */         {
/* 103:109 */           lbl.setXYInt(x, y, lblval);
/* 104:    */           
/* 105:111 */           double[] fp = this.input.getVXYDouble(x, y);
/* 106:    */           
/* 107:113 */           maxcc[0] = fp[0];
/* 108:114 */           maxcc[1] = fp[1];
/* 109:115 */           maxcc[2] = fp[2];
/* 110:    */           
/* 111:117 */           mincc[0] = fp[0];
/* 112:118 */           mincc[1] = fp[1];
/* 113:119 */           mincc[2] = fp[2];
/* 114:    */           
/* 115:121 */           rlcrt[0] = this.alpha[0];
/* 116:122 */           rlcrt[1] = this.alpha[1];
/* 117:123 */           rlcrt[2] = this.alpha[2];
/* 118:    */           ArrayList<Point> tmp;
/* 119:126 */           for (int i = 0; i < N.length; i++)
/* 120:    */           {
/* 121:128 */             int qx = x + N[i].x;
/* 122:129 */             int qy = y + N[i].y;
/* 123:132 */             if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/* 124:    */             {
/* 125:135 */               double[] fq = this.input.getVXYDouble(qx, qy);
/* 126:    */               
/* 127:137 */               rlval[0] = Math.abs(fp[0] - fq[0]);
/* 128:138 */               rlval[1] = Math.abs(fp[1] - fq[1]);
/* 129:139 */               rlval[2] = Math.abs(fp[2] - fq[2]);
/* 130:141 */               if (lbl.getXYInt(qx, qy) > 0)
/* 131:    */               {
/* 132:142 */                 if (this.vo.compare(rlcrt, rlval) >= 0)
/* 133:    */                 {
/* 134:144 */                   int rank = rank(rlval);
/* 135:    */                   
/* 136:    */ 
/* 137:147 */                   rlcrt[0] = this.cv[(rank - 1)].z[0];
/* 138:148 */                   rlcrt[1] = this.cv[(rank - 1)].z[1];
/* 139:149 */                   rlcrt[2] = this.cv[(rank - 1)].z[2];
/* 140:    */                 }
/* 141:    */               }
/* 142:154 */               else if (this.vo.compare(rlval, rlcrt) <= 0)
/* 143:    */               {
/* 144:155 */                 rl.setVXYDouble(qx, qy, rlval);
/* 145:    */                 
/* 146:157 */                 int rank = rank(rlval);
/* 147:159 */                 if (pq.containsKey(Integer.valueOf(rank)))
/* 148:    */                 {
/* 149:160 */                   ((ArrayList)pq.get(Integer.valueOf(rank))).add(new Point(qx, qy));
/* 150:    */                 }
/* 151:    */                 else
/* 152:    */                 {
/* 153:162 */                   tmp = new ArrayList();
/* 154:163 */                   tmp.add(new Point(qx, qy));
/* 155:164 */                   pq.put(Integer.valueOf(rank), tmp);
/* 156:    */                 }
/* 157:    */               }
/* 158:    */             }
/* 159:    */           }
/* 160:    */           int rcrt;
/* 161:    */           int rcrt;
/* 162:170 */           if (!pq.isEmpty()) {
/* 163:171 */             rcrt = ((Integer)pq.firstKey()).intValue();
/* 164:    */           } else {
/* 165:173 */             rcrt = 0;
/* 166:    */           }
/* 167:175 */           while (!pq.isEmpty())
/* 168:    */           {
/* 169:176 */             int datumPrio = ((Integer)pq.firstKey()).intValue();
/* 170:177 */             ArrayList<Point> tmp = (ArrayList)pq.get(Integer.valueOf(datumPrio));
/* 171:178 */             Point datumP = (Point)tmp.remove(0);
/* 172:181 */             if (tmp.isEmpty()) {
/* 173:182 */               pq.pollFirstEntry();
/* 174:    */             }
/* 175:184 */             if (lbl.getXYInt(datumP.x, datumP.y) <= 0) {
/* 176:187 */               if (datumPrio > rcrt)
/* 177:    */               {
/* 178:188 */                 while (!stack.isEmpty())
/* 179:    */                 {
/* 180:189 */                   Point sPoint = (Point)stack.pop();
/* 181:190 */                   lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 182:    */                 }
/* 183:193 */                 rcrt = datumPrio;
/* 184:195 */                 if (lbl.getXYInt(datumP.x, datumP.y) > 0) {}
/* 185:    */               }
/* 186:    */               else
/* 187:    */               {
/* 188:199 */                 stack.add(datumP);
/* 189:    */                 
/* 190:201 */                 double[] fDatumP = this.input.getVXYDouble(datumP.x, datumP.y);
/* 191:203 */                 if (this.vo.compare(fDatumP, mincc) < 0)
/* 192:    */                 {
/* 193:204 */                   mincc[0] = fDatumP[0];
/* 194:205 */                   mincc[1] = fDatumP[1];
/* 195:206 */                   mincc[2] = fDatumP[2];
/* 196:    */                 }
/* 197:209 */                 if (this.vo.compare(fDatumP, maxcc) > 0)
/* 198:    */                 {
/* 199:210 */                   maxcc[0] = fDatumP[0];
/* 200:211 */                   maxcc[1] = fDatumP[1];
/* 201:212 */                   maxcc[2] = fDatumP[2];
/* 202:    */                 }
/* 203:215 */                 maxcc[0] -= mincc[0];
/* 204:216 */                 maxcc[1] -= mincc[1];
/* 205:217 */                 maxcc[2] -= mincc[2];
/* 206:219 */                 if ((this.vo.compare(this.omega, diff) < 0) || (rcrt > rank(rlcrt)))
/* 207:    */                 {
/* 208:221 */                   for (Point pp : stack)
/* 209:    */                   {
/* 210:222 */                     rl.setXYCDouble(pp.x, pp.y, 0, 1.0D);
/* 211:223 */                     rl.setXYCDouble(pp.x, pp.y, 1, 1.0D);
/* 212:224 */                     rl.setXYCDouble(pp.x, pp.y, 2, 1.0D);
/* 213:    */                   }
/* 214:226 */                   stack.clear();
/* 215:    */                   
/* 216:228 */                   Collection<ArrayList<Point>> pointLists = pq.values();
/* 217:    */                   Iterator localIterator2;
/* 218:230 */                   for (Iterator localIterator1 = pointLists.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
/* 219:    */                   {
/* 220:230 */                     ArrayList<Point> pointList = (ArrayList)localIterator1.next();
/* 221:    */                     
/* 222:232 */                     localIterator2 = pointList.iterator(); continue;Point p = (Point)localIterator2.next();
/* 223:233 */                     rl.setXYCDouble(p.x, p.y, 0, 1.0D);
/* 224:234 */                     rl.setXYCDouble(p.x, p.y, 1, 1.0D);
/* 225:235 */                     rl.setXYCDouble(p.x, p.y, 2, 1.0D);
/* 226:    */                   }
/* 227:239 */                   pq.clear();
/* 228:240 */                   break;
/* 229:    */                 }
/* 230:243 */                 for (int i = 0; i < N.length; i++)
/* 231:    */                 {
/* 232:245 */                   int qx = datumP.x + N[i].x;
/* 233:246 */                   int qy = datumP.y + N[i].y;
/* 234:249 */                   if ((qx >= 0) && (qx < xdim) && (qy >= 0) && (qy < ydim))
/* 235:    */                   {
/* 236:252 */                     double[] fq = this.input.getVXYDouble(qx, qy);
/* 237:    */                     
/* 238:254 */                     rlval[0] = Math.abs(fDatumP[0] - fq[0]);
/* 239:255 */                     rlval[1] = Math.abs(fDatumP[1] - fq[1]);
/* 240:256 */                     rlval[2] = Math.abs(fDatumP[2] - fq[2]);
/* 241:    */                     
/* 242:258 */                     int lblq = lbl.getXYInt(qx, qy);
/* 243:260 */                     if ((lblq > 0) && (lblq != lblval) && (this.vo.compare(rlcrt, rlval) >= 0))
/* 244:    */                     {
/* 245:261 */                       int rank = rank(rlval);
/* 246:    */                       
/* 247:    */ 
/* 248:264 */                       rlcrt[0] = this.cv[(rank - 1)].z[0];
/* 249:265 */                       rlcrt[1] = this.cv[(rank - 1)].z[1];
/* 250:266 */                       rlcrt[2] = this.cv[(rank - 1)].z[2];
/* 251:268 */                       if (rcrt > rank(rlcrt))
/* 252:    */                       {
/* 253:269 */                         for (Point pp : stack)
/* 254:    */                         {
/* 255:270 */                           rl.setXYCDouble(pp.x, pp.y, 0, 1.0D);
/* 256:271 */                           rl.setXYCDouble(pp.x, pp.y, 1, 1.0D);
/* 257:272 */                           rl.setXYCDouble(pp.x, pp.y, 2, 1.0D);
/* 258:    */                         }
/* 259:274 */                         stack.clear();
/* 260:    */                         
/* 261:276 */                         Collection<ArrayList<Point>> pointLists = pq.values();
/* 262:    */                         Iterator localIterator5;
/* 263:278 */                         for (Iterator localIterator4 = pointLists.iterator(); localIterator4.hasNext(); localIterator5.hasNext())
/* 264:    */                         {
/* 265:278 */                           Object pointList = (ArrayList)localIterator4.next();
/* 266:    */                           
/* 267:280 */                           localIterator5 = ((ArrayList)pointList).iterator(); continue;Point p = (Point)localIterator5.next();
/* 268:281 */                           rl.setXYCDouble(p.x, p.y, 0, 1.0D);
/* 269:282 */                           rl.setXYCDouble(p.x, p.y, 1, 1.0D);
/* 270:283 */                           rl.setXYCDouble(p.x, p.y, 2, 1.0D);
/* 271:    */                         }
/* 272:287 */                         pq.clear();
/* 273:288 */                         break;
/* 274:    */                       }
/* 275:    */                     }
/* 276:    */                     else
/* 277:    */                     {
/* 278:293 */                       double[] rlq = rl.getVXYDouble(qx, qy);
/* 279:295 */                       if ((this.vo.compare(rlval, rlcrt) <= 0) && (this.vo.compare(rlval, rlq) < 0)) {
/* 280:298 */                         if (this.vo.compare(rlval, rlq) < 0)
/* 281:    */                         {
/* 282:300 */                           rl.setVXYDouble(qx, qy, rlval);
/* 283:    */                           
/* 284:302 */                           int rank = rank(rlval);
/* 285:304 */                           if (pq.containsKey(Integer.valueOf(rank)))
/* 286:    */                           {
/* 287:305 */                             ((ArrayList)pq.get(Integer.valueOf(rank))).add(new Point(qx, qy));
/* 288:    */                           }
/* 289:    */                           else
/* 290:    */                           {
/* 291:307 */                             Object tmp2 = new ArrayList();
/* 292:308 */                             ((ArrayList)tmp2).add(new Point(qx, qy));
/* 293:309 */                             pq.put(Integer.valueOf(rank), tmp2);
/* 294:    */                           }
/* 295:    */                         }
/* 296:    */                       }
/* 297:    */                     }
/* 298:    */                   }
/* 299:    */                 }
/* 300:    */               }
/* 301:    */             }
/* 302:    */           }
/* 303:315 */           while (!stack.isEmpty())
/* 304:    */           {
/* 305:316 */             Point sPoint = (Point)stack.pop();
/* 306:317 */             lbl.setXYInt(sPoint.x, sPoint.y, lblval);
/* 307:    */           }
/* 308:320 */           lblval++;
/* 309:    */         }
/* 310:    */       }
/* 311:    */     }
/* 312:325 */     System.err.println("Labels " + (lblval - 1));
/* 313:    */     
/* 314:    */ 
/* 315:328 */     this.output = lbl;
/* 316:    */   }
/* 317:    */   
/* 318:    */   private void initColorVectors()
/* 319:    */   {
/* 320:334 */     for (int r = 0; r < 256; r++) {
/* 321:335 */       for (int g = 0; g < 256; g++) {
/* 322:336 */         for (int b = 0; b < 256; b++) {
/* 323:337 */           this.cv[(r * 256 * 256 + g * 256 + b)] = 
/* 324:338 */             new ColorVector(r * 0.00392156862745098D, g * 0.00392156862745098D, b * 0.00392156862745098D);
/* 325:    */         }
/* 326:    */       }
/* 327:    */     }
/* 328:342 */     System.err.println("Sorting colors");
/* 329:    */     
/* 330:344 */     Arrays.sort(this.cv);
/* 331:    */     
/* 332:346 */     System.err.println("Colors sorted");
/* 333:    */     
/* 334:348 */     this.alphaIndex = rank(this.alpha);
/* 335:    */   }
/* 336:    */   
/* 337:    */   private int rank(double[] v)
/* 338:    */   {
/* 339:352 */     for (int i = 0; i < this.cv.length; i++) {
/* 340:353 */       if ((Tools.cmpr(this.cv[i].z[0], v[0]) == 0) && (Tools.cmpr(this.cv[i].z[1], v[1]) == 0) && (Tools.cmpr(this.cv[i].z[2], v[2]) == 0)) {
/* 341:354 */         return i;
/* 342:    */       }
/* 343:    */     }
/* 344:357 */     System.err.println("Vector rank not found!?");
/* 345:358 */     return -1;
/* 346:    */   }
/* 347:    */   
/* 348:    */   private class ColorVector
/* 349:    */     implements Comparable
/* 350:    */   {
/* 351:    */     double[] z;
/* 352:    */     
/* 353:    */     ColorVector(double a, double b, double c)
/* 354:    */     {
/* 355:366 */       this.z = new double[3];
/* 356:367 */       this.z[0] = a;this.z[1] = b;this.z[2] = c;
/* 357:    */     }
/* 358:    */     
/* 359:    */     public int compareTo(Object o)
/* 360:    */     {
/* 361:371 */       ColorVector c = (ColorVector)o;
/* 362:372 */       return ColorFZSoille.this.vo.compare(this.z, c.z);
/* 363:    */     }
/* 364:    */   }
/* 365:    */   
/* 366:    */   public static Image invoke(Image img, double[] alpha, double[] omega)
/* 367:    */   {
/* 368:    */     try
/* 369:    */     {
/* 370:378 */       return (Image)new ColorFZSoille().preprocess(new Object[] { img, alpha, omega });
/* 371:    */     }
/* 372:    */     catch (GlobalException e)
/* 373:    */     {
/* 374:380 */       e.printStackTrace();
/* 375:    */     }
/* 376:381 */     return null;
/* 377:    */   }
/* 378:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorFZSoille
 * JD-Core Version:    0.7.0.1
 */
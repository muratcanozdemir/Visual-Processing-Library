/*   1:    */ package vpt.algorithms.flatzones.angular;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Comparator;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.DoubleImage;
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ import vpt.util.SortedList;
/*  15:    */ import vpt.util.Tools;
/*  16:    */ 
/*  17:    */ public class AngularQFZ6
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20:    */   public Image input;
/*  21:    */   public IntegerImage output;
/*  22:    */   public Double alpha;
/*  23:    */   public Double omega;
/*  24: 40 */   private ArrayList<Point> list = new ArrayList();
/*  25: 41 */   private ArrayList<Point> list2 = new ArrayList();
/*  26: 42 */   private Stack<Point> s = new Stack();
/*  27:    */   private int xdim;
/*  28:    */   private int ydim;
/*  29: 45 */   private int label = 1;
/*  30: 47 */   private SortedList[] angles = new SortedList[360];
/*  31:    */   BooleanImage temp;
/*  32:    */   DoubleImage localRanges;
/*  33: 56 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  34: 57 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  35:    */   
/*  36:    */   public AngularQFZ6()
/*  37:    */   {
/*  38: 60 */     this.inputFields = "input,alpha,omega";
/*  39: 61 */     this.outputFields = "output";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 66 */     this.xdim = this.input.getXDim();
/*  46: 67 */     this.ydim = this.input.getYDim();
/*  47: 69 */     if (this.input.getCDim() != 1) {
/*  48: 69 */       throw new GlobalException("This implementation is only for monochannel angular data images.");
/*  49:    */     }
/*  50: 70 */     if ((this.alpha.doubleValue() <= 0.0D) || (this.alpha.doubleValue() >= 1.0D) || (this.omega.doubleValue() <= 0.0D) || (this.omega.doubleValue() >= 1.0D)) {
/*  51: 70 */       throw new GlobalException("Arguments out of range.");
/*  52:    */     }
/*  53: 72 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  54: 73 */     this.output.fill(0);
/*  55:    */     
/*  56: 75 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  57: 76 */     this.temp.fill(false);
/*  58:    */     
/*  59: 78 */     this.localRanges = new DoubleImage(this.input, false);
/*  60: 79 */     this.localRanges.fill(1.0D);
/*  61: 81 */     for (int k = 0; k < this.angles.length; k++) {
/*  62: 82 */       this.angles[k] = new SortedList(new DoubleComparator(null));
/*  63:    */     }
/*  64: 84 */     for (int x = 0; x < this.xdim; x++)
/*  65:    */     {
/*  66: 87 */       System.out.println("Sutun " + x);
/*  67: 89 */       for (int y = 0; y < this.ydim; y++) {
/*  68: 92 */         if (this.output.getXYInt(x, y) <= 0)
/*  69:    */         {
/*  70: 94 */           Point t = new Point(x, y);
/*  71: 97 */           for (int k = 0; k < this.angles.length; k++) {
/*  72: 98 */             this.angles[k].clear();
/*  73:    */           }
/*  74:101 */           double step = createQCC(t, this.alpha.doubleValue());
/*  75:103 */           if (step > 0.0D)
/*  76:    */           {
/*  77:105 */             for (Point s : this.list) {
/*  78:106 */               this.localRanges.setXYDouble(s.x, s.y, this.alpha.doubleValue());
/*  79:    */             }
/*  80:    */           }
/*  81:    */           else
/*  82:    */           {
/*  83:109 */             double tmpAlpha = this.alpha.doubleValue();
/*  84:    */             do
/*  85:    */             {
/*  86:112 */               for (Point p : this.list) {
/*  87:113 */                 this.output.setXYInt(p.x, p.y, 0);
/*  88:    */               }
/*  89:116 */               for (int k = 0; k < this.angles.length; k++) {
/*  90:117 */                 this.angles[k].clear();
/*  91:    */               }
/*  92:119 */               this.list.clear();
/*  93:    */               
/*  94:    */ 
/*  95:    */ 
/*  96:123 */               tmpAlpha = Math.min(Math.abs(step) - 0.0001D, tmpAlpha - 0.0001D);
/*  97:    */               
/*  98:    */ 
/*  99:    */ 
/* 100:127 */               step = createQCC(t, tmpAlpha);
/* 101:129 */             } while (step <= 0.0D);
/* 102:134 */             for (Point s : this.list) {
/* 103:135 */               this.localRanges.setXYDouble(s.x, s.y, tmpAlpha);
/* 104:    */             }
/* 105:    */           }
/* 106:138 */           this.list.clear();
/* 107:    */           
/* 108:140 */           this.label += 1;
/* 109:    */         }
/* 110:    */       }
/* 111:    */     }
/* 112:144 */     System.err.println("Total number of quasi flat zones: " + (this.label - 1));
/* 113:    */   }
/* 114:    */   
/* 115:    */   private double createQCC(Point r, double alpha2)
/* 116:    */   {
/* 117:157 */     this.s.clear();
/* 118:158 */     this.s.add(r);
/* 119:159 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 120:160 */     this.list2.add(r);
/* 121:    */     
/* 122:162 */     double maxDist = 0.0D;
/* 123:163 */     double maxAlphaDist = 0.0D;
/* 124:    */     int x;
/* 125:    */     int i;
/* 126:165 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 127:    */     {
/* 128:167 */       Point tmp = (Point)this.s.pop();
/* 129:    */       
/* 130:169 */       x = tmp.x;
/* 131:170 */       int y = tmp.y;
/* 132:    */       
/* 133:172 */       double p = this.input.getXYDouble(x, y);
/* 134:173 */       this.list.add(tmp);
/* 135:    */       
/* 136:175 */       int index = (int)Math.floor(p * 360.0D);
/* 137:176 */       int antiIndex = (index + 180) % 360;
/* 138:178 */       if (!contains(this.angles[index], p)) {
/* 139:179 */         this.angles[index].add(Double.valueOf(p));
/* 140:    */       }
/* 141:181 */       int searchedBin = 0;
/* 142:183 */       for (int k = antiIndex;; k = (k + 1) % 360) {
/* 143:185 */         if (!this.angles[k].isEmpty())
/* 144:    */         {
/* 145:186 */           searchedBin = k;
/* 146:    */           
/* 147:188 */           int size = this.angles[k].size();
/* 148:189 */           if (size == 1)
/* 149:    */           {
/* 150:190 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 151:191 */             if (tmp1 <= maxDist) {
/* 152:    */               break;
/* 153:    */             }
/* 154:191 */             maxDist = tmp1;
/* 155:192 */             break;
/* 156:    */           }
/* 157:197 */           if (k == index)
/* 158:    */           {
/* 159:198 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 160:199 */             double tmp2 = Tools.hueDistance(((Double)this.angles[k].get(size - 1)).doubleValue(), p);
/* 161:200 */             tmp1 = Math.max(tmp1, tmp2);
/* 162:201 */             if (tmp1 <= maxDist) {
/* 163:    */               break;
/* 164:    */             }
/* 165:202 */             maxDist = tmp1;
/* 166:203 */             break;
/* 167:    */           }
/* 168:205 */           int first = 0;
/* 169:206 */           int last = size - 1;
/* 170:207 */           int mid = 0;
/* 171:    */           
/* 172:209 */           boolean found = false;
/* 173:210 */           double leftDist = 0.0D;
/* 174:211 */           double rightDist = 0.0D;
/* 175:    */           do
/* 176:    */           {
/* 177:214 */             mid = first + (last - first) / 2;
/* 178:    */             
/* 179:216 */             double midDist = Tools.hueDistance(((Double)this.angles[k].get(mid)).doubleValue(), p);
/* 180:218 */             if (mid > first) {
/* 181:218 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(mid - 1)).doubleValue(), p);
/* 182:    */             } else {
/* 183:219 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(first)).doubleValue(), p);
/* 184:    */             }
/* 185:221 */             if (mid < last) {
/* 186:221 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(mid + 1)).doubleValue(), p);
/* 187:    */             } else {
/* 188:222 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(last)).doubleValue(), p);
/* 189:    */             }
/* 190:224 */             if ((Tools.cmpr(rightDist, midDist) <= 0) && (Tools.cmpr(leftDist, midDist) <= 0))
/* 191:    */             {
/* 192:225 */               found = true;
/* 193:226 */               if (Tools.cmpr(midDist, maxDist) > 0) {
/* 194:226 */                 maxDist = midDist;
/* 195:    */               }
/* 196:    */             }
/* 197:228 */             else if (Tools.cmpr(leftDist, midDist) > 0)
/* 198:    */             {
/* 199:229 */               last = mid - 1;
/* 200:    */             }
/* 201:230 */             else if (Tools.cmpr(midDist, rightDist) < 0)
/* 202:    */             {
/* 203:231 */               first = mid + 1;
/* 204:    */             }
/* 205:    */             else
/* 206:    */             {
/* 207:233 */               System.err.println(first + " " + mid + " " + last);
/* 208:    */               
/* 209:235 */               System.err.println(leftDist + " " + rightDist);
/* 210:    */             }
/* 211:213 */             if (found) {
/* 212:    */               break;
/* 213:    */             }
/* 214:213 */           } while (first <= last);
/* 215:240 */           break;
/* 216:    */         }
/* 217:    */       }
/* 218:    */       int size;
/* 219:    */       double rightDist;
/* 220:244 */       for (int k = antiIndex - 1;; k--)
/* 221:    */       {
/* 222:246 */         if (k < 0) {
/* 223:246 */           k += 360;
/* 224:    */         }
/* 225:249 */         if (!this.angles[k].isEmpty())
/* 226:    */         {
/* 227:252 */           if (searchedBin == k) {
/* 228:    */             break;
/* 229:    */           }
/* 230:254 */           size = this.angles[k].size();
/* 231:255 */           if (size == 1)
/* 232:    */           {
/* 233:256 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 234:257 */             if (tmp1 <= maxDist) {
/* 235:    */               break;
/* 236:    */             }
/* 237:257 */             maxDist = tmp1;
/* 238:258 */             break;
/* 239:    */           }
/* 240:263 */           if (k == index)
/* 241:    */           {
/* 242:264 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 243:265 */             double tmp2 = Tools.hueDistance(((Double)this.angles[k].get(size - 1)).doubleValue(), p);
/* 244:266 */             tmp1 = Math.max(tmp1, tmp2);
/* 245:267 */             if (tmp1 <= maxDist) {
/* 246:    */               break;
/* 247:    */             }
/* 248:268 */             maxDist = tmp1;
/* 249:269 */             break;
/* 250:    */           }
/* 251:271 */           int first = 0;
/* 252:272 */           int last = size - 1;
/* 253:273 */           int mid = 0;
/* 254:    */           
/* 255:275 */           boolean found = false;
/* 256:276 */           double leftDist = 0.0D;
/* 257:277 */           rightDist = 0.0D;
/* 258:    */           do
/* 259:    */           {
/* 260:280 */             mid = first + (last - first) / 2;
/* 261:    */             
/* 262:282 */             double midDist = Tools.hueDistance(((Double)this.angles[k].get(mid)).doubleValue(), p);
/* 263:284 */             if (mid > first) {
/* 264:284 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(mid - 1)).doubleValue(), p);
/* 265:    */             } else {
/* 266:285 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(first)).doubleValue(), p);
/* 267:    */             }
/* 268:287 */             if (mid < last) {
/* 269:287 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(mid + 1)).doubleValue(), p);
/* 270:    */             } else {
/* 271:288 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(last)).doubleValue(), p);
/* 272:    */             }
/* 273:290 */             if ((Tools.cmpr(rightDist, midDist) <= 0) && (Tools.cmpr(leftDist, midDist) <= 0))
/* 274:    */             {
/* 275:291 */               found = true;
/* 276:292 */               if (Tools.cmpr(midDist, maxDist) > 0) {
/* 277:292 */                 maxDist = midDist;
/* 278:    */               }
/* 279:    */             }
/* 280:294 */             else if (Tools.cmpr(leftDist, midDist) > 0)
/* 281:    */             {
/* 282:295 */               last = mid - 1;
/* 283:    */             }
/* 284:296 */             else if (Tools.cmpr(midDist, rightDist) < 0)
/* 285:    */             {
/* 286:297 */               first = mid + 1;
/* 287:    */             }
/* 288:    */             else
/* 289:    */             {
/* 290:299 */               System.err.println(first + " " + mid + " " + last);
/* 291:    */               
/* 292:301 */               System.err.println(leftDist + " " + rightDist);
/* 293:    */             }
/* 294:279 */             if (found) {
/* 295:    */               break;
/* 296:    */             }
/* 297:279 */           } while (first <= last);
/* 298:306 */           break;
/* 299:    */         }
/* 300:    */       }
/* 301:311 */       if (maxDist > this.omega.doubleValue())
/* 302:    */       {
/* 303:312 */         for (Point t : this.list2) {
/* 304:313 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 305:    */         }
/* 306:314 */         this.list2.clear();
/* 307:    */         
/* 308:316 */         return -1.0D * maxAlphaDist;
/* 309:    */       }
/* 310:319 */       this.output.setXYInt(x, y, this.label);
/* 311:    */       
/* 312:321 */       i = 0; continue;
/* 313:322 */       int _x = x + this.N[i].x;
/* 314:323 */       int _y = y + this.N[i].y;
/* 315:325 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 316:    */       {
/* 317:327 */         double q = this.input.getXYDouble(_x, _y);
/* 318:329 */         if (this.output.getXYInt(_x, _y) > 0)
/* 319:    */         {
/* 320:331 */           double localR = this.localRanges.getXYDouble(_x, _y);
/* 321:333 */           if ((Tools.hueDistance(p, q) <= alpha2) && (localR <= alpha2))
/* 322:    */           {
/* 323:335 */             for (Point t : this.list2) {
/* 324:336 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 325:    */             }
/* 326:337 */             this.list2.clear();
/* 327:    */             
/* 328:339 */             return -1.0D;
/* 329:    */           }
/* 330:    */         }
/* 331:345 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 332:    */         {
/* 333:347 */           double tmpAlphaDist = Tools.hueDistance(p, q);
/* 334:349 */           if (tmpAlphaDist <= alpha2)
/* 335:    */           {
/* 336:351 */             if (tmpAlphaDist > maxAlphaDist) {
/* 337:351 */               maxAlphaDist = tmpAlphaDist;
/* 338:    */             }
/* 339:353 */             Point t = new Point(_x, _y);
/* 340:354 */             this.s.add(t);
/* 341:    */             
/* 342:356 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 343:357 */             this.list2.add(t);
/* 344:    */           }
/* 345:    */         }
/* 346:    */       }
/* 347:321 */       i++;
/* 348:    */     }
/* 349:362 */     for (Point t : this.list2) {
/* 350:363 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 351:    */     }
/* 352:364 */     this.list2.clear();
/* 353:    */     
/* 354:366 */     return 1.0D;
/* 355:    */   }
/* 356:    */   
/* 357:    */   private boolean contains(SortedList s, double p)
/* 358:    */   {
/* 359:381 */     int first = 0;
/* 360:382 */     int last = s.size() - 1;
/* 361:383 */     int mid = 0;
/* 362:    */     
/* 363:385 */     boolean found = false;
/* 364:387 */     while ((!found) && (first <= last))
/* 365:    */     {
/* 366:388 */       mid = first + (last - first) / 2;
/* 367:390 */       if (((Double)s.get(mid)).doubleValue() == p) {
/* 368:390 */         found = true;
/* 369:391 */       } else if (((Double)s.get(mid)).doubleValue() > p) {
/* 370:391 */         last = mid - 1;
/* 371:392 */       } else if (((Double)s.get(mid)).doubleValue() < p) {
/* 372:392 */         first = mid + 1;
/* 373:    */       }
/* 374:    */     }
/* 375:395 */     return found;
/* 376:    */   }
/* 377:    */   
/* 378:    */   private class DoubleComparator
/* 379:    */     implements Comparator
/* 380:    */   {
/* 381:    */     private DoubleComparator() {}
/* 382:    */     
/* 383:    */     public int compare(Object arg0, Object arg1)
/* 384:    */     {
/* 385:    */       try
/* 386:    */       {
/* 387:402 */         double d0 = ((Double)arg0).doubleValue();
/* 388:403 */         double d1 = ((Double)arg1).doubleValue();
/* 389:405 */         if (d0 < d1) {
/* 390:405 */           return -1;
/* 391:    */         }
/* 392:406 */         if (d0 > d1) {
/* 393:406 */           return 1;
/* 394:    */         }
/* 395:407 */         return 0;
/* 396:    */       }
/* 397:    */       catch (Exception e)
/* 398:    */       {
/* 399:410 */         e.printStackTrace();
/* 400:    */       }
/* 401:411 */       return 0;
/* 402:    */     }
/* 403:    */   }
/* 404:    */   
/* 405:    */   public static Image invoke(Image img, double alpha, double omega)
/* 406:    */   {
/* 407:    */     try
/* 408:    */     {
/* 409:418 */       return (IntegerImage)new AngularQFZ6().preprocess(new Object[] { img, Double.valueOf(alpha), Double.valueOf(omega) });
/* 410:    */     }
/* 411:    */     catch (GlobalException e)
/* 412:    */     {
/* 413:420 */       e.printStackTrace();
/* 414:    */     }
/* 415:421 */     return null;
/* 416:    */   }
/* 417:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.angular.AngularQFZ6
 * JD-Core Version:    0.7.0.1
 */
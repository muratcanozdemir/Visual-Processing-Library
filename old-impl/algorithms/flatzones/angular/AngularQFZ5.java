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
/*  17:    */ public class AngularQFZ5
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20:    */   public Image input;
/*  21:    */   public IntegerImage output;
/*  22:    */   public Double alpha;
/*  23:    */   public Double omega;
/*  24: 35 */   private ArrayList<Point> list = new ArrayList();
/*  25: 36 */   private ArrayList<Point> list2 = new ArrayList();
/*  26: 37 */   private Stack<Point> s = new Stack();
/*  27:    */   private int xdim;
/*  28:    */   private int ydim;
/*  29: 40 */   private int label = 1;
/*  30: 42 */   private SortedList[] angles = new SortedList[360];
/*  31:    */   BooleanImage temp;
/*  32:    */   DoubleImage localRanges;
/*  33: 51 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  34: 52 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  35:    */   
/*  36:    */   public AngularQFZ5()
/*  37:    */   {
/*  38: 55 */     this.inputFields = "input,alpha,omega";
/*  39: 56 */     this.outputFields = "output";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 61 */     this.xdim = this.input.getXDim();
/*  46: 62 */     this.ydim = this.input.getYDim();
/*  47: 64 */     if (this.input.getCDim() != 1) {
/*  48: 64 */       throw new GlobalException("This implementation is only for monochannel angular data images.");
/*  49:    */     }
/*  50: 65 */     if ((this.alpha.doubleValue() <= 0.0D) || (this.alpha.doubleValue() >= 1.0D) || (this.omega.doubleValue() <= 0.0D) || (this.omega.doubleValue() >= 1.0D)) {
/*  51: 65 */       throw new GlobalException("Arguments out of range.");
/*  52:    */     }
/*  53: 67 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  54: 68 */     this.output.fill(0);
/*  55:    */     
/*  56: 70 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  57: 71 */     this.temp.fill(false);
/*  58:    */     
/*  59: 73 */     this.localRanges = new DoubleImage(this.input, false);
/*  60: 74 */     this.localRanges.fill(1.0D);
/*  61: 76 */     for (int k = 0; k < this.angles.length; k++) {
/*  62: 77 */       this.angles[k] = new SortedList(new DoubleComparator(null));
/*  63:    */     }
/*  64: 79 */     for (int x = 0; x < this.xdim; x++)
/*  65:    */     {
/*  66: 82 */       System.out.println("Sutun " + x);
/*  67: 84 */       for (int y = 0; y < this.ydim; y++) {
/*  68: 87 */         if (this.output.getXYInt(x, y) <= 0)
/*  69:    */         {
/*  70: 89 */           Point t = new Point(x, y);
/*  71: 92 */           for (int k = 0; k < this.angles.length; k++) {
/*  72: 93 */             this.angles[k].clear();
/*  73:    */           }
/*  74: 95 */           if (createQCC(t, this.alpha.doubleValue()) > 0)
/*  75:    */           {
/*  76: 97 */             for (Point s : this.list) {
/*  77: 98 */               this.localRanges.setXYDouble(s.x, s.y, this.alpha.doubleValue());
/*  78:    */             }
/*  79:    */           }
/*  80:    */           else
/*  81:    */           {
/*  82:101 */             double tmpAlpha = this.alpha.doubleValue();
/*  83:    */             do
/*  84:    */             {
/*  85:104 */               for (Point p : this.list) {
/*  86:105 */                 this.output.setXYInt(p.x, p.y, 0);
/*  87:    */               }
/*  88:108 */               for (int k = 0; k < this.angles.length; k++) {
/*  89:109 */                 this.angles[k].clear();
/*  90:    */               }
/*  91:111 */               this.list.clear();
/*  92:    */               
/*  93:113 */               tmpAlpha -= 0.001D;
/*  94:115 */             } while (createQCC(t, tmpAlpha) <= 0);
/*  95:120 */             for (Point s : this.list) {
/*  96:121 */               this.localRanges.setXYDouble(s.x, s.y, tmpAlpha);
/*  97:    */             }
/*  98:    */           }
/*  99:124 */           this.list.clear();
/* 100:    */           
/* 101:126 */           this.label += 1;
/* 102:    */         }
/* 103:    */       }
/* 104:    */     }
/* 105:130 */     System.err.println("Total number of quasi flat zones: " + (this.label - 1));
/* 106:    */   }
/* 107:    */   
/* 108:    */   private int createQCC(Point r, double alpha2)
/* 109:    */   {
/* 110:143 */     this.s.clear();
/* 111:144 */     this.s.add(r);
/* 112:145 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 113:146 */     this.list2.add(r);
/* 114:    */     
/* 115:148 */     double maxDist = 0.0D;
/* 116:    */     int x;
/* 117:    */     int i;
/* 118:150 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 119:    */     {
/* 120:152 */       Point tmp = (Point)this.s.pop();
/* 121:    */       
/* 122:154 */       x = tmp.x;
/* 123:155 */       int y = tmp.y;
/* 124:    */       
/* 125:157 */       double p = this.input.getXYDouble(x, y);
/* 126:158 */       this.list.add(tmp);
/* 127:    */       
/* 128:160 */       int index = (int)Math.floor(p * 360.0D);
/* 129:161 */       int antiIndex = (index + 180) % 360;
/* 130:163 */       if (!contains(this.angles[index], p)) {
/* 131:164 */         this.angles[index].add(Double.valueOf(p));
/* 132:    */       }
/* 133:166 */       int searchedBin = 0;
/* 134:168 */       for (int k = antiIndex;; k = (k + 1) % 360) {
/* 135:170 */         if (!this.angles[k].isEmpty())
/* 136:    */         {
/* 137:171 */           searchedBin = k;
/* 138:    */           
/* 139:173 */           int size = this.angles[k].size();
/* 140:174 */           if (size == 1)
/* 141:    */           {
/* 142:175 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 143:176 */             if (tmp1 <= maxDist) {
/* 144:    */               break;
/* 145:    */             }
/* 146:176 */             maxDist = tmp1;
/* 147:177 */             break;
/* 148:    */           }
/* 149:182 */           if (k == index)
/* 150:    */           {
/* 151:183 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 152:184 */             double tmp2 = Tools.hueDistance(((Double)this.angles[k].get(size - 1)).doubleValue(), p);
/* 153:185 */             tmp1 = Math.max(tmp1, tmp2);
/* 154:186 */             if (tmp1 <= maxDist) {
/* 155:    */               break;
/* 156:    */             }
/* 157:187 */             maxDist = tmp1;
/* 158:188 */             break;
/* 159:    */           }
/* 160:190 */           int first = 0;
/* 161:191 */           int last = size - 1;
/* 162:192 */           int mid = 0;
/* 163:    */           
/* 164:194 */           boolean found = false;
/* 165:195 */           double leftDist = 0.0D;
/* 166:196 */           double rightDist = 0.0D;
/* 167:    */           do
/* 168:    */           {
/* 169:199 */             mid = first + (last - first) / 2;
/* 170:    */             
/* 171:201 */             double midDist = Tools.hueDistance(((Double)this.angles[k].get(mid)).doubleValue(), p);
/* 172:203 */             if (mid > first) {
/* 173:203 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(mid - 1)).doubleValue(), p);
/* 174:    */             } else {
/* 175:204 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(first)).doubleValue(), p);
/* 176:    */             }
/* 177:206 */             if (mid < last) {
/* 178:206 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(mid + 1)).doubleValue(), p);
/* 179:    */             } else {
/* 180:207 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(last)).doubleValue(), p);
/* 181:    */             }
/* 182:209 */             if ((Tools.cmpr(rightDist, midDist) <= 0) && (Tools.cmpr(leftDist, midDist) <= 0))
/* 183:    */             {
/* 184:210 */               found = true;
/* 185:211 */               if (Tools.cmpr(midDist, maxDist) > 0) {
/* 186:211 */                 maxDist = midDist;
/* 187:    */               }
/* 188:    */             }
/* 189:213 */             else if (Tools.cmpr(leftDist, midDist) > 0)
/* 190:    */             {
/* 191:214 */               last = mid - 1;
/* 192:    */             }
/* 193:215 */             else if (Tools.cmpr(midDist, rightDist) < 0)
/* 194:    */             {
/* 195:216 */               first = mid + 1;
/* 196:    */             }
/* 197:    */             else
/* 198:    */             {
/* 199:218 */               System.err.println(first + " " + mid + " " + last);
/* 200:    */               
/* 201:220 */               System.err.println(leftDist + " " + rightDist);
/* 202:    */             }
/* 203:198 */             if (found) {
/* 204:    */               break;
/* 205:    */             }
/* 206:198 */           } while (first <= last);
/* 207:225 */           break;
/* 208:    */         }
/* 209:    */       }
/* 210:    */       int size;
/* 211:    */       double rightDist;
/* 212:229 */       for (int k = antiIndex - 1;; k--)
/* 213:    */       {
/* 214:231 */         if (k < 0) {
/* 215:231 */           k += 360;
/* 216:    */         }
/* 217:234 */         if (!this.angles[k].isEmpty())
/* 218:    */         {
/* 219:237 */           if (searchedBin == k) {
/* 220:    */             break;
/* 221:    */           }
/* 222:239 */           size = this.angles[k].size();
/* 223:240 */           if (size == 1)
/* 224:    */           {
/* 225:241 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 226:242 */             if (tmp1 <= maxDist) {
/* 227:    */               break;
/* 228:    */             }
/* 229:242 */             maxDist = tmp1;
/* 230:243 */             break;
/* 231:    */           }
/* 232:248 */           if (k == index)
/* 233:    */           {
/* 234:249 */             double tmp1 = Tools.hueDistance(((Double)this.angles[k].get(0)).doubleValue(), p);
/* 235:250 */             double tmp2 = Tools.hueDistance(((Double)this.angles[k].get(size - 1)).doubleValue(), p);
/* 236:251 */             tmp1 = Math.max(tmp1, tmp2);
/* 237:252 */             if (tmp1 <= maxDist) {
/* 238:    */               break;
/* 239:    */             }
/* 240:253 */             maxDist = tmp1;
/* 241:254 */             break;
/* 242:    */           }
/* 243:256 */           int first = 0;
/* 244:257 */           int last = size - 1;
/* 245:258 */           int mid = 0;
/* 246:    */           
/* 247:260 */           boolean found = false;
/* 248:261 */           double leftDist = 0.0D;
/* 249:262 */           rightDist = 0.0D;
/* 250:    */           do
/* 251:    */           {
/* 252:265 */             mid = first + (last - first) / 2;
/* 253:    */             
/* 254:267 */             double midDist = Tools.hueDistance(((Double)this.angles[k].get(mid)).doubleValue(), p);
/* 255:269 */             if (mid > first) {
/* 256:269 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(mid - 1)).doubleValue(), p);
/* 257:    */             } else {
/* 258:270 */               leftDist = Tools.hueDistance(((Double)this.angles[k].get(first)).doubleValue(), p);
/* 259:    */             }
/* 260:272 */             if (mid < last) {
/* 261:272 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(mid + 1)).doubleValue(), p);
/* 262:    */             } else {
/* 263:273 */               rightDist = Tools.hueDistance(((Double)this.angles[k].get(last)).doubleValue(), p);
/* 264:    */             }
/* 265:275 */             if ((Tools.cmpr(rightDist, midDist) <= 0) && (Tools.cmpr(leftDist, midDist) <= 0))
/* 266:    */             {
/* 267:276 */               found = true;
/* 268:277 */               if (Tools.cmpr(midDist, maxDist) > 0) {
/* 269:277 */                 maxDist = midDist;
/* 270:    */               }
/* 271:    */             }
/* 272:279 */             else if (Tools.cmpr(leftDist, midDist) > 0)
/* 273:    */             {
/* 274:280 */               last = mid - 1;
/* 275:    */             }
/* 276:281 */             else if (Tools.cmpr(midDist, rightDist) < 0)
/* 277:    */             {
/* 278:282 */               first = mid + 1;
/* 279:    */             }
/* 280:    */             else
/* 281:    */             {
/* 282:284 */               System.err.println(first + " " + mid + " " + last);
/* 283:    */               
/* 284:286 */               System.err.println(leftDist + " " + rightDist);
/* 285:    */             }
/* 286:264 */             if (found) {
/* 287:    */               break;
/* 288:    */             }
/* 289:264 */           } while (first <= last);
/* 290:291 */           break;
/* 291:    */         }
/* 292:    */       }
/* 293:296 */       if (maxDist > this.omega.doubleValue())
/* 294:    */       {
/* 295:297 */         for (Point t : this.list2) {
/* 296:298 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 297:    */         }
/* 298:299 */         this.list2.clear();
/* 299:    */         
/* 300:301 */         return -1;
/* 301:    */       }
/* 302:304 */       this.output.setXYInt(x, y, this.label);
/* 303:    */       
/* 304:306 */       i = 0; continue;
/* 305:307 */       int _x = x + this.N[i].x;
/* 306:308 */       int _y = y + this.N[i].y;
/* 307:310 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 308:    */       {
/* 309:312 */         double q = this.input.getXYDouble(_x, _y);
/* 310:314 */         if (this.output.getXYInt(_x, _y) > 0)
/* 311:    */         {
/* 312:316 */           double localR = this.localRanges.getXYDouble(_x, _y);
/* 313:318 */           if ((Tools.hueDistance(p, q) <= alpha2) && (localR <= alpha2))
/* 314:    */           {
/* 315:320 */             for (Point t : this.list2) {
/* 316:321 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 317:    */             }
/* 318:322 */             this.list2.clear();
/* 319:    */             
/* 320:324 */             return -1;
/* 321:    */           }
/* 322:    */         }
/* 323:330 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 324:    */         {
/* 325:332 */           if (Tools.hueDistance(p, q) <= alpha2)
/* 326:    */           {
/* 327:334 */             Point t = new Point(_x, _y);
/* 328:335 */             this.s.add(t);
/* 329:    */             
/* 330:337 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 331:338 */             this.list2.add(t);
/* 332:    */           }
/* 333:    */         }
/* 334:    */       }
/* 335:306 */       i++;
/* 336:    */     }
/* 337:343 */     for (Point t : this.list2) {
/* 338:344 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 339:    */     }
/* 340:345 */     this.list2.clear();
/* 341:    */     
/* 342:347 */     return 1;
/* 343:    */   }
/* 344:    */   
/* 345:    */   private boolean contains(SortedList s, double p)
/* 346:    */   {
/* 347:362 */     int first = 0;
/* 348:363 */     int last = s.size() - 1;
/* 349:364 */     int mid = 0;
/* 350:    */     
/* 351:366 */     boolean found = false;
/* 352:368 */     while ((!found) && (first <= last))
/* 353:    */     {
/* 354:369 */       mid = first + (last - first) / 2;
/* 355:371 */       if (((Double)s.get(mid)).doubleValue() == p) {
/* 356:371 */         found = true;
/* 357:372 */       } else if (((Double)s.get(mid)).doubleValue() > p) {
/* 358:372 */         last = mid - 1;
/* 359:373 */       } else if (((Double)s.get(mid)).doubleValue() < p) {
/* 360:373 */         first = mid + 1;
/* 361:    */       }
/* 362:    */     }
/* 363:376 */     return found;
/* 364:    */   }
/* 365:    */   
/* 366:    */   private class DoubleComparator
/* 367:    */     implements Comparator
/* 368:    */   {
/* 369:    */     private DoubleComparator() {}
/* 370:    */     
/* 371:    */     public int compare(Object arg0, Object arg1)
/* 372:    */     {
/* 373:    */       try
/* 374:    */       {
/* 375:383 */         double d0 = ((Double)arg0).doubleValue();
/* 376:384 */         double d1 = ((Double)arg1).doubleValue();
/* 377:386 */         if (d0 < d1) {
/* 378:386 */           return -1;
/* 379:    */         }
/* 380:387 */         if (d0 > d1) {
/* 381:387 */           return 1;
/* 382:    */         }
/* 383:388 */         return 0;
/* 384:    */       }
/* 385:    */       catch (Exception e)
/* 386:    */       {
/* 387:391 */         e.printStackTrace();
/* 388:    */       }
/* 389:392 */       return 0;
/* 390:    */     }
/* 391:    */   }
/* 392:    */   
/* 393:    */   public static Image invoke(Image img, double alpha, double omega)
/* 394:    */   {
/* 395:    */     try
/* 396:    */     {
/* 397:399 */       return (IntegerImage)new AngularQFZ5().preprocess(new Object[] { img, Double.valueOf(alpha), Double.valueOf(omega) });
/* 398:    */     }
/* 399:    */     catch (GlobalException e)
/* 400:    */     {
/* 401:401 */       e.printStackTrace();
/* 402:    */     }
/* 403:402 */     return null;
/* 404:    */   }
/* 405:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.angular.AngularQFZ5
 * JD-Core Version:    0.7.0.1
 */
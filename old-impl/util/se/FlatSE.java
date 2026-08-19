/*   1:    */ package vpt.util.se;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.util.Distance;
/*   8:    */ 
/*   9:    */ public class FlatSE
/*  10:    */   extends BooleanImage
/*  11:    */ {
/*  12:    */   private Point center;
/*  13:    */   private Point[] coords;
/*  14:    */   private ArrayList<Point> listCoords;
/*  15: 22 */   public boolean BOX = false;
/*  16:    */   
/*  17:    */   public FlatSE(int xdim, int ydim, Point center)
/*  18:    */   {
/*  19: 26 */     super(xdim, ydim, 1);
/*  20: 27 */     this.center = center;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static FlatSE square(int size)
/*  24:    */   {
/*  25: 32 */     size = sizeControl(size);
/*  26:    */     
/*  27: 34 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/*  28: 35 */     se.fill(true);
/*  29: 36 */     se.BOX = true;
/*  30:    */     
/*  31: 38 */     return se;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static FlatSE hollowSquare(int size)
/*  35:    */   {
/*  36: 42 */     size = sizeControl(size);
/*  37:    */     
/*  38: 44 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/*  39: 45 */     se.fill(false);
/*  40: 47 */     for (int x = 0; x < size; x++) {
/*  41: 48 */       for (int y = 0; y < size; y++) {
/*  42: 49 */         if ((x == 0) || (x == size - 1) || (y == 0) || (y == size - 1)) {
/*  43: 50 */           se.setXYBoolean(x, y, true);
/*  44:    */         }
/*  45:    */       }
/*  46:    */     }
/*  47: 54 */     return se;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static FlatSE disk(int radius)
/*  51:    */   {
/*  52: 58 */     if (radius < 1) {
/*  53: 58 */       radius = 1;
/*  54:    */     }
/*  55: 59 */     int size = radius * 2 + 1;
/*  56:    */     
/*  57: 61 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/*  58: 62 */     se.fill(false);
/*  59: 64 */     for (int x = 0; x < size; x++) {
/*  60: 65 */       for (int y = 0; y < size; y++)
/*  61:    */       {
/*  62: 66 */         double dist = Distance.EuclideanDistance(x, y, se.center.x, se.center.y);
/*  63: 67 */         if (dist <= radius) {
/*  64: 68 */           se.setXYBoolean(x, y, true);
/*  65:    */         }
/*  66:    */       }
/*  67:    */     }
/*  68: 72 */     return se;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static FlatSE partiallyFilledDisk(int radius, int radius2)
/*  72:    */   {
/*  73: 76 */     FlatSE se = circle(radius);
/*  74:    */     
/*  75:    */ 
/*  76: 79 */     int size = radius * 2 + 1;
/*  77: 81 */     for (int x = 0; x < size; x++) {
/*  78: 82 */       for (int y = 0; y < size; y++)
/*  79:    */       {
/*  80: 83 */         double dist = Distance.EuclideanDistance(x, y, se.center.x, se.center.y);
/*  81: 84 */         if (dist <= radius2) {
/*  82: 85 */           se.setXYBoolean(x, y, true);
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86: 89 */     return se;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static FlatSE[] partialArcs(int radius)
/*  90:    */   {
/*  91: 93 */     if (radius < 1) {
/*  92: 93 */       radius = 1;
/*  93:    */     }
/*  94: 94 */     int size = radius * 2 + 1;
/*  95:    */     
/*  96: 96 */     FlatSE[] ses = new FlatSE[4];
/*  97: 98 */     for (int j = 0; j < 4; j++)
/*  98:    */     {
/*  99: 99 */       ses[j] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 100:100 */       ses[j].fill(false);
/* 101:    */       
/* 102:102 */       ses[j].setXYBoolean(radius, radius, true);
/* 103:    */       
/* 104:104 */       int perimeter = 8 * radius;
/* 105:    */       
/* 106:106 */       int q = perimeter / 4;
/* 107:108 */       for (int i = j * q; i < q * (j + 1); i++)
/* 108:    */       {
/* 109:109 */         int x = ses[j].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 110:110 */         int y = ses[j].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 111:111 */         ses[j].setXYBoolean(x, y, true);
/* 112:    */       }
/* 113:    */     }
/* 114:115 */     return ses;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static FlatSE[] partialArcs2(int radius)
/* 118:    */   {
/* 119:119 */     if (radius < 1) {
/* 120:119 */       radius = 1;
/* 121:    */     }
/* 122:120 */     int size = radius * 2 + 1;
/* 123:    */     
/* 124:122 */     FlatSE[] ses = new FlatSE[8];
/* 125:124 */     for (int j = 0; j < 8; j++)
/* 126:    */     {
/* 127:125 */       ses[j] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 128:126 */       ses[j].fill(false);
/* 129:    */       
/* 130:128 */       ses[j].setXYBoolean(radius, radius, true);
/* 131:    */       
/* 132:130 */       int perimeter = 8 * radius;
/* 133:    */       
/* 134:132 */       int q = perimeter / 8;
/* 135:134 */       for (int i = j * q; i < q * (j + 1); i++)
/* 136:    */       {
/* 137:135 */         int x = ses[j].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 138:136 */         int y = ses[j].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 139:137 */         ses[j].setXYBoolean(x, y, true);
/* 140:    */       }
/* 141:    */     }
/* 142:141 */     return ses;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static FlatSE[] partialCircles(int radius)
/* 146:    */   {
/* 147:145 */     if (radius < 1) {
/* 148:145 */       radius = 1;
/* 149:    */     }
/* 150:146 */     int size = radius * 2 + 1;
/* 151:    */     
/* 152:148 */     FlatSE[] ses = new FlatSE[4];
/* 153:150 */     for (int j = 0; j < 4; j++)
/* 154:    */     {
/* 155:151 */       ses[j] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 156:152 */       ses[j].fill(false);
/* 157:    */       
/* 158:154 */       ses[j].setXYBoolean(radius, radius, true);
/* 159:    */       
/* 160:156 */       int perimeter = 8 * radius;
/* 161:    */       
/* 162:158 */       int q = perimeter / 4;
/* 163:160 */       for (int i = 0; i < perimeter; i++) {
/* 164:161 */         if ((i < j * q) || (i >= (j + 1) * q))
/* 165:    */         {
/* 166:162 */           int x = ses[j].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 167:163 */           int y = ses[j].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 168:164 */           ses[j].setXYBoolean(x, y, true);
/* 169:    */         }
/* 170:    */       }
/* 171:    */     }
/* 172:168 */     return ses;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static FlatSE[] partialCircles2(int radius)
/* 176:    */   {
/* 177:172 */     if (radius < 1) {
/* 178:172 */       radius = 1;
/* 179:    */     }
/* 180:173 */     int size = radius * 2 + 1;
/* 181:    */     
/* 182:175 */     FlatSE[] ses = new FlatSE[4];
/* 183:    */     
/* 184:    */ 
/* 185:178 */     ses[0] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 186:179 */     ses[0].fill(false);
/* 187:180 */     ses[0].setXYBoolean(radius, radius, true);
/* 188:    */     
/* 189:182 */     int perimeter = 8 * radius;
/* 190:183 */     int q = perimeter / 4;
/* 191:184 */     int r = perimeter / 8;
/* 192:186 */     for (int i = 0; i < perimeter; i++) {
/* 193:187 */       if (((i < 0) || (i >= q)) && (
/* 194:188 */         (i < 2 * q) || (i >= 3 * q)))
/* 195:    */       {
/* 196:189 */         int x = ses[0].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 197:190 */         int y = ses[0].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 198:191 */         ses[0].setXYBoolean(x, y, true);
/* 199:    */       }
/* 200:    */     }
/* 201:195 */     ses[1] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 202:196 */     ses[1].fill(false);
/* 203:197 */     ses[1].setXYBoolean(radius, radius, true);
/* 204:199 */     for (int i = 0; i < perimeter; i++) {
/* 205:200 */       if (((i < q) || (i >= 2 * q)) && (
/* 206:201 */         (i < 3 * q) || (i >= 4 * q)))
/* 207:    */       {
/* 208:203 */         int x = ses[1].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 209:204 */         int y = ses[1].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 210:205 */         ses[1].setXYBoolean(x, y, true);
/* 211:    */       }
/* 212:    */     }
/* 213:209 */     ses[2] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 214:210 */     ses[2].fill(false);
/* 215:211 */     ses[2].setXYBoolean(radius, radius, true);
/* 216:213 */     for (int i = 0; i < perimeter; i++) {
/* 217:214 */       if ((i < 7 * r) && (i >= r) && (
/* 218:215 */         (i < 3 * r) || (i >= 5 * r)))
/* 219:    */       {
/* 220:217 */         int x = ses[2].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 221:218 */         int y = ses[2].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 222:219 */         ses[2].setXYBoolean(x, y, true);
/* 223:    */       }
/* 224:    */     }
/* 225:223 */     ses[3] = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 226:224 */     ses[3].fill(false);
/* 227:225 */     ses[3].setXYBoolean(radius, radius, true);
/* 228:227 */     for (int i = 0; i < perimeter; i++) {
/* 229:228 */       if (((i < r) || (i >= 3 * r)) && (
/* 230:229 */         (i < 5 * r) || (i >= 7 * r)))
/* 231:    */       {
/* 232:231 */         int x = ses[3].center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 233:232 */         int y = ses[3].center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 234:233 */         ses[3].setXYBoolean(x, y, true);
/* 235:    */       }
/* 236:    */     }
/* 237:236 */     return ses;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static FlatSE circle(int radius)
/* 241:    */   {
/* 242:242 */     if (radius < 1) {
/* 243:242 */       radius = 1;
/* 244:    */     }
/* 245:243 */     int size = radius * 2 + 1;
/* 246:    */     
/* 247:245 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 248:246 */     se.fill(false);
/* 249:    */     
/* 250:248 */     se.setXYBoolean(radius, radius, true);
/* 251:    */     
/* 252:250 */     int perimeter = 8 * radius;
/* 253:252 */     for (int i = 0; i < perimeter; i++)
/* 254:    */     {
/* 255:253 */       int x = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 256:254 */       int y = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 257:255 */       se.setXYBoolean(x, y, true);
/* 258:    */     }
/* 259:258 */     return se;
/* 260:    */   }
/* 261:    */   
/* 262:    */   public static FlatSE[] pointCouples2(int radius)
/* 263:    */   {
/* 264:269 */     if (radius < 1) {
/* 265:269 */       radius = 1;
/* 266:    */     }
/* 267:270 */     int size = radius * 2 + 1;
/* 268:    */     
/* 269:272 */     ArrayList<FlatSE> ses = new ArrayList();
/* 270:273 */     int perimeter = 8 * radius;
/* 271:    */     
/* 272:275 */     FlatSE prev = null;
/* 273:277 */     for (int i = 0; i < perimeter; i++)
/* 274:    */     {
/* 275:278 */       FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 276:279 */       se.fill(false);
/* 277:    */       
/* 278:281 */       se.setXYBoolean(se.center.x, se.center.y, true);
/* 279:    */       
/* 280:283 */       int x1 = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 281:284 */       int y1 = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 282:285 */       se.setXYBoolean(x1, y1, true);
/* 283:290 */       if ((x1 != se.center.x) || (y1 != se.center.y)) {
/* 284:293 */         if (prev != null)
/* 285:    */         {
/* 286:294 */           if ((prev.getCoords()[1].x != se.getCoords()[1].x) || (prev.getCoords()[1].y != se.getCoords()[1].y))
/* 287:    */           {
/* 288:295 */             ses.add(se);
/* 289:296 */             prev = se;
/* 290:    */           }
/* 291:    */         }
/* 292:    */         else
/* 293:    */         {
/* 294:299 */           ses.add(se);
/* 295:300 */           prev = se;
/* 296:    */         }
/* 297:    */       }
/* 298:    */     }
/* 299:304 */     FlatSE[] arraySe = new FlatSE[ses.size()];
/* 300:305 */     arraySe = (FlatSE[])ses.toArray(arraySe);
/* 301:    */     
/* 302:307 */     return arraySe;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public static FlatSE[] pointCouples(int radius)
/* 306:    */   {
/* 307:311 */     if (radius < 1) {
/* 308:311 */       radius = 1;
/* 309:    */     }
/* 310:312 */     int size = radius * 2 + 1;
/* 311:    */     
/* 312:314 */     ArrayList<FlatSE> ses = new ArrayList();
/* 313:315 */     int perimeter = 8 * radius;
/* 314:    */     
/* 315:317 */     FlatSE prev = null;
/* 316:319 */     for (int i = 0; i < perimeter / 2; i++)
/* 317:    */     {
/* 318:320 */       FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 319:321 */       se.fill(false);
/* 320:    */       
/* 321:323 */       se.setXYBoolean(se.center.x, se.center.y, true);
/* 322:    */       
/* 323:325 */       int x1 = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 324:326 */       int y1 = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 325:327 */       se.setXYBoolean(x1, y1, true);
/* 326:    */       
/* 327:329 */       int x2 = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * ((i + perimeter / 2) % perimeter) / perimeter));
/* 328:330 */       int y2 = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * ((i + perimeter / 2) % perimeter) / perimeter));
/* 329:331 */       se.setXYBoolean(x2, y2, true);
/* 330:336 */       if ((x1 != x2) || (y1 != y2)) {
/* 331:339 */         if (prev != null)
/* 332:    */         {
/* 333:340 */           if ((prev.getCoords()[1].x != se.getCoords()[1].x) || (prev.getCoords()[1].y != se.getCoords()[1].y))
/* 334:    */           {
/* 335:341 */             ses.add(se);
/* 336:342 */             prev = se;
/* 337:    */           }
/* 338:    */         }
/* 339:    */         else
/* 340:    */         {
/* 341:345 */           ses.add(se);
/* 342:346 */           prev = se;
/* 343:    */         }
/* 344:    */       }
/* 345:    */     }
/* 346:350 */     FlatSE[] arraySe = new FlatSE[ses.size()];
/* 347:351 */     arraySe = (FlatSE[])ses.toArray(arraySe);
/* 348:    */     
/* 349:353 */     return arraySe;
/* 350:    */   }
/* 351:    */   
/* 352:    */   public static FlatSE centerlessCircle(int radius)
/* 353:    */   {
/* 354:358 */     if (radius < 1) {
/* 355:358 */       radius = 1;
/* 356:    */     }
/* 357:359 */     int size = radius * 2 + 1;
/* 358:    */     
/* 359:361 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 360:362 */     se.fill(false);
/* 361:363 */     se.listCoords = new ArrayList();
/* 362:    */     
/* 363:365 */     int perimeter = 8 * radius;
/* 364:367 */     for (int i = 0; i < perimeter; i++)
/* 365:    */     {
/* 366:368 */       int x = se.center.x + (int)Math.round(radius * Math.cos(6.283185307179586D * i / perimeter));
/* 367:369 */       int y = se.center.y + (int)Math.round(radius * Math.sin(6.283185307179586D * i / perimeter));
/* 368:370 */       se.setXYBoolean(x, y, true);
/* 369:371 */       addListCoord(se.listCoords, x - se.center.x, y - se.center.y);
/* 370:    */     }
/* 371:374 */     return se;
/* 372:    */   }
/* 373:    */   
/* 374:    */   private static void addListCoord(ArrayList<Point> a, int x, int y)
/* 375:    */   {
/* 376:380 */     int size = a.size();
/* 377:382 */     for (int i = 0; i < size; i++)
/* 378:    */     {
/* 379:383 */       Point p = (Point)a.get(i);
/* 380:385 */       if ((p.x == x) && (p.y == y)) {
/* 381:386 */         return;
/* 382:    */       }
/* 383:    */     }
/* 384:388 */     a.add(new Point(x, y));
/* 385:    */   }
/* 386:    */   
/* 387:    */   public static FlatSE cross(int size)
/* 388:    */   {
/* 389:393 */     size = sizeControl(size);
/* 390:    */     
/* 391:395 */     FlatSE se = new FlatSE(size, size, new Point(size / 2, size / 2));
/* 392:396 */     se.fill(false);
/* 393:398 */     for (int x = 0; x < se.xdim; x++) {
/* 394:399 */       for (int y = 0; y < se.ydim; y++) {
/* 395:400 */         if ((x == size / 2) || (y == size / 2)) {
/* 396:401 */           se.setXYBoolean(x, y, true);
/* 397:    */         }
/* 398:    */       }
/* 399:    */     }
/* 400:405 */     return se;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public static FlatSE square(int size, int x, int y)
/* 404:    */   {
/* 405:410 */     size = sizeControl(size);
/* 406:    */     
/* 407:412 */     FlatSE se = new FlatSE(size, size, new Point(x, y));
/* 408:413 */     se.fill(true);
/* 409:415 */     if ((x == size / 2) && (y == size / 2)) {
/* 410:415 */       se.BOX = true;
/* 411:    */     }
/* 412:417 */     return se;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public static FlatSE hLine(int size, int x)
/* 416:    */   {
/* 417:421 */     size = sizeControl(size);
/* 418:    */     
/* 419:423 */     FlatSE se = new FlatSE(size, 1, new Point(x, 0));
/* 420:424 */     se.fill(true);
/* 421:    */     
/* 422:426 */     return se;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public static FlatSE hLine(int size)
/* 426:    */   {
/* 427:430 */     size = sizeControl(size);
/* 428:    */     
/* 429:432 */     FlatSE se = new FlatSE(size, 1, new Point(size / 2, 0));
/* 430:433 */     se.fill(true);
/* 431:    */     
/* 432:435 */     return se;
/* 433:    */   }
/* 434:    */   
/* 435:    */   public static FlatSE vLine(int size, int y)
/* 436:    */   {
/* 437:439 */     size = sizeControl(size);
/* 438:    */     
/* 439:441 */     FlatSE se = new FlatSE(1, size, new Point(0, y));
/* 440:442 */     se.fill(true);
/* 441:    */     
/* 442:444 */     return se;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public static FlatSE vLine(int size)
/* 446:    */   {
/* 447:448 */     size = sizeControl(size);
/* 448:    */     
/* 449:450 */     FlatSE se = new FlatSE(1, size, new Point(0, size / 2));
/* 450:451 */     se.fill(true);
/* 451:    */     
/* 452:453 */     return se;
/* 453:    */   }
/* 454:    */   
/* 455:    */   private static int sizeControl(int size)
/* 456:    */   {
/* 457:457 */     int newSize = size;
/* 458:459 */     if (newSize < 3)
/* 459:    */     {
/* 460:460 */       System.err.println("Warning: square SE size increased to 3");
/* 461:461 */       newSize = 3;
/* 462:    */     }
/* 463:464 */     if (newSize % 2 == 0)
/* 464:    */     {
/* 465:465 */       newSize++;
/* 466:466 */       System.err.println("Warning: square SE modified to odd size " + newSize);
/* 467:    */     }
/* 468:469 */     return newSize;
/* 469:    */   }
/* 470:    */   
/* 471:    */   public FlatSE getHLine()
/* 472:    */   {
/* 473:474 */     if (!this.BOX) {
/* 474:475 */       return this;
/* 475:    */     }
/* 476:477 */     return hLine(getXDim(), this.center.x);
/* 477:    */   }
/* 478:    */   
/* 479:    */   public FlatSE getVLine()
/* 480:    */   {
/* 481:482 */     if (!this.BOX) {
/* 482:483 */       return this;
/* 483:    */     }
/* 484:485 */     return vLine(getYDim(), this.center.y);
/* 485:    */   }
/* 486:    */   
/* 487:    */   public FlatSE reflection()
/* 488:    */   {
/* 489:489 */     FlatSE se = new FlatSE(getXDim(), getYDim(), new Point(getXDim() - 1 - this.center.x, getYDim() - 1 - this.center.y));
/* 490:490 */     se.fill(false);
/* 491:492 */     for (int x = 0; x < getXDim(); x++) {
/* 492:493 */       for (int y = 0; y < getYDim(); y++) {
/* 493:494 */         se.setXYBoolean(getXDim() - 1 - x, getYDim() - 1 - y, getXYBoolean(x, y));
/* 494:    */       }
/* 495:    */     }
/* 496:496 */     return se;
/* 497:    */   }
/* 498:    */   
/* 499:    */   public Point[] getCoords()
/* 500:    */   {
/* 501:501 */     if (this.coords != null) {
/* 502:501 */       return this.coords;
/* 503:    */     }
/* 504:503 */     int size = 0;
/* 505:505 */     for (int x = 0; x < getXDim(); x++) {
/* 506:506 */       for (int y = 0; y < getYDim(); y++) {
/* 507:507 */         if (getXYBoolean(x, y)) {
/* 508:507 */           size++;
/* 509:    */         }
/* 510:    */       }
/* 511:    */     }
/* 512:509 */     this.coords = new Point[size];
/* 513:    */     
/* 514:511 */     int k = 0;
/* 515:514 */     if (getXYBoolean(this.center.x, this.center.y)) {
/* 516:514 */       this.coords[(k++)] = new Point(0, 0);
/* 517:    */     }
/* 518:516 */     for (int x = 0; x < getXDim(); x++) {
/* 519:517 */       for (int y = 0; y < getYDim(); y++) {
/* 520:518 */         if ((getXYBoolean(x, y)) && ((x != this.center.x) || (y != this.center.y))) {
/* 521:519 */           this.coords[(k++)] = new Point(x - this.center.x, y - this.center.y);
/* 522:    */         }
/* 523:    */       }
/* 524:    */     }
/* 525:524 */     return this.coords;
/* 526:    */   }
/* 527:    */   
/* 528:    */   public ArrayList<Point> getListCoords()
/* 529:    */   {
/* 530:528 */     return this.listCoords;
/* 531:    */   }
/* 532:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.se.FlatSE
 * JD-Core Version:    0.7.0.1
 */
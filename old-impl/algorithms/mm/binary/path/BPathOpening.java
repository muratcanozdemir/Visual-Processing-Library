/*   1:    */ package vpt.algorithms.mm.binary.path;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.ByteImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.IntegerImage;
/*   8:    */ import vpt.algorithms.arithmetic.Maximum;
/*   9:    */ import vpt.algorithms.segmentation.Threshold;
/*  10:    */ 
/*  11:    */ public class BPathOpening
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 23 */   public Image output = null;
/*  15: 24 */   public Image input = null;
/*  16: 25 */   public Integer length = null;
/*  17: 26 */   private Image aux = null;
/*  18:    */   private int xdim;
/*  19:    */   private int ydim;
/*  20: 30 */   private final int UNPROCESSED = -1;
/*  21:    */   
/*  22:    */   public BPathOpening()
/*  23:    */   {
/*  24: 33 */     this.inputFields = "input,length";
/*  25: 34 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 39 */     this.xdim = this.input.getXDim();
/*  32: 40 */     this.ydim = this.input.getYDim();
/*  33:    */     
/*  34: 42 */     Image outputSN = new ByteImage(this.input, false);
/*  35: 43 */     outputSN.fill(0);
/*  36:    */     
/*  37: 45 */     this.aux = new IntegerImage(this.xdim, this.ydim, 2);
/*  38: 46 */     this.aux.fill(-1);
/*  39: 49 */     for (int y = 0; y < this.ydim; y++) {
/*  40: 50 */       for (int x = 0; x < this.xdim; x++) {
/*  41: 51 */         if (this.input.getXYBoolean(x, y))
/*  42:    */         {
/*  43: 53 */           int upward = computeUpwardSN(x, y);
/*  44: 54 */           int downward = computeDownwardSN(x, y);
/*  45:    */           
/*  46:    */ 
/*  47: 57 */           outputSN.setXYByte(x, y, upward + 1 + downward);
/*  48:    */         }
/*  49:    */       }
/*  50:    */     }
/*  51: 62 */     Image outputWE = new ByteImage(this.input, false);
/*  52: 63 */     outputWE.fill(0);
/*  53:    */     
/*  54: 65 */     this.aux.fill(-1);
/*  55: 68 */     for (int y = 0; y < this.ydim; y++) {
/*  56: 69 */       for (int x = 0; x < this.xdim; x++) {
/*  57: 70 */         if (this.input.getXYBoolean(x, y))
/*  58:    */         {
/*  59: 72 */           int upward = computeUpwardWE(x, y);
/*  60: 73 */           int downward = computeDownwardWE(x, y);
/*  61:    */           
/*  62:    */ 
/*  63: 76 */           outputWE.setXYByte(x, y, upward + 1 + downward);
/*  64:    */         }
/*  65:    */       }
/*  66:    */     }
/*  67: 81 */     Image outputSWNE = new ByteImage(this.input, false);
/*  68: 82 */     outputSWNE.fill(0);
/*  69:    */     
/*  70: 84 */     this.aux.fill(-1);
/*  71: 87 */     for (int y = 0; y < this.ydim; y++) {
/*  72: 88 */       for (int x = 0; x < this.xdim; x++) {
/*  73: 89 */         if (this.input.getXYBoolean(x, y))
/*  74:    */         {
/*  75: 91 */           int upward = computeUpwardSWNE(x, y);
/*  76: 92 */           int downward = computeDownwardSWNE(x, y);
/*  77:    */           
/*  78:    */ 
/*  79: 95 */           outputSWNE.setXYByte(x, y, upward + 1 + downward);
/*  80:    */         }
/*  81:    */       }
/*  82:    */     }
/*  83:100 */     Image outputNWSE = new ByteImage(this.input, false);
/*  84:101 */     outputNWSE.fill(0);
/*  85:    */     
/*  86:103 */     this.aux.fill(-1);
/*  87:106 */     for (int y = 0; y < this.ydim; y++) {
/*  88:107 */       for (int x = 0; x < this.xdim; x++) {
/*  89:108 */         if (this.input.getXYBoolean(x, y))
/*  90:    */         {
/*  91:110 */           int upward = computeUpwardNWSE(x, y);
/*  92:111 */           int downward = computeDownwardNWSE(x, y);
/*  93:    */           
/*  94:    */ 
/*  95:114 */           outputNWSE.setXYByte(x, y, upward + 1 + downward);
/*  96:    */         }
/*  97:    */       }
/*  98:    */     }
/*  99:120 */     this.output = new ByteImage(this.input, false);
/* 100:121 */     this.output.fill(0);
/* 101:    */     
/* 102:123 */     this.output = Maximum.invoke(outputSN, outputWE);
/* 103:124 */     this.output = Maximum.invoke(this.output, outputSWNE);
/* 104:125 */     this.output = Maximum.invoke(this.output, outputNWSE);
/* 105:    */     
/* 106:127 */     this.output = Threshold.invoke(this.output, this.length.intValue() * 0.00392156862745098D);
/* 107:    */   }
/* 108:    */   
/* 109:    */   private int computeUpwardSN(int x, int y)
/* 110:    */   {
/* 111:131 */     if (!this.input.getXYBoolean(x, y))
/* 112:    */     {
/* 113:132 */       this.aux.setXYCInt(x, y, 0, 0);
/* 114:133 */       return 0;
/* 115:    */     }
/* 116:    */     int l1;
/* 117:    */     int l1;
/* 118:137 */     if ((x - 1 >= 0) && (y + 1 <= this.ydim - 1)) {
/* 119:137 */       l1 = this.aux.getXYCInt(x - 1, y + 1, 0);
/* 120:    */     } else {
/* 121:138 */       l1 = 0;
/* 122:    */     }
/* 123:    */     int l2;
/* 124:    */     int l2;
/* 125:140 */     if (y + 1 <= this.ydim - 1) {
/* 126:140 */       l2 = this.aux.getXYCInt(x, y + 1, 0);
/* 127:    */     } else {
/* 128:141 */       l2 = 0;
/* 129:    */     }
/* 130:    */     int l3;
/* 131:    */     int l3;
/* 132:143 */     if ((x + 1 <= this.xdim - 1) && (y + 1 <= this.ydim - 1)) {
/* 133:143 */       l3 = this.aux.getXYCInt(x + 1, y + 1, 0);
/* 134:    */     } else {
/* 135:144 */       l3 = 0;
/* 136:    */     }
/* 137:146 */     if (l1 == -1) {
/* 138:146 */       l1 = computeUpwardSN(x - 1, y + 1);
/* 139:    */     }
/* 140:147 */     if (l2 == -1) {
/* 141:147 */       l2 = computeUpwardSN(x, y + 1);
/* 142:    */     }
/* 143:148 */     if (l3 == -1) {
/* 144:148 */       l3 = computeUpwardSN(x + 1, y + 1);
/* 145:    */     }
/* 146:150 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 147:    */     
/* 148:152 */     this.aux.setXYCInt(x, y, 0, lamda);
/* 149:    */     
/* 150:154 */     return lamda;
/* 151:    */   }
/* 152:    */   
/* 153:    */   private int computeDownwardSN(int x, int y)
/* 154:    */   {
/* 155:159 */     if (!this.input.getXYBoolean(x, y))
/* 156:    */     {
/* 157:160 */       this.aux.setXYCInt(x, y, 1, 0);
/* 158:161 */       return 0;
/* 159:    */     }
/* 160:    */     int l1;
/* 161:    */     int l1;
/* 162:165 */     if ((x - 1 >= 0) && (y - 1 >= 0)) {
/* 163:165 */       l1 = this.aux.getXYCInt(x - 1, y - 1, 1);
/* 164:    */     } else {
/* 165:166 */       l1 = 0;
/* 166:    */     }
/* 167:    */     int l2;
/* 168:    */     int l2;
/* 169:168 */     if (y - 1 >= 0) {
/* 170:168 */       l2 = this.aux.getXYCInt(x, y - 1, 1);
/* 171:    */     } else {
/* 172:169 */       l2 = 0;
/* 173:    */     }
/* 174:    */     int l3;
/* 175:    */     int l3;
/* 176:171 */     if ((x + 1 <= this.xdim - 1) && (y - 1 >= 0)) {
/* 177:171 */       l3 = this.aux.getXYCInt(x + 1, y - 1, 1);
/* 178:    */     } else {
/* 179:172 */       l3 = 0;
/* 180:    */     }
/* 181:174 */     if (l1 == -1) {
/* 182:174 */       l1 = computeDownwardSN(x - 1, y - 1);
/* 183:    */     }
/* 184:175 */     if (l2 == -1) {
/* 185:175 */       l2 = computeDownwardSN(x, y - 1);
/* 186:    */     }
/* 187:176 */     if (l3 == -1) {
/* 188:176 */       l3 = computeDownwardSN(x + 1, y - 1);
/* 189:    */     }
/* 190:178 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 191:    */     
/* 192:180 */     this.aux.setXYCInt(x, y, 1, lamda);
/* 193:    */     
/* 194:182 */     return lamda;
/* 195:    */   }
/* 196:    */   
/* 197:    */   private int computeUpwardWE(int x, int y)
/* 198:    */   {
/* 199:187 */     if (!this.input.getXYBoolean(x, y))
/* 200:    */     {
/* 201:188 */       this.aux.setXYCInt(x, y, 0, 0);
/* 202:189 */       return 0;
/* 203:    */     }
/* 204:    */     int l1;
/* 205:    */     int l1;
/* 206:193 */     if ((x - 1 >= 0) && (y - 1 >= 0)) {
/* 207:193 */       l1 = this.aux.getXYCInt(x - 1, y - 1, 0);
/* 208:    */     } else {
/* 209:194 */       l1 = 0;
/* 210:    */     }
/* 211:    */     int l2;
/* 212:    */     int l2;
/* 213:196 */     if (x - 1 >= 0) {
/* 214:196 */       l2 = this.aux.getXYCInt(x - 1, y, 0);
/* 215:    */     } else {
/* 216:197 */       l2 = 0;
/* 217:    */     }
/* 218:    */     int l3;
/* 219:    */     int l3;
/* 220:199 */     if ((x - 1 >= 0) && (y + 1 <= this.ydim - 1)) {
/* 221:199 */       l3 = this.aux.getXYCInt(x - 1, y + 1, 0);
/* 222:    */     } else {
/* 223:200 */       l3 = 0;
/* 224:    */     }
/* 225:202 */     if (l1 == -1) {
/* 226:202 */       l1 = computeUpwardWE(x - 1, y - 1);
/* 227:    */     }
/* 228:203 */     if (l2 == -1) {
/* 229:203 */       l2 = computeUpwardWE(x - 1, y);
/* 230:    */     }
/* 231:204 */     if (l3 == -1) {
/* 232:204 */       l3 = computeUpwardWE(x - 1, y + 1);
/* 233:    */     }
/* 234:206 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 235:    */     
/* 236:208 */     this.aux.setXYCInt(x, y, 0, lamda);
/* 237:    */     
/* 238:210 */     return lamda;
/* 239:    */   }
/* 240:    */   
/* 241:    */   private int computeDownwardWE(int x, int y)
/* 242:    */   {
/* 243:215 */     if (!this.input.getXYBoolean(x, y))
/* 244:    */     {
/* 245:216 */       this.aux.setXYCInt(x, y, 1, 0);
/* 246:217 */       return 0;
/* 247:    */     }
/* 248:    */     int l1;
/* 249:    */     int l1;
/* 250:221 */     if ((x + 1 <= this.xdim - 1) && (y - 1 >= 0)) {
/* 251:221 */       l1 = this.aux.getXYCInt(x + 1, y - 1, 1);
/* 252:    */     } else {
/* 253:222 */       l1 = 0;
/* 254:    */     }
/* 255:    */     int l2;
/* 256:    */     int l2;
/* 257:224 */     if (x + 1 <= this.xdim - 1) {
/* 258:224 */       l2 = this.aux.getXYCInt(x + 1, y, 1);
/* 259:    */     } else {
/* 260:225 */       l2 = 0;
/* 261:    */     }
/* 262:    */     int l3;
/* 263:    */     int l3;
/* 264:227 */     if ((x + 1 <= this.xdim - 1) && (y + 1 <= this.ydim - 1)) {
/* 265:227 */       l3 = this.aux.getXYCInt(x + 1, y + 1, 1);
/* 266:    */     } else {
/* 267:228 */       l3 = 0;
/* 268:    */     }
/* 269:230 */     if (l1 == -1) {
/* 270:230 */       l1 = computeDownwardWE(x + 1, y - 1);
/* 271:    */     }
/* 272:231 */     if (l2 == -1) {
/* 273:231 */       l2 = computeDownwardWE(x + 1, y);
/* 274:    */     }
/* 275:232 */     if (l3 == -1) {
/* 276:232 */       l3 = computeDownwardWE(x + 1, y + 1);
/* 277:    */     }
/* 278:234 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 279:    */     
/* 280:236 */     this.aux.setXYCInt(x, y, 1, lamda);
/* 281:    */     
/* 282:238 */     return lamda;
/* 283:    */   }
/* 284:    */   
/* 285:    */   private int computeUpwardSWNE(int x, int y)
/* 286:    */   {
/* 287:243 */     if (!this.input.getXYBoolean(x, y))
/* 288:    */     {
/* 289:244 */       this.aux.setXYCInt(x, y, 0, 0);
/* 290:245 */       return 0;
/* 291:    */     }
/* 292:    */     int l1;
/* 293:    */     int l1;
/* 294:249 */     if (y - 1 >= 0) {
/* 295:249 */       l1 = this.aux.getXYCInt(x, y - 1, 0);
/* 296:    */     } else {
/* 297:250 */       l1 = 0;
/* 298:    */     }
/* 299:    */     int l2;
/* 300:    */     int l2;
/* 301:252 */     if ((x + 1 <= this.xdim - 1) && (y - 1 >= 0)) {
/* 302:252 */       l2 = this.aux.getXYCInt(x + 1, y - 1, 0);
/* 303:    */     } else {
/* 304:253 */       l2 = 0;
/* 305:    */     }
/* 306:    */     int l3;
/* 307:    */     int l3;
/* 308:255 */     if (x + 1 <= this.xdim - 1) {
/* 309:255 */       l3 = this.aux.getXYCInt(x + 1, y, 0);
/* 310:    */     } else {
/* 311:256 */       l3 = 0;
/* 312:    */     }
/* 313:258 */     if (l1 == -1) {
/* 314:258 */       l1 = computeUpwardSWNE(x, y - 1);
/* 315:    */     }
/* 316:259 */     if (l2 == -1) {
/* 317:259 */       l2 = computeUpwardSWNE(x + 1, y - 1);
/* 318:    */     }
/* 319:260 */     if (l3 == -1) {
/* 320:260 */       l3 = computeUpwardSWNE(x + 1, y);
/* 321:    */     }
/* 322:262 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 323:    */     
/* 324:264 */     this.aux.setXYCInt(x, y, 0, lamda);
/* 325:    */     
/* 326:266 */     return lamda;
/* 327:    */   }
/* 328:    */   
/* 329:    */   private int computeDownwardSWNE(int x, int y)
/* 330:    */   {
/* 331:271 */     if (!this.input.getXYBoolean(x, y))
/* 332:    */     {
/* 333:272 */       this.aux.setXYCInt(x, y, 1, 0);
/* 334:273 */       return 0;
/* 335:    */     }
/* 336:    */     int l1;
/* 337:    */     int l1;
/* 338:277 */     if (x - 1 >= 0) {
/* 339:277 */       l1 = this.aux.getXYCInt(x - 1, y, 1);
/* 340:    */     } else {
/* 341:278 */       l1 = 0;
/* 342:    */     }
/* 343:    */     int l2;
/* 344:    */     int l2;
/* 345:280 */     if ((x - 1 >= 0) && (y + 1 <= this.ydim - 1)) {
/* 346:280 */       l2 = this.aux.getXYCInt(x - 1, y + 1, 1);
/* 347:    */     } else {
/* 348:281 */       l2 = 0;
/* 349:    */     }
/* 350:    */     int l3;
/* 351:    */     int l3;
/* 352:283 */     if (y + 1 <= this.ydim - 1) {
/* 353:283 */       l3 = this.aux.getXYCInt(x, y + 1, 1);
/* 354:    */     } else {
/* 355:284 */       l3 = 0;
/* 356:    */     }
/* 357:286 */     if (l1 == -1) {
/* 358:286 */       l1 = computeDownwardSWNE(x - 1, y);
/* 359:    */     }
/* 360:287 */     if (l2 == -1) {
/* 361:287 */       l2 = computeDownwardSWNE(x - 1, y + 1);
/* 362:    */     }
/* 363:288 */     if (l3 == -1) {
/* 364:288 */       l3 = computeDownwardSWNE(x, y + 1);
/* 365:    */     }
/* 366:290 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 367:    */     
/* 368:292 */     this.aux.setXYCInt(x, y, 1, lamda);
/* 369:    */     
/* 370:294 */     return lamda;
/* 371:    */   }
/* 372:    */   
/* 373:    */   private int computeUpwardNWSE(int x, int y)
/* 374:    */   {
/* 375:299 */     if (!this.input.getXYBoolean(x, y))
/* 376:    */     {
/* 377:300 */       this.aux.setXYCInt(x, y, 0, 0);
/* 378:301 */       return 0;
/* 379:    */     }
/* 380:    */     int l1;
/* 381:    */     int l1;
/* 382:305 */     if (y - 1 >= 0) {
/* 383:305 */       l1 = this.aux.getXYCInt(x, y - 1, 0);
/* 384:    */     } else {
/* 385:306 */       l1 = 0;
/* 386:    */     }
/* 387:    */     int l2;
/* 388:    */     int l2;
/* 389:308 */     if ((x - 1 >= 0) && (y - 1 >= 0)) {
/* 390:308 */       l2 = this.aux.getXYCInt(x - 1, y - 1, 0);
/* 391:    */     } else {
/* 392:309 */       l2 = 0;
/* 393:    */     }
/* 394:    */     int l3;
/* 395:    */     int l3;
/* 396:311 */     if (x - 1 >= 0) {
/* 397:311 */       l3 = this.aux.getXYCInt(x - 1, y, 0);
/* 398:    */     } else {
/* 399:312 */       l3 = 0;
/* 400:    */     }
/* 401:314 */     if (l1 == -1) {
/* 402:314 */       l1 = computeUpwardNWSE(x, y - 1);
/* 403:    */     }
/* 404:315 */     if (l2 == -1) {
/* 405:315 */       l2 = computeUpwardNWSE(x - 1, y - 1);
/* 406:    */     }
/* 407:316 */     if (l3 == -1) {
/* 408:316 */       l3 = computeUpwardNWSE(x - 1, y);
/* 409:    */     }
/* 410:318 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 411:    */     
/* 412:320 */     this.aux.setXYCInt(x, y, 0, lamda);
/* 413:    */     
/* 414:322 */     return lamda;
/* 415:    */   }
/* 416:    */   
/* 417:    */   private int computeDownwardNWSE(int x, int y)
/* 418:    */   {
/* 419:327 */     if (!this.input.getXYBoolean(x, y))
/* 420:    */     {
/* 421:328 */       this.aux.setXYCInt(x, y, 1, 0);
/* 422:329 */       return 0;
/* 423:    */     }
/* 424:    */     int l1;
/* 425:    */     int l1;
/* 426:333 */     if (x + 1 <= this.xdim - 1) {
/* 427:333 */       l1 = this.aux.getXYCInt(x + 1, y, 1);
/* 428:    */     } else {
/* 429:334 */       l1 = 0;
/* 430:    */     }
/* 431:    */     int l2;
/* 432:    */     int l2;
/* 433:336 */     if ((x + 1 <= this.xdim - 1) && (y + 1 <= this.ydim - 1)) {
/* 434:336 */       l2 = this.aux.getXYCInt(x + 1, y + 1, 1);
/* 435:    */     } else {
/* 436:337 */       l2 = 0;
/* 437:    */     }
/* 438:    */     int l3;
/* 439:    */     int l3;
/* 440:339 */     if (y + 1 <= this.ydim - 1) {
/* 441:339 */       l3 = this.aux.getXYCInt(x, y + 1, 1);
/* 442:    */     } else {
/* 443:340 */       l3 = 0;
/* 444:    */     }
/* 445:342 */     if (l1 == -1) {
/* 446:342 */       l1 = computeDownwardNWSE(x + 1, y);
/* 447:    */     }
/* 448:343 */     if (l2 == -1) {
/* 449:343 */       l2 = computeDownwardNWSE(x + 1, y + 1);
/* 450:    */     }
/* 451:344 */     if (l3 == -1) {
/* 452:344 */       l3 = computeDownwardNWSE(x, y + 1);
/* 453:    */     }
/* 454:346 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 455:    */     
/* 456:348 */     this.aux.setXYCInt(x, y, 1, lamda);
/* 457:    */     
/* 458:350 */     return lamda;
/* 459:    */   }
/* 460:    */   
/* 461:    */   public static Image invoke(Image image, Integer length)
/* 462:    */   {
/* 463:    */     try
/* 464:    */     {
/* 465:357 */       return (Image)new BPathOpening().preprocess(new Object[] { image, length });
/* 466:    */     }
/* 467:    */     catch (GlobalException e)
/* 468:    */     {
/* 469:359 */       e.printStackTrace();
/* 470:    */     }
/* 471:360 */     return null;
/* 472:    */   }
/* 473:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.path.BPathOpening
 * JD-Core Version:    0.7.0.1
 */
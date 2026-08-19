/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.ByteImage;
/*   7:    */ import vpt.DoubleImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.arithmetic.Maximum;
/*  11:    */ import vpt.algorithms.arithmetic.Minimum;
/*  12:    */ import vpt.algorithms.mm.gray.GClosing;
/*  13:    */ import vpt.algorithms.mm.gray.GDilation;
/*  14:    */ import vpt.algorithms.mm.gray.GErosion;
/*  15:    */ import vpt.algorithms.mm.gray.GOpening;
/*  16:    */ import vpt.util.Tools;
/*  17:    */ import vpt.util.se.FlatSE;
/*  18:    */ 
/*  19:    */ public class ExtendedMorphologicalCovariance
/*  20:    */   extends Algorithm
/*  21:    */ {
/*  22: 33 */   public double[] output = null;
/*  23: 34 */   public Image input = null;
/*  24: 35 */   public Integer size = null;
/*  25:    */   private int imageSize;
/*  26:    */   
/*  27:    */   public ExtendedMorphologicalCovariance()
/*  28:    */   {
/*  29: 39 */     this.inputFields = "input, size";
/*  30: 40 */     this.outputFields = "output";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void execute()
/*  34:    */     throws GlobalException
/*  35:    */   {
/*  36: 44 */     int cdim = this.input.getCDim();
/*  37: 45 */     if (cdim != 1) {
/*  38: 45 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  39:    */     }
/*  40: 47 */     this.output = new double[2 * this.size.intValue() * 2 + this.size.intValue() * 2 + (this.size.intValue() + 1) * 2 + 2 * this.size.intValue() + 2 * this.size.intValue() + 2 * this.size.intValue() + 2 * this.size.intValue() + 2 * this.size.intValue()];
/*  41:    */     
/*  42: 49 */     this.imageSize = this.input.getSize();
/*  43:    */     
/*  44:    */ 
/*  45: 52 */     Image[] tmpsO = new Image[this.size.intValue()];
/*  46: 53 */     Image[] tmpsC = new Image[this.size.intValue()];
/*  47:    */     
/*  48: 55 */     FlatSE[] ses = new FlatSE[this.size.intValue()];
/*  49: 57 */     for (int i = 1; i <= this.size.intValue(); i++)
/*  50:    */     {
/*  51: 58 */       ses[(i - 1)] = FlatSE.circle(i);
/*  52:    */       
/*  53: 60 */       tmpsO[(i - 1)] = GErosion.invoke(this.input, ses[(i - 1)]);
/*  54: 61 */       tmpsC[(i - 1)] = GDilation.invoke(this.input, ses[(i - 1)]);
/*  55:    */     }
/*  56: 64 */     int offset = 0;
/*  57:    */     
/*  58:    */ 
/*  59: 67 */     histoED(tmpsO, tmpsC, ses, offset);
/*  60: 70 */     for (int i = 1; i <= this.size.intValue(); i++)
/*  61:    */     {
/*  62: 71 */       tmpsO[(i - 1)] = GDilation.invoke(tmpsO[(i - 1)], ses[(i - 1)]);
/*  63: 72 */       tmpsC[(i - 1)] = GErosion.invoke(tmpsC[(i - 1)], ses[(i - 1)]);
/*  64:    */     }
/*  65: 75 */     offset += this.size.intValue() * 2;
/*  66:    */     
/*  67:    */ 
/*  68: 78 */     histoOCDouble(tmpsO, tmpsC, ses, offset);
/*  69:    */     
/*  70: 80 */     offset += 4 * this.size.intValue();
/*  71:    */     
/*  72:    */ 
/*  73: 83 */     locHistoOC(tmpsO, tmpsC, ses, offset);
/*  74:    */     
/*  75: 85 */     offset += 2 * (this.size.intValue() + 1);
/*  76:    */     
/*  77:    */ 
/*  78: 88 */     ArrayList[] tmpsO2 = new ArrayList[this.size.intValue()];
/*  79: 89 */     ArrayList[] tmpsC2 = new ArrayList[this.size.intValue()];
/*  80: 92 */     for (int i = 1; i <= this.size.intValue(); i++)
/*  81:    */     {
/*  82: 93 */       FlatSE[] sesPc = FlatSE.pointCouples(i);
/*  83: 94 */       tmpsO2[(i - 1)] = new ArrayList(sesPc.length);
/*  84: 95 */       tmpsC2[(i - 1)] = new ArrayList(sesPc.length);
/*  85: 98 */       for (int k = 0; k < sesPc.length; k++)
/*  86:    */       {
/*  87: 99 */         tmpsO2[(i - 1)].add(GOpening.invoke(this.input, sesPc[k]));
/*  88:100 */         tmpsC2[(i - 1)].add(GClosing.invoke(this.input, sesPc[k]));
/*  89:    */       }
/*  90:    */     }
/*  91:107 */     ritMaxMin(tmpsO2, tmpsC2, offset);
/*  92:108 */     offset += this.size.intValue() * 2;
/*  93:    */     
/*  94:    */ 
/*  95:111 */     ritMinMin(tmpsO2, tmpsC2, offset);
/*  96:112 */     offset += this.size.intValue() * 2;
/*  97:    */     
/*  98:    */ 
/*  99:115 */     ritMedMed(tmpsO2, tmpsC2, offset);
/* 100:116 */     offset += this.size.intValue() * 2;
/* 101:    */     
/* 102:    */ 
/* 103:119 */     ritVarVar(tmpsO2, tmpsC2, offset);
/* 104:120 */     offset += this.size.intValue() * 2;
/* 105:    */     
/* 106:    */ 
/* 107:123 */     ritAngleDiffAngleDiff(tmpsO2, tmpsC2, offset);
/* 108:126 */     for (int i = 0; i < this.output.length; i++) {
/* 109:127 */       this.output[i] /= this.imageSize;
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   private void ritAngleDiffAngleDiff(ArrayList[] tmpsO2, ArrayList[] tmpsC2, int offset)
/* 114:    */   {
/* 115:131 */     Image[] tmpsO = new Image[this.size.intValue()];
/* 116:132 */     Image[] tmpsC = new Image[this.size.intValue()];
/* 117:135 */     for (int j = 0; j < this.size.intValue(); j++)
/* 118:    */     {
/* 119:136 */       tmpsO[j] = new DoubleImage(this.input, false);
/* 120:137 */       tmpsC[j] = new DoubleImage(this.input, false);
/* 121:    */       
/* 122:139 */       int len = tmpsO2[j].size();
/* 123:141 */       for (int i = 0; i < this.imageSize; i++)
/* 124:    */       {
/* 125:142 */         double max = 0.0D;
/* 126:143 */         int indexMax = 0;
/* 127:    */         
/* 128:145 */         double min = 1.7976931348623157E+308D;
/* 129:146 */         int indexMin = 0;
/* 130:148 */         for (int k = 0; k < len; k++)
/* 131:    */         {
/* 132:149 */           double d = ((Image)tmpsO2[j].get(k)).getDouble(i);
/* 133:151 */           if (d > max)
/* 134:    */           {
/* 135:152 */             indexMax = k;
/* 136:153 */             max = d;
/* 137:    */           }
/* 138:155 */           if (d < min)
/* 139:    */           {
/* 140:156 */             indexMin = k;
/* 141:157 */             min = d;
/* 142:    */           }
/* 143:    */         }
/* 144:162 */         int diff = Math.abs(indexMax - indexMin);
/* 145:163 */         tmpsO[j].setDouble(i, diff * 3.141592653589793D / len);
/* 146:    */         
/* 147:165 */         max = 0.0D;
/* 148:166 */         indexMax = 0;
/* 149:    */         
/* 150:168 */         min = 1.7976931348623157E+308D;
/* 151:169 */         indexMin = 0;
/* 152:171 */         for (int k = 0; k < len; k++)
/* 153:    */         {
/* 154:172 */           double d = ((Image)tmpsC2[j].get(k)).getDouble(i);
/* 155:174 */           if (d > max)
/* 156:    */           {
/* 157:175 */             indexMax = k;
/* 158:176 */             max = d;
/* 159:    */           }
/* 160:178 */           if (d < min)
/* 161:    */           {
/* 162:179 */             indexMin = k;
/* 163:180 */             min = d;
/* 164:    */           }
/* 165:    */         }
/* 166:185 */         diff = Math.abs(indexMax - indexMin);
/* 167:186 */         tmpsC[j].setDouble(i, diff * 3.141592653589793D / len);
/* 168:    */       }
/* 169:    */     }
/* 170:190 */     for (int i = 0; i < this.imageSize; i++)
/* 171:    */     {
/* 172:191 */       int indexO = 0;
/* 173:192 */       double minO = 1.7976931348623157E+308D;
/* 174:    */       
/* 175:194 */       int indexC = 0;
/* 176:195 */       double minC = 1.7976931348623157E+308D;
/* 177:197 */       for (int j = 0; j < this.size.intValue(); j++)
/* 178:    */       {
/* 179:198 */         double dO = tmpsO[j].getDouble(i);
/* 180:199 */         double dC = tmpsC[j].getDouble(i);
/* 181:    */         
/* 182:201 */         double tmp = dO;
/* 183:203 */         if (tmp < minO)
/* 184:    */         {
/* 185:204 */           minO = tmp;
/* 186:205 */           indexO = j;
/* 187:    */         }
/* 188:208 */         tmp = dC;
/* 189:210 */         if (tmp < minC)
/* 190:    */         {
/* 191:211 */           minC = tmp;
/* 192:212 */           indexC = j;
/* 193:    */         }
/* 194:    */       }
/* 195:217 */       this.output[(offset + indexO)] += 1.0D;
/* 196:218 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   private void ritVarVar(ArrayList[] tmpsO2, ArrayList[] tmpsC2, int offset)
/* 201:    */   {
/* 202:223 */     Image[] tmpsO = new Image[this.size.intValue()];
/* 203:224 */     Image[] tmpsC = new Image[this.size.intValue()];
/* 204:227 */     for (int j = 0; j < this.size.intValue(); j++)
/* 205:    */     {
/* 206:228 */       tmpsO[j] = new DoubleImage(this.input, false);
/* 207:229 */       tmpsC[j] = new DoubleImage(this.input, false);
/* 208:    */       
/* 209:231 */       int len = tmpsO2[j].size();
/* 210:232 */       double[] d = new double[len];
/* 211:234 */       for (int i = 0; i < this.imageSize; i++)
/* 212:    */       {
/* 213:236 */         double mean = 0.0D;
/* 214:237 */         double var = 0.0D;
/* 215:240 */         for (int k = 0; k < len; k++) {
/* 216:241 */           mean += ((Image)tmpsO2[j].get(k)).getDouble(i);
/* 217:    */         }
/* 218:242 */         mean /= len;
/* 219:245 */         for (int k = 0; k < len; k++) {
/* 220:246 */           var += (mean - ((Image)tmpsO2[j].get(k)).getDouble(i)) * (mean - ((Image)tmpsO2[j].get(k)).getDouble(i));
/* 221:    */         }
/* 222:248 */         var /= len;
/* 223:    */         
/* 224:250 */         tmpsO[j].setDouble(i, var);
/* 225:    */         
/* 226:252 */         mean = 0.0D;
/* 227:253 */         var = 0.0D;
/* 228:256 */         for (int k = 0; k < len; k++) {
/* 229:257 */           mean += ((Image)tmpsC2[j].get(k)).getDouble(i);
/* 230:    */         }
/* 231:258 */         mean /= len;
/* 232:261 */         for (int k = 0; k < len; k++) {
/* 233:262 */           var += (mean - ((Image)tmpsC2[j].get(k)).getDouble(i)) * (mean - ((Image)tmpsC2[j].get(k)).getDouble(i));
/* 234:    */         }
/* 235:264 */         var /= len;
/* 236:    */         
/* 237:266 */         tmpsC[j].setDouble(i, var);
/* 238:    */       }
/* 239:    */     }
/* 240:270 */     for (int i = 0; i < this.imageSize; i++)
/* 241:    */     {
/* 242:271 */       int indexO = 0;
/* 243:272 */       double maxO = 0.0D;
/* 244:    */       
/* 245:274 */       int indexC = 0;
/* 246:275 */       double maxC = 0.0D;
/* 247:277 */       for (int j = 0; j < this.size.intValue(); j++)
/* 248:    */       {
/* 249:278 */         double dO = tmpsO[j].getDouble(i);
/* 250:279 */         double dC = tmpsC[j].getDouble(i);
/* 251:    */         
/* 252:281 */         double tmp = dO;
/* 253:283 */         if (tmp > maxO)
/* 254:    */         {
/* 255:284 */           maxO = tmp;
/* 256:285 */           indexO = j;
/* 257:    */         }
/* 258:288 */         tmp = dC;
/* 259:290 */         if (tmp > maxC)
/* 260:    */         {
/* 261:291 */           maxC = tmp;
/* 262:292 */           indexC = j;
/* 263:    */         }
/* 264:    */       }
/* 265:297 */       this.output[(offset + indexO)] += 1.0D;
/* 266:298 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 267:    */     }
/* 268:    */   }
/* 269:    */   
/* 270:    */   private void ritMedMed(ArrayList[] tmpsO2, ArrayList[] tmpsC2, int offset)
/* 271:    */   {
/* 272:303 */     Image[] tmpsO = new Image[this.size.intValue()];
/* 273:304 */     Image[] tmpsC = new Image[this.size.intValue()];
/* 274:307 */     for (int j = 0; j < this.size.intValue(); j++)
/* 275:    */     {
/* 276:308 */       tmpsO[j] = new DoubleImage(this.input, false);
/* 277:309 */       tmpsC[j] = new DoubleImage(this.input, false);
/* 278:    */       
/* 279:311 */       int len = tmpsO2[j].size();
/* 280:312 */       double[] d = new double[len];
/* 281:314 */       for (int i = 0; i < this.imageSize; i++)
/* 282:    */       {
/* 283:315 */         for (int k = 0; k < len; k++) {
/* 284:316 */           d[k] = ((Image)tmpsO2[j].get(k)).getDouble(i);
/* 285:    */         }
/* 286:318 */         Arrays.sort(d);
/* 287:319 */         tmpsO[j].setDouble(i, d[(len / 2)]);
/* 288:321 */         for (int k = 0; k < len; k++) {
/* 289:322 */           d[k] = ((Image)tmpsC2[j].get(k)).getDouble(i);
/* 290:    */         }
/* 291:324 */         Arrays.sort(d);
/* 292:325 */         tmpsC[j].setDouble(i, d[(len / 2)]);
/* 293:    */       }
/* 294:    */     }
/* 295:329 */     for (int i = 0; i < this.imageSize; i++)
/* 296:    */     {
/* 297:330 */       int indexO = 0;
/* 298:331 */       double maxO = 0.0D;
/* 299:    */       
/* 300:333 */       int indexC = 0;
/* 301:334 */       double maxC = 0.0D;
/* 302:336 */       for (int j = 0; j < this.size.intValue(); j++)
/* 303:    */       {
/* 304:337 */         double dO = tmpsO[j].getDouble(i);
/* 305:338 */         double dC = tmpsC[j].getDouble(i);
/* 306:    */         
/* 307:340 */         double tmp = dO;
/* 308:342 */         if (tmp > maxO)
/* 309:    */         {
/* 310:343 */           maxO = tmp;
/* 311:344 */           indexO = j;
/* 312:    */         }
/* 313:347 */         tmp = dC;
/* 314:349 */         if (tmp > maxC)
/* 315:    */         {
/* 316:350 */           maxC = tmp;
/* 317:351 */           indexC = j;
/* 318:    */         }
/* 319:    */       }
/* 320:356 */       this.output[(offset + indexO)] += 1.0D;
/* 321:357 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 322:    */     }
/* 323:    */   }
/* 324:    */   
/* 325:    */   private void ritMinMin(ArrayList[] tmpsO2, ArrayList[] tmpsC2, int offset)
/* 326:    */   {
/* 327:362 */     Image[] tmpsO = new Image[this.size.intValue()];
/* 328:363 */     Image[] tmpsC = new Image[this.size.intValue()];
/* 329:366 */     for (int i = 0; i < this.size.intValue(); i++)
/* 330:    */     {
/* 331:368 */       int len = tmpsO2[i].size();
/* 332:369 */       tmpsO[i] = ((Image)tmpsO2[i].get(0));
/* 333:371 */       for (int j = 1; j < len; j++) {
/* 334:372 */         tmpsO[i] = Minimum.invoke(tmpsO[i], (Image)tmpsO2[i].get(j));
/* 335:    */       }
/* 336:374 */       tmpsC[i] = ((Image)tmpsC2[i].get(0));
/* 337:376 */       for (int j = 1; j < len; j++) {
/* 338:377 */         tmpsC[i] = Minimum.invoke(tmpsC[i], (Image)tmpsC2[i].get(j));
/* 339:    */       }
/* 340:    */     }
/* 341:380 */     for (int i = 0; i < this.imageSize; i++)
/* 342:    */     {
/* 343:381 */       int indexO = 0;
/* 344:382 */       double maxO = 0.0D;
/* 345:    */       
/* 346:384 */       int indexC = 0;
/* 347:385 */       double maxC = 0.0D;
/* 348:387 */       for (int j = 0; j < this.size.intValue(); j++)
/* 349:    */       {
/* 350:388 */         double dO = tmpsO[j].getDouble(i);
/* 351:389 */         double dC = tmpsC[j].getDouble(i);
/* 352:    */         
/* 353:391 */         double tmp = dO;
/* 354:393 */         if (tmp > maxO)
/* 355:    */         {
/* 356:394 */           maxO = tmp;
/* 357:395 */           indexO = j;
/* 358:    */         }
/* 359:398 */         tmp = dC;
/* 360:400 */         if (tmp > maxC)
/* 361:    */         {
/* 362:401 */           maxC = tmp;
/* 363:402 */           indexC = j;
/* 364:    */         }
/* 365:    */       }
/* 366:407 */       this.output[(offset + indexO)] += 1.0D;
/* 367:408 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   private void ritMaxMin(ArrayList[] tmpsO2, ArrayList[] tmpsC2, int offset)
/* 372:    */   {
/* 373:413 */     Image[] tmpsO = new Image[this.size.intValue()];
/* 374:414 */     Image[] tmpsC = new Image[this.size.intValue()];
/* 375:417 */     for (int i = 0; i < this.size.intValue(); i++)
/* 376:    */     {
/* 377:419 */       int len = tmpsO2[i].size();
/* 378:420 */       tmpsO[i] = ((Image)tmpsO2[i].get(0));
/* 379:422 */       for (int j = 1; j < len; j++) {
/* 380:423 */         tmpsO[i] = Maximum.invoke(tmpsO[i], (Image)tmpsO2[i].get(j));
/* 381:    */       }
/* 382:425 */       tmpsC[i] = ((Image)tmpsC2[i].get(0));
/* 383:427 */       for (int j = 1; j < len; j++) {
/* 384:428 */         tmpsC[i] = Minimum.invoke(tmpsC[i], (Image)tmpsC2[i].get(j));
/* 385:    */       }
/* 386:    */     }
/* 387:431 */     for (int i = 0; i < this.imageSize; i++)
/* 388:    */     {
/* 389:432 */       int indexO = 0;
/* 390:433 */       double maxO = 0.0D;
/* 391:    */       
/* 392:435 */       int indexC = 0;
/* 393:436 */       double maxC = 0.0D;
/* 394:438 */       for (int j = 0; j < this.size.intValue(); j++)
/* 395:    */       {
/* 396:439 */         double dO = tmpsO[j].getDouble(i);
/* 397:440 */         double dC = tmpsC[j].getDouble(i);
/* 398:    */         
/* 399:442 */         double tmp = dO;
/* 400:444 */         if (tmp > maxO)
/* 401:    */         {
/* 402:445 */           maxO = tmp;
/* 403:446 */           indexO = j;
/* 404:    */         }
/* 405:449 */         tmp = dC;
/* 406:451 */         if (tmp > maxC)
/* 407:    */         {
/* 408:452 */           maxC = tmp;
/* 409:453 */           indexC = j;
/* 410:    */         }
/* 411:    */       }
/* 412:458 */       this.output[(offset + indexO)] += 1.0D;
/* 413:459 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 414:    */     }
/* 415:    */   }
/* 416:    */   
/* 417:    */   private void locHistoOC(Image[] tmpsO, Image[] tmpsC, FlatSE[] ses, int offset)
/* 418:    */   {
/* 419:464 */     ByteImage tmpO = new ByteImage(this.input, false);
/* 420:465 */     tmpO.fill(0);
/* 421:    */     
/* 422:467 */     ByteImage tmpC = new ByteImage(this.input, false);
/* 423:468 */     tmpC.fill(0);
/* 424:470 */     for (int i = 0; i < this.size.intValue(); i++)
/* 425:    */     {
/* 426:471 */       for (int j = 0; j < this.input.getSize(); j++)
/* 427:    */       {
/* 428:472 */         double d = this.input.getDouble(j);
/* 429:473 */         double d1 = tmpsO[i].getDouble(j);
/* 430:476 */         if (Tools.cmpr(d, d1) != 0)
/* 431:    */         {
/* 432:477 */           int p = tmpO.getByte(j);
/* 433:478 */           p++;
/* 434:479 */           tmpO.setByte(j, p);
/* 435:    */         }
/* 436:    */       }
/* 437:483 */       for (int j = 0; j < this.input.getSize(); j++)
/* 438:    */       {
/* 439:484 */         double d = this.input.getDouble(j);
/* 440:485 */         double d2 = tmpsC[i].getDouble(j);
/* 441:487 */         if (Tools.cmpr(d, d2) != 0)
/* 442:    */         {
/* 443:488 */           int p = tmpC.getByte(j);
/* 444:489 */           p++;
/* 445:490 */           tmpC.setByte(j, p);
/* 446:    */         }
/* 447:    */       }
/* 448:    */     }
/* 449:495 */     for (int i = 0; i < tmpO.getSize(); i++) {
/* 450:496 */       this.output[(offset + tmpO.getByte(i))] += 1.0D;
/* 451:    */     }
/* 452:498 */     for (int i = 0; i < tmpC.getSize(); i++) {
/* 453:499 */       this.output[(offset + this.size.intValue() + 1 + tmpC.getByte(i))] += 1.0D;
/* 454:    */     }
/* 455:    */   }
/* 456:    */   
/* 457:    */   private void histoED(Image[] tmpsO, Image[] tmpsC, FlatSE[] ses, int offset)
/* 458:    */   {
/* 459:503 */     for (int i = 0; i < this.imageSize; i++)
/* 460:    */     {
/* 461:504 */       int indexO = 0;
/* 462:505 */       double maxO = 0.0D;
/* 463:    */       
/* 464:507 */       int indexC = 0;
/* 465:508 */       double maxC = 0.0D;
/* 466:510 */       for (int j = 0; j < this.size.intValue(); j++)
/* 467:    */       {
/* 468:511 */         double dO = tmpsO[j].getDouble(i);
/* 469:512 */         double dC = tmpsC[j].getDouble(i);
/* 470:    */         
/* 471:514 */         double original = this.input.getDouble(i);
/* 472:    */         
/* 473:516 */         double tmp = (original - dO) / ses[j].getCoords().length;
/* 474:518 */         if (tmp > maxO)
/* 475:    */         {
/* 476:519 */           maxO = tmp;
/* 477:520 */           indexO = j;
/* 478:    */         }
/* 479:523 */         tmp = (dC - original) / ses[j].getCoords().length;
/* 480:525 */         if (tmp > maxC)
/* 481:    */         {
/* 482:526 */           maxC = tmp;
/* 483:527 */           indexC = j;
/* 484:    */         }
/* 485:    */       }
/* 486:532 */       this.output[(offset + indexO)] += 1.0D;
/* 487:533 */       this.output[(offset + this.size.intValue() + indexC)] += 1.0D;
/* 488:    */     }
/* 489:    */   }
/* 490:    */   
/* 491:    */   private void histoOCDouble(Image[] tmpsO, Image[] tmpsC, FlatSE[] ses, int offset)
/* 492:    */   {
/* 493:538 */     for (int i = 0; i < this.imageSize; i++)
/* 494:    */     {
/* 495:539 */       int indexO = 0;
/* 496:540 */       double maxO = 0.0D;
/* 497:    */       
/* 498:542 */       int indexO2 = 0;
/* 499:543 */       double maxO2 = 0.0D;
/* 500:    */       
/* 501:545 */       int indexC = 0;
/* 502:546 */       double maxC = 0.0D;
/* 503:    */       
/* 504:548 */       int indexC2 = 0;
/* 505:549 */       double maxC2 = 0.0D;
/* 506:552 */       for (int j = 0; j < this.size.intValue(); j++)
/* 507:    */       {
/* 508:553 */         double dO = tmpsO[j].getDouble(i);
/* 509:554 */         double dC = tmpsC[j].getDouble(i);
/* 510:    */         
/* 511:556 */         double original = this.input.getDouble(i);
/* 512:    */         
/* 513:558 */         double tmp = Math.abs(original - dO) / ses[j].getCoords().length;
/* 514:560 */         if (tmp > maxO)
/* 515:    */         {
/* 516:561 */           maxO = tmp;
/* 517:562 */           indexO = j;
/* 518:    */         }
/* 519:565 */         tmp = Math.abs(dC - original) / ses[j].getCoords().length;
/* 520:567 */         if (tmp > maxC)
/* 521:    */         {
/* 522:568 */           maxC = tmp;
/* 523:569 */           indexC = j;
/* 524:    */         }
/* 525:    */       }
/* 526:574 */       for (int j = 0; j < this.size.intValue(); j++)
/* 527:    */       {
/* 528:575 */         double dO = tmpsO[j].getDouble(i);
/* 529:576 */         double dC = tmpsC[j].getDouble(i);
/* 530:    */         
/* 531:578 */         double original = this.input.getDouble(i);
/* 532:580 */         if (j != indexO)
/* 533:    */         {
/* 534:581 */           double tmp = Math.abs(original - dO) / ses[j].getCoords().length;
/* 535:583 */           if (tmp > maxO2)
/* 536:    */           {
/* 537:584 */             maxO2 = tmp;
/* 538:585 */             indexO2 = j;
/* 539:    */           }
/* 540:    */         }
/* 541:589 */         if (j != indexC)
/* 542:    */         {
/* 543:590 */           double tmp = Math.abs(dC - original) / ses[j].getCoords().length;
/* 544:592 */           if (tmp > maxC2)
/* 545:    */           {
/* 546:593 */             maxC2 = tmp;
/* 547:594 */             indexC2 = j;
/* 548:    */           }
/* 549:    */         }
/* 550:    */       }
/* 551:600 */       this.output[(offset + indexO)] += 1.0D;
/* 552:601 */       this.output[(offset + this.size.intValue() + indexO2)] += 1.0D;
/* 553:602 */       this.output[(offset + 2 * this.size.intValue() + indexC)] += 1.0D;
/* 554:603 */       this.output[(offset + 3 * this.size.intValue() + indexC2)] += 1.0D;
/* 555:    */     }
/* 556:    */   }
/* 557:    */   
/* 558:    */   public static double[] invoke(Image image, Integer size)
/* 559:    */   {
/* 560:    */     try
/* 561:    */     {
/* 562:609 */       return (double[])new ExtendedMorphologicalCovariance().preprocess(new Object[] { image, size });
/* 563:    */     }
/* 564:    */     catch (GlobalException e)
/* 565:    */     {
/* 566:611 */       e.printStackTrace();
/* 567:    */     }
/* 568:612 */     return null;
/* 569:    */   }
/* 570:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.ExtendedMorphologicalCovariance
 * JD-Core Version:    0.7.0.1
 */
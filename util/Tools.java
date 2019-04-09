/*   1:    */ package vpt.util;
/*   2:    */ 
/*   3:    */ import com.sun.media.jai.codecimpl.util.RasterFactory;
/*   4:    */ import java.awt.Point;
/*   5:    */ import java.awt.image.BufferedImage;
/*   6:    */ import java.awt.image.ColorModel;
/*   7:    */ import java.awt.image.DataBufferByte;
/*   8:    */ import java.awt.image.Raster;
/*   9:    */ import java.awt.image.SampleModel;
/*  10:    */ import java.awt.image.WritableRaster;
/*  11:    */ import java.io.InputStream;
/*  12:    */ import java.io.OutputStream;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.util.ArrayList;
/*  15:    */ import java.util.Random;
/*  16:    */ import vpt.BooleanImage;
/*  17:    */ import vpt.ByteImage;
/*  18:    */ import vpt.Image;
/*  19:    */ 
/*  20:    */ public class Tools
/*  21:    */ {
/*  22:    */   public static final double epsilon = 1.0E-008D;
/*  23:    */   
/*  24:    */   public static double radian2degree(double radian)
/*  25:    */   {
/*  26: 25 */     return radian * 180.0D / 3.141592653589793D;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static double degree2radian(double degree)
/*  30:    */   {
/*  31: 29 */     return degree * 3.141592653589793D / 180.0D;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static double factorial(long n)
/*  35:    */   {
/*  36: 33 */     if (n < 0L) {
/*  37: 34 */       return 0.0D;
/*  38:    */     }
/*  39: 35 */     if (n == 0L) {
/*  40: 36 */       return 1.0D;
/*  41:    */     }
/*  42: 38 */     return n * factorial(n - 1L);
/*  43:    */   }
/*  44:    */   
/*  45: 42 */   public static final Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  46: 43 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  47:    */   
/*  48:    */   public static int cmpr(double d1, double d2)
/*  49:    */   {
/*  50: 47 */     double diff = d1 - d2;
/*  51: 50 */     if ((diff >= -1.0E-008D) && (diff <= 1.0E-008D)) {
/*  52: 50 */       return 0;
/*  53:    */     }
/*  54: 51 */     if (diff > 0.0D) {
/*  55: 51 */       return 1;
/*  56:    */     }
/*  57: 52 */     return -1;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static double[] VAddC(double[] v, double c)
/*  61:    */   {
/*  62: 62 */     double[] tmp = new double[v.length];
/*  63: 64 */     for (int i = 0; i < v.length; i++) {
/*  64: 65 */       v[i] += c;
/*  65:    */     }
/*  66: 67 */     return tmp;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static void shuffle(int[] array)
/*  70:    */   {
/*  71: 71 */     Random r = new Random();
/*  72: 73 */     for (int i = array.length - 1; i >= 1; i--)
/*  73:    */     {
/*  74: 74 */       int j = r.nextInt(i + 1);
/*  75:    */       
/*  76: 76 */       int tmp = array[j];
/*  77: 77 */       array[j] = array[i];
/*  78: 78 */       array[i] = tmp;
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static double EuclideanDistance(int[] v, int[] w)
/*  83:    */   {
/*  84: 84 */     int sum = 0;
/*  85: 86 */     for (int i = 0; i < v.length; i++) {
/*  86: 87 */       sum += (v[i] - w[i]) * (v[i] - w[i]);
/*  87:    */     }
/*  88: 89 */     return Math.sqrt(sum);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static double euclideanDistance(double[] v, double[] w)
/*  92:    */   {
/*  93: 95 */     double sum = 0.0D;
/*  94: 97 */     for (int i = 0; i < v.length; i++) {
/*  95: 98 */       sum += (v[i] - w[i]) * (v[i] - w[i]);
/*  96:    */     }
/*  97:100 */     return Math.sqrt(sum);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static double EuclideanNorm(double[] v)
/* 101:    */   {
/* 102:105 */     double norm = 0.0D;
/* 103:107 */     for (int i = 0; i < v.length; i++) {
/* 104:108 */       norm += v[i] * v[i];
/* 105:    */     }
/* 106:110 */     return Math.sqrt(norm);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static double[] VSub(double[] v1, double[] v2)
/* 110:    */   {
/* 111:115 */     if (v1.length != v2.length)
/* 112:    */     {
/* 113:116 */       System.err.println("Incompatible vector lengths");
/* 114:117 */       return null;
/* 115:    */     }
/* 116:120 */     double[] diff = new double[v1.length];
/* 117:122 */     for (int i = 0; i < v1.length; i++) {
/* 118:123 */       v1[i] -= v2[i];
/* 119:    */     }
/* 120:125 */     return diff;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void cleanupProcess(Process p)
/* 124:    */   {
/* 125:    */     try
/* 126:    */     {
/* 127:136 */       p.waitFor();
/* 128:137 */       p.getInputStream().close();
/* 129:138 */       p.getOutputStream().close();
/* 130:139 */       p.getErrorStream().close();
/* 131:    */     }
/* 132:    */     catch (Exception e)
/* 133:    */     {
/* 134:141 */       e.printStackTrace();
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public static double[] Vext(double[] v, int dim)
/* 139:    */   {
/* 140:152 */     double[] newVector = new double[v.length - dim];
/* 141:154 */     for (int i = 0; i < newVector.length; i++) {
/* 142:155 */       newVector[i] = v[(dim + i)];
/* 143:    */     }
/* 144:157 */     return newVector;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static double volumeAll(Image img)
/* 148:    */   {
/* 149:167 */     double d = 0.0D;
/* 150:168 */     int cdim = img.getCDim();
/* 151:170 */     for (int c = 0; c < cdim; c++) {
/* 152:171 */       d += volume(img, c);
/* 153:    */     }
/* 154:173 */     return d;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static double volume(Image img, int channel)
/* 158:    */   {
/* 159:184 */     double d = 0.0D;
/* 160:185 */     int xdim = img.getXDim();
/* 161:186 */     int ydim = img.getYDim();
/* 162:188 */     for (int x = 0; x < xdim; x++) {
/* 163:189 */       for (int y = 0; y < ydim; y++) {
/* 164:190 */         d += img.getXYCDouble(x, y, channel);
/* 165:    */       }
/* 166:    */     }
/* 167:192 */     return d;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static double hueDistance(double h, double ref)
/* 171:    */   {
/* 172:196 */     double abs = Math.abs(ref - h);
/* 173:198 */     if (abs <= 0.5D) {
/* 174:198 */       return abs;
/* 175:    */     }
/* 176:199 */     return 1.0D - abs;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static double circularVolume(Image img, int channel, int circleIndex, int howManyCircles)
/* 180:    */   {
/* 181:215 */     int centerX = img.getXDim() / 2;
/* 182:216 */     int centerY = img.getYDim() / 2;
/* 183:    */     
/* 184:218 */     int radius = (circleIndex + 1) * Math.min(centerX, centerY) / howManyCircles;
/* 185:221 */     if (circleIndex + 1 == howManyCircles) {
/* 186:221 */       radius = 2147483647;
/* 187:    */     }
/* 188:223 */     double d = 0.0D;
/* 189:224 */     int xdim = img.getXDim();
/* 190:225 */     int ydim = img.getYDim();
/* 191:227 */     for (int x = 0; x < xdim; x++) {
/* 192:228 */       for (int y = 0; y < ydim; y++) {
/* 193:229 */         if (Distance.EuclideanDistance(x, y, centerX, centerY) <= radius) {
/* 194:230 */           d += img.getXYCDouble(x, y, channel);
/* 195:    */         }
/* 196:    */       }
/* 197:    */     }
/* 198:233 */     return d;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static double circularMoment(Image img, int channel, int circleIndex, int howManyCircles, int moment)
/* 202:    */   {
/* 203:250 */     int centerX = img.getXDim() / 2;
/* 204:251 */     int centerY = img.getYDim() / 2;
/* 205:    */     
/* 206:253 */     int radius = (circleIndex + 1) * Math.min(centerX, centerY) / howManyCircles;
/* 207:256 */     if (circleIndex + 1 == howManyCircles) {
/* 208:256 */       radius = 2147483647;
/* 209:    */     }
/* 210:258 */     int xdim = img.getXDim();
/* 211:259 */     int ydim = img.getYDim();
/* 212:    */     
/* 213:261 */     Image tmp = img.getChannel(channel);
/* 214:262 */     tmp.fill(0);
/* 215:264 */     for (int x = 0; x < xdim; x++) {
/* 216:265 */       for (int y = 0; y < ydim; y++) {
/* 217:266 */         if (Distance.EuclideanDistance(x, y, centerX, centerY) <= radius) {
/* 218:267 */           tmp.setXYDouble(x, y, img.getXYCDouble(x, y, channel));
/* 219:    */         }
/* 220:    */       }
/* 221:    */     }
/* 222:271 */     return vpt.algorithms.statistical.InvariantMoment.invoke(tmp, java.lang.Integer.valueOf(moment))[0];
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static double circularBandVolume(Image img, int channel, int circleIndex, int howManyCircles)
/* 226:    */   {
/* 227:287 */     int centerX = img.getXDim() / 2;
/* 228:288 */     int centerY = img.getYDim() / 2;
/* 229:    */     
/* 230:290 */     int outerRadius = (circleIndex + 1) * Math.min(centerX, centerY) / howManyCircles;
/* 231:291 */     int innerRadius = circleIndex * Math.min(centerX, centerY) / howManyCircles;
/* 232:294 */     if (circleIndex + 1 == howManyCircles) {
/* 233:294 */       outerRadius = 2147483647;
/* 234:    */     }
/* 235:296 */     double d = 0.0D;
/* 236:297 */     int xdim = img.getXDim();
/* 237:298 */     int ydim = img.getYDim();
/* 238:300 */     for (int x = 0; x < xdim; x++) {
/* 239:301 */       for (int y = 0; y < ydim; y++)
/* 240:    */       {
/* 241:302 */         double dist = Distance.EuclideanDistance(x, y, centerX, centerY);
/* 242:303 */         if ((dist <= outerRadius) && (dist >= innerRadius)) {
/* 243:304 */           d += img.getXYCDouble(x, y, channel);
/* 244:    */         }
/* 245:    */       }
/* 246:    */     }
/* 247:307 */     return d;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public static double irregularCircularBandVolumes(Image img, int bandIndex)
/* 251:    */   {
/* 252:318 */     int centerX = img.getXDim() / 2;
/* 253:319 */     int centerY = img.getYDim() / 2;
/* 254:    */     
/* 255:321 */     double outerRadius = Math.pow(2.0D, bandIndex);
/* 256:322 */     double innerRadius = Math.pow(2.0D, bandIndex - 1);
/* 257:324 */     if ((bandIndex == 0) || (bandIndex == 1))
/* 258:    */     {
/* 259:325 */       outerRadius = 2.0D;
/* 260:326 */       innerRadius = 0.0D;
/* 261:    */     }
/* 262:329 */     double d = 0.0D;
/* 263:330 */     int xdim = img.getXDim();
/* 264:331 */     int ydim = img.getYDim();
/* 265:333 */     for (int x = 0; x < xdim; x++) {
/* 266:334 */       for (int y = 0; y < ydim; y++)
/* 267:    */       {
/* 268:335 */         double dist = Distance.EuclideanDistance(x, y, centerX, centerY);
/* 269:336 */         if ((dist <= outerRadius) && (dist >= innerRadius)) {
/* 270:337 */           d += img.getXYDouble(x, y);
/* 271:    */         }
/* 272:    */       }
/* 273:    */     }
/* 274:340 */     return d;
/* 275:    */   }
/* 276:    */   
/* 277:    */   public static double irregularCircularBandVolumes2(Image img, int bandIndex, int howManyCircles)
/* 278:    */   {
/* 279:352 */     int xdim = img.getXDim();
/* 280:353 */     int ydim = img.getYDim();
/* 281:    */     
/* 282:355 */     int centerX = xdim / 2;
/* 283:356 */     int centerY = ydim / 2;
/* 284:    */     
/* 285:358 */     double maxRadius = Math.sqrt(2.0D) * Math.min(xdim / 2, ydim / 2);
/* 286:    */     
/* 287:360 */     double outerRadius = 1.0D / Math.pow(2.0D, howManyCircles - bandIndex) * maxRadius;
/* 288:361 */     double innerRadius = 1.0D / Math.pow(2.0D, howManyCircles - bandIndex + 1) * maxRadius;
/* 289:363 */     if (bandIndex <= 1) {
/* 290:363 */       innerRadius = 0.0D;
/* 291:    */     }
/* 292:365 */     double d = 0.0D;
/* 293:367 */     for (int x = 0; x < xdim; x++) {
/* 294:368 */       for (int y = 0; y < ydim; y++)
/* 295:    */       {
/* 296:369 */         double dist = Distance.EuclideanDistance(x, y, centerX, centerY);
/* 297:370 */         if ((dist <= outerRadius) && (dist >= innerRadius)) {
/* 298:371 */           d += img.getXYDouble(x, y);
/* 299:    */         }
/* 300:    */       }
/* 301:    */     }
/* 302:374 */     return d;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public static double regularAngularVolumes(Image img, int angleIndex, int howManyAngles)
/* 306:    */   {
/* 307:387 */     int xdim = img.getXDim();
/* 308:388 */     int ydim = img.getYDim();
/* 309:    */     
/* 310:    */ 
/* 311:391 */     double theta = 3.141592653589793D / howManyAngles;
/* 312:    */     
/* 313:393 */     double angle1 = (angleIndex + 1) % howManyAngles * theta;
/* 314:394 */     double angle2 = angleIndex * theta;
/* 315:395 */     if (angleIndex == howManyAngles - 1) {
/* 316:395 */       angle1 = 3.141592653589793D;
/* 317:    */     }
/* 318:397 */     double d = 0.0D;
/* 319:400 */     for (int x = xdim / 2; x < xdim; x++) {
/* 320:401 */       for (int y = 0; y < ydim; y++)
/* 321:    */       {
/* 322:403 */         int _x = x - xdim / 2;
/* 323:    */         int _y;
/* 324:    */         int _y;
/* 325:406 */         if (y <= ydim / 2) {
/* 326:406 */           _y = ydim / 2 - y;
/* 327:    */         } else {
/* 328:407 */           _y = -1 * (y - ydim / 2);
/* 329:    */         }
/* 330:410 */         double myAngle = Math.atan2(_y, _x);
/* 331:412 */         if (myAngle < 0.0D) {
/* 332:412 */           myAngle += 3.141592653589793D;
/* 333:    */         }
/* 334:414 */         if ((angle1 >= myAngle) && (myAngle >= angle2)) {
/* 335:415 */           d += img.getXYDouble(x, y);
/* 336:    */         }
/* 337:    */       }
/* 338:    */     }
/* 339:419 */     return d;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public static double[] normalize(double[] feature)
/* 343:    */   {
/* 344:430 */     double mean = mean(feature);
/* 345:431 */     double sdev = sdev(feature);
/* 346:    */     
/* 347:433 */     double[] newFeature = new double[feature.length];
/* 348:435 */     if (cmpr(sdev, 0.0D) == 0) {
/* 349:435 */       return null;
/* 350:    */     }
/* 351:437 */     for (int i = 0; i < feature.length; i++) {
/* 352:438 */       newFeature[i] = ((feature[i] - mean) / sdev);
/* 353:    */     }
/* 354:440 */     return newFeature;
/* 355:    */   }
/* 356:    */   
/* 357:    */   public static double sdev(double[] feature)
/* 358:    */   {
/* 359:450 */     double mean = mean(feature);
/* 360:451 */     double sdev = 0.0D;
/* 361:453 */     if (feature.length == 0) {
/* 362:453 */       return 0.0D;
/* 363:    */     }
/* 364:455 */     for (int i = 0; i < feature.length; i++) {
/* 365:456 */       sdev += (feature[i] - mean) * (feature[i] - mean);
/* 366:    */     }
/* 367:458 */     sdev /= feature.length;
/* 368:    */     
/* 369:460 */     return Math.sqrt(sdev);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static double mean(double[] feature)
/* 373:    */   {
/* 374:470 */     double mean = 0.0D;
/* 375:472 */     if (feature.length == 0) {
/* 376:472 */       return 0.0D;
/* 377:    */     }
/* 378:474 */     for (int i = 0; i < feature.length; i++) {
/* 379:475 */       mean += feature[i];
/* 380:    */     }
/* 381:477 */     return mean / feature.length;
/* 382:    */   }
/* 383:    */   
/* 384:    */   public static double[] histogramOfDistancesFromCoG(Image img, int histoLength)
/* 385:    */   {
/* 386:481 */     int xdim = img.getXDim();
/* 387:482 */     int ydim = img.getYDim();
/* 388:483 */     int cdim = img.getCDim();
/* 389:485 */     if ((cdim != 1) || (histoLength == 0)) {
/* 390:485 */       return null;
/* 391:    */     }
/* 392:487 */     Point cog = centerOfGravity(img);
/* 393:    */     
/* 394:489 */     long numberOfNonZeroPixels = 0L;
/* 395:490 */     double[] histo = new double[histoLength + 1];
/* 396:    */     
/* 397:    */ 
/* 398:493 */     double min = 1.7976931348623157E+308D;
/* 399:494 */     double max = 4.9E-324D;
/* 400:496 */     for (int x = 0; x < xdim; x++) {
/* 401:497 */       for (int y = 0; y < ydim; y++)
/* 402:    */       {
/* 403:498 */         double p = img.getXYDouble(x, y);
/* 404:499 */         if (p > 0.0D)
/* 405:    */         {
/* 406:500 */           numberOfNonZeroPixels += 1L;
/* 407:501 */           double tmp = Distance.EuclideanDistance(cog.x, cog.y, x, y);
/* 408:503 */           if (tmp < min) {
/* 409:503 */             min = tmp;
/* 410:504 */           } else if (tmp > max) {
/* 411:504 */             max = tmp;
/* 412:    */           }
/* 413:    */         }
/* 414:    */       }
/* 415:    */     }
/* 416:509 */     double coeff = (max - min) / histoLength;
/* 417:511 */     if (numberOfNonZeroPixels == 0L) {
/* 418:511 */       return null;
/* 419:    */     }
/* 420:514 */     for (int x = 0; x < xdim; x++) {
/* 421:515 */       for (int y = 0; y < ydim; y++)
/* 422:    */       {
/* 423:516 */         double p = img.getXYDouble(x, y);
/* 424:517 */         if (p > 0.0D)
/* 425:    */         {
/* 426:518 */           double tmp = Distance.EuclideanDistance(cog.x, cog.y, x, y);
/* 427:519 */           histo[((int)((tmp - min) / coeff))] += 1.0D;
/* 428:    */         }
/* 429:    */       }
/* 430:    */     }
/* 431:525 */     for (int i = 0; i < histo.length; i++) {
/* 432:526 */       histo[i] /= numberOfNonZeroPixels;
/* 433:    */     }
/* 434:528 */     return histo;
/* 435:    */   }
/* 436:    */   
/* 437:    */   public static double avgDistanceFromCoG(Image img)
/* 438:    */   {
/* 439:532 */     int xdim = img.getXDim();
/* 440:533 */     int ydim = img.getYDim();
/* 441:534 */     int cdim = img.getCDim();
/* 442:536 */     if (cdim != 1) {
/* 443:536 */       return -1.0D;
/* 444:    */     }
/* 445:538 */     Point cog = centerOfGravity(img);
/* 446:    */     
/* 447:540 */     double avgDistance = 0.0D;
/* 448:541 */     long numberOfNonZeroPixels = 0L;
/* 449:543 */     for (int x = 0; x < xdim; x++) {
/* 450:544 */       for (int y = 0; y < ydim; y++)
/* 451:    */       {
/* 452:545 */         double p = img.getXYDouble(x, y);
/* 453:546 */         if (p > 0.0D)
/* 454:    */         {
/* 455:547 */           numberOfNonZeroPixels += 1L;
/* 456:548 */           avgDistance += Distance.EuclideanDistance(cog.x, cog.y, x, y);
/* 457:    */         }
/* 458:    */       }
/* 459:    */     }
/* 460:553 */     if (numberOfNonZeroPixels != 0L) {
/* 461:554 */       return avgDistance / numberOfNonZeroPixels;
/* 462:    */     }
/* 463:555 */     return -1.0D;
/* 464:    */   }
/* 465:    */   
/* 466:    */   public static Point centerOfGravity(Image img)
/* 467:    */   {
/* 468:559 */     int xdim = img.getXDim();
/* 469:560 */     int ydim = img.getYDim();
/* 470:561 */     int cdim = img.getCDim();
/* 471:563 */     if (cdim != 1) {
/* 472:563 */       return null;
/* 473:    */     }
/* 474:565 */     Point cog = new Point(0, 0);
/* 475:    */     
/* 476:567 */     double cx = 0.0D;
/* 477:568 */     double cy = 0.0D;
/* 478:    */     
/* 479:570 */     double volume = volume(img, 0);
/* 480:572 */     for (int x = 0; x < xdim; x++) {
/* 481:573 */       for (int y = 0; y < ydim; y++)
/* 482:    */       {
/* 483:574 */         double p = img.getXYDouble(x, y);
/* 484:575 */         if (p > 0.0D)
/* 485:    */         {
/* 486:576 */           cx += x * p;
/* 487:577 */           cy += y * p;
/* 488:    */         }
/* 489:    */       }
/* 490:    */     }
/* 491:582 */     cog.x = ((int)(cx / volume));
/* 492:583 */     cog.y = ((int)(cy / volume));
/* 493:    */     
/* 494:585 */     return cog;
/* 495:    */   }
/* 496:    */   
/* 497:    */   public static long numberOfNonZeroPixels(Image img)
/* 498:    */   {
/* 499:589 */     int xdim = img.getXDim();
/* 500:590 */     int ydim = img.getYDim();
/* 501:591 */     int cdim = img.getCDim();
/* 502:593 */     if (cdim != 1) {
/* 503:593 */       return -1L;
/* 504:    */     }
/* 505:595 */     long counter = 0L;
/* 506:597 */     for (int x = 0; x < xdim; x++) {
/* 507:598 */       for (int y = 0; y < ydim; y++)
/* 508:    */       {
/* 509:599 */         int p = img.getXYByte(x, y);
/* 510:600 */         if (p > 0) {
/* 511:600 */           counter += 1L;
/* 512:    */         }
/* 513:    */       }
/* 514:    */     }
/* 515:604 */     return counter;
/* 516:    */   }
/* 517:    */   
/* 518:    */   public static long numberOfDifferentPixels(Image img1, Image img2)
/* 519:    */   {
/* 520:616 */     if (!img1.hasSameDimensionsAs(img2)) {
/* 521:616 */       return -1L;
/* 522:    */     }
/* 523:618 */     int xdim = img1.getXDim();
/* 524:619 */     int ydim = img1.getYDim();
/* 525:    */     
/* 526:621 */     long counter = 0L;
/* 527:623 */     for (int x = 0; x < xdim; x++) {
/* 528:624 */       for (int y = 0; y < ydim; y++)
/* 529:    */       {
/* 530:625 */         int p = img1.getXYByte(x, y);
/* 531:626 */         int q = img2.getXYByte(x, y);
/* 532:627 */         if (q != p) {
/* 533:627 */           counter += 1L;
/* 534:    */         }
/* 535:    */       }
/* 536:    */     }
/* 537:631 */     return counter;
/* 538:    */   }
/* 539:    */   
/* 540:    */   public static Image shiftOrigin(Image img)
/* 541:    */   {
/* 542:642 */     Image output = img.newInstance(false);
/* 543:    */     
/* 544:644 */     int xdim = img.getXDim();
/* 545:645 */     int ydim = img.getYDim();
/* 546:647 */     for (int x = 0; x < xdim / 2; x++) {
/* 547:648 */       for (int y = 0; y < ydim / 2; y++)
/* 548:    */       {
/* 549:649 */         double p = img.getXYDouble(x, y);
/* 550:650 */         output.setXYDouble(x + xdim / 2, y + ydim / 2, p);
/* 551:    */       }
/* 552:    */     }
/* 553:654 */     for (int x = 0; x < xdim / 2; x++) {
/* 554:655 */       for (int y = ydim / 2; y < ydim; y++)
/* 555:    */       {
/* 556:656 */         double p = img.getXYDouble(x, y);
/* 557:657 */         output.setXYDouble(x + xdim / 2, y - ydim / 2, p);
/* 558:    */       }
/* 559:    */     }
/* 560:661 */     for (int x = xdim / 2; x < xdim; x++) {
/* 561:662 */       for (int y = 0; y < ydim / 2; y++)
/* 562:    */       {
/* 563:663 */         double p = img.getXYDouble(x, y);
/* 564:664 */         output.setXYDouble(x - xdim / 2, y + ydim / 2, p);
/* 565:    */       }
/* 566:    */     }
/* 567:668 */     for (int x = xdim / 2; x < xdim; x++) {
/* 568:669 */       for (int y = ydim / 2; y < ydim; y++)
/* 569:    */       {
/* 570:670 */         double p = img.getXYDouble(x, y);
/* 571:671 */         output.setXYDouble(x - xdim / 2, y - ydim / 2, p);
/* 572:    */       }
/* 573:    */     }
/* 574:675 */     return output;
/* 575:    */   }
/* 576:    */   
/* 577:    */   public static double DftMagVol(double[] d)
/* 578:    */   {
/* 579:681 */     double[] real = new double[d.length];
/* 580:682 */     double[] imag = new double[d.length];
/* 581:683 */     double vol = 0.0D;
/* 582:685 */     for (int i = 0; i < d.length; i++)
/* 583:    */     {
/* 584:686 */       double tmp25_24 = 0.0D;imag[i] = tmp25_24;real[i] = tmp25_24;
/* 585:688 */       for (int j = 0; j < d.length; j++)
/* 586:    */       {
/* 587:689 */         real[i] += d[j] * Math.cos(-6.283185307179586D * j * i / d.length);
/* 588:690 */         imag[i] += d[j] * Math.sin(-6.283185307179586D * j * i / d.length);
/* 589:    */       }
/* 590:693 */       real[i] /= Math.sqrt(d.length);
/* 591:694 */       imag[i] /= Math.sqrt(d.length);
/* 592:    */     }
/* 593:698 */     for (int i = 0; i < d.length; i++) {
/* 594:699 */       vol += Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
/* 595:    */     }
/* 596:702 */     return vol;
/* 597:    */   }
/* 598:    */   
/* 599:    */   public static double[] dftMag1d(double[] d)
/* 600:    */   {
/* 601:713 */     double[] real = new double[d.length];
/* 602:714 */     double[] imag = new double[d.length];
/* 603:715 */     double[] output = new double[d.length];
/* 604:717 */     for (int i = 0; i < d.length; i++)
/* 605:    */     {
/* 606:718 */       double tmp28_27 = 0.0D;imag[i] = tmp28_27;real[i] = tmp28_27;
/* 607:720 */       for (int j = 0; j < d.length; j++)
/* 608:    */       {
/* 609:721 */         real[i] += d[j] * Math.cos(-6.283185307179586D * j * i / d.length);
/* 610:722 */         imag[i] += d[j] * Math.sin(-6.283185307179586D * j * i / d.length);
/* 611:    */       }
/* 612:725 */       real[i] /= Math.sqrt(d.length);
/* 613:726 */       imag[i] /= Math.sqrt(d.length);
/* 614:    */     }
/* 615:730 */     for (int i = 0; i < d.length; i++) {
/* 616:731 */       output[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
/* 617:    */     }
/* 618:733 */     return output;
/* 619:    */   }
/* 620:    */   
/* 621:    */   public static double norm(double[] a)
/* 622:    */   {
/* 623:743 */     double d = 0.0D;
/* 624:745 */     for (int i = 0; i < a.length; i++) {
/* 625:746 */       d += a[i] * a[i];
/* 626:    */     }
/* 627:748 */     return Math.sqrt(d);
/* 628:    */   }
/* 629:    */   
/* 630:    */   public static double dotProduct(double[] a, double[] b)
/* 631:    */   {
/* 632:759 */     double d = 0.0D;
/* 633:761 */     for (int i = 0; i < a.length; i++) {
/* 634:762 */       d += a[i] * b[i];
/* 635:    */     }
/* 636:764 */     return d;
/* 637:    */   }
/* 638:    */   
/* 639:    */   public static Image superpose(Image mask, Image support)
/* 640:    */   {
/* 641:776 */     Image tmp = support.newInstance(true);
/* 642:778 */     for (int x = 0; x < support.getXDim(); x++) {
/* 643:779 */       for (int y = 0; y < support.getYDim(); y++) {
/* 644:780 */         if (!mask.getXYBoolean(x, y))
/* 645:    */         {
/* 646:781 */           tmp.setXYCByte(x, y, 0, 0);
/* 647:782 */           tmp.setXYCByte(x, y, 1, 0);
/* 648:783 */           tmp.setXYCByte(x, y, 2, 0);
/* 649:    */         }
/* 650:    */       }
/* 651:    */     }
/* 652:788 */     return tmp;
/* 653:    */   }
/* 654:    */   
/* 655:    */   public static double getAngleABC(Point a, Point b, Point c)
/* 656:    */   {
/* 657:801 */     Point ab = new Point(b.x - a.x, b.y - a.y);
/* 658:802 */     Point cb = new Point(b.x - c.x, b.y - c.y);
/* 659:    */     
/* 660:    */ 
/* 661:805 */     double dot = ab.x * cb.x + ab.y * cb.y;
/* 662:    */     
/* 663:    */ 
/* 664:808 */     double abSqr = ab.x * ab.x + ab.y * ab.y;
/* 665:809 */     double cbSqr = cb.x * cb.x + cb.y * cb.y;
/* 666:    */     
/* 667:    */ 
/* 668:812 */     double cosSqr = dot * dot / abSqr / cbSqr;
/* 669:    */     
/* 670:    */ 
/* 671:    */ 
/* 672:816 */     double cos2 = 2.0D * cosSqr - 1.0D;
/* 673:    */     
/* 674:    */ 
/* 675:    */ 
/* 676:    */ 
/* 677:821 */     double alpha2 = 
/* 678:    */     
/* 679:823 */       cos2 >= 1.0D ? 0.0D : cos2 <= -1.0D ? 3.141592653589793D : 
/* 680:824 */       Math.acos(cos2);
/* 681:    */     
/* 682:826 */     double rslt = alpha2 / 2.0D;
/* 683:    */     
/* 684:828 */     double rs = rslt * 180.0D / 3.141592653589793D;
/* 685:838 */     if (dot < 0.0D) {
/* 686:838 */       rs = 180.0D - rs;
/* 687:    */     }
/* 688:841 */     float det = ab.x * cb.y - ab.y * cb.y;
/* 689:842 */     if (det < 0.0F) {
/* 690:842 */       rs = -rs;
/* 691:    */     }
/* 692:844 */     return Math.floor(rs + 0.5D);
/* 693:    */   }
/* 694:    */   
/* 695:    */   public static BufferedImage Image2BufferedImage(Image img)
/* 696:    */   {
/* 697:848 */     int[] bandOffsets = { 0, 1, 2 };
/* 698:    */     
/* 699:850 */     int bdim = img.getCDim();
/* 700:851 */     int xdim = img.getXDim();
/* 701:852 */     int ydim = img.getYDim();
/* 702:853 */     int size = bdim * xdim * ydim;
/* 703:    */     
/* 704:855 */     byte[] pixels = new byte[size];
/* 705:856 */     for (int i = 0; i < size; i++) {
/* 706:857 */       pixels[i] = ((byte)img.getByte(i));
/* 707:    */     }
/* 708:859 */     DataBufferByte dbb = new DataBufferByte(pixels, size);
/* 709:860 */     SampleModel s = RasterFactory.createPixelInterleavedSampleModel(0, xdim, ydim, bdim, bdim * xdim, bandOffsets);
/* 710:861 */     Raster r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
/* 711:862 */     BufferedImage bimg = new BufferedImage(xdim, ydim, 5);
/* 712:863 */     bimg.setData(r);
/* 713:    */     
/* 714:865 */     return bimg;
/* 715:    */   }
/* 716:    */   
/* 717:    */   public static Image BufferedImage2Image(BufferedImage bimage)
/* 718:    */   {
/* 719:870 */     WritableRaster raster = bimage.getRaster();
/* 720:871 */     Image output = null;
/* 721:    */     
/* 722:873 */     int xdim = bimage.getWidth();
/* 723:874 */     int ydim = bimage.getHeight();
/* 724:875 */     int cdim = raster.getNumBands();
/* 725:    */     
/* 726:877 */     int pixelsize = bimage.getColorModel().getPixelSize();
/* 727:879 */     if (pixelsize / cdim == 8)
/* 728:    */     {
/* 729:880 */       output = new ByteImage(xdim, ydim, cdim);
/* 730:882 */       for (int x = 0; x < xdim; x++) {
/* 731:883 */         for (int y = 0; y < ydim; y++) {
/* 732:884 */           for (int c = 0; c < cdim; c++) {
/* 733:885 */             output.setXYCByte(x, y, c, raster.getSample(x, y, c));
/* 734:    */           }
/* 735:    */         }
/* 736:    */       }
/* 737:886 */       return output;
/* 738:    */     }
/* 739:887 */     if (pixelsize / cdim == 1)
/* 740:    */     {
/* 741:889 */       output = new BooleanImage(xdim, ydim, cdim);
/* 742:891 */       for (int x = 0; x < xdim; x++) {
/* 743:892 */         for (int y = 0; y < ydim; y++) {
/* 744:893 */           for (int c = 0; c < cdim; c++) {
/* 745:894 */             output.setXYCBoolean(x, y, c, raster.getSample(x, y, c) != 0);
/* 746:    */           }
/* 747:    */         }
/* 748:    */       }
/* 749:896 */       return output;
/* 750:    */     }
/* 751:898 */     System.err.println("Unsupported file type");
/* 752:899 */     return null;
/* 753:    */   }
/* 754:    */   
/* 755:    */   public static Point centroid(ArrayList<Point> points)
/* 756:    */   {
/* 757:912 */     Point p = new Point(0, 0);
/* 758:914 */     for (Point q : points)
/* 759:    */     {
/* 760:915 */       p.x += q.x;
/* 761:916 */       p.y += q.y;
/* 762:    */     }
/* 763:919 */     p.x /= points.size();
/* 764:920 */     p.y /= points.size();
/* 765:    */     
/* 766:922 */     return p;
/* 767:    */   }
/* 768:    */   
/* 769:    */   public static ArrayList<Point> drawLine(Point p1, Point p2)
/* 770:    */   {
/* 771:931 */     ArrayList<Point> line = new ArrayList();
/* 772:    */     
/* 773:933 */     int x0 = p1.x;
/* 774:934 */     int x1 = p2.x;
/* 775:    */     
/* 776:936 */     int y0 = p1.y;
/* 777:937 */     int y1 = p2.y;
/* 778:    */     
/* 779:939 */     int dx = x1 - x0;
/* 780:940 */     int dy = y1 - y0;
/* 781:    */     
/* 782:942 */     line.add(new Point(x0, y0));
/* 783:944 */     if (Math.abs(dx) > Math.abs(dy))
/* 784:    */     {
/* 785:945 */       float m = dy / dx;
/* 786:946 */       float b = y0 - m * x0;
/* 787:947 */       dx = dx < 0 ? -1 : 1;
/* 788:948 */       while (x0 != x1)
/* 789:    */       {
/* 790:949 */         x0 += dx;
/* 791:950 */         line.add(new Point(x0, Math.round(m * x0 + b)));
/* 792:    */       }
/* 793:    */     }
/* 794:953 */     else if (dy != 0)
/* 795:    */     {
/* 796:954 */       float m = dx / dy;
/* 797:955 */       float b = x0 - m * y0;
/* 798:956 */       dy = dy < 0 ? -1 : 1;
/* 799:957 */       while (y0 != y1)
/* 800:    */       {
/* 801:958 */         y0 += dy;
/* 802:959 */         line.add(new Point(Math.round(m * y0 + b), y0));
/* 803:    */       }
/* 804:    */     }
/* 805:963 */     return line;
/* 806:    */   }
/* 807:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.Tools
 * JD-Core Version:    0.7.0.1
 */
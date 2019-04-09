/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Vector;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.ByteImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.algorithms.display.Display2D;
/*  12:    */ import vpt.algorithms.flatzones.color.ColorQFZSoille;
/*  13:    */ import vpt.algorithms.io.Load;
/*  14:    */ import vpt.util.Tools;
/*  15:    */ 
/*  16:    */ public class MeanShift
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 18 */   public Image output = null;
/*  20: 19 */   public Image input = null;
/*  21: 21 */   public Double band = null;
/*  22:    */   
/*  23:    */   public MeanShift()
/*  24:    */   {
/*  25: 24 */     this.inputFields = "input, band";
/*  26: 25 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 29 */     int xdim = this.input.getXDim();
/*  33: 30 */     int ydim = this.input.getYDim();
/*  34:    */     
/*  35: 32 */     int size = xdim * ydim;
/*  36:    */     
/*  37: 34 */     double bandwidth = this.band.doubleValue() * this.band.doubleValue();
/*  38:    */     
/*  39: 36 */     double esik = 0.001D * bandwidth;
/*  40:    */     
/*  41:    */ 
/*  42: 39 */     ArrayList pixels = new ArrayList();
/*  43:    */     
/*  44:    */ 
/*  45: 42 */     boolean[][][] flags = new boolean[256][256][256];
/*  46: 43 */     boolean[][][] flags2 = new boolean[256][256][256];
/*  47:    */     
/*  48:    */ 
/*  49: 46 */     int[][][] renkler = new int[256][256][256];
/*  50:    */     
/*  51:    */ 
/*  52: 49 */     ArrayList[] points = new ArrayList[size];
/*  53:    */     
/*  54: 51 */     int renkSyc = 1;
/*  55: 52 */     for (int x = 0; x < xdim; x++) {
/*  56: 53 */       for (int y = 0; y < ydim; y++)
/*  57:    */       {
/*  58: 54 */         double[] p = this.input.getVXYDouble(x, y);
/*  59: 58 */         if (renkler[((int)Math.round(p[0] * 255.0D))][((int)Math.round(p[1] * 255.0D))][((int)Math.round(p[2] * 255.0D))] == 0)
/*  60:    */         {
/*  61: 59 */           ArrayList d = new ArrayList();
/*  62: 60 */           d.add(new Point(x, y));
/*  63: 61 */           points[renkSyc] = d;
/*  64: 62 */           renkler[((int)Math.round(p[0] * 255.0D))][((int)Math.round(p[1] * 255.0D))][((int)Math.round(p[2] * 255.0D))] = renkSyc;
/*  65: 63 */           renkSyc++;
/*  66: 64 */           pixels.add(new VectorPixel(p, x, y));
/*  67:    */         }
/*  68:    */         else
/*  69:    */         {
/*  70: 66 */           int index = renkler[((int)Math.round(p[0] * 255.0D))][((int)Math.round(p[1] * 255.0D))][((int)Math.round(p[2] * 255.0D))];
/*  71: 67 */           points[index].add(new Point(x, y));
/*  72:    */         }
/*  73:    */       }
/*  74:    */     }
/*  75: 73 */     size = pixels.size();
/*  76: 74 */     System.err.println("Renk sayisi " + size);
/*  77:    */     
/*  78: 76 */     Vector clusters = new Vector();
/*  79: 77 */     Vector means = new Vector();
/*  80:    */     
/*  81: 79 */     int islenmisler = 0;
/*  82: 81 */     while (islenmisler < size)
/*  83:    */     {
/*  84: 84 */       int tmpIndex = -1;
/*  85: 87 */       for (int i = 0; i < 10; i++)
/*  86:    */       {
/*  87: 88 */         int tmp = (int)Math.floor(Math.random() * size);
/*  88:    */         
/*  89: 90 */         double[] coord = ((VectorPixel)pixels.get(tmp)).getVector();
/*  90: 93 */         if (flags[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] == 0)
/*  91:    */         {
/*  92: 96 */           tmpIndex = tmp;
/*  93: 97 */           break;
/*  94:    */         }
/*  95:    */       }
/*  96:102 */       if (tmpIndex == -1) {
/*  97:103 */         for (int i = 0; i < size; i++)
/*  98:    */         {
/*  99:104 */           double[] coord = ((VectorPixel)pixels.get(i)).getVector();
/* 100:107 */           if (flags[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] == 0)
/* 101:    */           {
/* 102:108 */             tmpIndex = i;
/* 103:109 */             break;
/* 104:    */           }
/* 105:    */         }
/* 106:    */       }
/* 107:115 */       if (tmpIndex == -1)
/* 108:    */       {
/* 109:116 */         System.err.println("Uyari : " + islenmisler + " " + size);
/* 110:117 */         break;
/* 111:    */       }
/* 112:120 */       double[] mean = ((VectorPixel)pixels.get(tmpIndex)).getVector();
/* 113:    */       
/* 114:122 */       Vector cluster = new Vector();
/* 115:    */       double[] eskiMean;
/* 116:    */       do
/* 117:    */       {
/* 118:126 */         eskiMean = mean;
/* 119:127 */         mean = new double[3];
/* 120:    */         
/* 121:129 */         int syc = 0;
/* 122:133 */         for (int i = 0; i < size; i++) {
/* 123:136 */           if (Tools.euclideanDistance(eskiMean, ((VectorPixel)pixels.get(i)).vector) < bandwidth)
/* 124:    */           {
/* 125:139 */             mean[0] += ((VectorPixel)pixels.get(i)).vector[0];
/* 126:140 */             mean[1] += ((VectorPixel)pixels.get(i)).vector[1];
/* 127:141 */             mean[2] += ((VectorPixel)pixels.get(i)).vector[2];
/* 128:    */             
/* 129:143 */             syc++;
/* 130:    */             
/* 131:    */ 
/* 132:146 */             double[] coord = ((VectorPixel)pixels.get(i)).getVector();
/* 133:149 */             if ((flags2[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] == 0) && 
/* 134:150 */               (flags[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] == 0))
/* 135:    */             {
/* 136:151 */               cluster.add(pixels.get(i));
/* 137:152 */               flags2[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] = 1;
/* 138:    */             }
/* 139:    */           }
/* 140:    */         }
/* 141:158 */         mean[0] /= syc;
/* 142:159 */         mean[1] /= syc;
/* 143:160 */         mean[2] /= syc;
/* 144:163 */       } while (Tools.euclideanDistance(mean, eskiMean) >= esik);
/* 145:167 */       int mergeWith = -1;
/* 146:168 */       int tmpSize = clusters.size();
/* 147:170 */       for (int i = 0; i < tmpSize; i++)
/* 148:    */       {
/* 149:171 */         double[] tmpMean = (double[])means.get(i);
/* 150:172 */         if (Tools.EuclideanNorm(vectorDifference(mean, tmpMean)) < this.band.doubleValue() / 2.0D)
/* 151:    */         {
/* 152:173 */           mergeWith = i;
/* 153:174 */           break;
/* 154:    */         }
/* 155:    */       }
/* 156:178 */       tmpSize = cluster.size();
/* 157:181 */       for (int i = 0; i < tmpSize; i++)
/* 158:    */       {
/* 159:182 */         VectorPixel vp = (VectorPixel)cluster.get(i);
/* 160:183 */         double[] coord = vp.getVector();
/* 161:184 */         flags2[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] = 0;
/* 162:    */       }
/* 163:188 */       if (mergeWith > -1)
/* 164:    */       {
/* 165:190 */         double[] mergedMean = (double[])means.get(mergeWith);
/* 166:    */         
/* 167:192 */         means.setElementAt(vectorAverage(mean, mergedMean), mergeWith);
/* 168:    */         
/* 169:    */ 
/* 170:    */ 
/* 171:196 */         tmpSize = cluster.size();
/* 172:    */         
/* 173:198 */         Vector mergedCluster = (Vector)clusters.get(mergeWith);
/* 174:200 */         for (int i = 0; i < tmpSize; i++)
/* 175:    */         {
/* 176:201 */           VectorPixel p = (VectorPixel)cluster.get(i);
/* 177:202 */           double[] coord = p.getVector();
/* 178:205 */           if (flags[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] == 0)
/* 179:    */           {
/* 180:206 */             mergedCluster.add(p);
/* 181:207 */             islenmisler++;
/* 182:    */           }
/* 183:    */         }
/* 184:    */       }
/* 185:    */       else
/* 186:    */       {
/* 187:212 */         means.add(mean);
/* 188:213 */         clusters.add(cluster);
/* 189:214 */         islenmisler += cluster.size();
/* 190:    */       }
/* 191:219 */       for (int i = 0; i < tmpSize; i++)
/* 192:    */       {
/* 193:220 */         VectorPixel vp = (VectorPixel)cluster.get(i);
/* 194:221 */         double[] coord = vp.getVector();
/* 195:222 */         flags[((int)Math.round(coord[0] * 255.0D))][((int)Math.round(coord[1] * 255.0D))][((int)Math.round(coord[2] * 255.0D))] = 1;
/* 196:    */       }
/* 197:231 */       System.err.println(islenmisler + " " + size + " percent classified : " + 100 * islenmisler / size + "% + cluster sayisi " + clusters.size());
/* 198:    */     }
/* 199:234 */     System.err.println("Siniflandirma tamamlandi sira goruntulemede..eskiden cok uzun suruyodu..simdiki gencler cok sansli peeeh");
/* 200:    */     
/* 201:    */ 
/* 202:    */ 
/* 203:238 */     this.output = new ByteImage(xdim, ydim, 3);
/* 204:239 */     size = clusters.size();
/* 205:    */     
/* 206:    */ 
/* 207:    */ 
/* 208:243 */     int syc2 = 0;
/* 209:244 */     for (int i = 0; i < size; i++)
/* 210:    */     {
/* 211:247 */       Vector cluster = (Vector)clusters.get(i);
/* 212:    */       
/* 213:    */ 
/* 214:250 */       double[] renk = (double[])means.get(i);
/* 215:253 */       for (int j = 0; j < cluster.size(); j++)
/* 216:    */       {
/* 217:254 */         VectorPixel p = (VectorPixel)cluster.get(j);
/* 218:    */         
/* 219:256 */         double[] d = p.vector;
/* 220:    */         
/* 221:    */ 
/* 222:259 */         int index = renkler[((int)Math.round(d[0] * 255.0D))][((int)Math.round(d[1] * 255.0D))][((int)Math.round(d[2] * 255.0D))];
/* 223:261 */         for (int k = 0; k < points[index].size(); k++)
/* 224:    */         {
/* 225:262 */           Point pixel = (Point)points[index].get(k);
/* 226:263 */           this.output.setVXYDouble(pixel.x, pixel.y, renk);
/* 227:264 */           syc2++;
/* 228:    */         }
/* 229:    */       }
/* 230:    */     }
/* 231:269 */     System.err.println("siniflandirilan piksel sayisi " + syc2);
/* 232:    */   }
/* 233:    */   
/* 234:    */   private double[] vectorDifference(double[] p1, double[] p2)
/* 235:    */   {
/* 236:273 */     if (p1.length != p2.length)
/* 237:    */     {
/* 238:274 */       System.err.println("Incompatible vector lengths");
/* 239:275 */       return null;
/* 240:    */     }
/* 241:278 */     double[] fark = new double[p1.length];
/* 242:280 */     for (int i = 0; i < p1.length; i++) {
/* 243:281 */       p1[i] -= p2[i];
/* 244:    */     }
/* 245:283 */     return fark;
/* 246:    */   }
/* 247:    */   
/* 248:    */   private double[] vectorAverage(double[] p1, double[] p2)
/* 249:    */   {
/* 250:287 */     if (p1.length != p2.length)
/* 251:    */     {
/* 252:288 */       System.err.println("Incompatible vector lengths");
/* 253:289 */       return null;
/* 254:    */     }
/* 255:292 */     double[] ort = new double[p1.length];
/* 256:294 */     for (int i = 0; i < p1.length; i++) {
/* 257:295 */       ort[i] = ((p1[i] + p2[i]) / 2.0D);
/* 258:    */     }
/* 259:297 */     return ort;
/* 260:    */   }
/* 261:    */   
/* 262:    */   private class VectorPixel
/* 263:    */   {
/* 264:    */     public double[] vector;
/* 265:    */     public int x;
/* 266:    */     public int y;
/* 267:    */     
/* 268:    */     VectorPixel(double[] vector, int x, int y)
/* 269:    */     {
/* 270:306 */       this.x = x;
/* 271:307 */       this.y = y;
/* 272:308 */       this.vector = vector;
/* 273:    */     }
/* 274:    */     
/* 275:    */     public double[] getVector()
/* 276:    */     {
/* 277:312 */       return (double[])this.vector.clone();
/* 278:    */     }
/* 279:    */     
/* 280:    */     public int getX()
/* 281:    */     {
/* 282:316 */       return this.x;
/* 283:    */     }
/* 284:    */     
/* 285:    */     public int getY()
/* 286:    */     {
/* 287:320 */       return this.y;
/* 288:    */     }
/* 289:    */   }
/* 290:    */   
/* 291:    */   public static void main(String[] args)
/* 292:    */   {
/* 293:327 */     Image img = Load.invoke("/media/data/arge/veri_yedegi/clef_2013/train/categorized/scan/8.jpg");
/* 294:328 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 295:    */     
/* 296:330 */     Image labels = ColorQFZSoille.invoke(img, 50, 50);
/* 297:331 */     img = LabelsToMeanValue.invoke(labels, img);
/* 298:332 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 299:    */     
/* 300:334 */     Image output = invoke(img, Double.valueOf(0.2D));
/* 301:335 */     Display2D.invoke(output, Boolean.valueOf(true));
/* 302:    */   }
/* 303:    */   
/* 304:    */   public static Image invoke(Image image, Double band)
/* 305:    */   {
/* 306:    */     try
/* 307:    */     {
/* 308:341 */       return (Image)new MeanShift().preprocess(new Object[] { image, band });
/* 309:    */     }
/* 310:    */     catch (GlobalException e)
/* 311:    */     {
/* 312:343 */       e.printStackTrace();
/* 313:    */     }
/* 314:344 */     return null;
/* 315:    */   }
/* 316:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.MeanShift
 * JD-Core Version:    0.7.0.1
 */
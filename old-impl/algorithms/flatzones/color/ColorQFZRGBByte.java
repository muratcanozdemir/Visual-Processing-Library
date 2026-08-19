/*   1:    */ package vpt.algorithms.flatzones.color;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Comparator;
/*   8:    */ import java.util.Stack;
/*   9:    */ import vpt.Algorithm;
/*  10:    */ import vpt.BooleanImage;
/*  11:    */ import vpt.ByteImage;
/*  12:    */ import vpt.GlobalException;
/*  13:    */ import vpt.Image;
/*  14:    */ import vpt.IntegerImage;
/*  15:    */ 
/*  16:    */ public class ColorQFZRGBByte
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public int[] alpha;
/*  22:    */   public int[] omega;
/*  23: 39 */   public Ordering vo = new Ordering(null);
/*  24: 41 */   private ArrayList<Point> list = new ArrayList();
/*  25: 42 */   private ArrayList<Point> list2 = new ArrayList();
/*  26: 43 */   private Stack<Point> s = new Stack();
/*  27:    */   private int xdim;
/*  28:    */   private int ydim;
/*  29: 46 */   private int label = 1;
/*  30: 48 */   private ColorVector[] cv = new ColorVector[16777216];
/*  31: 49 */   private int alphaIndex = -1;
/*  32: 50 */   private int omegaIndex = -1;
/*  33:    */   BooleanImage temp;
/*  34:    */   Image localRanges;
/*  35: 62 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  36: 63 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  37:    */   
/*  38:    */   public ColorQFZRGBByte()
/*  39:    */   {
/*  40: 66 */     this.inputFields = "input,alpha,omega";
/*  41: 67 */     this.outputFields = "output";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void execute()
/*  45:    */     throws GlobalException
/*  46:    */   {
/*  47: 72 */     this.xdim = this.input.getXDim();
/*  48: 73 */     this.ydim = this.input.getYDim();
/*  49: 75 */     if (this.input.getCDim() != 3) {
/*  50: 75 */       throw new GlobalException("This implementation is only for color images.");
/*  51:    */     }
/*  52: 77 */     if ((this.alpha[0] < 0) || (this.alpha[1] < 0) || (this.alpha[2] < 0) || (this.alpha[0] > 254) || (this.alpha[1] > 254) || (this.alpha[2] > 254) || 
/*  53: 78 */       (this.omega[0] < 0) || (this.omega[1] < 0) || (this.omega[2] < 0) || (this.omega[0] > 254) || (this.omega[1] > 254) || (this.omega[2] > 254)) {
/*  54: 79 */       throw new GlobalException("Arguments out of range.");
/*  55:    */     }
/*  56: 81 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  57: 82 */     this.output.fill(0);
/*  58:    */     
/*  59: 84 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  60: 85 */     this.temp.fill(false);
/*  61:    */     
/*  62: 87 */     this.localRanges = new ByteImage(this.xdim, this.ydim, 3);
/*  63: 88 */     this.localRanges.fill(255);
/*  64:    */     
/*  65: 90 */     initColorVectors();
/*  66: 92 */     if (this.alphaIndex == -1) {
/*  67: 92 */       System.err.println("What the hell, where is the alpha index?");
/*  68:    */     } else {
/*  69: 93 */       System.err.println("Rank found at " + this.alphaIndex);
/*  70:    */     }
/*  71: 95 */     if (this.omegaIndex == -1) {
/*  72: 95 */       System.err.println("What the hell, where is the omega index?");
/*  73:    */     } else {
/*  74: 96 */       System.err.println("Rank found at " + this.alphaIndex);
/*  75:    */     }
/*  76: 98 */     for (int y = 0; y < this.ydim; y++)
/*  77:    */     {
/*  78:101 */       System.out.println("Satir " + y);
/*  79:103 */       for (int x = 0; x < this.xdim; x++) {
/*  80:108 */         if (this.output.getXYInt(x, y) <= 0)
/*  81:    */         {
/*  82:110 */           Point t = new Point(x, y);
/*  83:112 */           if (createQCC(t, this.alpha) > 0)
/*  84:    */           {
/*  85:114 */             for (Point s : this.list)
/*  86:    */             {
/*  87:115 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  88:115 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  89:    */               }
/*  90:116 */               this.output.setXYInt(s.x, s.y, this.label);
/*  91:    */             }
/*  92:120 */             for (Point s : this.list) {
/*  93:121 */               this.localRanges.setVXYByte(s.x, s.y, this.alpha);
/*  94:    */             }
/*  95:    */           }
/*  96:    */           else
/*  97:    */           {
/*  98:124 */             int a_1 = 0;
/*  99:125 */             int a_2 = this.alphaIndex;
/* 100:    */             for (;;)
/* 101:    */             {
/* 102:128 */               this.list.clear();
/* 103:    */               
/* 104:130 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 105:132 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 106:    */               {
/* 107:133 */                 a_1 = tmpAlpha;
/* 108:137 */                 if (a_1 + 1 == a_2)
/* 109:    */                 {
/* 110:139 */                   for (Point s : this.list)
/* 111:    */                   {
/* 112:140 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 113:141 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 114:    */                     }
/* 115:142 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 116:    */                   }
/* 117:146 */                   for (Point s : this.list) {
/* 118:147 */                     this.localRanges.setVXYByte(s.x, s.y, this.cv[tmpAlpha].z);
/* 119:    */                   }
/* 120:149 */                   break;
/* 121:    */                 }
/* 122:    */               }
/* 123:    */               else
/* 124:    */               {
/* 125:152 */                 a_2 = tmpAlpha;
/* 126:    */               }
/* 127:    */             }
/* 128:    */           }
/* 129:159 */           this.list.clear();
/* 130:    */           
/* 131:161 */           this.label += 1;
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:165 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 136:    */   }
/* 137:    */   
/* 138:    */   private int createQCC(Point r, int[] alpha2)
/* 139:    */   {
/* 140:177 */     int[] max = new int[3];
/* 141:178 */     int[] min = { 255, 255, 255 };
/* 142:179 */     int rankA = rankOf(alpha2);
/* 143:182 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 144:182 */       System.err.println("What the hell is going on?");
/* 145:    */     }
/* 146:184 */     this.s.clear();
/* 147:185 */     this.s.add(r);
/* 148:186 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 149:187 */     this.list2.add(r);
/* 150:    */     int x;
/* 151:    */     int i;
/* 152:189 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 153:    */     {
/* 154:191 */       Point tmp = (Point)this.s.pop();
/* 155:192 */       x = tmp.x;
/* 156:193 */       int y = tmp.y;
/* 157:    */       
/* 158:195 */       int[] p = this.input.getVXYByte(x, y);
/* 159:198 */       if (this.vo.compare(max, p) < 0) {
/* 160:198 */         max = p;
/* 161:    */       }
/* 162:199 */       if (this.vo.compare(min, p) > 0) {
/* 163:199 */         min = p;
/* 164:    */       }
/* 165:201 */       int rankDiff = rankOf(max) - rankOf(min);
/* 166:202 */       int rankP = rankOf(p);
/* 167:204 */       if (rankDiff > this.omegaIndex)
/* 168:    */       {
/* 169:206 */         for (Point t : this.list2) {
/* 170:207 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 171:    */         }
/* 172:208 */         this.list2.clear();
/* 173:    */         
/* 174:210 */         return -1;
/* 175:    */       }
/* 176:213 */       this.list.add(tmp);
/* 177:    */       
/* 178:215 */       i = 0; continue;
/* 179:216 */       int _x = x + this.N[i].x;
/* 180:217 */       int _y = y + this.N[i].y;
/* 181:219 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 182:221 */         if (!this.temp.getXYBoolean(_x, _y))
/* 183:    */         {
/* 184:223 */           int[] q = this.input.getVXYByte(_x, _y);
/* 185:    */           
/* 186:225 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 187:227 */           if (this.output.getXYInt(_x, _y) > 0)
/* 188:    */           {
/* 189:229 */             int[] localR = this.localRanges.getVXYByte(_x, _y);
/* 190:231 */             if ((rankDiff <= rankA) && (this.vo.compare(localR, alpha2) <= 0))
/* 191:    */             {
/* 192:233 */               for (Point t : this.list2) {
/* 193:234 */                 this.temp.setXYBoolean(t.x, t.y, false);
/* 194:    */               }
/* 195:235 */               this.list2.clear();
/* 196:    */               
/* 197:237 */               return -1;
/* 198:    */             }
/* 199:    */           }
/* 200:243 */           else if (rankDiff <= rankA)
/* 201:    */           {
/* 202:245 */             Point t = new Point(_x, _y);
/* 203:246 */             this.s.add(t);
/* 204:    */             
/* 205:248 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 206:249 */             this.list2.add(t);
/* 207:    */           }
/* 208:    */         }
/* 209:    */       }
/* 210:215 */       i++;
/* 211:    */     }
/* 212:254 */     for (Point t : this.list2) {
/* 213:255 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 214:    */     }
/* 215:256 */     this.list2.clear();
/* 216:    */     
/* 217:258 */     return 1;
/* 218:    */   }
/* 219:    */   
/* 220:    */   private void initColorVectors()
/* 221:    */   {
/* 222:263 */     int j = 0;
/* 223:264 */     for (int r = 0; r < 256; r++) {
/* 224:265 */       for (int g = 0; g < 256; g++) {
/* 225:266 */         for (int b = 0; b < 256; b++) {
/* 226:267 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 227:    */         }
/* 228:    */       }
/* 229:    */     }
/* 230:271 */     System.err.println("Sorting colors");
/* 231:    */     
/* 232:273 */     Arrays.sort(this.cv);
/* 233:    */     
/* 234:275 */     System.err.println("Colors sorted");
/* 235:    */     
/* 236:277 */     this.alphaIndex = rankOf(this.alpha);
/* 237:278 */     this.omegaIndex = rankOf(this.omega);
/* 238:    */   }
/* 239:    */   
/* 240:    */   private int rankOf(int[] a)
/* 241:    */   {
/* 242:282 */     int first = 0;
/* 243:283 */     int last = this.cv.length - 1;
/* 244:284 */     int mid = 0;
/* 245:    */     
/* 246:286 */     boolean found = false;
/* 247:288 */     while ((!found) && (first <= last))
/* 248:    */     {
/* 249:289 */       mid = first + (last - first) / 2;
/* 250:290 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 251:290 */         found = true;
/* 252:291 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 253:291 */         last = mid - 1;
/* 254:292 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 255:292 */         first = mid + 1;
/* 256:    */       }
/* 257:    */     }
/* 258:295 */     return mid;
/* 259:    */   }
/* 260:    */   
/* 261:    */   private class Ordering
/* 262:    */     implements Comparator<int[]>
/* 263:    */   {
/* 264:    */     private Ordering() {}
/* 265:    */     
/* 266:    */     public int compare(int[] v1, int[] v2)
/* 267:    */     {
/* 268:333 */       if (v1[0] > v2[0]) {
/* 269:333 */         return 1;
/* 270:    */       }
/* 271:334 */       if (v1[0] < v2[0]) {
/* 272:334 */         return -1;
/* 273:    */       }
/* 274:335 */       if (v1[1] > v2[1]) {
/* 275:335 */         return 1;
/* 276:    */       }
/* 277:336 */       if (v1[1] < v2[1]) {
/* 278:336 */         return -1;
/* 279:    */       }
/* 280:337 */       if (v1[2] > v2[2]) {
/* 281:337 */         return 1;
/* 282:    */       }
/* 283:338 */       if (v1[2] < v2[2]) {
/* 284:338 */         return -1;
/* 285:    */       }
/* 286:339 */       return 0;
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   private class ColorVector
/* 291:    */     implements Comparable<ColorVector>
/* 292:    */   {
/* 293:    */     int[] z;
/* 294:    */     
/* 295:    */     ColorVector(int a, int b, int c)
/* 296:    */     {
/* 297:348 */       this.z = new int[3];
/* 298:349 */       this.z[0] = a;
/* 299:350 */       this.z[1] = b;
/* 300:351 */       this.z[2] = c;
/* 301:    */     }
/* 302:    */     
/* 303:    */     public int compareTo(ColorVector c)
/* 304:    */     {
/* 305:355 */       return ColorQFZRGBByte.this.vo.compare(this.z, c.z);
/* 306:    */     }
/* 307:    */   }
/* 308:    */   
/* 309:    */   public static Image invoke(Image img, int[] alpha, int[] omega)
/* 310:    */   {
/* 311:    */     try
/* 312:    */     {
/* 313:362 */       return (IntegerImage)new ColorQFZRGBByte().preprocess(new Object[] { img, alpha, omega });
/* 314:    */     }
/* 315:    */     catch (GlobalException e)
/* 316:    */     {
/* 317:364 */       e.printStackTrace();
/* 318:    */     }
/* 319:365 */     return null;
/* 320:    */   }
/* 321:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZRGBByte
 * JD-Core Version:    0.7.0.1
 */
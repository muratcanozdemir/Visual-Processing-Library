/*   1:    */ package vpt.algorithms.experimental.fz;
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
/*  15:    */ import vpt.util.Tools;
/*  16:    */ 
/*  17:    */ public class ColorQFZRGBByte2
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20:    */   public Image input;
/*  21:    */   public IntegerImage output;
/*  22:    */   public int[] alpha;
/*  23:    */   public int[] omega;
/*  24: 33 */   public Ordering vo = new Ordering(null);
/*  25: 35 */   private ArrayList<Point> list = new ArrayList();
/*  26: 36 */   private ArrayList<Point> list2 = new ArrayList();
/*  27: 37 */   private Stack<Point> s = new Stack();
/*  28:    */   private int xdim;
/*  29:    */   private int ydim;
/*  30: 40 */   private int label = 1;
/*  31: 42 */   private ColorVector[] cv = new ColorVector[16777216];
/*  32: 43 */   private int alphaIndex = -1;
/*  33: 44 */   private int omegaIndex = -1;
/*  34:    */   BooleanImage temp;
/*  35:    */   Image localRanges;
/*  36: 56 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  37: 57 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  38:    */   
/*  39:    */   public ColorQFZRGBByte2()
/*  40:    */   {
/*  41: 60 */     this.inputFields = "input,alpha,omega";
/*  42: 61 */     this.outputFields = "output";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void execute()
/*  46:    */     throws GlobalException
/*  47:    */   {
/*  48: 66 */     this.xdim = this.input.getXDim();
/*  49: 67 */     this.ydim = this.input.getYDim();
/*  50: 69 */     if (this.input.getCDim() != 3) {
/*  51: 69 */       throw new GlobalException("This implementation is only for color images.");
/*  52:    */     }
/*  53: 71 */     if ((this.alpha[0] < 0) || (this.alpha[1] < 0) || (this.alpha[2] < 0) || (this.alpha[0] > 254) || (this.alpha[1] > 254) || (this.alpha[2] > 254) || 
/*  54: 72 */       (this.omega[0] < 0) || (this.omega[1] < 0) || (this.omega[2] < 0) || (this.omega[0] > 254) || (this.omega[1] > 254) || (this.omega[2] > 254)) {
/*  55: 73 */       throw new GlobalException("Arguments out of range.");
/*  56:    */     }
/*  57: 75 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  58: 76 */     this.output.fill(0);
/*  59:    */     
/*  60: 78 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  61: 79 */     this.temp.fill(false);
/*  62:    */     
/*  63: 81 */     this.localRanges = new ByteImage(this.xdim, this.ydim, 3);
/*  64: 82 */     this.localRanges.fill(255);
/*  65:    */     
/*  66: 84 */     initColorVectors();
/*  67: 86 */     if (this.alphaIndex == -1) {
/*  68: 86 */       System.err.println("What the hell, where is the alpha index?");
/*  69:    */     } else {
/*  70: 87 */       System.err.println("Rank found at " + this.alphaIndex);
/*  71:    */     }
/*  72: 89 */     if (this.omegaIndex == -1) {
/*  73: 89 */       System.err.println("What the hell, where is the omega index?");
/*  74:    */     } else {
/*  75: 90 */       System.err.println("Rank found at " + this.alphaIndex);
/*  76:    */     }
/*  77: 92 */     for (int y = 0; y < this.ydim; y++)
/*  78:    */     {
/*  79: 95 */       System.out.println("Satir " + y);
/*  80: 97 */       for (int x = 0; x < this.xdim; x++) {
/*  81:102 */         if (this.output.getXYInt(x, y) <= 0)
/*  82:    */         {
/*  83:104 */           Point t = new Point(x, y);
/*  84:106 */           if (createQCC(t, this.alpha) > 0)
/*  85:    */           {
/*  86:108 */             for (Point s : this.list)
/*  87:    */             {
/*  88:109 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  89:109 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  90:    */               }
/*  91:110 */               this.output.setXYInt(s.x, s.y, this.label);
/*  92:    */             }
/*  93:114 */             for (Point s : this.list) {
/*  94:115 */               this.localRanges.setVXYByte(s.x, s.y, this.alpha);
/*  95:    */             }
/*  96:    */           }
/*  97:    */           else
/*  98:    */           {
/*  99:118 */             int a_1 = 0;
/* 100:119 */             int a_2 = this.alphaIndex;
/* 101:    */             for (;;)
/* 102:    */             {
/* 103:122 */               this.list.clear();
/* 104:    */               
/* 105:124 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 106:126 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 107:    */               {
/* 108:127 */                 a_1 = tmpAlpha;
/* 109:131 */                 if (a_1 + 1 == a_2)
/* 110:    */                 {
/* 111:133 */                   for (Point s : this.list)
/* 112:    */                   {
/* 113:134 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 114:135 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 115:    */                     }
/* 116:136 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 117:    */                   }
/* 118:140 */                   for (Point s : this.list) {
/* 119:141 */                     this.localRanges.setVXYByte(s.x, s.y, this.cv[tmpAlpha].z);
/* 120:    */                   }
/* 121:143 */                   break;
/* 122:    */                 }
/* 123:    */               }
/* 124:    */               else
/* 125:    */               {
/* 126:146 */                 a_2 = tmpAlpha;
/* 127:    */               }
/* 128:    */             }
/* 129:    */           }
/* 130:153 */           this.list.clear();
/* 131:    */           
/* 132:155 */           this.label += 1;
/* 133:    */         }
/* 134:    */       }
/* 135:    */     }
/* 136:159 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 137:    */   }
/* 138:    */   
/* 139:    */   private int createQCC(Point r, int[] alpha2)
/* 140:    */   {
/* 141:171 */     int[] max = new int[3];
/* 142:172 */     int[] min = { 255, 255, 255 };
/* 143:173 */     int[] diff = new int[3];
/* 144:176 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 145:176 */       System.err.println("What the hell is going on?");
/* 146:    */     }
/* 147:178 */     this.s.clear();
/* 148:179 */     this.s.add(r);
/* 149:180 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 150:181 */     this.list2.add(r);
/* 151:    */     int x;
/* 152:    */     int i;
/* 153:183 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 154:    */     {
/* 155:185 */       Point tmp = (Point)this.s.pop();
/* 156:186 */       x = tmp.x;
/* 157:187 */       int y = tmp.y;
/* 158:    */       
/* 159:189 */       int[] p = this.input.getVXYByte(x, y);
/* 160:    */       
/* 161:191 */       max[0] = (max[0] < p[0] ? p[0] : max[0]);
/* 162:192 */       max[1] = (max[1] < p[1] ? p[1] : max[1]);
/* 163:193 */       max[2] = (max[2] < p[2] ? p[2] : max[2]);
/* 164:    */       
/* 165:195 */       min[0] = (min[0] > p[0] ? p[0] : min[0]);
/* 166:196 */       min[1] = (min[1] > p[1] ? p[1] : min[1]);
/* 167:197 */       min[2] = (min[2] > p[2] ? p[2] : min[2]);
/* 168:    */       
/* 169:199 */       diff[0] = Math.abs(max[0] - min[0]);
/* 170:200 */       diff[1] = Math.abs(max[1] - min[1]);
/* 171:201 */       diff[2] = Math.abs(max[2] - min[2]);
/* 172:203 */       if ((this.omega[0] < diff[0]) || (this.omega[1] < diff[1]) || (this.omega[2] < diff[2]))
/* 173:    */       {
/* 174:205 */         for (Point t : this.list2) {
/* 175:206 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 176:    */         }
/* 177:207 */         this.list2.clear();
/* 178:    */         
/* 179:209 */         return -1;
/* 180:    */       }
/* 181:212 */       this.list.add(tmp);
/* 182:    */       
/* 183:214 */       i = 0; continue;
/* 184:215 */       int _x = x + this.N[i].x;
/* 185:216 */       int _y = y + this.N[i].y;
/* 186:218 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 187:220 */         if (!this.temp.getXYBoolean(_x, _y))
/* 188:    */         {
/* 189:222 */           int[] q = this.input.getVXYByte(_x, _y);
/* 190:    */           
/* 191:224 */           diff[0] = Math.abs(p[0] - q[0]);
/* 192:225 */           diff[1] = Math.abs(p[1] - q[1]);
/* 193:226 */           diff[2] = Math.abs(p[2] - q[2]);
/* 194:228 */           if (this.output.getXYInt(_x, _y) > 0)
/* 195:    */           {
/* 196:230 */             int[] localR = this.localRanges.getVXYByte(_x, _y);
/* 197:232 */             if ((diff[0] <= alpha2[0]) && (diff[1] <= alpha2[1]) && (diff[2] <= alpha2[2]) && (this.vo.compare(localR, alpha2) <= 0))
/* 198:    */             {
/* 199:234 */               for (Point t : this.list2) {
/* 200:235 */                 this.temp.setXYBoolean(t.x, t.y, false);
/* 201:    */               }
/* 202:236 */               this.list2.clear();
/* 203:    */               
/* 204:238 */               return -1;
/* 205:    */             }
/* 206:    */           }
/* 207:244 */           else if ((diff[0] <= alpha2[0]) && (diff[1] <= alpha2[1]) && (diff[2] <= alpha2[2]))
/* 208:    */           {
/* 209:246 */             Point t = new Point(_x, _y);
/* 210:247 */             this.s.add(t);
/* 211:    */             
/* 212:249 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 213:250 */             this.list2.add(t);
/* 214:    */           }
/* 215:    */         }
/* 216:    */       }
/* 217:214 */       i++;
/* 218:    */     }
/* 219:255 */     for (Point t : this.list2) {
/* 220:256 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 221:    */     }
/* 222:257 */     this.list2.clear();
/* 223:    */     
/* 224:259 */     return 1;
/* 225:    */   }
/* 226:    */   
/* 227:    */   private void initColorVectors()
/* 228:    */   {
/* 229:264 */     int j = 0;
/* 230:265 */     for (int r = 0; r < 256; r++) {
/* 231:266 */       for (int g = 0; g < 256; g++) {
/* 232:267 */         for (int b = 0; b < 256; b++) {
/* 233:268 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 234:    */         }
/* 235:    */       }
/* 236:    */     }
/* 237:272 */     System.err.println("Sorting colors");
/* 238:    */     
/* 239:274 */     Arrays.sort(this.cv);
/* 240:    */     
/* 241:276 */     System.err.println("Colors sorted");
/* 242:    */     
/* 243:278 */     this.alphaIndex = rankOf(this.alpha);
/* 244:279 */     this.omegaIndex = rankOf(this.omega);
/* 245:    */   }
/* 246:    */   
/* 247:    */   private int rankOf(int[] a)
/* 248:    */   {
/* 249:283 */     int first = 0;
/* 250:284 */     int last = this.cv.length - 1;
/* 251:285 */     int mid = 0;
/* 252:    */     
/* 253:287 */     boolean found = false;
/* 254:289 */     while ((!found) && (first <= last))
/* 255:    */     {
/* 256:290 */       mid = first + (last - first) / 2;
/* 257:291 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 258:291 */         found = true;
/* 259:292 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 260:292 */         last = mid - 1;
/* 261:293 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 262:293 */         first = mid + 1;
/* 263:    */       }
/* 264:    */     }
/* 265:296 */     return mid;
/* 266:    */   }
/* 267:    */   
/* 268:    */   private class Ordering
/* 269:    */     implements Comparator<int[]>
/* 270:    */   {
/* 271:    */     private Ordering() {}
/* 272:    */     
/* 273:    */     public int compare(int[] v1, int[] v2)
/* 274:    */     {
/* 275:305 */       double norm1 = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);
/* 276:306 */       double norm2 = Math.sqrt(v2[0] * v2[0] + v2[1] * v2[1] + v2[2] * v2[2]);
/* 277:308 */       if (Tools.cmpr(norm1, norm2) < 0) {
/* 278:308 */         return -1;
/* 279:    */       }
/* 280:309 */       if (Tools.cmpr(norm1, norm2) > 0) {
/* 281:309 */         return 1;
/* 282:    */       }
/* 283:310 */       if (v1[0] > v2[0]) {
/* 284:310 */         return 1;
/* 285:    */       }
/* 286:311 */       if (v1[0] < v2[0]) {
/* 287:311 */         return -1;
/* 288:    */       }
/* 289:312 */       if (v1[1] > v2[1]) {
/* 290:312 */         return 1;
/* 291:    */       }
/* 292:313 */       if (v1[1] < v2[1]) {
/* 293:313 */         return -1;
/* 294:    */       }
/* 295:314 */       if (v1[2] > v2[2]) {
/* 296:314 */         return 1;
/* 297:    */       }
/* 298:315 */       if (v1[2] < v2[2]) {
/* 299:315 */         return -1;
/* 300:    */       }
/* 301:316 */       return 0;
/* 302:    */     }
/* 303:    */   }
/* 304:    */   
/* 305:    */   private class ColorVector
/* 306:    */     implements Comparable<ColorVector>
/* 307:    */   {
/* 308:    */     int[] z;
/* 309:    */     
/* 310:    */     ColorVector(int a, int b, int c)
/* 311:    */     {
/* 312:325 */       this.z = new int[3];
/* 313:326 */       this.z[0] = a;
/* 314:327 */       this.z[1] = b;
/* 315:328 */       this.z[2] = c;
/* 316:    */     }
/* 317:    */     
/* 318:    */     public int compareTo(ColorVector c)
/* 319:    */     {
/* 320:332 */       return ColorQFZRGBByte2.this.vo.compare(this.z, c.z);
/* 321:    */     }
/* 322:    */   }
/* 323:    */   
/* 324:    */   public static Image invoke(Image img, int[] alpha, int[] omega)
/* 325:    */   {
/* 326:    */     try
/* 327:    */     {
/* 328:339 */       return (IntegerImage)new ColorQFZRGBByte2().preprocess(new Object[] { img, alpha, omega });
/* 329:    */     }
/* 330:    */     catch (GlobalException e)
/* 331:    */     {
/* 332:341 */       e.printStackTrace();
/* 333:    */     }
/* 334:342 */     return null;
/* 335:    */   }
/* 336:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorQFZRGBByte2
 * JD-Core Version:    0.7.0.1
 */
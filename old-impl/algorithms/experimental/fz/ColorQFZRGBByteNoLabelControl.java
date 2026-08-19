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
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ import vpt.util.Tools;
/*  15:    */ 
/*  16:    */ public class ColorQFZRGBByteNoLabelControl
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public int[] alpha;
/*  22:    */   public int[] omega;
/*  23: 42 */   public Ordering vo = new Ordering(null);
/*  24: 44 */   private ArrayList<Point> list = new ArrayList();
/*  25: 45 */   private ArrayList<Point> list2 = new ArrayList();
/*  26: 46 */   private Stack<Point> s = new Stack();
/*  27:    */   private int xdim;
/*  28:    */   private int ydim;
/*  29: 49 */   private int label = 1;
/*  30: 51 */   private ColorVector[] cv = new ColorVector[16777216];
/*  31: 52 */   private int alphaIndex = -1;
/*  32: 53 */   private int omegaIndex = -1;
/*  33:    */   BooleanImage temp;
/*  34: 63 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  35: 64 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  36:    */   
/*  37:    */   public ColorQFZRGBByteNoLabelControl()
/*  38:    */   {
/*  39: 67 */     this.inputFields = "input,alpha,omega";
/*  40: 68 */     this.outputFields = "output";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void execute()
/*  44:    */     throws GlobalException
/*  45:    */   {
/*  46: 73 */     this.xdim = this.input.getXDim();
/*  47: 74 */     this.ydim = this.input.getYDim();
/*  48: 76 */     if (this.input.getCDim() != 3) {
/*  49: 76 */       throw new GlobalException("This implementation is only for color images.");
/*  50:    */     }
/*  51: 78 */     if ((this.alpha[0] < 0) || (this.alpha[1] < 0) || (this.alpha[2] < 0) || (this.alpha[0] > 254) || (this.alpha[1] > 254) || (this.alpha[2] > 254) || 
/*  52: 79 */       (this.omega[0] < 0) || (this.omega[1] < 0) || (this.omega[2] < 0) || (this.omega[0] > 254) || (this.omega[1] > 254) || (this.omega[2] > 254)) {
/*  53: 80 */       throw new GlobalException("Arguments out of range.");
/*  54:    */     }
/*  55: 82 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  56: 83 */     this.output.fill(0);
/*  57:    */     
/*  58: 85 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  59: 86 */     this.temp.fill(false);
/*  60:    */     
/*  61: 88 */     initColorVectors();
/*  62: 90 */     if (this.alphaIndex == -1) {
/*  63: 90 */       System.err.println("What the hell, where is the alpha index?");
/*  64:    */     } else {
/*  65: 91 */       System.err.println("Rank found at " + this.alphaIndex);
/*  66:    */     }
/*  67: 93 */     if (this.omegaIndex == -1) {
/*  68: 93 */       System.err.println("What the hell, where is the omega index?");
/*  69:    */     } else {
/*  70: 94 */       System.err.println("Rank found at " + this.alphaIndex);
/*  71:    */     }
/*  72: 96 */     for (int y = 0; y < this.ydim; y++)
/*  73:    */     {
/*  74: 99 */       System.out.println("Satir " + y);
/*  75:101 */       for (int x = 0; x < this.xdim; x++) {
/*  76:106 */         if (this.output.getXYInt(x, y) <= 0)
/*  77:    */         {
/*  78:108 */           Point t = new Point(x, y);
/*  79:110 */           if (createQCC(t, this.alpha) > 0)
/*  80:    */           {
/*  81:112 */             for (Point s : this.list)
/*  82:    */             {
/*  83:113 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  84:113 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  85:    */               }
/*  86:114 */               this.output.setXYInt(s.x, s.y, this.label);
/*  87:    */             }
/*  88:    */           }
/*  89:    */           else
/*  90:    */           {
/*  91:117 */             int a_1 = 0;
/*  92:118 */             int a_2 = this.alphaIndex;
/*  93:    */             for (;;)
/*  94:    */             {
/*  95:121 */               this.list.clear();
/*  96:    */               
/*  97:123 */               int tmpAlpha = (a_1 + a_2) / 2;
/*  98:125 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/*  99:    */               {
/* 100:126 */                 a_1 = tmpAlpha;
/* 101:130 */                 if (a_1 + 1 == a_2)
/* 102:    */                 {
/* 103:132 */                   for (Point s : this.list)
/* 104:    */                   {
/* 105:133 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 106:134 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 107:    */                     }
/* 108:135 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 109:    */                   }
/* 110:138 */                   break;
/* 111:    */                 }
/* 112:    */               }
/* 113:    */               else
/* 114:    */               {
/* 115:141 */                 a_2 = tmpAlpha;
/* 116:    */               }
/* 117:    */             }
/* 118:    */           }
/* 119:148 */           this.list.clear();
/* 120:    */           
/* 121:150 */           this.label += 1;
/* 122:    */         }
/* 123:    */       }
/* 124:    */     }
/* 125:154 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 126:    */   }
/* 127:    */   
/* 128:    */   private int createQCC(Point r, int[] alpha2)
/* 129:    */   {
/* 130:166 */     int[] max = new int[3];
/* 131:167 */     int[] min = { 255, 255, 255 };
/* 132:168 */     int rankA = rankOf(alpha2);
/* 133:171 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 134:171 */       System.err.println("What the hell is going on?");
/* 135:    */     }
/* 136:173 */     this.s.clear();
/* 137:174 */     this.s.add(r);
/* 138:175 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 139:176 */     this.list2.add(r);
/* 140:    */     int x;
/* 141:    */     int i;
/* 142:178 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 143:    */     {
/* 144:180 */       Point tmp = (Point)this.s.pop();
/* 145:181 */       x = tmp.x;
/* 146:182 */       int y = tmp.y;
/* 147:    */       
/* 148:184 */       int[] p = this.input.getVXYByte(x, y);
/* 149:187 */       if (this.vo.compare(max, p) < 0) {
/* 150:187 */         max = p;
/* 151:    */       }
/* 152:188 */       if (this.vo.compare(min, p) > 0) {
/* 153:188 */         min = p;
/* 154:    */       }
/* 155:190 */       int rankDiff = rankOf(max) - rankOf(min);
/* 156:191 */       int rankP = rankOf(p);
/* 157:193 */       if (rankDiff > this.omegaIndex)
/* 158:    */       {
/* 159:195 */         for (Point t : this.list2) {
/* 160:196 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 161:    */         }
/* 162:197 */         this.list2.clear();
/* 163:    */         
/* 164:199 */         return -1;
/* 165:    */       }
/* 166:202 */       this.list.add(tmp);
/* 167:    */       
/* 168:204 */       i = 0; continue;
/* 169:205 */       int _x = x + this.N[i].x;
/* 170:206 */       int _y = y + this.N[i].y;
/* 171:208 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 172:210 */         if (!this.temp.getXYBoolean(_x, _y))
/* 173:    */         {
/* 174:212 */           int[] q = this.input.getVXYByte(_x, _y);
/* 175:    */           
/* 176:214 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 177:216 */           if (rankDiff <= rankA)
/* 178:    */           {
/* 179:218 */             Point t = new Point(_x, _y);
/* 180:219 */             this.s.add(t);
/* 181:    */             
/* 182:221 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 183:222 */             this.list2.add(t);
/* 184:    */           }
/* 185:    */         }
/* 186:    */       }
/* 187:204 */       i++;
/* 188:    */     }
/* 189:227 */     for (Point t : this.list2) {
/* 190:228 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 191:    */     }
/* 192:229 */     this.list2.clear();
/* 193:    */     
/* 194:231 */     return 1;
/* 195:    */   }
/* 196:    */   
/* 197:    */   private void initColorVectors()
/* 198:    */   {
/* 199:236 */     int j = 0;
/* 200:237 */     for (int r = 0; r < 256; r++) {
/* 201:238 */       for (int g = 0; g < 256; g++) {
/* 202:239 */         for (int b = 0; b < 256; b++) {
/* 203:240 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 204:    */         }
/* 205:    */       }
/* 206:    */     }
/* 207:244 */     System.err.println("Sorting colors");
/* 208:    */     
/* 209:246 */     Arrays.sort(this.cv);
/* 210:    */     
/* 211:248 */     System.err.println("Colors sorted");
/* 212:    */     
/* 213:250 */     this.alphaIndex = rankOf(this.alpha);
/* 214:251 */     this.omegaIndex = rankOf(this.omega);
/* 215:    */   }
/* 216:    */   
/* 217:    */   private int rankOf(int[] a)
/* 218:    */   {
/* 219:255 */     int first = 0;
/* 220:256 */     int last = this.cv.length - 1;
/* 221:257 */     int mid = 0;
/* 222:    */     
/* 223:259 */     boolean found = false;
/* 224:261 */     while ((!found) && (first <= last))
/* 225:    */     {
/* 226:262 */       mid = first + (last - first) / 2;
/* 227:263 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 228:263 */         found = true;
/* 229:264 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 230:264 */         last = mid - 1;
/* 231:265 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 232:265 */         first = mid + 1;
/* 233:    */       }
/* 234:    */     }
/* 235:268 */     return mid;
/* 236:    */   }
/* 237:    */   
/* 238:    */   private class Ordering
/* 239:    */     implements Comparator<int[]>
/* 240:    */   {
/* 241:    */     private Ordering() {}
/* 242:    */     
/* 243:    */     public int compare(int[] v1, int[] v2)
/* 244:    */     {
/* 245:277 */       double norm1 = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);
/* 246:278 */       double norm2 = Math.sqrt(v2[0] * v2[0] + v2[1] * v2[1] + v2[2] * v2[2]);
/* 247:304 */       if (Tools.cmpr(norm1, norm2) < 0) {
/* 248:304 */         return -1;
/* 249:    */       }
/* 250:305 */       if (Tools.cmpr(norm1, norm2) > 0) {
/* 251:305 */         return 1;
/* 252:    */       }
/* 253:306 */       if (v1[0] > v2[0]) {
/* 254:306 */         return 1;
/* 255:    */       }
/* 256:307 */       if (v1[0] < v2[0]) {
/* 257:307 */         return -1;
/* 258:    */       }
/* 259:308 */       if (v1[1] > v2[1]) {
/* 260:308 */         return 1;
/* 261:    */       }
/* 262:309 */       if (v1[1] < v2[1]) {
/* 263:309 */         return -1;
/* 264:    */       }
/* 265:310 */       if (v1[2] > v2[2]) {
/* 266:310 */         return 1;
/* 267:    */       }
/* 268:311 */       if (v1[2] < v2[2]) {
/* 269:311 */         return -1;
/* 270:    */       }
/* 271:312 */       return 0;
/* 272:    */     }
/* 273:    */   }
/* 274:    */   
/* 275:    */   private class ColorVector
/* 276:    */     implements Comparable<ColorVector>
/* 277:    */   {
/* 278:    */     int[] z;
/* 279:    */     
/* 280:    */     ColorVector(int a, int b, int c)
/* 281:    */     {
/* 282:321 */       this.z = new int[3];
/* 283:322 */       this.z[0] = a;
/* 284:323 */       this.z[1] = b;
/* 285:324 */       this.z[2] = c;
/* 286:    */     }
/* 287:    */     
/* 288:    */     public int compareTo(ColorVector c)
/* 289:    */     {
/* 290:328 */       return ColorQFZRGBByteNoLabelControl.this.vo.compare(this.z, c.z);
/* 291:    */     }
/* 292:    */   }
/* 293:    */   
/* 294:    */   public static Image invoke(Image img, int[] alpha, int[] omega)
/* 295:    */   {
/* 296:    */     try
/* 297:    */     {
/* 298:335 */       return (IntegerImage)new ColorQFZRGBByteNoLabelControl().preprocess(new Object[] { img, alpha, omega });
/* 299:    */     }
/* 300:    */     catch (GlobalException e)
/* 301:    */     {
/* 302:337 */       e.printStackTrace();
/* 303:    */     }
/* 304:338 */     return null;
/* 305:    */   }
/* 306:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorQFZRGBByteNoLabelControl
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.algorithms.flatzones.color;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.DoubleImage;
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  15:    */ 
/*  16:    */ public class ColorQFZRGB
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public int[] alphaByte;
/*  22:    */   public int[] omegaByte;
/*  23: 39 */   public AlgebraicOrdering vo = null;
/*  24: 41 */   private double[] alpha = new double[3];
/*  25: 42 */   private double[] omega = new double[3];
/*  26: 44 */   private ArrayList<Point> list = new ArrayList();
/*  27: 45 */   private ArrayList<Point> list2 = new ArrayList();
/*  28: 46 */   private Stack<Point> s = new Stack();
/*  29:    */   private int xdim;
/*  30:    */   private int ydim;
/*  31: 49 */   private int label = 1;
/*  32: 51 */   private ColorVector[] cv = new ColorVector[16777216];
/*  33: 52 */   private int alphaIndex = -1;
/*  34: 53 */   private int omegaIndex = -1;
/*  35:    */   BooleanImage temp;
/*  36:    */   Image localRanges;
/*  37: 65 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  38: 66 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  39:    */   
/*  40:    */   public ColorQFZRGB()
/*  41:    */   {
/*  42: 69 */     this.inputFields = "input,alphaByte,omegaByte,vo";
/*  43: 70 */     this.outputFields = "output";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void execute()
/*  47:    */     throws GlobalException
/*  48:    */   {
/*  49: 75 */     this.xdim = this.input.getXDim();
/*  50: 76 */     this.ydim = this.input.getYDim();
/*  51: 78 */     if (this.input.getCDim() != 3) {
/*  52: 78 */       throw new GlobalException("This implementation is only for color images.");
/*  53:    */     }
/*  54: 80 */     if ((this.alpha[0] < 0.0D) || (this.alpha[1] < 0.0D) || (this.alpha[2] < 0.0D) || (this.alpha[0] > 254.0D) || (this.alpha[1] > 254.0D) || (this.alpha[2] > 254.0D) || 
/*  55: 81 */       (this.omega[0] < 0.0D) || (this.omega[1] < 0.0D) || (this.omega[2] < 0.0D) || (this.omega[0] > 254.0D) || (this.omega[1] > 254.0D) || (this.omega[2] > 254.0D)) {
/*  56: 82 */       throw new GlobalException("Arguments out of range.");
/*  57:    */     }
/*  58: 84 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  59: 85 */     this.output.fill(0);
/*  60:    */     
/*  61: 87 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  62: 88 */     this.temp.fill(false);
/*  63:    */     
/*  64: 90 */     this.localRanges = new DoubleImage(this.xdim, this.ydim, 3);
/*  65: 91 */     this.localRanges.fill(1.0D);
/*  66: 93 */     for (int c = 0; c < this.alpha.length; c++) {
/*  67: 94 */       this.alpha[c] = (this.alphaByte[c] * 0.00392156862745098D);
/*  68:    */     }
/*  69: 96 */     for (int c = 0; c < this.omega.length; c++) {
/*  70: 97 */       this.omega[c] = (this.omegaByte[c] * 0.00392156862745098D);
/*  71:    */     }
/*  72: 99 */     initColorVectors();
/*  73:101 */     if (this.alphaIndex == -1) {
/*  74:101 */       System.err.println("What the hell, where is the alpha index?");
/*  75:    */     } else {
/*  76:102 */       System.err.println("Rank found at " + this.alphaIndex);
/*  77:    */     }
/*  78:104 */     if (this.omegaIndex == -1) {
/*  79:104 */       System.err.println("What the hell, where is the omega index?");
/*  80:    */     } else {
/*  81:105 */       System.err.println("Rank found at " + this.alphaIndex);
/*  82:    */     }
/*  83:107 */     for (int y = 0; y < this.ydim; y++)
/*  84:    */     {
/*  85:110 */       System.out.println("Satir " + y);
/*  86:112 */       for (int x = 0; x < this.xdim; x++) {
/*  87:117 */         if (this.output.getXYInt(x, y) <= 0)
/*  88:    */         {
/*  89:119 */           Point t = new Point(x, y);
/*  90:121 */           if (createQCC(t, this.alpha) > 0)
/*  91:    */           {
/*  92:123 */             for (Point s : this.list)
/*  93:    */             {
/*  94:124 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  95:124 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  96:    */               }
/*  97:125 */               this.output.setXYInt(s.x, s.y, this.label);
/*  98:    */             }
/*  99:129 */             for (Point s : this.list) {
/* 100:130 */               this.localRanges.setVXYDouble(s.x, s.y, this.alpha);
/* 101:    */             }
/* 102:    */           }
/* 103:    */           else
/* 104:    */           {
/* 105:134 */             int a_1 = 0;
/* 106:135 */             int a_2 = this.alphaIndex;
/* 107:    */             for (;;)
/* 108:    */             {
/* 109:138 */               this.list.clear();
/* 110:    */               
/* 111:140 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 112:142 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 113:    */               {
/* 114:143 */                 a_1 = tmpAlpha;
/* 115:147 */                 if (a_1 + 1 == a_2)
/* 116:    */                 {
/* 117:149 */                   for (Point s : this.list)
/* 118:    */                   {
/* 119:150 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 120:151 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 121:    */                     }
/* 122:152 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 123:    */                   }
/* 124:156 */                   for (Point s : this.list) {
/* 125:157 */                     this.localRanges.setVXYDouble(s.x, s.y, this.cv[tmpAlpha].z);
/* 126:    */                   }
/* 127:159 */                   break;
/* 128:    */                 }
/* 129:    */               }
/* 130:    */               else
/* 131:    */               {
/* 132:162 */                 a_2 = tmpAlpha;
/* 133:    */               }
/* 134:    */             }
/* 135:    */           }
/* 136:169 */           this.list.clear();
/* 137:    */           
/* 138:171 */           this.label += 1;
/* 139:    */         }
/* 140:    */       }
/* 141:    */     }
/* 142:175 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 143:    */   }
/* 144:    */   
/* 145:    */   private int createQCC(Point r, double[] alpha2)
/* 146:    */   {
/* 147:187 */     double[] max = { 0.0D, 0.0D, 0.0D };
/* 148:188 */     double[] min = { 1.0D, 1.0D, 1.0D };
/* 149:189 */     int rankA = rankOf(alpha2);
/* 150:192 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 151:192 */       System.err.println("What the hell is going on?");
/* 152:    */     }
/* 153:194 */     this.s.clear();
/* 154:195 */     this.s.add(r);
/* 155:196 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 156:197 */     this.list2.add(r);
/* 157:    */     int x;
/* 158:    */     int i;
/* 159:199 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 160:    */     {
/* 161:201 */       Point tmp = (Point)this.s.pop();
/* 162:202 */       x = tmp.x;
/* 163:203 */       int y = tmp.y;
/* 164:    */       
/* 165:205 */       double[] p = this.input.getVXYDouble(x, y);
/* 166:208 */       if (this.vo.compare(max, p) < 0) {
/* 167:208 */         max = p;
/* 168:    */       }
/* 169:209 */       if (this.vo.compare(min, p) > 0) {
/* 170:209 */         min = p;
/* 171:    */       }
/* 172:211 */       int rankDiff = rankOf(max) - rankOf(min);
/* 173:212 */       int rankP = rankOf(p);
/* 174:214 */       if (rankDiff > this.omegaIndex)
/* 175:    */       {
/* 176:216 */         for (Point t : this.list2) {
/* 177:217 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 178:    */         }
/* 179:218 */         this.list2.clear();
/* 180:    */         
/* 181:220 */         return -1;
/* 182:    */       }
/* 183:223 */       this.list.add(tmp);
/* 184:    */       
/* 185:225 */       i = 0; continue;
/* 186:226 */       int _x = x + this.N[i].x;
/* 187:227 */       int _y = y + this.N[i].y;
/* 188:229 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 189:231 */         if (!this.temp.getXYBoolean(_x, _y))
/* 190:    */         {
/* 191:233 */           double[] q = this.input.getVXYDouble(_x, _y);
/* 192:    */           
/* 193:235 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 194:237 */           if (this.output.getXYInt(_x, _y) > 0)
/* 195:    */           {
/* 196:239 */             double[] localR = this.localRanges.getVXYDouble(_x, _y);
/* 197:241 */             if ((rankDiff <= rankA) && (this.vo.compare(localR, alpha2) <= 0))
/* 198:    */             {
/* 199:243 */               for (Point t : this.list2) {
/* 200:244 */                 this.temp.setXYBoolean(t.x, t.y, false);
/* 201:    */               }
/* 202:245 */               this.list2.clear();
/* 203:    */               
/* 204:247 */               return -1;
/* 205:    */             }
/* 206:    */           }
/* 207:253 */           else if (rankDiff <= rankA)
/* 208:    */           {
/* 209:255 */             Point t = new Point(_x, _y);
/* 210:256 */             this.s.add(t);
/* 211:    */             
/* 212:258 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 213:259 */             this.list2.add(t);
/* 214:    */           }
/* 215:    */         }
/* 216:    */       }
/* 217:225 */       i++;
/* 218:    */     }
/* 219:264 */     for (Point t : this.list2) {
/* 220:265 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 221:    */     }
/* 222:266 */     this.list2.clear();
/* 223:    */     
/* 224:268 */     return 1;
/* 225:    */   }
/* 226:    */   
/* 227:    */   private void initColorVectors()
/* 228:    */   {
/* 229:273 */     int j = 0;
/* 230:274 */     for (int r = 0; r < 256; r++) {
/* 231:275 */       for (int g = 0; g < 256; g++) {
/* 232:276 */         for (int b = 0; b < 256; b++) {
/* 233:277 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 234:    */         }
/* 235:    */       }
/* 236:    */     }
/* 237:281 */     System.err.println("Sorting colors");
/* 238:    */     
/* 239:283 */     Arrays.sort(this.cv);
/* 240:    */     
/* 241:285 */     System.err.println("Colors sorted");
/* 242:    */     
/* 243:287 */     this.alphaIndex = rankOf(this.alpha);
/* 244:288 */     this.omegaIndex = rankOf(this.omega);
/* 245:    */   }
/* 246:    */   
/* 247:    */   private int rankOf(double[] a)
/* 248:    */   {
/* 249:292 */     int first = 0;
/* 250:293 */     int last = this.cv.length - 1;
/* 251:294 */     int mid = 0;
/* 252:    */     
/* 253:296 */     boolean found = false;
/* 254:298 */     while ((!found) && (first <= last))
/* 255:    */     {
/* 256:299 */       mid = first + (last - first) / 2;
/* 257:300 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 258:300 */         found = true;
/* 259:301 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 260:301 */         last = mid - 1;
/* 261:302 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 262:302 */         first = mid + 1;
/* 263:    */       }
/* 264:    */     }
/* 265:305 */     return mid;
/* 266:    */   }
/* 267:    */   
/* 268:    */   private class ColorVector
/* 269:    */     implements Comparable
/* 270:    */   {
/* 271:    */     double[] z;
/* 272:    */     
/* 273:    */     ColorVector(int a, int b, int c)
/* 274:    */     {
/* 275:313 */       this.z = new double[3];
/* 276:314 */       this.z[0] = (a * 0.00392156862745098D);
/* 277:315 */       this.z[1] = (b * 0.00392156862745098D);
/* 278:316 */       this.z[2] = (c * 0.00392156862745098D);
/* 279:    */     }
/* 280:    */     
/* 281:    */     public int compareTo(Object o)
/* 282:    */     {
/* 283:320 */       ColorVector c = (ColorVector)o;
/* 284:321 */       return ColorQFZRGB.this.vo.compare(this.z, c.z);
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   public static Image invoke(Image img, int[] alpha, int[] omega, AlgebraicOrdering vo)
/* 289:    */   {
/* 290:    */     try
/* 291:    */     {
/* 292:328 */       return (IntegerImage)new ColorQFZRGB().preprocess(new Object[] { img, alpha, omega, vo });
/* 293:    */     }
/* 294:    */     catch (GlobalException e)
/* 295:    */     {
/* 296:330 */       e.printStackTrace();
/* 297:    */     }
/* 298:331 */     return null;
/* 299:    */   }
/* 300:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZRGB
 * JD-Core Version:    0.7.0.1
 */
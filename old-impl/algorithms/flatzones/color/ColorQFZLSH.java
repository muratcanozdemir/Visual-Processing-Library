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
/*  14:    */ import vpt.algorithms.conversion.RGB2HSY;
/*  15:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  16:    */ 
/*  17:    */ public class ColorQFZLSH
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20:    */   public Image input;
/*  21:    */   public IntegerImage output;
/*  22:    */   public int[] alphaByte;
/*  23:    */   public int[] omegaByte;
/*  24: 40 */   public AlgebraicOrdering vo = null;
/*  25: 42 */   private double[] alpha = new double[3];
/*  26: 43 */   private double[] omega = new double[3];
/*  27: 45 */   private ArrayList<Point> list = new ArrayList();
/*  28: 46 */   private ArrayList<Point> list2 = new ArrayList();
/*  29: 47 */   private Stack<Point> s = new Stack();
/*  30:    */   private int xdim;
/*  31:    */   private int ydim;
/*  32: 50 */   private int label = 1;
/*  33: 52 */   private ColorVector[] cv = new ColorVector[16777216];
/*  34: 53 */   private int alphaIndex = -1;
/*  35: 54 */   private int omegaIndex = -1;
/*  36:    */   BooleanImage temp;
/*  37:    */   Image localRanges;
/*  38: 66 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  39: 67 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  40:    */   
/*  41:    */   public ColorQFZLSH()
/*  42:    */   {
/*  43: 70 */     this.inputFields = "input,alphaByte,omegaByte,vo";
/*  44: 71 */     this.outputFields = "output";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void execute()
/*  48:    */     throws GlobalException
/*  49:    */   {
/*  50: 76 */     this.xdim = this.input.getXDim();
/*  51: 77 */     this.ydim = this.input.getYDim();
/*  52: 79 */     if (this.input.getCDim() != 3) {
/*  53: 79 */       throw new GlobalException("This implementation is only for color images.");
/*  54:    */     }
/*  55: 81 */     if ((this.alpha[0] < 0.0D) || (this.alpha[1] < 0.0D) || (this.alpha[2] < 0.0D) || (this.alpha[0] > 254.0D) || (this.alpha[1] > 254.0D) || (this.alpha[2] > 254.0D) || 
/*  56: 82 */       (this.omega[0] < 0.0D) || (this.omega[1] < 0.0D) || (this.omega[2] < 0.0D) || (this.omega[0] > 254.0D) || (this.omega[1] > 254.0D) || (this.omega[2] > 254.0D)) {
/*  57: 83 */       throw new GlobalException("Arguments out of range.");
/*  58:    */     }
/*  59: 85 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  60: 86 */     this.output.fill(0);
/*  61:    */     
/*  62: 88 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  63: 89 */     this.temp.fill(false);
/*  64:    */     
/*  65: 91 */     this.localRanges = new DoubleImage(this.xdim, this.ydim, 3);
/*  66: 92 */     this.localRanges.fill(1.0D);
/*  67: 94 */     for (int c = 0; c < this.alpha.length; c++) {
/*  68: 95 */       this.alpha[c] = (this.alphaByte[c] * 0.00392156862745098D);
/*  69:    */     }
/*  70: 97 */     for (int c = 0; c < this.omega.length; c++) {
/*  71: 98 */       this.omega[c] = (this.omegaByte[c] * 0.00392156862745098D);
/*  72:    */     }
/*  73:100 */     initColorVectors();
/*  74:102 */     if (this.alphaIndex == -1) {
/*  75:102 */       System.err.println("What the hell, where is the alpha index?");
/*  76:    */     } else {
/*  77:103 */       System.err.println("Rank found at " + this.alphaIndex);
/*  78:    */     }
/*  79:105 */     if (this.omegaIndex == -1) {
/*  80:105 */       System.err.println("What the hell, where is the omega index?");
/*  81:    */     } else {
/*  82:106 */       System.err.println("Rank found at " + this.alphaIndex);
/*  83:    */     }
/*  84:108 */     for (int y = 0; y < this.ydim; y++)
/*  85:    */     {
/*  86:111 */       System.out.println("Satir " + y);
/*  87:113 */       for (int x = 0; x < this.xdim; x++) {
/*  88:118 */         if (this.output.getXYInt(x, y) <= 0)
/*  89:    */         {
/*  90:120 */           Point t = new Point(x, y);
/*  91:122 */           if (createQCC(t, this.alpha) > 0)
/*  92:    */           {
/*  93:124 */             for (Point s : this.list)
/*  94:    */             {
/*  95:125 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  96:125 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  97:    */               }
/*  98:126 */               this.output.setXYInt(s.x, s.y, this.label);
/*  99:    */             }
/* 100:130 */             for (Point s : this.list) {
/* 101:131 */               this.localRanges.setVXYDouble(s.x, s.y, this.alpha);
/* 102:    */             }
/* 103:    */           }
/* 104:    */           else
/* 105:    */           {
/* 106:135 */             int a_1 = 0;
/* 107:136 */             int a_2 = this.alphaIndex;
/* 108:    */             for (;;)
/* 109:    */             {
/* 110:139 */               this.list.clear();
/* 111:    */               
/* 112:141 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 113:143 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 114:    */               {
/* 115:144 */                 a_1 = tmpAlpha;
/* 116:148 */                 if (a_1 + 1 == a_2)
/* 117:    */                 {
/* 118:150 */                   for (Point s : this.list)
/* 119:    */                   {
/* 120:151 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 121:152 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 122:    */                     }
/* 123:153 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 124:    */                   }
/* 125:157 */                   for (Point s : this.list) {
/* 126:158 */                     this.localRanges.setVXYDouble(s.x, s.y, this.cv[tmpAlpha].z);
/* 127:    */                   }
/* 128:160 */                   break;
/* 129:    */                 }
/* 130:    */               }
/* 131:    */               else
/* 132:    */               {
/* 133:163 */                 a_2 = tmpAlpha;
/* 134:    */               }
/* 135:    */             }
/* 136:    */           }
/* 137:170 */           this.list.clear();
/* 138:    */           
/* 139:172 */           this.label += 1;
/* 140:    */         }
/* 141:    */       }
/* 142:    */     }
/* 143:176 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 144:    */   }
/* 145:    */   
/* 146:    */   private int createQCC(Point r, double[] alpha2)
/* 147:    */   {
/* 148:188 */     double[] max = { 0.0D, 0.0D, 0.0D };
/* 149:189 */     double[] min = { 1.0D, 1.0D, 1.0D };
/* 150:190 */     int rankA = rankOf(alpha2);
/* 151:193 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 152:193 */       System.err.println("What the hell is going on?");
/* 153:    */     }
/* 154:195 */     this.s.clear();
/* 155:196 */     this.s.add(r);
/* 156:197 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 157:198 */     this.list2.add(r);
/* 158:    */     int x;
/* 159:    */     int i;
/* 160:200 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 161:    */     {
/* 162:202 */       Point tmp = (Point)this.s.pop();
/* 163:203 */       x = tmp.x;
/* 164:204 */       int y = tmp.y;
/* 165:    */       
/* 166:206 */       double[] p = this.input.getVXYDouble(x, y);
/* 167:209 */       if (this.vo.compare(max, p) < 0) {
/* 168:209 */         max = p;
/* 169:    */       }
/* 170:210 */       if (this.vo.compare(min, p) > 0) {
/* 171:210 */         min = p;
/* 172:    */       }
/* 173:212 */       int rankDiff = rankOf(max) - rankOf(min);
/* 174:213 */       int rankP = rankOf(p);
/* 175:215 */       if (rankDiff > this.omegaIndex)
/* 176:    */       {
/* 177:217 */         for (Point t : this.list2) {
/* 178:218 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 179:    */         }
/* 180:219 */         this.list2.clear();
/* 181:    */         
/* 182:221 */         return -1;
/* 183:    */       }
/* 184:224 */       this.list.add(tmp);
/* 185:    */       
/* 186:226 */       i = 0; continue;
/* 187:227 */       int _x = x + this.N[i].x;
/* 188:228 */       int _y = y + this.N[i].y;
/* 189:230 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 190:232 */         if (!this.temp.getXYBoolean(_x, _y))
/* 191:    */         {
/* 192:234 */           double[] q = this.input.getVXYDouble(_x, _y);
/* 193:    */           
/* 194:236 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 195:238 */           if (this.output.getXYInt(_x, _y) > 0)
/* 196:    */           {
/* 197:240 */             double[] localR = this.localRanges.getVXYDouble(_x, _y);
/* 198:242 */             if ((rankDiff <= rankA) && (this.vo.compare(localR, alpha2) <= 0))
/* 199:    */             {
/* 200:244 */               for (Point t : this.list2) {
/* 201:245 */                 this.temp.setXYBoolean(t.x, t.y, false);
/* 202:    */               }
/* 203:246 */               this.list2.clear();
/* 204:    */               
/* 205:248 */               return -1;
/* 206:    */             }
/* 207:    */           }
/* 208:254 */           else if (rankDiff <= rankA)
/* 209:    */           {
/* 210:256 */             Point t = new Point(_x, _y);
/* 211:257 */             this.s.add(t);
/* 212:    */             
/* 213:259 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 214:260 */             this.list2.add(t);
/* 215:    */           }
/* 216:    */         }
/* 217:    */       }
/* 218:226 */       i++;
/* 219:    */     }
/* 220:265 */     for (Point t : this.list2) {
/* 221:266 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 222:    */     }
/* 223:267 */     this.list2.clear();
/* 224:    */     
/* 225:269 */     return 1;
/* 226:    */   }
/* 227:    */   
/* 228:    */   private void initColorVectors()
/* 229:    */   {
/* 230:274 */     int j = 0;
/* 231:275 */     for (int r = 0; r < 256; r++) {
/* 232:276 */       for (int g = 0; g < 256; g++) {
/* 233:277 */         for (int b = 0; b < 256; b++) {
/* 234:278 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 235:    */         }
/* 236:    */       }
/* 237:    */     }
/* 238:282 */     System.err.println("Sorting colors");
/* 239:    */     
/* 240:284 */     Arrays.sort(this.cv);
/* 241:    */     
/* 242:286 */     System.err.println("Colors sorted");
/* 243:    */     
/* 244:288 */     this.alphaIndex = rankOf(this.alpha);
/* 245:289 */     this.omegaIndex = rankOf(this.omega);
/* 246:    */   }
/* 247:    */   
/* 248:    */   private int rankOf(double[] a)
/* 249:    */   {
/* 250:293 */     int first = 0;
/* 251:294 */     int last = this.cv.length - 1;
/* 252:295 */     int mid = 0;
/* 253:    */     
/* 254:297 */     boolean found = false;
/* 255:299 */     while ((!found) && (first <= last))
/* 256:    */     {
/* 257:300 */       mid = first + (last - first) / 2;
/* 258:301 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 259:301 */         found = true;
/* 260:302 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 261:302 */         last = mid - 1;
/* 262:303 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 263:303 */         first = mid + 1;
/* 264:    */       }
/* 265:    */     }
/* 266:306 */     return mid;
/* 267:    */   }
/* 268:    */   
/* 269:    */   private class ColorVector
/* 270:    */     implements Comparable
/* 271:    */   {
/* 272:    */     double[] z;
/* 273:    */     
/* 274:    */     ColorVector(int a, int b, int c)
/* 275:    */     {
/* 276:314 */       this.z = RGB2HSY.convert(a * 0.00392156862745098D, b * 0.00392156862745098D, c * 0.00392156862745098D);
/* 277:    */     }
/* 278:    */     
/* 279:    */     public int compareTo(Object o)
/* 280:    */     {
/* 281:318 */       ColorVector c = (ColorVector)o;
/* 282:319 */       return ColorQFZLSH.this.vo.compare(this.z, c.z);
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static Image invoke(Image img, int[] alpha, int[] omega, AlgebraicOrdering vo)
/* 287:    */   {
/* 288:    */     try
/* 289:    */     {
/* 290:326 */       return (IntegerImage)new ColorQFZLSH().preprocess(new Object[] { img, alpha, omega, vo });
/* 291:    */     }
/* 292:    */     catch (GlobalException e)
/* 293:    */     {
/* 294:328 */       e.printStackTrace();
/* 295:    */     }
/* 296:329 */     return null;
/* 297:    */   }
/* 298:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZLSH
 * JD-Core Version:    0.7.0.1
 */
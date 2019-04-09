/*   1:    */ package vpt.algorithms.segmentation.berkeley;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.BooleanImage;
/*   9:    */ import vpt.DoubleImage;
/*  10:    */ import vpt.GlobalException;
/*  11:    */ import vpt.Image;
/*  12:    */ import vpt.IntegerImage;
/*  13:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  14:    */ 
/*  15:    */ public class ColorQFZRGB
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18:    */   public Image input;
/*  19:    */   public IntegerImage output;
/*  20: 34 */   public ColorVector[] cv = null;
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
/*  32: 51 */   private int alphaIndex = -1;
/*  33: 52 */   private int omegaIndex = -1;
/*  34:    */   BooleanImage temp;
/*  35:    */   Image localRanges;
/*  36: 64 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  37: 65 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  38:    */   
/*  39:    */   public ColorQFZRGB()
/*  40:    */   {
/*  41: 68 */     this.inputFields = "input,alphaByte,omegaByte,cv,vo";
/*  42: 69 */     this.outputFields = "output";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void execute()
/*  46:    */     throws GlobalException
/*  47:    */   {
/*  48: 74 */     this.xdim = this.input.getXDim();
/*  49: 75 */     this.ydim = this.input.getYDim();
/*  50: 77 */     if (this.input.getCDim() != 3) {
/*  51: 77 */       throw new GlobalException("This implementation is only for color images.");
/*  52:    */     }
/*  53: 79 */     if ((this.alpha[0] < 0.0D) || (this.alpha[1] < 0.0D) || (this.alpha[2] < 0.0D) || (this.alpha[0] > 254.0D) || (this.alpha[1] > 254.0D) || (this.alpha[2] > 254.0D) || 
/*  54: 80 */       (this.omega[0] < 0.0D) || (this.omega[1] < 0.0D) || (this.omega[2] < 0.0D) || (this.omega[0] > 254.0D) || (this.omega[1] > 254.0D) || (this.omega[2] > 254.0D)) {
/*  55: 81 */       throw new GlobalException("Arguments out of range.");
/*  56:    */     }
/*  57: 83 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  58: 84 */     this.output.fill(0);
/*  59:    */     
/*  60: 86 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  61: 87 */     this.temp.fill(false);
/*  62:    */     
/*  63: 89 */     this.localRanges = new DoubleImage(this.xdim, this.ydim, 3);
/*  64: 90 */     this.localRanges.fill(1.0D);
/*  65: 92 */     for (int c = 0; c < this.alpha.length; c++) {
/*  66: 93 */       this.alpha[c] = (this.alphaByte[c] * 0.00392156862745098D);
/*  67:    */     }
/*  68: 95 */     for (int c = 0; c < this.omega.length; c++) {
/*  69: 96 */       this.omega[c] = (this.omegaByte[c] * 0.00392156862745098D);
/*  70:    */     }
/*  71: 98 */     this.alphaIndex = rankOf(this.alpha);
/*  72: 99 */     this.omegaIndex = rankOf(this.omega);
/*  73:101 */     if (this.alphaIndex == -1) {
/*  74:101 */       System.err.println("What the hell, where is the alpha index?");
/*  75:    */     }
/*  76:103 */     if (this.omegaIndex == -1) {
/*  77:103 */       System.err.println("What the hell, where is the omega index?");
/*  78:    */     }
/*  79:105 */     for (int y = 0; y < this.ydim; y++) {
/*  80:108 */       for (int x = 0; x < this.xdim; x++) {
/*  81:113 */         if (this.output.getXYInt(x, y) <= 0)
/*  82:    */         {
/*  83:115 */           Point t = new Point(x, y);
/*  84:117 */           if (createQCC(t, this.alpha) > 0)
/*  85:    */           {
/*  86:119 */             for (Point s : this.list)
/*  87:    */             {
/*  88:120 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  89:120 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  90:    */               }
/*  91:121 */               this.output.setXYInt(s.x, s.y, this.label);
/*  92:    */             }
/*  93:125 */             for (Point s : this.list) {
/*  94:126 */               this.localRanges.setVXYDouble(s.x, s.y, this.alpha);
/*  95:    */             }
/*  96:    */           }
/*  97:    */           else
/*  98:    */           {
/*  99:130 */             int a_1 = 0;
/* 100:131 */             int a_2 = this.alphaIndex;
/* 101:    */             for (;;)
/* 102:    */             {
/* 103:134 */               this.list.clear();
/* 104:    */               
/* 105:136 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 106:138 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 107:    */               {
/* 108:139 */                 a_1 = tmpAlpha;
/* 109:143 */                 if (a_1 + 1 == a_2)
/* 110:    */                 {
/* 111:145 */                   for (Point s : this.list)
/* 112:    */                   {
/* 113:146 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 114:147 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 115:    */                     }
/* 116:148 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 117:    */                   }
/* 118:152 */                   for (Point s : this.list) {
/* 119:153 */                     this.localRanges.setVXYDouble(s.x, s.y, this.cv[tmpAlpha].z);
/* 120:    */                   }
/* 121:155 */                   break;
/* 122:    */                 }
/* 123:    */               }
/* 124:    */               else
/* 125:    */               {
/* 126:158 */                 a_2 = tmpAlpha;
/* 127:    */               }
/* 128:    */             }
/* 129:    */           }
/* 130:165 */           this.list.clear();
/* 131:    */           
/* 132:167 */           this.label += 1;
/* 133:    */         }
/* 134:    */       }
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   private int createQCC(Point r, double[] alpha2)
/* 139:    */   {
/* 140:183 */     double[] max = { 0.0D, 0.0D, 0.0D };
/* 141:184 */     double[] min = { 1.0D, 1.0D, 1.0D };
/* 142:185 */     int rankA = rankOf(alpha2);
/* 143:188 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 144:188 */       System.err.println("What the hell is going on?");
/* 145:    */     }
/* 146:190 */     this.s.clear();
/* 147:191 */     this.s.add(r);
/* 148:192 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 149:193 */     this.list2.add(r);
/* 150:    */     int x;
/* 151:    */     int i;
/* 152:195 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 153:    */     {
/* 154:197 */       Point tmp = (Point)this.s.pop();
/* 155:198 */       x = tmp.x;
/* 156:199 */       int y = tmp.y;
/* 157:    */       
/* 158:201 */       double[] p = this.input.getVXYDouble(x, y);
/* 159:204 */       if (this.vo.compare(max, p) < 0) {
/* 160:204 */         max = p;
/* 161:    */       }
/* 162:205 */       if (this.vo.compare(min, p) > 0) {
/* 163:205 */         min = p;
/* 164:    */       }
/* 165:207 */       int rankDiff = rankOf(max) - rankOf(min);
/* 166:208 */       int rankP = rankOf(p);
/* 167:210 */       if (rankDiff > this.omegaIndex)
/* 168:    */       {
/* 169:212 */         for (Point t : this.list2) {
/* 170:213 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 171:    */         }
/* 172:214 */         this.list2.clear();
/* 173:    */         
/* 174:216 */         return -1;
/* 175:    */       }
/* 176:219 */       this.list.add(tmp);
/* 177:    */       
/* 178:221 */       i = 0; continue;
/* 179:222 */       int _x = x + this.N[i].x;
/* 180:223 */       int _y = y + this.N[i].y;
/* 181:225 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 182:227 */         if (!this.temp.getXYBoolean(_x, _y))
/* 183:    */         {
/* 184:229 */           double[] q = this.input.getVXYDouble(_x, _y);
/* 185:    */           
/* 186:231 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 187:233 */           if (this.output.getXYInt(_x, _y) > 0)
/* 188:    */           {
/* 189:235 */             double[] localR = this.localRanges.getVXYDouble(_x, _y);
/* 190:237 */             if ((rankDiff <= rankA) && (this.vo.compare(localR, alpha2) <= 0))
/* 191:    */             {
/* 192:239 */               for (Point t : this.list2) {
/* 193:240 */                 this.temp.setXYBoolean(t.x, t.y, false);
/* 194:    */               }
/* 195:241 */               this.list2.clear();
/* 196:    */               
/* 197:243 */               return -1;
/* 198:    */             }
/* 199:    */           }
/* 200:249 */           else if (rankDiff <= rankA)
/* 201:    */           {
/* 202:251 */             Point t = new Point(_x, _y);
/* 203:252 */             this.s.add(t);
/* 204:    */             
/* 205:254 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 206:255 */             this.list2.add(t);
/* 207:    */           }
/* 208:    */         }
/* 209:    */       }
/* 210:221 */       i++;
/* 211:    */     }
/* 212:260 */     for (Point t : this.list2) {
/* 213:261 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 214:    */     }
/* 215:262 */     this.list2.clear();
/* 216:    */     
/* 217:264 */     return 1;
/* 218:    */   }
/* 219:    */   
/* 220:    */   private int rankOf(double[] a)
/* 221:    */   {
/* 222:268 */     int first = 0;
/* 223:269 */     int last = this.cv.length - 1;
/* 224:270 */     int mid = 0;
/* 225:    */     
/* 226:272 */     boolean found = false;
/* 227:274 */     while ((!found) && (first <= last))
/* 228:    */     {
/* 229:275 */       mid = first + (last - first) / 2;
/* 230:276 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 231:276 */         found = true;
/* 232:277 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 233:277 */         last = mid - 1;
/* 234:278 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 235:278 */         first = mid + 1;
/* 236:    */       }
/* 237:    */     }
/* 238:281 */     return mid;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public static Image invoke(Image img, int[] alpha, int[] omega, ColorVector[] cv, AlgebraicOrdering vo)
/* 242:    */   {
/* 243:    */     try
/* 244:    */     {
/* 245:286 */       return (IntegerImage)new ColorQFZRGB().preprocess(new Object[] { img, alpha, omega, cv, vo });
/* 246:    */     }
/* 247:    */     catch (GlobalException e)
/* 248:    */     {
/* 249:288 */       e.printStackTrace();
/* 250:    */     }
/* 251:289 */     return null;
/* 252:    */   }
/* 253:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.ColorQFZRGB
 * JD-Core Version:    0.7.0.1
 */
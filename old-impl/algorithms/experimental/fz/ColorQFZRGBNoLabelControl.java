/*   1:    */ package vpt.algorithms.experimental.fz;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.GlobalException;
/*  11:    */ import vpt.Image;
/*  12:    */ import vpt.IntegerImage;
/*  13:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  14:    */ import vpt.util.ordering.EuclideanNorm;
/*  15:    */ 
/*  16:    */ public class ColorQFZRGBNoLabelControl
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public int[] alphaByte;
/*  22:    */   public int[] omegaByte;
/*  23: 45 */   public AlgebraicOrdering vo = new EuclideanNorm();
/*  24: 48 */   private double[] alpha = new double[3];
/*  25: 49 */   private double[] omega = new double[3];
/*  26: 51 */   private ArrayList<Point> list = new ArrayList();
/*  27: 52 */   private ArrayList<Point> list2 = new ArrayList();
/*  28: 53 */   private Stack<Point> s = new Stack();
/*  29:    */   private int xdim;
/*  30:    */   private int ydim;
/*  31: 56 */   private int label = 1;
/*  32: 58 */   private ColorVector[] cv = new ColorVector[16777216];
/*  33: 59 */   private int alphaIndex = -1;
/*  34: 60 */   private int omegaIndex = -1;
/*  35:    */   BooleanImage temp;
/*  36: 70 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  37: 71 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  38:    */   
/*  39:    */   public ColorQFZRGBNoLabelControl()
/*  40:    */   {
/*  41: 74 */     this.inputFields = "input,alphaByte,omegaByte";
/*  42: 75 */     this.outputFields = "output";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void execute()
/*  46:    */     throws GlobalException
/*  47:    */   {
/*  48: 80 */     this.xdim = this.input.getXDim();
/*  49: 81 */     this.ydim = this.input.getYDim();
/*  50: 83 */     if (this.input.getCDim() != 3) {
/*  51: 83 */       throw new GlobalException("This implementation is only for color images.");
/*  52:    */     }
/*  53: 85 */     if ((this.alpha[0] < 0.0D) || (this.alpha[1] < 0.0D) || (this.alpha[2] < 0.0D) || (this.alpha[0] > 254.0D) || (this.alpha[1] > 254.0D) || (this.alpha[2] > 254.0D) || 
/*  54: 86 */       (this.omega[0] < 0.0D) || (this.omega[1] < 0.0D) || (this.omega[2] < 0.0D) || (this.omega[0] > 254.0D) || (this.omega[1] > 254.0D) || (this.omega[2] > 254.0D)) {
/*  55: 87 */       throw new GlobalException("Arguments out of range.");
/*  56:    */     }
/*  57: 89 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  58: 90 */     this.output.fill(0);
/*  59:    */     
/*  60: 92 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  61: 93 */     this.temp.fill(false);
/*  62: 95 */     for (int c = 0; c < this.alpha.length; c++) {
/*  63: 96 */       this.alpha[c] = (this.alphaByte[c] * 0.00392156862745098D);
/*  64:    */     }
/*  65: 98 */     for (int c = 0; c < this.omega.length; c++) {
/*  66: 99 */       this.omega[c] = (this.omegaByte[c] * 0.00392156862745098D);
/*  67:    */     }
/*  68:101 */     initColorVectors();
/*  69:103 */     if (this.alphaIndex == -1) {
/*  70:103 */       System.err.println("What the hell, where is the alpha index?");
/*  71:    */     } else {
/*  72:104 */       System.err.println("Rank found at " + this.alphaIndex);
/*  73:    */     }
/*  74:106 */     if (this.omegaIndex == -1) {
/*  75:106 */       System.err.println("What the hell, where is the omega index?");
/*  76:    */     } else {
/*  77:107 */       System.err.println("Rank found at " + this.alphaIndex);
/*  78:    */     }
/*  79:110 */     for (int y = this.ydim - 1; y >= 0; y--)
/*  80:    */     {
/*  81:112 */       System.out.println("Satir " + y);
/*  82:115 */       for (int x = this.xdim - 1; x >= 0; x--) {
/*  83:119 */         if (this.output.getXYInt(x, y) <= 0)
/*  84:    */         {
/*  85:121 */           Point t = new Point(x, y);
/*  86:123 */           if (createQCC(t, this.alpha) > 0)
/*  87:    */           {
/*  88:125 */             for (Point s : this.list)
/*  89:    */             {
/*  90:126 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  91:126 */                 System.err.println("dafuq " + s.x + " " + s.y);
/*  92:    */               }
/*  93:127 */               this.output.setXYInt(s.x, s.y, this.label);
/*  94:    */             }
/*  95:    */           }
/*  96:    */           else
/*  97:    */           {
/*  98:130 */             int a_1 = 0;
/*  99:131 */             int a_2 = this.alphaIndex;
/* 100:    */             for (;;)
/* 101:    */             {
/* 102:134 */               this.list.clear();
/* 103:    */               
/* 104:136 */               int tmpAlpha = (a_1 + a_2) / 2;
/* 105:138 */               if (createQCC(t, this.cv[tmpAlpha].z) > 0)
/* 106:    */               {
/* 107:139 */                 a_1 = tmpAlpha;
/* 108:143 */                 if (a_1 + 1 == a_2)
/* 109:    */                 {
/* 110:145 */                   for (Point s : this.list)
/* 111:    */                   {
/* 112:146 */                     if (this.output.getXYInt(s.x, s.y) > 0) {
/* 113:147 */                       System.err.println("dafuq " + s.x + " " + s.y + " eski label " + this.output.getXYInt(s.x, s.y) + " yenisi " + this.label);
/* 114:    */                     }
/* 115:148 */                     this.output.setXYInt(s.x, s.y, this.label);
/* 116:    */                   }
/* 117:151 */                   break;
/* 118:    */                 }
/* 119:    */               }
/* 120:    */               else
/* 121:    */               {
/* 122:154 */                 a_2 = tmpAlpha;
/* 123:    */               }
/* 124:    */             }
/* 125:    */           }
/* 126:161 */           this.list.clear();
/* 127:    */           
/* 128:163 */           this.label += 1;
/* 129:    */         }
/* 130:    */       }
/* 131:    */     }
/* 132:167 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 133:    */   }
/* 134:    */   
/* 135:    */   private int createQCC(Point r, double[] alpha2)
/* 136:    */   {
/* 137:179 */     double[] max = { 0.0D, 0.0D, 0.0D };
/* 138:180 */     double[] min = { 1.0D, 1.0D, 1.0D };
/* 139:181 */     int rankA = rankOf(alpha2);
/* 140:184 */     if (this.output.getXYInt(r.x, r.y) > 0) {
/* 141:184 */       System.err.println("What the hell is going on?");
/* 142:    */     }
/* 143:186 */     this.s.clear();
/* 144:187 */     this.s.add(r);
/* 145:188 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 146:189 */     this.list2.add(r);
/* 147:    */     int x;
/* 148:    */     int i;
/* 149:191 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 150:    */     {
/* 151:193 */       Point tmp = (Point)this.s.pop();
/* 152:194 */       x = tmp.x;
/* 153:195 */       int y = tmp.y;
/* 154:    */       
/* 155:197 */       double[] p = this.input.getVXYDouble(x, y);
/* 156:200 */       if (this.vo.compare(max, p) < 0) {
/* 157:200 */         max = p;
/* 158:    */       }
/* 159:201 */       if (this.vo.compare(min, p) > 0) {
/* 160:201 */         min = p;
/* 161:    */       }
/* 162:203 */       int rankDiff = rankOf(max) - rankOf(min);
/* 163:204 */       int rankP = rankOf(p);
/* 164:206 */       if (rankDiff > this.omegaIndex)
/* 165:    */       {
/* 166:208 */         for (Point t : this.list2) {
/* 167:209 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 168:    */         }
/* 169:210 */         this.list2.clear();
/* 170:    */         
/* 171:212 */         return -1;
/* 172:    */       }
/* 173:215 */       this.list.add(tmp);
/* 174:    */       
/* 175:217 */       i = 0; continue;
/* 176:218 */       int _x = x + this.N[i].x;
/* 177:219 */       int _y = y + this.N[i].y;
/* 178:221 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 179:223 */         if (!this.temp.getXYBoolean(_x, _y))
/* 180:    */         {
/* 181:225 */           double[] q = this.input.getVXYDouble(_x, _y);
/* 182:    */           
/* 183:227 */           rankDiff = Math.abs(rankP - rankOf(q));
/* 184:229 */           if (rankDiff <= rankA)
/* 185:    */           {
/* 186:231 */             Point t = new Point(_x, _y);
/* 187:232 */             this.s.add(t);
/* 188:    */             
/* 189:234 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 190:235 */             this.list2.add(t);
/* 191:    */           }
/* 192:    */         }
/* 193:    */       }
/* 194:217 */       i++;
/* 195:    */     }
/* 196:240 */     for (Point t : this.list2) {
/* 197:241 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 198:    */     }
/* 199:242 */     this.list2.clear();
/* 200:    */     
/* 201:244 */     return 1;
/* 202:    */   }
/* 203:    */   
/* 204:    */   private void initColorVectors()
/* 205:    */   {
/* 206:249 */     int j = 0;
/* 207:250 */     for (int r = 0; r < 256; r++) {
/* 208:251 */       for (int g = 0; g < 256; g++) {
/* 209:252 */         for (int b = 0; b < 256; b++) {
/* 210:253 */           this.cv[(j++)] = new ColorVector(r, g, b);
/* 211:    */         }
/* 212:    */       }
/* 213:    */     }
/* 214:257 */     System.err.println("Sorting colors");
/* 215:    */     
/* 216:259 */     Arrays.sort(this.cv);
/* 217:    */     
/* 218:261 */     System.err.println("Colors sorted");
/* 219:    */     
/* 220:263 */     this.alphaIndex = rankOf(this.alpha);
/* 221:264 */     this.omegaIndex = rankOf(this.omega);
/* 222:    */   }
/* 223:    */   
/* 224:    */   private int rankOf(double[] a)
/* 225:    */   {
/* 226:268 */     int first = 0;
/* 227:269 */     int last = this.cv.length - 1;
/* 228:270 */     int mid = 0;
/* 229:    */     
/* 230:272 */     boolean found = false;
/* 231:274 */     while ((!found) && (first <= last))
/* 232:    */     {
/* 233:275 */       mid = first + (last - first) / 2;
/* 234:276 */       if (this.vo.compare(a, this.cv[mid].z) == 0) {
/* 235:276 */         found = true;
/* 236:277 */       } else if (this.vo.compare(a, this.cv[mid].z) < 0) {
/* 237:277 */         last = mid - 1;
/* 238:278 */       } else if (this.vo.compare(a, this.cv[mid].z) > 0) {
/* 239:278 */         first = mid + 1;
/* 240:    */       }
/* 241:    */     }
/* 242:281 */     return mid;
/* 243:    */   }
/* 244:    */   
/* 245:    */   private class ColorVector
/* 246:    */     implements Comparable
/* 247:    */   {
/* 248:    */     double[] z;
/* 249:    */     
/* 250:    */     ColorVector(int a, int b, int c)
/* 251:    */     {
/* 252:289 */       this.z = new double[3];
/* 253:290 */       this.z[0] = (a * 0.00392156862745098D);
/* 254:291 */       this.z[1] = (b * 0.00392156862745098D);
/* 255:292 */       this.z[2] = (c * 0.00392156862745098D);
/* 256:    */     }
/* 257:    */     
/* 258:    */     public int compareTo(Object o)
/* 259:    */     {
/* 260:296 */       ColorVector c = (ColorVector)o;
/* 261:297 */       return ColorQFZRGBNoLabelControl.this.vo.compare(this.z, c.z);
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   public static Image invoke(Image img, int[] alpha, int[] omega)
/* 266:    */   {
/* 267:    */     try
/* 268:    */     {
/* 269:304 */       return (IntegerImage)new ColorQFZRGBNoLabelControl().preprocess(new Object[] { img, alpha, omega });
/* 270:    */     }
/* 271:    */     catch (GlobalException e)
/* 272:    */     {
/* 273:306 */       e.printStackTrace();
/* 274:    */     }
/* 275:307 */     return null;
/* 276:    */   }
/* 277:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorQFZRGBNoLabelControl
 * JD-Core Version:    0.7.0.1
 */
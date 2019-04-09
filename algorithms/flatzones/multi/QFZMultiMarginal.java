/*   1:    */ package vpt.algorithms.flatzones.multi;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.BooleanImage;
/*   9:    */ import vpt.ByteImage;
/*  10:    */ import vpt.GlobalException;
/*  11:    */ import vpt.Image;
/*  12:    */ import vpt.IntegerImage;
/*  13:    */ 
/*  14:    */ public class QFZMultiMarginal
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17:    */   public Image input;
/*  18:    */   public IntegerImage output;
/*  19:    */   public int alpha;
/*  20:    */   public int omega;
/*  21: 33 */   private ArrayList<Point> list = new ArrayList();
/*  22: 34 */   private ArrayList<Point> list2 = new ArrayList();
/*  23: 35 */   private Stack<Point> s = new Stack();
/*  24:    */   private int xdim;
/*  25:    */   private int ydim;
/*  26: 38 */   private int label = 1;
/*  27: 39 */   private int[] max = null;
/*  28: 40 */   private int[] min = null;
/*  29: 41 */   private int[] diff = null;
/*  30:    */   BooleanImage temp;
/*  31:    */   Image localRanges;
/*  32: 53 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  33: 54 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  34:    */   
/*  35:    */   public QFZMultiMarginal()
/*  36:    */   {
/*  37: 57 */     this.inputFields = "input,alpha,omega";
/*  38: 58 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 63 */     this.xdim = this.input.getXDim();
/*  45: 64 */     this.ydim = this.input.getYDim();
/*  46: 66 */     if ((this.alpha < 0) || (this.alpha > 254) || (this.omega < 0) || (this.omega > 254)) {
/*  47: 67 */       throw new GlobalException("Arguments out of range.");
/*  48:    */     }
/*  49: 69 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  50: 70 */     this.output.fill(0);
/*  51:    */     
/*  52:    */ 
/*  53: 73 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  54: 74 */     this.temp.fill(false);
/*  55:    */     
/*  56: 76 */     this.localRanges = new ByteImage(this.xdim, this.ydim, 1);
/*  57: 77 */     this.localRanges.fill(255);
/*  58:    */     
/*  59: 79 */     this.max = new int[this.input.getCDim()];
/*  60: 80 */     this.min = new int[this.input.getCDim()];
/*  61: 81 */     this.diff = new int[this.input.getCDim()];
/*  62: 84 */     for (int x = 0; x < this.xdim; x++)
/*  63:    */     {
/*  64: 87 */       System.out.println("Sutun " + x);
/*  65: 89 */       for (int y = 0; y < this.ydim; y++) {
/*  66: 93 */         if (this.output.getXYInt(x, y) <= 0)
/*  67:    */         {
/*  68: 96 */           resetMaxMin();
/*  69:    */           
/*  70: 98 */           Point t = new Point(x, y);
/*  71:101 */           if (createQCC(t, this.alpha) > 0)
/*  72:    */           {
/*  73:107 */             for (Point s : this.list) {
/*  74:108 */               this.localRanges.setXYByte(s.x, s.y, this.alpha);
/*  75:    */             }
/*  76:    */           }
/*  77:    */           else
/*  78:    */           {
/*  79:118 */             int tmpAlpha = this.alpha;
/*  80:    */             do
/*  81:    */             {
/*  82:121 */               resetMaxMin();
/*  83:124 */               for (Point p : this.list) {
/*  84:125 */                 this.output.setXYInt(p.x, p.y, 0);
/*  85:    */               }
/*  86:129 */               this.list.clear();
/*  87:134 */             } while (createQCC(t, --tmpAlpha) <= 0);
/*  88:140 */             for (Point s : this.list) {
/*  89:141 */               this.localRanges.setXYByte(s.x, s.y, tmpAlpha);
/*  90:    */             }
/*  91:    */           }
/*  92:145 */           this.list.clear();
/*  93:    */           
/*  94:    */ 
/*  95:148 */           this.label += 1;
/*  96:    */         }
/*  97:    */       }
/*  98:    */     }
/*  99:152 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 100:    */   }
/* 101:    */   
/* 102:    */   private void resetMaxMin()
/* 103:    */   {
/* 104:157 */     for (int i = 0; i < this.max.length; i++)
/* 105:    */     {
/* 106:158 */       this.max[i] = 0;
/* 107:159 */       this.min[i] = 255;
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   private int createQCC(Point r, int alpha2)
/* 112:    */   {
/* 113:173 */     this.s.clear();
/* 114:174 */     this.s.add(r);
/* 115:    */     int x;
/* 116:    */     int i;
/* 117:176 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 118:    */     {
/* 119:178 */       Point tmp = (Point)this.s.pop();
/* 120:179 */       x = tmp.x;
/* 121:180 */       int y = tmp.y;
/* 122:    */       
/* 123:182 */       int[] p = this.input.getVXYByte(x, y);
/* 124:185 */       for (int i = 0; i < this.max.length; i++)
/* 125:    */       {
/* 126:186 */         this.max[i] = (this.max[i] < p[i] ? p[i] : this.max[i]);
/* 127:187 */         this.min[i] = (this.min[i] > p[i] ? p[i] : this.min[i]);
/* 128:188 */         this.diff[i] = Math.abs(this.max[i] - this.min[i]);
/* 129:    */       }
/* 130:192 */       boolean tmpFlag = true;
/* 131:194 */       for (int i = 0; i < this.diff.length; i++) {
/* 132:195 */         if (this.omega < this.diff[i]) {
/* 133:195 */           tmpFlag = false;
/* 134:    */         }
/* 135:    */       }
/* 136:198 */       if (!tmpFlag)
/* 137:    */       {
/* 138:201 */         for (Point t : this.list2) {
/* 139:202 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 140:    */         }
/* 141:203 */         this.list2.clear();
/* 142:    */         
/* 143:205 */         return -1;
/* 144:    */       }
/* 145:209 */       this.output.setXYInt(x, y, this.label);
/* 146:    */       
/* 147:    */ 
/* 148:212 */       this.list.add(tmp);
/* 149:    */       
/* 150:    */ 
/* 151:215 */       i = 0; continue;
/* 152:216 */       int _x = x + this.N[i].x;
/* 153:217 */       int _y = y + this.N[i].y;
/* 154:220 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 155:    */       {
/* 156:222 */         int[] q = this.input.getVXYByte(_x, _y);
/* 157:225 */         for (int j = 0; j < this.diff.length; j++) {
/* 158:226 */           this.diff[j] = Math.abs(p[j] - q[j]);
/* 159:    */         }
/* 160:232 */         if (this.output.getXYInt(_x, _y) > 0)
/* 161:    */         {
/* 162:234 */           int localR = this.localRanges.getXYByte(_x, _y);
/* 163:    */           
/* 164:236 */           boolean tmpFlag2 = true;
/* 165:237 */           for (int k = 0; k < this.diff.length; k++) {
/* 166:238 */             if (this.diff[k] > alpha2) {
/* 167:238 */               tmpFlag2 = false;
/* 168:    */             }
/* 169:    */           }
/* 170:241 */           if ((tmpFlag2) && (localR <= alpha2))
/* 171:    */           {
/* 172:243 */             for (Point t : this.list2) {
/* 173:244 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 174:    */             }
/* 175:245 */             this.list2.clear();
/* 176:    */             
/* 177:247 */             return -1;
/* 178:    */           }
/* 179:    */         }
/* 180:253 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 181:    */         {
/* 182:256 */           boolean tmpFlag3 = true;
/* 183:257 */           for (int k = 0; k < this.diff.length; k++) {
/* 184:258 */             if (this.diff[k] > alpha2) {
/* 185:258 */               tmpFlag3 = false;
/* 186:    */             }
/* 187:    */           }
/* 188:260 */           if (tmpFlag3)
/* 189:    */           {
/* 190:263 */             Point t = new Point(_x, _y);
/* 191:264 */             this.s.add(t);
/* 192:    */             
/* 193:    */ 
/* 194:267 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 195:268 */             this.list2.add(t);
/* 196:    */           }
/* 197:    */         }
/* 198:    */       }
/* 199:215 */       i++;
/* 200:    */     }
/* 201:274 */     for (Point t : this.list2) {
/* 202:275 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 203:    */     }
/* 204:276 */     this.list2.clear();
/* 205:    */     
/* 206:278 */     return 1;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public static Image invoke(Image img, int alpha, int omega)
/* 210:    */   {
/* 211:    */     try
/* 212:    */     {
/* 213:283 */       return (IntegerImage)new QFZMultiMarginal().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 214:    */     }
/* 215:    */     catch (GlobalException e)
/* 216:    */     {
/* 217:285 */       e.printStackTrace();
/* 218:    */     }
/* 219:286 */     return null;
/* 220:    */   }
/* 221:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.multi.QFZMultiMarginal
 * JD-Core Version:    0.7.0.1
 */
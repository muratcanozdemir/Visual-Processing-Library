/*   1:    */ package vpt.algorithms.flatzones.color;
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
/*  14:    */ public class ColorQFZRGBMarginal
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17:    */   public Image input;
/*  18:    */   public IntegerImage output;
/*  19:    */   public int alpha;
/*  20:    */   public int omega;
/*  21: 35 */   private ArrayList<Point> list = new ArrayList();
/*  22: 36 */   private ArrayList<Point> list2 = new ArrayList();
/*  23: 37 */   private Stack<Point> s = new Stack();
/*  24:    */   private int xdim;
/*  25:    */   private int ydim;
/*  26: 40 */   private int label = 1;
/*  27: 41 */   private int[] max = new int[3];
/*  28: 42 */   private int[] min = new int[3];
/*  29: 43 */   private int[] diff = new int[3];
/*  30:    */   BooleanImage temp;
/*  31:    */   Image localRanges;
/*  32: 55 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  33: 56 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  34:    */   
/*  35:    */   public ColorQFZRGBMarginal()
/*  36:    */   {
/*  37: 59 */     this.inputFields = "input,alpha,omega";
/*  38: 60 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 65 */     this.xdim = this.input.getXDim();
/*  45: 66 */     this.ydim = this.input.getYDim();
/*  46: 68 */     if (this.input.getCDim() != 3) {
/*  47: 68 */       throw new GlobalException("This implementation is only for color images.");
/*  48:    */     }
/*  49: 70 */     if ((this.alpha < 0) || (this.alpha > 254) || (this.omega < 0) || (this.omega > 254)) {
/*  50: 71 */       throw new GlobalException("Arguments out of range.");
/*  51:    */     }
/*  52: 73 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  53: 74 */     this.output.fill(0);
/*  54:    */     
/*  55:    */ 
/*  56: 77 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  57: 78 */     this.temp.fill(false);
/*  58:    */     
/*  59: 80 */     this.localRanges = new ByteImage(this.xdim, this.ydim, 1);
/*  60: 81 */     this.localRanges.fill(255);
/*  61: 84 */     for (int x = 0; x < this.xdim; x++)
/*  62:    */     {
/*  63: 87 */       System.out.println("Sutun " + x);
/*  64: 89 */       for (int y = 0; y < this.ydim; y++) {
/*  65: 93 */         if (this.output.getXYInt(x, y) <= 0)
/*  66:    */         {
/*  67: 96 */           resetMaxMin();
/*  68:    */           
/*  69: 98 */           Point t = new Point(x, y);
/*  70:101 */           if (createQCC(t, this.alpha) > 0)
/*  71:    */           {
/*  72:107 */             for (Point s : this.list) {
/*  73:108 */               this.localRanges.setXYByte(s.x, s.y, this.alpha);
/*  74:    */             }
/*  75:    */           }
/*  76:    */           else
/*  77:    */           {
/*  78:118 */             int tmpAlpha = this.alpha;
/*  79:    */             do
/*  80:    */             {
/*  81:121 */               resetMaxMin();
/*  82:124 */               for (Point p : this.list) {
/*  83:125 */                 this.output.setXYInt(p.x, p.y, 0);
/*  84:    */               }
/*  85:129 */               this.list.clear();
/*  86:134 */             } while (createQCC(t, --tmpAlpha) <= 0);
/*  87:140 */             for (Point s : this.list) {
/*  88:141 */               this.localRanges.setXYByte(s.x, s.y, tmpAlpha);
/*  89:    */             }
/*  90:    */           }
/*  91:145 */           this.list.clear();
/*  92:    */           
/*  93:    */ 
/*  94:148 */           this.label += 1;
/*  95:    */         }
/*  96:    */       }
/*  97:    */     }
/*  98:152 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/*  99:    */   }
/* 100:    */   
/* 101:    */   private void resetMaxMin()
/* 102:    */   {
/* 103:156 */     this.max[0] = 0;
/* 104:157 */     this.max[1] = 0;
/* 105:158 */     this.max[2] = 0;
/* 106:    */     
/* 107:160 */     this.min[0] = 255;
/* 108:161 */     this.min[1] = 255;
/* 109:162 */     this.min[2] = 255;
/* 110:    */   }
/* 111:    */   
/* 112:    */   private int createQCC(Point r, int alpha2)
/* 113:    */   {
/* 114:175 */     this.s.clear();
/* 115:176 */     this.s.add(r);
/* 116:    */     int x;
/* 117:    */     int i;
/* 118:178 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 119:    */     {
/* 120:180 */       Point tmp = (Point)this.s.pop();
/* 121:181 */       x = tmp.x;
/* 122:182 */       int y = tmp.y;
/* 123:    */       
/* 124:184 */       int[] p = this.input.getVXYByte(x, y);
/* 125:    */       
/* 126:    */ 
/* 127:187 */       this.max[0] = (this.max[0] < p[0] ? p[0] : this.max[0]);
/* 128:188 */       this.max[1] = (this.max[1] < p[1] ? p[1] : this.max[1]);
/* 129:189 */       this.max[2] = (this.max[2] < p[2] ? p[2] : this.max[2]);
/* 130:    */       
/* 131:191 */       this.min[0] = (this.min[0] > p[0] ? p[0] : this.min[0]);
/* 132:192 */       this.min[1] = (this.min[1] > p[1] ? p[1] : this.min[1]);
/* 133:193 */       this.min[2] = (this.min[2] > p[2] ? p[2] : this.min[2]);
/* 134:    */       
/* 135:195 */       this.diff[0] = Math.abs(this.max[0] - this.min[0]);
/* 136:196 */       this.diff[1] = Math.abs(this.max[1] - this.min[1]);
/* 137:197 */       this.diff[2] = Math.abs(this.max[2] - this.min[2]);
/* 138:200 */       if ((this.omega < this.diff[0]) || (this.omega < this.diff[1]) || (this.omega < this.diff[2]))
/* 139:    */       {
/* 140:203 */         for (Point t : this.list2) {
/* 141:204 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 142:    */         }
/* 143:205 */         this.list2.clear();
/* 144:    */         
/* 145:207 */         return -1;
/* 146:    */       }
/* 147:211 */       this.output.setXYInt(x, y, this.label);
/* 148:    */       
/* 149:    */ 
/* 150:214 */       this.list.add(tmp);
/* 151:    */       
/* 152:    */ 
/* 153:217 */       i = 0; continue;
/* 154:218 */       int _x = x + this.N[i].x;
/* 155:219 */       int _y = y + this.N[i].y;
/* 156:222 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 157:    */       {
/* 158:224 */         int[] q = this.input.getVXYByte(_x, _y);
/* 159:    */         
/* 160:    */ 
/* 161:227 */         this.diff[0] = Math.abs(p[0] - q[0]);
/* 162:228 */         this.diff[1] = Math.abs(p[1] - q[1]);
/* 163:229 */         this.diff[2] = Math.abs(p[2] - q[2]);
/* 164:234 */         if (this.output.getXYInt(_x, _y) > 0)
/* 165:    */         {
/* 166:236 */           int localR = this.localRanges.getXYByte(_x, _y);
/* 167:238 */           if ((this.diff[0] <= alpha2) && (this.diff[1] <= alpha2) && (this.diff[2] <= alpha2) && (localR <= alpha2))
/* 168:    */           {
/* 169:240 */             for (Point t : this.list2) {
/* 170:241 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 171:    */             }
/* 172:242 */             this.list2.clear();
/* 173:    */             
/* 174:244 */             return -1;
/* 175:    */           }
/* 176:    */         }
/* 177:250 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 178:    */         {
/* 179:253 */           if ((this.diff[0] <= alpha2) && (this.diff[1] <= alpha2) && (this.diff[2] <= alpha2))
/* 180:    */           {
/* 181:256 */             Point t = new Point(_x, _y);
/* 182:257 */             this.s.add(t);
/* 183:    */             
/* 184:    */ 
/* 185:260 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 186:261 */             this.list2.add(t);
/* 187:    */           }
/* 188:    */         }
/* 189:    */       }
/* 190:217 */       i++;
/* 191:    */     }
/* 192:267 */     for (Point t : this.list2) {
/* 193:268 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 194:    */     }
/* 195:269 */     this.list2.clear();
/* 196:    */     
/* 197:271 */     return 1;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static Image invoke(Image img, int alpha, int omega)
/* 201:    */   {
/* 202:    */     try
/* 203:    */     {
/* 204:276 */       return (IntegerImage)new ColorQFZRGBMarginal().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 205:    */     }
/* 206:    */     catch (GlobalException e)
/* 207:    */     {
/* 208:278 */       e.printStackTrace();
/* 209:    */     }
/* 210:279 */     return null;
/* 211:    */   }
/* 212:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZRGBMarginal
 * JD-Core Version:    0.7.0.1
 */
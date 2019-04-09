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
/*  14:    */ public class ColorQFZRGBMarginalFromMarkers
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17:    */   public Image input;
/*  18:    */   public IntegerImage output;
/*  19:    */   public int alpha;
/*  20:    */   public int omega;
/*  21:    */   public Image mask;
/*  22: 31 */   private ArrayList<Point> list = new ArrayList();
/*  23: 32 */   private ArrayList<Point> list2 = new ArrayList();
/*  24: 33 */   private Stack<Point> s = new Stack();
/*  25:    */   private int xdim;
/*  26:    */   private int ydim;
/*  27: 36 */   private int label = 1;
/*  28: 37 */   private int[] max = new int[3];
/*  29: 38 */   private int[] min = new int[3];
/*  30: 39 */   private int[] diff = new int[3];
/*  31:    */   BooleanImage temp;
/*  32:    */   Image localRanges;
/*  33: 51 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  34: 52 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  35:    */   
/*  36:    */   public ColorQFZRGBMarginalFromMarkers()
/*  37:    */   {
/*  38: 55 */     this.inputFields = "input,alpha,omega,mask";
/*  39: 56 */     this.outputFields = "output";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 61 */     this.xdim = this.input.getXDim();
/*  46: 62 */     this.ydim = this.input.getYDim();
/*  47: 64 */     if (this.input.getCDim() != 3) {
/*  48: 64 */       throw new GlobalException("This implementation is only for color images.");
/*  49:    */     }
/*  50: 66 */     if ((this.alpha < 0) || (this.alpha > 254) || (this.omega < 0) || (this.omega > 254)) {
/*  51: 67 */       throw new GlobalException("Arguments out of range.");
/*  52:    */     }
/*  53: 69 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  54: 70 */     this.output.fill(0);
/*  55:    */     
/*  56:    */ 
/*  57: 73 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  58: 74 */     this.temp.fill(false);
/*  59:    */     
/*  60: 76 */     this.localRanges = new ByteImage(this.xdim, this.ydim, 1);
/*  61: 77 */     this.localRanges.fill(255);
/*  62: 80 */     for (int x = 0; x < this.xdim; x++)
/*  63:    */     {
/*  64: 83 */       System.out.println("Sutun " + x);
/*  65: 85 */       for (int y = 0; y < this.ydim; y++) {
/*  66: 89 */         if (this.mask.getXYBoolean(x, y)) {
/*  67: 92 */           if (this.output.getXYInt(x, y) <= 0)
/*  68:    */           {
/*  69: 95 */             resetMaxMin();
/*  70:    */             
/*  71: 97 */             Point t = new Point(x, y);
/*  72:100 */             if (createQCC(t, this.alpha) > 0)
/*  73:    */             {
/*  74:106 */               for (Point s : this.list) {
/*  75:107 */                 this.localRanges.setXYByte(s.x, s.y, this.alpha);
/*  76:    */               }
/*  77:    */             }
/*  78:    */             else
/*  79:    */             {
/*  80:117 */               int tmpAlpha = this.alpha;
/*  81:    */               do
/*  82:    */               {
/*  83:120 */                 resetMaxMin();
/*  84:123 */                 for (Point p : this.list) {
/*  85:124 */                   this.output.setXYInt(p.x, p.y, 0);
/*  86:    */                 }
/*  87:128 */                 this.list.clear();
/*  88:133 */               } while (createQCC(t, --tmpAlpha) <= 0);
/*  89:139 */               for (Point s : this.list) {
/*  90:140 */                 this.localRanges.setXYByte(s.x, s.y, tmpAlpha);
/*  91:    */               }
/*  92:    */             }
/*  93:144 */             this.list.clear();
/*  94:    */           }
/*  95:    */         }
/*  96:    */       }
/*  97:    */     }
/*  98:151 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/*  99:    */   }
/* 100:    */   
/* 101:    */   private void resetMaxMin()
/* 102:    */   {
/* 103:155 */     this.max[0] = 0;
/* 104:156 */     this.max[1] = 0;
/* 105:157 */     this.max[2] = 0;
/* 106:    */     
/* 107:159 */     this.min[0] = 255;
/* 108:160 */     this.min[1] = 255;
/* 109:161 */     this.min[2] = 255;
/* 110:    */   }
/* 111:    */   
/* 112:    */   private int createQCC(Point r, int alpha2)
/* 113:    */   {
/* 114:174 */     this.s.clear();
/* 115:175 */     this.s.add(r);
/* 116:    */     int x;
/* 117:    */     int i;
/* 118:177 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 119:    */     {
/* 120:179 */       Point tmp = (Point)this.s.pop();
/* 121:180 */       x = tmp.x;
/* 122:181 */       int y = tmp.y;
/* 123:    */       
/* 124:183 */       int[] p = this.input.getVXYByte(x, y);
/* 125:    */       
/* 126:    */ 
/* 127:186 */       this.max[0] = (this.max[0] < p[0] ? p[0] : this.max[0]);
/* 128:187 */       this.max[1] = (this.max[1] < p[1] ? p[1] : this.max[1]);
/* 129:188 */       this.max[2] = (this.max[2] < p[2] ? p[2] : this.max[2]);
/* 130:    */       
/* 131:190 */       this.min[0] = (this.min[0] > p[0] ? p[0] : this.min[0]);
/* 132:191 */       this.min[1] = (this.min[1] > p[1] ? p[1] : this.min[1]);
/* 133:192 */       this.min[2] = (this.min[2] > p[2] ? p[2] : this.min[2]);
/* 134:    */       
/* 135:194 */       this.diff[0] = Math.abs(this.max[0] - this.min[0]);
/* 136:195 */       this.diff[1] = Math.abs(this.max[1] - this.min[1]);
/* 137:196 */       this.diff[2] = Math.abs(this.max[2] - this.min[2]);
/* 138:199 */       if ((this.omega < this.diff[0]) || (this.omega < this.diff[1]) || (this.omega < this.diff[2]))
/* 139:    */       {
/* 140:202 */         for (Point t : this.list2) {
/* 141:203 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 142:    */         }
/* 143:204 */         this.list2.clear();
/* 144:    */         
/* 145:206 */         return -1;
/* 146:    */       }
/* 147:210 */       this.output.setXYInt(x, y, this.label);
/* 148:    */       
/* 149:    */ 
/* 150:213 */       this.list.add(tmp);
/* 151:    */       
/* 152:    */ 
/* 153:216 */       i = 0; continue;
/* 154:217 */       int _x = x + this.N[i].x;
/* 155:218 */       int _y = y + this.N[i].y;
/* 156:221 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 157:    */       {
/* 158:223 */         int[] q = this.input.getVXYByte(_x, _y);
/* 159:    */         
/* 160:    */ 
/* 161:226 */         this.diff[0] = Math.abs(p[0] - q[0]);
/* 162:227 */         this.diff[1] = Math.abs(p[1] - q[1]);
/* 163:228 */         this.diff[2] = Math.abs(p[2] - q[2]);
/* 164:233 */         if (this.output.getXYInt(_x, _y) > 0)
/* 165:    */         {
/* 166:235 */           int localR = this.localRanges.getXYByte(_x, _y);
/* 167:237 */           if ((this.diff[0] <= alpha2) && (this.diff[1] <= alpha2) && (this.diff[2] <= alpha2) && (localR <= alpha2))
/* 168:    */           {
/* 169:239 */             for (Point t : this.list2) {
/* 170:240 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 171:    */             }
/* 172:241 */             this.list2.clear();
/* 173:    */             
/* 174:243 */             return -1;
/* 175:    */           }
/* 176:    */         }
/* 177:249 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 178:    */         {
/* 179:252 */           if ((this.diff[0] <= alpha2) && (this.diff[1] <= alpha2) && (this.diff[2] <= alpha2))
/* 180:    */           {
/* 181:255 */             Point t = new Point(_x, _y);
/* 182:256 */             this.s.add(t);
/* 183:    */             
/* 184:    */ 
/* 185:259 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 186:260 */             this.list2.add(t);
/* 187:    */           }
/* 188:    */         }
/* 189:    */       }
/* 190:216 */       i++;
/* 191:    */     }
/* 192:266 */     for (Point t : this.list2) {
/* 193:267 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 194:    */     }
/* 195:268 */     this.list2.clear();
/* 196:    */     
/* 197:270 */     return 1;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static Image invoke(Image img, int alpha, int omega, Image mask)
/* 201:    */   {
/* 202:    */     try
/* 203:    */     {
/* 204:275 */       return (IntegerImage)new ColorQFZRGBMarginalFromMarkers().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega), mask });
/* 205:    */     }
/* 206:    */     catch (GlobalException e)
/* 207:    */     {
/* 208:277 */       e.printStackTrace();
/* 209:    */     }
/* 210:278 */     return null;
/* 211:    */   }
/* 212:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorQFZRGBMarginalFromMarkers
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.algorithms.experimental.fz;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.BooleanImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ 
/*  13:    */ public class ColorQFZRGBMarginalNoLabelControl
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public IntegerImage output;
/*  18:    */   public int alpha;
/*  19:    */   public int omega;
/*  20: 39 */   private ArrayList<Point> list = new ArrayList();
/*  21: 40 */   private ArrayList<Point> list2 = new ArrayList();
/*  22: 41 */   private Stack<Point> s = new Stack();
/*  23:    */   private int xdim;
/*  24:    */   private int ydim;
/*  25: 44 */   private int label = 1;
/*  26: 45 */   private int[] max = new int[3];
/*  27: 46 */   private int[] min = new int[3];
/*  28: 47 */   private int[] diff = new int[3];
/*  29:    */   BooleanImage temp;
/*  30: 57 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  31: 58 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  32:    */   
/*  33:    */   public ColorQFZRGBMarginalNoLabelControl()
/*  34:    */   {
/*  35: 61 */     this.inputFields = "input,alpha,omega";
/*  36: 62 */     this.outputFields = "output";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void execute()
/*  40:    */     throws GlobalException
/*  41:    */   {
/*  42: 67 */     this.xdim = this.input.getXDim();
/*  43: 68 */     this.ydim = this.input.getYDim();
/*  44: 70 */     if (this.input.getCDim() != 3) {
/*  45: 70 */       throw new GlobalException("This implementation is only for color images.");
/*  46:    */     }
/*  47: 72 */     if ((this.alpha < 0) || (this.alpha > 254) || (this.omega < 0) || (this.omega > 254)) {
/*  48: 73 */       throw new GlobalException("Arguments out of range.");
/*  49:    */     }
/*  50: 75 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  51: 76 */     this.output.fill(0);
/*  52:    */     
/*  53:    */ 
/*  54: 79 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  55: 80 */     this.temp.fill(false);
/*  56: 84 */     for (int y = this.ydim - 1; y >= 0; y--)
/*  57:    */     {
/*  58: 86 */       System.out.println("Satir " + y);
/*  59: 88 */       for (int x = this.xdim - 1; x >= 0; x--) {
/*  60: 92 */         if (this.output.getXYInt(x, y) <= 0)
/*  61:    */         {
/*  62: 95 */           resetMaxMin();
/*  63:    */           
/*  64: 97 */           Point t = new Point(x, y);
/*  65:100 */           if (createQCC(t, this.alpha) > 0)
/*  66:    */           {
/*  67:106 */             for (Point s : this.list)
/*  68:    */             {
/*  69:107 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  70:107 */                 System.err.println("LAN");
/*  71:    */               }
/*  72:108 */               this.output.setXYInt(s.x, s.y, this.label);
/*  73:    */             }
/*  74:    */           }
/*  75:    */           else
/*  76:    */           {
/*  77:119 */             int tmpAlpha = this.alpha;
/*  78:    */             do
/*  79:    */             {
/*  80:122 */               resetMaxMin();
/*  81:    */               
/*  82:    */ 
/*  83:125 */               this.list.clear();
/*  84:130 */             } while (createQCC(t, --tmpAlpha) <= 0);
/*  85:132 */             for (Point s : this.list)
/*  86:    */             {
/*  87:133 */               if (this.output.getXYInt(s.x, s.y) > 0) {
/*  88:133 */                 System.err.println("LAN");
/*  89:    */               }
/*  90:134 */               this.output.setXYInt(s.x, s.y, this.label);
/*  91:    */             }
/*  92:    */           }
/*  93:144 */           this.list.clear();
/*  94:    */           
/*  95:    */ 
/*  96:147 */           this.label += 1;
/*  97:    */         }
/*  98:    */       }
/*  99:    */     }
/* 100:151 */     System.out.println("Total number of quasi flat zones: " + (this.label - 1));
/* 101:    */   }
/* 102:    */   
/* 103:    */   private void resetMaxMin()
/* 104:    */   {
/* 105:155 */     this.max[0] = 0;
/* 106:156 */     this.max[1] = 0;
/* 107:157 */     this.max[2] = 0;
/* 108:    */     
/* 109:159 */     this.min[0] = 255;
/* 110:160 */     this.min[1] = 255;
/* 111:161 */     this.min[2] = 255;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private int createQCC(Point r, int alpha2)
/* 115:    */   {
/* 116:174 */     this.s.clear();
/* 117:175 */     this.s.add(r);
/* 118:    */     int x;
/* 119:    */     int i;
/* 120:177 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 121:    */     {
/* 122:179 */       Point tmp = (Point)this.s.pop();
/* 123:180 */       x = tmp.x;
/* 124:181 */       int y = tmp.y;
/* 125:    */       
/* 126:183 */       int[] p = this.input.getVXYByte(x, y);
/* 127:    */       
/* 128:    */ 
/* 129:186 */       this.max[0] = (this.max[0] < p[0] ? p[0] : this.max[0]);
/* 130:187 */       this.max[1] = (this.max[1] < p[1] ? p[1] : this.max[1]);
/* 131:188 */       this.max[2] = (this.max[2] < p[2] ? p[2] : this.max[2]);
/* 132:    */       
/* 133:190 */       this.min[0] = (this.min[0] > p[0] ? p[0] : this.min[0]);
/* 134:191 */       this.min[1] = (this.min[1] > p[1] ? p[1] : this.min[1]);
/* 135:192 */       this.min[2] = (this.min[2] > p[2] ? p[2] : this.min[2]);
/* 136:    */       
/* 137:194 */       this.diff[0] = Math.abs(this.max[0] - this.min[0]);
/* 138:195 */       this.diff[1] = Math.abs(this.max[1] - this.min[1]);
/* 139:196 */       this.diff[2] = Math.abs(this.max[2] - this.min[2]);
/* 140:199 */       if ((this.omega < this.diff[0]) || (this.omega < this.diff[1]) || (this.omega < this.diff[2]))
/* 141:    */       {
/* 142:202 */         for (Point t : this.list2) {
/* 143:203 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 144:    */         }
/* 145:204 */         this.list2.clear();
/* 146:    */         
/* 147:206 */         return -1;
/* 148:    */       }
/* 149:210 */       this.list.add(tmp);
/* 150:    */       
/* 151:    */ 
/* 152:213 */       i = 0; continue;
/* 153:214 */       int _x = x + this.N[i].x;
/* 154:215 */       int _y = y + this.N[i].y;
/* 155:218 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 156:    */       {
/* 157:220 */         int[] q = this.input.getVXYByte(_x, _y);
/* 158:    */         
/* 159:    */ 
/* 160:223 */         this.diff[0] = Math.abs(p[0] - q[0]);
/* 161:224 */         this.diff[1] = Math.abs(p[1] - q[1]);
/* 162:225 */         this.diff[2] = Math.abs(p[2] - q[2]);
/* 163:228 */         if (!this.temp.getXYBoolean(_x, _y)) {
/* 164:231 */           if ((this.diff[0] <= alpha2) && (this.diff[1] <= alpha2) && (this.diff[2] <= alpha2))
/* 165:    */           {
/* 166:234 */             Point t = new Point(_x, _y);
/* 167:235 */             this.s.add(t);
/* 168:    */             
/* 169:    */ 
/* 170:238 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 171:239 */             this.list2.add(t);
/* 172:    */           }
/* 173:    */         }
/* 174:    */       }
/* 175:213 */       i++;
/* 176:    */     }
/* 177:245 */     for (Point t : this.list2) {
/* 178:246 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 179:    */     }
/* 180:247 */     this.list2.clear();
/* 181:    */     
/* 182:249 */     return 1;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static Image invoke(Image img, int alpha, int omega)
/* 186:    */   {
/* 187:    */     try
/* 188:    */     {
/* 189:254 */       return (IntegerImage)new ColorQFZRGBMarginalNoLabelControl().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 190:    */     }
/* 191:    */     catch (GlobalException e)
/* 192:    */     {
/* 193:256 */       e.printStackTrace();
/* 194:    */     }
/* 195:257 */     return null;
/* 196:    */   }
/* 197:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.fz.ColorQFZRGBMarginalNoLabelControl
 * JD-Core Version:    0.7.0.1
 */
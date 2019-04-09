/*   1:    */ package vpt.algorithms.flatzones.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Stack;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.ByteImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ 
/*  13:    */ public class GrayQFZ
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public IntegerImage output;
/*  18:    */   public int alpha;
/*  19:    */   public int omega;
/*  20: 30 */   private ArrayList<Point> list = new ArrayList();
/*  21: 31 */   private ArrayList<Point> list2 = new ArrayList();
/*  22: 32 */   private Stack<Point> s = new Stack();
/*  23:    */   private int xdim;
/*  24:    */   private int ydim;
/*  25: 35 */   private int label = 1;
/*  26:    */   private int max;
/*  27:    */   private int min;
/*  28:    */   private int diff;
/*  29:    */   BooleanImage temp;
/*  30:    */   ByteImage localRanges;
/*  31: 50 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  32: 51 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  33:    */   
/*  34:    */   public GrayQFZ()
/*  35:    */   {
/*  36: 54 */     this.inputFields = "input,alpha,omega";
/*  37: 55 */     this.outputFields = "output";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void execute()
/*  41:    */     throws GlobalException
/*  42:    */   {
/*  43: 60 */     this.xdim = this.input.getXDim();
/*  44: 61 */     this.ydim = this.input.getYDim();
/*  45: 63 */     if (this.input.getCDim() != 1) {
/*  46: 63 */       throw new GlobalException("This implementation is only for grayscale images.");
/*  47:    */     }
/*  48: 65 */     if ((this.alpha < 0) || (this.alpha > 255) || (this.omega < 0) || (this.omega > 255)) {
/*  49: 66 */       throw new GlobalException("Arguments out of range.");
/*  50:    */     }
/*  51: 68 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  52: 69 */     this.output.fill(0);
/*  53:    */     
/*  54:    */ 
/*  55: 72 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  56: 73 */     this.temp.fill(false);
/*  57:    */     
/*  58:    */ 
/*  59: 76 */     this.localRanges = new ByteImage(this.input, false);
/*  60: 77 */     this.localRanges.fill(255);
/*  61: 81 */     for (int x = 0; x < this.xdim; x++) {
/*  62: 86 */       for (int y = 0; y < this.ydim; y++) {
/*  63: 89 */         if (this.output.getXYInt(x, y) <= 0)
/*  64:    */         {
/*  65: 91 */           this.max = 0;
/*  66: 92 */           this.min = 255;
/*  67:    */           
/*  68: 94 */           Point t = new Point(x, y);
/*  69: 96 */           if (createQCC(t, this.alpha) > 0)
/*  70:    */           {
/*  71: 98 */             for (Point s : this.list) {
/*  72: 99 */               this.localRanges.setXYByte(s.x, s.y, this.alpha);
/*  73:    */             }
/*  74:    */           }
/*  75:    */           else
/*  76:    */           {
/*  77:102 */             int tmpAlpha = this.alpha;
/*  78:    */             do
/*  79:    */             {
/*  80:105 */               this.max = 0;
/*  81:106 */               this.min = 255;
/*  82:108 */               for (Point p : this.list) {
/*  83:109 */                 this.output.setXYInt(p.x, p.y, 0);
/*  84:    */               }
/*  85:111 */               this.list.clear();
/*  86:113 */             } while (createQCC(t, --tmpAlpha) <= 0);
/*  87:118 */             for (Point s : this.list) {
/*  88:119 */               this.localRanges.setXYByte(s.x, s.y, tmpAlpha);
/*  89:    */             }
/*  90:    */           }
/*  91:122 */           this.list.clear();
/*  92:    */           
/*  93:124 */           this.label += 1;
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   private int createQCC(Point r, int alpha2)
/* 100:    */   {
/* 101:140 */     this.s.clear();
/* 102:141 */     this.s.add(r);
/* 103:    */     int x;
/* 104:    */     int i;
/* 105:143 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 106:    */     {
/* 107:145 */       Point tmp = (Point)this.s.pop();
/* 108:    */       
/* 109:147 */       x = tmp.x;
/* 110:148 */       int y = tmp.y;
/* 111:    */       
/* 112:150 */       int p = this.input.getXYByte(x, y);
/* 113:152 */       if (this.max < p) {
/* 114:152 */         this.max = p;
/* 115:    */       }
/* 116:154 */       if (this.min > p) {
/* 117:154 */         this.min = p;
/* 118:    */       }
/* 119:156 */       this.diff = (this.max - this.min);
/* 120:158 */       if (this.omega < this.diff)
/* 121:    */       {
/* 122:160 */         for (Point t : this.list2) {
/* 123:161 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 124:    */         }
/* 125:162 */         this.list2.clear();
/* 126:    */         
/* 127:164 */         return -1;
/* 128:    */       }
/* 129:167 */       this.output.setXYInt(x, y, this.label);
/* 130:    */       
/* 131:169 */       this.list.add(tmp);
/* 132:    */       
/* 133:171 */       i = 0; continue;
/* 134:172 */       int _x = x + this.N[i].x;
/* 135:173 */       int _y = y + this.N[i].y;
/* 136:175 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 137:    */       {
/* 138:177 */         int q = this.input.getXYByte(_x, _y);
/* 139:179 */         if (this.output.getXYInt(_x, _y) > 0)
/* 140:    */         {
/* 141:181 */           int localR = this.localRanges.getXYByte(_x, _y);
/* 142:183 */           if ((Math.abs(p - q) <= alpha2) && (localR <= alpha2))
/* 143:    */           {
/* 144:185 */             for (Point t : this.list2) {
/* 145:186 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 146:    */             }
/* 147:187 */             this.list2.clear();
/* 148:    */             
/* 149:189 */             return -1;
/* 150:    */           }
/* 151:    */         }
/* 152:195 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 153:    */         {
/* 154:197 */           this.diff = Math.abs(p - q);
/* 155:199 */           if (this.diff <= alpha2)
/* 156:    */           {
/* 157:201 */             Point t = new Point(_x, _y);
/* 158:202 */             this.s.add(t);
/* 159:    */             
/* 160:204 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 161:205 */             this.list2.add(t);
/* 162:    */           }
/* 163:    */         }
/* 164:    */       }
/* 165:171 */       i++;
/* 166:    */     }
/* 167:210 */     for (Point t : this.list2) {
/* 168:211 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 169:    */     }
/* 170:212 */     this.list2.clear();
/* 171:    */     
/* 172:214 */     return 1;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static Image invoke(Image img, int alpha, int omega)
/* 176:    */   {
/* 177:    */     try
/* 178:    */     {
/* 179:219 */       return (IntegerImage)new GrayQFZ().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 180:    */     }
/* 181:    */     catch (GlobalException e)
/* 182:    */     {
/* 183:221 */       e.printStackTrace();
/* 184:    */     }
/* 185:222 */     return null;
/* 186:    */   }
/* 187:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.gray.GrayQFZ
 * JD-Core Version:    0.7.0.1
 */
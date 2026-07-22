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
/*  13:    */ public class GrayQFZLocalRanges
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public Image output;
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
/*  34:    */   public GrayQFZLocalRanges()
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
/*  48: 65 */     if ((this.alpha < 0) || (this.alpha > 254) || (this.omega < 0) || (this.omega > 254)) {
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
/*  61: 79 */     for (int x = 0; x < this.xdim; x++) {
/*  62: 84 */       for (int y = 0; y < this.ydim; y++) {
/*  63: 87 */         if (this.output.getXYInt(x, y) <= 0)
/*  64:    */         {
/*  65: 89 */           this.max = 0;
/*  66: 90 */           this.min = 255;
/*  67:    */           
/*  68: 92 */           Point t = new Point(x, y);
/*  69: 94 */           if (createQCC(t, this.alpha) > 0)
/*  70:    */           {
/*  71: 96 */             for (Point s : this.list) {
/*  72: 97 */               this.localRanges.setXYByte(s.x, s.y, this.alpha);
/*  73:    */             }
/*  74:    */           }
/*  75:    */           else
/*  76:    */           {
/*  77:100 */             int tmpAlpha = this.alpha;
/*  78:    */             do
/*  79:    */             {
/*  80:103 */               this.max = 0;
/*  81:104 */               this.min = 255;
/*  82:106 */               for (Point p : this.list) {
/*  83:107 */                 this.output.setXYInt(p.x, p.y, 0);
/*  84:    */               }
/*  85:109 */               this.list.clear();
/*  86:111 */             } while (createQCC(t, --tmpAlpha) <= 0);
/*  87:116 */             for (Point s : this.list) {
/*  88:117 */               this.localRanges.setXYByte(s.x, s.y, tmpAlpha);
/*  89:    */             }
/*  90:    */           }
/*  91:120 */           this.list.clear();
/*  92:    */           
/*  93:122 */           this.label += 1;
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:128 */     this.output = this.localRanges;
/*  98:    */   }
/*  99:    */   
/* 100:    */   private int createQCC(Point r, int alpha2)
/* 101:    */   {
/* 102:140 */     this.s.clear();
/* 103:141 */     this.s.add(r);
/* 104:    */     int x;
/* 105:    */     int i;
/* 106:143 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 107:    */     {
/* 108:145 */       Point tmp = (Point)this.s.pop();
/* 109:    */       
/* 110:147 */       x = tmp.x;
/* 111:148 */       int y = tmp.y;
/* 112:    */       
/* 113:150 */       int p = this.input.getXYByte(x, y);
/* 114:152 */       if (this.max < p) {
/* 115:152 */         this.max = p;
/* 116:    */       }
/* 117:154 */       if (this.min > p) {
/* 118:154 */         this.min = p;
/* 119:    */       }
/* 120:156 */       this.diff = (this.max - this.min);
/* 121:158 */       if (this.omega < this.diff)
/* 122:    */       {
/* 123:160 */         for (Point t : this.list2) {
/* 124:161 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 125:    */         }
/* 126:162 */         this.list2.clear();
/* 127:    */         
/* 128:164 */         return -1;
/* 129:    */       }
/* 130:167 */       this.output.setXYInt(x, y, this.label);
/* 131:    */       
/* 132:169 */       this.list.add(tmp);
/* 133:    */       
/* 134:171 */       i = 0; continue;
/* 135:172 */       int _x = x + this.N[i].x;
/* 136:173 */       int _y = y + this.N[i].y;
/* 137:175 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 138:    */       {
/* 139:177 */         int q = this.input.getXYByte(_x, _y);
/* 140:179 */         if (this.output.getXYInt(_x, _y) > 0)
/* 141:    */         {
/* 142:181 */           int localR = this.localRanges.getXYByte(_x, _y);
/* 143:183 */           if ((Math.abs(p - q) <= alpha2) && (localR <= alpha2))
/* 144:    */           {
/* 145:185 */             for (Point t : this.list2) {
/* 146:186 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 147:    */             }
/* 148:187 */             this.list2.clear();
/* 149:    */             
/* 150:189 */             return -1;
/* 151:    */           }
/* 152:    */         }
/* 153:195 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 154:    */         {
/* 155:197 */           this.diff = Math.abs(p - q);
/* 156:199 */           if (this.diff <= alpha2)
/* 157:    */           {
/* 158:201 */             Point t = new Point(_x, _y);
/* 159:202 */             this.s.add(t);
/* 160:    */             
/* 161:204 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 162:205 */             this.list2.add(t);
/* 163:    */           }
/* 164:    */         }
/* 165:    */       }
/* 166:171 */       i++;
/* 167:    */     }
/* 168:210 */     for (Point t : this.list2) {
/* 169:211 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 170:    */     }
/* 171:212 */     this.list2.clear();
/* 172:    */     
/* 173:214 */     return 1;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public static Image invoke(Image img, int alpha, int omega)
/* 177:    */   {
/* 178:    */     try
/* 179:    */     {
/* 180:219 */       return (Image)new GrayQFZLocalRanges().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega) });
/* 181:    */     }
/* 182:    */     catch (GlobalException e)
/* 183:    */     {
/* 184:221 */       e.printStackTrace();
/* 185:    */     }
/* 186:222 */     return null;
/* 187:    */   }
/* 188:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.gray.GrayQFZLocalRanges
 * JD-Core Version:    0.7.0.1
 */
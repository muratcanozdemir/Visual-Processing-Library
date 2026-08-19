/*   1:    */ package vpt.algorithms.flatzones.angular;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.Stack;
/*   8:    */ import vpt.Algorithm;
/*   9:    */ import vpt.BooleanImage;
/*  10:    */ import vpt.DoubleImage;
/*  11:    */ import vpt.GlobalException;
/*  12:    */ import vpt.Image;
/*  13:    */ import vpt.IntegerImage;
/*  14:    */ import vpt.util.Tools;
/*  15:    */ 
/*  16:    */ public class AngularQFZ2
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public Double alpha;
/*  22:    */   public Double omega;
/*  23: 36 */   private ArrayList<Point> list = new ArrayList();
/*  24: 37 */   private ArrayList<Point> list2 = new ArrayList();
/*  25: 38 */   private Stack<Point> s = new Stack();
/*  26:    */   private int xdim;
/*  27:    */   private int ydim;
/*  28: 41 */   private int label = 1;
/*  29:    */   private double angle1;
/*  30:    */   private double angle2;
/*  31:    */   BooleanImage temp;
/*  32:    */   DoubleImage localRanges;
/*  33: 52 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  34: 53 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  35:    */   
/*  36:    */   public AngularQFZ2()
/*  37:    */   {
/*  38: 56 */     this.inputFields = "input,alpha,omega";
/*  39: 57 */     this.outputFields = "output";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void execute()
/*  43:    */     throws GlobalException
/*  44:    */   {
/*  45: 62 */     this.xdim = this.input.getXDim();
/*  46: 63 */     this.ydim = this.input.getYDim();
/*  47: 65 */     if (this.input.getCDim() != 1) {
/*  48: 65 */       throw new GlobalException("This implementation is only for monochannel angular data images.");
/*  49:    */     }
/*  50: 66 */     if ((this.alpha.doubleValue() <= 0.0D) || (this.alpha.doubleValue() >= 1.0D) || (this.omega.doubleValue() <= 0.0D) || (this.omega.doubleValue() >= 1.0D)) {
/*  51: 66 */       throw new GlobalException("Arguments out of range.");
/*  52:    */     }
/*  53: 68 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  54: 69 */     this.output.fill(0);
/*  55:    */     
/*  56: 71 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  57: 72 */     this.temp.fill(false);
/*  58:    */     
/*  59: 74 */     this.localRanges = new DoubleImage(this.input, false);
/*  60: 75 */     this.localRanges.fill(1.0D);
/*  61:    */     
/*  62: 77 */     System.out.println("Computing angular qfz...");
/*  63: 80 */     for (int x = this.xdim - 1; x >= 0; x--) {
/*  64: 85 */       for (int y = this.ydim - 1; y >= 0; y--) {
/*  65: 87 */         if (this.output.getXYInt(x, y) <= 0)
/*  66:    */         {
/*  67: 89 */           Point t = new Point(x, y);
/*  68:    */           
/*  69: 91 */           this.angle1 = (this.angle2 = -1.0D);
/*  70: 93 */           if (createQCC(t, this.alpha.doubleValue()) > 0)
/*  71:    */           {
/*  72: 95 */             for (Point s : this.list) {
/*  73: 96 */               this.localRanges.setXYDouble(s.x, s.y, this.alpha.doubleValue());
/*  74:    */             }
/*  75:    */           }
/*  76:    */           else
/*  77:    */           {
/*  78: 99 */             double tmpAlpha = this.alpha.doubleValue();
/*  79:    */             do
/*  80:    */             {
/*  81:102 */               for (Point p : this.list) {
/*  82:103 */                 this.output.setXYInt(p.x, p.y, 0);
/*  83:    */               }
/*  84:105 */               this.angle1 = (this.angle2 = -1.0D);
/*  85:    */               
/*  86:107 */               this.list.clear();
/*  87:    */               
/*  88:109 */               tmpAlpha -= 0.0001D;
/*  89:111 */             } while (createQCC(t, tmpAlpha) <= 0);
/*  90:116 */             for (Point s : this.list) {
/*  91:117 */               this.localRanges.setXYDouble(s.x, s.y, tmpAlpha);
/*  92:    */             }
/*  93:    */           }
/*  94:120 */           this.list.clear();
/*  95:    */           
/*  96:122 */           this.label += 1;
/*  97:    */         }
/*  98:    */       }
/*  99:    */     }
/* 100:126 */     System.err.println("Total number of quasi flat zones: " + (this.label - 1));
/* 101:    */   }
/* 102:    */   
/* 103:    */   private int createQCC(Point r, double alpha2)
/* 104:    */   {
/* 105:139 */     this.s.clear();
/* 106:140 */     this.s.add(r);
/* 107:141 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 108:142 */     this.list2.add(r);
/* 109:    */     int x;
/* 110:    */     int i;
/* 111:144 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 112:    */     {
/* 113:146 */       Point tmp = (Point)this.s.pop();
/* 114:    */       
/* 115:148 */       x = tmp.x;
/* 116:149 */       int y = tmp.y;
/* 117:    */       
/* 118:151 */       double p = this.input.getXYDouble(x, y);
/* 119:    */       
/* 120:153 */       this.list.add(tmp);
/* 121:    */       
/* 122:    */ 
/* 123:156 */       double maxDist = 0.0D;
/* 124:    */       Iterator localIterator2;
/* 125:157 */       for (Iterator localIterator1 = this.list.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
/* 126:    */       {
/* 127:157 */         Point c = (Point)localIterator1.next();
/* 128:158 */         localIterator2 = this.list.iterator(); continue;Point d = (Point)localIterator2.next();
/* 129:159 */         double tmpDist = Tools.hueDistance(this.input.getXYDouble(c.x, c.y), this.input.getXYDouble(d.x, d.y));
/* 130:160 */         if (tmpDist > maxDist) {
/* 131:160 */           maxDist = tmpDist;
/* 132:    */         }
/* 133:    */       }
/* 134:164 */       if (maxDist > this.omega.doubleValue())
/* 135:    */       {
/* 136:166 */         for (Point t : this.list2) {
/* 137:167 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 138:    */         }
/* 139:168 */         this.list2.clear();
/* 140:    */         
/* 141:170 */         return -1;
/* 142:    */       }
/* 143:173 */       this.output.setXYInt(x, y, this.label);
/* 144:    */       
/* 145:175 */       i = 0; continue;
/* 146:176 */       int _x = x + this.N[i].x;
/* 147:177 */       int _y = y + this.N[i].y;
/* 148:179 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 149:    */       {
/* 150:181 */         double q = this.input.getXYDouble(_x, _y);
/* 151:183 */         if (this.output.getXYInt(_x, _y) > 0)
/* 152:    */         {
/* 153:185 */           double localR = this.localRanges.getXYDouble(_x, _y);
/* 154:187 */           if ((Tools.hueDistance(p, q) <= alpha2) && (localR <= alpha2))
/* 155:    */           {
/* 156:189 */             for (Point t : this.list2) {
/* 157:190 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 158:    */             }
/* 159:191 */             this.list2.clear();
/* 160:    */             
/* 161:193 */             return -1;
/* 162:    */           }
/* 163:    */         }
/* 164:199 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 165:    */         {
/* 166:201 */           if (Tools.hueDistance(p, q) <= alpha2)
/* 167:    */           {
/* 168:203 */             Point t = new Point(_x, _y);
/* 169:204 */             this.s.add(t);
/* 170:    */             
/* 171:206 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 172:207 */             this.list2.add(t);
/* 173:    */           }
/* 174:    */         }
/* 175:    */       }
/* 176:175 */       i++;
/* 177:    */     }
/* 178:212 */     for (Point t : this.list2) {
/* 179:213 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 180:    */     }
/* 181:214 */     this.list2.clear();
/* 182:    */     
/* 183:216 */     return 1;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public static Image invoke(Image img, double alpha, double omega)
/* 187:    */   {
/* 188:    */     try
/* 189:    */     {
/* 190:221 */       return (IntegerImage)new AngularQFZ2().preprocess(new Object[] { img, Double.valueOf(alpha), Double.valueOf(omega) });
/* 191:    */     }
/* 192:    */     catch (GlobalException e)
/* 193:    */     {
/* 194:223 */       e.printStackTrace();
/* 195:    */     }
/* 196:224 */     return null;
/* 197:    */   }
/* 198:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.angular.AngularQFZ2
 * JD-Core Version:    0.7.0.1
 */
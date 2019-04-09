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
/*  16:    */ public class AngularQFZ
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19:    */   public Image input;
/*  20:    */   public IntegerImage output;
/*  21:    */   public Double alpha;
/*  22:    */   public Double omega;
/*  23: 31 */   private ArrayList<Point> list = new ArrayList();
/*  24: 32 */   private ArrayList<Point> list2 = new ArrayList();
/*  25: 33 */   private Stack<Point> s = new Stack();
/*  26:    */   private int xdim;
/*  27:    */   private int ydim;
/*  28: 36 */   private int label = 1;
/*  29: 38 */   private ArrayList[] angles = new ArrayList[360];
/*  30:    */   BooleanImage temp;
/*  31:    */   DoubleImage localRanges;
/*  32: 47 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  33: 48 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  34:    */   
/*  35:    */   public AngularQFZ()
/*  36:    */   {
/*  37: 51 */     this.inputFields = "input,alpha,omega";
/*  38: 52 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 57 */     this.xdim = this.input.getXDim();
/*  45: 58 */     this.ydim = this.input.getYDim();
/*  46: 60 */     if (this.input.getCDim() != 1) {
/*  47: 60 */       throw new GlobalException("This implementation is only for monochannel angular data images.");
/*  48:    */     }
/*  49: 61 */     if ((this.alpha.doubleValue() <= 0.0D) || (this.alpha.doubleValue() >= 1.0D) || (this.omega.doubleValue() <= 0.0D) || (this.omega.doubleValue() >= 1.0D)) {
/*  50: 61 */       throw new GlobalException("Arguments out of range.");
/*  51:    */     }
/*  52: 63 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  53: 64 */     this.output.fill(0);
/*  54:    */     
/*  55: 66 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  56: 67 */     this.temp.fill(false);
/*  57:    */     
/*  58: 69 */     this.localRanges = new DoubleImage(this.input, false);
/*  59: 70 */     this.localRanges.fill(1.0D);
/*  60: 72 */     for (int k = 0; k < this.angles.length; k++) {
/*  61: 73 */       this.angles[k] = new ArrayList();
/*  62:    */     }
/*  63: 93 */     for (int x = this.xdim - 1; x >= 0; x--)
/*  64:    */     {
/*  65: 95 */       System.out.println("Sutun " + x);
/*  66: 98 */       for (int y = this.ydim - 1; y >= 0; y--) {
/*  67:100 */         if (this.output.getXYInt(x, y) <= 0)
/*  68:    */         {
/*  69:102 */           Point t = new Point(x, y);
/*  70:105 */           for (int k = 0; k < this.angles.length; k++) {
/*  71:106 */             this.angles[k].clear();
/*  72:    */           }
/*  73:108 */           if (createQCC(t, this.alpha.doubleValue()) > 0)
/*  74:    */           {
/*  75:110 */             for (Point s : this.list) {
/*  76:111 */               this.localRanges.setXYDouble(s.x, s.y, this.alpha.doubleValue());
/*  77:    */             }
/*  78:    */           }
/*  79:    */           else
/*  80:    */           {
/*  81:114 */             double tmpAlpha = this.alpha.doubleValue();
/*  82:    */             do
/*  83:    */             {
/*  84:117 */               for (Point p : this.list) {
/*  85:118 */                 this.output.setXYInt(p.x, p.y, 0);
/*  86:    */               }
/*  87:121 */               for (int k = 0; k < this.angles.length; k++) {
/*  88:122 */                 this.angles[k].clear();
/*  89:    */               }
/*  90:124 */               this.list.clear();
/*  91:    */               
/*  92:126 */               tmpAlpha -= 0.001D;
/*  93:128 */             } while (createQCC(t, tmpAlpha) <= 0);
/*  94:133 */             for (Point s : this.list) {
/*  95:134 */               this.localRanges.setXYDouble(s.x, s.y, tmpAlpha);
/*  96:    */             }
/*  97:    */           }
/*  98:137 */           this.list.clear();
/*  99:    */           
/* 100:139 */           this.label += 1;
/* 101:    */         }
/* 102:    */       }
/* 103:    */     }
/* 104:143 */     System.err.println("Total number of quasi flat zones: " + (this.label - 1));
/* 105:    */   }
/* 106:    */   
/* 107:    */   private int createQCC(Point r, double alpha2)
/* 108:    */   {
/* 109:156 */     this.s.clear();
/* 110:157 */     this.s.add(r);
/* 111:158 */     this.temp.setXYBoolean(r.x, r.y, true);
/* 112:159 */     this.list2.add(r);
/* 113:    */     
/* 114:161 */     double maxDist = 0.0D;
/* 115:    */     int x;
/* 116:    */     int i;
/* 117:163 */     for (; !this.s.isEmpty(); i < this.N.length)
/* 118:    */     {
/* 119:165 */       Point tmp = (Point)this.s.pop();
/* 120:    */       
/* 121:167 */       x = tmp.x;
/* 122:168 */       int y = tmp.y;
/* 123:    */       
/* 124:170 */       double p = this.input.getXYDouble(x, y);
/* 125:171 */       this.list.add(tmp);
/* 126:    */       
/* 127:173 */       int index = (int)Math.floor(p * 360.0D);
/* 128:174 */       int antiIndex = (index + 180) % 360;
/* 129:    */       
/* 130:176 */       this.angles[index].add(Double.valueOf(p));
/* 131:178 */       for (int k = antiIndex;; k = (k + 1) % 360) {
/* 132:180 */         if (!this.angles[k].isEmpty())
/* 133:    */         {
/* 134:182 */           for (Object o : this.angles[k])
/* 135:    */           {
/* 136:183 */             double h = ((Double)o).doubleValue();
/* 137:184 */             double tmpDist = Tools.hueDistance(h, p);
/* 138:185 */             if (tmpDist > maxDist) {
/* 139:185 */               maxDist = tmpDist;
/* 140:    */             }
/* 141:    */           }
/* 142:188 */           break;
/* 143:    */         }
/* 144:    */       }
/* 145:    */       Object o;
/* 146:192 */       for (int k = antiIndex - 1;; k--)
/* 147:    */       {
/* 148:194 */         if (k < 0) {
/* 149:194 */           k += 360;
/* 150:    */         }
/* 151:197 */         if (!this.angles[k].isEmpty())
/* 152:    */         {
/* 153:199 */           for (??? = this.angles[k].iterator(); ???.hasNext();)
/* 154:    */           {
/* 155:199 */             o = ???.next();
/* 156:200 */             double h = ((Double)o).doubleValue();
/* 157:201 */             double tmpDist = Tools.hueDistance(h, p);
/* 158:202 */             if (tmpDist > maxDist) {
/* 159:202 */               maxDist = tmpDist;
/* 160:    */             }
/* 161:    */           }
/* 162:205 */           break;
/* 163:    */         }
/* 164:    */       }
/* 165:210 */       if (maxDist > this.omega.doubleValue())
/* 166:    */       {
/* 167:211 */         for (Point t : this.list2) {
/* 168:212 */           this.temp.setXYBoolean(t.x, t.y, false);
/* 169:    */         }
/* 170:213 */         this.list2.clear();
/* 171:    */         
/* 172:215 */         return -1;
/* 173:    */       }
/* 174:218 */       this.output.setXYInt(x, y, this.label);
/* 175:    */       
/* 176:220 */       i = 0; continue;
/* 177:221 */       int _x = x + this.N[i].x;
/* 178:222 */       int _y = y + this.N[i].y;
/* 179:224 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim))
/* 180:    */       {
/* 181:226 */         double q = this.input.getXYDouble(_x, _y);
/* 182:228 */         if (this.output.getXYInt(_x, _y) > 0)
/* 183:    */         {
/* 184:230 */           double localR = this.localRanges.getXYDouble(_x, _y);
/* 185:232 */           if ((Tools.hueDistance(p, q) <= alpha2) && (localR <= alpha2))
/* 186:    */           {
/* 187:234 */             for (Point t : this.list2) {
/* 188:235 */               this.temp.setXYBoolean(t.x, t.y, false);
/* 189:    */             }
/* 190:236 */             this.list2.clear();
/* 191:    */             
/* 192:238 */             return -1;
/* 193:    */           }
/* 194:    */         }
/* 195:244 */         else if (!this.temp.getXYBoolean(_x, _y))
/* 196:    */         {
/* 197:246 */           if (Tools.hueDistance(p, q) <= alpha2)
/* 198:    */           {
/* 199:248 */             Point t = new Point(_x, _y);
/* 200:249 */             this.s.add(t);
/* 201:    */             
/* 202:251 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 203:252 */             this.list2.add(t);
/* 204:    */           }
/* 205:    */         }
/* 206:    */       }
/* 207:220 */       i++;
/* 208:    */     }
/* 209:257 */     for (Point t : this.list2) {
/* 210:258 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 211:    */     }
/* 212:259 */     this.list2.clear();
/* 213:    */     
/* 214:261 */     return 1;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public static Image invoke(Image img, double alpha, double omega)
/* 218:    */   {
/* 219:    */     try
/* 220:    */     {
/* 221:266 */       return (IntegerImage)new AngularQFZ().preprocess(new Object[] { img, Double.valueOf(alpha), Double.valueOf(omega) });
/* 222:    */     }
/* 223:    */     catch (GlobalException e)
/* 224:    */     {
/* 225:268 */       e.printStackTrace();
/* 226:    */     }
/* 227:269 */     return null;
/* 228:    */   }
/* 229:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.angular.AngularQFZ
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.algorithms.mm.multi.geo;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.Vector;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.util.ordering.AlgebraicOrdering;
/*   9:    */ 
/*  10:    */ public class MFastReconstruction
/*  11:    */   extends Algorithm
/*  12:    */ {
/*  13: 22 */   public static int CONNEXITY4 = 0;
/*  14: 23 */   public static int CONNEXITY8 = 1;
/*  15:    */   public Image marker;
/*  16:    */   public Image mask;
/*  17: 27 */   public int connexity = CONNEXITY8;
/*  18: 28 */   public boolean inverse = false;
/*  19:    */   public Image output;
/*  20: 30 */   public AlgebraicOrdering or = null;
/*  21:    */   private int xdim;
/*  22:    */   private int ydim;
/*  23:    */   
/*  24:    */   public MFastReconstruction()
/*  25:    */   {
/*  26: 40 */     this.inputFields = "marker,mask,connexity,inverse,or";
/*  27: 41 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 45 */     this.output = this.marker.newInstance(true);
/*  34:    */     
/*  35: 47 */     this.xdim = this.marker.getXDim();
/*  36: 48 */     this.ydim = this.marker.getYDim();
/*  37:    */     
/*  38: 50 */     Fifo f = new Fifo();
/*  39: 51 */     Point p = new Point();
/*  40: 54 */     for (int y = 0; y < this.ydim; y++) {
/*  41: 55 */       for (int x = 0; x < this.xdim; x++) {
/*  42: 56 */         if (!this.inverse) {
/*  43: 57 */           this.output.setVXYDouble(x, y, this.or.min(minForward(x, y), this.mask.getVXYDouble(x, y)));
/*  44:    */         } else {
/*  45: 59 */           this.output.setVXYDouble(x, y, this.or.max(maxForward(x, y), this.mask.getVXYDouble(x, y)));
/*  46:    */         }
/*  47:    */       }
/*  48:    */     }
/*  49: 62 */     for (int y = this.ydim - 1; y >= 0; y--) {
/*  50: 63 */       for (int x = this.xdim - 1; x >= 0; x--)
/*  51:    */       {
/*  52: 64 */         if (!this.inverse) {
/*  53: 65 */           this.output.setVXYDouble(x, y, this.or.min(minBackward(x, y), this.mask.getVXYDouble(x, y)));
/*  54:    */         } else {
/*  55: 67 */           this.output.setVXYDouble(x, y, this.or.max(maxBackward(x, y), this.mask.getVXYDouble(x, y)));
/*  56:    */         }
/*  57: 68 */         if (updateFifo(x, y, this.inverse)) {
/*  58: 69 */           f.add(new Point(x, y));
/*  59:    */         }
/*  60:    */       }
/*  61:    */     }
/*  62: 73 */     while (!f.isEmpty())
/*  63:    */     {
/*  64: 74 */       p = f.retrieve();
/*  65: 75 */       checkNeighbours(p.x, p.y, f, this.output.getVXYDouble(p.x, p.y), this.inverse);
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   private double[] maxForward(int x, int y)
/*  70:    */   {
/*  71: 80 */     double[] val = this.output.getVXYDouble(x, y);
/*  72: 82 */     if (x > 0) {
/*  73: 82 */       val = this.or.min(val, this.output.getVXYDouble(x - 1, y));
/*  74:    */     }
/*  75: 84 */     if (y > 0) {
/*  76: 84 */       val = this.or.min(val, this.output.getVXYDouble(x, y - 1));
/*  77:    */     }
/*  78: 86 */     if (this.connexity == CONNEXITY4) {
/*  79: 86 */       return val;
/*  80:    */     }
/*  81: 88 */     if ((x > 0) && (y > 0)) {
/*  82: 88 */       val = this.or.min(val, this.output.getVXYDouble(x - 1, y - 1));
/*  83:    */     }
/*  84: 90 */     if ((x < this.xdim - 1) && (y > 0)) {
/*  85: 90 */       val = this.or.min(val, this.output.getVXYDouble(x + 1, y - 1));
/*  86:    */     }
/*  87: 92 */     return val;
/*  88:    */   }
/*  89:    */   
/*  90:    */   private double[] maxBackward(int x, int y)
/*  91:    */   {
/*  92: 96 */     double[] val = this.output.getVXYDouble(x, y);
/*  93: 98 */     if (x < this.xdim - 1) {
/*  94: 98 */       val = this.or.min(val, this.output.getVXYDouble(x + 1, y));
/*  95:    */     }
/*  96:100 */     if (y < this.ydim - 1) {
/*  97:100 */       val = this.or.min(val, this.output.getVXYDouble(x, y + 1));
/*  98:    */     }
/*  99:102 */     if (this.connexity == CONNEXITY4) {
/* 100:102 */       return val;
/* 101:    */     }
/* 102:104 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 103:104 */       val = this.or.min(val, this.output.getVXYDouble(x + 1, y + 1));
/* 104:    */     }
/* 105:106 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 106:106 */       val = this.or.min(val, this.output.getVXYDouble(x - 1, y + 1));
/* 107:    */     }
/* 108:108 */     return val;
/* 109:    */   }
/* 110:    */   
/* 111:    */   private double[] minForward(int x, int y)
/* 112:    */   {
/* 113:112 */     double[] val = this.output.getVXYDouble(x, y);
/* 114:114 */     if (x > 0) {
/* 115:114 */       val = this.or.max(val, this.output.getVXYDouble(x - 1, y));
/* 116:    */     }
/* 117:116 */     if (y > 0) {
/* 118:116 */       val = this.or.max(val, this.output.getVXYDouble(x, y - 1));
/* 119:    */     }
/* 120:118 */     if (this.connexity == CONNEXITY4) {
/* 121:118 */       return val;
/* 122:    */     }
/* 123:120 */     if ((x > 0) && (y > 0)) {
/* 124:120 */       this.or.max(val, this.output.getVXYDouble(x - 1, y - 1));
/* 125:    */     }
/* 126:122 */     if ((x < this.xdim - 1) && (y > 0)) {
/* 127:122 */       this.or.max(val, this.output.getVXYDouble(x + 1, y - 1));
/* 128:    */     }
/* 129:124 */     return val;
/* 130:    */   }
/* 131:    */   
/* 132:    */   private double[] minBackward(int x, int y)
/* 133:    */   {
/* 134:128 */     double[] val = this.output.getVXYDouble(x, y);
/* 135:130 */     if (x < this.xdim - 1) {
/* 136:130 */       val = this.or.max(val, this.output.getVXYDouble(x + 1, y));
/* 137:    */     }
/* 138:132 */     if (y < this.ydim - 1) {
/* 139:132 */       val = this.or.max(val, this.output.getVXYDouble(x, y + 1));
/* 140:    */     }
/* 141:134 */     if (this.connexity == CONNEXITY4) {
/* 142:134 */       return val;
/* 143:    */     }
/* 144:136 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 145:136 */       val = this.or.max(val, this.output.getVXYDouble(x + 1, y + 1));
/* 146:    */     }
/* 147:138 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 148:138 */       val = this.or.max(val, this.output.getVXYDouble(x - 1, y + 1));
/* 149:    */     }
/* 150:140 */     return val;
/* 151:    */   }
/* 152:    */   
/* 153:    */   private boolean checkFifo(int x, int y, double[] val, boolean inverse)
/* 154:    */   {
/* 155:144 */     if (!inverse) {
/* 156:144 */       return (this.or.compare(this.output.getVXYDouble(x, y), val) < 0) && (this.or.compare(this.output.getVXYDouble(x, y), this.mask.getVXYDouble(x, y)) < 0);
/* 157:    */     }
/* 158:145 */     return (this.or.compare(this.output.getVXYDouble(x, y), val) > 0) && (this.or.compare(this.output.getVXYDouble(x, y), this.mask.getVXYDouble(x, y)) > 0);
/* 159:    */   }
/* 160:    */   
/* 161:    */   private boolean updateFifo(int x, int y, boolean inverse)
/* 162:    */   {
/* 163:149 */     double[] val = this.output.getVXYDouble(x, y);
/* 164:151 */     if ((x < this.xdim - 1) && (checkFifo(x + 1, y, val, inverse))) {
/* 165:151 */       return true;
/* 166:    */     }
/* 167:153 */     if ((y < this.ydim - 1) && (checkFifo(x, y + 1, val, inverse))) {
/* 168:153 */       return true;
/* 169:    */     }
/* 170:155 */     if (this.connexity == CONNEXITY4) {
/* 171:155 */       return false;
/* 172:    */     }
/* 173:157 */     if ((x < this.xdim - 1) && (y < this.ydim - 1) && (checkFifo(x + 1, y + 1, val, inverse))) {
/* 174:157 */       return true;
/* 175:    */     }
/* 176:159 */     if ((x > 0) && (y < this.ydim - 1) && (checkFifo(x - 1, y + 1, val, inverse))) {
/* 177:159 */       return true;
/* 178:    */     }
/* 179:161 */     return false;
/* 180:    */   }
/* 181:    */   
/* 182:    */   private void checkNeighbours(int x, int y, Fifo f, double[] val, boolean inverse)
/* 183:    */   {
/* 184:165 */     if (x > 0) {
/* 185:165 */       checkPixel(x - 1, y, f, val, inverse);
/* 186:    */     }
/* 187:166 */     if (y > 0) {
/* 188:166 */       checkPixel(x, y - 1, f, val, inverse);
/* 189:    */     }
/* 190:167 */     if (x < this.xdim - 1) {
/* 191:167 */       checkPixel(x + 1, y, f, val, inverse);
/* 192:    */     }
/* 193:168 */     if (y < this.ydim - 1) {
/* 194:168 */       checkPixel(x, y + 1, f, val, inverse);
/* 195:    */     }
/* 196:169 */     if (this.connexity == CONNEXITY4) {
/* 197:169 */       return;
/* 198:    */     }
/* 199:170 */     if ((x > 0) && (y > 0)) {
/* 200:170 */       checkPixel(x - 1, y - 1, f, val, inverse);
/* 201:    */     }
/* 202:171 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 203:171 */       checkPixel(x - 1, y + 1, f, val, inverse);
/* 204:    */     }
/* 205:172 */     if ((x < this.xdim - 1) && (y > 0)) {
/* 206:172 */       checkPixel(x + 1, y - 1, f, val, inverse);
/* 207:    */     }
/* 208:173 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 209:173 */       checkPixel(x + 1, y + 1, f, val, inverse);
/* 210:    */     }
/* 211:    */   }
/* 212:    */   
/* 213:    */   private void checkPixel(int x, int y, Fifo f, double[] val, boolean inverse)
/* 214:    */   {
/* 215:177 */     if ((!inverse) && (this.or.compare(this.output.getVXYDouble(x, y), val) < 0) && (this.or.compare(this.mask.getVXYDouble(x, y), this.output.getVXYDouble(x, y)) != 0))
/* 216:    */     {
/* 217:178 */       this.output.setVXYDouble(x, y, this.or.min(val, this.mask.getVXYDouble(x, y)));
/* 218:179 */       f.add(new Point(x, y));
/* 219:    */     }
/* 220:180 */     else if ((inverse) && (this.or.compare(this.output.getVXYDouble(x, y), val) > 0) && (this.or.compare(this.mask.getVXYDouble(x, y), this.output.getVXYDouble(x, y)) != 0))
/* 221:    */     {
/* 222:181 */       this.output.setVXYDouble(x, y, this.or.max(val, this.mask.getVXYDouble(x, y)));
/* 223:182 */       f.add(new Point(x, y));
/* 224:    */     }
/* 225:    */   }
/* 226:    */   
/* 227:    */   private class Fifo
/* 228:    */   {
/* 229:    */     private Vector<Point> v;
/* 230:    */     
/* 231:    */     Fifo()
/* 232:    */     {
/* 233:190 */       this.v = new Vector();
/* 234:    */     }
/* 235:    */     
/* 236:    */     void add(Point p)
/* 237:    */     {
/* 238:194 */       this.v.add(p);
/* 239:    */     }
/* 240:    */     
/* 241:    */     Point retrieve()
/* 242:    */     {
/* 243:198 */       Object o = this.v.firstElement();
/* 244:199 */       this.v.remove(0);
/* 245:    */       
/* 246:201 */       return (Point)o;
/* 247:    */     }
/* 248:    */     
/* 249:    */     boolean isEmpty()
/* 250:    */     {
/* 251:205 */       return this.v.size() == 0;
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public static Image invoke(Image marker, Image mask, int connexity, boolean flag, AlgebraicOrdering or)
/* 256:    */   {
/* 257:    */     try
/* 258:    */     {
/* 259:211 */       return (Image)new MFastReconstruction().preprocess(new Object[] { marker, mask, Integer.valueOf(connexity), Boolean.valueOf(flag), or });
/* 260:    */     }
/* 261:    */     catch (GlobalException e)
/* 262:    */     {
/* 263:213 */       e.printStackTrace();
/* 264:    */     }
/* 265:214 */     return null;
/* 266:    */   }
/* 267:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MFastReconstruction
 * JD-Core Version:    0.7.0.1
 */
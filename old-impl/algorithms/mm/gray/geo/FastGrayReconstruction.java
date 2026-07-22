/*   1:    */ package vpt.algorithms.mm.gray.geo;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.Vector;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ 
/*   9:    */ public class FastGrayReconstruction
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 21 */   public static int CONNEXITY4 = 0;
/*  13: 22 */   public static int CONNEXITY8 = 1;
/*  14:    */   public Image marker;
/*  15:    */   public Image mask;
/*  16: 26 */   public int connexity = CONNEXITY8;
/*  17: 27 */   public boolean inverse = false;
/*  18:    */   public Image output;
/*  19:    */   private int xdim;
/*  20:    */   private int ydim;
/*  21:    */   private int cdim;
/*  22:    */   
/*  23:    */   public FastGrayReconstruction()
/*  24:    */   {
/*  25: 39 */     this.inputFields = "marker,mask,connexity,inverse";
/*  26: 40 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 44 */     this.output = this.marker.newInstance(true);
/*  33:    */     
/*  34: 46 */     this.xdim = this.marker.getXDim();
/*  35: 47 */     this.ydim = this.marker.getYDim();
/*  36: 48 */     this.cdim = this.marker.getCDim();
/*  37: 49 */     Fifo f = new Fifo();
/*  38: 50 */     Point p = new Point();
/*  39: 52 */     for (int c = 0; c < this.cdim; c++)
/*  40:    */     {
/*  41: 54 */       for (int y = 0; y < this.ydim; y++) {
/*  42: 55 */         for (int x = 0; x < this.xdim; x++) {
/*  43: 56 */           if (!this.inverse) {
/*  44: 57 */             this.output.setXYCByte(x, y, c, Math.min(minForward(x, y, c), this.mask.getXYCByte(x, y, c)));
/*  45:    */           } else {
/*  46: 59 */             this.output.setXYCByte(x, y, c, Math.max(maxForward(x, y, c), this.mask.getXYCByte(x, y, c)));
/*  47:    */           }
/*  48:    */         }
/*  49:    */       }
/*  50: 62 */       for (int y = this.ydim - 1; y >= 0; y--) {
/*  51: 63 */         for (int x = this.xdim - 1; x >= 0; x--)
/*  52:    */         {
/*  53: 64 */           if (!this.inverse) {
/*  54: 65 */             this.output.setXYCByte(x, y, c, Math.min(minBackward(x, y, c), this.mask.getXYCByte(x, y, c)));
/*  55:    */           } else {
/*  56: 67 */             this.output.setXYCByte(x, y, c, Math.max(maxBackward(x, y, c), this.mask.getXYCByte(x, y, c)));
/*  57:    */           }
/*  58: 68 */           if (updateFifo(x, y, c, this.inverse)) {
/*  59: 69 */             f.add(new Point(x, y));
/*  60:    */           }
/*  61:    */         }
/*  62:    */       }
/*  63: 73 */       while (!f.isEmpty())
/*  64:    */       {
/*  65: 74 */         p = f.retrieve();
/*  66: 75 */         checkNeighbours(p.x, p.y, c, f, this.output.getXYCByte(p.x, p.y, c), this.inverse);
/*  67:    */       }
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   private int maxForward(int x, int y, int c)
/*  72:    */   {
/*  73: 81 */     int val = this.output.getXYCByte(x, y, c);
/*  74: 83 */     if (x > 0) {
/*  75: 83 */       val = Math.min(val, this.output.getXYCByte(x - 1, y, c));
/*  76:    */     }
/*  77: 85 */     if (y > 0) {
/*  78: 85 */       val = Math.min(val, this.output.getXYCByte(x, y - 1, c));
/*  79:    */     }
/*  80: 87 */     if (this.connexity == CONNEXITY4) {
/*  81: 87 */       return val;
/*  82:    */     }
/*  83: 89 */     if ((x > 0) && (y > 0)) {
/*  84: 89 */       val = Math.min(val, this.output.getXYCByte(x - 1, y - 1, c));
/*  85:    */     }
/*  86: 91 */     if ((x < this.xdim - 1) && (y > 0)) {
/*  87: 91 */       val = Math.min(val, this.output.getXYCByte(x + 1, y - 1, c));
/*  88:    */     }
/*  89: 93 */     return val;
/*  90:    */   }
/*  91:    */   
/*  92:    */   private int maxBackward(int x, int y, int c)
/*  93:    */   {
/*  94: 97 */     int val = this.output.getXYCByte(x, y, c);
/*  95: 99 */     if (x < this.xdim - 1) {
/*  96:100 */       val = Math.min(val, this.output.getXYCByte(x + 1, y, c));
/*  97:    */     }
/*  98:102 */     if (y < this.ydim - 1) {
/*  99:102 */       val = Math.min(val, this.output.getXYCByte(x, y + 1, c));
/* 100:    */     }
/* 101:104 */     if (this.connexity == CONNEXITY4) {
/* 102:104 */       return val;
/* 103:    */     }
/* 104:106 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 105:106 */       val = Math.min(val, this.output.getXYCByte(x + 1, y + 1, c));
/* 106:    */     }
/* 107:108 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 108:108 */       val = Math.min(val, this.output.getXYCByte(x - 1, y + 1, c));
/* 109:    */     }
/* 110:110 */     return val;
/* 111:    */   }
/* 112:    */   
/* 113:    */   private int minForward(int x, int y, int c)
/* 114:    */   {
/* 115:114 */     int val = this.output.getXYCByte(x, y, c);
/* 116:116 */     if (x > 0) {
/* 117:116 */       val = Math.max(val, this.output.getXYCByte(x - 1, y, c));
/* 118:    */     }
/* 119:118 */     if (y > 0) {
/* 120:118 */       val = Math.max(val, this.output.getXYCByte(x, y - 1, c));
/* 121:    */     }
/* 122:120 */     if (this.connexity == CONNEXITY4) {
/* 123:120 */       return val;
/* 124:    */     }
/* 125:122 */     if ((x > 0) && (y > 0)) {
/* 126:122 */       val = Math.max(val, this.output.getXYCByte(x - 1, y - 1, c));
/* 127:    */     }
/* 128:124 */     if ((x < this.xdim - 1) && (y > 0)) {
/* 129:124 */       val = Math.max(val, this.output.getXYCByte(x + 1, y - 1, c));
/* 130:    */     }
/* 131:126 */     return val;
/* 132:    */   }
/* 133:    */   
/* 134:    */   private int minBackward(int x, int y, int c)
/* 135:    */   {
/* 136:130 */     int val = this.output.getXYCByte(x, y, c);
/* 137:132 */     if (x < this.xdim - 1) {
/* 138:132 */       val = Math.max(val, this.output.getXYCByte(x + 1, y, c));
/* 139:    */     }
/* 140:134 */     if (y < this.ydim - 1) {
/* 141:134 */       val = Math.max(val, this.output.getXYCByte(x, y + 1, c));
/* 142:    */     }
/* 143:136 */     if (this.connexity == CONNEXITY4) {
/* 144:136 */       return val;
/* 145:    */     }
/* 146:138 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 147:138 */       val = Math.max(val, this.output.getXYCByte(x + 1, y + 1, c));
/* 148:    */     }
/* 149:140 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 150:140 */       val = Math.max(val, this.output.getXYCByte(x - 1, y + 1, c));
/* 151:    */     }
/* 152:142 */     return val;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private boolean checkFifo(int x, int y, int c, int val, boolean inverse)
/* 156:    */   {
/* 157:146 */     if (!inverse) {
/* 158:146 */       return (this.output.getXYCByte(x, y, c) < val) && (this.output.getXYCByte(x, y, c) < this.mask.getXYCByte(x, y, c));
/* 159:    */     }
/* 160:147 */     return (this.output.getXYCByte(x, y, c) > val) && (this.output.getXYCByte(x, y, c) > this.mask.getXYCByte(x, y, c));
/* 161:    */   }
/* 162:    */   
/* 163:    */   private boolean updateFifo(int x, int y, int c, boolean inverse)
/* 164:    */   {
/* 165:151 */     int val = this.output.getXYCByte(x, y, c);
/* 166:153 */     if ((x < this.xdim - 1) && (checkFifo(x + 1, y, c, val, inverse))) {
/* 167:153 */       return true;
/* 168:    */     }
/* 169:155 */     if ((y < this.ydim - 1) && (checkFifo(x, y + 1, c, val, inverse))) {
/* 170:155 */       return true;
/* 171:    */     }
/* 172:157 */     if (this.connexity == CONNEXITY4) {
/* 173:157 */       return false;
/* 174:    */     }
/* 175:159 */     if ((x < this.xdim - 1) && (y < this.ydim - 1) && (checkFifo(x + 1, y + 1, c, val, inverse))) {
/* 176:159 */       return true;
/* 177:    */     }
/* 178:161 */     if ((x > 0) && (y < this.ydim - 1) && (checkFifo(x - 1, y + 1, c, val, inverse))) {
/* 179:161 */       return true;
/* 180:    */     }
/* 181:163 */     return false;
/* 182:    */   }
/* 183:    */   
/* 184:    */   private void checkNeighbours(int x, int y, int c, Fifo f, int val, boolean inverse)
/* 185:    */   {
/* 186:167 */     if (x > 0) {
/* 187:168 */       checkPixel(x - 1, y, c, f, val, inverse);
/* 188:    */     }
/* 189:169 */     if (y > 0) {
/* 190:170 */       checkPixel(x, y - 1, c, f, val, inverse);
/* 191:    */     }
/* 192:171 */     if (x < this.xdim - 1) {
/* 193:172 */       checkPixel(x + 1, y, c, f, val, inverse);
/* 194:    */     }
/* 195:173 */     if (y < this.ydim - 1) {
/* 196:174 */       checkPixel(x, y + 1, c, f, val, inverse);
/* 197:    */     }
/* 198:175 */     if (this.connexity == CONNEXITY4) {
/* 199:176 */       return;
/* 200:    */     }
/* 201:177 */     if ((x > 0) && (y > 0)) {
/* 202:178 */       checkPixel(x - 1, y - 1, c, f, val, inverse);
/* 203:    */     }
/* 204:179 */     if ((x > 0) && (y < this.ydim - 1)) {
/* 205:180 */       checkPixel(x - 1, y + 1, c, f, val, inverse);
/* 206:    */     }
/* 207:181 */     if ((x < this.xdim - 1) && (y > 0)) {
/* 208:182 */       checkPixel(x + 1, y - 1, c, f, val, inverse);
/* 209:    */     }
/* 210:183 */     if ((x < this.xdim - 1) && (y < this.ydim - 1)) {
/* 211:184 */       checkPixel(x + 1, y + 1, c, f, val, inverse);
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   private void checkPixel(int x, int y, int c, Fifo f, int val, boolean inverse)
/* 216:    */   {
/* 217:188 */     if ((!inverse) && (this.output.getXYCByte(x, y, c) < val) && (this.mask.getXYCByte(x, y, c) != this.output.getXYCByte(x, y, c)))
/* 218:    */     {
/* 219:189 */       this.output.setXYCByte(x, y, c, Math.min(val, this.mask.getXYCByte(x, y, c)));
/* 220:190 */       f.add(new Point(x, y));
/* 221:    */     }
/* 222:191 */     else if ((inverse) && (this.output.getXYCByte(x, y, c) > val) && (this.mask.getXYCByte(x, y, c) != this.output.getXYCByte(x, y, c)))
/* 223:    */     {
/* 224:192 */       this.output.setXYCByte(x, y, c, Math.max(val, this.mask.getXYCByte(x, y, c)));
/* 225:193 */       f.add(new Point(x, y));
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   private class Fifo
/* 230:    */   {
/* 231:    */     private Vector<Point> v;
/* 232:    */     
/* 233:    */     Fifo()
/* 234:    */     {
/* 235:201 */       this.v = new Vector();
/* 236:    */     }
/* 237:    */     
/* 238:    */     void add(Point p)
/* 239:    */     {
/* 240:205 */       this.v.add(p);
/* 241:    */     }
/* 242:    */     
/* 243:    */     Point retrieve()
/* 244:    */     {
/* 245:209 */       Object o = this.v.firstElement();
/* 246:210 */       this.v.remove(0);
/* 247:    */       
/* 248:212 */       return (Point)o;
/* 249:    */     }
/* 250:    */     
/* 251:    */     boolean isEmpty()
/* 252:    */     {
/* 253:216 */       return this.v.size() == 0;
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static Image invoke(Image marker, Image mask, int connexity, boolean flag)
/* 258:    */   {
/* 259:    */     try
/* 260:    */     {
/* 261:222 */       return (Image)new FastGrayReconstruction().preprocess(new Object[] { marker, mask, Integer.valueOf(connexity), Boolean.valueOf(flag) });
/* 262:    */     }
/* 263:    */     catch (GlobalException e)
/* 264:    */     {
/* 265:224 */       e.printStackTrace();
/* 266:    */     }
/* 267:225 */     return null;
/* 268:    */   }
/* 269:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.FastGrayReconstruction
 * JD-Core Version:    0.7.0.1
 */
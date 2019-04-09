/*   1:    */ package vpt;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.util.Tools;
/*   5:    */ 
/*   6:    */ public abstract class Image
/*   7:    */ {
/*   8:    */   protected String name;
/*   9:    */   protected int xdim;
/*  10:    */   protected int ydim;
/*  11:    */   protected int cdim;
/*  12:    */   
/*  13:    */   public abstract boolean getBoolean(int paramInt);
/*  14:    */   
/*  15:    */   public abstract void setBoolean(int paramInt, boolean paramBoolean);
/*  16:    */   
/*  17:    */   public abstract int getByte(int paramInt);
/*  18:    */   
/*  19:    */   public abstract void setByte(int paramInt1, int paramInt2);
/*  20:    */   
/*  21:    */   public abstract int getInt(int paramInt);
/*  22:    */   
/*  23:    */   public abstract void setInt(int paramInt1, int paramInt2);
/*  24:    */   
/*  25:    */   public abstract double getDouble(int paramInt);
/*  26:    */   
/*  27:    */   public abstract void setDouble(int paramInt, double paramDouble);
/*  28:    */   
/*  29:    */   public int getXYByte(int x, int y)
/*  30:    */   {
/*  31: 67 */     return getByte(x + this.xdim * y);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setXYByte(int x, int y, int p)
/*  35:    */   {
/*  36: 71 */     setByte(x + this.xdim * y, p);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean getXYBoolean(int x, int y)
/*  40:    */   {
/*  41: 75 */     return getBoolean(x + this.xdim * y);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setXYBoolean(int x, int y, boolean p)
/*  45:    */   {
/*  46: 79 */     setBoolean(x + this.xdim * y, p);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setXYDouble(int x, int y, double p)
/*  50:    */   {
/*  51: 83 */     setDouble(x + this.xdim * y, p);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double getXYDouble(int x, int y)
/*  55:    */   {
/*  56: 87 */     return getDouble(x + this.xdim * y);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setXYInt(int x, int y, int p)
/*  60:    */   {
/*  61: 91 */     setInt(x + this.xdim * y, p);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public int getXYInt(int x, int y)
/*  65:    */   {
/*  66: 95 */     return getInt(x + this.xdim * y);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int getXYCByte(int x, int y, int c)
/*  70:    */   {
/*  71:102 */     return getByte(c + this.cdim * (x + this.xdim * y));
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setXYCByte(int x, int y, int c, int p)
/*  75:    */   {
/*  76:106 */     setByte(c + this.cdim * (x + this.xdim * y), p);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public boolean getXYCBoolean(int x, int y, int c)
/*  80:    */   {
/*  81:110 */     return getBoolean(c + this.cdim * (x + this.xdim * y));
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setXYCBoolean(int x, int y, int c, boolean p)
/*  85:    */   {
/*  86:114 */     setBoolean(c + this.cdim * (x + this.xdim * y), p);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setXYCDouble(int x, int y, int c, double p)
/*  90:    */   {
/*  91:118 */     setDouble(c + this.cdim * (x + this.xdim * y), p);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getXYCDouble(int x, int y, int c)
/*  95:    */   {
/*  96:122 */     return getDouble(c + this.cdim * (x + this.xdim * y));
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setXYCInt(int x, int y, int c, int p)
/* 100:    */   {
/* 101:126 */     setInt(c + this.cdim * (x + this.xdim * y), p);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public int getXYCInt(int x, int y, int c)
/* 105:    */   {
/* 106:130 */     return getInt(c + this.cdim * (x + this.xdim * y));
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double pgetXYCDouble(double x, double y, int c)
/* 110:    */   {
/* 111:142 */     if ((Tools.cmpr(Math.abs(Math.round(x) - x), 0.0D) == 0) && (Tools.cmpr(Math.abs(Math.round(y) - y), 0.0D) == 0)) {
/* 112:143 */       return getXYCDouble((int)x, (int)y, c);
/* 113:    */     }
/* 114:145 */     int l = (int)Math.floor(x);
/* 115:146 */     int k = (int)Math.floor(y);
/* 116:    */     
/* 117:148 */     double a = x - l;
/* 118:149 */     double b = y - k;
/* 119:    */     
/* 120:151 */     double p1 = getXYCDouble(l, k, c);
/* 121:152 */     double p2 = getXYCDouble(Math.min(l + 1, this.xdim - 1), k, c);
/* 122:153 */     double p3 = getXYCDouble(l, Math.min(k + 1, this.ydim - 1), c);
/* 123:154 */     double p4 = getXYCDouble(Math.min(l + 1, this.xdim - 1), Math.min(k + 1, this.ydim - 1), c);
/* 124:    */     
/* 125:156 */     return (1.0D - a) * (1.0D - b) * p1 + a * (1.0D - b) * p2 + (1.0D - a) * b * p3 + a * b * p4;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setVXYDouble(int x, int y, double[] p)
/* 129:    */   {
/* 130:164 */     for (int c = 0; c < this.cdim; c++) {
/* 131:165 */       setXYCDouble(x, y, c, p[c]);
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public double[] getVXYDouble(int x, int y)
/* 136:    */   {
/* 137:169 */     double[] p = new double[this.cdim];
/* 138:171 */     for (int c = 0; c < this.cdim; c++) {
/* 139:172 */       p[c] = getXYCDouble(x, y, c);
/* 140:    */     }
/* 141:174 */     return p;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setVXYByte(int x, int y, int[] p)
/* 145:    */   {
/* 146:178 */     for (int c = 0; c < this.cdim; c++) {
/* 147:179 */       setXYCByte(x, y, c, p[c]);
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public int[] getVXYByte(int x, int y)
/* 152:    */   {
/* 153:183 */     int[] p = new int[this.cdim];
/* 154:185 */     for (int c = 0; c < this.cdim; c++) {
/* 155:186 */       p[c] = getXYCByte(x, y, c);
/* 156:    */     }
/* 157:188 */     return p;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public int getSize()
/* 161:    */   {
/* 162:222 */     return this.xdim * this.ydim * this.cdim;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Image getChannel(int c)
/* 166:    */   {
/* 167:232 */     Image output = newInstance(this.xdim, this.ydim, 1);
/* 168:234 */     for (int x = 0; x < this.xdim; x++) {
/* 169:235 */       for (int y = 0; y < this.ydim; y++) {
/* 170:236 */         output.setXYDouble(x, y, getXYCDouble(x, y, c));
/* 171:    */       }
/* 172:    */     }
/* 173:238 */     return output;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setChannel(Image img, int c)
/* 177:    */   {
/* 178:249 */     for (int x = 0; x < this.xdim; x++) {
/* 179:250 */       for (int y = 0; y < this.ydim; y++) {
/* 180:251 */         setXYCByte(x, y, c, img.getXYByte(x, y));
/* 181:    */       }
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public abstract Image newInstance(boolean paramBoolean);
/* 186:    */   
/* 187:    */   public abstract Image newInstance(int paramInt1, int paramInt2, int paramInt3);
/* 188:    */   
/* 189:    */   public boolean hasSameDimensionsAs(Image img)
/* 190:    */   {
/* 191:261 */     if ((this.xdim != img.xdim) || (this.ydim != img.ydim) || (this.cdim != img.cdim)) {
/* 192:262 */       return false;
/* 193:    */     }
/* 194:264 */     return true;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public Point getXYCMaximum(int c)
/* 198:    */   {
/* 199:274 */     Point p = new Point(0, 0);
/* 200:275 */     double max = -1.797693134862316E+30D;
/* 201:277 */     for (int x = 0; x < this.xdim; x++) {
/* 202:278 */       for (int y = 0; y < this.ydim; y++)
/* 203:    */       {
/* 204:279 */         double tmp = getXYCDouble(x, y, c);
/* 205:280 */         if (tmp > max)
/* 206:    */         {
/* 207:281 */           max = tmp;
/* 208:282 */           p.x = x;
/* 209:283 */           p.y = y;
/* 210:    */         }
/* 211:    */       }
/* 212:    */     }
/* 213:288 */     return p;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public Point getXYCMinimum(int c)
/* 217:    */   {
/* 218:298 */     Point p = new Point(0, 0);
/* 219:    */     
/* 220:300 */     double min = 1.7976931348623157E+308D;
/* 221:302 */     for (int x = 0; x < this.xdim; x++) {
/* 222:303 */       for (int y = 0; y < this.ydim; y++)
/* 223:    */       {
/* 224:305 */         double tmp = getXYCDouble(x, y, c);
/* 225:307 */         if (tmp < min)
/* 226:    */         {
/* 227:308 */           min = tmp;
/* 228:309 */           p.x = x;
/* 229:310 */           p.y = y;
/* 230:    */         }
/* 231:    */       }
/* 232:    */     }
/* 233:316 */     return p;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public boolean isEmpty(int c)
/* 237:    */   {
/* 238:327 */     for (int x = 0; x < this.xdim; x++) {
/* 239:328 */       for (int y = 0; y < this.ydim; y++)
/* 240:    */       {
/* 241:329 */         double tmp = getXYCDouble(x, y, c);
/* 242:330 */         if (tmp > 0.0D) {
/* 243:331 */           return false;
/* 244:    */         }
/* 245:    */       }
/* 246:    */     }
/* 247:335 */     return true;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public boolean isFull(int c)
/* 251:    */   {
/* 252:346 */     for (int x = 0; x < this.xdim; x++) {
/* 253:347 */       for (int y = 0; y < this.ydim; y++)
/* 254:    */       {
/* 255:348 */         boolean tmp = getXYCBoolean(x, y, c);
/* 256:349 */         if (!tmp) {
/* 257:350 */           return false;
/* 258:    */         }
/* 259:    */       }
/* 260:    */     }
/* 261:354 */     return true;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public int getDiagonal()
/* 265:    */   {
/* 266:358 */     return (int)Math.round(Math.sqrt(this.xdim * this.xdim + this.ydim * this.ydim));
/* 267:    */   }
/* 268:    */   
/* 269:    */   public abstract void fill(double paramDouble);
/* 270:    */   
/* 271:    */   public abstract void fill(int paramInt);
/* 272:    */   
/* 273:    */   public abstract void fill(boolean paramBoolean);
/* 274:    */   
/* 275:    */   public int getXDim()
/* 276:    */   {
/* 277:367 */     return this.xdim;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public int getYDim()
/* 281:    */   {
/* 282:371 */     return this.ydim;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public int getCDim()
/* 286:    */   {
/* 287:375 */     return this.cdim;
/* 288:    */   }
/* 289:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.Image
 * JD-Core Version:    0.7.0.1
 */
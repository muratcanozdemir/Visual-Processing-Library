/*   1:    */ package vpt.testing;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.ByteImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.IntegerImage;
/*   8:    */ import vpt.algorithms.display.Display2D;
/*   9:    */ import vpt.algorithms.io.Load;
/*  10:    */ 
/*  11:    */ public class GPathOpening2
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 32 */   public Image output = null;
/*  15: 33 */   public Image input = null;
/*  16: 34 */   public Integer length = null;
/*  17: 35 */   private Image aux = null;
/*  18: 37 */   private final int UNPROCESSED = -1;
/*  19: 38 */   private final int PROCESSED = 0;
/*  20: 39 */   private final int ACTIVE = 1;
/*  21:    */   private int xdim;
/*  22:    */   private int ydim;
/*  23:    */   
/*  24:    */   public GPathOpening2()
/*  25:    */   {
/*  26: 45 */     this.inputFields = "input,length";
/*  27: 46 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 51 */     this.xdim = this.input.getXDim();
/*  34: 52 */     this.ydim = this.input.getYDim();
/*  35:    */     
/*  36: 54 */     Image outputSN = new ByteImage(this.input, false);
/*  37: 55 */     outputSN.fill(0);
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42: 60 */     this.aux = new IntegerImage(this.xdim, this.ydim, 3);
/*  43: 61 */     this.aux.fill(-1);
/*  44: 64 */     for (int y = 0; y < this.ydim; y++) {
/*  45: 65 */       for (int x = 0; x < this.xdim; x++) {
/*  46: 66 */         if (this.input.getXYByte(x, y) > 0)
/*  47:    */         {
/*  48: 67 */           this.aux.setXYCInt(x, y, 0, computeUpwardSN(x, y, 1));
/*  49: 68 */           this.aux.setXYCInt(x, y, 1, computeDownwardSN(x, y, 1));
/*  50:    */         }
/*  51:    */       }
/*  52:    */     }
/*  53: 74 */     for (int t = 2; t < 256; t++)
/*  54:    */     {
/*  55: 77 */       for (int y = 0; y < this.ydim; y++) {
/*  56: 80 */         for (int x = 0; x < this.xdim; x++) {
/*  57: 83 */           if ((this.input.getXYByte(x, y) >= t) || (this.aux.getXYCInt(x, y, 2) == 1))
/*  58:    */           {
/*  59: 85 */             int oldUpwardLabel = this.aux.getXYCInt(x, y, 0);
/*  60: 89 */             if (oldUpwardLabel != computeUpwardSN(x, y, t))
/*  61:    */             {
/*  62: 93 */               if ((x - 1 >= 0) && (y + 1 <= this.ydim - 1)) {
/*  63: 93 */                 this.aux.setXYCInt(x - 1, y + 1, 2, 1);
/*  64:    */               }
/*  65: 94 */               if (y + 1 <= this.ydim - 1) {
/*  66: 94 */                 this.aux.setXYCInt(x, y + 1, 2, 1);
/*  67:    */               }
/*  68: 95 */               if ((x + 1 <= this.xdim - 1) && (y + 1 <= this.ydim - 1)) {
/*  69: 95 */                 this.aux.setXYCInt(x + 1, y + 1, 2, 1);
/*  70:    */               }
/*  71:    */             }
/*  72: 99 */             this.aux.setXYCInt(x, y, 2, 0);
/*  73:    */           }
/*  74:    */         }
/*  75:    */       }
/*  76:104 */       for (int x = 0; x < this.xdim; x++) {
/*  77:105 */         for (int y = 0; y < this.ydim; y++) {
/*  78:106 */           this.aux.setXYCInt(x, y, 2, -1);
/*  79:    */         }
/*  80:    */       }
/*  81:109 */       for (int y = this.ydim - 1; y >= 0; y--) {
/*  82:112 */         for (int x = 0; x < this.xdim; x++) {
/*  83:115 */           if ((this.input.getXYByte(x, y) >= t) || (this.aux.getXYCInt(x, y, 2) == 1))
/*  84:    */           {
/*  85:117 */             int oldDownwardLabel = this.aux.getXYCInt(x, y, 1);
/*  86:121 */             if (oldDownwardLabel != computeDownwardSN(x, y, t))
/*  87:    */             {
/*  88:125 */               if ((x - 1 >= 0) && (y - 1 >= 0)) {
/*  89:125 */                 this.aux.setXYCInt(x - 1, y - 1, 2, 1);
/*  90:    */               }
/*  91:126 */               if (y - 1 >= 0) {
/*  92:126 */                 this.aux.setXYCInt(x, y - 1, 2, 1);
/*  93:    */               }
/*  94:127 */               if ((x + 1 <= this.xdim - 1) && (y - 1 >= 0)) {
/*  95:127 */                 this.aux.setXYCInt(x + 1, y - 1, 2, 1);
/*  96:    */               }
/*  97:    */             }
/*  98:131 */             this.aux.setXYCInt(x, y, 2, 0);
/*  99:    */           }
/* 100:    */         }
/* 101:    */       }
/* 102:136 */       for (int x = 0; x < this.xdim; x++) {
/* 103:137 */         for (int y = 0; y < this.ydim; y++) {
/* 104:138 */           this.aux.setXYCInt(x, y, 2, -1);
/* 105:    */         }
/* 106:    */       }
/* 107:    */     }
/* 108:142 */     for (int x = 0; x < this.xdim; x++) {
/* 109:143 */       for (int y = 0; y < this.ydim; y++) {
/* 110:144 */         if (this.input.getXYByte(x, y) > 0) {
/* 111:145 */           outputSN.setXYByte(x, y, this.aux.getXYCInt(x, y, 0) + 1 + this.aux.getXYCInt(x, y, 1));
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:149 */     this.output = outputSN;
/* 116:    */   }
/* 117:    */   
/* 118:    */   private int computeUpwardSN(int x, int y, int t)
/* 119:    */   {
/* 120:155 */     if (this.input.getXYByte(x, y) < t)
/* 121:    */     {
/* 122:156 */       this.aux.setXYCInt(x, y, 0, 0);
/* 123:157 */       return 0;
/* 124:    */     }
/* 125:    */     int l1;
/* 126:    */     int l1;
/* 127:161 */     if ((x - 1 >= 0) && (y + 1 <= this.ydim - 1)) {
/* 128:161 */       l1 = this.aux.getXYCInt(x - 1, y + 1, 0);
/* 129:    */     } else {
/* 130:162 */       l1 = 0;
/* 131:    */     }
/* 132:    */     int l2;
/* 133:    */     int l2;
/* 134:164 */     if (y + 1 <= this.ydim - 1) {
/* 135:164 */       l2 = this.aux.getXYCInt(x, y + 1, 0);
/* 136:    */     } else {
/* 137:165 */       l2 = 0;
/* 138:    */     }
/* 139:    */     int l3;
/* 140:    */     int l3;
/* 141:167 */     if ((x + 1 <= this.xdim - 1) && (y + 1 <= this.ydim - 1)) {
/* 142:167 */       l3 = this.aux.getXYCInt(x + 1, y + 1, 0);
/* 143:    */     } else {
/* 144:168 */       l3 = 0;
/* 145:    */     }
/* 146:170 */     if (l1 == -1) {
/* 147:170 */       l1 = computeUpwardSN(x - 1, y + 1, t);
/* 148:    */     }
/* 149:171 */     if (l2 == -1) {
/* 150:171 */       l2 = computeUpwardSN(x, y + 1, t);
/* 151:    */     }
/* 152:172 */     if (l3 == -1) {
/* 153:172 */       l3 = computeUpwardSN(x + 1, y + 1, t);
/* 154:    */     }
/* 155:174 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 156:    */     
/* 157:176 */     this.aux.setXYCInt(x, y, 0, lamda);
/* 158:    */     
/* 159:178 */     return lamda;
/* 160:    */   }
/* 161:    */   
/* 162:    */   private int computeDownwardSN(int x, int y, int t)
/* 163:    */   {
/* 164:183 */     if (this.input.getXYByte(x, y) < t)
/* 165:    */     {
/* 166:184 */       this.aux.setXYCInt(x, y, 1, 0);
/* 167:185 */       return 0;
/* 168:    */     }
/* 169:    */     int l1;
/* 170:    */     int l1;
/* 171:189 */     if ((x - 1 >= 0) && (y - 1 >= 0)) {
/* 172:189 */       l1 = this.aux.getXYCInt(x - 1, y - 1, 1);
/* 173:    */     } else {
/* 174:190 */       l1 = 0;
/* 175:    */     }
/* 176:    */     int l2;
/* 177:    */     int l2;
/* 178:192 */     if (y - 1 >= 0) {
/* 179:192 */       l2 = this.aux.getXYCInt(x, y - 1, 1);
/* 180:    */     } else {
/* 181:193 */       l2 = 0;
/* 182:    */     }
/* 183:    */     int l3;
/* 184:    */     int l3;
/* 185:195 */     if ((x + 1 <= this.xdim - 1) && (y - 1 >= 0)) {
/* 186:195 */       l3 = this.aux.getXYCInt(x + 1, y - 1, 1);
/* 187:    */     } else {
/* 188:196 */       l3 = 0;
/* 189:    */     }
/* 190:198 */     if (l1 == -1) {
/* 191:198 */       l1 = computeDownwardSN(x - 1, y - 1, t);
/* 192:    */     }
/* 193:199 */     if (l2 == -1) {
/* 194:199 */       l2 = computeDownwardSN(x, y - 1, t);
/* 195:    */     }
/* 196:200 */     if (l3 == -1) {
/* 197:200 */       l3 = computeDownwardSN(x + 1, y - 1, t);
/* 198:    */     }
/* 199:202 */     int lamda = 1 + Math.max(l1, Math.max(l2, l3));
/* 200:    */     
/* 201:204 */     this.aux.setXYCInt(x, y, 1, lamda);
/* 202:    */     
/* 203:206 */     return lamda;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public static Image invoke(Image image, Integer length)
/* 207:    */   {
/* 208:    */     try
/* 209:    */     {
/* 210:213 */       return (Image)new GPathOpening2().preprocess(new Object[] { image, length });
/* 211:    */     }
/* 212:    */     catch (GlobalException e)
/* 213:    */     {
/* 214:215 */       e.printStackTrace();
/* 215:    */     }
/* 216:216 */     return null;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static void main(String[] args)
/* 220:    */   {
/* 221:221 */     Image img = Load.invoke("samples/grayPaths.png");
/* 222:222 */     Display2D.invoke(img);
/* 223:    */     
/* 224:224 */     Image tmp = invoke(img, Integer.valueOf(30));
/* 225:225 */     Display2D.invoke(tmp, "30");
/* 226:    */   }
/* 227:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.GPathOpening2
 * JD-Core Version:    0.7.0.1
 */
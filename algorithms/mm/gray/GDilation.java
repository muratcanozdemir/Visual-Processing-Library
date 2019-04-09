/*   1:    */ package vpt.algorithms.mm.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.se.FlatSE;
/*   8:    */ 
/*   9:    */ public class GDilation
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 16 */   public Image output = null;
/*  13: 17 */   public Image input = null;
/*  14: 18 */   public FlatSE se = null;
/*  15:    */   private int xdim;
/*  16:    */   private int ydim;
/*  17:    */   private int cdim;
/*  18: 24 */   private final double MIN = -1.797693134862316E+308D;
/*  19:    */   
/*  20:    */   public GDilation()
/*  21:    */   {
/*  22: 27 */     this.inputFields = "input,se";
/*  23: 28 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 32 */     this.output = this.input.newInstance(false);
/*  30:    */     
/*  31: 34 */     this.xdim = this.output.getXDim();
/*  32: 35 */     this.ydim = this.output.getYDim();
/*  33: 36 */     this.cdim = this.output.getCDim();
/*  34: 39 */     if (!this.se.BOX)
/*  35:    */     {
/*  36: 40 */       for (int x = 0; x < this.xdim; x++) {
/*  37: 41 */         for (int y = 0; y < this.ydim; y++) {
/*  38: 42 */           for (int c = 0; c < this.cdim; c++) {
/*  39: 43 */             this.output.setXYCDouble(x, y, c, getMax(this.input, x, y, c, this.se.getCoords()));
/*  40:    */           }
/*  41:    */         }
/*  42:    */       }
/*  43:    */     }
/*  44:    */     else
/*  45:    */     {
/*  46: 46 */       FlatSE hline = this.se.getHLine();
/*  47: 47 */       FlatSE vline = this.se.getVLine();
/*  48: 48 */       Image tmp = this.input.newInstance(false);
/*  49:    */       
/*  50: 50 */       int seSize = hline.getXDim();
/*  51: 51 */       int halfSize = seSize / 2;
/*  52: 52 */       int padding = halfSize;
/*  53: 53 */       int segmentSize = 2 * seSize + 1;
/*  54: 54 */       int numberOfSegments = (this.input.getXDim() + padding + 3 * padding - 2 * halfSize) / seSize;
/*  55: 55 */       double[] maxBuf = new double[segmentSize];
/*  56: 58 */       for (int y = 0; y < 4 * this.ydim; y++) {
/*  57: 59 */         for (int c = 0; c < this.cdim; c++) {
/*  58: 61 */           for (int i = 0; i < numberOfSegments; i++)
/*  59:    */           {
/*  60: 65 */             int index = (i + 1) * seSize - 1;
/*  61: 66 */             maxBuf[(seSize - 1)] = getPixelWithPadding(index, y, c, padding, this.input);
/*  62: 68 */             for (int j = 1; j < seSize; j++)
/*  63:    */             {
/*  64: 69 */               maxBuf[(seSize - j - 1)] = Math.max(maxBuf[(seSize - j)], getPixelWithPadding(index - j, y, c, padding, this.input));
/*  65: 70 */               maxBuf[(seSize + j - 1)] = Math.max(maxBuf[(seSize + j - 2)], getPixelWithPadding(index + j, y, c, padding, this.input));
/*  66:    */             }
/*  67: 74 */             index = halfSize + i * seSize;
/*  68: 75 */             setPixelWithPadding(index, y, c, padding, tmp, maxBuf[0]);
/*  69: 76 */             setPixelWithPadding(index + seSize - 1, y, c, padding, tmp, maxBuf[(2 * seSize - 2)]);
/*  70: 78 */             for (int k = 1; k < seSize - 1; k++) {
/*  71: 79 */               setPixelWithPadding(index + k, y, c, padding, tmp, Math.max(maxBuf[k], maxBuf[(k + seSize - 1)]));
/*  72:    */             }
/*  73:    */           }
/*  74:    */         }
/*  75:    */       }
/*  76: 84 */       seSize = vline.getYDim();
/*  77: 85 */       halfSize = seSize / 2;
/*  78: 86 */       padding = halfSize;
/*  79: 87 */       segmentSize = 2 * seSize + 1;
/*  80: 88 */       numberOfSegments = (this.input.getYDim() + padding + 3 * padding - 2 * halfSize) / seSize;
/*  81: 91 */       for (int x = 0; x < 4 * this.xdim; x++) {
/*  82: 92 */         for (int c = 0; c < this.cdim; c++) {
/*  83: 94 */           for (int i = 0; i < numberOfSegments; i++)
/*  84:    */           {
/*  85: 98 */             int index = (i + 1) * seSize - 1;
/*  86: 99 */             maxBuf[(seSize - 1)] = getPixelWithPadding(x, index, c, padding, tmp);
/*  87:101 */             for (int j = 1; j < seSize; j++)
/*  88:    */             {
/*  89:102 */               maxBuf[(seSize - j - 1)] = Math.max(maxBuf[(seSize - j)], getPixelWithPadding(x, index - j, c, padding, tmp));
/*  90:103 */               maxBuf[(seSize + j - 1)] = Math.max(maxBuf[(seSize + j - 2)], getPixelWithPadding(x, index + j, c, padding, tmp));
/*  91:    */             }
/*  92:107 */             index = halfSize + i * seSize;
/*  93:108 */             setPixelWithPadding(x, index, c, padding, this.output, maxBuf[0]);
/*  94:109 */             setPixelWithPadding(x, index + seSize - 1, c, padding, this.output, maxBuf[(2 * seSize - 2)]);
/*  95:111 */             for (int k = 1; k < seSize - 1; k++) {
/*  96:112 */               setPixelWithPadding(x, index + k, c, padding, this.output, Math.max(maxBuf[k], maxBuf[(k + seSize - 1)]));
/*  97:    */             }
/*  98:    */           }
/*  99:    */         }
/* 100:    */       }
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private void setPixelWithPadding(int x, int y, int c, int padding, Image img, double p)
/* 105:    */   {
/* 106:124 */     if ((x >= padding) && (y >= padding)) {
/* 107:126 */       if ((x <= this.xdim + padding - 1) && (y <= this.ydim + padding - 1)) {
/* 108:128 */         img.setXYCDouble(x - padding, y - padding, c, p);
/* 109:    */       }
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   private double getPixelWithPadding(int x, int y, int c, int padding, Image img)
/* 114:    */   {
/* 115:138 */     if ((x < padding) || (y < padding)) {
/* 116:138 */       return -1.797693134862316E+308D;
/* 117:    */     }
/* 118:140 */     if ((x > this.xdim + padding - 1) || (y > this.ydim + padding - 1)) {
/* 119:140 */       return -1.797693134862316E+308D;
/* 120:    */     }
/* 121:142 */     return img.getXYCDouble(x - padding, y - padding, c);
/* 122:    */   }
/* 123:    */   
/* 124:    */   private double getMax(Image img, int x, int y, int c, Point[] coords)
/* 125:    */   {
/* 126:147 */     double max = -1.797693134862316E+308D;
/* 127:149 */     for (int i = 0; i < coords.length; i++)
/* 128:    */     {
/* 129:152 */       int _x = x - coords[i].x;
/* 130:153 */       int _y = y - coords[i].y;
/* 131:155 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 132:    */       {
/* 133:158 */         double p = img.getXYCDouble(_x, _y, c);
/* 134:159 */         if (p > max) {
/* 135:159 */           max = p;
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:163 */     return max > -1.797693134862316E+308D ? max : img.getXYCDouble(x, y, c);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static Image invoke(Image image, FlatSE se)
/* 143:    */   {
/* 144:    */     try
/* 145:    */     {
/* 146:168 */       return (Image)new GDilation().preprocess(new Object[] { image, se });
/* 147:    */     }
/* 148:    */     catch (GlobalException e)
/* 149:    */     {
/* 150:170 */       e.printStackTrace();
/* 151:    */     }
/* 152:171 */     return null;
/* 153:    */   }
/* 154:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GDilation
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package vpt.algorithms.mm.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.se.FlatSE;
/*   8:    */ 
/*   9:    */ public class GErosion
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 15 */   public Image output = null;
/*  13: 16 */   public Image input = null;
/*  14: 17 */   public FlatSE se = null;
/*  15:    */   private int xdim;
/*  16:    */   private int ydim;
/*  17:    */   private int cdim;
/*  18: 22 */   private final double MAX = 1.7976931348623157E+308D;
/*  19:    */   
/*  20:    */   public GErosion()
/*  21:    */   {
/*  22: 25 */     this.inputFields = "input,se";
/*  23: 26 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 30 */     this.output = this.input.newInstance(false);
/*  30:    */     
/*  31: 32 */     this.xdim = this.output.getXDim();
/*  32: 33 */     this.ydim = this.output.getYDim();
/*  33: 34 */     this.cdim = this.output.getCDim();
/*  34: 37 */     if (!this.se.BOX)
/*  35:    */     {
/*  36: 38 */       this.se = this.se.reflection();
/*  37: 40 */       for (int x = 0; x < this.xdim; x++) {
/*  38: 41 */         for (int y = 0; y < this.ydim; y++) {
/*  39: 42 */           for (int c = 0; c < this.cdim; c++) {
/*  40: 43 */             this.output.setXYCDouble(x, y, c, getMin(this.input, x, y, c, this.se.getCoords()));
/*  41:    */           }
/*  42:    */         }
/*  43:    */       }
/*  44:    */     }
/*  45:    */     else
/*  46:    */     {
/*  47: 48 */       FlatSE hline = this.se.getHLine();
/*  48: 49 */       FlatSE vline = this.se.getVLine();
/*  49: 50 */       Image tmp = this.input.newInstance(false);
/*  50:    */       
/*  51: 52 */       int seSize = hline.getXDim();
/*  52: 53 */       int halfSize = seSize / 2;
/*  53: 54 */       int padding = halfSize;
/*  54: 55 */       int segmentSize = 2 * seSize + 1;
/*  55: 56 */       int numberOfSegments = (this.input.getXDim() + padding + 3 * padding - 2 * halfSize) / seSize;
/*  56: 57 */       double[] minBuf = new double[segmentSize];
/*  57: 60 */       for (int y = 0; y < 4 * this.ydim; y++) {
/*  58: 61 */         for (int c = 0; c < this.cdim; c++) {
/*  59: 63 */           for (int i = 0; i < numberOfSegments; i++)
/*  60:    */           {
/*  61: 67 */             int index = (i + 1) * seSize - 1;
/*  62: 68 */             minBuf[(seSize - 1)] = getPixelWithPadding(index, y, c, padding, this.input);
/*  63: 70 */             for (int j = 1; j < seSize; j++)
/*  64:    */             {
/*  65: 71 */               minBuf[(seSize - j - 1)] = Math.min(minBuf[(seSize - j)], getPixelWithPadding(index - j, y, c, padding, this.input));
/*  66: 72 */               minBuf[(seSize + j - 1)] = Math.min(minBuf[(seSize + j - 2)], getPixelWithPadding(index + j, y, c, padding, this.input));
/*  67:    */             }
/*  68: 76 */             index = halfSize + i * seSize;
/*  69: 77 */             setPixelWithPadding(index, y, c, padding, tmp, minBuf[0]);
/*  70: 78 */             setPixelWithPadding(index + seSize - 1, y, c, padding, tmp, minBuf[(2 * seSize - 2)]);
/*  71: 80 */             for (int k = 1; k < seSize - 1; k++) {
/*  72: 81 */               setPixelWithPadding(index + k, y, c, padding, tmp, Math.min(minBuf[k], minBuf[(k + seSize - 1)]));
/*  73:    */             }
/*  74:    */           }
/*  75:    */         }
/*  76:    */       }
/*  77: 86 */       seSize = vline.getYDim();
/*  78: 87 */       halfSize = seSize / 2;
/*  79: 88 */       padding = halfSize;
/*  80: 89 */       segmentSize = 2 * seSize + 1;
/*  81: 90 */       numberOfSegments = (this.input.getYDim() + padding + 3 * padding - 2 * halfSize) / seSize;
/*  82: 93 */       for (int x = 0; x < 4 * this.xdim; x++) {
/*  83: 94 */         for (int c = 0; c < this.cdim; c++) {
/*  84: 96 */           for (int i = 0; i < numberOfSegments; i++)
/*  85:    */           {
/*  86:100 */             int index = (i + 1) * seSize - 1;
/*  87:101 */             minBuf[(seSize - 1)] = getPixelWithPadding(x, index, c, padding, tmp);
/*  88:103 */             for (int j = 1; j < seSize; j++)
/*  89:    */             {
/*  90:104 */               minBuf[(seSize - j - 1)] = Math.min(minBuf[(seSize - j)], getPixelWithPadding(x, index - j, c, padding, tmp));
/*  91:105 */               minBuf[(seSize + j - 1)] = Math.min(minBuf[(seSize + j - 2)], getPixelWithPadding(x, index + j, c, padding, tmp));
/*  92:    */             }
/*  93:109 */             index = halfSize + i * seSize;
/*  94:110 */             setPixelWithPadding(x, index, c, padding, this.output, minBuf[0]);
/*  95:111 */             setPixelWithPadding(x, index + seSize - 1, c, padding, this.output, minBuf[(2 * seSize - 2)]);
/*  96:113 */             for (int k = 1; k < seSize - 1; k++) {
/*  97:114 */               setPixelWithPadding(x, index + k, c, padding, this.output, Math.min(minBuf[k], minBuf[(k + seSize - 1)]));
/*  98:    */             }
/*  99:    */           }
/* 100:    */         }
/* 101:    */       }
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   private void setPixelWithPadding(int x, int y, int c, int padding, Image img, double p)
/* 106:    */   {
/* 107:126 */     if ((x >= padding) && (y >= padding)) {
/* 108:128 */       if ((x <= this.xdim + padding - 1) && (y <= this.ydim + padding - 1)) {
/* 109:130 */         img.setXYCDouble(x - padding, y - padding, c, p);
/* 110:    */       }
/* 111:    */     }
/* 112:    */   }
/* 113:    */   
/* 114:    */   private double getPixelWithPadding(int x, int y, int c, int padding, Image img)
/* 115:    */   {
/* 116:140 */     if ((x < padding) || (y < padding)) {
/* 117:140 */       return 1.7976931348623157E+308D;
/* 118:    */     }
/* 119:142 */     if ((x > this.xdim + padding - 1) || (y > this.ydim + padding - 1)) {
/* 120:142 */       return 1.7976931348623157E+308D;
/* 121:    */     }
/* 122:144 */     return img.getXYCDouble(x - padding, y - padding, c);
/* 123:    */   }
/* 124:    */   
/* 125:    */   private double getMin(Image img, int x, int y, int c, Point[] coords)
/* 126:    */   {
/* 127:149 */     double min = 1.7976931348623157E+308D;
/* 128:151 */     for (int i = 0; i < coords.length; i++)
/* 129:    */     {
/* 130:156 */       int _x = x - coords[i].x;
/* 131:157 */       int _y = y - coords[i].y;
/* 132:159 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 133:    */       {
/* 134:162 */         double p = img.getXYCDouble(_x, _y, c);
/* 135:163 */         if (p < min) {
/* 136:163 */           min = p;
/* 137:    */         }
/* 138:    */       }
/* 139:    */     }
/* 140:167 */     return min < 1.7976931348623157E+308D ? min : img.getXYCDouble(x, y, c);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static Image invoke(Image image, FlatSE se)
/* 144:    */   {
/* 145:    */     try
/* 146:    */     {
/* 147:173 */       return (Image)new GErosion().preprocess(new Object[] { image, se });
/* 148:    */     }
/* 149:    */     catch (GlobalException e)
/* 150:    */     {
/* 151:175 */       e.printStackTrace();
/* 152:    */     }
/* 153:176 */     return null;
/* 154:    */   }
/* 155:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GErosion
 * JD-Core Version:    0.7.0.1
 */
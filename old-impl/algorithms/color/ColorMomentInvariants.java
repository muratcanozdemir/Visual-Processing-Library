/*   1:    */ package vpt.algorithms.color;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class ColorMomentInvariants
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11:    */   public Image input;
/*  12:    */   public double[] output;
/*  13:    */   private int xdim;
/*  14:    */   private int ydim;
/*  15:    */   
/*  16:    */   public ColorMomentInvariants()
/*  17:    */   {
/*  18: 25 */     this.inputFields = "input";
/*  19: 26 */     this.outputFields = "output";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void execute()
/*  23:    */     throws GlobalException
/*  24:    */   {
/*  25: 31 */     int cdim = this.input.getCDim();
/*  26: 32 */     this.xdim = this.input.getXDim();
/*  27: 33 */     this.ydim = this.input.getYDim();
/*  28: 35 */     if (cdim != 3) {
/*  29: 35 */       throw new GlobalException("Why is the input not in color ?");
/*  30:    */     }
/*  31: 37 */     Image tmp = new DoubleImage(this.input, true);
/*  32:    */     
/*  33: 39 */     double m00000 = moment(0, 0, tmp, 0, 0, 0);
/*  34:    */     
/*  35: 41 */     double m00100 = moment(0, 0, tmp, 1, 0, 0);
/*  36: 42 */     double m00010 = moment(0, 0, tmp, 0, 1, 0);
/*  37: 43 */     double m00001 = moment(0, 0, tmp, 0, 0, 1);
/*  38: 44 */     double m00200 = moment(0, 0, tmp, 2, 0, 0);
/*  39: 45 */     double m00020 = moment(0, 0, tmp, 0, 2, 0);
/*  40: 46 */     double m00002 = moment(0, 0, tmp, 0, 0, 2);
/*  41: 47 */     double m00110 = moment(0, 0, tmp, 1, 1, 0);
/*  42: 48 */     double m00101 = moment(0, 0, tmp, 1, 0, 1);
/*  43: 49 */     double m00011 = moment(0, 0, tmp, 0, 1, 1);
/*  44:    */     
/*  45: 51 */     double m01100 = moment(0, 1, tmp, 1, 0, 0);
/*  46: 52 */     double m01010 = moment(0, 1, tmp, 0, 1, 0);
/*  47: 53 */     double m01001 = moment(0, 1, tmp, 0, 0, 1);
/*  48: 54 */     double m01200 = moment(0, 1, tmp, 2, 0, 0);
/*  49: 55 */     double m01020 = moment(0, 1, tmp, 0, 2, 0);
/*  50: 56 */     double m01002 = moment(0, 1, tmp, 0, 0, 2);
/*  51: 57 */     double m01110 = moment(0, 1, tmp, 1, 1, 0);
/*  52: 58 */     double m01101 = moment(0, 1, tmp, 1, 0, 1);
/*  53: 59 */     double m01011 = moment(0, 1, tmp, 0, 1, 1);
/*  54:    */     
/*  55: 61 */     double m10100 = moment(1, 0, tmp, 1, 0, 0);
/*  56: 62 */     double m10010 = moment(1, 0, tmp, 0, 1, 0);
/*  57: 63 */     double m10001 = moment(1, 0, tmp, 0, 0, 1);
/*  58: 64 */     double m10200 = moment(1, 0, tmp, 2, 0, 0);
/*  59: 65 */     double m10020 = moment(1, 0, tmp, 0, 2, 0);
/*  60: 66 */     double m10002 = moment(1, 0, tmp, 0, 0, 2);
/*  61: 67 */     double m10110 = moment(1, 0, tmp, 1, 1, 0);
/*  62: 68 */     double m10101 = moment(1, 0, tmp, 1, 0, 1);
/*  63: 69 */     double m10011 = moment(1, 0, tmp, 0, 1, 1);
/*  64:    */     
/*  65: 71 */     double m01000 = moment(0, 1, tmp, 0, 0, 0);
/*  66: 72 */     double m10000 = moment(1, 0, tmp, 0, 0, 0);
/*  67:    */     
/*  68: 74 */     this.output = new double[20];
/*  69:    */     
/*  70: 76 */     this.output[0] = (m00200 * m00000 / (m00100 * m00100));
/*  71:    */     
/*  72: 78 */     this.output[1] = (m00020 * m00000 / (m00010 * m00010));
/*  73:    */     
/*  74: 80 */     this.output[2] = (m00002 * m00000 / (m00001 * m00001));
/*  75:    */     
/*  76: 82 */     this.output[3] = (m00110 * m00000 / (m00100 * m00010));
/*  77:    */     
/*  78: 84 */     this.output[4] = (m00101 * m00000 / (m00100 * m00001));
/*  79:    */     
/*  80: 86 */     this.output[5] = (m00011 * m00000 / (m00010 * m00001));
/*  81:    */     
/*  82: 88 */     this.output[6] = 
/*  83: 89 */       ((m10100 * m01010 * m00000 + m10010 * m01000 * m00100 + m10000 * m01100 * m00010) / (m00100 * m00010 * m00000) - (m10100 * m01000 * m00010 + m10010 * m01100 * m00000 + m10000 * m01010 * m00100) / (m00100 * m00010 * m00000));
/*  84:    */     
/*  85: 91 */     this.output[7] = 
/*  86: 92 */       ((m10100 * m01001 * m00000 + m10001 * m01000 * m00100 + m10000 * m01100 * m00001) / (m00100 * m00001 * m00000) - (m10100 * m01000 * m00001 + m10001 * m01100 * m00000 + m10000 * m01001 * m00100) / (m00100 * m00001 * m00000));
/*  87:    */     
/*  88: 94 */     this.output[8] = 
/*  89: 95 */       ((m10010 * m01001 * m00000 + m10001 * m01000 * m00010 + m10000 * m01010 * m00001) / (m00010 * m00001 * m00000) - (m10010 * m01000 * m00001 + m10001 * m01010 * m00000 + m10000 * m01001 * m00010) / (m00010 * m00001 * m00000));
/*  90:    */     
/*  91:    */ 
/*  92: 98 */     this.output[9] = 
/*  93: 99 */       ((m10200 * m01000 * m00100 + m10100 * m01200 * m00000 + m10000 * m01100 * m00200) / (m00200 * m00100 * m00000) - (m10200 * m01100 * m00000 + m10100 * m01000 * m00200 + m10000 * m01200 * m00100) / (m00200 * m00100 * m00000));
/*  94:    */     
/*  95:101 */     this.output[10] = 
/*  96:102 */       ((m10020 * m01000 * m00010 + m10010 * m01020 * m00000 + m10000 * m01010 * m00020) / (m00020 * m00010 * m00000) - (m10020 * m01010 * m00000 + m10010 * m01000 * m00020 + m10000 * m01020 * m00010) / (m00020 * m00010 * m00000));
/*  97:    */     
/*  98:104 */     this.output[11] = 
/*  99:105 */       ((m10002 * m01000 * m00001 + m10001 * m01002 * m00000 + m10000 * m01001 * m00002) / (m00002 * m00001 * m00000) - (m10002 * m01001 * m00000 + m10001 * m01000 * m00002 + m10000 * m01002 * m00001) / (m00002 * m00001 * m00000));
/* 100:    */     
/* 101:107 */     this.output[12] = 
/* 102:108 */       ((m10110 * m01000 * m00100 + m10100 * m01110 * m00000 + m10000 * m01100 * m00110) / (m00110 * m00100 * m00000) - (m10110 * m01100 * m00000 + m10100 * m01000 * m00110 + m10000 * m01110 * m00100) / (m00110 * m00100 * m00000));
/* 103:    */     
/* 104:110 */     this.output[13] = 
/* 105:111 */       ((m10101 * m01000 * m00100 + m10100 * m01101 * m00000 + m10000 * m01100 * m00101) / (m00101 * m00100 * m00000) - (m10101 * m01100 * m00000 + m10100 * m01000 * m00101 + m10000 * m01101 * m00100) / (m00101 * m00100 * m00000));
/* 106:    */     
/* 107:113 */     this.output[14] = 
/* 108:114 */       ((m10011 * m01000 * m00010 + m10010 * m01011 * m00000 + m10000 * m01010 * m00011) / (m00011 * m00010 * m00000) - (m10011 * m01010 * m00000 + m10010 * m01000 * m00011 + m10000 * m01011 * m00010) / (m00011 * m00010 * m00000));
/* 109:    */     
/* 110:116 */     this.output[15] = 
/* 111:117 */       ((m10110 * m01000 * m00010 + m10010 * m01110 * m00000 + m10000 * m01010 * m00110) / (m00110 * m00010 * m00000) - (m10110 * m01010 * m00000 + m10010 * m01000 * m00110 + m10000 * m01110 * m00010) / (m00110 * m00010 * m00000));
/* 112:    */     
/* 113:119 */     this.output[16] = 
/* 114:120 */       ((m10101 * m01000 * m00001 + m10001 * m01101 * m00000 + m10000 * m01001 * m00101) / (m00101 * m00001 * m00000) - (m10101 * m01001 * m00000 + m10001 * m01000 * m00101 + m10000 * m01101 * m00001) / (m00101 * m00001 * m00000));
/* 115:    */     
/* 116:122 */     this.output[17] = 
/* 117:123 */       ((m10011 * m01000 * m00001 + m10001 * m01011 * m00000 + m10000 * m01001 * m00011) / (m00011 * m00001 * m00000) - (m10011 * m01001 * m00000 + m10001 * m01000 * m00011 + m10000 * m01011 * m00001) / (m00011 * m00001 * m00000));
/* 118:    */     
/* 119:125 */     this.output[18] = 
/* 120:126 */       ((m10020 * m01000 * m00100 + m10100 * m01020 * m00000 + m10000 * m01100 * m00020) / (m00020 * m00100 * m00000) - (m10020 * m01100 * m00000 + m10100 * m01000 * m00020 + m10000 * m01020 * m00100) / (m00020 * m00100 * m00000));
/* 121:    */     
/* 122:128 */     this.output[19] = 
/* 123:129 */       ((m10002 * m01000 * m00010 + m10010 * m01002 * m00000 + m10000 * m01010 * m00002) / (m00002 * m00010 * m00000) - (m10002 * m01010 * m00000 + m10010 * m01000 * m00002 + m10000 * m01002 * m00010) / (m00002 * m00010 * m00000));
/* 124:    */   }
/* 125:    */   
/* 126:    */   private double moment(int p, int q, Image tmp, int r, int g, int b)
/* 127:    */   {
/* 128:136 */     double moment = 0.0D;
/* 129:138 */     for (int x = 0; x < this.xdim; x++) {
/* 130:139 */       for (int y = 0; y < this.ydim; y++)
/* 131:    */       {
/* 132:140 */         double red = tmp.getXYCDouble(x, y, 0);
/* 133:141 */         double green = tmp.getXYCDouble(x, y, 1);
/* 134:142 */         double blue = tmp.getXYCDouble(x, y, 2);
/* 135:    */         
/* 136:144 */         moment += Math.pow(x + 1, p) * Math.pow(y + 1, q) * Math.pow(red, r) * Math.pow(green, g) * Math.pow(blue, b);
/* 137:    */       }
/* 138:    */     }
/* 139:148 */     return moment;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static double[] invoke(Image image)
/* 143:    */   {
/* 144:    */     try
/* 145:    */     {
/* 146:153 */       return (double[])new ColorMomentInvariants().preprocess(new Object[] { image });
/* 147:    */     }
/* 148:    */     catch (GlobalException e)
/* 149:    */     {
/* 150:155 */       e.printStackTrace();
/* 151:    */     }
/* 152:156 */     return null;
/* 153:    */   }
/* 154:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.color.ColorMomentInvariants
 * JD-Core Version:    0.7.0.1
 */
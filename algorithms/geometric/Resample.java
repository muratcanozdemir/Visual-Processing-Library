/*   1:    */ package vpt.algorithms.geometric;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ 
/*   7:    */ public class Resample
/*   8:    */   extends Algorithm
/*   9:    */ {
/*  10: 14 */   public Image output = null;
/*  11: 16 */   public Image input = null;
/*  12: 17 */   public Double ratioX = null;
/*  13: 18 */   public Double ratioY = null;
/*  14: 19 */   public Integer method = null;
/*  15:    */   public static final int NEAREST_NEIGHBOR = 0;
/*  16:    */   public static final int BILINEAR = 1;
/*  17:    */   public static final int BICUBIC = 2;
/*  18:    */   
/*  19:    */   public Resample()
/*  20:    */   {
/*  21: 27 */     this.inputFields = "input,ratioX,ratioY,method";
/*  22: 28 */     this.outputFields = "output";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void execute()
/*  26:    */     throws GlobalException
/*  27:    */   {
/*  28: 33 */     if ((this.ratioX.doubleValue() <= 0.0D) || (this.ratioY.doubleValue() <= 0.0D)) {
/*  29: 34 */       throw new GlobalException("Invalid ratio value");
/*  30:    */     }
/*  31: 36 */     if ((this.method.intValue() != 1) && (this.method.intValue() != 0) && (this.method.intValue() != 2)) {
/*  32: 37 */       throw new GlobalException("Invalid interpolation method");
/*  33:    */     }
/*  34: 39 */     int cdim = this.input.getCDim();
/*  35: 40 */     int xdim = this.input.getXDim();
/*  36: 41 */     int ydim = this.input.getYDim();
/*  37:    */     
/*  38: 43 */     int newXdim = (int)(xdim * this.ratioX.doubleValue());
/*  39: 44 */     int newYdim = (int)(ydim * this.ratioY.doubleValue());
/*  40:    */     
/*  41:    */ 
/*  42: 47 */     this.output = this.input.newInstance(newXdim, newYdim, cdim);
/*  43: 50 */     for (int c = 0; c < cdim; c++) {
/*  44: 51 */       for (int x = 0; x < newXdim; x++) {
/*  45: 52 */         for (int y = 0; y < newYdim; y++)
/*  46:    */         {
/*  47: 53 */           double p = 0.0D;
/*  48: 55 */           if (this.method.intValue() == 0)
/*  49:    */           {
/*  50: 56 */             p = this.input.getXYCDouble((int)(x / this.ratioX.doubleValue()), (int)(y / this.ratioY.doubleValue()), c);
/*  51:    */           }
/*  52: 58 */           else if (this.method.intValue() == 1)
/*  53:    */           {
/*  54: 59 */             int l = (int)Math.floor(x / this.ratioX.doubleValue());
/*  55: 60 */             int k = (int)Math.floor(y / this.ratioY.doubleValue());
/*  56:    */             
/*  57: 62 */             double a = x / this.ratioX.doubleValue() - l;
/*  58: 63 */             double b = y / this.ratioY.doubleValue() - k;
/*  59:    */             
/*  60: 65 */             double p1 = this.input.getXYCDouble(l, k, c);
/*  61: 66 */             double p2 = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), k, c);
/*  62: 67 */             double p3 = this.input.getXYCDouble(l, Math.min(k + 1, ydim - 1), c);
/*  63: 68 */             double p4 = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), Math.min(k + 1, ydim - 1), c);
/*  64:    */             
/*  65: 70 */             p = (1.0D - a) * (1.0D - b) * p1 + a * (1.0D - b) * p2 + (1.0D - a) * b * p3 + a * b * p4;
/*  66:    */           }
/*  67:    */           else
/*  68:    */           {
/*  69: 73 */             int l = (int)Math.floor(x / this.ratioX.doubleValue());
/*  70: 74 */             int k = (int)Math.floor(y / this.ratioY.doubleValue());
/*  71: 75 */             double dx = x / this.ratioX.doubleValue() - l;
/*  72: 76 */             double dy = y / this.ratioY.doubleValue() - k;
/*  73: 77 */             double dx2 = dx * dx;
/*  74: 78 */             double dx3 = dx2 * dx;
/*  75: 79 */             double dy2 = dy * dy;
/*  76: 80 */             double dy3 = dy2 * dy;
/*  77:    */             
/*  78: 82 */             double[][] neighbors = new double[4][4];
/*  79: 83 */             neighbors[0][0] = this.input.getXYCDouble(Math.max(l - 1, 0), Math.max(k - 1, 0), c);
/*  80: 84 */             neighbors[0][1] = this.input.getXYCDouble(Math.max(l - 1, 0), k, c);
/*  81: 85 */             neighbors[0][2] = this.input.getXYCDouble(Math.max(l - 1, 0), Math.min(k + 1, ydim - 1), c);
/*  82: 86 */             neighbors[0][3] = this.input.getXYCDouble(Math.max(l - 1, 0), Math.min(k + 2, ydim - 1), c);
/*  83: 87 */             neighbors[1][0] = this.input.getXYCDouble(l, Math.max(k - 1, 0), c);
/*  84: 88 */             neighbors[1][1] = this.input.getXYCDouble(l, k, c);
/*  85: 89 */             neighbors[1][2] = this.input.getXYCDouble(l, Math.min(k + 1, ydim - 1), c);
/*  86: 90 */             neighbors[1][3] = this.input.getXYCDouble(l, Math.min(k + 2, ydim - 1), c);
/*  87: 91 */             neighbors[2][0] = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), Math.max(k - 1, 0), c);
/*  88: 92 */             neighbors[2][1] = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), k, c);
/*  89: 93 */             neighbors[2][2] = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), Math.min(k + 1, ydim - 1), c);
/*  90: 94 */             neighbors[2][3] = this.input.getXYCDouble(Math.min(l + 1, xdim - 1), Math.min(k + 2, ydim - 1), c);
/*  91: 95 */             neighbors[3][0] = this.input.getXYCDouble(Math.min(l + 2, xdim - 1), Math.max(k - 1, 0), c);
/*  92: 96 */             neighbors[3][1] = this.input.getXYCDouble(Math.min(l + 2, xdim - 1), k, c);
/*  93: 97 */             neighbors[3][2] = this.input.getXYCDouble(Math.min(l + 2, xdim - 1), Math.min(k + 1, ydim - 1), c);
/*  94: 98 */             neighbors[3][3] = this.input.getXYCDouble(Math.min(l + 2, xdim - 1), Math.min(k + 2, ydim - 1), c);
/*  95:    */             
/*  96:100 */             double[] cc = new double[4];
/*  97:104 */             for (int j = 0; j < 4; j++)
/*  98:    */             {
/*  99:105 */               double d0 = neighbors[0][j] - neighbors[1][j];
/* 100:106 */               double d2 = neighbors[2][j] - neighbors[1][j];
/* 101:107 */               double d3 = neighbors[3][j] - neighbors[1][j];
/* 102:108 */               double a0 = neighbors[1][j];
/* 103:109 */               double a1 = -0.3333333333333333D * d0 + d2 - 0.1666666666666667D * d3;
/* 104:110 */               double a2 = 0.5D * d0 + 0.5D * d2;
/* 105:111 */               double a3 = -0.1666666666666667D * d0 - 0.5D * d2 + 0.1666666666666667D * d3;
/* 106:112 */               cc[j] = (a0 + a1 * dx + a2 * dx2 + a3 * dx3);
/* 107:    */             }
/* 108:115 */             double d0 = cc[0] - cc[1];
/* 109:116 */             double d2 = cc[2] - cc[1];
/* 110:117 */             double d3 = cc[3] - cc[1];
/* 111:118 */             double a0 = cc[1];
/* 112:119 */             double a1 = -0.3333333333333333D * d0 + d2 - 0.1666666666666667D * d3;
/* 113:120 */             double a2 = 0.5D * d0 + 0.5D * d2;
/* 114:121 */             double a3 = -0.1666666666666667D * d0 - 0.5D * d2 + 0.1666666666666667D * d3;
/* 115:122 */             p = a0 + a1 * dy + a2 * dy2 + a3 * dy3;
/* 116:    */           }
/* 117:125 */           p = Math.max(Math.min(p, 1.0D), 0.0D);
/* 118:126 */           this.output.setXYCDouble(x, y, c, p);
/* 119:    */         }
/* 120:    */       }
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static Image invoke(Image image, Double ratioX, Double ratioY, Integer method)
/* 125:    */   {
/* 126:    */     try
/* 127:    */     {
/* 128:135 */       return (Image)new Resample().preprocess(new Object[] { image, ratioX, ratioY, method });
/* 129:    */     }
/* 130:    */     catch (GlobalException e)
/* 131:    */     {
/* 132:137 */       e.printStackTrace();
/* 133:    */     }
/* 134:138 */     return null;
/* 135:    */   }
/* 136:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.Resample
 * JD-Core Version:    0.7.0.1
 */
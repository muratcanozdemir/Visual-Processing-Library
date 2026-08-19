/*   1:    */ package vpt.algorithms.conversion;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.ByteImage;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ 
/*   9:    */ public class XYZ2LAB
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 18 */   public Image input = null;
/*  13: 19 */   public Image output = null;
/*  14:    */   
/*  15:    */   public XYZ2LAB()
/*  16:    */   {
/*  17: 22 */     this.inputFields = "input";
/*  18: 23 */     this.outputFields = "output";
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void execute()
/*  22:    */     throws GlobalException
/*  23:    */   {
/*  24: 28 */     this.output = new DoubleImage(this.input, false);
/*  25:    */     
/*  26: 30 */     int xdim = this.input.getXDim();
/*  27: 31 */     int ydim = this.input.getYDim();
/*  28: 32 */     int cdim = this.input.getCDim();
/*  29: 34 */     if (cdim != 3) {
/*  30: 34 */       throw new GlobalException("The input must be a color image");
/*  31:    */     }
/*  32: 36 */     for (int x = 0; x < xdim; x++) {
/*  33: 37 */       for (int y = 0; y < ydim; y++)
/*  34:    */       {
/*  35: 38 */         double X = this.input.getXYCDouble(x, y, 0);
/*  36: 39 */         double Y = this.input.getXYCDouble(x, y, 1);
/*  37: 40 */         double Z = this.input.getXYCDouble(x, y, 2);
/*  38:    */         
/*  39: 42 */         double[] lab = convert(X, Y, Z);
/*  40:    */         
/*  41: 44 */         this.output.setXYCDouble(x, y, 0, lab[0]);
/*  42: 45 */         this.output.setXYCDouble(x, y, 1, lab[1]);
/*  43: 46 */         this.output.setXYCDouble(x, y, 2, lab[2]);
/*  44:    */       }
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static double[] convert(double x, double y, double z)
/*  49:    */   {
/*  50: 61 */     double[] lab = new double[3];
/*  51:    */     
/*  52:    */ 
/*  53: 64 */     double Xn = 0.95047D;
/*  54: 65 */     double Yn = 1.0D;
/*  55: 66 */     double Zn = 1.08883D;
/*  56:    */     
/*  57: 68 */     double Xfrac = x / Xn;
/*  58: 69 */     double Yfrac = y / Yn;
/*  59: 70 */     double Zfrac = z / Zn;
/*  60: 72 */     if (Xfrac > 0.008855999999999999D) {
/*  61: 73 */       Xfrac = Math.pow(Xfrac, 0.333333D);
/*  62:    */     } else {
/*  63: 75 */       Xfrac = (903.29999999999995D * Xfrac + 16.0D) / 116.0D;
/*  64:    */     }
/*  65: 77 */     if (Yfrac > 0.008855999999999999D) {
/*  66: 78 */       Yfrac = Math.pow(Yfrac, 0.333333D);
/*  67:    */     } else {
/*  68: 80 */       Yfrac = (903.29999999999995D * Yfrac + 16.0D) / 116.0D;
/*  69:    */     }
/*  70: 82 */     if (Zfrac > 0.008855999999999999D) {
/*  71: 83 */       Zfrac = Math.pow(Zfrac, 0.333333D);
/*  72:    */     } else {
/*  73: 85 */       Zfrac = (903.29999999999995D * Zfrac + 16.0D) / 116.0D;
/*  74:    */     }
/*  75: 87 */     lab[0] = (116.0D * Yfrac - 16.0D);
/*  76: 88 */     lab[1] = (500.0D * (Xfrac - Yfrac));
/*  77: 89 */     lab[2] = (200.0D * (Yfrac - Zfrac));
/*  78:    */     
/*  79: 91 */     return lab;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Image scaleToByte(Image lab)
/*  83:    */   {
/*  84: 95 */     ByteImage bimg = new ByteImage(lab, false);
/*  85:    */     
/*  86: 97 */     double f = 2.55D;
/*  87: 99 */     for (int x = 0; x < lab.getXDim(); x++) {
/*  88:100 */       for (int y = 0; y < lab.getYDim(); y++)
/*  89:    */       {
/*  90:101 */         double d = lab.getXYCDouble(x, y, 0);
/*  91:    */         
/*  92:103 */         bimg.setXYCByte(x, y, 0, (int)Math.round(d * f));
/*  93:    */         
/*  94:    */ 
/*  95:106 */         d = lab.getXYCDouble(x, y, 1);
/*  96:107 */         bimg.setXYCByte(x, y, 1, (int)Math.round(d + 128.0D));
/*  97:    */         
/*  98:109 */         d = lab.getXYCDouble(x, y, 2);
/*  99:110 */         bimg.setXYCByte(x, y, 2, (int)Math.round(d + 128.0D));
/* 100:    */       }
/* 101:    */     }
/* 102:114 */     return bimg;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Image invoke(Image image)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:127 */       return (Image)new XYZ2LAB().preprocess(new Object[] { image });
/* 110:    */     }
/* 111:    */     catch (GlobalException e)
/* 112:    */     {
/* 113:129 */       e.printStackTrace();
/* 114:    */     }
/* 115:130 */     return null;
/* 116:    */   }
/* 117:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.XYZ2LAB
 * JD-Core Version:    0.7.0.1
 */
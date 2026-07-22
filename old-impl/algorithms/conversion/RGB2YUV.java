/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class RGB2YUV
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:17 */   public Image input = null;
/* 12:18 */   public Image output = null;
/* 13:   */   
/* 14:   */   public RGB2YUV()
/* 15:   */   {
/* 16:21 */     this.inputFields = "input";
/* 17:22 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:27 */     this.output = new DoubleImage(this.input, false);
/* 24:   */     
/* 25:29 */     int xdim = this.input.getXDim();
/* 26:30 */     int ydim = this.input.getYDim();
/* 27:31 */     int cdim = this.input.getCDim();
/* 28:33 */     if (cdim != 3) {
/* 29:34 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 30:   */     }
/* 31:36 */     for (int x = 0; x < xdim; x++) {
/* 32:37 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:38 */         int R = this.input.getXYCByte(x, y, 0);
/* 35:39 */         int G = this.input.getXYCByte(x, y, 1);
/* 36:40 */         int B = this.input.getXYCByte(x, y, 2);
/* 37:   */         
/* 38:42 */         double[] yuv = convert(R, G, B);
/* 39:   */         
/* 40:44 */         this.output.setXYCDouble(x, y, 0, yuv[0]);
/* 41:45 */         this.output.setXYCDouble(x, y, 1, yuv[1]);
/* 42:46 */         this.output.setXYCDouble(x, y, 2, yuv[2]);
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static double[] convert(int r, int g, int b)
/* 48:   */   {
/* 49:61 */     double[] yuv = new double[3];
/* 50:   */     
/* 51:   */ 
/* 52:64 */     double rN = r * 0.003921D;
/* 53:65 */     double gN = g * 0.003921D;
/* 54:66 */     double bN = b * 0.003921D;
/* 55:   */     
/* 56:68 */     yuv[0] = (0.299D * rN + 0.587D * gN + 0.114D * bN);
/* 57:69 */     yuv[1] = (-0.147D * rN - 0.289D * gN + 0.436D * bN);
/* 58:70 */     yuv[2] = (0.615D * rN - 0.515D * gN - 0.1D * bN);
/* 59:   */     
/* 60:72 */     return yuv;
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static Image invoke(Image image)
/* 64:   */   {
/* 65:   */     try
/* 66:   */     {
/* 67:85 */       return (Image)new RGB2YUV().preprocess(new Object[] { image });
/* 68:   */     }
/* 69:   */     catch (GlobalException e)
/* 70:   */     {
/* 71:87 */       e.printStackTrace();
/* 72:   */     }
/* 73:88 */     return null;
/* 74:   */   }
/* 75:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2YUV
 * JD-Core Version:    0.7.0.1
 */
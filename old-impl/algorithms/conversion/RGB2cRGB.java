/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class RGB2cRGB
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:18 */   public Image input = null;
/* 12:19 */   public Image output = null;
/* 13:   */   
/* 14:   */   public RGB2cRGB()
/* 15:   */   {
/* 16:22 */     this.inputFields = "input";
/* 17:23 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:28 */     this.output = new DoubleImage(this.input, false);
/* 24:   */     
/* 25:30 */     int xdim = this.input.getXDim();
/* 26:31 */     int ydim = this.input.getYDim();
/* 27:32 */     int cdim = this.input.getCDim();
/* 28:34 */     if (cdim != 3) {
/* 29:35 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 30:   */     }
/* 31:37 */     for (int x = 0; x < xdim; x++) {
/* 32:38 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:39 */         int R = this.input.getXYCByte(x, y, 0);
/* 35:40 */         int G = this.input.getXYCByte(x, y, 1);
/* 36:41 */         int B = this.input.getXYCByte(x, y, 2);
/* 37:   */         
/* 38:43 */         double[] crgb = convert(R, G, B);
/* 39:   */         
/* 40:45 */         this.output.setXYCDouble(x, y, 0, crgb[0]);
/* 41:46 */         this.output.setXYCDouble(x, y, 1, crgb[1]);
/* 42:47 */         this.output.setXYCDouble(x, y, 2, crgb[2]);
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static double[] convert(int r, int g, int b)
/* 48:   */   {
/* 49:62 */     double[] crgb = new double[3];
/* 50:   */     
/* 51:64 */     double sum = r + g + b;
/* 52:   */     
/* 53:66 */     crgb[0] = (r / sum);
/* 54:67 */     crgb[1] = (g / sum);
/* 55:68 */     crgb[2] = (b / sum);
/* 56:   */     
/* 57:70 */     return crgb;
/* 58:   */   }
/* 59:   */   
/* 60:   */   public static Image invoke(Image image)
/* 61:   */   {
/* 62:   */     try
/* 63:   */     {
/* 64:83 */       return (Image)new RGB2cRGB().preprocess(new Object[] { image });
/* 65:   */     }
/* 66:   */     catch (GlobalException e)
/* 67:   */     {
/* 68:85 */       e.printStackTrace();
/* 69:   */     }
/* 70:86 */     return null;
/* 71:   */   }
/* 72:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2cRGB
 * JD-Core Version:    0.7.0.1
 */
/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class RGB2RGB64
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image input = null;
/* 12:17 */   public Image output = null;
/* 13:   */   
/* 14:   */   public RGB2RGB64()
/* 15:   */   {
/* 16:20 */     this.inputFields = "input";
/* 17:21 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:26 */     this.output = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 3);
/* 24:   */     
/* 25:28 */     int xdim = this.input.getXDim();
/* 26:29 */     int ydim = this.input.getYDim();
/* 27:30 */     int cdim = this.input.getCDim();
/* 28:32 */     if (cdim != 3) {
/* 29:33 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 30:   */     }
/* 31:35 */     for (int x = 0; x < xdim; x++) {
/* 32:36 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:37 */         int R = this.input.getXYCByte(x, y, 0);
/* 35:38 */         int G = this.input.getXYCByte(x, y, 1);
/* 36:39 */         int B = this.input.getXYCByte(x, y, 2);
/* 37:   */         
/* 38:41 */         this.output.setXYCByte(x, y, 0, R / 64 * 64);
/* 39:42 */         this.output.setXYCByte(x, y, 1, G / 64 * 64);
/* 40:43 */         this.output.setXYCByte(x, y, 2, B / 64 * 64);
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static double convert(int r, int g, int b)
/* 46:   */   {
/* 47:58 */     return (r + g + b) / 3.0D;
/* 48:   */   }
/* 49:   */   
/* 50:   */   public static Image invoke(Image image)
/* 51:   */   {
/* 52:   */     try
/* 53:   */     {
/* 54:71 */       return (Image)new RGB2RGB64().preprocess(new Object[] { image });
/* 55:   */     }
/* 56:   */     catch (GlobalException e)
/* 57:   */     {
/* 58:73 */       e.printStackTrace();
/* 59:   */     }
/* 60:74 */     return null;
/* 61:   */   }
/* 62:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2RGB64
 * JD-Core Version:    0.7.0.1
 */
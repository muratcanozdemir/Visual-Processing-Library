/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Angular2Color
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:18 */   public Image input = null;
/* 12:19 */   public Image output = null;
/* 13:   */   
/* 14:   */   public Angular2Color()
/* 15:   */   {
/* 16:22 */     this.inputFields = "input";
/* 17:23 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:28 */     if (this.input.getCDim() != 1) {
/* 24:28 */       throw new GlobalException("The input must be a mono-channel angular image");
/* 25:   */     }
/* 26:30 */     int xdim = this.input.getXDim();
/* 27:31 */     int ydim = this.input.getYDim();
/* 28:   */     
/* 29:33 */     this.output = new DoubleImage(xdim, ydim, 3);
/* 30:34 */     this.output.fill(0);
/* 31:36 */     for (int y = 0; y < ydim; y++) {
/* 32:37 */       for (int x = 0; x < xdim; x++)
/* 33:   */       {
/* 34:39 */         this.output.setXYCDouble(x, y, 0, this.input.getXYDouble(x, y) - 0.25D);
/* 35:40 */         this.output.setXYCDouble(x, y, 1, 0.8D);
/* 36:41 */         this.output.setXYCDouble(x, y, 2, 0.6D);
/* 37:   */       }
/* 38:   */     }
/* 39:45 */     this.output = HSY2RGB.invoke(this.output);
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image image)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:58 */       return (Image)new Angular2Color().preprocess(new Object[] { image });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:60 */       e.printStackTrace();
/* 51:   */     }
/* 52:61 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.Angular2Color
 * JD-Core Version:    0.7.0.1
 */
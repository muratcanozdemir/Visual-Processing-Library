/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class OrientationHistogram
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:17 */   public double[] output = null;
/* 11:18 */   public Image input = null;
/* 12:19 */   public Integer bins = null;
/* 13:20 */   public Integer maskSize = null;
/* 14:   */   private int xdim;
/* 15:   */   private int ydim;
/* 16:   */   
/* 17:   */   public OrientationHistogram()
/* 18:   */   {
/* 19:26 */     this.inputFields = "input, bins, maskSize";
/* 20:27 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:31 */     if (this.input.getCDim() != 1) {
/* 27:31 */       throw new GlobalException("The input image must be grayscale");
/* 28:   */     }
/* 29:32 */     if (this.bins.intValue() < 1) {
/* 30:32 */       throw new GlobalException("Invalid bin value");
/* 31:   */     }
/* 32:34 */     this.xdim = this.input.getXDim();
/* 33:35 */     this.ydim = this.input.getYDim();
/* 34:   */     
/* 35:37 */     int size = this.xdim * this.ydim;
/* 36:   */     
/* 37:39 */     this.output = new double[this.bins.intValue()];
/* 38:   */     
/* 39:41 */     this.input = OrientationMap.invoke(this.input, this.maskSize);
/* 40:43 */     for (int y = 0; y < this.ydim; y++) {
/* 41:44 */       for (int x = 0; x < this.xdim; x++)
/* 42:   */       {
/* 43:45 */         double orientation = this.input.getXYDouble(x, y);
/* 44:47 */         if (orientation == 1.0D) {
/* 45:47 */           orientation -= 1.0E-008D;
/* 46:   */         }
/* 47:48 */         this.output[((int)(orientation * this.bins.intValue()))] += 1.0D;
/* 48:   */       }
/* 49:   */     }
/* 50:52 */     for (int i = 0; i < this.output.length; i++) {
/* 51:53 */       this.output[i] /= size;
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static double[] invoke(Image image, Integer bins, Integer maskSize)
/* 56:   */   {
/* 57:   */     try
/* 58:   */     {
/* 59:58 */       return (double[])new OrientationHistogram().preprocess(new Object[] { image, bins, maskSize });
/* 60:   */     }
/* 61:   */     catch (GlobalException e)
/* 62:   */     {
/* 63:60 */       e.printStackTrace();
/* 64:   */     }
/* 65:61 */     return null;
/* 66:   */   }
/* 67:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.OrientationHistogram
 * JD-Core Version:    0.7.0.1
 */
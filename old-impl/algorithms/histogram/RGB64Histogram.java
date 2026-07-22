/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class RGB64Histogram
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:10 */   public double[] output = null;
/* 12:11 */   public Image inputImage = null;
/* 13:12 */   public Boolean normalized = null;
/* 14:   */   
/* 15:   */   public RGB64Histogram()
/* 16:   */   {
/* 17:15 */     this.inputFields = "inputImage,normalized";
/* 18:16 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:21 */     int xdim = this.inputImage.getXDim();
/* 25:22 */     int ydim = this.inputImage.getYDim();
/* 26:23 */     int cdim = this.inputImage.getCDim();
/* 27:25 */     if (cdim != 3) {
/* 28:25 */       throw new GlobalException("Only color images please !");
/* 29:   */     }
/* 30:27 */     this.output = new double[64];
/* 31:28 */     Arrays.fill(this.output, 0.0D);
/* 32:30 */     for (int x = 0; x < xdim; x++) {
/* 33:31 */       for (int y = 0; y < ydim; y++)
/* 34:   */       {
/* 35:32 */         int r = this.inputImage.getXYCByte(x, y, 0);
/* 36:33 */         int g = this.inputImage.getXYCByte(x, y, 1);
/* 37:34 */         int b = this.inputImage.getXYCByte(x, y, 2);
/* 38:   */         
/* 39:36 */         r /= 64;
/* 40:37 */         g /= 64;
/* 41:38 */         b /= 64;
/* 42:   */         
/* 43:40 */         this.output[(r * 16 + g * 4 + b)] += 1.0D;
/* 44:   */       }
/* 45:   */     }
/* 46:44 */     if (this.normalized.booleanValue())
/* 47:   */     {
/* 48:45 */       int size = xdim * ydim;
/* 49:47 */       for (int i = 0; i < this.output.length; i++) {
/* 50:48 */         this.output[i] /= size;
/* 51:   */       }
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static double[] invoke(Image image, Boolean normalized)
/* 56:   */   {
/* 57:   */     try
/* 58:   */     {
/* 59:55 */       return (double[])new RGB64Histogram().preprocess(new Object[] { image, normalized });
/* 60:   */     }
/* 61:   */     catch (GlobalException e)
/* 62:   */     {
/* 63:57 */       e.printStackTrace();
/* 64:   */     }
/* 65:58 */     return null;
/* 66:   */   }
/* 67:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.RGB64Histogram
 * JD-Core Version:    0.7.0.1
 */
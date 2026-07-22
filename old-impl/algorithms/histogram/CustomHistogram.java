/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class CustomHistogram
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public double[] output = null;
/* 12:17 */   public Image inputImage = null;
/* 13:18 */   public Boolean normalized = null;
/* 14:19 */   public Integer bins = null;
/* 15:   */   
/* 16:   */   public CustomHistogram()
/* 17:   */   {
/* 18:22 */     this.inputFields = "inputImage, normalized, bins";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:28 */     int xdim = this.inputImage.getXDim();
/* 26:29 */     int ydim = this.inputImage.getYDim();
/* 27:30 */     int cdim = this.inputImage.getCDim();
/* 28:   */     
/* 29:32 */     this.output = new double[cdim * this.bins.intValue()];
/* 30:33 */     Arrays.fill(this.output, 0.0D);
/* 31:35 */     for (int c = 0; c < cdim; c++) {
/* 32:36 */       for (int x = 0; x < xdim; x++) {
/* 33:37 */         for (int y = 0; y < ydim; y++)
/* 34:   */         {
/* 35:38 */           int pixel = this.inputImage.getXYCByte(x, y, c);
/* 36:40 */           if (pixel >= this.bins.intValue()) {
/* 37:40 */             throw new GlobalException("A pixel value is beyond the allowed bin size! " + pixel);
/* 38:   */           }
/* 39:42 */           this.output[(c * this.bins.intValue() + pixel)] += 1.0D;
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:47 */     if (this.normalized.booleanValue())
/* 44:   */     {
/* 45:48 */       int size = xdim * ydim;
/* 46:50 */       for (int i = 0; i < this.output.length; i++) {
/* 47:51 */         this.output[i] /= size;
/* 48:   */       }
/* 49:   */     }
/* 50:   */   }
/* 51:   */   
/* 52:   */   public static double[] invoke(Image image, Boolean normalized, Integer bins)
/* 53:   */   {
/* 54:   */     try
/* 55:   */     {
/* 56:59 */       return (double[])new CustomHistogram().preprocess(new Object[] { image, normalized, bins });
/* 57:   */     }
/* 58:   */     catch (GlobalException e)
/* 59:   */     {
/* 60:61 */       e.printStackTrace();
/* 61:   */     }
/* 62:62 */     return null;
/* 63:   */   }
/* 64:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.CustomHistogram
 * JD-Core Version:    0.7.0.1
 */
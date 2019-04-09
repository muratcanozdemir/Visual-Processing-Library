/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Histogram
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:10 */   public double[] output = null;
/* 12:11 */   public Image inputImage = null;
/* 13:12 */   public Boolean normalized = null;
/* 14:   */   
/* 15:   */   public Histogram()
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
/* 27:   */     
/* 28:25 */     this.output = new double[cdim * 256];
/* 29:26 */     Arrays.fill(this.output, 0.0D);
/* 30:28 */     for (int c = 0; c < cdim; c++) {
/* 31:29 */       for (int x = 0; x < xdim; x++) {
/* 32:30 */         for (int y = 0; y < ydim; y++)
/* 33:   */         {
/* 34:31 */           int pixel = this.inputImage.getXYCByte(x, y, c);
/* 35:   */           
/* 36:33 */           this.output[(c * 256 + pixel)] += 1.0D;
/* 37:   */         }
/* 38:   */       }
/* 39:   */     }
/* 40:38 */     if (this.normalized.booleanValue())
/* 41:   */     {
/* 42:39 */       int size = xdim * ydim;
/* 43:41 */       for (int i = 0; i < this.output.length; i++) {
/* 44:42 */         this.output[i] /= size;
/* 45:   */       }
/* 46:   */     }
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static double[] invoke(Image image, Boolean normalized)
/* 50:   */   {
/* 51:   */     try
/* 52:   */     {
/* 53:50 */       return (double[])new Histogram().preprocess(new Object[] { image, normalized });
/* 54:   */     }
/* 55:   */     catch (GlobalException e)
/* 56:   */     {
/* 57:52 */       e.printStackTrace();
/* 58:   */     }
/* 59:53 */     return null;
/* 60:   */   }
/* 61:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.Histogram
 * JD-Core Version:    0.7.0.1
 */
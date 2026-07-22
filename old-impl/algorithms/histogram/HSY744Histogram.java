/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class HSY744Histogram
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:10 */   public double[] output = null;
/* 12:11 */   public Image inputImage = null;
/* 13:12 */   public Boolean normalized = null;
/* 14:   */   
/* 15:   */   public HSY744Histogram()
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
/* 28:25 */       throw new GlobalException("Where is my color input image ?");
/* 29:   */     }
/* 30:27 */     this.output = new double[112];
/* 31:28 */     Arrays.fill(this.output, 0.0D);
/* 32:30 */     for (int x = 0; x < xdim; x++) {
/* 33:31 */       for (int y = 0; y < ydim; y++)
/* 34:   */       {
/* 35:32 */         int hue = this.inputImage.getXYCByte(x, y, 0);
/* 36:33 */         int sat = this.inputImage.getXYCByte(x, y, 1);
/* 37:34 */         int lum = this.inputImage.getXYCByte(x, y, 2);
/* 38:   */         
/* 39:36 */         this.output[(hue * 16 + sat * 4 + lum)] += 1.0D;
/* 40:   */       }
/* 41:   */     }
/* 42:40 */     if (this.normalized.booleanValue())
/* 43:   */     {
/* 44:41 */       int size = xdim * ydim;
/* 45:43 */       for (int i = 0; i < this.output.length; i++) {
/* 46:44 */         this.output[i] /= size;
/* 47:   */       }
/* 48:   */     }
/* 49:   */   }
/* 50:   */   
/* 51:   */   public static double[] invoke(Image image, Boolean normalized)
/* 52:   */   {
/* 53:   */     try
/* 54:   */     {
/* 55:52 */       return (double[])new HSY744Histogram().preprocess(new Object[] { image, normalized });
/* 56:   */     }
/* 57:   */     catch (GlobalException e)
/* 58:   */     {
/* 59:54 */       e.printStackTrace();
/* 60:   */     }
/* 61:55 */     return null;
/* 62:   */   }
/* 63:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.HSY744Histogram
 * JD-Core Version:    0.7.0.1
 */
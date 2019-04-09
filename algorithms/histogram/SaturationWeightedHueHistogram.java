/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class SaturationWeightedHueHistogram
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:17 */   public double[] output = null;
/* 12:18 */   public Image hsy = null;
/* 13:   */   
/* 14:   */   public SaturationWeightedHueHistogram()
/* 15:   */   {
/* 16:21 */     this.inputFields = "hsy";
/* 17:22 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:27 */     int xdim = this.hsy.getXDim();
/* 24:28 */     int ydim = this.hsy.getYDim();
/* 25:29 */     int cdim = this.hsy.getCDim();
/* 26:   */     
/* 27:31 */     double normalization = 0.0D;
/* 28:33 */     if (cdim != 3) {
/* 29:33 */       throw new GlobalException("cdim error");
/* 30:   */     }
/* 31:35 */     this.output = new double[12];
/* 32:36 */     Arrays.fill(this.output, 0.0D);
/* 33:38 */     for (int y = 0; y < ydim; y++) {
/* 34:39 */       for (int x = 0; x < xdim; x++)
/* 35:   */       {
/* 36:40 */         double h = this.hsy.getXYCDouble(x, y, 0) * 11.0D;
/* 37:41 */         double s = this.hsy.getXYCDouble(x, y, 1);
/* 38:42 */         double l = this.hsy.getXYCDouble(x, y, 2);
/* 39:44 */         if ((s >= 0.04705882352941176D) && 
/* 40:45 */           (l >= 0.04705882352941176D))
/* 41:   */         {
/* 42:47 */           double coeff = sigma(s);
/* 43:   */           
/* 44:49 */           this.output[((int)Math.round(h))] += coeff;
/* 45:50 */           normalization += coeff;
/* 46:   */         }
/* 47:   */       }
/* 48:   */     }
/* 49:54 */     for (int i = 0; i < this.output.length; i++) {
/* 50:55 */       this.output[i] /= normalization;
/* 51:   */     }
/* 52:   */   }
/* 53:   */   
/* 54:   */   private double sigma(double z)
/* 55:   */   {
/* 56:60 */     return 1.0D / (1.0D + Math.exp(-5.0D * (z - 0.5D)));
/* 57:   */   }
/* 58:   */   
/* 59:   */   public static double[] invoke(Image hsy)
/* 60:   */   {
/* 61:   */     try
/* 62:   */     {
/* 63:66 */       return (double[])new SaturationWeightedHueHistogram().preprocess(new Object[] { hsy });
/* 64:   */     }
/* 65:   */     catch (GlobalException e)
/* 66:   */     {
/* 67:68 */       e.printStackTrace();
/* 68:   */     }
/* 69:69 */     return null;
/* 70:   */   }
/* 71:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.SaturationWeightedHueHistogram
 * JD-Core Version:    0.7.0.1
 */
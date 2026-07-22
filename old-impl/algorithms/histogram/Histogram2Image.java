/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Histogram2Image
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:15 */   public Image output = null;
/* 12:16 */   public double[] histo = null;
/* 13:   */   
/* 14:   */   public Histogram2Image()
/* 15:   */   {
/* 16:19 */     this.inputFields = "histo";
/* 17:20 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:24 */     int cdim = this.histo.length / 256;
/* 24:25 */     int height = 256;
/* 25:   */     
/* 26:27 */     this.output = new BooleanImage(256, height, cdim);
/* 27:28 */     this.output.fill(true);
/* 28:31 */     for (int c = 0; c < cdim; c++)
/* 29:   */     {
/* 30:34 */       double max = 0.0D;
/* 31:35 */       for (int i = 0; i < 256; i++) {
/* 32:36 */         if (this.histo[(c * 256 + i)] > max) {
/* 33:37 */           max = this.histo[(c * 256 + i)];
/* 34:   */         }
/* 35:   */       }
/* 36:40 */       if (max == 0.0D) {
/* 37:40 */         return;
/* 38:   */       }
/* 39:43 */       for (int x = 0; x < this.output.getXDim(); x++)
/* 40:   */       {
/* 41:46 */         double p = height * this.histo[(c * 256 + x)] / max;
/* 42:48 */         for (int y = 0; y < height - p; y++) {
/* 43:49 */           this.output.setXYCBoolean(x, y, c, false);
/* 44:   */         }
/* 45:   */       }
/* 46:   */     }
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static Image invoke(double[] histo)
/* 50:   */   {
/* 51:   */     try
/* 52:   */     {
/* 53:58 */       return (Image)new Histogram2Image().preprocess(new Object[] { histo });
/* 54:   */     }
/* 55:   */     catch (GlobalException e)
/* 56:   */     {
/* 57:60 */       e.printStackTrace();
/* 58:   */     }
/* 59:61 */     return null;
/* 60:   */   }
/* 61:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.Histogram2Image
 * JD-Core Version:    0.7.0.1
 */
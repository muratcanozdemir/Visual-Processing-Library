/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Equalization
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10: 8 */   public Image output = null;
/* 11: 9 */   public Image inputImage = null;
/* 12:   */   
/* 13:   */   public Equalization()
/* 14:   */   {
/* 15:12 */     this.inputFields = "inputImage";
/* 16:13 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:18 */     this.output = this.inputImage.newInstance(true);
/* 23:   */     
/* 24:   */ 
/* 25:21 */     double[] histo = Histogram.invoke(this.inputImage, Boolean.valueOf(true));
/* 26:23 */     for (int c = 0; c < this.output.getCDim(); c++)
/* 27:   */     {
/* 28:26 */       for (int i = 1; i < 256; i++) {
/* 29:27 */         histo[(c * 256 + i)] += histo[(c * 256 + i - 1)];
/* 30:   */       }
/* 31:29 */       for (int x = 0; x < this.inputImage.getXDim(); x++) {
/* 32:30 */         for (int y = 0; y < this.inputImage.getYDim(); y++) {
/* 33:31 */           this.output.setXYCDouble(x, y, c, histo[(c * 256 + this.inputImage.getXYCByte(x, y, c))]);
/* 34:   */         }
/* 35:   */       }
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   public static Image invoke(Image image)
/* 40:   */   {
/* 41:   */     try
/* 42:   */     {
/* 43:41 */       return (Image)new Equalization().preprocess(new Object[] { image });
/* 44:   */     }
/* 45:   */     catch (GlobalException e)
/* 46:   */     {
/* 47:43 */       e.printStackTrace();
/* 48:   */     }
/* 49:44 */     return null;
/* 50:   */   }
/* 51:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.Equalization
 * JD-Core Version:    0.7.0.1
 */
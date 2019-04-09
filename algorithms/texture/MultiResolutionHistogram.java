/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.histogram.CustomHistogram;
/*  7:   */ import vpt.algorithms.mm.gray.GClosing;
/*  8:   */ import vpt.algorithms.mm.gray.GOpening;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MultiResolutionHistogram
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:18 */   public double[] output = null;
/* 15:19 */   public Image input = null;
/* 16:   */   
/* 17:   */   public MultiResolutionHistogram()
/* 18:   */   {
/* 19:22 */     this.inputFields = "input";
/* 20:23 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:27 */     int cdim = this.input.getCDim();
/* 27:28 */     int bins = 32;
/* 28:29 */     int scales = 8;
/* 29:31 */     if (cdim != 1) {
/* 30:31 */       throw new GlobalException("Only greyscale images for now!");
/* 31:   */     }
/* 32:33 */     this.output = new double[2 * scales * bins];
/* 33:34 */     Image tmp = this.input.newInstance(true);
/* 34:37 */     for (int i = 0; i < tmp.getSize(); i++) {
/* 35:38 */       tmp.setByte(i, (int)Math.floor(tmp.getByte(i) / 8));
/* 36:   */     }
/* 37:41 */     for (int i = 1; i <= scales; i++)
/* 38:   */     {
/* 39:42 */       FlatSE se = FlatSE.circle((i - 1) * 2 + 1);
/* 40:   */       
/* 41:44 */       Image tmp2 = GOpening.invoke(tmp, se);
/* 42:   */       
/* 43:46 */       double[] histo = CustomHistogram.invoke(tmp2, Boolean.valueOf(true), Integer.valueOf(bins));
/* 44:47 */       for (int j = 0; j < histo.length; j++) {
/* 45:48 */         this.output[((i - 1) * bins + j)] = histo[j];
/* 46:   */       }
/* 47:50 */       tmp2 = GClosing.invoke(tmp, se);
/* 48:   */       
/* 49:52 */       histo = CustomHistogram.invoke(tmp2, Boolean.valueOf(true), Integer.valueOf(bins));
/* 50:53 */       for (int j = 0; j < histo.length; j++) {
/* 51:54 */         this.output[(scales * bins + (i - 1) * bins + j)] = histo[j];
/* 52:   */       }
/* 53:   */     }
/* 54:   */   }
/* 55:   */   
/* 56:   */   public static double[] invoke(Image image)
/* 57:   */   {
/* 58:   */     try
/* 59:   */     {
/* 60:60 */       return (double[])new MultiResolutionHistogram().preprocess(new Object[] { image });
/* 61:   */     }
/* 62:   */     catch (GlobalException e)
/* 63:   */     {
/* 64:62 */       e.printStackTrace();
/* 65:   */     }
/* 66:63 */     return null;
/* 67:   */   }
/* 68:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.MultiResolutionHistogram
 * JD-Core Version:    0.7.0.1
 */
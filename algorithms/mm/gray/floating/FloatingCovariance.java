/*  1:   */ package vpt.algorithms.mm.gray.floating;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Tools;
/*  7:   */ import vpt.util.se.FloatingFlatSE;
/*  8:   */ 
/*  9:   */ public class FloatingCovariance
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:22 */   public double[] output = null;
/* 13:23 */   public Image input = null;
/* 14:24 */   public Integer len = null;
/* 15:25 */   public Integer step = null;
/* 16:   */   
/* 17:   */   public FloatingCovariance()
/* 18:   */   {
/* 19:28 */     this.inputFields = "input,len,step";
/* 20:29 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:33 */     int cdim = this.input.getCDim();
/* 27:34 */     int size = this.len.intValue() * cdim;
/* 28:35 */     this.output = new double[size];
/* 29:   */     
/* 30:37 */     double[] originalVolumes = new double[cdim];
/* 31:39 */     for (int c = 0; c < cdim; c++) {
/* 32:40 */       originalVolumes[c] = Tools.volume(this.input, c);
/* 33:   */     }
/* 34:42 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 35:   */     {
/* 36:43 */       FloatingFlatSE se = FloatingFlatSE.circle((i - 1) * this.step.intValue() + 1, ((i - 1) * this.step.intValue() + 1) * 8);
/* 37:   */       
/* 38:45 */       Image tmp = GFloatingErosion.invoke(this.input, se);
/* 39:47 */       for (int c = 0; c < cdim; c++) {
/* 40:48 */         this.output[(c * this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static double[] invoke(Image image, Integer len, Integer step)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:54 */       return (double[])new FloatingCovariance().preprocess(new Object[] { image, len, step });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:56 */       e.printStackTrace();
/* 54:   */     }
/* 55:57 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.floating.FloatingCovariance
 * JD-Core Version:    0.7.0.1
 */
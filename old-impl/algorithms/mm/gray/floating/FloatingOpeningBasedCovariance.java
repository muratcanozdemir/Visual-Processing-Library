/*  1:   */ package vpt.algorithms.mm.gray.floating;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Tools;
/*  7:   */ import vpt.util.se.FloatingFlatSE;
/*  8:   */ 
/*  9:   */ public class FloatingOpeningBasedCovariance
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:23 */   public double[] output = null;
/* 13:24 */   public Image input = null;
/* 14:25 */   public Integer len = null;
/* 15:26 */   public Integer step = null;
/* 16:   */   
/* 17:   */   public FloatingOpeningBasedCovariance()
/* 18:   */   {
/* 19:29 */     this.inputFields = "input,len,step";
/* 20:30 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:34 */     int cdim = this.input.getCDim();
/* 27:35 */     int size = this.len.intValue() * cdim;
/* 28:36 */     this.output = new double[size];
/* 29:   */     
/* 30:38 */     double[] originalVolumes = new double[cdim];
/* 31:40 */     for (int c = 0; c < cdim; c++) {
/* 32:41 */       originalVolumes[c] = Tools.volume(this.input, c);
/* 33:   */     }
/* 34:43 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 35:   */     {
/* 36:44 */       FloatingFlatSE se = FloatingFlatSE.circle((i - 1) * this.step.intValue() + 1, ((i - 1) * this.step.intValue() + 1) * 8);
/* 37:   */       
/* 38:46 */       Image tmp = GFloatingOpening.invoke(this.input, se);
/* 39:48 */       for (int c = 0; c < cdim; c++) {
/* 40:49 */         this.output[(c * this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static double[] invoke(Image image, Integer len, Integer step)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:55 */       return (double[])new FloatingOpeningBasedCovariance().preprocess(new Object[] { image, len, step });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:57 */       e.printStackTrace();
/* 54:   */     }
/* 55:58 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.floating.FloatingOpeningBasedCovariance
 * JD-Core Version:    0.7.0.1
 */
/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.linear.RotationInvariantGabor;
/*  7:   */ import vpt.algorithms.statistical.Mean;
/*  8:   */ import vpt.algorithms.statistical.SDev;
/*  9:   */ 
/* 10:   */ public class RotationInvariantGaborFeatures
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:26 */   public double[] output = null;
/* 14:27 */   public Image input = null;
/* 15:28 */   public Integer scales = null;
/* 16:   */   
/* 17:   */   public RotationInvariantGaborFeatures()
/* 18:   */   {
/* 19:31 */     this.inputFields = "input,scales";
/* 20:32 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:37 */     if (this.input.getCDim() != 1) {
/* 27:37 */       throw new GlobalException("Only grayscale data please...");
/* 28:   */     }
/* 29:38 */     if (this.scales.intValue() < 1) {
/* 30:38 */       throw new GlobalException("Invalid scale count");
/* 31:   */     }
/* 32:40 */     this.output = new double[this.scales.intValue() * 2];
/* 33:43 */     for (int i = 1; i <= this.scales.intValue(); i++)
/* 34:   */     {
/* 35:44 */       Image tmp = RotationInvariantGabor.invoke(this.input, Double.valueOf(1.0D), new double[] { 0.0D, 1.047197551196598D, 2.094395102393195D, 3.141592653589793D, 4.188790204786391D, 5.235987755982989D }, Double.valueOf(0.0D), Double.valueOf(0.5D), Double.valueOf(1.0D), Integer.valueOf(i * 60 / this.scales.intValue() + 1), Integer.valueOf(i * 60 / this.scales.intValue() + 1));
/* 36:45 */       double mean = Mean.invoke(tmp).doubleValue();
/* 37:46 */       double sdev = SDev.invoke(tmp).doubleValue();
/* 38:   */       
/* 39:48 */       this.output[(2 * (i - 1))] = mean;
/* 40:49 */       this.output[(2 * (i - 1) + 1)] = sdev;
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static double[] invoke(Image image, Integer scales)
/* 45:   */   {
/* 46:   */     try
/* 47:   */     {
/* 48:55 */       return (double[])new RotationInvariantGaborFeatures().preprocess(new Object[] { image, scales });
/* 49:   */     }
/* 50:   */     catch (GlobalException e)
/* 51:   */     {
/* 52:57 */       e.printStackTrace();
/* 53:   */     }
/* 54:58 */     return null;
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.RotationInvariantGaborFeatures
 * JD-Core Version:    0.7.0.1
 */
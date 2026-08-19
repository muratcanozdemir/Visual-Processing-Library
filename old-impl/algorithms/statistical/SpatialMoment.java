/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class SpatialMoment
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:15 */   public double[] output = null;
/* 11:16 */   public Image input = null;
/* 12:17 */   public Integer momentX = null;
/* 13:18 */   public Integer momentY = null;
/* 14:   */   
/* 15:   */   public SpatialMoment()
/* 16:   */   {
/* 17:21 */     this.inputFields = "input, momentX, momentY";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:26 */     if (this.momentX.intValue() + this.momentY.intValue() < 2) {
/* 25:26 */       throw new GlobalException("The sum of momentX and momentY must be >= 2 !");
/* 26:   */     }
/* 27:28 */     int cdim = this.input.getCDim();
/* 28:   */     
/* 29:30 */     this.output = new double[cdim];
/* 30:   */     
/* 31:32 */     double[] muXY = CentralMoment.invoke(this.input, this.momentX, this.momentY);
/* 32:33 */     double[] m00 = Moment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(0));
/* 33:34 */     double alpha = (this.momentX.intValue() + this.momentY.intValue()) / 2.0D + 1.0D;
/* 34:36 */     for (int c = 0; c < cdim; c++)
/* 35:   */     {
/* 36:37 */       m00[c] = Math.pow(m00[c], alpha);
/* 37:38 */       this.output[c] = (muXY[c] / m00[c]);
/* 38:   */     }
/* 39:   */   }
/* 40:   */   
/* 41:   */   public static double[] invoke(Image input, Integer momentX, Integer momentY)
/* 42:   */   {
/* 43:   */     try
/* 44:   */     {
/* 45:44 */       return (double[])new SpatialMoment().preprocess(new Object[] { input, momentX, momentY });
/* 46:   */     }
/* 47:   */     catch (GlobalException e)
/* 48:   */     {
/* 49:46 */       e.printStackTrace();
/* 50:   */     }
/* 51:47 */     return null;
/* 52:   */   }
/* 53:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.SpatialMoment
 * JD-Core Version:    0.7.0.1
 */
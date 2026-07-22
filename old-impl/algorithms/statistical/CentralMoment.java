/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class CentralMoment
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:15 */   public double[] output = null;
/* 11:16 */   public Image input = null;
/* 12:17 */   public Integer momentX = null;
/* 13:18 */   public Integer momentY = null;
/* 14:   */   
/* 15:   */   public CentralMoment()
/* 16:   */   {
/* 17:21 */     this.inputFields = "input, momentX, momentY";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:26 */     int xdim = this.input.getXDim();
/* 25:27 */     int ydim = this.input.getYDim();
/* 26:28 */     int cdim = this.input.getCDim();
/* 27:   */     
/* 28:30 */     double[] m00 = Moment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(0));
/* 29:31 */     double[] m01 = Moment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(1));
/* 30:32 */     double[] m10 = Moment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(0));
/* 31:   */     
/* 32:34 */     double[] centroidX = new double[cdim];
/* 33:35 */     double[] centroidY = new double[cdim];
/* 34:   */     
/* 35:37 */     this.output = new double[cdim];
/* 36:39 */     for (int c = 0; c < cdim; c++)
/* 37:   */     {
/* 38:40 */       m10[c] /= m00[c];
/* 39:41 */       m01[c] /= m00[c];
/* 40:43 */       for (int x = 0; x < xdim; x++) {
/* 41:44 */         for (int y = 0; y < ydim; y++)
/* 42:   */         {
/* 43:45 */           double tmp = this.input.getXYCDouble(x, y, c);
/* 44:46 */           this.output[c] += Math.pow(x + 1 - centroidX[c], this.momentX.intValue()) * Math.pow(y + 1 - centroidY[c], this.momentY.intValue()) * tmp;
/* 45:   */         }
/* 46:   */       }
/* 47:   */     }
/* 48:   */   }
/* 49:   */   
/* 50:   */   public static double[] invoke(Image input, Integer momentX, Integer momentY)
/* 51:   */   {
/* 52:   */     try
/* 53:   */     {
/* 54:55 */       return (double[])new CentralMoment().preprocess(new Object[] { input, momentX, momentY });
/* 55:   */     }
/* 56:   */     catch (GlobalException e)
/* 57:   */     {
/* 58:57 */       e.printStackTrace();
/* 59:   */     }
/* 60:58 */     return null;
/* 61:   */   }
/* 62:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.CentralMoment
 * JD-Core Version:    0.7.0.1
 */
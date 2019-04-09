/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Moment
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:14 */   public double[] output = null;
/* 11:15 */   public Image input = null;
/* 12:16 */   public Integer momentX = null;
/* 13:17 */   public Integer momentY = null;
/* 14:   */   
/* 15:   */   public Moment()
/* 16:   */   {
/* 17:20 */     this.inputFields = "input, momentX, momentY";
/* 18:21 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:25 */     int xdim = this.input.getXDim();
/* 25:26 */     int ydim = this.input.getYDim();
/* 26:27 */     int cdim = this.input.getCDim();
/* 27:   */     
/* 28:29 */     this.output = new double[cdim];
/* 29:31 */     for (int c = 0; c < cdim; c++) {
/* 30:32 */       for (int x = 0; x < xdim; x++) {
/* 31:33 */         for (int y = 0; y < ydim; y++)
/* 32:   */         {
/* 33:34 */           double tmp = this.input.getXYCDouble(x, y, c);
/* 34:35 */           this.output[c] += Math.pow(x + 1, this.momentX.intValue()) * Math.pow(y + 1, this.momentY.intValue()) * tmp;
/* 35:   */         }
/* 36:   */       }
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static double[] invoke(Image input, Integer momentX, Integer momentY)
/* 41:   */   {
/* 42:   */     try
/* 43:   */     {
/* 44:44 */       return (double[])new Moment().preprocess(new Object[] { input, momentX, momentY });
/* 45:   */     }
/* 46:   */     catch (GlobalException e)
/* 47:   */     {
/* 48:46 */       e.printStackTrace();
/* 49:   */     }
/* 50:47 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.Moment
 * JD-Core Version:    0.7.0.1
 */
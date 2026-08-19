/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Skewness
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:14 */   public Double output = null;
/* 11:15 */   public Image input = null;
/* 12:   */   
/* 13:   */   public Skewness()
/* 14:   */   {
/* 15:18 */     this.inputFields = "input";
/* 16:19 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:23 */     int cdim = this.input.getCDim();
/* 23:25 */     if (cdim != 1) {
/* 24:25 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 25:   */     }
/* 26:27 */     double mean = Mean.invoke(this.input).doubleValue();
/* 27:   */     
/* 28:29 */     int size = this.input.getSize();
/* 29:   */     
/* 30:31 */     double s1 = 0.0D;
/* 31:32 */     double s2 = 0.0D;
/* 32:34 */     for (int i = 0; i < size; i++)
/* 33:   */     {
/* 34:35 */       double p = this.input.getDouble(i);
/* 35:36 */       s1 += (p - mean) * (p - mean) * (p - mean);
/* 36:37 */       s2 += (p - mean) * (p - mean);
/* 37:   */     }
/* 38:40 */     s1 /= size;
/* 39:41 */     s2 /= size;
/* 40:   */     
/* 41:43 */     this.output = Double.valueOf(s1 / Math.pow(s2, 1.5D));
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static Double invoke(Image img)
/* 45:   */   {
/* 46:   */     try
/* 47:   */     {
/* 48:49 */       return (Double)new Skewness().preprocess(new Object[] { img });
/* 49:   */     }
/* 50:   */     catch (GlobalException e)
/* 51:   */     {
/* 52:51 */       e.printStackTrace();
/* 53:   */     }
/* 54:52 */     return null;
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.Skewness
 * JD-Core Version:    0.7.0.1
 */
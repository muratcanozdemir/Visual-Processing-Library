/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class SDev
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:14 */   public Double output = null;
/* 11:15 */   public Image input = null;
/* 12:   */   
/* 13:   */   public SDev()
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
/* 28:29 */     double sdev = 0.0D;
/* 29:30 */     int size = this.input.getSize();
/* 30:32 */     for (int i = 0; i < size; i++)
/* 31:   */     {
/* 32:33 */       double p = this.input.getDouble(i);
/* 33:34 */       sdev += (p - mean) * (p - mean);
/* 34:   */     }
/* 35:37 */     this.output = Double.valueOf(Math.sqrt(sdev / size));
/* 36:   */   }
/* 37:   */   
/* 38:   */   public static Double invoke(Image img)
/* 39:   */   {
/* 40:   */     try
/* 41:   */     {
/* 42:43 */       return (Double)new SDev().preprocess(new Object[] { img });
/* 43:   */     }
/* 44:   */     catch (GlobalException e)
/* 45:   */     {
/* 46:45 */       e.printStackTrace();
/* 47:   */     }
/* 48:46 */     return null;
/* 49:   */   }
/* 50:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.SDev
 * JD-Core Version:    0.7.0.1
 */